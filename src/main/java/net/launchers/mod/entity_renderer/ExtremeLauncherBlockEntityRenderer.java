package net.launchers.mod.entity_renderer;

import net.launchers.mod.entity.ExtremeLauncherBlockEntity;
import net.launchers.mod.entity_renderer.abstraction.AbstractLauncherBlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class ExtremeLauncherBlockEntityRenderer extends AbstractLauncherBlockEntityRenderer<ExtremeLauncherBlockEntity> {
    public ExtremeLauncherBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
    }
}
