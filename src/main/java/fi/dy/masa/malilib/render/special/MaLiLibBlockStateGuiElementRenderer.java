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
	        matrices.scale(state.size(), state.size(), state.size());
            matrices.scale(state.scale(), state.scale(), state.scale());

            // i dunno.
            matrices.translate(-0.5, -1.5 + 0.0625, 0);

            // apply rotation around the center of the block
            matrices.translate(0.5, 0.5, 0.5);
            matrices.rotate(state.rotation());
            matrices.translate(-0.5, -0.5, -0.5);

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
