package fi.dy.masa.malilib.render;

import java.util.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix3x2f;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.SpecialGuiElementRenderer;
import net.minecraft.client.gui.render.state.ItemGuiElementRenderState;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.gui.render.state.TextGuiElementRenderState;
import net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.texture.TextureSetup;
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
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Colors;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.config.HudAlignment;
import fi.dy.masa.malilib.event.RenderEventHandler;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.interfaces.IGuiRendererInvoker;
import fi.dy.masa.malilib.mixin.render.IMixinAbstractTexture;
import fi.dy.masa.malilib.mixin.render.IMixinDrawContext;
import fi.dy.masa.malilib.mixin.render.IMixinGuiRenderer;
import fi.dy.masa.malilib.render.element.*;
import fi.dy.masa.malilib.util.*;
import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.malilib.util.log.AnsiLogger;
import fi.dy.masa.malilib.util.nbt.NbtBlockUtils;
import fi.dy.masa.malilib.util.position.PositionUtils;

public class RenderUtils
{
    private static final AnsiLogger LOGGER = new AnsiLogger(RenderUtils.class);
    public static final Identifier TEXTURE_MAP_BACKGROUND = Identifier.ofVanilla("textures/map/map_background.png");
    public static final Identifier TEXTURE_MAP_BACKGROUND_CHECKERBOARD = Identifier.ofVanilla("textures/map/map_background_checkerboard.png");

    private static final LocalRandom RAND = new LocalRandom(0);

    //private static final Vec3d LIGHT0_POS = (new Vec3d( 0.2D, 1.0D, -0.7D)).normalize();
    //private static final Vec3d LIGHT1_POS = (new Vec3d(-0.2D, 1.0D,  0.7D)).normalize();

    @Deprecated
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

    @Deprecated
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

    @Deprecated
    public static void depthFunc(int depth)
    {
        GlStateManager._depthFunc(depth);
    }

    @Deprecated
    public static void depthMask(boolean toggle)
    {
        GlStateManager._depthMask(toggle);
    }

    @Deprecated
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

    @Deprecated
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

    @Deprecated
    public static void polygonOffset(float factor, float units)
    {
        GlStateManager._polygonOffset(factor, units);
    }

