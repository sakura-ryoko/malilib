package fi.dy.masa.malilib.render.special;

import javax.annotation.Nonnull;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.RenderShape;

import fi.dy.masa.malilib.MaLiLibReference;

/**
 * DISABLED -- DOES NOT WORK, DO NOT USE
 */
@Deprecated
public class MaLiLibBlockModelGuiElementRenderer extends PictureInPictureRenderer<MaLiLibBlockStateModelGuiElement>
{
    BlockRenderDispatcher blockRenderManager;
    Minecraft mc = Minecraft.getInstance();

    public MaLiLibBlockModelGuiElementRenderer(MultiBufferSource.BufferSource immediate, BlockRenderDispatcher blockRenderManager)
    {
        super(immediate);
        this.blockRenderManager = blockRenderManager;
    }

    @Override
    public @Nonnull Class<MaLiLibBlockStateModelGuiElement> getRenderStateClass()
    {
        return MaLiLibBlockStateModelGuiElement.class;
    }

	@Override
    protected void renderToTexture(MaLiLibBlockStateModelGuiElement state, PoseStack matrices)
    {
        if (state.state().getRenderShape() == RenderShape.MODEL)
        {
	        Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
	        int light = LightTexture.pack(15, 15);
			float zLevel = 0f;
			float halfSize = (float) (state.size() / 2);

//	        this.vertexConsumers.draw();
	        matrix4fStack.pushMatrix();
	        matrix4fStack.translate(state.x0() + halfSize, state.y0() + halfSize,  zLevel + 100f);
//	        matrix4fStack.scale((float) state.size(), (float) -state.size(), (float) state.size());
//	        matrices.scale((float) state.size(), (float) -state.size(), (float) state.size());
//	        matrix4fStack.translate(halfSize, halfSize, zLevel);
			matrix4fStack.scale(state.size(), state.size(), state.size());

//			matrices.scale(1f, -1f, 1f);
//			matrices.translate(0.5f, 0.5f, 0.5f);
	        matrices.mulPose(new Quaternionf().rotationXYZ(30 * (float) (Math.PI / 180.0), 225 * (float) (Math.PI / 180.0), 0.0F));
	        matrices.scale(state.scale(), state.scale(), state.scale());
	        matrices.translate(-0.5f, -0.5f, -0.5f);

	        this.blockRenderManager.renderSingleBlock(state.state(), matrices, this.bufferSource, light, OverlayTexture.NO_OVERLAY);
			this.bufferSource.endBatch();
	        matrix4fStack.popMatrix();
        }
    }

    @Override
    protected @Nonnull String getTextureLabel()
    {
        return MaLiLibReference.MOD_ID+ ":block_model";
    }
}
