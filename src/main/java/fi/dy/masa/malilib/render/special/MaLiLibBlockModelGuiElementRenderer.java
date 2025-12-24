package fi.dy.masa.malilib.render.special;

import javax.annotation.Nonnull;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockRenderType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.render.SpecialGuiElementRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.util.math.MatrixStack;

import fi.dy.masa.malilib.MaLiLibReference;

/**
 * DISABLED -- DOES NOT WORK, DO NOT USE
 */
@Deprecated
@ApiStatus.Experimental
public class MaLiLibBlockModelGuiElementRenderer extends SpecialGuiElementRenderer<MaLiLibBlockStateModelGuiElement>
{
    BlockRenderManager blockRenderManager;
    MinecraftClient mc = MinecraftClient.getInstance();

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
	        Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
	        int light = LightmapTextureManager.pack(15, 15);
			float zLevel = 0f;
			float halfSize = (float) (state.size() / 2);

//	        this.vertexConsumers.draw();
	        matrix4fStack.pushMatrix();
	        matrix4fStack.translate(state.x1() + halfSize, state.y1() + halfSize,  zLevel + 100f);
//	        matrix4fStack.scale((float) state.size(), (float) -state.size(), (float) state.size());
//	        matrices.scale((float) state.size(), (float) -state.size(), (float) state.size());
//	        matrix4fStack.translate(halfSize, halfSize, zLevel);
			matrix4fStack.scale(state.size(), state.size(), state.size());

//			matrices.scale(1f, -1f, 1f);
//			matrices.translate(0.5f, 0.5f, 0.5f);
	        matrices.multiply(new Quaternionf().rotationXYZ(30 * (float) (Math.PI / 180.0), 225 * (float) (Math.PI / 180.0), 0.0F));
	        matrices.scale(state.scale(), state.scale(), state.scale());
	        matrices.translate(-0.5f, -0.5f, -0.5f);

	        this.blockRenderManager.renderBlockAsEntity(state.state(), matrices, this.vertexConsumers, light, OverlayTexture.DEFAULT_UV);
			this.vertexConsumers.draw();
	        matrix4fStack.popMatrix();
        }
    }

    @Override
    protected @Nonnull String getName()
    {
        return MaLiLibReference.MOD_ID+ ":block_model";
    }
}
