package malilib.render;

import java.util.List;
import javax.annotation.Nullable;
import org.lwjgl.opengl.GL11;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;

import malilib.render.buffer.VanillaWrappingVertexBuilder;
import malilib.render.buffer.VertexBuilder;
import malilib.util.data.Identifier;
import malilib.util.game.wrap.GameWrap;
import malilib.util.game.wrap.RenderWrap;
import malilib.util.position.Direction;
import malilib.util.position.PositionUtils;
import malilib.util.position.Vec3d;

public class ModelRenderUtils
{
    private static final Identifier BLOCK_TEXTURE = new Identifier(TextureMap.LOCATION_BLOCKS_TEXTURE);

    public static void renderModelInGui(int x, int y, float zLevel,
                                        IBakedModel model, IBlockState state, RenderContext ctx)
    {
        if (state.getBlock() == Blocks.AIR)
        {
            return;
        }

        RenderWrap.pushMatrix(ctx);

        RenderWrap.bindTexture(BLOCK_TEXTURE);
        RenderWrap.enableRescaleNormal();
        RenderWrap.enableAlpha();
        RenderWrap.alphaFunc(GL11.GL_GREATER, 0.01F);
        RenderWrap.setupBlendSimple();
        RenderWrap.color(1f, 1f, 1f, 1f);

        setupGuiTransform(x, y, model.isGui3d(), zLevel, ctx);
        //model.getItemCameraTransforms().applyTransform(ItemCameraTransforms.TransformType.GUI);
        RenderWrap.rotate( 30, 1, 0, 0, ctx);
        RenderWrap.rotate(225, 0, 1, 0, ctx);
        RenderWrap.scale(0.625, 0.625, 0.625, ctx);

        renderModel(model, state, zLevel, ctx);

        RenderWrap.disableAlpha();
        RenderWrap.disableRescaleNormal();
        RenderWrap.disableLighting();

        RenderWrap.popMatrix(ctx);
    }

    public static void setupGuiTransform(int x, int y, boolean isGui3d, float zLevel, RenderContext ctx)
    {
        RenderWrap.translate(x + 8.0F, y + 8.0F, 100.0F + zLevel, ctx);
        //RenderWrap.translate(8.0F, 8.0F, 0.0F, ctx);
        RenderWrap.scale(1.0F, -1.0F, 1.0F, ctx);
        RenderWrap.scale(16.0F, 16.0F, 16.0F, ctx);

        if (isGui3d)
        {
            RenderWrap.enableLighting();
        }
        else
        {
            RenderWrap.disableLighting();
        }
    }

    public static void renderModel(IBakedModel model, IBlockState state, float zLevel, RenderContext ctx)
    {
        RenderWrap.pushMatrix(ctx);
        RenderWrap.translate(-0.5F, -0.5F, zLevel, ctx);
        int color = 0xFFFFFFFF;

        if (model.isBuiltInRenderer() == false)
        {
            VertexBuilder builder = VanillaWrappingVertexBuilder.create(GL11.GL_QUADS, DefaultVertexFormats.ITEM);

            for (Direction side : PositionUtils.ALL_DIRECTIONS)
            {
                renderQuads(model.getQuads(state, side.getVanillaDirection(), 0L), state, color, builder);
            }

            renderQuads(model.getQuads(state, null, 0L), state, color, builder);

            builder.draw();
        }

        RenderWrap.popMatrix(ctx);
    }

    public static void renderQuads(List<BakedQuad> quads, IBlockState state, int color, VertexBuilder builder)
    {
        for (BakedQuad quad : quads)
        {
            renderQuad(quad, state, color, builder);
        }
    }

    public static void renderQuad(BakedQuad quad, IBlockState state, int color, VertexBuilder builder)
    {
        builder.addVertexData(quad.getVertexData());
        builder.putQuadColor(color);

        if (quad.hasTintIndex())
        {
            BlockColors blockColors = GameWrap.getClient().getBlockColors();
            int m = blockColors.colorMultiplier(state, null, null, quad.getTintIndex());

            float r = (float) (m >>> 16 & 0xFF) / 255F;
            float g = (float) (m >>>  8 & 0xFF) / 255F;
            float b = (float) (m        & 0xFF) / 255F;
            builder.putColorMultiplier(r, g, b, 4);
            builder.putColorMultiplier(r, g, b, 3);
            builder.putColorMultiplier(r, g, b, 2);
            builder.putColorMultiplier(r, g, b, 1);
        }

        putQuadNormal(quad, builder);
    }

    public static void putQuadNormal(BakedQuad quad, VertexBuilder builder)
    {
        net.minecraft.util.math.Vec3i direction = quad.getFace().getDirectionVec();
        builder.putNormal(direction.getX(), direction.getY(), direction.getZ());
    }

    /**
     * Renders the given model to the given vertex consumer.
     * Needs a vertex consumer initialized with mode GL11.GL_QUADS and DefaultVertexFormats.ITEM
     */
    public static void renderModelBrightnessColor(IBakedModel model, Vec3d pos, VertexBuilder builder)
    {
        renderModelBrightnessColor(model, pos, null, 1f, 1f, 1f, 1f, builder);
    }

    /**
     * Renders the given model to the given vertex consumer.
     * Needs a vertex consumer initialized with mode GL11.GL_QUADS and DefaultVertexFormats.ITEM
     */
    public static void renderModelBrightnessColor(IBakedModel model, Vec3d pos, @Nullable IBlockState state,
                                                  float brightness, float r, float g, float b, VertexBuilder builder)
    {
        for (Direction side : PositionUtils.ALL_DIRECTIONS)
        {
            renderQuads(model.getQuads(state, side.getVanillaDirection(), 0L), pos, brightness, r, g, b, builder);
        }

        renderQuads(model.getQuads(state, null, 0L), pos, brightness, r, g, b, builder);
    }

    /**
     * Renders the given quads to the given vertex consumer.
     * Needs a vertex consumer initialized with mode GL11.GL_QUADS and DefaultVertexFormats.ITEM
     */
    public static void renderQuads(List<BakedQuad> quads, Vec3d pos, float brightness,
                                   float red, float green, float blue, VertexBuilder builder)
    {
        for (BakedQuad quad : quads)
        {
            builder.addVertexData(quad.getVertexData());

            if (quad.hasTintIndex())
            {
                builder.putQuadColor(red * brightness, green * brightness, blue * brightness);
            }
            else
            {
                builder.putQuadColor(brightness, brightness, brightness);
            }

            builder.putPosition(pos.x, pos.y, pos.z);
            putQuadNormal(quad, builder);
        }
    }
}
