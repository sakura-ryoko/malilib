package fi.dy.masa.malilib.render.special;

import javax.annotation.Nonnull;
import org.jetbrains.annotations.ApiStatus;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.render.SpecialGuiElementRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.command.OrderedRenderCommandQueueImpl;
import net.minecraft.client.render.command.RenderDispatcher;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.util.math.MatrixStack;

import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.mixin.render.IMixinBlockRenderManager;

/**
 * DISABLED -- DOES NOT WORK, DO NOT USE
 */
@ApiStatus.Experimental
public class MaLiLibBlockModelGuiElementRenderer extends SpecialGuiElementRenderer<MaLiLibBlockStateModelGuiElement>
{
    private final BlockRenderManager blockRenderManager;

    public MaLiLibBlockModelGuiElementRenderer(VertexConsumerProvider.Immediate immediate, BlockRenderManager blockRenderManager)
    {
        super(immediate);
        this.blockRenderManager = blockRenderManager;
    }

    @Override
    public @Nonnull Class<MaLiLibBlockStateModelGuiElement> getElementClass()
    {
        return MaLiLibBlockStateModelGuiElement.class;
    }

	@Override
    protected void render(MaLiLibBlockStateModelGuiElement state, MatrixStack matrices)
    {
        if (state.state().getRenderType() == BlockRenderType.MODEL)
        {
	        matrices.push();
	        matrices.scale(state.size(), -state.size(), state.size());

	        matrices.multiply(state.rotation());
	        matrices.scale(state.scale(), state.scale(), state.scale());
	        matrices.translate(-0.5F, (0.5F + state.yOffset()), -0.5F);

	        this.submitBlockStateModel(state.state(), matrices);
	        matrices.pop();
        }
    }

	private void submitBlockStateModel(BlockState state, MatrixStack matrices)
	{
		final int l = LightmapTextureManager.pack(15, 15);
		final int overlay = OverlayTexture.DEFAULT_UV;
		final int blockColor = ((IMixinBlockRenderManager) this.blockRenderManager).malilib_getBlockColors().getColor(state, null, null, 0);
		float[] color = new float[] {
				(blockColor >> 16 & 0xFF) / 255.0F,
				(blockColor >> 16 & 0xFF) / 255.0F,
				(blockColor & 0xFF) / 255.0F,
				1.0F
		};

		RenderDispatcher featureRenderer = MinecraftClient.getInstance().gameRenderer.getEntityRenderDispatcher();
		OrderedRenderCommandQueueImpl nodeStorage = featureRenderer.getQueue();
		BlockStateModel model = this.blockRenderManager.getModel(state);

		nodeStorage.submitBlockStateModel(matrices, RenderLayer.getTripwire(), model, color[0], color[1], color[2], l, overlay, 0);
		featureRenderer.render();
	}

    @Override
    protected @Nonnull String getName()
    {
        return MaLiLibReference.MOD_ID+ ":block_model";
    }
}
