package net.launchers.mod.block;

import com.mojang.serialization.MapCodec;
import net.launchers.mod.block.abstraction.AbstractLauncherBlock;
import net.launchers.mod.entity.ExtremeLauncherBlockEntity;
import net.launchers.mod.initializer.LMEntities;
import net.launchers.mod.initializer.LMSounds;
import net.launchers.mod.loader.LMLoader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ExtremeLauncherBlock extends AbstractLauncherBlock {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(LMLoader.MOD_ID, "extreme_launcher_block");

    public ExtremeLauncherBlock() {
        super(Properties.of().strength(2F, 1.8F).sound(SoundType.METAL).noOcclusion().dynamicShape());
        baseMultiplier = 2.95F;
        stackPowerPercentage = 0.275F;
        stackMultiplier = baseMultiplier * stackPowerPercentage;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, LMEntities.EX_LAUNCHER_BLOCK_ENTITY.get(), ExtremeLauncherBlockEntity::tick);
    }

    @Override
    public ClientboundSoundPacket createLaunchSoundPacket(double x, double y, double z) {
        return new ClientboundSoundPacket(LMSounds.LAUNCHER_BLOCK_LAUNCH_SOUNDEVENT, SoundSource.BLOCKS, x, y, z, 1F, 0.7F, 0);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ExtremeLauncherBlockEntity(pos, state);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return MapCodec.unit(this);
    }
}