    @Deprecated
    public static void fbStartDrawing()
    {
        RenderSystem.assertOnRenderThread();
        GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, 0);
    }

    /**
     * Attempts to bind the Shader Texture
     * @param texture ()
     * @param textureId ()
     * @return ()
     */
    public static ResourceTexture bindShaderTexture(Identifier texture, int textureId) throws RuntimeException
    {
        if (textureId < 0 || textureId > 12)
        {
            throw new RuntimeException("Invalid textureId of: " + textureId + " for texture: " + texture.toString());
        }

        ResourceTexture tex = (ResourceTexture) tex().getTexture(texture);
        tex.setFilter(false, false);
        RenderSystem.setShaderTexture(textureId, tex.getGlTextureView());

        return tex;
    }

    /**
     * Attempt a simple binding of a GpuTexture, returns null if failed to loadContents.
     *
     * @param texture ()
     * @return ()
     */
    public static @Nullable GpuTexture bindGpuTexture(Identifier texture)
    {
        ResourceTexture tex = (ResourceTexture) tex().getTexture(texture);

        if (tex != null && ((IMixinAbstractTexture) tex).malilib_getGlTexture() != null)
        {
            return tex.getGlTexture();
        }

        return null;
    }

    /**
     * Attempt a simple binding of a GpuTextureView, returns null if failed to loadContents.
     *
     * @param texture ()
     * @return ()
     */
    public static @Nullable GpuTextureView bindGpuTextureView(Identifier texture)
    {
        ResourceTexture tex = (ResourceTexture) tex().getTexture(texture);

        if (tex != null && ((IMixinAbstractTexture) tex).malilib_getGlTextureView() != null)
        {
            return tex.getGlTextureView();
        }

        MaLiLib.LOGGER.error("bindGpuTextureView: Result is null!");
        return null;
    }

    /**
     * Add a 'Simple' Element to the DrawContext.
     * Don't forget to manage the Layers / Checkpoints.
     *
     * @param drawContext ()
     * @param simpleElement ()
     */
    public static void addSimpleElement(DrawContext drawContext, SimpleGuiElementRenderState simpleElement)
    {
        ((IMixinDrawContext) drawContext).malilib_getRenderState().addSimpleElement(simpleElement);
    }

    /**
     * Add a 'Special' Element to the DrawContext
     * Don't forget to manage the Layers / Checkpoints.
     *
     * @param drawContext ()
     * @param specialElement ()
     */
    public static void addSpecialElement(DrawContext drawContext, SpecialGuiElementRenderState specialElement)
    {
        ((IMixinDrawContext) drawContext).malilib_getRenderState().addSpecialElement(specialElement);
    }

    // FIXME
    @ApiStatus.Internal
    public static void registerSpecialGuiRenderers(GuiRenderer guiRenderer, VertexConsumerProvider.Immediate immediate, MinecraftClient mc)
    {
        ImmutableMap.Builder<Class<? extends SpecialGuiElementRenderState>, SpecialGuiElementRenderer<?>> builder = new ImmutableMap.Builder<>();

        // Build new ImmutableMap
        builder.putAll(((IMixinGuiRenderer) guiRenderer).malilib_getSpecialGuiRenderers());

        // Add Gui Block Model Renderer
//        builder.put(MaLiLibBlockStateModelGuiElement.class, new MaLiLibBlockModelGuiElementRenderer(immediate, mc.getBlockRenderManager()));

        // Event Callback
        ((RenderEventHandler) RenderEventHandler.getInstance()).onRegisterSpecialGuiRenderer(guiRenderer, immediate, mc, builder);

        // Invoke / Update
        ((IGuiRendererInvoker) guiRenderer).malilib$replaceSpecialGuiRenderers(builder.buildOrThrow());

        // Debug Built Map
        if (MaLiLibReference.DEBUG_MODE)
        {
            dumpBuilderMap(((IMixinGuiRenderer) guiRenderer).malilib_getSpecialGuiRenderers());
        }
    }

    public static void dumpBuilderMap(Map<Class<? extends SpecialGuiElementRenderState>, SpecialGuiElementRenderer<?>> entries)
    {
        System.out.print("DUMP SpecialGuiRenderers()\n");

        if (entries == null || entries.size() == 0)
        {
            System.out.print("NULL OR EMPTY!\n");
            return;
        }

        int i = 0;

        for (Class<? extends SpecialGuiElementRenderState> entry : entries.keySet())
        {
            System.out.printf("[%d] K (State): [%s], V (Renderer): [%s]\n", i, entry.getName(), entries.get(entry).getClass().getName());
            i++;
        }

        System.out.print("DUMP END\n");
    }

    /**
     * Add a 'Item' Element to the DrawContext
     * Don't forget to manage the Layers / Checkpoints.
     *
     * @param drawContext ()
     * @param itemElement ()
     */
    public static void addItemElement(DrawContext drawContext, ItemGuiElementRenderState itemElement)
    {
        ((IMixinDrawContext) drawContext).malilib_getRenderState().addItem(itemElement);
    }

    /**
     * Add a 'Text' Element to the DrawContext.
     * Don't forget to manage the Layers / Checkpoints.
     *
     * @param drawContext ()
     * @param textElement ()
     */
    public static void addTextElement(DrawContext drawContext, TextGuiElementRenderState textElement)
    {
        ((IMixinDrawContext) drawContext).malilib_getRenderState().addText(textElement);
    }

    /**
     * Pushes the Scissor Stack using rect
     * @param drawContext ()
     * @param rect ()
     */
    public static void pushScissor(DrawContext drawContext, @Nonnull ScreenRect rect)
    {
        ((IMixinDrawContext) drawContext).malilib_getScissorStack().push(rect);
    }

    /**
     * Returns if the Scissor Stack contains the position x, y
     * @param drawContext ()
     * @param x ()
     * @param y ()
     * @return ()
     */
    public static boolean containsScissor(DrawContext drawContext, int x, int y)
    {
        return ((IMixinDrawContext) drawContext).malilib_getScissorStack().contains(x, y);
    }

    /**
     * Peeks the Scissor Stack's Screen Rect
     * @param drawContext ()
     * @return ()
     */
    public static ScreenRect peekLastScissor(DrawContext drawContext)
    {
        return ((IMixinDrawContext) drawContext).malilib_getScissorStack().peekLast();
    }

    /**
     * Pop's the Scissor Stack's Screen Rect
     * @param drawContext ()
     * @return ()
     */
    public static ScreenRect popScissor(DrawContext drawContext)
    {
        return ((IMixinDrawContext) drawContext).malilib_getScissorStack().pop();
    }

    public static void drawOutlinedBox(DrawContext drawContext, int x, int y, int width, int height, int colorBg, int colorBorder)
    {
        // Draw the background
        drawRect(drawContext, x, y, width, height, colorBg);

        // Draw the border
        drawOutline(drawContext, x - 1, y - 1, width + 2, height + 2, colorBorder);
    }

    public static void drawOutlinedBox(DrawContext drawContext, int x, int y, int width, int height, float scale, int colorBg, int colorBorder)
    {
        // Draw the background
        drawRect(drawContext, x, y, width, height, colorBg, scale);

        // Draw the border
        drawOutline(drawContext, x - 1, y - 1, width + 2, height + 2, scale, colorBorder);
    }

    public static void drawOutline(DrawContext drawContext, int x, int y, int width, int height, int colorBorder)
    {
        drawOutline(drawContext, x, y, width, height, 1, colorBorder);
    }

    public static void drawOutline(DrawContext drawContext, int x, int y, int width, int height, float scale, int colorBorder)
    {
        drawOutline(drawContext, x, y, width, height, scale, 1, colorBorder);
    }

    public static void drawOutline(DrawContext drawContext, int x, int y, int width, int height, int borderWidth, int colorBorder)
    {
        drawRect(drawContext, x, y, borderWidth, height, colorBorder); // left edge
        drawRect(drawContext, x + width - borderWidth, y, borderWidth, height, colorBorder); // right edge
        drawRect(drawContext, x + borderWidth, y, width - 2 * borderWidth, borderWidth, colorBorder); // top edge
        drawRect(drawContext, x + borderWidth, y + height - borderWidth, width - 2 * borderWidth, borderWidth, colorBorder); // bottom edge
    }

    public static void drawOutline(DrawContext drawContext, int x, int y, int width, int height, float scale, int borderWidth, int colorBorder)
    {
        drawRect(drawContext, x, y, borderWidth, height, colorBorder, scale); // left edge
        drawRect(drawContext, x + width - borderWidth, y, borderWidth, height, colorBorder, scale); // right edge
        drawRect(drawContext, x + borderWidth, y, width - 2 * borderWidth, borderWidth, colorBorder, scale); // top edge
        drawRect(drawContext, x + borderWidth, y + height - borderWidth, width - 2 * borderWidth, borderWidth, colorBorder, scale); // bottom edge
    }

    @Deprecated
    public static void drawRect(int x, int y, int width, int height, int color)
    {
        drawRect(x, y, width, height, color, 0f);
    }

    @Deprecated
    public static void drawRect(int x, int y, int width, int height, int color, boolean depthMask)
    {
        drawRect(x, y, width, height, color, 0f, 1.0f, depthMask);
    }

    @Deprecated
    public static void drawRect(int x, int y, int width, int height, int color, float zLevel)
    {
        drawRect(x, y, width, height, color, zLevel, 1.0f, false);
    }

    @Deprecated
    public static void drawRect(int x, int y, int width, int height, int color, float zLevel, boolean depthMask)
    {
        drawRect(x, y, width, height, color, zLevel, 1.0f, depthMask);
    }

    @Deprecated
    public static void drawRect(int x, int y, int width, int height, int color, float zLevel, float scale, boolean depthMask)
    {
        float a = (float) (color >> 24 & 255) / 255.0F;
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;

        // POSITION_COLOR_SIMPLE
        RenderContext ctx = new RenderContext(() -> "malilib:drawRect", depthMask ? MaLiLibPipelines.POSITION_COLOR_MASA_DEPTH_MASK : MaLiLibPipelines.POSITION_COLOR_MASA_NO_DEPTH_NO_CULL);
        BufferBuilder buffer = ctx.getBuilder();

        buffer.vertex(x * scale,           y * scale,            zLevel).color(r, g, b, a);
        buffer.vertex(x * scale,           (y + height) * scale, zLevel).color(r, g, b, a);
        buffer.vertex((x + width) * scale, (y + height) * scale, zLevel).color(r, g, b, a);
        buffer.vertex((x + width) * scale, y * scale           , zLevel).color(r, g, b, a);

        try
        {
            BuiltBuffer meshData = buffer.endNullable();

            if (meshData != null)
            {
                ctx.draw(meshData, false);
                meshData.close();
            }

            ctx.close();
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("drawRect(): Draw Exception; {}", err.getMessage());
        }
    }

    /**
     * New DrawContext based drawRect() for GUI Rendering.
     * @param drawContext
     * @param x
     * @param y
     * @param width
     * @param height
     * @param color
     */
    public static void drawRect(DrawContext drawContext, int x, int y, int width, int height, int color)
    {
        drawRect(drawContext, x, y, width, height, color, 1.0f);
    }

    public static void drawRect(DrawContext drawContext, int x, int y, int width, int height, int color, float scale)
    {
        addSimpleElement(drawContext, new MaLiLibBasicRectGuiElement(
                RenderPipelines.GUI,
                TextureSetup.empty(),
                new Matrix3x2f(drawContext.getMatrices()),
                x, y,
                width, height,
                scale, color,
                peekLastScissor(drawContext))
        );
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

    @Deprecated
    public static void drawTexturedRect(Matrix4f posMatrix, int x, int y, int u, int v, int width, int height, VertexConsumer buffer)
    {
        drawTexturedRect(posMatrix, x, y, u, v, width, height, 0f, -1, buffer);
    }

    @Deprecated
    public static void drawTexturedRect(Matrix4f posMatrix, int x, int y, int u, int v, int width, int height, int color, VertexConsumer buffer)
    {
        drawTexturedRect(posMatrix, x, y, u, v, width, height, 0f, color, buffer);
    }

    @Deprecated
    public static void drawTexturedRect(Matrix4f posMatrix, int x, int y, int u, int v, int width, int height, float zLevel, int color, VertexConsumer buffer)
    {
        float pixelWidth = 0.00390625F;

        // GUI_TEXTURED_OVERLAY
        buffer.vertex(posMatrix, x, y + height, zLevel).texture(u * pixelWidth, (v + height) * pixelWidth).color(color);
        buffer.vertex(posMatrix, x + width, y + height, zLevel).texture((u + width) * pixelWidth, (v + height) * pixelWidth).color(color);
        buffer.vertex(posMatrix, x + width, y, zLevel).texture((u + width) * pixelWidth, v * pixelWidth).color(color);
        buffer.vertex(posMatrix, x, y, zLevel).texture(u * pixelWidth, v * pixelWidth).color(color);
    }

    /**
     * New DrawContext-based Textured Rect method.  Use this when the original method fails.
     *
     * @param drawContext
     * @param texture
     * @param x
     * @param y
     * @param u
     * @param v
     * @param width
     * @param height
     */
    public static void drawTexturedRect(DrawContext drawContext, Identifier texture, int x, int y, int u, int v, int width, int height)
    {
        drawTexturedRect(drawContext, texture, x, y, u, v, width, height, 0F, -1);
    }

    /**
     * New DrawContext-based Textured Rect method.  Use this when the original method fails.
     *
     * @param drawContext
     * @param texture
     * @param x
     * @param y
     * @param u
     * @param v
     * @param width
     * @param height
     * @param zLevel (NOT USED)
     */
    public static void drawTexturedRect(DrawContext drawContext, Identifier texture, int x, int y, int u, int v, int width, int height, float zLevel)
    {
        drawTexturedRect(drawContext, texture, x, y, u, v, width, height, zLevel, -1);
    }

    /**
     * New DrawContext-based Textured Rect method.  Use this when the original method fails.
     *
     * @param drawContext
     * @param texture
     * @param x
     * @param y
     * @param u
     * @param v
     * @param width
     * @param height
     * @param zLevel (NOT USED)
     * @param argb
     */
    public static void drawTexturedRect(DrawContext drawContext, Identifier texture, int x, int y, int u, int v, int width, int height, float zLevel, int argb)
    {
        float pixelWidth = 0.00390625F;
        GpuTextureView gpuTextureView = bindGpuTextureView(texture);

        if (gpuTextureView == null)
        {
            MaLiLib.LOGGER.error("drawTexturedRect(): GpuTextureView for '{}' is null!", texture.toString());
            return;
        }

        addSimpleElement(drawContext, new MaLiLibTexturedGuiElement(
                RenderPipelines.GUI_TEXTURED,
                TextureSetup.withoutGlTexture(gpuTextureView),
                new Matrix3x2f(drawContext.getMatrices()),
                x, y, x + width, y + height,
                u * pixelWidth, (u + width) * pixelWidth,
                v * pixelWidth, (v + height) * pixelWidth,
                argb,
                peekLastScissor(drawContext))
        );
    }

    @Deprecated
    public static void drawTexturedRectBatched(int x, int y, int u, int v, int width, int height, VertexConsumer buffer)
    {
        drawTexturedRectBatched(x, y, u, v, width, height, 0, -1, buffer);
    }

    @Deprecated
    public static void drawTexturedRectBatched(int x, int y, int u, int v, int width, int height, int argb, VertexConsumer buffer)
    {
        drawTexturedRectBatched(x, y, u, v, width, height, 0, argb, buffer);
    }

    @Deprecated
    public static void drawTexturedRectBatched(int x, int y, int u, int v, int width, int height, float zLevel, int argb, VertexConsumer buffer)
    {
        float pixelWidth = 0.00390625F;

        buffer.vertex(x, y + height, zLevel).texture(u * pixelWidth, (v + height) * pixelWidth).color(argb);
        buffer.vertex(x + width, y + height, zLevel).texture((u + width) * pixelWidth, (v + height) * pixelWidth).color(argb);
        buffer.vertex(x + width, y, zLevel).texture((u + width) * pixelWidth, v * pixelWidth).color(argb);
        buffer.vertex(x, y, zLevel).texture(u * pixelWidth, v * pixelWidth).color(argb);
    }

    public static void drawTexturedRectBatched(DrawContext drawContext, @Nonnull GpuTextureView gpuTextureView, int x, int y, int u, int v, int width, int height)
    {
        drawTexturedRectBatched(drawContext, gpuTextureView, x, y, u, v, width, height, 0, -1);
    }

    public static void drawTexturedRectBatched(DrawContext drawContext, @Nonnull GpuTextureView gpuTextureView, int x, int y, int u, int v, int width, int height, int argb)
    {
        drawTexturedRectBatched(drawContext, gpuTextureView, x, y, u, v, width, height, 0, argb);
    }

    public static void drawTexturedRectBatched(DrawContext drawContext, @Nonnull GpuTextureView gpuTextureView, int x, int y, int u, int v, int width, int height, float zLevel, int argb)
    {
        addSimpleElement(drawContext,
                         new MaLiLibTexturedRectGuiElement(
                                 RenderPipelines.GUI_TEXTURED,
                                 TextureSetup.withoutGlTexture(gpuTextureView),
                                 new Matrix3x2f(drawContext.getMatrices()),
                                 x, y, u, v,
                                 width, height, argb,
                                 peekLastScissor(drawContext))
        );
    }

    public static void drawHoverText(DrawContext drawContext, int x, int y, List<String> textLines)
    {
        if (textLines.isEmpty() == false && GuiUtils.getCurrentScreen() != null)
        {
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

            drawContext.getMatrices().pushMatrix();
            drawContext.getMatrices().translate(0, 0);

            float zLevel = (float) 300;
            int borderColor = 0xF0100010;
            drawGradientRectBatched(drawContext, textStartX - 3, textStartY - 4, textStartX + maxLineLength + 3, textStartY - 3, borderColor, borderColor);
            drawGradientRectBatched(drawContext, textStartX - 3, textStartY + textHeight + 3, textStartX + maxLineLength + 3, textStartY + textHeight + 4, borderColor, borderColor);
            drawGradientRectBatched(drawContext, textStartX - 3, textStartY - 3, textStartX + maxLineLength + 3, textStartY + textHeight + 3, borderColor, borderColor);
            drawGradientRectBatched(drawContext, textStartX - 4, textStartY - 3, textStartX - 3, textStartY + textHeight + 3, borderColor, borderColor);
            drawGradientRectBatched(drawContext, textStartX + maxLineLength + 3, textStartY - 3, textStartX + maxLineLength + 4, textStartY + textHeight + 3, borderColor, borderColor);

            int fillColor1 = 0x505000FF;
            int fillColor2 = 0x5028007F;
            drawGradientRectBatched(drawContext, textStartX - 3, textStartY - 3 + 1, textStartX - 3 + 1, textStartY + textHeight + 3 - 1, fillColor1, fillColor2);
            drawGradientRectBatched(drawContext, textStartX + maxLineLength + 2, textStartY - 3 + 1, textStartX + maxLineLength + 3, textStartY + textHeight + 3 - 1, fillColor1, fillColor2);
            drawGradientRectBatched(drawContext, textStartX - 3, textStartY - 3, textStartX + maxLineLength + 3, textStartY - 3 + 1, fillColor1, fillColor1);
            drawGradientRectBatched(drawContext, textStartX - 3, textStartY + textHeight + 2, textStartX + maxLineLength + 3, textStartY + textHeight + 3, fillColor2, fillColor2);

            for (int i = 0; i < textLines.size(); ++i)
            {
                String str = textLines.get(i);
                drawContext.drawText(font, str, textStartX, textStartY, 0xFFFFFFFF, false);
                textStartY += lineHeight;
            }

            drawContext.getMatrices().popMatrix();
        }
    }

    public static void drawGradientRectBatched(DrawContext drawContext, float left, float top, float right, float bottom, int startColor, int endColor)
    {
        addSimpleElement(drawContext, new MaLiLibGradientRectGuiElement(
                RenderPipelines.GUI,
                TextureSetup.empty(),
                new Matrix3x2f(drawContext.getMatrices()),
                left, top, right, bottom,
                startColor, endColor,
                peekLastScissor(drawContext))
        );
    }

    @Deprecated
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

        RenderContext ctx = new RenderContext(() -> "malilib:drawGradientRect", MaLiLibPipelines.POSITION_COLOR_MASA_NO_DEPTH_NO_CULL);
        BufferBuilder buffer = ctx.getBuilder();

        buffer.vertex(right, top, zLevel).color(sr, sg, sb, sa);
        buffer.vertex(left, top, zLevel).color(sr, sg, sb, sa);
        buffer.vertex(left, bottom, zLevel).color(er, eg, eb, ea);
        buffer.vertex(right, bottom, zLevel).color(er, eg, eb, ea);

        try
        {
            BuiltBuffer meshData = buffer.endNullable();

            if (meshData != null)
            {
                ctx.draw(meshData, false);
                meshData.close();
            }

            ctx.close();
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("drawGradientRect(): Draw Exception; {}", err.getMessage());
        }
    }

    public static void drawCenteredString(DrawContext drawContext, int x, int y, int color, String text)
    {
        drawContext.drawCenteredTextWithShadow(mc().textRenderer, text, x, y, color);
    }

    public static void drawHorizontalLine(DrawContext drawContext, int x, int y, int width, int color)
    {
        drawRect(drawContext, x, y, width, 1, color);
    }

    public static void drawVerticalLine(DrawContext drawContext, int x, int y, int height, int color)
    {
        drawRect(drawContext, x, y, 1, height, color);
    }

    public static void renderSprite(DrawContext drawContext, Identifier atlas, Identifier texture, int x, int y, int width, int height)
    {
        if (texture != null)
        {
            Sprite sprite = mc().getAtlasManager().getAtlasTexture(atlas).getSprite(texture);

            if (sprite != null)
            {
                drawContext.drawSpriteStretched(RenderPipelines.GUI_TEXTURED, sprite, x, y, width, height, -1);
            }
        }
    }

    public static void renderText(DrawContext drawContext, int x, int y, int color, String text)
    {
        String[] parts = text.split("\\\\n");
        TextRenderer textRenderer = mc().textRenderer;

        for (String line : parts)
        {
            drawContext.drawText(textRenderer, line, x, y, color, true);
            y += textRenderer.fontHeight + 1;
        }
    }

    public static void renderText(DrawContext drawContext, int x, int y, int color, List<String> lines)
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

    public static int renderText(DrawContext drawContext, int xOff, int yOff, double scale,
                                 int textColor, int bgColor, HudAlignment alignment,
                                 boolean useBackground, boolean useShadow,
                                 List<String> lines)
    {
        return renderText(drawContext, xOff, yOff, scale,
                          textColor, bgColor, alignment,
                          useBackground, useShadow, true,
                          lines);
    }

    public static int renderText(DrawContext drawContext, int xOff, int yOff, double scale,
                                 int textColor, int bgColor, HudAlignment alignment,
                                 boolean useBackground, boolean useShadow, boolean useStatusShift,
                                 List<String> lines)
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

        if (scaled)
        {
//            if (scale != 0)
//            {
//                xOff = (int) (xOff * scale);
//                yOff = (int) (yOff * scale);
//            }

            drawContext.getMatrices().pushMatrix();
            drawContext.getMatrices().scale((float) scale, (float) scale);      // z = 1.0f
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
//                drawRect(drawContext, x - bgMargin, y - bgMargin, width + bgMargin, bgMargin + fontRenderer.fontHeight, bgColor, (float) (scale * 2));
                drawRect(drawContext, x - bgMargin, y - bgMargin, width + bgMargin, bgMargin + fontRenderer.fontHeight, bgColor);
            }

            drawContext.drawText(fontRenderer, line, x, y, textColor, useShadow);
        }

        if (scaled)
        {
            drawContext.getMatrices().popMatrix();
        }

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
    public static void drawBlockBoundingBoxSidesBatchedQuads(BlockPos pos, Color4f color, double expand,
                                                             BufferBuilder buffer)
    {
        float minX = (float) (pos.getX() - expand);
        float minY = (float) (pos.getY() - expand);
        float minZ = (float) (pos.getZ() - expand);
        float maxX = (float) (pos.getX() + expand + 1);
        float maxY = (float) (pos.getY() + expand + 1);
        float maxZ = (float) (pos.getZ() + expand + 1);

        drawBoxAllSidesBatchedQuads(minX, minY, minZ, maxX, maxY, maxZ, color, buffer);
    }

    public static void drawBlockBoundingBoxSidesBatchedQuads(BlockPos pos, Vec3d cameraPos, Color4f color, double expand,
                                                             BufferBuilder buffer)
    {
        float minX = (float) (pos.getX() - cameraPos.x - expand);
        float minY = (float) (pos.getY() - cameraPos.y - expand);
        float minZ = (float) (pos.getZ() - cameraPos.z - expand);
        float maxX = (float) (pos.getX() - cameraPos.x + expand + 1);
        float maxY = (float) (pos.getY() - cameraPos.y + expand + 1);
        float maxZ = (float) (pos.getZ() - cameraPos.z + expand + 1);

        RenderUtils.drawBoxAllSidesBatchedQuads(minX, minY, minZ, maxX, maxY, maxZ, color, buffer);
    }

    /**
     * Assumes a BufferBuilder in GL_LINES mode has been initialized
     */
    public static void drawBlockBoundingBoxOutlinesBatchedLines(BlockPos pos, Color4f color, double expand,
//                                                                BufferBuilder buffer, MatrixStack.Entry e)
                                                                BufferBuilder buffer)
    {
        drawBlockBoundingBoxOutlinesBatchedLines(pos, Vec3d.ZERO, color, expand, buffer);
//        drawBlockBoundingBoxOutlinesBatchedLines(pos, Vec3d.ZERO, color, expand, buffer, e);
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
    public static void drawBlockBoundingBoxOutlinesBatchedLines(BlockPos pos, Vec3d cameraPos, Color4f color, double expand,
//                                                                BufferBuilder buffer, MatrixStack.Entry e)
                                                                BufferBuilder buffer)
    {
        float minX = (float) (pos.getX() - expand - cameraPos.x);
        float minY = (float) (pos.getY() - expand - cameraPos.y);
        float minZ = (float) (pos.getZ() - expand - cameraPos.z);
        float maxX = (float) (pos.getX() + expand - cameraPos.x + 1);
        float maxY = (float) (pos.getY() + expand - cameraPos.y + 1);
        float maxZ = (float) (pos.getZ() + expand - cameraPos.z + 1);

        drawBoxAllEdgesBatchedLines(minX, minY, minZ, maxX, maxY, maxZ, color, buffer);
//        drawBoxAllEdgesBatchedLines(minX, minY, minZ, maxX, maxY, maxZ, color, buffer, e);
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
    public static void drawBoxWithEdgesBatched(BlockPos posMin, BlockPos posMax, Color4f colorLines, Color4f colorSides,
//                                               BufferBuilder bufferQuads, BufferBuilder bufferLines, MatrixStack matrices)
                                               BufferBuilder bufferQuads, BufferBuilder bufferLines)
    {
        drawBoxWithEdgesBatched(posMin, posMax, Vec3d.ZERO, colorLines, colorSides, bufferQuads, bufferLines);
//        drawBoxWithEdgesBatched(posMin, posMax, Vec3d.ZERO, colorLines, colorSides, bufferQuads, bufferLines, matrices);
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
    public static void drawBoxWithEdgesBatched(BlockPos posMin, BlockPos posMax, Vec3d cameraPos, Color4f colorLines, Color4f colorSides,
//                                               BufferBuilder bufferQuads, BufferBuilder bufferLines, MatrixStack matrices)
                                               BufferBuilder bufferQuads, BufferBuilder bufferLines)
    {
        final float x1 = (float) (posMin.getX() - cameraPos.x);
        final float y1 = (float) (posMin.getY() - cameraPos.y);
        final float z1 = (float) (posMin.getZ() - cameraPos.z);
        final float x2 = (float) (posMax.getX() + 1 - cameraPos.x);
        final float y2 = (float) (posMax.getY() + 1 - cameraPos.y);
        final float z2 = (float) (posMax.getZ() + 1 - cameraPos.z);

//        MatrixStack.Entry e = matrices.peek();

        drawBoxAllSidesBatchedQuads(x1, y1, z1, x2, y2, z2, colorSides, bufferQuads);
        drawBoxAllEdgesBatchedLines(x1, y1, z1, x2, y2, z2, colorLines, bufferLines);

//        drawBoxAllEdgesBatchedLines(x1, y1, z1, x2, y2, z2, colorLines, bufferLines, e);
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

    /**
     * Assumes a BufferBuilder in GL_LINES mode has been initialized
     */
    public static void drawBoxAllEdgesBatchedLines(float minX, float minY, float minZ, float maxX, float maxY, float maxZ,
//                                                   Color4f color, BufferBuilder buffer, MatrixStack.Entry e)
                                                   Color4f color, BufferBuilder buffer)
    {
        // West side
        buffer.vertex(minX, minY, minZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(minX, minY, maxZ).color(color.r, color.g, color.b, color.a);

        buffer.vertex(minX, minY, maxZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(minX, maxY, maxZ).color(color.r, color.g, color.b, color.a);

        buffer.vertex(minX, maxY, maxZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(minX, maxY, minZ).color(color.r, color.g, color.b, color.a);

        buffer.vertex(minX, maxY, minZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(minX, minY, minZ).color(color.r, color.g, color.b, color.a);

        // East side
        buffer.vertex(maxX, minY, maxZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(maxX, minY, minZ).color(color.r, color.g, color.b, color.a);

        buffer.vertex(maxX, minY, minZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(maxX, maxY, minZ).color(color.r, color.g, color.b, color.a);

        buffer.vertex(maxX, maxY, minZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(maxX, maxY, maxZ).color(color.r, color.g, color.b, color.a);

        buffer.vertex(maxX, maxY, maxZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(maxX, minY, maxZ).color(color.r, color.g, color.b, color.a);

        // North side (don't repeat the vertical lines that are done by the east/west sides)
        buffer.vertex(maxX, minY, minZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(minX, minY, minZ).color(color.r, color.g, color.b, color.a);

        buffer.vertex(minX, maxY, minZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(maxX, maxY, minZ).color(color.r, color.g, color.b, color.a);

        // South side (don't repeat the vertical lines that are done by the east/west sides)
        buffer.vertex(minX, minY, maxZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(maxX, minY, maxZ).color(color.r, color.g, color.b, color.a);

        buffer.vertex(maxX, maxY, maxZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(minX, maxY, maxZ).color(color.r, color.g, color.b, color.a);
    }

    public static void drawBox(IntBoundingBox bb, Vec3d cameraPos, Color4f color,
//                               BufferBuilder bufferQuads, BufferBuilder bufferLines, MatrixStack matrices)
                               BufferBuilder bufferQuads, BufferBuilder bufferLines)
    {
        float minX = (float) (bb.minX - cameraPos.x);
        float minY = (float) (bb.minY - cameraPos.y);
        float minZ = (float) (bb.minZ - cameraPos.z);
        float maxX = (float) (bb.maxX + 1 - cameraPos.x);
        float maxY = (float) (bb.maxY + 1 - cameraPos.y);
        float maxZ = (float) (bb.maxZ + 1 - cameraPos.z);

//        MatrixStack.Entry e = matrices.peek();
        drawBoxAllSidesBatchedQuads(minX, minY, minZ, maxX, maxY, maxZ, color, bufferQuads);
        drawBoxAllEdgesBatchedLines(minX, minY, minZ, maxX, maxY, maxZ, color, bufferLines);
//        drawBoxAllEdgesBatchedLines(minX, minY, minZ, maxX, maxY, maxZ, color, bufferLines, e);
    }

    public static void drawBoxNoOutlines(IntBoundingBox bb, Vec3d cameraPos, Color4f color,
                                         BufferBuilder bufferQuads)
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

        RenderContext ctx = new RenderContext(() -> "malilib:drawTextPlate", MaLiLibPipelines.POSITION_COLOR_MASA);
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
            BuiltBuffer meshData = buffer.endNullable();

            if (meshData != null)
            {
                ctx.draw(meshData, false);
                meshData.close();
            }

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

//        color(1f, 1f, 1f, 1f);
        culling(true);
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
        RenderContext ctx = new RenderContext(() -> "malilib:renderBlockTargetingOverlay Side", MaLiLibPipelines.POSITION_COLOR_MASA_NO_DEPTH_NO_CULL);
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
            BuiltBuffer meshData = buffer.endNullable();

            if (meshData != null)
            {
                ctx.draw(meshData, false);
                meshData.close();
            }

            ctx.reset();
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("renderBlockTargetingOverlay():1: Draw Exception; {}", err.getMessage());
        }

        int wireColor = -1;

        // Target "Center" -->
        // MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_NO_DEPTH_NO_CULL
        buffer = ctx.start(() -> "malilib:renderBlockTargetingOverlay/center", MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_NO_DEPTH_NO_CULL);

        // Middle small rectangle
        buffer.vertex((float) (x - 0.25), (float) (y - 0.25), (float) z).color(c, c, c, c);
        buffer.vertex((float) (x + 0.25), (float) (y - 0.25), (float) z).color(c, c, c, c);
        buffer.vertex((float) (x + 0.25), (float) (y + 0.25), (float) z).color(c, c, c, c);
        buffer.vertex((float) (x - 0.25), (float) (y + 0.25), (float) z).color(c, c, c, c);
        buffer.vertex((float) (x - 0.25), (float) (y - 0.25), (float) z).color(c, c, c, c);

        try
        {
            BuiltBuffer meshData = buffer.endNullable();

            if (meshData != null)
            {
                ctx.color(wireColor);
                ctx.lineWidth(1.6f);
                ctx.draw(meshData, false, true);
                meshData.close();
            }

            ctx.reset();
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("renderBlockTargetingOverlay():2: Draw Exception; {}", err.getMessage());
        }

        // Target "Edges" -->
        // MaLiLibPipelines.LINES_TRANSLUCENT_NO_DEPTH_NO_CULL
        buffer = ctx.start(() -> "malilib:renderBlockTargetingOverlay/edges", MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_NO_DEPTH_NO_CULL);

        // Bottom left
        buffer.vertex((float) (x - 0.50), (float) (y - 0.50), (float) z).color(c, c, c, c);
        buffer.vertex((float) (x - 0.25), (float) (y - 0.25), (float) z).color(c, c, c, c);

        // Top left
        buffer.vertex((float) (x - 0.50), (float) (y + 0.50), (float) z).color(c, c, c, c);
        buffer.vertex((float) (x - 0.25), (float) (y + 0.25), (float) z).color(c, c, c, c);

        // Bottom right
        buffer.vertex((float) (x + 0.50), (float) (y - 0.50), (float) z).color(c, c, c, c);
        buffer.vertex((float) (x + 0.25), (float) (y - 0.25), (float) z).color(c, c, c, c);

        // Top right
        buffer.vertex((float) (x + 0.50), (float) (y + 0.50), (float) z).color(c, c, c, c);
        buffer.vertex((float) (x + 0.25), (float) (y + 0.25), (float) z).color(c, c, c, c);

        try
        {
            BuiltBuffer meshData = buffer.endNullable();

            if (meshData != null)
            {
                ctx.color(wireColor);
                ctx.lineWidth(1.6f);
                ctx.draw(meshData, false, true);
                meshData.close();
            }

            ctx.close();
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("renderBlockTargetingOverlay():3: Draw Exception; {}", err.getMessage());
        }

        global4fStack.popMatrix();
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

        RenderContext ctx = new RenderContext(() -> "malilib:renderBlockTargetingOverlaySimple/quads", MaLiLibPipelines.POSITION_COLOR_MASA_NO_DEPTH_NO_CULL);
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
            BuiltBuffer meshData = buffer.endNullable();

            if (meshData != null)
            {
                ctx.draw(meshData, false);
                meshData.close();
            }

            ctx.reset();
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("renderBlockTargetingOverlaySimple():1: Draw Exception; {}", err.getMessage());
        }

        // MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_NO_DEPTH_NO_CULL
        buffer = ctx.start(() -> "malilib:renderBlockTargetingOverlaySimple/lines", MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_NO_DEPTH_NO_CULL);

        // Middle rectangle
        buffer.vertex((float) (x - 0.375), (float) (y - 0.375), (float) z).color(c, c, c, c);
        buffer.vertex((float) (x + 0.375), (float) (y - 0.375), (float) z).color(c, c, c, c);
        buffer.vertex((float) (x + 0.375), (float) (y + 0.375), (float) z).color(c, c, c, c);
        buffer.vertex((float) (x - 0.375), (float) (y + 0.375), (float) z).color(c, c, c, c);

        try
        {
            BuiltBuffer meshData = buffer.endNullable();

            if (meshData != null)
            {
                ctx.lineWidth(1.6f);
                ctx.draw(meshData, false, true);
                meshData.close();
            }

            ctx.close();
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("renderBlockTargetingOverlaySimple():2: Draw Exception; {}", err.getMessage());
        }

        global4fStack.popMatrix();
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

    public static void renderMapPreview(DrawContext drawContext, ItemStack stack, int x, int y, int dimensions)
    {
        renderMapPreview(drawContext, stack, x, y, dimensions, true);
    }

    public static void renderMapPreview(DrawContext drawContext, ItemStack stack, int x, int y, int dimensions, boolean requireShift)
    {
        if (stack.getItem() instanceof FilledMapItem && (!requireShift || GuiBase.isShiftDown()))
        {
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
            GpuTextureView gpuTextureView = bindGpuTextureView(bgTexture);

            if (gpuTextureView == null)
            {
                MaLiLib.LOGGER.error("renderMapPreview(): Failed to bind GpuTexture!");
                return;
            }

            addSimpleElement(drawContext,
                    new MaLiLibLightTexturedGuiElement(
                            RenderPipelines.GUI_TEXTURED,
                            TextureSetup.withoutGlTexture(gpuTextureView),
                            new Matrix3x2f(drawContext.getMatrices()),
                            x1, y1, x2, y2,
                            0.0f, 1.0f, 0.0f, 1.0f,
                            -1, uv,
                            peekLastScissor(drawContext))
            );

            if (mapId != null && mapState != null)
            {
                x1 += 8;
                y1 += 8;
//                z = 310;

//                drawContext.enableScissor(x1, y1, x1 + z, y1 + z);
                double scale = (double) (dimensions - 16) / 128.0D;

                drawContext.getMatrices().pushMatrix();
                drawContext.getMatrices().translate(x1, y1);
                drawContext.getMatrices().scale((float) scale, (float) scale);

                MapRenderState mapRenderState = new MapRenderState();
                mc().getMapRenderer().update(mapId, mapState, mapRenderState);
                drawContext.drawMap(mapRenderState);
                drawContext.getMatrices().popMatrix();
//                drawContext.disableScissor();
            }
        }
    }

    public static void renderShulkerBoxPreview(DrawContext drawContext, ItemStack stack, int baseX, int baseY, boolean useBgColors)
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

            if (stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() instanceof ShulkerBoxBlock)
            {
                color = setShulkerboxBackgroundTintColor((ShulkerBoxBlock) ((BlockItem) stack.getItem()).getBlock(), useBgColors);
            }
            else
            {
                color = Colors.WHITE;
            }

            Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
            matrix4fStack.pushMatrix();
            matrix4fStack.translate(0, 0, 500);

            InventoryOverlay.renderInventoryBackground(drawContext, type, x, y, props.slotsPerRow, props.totalSlots, color, mc());
            color = Colors.WHITE;

            if (type == InventoryOverlay.InventoryRenderType.BREWING_STAND)
            {
                InventoryOverlay.renderBrewerBackgroundSlots(drawContext, inv, x, y);
            }

            if (type == InventoryOverlay.InventoryRenderType.CRAFTER && !nbt.isEmpty())
            {
                lockedSlots = NbtBlockUtils.getDisabledSlotsFromNbt(nbt);
                InventoryOverlay.renderInventoryStacks(drawContext, type, inv, x + props.slotOffsetX, y + props.slotOffsetY, props.slotsPerRow, 0, -1, lockedSlots, mc());
            }
            else
            {
                InventoryOverlay.renderInventoryStacks(drawContext, type, inv, x + props.slotOffsetX, y + props.slotOffsetY, props.slotsPerRow, 0, -1, mc());
            }

            matrix4fStack.popMatrix();
        }
    }

    public static void renderBundlePreview(DrawContext drawContext, ItemStack stack, int baseX, int baseY, boolean useBgColors)
    {
        // Default is 9 to make the default display the same as Shulker Boxes
        renderBundlePreview(drawContext, stack, baseX, baseY, 9, useBgColors);
    }

    public static void renderBundlePreview(DrawContext drawContext, ItemStack stack, int baseX, int baseY, int slotsPerRow, boolean useBgColors)
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

            int color = setBundleBackgroundTintColor(stack, useBgColors);

            Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
            matrix4fStack.pushMatrix();
            matrix4fStack.translate(0, 0, 500);

            InventoryOverlay.renderInventoryBackground(drawContext, type, x, y, props.slotsPerRow, props.totalSlots, color, mc());
            InventoryOverlay.renderInventoryStacks(drawContext, type, inv, x + props.slotOffsetX, y + props.slotOffsetY, props.slotsPerRow, 0, count, mc());

            matrix4fStack.popMatrix();
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
    public static void renderNbtItemsPreview(DrawContext drawContext, ItemStack stackIn, @Nonnull NbtCompound itemsTag, int baseX, int baseY, boolean useBgColors)
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

            int color = Colors.WHITE;

            Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
            matrix4fStack.pushMatrix();
            matrix4fStack.translate(0, 0, 500);

            InventoryOverlay.renderInventoryBackground(drawContext, type, x, y, props.slotsPerRow, items.size(), color, mc());

            Inventory inv = InventoryUtils.getAsInventory(items);
            InventoryOverlay.renderInventoryStacks(drawContext, type, inv, x + props.slotOffsetX, y + props.slotOffsetY, props.slotsPerRow, 0, -1, mc());

            matrix4fStack.popMatrix();
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
            final float[] colors = getColorComponents(block.getColor() != null ? block.getColor().getEntityColor() : 0xFF875F87);
            return ColorHelper.fromFloats(1f, colors[0], colors[1], colors[2]);
        }
        else
        {
            return Colors.WHITE;
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
        if (bundle.isIn(ItemTags.BUNDLES) && useBgColors)
        {
            // In 1.17+ there is the uncolored Bundle variant, which returns null from getColor()
//            final DyeColor dye = getBundleColor(bundle);
//            final float[] colors = getColorComponents(dye != null ? dye.getEntityColor() : 0xFFA6572C);
//            return ColorHelper.fromFloats(1f, colors[0], colors[1], colors[2]);

			// Requires Alpha value
			return getBundleColor(bundle);
        }

        return Colors.WHITE;
    }

    // returns real colors now instead of Dye Colors.
    public static int getBundleColor(ItemStack bundle)
    {
        Item item = bundle.getItem();

        if (item == null)
        {
            return Colors.WHITE;
        }
        if (item.equals(Items.WHITE_BUNDLE))            // 	#ffe6e6e6
        {
            return 0xFFE6E6E6;
        }
        else if (item.equals(Items.ORANGE_BUNDLE))      //	#fffb9320
        {
            return 0xFFFB9320;
        }
        else if (item.equals(Items.MAGENTA_BUNDLE))     // 	#ffcc49b9
        {
            return 0xFFCC49B9;
        }
        else if (item.equals(Items.LIGHT_BLUE_BUNDLE))  // 	#ff30afe5
        {
            return 0xFF30AFE5;
        }
        else if (item.equals(Items.YELLOW_BUNDLE))      //	#fff2c705
        {
            return 0xFFF2C705;
        }
        else if (item.equals(Items.LIME_BUNDLE))        // 	#ff9bdf39
        {
            return 0xFF9BDF39;
        }
        else if (item.equals(Items.PINK_BUNDLE))        //	#fff8a6bd
        {
            return 0xFFF8A6BD;
        }
        else if (item.equals(Items.GRAY_BUNDLE))        // 	#ff6c7b83
        {
            return 0xFF6C7B83;
        }
        else if (item.equals(Items.LIGHT_GRAY_BUNDLE))  // 	#ffb1aca3
        {
            return 0xFFB1ACA3;
        }
        else if (item.equals(Items.CYAN_BUNDLE))        //  #ff14b4b4
        {
            return 0xFF14B4B4;
        }
        else if (item.equals(Items.BLUE_BUNDLE))        //  #ff4573c7
        {
            return 0xFF4573C7;
        }
        else if (item.equals(Items.BROWN_BUNDLE))       // 	#ffd18a59
        {
            return 0xFFD18A59;
        }
        else if (item.equals(Items.GREEN_BUNDLE))       // 	#ff77a119
        {
            return 0xFF77A119;
        }
        else if (item.equals(Items.RED_BUNDLE))         //	#ffd2382e
        {
            return 0xFFD2382E;
        }
        else if (item.equals(Items.BLACK_BUNDLE))       //  #ff38364f
        {
            return 0xFF38364F;
        }
        else if (item.equals(Items.PURPLE_BUNDLE))      // 	#ff942aca
        {
            return 0xFF942ACA;
        }
        else
        {
            return 0xFFA6572C;                                // #FFA6572C
        }
    }

    public static int setVillagerBackgroundTintColor(VillagerData data, boolean useBgColors)
    {
        if (useBgColors)
        {
            RegistryEntry<VillagerProfession> profession = data != null ? data.profession() : null;
            return setVillagerBackgroundTintColor(profession, useBgColors);
        }

        return Colors.WHITE;
    }

    public static int setVillagerBackgroundTintColor(RegistryEntry<VillagerProfession> profession, boolean useBgColors)
    {
        if (useBgColors)
        {
            final DyeColor dye = getVillagerColor(profession);

            if (dye != null)
            {
                final float[] colors = getColorComponents(dye.getEntityColor());
                return ColorHelper.fromFloats(1f, colors[0], colors[1], colors[2]);
            }
        }

        return Colors.WHITE;
    }

    // todo - return real colors
    public static DyeColor getVillagerColor(RegistryEntry<VillagerProfession> profession)
    {
        if (profession == null)
        {
            return null;
        }

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

    public static boolean stateModelHasQuads(BlockState state)
    {
        return modelHasQuads(Objects.requireNonNull(MinecraftClient.getInstance().getBlockRenderManager().getModel(state)));
    }

    public static boolean modelHasQuads(@Nonnull BlockStateModel model)
    {
        return hasQuads(model.getParts(new LocalRandom(0)));
    }

    public static boolean hasQuads(List<BlockModelPart> modelParts)
    {
        if (modelParts.isEmpty()) return false;
        int totalSize = 0;

        for (BlockModelPart part : modelParts)
        {
            for (Direction face : PositionUtils.ALL_DIRECTIONS)
            {
                totalSize += part.getQuads(face).size();
            }

            totalSize += part.getQuads(null).size();
        }

        return totalSize > 0;
    }

    public static void renderModelInGui(DrawContext drawContext, int x, int y, BlockState state)
    {
        renderModelInGui(drawContext, x, y, 16, state, 0.625f);
    }

    public static void renderModelInGui(DrawContext drawContext, int x, int y, int size, BlockState state, float scale)
    {
        if (state.getBlock() == Blocks.AIR)
        {
            return;
        }

        // FIXME
//        RenderUtils.addSpecialElement(drawContext, new MaLiLibBlockStateModelGuiElement(
//                state,
//                x, y,
//                size,
//                scale,
//                RenderUtils.peekLastScissor(drawContext))
//        );
    }

    private static void renderModel(BlockStateModel model, BlockState state,
                                    MatrixStack matrices, BufferBuilder builder)
    {
        LocalRandom random = new LocalRandom(0);
        List<BlockModelPart> parts = model.getParts(random);
        MatrixStack.Entry entry = matrices.peek();
        int l = LightmapTextureManager.pack(15, 15);
        int[] light = new int[] { l, l, l, l };
        float[] brightness = new float[] { 0.75f, 0.75f, 0.75f, 1.0f };

        for (BlockModelPart part : parts)
        {
            for (Direction face : PositionUtils.ALL_DIRECTIONS)
            {
                random.setSeed(0);
                renderQuads(part.getQuads(face), brightness, light, entry, builder);
            }

            random.setSeed(0);
            renderQuads(part.getQuads(null), brightness, light, entry, builder);
        }
    }

    private static void renderQuads(List<BakedQuad> quads, float[] brightness, int[] light,
                                    MatrixStack.Entry matrixEntry, BufferBuilder builder)
    {
        for (BakedQuad quad : quads)
        {
            renderQuad(quad, brightness, light, matrixEntry, builder);
        }
    }

    private static void renderQuad(BakedQuad quad, float[] brightness, int[] light,
                                   MatrixStack.Entry matrixEntry, BufferBuilder builder)
    {
        builder.quad(matrixEntry, quad, brightness, 1.0f, 1.0f, 1.0f, 1.0f, light, OverlayTexture.DEFAULT_UV, true);
    }

    private static void renderModelQuadOverlayBatched(BlockPos pos, BufferBuilder buffer, Color4f color, BakedQuad quad)
    {
        final int[] vertexData = quad.vertexData();
        final int x = pos.getX();
        final int y = pos.getY();
        final int z = pos.getZ();
        final int vertexSize = vertexData.length / 4;
        float fx, fy, fz;

        for (int index = 0; index < 4; ++index)
        {
            fx = x + Float.intBitsToFloat(vertexData[index * vertexSize    ]);
            fy = y + Float.intBitsToFloat(vertexData[index * vertexSize + 1]);
            fz = z + Float.intBitsToFloat(vertexData[index * vertexSize + 2]);

            buffer.vertex(fx, fy, fz).color(color.r, color.g, color.b, color.a);
        }
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

    public static LightmapTextureManager lightmap()
    {
        return mc().gameRenderer.getLightmapTextureManager();
    }

    /**
     * Only required for translating the values to their RotationAxis.POSITIVE_?.rotationDegrees() equivalence
     */
    public static float matrix4fRotateFix(float ang) {return (ang * 0.017453292F);}

    public static void renderBlockOutline(BlockPos pos, float expand, float lineWidth, Color4f color)
    {
        renderBlockOutline(pos, expand, lineWidth, color, false);
    }

    public static void renderBlockOutline(BlockPos pos, float expand, float lineWidth, Color4f color, boolean renderThrough)
    {
        // renderThrough ? MaLiLibPipelines.LINES_MASA_SIMPLE_NO_DEPTH_NO_CULL : RenderPipelines.LINES
        RenderContext ctx = new RenderContext(() -> "malilib:renderBlockOutline", renderThrough ? MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_NO_DEPTH_NO_CULL : MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_LEQUAL_DEPTH);
        BufferBuilder buffer = ctx.getBuilder();

        drawBlockBoundingBoxOutlinesBatchedLinesSimple(pos, color, expand, buffer);

        try
        {
            BuiltBuffer meshData = buffer.endNullable();

            if (meshData != null)
            {
                ctx.lineWidth(lineWidth);
                ctx.draw(meshData, false, true);
                meshData.close();
            }

            ctx.close();
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("renderBlockOutline(): Draw Exception; {}", err.getMessage());
        }
    }

    public static void drawBlockBoundingBoxOutlinesBatchedLinesSimple(BlockPos pos, Color4f color,
                                                                      double expand, BufferBuilder buffer)
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

        drawBoxAllEdgesBatchedLines(minX, minY, minZ, maxX, maxY, maxZ, color, buffer);
    }

    public static void drawConnectingLineBatchedLines(BlockPos pos1, BlockPos pos2, boolean center,
                                                      Color4f color, BufferBuilder buffer)
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

        buffer.vertex(x1, y1, z1).color(color.r, color.g, color.b, color.a);
        buffer.vertex(x2, y2, z2).color(color.r, color.g, color.b, color.a);
    }

    public static void renderBlockOutlineOverlapping(BlockPos pos, float expand, float lineWidth,
                                                     Color4f color1, Color4f color2, Color4f color3, Matrix4f matrix4f)
    {
        renderBlockOutlineOverlapping(pos, expand, lineWidth, color1, color2, color3, matrix4f, false);
    }

    public static void renderBlockOutlineOverlapping(BlockPos pos, float expand, float lineWidth,
                                                     Color4f color1, Color4f color2, Color4f color3, Matrix4f matrix4f,
                                                     boolean renderThrough)
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

        // renderThrough ? MaLiLibPipelines.LINES_MASA_SIMPLE_NO_DEPTH_NO_CULL : RenderPipelines.LINES
        RenderContext ctx = new RenderContext(() -> "malilib:renderBlockOutlineOverlapping", renderThrough ? MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_NO_DEPTH_NO_CULL : MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_LEQUAL_DEPTH);
        BufferBuilder buffer = ctx.getBuilder();

        // Min corner
        buffer.vertex(minX, minY, minZ).color(color1.r, color1.g, color1.b, color1.a);
        buffer.vertex(maxX, minY, minZ).color(color1.r, color1.g, color1.b, color1.a);

        buffer.vertex(minX, minY, minZ).color(color1.r, color1.g, color1.b, color1.a);
        buffer.vertex(minX, maxY, minZ).color(color1.r, color1.g, color1.b, color1.a);

        buffer.vertex(minX, minY, minZ).color(color1.r, color1.g, color1.b, color1.a);
        buffer.vertex(minX, minY, maxZ).color(color1.r, color1.g, color1.b, color1.a);

        // Max corner
        buffer.vertex(minX, maxY, maxZ).color(color2.r, color2.g, color2.b, color2.a);
        buffer.vertex(maxX, maxY, maxZ).color(color2.r, color2.g, color2.b, color2.a);

        buffer.vertex(maxX, minY, maxZ).color(color2.r, color2.g, color2.b, color2.a);
        buffer.vertex(maxX, maxY, maxZ).color(color2.r, color2.g, color2.b, color2.a);

        buffer.vertex(maxX, maxY, minZ).color(color2.r, color2.g, color2.b, color2.a);
        buffer.vertex(maxX, maxY, maxZ).color(color2.r, color2.g, color2.b, color2.a);

        // The rest of the edges
        buffer.vertex(minX, maxY, minZ).color(color3.r, color3.g, color3.b, color3.a);
        buffer.vertex(maxX, maxY, minZ).color(color3.r, color3.g, color3.b, color3.a);

        buffer.vertex(minX, minY, maxZ).color(color3.r, color3.g, color3.b, color3.a);
        buffer.vertex(maxX, minY, maxZ).color(color3.r, color3.g, color3.b, color3.a);

        buffer.vertex(maxX, minY, minZ).color(color3.r, color3.g, color3.b, color3.a);
        buffer.vertex(maxX, maxY, minZ).color(color3.r, color3.g, color3.b, color3.a);

        buffer.vertex(minX, minY, maxZ).color(color3.r, color3.g, color3.b, color3.a);
        buffer.vertex(minX, maxY, maxZ).color(color3.r, color3.g, color3.b, color3.a);

        buffer.vertex(maxX, minY, minZ).color(color3.r, color3.g, color3.b, color3.a);
        buffer.vertex(maxX, minY, maxZ).color(color3.r, color3.g, color3.b, color3.a);

        buffer.vertex(minX, maxY, minZ).color(color3.r, color3.g, color3.b, color3.a);
        buffer.vertex(minX, maxY, maxZ).color(color3.r, color3.g, color3.b, color3.a);

        try
        {
            BuiltBuffer meshData = buffer.endNullable();

            if (meshData != null)
            {
                ctx.lineWidth(lineWidth);
                ctx.draw(meshData, false, true);
                meshData.close();
            }

            ctx.close();
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("renderBlockOutlineOverlapping(): Draw Exception; {}", err.getMessage());
        }
    }

    public static void renderAreaOutline(BlockPos pos1, BlockPos pos2, float lineWidth,
                                         Color4f colorX, Color4f colorY, Color4f colorZ)
    {
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

        drawBoundingBoxEdges((float) minX, (float) minY, (float) minZ, (float) maxX, (float) maxY, (float) maxZ, colorX, colorY, colorZ, lineWidth);
    }

    private static void drawBoundingBoxEdges(float minX, float minY, float minZ, float maxX, float maxY, float maxZ,
                                             Color4f colorX, Color4f colorY, Color4f colorZ, float lineWidth)
    {
        // MaLiLibPipelines.LINES_NO_DEPTH_NO_CULL
        RenderContext ctx = new RenderContext(() -> "malilib:drawBoundingBoxEdges", MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_NO_DEPTH_NO_CULL);
        BufferBuilder buffer = ctx.getBuilder();

        drawBoundingBoxLinesX(buffer, minX, minY, minZ, maxX, maxY, maxZ, colorX);
        drawBoundingBoxLinesY(buffer, minX, minY, minZ, maxX, maxY, maxZ, colorY);
        drawBoundingBoxLinesZ(buffer, minX, minY, minZ, maxX, maxY, maxZ, colorZ);

        try
        {
            BuiltBuffer meshData = buffer.endNullable();

            if (meshData != null)
            {
                ctx.lineWidth(lineWidth);
                ctx.draw(meshData, false, true);
                meshData.close();
            }

            ctx.close();
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("drawBoundingBoxEdges(): Draw Exception; {}", err.getMessage());
        }
    }

    private static void drawBoundingBoxLinesX(BufferBuilder buffer, float minX, float minY, float minZ, float maxX, float maxY, float maxZ,
                                              Color4f color)
    {
        buffer.vertex(minX, minY, minZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(maxX, minY, minZ).color(color.r, color.g, color.b, color.a);

        buffer.vertex(minX, maxY, minZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(maxX, maxY, minZ).color(color.r, color.g, color.b, color.a);

        buffer.vertex(minX, minY, maxZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(maxX, minY, maxZ).color(color.r, color.g, color.b, color.a);

        buffer.vertex(minX, maxY, maxZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(maxX, maxY, maxZ).color(color.r, color.g, color.b, color.a);
    }

    private static void drawBoundingBoxLinesY(BufferBuilder buffer, float minX, float minY, float minZ, float maxX, float maxY, float maxZ,
                                              Color4f color)
    {
        buffer.vertex(minX, minY, minZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(minX, maxY, minZ).color(color.r, color.g, color.b, color.a);

        buffer.vertex(maxX, minY, minZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(maxX, maxY, minZ).color(color.r, color.g, color.b, color.a);

        buffer.vertex(minX, minY, maxZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(minX, maxY, maxZ).color(color.r, color.g, color.b, color.a);

        buffer.vertex(maxX, minY, maxZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(maxX, maxY, maxZ).color(color.r, color.g, color.b, color.a);
    }

    private static void drawBoundingBoxLinesZ(BufferBuilder buffer, float minX, float minY, float minZ, float maxX, float maxY, float maxZ,
                                              Color4f color)
    {
        buffer.vertex(minX, minY, minZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(minX, minY, maxZ).color(color.r, color.g, color.b, color.a);

        buffer.vertex(maxX, minY, minZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(maxX, minY, maxZ).color(color.r, color.g, color.b, color.a);

        buffer.vertex(minX, maxY, minZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(minX, maxY, maxZ).color(color.r, color.g, color.b, color.a);

        buffer.vertex(maxX, maxY, minZ).color(color.r, color.g, color.b, color.a);
        buffer.vertex(maxX, maxY, maxZ).color(color.r, color.g, color.b, color.a);
    }

    public static void renderAreaSides(BlockPos pos1, BlockPos pos2, Color4f color, Matrix4f matrix4f)
    {
        renderAreaSides(pos1, pos2, color, matrix4f, false);
    }

    public static void renderAreaSides(BlockPos pos1, BlockPos pos2, Color4f color, Matrix4f matrix4f, boolean shouldResort)
    {
		boolean culling = shouldCull(pos1, pos2);
        // MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_2
        RenderContext ctx = new RenderContext(() -> "malilib:renderAreaSides", culling ? MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_3 : MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_LEQUAL_DEPTH_NO_CULL);
        BufferBuilder buffer = ctx.getBuilder();

        renderAreaSidesBatched(pos1, pos2, color, 0.002, buffer);

        try
        {
            BuiltBuffer meshData = buffer.endNullable();

            if (meshData != null)
            {
                if (shouldResort)
                {
                    ctx.upload(meshData, true);
                    ctx.startResorting(meshData, ctx.createVertexSorter(camPos()));
                }
                else
                {
                    ctx.upload(meshData, false);
                }

                meshData.close();
                ctx.drawPost();
            }

            ctx.close();
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("renderAreaSides(): Draw Exception; {}", err.getMessage());
        }
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

        // RenderPipelines.LINES
        RenderContext ctx = new RenderContext(() -> "malilib:renderAreaOutlineNoCorners", MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_LEQUAL_DEPTH);
        BufferBuilder buffer = ctx.getBuilder();

        // Edges along the X-axis
        start = (pos1.getX() == xMin && pos1.getY() == yMin && pos1.getZ() == zMin) || (pos2.getX() == xMin && pos2.getY() == yMin && pos2.getZ() == zMin) ? xMin + 1 : xMin;
        end = (pos1.getX() == xMax && pos1.getY() == yMin && pos1.getZ() == zMin) || (pos2.getX() == xMax && pos2.getY() == yMin && pos2.getZ() == zMin) ? xMax : xMax + 1;

        if (end > start)
        {
            buffer.vertex(start + dxMin, minY, minZ).color(colorX.r, colorX.g, colorX.b, colorX.a);
            buffer.vertex(end + dxMax, minY, minZ).color(colorX.r, colorX.g, colorX.b, colorX.a);
        }

        start = (pos1.getX() == xMin && pos1.getY() == yMax && pos1.getZ() == zMin) || (pos2.getX() == xMin && pos2.getY() == yMax && pos2.getZ() == zMin) ? xMin + 1 : xMin;
        end = (pos1.getX() == xMax && pos1.getY() == yMax && pos1.getZ() == zMin) || (pos2.getX() == xMax && pos2.getY() == yMax && pos2.getZ() == zMin) ? xMax : xMax + 1;

        if (end > start)
        {
            buffer.vertex(start + dxMin, maxY + 1, minZ).color(colorX.r, colorX.g, colorX.b, colorX.a);
            buffer.vertex(end + dxMax, maxY + 1, minZ).color(colorX.r, colorX.g, colorX.b, colorX.a);
        }

        start = (pos1.getX() == xMin && pos1.getY() == yMin && pos1.getZ() == zMax) || (pos2.getX() == xMin && pos2.getY() == yMin && pos2.getZ() == zMax) ? xMin + 1 : xMin;
        end = (pos1.getX() == xMax && pos1.getY() == yMin && pos1.getZ() == zMax) || (pos2.getX() == xMax && pos2.getY() == yMin && pos2.getZ() == zMax) ? xMax : xMax + 1;

        if (end > start)
        {
            buffer.vertex(start + dxMin, minY, maxZ + 1).color(colorX.r, colorX.g, colorX.b, colorX.a);
            buffer.vertex(end + dxMax, minY, maxZ + 1).color(colorX.r, colorX.g, colorX.b, colorX.a);
        }

        start = (pos1.getX() == xMin && pos1.getY() == yMax && pos1.getZ() == zMax) || (pos2.getX() == xMin && pos2.getY() == yMax && pos2.getZ() == zMax) ? xMin + 1 : xMin;
        end = (pos1.getX() == xMax && pos1.getY() == yMax && pos1.getZ() == zMax) || (pos2.getX() == xMax && pos2.getY() == yMax && pos2.getZ() == zMax) ? xMax : xMax + 1;

        if (end > start)
        {
            buffer.vertex(start + dxMin, maxY + 1, maxZ + 1).color(colorX.r, colorX.g, colorX.b, colorX.a);
            buffer.vertex(end + dxMax, maxY + 1, maxZ + 1).color(colorX.r, colorX.g, colorX.b, colorX.a);
        }

        // Edges along the Y-axis
        start = (pos1.getX() == xMin && pos1.getY() == yMin && pos1.getZ() == zMin) || (pos2.getX() == xMin && pos2.getY() == yMin && pos2.getZ() == zMin) ? yMin + 1 : yMin;
        end = (pos1.getX() == xMin && pos1.getY() == yMax && pos1.getZ() == zMin) || (pos2.getX() == xMin && pos2.getY() == yMax && pos2.getZ() == zMin) ? yMax : yMax + 1;

        if (end > start)
        {
            buffer.vertex(minX, start + dyMin, minZ).color(colorY.r, colorY.g, colorY.b, colorY.a);
            buffer.vertex(minX, end + dyMax, minZ).color(colorY.r, colorY.g, colorY.b, colorY.a);
        }

        start = (pos1.getX() == xMax && pos1.getY() == yMin && pos1.getZ() == zMin) || (pos2.getX() == xMax && pos2.getY() == yMin && pos2.getZ() == zMin) ? yMin + 1 : yMin;
        end = (pos1.getX() == xMax && pos1.getY() == yMax && pos1.getZ() == zMin) || (pos2.getX() == xMax && pos2.getY() == yMax && pos2.getZ() == zMin) ? yMax : yMax + 1;

        if (end > start)
        {
            buffer.vertex(maxX + 1, start + dyMin, minZ).color(colorY.r, colorY.g, colorY.b, colorY.a);
            buffer.vertex(maxX + 1, end + dyMax, minZ).color(colorY.r, colorY.g, colorY.b, colorY.a);
        }

        start = (pos1.getX() == xMin && pos1.getY() == yMin && pos1.getZ() == zMax) || (pos2.getX() == xMin && pos2.getY() == yMin && pos2.getZ() == zMax) ? yMin + 1 : yMin;
        end = (pos1.getX() == xMin && pos1.getY() == yMax && pos1.getZ() == zMax) || (pos2.getX() == xMin && pos2.getY() == yMax && pos2.getZ() == zMax) ? yMax : yMax + 1;

        if (end > start)
        {
            buffer.vertex(minX, start + dyMin, maxZ + 1).color(colorY.r, colorY.g, colorY.b, colorY.a);
            buffer.vertex(minX, end + dyMax, maxZ + 1).color(colorY.r, colorY.g, colorY.b, colorY.a);
        }

        start = (pos1.getX() == xMax && pos1.getY() == yMin && pos1.getZ() == zMax) || (pos2.getX() == xMax && pos2.getY() == yMin && pos2.getZ() == zMax) ? yMin + 1 : yMin;
        end = (pos1.getX() == xMax && pos1.getY() == yMax && pos1.getZ() == zMax) || (pos2.getX() == xMax && pos2.getY() == yMax && pos2.getZ() == zMax) ? yMax : yMax + 1;

        if (end > start)
        {
            buffer.vertex(maxX + 1, start + dyMin, maxZ + 1).color(colorY.r, colorY.g, colorY.b, colorY.a);
            buffer.vertex(maxX + 1, end + dyMax, maxZ + 1).color(colorY.r, colorY.g, colorY.b, colorY.a);
        }

        // Edges along the Z-axis
        start = (pos1.getX() == xMin && pos1.getY() == yMin && pos1.getZ() == zMin) || (pos2.getX() == xMin && pos2.getY() == yMin && pos2.getZ() == zMin) ? zMin + 1 : zMin;
        end = (pos1.getX() == xMin && pos1.getY() == yMin && pos1.getZ() == zMax) || (pos2.getX() == xMin && pos2.getY() == yMin && pos2.getZ() == zMax) ? zMax : zMax + 1;

        if (end > start)
        {
            buffer.vertex(minX, minY, start + dzMin).color(colorZ.r, colorZ.g, colorZ.b, colorZ.a);
            buffer.vertex(minX, minY, end + dzMax).color(colorZ.r, colorZ.g, colorZ.b, colorZ.a);
        }

        start = (pos1.getX() == xMax && pos1.getY() == yMin && pos1.getZ() == zMin) || (pos2.getX() == xMax && pos2.getY() == yMin && pos2.getZ() == zMin) ? zMin + 1 : zMin;
        end = (pos1.getX() == xMax && pos1.getY() == yMin && pos1.getZ() == zMax) || (pos2.getX() == xMax && pos2.getY() == yMin && pos2.getZ() == zMax) ? zMax : zMax + 1;

        if (end > start)
        {
            buffer.vertex(maxX + 1, minY, start + dzMin).color(colorZ.r, colorZ.g, colorZ.b, colorZ.a);
            buffer.vertex(maxX + 1, minY, end + dzMax).color(colorZ.r, colorZ.g, colorZ.b, colorZ.a);
        }

        start = (pos1.getX() == xMin && pos1.getY() == yMax && pos1.getZ() == zMin) || (pos2.getX() == xMin && pos2.getY() == yMax && pos2.getZ() == zMin) ? zMin + 1 : zMin;
        end = (pos1.getX() == xMin && pos1.getY() == yMax && pos1.getZ() == zMax) || (pos2.getX() == xMin && pos2.getY() == yMax && pos2.getZ() == zMax) ? zMax : zMax + 1;

        if (end > start)
        {
            buffer.vertex(minX, maxY + 1, start + dzMin).color(colorZ.r, colorZ.g, colorZ.b, colorZ.a);
            buffer.vertex(minX, maxY + 1, end + dzMax).color(colorZ.r, colorZ.g, colorZ.b, colorZ.a);
        }

        start = (pos1.getX() == xMax && pos1.getY() == yMax && pos1.getZ() == zMin) || (pos2.getX() == xMax && pos2.getY() == yMax && pos2.getZ() == zMin) ? zMin + 1 : zMin;
        end = (pos1.getX() == xMax && pos1.getY() == yMax && pos1.getZ() == zMax) || (pos2.getX() == xMax && pos2.getY() == yMax && pos2.getZ() == zMax) ? zMax : zMax + 1;

        if (end > start)
        {
            buffer.vertex(maxX + 1, maxY + 1, start + dzMin).color(colorZ.r, colorZ.g, colorZ.b, colorZ.a);
            buffer.vertex(maxX + 1, maxY + 1, end + dzMax).color(colorZ.r, colorZ.g, colorZ.b, colorZ.a);
        }

        try
        {
            BuiltBuffer meshData = buffer.endNullable();

            if (meshData != null)
            {
                ctx.lineWidth(lineWidth);
                ctx.draw(meshData, false, true);
                meshData.close();
            }

            ctx.close();
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("drawAreaOutlineNoCorners(): Draw Exception; {}", err.getMessage());
        }
    }

	public static boolean shouldCull(BlockPos pos1, BlockPos pos2)
	{
		return shouldCull(new Box(pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX(), pos2.getY(), pos2.getZ()));
	}

	public static boolean shouldCull(Vec3d pos1, Vec3d pos2)
	{
		return shouldCull(new Box(pos1, pos2));
	}

	/**
	 * The POSITION_COLOR Bottom Side causes Z fighting at certain distances;
	 * If and only if the Side collides with a Block surface.
	 * Calculate `withCull` if and only if the bottom side
	 * is Air that meets with Non-Air.  We are only considering the Center Block Pos here.
	 * NOTE that this causes a noticable "shift" in how the selection box appears when
	 * Enabling culling; so we should only do so in this case; and only to stop "Z Fighting" .
	 * @param bb
	 * @return
	 */
	public static boolean shouldCull(Box bb)
	{
		Entity camera = mc().getCameraEntity();
		final Vec3d minPos = bb.getMinPos();
		final Vec3d maxPos = bb.getMaxPos();
		Vec3d mid = bb.getCenter();
		BlockPos pos;

		if (minPos.getY() < maxPos.getY())
		{
			pos = new BlockPos((int) mid.x, (int) minPos.y, (int) mid.z);
		}
		else
		{
			pos = new BlockPos((int) mid.x, (int) maxPos.y, (int) mid.z);
		}

		if (mc().world != null)
		{
			// Calculate only if the Down Direction is a Block, while above it is Air.
			BlockState state = mc().world.getBlockState(pos);
			BlockState stateDown = mc().world.getBlockState(pos.offset(Direction.DOWN));

			if (camera != null && state.isAir() && !stateDown.isAir())
			{
				// Causes Z fighting on the floor (~24 Block distance)
				return camera.getEntityPos().distanceTo(mid) >= 23;
			}
		}

		return false;
	}
}
