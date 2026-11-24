package net.launchers.mod.entity_renderer.abstraction;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.launchers.mod.block.abstraction.AbstractLauncherBlock;
import net.launchers.mod.entity.abstraction.AbstractLauncherBlockEntity;
import net.launchers.mod.utils.MathUtils;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.client.model.data.ModelData;

public abstract class AbstractLauncherBlockEntityRenderer<T extends AbstractLauncherBlockEntity> implements BlockEntityRenderer<T> {
    protected final BlockRenderDispatcher blockRenderManager = Minecraft.getInstance().getBlockRenderer();

    public AbstractLauncherBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {}

    @Override
    public void render(T blockEntity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        BlockState entityState = blockEntity.getBlockState();
        matrices.pushPose();
        float extension = blockEntity.getDeltaProgress(tickDelta);
        BlockState blockState = blockEntity.getBlockState();
        RenderType renderLayer = ItemBlockRenderTypes.getRenderType(entityState, false);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);

        BakedModel model;
        if (extension < 0.35F) {
            model = blockRenderManager.getBlockModel(blockState.setValue(AbstractLauncherBlock.MODELS, 2).setValue(AbstractLauncherBlock.FACING, blockState.getValue(AbstractLauncherBlock.FACING)));
        }
        else {
            model = blockRenderManager.getBlockModel(blockState.setValue(AbstractLauncherBlock.MODELS, 1).setValue(AbstractLauncherBlock.FACING, blockState.getValue(AbstractLauncherBlock.FACING)));
        }
        Vec3 translation = MathUtils.fromDirection(blockState.getValue(AbstractLauncherBlock.FACING));
        matrices.translate(translation.x * extension, translation.y * extension, translation.z * extension);
        blockRenderManager.getModelRenderer().tesselateBlock(blockEntity.getLevel(), model, entityState, blockEntity.getBlockPos(), matrices, vertexConsumer, true, RandomSource.create(4), 4, overlay, ModelData.EMPTY, null);
        matrices.popPose();
    }
}
