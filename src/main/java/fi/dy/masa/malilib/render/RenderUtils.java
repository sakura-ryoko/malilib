package fi.dy.masa.malilib.render;

import java.util.*;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.texture.*;

import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

import com.mojang.blaze3d.buffers.BufferUsage;
import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.*;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.TriState;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.config.HudAlignment;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.mixin.render.IMixinDrawContext;
import fi.dy.masa.malilib.util.*;
import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.malilib.util.nbt.NbtBlockUtils;
import fi.dy.masa.malilib.util.position.PositionUtils;

public class RenderUtils
{
    public static final Identifier TEXTURE_MAP_BACKGROUND = Identifier.ofVanilla("textures/map/map_background.png");
    public static final Identifier TEXTURE_MAP_BACKGROUND_CHECKERBOARD = Identifier.ofVanilla("textures/map/map_background_checkerboard.png");

    private static final LocalRandom RAND = new LocalRandom(0);

    //private static final Vec3d LIGHT0_POS = (new Vec3d( 0.2D, 1.0D, -0.7D)).normalize();
    //private static final Vec3d LIGHT1_POS = (new Vec3d(-0.2D, 1.0D,  0.7D)).normalize();

    public static void blend(boolean toggle)
    {
        //RenderSystem.enableBlend();
        //RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);

        if (toggle)
        {
            GlStateManager._enableBlend();
            GlStateManager._blendFuncSeparate(770, 771, 1, 0);
        }
        else
        {
            GlStateManager._disableBlend();
        }
    }

    /*
    public static void setupBlendSimple()
    {
        //RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
    }
     */

    public static void depthTest(boolean toggle)
    {
        if (toggle)
        {
            GlStateManager._enableDepthTest();
        }
        else
        {
            GlStateManager._disableDepthTest();
        }
    }

    public static void depthFunc(int depth)
    {
        GlStateManager._depthFunc(depth);
    }

    public static void depthMask(boolean toggle)
    {
        GlStateManager._depthMask(toggle);
    }

    public static void culling(boolean toggle)
    {
        if (toggle)
        {
            GlStateManager._enableCull();
        }
        else
        {
            GlStateManager._disableCull();
        }
    }

    public static void polygonOffset(boolean toggle)
    {
        if (toggle)
        {
            GlStateManager._enablePolygonOffset();
        }
        else
        {
            GlStateManager._disablePolygonOffset();
        }
    }

