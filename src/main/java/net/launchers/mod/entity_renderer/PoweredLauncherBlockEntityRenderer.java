package net.launchers.mod.entity_renderer;

import net.launchers.mod.entity.PoweredLauncherBlockEntity;
import net.launchers.mod.entity_renderer.abstraction.AbstractLauncherBlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class PoweredLauncherBlockEntityRenderer extends AbstractLauncherBlockEntityRenderer<PoweredLauncherBlockEntity> {
    public PoweredLauncherBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
    }
}
