package net.launchers.mod.initializer;

import net.launchers.mod.loader.LMLoader;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LMItem {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(LMLoader.MOD_ID);
    public static final DeferredItem<BlockItem> LAUNCHER = ITEMS.register("launcher_block", () -> new BlockItem(LMBlock.LAUNCHER_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> POWERED_LAUNCHER = ITEMS.register("powered_launcher_block", () -> new BlockItem(LMBlock.POWERED_LAUNCHER_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> EXTREME_LAUNCHER = ITEMS.register("extreme_launcher_block", () -> new BlockItem(LMBlock.EXTREME_LAUNCHER_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> ULTIMATE_LAUNCHER = ITEMS.register("ultimate_launcher_block", () -> new BlockItem(LMBlock.ULTIMATE_LAUNCHER_BLOCK.get(), new Item.Properties()));

    public static void initialize(IEventBus bus) {
        ITEMS.register(bus);
        bus.addListener(LMItem::addCreativeTabItems);
    }

    private static void addCreativeTabItems(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.REDSTONE_BLOCKS)) {
            event.accept(LAUNCHER);
            event.accept(POWERED_LAUNCHER);
            event.accept(EXTREME_LAUNCHER);
            event.accept(ULTIMATE_LAUNCHER);
        }
    }
}
