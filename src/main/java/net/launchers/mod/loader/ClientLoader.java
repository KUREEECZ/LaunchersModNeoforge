package net.launchers.mod.loader;

import net.launchers.mod.entity_renderer.ExtremeLauncherBlockEntityRenderer;
import net.launchers.mod.entity_renderer.LauncherBlockEntityRenderer;
import net.launchers.mod.entity_renderer.PoweredLauncherBlockEntityRenderer;
import net.launchers.mod.entity_renderer.UltimateLauncherBlockEntityRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import static net.launchers.mod.initializer.LMEntities.*;

@EventBusSubscriber(modid = LMLoader.MOD_ID, value = Dist.CLIENT)
public final class ClientLoader {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(LAUNCHER_BLOCK_ENTITY.get(), LauncherBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(PW_LAUNCHER_BLOCK_ENTITY.get(), PoweredLauncherBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(EX_LAUNCHER_BLOCK_ENTITY.get(), ExtremeLauncherBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(UL_LAUNCHER_BLOCK_ENTITY.get(), UltimateLauncherBlockEntityRenderer::new);
    }
}
