package fi.dy.masa.malilib.render.special;

import java.util.List;

import org.joml.Quaternionf;

import net.minecraft.block.BlockRenderType;
import net.minecraft.client.gui.render.SpecialGuiElementRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.LocalRandom;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.render.RenderContext;
import fi.dy.masa.malilib.util.position.PositionUtils;

public class MaLiLibBlockModelGuiElementRenderer extends SpecialGuiElementRenderer<MaLiLibBlockStateModelGuiElement>
{
    BlockRenderManager blockRenderManager;

    public MaLiLibBlockModelGuiElementRenderer(VertexConsumerProvider.Immediate immediate, BlockRenderManager blockRenderManager)
    {
        super(immediate);
        this.blockRenderManager = blockRenderManager;
    }

    @Override
    public Class<MaLiLibBlockStateModelGuiElement> getElementClass()
    {
        return MaLiLibBlockStateModelGuiElement.class;
    }

    @Override
    protected void render(MaLiLibBlockStateModelGuiElement state, MatrixStack matrices)
    {
        if (state.state().getRenderType() == BlockRenderType.MODEL)
        {
            BlockStateModel model = this.blockRenderManager.getModel(state.state());
            BlockRenderLayer layer = RenderLayers.getBlockLayer(state.state());
            RenderContext ctx = new RenderContext(() -> "malilib:gui_block_state_model", layer.getPipeline());
            BufferBuilder builder = ctx.getBuilder();

            matrices.push();
            this.setupTransforms(matrices, state.x1(), state.y1(), state.size(), state.zLevel(), state.scale());
            this.renderModel(model, matrices, builder);

            try
            {
                BuiltBuffer meshData = builder.endNullable();

                if (meshData != null)
                {
                    ctx.draw(meshData, false);
                    meshData.close();
                }

                ctx.close();
            }
            catch (Exception err)
            {
                MaLiLib.LOGGER.error("MaLiLibBlockModelGuiElementRenderer: Exception drawing block model; {}", err.getLocalizedMessage());
            }

            matrices.pop();
        }
    }

    @Override
    protected String getName()
    {
        return MaLiLibReference.MOD_ID+ ":block_model";
    }

    private void setupTransforms(MatrixStack matrices, int x, int y, int size, float zLevel, float scale)
    {
        matrices.translate((float) (x + (size / 2)), (float) (y + (size / 2)), (float) (zLevel + 100.0));
        matrices.scale((float) size, (float) -size, (float) size);
        Quaternionf rot = new Quaternionf().rotationXYZ(30 * (float) (Math.PI / 180.0), 225 * (float) (Math.PI / 180.0), 0.0F);
        matrices.multiply(rot);
        matrices.scale(scale, scale, scale);
    }

    private void renderModel(BlockStateModel model, MatrixStack matrices, BufferBuilder builder)
    {
        LocalRandom random = new LocalRandom(0);
        List<BlockModelPart> parts = model.getParts(random);
        int l = LightmapTextureManager.pack(15, 15);
        int[] light = new int[] { l, l, l, l };
        float[] brightness = new float[] { 0.75f, 0.75f, 0.75f, 1.0f };
        MatrixStack.Entry entry = matrices.peek();

        for (BlockModelPart part : parts)
        {
            for (Direction face : PositionUtils.ALL_DIRECTIONS)
            {
                List<BakedQuad> quads = part.getQuads(face);
                random.setSeed(0);

                if (!quads.isEmpty())
                {
                    this.renderQuads(quads, brightness, light, entry, builder);
                }
                else
                {
                    MaLiLib.LOGGER.warn("MaLiLibBlockModelGuiElementRenderer#renderModel(): No quads for block face [{}]", face.asString());
                }
            }

            List<BakedQuad> quads = part.getQuads(null);
            random.setSeed(0);

            if (!quads.isEmpty())
            {
                this.renderQuads(part.getQuads(null), brightness, light, entry, builder);
            }
            else
            {
                MaLiLib.LOGGER.warn("MaLiLibBlockModelGuiElementRenderer#renderModel(): No quads for block face [NULL]");
            }
        }
    }

    private void renderQuads(List<BakedQuad> quads, float[] brightness, int[] light,
                             MatrixStack.Entry matrixEntry, BufferBuilder builder)
    {
        for (BakedQuad quad : quads)
        {
            renderQuad(quad, brightness, light, matrixEntry, builder);
        }
    }

    private void renderQuad(BakedQuad quad, float[] brightness, int[] light,
                            MatrixStack.Entry matrixEntry, BufferBuilder builder)
    {
        builder.quad(matrixEntry, quad, brightness, 1.0f, 1.0f, 1.0f, 1.0f, light, OverlayTexture.DEFAULT_UV, true);
    }
}
