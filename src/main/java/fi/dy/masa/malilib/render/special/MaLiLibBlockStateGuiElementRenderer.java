package fi.dy.masa.malilib.render.special;

import javax.annotation.Nonnull;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

import fi.dy.masa.malilib.MaLiLibReference;

@ApiStatus.Experimental
public class MaLiLibBlockStateGuiElementRenderer extends PictureInPictureRenderer<@NotNull MaLiLibBlockStateGuiElement>
{
    private final BlockModelResolver modelResolver;

    public MaLiLibBlockStateGuiElementRenderer(MultiBufferSource.BufferSource immediate, BlockModelResolver modelResolver)
    {
        super(immediate);
        this.modelResolver = modelResolver;
    }

    @Override
    public @Nonnull Class<MaLiLibBlockStateGuiElement> getRenderStateClass()
    {
        return MaLiLibBlockStateGuiElement.class;
    }

	@Override
    protected void renderToTexture(MaLiLibBlockStateGuiElement state, @NotNull PoseStack matrices)
    {
        if (state.state().getRenderShape() == RenderShape.MODEL)
        {
	        matrices.pushPose();
	        matrices.scale(state.size(), -state.size(), state.size());

			matrices.mulPose(state.rotation());
	        matrices.scale(state.scale(), state.scale(), state.scale());
	        matrices.translate(-0.5F, (0.5F + state.yOffset()), -0.5F);

	        this.submitBlockStateModel(state.state(), matrices);
	        matrices.popPose();
        }
    }

	private void submitBlockStateModel(BlockState state, PoseStack matrices)
	{
		final int l = LightCoordsUtil.pack(15, 15);
		final int overlay = OverlayTexture.NO_OVERLAY;
//		final int blockColor = this.blockColors.getColor(state, null, null, 0);
//
//		float[] color = new float[] {
//				(blockColor >> 16 & 0xFF) / 255.0F,
//				(blockColor >> 16 & 0xFF) / 255.0F,
//				(blockColor & 0xFF) / 255.0F,
//				1.0F
//		};

		FeatureRenderDispatcher featureRenderer = Minecraft.getInstance().gameRenderer.getFeatureRenderDispatcher();
		SubmitNodeStorage nodeStorage = featureRenderer.getSubmitNodeStorage();
		BlockModelRenderState renderState = new BlockModelRenderState();

		this.modelResolver.update(renderState, state, BlockDisplayContext.create());
		renderState.submit(matrices, nodeStorage, l, overlay, -1);
//		nodeStorage.submitBlockModel(matrices, RenderTypes.translucentMovingBlock(), parts, color[0], color[1], color[2], l, overlay, 0);
		featureRenderer.renderAllFeatures();
	}

    @Override
    protected @Nonnull String getTextureLabel()
    {
        return MaLiLibReference.MOD_ID+ ":block_model";
    }
}
