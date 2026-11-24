package net.launchers.mod.initializer;

import net.launchers.mod.entity.ExtremeLauncherBlockEntity;
import net.launchers.mod.entity.LauncherBlockEntity;
import net.launchers.mod.entity.PoweredLauncherBlockEntity;
import net.launchers.mod.entity.UltimateLauncherBlockEntity;
import net.launchers.mod.loader.LMLoader;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LMEntities {
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, LMLoader.MOD_ID);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LauncherBlockEntity>> LAUNCHER_BLOCK_ENTITY = ENTITIES.register("launcher_block", () -> BlockEntityType.Builder.of(LauncherBlockEntity::new, LMBlock.LAUNCHER_BLOCK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PoweredLauncherBlockEntity>> PW_LAUNCHER_BLOCK_ENTITY = ENTITIES.register("powered_launcher_block", () -> BlockEntityType.Builder.of(PoweredLauncherBlockEntity::new, LMBlock.POWERED_LAUNCHER_BLOCK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ExtremeLauncherBlockEntity>> EX_LAUNCHER_BLOCK_ENTITY = ENTITIES.register("extreme_launcher_block", () -> BlockEntityType.Builder.of(ExtremeLauncherBlockEntity::new, LMBlock.EXTREME_LAUNCHER_BLOCK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<UltimateLauncherBlockEntity>> UL_LAUNCHER_BLOCK_ENTITY = ENTITIES.register("ultimate_launcher_block", () -> BlockEntityType.Builder.of(UltimateLauncherBlockEntity::new, LMBlock.ULTIMATE_LAUNCHER_BLOCK.get()).build(null));

    public static void initialize(IEventBus bus) {
        ENTITIES.register(bus);
    }
}
