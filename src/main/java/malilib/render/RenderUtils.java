package malilib.render;

import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.MapData;

import malilib.gui.icon.Icon;
import malilib.gui.icon.PositionedIcon;
import malilib.gui.util.GuiUtils;
import malilib.render.buffer.VanillaWrappingVertexBuilder;
import malilib.render.buffer.VertexBuilder;
import malilib.util.data.Identifier;
import malilib.util.game.wrap.GameWrap;
import malilib.util.game.wrap.RenderWrap;
import malilib.util.position.Vec2i;

public class RenderUtils
{
    public static final Identifier MAP_BACKGROUND_TEXTURE = new Identifier("textures/map/map_background.png");

    /**
     * Renders the given list of icons at their relative positions.
     * If the tintColor is not 0xFFFFFFFF, then the icons will be tinted/colored.
     */
    public static void renderPositionedIcons(int x, int y, float z, int tintColor,
                                             List<PositionedIcon> icons, RenderContext ctx)
    {
        for (PositionedIcon posIcon : icons)
        {
            Vec2i position = posIcon.pos;
            int posX = x + position.x;
            int posY = y + position.y;

            if (tintColor == 0xFFFFFFFF)
            {
                posIcon.icon.renderAt(posX, posY, z, ctx);
            }
            else
            {
                posIcon.icon.renderTintedAt(posX, posY, z, tintColor, ctx);
            }
        }
    }

    public static void renderNineSlicedTexture(int x, int y, float z, int width, int height,
                                               int edgeThickness, Icon icon, int variantIndex, RenderContext ctx)
    {
        int textureWidth = icon.getWidth();
        int textureHeight = icon.getHeight();

        if (textureWidth == 0 || textureHeight == 0)
        {
            return;
        }

        int u = icon.getVariantU(variantIndex);
        int v = icon.getVariantV(variantIndex);

        RenderWrap.color(1f, 1f, 1f, 1f);
        RenderWrap.bindTexture(icon.getTexture());

        renderNineSlicedTexture(x, y, z, u, v, width, height, textureWidth, textureHeight, edgeThickness, ctx);
    }