    public static void fbStartDrawing()
    {
        RenderSystem.assertOnRenderThread();
        GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, 0);
    }

    public static void polygonOffset(float factor, float units)
    {
        GlStateManager._polygonOffset(factor, units);
    }

    public static ResourceTexture bindShaderTexture(Identifier texture, int textureId)
    {
        if (textureId < 0 || textureId > 12)
        {
            throw new RuntimeException("Invalid textureId of: "+textureId+" for texture: "+texture.toString());
        }

        ResourceTexture tex = (ResourceTexture) tex().getTexture(texture);
        tex.setFilter(TriState.DEFAULT, false);
        RenderSystem.setShaderTexture(textureId, tex.getGlTexture());

        return tex;
    }

    /**
     * Bind a Gui Overlay Texture using DrawContext.
     *
     * @param texture
     * @param drawContext
     * @return
     */
    public static VertexConsumer bindGuiOverlayTexture(Identifier texture, DrawContext drawContext)
    {
        return getVertexConsumer(getTextureLayer(RenderLayer::getGuiTexturedOverlay, texture), drawContext);
    }

    /**
     * Bind a Gui Texture using DrawContext.
     *
     * @param texture
     * @param drawContext
     * @return
     */
    public static VertexConsumer bindGuiTexture(Identifier texture, DrawContext drawContext)
    {
        return getVertexConsumer(getTextureLayer(RenderLayer::getGuiTextured, texture), drawContext);
    }

    /**
     * Get RenderLayer based on the function, and apply the texture.
     *
     * @param function
     * @param texture
     * @return
     */
    public static RenderLayer getTextureLayer(Function<Identifier, RenderLayer> function, Identifier texture)
    {
        return (RenderLayer) function.apply(texture);
    }

    /**
     * Get the VertexConsumer for the texture Layer from DrawContext.
     *
     * @param textureLayer
     * @param drawContext
     * @return
     */
    public static VertexConsumer getVertexConsumer(RenderLayer textureLayer, DrawContext drawContext)
    {
        return ((IMixinDrawContext) drawContext).malilib_getVertexConsumers().getBuffer(textureLayer);
    }

    /**
     * Executes the draw() operation on the DrawContext.
     *
     * @param drawContext
     */
    public static void forceDraw(DrawContext drawContext)
    {
        if (drawContext != null)
        {
            drawContext.draw();
        }
    }

    public static int color(float r, float g, float b, float a)
    {
        RenderSystem.setShaderColor(r, g, b, a);
        return ColorHelper.fromFloats(a, r, g, b);
    }

    public static void disableDiffuseLighting()
    {
        // FIXME 1.15-pre4+
        DiffuseLighting.disableGuiDepthLighting();
    }

    public static void enableDiffuseLightingForLevel()
    {
        DiffuseLighting.enableForLevel();
    }

    public static void enableDiffuseLightingGui3D()
    {
        // FIXME 1.15-pre4+
        DiffuseLighting.enableGuiDepthLighting();
    }

    public static void drawOutlinedBox(int x, int y, int width, int height, int colorBg, int colorBorder)
    {
        drawOutlinedBox(x, y, width, height, colorBg, colorBorder, 0f);
    }

    public static void drawOutlinedBox(int x, int y, int width, int height, int colorBg, int colorBorder, float zLevel)
    {
        // Draw the background
        drawRect(x, y, width, height, colorBg, zLevel);

        // Draw the border
        drawOutline(x - 1, y - 1, width + 2, height + 2, colorBorder, zLevel);
    }

    public static void drawOutline(int x, int y, int width, int height, int colorBorder)
    {
        drawOutline(x, y, width, height, colorBorder, 0f);
    }

    public static void drawOutline(int x, int y, int width, int height, int colorBorder, float zLevel)
    {
        drawRect(x                    , y,      1, height, colorBorder, zLevel); // left edge
        drawRect(x + width - 1        , y,      1, height, colorBorder, zLevel); // right edge
        drawRect(x + 1,              y, width - 2,      1, colorBorder, zLevel); // top edge
        drawRect(x + 1, y + height - 1, width - 2,      1, colorBorder, zLevel); // bot
    }

    public static void drawOutline(int x, int y, int width, int height, int borderWidth, int colorBorder)
    {
        drawOutline(x, y, width, height, borderWidth, colorBorder, 0f);
    }

    public static void drawOutline(int x, int y, int width, int height, int borderWidth, int colorBorder, float zLevel)
    {
        drawRect(x                      ,                        y, borderWidth            , height     , colorBorder, zLevel); // left edge
        drawRect(x + width - borderWidth,                        y, borderWidth            , height     , colorBorder, zLevel); // right edge
        drawRect(x + borderWidth        ,                        y, width - 2 * borderWidth, borderWidth, colorBorder, zLevel); // top edge
        drawRect(x + borderWidth        , y + height - borderWidth, width - 2 * borderWidth, borderWidth, colorBorder, zLevel); // bottom edge
    }

    public static void drawTexturedRect(Matrix4f posMatrix, int x, int y, int u, int v, int width, int height, VertexConsumer buffer)
    {
        drawTexturedRect(posMatrix, x, y, u, v, width, height, 0f, -1, buffer);
    }

    public static void drawTexturedRect(Matrix4f posMatrix, int x, int y, int u, int v, int width, int height, int color, VertexConsumer buffer)
    {
        drawTexturedRect(posMatrix, x, y, u, v, width, height, 0f, color, buffer);
    }

    public static void drawRect(int x, int y, int width, int height, int color)
    {
        drawRect(x, y, width, height, color, 0f);
    }

    public static void drawRect(int x, int y, int width, int height, int color, float zLevel)
    {
        drawRect(x, y, width, height, color, zLevel, 1.0f);
    }

    public static void drawRect(int x, int y, int width, int height, int color, float zLevel, float scale)
    {
        float a = (float) (color >> 24 & 255) / 255.0F;
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;

//        blend(true);

        // POSITION_COLOR_SIMPLE
        RenderContext ctx = new RenderContext(MaLiLibPipelines.POSITION_COLOR_MASA_NO_DEPTH_NO_CULL, BufferUsage.STATIC_WRITE);
        BufferBuilder buffer = ctx.getBuilder();

        buffer.vertex(x * scale,           y * scale,            zLevel).color(r, g, b, a);
        buffer.vertex(x * scale,           (y + height) * scale, zLevel).color(r, g, b, a);
        buffer.vertex((x + width) * scale, (y + height) * scale, zLevel).color(r, g, b, a);
        buffer.vertex((x + width) * scale, y * scale           , zLevel).color(r, g, b, a);

        try
        {
            ctx.draw(buffer.endNullable());
            ctx.close();
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("drawRect(): Draw Exception; {}", err.getMessage());
        }

//        blend(false);
    }

    /**
     * Draws the Vanilla "Screen Blur" effect.
     *
     * @param mc
     */
    public static void drawScreenBlur(MinecraftClient mc)
    {
        mc.gameRenderer.renderBlur();
    }

    public static void drawTexturedRect(Matrix4f posMatrix, int x, int y, int u, int v, int width, int height, float zLevel, int color, VertexConsumer buffer)
    {
        float pixelWidth = 0.00390625F;

        // GUI_TEXTURED_OVERLAY
        blend(true);

        buffer.vertex(posMatrix, x, y + height, zLevel).texture(u * pixelWidth, (v + height) * pixelWidth).color(color);
        buffer.vertex(posMatrix, x + width, y + height, zLevel).texture((u + width) * pixelWidth, (v + height) * pixelWidth).color(color);
        buffer.vertex(posMatrix, x + width, y, zLevel).texture((u + width) * pixelWidth, v * pixelWidth).color(color);
        buffer.vertex(posMatrix, x, y, zLevel).texture(u * pixelWidth, v * pixelWidth).color(color);
    }

    /**
     * New DrawContext-based Textured Rect method.  Use this when the original method fails.
     *
     * @param texture
     * @param x
     * @param y
     * @param u
     * @param v
     * @param width
     * @param height
     * @param drawContext
     */
    public static void drawTexturedRect(Identifier texture, int x, int y, int u, int v, int width, int height, DrawContext drawContext)
    {
        drawTexturedRect(texture, x, y, u, v, width, height, 0F, -1, drawContext);
    }

    public static void drawTexturedRectAndDraw(Identifier texture, int x, int y, int u, int v, int width, int height, DrawContext drawContext)
    {
        drawTexturedRect(texture, x, y, u, v, width, height, 0F, -1, drawContext);
        forceDraw(drawContext);
    }

    /**
     * New DrawContext-based Textured Rect method.  Use this when the original method fails.
     *
     * @param texture
     * @param x
     * @param y
     * @param u
     * @param v
     * @param width
     * @param height
     * @param zLevel
     * @param drawContext
     */
    public static void drawTexturedRect(Identifier texture, int x, int y, int u, int v, int width, int height, float zLevel, DrawContext drawContext)
    {
        drawTexturedRect(texture, x, y, u, v, width, height, zLevel, -1, drawContext);
    }

    public static void drawTexturedRectAndDraw(Identifier texture, int x, int y, int u, int v, int width, int height, float zLevel, DrawContext drawContext)
    {
        drawTexturedRect(texture, x, y, u, v, width, height, zLevel, -1, drawContext);
        forceDraw(drawContext);
    }

    /**
     * New DrawContext-based Textured Rect method.  Use this when the original method fails.
     *
     * @param texture
     * @param x
     * @param y
     * @param u
     * @param v
     * @param width
     * @param height
     * @param zLevel
     * @param argb
     * @param drawContext
     */
    public static void drawTexturedRect(Identifier texture, int x, int y, int u, int v, int width, int height, float zLevel, int argb, DrawContext drawContext)
    {
        float pixelWidth = 0.00390625F;

        //RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
        blend(true);
        VertexConsumer vertexConsumer = bindGuiOverlayTexture(texture, drawContext);
        Matrix4f matrix4f = drawContext.getMatrices().peek().getPositionMatrix();

        vertexConsumer.vertex(matrix4f, x, y + height, zLevel).texture(u * pixelWidth, (v + height) * pixelWidth).color(argb);
        vertexConsumer.vertex(matrix4f, x + width, y + height, zLevel).texture((u + width) * pixelWidth, (v + height) * pixelWidth).color(argb);
        vertexConsumer.vertex(matrix4f, x + width, y, zLevel).texture((u + width) * pixelWidth, v * pixelWidth).color(argb);
        vertexConsumer.vertex(matrix4f, x, y, zLevel).texture(u * pixelWidth, v * pixelWidth).color(argb);
    }

    public static void drawTexturedRectBatched(Matrix4f posMatrix, int x, int y, int u, int v, int width, int height, VertexConsumer buffer)
    {
        drawTexturedRectBatched(posMatrix, x, y, u, v, width, height, 0, -1, buffer);
    }

    public static void drawTexturedRectBatched(Matrix4f posMatrix, int x, int y, int u, int v, int width, int height, int argb, VertexConsumer buffer)
    {
        drawTexturedRectBatched(posMatrix, x, y, u, v, width, height, 0, argb, buffer);
    }

    public static void drawTexturedRectBatched(Matrix4f posMatrix, int x, int y, int u, int v, int width, int height, float zLevel, int argb, VertexConsumer buffer)
    {
        float pixelWidth = 0.00390625F;

        buffer.vertex(posMatrix, x, y + height, zLevel).texture(u * pixelWidth, (v + height) * pixelWidth).color(argb);
        buffer.vertex(posMatrix, x + width, y + height, zLevel).texture((u + width) * pixelWidth, (v + height) * pixelWidth).color(argb);
        buffer.vertex(posMatrix, x + width, y, zLevel).texture((u + width) * pixelWidth, v * pixelWidth).color(argb);
        buffer.vertex(posMatrix, x, y, zLevel).texture(u * pixelWidth, v * pixelWidth).color(argb);
    }

    public static void drawHoverText(int x, int y, List<String> textLines, DrawContext drawContext)
    {
        if (textLines.isEmpty() == false && GuiUtils.getCurrentScreen() != null)
        {
            depthTest(true);
            TextRenderer font = mc().textRenderer;
            int maxLineLength = 0;
            int maxWidth = GuiUtils.getCurrentScreen().width;
            List<String> linesNew = new ArrayList<>();

            for (String lineOrig : textLines)
            {
                String[] lines = lineOrig.split("\\n");

                for (String line : lines)
                {
                    int length = font.getWidth(line);

                    if (length > maxLineLength)
                    {
                        maxLineLength = length;
                    }

                    linesNew.add(line);
                }
            }

            textLines = linesNew;

            final int lineHeight = font.fontHeight + 1;
            int textHeight = textLines.size() * lineHeight - 2;
            int textStartX = x + 4;
            int textStartY = Math.max(8, y - textHeight - 6);

            if (textStartX + maxLineLength + 6 > maxWidth)
            {
                textStartX = Math.max(2, maxWidth - maxLineLength - 8);
            }

            //drawTexturedRect(GuiBase.BG_TEXTURE, x, y, 0, 0, maxLineLength, maxWidth, drawContext);

            drawContext.getMatrices().push();
            drawContext.getMatrices().translate(0, 0, 300);

            float zLevel = (float) 300;
            int borderColor = 0xF0100010;
            drawGradientRect(textStartX - 3, textStartY - 4, textStartX + maxLineLength + 3, textStartY - 3, zLevel, borderColor, borderColor);
            drawGradientRect(textStartX - 3, textStartY + textHeight + 3, textStartX + maxLineLength + 3, textStartY + textHeight + 4, zLevel, borderColor, borderColor);
            drawGradientRect(textStartX - 3, textStartY - 3, textStartX + maxLineLength + 3, textStartY + textHeight + 3, zLevel, borderColor, borderColor);
            drawGradientRect(textStartX - 4, textStartY - 3, textStartX - 3, textStartY + textHeight + 3, zLevel, borderColor, borderColor);
            drawGradientRect(textStartX + maxLineLength + 3, textStartY - 3, textStartX + maxLineLength + 4, textStartY + textHeight + 3, zLevel, borderColor, borderColor);

            int fillColor1 = 0x505000FF;
            int fillColor2 = 0x5028007F;
            drawGradientRect(textStartX - 3, textStartY - 3 + 1, textStartX - 3 + 1, textStartY + textHeight + 3 - 1, zLevel, fillColor1, fillColor2);
            drawGradientRect(textStartX + maxLineLength + 2, textStartY - 3 + 1, textStartX + maxLineLength + 3, textStartY + textHeight + 3 - 1, zLevel, fillColor1, fillColor2);
            drawGradientRect(textStartX - 3, textStartY - 3, textStartX + maxLineLength + 3, textStartY - 3 + 1, zLevel, fillColor1, fillColor1);
            drawGradientRect(textStartX - 3, textStartY + textHeight + 2, textStartX + maxLineLength + 3, textStartY + textHeight + 3, zLevel, fillColor2, fillColor2);

            //forceDraw(drawContext);

            for (int i = 0; i < textLines.size(); ++i)
            {
                String str = textLines.get(i);

                drawContext.drawText(font, str, textStartX, textStartY, 0xFFFFFFFF, false);
                textStartY += lineHeight;
            }

            //forceDraw(drawContext);
            drawContext.getMatrices().pop();

            //RenderSystem.disableDepthTest();
            //enableDiffuseLightingGui3D();
        }
    }

    public static void drawGradientRect(float left, float top, float right, float bottom, float zLevel, int startColor, int endColor)
    {
        int sa = (startColor >> 24 & 0xFF);
        int sr = (startColor >> 16 & 0xFF);
        int sg = (startColor >> 8 & 0xFF);
        int sb = (startColor & 0xFF);

        int ea = (endColor >> 24 & 0xFF);
        int er = (endColor >> 16 & 0xFF);
        int eg = (endColor >> 8 & 0xFF);
        int eb = (endColor & 0xFF);

        blend(true);
        RenderContext ctx = new RenderContext(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_NO_DEPTH_NO_CULL, BufferUsage.STATIC_WRITE);
        BufferBuilder buffer = ctx.getBuilder();

        buffer.vertex(right, top, zLevel).color(sr, sg, sb, sa);
        buffer.vertex(left, top, zLevel).color(sr, sg, sb, sa);
        buffer.vertex(left, bottom, zLevel).color(er, eg, eb, ea);
        buffer.vertex(right, bottom, zLevel).color(er, eg, eb, ea);

        try
        {
            // fb()
            ctx.draw(buffer.endNullable());
            ctx.close();
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("drawGradientRect(): Draw Exception; {}", err.getMessage());
        }
        blend(false);
    }

    public static void drawCenteredString(int x, int y, int color, String text, DrawContext drawContext)
    {
        TextRenderer textRenderer = mc().textRenderer;
        drawContext.drawCenteredTextWithShadow(textRenderer, text, x, y, color);
    }

    public static void drawHorizontalLine(int x, int y, int width, int color)
    {
        drawRect(x, y, width, 1, color);
    }

    public static void drawVerticalLine(int x, int y, int height, int color)
    {
        drawRect(x, y, 1, height, color);
    }

    public static void renderSprite(int x, int y, int width, int height, Identifier atlas, Identifier texture, DrawContext drawContext)
    {
        if (texture != null)
        {
            Sprite sprite = mc().getSpriteAtlas(atlas).apply(texture);
            drawContext.drawSpriteStretched(RenderLayer::getGuiTextured, sprite, x, y, width, height, -1);
        }
    }

    public static void renderText(int x, int y, int color, String text, DrawContext drawContext)
    {
        String[] parts = text.split("\\\\n");
        TextRenderer textRenderer = mc().textRenderer;

        for (String line : parts)
        {
            drawContext.drawText(textRenderer, line, x, y, color, true);
            y += textRenderer.fontHeight + 1;
        }
    }

    public static void renderText(int x, int y, int color, List<String> lines, DrawContext drawContext)
    {
        if (lines.isEmpty() == false)
        {
            TextRenderer textRenderer = mc().textRenderer;

            for (String line : lines)
            {
                drawContext.drawText(textRenderer, line, x, y, color, false);
                y += textRenderer.fontHeight + 2;
            }
        }
    }

    public static int renderText(int xOff, int yOff, double scale, int textColor, int bgColor, HudAlignment alignment,
                                 boolean useBackground, boolean useShadow,
                                 List<String> lines, DrawContext drawContext)
    {
        return renderText(xOff, yOff, scale, textColor, bgColor, alignment,
                          useBackground, useShadow, MaLiLibConfigs.Generic.ENABLE_STATUS_EFFECTS_SHIFT.getBooleanValue(),
                          lines, drawContext);
    }

    public static int renderText(int xOff, int yOff, double scale, int textColor, int bgColor, HudAlignment alignment,
                                 boolean useBackground, boolean useShadow, boolean useStatusShift,
                                 List<String> lines, DrawContext drawContext)
    {
        TextRenderer fontRenderer = mc().textRenderer;
        final int scaledWidth = GuiUtils.getScaledWindowWidth();
        final int lineHeight = fontRenderer.fontHeight + 2;
        final int contentHeight = lines.size() * lineHeight - 2;
        final int bgMargin = 2;

        // Only Chuck Norris can divide by zero
        if (scale < 0.0125)
        {
            return 0;
        }

        //Matrix4fStack global4fStack = RenderSystem.getModelViewStack();
        boolean scaled = scale != 1.0;

//        depthTest(true);
        blend(true);

        if (scaled)
        {
            if (scale != 0)
            {
                xOff = (int) (xOff * scale);
                yOff = (int) (yOff * scale);
            }

            drawContext.getMatrices().push();
            drawContext.getMatrices().scale((float) scale, (float) scale, 1.0f);
            //global4fStack.pushMatrix();
            //global4fStack.scale((float) scale, (float) scale, 1.0f);
            //RenderSystem.applyModelViewMatrix();
        }

        double posX = xOff + bgMargin;
        double posY = yOff + bgMargin;

        posY = getHudPosY((int) posY, yOff, contentHeight, scale, alignment);

        if (useStatusShift)
        {
            posY += getHudOffsetForPotions(alignment, scale, mc().player);
        }

        for (String line : lines)
        {
            final int width = fontRenderer.getWidth(line);

            switch (alignment)
            {
                case TOP_RIGHT:
                case BOTTOM_RIGHT:
                    posX = (scaledWidth / scale) - width - xOff - bgMargin;
                    break;
                case CENTER:
                    posX = (scaledWidth / scale / 2) - ((double) width / 2) - xOff;
                    break;
                default:
            }

            final int x = (int) posX;
            final int y = (int) posY;
            posY += lineHeight;

            if (useBackground)
            {
                drawRect(x - bgMargin, y - bgMargin, width + bgMargin, bgMargin + fontRenderer.fontHeight, bgColor, 0f, (float) scale);
            }

            drawContext.drawText(fontRenderer, line, x, y, textColor, useShadow);
            //forceDraw(drawContext);
        }

        if (scaled)
        {
            //global4fStack.popMatrix();
            drawContext.getMatrices().pop();
            //RenderSystem.applyModelViewMatrix();
        }

//        depthTest(false);
        blend(false);

        return contentHeight + bgMargin * 2;
    }

    public static int getHudOffsetForPotions(HudAlignment alignment, double scale, PlayerEntity player)
    {
        if (alignment == HudAlignment.TOP_RIGHT)
        {
            // Only Chuck Norris can divide by zero
            if (scale == 0d)
            {
                return 0;
            }

            Collection<StatusEffectInstance> effects = player.getStatusEffects();
            boolean hasTurtleHelmet = EntityUtils.hasTurtleHelmetEquipped(player);
            // Turtle Helmets only add their status effects when in water

            if (effects.isEmpty() == false)
            {
                int y1 = 0;
                int y2 = 0;

                for (StatusEffectInstance effectInstance : effects)
                {
                    StatusEffect effect = effectInstance.getEffectType().value();

                    if (effectInstance.shouldShowParticles() && effectInstance.shouldShowIcon())
                    {
                        if (effect.isBeneficial())
                        {
                            y1 = 26;
                        }
                        else
                        {
                            y2 = 52;
                            break;
                        }
                    }
                }

                if (hasTurtleHelmet && y1 == 0)
                {
                    y1 = 26;
                }

                return (int) (Math.max(y1, y2) / scale);
            }
            else if (hasTurtleHelmet)
            {
                return (int) ((int) 26 / scale);
            }
        }

        return 0;
    }

    public static int getHudPosY(int yOrig, int yOffset, int contentHeight, double scale, HudAlignment alignment)
    {
        int scaledHeight = GuiUtils.getScaledWindowHeight();
        int posY = yOrig;

        switch (alignment)
        {
            case BOTTOM_LEFT:
            case BOTTOM_RIGHT:
                posY = (int) ((scaledHeight / scale) - contentHeight - yOffset);
                break;
            case CENTER:
                posY = (int) ((scaledHeight / scale / 2.0d) - (contentHeight / 2.0d) + yOffset);
                break;
            default:
        }

        return posY;
    }

    /**
     * Assumes a BufferBuilder in GL_QUADS mode has been initialized
     */
    public static void drawBlockBoundingBoxSidesBatchedQuads(BlockPos pos, Color4f color, double expand, BufferBuilder buffer)
    {
        float minX = (float) (pos.getX() - expand);
        float minY = (float) (pos.getY() - expand);
        float minZ = (float) (pos.getZ() - expand);
        float maxX = (float) (pos.getX() + expand + 1);
        float maxY = (float) (pos.getY() + expand + 1);
        float maxZ = (float) (pos.getZ() + expand + 1);

        drawBoxAllSidesBatchedQuads(minX, minY, minZ, maxX, maxY, maxZ, color, buffer);
    }

    public static void drawBlockBoundingBoxSidesBatchedQuads(BlockPos pos, Vec3d cameraPos, Color4f color, double expand, BufferBuilder buffer)
    {
        float minX = (float) (pos.getX() - cameraPos.x - expand);
        float minY = (float) (pos.getY() - cameraPos.y - expand);
        float minZ = (float) (pos.getZ() - cameraPos.z - expand);
        float maxX = (float) (pos.getX() - cameraPos.x + expand + 1);
        float maxY = (float) (pos.getY() - cameraPos.y + expand + 1);
        float maxZ = (float) (pos.getZ() - cameraPos.z + expand + 1);

        RenderUtils.drawBoxAllSidesBatchedQuads(minX, minY, minZ, maxX, maxY, maxZ, color, buffer);
    }

    public static void drawBlockBoundingBoxSidesBatchedQuads(BlockPos pos, Vec3d cameraPos, Color4f color, double expand, BufferBuilder buffer, MatrixStack.Entry e)
    {
        float minX = (float) (pos.getX() - cameraPos.x - expand);
        float minY = (float) (pos.getY() - cameraPos.y - expand);
        float minZ = (float) (pos.getZ() - cameraPos.z - expand);
        float maxX = (float) (pos.getX() - cameraPos.x + expand + 1);
        float maxY = (float) (pos.getY() - cameraPos.y + expand + 1);
        float maxZ = (float) (pos.getZ() - cameraPos.z + expand + 1);

        RenderUtils.drawBoxAllSidesBatchedQuads(minX, minY, minZ, maxX, maxY, maxZ, color, buffer, e);
    }

    /**
     * Assumes a BufferBuilder in GL_LINES mode has been initialized
     */
    public static void drawBlockBoundingBoxOutlinesBatchedLines(BlockPos pos, Color4f color, double expand, BufferBuilder buffer, MatrixStack.Entry e)
    {
        drawBlockBoundingBoxOutlinesBatchedLines(pos, Vec3d.ZERO, color, expand, buffer, e);
    }

    /**
     * Assumes a BufferBuilder in GL_LINES mode has been initialized.
     * The cameraPos value will be subtracted from the absolute coordinate values of the passed in BlockPos.
     *
     * @param pos
     * @param cameraPos
     * @param color
     * @param expand
     * @param buffer
     */
    public static void drawBlockBoundingBoxOutlinesBatchedLines(BlockPos pos, Vec3d cameraPos, Color4f color, double expand, BufferBuilder buffer, MatrixStack.Entry e)
    {
        float minX = (float) (pos.getX() - expand - cameraPos.x);
        float minY = (float) (pos.getY() - expand - cameraPos.y);
        float minZ = (float) (pos.getZ() - expand - cameraPos.z);
        float maxX = (float) (pos.getX() + expand - cameraPos.x + 1);
        float maxY = (float) (pos.getY() + expand - cameraPos.y + 1);
        float maxZ = (float) (pos.getZ() + expand - cameraPos.z + 1);

        drawBoxAllEdgesBatchedLines(minX, minY, minZ, maxX, maxY, maxZ, color, buffer, e);
    }

    /**
     * Assumes a BufferBuilder in GL_QUADS mode has been initialized
     */
    public static void drawBoxAllSidesBatchedQuads(float minX, float minY, float minZ, float maxX, float maxY, float maxZ,
                                                   Color4f color, BufferBuilder buffer)
    {
        drawBoxHorizontalSidesBatchedQuads(minX, minY, minZ, maxX, maxY, maxZ, color, buffer);
        drawBoxTopBatchedQuads(minX, minZ, maxX, maxY, maxZ, color, buffer);
        drawBoxBottomBatchedQuads(minX, minY, minZ, maxX, maxZ, color, buffer);
    }

    public static void drawBoxAllSidesBatchedQuads(float minX, float minY, float minZ, float maxX, float maxY, float maxZ,
                                                   Color4f color, BufferBuilder buffer, MatrixStack.Entry e)
    {
        drawBoxHorizontalSidesBatchedQuads(minX, minY, minZ, maxX, maxY, maxZ, color, buffer, e);
        drawBoxTopBatchedQuads(minX, minZ, maxX, maxY, maxZ, color, buffer, e);
        drawBoxBottomBatchedQuads(minX, minY, minZ, maxX, maxZ, color, buffer, e);
    }

    /**
     * Draws a box with outlines around the given corner positions.
     * Takes in buffers initialized for GL_QUADS and GL_LINES modes.
     *
     * @param posMin
     * @param posMax
     * @param colorLines
     * @param colorSides
     * @param bufferQuads
     * @param bufferLines
     */
    public static void drawBoxWithEdgesBatched(BlockPos posMin, BlockPos posMax, Color4f colorLines, Color4f colorSides, BufferBuilder bufferQuads, BufferBuilder bufferLines, MatrixStack matrices)
    {
        drawBoxWithEdgesBatched(posMin, posMax, Vec3d.ZERO, colorLines, colorSides, bufferQuads, bufferLines, matrices);
    }

    /**
     * Draws a box with outlines around the given corner positions.
     * Takes in buffers initialized for GL_QUADS and GL_LINES modes.
     * The cameraPos value will be subtracted from the absolute coordinate values of the passed in block positions.
     *
     * @param posMin
     * @param posMax
     * @param cameraPos
     * @param colorLines
     * @param colorSides
     * @param bufferQuads
     * @param bufferLines
     */
    public static void drawBoxWithEdgesBatched(BlockPos posMin, BlockPos posMax, Vec3d cameraPos, Color4f colorLines, Color4f colorSides, BufferBuilder bufferQuads, BufferBuilder bufferLines, MatrixStack matrices)
    {
        final float x1 = (float) (posMin.getX() - cameraPos.x);
        final float y1 = (float) (posMin.getY() - cameraPos.y);
        final float z1 = (float) (posMin.getZ() - cameraPos.z);
        final float x2 = (float) (posMax.getX() + 1 - cameraPos.x);
        final float y2 = (float) (posMax.getY() + 1 - cameraPos.y);
        final float z2 = (float) (posMax.getZ() + 1 - cameraPos.z);

        MatrixStack.Entry e = matrices.peek();

        drawBoxAllSidesBatchedQuads(x1, y1, z1, x2, y2, z2, colorSides, bufferQuads);
        drawBoxAllEdgesBatchedLines(x1, y1, z1, x2, y2, z2, colorLines, bufferLines, e);
    }

    /**
     * Assumes a BufferBuilder in GL_QUADS mode has been initialized
     */
    public static void drawBoxHorizontalSidesBatchedQuads(float minX, float minY, float minZ, float maxX, float maxY, float maxZ,
                                                          Color4f color, BufferBuilder buffer)
    {
        // West side
        buffer.vertex(minX, minY, minZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(minX, minY, maxZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(minX, maxY, maxZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(minX, maxY, minZ).color(color.r, color.g, color.b, color.a);

        // East side
        buffer.vertex(maxX, minY, maxZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(maxX, minY, minZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(maxX, maxY, minZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(maxX, maxY, maxZ).color(color.r, color.g, color.b, color.a);

        // North side
        buffer.vertex(maxX, minY, minZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(minX, minY, minZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(minX, maxY, minZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(maxX, maxY, minZ).color(color.r, color.g, color.b, color.a);

        // South side
        buffer.vertex(minX, minY, maxZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(maxX, minY, maxZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(maxX, maxY, maxZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(minX, maxY, maxZ).color(color.r, color.g, color.b, color.a);
    }

    public static void drawBoxHorizontalSidesBatchedQuads(float minX, float minY, float minZ, float maxX, float maxY, float maxZ,
                                                          Color4f color, BufferBuilder buffer, MatrixStack.Entry e)
    {
        // West side
        buffer.vertex(e, minX, minY, minZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(e, minX, minY, maxZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(e, minX, maxY, maxZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(e, minX, maxY, minZ).color(color.r, color.g, color.b, color.a);

        // East side
        buffer.vertex(e, maxX, minY, maxZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(e, maxX, minY, minZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(e, maxX, maxY, minZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(e, maxX, maxY, maxZ).color(color.r, color.g, color.b, color.a);

        // North side
        buffer.vertex(e, maxX, minY, minZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(e, minX, minY, minZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(e, minX, maxY, minZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(e, maxX, maxY, minZ).color(color.r, color.g, color.b, color.a);

        // South side
        buffer.vertex(e, minX, minY, maxZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(e, maxX, minY, maxZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(e, maxX, maxY, maxZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(e, minX, maxY, maxZ).color(color.r, color.g, color.b, color.a);
    }

    /**
     * Assumes a BufferBuilder in GL_QUADS mode has been initialized
     */
    public static void drawBoxTopBatchedQuads(float minX, float minZ, float maxX, float maxY, float maxZ, Color4f color, BufferBuilder buffer)
    {
        // Top side
        buffer.vertex(minX, maxY, maxZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(maxX, maxY, maxZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(maxX, maxY, minZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(minX, maxY, minZ).color(color.r, color.g, color.b, color.a);
    }

    public static void drawBoxTopBatchedQuads(float minX, float minZ, float maxX, float maxY, float maxZ, Color4f color, BufferBuilder buffer, MatrixStack.Entry e)
    {
        // Top side
        buffer.vertex(e, minX, maxY, maxZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(e, maxX, maxY, maxZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(e, maxX, maxY, minZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(e, minX, maxY, minZ).color(color.r, color.g, color.b, color.a);
    }

    /**
     * Assumes a BufferBuilder in GL_QUADS mode has been initialized
     */
    public static void drawBoxBottomBatchedQuads(float minX, float minY, float minZ, float maxX, float maxZ, Color4f color, BufferBuilder buffer)
    {
        // Bottom side
        buffer.vertex(maxX, minY, maxZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(minX, minY, maxZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(minX, minY, minZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(maxX, minY, minZ).color(color.r, color.g, color.b, color.a);
    }

    public static void drawBoxBottomBatchedQuads(float minX, float minY, float minZ, float maxX, float maxZ, Color4f color, BufferBuilder buffer, MatrixStack.Entry e)
    {
        // Bottom side
        buffer.vertex(e, maxX, minY, maxZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(e, minX, minY, maxZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(e, minX, minY, minZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(e, maxX, minY, minZ).color(color.r, color.g, color.b, color.a);
    }

    /**
     * Assumes a BufferBuilder in GL_LINES mode has been initialized
     */
    public static void drawBoxAllEdgesBatchedLines(float minX, float minY, float minZ, float maxX, float maxY, float maxZ,
                                                   Color4f color, BufferBuilder buffer, MatrixStack.Entry e)
    {
        // West side
        buffer.vertex(e, minX, minY, minZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, minX, minY, maxZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);

        buffer.vertex(e, minX, minY, maxZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, minX, maxY, maxZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);

        buffer.vertex(e, minX, maxY, maxZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, minX, maxY, minZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);

        buffer.vertex(e, minX, maxY, minZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, minX, minY, minZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);

        // East side
        buffer.vertex(e, maxX, minY, maxZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, maxX, minY, minZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);

        buffer.vertex(e, maxX, minY, minZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, maxX, maxY, minZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);

        buffer.vertex(e, maxX, maxY, minZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, maxX, maxY, maxZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);

        buffer.vertex(e, maxX, maxY, maxZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, maxX, minY, maxZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);

        // North side (don't repeat the vertical lines that are done by the east/west sides)
        buffer.vertex(e, maxX, minY, minZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, minX, minY, minZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);

        buffer.vertex(e, minX, maxY, minZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, maxX, maxY, minZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);

        // South side (don't repeat the vertical lines that are done by the east/west sides)
        buffer.vertex(e, minX, minY, maxZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, maxX, minY, maxZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);

        buffer.vertex(e, maxX, maxY, maxZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, minX, maxY, maxZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);
    }

    public static void drawBox(IntBoundingBox bb, Vec3d cameraPos, Color4f color, BufferBuilder bufferQuads, BufferBuilder bufferLines, MatrixStack matrices)
    {
        float minX = (float) (bb.minX - cameraPos.x);
        float minY = (float) (bb.minY - cameraPos.y);
        float minZ = (float) (bb.minZ - cameraPos.z);
        float maxX = (float) (bb.maxX + 1 - cameraPos.x);
        float maxY = (float) (bb.maxY + 1 - cameraPos.y);
        float maxZ = (float) (bb.maxZ + 1 - cameraPos.z);

        MatrixStack.Entry e = matrices.peek();
        drawBoxAllSidesBatchedQuads(minX, minY, minZ, maxX, maxY, maxZ, color, bufferQuads);
        drawBoxAllEdgesBatchedLines(minX, minY, minZ, maxX, maxY, maxZ, color, bufferLines, e);
    }

    public static void drawBoxNoOutlines(IntBoundingBox bb, Vec3d cameraPos, Color4f color, BufferBuilder bufferQuads)
    {
        float minX = (float) (bb.minX - cameraPos.x);
        float minY = (float) (bb.minY - cameraPos.y);
        float minZ = (float) (bb.minZ - cameraPos.z);
        float maxX = (float) (bb.maxX + 1 - cameraPos.x);
        float maxY = (float) (bb.maxY + 1 - cameraPos.y);
        float maxZ = (float) (bb.maxZ + 1 - cameraPos.z);

        drawBoxAllSidesBatchedQuads(minX, minY, minZ, maxX, maxY, maxZ, color, bufferQuads);
    }

    /**
     * Renders a text plate/billboard, similar to the player name plate.<br>
     * The plate will always face towards the viewer.
     *
     * @param text
     * @param x
     * @param y
     * @param z
     * @param scale
     */
    public static void drawTextPlate(List<String> text, double x, double y, double z, float scale)
    {
        Entity entity = mc().getCameraEntity();

        if (entity != null)
        {
            drawTextPlate(text, x, y, z, entity.getYaw(), entity.getPitch(), scale, 0xFFFFFFFF, 0x40000000, true);
        }
    }

    public static void drawTextPlate(List<String> text, double x, double y, double z, float yaw, float pitch,
                                     float scale, int textColor, int bgColor, boolean disableDepth)
    {
        Vec3d cameraPos = camPos();
        double cx = cameraPos.x;
        double cy = cameraPos.y;
        double cz = cameraPos.z;
        TextRenderer textRenderer = mc().textRenderer;

        Matrix4fStack global4fStack = RenderSystem.getModelViewStack();
        global4fStack.pushMatrix();

        global4fStack.translate((float) (x - cx), (float) (y - cy), (float) (z - cz));

        //  Wrap it with matrix4fRotateFix() if rotation errors are found.
        global4fStack.rotateYXZ((-yaw) * ((float) (Math.PI / 180.0)), pitch * ((float) (Math.PI / 180.0)), 0.0F);

        global4fStack.scale((-scale), (-scale), scale);
        //RenderSystem.applyModelViewMatrix();

        culling(false);
        blend(true);

        RenderContext ctx = new RenderContext(MaLiLibPipelines.POSITION_COLOR_MASA, BufferUsage.STATIC_WRITE);
        BufferBuilder buffer = ctx.getBuilder();
        int maxLineLen = 0;

        for (String line : text)
        {
            maxLineLen = Math.max(maxLineLen, textRenderer.getWidth(line));
        }

        int strLenHalf = maxLineLen / 2;
        int textHeight = textRenderer.fontHeight * text.size() - 1;
        int bga = ((bgColor >>> 24) & 0xFF);
        int bgr = ((bgColor >>> 16) & 0xFF);
        int bgg = ((bgColor >>> 8) & 0xFF);
        int bgb = (bgColor & 0xFF);

        if (disableDepth)
        {
            //RenderSystem.depthMask(false);
            depthTest(false);
        }

        buffer.vertex((float) (-strLenHalf - 1), (float) -1, 0.0F).color(bgr, bgg, bgb, bga);
        buffer.vertex((float) (-strLenHalf - 1), (float) textHeight, 0.0F).color(bgr, bgg, bgb, bga);
        buffer.vertex((float) strLenHalf, (float) textHeight, 0.0F).color(bgr, bgg, bgb, bga);
        buffer.vertex((float) strLenHalf, (float) -1, 0.0F).color(bgr, bgg, bgb, bga);

        try
        {
            ctx.draw(buffer.endNullable());
            ctx.close();
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("drawTextPlate(): Draw Exception; {}", err.getMessage());
        }

        int textY = 0;

        // translate the text a bit infront of the background
        if (disableDepth == false)
        {
            polygonOffset(true);
            polygonOffset(-0.6f, -1.2f);
        }

        Matrix4f modelMatrix = new Matrix4f();
        modelMatrix.identity();

        BufferAllocator allocator = new BufferAllocator(RenderLayer.DEFAULT_BUFFER_SIZE);

        for (String line : text)
        {
            if (disableDepth)
            {
                //depthMask(false);
                depthTest(false);
                VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(allocator);
                textRenderer.draw(line, -strLenHalf, textY, 0x20000000 | (textColor & 0xFFFFFF), false, modelMatrix, immediate, TextRenderer.TextLayerType.SEE_THROUGH, 0, 15728880);
                immediate.draw();
                depthTest(true);
                //depthMask(true);
            }

            VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(allocator);
            textRenderer.draw(line, -strLenHalf, textY, textColor, false, modelMatrix, immediate, TextRenderer.TextLayerType.SEE_THROUGH, 0, 15728880);
            immediate.draw();
            textY += textRenderer.fontHeight;
        }

        allocator.close();

        if (disableDepth == false)
        {
            polygonOffset(0f, 0f);
            polygonOffset(false);
        }

        color(1f, 1f, 1f, 1f);
        culling(true);
        //RenderSystem.disableBlend();
        global4fStack.popMatrix();
    }

    public static void renderBlockTargetingOverlay(Entity entity, BlockPos pos, Direction side, Vec3d hitVec,
                                                   Color4f color, Matrix4f posMatrix)
    {
        Direction playerFacing = entity.getHorizontalFacing();
        PositionUtils.HitPart part = PositionUtils.getHitPart(side, playerFacing, pos, hitVec);
        Vec3d cameraPos = camPos();

        double x = (pos.getX() + 0.5d - cameraPos.x);
        double y = (pos.getY() + 0.5d - cameraPos.y);
        double z = (pos.getZ() + 0.5d - cameraPos.z);

        Matrix4fStack global4fStack = RenderSystem.getModelViewStack();
        global4fStack.pushMatrix();
        blockTargetingOverlayTranslations(x, y, z, side, playerFacing, global4fStack);

        // Target "Side" -->
        // DEBUG_LINE_STRIP
        RenderContext ctx = new RenderContext(() -> "TestTarget A", MaLiLibPipelines.POSITION_COLOR_MASA_NO_DEPTH_NO_CULL, BufferUsage.STATIC_WRITE);
        BufferBuilder buffer = ctx.getBuilder();

        int quadAlpha = (int) (0.18f * 255f);
        int hr = (int) (color.r * 255f);
        int hg = (int) (color.g * 255f);
        int hb = (int) (color.b * 255f);
        int ha = (int) (color.a * 255f);
        int c = 255;

        // White full block background
        buffer.vertex((float) (x - 0.5), (float) (y - 0.5), (float) z).color(c, c, c, quadAlpha);
        buffer.vertex((float) (x + 0.5), (float) (y - 0.5), (float) z).color(c, c, c, quadAlpha);
        buffer.vertex((float) (x + 0.5), (float) (y + 0.5), (float) z).color(c, c, c, quadAlpha);
        buffer.vertex((float) (x - 0.5), (float) (y + 0.5), (float) z).color(c, c, c, quadAlpha);

        switch (part)
        {
            case CENTER:
                buffer.vertex((float) (x - 0.25), (float) (y - 0.25), (float) z).color(hr, hg, hb, ha);
                buffer.vertex((float) (x + 0.25), (float) (y - 0.25), (float) z).color(hr, hg, hb, ha);
                buffer.vertex((float) (x + 0.25), (float) (y + 0.25), (float) z).color(hr, hg, hb, ha);
                buffer.vertex((float) (x - 0.25), (float) (y + 0.25), (float) z).color(hr, hg, hb, ha);
                break;
            case LEFT:
                buffer.vertex((float) (x - 0.50), (float) (y - 0.50), (float) z).color(hr, hg, hb, ha);
                buffer.vertex((float) (x - 0.25), (float) (y - 0.25), (float) z).color(hr, hg, hb, ha);
                buffer.vertex((float) (x - 0.25), (float) (y + 0.25), (float) z).color(hr, hg, hb, ha);
                buffer.vertex((float) (x - 0.50), (float) (y + 0.50), (float) z).color(hr, hg, hb, ha);
                break;
            case RIGHT:
                buffer.vertex((float) (x + 0.50), (float) (y - 0.50), (float) z).color(hr, hg, hb, ha);
                buffer.vertex((float) (x + 0.25), (float) (y - 0.25), (float) z).color(hr, hg, hb, ha);
                buffer.vertex((float) (x + 0.25), (float) (y + 0.25), (float) z).color(hr, hg, hb, ha);
                buffer.vertex((float) (x + 0.50), (float) (y + 0.50), (float) z).color(hr, hg, hb, ha);
                break;
            case TOP:
                buffer.vertex((float) (x - 0.50), (float) (y + 0.50), (float) z).color(hr, hg, hb, ha);
                buffer.vertex((float) (x - 0.25), (float) (y + 0.25), (float) z).color(hr, hg, hb, ha);
                buffer.vertex((float) (x + 0.25), (float) (y + 0.25), (float) z).color(hr, hg, hb, ha);
                buffer.vertex((float) (x + 0.50), (float) (y + 0.50), (float) z).color(hr, hg, hb, ha);
                break;
            case BOTTOM:
                buffer.vertex((float) (x - 0.50), (float) (y - 0.50), (float) z).color(hr, hg, hb, ha);
                buffer.vertex((float) (x - 0.25), (float) (y - 0.25), (float) z).color(hr, hg, hb, ha);
                buffer.vertex((float) (x + 0.25), (float) (y - 0.25), (float) z).color(hr, hg, hb, ha);
                buffer.vertex((float) (x + 0.50), (float) (y - 0.50), (float) z).color(hr, hg, hb, ha);
                break;
            default:
        }

        try
        {
            ctx.draw(buffer.endNullable());
            ctx.reset();
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("renderBlockTargetingOverlay():1: Draw Exception; {}", err.getMessage());
        }

        RenderSystem.lineWidth(1.6f);
        int wireColor = -1;

        // Target "Center" -->
        // ShaderPipelines.DEBUG_LINE_STRIP
        buffer = ctx.start(() -> "TestTarget B", MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_NO_DEPTH_NO_CULL, BufferUsage.STATIC_WRITE);
        //ctx.setShader(MaLiLibPipelines.DEBUG_LINES_SIMPLE);

        MatrixStack matrices = new MatrixStack();

//        matrices.push();
        MatrixStack.Entry e = matrices.peek();

        // Middle small rectangle
        buffer.vertex((float) (x - 0.25), (float) (y - 0.25), (float) z).color(c, c, c, c);
        buffer.vertex((float) (x + 0.25), (float) (y - 0.25), (float) z).color(c, c, c, c);
        buffer.vertex((float) (x + 0.25), (float) (y + 0.25), (float) z).color(c, c, c, c);
        buffer.vertex((float) (x - 0.25), (float) (y + 0.25), (float) z).color(c, c, c, c);
        buffer.vertex((float) (x - 0.25), (float) (y - 0.25), (float) z).color(c, c, c, c);

//        buffer.vertex(e, (float) (x - 0.25), (float) (y - 0.25), (float) z).color(c, c, c, c).normal(e, 0.0f, 0.0f, 0.0f);
//        buffer.vertex(e, (float) (x + 0.25), (float) (y - 0.25), (float) z).color(c, c, c, c).normal(e, 0.0f, 0.0f, 0.0f);
//        buffer.vertex(e, (float) (x + 0.25), (float) (y + 0.25), (float) z).color(c, c, c, c).normal(e, 0.0f, 0.0f, 0.0f);
//        buffer.vertex(e, (float) (x - 0.25), (float) (y + 0.25), (float) z).color(c, c, c, c).normal(e, 0.0f, 0.0f, 0.0f);
//        buffer.vertex(e, (float) (x - 0.25), (float) (y - 0.25), (float) z).color(c, c, c, c).normal(e, 0.0f, 0.0f, 0.0f);

        try
        {
            ctx.color(wireColor);
            ctx.lineWidth(1.6f);
            ctx.draw(buffer.endNullable(), true);
            ctx.reset();
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("renderBlockTargetingOverlay():2: Draw Exception; {}", err.getMessage());
        }

//        matrices.pop();

        // Target "Edges" -->
        // RenderPipelines.LINES
        // MaLiLibPipelines.LINES_TRANSLUCENT_NO_DEPTH_NO_CULL
        buffer = ctx.start(() -> "TestTarget C", MaLiLibPipelines.LINES_MASA_SIMPLE_NO_DEPTH_NO_CULL, BufferUsage.STATIC_WRITE);
        //ctx.setShader(MaLiLibPipelines.DEBUG_LINES_SIMPLE);

//        // Bottom left
//        buffer.vertex((float) (x - 0.50), (float) (y - 0.50), (float) z).color(c, c, c, c);
//        buffer.vertex((float) (x - 0.25), (float) (y - 0.25), (float) z).color(c, c, c, c);
//
//        // Top left
//        buffer.vertex((float) (x - 0.50), (float) (y + 0.50), (float) z).color(c, c, c, c);
//        buffer.vertex((float) (x - 0.25), (float) (y + 0.25), (float) z).color(c, c, c, c);
//
//        // Bottom right
//        buffer.vertex((float) (x + 0.50), (float) (y - 0.50), (float) z).color(c, c, c, c);
//        buffer.vertex((float) (x + 0.25), (float) (y - 0.25), (float) z).color(c, c, c, c);
//
//        // Top right
//        buffer.vertex((float) (x + 0.50), (float) (y + 0.50), (float) z).color(c, c, c, c);
//        buffer.vertex((float) (x + 0.25), (float) (y + 0.25), (float) z).color(c, c, c, c);

//        matrices.push();
//        MatrixStack.Entry e = matrices.peek();
        e = matrices.peek();

        // Bottom left
        buffer.vertex(e, (float) (x - 0.50), (float) (y - 0.50), (float) z).color(c, c, c, c).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, (float) (x - 0.25), (float) (y - 0.25), (float) z).color(c, c, c, c).normal(e, 0.0f, 0.0f, 0.0f);

        // Top left
        buffer.vertex(e, (float) (x - 0.50), (float) (y + 0.50), (float) z).color(c, c, c, c).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, (float) (x - 0.25), (float) (y + 0.25), (float) z).color(c, c, c, c).normal(e, 0.0f, 0.0f, 0.0f);

        // Bottom right
        buffer.vertex(e, (float) (x + 0.50), (float) (y - 0.50), (float) z).color(c, c, c, c).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, (float) (x + 0.25), (float) (y - 0.25), (float) z).color(c, c, c, c).normal(e, 0.0f, 0.0f, 0.0f);

        // Top right
        buffer.vertex(e, (float) (x + 0.50), (float) (y + 0.50), (float) z).color(c, c, c, c).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, (float) (x + 0.25), (float) (y + 0.25), (float) z).color(c, c, c, c).normal(e, 0.0f, 0.0f, 0.0f);

        try
        {
            ctx.color(wireColor);
            ctx.lineWidth(1.6f);
            ctx.draw(buffer.endNullable(), true);
            ctx.close();
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("renderBlockTargetingOverlay():3: Draw Exception; {}", err.getMessage());
        }

//        matrices.pop();
        global4fStack.popMatrix();
        //RenderSystem.applyModelViewMatrix();
    }

    public static void renderBlockTargetingOverlaySimple(Entity entity, BlockPos pos, Direction side,
                                                         Color4f color, Matrix4f posMatrix)
    {
        Direction playerFacing = entity.getHorizontalFacing();
        Vec3d cameraPos = camPos();

        double x = pos.getX() + 0.5d - cameraPos.x;
        double y = pos.getY() + 0.5d - cameraPos.y;
        double z = pos.getZ() + 0.5d - cameraPos.z;

        Matrix4fStack global4fStack = RenderSystem.getModelViewStack();
        global4fStack.pushMatrix();

        blockTargetingOverlayTranslations(x, y, z, side, playerFacing, global4fStack);

        RenderContext ctx = new RenderContext(() -> "TestTarget A", MaLiLibPipelines.POSITION_COLOR_MASA_NO_DEPTH_NO_CULL, BufferUsage.STATIC_WRITE);
        BufferBuilder buffer = ctx.getBuilder();

        int a = (int) (color.a * 255f);
        int r = (int) (color.r * 255f);
        int g = (int) (color.g * 255f);
        int b = (int) (color.b * 255f);
        int c = 255;

        // Simple colored quad
        buffer.vertex((float) (x - 0.5), (float) (y - 0.5), (float) z).color(r, g, b, a);
        buffer.vertex((float) (x + 0.5), (float) (y - 0.5), (float) z).color(r, g, b, a);
        buffer.vertex((float) (x + 0.5), (float) (y + 0.5), (float) z).color(r, g, b, a);
        buffer.vertex((float) (x - 0.5), (float) (y + 0.5), (float) z).color(r, g, b, a);

        try
        {
            ctx.draw(buffer.endNullable());
            ctx.reset();
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("renderBlockTargetingOverlaySimple():1: Draw Exception; {}", err.getMessage());
        }

        RenderSystem.lineWidth(1.6f);

        buffer = ctx.start(() -> "TestTarget B", MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_NO_DEPTH_NO_CULL, BufferUsage.STATIC_WRITE);
        //ctx.setShader(ShaderPipelines.LINE_STRIP);

        // Middle rectangle
        buffer.vertex((float) (x - 0.375), (float) (y - 0.375), (float) z).color(c, c, c, c);
        buffer.vertex((float) (x + 0.375), (float) (y - 0.375), (float) z).color(c, c, c, c);
        buffer.vertex((float) (x + 0.375), (float) (y + 0.375), (float) z).color(c, c, c, c);
        buffer.vertex((float) (x - 0.375), (float) (y + 0.375), (float) z).color(c, c, c, c);

        try
        {
            ctx.lineWidth(1.6f);
            ctx.draw(buffer.endNullable(), true);
            ctx.close();
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("renderBlockTargetingOverlaySimple():2: Draw Exception; {}", err.getMessage());
        }

        global4fStack.popMatrix();
        //RenderSystem.applyModelViewMatrix();
    }

    /**
     * Matrix4f rotation adds direct values without adding these numbers.
     * (angle * 0.017453292F) --> easy fix with matrix4fRotateFix()
     */
    private static void blockTargetingOverlayTranslations(double x, double y, double z,
                                                          Direction side, Direction playerFacing, Matrix4fStack matrix4fStack)
    {
        matrix4fStack.translate((float) x, (float) y, (float) z);

        switch (side)
        {
            case DOWN:
                matrix4fStack.rotateY(matrix4fRotateFix(180f - playerFacing.getPositiveHorizontalDegrees()));
                matrix4fStack.rotateX(matrix4fRotateFix(90f));
                break;
            case UP:
                matrix4fStack.rotateY(matrix4fRotateFix(180f - playerFacing.getPositiveHorizontalDegrees()));
                matrix4fStack.rotateX(matrix4fRotateFix(-90f));
                break;
            case NORTH:
                matrix4fStack.rotateY(matrix4fRotateFix(180f));
                break;
            case SOUTH:
                break;
            case WEST:
                matrix4fStack.rotateY(matrix4fRotateFix(-90f));
                break;
            case EAST:
                matrix4fStack.rotateY(matrix4fRotateFix(90f));
                break;
        }

        matrix4fStack.translate((float) (-x), (float) (-y), (float) ((-z) + 0.510));
    }

    public static void renderMapPreview(ItemStack stack, int x, int y, int dimensions, DrawContext drawContext)
    {
        renderMapPreview(stack, x, y, dimensions, true, drawContext);
    }

    public static void renderMapPreview(ItemStack stack, int x, int y, int dimensions, boolean requireShift, DrawContext drawContext)
    {
        if (stack.getItem() instanceof FilledMapItem && (!requireShift || GuiBase.isShiftDown()))
        {
            forceDraw(drawContext);
            color(1f, 1f, 1f, 1f);

            int y1 = y - dimensions - 20;
            int y2 = y1 + dimensions;
            int x1 = x + 8;
            int x2 = x1 + dimensions;
            int z = 300;
            int uv = 0xF000F0;

            MapState mapState = FilledMapItem.getMapState(stack, mc().world);
            ComponentMap data = stack.getComponents();
            MapIdComponent mapId = data.get(DataComponentTypes.MAP_ID);

            Identifier bgTexture = mapState == null ? TEXTURE_MAP_BACKGROUND : TEXTURE_MAP_BACKGROUND_CHECKERBOARD;
            VertexConsumer vertex = bindGuiTexture(bgTexture, drawContext);
            Matrix4f matrix4f = drawContext.getMatrices().peek().getPositionMatrix();

            vertex.vertex(matrix4f, x1, y2, z).color(-1).texture(0.0f, 1.0f).light(uv);
            vertex.vertex(matrix4f, x2, y2, z).color(-1).texture(1.0f, 1.0f).light(uv);
            vertex.vertex(matrix4f, x2, y1, z).color(-1).texture(1.0f, 0.0f).light(uv);
            vertex.vertex(matrix4f, x1, y1, z).color(-1).texture(0.0f, 0.0f).light(uv);

            forceDraw(drawContext);

            if (mapState != null)
            {
                x1 += 8;
                y1 += 8;
                z = 310;
                BufferAllocator allocator = new BufferAllocator(RenderLayer.DEFAULT_BUFFER_SIZE);
                VertexConsumerProvider.Immediate consumer = VertexConsumerProvider.immediate(allocator);
                double scale = (double) (dimensions - 16) / 128.0D;

                MatrixStack matrixStack = new MatrixStack();
                matrixStack.push();
                matrixStack.translate(x1, y1, z);
                matrixStack.scale((float) scale, (float) scale, 0);

                MapRenderState mapRenderState = new MapRenderState();
                mc().getMapRenderer().update(mapId, mapState, mapRenderState);
                mc().getMapRenderer().draw(mapRenderState, matrixStack, consumer, false, uv);
                consumer.draw();
                matrixStack.pop();
                allocator.close();
            }
        }
    }

    public static void renderShulkerBoxPreview(ItemStack stack, int baseX, int baseY, boolean useBgColors, DrawContext drawContext)
    {
        DefaultedList<ItemStack> items;

        if (stack.getComponents().contains(DataComponentTypes.CONTAINER))
        {
            //items = InventoryUtils.getStoredItems(stack, ShulkerBoxBlockEntity.INVENTORY_SIZE);
            items = InventoryUtils.getStoredItems(stack, -1);

            if (items.isEmpty())
            {
                return;
            }

            NbtCompound nbt = InventoryUtils.getStoredBlockEntityNbt(stack);
            Set<Integer> lockedSlots = new HashSet<>();
            Inventory inv = InventoryUtils.getAsInventory(items);
            InventoryOverlay.InventoryRenderType type = InventoryOverlay.getInventoryType(stack);
            InventoryOverlay.InventoryProperties props = InventoryOverlay.getInventoryPropsTemp(type, items.size());

            int screenWidth = GuiUtils.getScaledWindowWidth();
            int screenHeight = GuiUtils.getScaledWindowHeight();
            int height = props.height + 18;
            int x = MathHelper.clamp(baseX + 8, 0, screenWidth - props.width);
            int y = MathHelper.clamp(baseY - height, 0, screenHeight - height);
            int color;

            // Mask items behind the shulker box display, trying to minimize the sharp corners
            //drawTexturedRect(GuiBase.BG_TEXTURE, x + 1, y + 1, 0, 0, props.width - 2, props.height - 2, drawContext);
            forceDraw(drawContext);
            //depthTest(true);

            if (stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() instanceof ShulkerBoxBlock)
            {
                color = setShulkerboxBackgroundTintColor((ShulkerBoxBlock) ((BlockItem) stack.getItem()).getBlock(), useBgColors);
            }
            else
            {
                color = color(1f, 1f, 1f, 1f);
            }

            disableDiffuseLighting();

            Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
            matrix4fStack.pushMatrix();
            matrix4fStack.translate(0, 0, 500);
            //drawContext.getMatrices().push();
            //drawContext.getMatrices().translate(0, 0, 500);
            //RenderSystem.applyModelViewMatrix();

            InventoryOverlay.renderInventoryBackground(type, x, y, props.slotsPerRow, props.totalSlots, color, mc(), drawContext);
            color = color(1f, 1f, 1f, 1f);

            enableDiffuseLightingGui3D();

            if (type == InventoryOverlay.InventoryRenderType.BREWING_STAND)
            {
                InventoryOverlay.renderBrewerBackgroundSlots(inv, x, y, drawContext);
            }

            if (type == InventoryOverlay.InventoryRenderType.CRAFTER && !nbt.isEmpty())
            {
                lockedSlots = NbtBlockUtils.getDisabledSlotsFromNbt(nbt);
                InventoryOverlay.renderInventoryStacks(type, inv, x + props.slotOffsetX, y + props.slotOffsetY, props.slotsPerRow, 0, -1, lockedSlots, mc(), drawContext);
            }
            else
            {
                InventoryOverlay.renderInventoryStacks(type, inv, x + props.slotOffsetX, y + props.slotOffsetY, props.slotsPerRow, 0, -1, mc(), drawContext);
            }

            matrix4fStack.popMatrix();
            //depthTest(false);
            //drawContext.getMatrices().pop();
            //forceDraw(drawContext);
            //RenderSystem.applyModelViewMatrix();
        }
    }

    public static void renderBundlePreview(ItemStack stack, int baseX, int baseY, boolean useBgColors, DrawContext drawContext)
    {
        // Default is 9 to make the default display the same as Shulker Boxes
        renderBundlePreview(stack, baseX, baseY, 9, useBgColors, drawContext);
    }

    public static void renderBundlePreview(ItemStack stack, int baseX, int baseY, int slotsPerRow, boolean useBgColors, DrawContext drawContext)
    {
        DefaultedList<ItemStack> items;

        if (stack.getComponents().contains(DataComponentTypes.BUNDLE_CONTENTS))
        {
            int count = InventoryUtils.bundleCountItems(stack);
            items = InventoryUtils.getBundleItems(stack, count);
            slotsPerRow = slotsPerRow != 9 ? MathUtils.clamp(slotsPerRow, 6, 9) : 9;

            if (items.isEmpty())
            {
                return;
            }

            Inventory inv = InventoryUtils.getAsInventory(items);
            InventoryOverlay.InventoryRenderType type = InventoryOverlay.getInventoryType(stack);
            InventoryOverlay.InventoryProperties props = InventoryOverlay.getInventoryPropsTemp(type, count, slotsPerRow);

            int screenWidth = GuiUtils.getScaledWindowWidth();
            int screenHeight = GuiUtils.getScaledWindowHeight();
            int height = props.height + 18;
            int x = MathHelper.clamp(baseX + 8, 0, screenWidth - props.width);
            int y = MathHelper.clamp(baseY - height, 0, screenHeight - height);

            // Mask items behind the shulker box display, trying to minimize the sharp corners
            //drawTexturedRect(GuiBase.BG_TEXTURE, x + 1, y + 1, 0, 0, props.width - 2, props.height - 2, drawContext);
            forceDraw(drawContext);
            //depthTest(true);
            int color = setBundleBackgroundTintColor(stack, useBgColors);
            disableDiffuseLighting();

            Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
            matrix4fStack.pushMatrix();
            matrix4fStack.translate(0, 0, 500);
            //drawContext.getMatrices().push();
            //drawContext.getMatrices().translate(0, 0, 500);
            //RenderSystem.applyModelViewMatrix();

            InventoryOverlay.renderInventoryBackground(type, x, y, props.slotsPerRow, props.totalSlots, color, mc(), drawContext);
            color(1f, 1f, 1f, 1f);

            enableDiffuseLightingGui3D();

            InventoryOverlay.renderInventoryStacks(type, inv, x + props.slotOffsetX, y + props.slotOffsetY, props.slotsPerRow, 0, count, mc(), drawContext);

            matrix4fStack.popMatrix();
            //depthTest(false);
            //RenderSystem.disableDepthTest();
            //drawContext.getMatrices().pop();
            //forceDraw(drawContext);
            //RenderSystem.applyModelViewMatrix();
        }
    }

    /**
     * Render's the Inventory Overlay using an NbtCompound Items[] List format instead of the Item Container Component,
     * Such as for a Crafter, etc.  This is meant to be simillar to the 1.20.4 behavior, minus the "BlockEntityTag";
     * since it no longer exists; but this can be used as such, if the "BlockEntityTag" or its eqivalent, is read in first.
     * -
     *
     * @param stackIn     (Stack of the Entity for selecting the right textures)
     * @param itemsTag    (Nbt Items[] list)
     * @param baseX
     * @param baseY
     * @param useBgColors
     * @param drawContext
     */
    public static void renderNbtItemsPreview(ItemStack stackIn, @Nonnull NbtCompound itemsTag, int baseX, int baseY, boolean useBgColors, DrawContext drawContext)
    {
        if (InventoryUtils.hasNbtItems(itemsTag))
        {
            if (mc().world == null)
            {
                return;
            }
            DefaultedList<ItemStack> items = InventoryUtils.getNbtItems(itemsTag, -1, mc().world.getRegistryManager());

            if (items.size() == 0)
            {
                return;
            }

            InventoryOverlay.InventoryRenderType type = InventoryOverlay.getInventoryType(stackIn);
            InventoryOverlay.InventoryProperties props = InventoryOverlay.getInventoryPropsTemp(type, items.size());

            int screenWidth = GuiUtils.getScaledWindowWidth();
            int screenHeight = GuiUtils.getScaledWindowHeight();
            int height = props.height + 18;
            int x = MathHelper.clamp(baseX + 8, 0, screenWidth - props.width);
            int y = MathHelper.clamp(baseY - height, 0, screenHeight - height);

            // Mask items behind the shulker box display, trying to minimize the sharp corners
            //drawTexturedRect(GuiBase.BG_TEXTURE, x + 1, y + 1, 0, 0, props.width - 2, props.height - 2, drawContext);
            forceDraw(drawContext);
            //depthTest(true);

            int color = color(1f, 1f, 1f, 1f);
            disableDiffuseLighting();

            Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
            matrix4fStack.pushMatrix();
            matrix4fStack.translate(0, 0, 500);
            //drawContext.getMatrices().push();
            //drawContext.getMatrices().translate(0, 0, 500);
            //RenderSystem.applyModelViewMatrix();

            InventoryOverlay.renderInventoryBackground(type, x, y, props.slotsPerRow, items.size(), color, mc(), drawContext);

            enableDiffuseLightingGui3D();

            Inventory inv = InventoryUtils.getAsInventory(items);
            InventoryOverlay.renderInventoryStacks(type, inv, x + props.slotOffsetX, y + props.slotOffsetY, props.slotsPerRow, 0, -1, mc(), drawContext);

            matrix4fStack.popMatrix();
            //depthTest(false);
            //drawContext.getMatrices().pop();
            //forceDraw(drawContext);
            //RenderSystem.applyModelViewMatrix();
        }
    }

    /**
     * Calls RenderUtils.color() with the dye color of the provided shulker box block's color
     *
     * @param block
     * @param useBgColors
     */
    public static int setShulkerboxBackgroundTintColor(@Nullable ShulkerBoxBlock block, boolean useBgColors)
    {
        if (block != null && useBgColors)
        {
            // In 1.13+ there is the uncolored Shulker Box variant, which returns null from getColor()
            final DyeColor dye = block.getColor() != null ? block.getColor() : DyeColor.PURPLE;
            final float[] colors = getColorComponents(dye.getEntityColor());
            return color(colors[0], colors[1], colors[2], 1f);
        }
        else
        {
            return color(1f, 1f, 1f, 1f);
        }
    }

    /**
     * Copied from 1.20.6 DyeColor for compatibility
     *
     * @param color (Color int / entityColor)
     * @return (float[] of color, old DyeColor method)
     */
    public static float[] getColorComponents(int color)
    {
        int j = (color & 16711680) >> 16;
        int k = (color & '\uff00') >> 8;
        int l = (color & 255) >> 0;

        return new float[]{(float) j / 255.0F, (float) k / 255.0F, (float) l / 255.0F};
    }

    public static int setBundleBackgroundTintColor(ItemStack bundle, boolean useBgColors)
    {
        if (useBgColors)
        {
            final DyeColor dye = getBundleColor(bundle);

            if (dye != null)
            {
                final float[] colors = getColorComponents(dye.getEntityColor());
                return color(colors[0], colors[1], colors[2], 1f);
            }
        }

        return color(1f, 1f, 1f, 1f);
    }

    public static DyeColor getBundleColor(ItemStack bundle)
    {
        Item item = bundle.getItem();

        if (item == null)
        {
            return null;
        }
        if (item.equals(Items.WHITE_BUNDLE))
        {
            return DyeColor.WHITE;
        }
        else if (item.equals(Items.ORANGE_BUNDLE))
        {
            return DyeColor.ORANGE;
        }
        else if (item.equals(Items.MAGENTA_BUNDLE))
        {
            return DyeColor.MAGENTA;
        }
        else if (item.equals(Items.LIGHT_BLUE_BUNDLE))
        {
            return DyeColor.LIGHT_BLUE;
        }
        else if (item.equals(Items.YELLOW_BUNDLE))
        {
            return DyeColor.YELLOW;
        }
        else if (item.equals(Items.LIME_BUNDLE))
        {
            return DyeColor.LIME;
        }
        else if (item.equals(Items.PINK_BUNDLE))
        {
            return DyeColor.PINK;
        }
        else if (item.equals(Items.GRAY_BUNDLE))
        {
            return DyeColor.GRAY;
        }
        else if (item.equals(Items.LIGHT_GRAY_BUNDLE))
        {
            return DyeColor.LIGHT_GRAY;
        }
        else if (item.equals(Items.CYAN_BUNDLE))
        {
            return DyeColor.CYAN;
        }
        else if (item.equals(Items.BLUE_BUNDLE))
        {
            return DyeColor.BLUE;
        }
        else if (item.equals(Items.BROWN_BUNDLE))
        {
            return DyeColor.BROWN;
        }
        else if (item.equals(Items.GREEN_BUNDLE))
        {
            return DyeColor.GREEN;
        }
        else if (item.equals(Items.RED_BUNDLE))
        {
            return DyeColor.RED;
        }
        else if (item.equals(Items.BLACK_BUNDLE))
        {
            return DyeColor.BLACK;
        }
        else if (item.equals(Items.PURPLE_BUNDLE))
        {
            return DyeColor.PURPLE;
        }
        else
        {
            return null;
        }
    }

    public static int setVillagerBackgroundTintColor(VillagerData data, boolean useBgColors)
    {
        if (useBgColors)
        {
            RegistryEntry<VillagerProfession> profession = data != null ? data.profession() : null;
            return setVillagerBackgroundTintColor(profession, useBgColors);
        }

        return color(1f, 1f, 1f, 1f);
    }

    public static int setVillagerBackgroundTintColor(RegistryEntry<VillagerProfession> profession, boolean useBgColors)
    {
        if (useBgColors)
        {
            final DyeColor dye = getVillagerColor(profession);

            if (dye != null)
            {
                final float[] colors = getColorComponents(dye.getEntityColor());
                return color(colors[0], colors[1], colors[2], 1f);
            }
        }

        return color(1f, 1f, 1f, 1f);
    }

    public static DyeColor getVillagerColor(RegistryEntry<VillagerProfession> profession)
    {
        if (profession == null) return null;

        if (profession.equals(VillagerProfession.NONE))
        {
            return DyeColor.BLUE;
        }
        else if (profession.matchesKey(VillagerProfession.ARMORER))
        {
            return DyeColor.GRAY;
        }
        else if (profession.matchesKey(VillagerProfession.BUTCHER))
        {
            return DyeColor.PINK;
        }
        else if (profession.matchesKey(VillagerProfession.CARTOGRAPHER))
        {
            return DyeColor.LIGHT_BLUE;
        }
        else if (profession.matchesKey(VillagerProfession.CLERIC))
        {
            return DyeColor.PURPLE;
        }
        else if (profession.matchesKey(VillagerProfession.FARMER))
        {
            return DyeColor.YELLOW;
        }
        else if (profession.matchesKey(VillagerProfession.FISHERMAN))
        {
            return DyeColor.CYAN;
        }
        else if (profession.matchesKey(VillagerProfession.FLETCHER))
        {
            return DyeColor.ORANGE;
        }
        else if (profession.matchesKey(VillagerProfession.LEATHERWORKER))
        {
            return DyeColor.BROWN;
        }
        else if (profession.matchesKey(VillagerProfession.LIBRARIAN))
        {
            return DyeColor.RED;
        }
        else if (profession.matchesKey(VillagerProfession.MASON))
        {
            return DyeColor.MAGENTA;
        }
        else if (profession.matchesKey(VillagerProfession.NITWIT))
        {
            return DyeColor.GREEN;
        }
        else if (profession.matchesKey(VillagerProfession.SHEPHERD))
        {
            return DyeColor.WHITE;
        }
        else if (profession.matchesKey(VillagerProfession.TOOLSMITH))
        {
            return DyeColor.LIGHT_GRAY;
        }
        else if (profession.matchesKey(VillagerProfession.WEAPONSMITH))
        {
            return DyeColor.BLACK;
        }
        else
        {
            // Unhandled Profession
            return DyeColor.LIME;
        }
    }

    @SuppressWarnings("deprecation")
    public static void renderModelInGui(int x, int y, BlockStateModel model, BlockState state, float zLevel, DrawContext context)
    {
        if (state.getBlock() == Blocks.AIR)
        {
            return;
        }

        Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
        matrix4fStack.pushMatrix();
        bindGuiOverlayTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, context);
        tex().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).setFilter(false, false);

        blend(true);
        color(1f, 1f, 1f, 1f);

        //setupGuiTransform(x, y, model.hasDepth(), zLevel);
        setupGuiTransform(x, y, zLevel);

        matrix4fStack.rotateX(matrix4fRotateFix(30));
        matrix4fStack.rotateY(matrix4fRotateFix(225));
        matrix4fStack.scale(0.625f, 0.625f, 0.625f);

        renderBlockModel(model, state);
        //blend(false);
        matrix4fStack.popMatrix();
    }

    public static void setupGuiTransform(int xPosition, int yPosition, float zLevel)
    {
        setupGuiTransform(RenderSystem.getModelViewStack(), xPosition, yPosition, zLevel);
    }

    public static void setupGuiTransform(Matrix4fStack matrix4fStack, int xPosition, int yPosition, float zLevel)
    {
        matrix4fStack.translate((float) (xPosition + 8.0), (float) (yPosition + 8.0), (float) (zLevel + 100.0));
        matrix4fStack.scale((float) 16, (float) -16, (float) 16);
    }

    // FIXME, is this even used?
    public static void renderBlockModel(BlockStateModel model, BlockState state)
    {
        Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
        matrix4fStack.pushMatrix();

        matrix4fStack.translate((float) -0.5, (float) -0.5, (float) -0.5);
        int color = 0xFFFFFFFF;

        // TODO watch for side effects
        //if (model.isBuiltin() == false)
        //{
        //RenderSystem.setShader(ShaderProgramKeys.RENDERTYPE_SOLID);

        RenderContext ctx = new RenderContext(RenderPipelines.SOLID, BufferUsage.STATIC_WRITE);
        BufferBuilder buffer = ctx.getBuilder();

        for (Direction face : Direction.values())
        {
            RAND.setSeed(0);
            //renderQuads(buffer, model.getQuads(state, face, RAND), state, color);
            renderQuads(buffer, model.getParts(RAND), face, state, color);
        }

        RAND.setSeed(0);
        renderQuads(buffer, model.getParts(RAND), null, state, color);

        try
        {
            ctx.draw(buffer.endNullable());
            ctx.close();
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("renderBlockModel(): Draw Exception; {}", err.getMessage());
        }
        matrix4fStack.popMatrix();
    }

    private static void renderQuads(BufferBuilder renderer, List<BlockModelPart> quadlist, Direction face, BlockState state, int color)
    {
        for (BlockModelPart entry : quadlist)
        {
            List<BakedQuad> quads = entry.getQuads(face);
            final int quadCount = quads.size();

            for (int i = 0; i < quadCount; ++i)
            {
                BakedQuad quad = quads.get(i);
                renderQuad(renderer, quad, state, 0xFFFFFFFF);
            }
        }
    }

    private static void renderQuad(BufferBuilder buffer, BakedQuad quad, BlockState state, int color)
    {
        /*
        buffer.putVertexData(quad.getVertexData());
        buffer.setQuadColor(color);

        if (quad.hasColor())
        {
            BlockColors blockColors = mc().getBlockColorMap();
            int m = blockColors.getColorMultiplier(state, null, null, quad.getColorIndex());

            float r = (float) (m >>> 16 & 0xFF) / 255F;
            float g = (float) (m >>>  8 & 0xFF) / 255F;
            float b = (float) (m        & 0xFF) / 255F;
            buffer.multiplyColor(r, g, b, 4);
            buffer.multiplyColor(r, g, b, 3);
            buffer.multiplyColor(r, g, b, 2);
            buffer.multiplyColor(r, g, b, 1);
        }

        putQuadNormal(buffer, quad);
    }

    private static void putQuadNormal(BufferBuilder renderer, BakedQuad quad)
    {
        Vec3i direction = quad.getFace().getVector();
        renderer.normal(direction.getX(), direction.getY(), direction.getZ());
        */
    }

    public static MinecraftClient mc()
    {
        return MinecraftClient.getInstance();
    }

    public static Framebuffer fb()
    {
        return mc().getFramebuffer();
    }

    public static Vec3d camPos()
    {
        return mc().gameRenderer.getCamera().getPos();
    }

    public static TextureManager tex()
    {
        return mc().getTextureManager();
    }

    /*
    public static void enableGUIStandardItemLighting(float scale)
    {
        RenderSystem.pushMatrix();
        RenderSystem.rotate(-30.0F, 0.0F, 1.0F, 0.0F);
        RenderSystem.rotate(165.0F, 1.0F, 0.0F, 0.0F);

        enableStandardItemLighting(scale);

        RenderSystem.popMatrix();
    }

    public static void enableStandardItemLighting(float scale)
    {
        RenderSystem.enableLighting();
        RenderSystem.enableLight(0);
        RenderSystem.enableLight(1);
        RenderSystem.enableColorMaterial();
        RenderUtils.colorMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_AMBIENT_AND_DIFFUSE);
        RenderSystem.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, RenderHelper.setColorBuffer((float) LIGHT0_POS.x, (float) LIGHT0_POS.y, (float) LIGHT0_POS.z, 0.0f));

        float lightStrength = 0.3F * scale;
        RenderSystem.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, RenderHelper.setColorBuffer(lightStrength, lightStrength, lightStrength, 1.0F));
        RenderSystem.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        RenderSystem.glLight(GL11.GL_LIGHT0, GL11.GL_SPECULAR, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        RenderSystem.glLight(GL11.GL_LIGHT1, GL11.GL_POSITION, RenderHelper.setColorBuffer((float) LIGHT1_POS.x, (float) LIGHT1_POS.y, (float) LIGHT1_POS.z, 0.0f));
        RenderSystem.glLight(GL11.GL_LIGHT1, GL11.GL_DIFFUSE, RenderHelper.setColorBuffer(lightStrength, lightStrength, lightStrength, 1.0F));
        RenderSystem.glLight(GL11.GL_LIGHT1, GL11.GL_AMBIENT, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        RenderSystem.glLight(GL11.GL_LIGHT1, GL11.GL_SPECULAR, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));

        RenderSystem.shadeModel(GL11.GL_FLAT);

        float ambientLightStrength = 0.4F;
        RenderSystem.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, RenderHelper.setColorBuffer(ambientLightStrength, ambientLightStrength, ambientLightStrength, 1.0F));
    }
    */

    /**
     * Only required for translating the values to their RotationAxis.POSITIVE_?.rotationDegrees() equivalence
     */
    public static float matrix4fRotateFix(float ang) {return (ang * 0.017453292F);}

    public static void renderBlockOutline(BlockPos pos, float expand, float lineWidth, Color4f color)
    {
//        RenderSystem.lineWidth(lineWidth);

        // MaLiLibPipelines.LINES_NO_DEPTH_NO_CULL
        RenderContext ctx = new RenderContext(MaLiLibPipelines.LINES_MASA_SIMPLE_NO_DEPTH_NO_CULL, BufferUsage.STATIC_WRITE);
        BufferBuilder buffer = ctx.getBuilder();
        MatrixStack matrices = new MatrixStack();

        matrices.push();
        drawBlockBoundingBoxOutlinesBatchedLinesSimple(pos, color, expand, buffer, matrices);

        try
        {
            ctx.lineWidth(lineWidth);
            ctx.draw(buffer.endNullable(), true);
            ctx.close();
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("renderBlockOutline(): Draw Exception; {}", err.getMessage());
        }
        matrices.pop();
    }

    public static void drawBlockBoundingBoxOutlinesBatchedLinesSimple(BlockPos pos, Color4f color,
                                                                       double expand, BufferBuilder buffer, MatrixStack matrices)
    {
        Vec3d cameraPos = camPos();
        final double dx = cameraPos.x;
        final double dy = cameraPos.y;
        final double dz = cameraPos.z;

        float minX = (float) (pos.getX() - dx - expand);
        float minY = (float) (pos.getY() - dy - expand);
        float minZ = (float) (pos.getZ() - dz - expand);
        float maxX = (float) (pos.getX() - dx + expand + 1);
        float maxY = (float) (pos.getY() - dy + expand + 1);
        float maxZ = (float) (pos.getZ() - dz + expand + 1);

        drawBoxAllEdgesBatchedLines(minX, minY, minZ, maxX, maxY, maxZ, color, buffer, matrices.peek());
    }

    public static void drawConnectingLineBatchedLines(BlockPos pos1, BlockPos pos2, boolean center,
                                                      Color4f color, BufferBuilder buffer, MatrixStack matrices)
    {
        Vec3d cameraPos = camPos();
        final double dx = cameraPos.x;
        final double dy = cameraPos.y;
        final double dz = cameraPos.z;

        float x1 = (float) (pos1.getX() - dx);
        float y1 = (float) (pos1.getY() - dy);
        float z1 = (float) (pos1.getZ() - dz);
        float x2 = (float) (pos2.getX() - dx);
        float y2 = (float) (pos2.getY() - dy);
        float z2 = (float) (pos2.getZ() - dz);

        if (center)
        {
            x1 += 0.5F;
            y1 += 0.5F;
            z1 += 0.5F;
            x2 += 0.5F;
            y2 += 0.5F;
            z2 += 0.5F;
        }

        matrices.push();
        MatrixStack.Entry e = matrices.peek();

        buffer.vertex(e, x1, y1, z1).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, x2, y2, z2).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);
        matrices.pop();
    }

    public static void renderBlockOutlineOverlapping(BlockPos pos, float expand, float lineWidth,
                                                     Color4f color1, Color4f color2, Color4f color3, Matrix4f matrix4f)
    {
        Vec3d cameraPos = camPos();
        final double dx = cameraPos.x;
        final double dy = cameraPos.y;
        final double dz = cameraPos.z;

        final float minX = (float) (pos.getX() - dx - expand);
        final float minY = (float) (pos.getY() - dy - expand);
        final float minZ = (float) (pos.getZ() - dz - expand);
        final float maxX = (float) (pos.getX() - dx + expand + 1);
        final float maxY = (float) (pos.getY() - dy + expand + 1);
        final float maxZ = (float) (pos.getZ() - dz + expand + 1);

//        RenderSystem.lineWidth(lineWidth);

        // RenderPipelines.LINES
        // MaLiLibPipelines.LINES_NO_DEPTH_NO_CULL
        RenderContext ctx = new RenderContext(MaLiLibPipelines.LINES_MASA_SIMPLE_OFFSET_2, BufferUsage.STATIC_WRITE);
        BufferBuilder buffer = ctx.getBuilder();
        MatrixStack matrices = new MatrixStack();

        matrices.push();
        MatrixStack.Entry e = matrices.peek();

        // Min corner
        buffer.vertex(e, minX, minY, minZ).color(color1.r, color1.g, color1.b, color1.a).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, maxX, minY, minZ).color(color1.r, color1.g, color1.b, color1.a).normal(e, 0.0f, 0.0f, 0.0f);

        buffer.vertex(e, minX, minY, minZ).color(color1.r, color1.g, color1.b, color1.a).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, minX, maxY, minZ).color(color1.r, color1.g, color1.b, color1.a).normal(e, 0.0f, 0.0f, 0.0f);

        buffer.vertex(e, minX, minY, minZ).color(color1.r, color1.g, color1.b, color1.a).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, minX, minY, maxZ).color(color1.r, color1.g, color1.b, color1.a).normal(e, 0.0f, 0.0f, 0.0f);

        // Max corner
        buffer.vertex(e, minX, maxY, maxZ).color(color2.r, color2.g, color2.b, color2.a).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, maxX, maxY, maxZ).color(color2.r, color2.g, color2.b, color2.a).normal(e, 0.0f, 0.0f, 0.0f);

        buffer.vertex(e, maxX, minY, maxZ).color(color2.r, color2.g, color2.b, color2.a).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, maxX, maxY, maxZ).color(color2.r, color2.g, color2.b, color2.a).normal(e, 0.0f, 0.0f, 0.0f);

        buffer.vertex(e, maxX, maxY, minZ).color(color2.r, color2.g, color2.b, color2.a).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, maxX, maxY, maxZ).color(color2.r, color2.g, color2.b, color2.a).normal(e, 0.0f, 0.0f, 0.0f);

        // The rest of the edges
        buffer.vertex(e, minX, maxY, minZ).color(color3.r, color3.g, color3.b, color3.a).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, maxX, maxY, minZ).color(color3.r, color3.g, color3.b, color3.a).normal(e, 0.0f, 0.0f, 0.0f);

        buffer.vertex(e, minX, minY, maxZ).color(color3.r, color3.g, color3.b, color3.a).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, maxX, minY, maxZ).color(color3.r, color3.g, color3.b, color3.a).normal(e, 0.0f, 0.0f, 0.0f);

        buffer.vertex(e, maxX, minY, minZ).color(color3.r, color3.g, color3.b, color3.a).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, maxX, maxY, minZ).color(color3.r, color3.g, color3.b, color3.a).normal(e, 0.0f, 0.0f, 0.0f);

        buffer.vertex(e, minX, minY, maxZ).color(color3.r, color3.g, color3.b, color3.a).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, minX, maxY, maxZ).color(color3.r, color3.g, color3.b, color3.a).normal(e, 0.0f, 0.0f, 0.0f);

        buffer.vertex(e, maxX, minY, minZ).color(color3.r, color3.g, color3.b, color3.a).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, maxX, minY, maxZ).color(color3.r, color3.g, color3.b, color3.a).normal(e, 0.0f, 0.0f, 0.0f);

        buffer.vertex(e, minX, maxY, minZ).color(color3.r, color3.g, color3.b, color3.a).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, minX, maxY, maxZ).color(color3.r, color3.g, color3.b, color3.a).normal(e, 0.0f, 0.0f, 0.0f);

        try
        {
            ctx.lineWidth(lineWidth);
            ctx.draw(buffer.endNullable(), true);
            ctx.close();
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("renderBlockOutlineOverlapping(): Draw Exception; {}", err.getMessage());
        }
        matrices.pop();
    }

    public static void renderAreaOutline(BlockPos pos1, BlockPos pos2, float lineWidth,
                                         Color4f colorX, Color4f colorY, Color4f colorZ)
    {
        RenderSystem.lineWidth(lineWidth);

        Vec3d cameraPos = camPos();
        final double dx = cameraPos.x;
        final double dy = cameraPos.y;
        final double dz = cameraPos.z;

        double minX = Math.min(pos1.getX(), pos2.getX()) - dx;
        double minY = Math.min(pos1.getY(), pos2.getY()) - dy;
        double minZ = Math.min(pos1.getZ(), pos2.getZ()) - dz;
        double maxX = Math.max(pos1.getX(), pos2.getX()) - dx + 1;
        double maxY = Math.max(pos1.getY(), pos2.getY()) - dy + 1;
        double maxZ = Math.max(pos1.getZ(), pos2.getZ()) - dz + 1;

        drawBoundingBoxEdges((float) minX, (float) minY, (float) minZ, (float) maxX, (float) maxY, (float) maxZ, colorX, colorY, colorZ);
    }

    private static void drawBoundingBoxEdges(float minX, float minY, float minZ, float maxX, float maxY, float maxZ,
                                             Color4f colorX, Color4f colorY, Color4f colorZ)
    {
        // RenderPipelines.LINES
        // MaLiLibPipelines.LINES_NO_DEPTH_NO_CULL
        RenderContext ctx = new RenderContext(RenderPipelines.LINES, BufferUsage.STATIC_WRITE);
        BufferBuilder buffer = ctx.getBuilder();
        MatrixStack matrices = new MatrixStack();

        matrices.push();
        MatrixStack.Entry e = matrices.peek();

        drawBoundingBoxLinesX(buffer, minX, minY, minZ, maxX, maxY, maxZ, colorX, e);
        drawBoundingBoxLinesY(buffer, minX, minY, minZ, maxX, maxY, maxZ, colorY, e);
        drawBoundingBoxLinesZ(buffer, minX, minY, minZ, maxX, maxY, maxZ, colorZ, e);

        try
        {
            ctx.draw(buffer.endNullable());
            ctx.close();
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("drawBoundingBoxEdges(): Draw Exception; {}", err.getMessage());
        }
        matrices.pop();
    }

    private static void drawBoundingBoxLinesX(BufferBuilder buffer, float minX, float minY, float minZ, float maxX, float maxY, float maxZ,
                                              Color4f color, MatrixStack.Entry e)
    {
        buffer.vertex(e, minX, minY, minZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, maxX, minY, minZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);

        buffer.vertex(e, minX, maxY, minZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, maxX, maxY, minZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);

        buffer.vertex(e, minX, minY, maxZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, maxX, minY, maxZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);

        buffer.vertex(e, minX, maxY, maxZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, maxX, maxY, maxZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);
    }

    private static void drawBoundingBoxLinesY(BufferBuilder buffer, float minX, float minY, float minZ, float maxX, float maxY, float maxZ,
                                              Color4f color, MatrixStack.Entry e)
    {
        buffer.vertex(e, minX, minY, minZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, minX, maxY, minZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);

        buffer.vertex(e, maxX, minY, minZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, maxX, maxY, minZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);

        buffer.vertex(e, minX, minY, maxZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, minX, maxY, maxZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);

        buffer.vertex(e, maxX, minY, maxZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, maxX, maxY, maxZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);
    }

    private static void drawBoundingBoxLinesZ(BufferBuilder buffer, float minX, float minY, float minZ, float maxX, float maxY, float maxZ,
                                              Color4f color, MatrixStack.Entry e)
    {
        buffer.vertex(e, minX, minY, minZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, minX, minY, maxZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);

        buffer.vertex(e, maxX, minY, minZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, maxX, minY, maxZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);

        buffer.vertex(e, minX, maxY, minZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, minX, maxY, maxZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);

        buffer.vertex(e, maxX, maxY, minZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);
        buffer.vertex(e, maxX, maxY, maxZ).color(color.r, color.g, color.b, color.a).normal(e, 0.0f, 0.0f, 0.0f);
    }

    public static void renderAreaSides(BlockPos pos1, BlockPos pos2, Color4f color, Matrix4f matrix4f)
    {
        blend(true);
        culling(false);

        // MaLiLibPipelines.POSITION_COLOR_MASA_NO_DEPTH_NO_CULL
        RenderContext ctx = new RenderContext(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_LESSER_DEPTH_OFFSET_2, BufferUsage.STATIC_WRITE);
        BufferBuilder buffer = ctx.getBuilder();

        renderAreaSidesBatched(pos1, pos2, color, 0.002, buffer);

        try
        {
            ctx.draw(buffer.endNullable());
            ctx.close();
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("renderAreaSides(): Draw Exception; {}", err.getMessage());
        }
        culling(true);
        blend(false);
    }

    /**
     * Assumes a BufferBuilder in GL_QUADS mode has been initialized
     */
    public static void renderAreaSidesBatched(BlockPos pos1, BlockPos pos2, Color4f color,
                                              double expand, BufferBuilder buffer)
    {
        Vec3d cameraPos = camPos();
        final double dx = cameraPos.x;
        final double dy = cameraPos.y;
        final double dz = cameraPos.z;
        double minX = Math.min(pos1.getX(), pos2.getX()) - dx - expand;
        double minY = Math.min(pos1.getY(), pos2.getY()) - dy - expand;
        double minZ = Math.min(pos1.getZ(), pos2.getZ()) - dz - expand;
        double maxX = Math.max(pos1.getX(), pos2.getX()) + 1 - dx + expand;
        double maxY = Math.max(pos1.getY(), pos2.getY()) + 1 - dy + expand;
        double maxZ = Math.max(pos1.getZ(), pos2.getZ()) + 1 - dz + expand;

        drawBoxAllSidesBatchedQuads((float) minX, (float) minY, (float) minZ, (float) maxX, (float) maxY, (float) maxZ, color, buffer);
    }

    public static void renderAreaOutlineNoCorners(BlockPos pos1, BlockPos pos2,
                                                  float lineWidth, Color4f colorX, Color4f colorY, Color4f colorZ)
    {
        final int xMin = Math.min(pos1.getX(), pos2.getX());
        final int yMin = Math.min(pos1.getY(), pos2.getY());
        final int zMin = Math.min(pos1.getZ(), pos2.getZ());
        final int xMax = Math.max(pos1.getX(), pos2.getX());
        final int yMax = Math.max(pos1.getY(), pos2.getY());
        final int zMax = Math.max(pos1.getZ(), pos2.getZ());

        final double expand = 0.001;
        Vec3d cameraPos = camPos();
        final double dx = cameraPos.x;
        final double dy = cameraPos.y;
        final double dz = cameraPos.z;

        final float dxMin = (float) (-dx - expand);
        final float dyMin = (float) (-dy - expand);
        final float dzMin = (float) (-dz - expand);
        final float dxMax = (float) (-dx + expand);
        final float dyMax = (float) (-dy + expand);
        final float dzMax = (float) (-dz + expand);

        final float minX = xMin + dxMin;
        final float minY = yMin + dyMin;
        final float minZ = zMin + dzMin;
        final float maxX = xMax + dxMax;
        final float maxY = yMax + dyMax;
        final float maxZ = zMax + dzMax;

        int start, end;

//        RenderSystem.lineWidth(lineWidth);

        // RenderPipelines.LINES
        // MaLiLibPipelines.LINES_NO_DEPTH_NO_CULL
        RenderContext ctx = new RenderContext(RenderPipelines.LINES, BufferUsage.STATIC_WRITE);
        BufferBuilder buffer = ctx.getBuilder();
        MatrixStack matrices = new MatrixStack();

        matrices.push();
        MatrixStack.Entry e = matrices.peek();

        // Edges along the X-axis
        start = (pos1.getX() == xMin && pos1.getY() == yMin && pos1.getZ() == zMin) || (pos2.getX() == xMin && pos2.getY() == yMin && pos2.getZ() == zMin) ? xMin + 1 : xMin;
        end   = (pos1.getX() == xMax && pos1.getY() == yMin && pos1.getZ() == zMin) || (pos2.getX() == xMax && pos2.getY() == yMin && pos2.getZ() == zMin) ? xMax : xMax + 1;

        if (end > start)
        {
            buffer.vertex(e, start + dxMin, minY, minZ).color(colorX.r, colorX.g, colorX.b, colorX.a).normal(e, 0.0f, 0.0f, 0.0f);
            buffer.vertex(e, end   + dxMax, minY, minZ).color(colorX.r, colorX.g, colorX.b, colorX.a).normal(e, 0.0f, 0.0f, 0.0f);
        }

        start = (pos1.getX() == xMin && pos1.getY() == yMax && pos1.getZ() == zMin) || (pos2.getX() == xMin && pos2.getY() == yMax && pos2.getZ() == zMin) ? xMin + 1 : xMin;
        end   = (pos1.getX() == xMax && pos1.getY() == yMax && pos1.getZ() == zMin) || (pos2.getX() == xMax && pos2.getY() == yMax && pos2.getZ() == zMin) ? xMax : xMax + 1;

        if (end > start)
        {
            buffer.vertex(e, start + dxMin, maxY + 1, minZ).color(colorX.r, colorX.g, colorX.b, colorX.a).normal(e, 0.0f, 0.0f, 0.0f);
            buffer.vertex(e, end   + dxMax, maxY + 1, minZ).color(colorX.r, colorX.g, colorX.b, colorX.a).normal(e, 0.0f, 0.0f, 0.0f);
        }

        start = (pos1.getX() == xMin && pos1.getY() == yMin && pos1.getZ() == zMax) || (pos2.getX() == xMin && pos2.getY() == yMin && pos2.getZ() == zMax) ? xMin + 1 : xMin;
        end   = (pos1.getX() == xMax && pos1.getY() == yMin && pos1.getZ() == zMax) || (pos2.getX() == xMax && pos2.getY() == yMin && pos2.getZ() == zMax) ? xMax : xMax + 1;

        if (end > start)
        {
            buffer.vertex(e, start + dxMin, minY, maxZ + 1).color(colorX.r, colorX.g, colorX.b, colorX.a).normal(e, 0.0f, 0.0f, 0.0f);
            buffer.vertex(e, end   + dxMax, minY, maxZ + 1).color(colorX.r, colorX.g, colorX.b, colorX.a).normal(e, 0.0f, 0.0f, 0.0f);
        }

        start = (pos1.getX() == xMin && pos1.getY() == yMax && pos1.getZ() == zMax) || (pos2.getX() == xMin && pos2.getY() == yMax && pos2.getZ() == zMax) ? xMin + 1 : xMin;
        end   = (pos1.getX() == xMax && pos1.getY() == yMax && pos1.getZ() == zMax) || (pos2.getX() == xMax && pos2.getY() == yMax && pos2.getZ() == zMax) ? xMax : xMax + 1;

        if (end > start)
        {
            buffer.vertex(e, start + dxMin, maxY + 1, maxZ + 1).color(colorX.r, colorX.g, colorX.b, colorX.a).normal(e, 0.0f, 0.0f, 0.0f);
            buffer.vertex(e, end   + dxMax, maxY + 1, maxZ + 1).color(colorX.r, colorX.g, colorX.b, colorX.a).normal(e, 0.0f, 0.0f, 0.0f);
        }

        // Edges along the Y-axis
        start = (pos1.getX() == xMin && pos1.getY() == yMin && pos1.getZ() == zMin) || (pos2.getX() == xMin && pos2.getY() == yMin && pos2.getZ() == zMin) ? yMin + 1 : yMin;
        end   = (pos1.getX() == xMin && pos1.getY() == yMax && pos1.getZ() == zMin) || (pos2.getX() == xMin && pos2.getY() == yMax && pos2.getZ() == zMin) ? yMax : yMax + 1;

        if (end > start)
        {
            buffer.vertex(e, minX, start + dyMin, minZ).color(colorY.r, colorY.g, colorY.b, colorY.a).normal(e, 0.0f, 0.0f, 0.0f);
            buffer.vertex(e, minX, end   + dyMax, minZ).color(colorY.r, colorY.g, colorY.b, colorY.a).normal(e, 0.0f, 0.0f, 0.0f);
        }

        start = (pos1.getX() == xMax && pos1.getY() == yMin && pos1.getZ() == zMin) || (pos2.getX() == xMax && pos2.getY() == yMin && pos2.getZ() == zMin) ? yMin + 1 : yMin;
        end   = (pos1.getX() == xMax && pos1.getY() == yMax && pos1.getZ() == zMin) || (pos2.getX() == xMax && pos2.getY() == yMax && pos2.getZ() == zMin) ? yMax : yMax + 1;

        if (end > start)
        {
            buffer.vertex(e, maxX + 1, start + dyMin, minZ).color(colorY.r, colorY.g, colorY.b, colorY.a).normal(e, 0.0f, 0.0f, 0.0f);
            buffer.vertex(e, maxX + 1, end   + dyMax, minZ).color(colorY.r, colorY.g, colorY.b, colorY.a).normal(e, 0.0f, 0.0f, 0.0f);
        }

        start = (pos1.getX() == xMin && pos1.getY() == yMin && pos1.getZ() == zMax) || (pos2.getX() == xMin && pos2.getY() == yMin && pos2.getZ() == zMax) ? yMin + 1 : yMin;
        end   = (pos1.getX() == xMin && pos1.getY() == yMax && pos1.getZ() == zMax) || (pos2.getX() == xMin && pos2.getY() == yMax && pos2.getZ() == zMax) ? yMax : yMax + 1;

        if (end > start)
        {
            buffer.vertex(e, minX, start + dyMin, maxZ + 1).color(colorY.r, colorY.g, colorY.b, colorY.a).normal(e, 0.0f, 0.0f, 0.0f);
            buffer.vertex(e, minX, end   + dyMax, maxZ + 1).color(colorY.r, colorY.g, colorY.b, colorY.a).normal(e, 0.0f, 0.0f, 0.0f);
        }

        start = (pos1.getX() == xMax && pos1.getY() == yMin && pos1.getZ() == zMax) || (pos2.getX() == xMax && pos2.getY() == yMin && pos2.getZ() == zMax) ? yMin + 1 : yMin;
        end   = (pos1.getX() == xMax && pos1.getY() == yMax && pos1.getZ() == zMax) || (pos2.getX() == xMax && pos2.getY() == yMax && pos2.getZ() == zMax) ? yMax : yMax + 1;

        if (end > start)
        {
            buffer.vertex(e, maxX + 1, start + dyMin, maxZ + 1).color(colorY.r, colorY.g, colorY.b, colorY.a).normal(e, 0.0f, 0.0f, 0.0f);
            buffer.vertex(e, maxX + 1, end   + dyMax, maxZ + 1).color(colorY.r, colorY.g, colorY.b, colorY.a).normal(e, 0.0f, 0.0f, 0.0f);
        }

        // Edges along the Z-axis
        start = (pos1.getX() == xMin && pos1.getY() == yMin && pos1.getZ() == zMin) || (pos2.getX() == xMin && pos2.getY() == yMin && pos2.getZ() == zMin) ? zMin + 1 : zMin;
        end   = (pos1.getX() == xMin && pos1.getY() == yMin && pos1.getZ() == zMax) || (pos2.getX() == xMin && pos2.getY() == yMin && pos2.getZ() == zMax) ? zMax : zMax + 1;

        if (end > start)
        {
            buffer.vertex(e, minX, minY, start + dzMin).color(colorZ.r, colorZ.g, colorZ.b, colorZ.a).normal(e, 0.0f, 0.0f, 0.0f);
            buffer.vertex(e, minX, minY, end   + dzMax).color(colorZ.r, colorZ.g, colorZ.b, colorZ.a).normal(e, 0.0f, 0.0f, 0.0f);
        }

        start = (pos1.getX() == xMax && pos1.getY() == yMin && pos1.getZ() == zMin) || (pos2.getX() == xMax && pos2.getY() == yMin && pos2.getZ() == zMin) ? zMin + 1 : zMin;
        end   = (pos1.getX() == xMax && pos1.getY() == yMin && pos1.getZ() == zMax) || (pos2.getX() == xMax && pos2.getY() == yMin && pos2.getZ() == zMax) ? zMax : zMax + 1;

        if (end > start)
        {
            buffer.vertex(e, maxX + 1, minY, start + dzMin).color(colorZ.r, colorZ.g, colorZ.b, colorZ.a).normal(e, 0.0f, 0.0f, 0.0f);
            buffer.vertex(e, maxX + 1, minY, end   + dzMax).color(colorZ.r, colorZ.g, colorZ.b, colorZ.a).normal(e, 0.0f, 0.0f, 0.0f);
        }

        start = (pos1.getX() == xMin && pos1.getY() == yMax && pos1.getZ() == zMin) || (pos2.getX() == xMin && pos2.getY() == yMax && pos2.getZ() == zMin) ? zMin + 1 : zMin;
        end   = (pos1.getX() == xMin && pos1.getY() == yMax && pos1.getZ() == zMax) || (pos2.getX() == xMin && pos2.getY() == yMax && pos2.getZ() == zMax) ? zMax : zMax + 1;

        if (end > start)
        {
            buffer.vertex(e, minX, maxY + 1, start + dzMin).color(colorZ.r, colorZ.g, colorZ.b, colorZ.a).normal(e, 0.0f, 0.0f, 0.0f);
            buffer.vertex(e, minX, maxY + 1, end   + dzMax).color(colorZ.r, colorZ.g, colorZ.b, colorZ.a).normal(e, 0.0f, 0.0f, 0.0f);
        }

        start = (pos1.getX() == xMax && pos1.getY() == yMax && pos1.getZ() == zMin) || (pos2.getX() == xMax && pos2.getY() == yMax && pos2.getZ() == zMin) ? zMin + 1 : zMin;
        end   = (pos1.getX() == xMax && pos1.getY() == yMax && pos1.getZ() == zMax) || (pos2.getX() == xMax && pos2.getY() == yMax && pos2.getZ() == zMax) ? zMax : zMax + 1;

        if (end > start)
        {
            buffer.vertex(e, maxX + 1, maxY + 1, start + dzMin).color(colorZ.r, colorZ.g, colorZ.b, colorZ.a).normal(e, 0.0f, 0.0f, 0.0f);
            buffer.vertex(e, maxX + 1, maxY + 1, end   + dzMax).color(colorZ.r, colorZ.g, colorZ.b, colorZ.a).normal(e, 0.0f, 0.0f, 0.0f);
        }

        try
        {
            ctx.lineWidth(lineWidth);
            ctx.draw(buffer.endNullable(), true);
            ctx.close();
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("drawAreaOutlineNoCorners(): Draw Exception; {}", err.getMessage());
        }
        matrices.pop();
    }
}
