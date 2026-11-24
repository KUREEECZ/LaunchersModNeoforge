package net.launchers.mod.initializer;

import net.launchers.mod.block.ExtremeLauncherBlock;
import net.launchers.mod.block.LauncherBlock;
import net.launchers.mod.block.PoweredLauncherBlock;
import net.launchers.mod.block.UltimateLauncherBlock;
import net.launchers.mod.loader.LMLoader;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LMBlock {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, LMLoader.MOD_ID);
    public static final DeferredHolder<Block, LauncherBlock> LAUNCHER_BLOCK = BLOCKS.register("launcher_block", LauncherBlock::new);
    public static final DeferredHolder<Block, PoweredLauncherBlock> POWERED_LAUNCHER_BLOCK = BLOCKS.register("powered_launcher_block", PoweredLauncherBlock::new);
    public static final DeferredHolder<Block, ExtremeLauncherBlock> EXTREME_LAUNCHER_BLOCK = BLOCKS.register("extreme_launcher_block", ExtremeLauncherBlock::new);
    public static final DeferredHolder<Block, UltimateLauncherBlock> ULTIMATE_LAUNCHER_BLOCK = BLOCKS.register("ultimate_launcher_block", UltimateLauncherBlock::new);

    public static void initialize(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
