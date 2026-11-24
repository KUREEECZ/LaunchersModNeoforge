package net.launchers.mod.entity.abstraction;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractLauncherBlockEntity extends BlockEntity {
    private final VoxelShape RETRACTED_BASE_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private final VoxelShape EXTENDED_BASE_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);
    private final VoxelShape SHORT_EXTENDER_SHAPE = Block.box(6.0D, 2.0D, 6.0D, 10.0D, 10.0D, 10.0D);
    private final VoxelShape LONG_EXTENDER_SHAPE = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 16.0D, 10.0D);
    private final VoxelShape HEAD_SHAPE = Block.box(0.0D, 12.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private final float extensionStride = 1F; // 1/stride ticks per move
    private final float retractingStride = extensionStride / 4;
    private final int retractingDelay = 2;
    public LauncherState[] states;
    public LauncherState launcherState;
    protected int currentTick = 0;
    private float maxExtendCoefficient;
    private float progress;
    private float lastProgress;
    private boolean extending = true; // true if its extending, false if retracting

    public AbstractLauncherBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        states = LauncherState.values();
        launcherState = LauncherState.RETRACTED;
    }


    public static void tick(Level world, BlockPos pos, BlockState state, AbstractLauncherBlockEntity be) {
        switch (be.launcherState) {
            case EXTENDED:
                be.currentTick++;
                if (be.currentTick >= be.retractingDelay) {
                    be.currentTick = 0;
                    be.startRetracting();
                }
                break;
            case RETRACTED:
                break;
            case MOVING:
                be.lastProgress = be.progress;
                if (be.extending) {
                    if (be.lastProgress >= 1.0F) {
                        be.launcherState = LauncherState.EXTENDED;
                        be.lastProgress = 1F;
                    }
                    else {
                        be.progress += be.extensionStride;
                        if (be.progress >= 1.0F) {
                            be.progress = 1.0F;
                        }
                    }
                }
                else {
                    if (be.lastProgress <= 0F) {
                        be.launcherState = LauncherState.RETRACTED;
                        be.lastProgress = 0F;
                    }
                    else {
                        be.progress -= be.retractingStride;
                        if (be.progress <= 0F) {
                            be.progress = 0F;
                        }
                    }
                }
                break;
        }
    }

    public boolean isRetracted() {
        return launcherState == LauncherState.RETRACTED;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
        return saveWithoutMetadata(registryLookup);
    }

    public VoxelShape getCollisionShape() {
        VoxelShape baseShape = null;
        if (isRetracted()) {
            baseShape = RETRACTED_BASE_SHAPE;
            return baseShape;
        }
        else {
            baseShape = EXTENDED_BASE_SHAPE;
            VoxelShape extenderShape = null;
            if (progress < 0.35) {
                extenderShape = Shapes.or(baseShape, SHORT_EXTENDER_SHAPE.move(0, progress - 0.25, 0));
            }
            else {
                extenderShape = Shapes.or(baseShape, LONG_EXTENDER_SHAPE.move(0, progress - 0.25F, 0));
            }
            return Shapes.or(extenderShape, HEAD_SHAPE.move(0, progress, 0));
        }
    }

    public float getDeltaProgress(float tickDelta) {
        if (tickDelta > 1.0F) {
            tickDelta = 1.0F;
        }
        return Mth.lerp(tickDelta, this.lastProgress, this.progress);
    }

    public void startExtending() {
        extending = true;
        launcherState = LauncherState.MOVING;
        progress = 0;
        level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), Block.UPDATE_CLIENTS);
    }

    public void startRetracting() {
        extending = false;
        launcherState = LauncherState.MOVING;
        level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), Block.UPDATE_CLIENTS);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);
        this.currentTick = nbt.getInt("currentTick");
        this.progress = nbt.getFloat("progress");
        this.lastProgress = this.progress;
        this.extending = nbt.getBoolean("extending");
        this.launcherState = states[nbt.getInt("launcherState")];
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        nbt.putInt("currentTick", currentTick);
        nbt.putFloat("progress", this.lastProgress);
        nbt.putBoolean("extending", this.extending);
        nbt.putInt("launcherState", launcherState.ordinal());
        super.saveAdditional(nbt, registryLookup);
    }


    public enum LauncherState {EXTENDED, RETRACTED, MOVING}
}
