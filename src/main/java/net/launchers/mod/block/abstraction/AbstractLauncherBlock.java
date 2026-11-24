package net.launchers.mod.block.abstraction;

import net.launchers.mod.block.UltimateLauncherBlock;
import net.launchers.mod.entity.abstraction.AbstractLauncherBlockEntity;
import net.launchers.mod.initializer.LMSounds;
import net.launchers.mod.loader.LMLoader;
import net.launchers.mod.network.NetworkHandler;
import net.launchers.mod.network.packet.UnboundedEntityVelocityPayload;
import net.launchers.mod.utils.MathUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AbstractLauncherBlock extends BaseEntityBlock {
    public static final ResourceLocation LAUNCH_SOUND = ResourceLocation.fromNamespaceAndPath(LMLoader.MOD_ID, "launcher_block_launch");
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;
    public static final IntegerProperty MODELS = IntegerProperty.create("models", 0, 2);
    public static final EnumProperty<Direction> FACING = DirectionalBlock.FACING;

    public float stackMultiplier;
    public float baseMultiplier;
    protected int maxStackable = 4;
    protected float stackPowerPercentage;

    public AbstractLauncherBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(MODELS, 0).setValue(TRIGGERED, false).setValue(FACING, Direction.NORTH));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext context) {
        return Block.box(0F, 0F, 0F, 16F, 16F, 16F);
    }

    @Override
    public void fallOn(Level world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        entity.causeFallDamage(fallDistance, 0.0F, world.damageSources().fall());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(MODELS, TRIGGERED, FACING);
    }

    public void launchEntities(Level world, BlockPos pos, List<? extends Entity> entities) {
        if (!world.isClientSide) {
            if (entities.isEmpty()) {
                return;
            }
            float launchForce = 1F;
            float force = launchForce * baseMultiplier;
            BlockState parentState = world.getBlockState(pos);
            Direction stackDirection = parentState.getValue(FACING).getOpposite();
            BlockPos currentPos = pos.relative(stackDirection);
            int currentIndex = 1;
            double multiplier = 1F;
            if (!stackDirection.equals(Direction.UP) && !stackDirection.equals(Direction.DOWN)) {
                multiplier *= 1.75F;
            }
            Block current;
            while (currentIndex < maxStackable && ((current = world.getBlockState(currentPos).getBlock()) instanceof AbstractLauncherBlock
                    && !(current instanceof UltimateLauncherBlock)
                    && world.getBlockState(currentPos).getValue(FACING).equals(parentState.getValue(FACING)))) {
                AbstractLauncherBlock launcherBlock = (AbstractLauncherBlock) current;
                multiplier += launcherBlock.stackMultiplier;
                currentPos = currentPos.relative(stackDirection);
                currentIndex++;
            }
            force *= (float) multiplier;
            for (Entity entity : entities) {
                Vec3 initialVelocity = entity.getDeltaMovement();
                Vec3 vectorForce = MathUtils.fromDirection(world.getBlockState(pos).getValue(FACING));
                Vec3 velocity = vectorForce.scale(force).add(initialVelocity);
                entity.setDeltaMovement(velocity);
                UnboundedEntityVelocityPayload packet = new UnboundedEntityVelocityPayload(velocity, entity.getId());
                NetworkHandler.sendToAll(packet, world.getServer().getPlayerList());
            }
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!(world instanceof ServerLevel serverWorld) || !(world.getBlockEntity(pos) instanceof AbstractLauncherBlockEntity launcherBlockEntity)) {
            return;
        }
        boolean isReceivingRedstonePower = world.hasNeighborSignal(pos) || world.hasNeighborSignal(pos.above());
        boolean isTriggered = state.getValue(TRIGGERED);
        boolean isRetracted = launcherBlockEntity.launcherState == AbstractLauncherBlockEntity.LauncherState.RETRACTED;
        if (!isRetracted) return;
        if (isReceivingRedstonePower && !isTriggered) {
            tick(state, serverWorld, pos, world.random);
            world.setBlock(pos, state.setValue(TRIGGERED, true), 4);
        }
        else if (!isReceivingRedstonePower && isTriggered) {
            world.setBlock(pos, state.setValue(TRIGGERED, false), 4);
        }
    }

    @Nullable
    @Override
    public abstract <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type);

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (canLaunch(world, pos)) {
            List<Entity> entities = world.getEntitiesOfClass(Entity.class, new AABB(pos.relative(state.getValue(FACING))), entity -> !entity.isSpectator());
            launchEntities(world, pos, entities);
            playLaunchSound(world, pos);
            ((AbstractLauncherBlockEntity) world.getBlockEntity(pos)).startExtending();
        }
    }

    protected void playLaunchSound(Level world, BlockPos pos) {
        double x = pos.getX() + 0.5D;
        double y = pos.getY();
        double z = pos.getZ() + 0.5D;
        world.getServer().getPlayerList().broadcastAll(createLaunchSoundPacket(x, y, z));
    }

    public ClientboundSoundPacket createLaunchSoundPacket(double x, double y, double z) {
        return new ClientboundSoundPacket(LMSounds.LAUNCHER_BLOCK_LAUNCH_SOUNDEVENT, SoundSource.BLOCKS, x, y, z, 1F, 1F, 0);
    }

    public int getTickRate(LevelReader worldView) {
        return 1;
    }

    public boolean canLaunch(Level world, BlockPos pos) {
        AbstractLauncherBlockEntity launcherBlockEntity = (AbstractLauncherBlockEntity) world.getBlockEntity(pos);
        BlockPos offset = pos.relative(world.getBlockState(pos).getValue(FACING));
        return (world.getBlockState(offset).isAir() || world.getBlockState(offset).getBlock().equals(Blocks.TRIPWIRE))
                && launcherBlockEntity.launcherState == AbstractLauncherBlockEntity.LauncherState.RETRACTED;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getNearestLookingDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Nullable
    @Override
    public abstract BlockEntity newBlockEntity(BlockPos pos, BlockState state);
}
