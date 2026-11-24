package net.launchers.mod.entity_renderer;

import net.launchers.mod.entity.UltimateLauncherBlockEntity;
import net.launchers.mod.entity_renderer.abstraction.AbstractLauncherBlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class UltimateLauncherBlockEntityRenderer extends AbstractLauncherBlockEntityRenderer<UltimateLauncherBlockEntity> {
    public UltimateLauncherBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
    }
}
