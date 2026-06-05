package fi.dy.masa.malilib.render.special;

import javax.annotation.Nonnull;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.mixin.render.IMixinEntityRenderDispatcher;

public class MaLiLibBlockStateGuiElementRenderer extends PictureInPictureRenderer<@NotNull MaLiLibBlockStateGuiElement>
{
    public MaLiLibBlockStateGuiElementRenderer()
    {
        super();
    }

    @Override
    public @Nonnull Class<MaLiLibBlockStateGuiElement> getRenderStateClass()
    {
        return MaLiLibBlockStateGuiElement.class;
    }

	@Override
    protected void renderToTexture(MaLiLibBlockStateGuiElement state, @NotNull PoseStack matrices, @NonNull SubmitNodeCollector nodes)
    {
        if (state.state().getRenderShape() == RenderShape.MODEL)
        {
	        matrices.pushPose();
	        matrices.scale(state.size(), -state.size(), state.size());

			matrices.mulPose(state.rotation());
	        matrices.scale(state.scale(), state.scale(), state.scale());
	        matrices.translate(-0.5F, (0.5F + state.yOffset()), -0.5F);

	        this.submitBlockStateModel(state.state(), matrices, nodes);
	        matrices.popPose();
        }
    }

	private void submitBlockStateModel(BlockState state, PoseStack matrices, SubmitNodeCollector nodes)
	{
		BlockModelResolver resolver = ((IMixinEntityRenderDispatcher) Minecraft.getInstance().getEntityRenderDispatcher()).malilib_getBlockModelResolver();
		final int l = LightCoordsUtil.pack(15, 15);
		final int overlay = OverlayTexture.NO_OVERLAY;
		BlockModelRenderState renderState = new BlockModelRenderState();

		resolver.update(renderState, state, BlockDisplayContext.create());
		renderState.submit(matrices, nodes, l, overlay, -1);
	}

    @Override
    protected @Nonnull String getTextureLabel()
    {
        return MaLiLibReference.MOD_ID+ ":block_model";
    }
}
