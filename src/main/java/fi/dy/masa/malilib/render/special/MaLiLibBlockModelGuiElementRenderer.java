package fi.dy.masa.malilib.render.special;

import java.util.List;
import org.joml.Quaternionf;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.render.SpecialGuiElementRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.world.World;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.render.MaLiLibPipelines;
import fi.dy.masa.malilib.render.RenderContext;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.position.PositionUtils;

/**
 * DISABLED -- DOES NOT WORK, DO NOT USE
 */
@Deprecated
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
//            BlockRenderLayer layer = RenderLayers.getBlockLayer(state.state());

            for (BlockRenderLayer layer : BlockRenderLayer.values())
            {
                RenderPipeline pipeline = this.swapPipeline(layer);
                RenderContext ctx = new RenderContext(() -> "malilib:gui_block_state_model/"+layer.getName(), pipeline);
                BufferBuilder builder = ctx.getBuilder();

                matrices.push();
                this.setupTransforms(matrices, state.x1(), state.y1(), state.size(), state.zLevel(), state.scale());
                this.renderModel(model, matrices, state.state(), builder);

                ctx.bindTextureDirect(this.getTexture(layer), 0);

                try
                {
                    BuiltBuffer meshData = builder.endNullable();

                    if (meshData != null)
                    {
                        ctx.draw(meshData, false, false, false, false, true);
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

    private void renderModel(BlockStateModel model, MatrixStack matrices, BlockState state, BufferBuilder builder)
    {
        LocalRandom random = new LocalRandom(0);
        List<BlockModelPart> parts = model.getParts(random);
        int l = LightmapTextureManager.pack(15, 15);
        int[] light = new int[] { l, l, l, l };
        float[] brightness = new float[] { 0.75f, 0.75f, 0.75f, 1.0f };
        BlockPos pos = BlockPos.ORIGIN;

        if (this.mc.world == null)
        {
            return;
        }

//        this.blockRenderManager.renderBlock(state, BlockPos.ORIGIN, this.mc.world, matrices, builder, false, parts);

        for (BlockModelPart part : parts)
        {
            for (Direction face : PositionUtils.ALL_DIRECTIONS)
            {
                List<BakedQuad> quads = part.getQuads(face);

                if (!quads.isEmpty())
                {
                    this.renderQuads(quads, brightness, light, matrices, this.mc.world, pos, state, builder);
                }
            }

            List<BakedQuad> quads = part.getQuads(null);

            if (!quads.isEmpty())
            {
                this.renderQuads(part.getQuads(null), brightness, light, matrices, this.mc.world, pos, state, builder);
            }
        }
    }

    private void renderQuads(List<BakedQuad> quads, float[] brightness, int[] light,
                             MatrixStack matrices, World world, BlockPos pos, BlockState state, BufferBuilder builder)
    {
        for (BakedQuad quad : quads)
        {
            renderQuad(quad, brightness, light, matrices, world, pos, state, builder);
        }
    }

    private void renderQuad(BakedQuad quad, float[] brightness, int[] light,
                            MatrixStack matrices, World world, BlockPos pos, BlockState state, BufferBuilder builder)
    {
        float r;
        float g;
        float b;

        if (quad.hasTint())
        {
            int color = this.mc.getBlockColors().getColor(state, world, pos, quad.tintIndex());
            r = (float) (color >> 16 & 0xFF) / 255.0F;
            g = (float) (color >> 8 & 0xFF) / 255.0F;
            b = (float) (color & 0xFF) / 255.0F;
        }
        else
        {
            r = 1.0F;
            g = 1.0F;
            b = 1.0F;
        }

        float a = 1.0F;
//        final int[] vertexData = quad.vertexData();
//        final int x = pos.getX();
//        final int y = pos.getY();
//        final int z = pos.getZ();
//        final int vertexSize = vertexData.length / 4;
//        float fx, fy, fz;
//
//        for (int index = 0; index < 4; ++index)
//        {
//            fx = x + Float.intBitsToFloat(vertexData[index * vertexSize    ]);
//            fy = y + Float.intBitsToFloat(vertexData[index * vertexSize + 1]);
//            fz = z + Float.intBitsToFloat(vertexData[index * vertexSize + 2]);
//
//            builder.vertex(fx, fy, fz).color(r, g, b, a);
//        }

        builder.quad(matrices.peek(), quad, brightness, r, g, b, a, light, OverlayTexture.DEFAULT_UV, false);
    }

    private boolean useMipMap(BlockRenderLayer layer)
    {
        return switch (layer)
        {
            case SOLID, CUTOUT_MIPPED, TRANSLUCENT, TRIPWIRE -> true;
            case CUTOUT -> false;
        };
    }

    private RenderPipeline swapPipeline(BlockRenderLayer layer)
    {
        return switch (layer)
        {
            case SOLID -> MaLiLibPipelines.SOLID_MASA;
            case CUTOUT -> MaLiLibPipelines.CUTOUT_MASA;
            case CUTOUT_MIPPED -> MaLiLibPipelines.CUTOUT_MIPPED_MASA;
            case TRANSLUCENT -> MaLiLibPipelines.TRANSLUCENT_MASA;
            case TRIPWIRE -> MaLiLibPipelines.TRIPWIRE_MASA;
        };
    }

    @SuppressWarnings("deprecation")
    private AbstractTexture getTexture(BlockRenderLayer layer)
    {
        AbstractTexture abstractTexture = RenderUtils.tex().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
        abstractTexture.setUseMipmaps(this.useMipMap(layer));
        return abstractTexture;
    }
}
