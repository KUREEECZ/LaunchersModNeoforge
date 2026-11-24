package net.launchers.mod.loader;

import net.launchers.mod.initializer.*;
import net.launchers.mod.network.NetworkHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(LMLoader.MOD_ID)
public final class LMLoader {
    public static final String MOD_ID = "launchersmod";

    public LMLoader(IEventBus modBus) {
        LMBlock.initialize(modBus);
        LMEntities.initialize(modBus);
        LMItem.initialize(modBus);
        LMSounds.initialize(modBus);
        LMCommands.initialize();
        modBus.addListener(NetworkHandler::registerPayloads);
    }
}
