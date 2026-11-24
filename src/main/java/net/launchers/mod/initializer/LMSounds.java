package net.launchers.mod.initializer;

import net.launchers.mod.loader.LMLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;

public final class LMSounds {
    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, LMLoader.MOD_ID);
    public static final DeferredHolder<SoundEvent, SoundEvent> LAUNCHER_BLOCK_LAUNCH_SOUNDEVENT = SOUNDS.register("launcher_block_launch", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(LMLoader.MOD_ID, "launcher_block_launch")));

    public static void initialize(IEventBus bus) {
        SOUNDS.register(bus);
    }
}