    public static void renderNineSlicedTexture(int x, int y, float z, int u, int v, int width, int height,
                                               int texWidth, int texHeight, int edgeThickness, RenderContext ctx)
    {
        VertexBuilder builder = VanillaWrappingVertexBuilder.texturedQuad();
        int e = edgeThickness;
        
        ShapeRenderUtils.renderTexturedRectangle256(x, y             , z, u, v                , e, e, builder); // top left
        ShapeRenderUtils.renderTexturedRectangle256(x, y + height - e, z, u, v + texHeight - e, e, e, builder); // bottom left

        ShapeRenderUtils.renderTexturedRectangle256(x + width - e, y             , z, u + texWidth - e, v                , e, e, builder); // top right
        ShapeRenderUtils.renderTexturedRectangle256(x + width - e, y + height - e, z, u + texWidth - e, v + texHeight - e, e, e, builder); // bottom right

        // Texture is smaller than the requested width, repeat stuff horizontally
        if (texWidth < width)
        {
            final int repeatableWidth = texWidth - 2 * e;
            final int requiredWidth = width - 2 * e;

            for (int doneWidth = 0, tmpX = x + e, tmpW; doneWidth < requiredWidth; )
            {
                tmpW = Math.min(repeatableWidth, requiredWidth - doneWidth);

                ShapeRenderUtils.renderTexturedRectangle256(tmpX, y             , z, u + e, v                , tmpW, e, builder); // top center
                ShapeRenderUtils.renderTexturedRectangle256(tmpX, y + height - e, z, u + e, v + texHeight - e, tmpW, e, builder); // bottom center

                tmpX += tmpW;
                doneWidth += tmpW;
            }
        }
        // Texture is wide enough, no need to repeat horizontally
        else
        {
            ShapeRenderUtils.renderTexturedRectangle256(x + e, y             , z, u + e, v                , width - 2 * e, e, builder); // top center
            ShapeRenderUtils.renderTexturedRectangle256(x + e, y + height - e, z, u + e, v + texHeight - e, width - 2 * e, e, builder); // bottom center
        }

        // Texture is smaller than the requested height, repeat stuff vertically
        if (texHeight < height)
        {
            final int repeatableHeight = texHeight - 2 * e;
            final int requiredHeight = height - 2 * e;

            for (int doneHeight = 0, tmpY = y + e, tmpH; doneHeight < requiredHeight; )
            {
                tmpH = Math.min(repeatableHeight, requiredHeight - doneHeight);

                ShapeRenderUtils.renderTexturedRectangle256(x            , tmpY, z, u               , v + e, e, tmpH, builder); // left center
                ShapeRenderUtils.renderTexturedRectangle256(x + width - e, tmpY, z, u + texWidth - e, v + e, e, tmpH, builder); // right center

                tmpY += tmpH;
                doneHeight += tmpH;
            }
        }
        // Texture is tall enough, no need to repeat vertically
        else
        {
            ShapeRenderUtils.renderTexturedRectangle256(x            , y + e, z, u               , v + e, e, height - 2 * e, builder); // left center
            ShapeRenderUtils.renderTexturedRectangle256(x + width - e, y + e, z, u + texWidth - e, v + e, e, height - 2 * e, builder); // right center
        }

        // The center part needs to be repeated
        if (texWidth < width || texHeight < height)
        {
            final int repeatableWidth = texWidth - 2 * e;
            final int requiredWidth = width - 2 * e;

            for (int doneWidth = 0, tmpX = x + e, tmpW; doneWidth < requiredWidth; )
            {
                final int repeatableHeight = texHeight - 2 * e;
                final int requiredHeight = height - 2 * e;
                tmpW = Math.min(repeatableWidth, requiredWidth - doneWidth);

                for (int doneHeight = 0, tmpY = y + e, tmpH; doneHeight < requiredHeight; )
                {
                    tmpH = Math.min(repeatableHeight, requiredHeight - doneHeight);

                    ShapeRenderUtils.renderTexturedRectangle256(tmpX, tmpY, z, u + e, v + e, tmpW, tmpH, builder); // center

                    tmpY += tmpH;
                    doneHeight += tmpH;
                }

                tmpX += tmpW;
                doneWidth += tmpW;
            }
        }
        // No need to repeat the center part
        else
        {
            ShapeRenderUtils.renderTexturedRectangle256(x + e, y + e, z, u + e, v + e, width - 2 * e, height - 2 * e, builder);
        }

        builder.draw();
    }

    public static void renderMapPreview(ItemStack stack, int x, int y, float z, int dimensions, RenderContext ctx)
    {
        if (stack.getItem() instanceof ItemMap)
        {
            RenderWrap.pushMatrix(ctx);
            RenderWrap.disableLighting();
            RenderWrap.color(1f, 1f, 1f, 1f);

            int screenWidth = GuiUtils.getScaledWindowWidth();
            int screenHeight = GuiUtils.getScaledWindowHeight();
            int y1 = Math.max(y - dimensions - 20, 2);

            if (x + dimensions + 10 > screenWidth)
            {
                x = Math.max(x - dimensions - 10, 2);
            }

            if (y1 + dimensions + 2 > screenHeight)
            {
                y1 = screenHeight - dimensions - 2;
            }

            int x1 = x + 8;
            int x2 = x1 + dimensions;
            int y2 = y1 + dimensions;

            RenderWrap.bindTexture(MAP_BACKGROUND_TEXTURE);

            VertexBuilder builder = VanillaWrappingVertexBuilder.texturedQuad();

            builder.posUv(x1, y2, z, 0.0f, 1.0f);
            builder.posUv(x2, y2, z, 1.0f, 1.0f);
            builder.posUv(x2, y1, z, 1.0f, 0.0f);
            builder.posUv(x1, y1, z, 0.0f, 0.0f);

            builder.draw();

            MapData mapdata = Items.FILLED_MAP.getMapData(stack, GameWrap.getClientWorld());

            if (mapdata != null)
            {
                x1 += 8;
                y1 += 8;
                double scale = (double) (dimensions - 16) / 128.0;
                RenderWrap.translate(x1, y1, z + 1f, ctx);
                RenderWrap.scale(scale, scale, 0, ctx);
                GameWrap.getClient().entityRenderer.getMapItemRenderer().renderMap(mapdata, false);
            }

            RenderWrap.enableLighting();
            RenderWrap.popMatrix(ctx);
            RenderWrap.color(1f, 1f, 1f, 1f);
        }
    }
}
