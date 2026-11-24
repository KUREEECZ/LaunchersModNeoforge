package net.launchers.mod.block;

import com.mojang.serialization.MapCodec;
import net.launchers.mod.block.abstraction.AbstractLauncherBlock;
import net.launchers.mod.entity.PoweredLauncherBlockEntity;
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

public class PoweredLauncherBlock extends AbstractLauncherBlock {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(LMLoader.MOD_ID, "powered_launcher_block");

    public PoweredLauncherBlock() {
        super(Properties.of().strength(1F, 0.85F).sound(SoundType.METAL).noOcclusion().dynamicShape());
        baseMultiplier = 2.125F;
        stackPowerPercentage = 0.2975F;
        stackMultiplier = baseMultiplier * stackPowerPercentage;
    }


    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, LMEntities.PW_LAUNCHER_BLOCK_ENTITY.get(), PoweredLauncherBlockEntity::tick);
    }

    @Override
    public ClientboundSoundPacket createLaunchSoundPacket(double x, double y, double z) {
        return new ClientboundSoundPacket(LMSounds.LAUNCHER_BLOCK_LAUNCH_SOUNDEVENT, SoundSource.BLOCKS, x, y, z, 0.9F, 0.875F, 0);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PoweredLauncherBlockEntity(pos, state);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return MapCodec.unit(this);
    }
}
