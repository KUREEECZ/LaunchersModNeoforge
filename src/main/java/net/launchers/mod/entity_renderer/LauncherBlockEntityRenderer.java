package net.launchers.mod.entity_renderer;

import net.launchers.mod.entity.LauncherBlockEntity;
import net.launchers.mod.entity_renderer.abstraction.AbstractLauncherBlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class LauncherBlockEntityRenderer extends AbstractLauncherBlockEntityRenderer<LauncherBlockEntity> {
    public LauncherBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
    }
}
