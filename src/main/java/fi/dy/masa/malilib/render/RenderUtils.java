package fi.dy.masa.malilib.render;

import java.util.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix3x2f;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;

import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.gui.render.state.GuiItemRenderState;
import net.minecraft.client.gui.render.state.GuiTextRenderState;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ARGB;
import net.minecraft.util.CommonColors;
import net.minecraft.world.Container;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.villager.VillagerData;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.npc.villager.VillagerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.config.HudAlignment;
import fi.dy.masa.malilib.event.RenderEventHandler;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.interfaces.IGuiRendererInvoker;
import fi.dy.masa.malilib.mixin.render.IMixinAbstractTexture;
import fi.dy.masa.malilib.mixin.render.IMixinGuiGraphics;
import fi.dy.masa.malilib.mixin.render.IMixinGuiRenderer;
import fi.dy.masa.malilib.render.element.*;
import fi.dy.masa.malilib.render.special.MaLiLibBlockStateGuiElementRenderer;
import fi.dy.masa.malilib.render.special.MaLiLibBlockStateGuiElement;
import fi.dy.masa.malilib.util.*;
import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.malilib.util.data.tag.CompoundData;
import fi.dy.masa.malilib.util.data.tag.converter.DataConverterNbt;
import fi.dy.masa.malilib.util.log.AnsiLogger;
import fi.dy.masa.malilib.util.nbt.NbtBlockUtils;
import fi.dy.masa.malilib.util.position.PositionUtils;

public class RenderUtils
{
    private static final AnsiLogger LOGGER = new AnsiLogger(RenderUtils.class);
    public static final Identifier TEXTURE_MAP_BACKGROUND = Identifier.withDefaultNamespace("textures/map/map_background.png");
    public static final Identifier TEXTURE_MAP_BACKGROUND_CHECKERBOARD = Identifier.withDefaultNamespace("textures/map/map_background_checkerboard.png");

    private static final SingleThreadedRandomSource RAND = new SingleThreadedRandomSource(0);

	/**
	 * @deprecated Please transition to using RenderPipelines
	 */
	@Deprecated(forRemoval = true)
    public static void blend(boolean toggle)
    {
        //RenderSystem.enableBlend();
        //RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);

        if (toggle)
        {
            GlStateManager._enableBlend();
//            GlStateManager._blendFuncSeparate(770, 771, 1, 0);
	        GlStateManager._blendFuncSeparate(
					GlConst.toGl(SourceFactor.SRC_ALPHA),
					GlConst.toGl(DestFactor.ONE_MINUS_SRC_ALPHA),
					GlConst.toGl(SourceFactor.ONE),
					GlConst.toGl(DestFactor.ZERO)
	        );
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

	/**
	 * @deprecated Please transition to using RenderPipelines
	 */
	@Deprecated(forRemoval = true)
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

	/**
	 * @deprecated Please transition to using RenderPipelines
	 */
	@Deprecated(forRemoval = true)
    public static void depthFunc(int depth)
    {
        GlStateManager._depthFunc(depth);
    }

	/**
	 * @deprecated Please transition to using RenderPipelines
	 */
	@Deprecated(forRemoval = true)
    public static void depthMask(boolean toggle)
    {
        GlStateManager._depthMask(toggle);
    }

	/**
	 * @deprecated Please transition to using RenderPipelines
	 */
	@Deprecated(forRemoval = true)
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

	/**
	 * @deprecated Please transition to using RenderPipelines
	 */
	@Deprecated(forRemoval = true)
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

	/**
	 * @deprecated Please transition to using RenderPipelines
	 */
	@Deprecated(forRemoval = true)
    public static void polygonOffset(float factor, float units)
    {
        GlStateManager._polygonOffset(factor, units);
    }

	/**
	 * @deprecated Please transition to using RenderPipelines
	 */
    @Deprecated(forRemoval = true)
    public static void fbStartDrawing()
    {
        RenderSystem.assertOnRenderThread();
        GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, 0);
    }

    /**
     * @deprecated Please Migrate to using {@link GuiContext}
     */
	@Deprecated(forRemoval = true)
    public static @Nullable Pair<GpuTexture, GpuSampler> bindGpuTexture(Identifier texture)
    {
        AbstractTexture tex = (AbstractTexture) tex().getTexture(texture);

        if (tex != null && ((IMixinAbstractTexture) tex).malilib_getGlTexture() != null)
        {
            return Pair.of(tex.getTexture(), tex.getSampler());
        }

	    MaLiLib.LOGGER.error("bindGpuTexture: Result is null!");
        return null;
    }

	/**
	 * @deprecated Please Migrate to using {@link GuiContext}
	 */
	@Deprecated(forRemoval = true)
	public static @Nullable Pair<GpuTextureView, GpuSampler> bindGpuTextureView(Identifier texture)
	{
		SimpleTexture tex = (SimpleTexture) tex().getTexture(texture);

		if (tex != null && ((IMixinAbstractTexture) tex).malilib_getGlTextureView() != null)
		{
			return Pair.of(tex.getTextureView(), tex.getSampler());
		}

		MaLiLib.LOGGER.error("bindGpuTextureView: Result is null!");
		return null;
	}

	/**
	 * @deprecated Please Migrate to using {@link GuiContext}
	 */
	@Deprecated(forRemoval = true)
	public static void addSimpleElement(GuiGraphics drawContext, GuiElementRenderState simpleElement)
	{
		((IMixinGuiGraphics) drawContext).malilib_getRenderState().submitGuiElement(simpleElement);
	}

	/**
	 * @deprecated Please Migrate to using {@link GuiContext}
	 */
    @Deprecated(forRemoval = true)
    public static void addSpecialElement(GuiGraphics drawContext, PictureInPictureRenderState specialElement)
    {
        ((IMixinGuiGraphics) drawContext).malilib_getRenderState().submitPicturesInPictureState(specialElement);
    }

    // FIXME
    @ApiStatus.Internal
    public static void registerSpecialGuiRenderers(GuiRenderer guiRenderer, MultiBufferSource.BufferSource immediate, Minecraft mc)
    {
        ImmutableMap.Builder<Class<? extends PictureInPictureRenderState>, PictureInPictureRenderer<?>> builder = new ImmutableMap.Builder<>();

        // Build new ImmutableMap
        builder.putAll(((IMixinGuiRenderer) guiRenderer).malilib_getSpecialGuiRenderers());

        // Add Gui Block Model Renderer
        builder.put(MaLiLibBlockStateGuiElement.class, new MaLiLibBlockStateGuiElementRenderer(immediate, mc.getBlockRenderer()));

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

    public static void dumpBuilderMap(Map<Class<? extends PictureInPictureRenderState>, PictureInPictureRenderer<?>> entries)
    {
        System.out.print("DUMP SpecialGuiRenderers()\n");

        if (entries == null || entries.size() == 0)
        {
            System.out.print("NULL OR EMPTY!\n");
            return;
        }

        int i = 0;

        for (Class<? extends PictureInPictureRenderState> entry : entries.keySet())
        {
            System.out.printf("[%d] K (State): [%s], V (Renderer): [%s]\n", i, entry.getName(), entries.get(entry).getClass().getName());
            i++;
        }

        System.out.print("DUMP END\n");
    }

	/**
	 * @deprecated Please Migrate to using {@link GuiContext}
	 */
	@Deprecated(forRemoval = true)
    public static void addItemElement(GuiGraphics drawContext, GuiItemRenderState itemElement)
    {
        ((IMixinGuiGraphics) drawContext).malilib_getRenderState().submitItem(itemElement);
    }

	/**
	 * @deprecated Please Migrate to using {@link GuiContext}
	 */
	@Deprecated(forRemoval = true)
    public static void addTextElement(GuiGraphics drawContext, GuiTextRenderState textElement)
    {
        ((IMixinGuiGraphics) drawContext).malilib_getRenderState().submitText(textElement);
    }

	/**
	 * @deprecated Please Migrate to using {@link GuiContext}
	 */
	@Deprecated(forRemoval = true)
    public static void pushScissor(GuiGraphics drawContext, @Nonnull ScreenRectangle rect)
    {
        ((IMixinGuiGraphics) drawContext).malilib_getScissorStack().push(rect);
    }

	/**
	 * @deprecated Please Migrate to using {@link GuiContext}
	 */
	@Deprecated(forRemoval = true)
    public static boolean containsScissor(GuiGraphics drawContext, int x, int y)
    {
        return ((IMixinGuiGraphics) drawContext).malilib_getScissorStack().containsPoint(x, y);
    }

	/**
	 * @deprecated Please Migrate to using {@link GuiContext}
	 */
	@Deprecated(forRemoval = true)
    public static ScreenRectangle peekLastScissor(GuiGraphics drawContext)
    {
        return ((IMixinGuiGraphics) drawContext).malilib_getScissorStack().peek();
    }

	/**
	 * @deprecated Please Migrate to using {@link GuiContext}
	 */
	@Deprecated(forRemoval = true)
    public static ScreenRectangle popScissor(GuiGraphics drawContext)
    {
        return ((IMixinGuiGraphics) drawContext).malilib_getScissorStack().pop();
    }

    public static void drawOutlinedBox(GuiContext ctx, int x, int y, int width, int height, int colorBg, int colorBorder)
    {
        // Draw the background
        drawRect(ctx, x, y, width, height, colorBg);

        // Draw the border
        drawOutline(ctx, x - 1, y - 1, width + 2, height + 2, colorBorder);
    }

    public static void drawOutlinedBox(GuiContext ctx, int x, int y, int width, int height, float scale, int colorBg, int colorBorder)
    {
        // Draw the background
        drawRect(ctx, x, y, width, height, colorBg, scale);

        // Draw the border
        drawOutline(ctx, x - 1, y - 1, width + 2, height + 2, scale, colorBorder);
    }

    public static void drawOutline(GuiContext ctx, int x, int y, int width, int height, int colorBorder)
    {
        drawOutline(ctx, x, y, width, height, 1, colorBorder);
    }

    public static void drawOutline(GuiContext ctx, int x, int y, int width, int height, float scale, int colorBorder)
    {
        drawOutline(ctx, x, y, width, height, scale, 1, colorBorder);
    }

    public static void drawOutline(GuiContext ctx, int x, int y, int width, int height, int borderWidth, int colorBorder)
    {
        drawRect(ctx, x, y, borderWidth, height, colorBorder); // left edge
        drawRect(ctx, x + width - borderWidth, y, borderWidth, height, colorBorder); // right edge
        drawRect(ctx, x + borderWidth, y, width - 2 * borderWidth, borderWidth, colorBorder); // top edge
        drawRect(ctx, x + borderWidth, y + height - borderWidth, width - 2 * borderWidth, borderWidth, colorBorder); // bottom edge
    }

    public static void drawOutline(GuiContext ctx, int x, int y, int width, int height, float scale, int borderWidth, int colorBorder)
    {
        drawRect(ctx, x, y, borderWidth, height, colorBorder, scale); // left edge
        drawRect(ctx, x + width - borderWidth, y, borderWidth, height, colorBorder, scale); // right edge
        drawRect(ctx, x + borderWidth, y, width - 2 * borderWidth, borderWidth, colorBorder, scale); // top edge
        drawRect(ctx, x + borderWidth, y + height - borderWidth, width - 2 * borderWidth, borderWidth, colorBorder, scale); // bottom edge
    }

	/**
	 * Old DrawRect.
	 *
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param color
	 */
    @Deprecated
    public static void drawRect(int x, int y, int width, int height, int color)
    {
        drawRect(x, y, width, height, color, 0f);
    }

	/**
	 * Old DrawRect.
	 *
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param color
	 * @param depthMask
	 */
    @Deprecated
    public static void drawRect(int x, int y, int width, int height, int color, boolean depthMask)
    {
        drawRect(x, y, width, height, color, 0f, 1.0f, depthMask);
    }

	/**
	 * Old DrawRect.
	 *
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param color
	 * @param zLevel
	 */
    @Deprecated
    public static void drawRect(int x, int y, int width, int height, int color, float zLevel)
    {
        drawRect(x, y, width, height, color, zLevel, 1.0f, false);
    }

	/**
	 * Old DrawRect.
	 *
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param color
	 * @param zLevel
	 * @param depthMask
	 */
    @Deprecated
    public static void drawRect(int x, int y, int width, int height, int color, float zLevel, boolean depthMask)
    {
        drawRect(x, y, width, height, color, zLevel, 1.0f, depthMask);
    }

	/**
	 * Old DrawRect.
	 *
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param color
	 * @param zLevel
	 * @param scale
	 * @param depthMask
	 */
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

        buffer.addVertex(x * scale,           y * scale,            zLevel).setColor(r, g, b, a);
        buffer.addVertex(x * scale,           (y + height) * scale, zLevel).setColor(r, g, b, a);
        buffer.addVertex((x + width) * scale, (y + height) * scale, zLevel).setColor(r, g, b, a);
        buffer.addVertex((x + width) * scale, y * scale           , zLevel).setColor(r, g, b, a);

        try
        {
            MeshData meshData = buffer.build();

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
     * New drawRect() for GUI Rendering.
	 *
     * @param ctx
     * @param x
     * @param y
     * @param width
     * @param height
     * @param color
     */
    public static void drawRect(GuiContext ctx, int x, int y, int width, int height, int color)
    {
        drawRect(ctx, x, y, width, height, color, 1.0f);
    }

	/**
	 * New drawRect() for GUI Rendering.
	 *
	 * @param ctx
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param color
	 * @param scale
	 */
    public static void drawRect(GuiContext ctx, int x, int y, int width, int height, int color, float scale)
    {
        ctx.addSimpleElement(new MaLiLibBasicRectGuiElement(
                RenderPipelines.GUI,
                TextureSetup.noTexture(),
                new Matrix3x2f(ctx.pose()),
                x, y,
                width, height,
                scale, color,
                ctx.peekLastScissor())
        );
    }

    /**
     * Draws the Vanilla "Screen Blur" effect.
     *
     * @param mc
     */
    public static void drawScreenBlur(Minecraft mc)
    {
        mc.gameRenderer.processBlurEffect();
    }

	/**
	 * Old DrawTextuedRect
	 *
	 * @param posMatrix
	 * @param x
	 * @param y
	 * @param u
	 * @param v
	 * @param width
	 * @param height
	 * @param buffer
	 */
	@Deprecated
    public static void drawTexturedRect(Matrix4f posMatrix, int x, int y, int u, int v, int width, int height, VertexConsumer buffer)
    {
        drawTexturedRect(posMatrix, x, y, u, v, width, height, 0f, -1, buffer);
    }

	/**
	 * Old DrawTextuedRect
	 *
	 * @param posMatrix
	 * @param x
	 * @param y
	 * @param u
	 * @param v
	 * @param width
	 * @param height
	 * @param color
	 * @param buffer
	 */
	@Deprecated
    public static void drawTexturedRect(Matrix4f posMatrix, int x, int y, int u, int v, int width, int height, int color, VertexConsumer buffer)
    {
        drawTexturedRect(posMatrix, x, y, u, v, width, height, 0f, color, buffer);
    }

	/**
	 * Old DrawTextuedRect
	 *
	 * @param posMatrix
	 * @param x
	 * @param y
	 * @param u
	 * @param v
	 * @param width
	 * @param height
	 * @param zLevel
	 * @param color
	 * @param buffer
	 */
	@Deprecated
    public static void drawTexturedRect(Matrix4f posMatrix, int x, int y, int u, int v, int width, int height, float zLevel, int color, VertexConsumer buffer)
    {
        float pixelWidth = 0.00390625F;

        // GUI_TEXTURED_OVERLAY
        buffer.addVertex(posMatrix, x, y + height, zLevel).setUv(u * pixelWidth, (v + height) * pixelWidth).setColor(color);
        buffer.addVertex(posMatrix, x + width, y + height, zLevel).setUv((u + width) * pixelWidth, (v + height) * pixelWidth).setColor(color);
        buffer.addVertex(posMatrix, x + width, y, zLevel).setUv((u + width) * pixelWidth, v * pixelWidth).setColor(color);
        buffer.addVertex(posMatrix, x, y, zLevel).setUv(u * pixelWidth, v * pixelWidth).setColor(color);
    }

    /**
     * New GuiGraphics-based Textured Rect method.
     *
     * @param ctx
     * @param texture
     * @param x
     * @param y
     * @param u
     * @param v
     * @param width
     * @param height
     */
    public static void drawTexturedRect(GuiContext ctx, Identifier texture, int x, int y, int u, int v, int width, int height)
    {
        drawTexturedRect(ctx, texture, x, y, u, v, width, height, 0F, -1);
    }

    /**
     * New GuiGraphics-based Textured Rect method.
     *
     * @param ctx
     * @param texture
     * @param x
     * @param y
     * @param u
     * @param v
     * @param width
     * @param height
     * @param zLevel (NOT USED)
     */
    public static void drawTexturedRect(GuiContext ctx, Identifier texture, int x, int y, int u, int v, int width, int height, float zLevel)
    {
        drawTexturedRect(ctx, texture, x, y, u, v, width, height, zLevel, -1);
    }

    /**
     * New GuiGraphics-based Textured Rect method.
     *
     * @param ctx
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
    public static void drawTexturedRect(GuiContext ctx, Identifier texture, int x, int y, int u, int v, int width, int height, float zLevel, int argb)
    {
        float pixelWidth = 0.00390625F;
        Pair<GpuTextureView, GpuSampler> pair = ctx.bindTexture(texture);

        if (pair == null)
        {
            MaLiLib.LOGGER.error("drawTexturedRect(): GpuTextureView for '{}' is null!", texture.toString());
            return;
        }

        ctx.addSimpleElement(new MaLiLibTexturedGuiElement(
                RenderPipelines.GUI_TEXTURED,
                ctx.setupTexture(pair),
                new Matrix3x2f(ctx.pose()),
                x, y, x + width, y + height,
                u * pixelWidth, (u + width) * pixelWidth,
                v * pixelWidth, (v + height) * pixelWidth,
                argb,
                ctx.peekLastScissor())
        );
    }

	/**
	 * Old DrawTexturedRectBatched
	 *
	 * @param x
	 * @param y
	 * @param u
	 * @param v
	 * @param width
	 * @param height
	 * @param buffer
	 */
	@Deprecated
    public static void drawTexturedRectBatched(int x, int y, int u, int v, int width, int height, VertexConsumer buffer)
    {
        drawTexturedRectBatched(x, y, u, v, width, height, 0, -1, buffer);
    }

	/**
	 * Old DrawTexturedRectBatched
	 *
	 * @param x
	 * @param y
	 * @param u
	 * @param v
	 * @param width
	 * @param height
	 * @param argb
	 * @param buffer
	 */
	@Deprecated
    public static void drawTexturedRectBatched(int x, int y, int u, int v, int width, int height, int argb, VertexConsumer buffer)
    {
        drawTexturedRectBatched(x, y, u, v, width, height, 0, argb, buffer);
    }

	/**
	 * Old DrawTexturedRectBatched
	 *
	 * @param x
	 * @param y
	 * @param u
	 * @param v
	 * @param width
	 * @param height
	 * @param zLevel
	 * @param argb
	 * @param buffer
	 */
	@Deprecated
    public static void drawTexturedRectBatched(int x, int y, int u, int v, int width, int height, float zLevel, int argb, VertexConsumer buffer)
    {
        float pixelWidth = 0.00390625F;

        buffer.addVertex(x, y + height, zLevel).setUv(u * pixelWidth, (v + height) * pixelWidth).setColor(argb);
        buffer.addVertex(x + width, y + height, zLevel).setUv((u + width) * pixelWidth, (v + height) * pixelWidth).setColor(argb);
        buffer.addVertex(x + width, y, zLevel).setUv((u + width) * pixelWidth, v * pixelWidth).setColor(argb);
        buffer.addVertex(x, y, zLevel).setUv(u * pixelWidth, v * pixelWidth).setColor(argb);
    }

	/**
	 * New GuiGraphics-based DrawTexturedBatched
	 *
	 * @param ctx
	 * @param pair
	 * @param x
	 * @param y
	 * @param u
	 * @param v
	 * @param width
	 * @param height
	 */
    public static void drawTexturedRectBatched(GuiContext ctx, @Nonnull Pair<GpuTextureView, GpuSampler> pair, int x, int y, int u, int v, int width, int height)
    {
        drawTexturedRectBatched(ctx, pair, x, y, u, v, width, height, 0, -1);
    }

	/**
	 * New GuiGraphics-based DrawTexturedBatched
	 *
	 * @param ctx
	 * @param pair
	 * @param x
	 * @param y
	 * @param u
	 * @param v
	 * @param width
	 * @param height
	 * @param argb
	 */
    public static void drawTexturedRectBatched(GuiContext ctx, @Nonnull Pair<GpuTextureView, GpuSampler> pair, int x, int y, int u, int v, int width, int height, int argb)
    {
        drawTexturedRectBatched(ctx, pair, x, y, u, v, width, height, 0, argb);
    }

	/**
	 * New GuiGraphics-based DrawTexturedBatched
	 *
	 * @param ctx
	 * @param pair
	 * @param x
	 * @param y
	 * @param u
	 * @param v
	 * @param width
	 * @param height
	 * @param zLevel
	 * @param argb
	 */
    public static void drawTexturedRectBatched(GuiContext ctx, @Nonnull Pair<GpuTextureView, GpuSampler> pair, int x, int y, int u, int v, int width, int height, float zLevel, int argb)
    {
        ctx.addSimpleElement(new MaLiLibTexturedRectGuiElement(
				RenderPipelines.GUI_TEXTURED,
				ctx.setupTexture(pair),
				new Matrix3x2f(ctx.pose()),
				x, y, u, v,
				width, height, argb,
				ctx.peekLastScissor())
        );
    }

	/**
	 * Draw a 'Hover Text' Bubble object, simillar to Vanilla.
	 *
	 * @param ctx
	 * @param x
	 * @param y
	 * @param textLines
	 */
    public static void drawHoverText(GuiContext ctx, int x, int y, List<String> textLines)
    {
        if (textLines.isEmpty() == false && GuiUtils.getCurrentScreen() != null)
        {
            Font font = mc().font;
            int maxLineLength = 0;
            int maxWidth = GuiUtils.getCurrentScreen().width;
            List<String> linesNew = new ArrayList<>();

            for (String lineOrig : textLines)
            {
                String[] lines = lineOrig.split("\\n");

                for (String line : lines)
                {
                    int length = font.width(line);

                    if (length > maxLineLength)
                    {
                        maxLineLength = length;
                    }

                    linesNew.add(line);
                }
            }

            textLines = linesNew;

            final int lineHeight = font.lineHeight + 1;
            int textHeight = textLines.size() * lineHeight - 2;
            int textStartX = x + 4;
            int textStartY = Math.max(8, y - textHeight - 6);

            if (textStartX + maxLineLength + 6 > maxWidth)
            {
                textStartX = Math.max(2, maxWidth - maxLineLength - 8);
            }

	        ctx.pose().pushMatrix();
	        ctx.pose().translate(0, 0);

            float zLevel = (float) 300;
            int borderColor = 0xF0100010;
            drawGradientRectBatched(ctx, textStartX - 3, textStartY - 4, textStartX + maxLineLength + 3, textStartY - 3, borderColor, borderColor);
            drawGradientRectBatched(ctx, textStartX - 3, textStartY + textHeight + 3, textStartX + maxLineLength + 3, textStartY + textHeight + 4, borderColor, borderColor);
            drawGradientRectBatched(ctx, textStartX - 3, textStartY - 3, textStartX + maxLineLength + 3, textStartY + textHeight + 3, borderColor, borderColor);
            drawGradientRectBatched(ctx, textStartX - 4, textStartY - 3, textStartX - 3, textStartY + textHeight + 3, borderColor, borderColor);
            drawGradientRectBatched(ctx, textStartX + maxLineLength + 3, textStartY - 3, textStartX + maxLineLength + 4, textStartY + textHeight + 3, borderColor, borderColor);

            int fillColor1 = 0x505000FF;
            int fillColor2 = 0x5028007F;
            drawGradientRectBatched(ctx, textStartX - 3, textStartY - 3 + 1, textStartX - 3 + 1, textStartY + textHeight + 3 - 1, fillColor1, fillColor2);
            drawGradientRectBatched(ctx, textStartX + maxLineLength + 2, textStartY - 3 + 1, textStartX + maxLineLength + 3, textStartY + textHeight + 3 - 1, fillColor1, fillColor2);
            drawGradientRectBatched(ctx, textStartX - 3, textStartY - 3, textStartX + maxLineLength + 3, textStartY - 3 + 1, fillColor1, fillColor1);
            drawGradientRectBatched(ctx, textStartX - 3, textStartY + textHeight + 2, textStartX + maxLineLength + 3, textStartY + textHeight + 3, fillColor2, fillColor2);

            for (int i = 0; i < textLines.size(); ++i)
            {
                String str = textLines.get(i);
	            ctx.drawString(font, str, textStartX, textStartY, 0xFFFFFFFF, false);
                textStartY += lineHeight;
            }

	        ctx.pose().popMatrix();
        }
    }

	/**
	 * Draw a Gradient Rect Element
	 *
	 * @param ctx
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 * @param startColor
	 * @param endColor
	 */
    public static void drawGradientRectBatched(GuiContext ctx, float left, float top, float right, float bottom, int startColor, int endColor)
    {
        ctx.addSimpleElement(new MaLiLibGradientRectGuiElement(
                RenderPipelines.GUI,
                TextureSetup.noTexture(),
                new Matrix3x2f(ctx.pose()),
                left, top, right, bottom,
                startColor, endColor,
                ctx.peekLastScissor())
        );
    }

	/**
	 * Old DrawGradientRect
	 *
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 * @param zLevel
	 * @param startColor
	 * @param endColor
	 */
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

        buffer.addVertex(right, top, zLevel).setColor(sr, sg, sb, sa);
        buffer.addVertex(left, top, zLevel).setColor(sr, sg, sb, sa);
        buffer.addVertex(left, bottom, zLevel).setColor(er, eg, eb, ea);
        buffer.addVertex(right, bottom, zLevel).setColor(er, eg, eb, ea);

        try
        {
            MeshData meshData = buffer.build();

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

	/**
	 * Render a Centered String (GUI)
	 *
	 * @param ctx
	 * @param x
	 * @param y
	 * @param color
	 * @param text
	 */
    public static void drawCenteredString(GuiContext ctx, int x, int y, int color, String text)
    {
	    ctx.drawCenteredString(mc().font, text, x, y, color);
    }

	/**
	 * Render a Horizontal Line (GUI)
	 *
	 * @param ctx
	 * @param x
	 * @param y
	 * @param width
	 * @param color
	 */
    public static void drawHorizontalLine(GuiContext ctx, int x, int y, int width, int color)
    {
        drawRect(ctx, x, y, width, 1, color);
    }

	/**
	 * Render a Vertical Line (GUI)
	 *
	 * @param ctx
	 * @param x
	 * @param y
	 * @param height
	 * @param color
	 */
    public static void drawVerticalLine(GuiContext ctx, int x, int y, int height, int color)
    {
        drawRect(ctx, x, y, 1, height, color);
    }

	/**
	 * Render a Texture Atlas Sprite (GUI)
	 *
	 * @param ctx
	 * @param atlas
	 * @param texture
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
    public static void renderSprite(GuiContext ctx, Identifier atlas, Identifier texture, int x, int y, int width, int height)
    {
        if (texture != null)
        {
            TextureAtlasSprite sprite = mc().getAtlasManager().getAtlasOrThrow(atlas).getSprite(texture);

            if (sprite != null)
            {
	            ctx.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, width, height, -1);
            }
        }
    }

	/**
	 * Render Text (GUI)
	 *
	 * @param ctx
	 * @param x
	 * @param y
	 * @param color
	 * @param text
	 */
    public static void renderText(GuiContext ctx, int x, int y, int color, String text)
    {
        String[] parts = text.split("\\\\n");
        Font textRenderer = mc().font;

        for (String line : parts)
        {
	        ctx.drawString(textRenderer, line, x, y, color, true);
            y += textRenderer.lineHeight + 1;
        }
    }

	/**
	 * Render Text (GUI)
	 *
	 * @param ctx
	 * @param x
	 * @param y
	 * @param color
	 * @param lines
	 */
    public static void renderText(GuiContext ctx, int x, int y, int color, List<String> lines)
    {
        if (lines.isEmpty() == false)
        {
            Font textRenderer = mc().font;

            for (String line : lines)
            {
	            ctx.drawString(textRenderer, line, x, y, color, false);
                y += textRenderer.lineHeight + 2;
            }
        }
    }

	/**
	 * Render Scaled Text with a background (GUI)
	 *
	 * @param ctx
	 * @param xOff
	 * @param yOff
	 * @param scale
	 * @param textColor
	 * @param bgColor
	 * @param alignment
	 * @param useBackground
	 * @param useShadow
	 * @param lines
	 * @return
	 */
    public static int renderText(GuiContext ctx, int xOff, int yOff, double scale,
                                 int textColor, int bgColor, HudAlignment alignment,
                                 boolean useBackground, boolean useShadow,
                                 List<String> lines)
    {
        return renderText(ctx, xOff, yOff, scale,
                          textColor, bgColor, alignment,
                          useBackground, useShadow, true,
                          lines);
    }

	/**
	 * Render Scaled Text with a background (GUI)
	 *
	 * @param ctx
	 * @param xOff
	 * @param yOff
	 * @param scale
	 * @param textColor
	 * @param bgColor
	 * @param alignment
	 * @param useBackground
	 * @param useShadow
	 * @param useStatusShift
	 * @param lines
	 * @return
	 */
    public static int renderText(GuiContext ctx,
                                 int xOff, int yOff, double scale,
                                 int textColor, int bgColor, HudAlignment alignment,
                                 boolean useBackground, boolean useShadow, boolean useStatusShift,
                                 List<String> lines)
    {
        Font fontRenderer = mc().font;
        final int scaledWidth = GuiUtils.getScaledWindowWidth();
        final int lineHeight = fontRenderer.lineHeight + 2;
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

	        ctx.pose().pushMatrix();
	        ctx.pose().scale((float) scale, (float) scale);      // z = 1.0f
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
            final int width = fontRenderer.width(line);

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
                drawRect(ctx, x - bgMargin, y - bgMargin, width + bgMargin, bgMargin + fontRenderer.lineHeight, bgColor);
            }

	        ctx.drawString(fontRenderer, line, x, y, textColor, useShadow);
        }

        if (scaled)
        {
	        ctx.pose().popMatrix();
        }

        return contentHeight + bgMargin * 2;
    }

	/**
	 * Calculate HUD offest based on Active Effects
	 *
	 * @param alignment
	 * @param scale
	 * @param player
	 * @return
	 */
    public static int getHudOffsetForPotions(HudAlignment alignment, double scale, Player player)
    {
        if (alignment == HudAlignment.TOP_RIGHT)
        {
            // Only Chuck Norris can divide by zero
            if (scale == 0d)
            {
                return 0;
            }

            Collection<MobEffectInstance> effects = player.getActiveEffects();
            boolean hasTurtleHelmet = EntityUtils.hasTurtleHelmetEquipped(player);
            // Turtle Helmets only add their status effects when in water

            if (effects.isEmpty() == false)
            {
                int y1 = 0;
                int y2 = 0;

                for (MobEffectInstance effectInstance : effects)
                {
                    MobEffect effect = effectInstance.getEffect().value();

                    if (effectInstance.isVisible() && effectInstance.showIcon())
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

    public static void drawBlockBoundingBoxSidesBatchedQuads(BlockPos pos, Vec3 cameraPos, Color4f color, double expand,
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
                                                                float lineWidth,
                                                                BufferBuilder buffer)
    {
        drawBlockBoundingBoxOutlinesBatchedLines(pos, Vec3.ZERO, color, expand, lineWidth, buffer);
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
    public static void drawBlockBoundingBoxOutlinesBatchedLines(BlockPos pos, Vec3 cameraPos, Color4f color, double expand,
																float lineWidth,
                                                                BufferBuilder buffer)
    {
        float minX = (float) (pos.getX() - expand - cameraPos.x);
        float minY = (float) (pos.getY() - expand - cameraPos.y);
        float minZ = (float) (pos.getZ() - expand - cameraPos.z);
        float maxX = (float) (pos.getX() + expand - cameraPos.x + 1);
        float maxY = (float) (pos.getY() + expand - cameraPos.y + 1);
        float maxZ = (float) (pos.getZ() + expand - cameraPos.z + 1);

        drawBoxAllEdgesBatchedLines(minX, minY, minZ, maxX, maxY, maxZ, color, lineWidth, buffer);
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
     * @param lineWidth
     * @param bufferQuads
     * @param bufferLines
     */
    public static void drawBoxWithEdgesBatched(BlockPos posMin, BlockPos posMax, Color4f colorLines, Color4f colorSides,
                                               float lineWidth,
                                               BufferBuilder bufferQuads, BufferBuilder bufferLines)
    {
        drawBoxWithEdgesBatched(posMin, posMax, Vec3.ZERO, colorLines, colorSides, lineWidth, bufferQuads, bufferLines);
    }

    /**
     * Draws a box with outlines around the given corner positions.
     * Takes in buffers initialized for GL_QUADS and GL_LINES modes.
     * The cameraPos value will be subtracted from the absolute coordinate values of the passed in block positions.
     *
	 * Requires a Pipeline with a LINE_WIDTH param.
	 *
     * @param posMin
     * @param posMax
     * @param cameraPos
     * @param colorLines
     * @param colorSides
     * @param lineWidth
     * @param bufferQuads
     * @param bufferLines
     */
    public static void drawBoxWithEdgesBatched(BlockPos posMin, BlockPos posMax, Vec3 cameraPos, Color4f colorLines, Color4f colorSides,
											   float lineWidth,
                                               BufferBuilder bufferQuads, BufferBuilder bufferLines)
    {
        final float x1 = (float) (posMin.getX() - cameraPos.x);
        final float y1 = (float) (posMin.getY() - cameraPos.y);
        final float z1 = (float) (posMin.getZ() - cameraPos.z);
        final float x2 = (float) (posMax.getX() + 1 - cameraPos.x);
        final float y2 = (float) (posMax.getY() + 1 - cameraPos.y);
        final float z2 = (float) (posMax.getZ() + 1 - cameraPos.z);

        drawBoxAllSidesBatchedQuads(x1, y1, z1, x2, y2, z2, colorSides, bufferQuads);
        drawBoxAllEdgesBatchedLines(x1, y1, z1, x2, y2, z2, colorLines, lineWidth, bufferLines);
    }

    /**
     * Assumes a BufferBuilder in GL_QUADS mode has been initialized
     */
    public static void drawBoxHorizontalSidesBatchedQuads(float minX, float minY, float minZ, float maxX, float maxY, float maxZ,
                                                          Color4f color, BufferBuilder buffer)
    {
        // West side
        buffer.addVertex(minX, minY, minZ).setColor(color.r, color.g, color.b, color.a);
        buffer.addVertex(minX, minY, maxZ).setColor(color.r, color.g, color.b, color.a);
        buffer.addVertex(minX, maxY, maxZ).setColor(color.r, color.g, color.b, color.a);
        buffer.addVertex(minX, maxY, minZ).setColor(color.r, color.g, color.b, color.a);

        // East side
        buffer.addVertex(maxX, minY, maxZ).setColor(color.r, color.g, color.b, color.a);
        buffer.addVertex(maxX, minY, minZ).setColor(color.r, color.g, color.b, color.a);
        buffer.addVertex(maxX, maxY, minZ).setColor(color.r, color.g, color.b, color.a);
        buffer.addVertex(maxX, maxY, maxZ).setColor(color.r, color.g, color.b, color.a);

        // North side
        buffer.addVertex(maxX, minY, minZ).setColor(color.r, color.g, color.b, color.a);
        buffer.addVertex(minX, minY, minZ).setColor(color.r, color.g, color.b, color.a);
        buffer.addVertex(minX, maxY, minZ).setColor(color.r, color.g, color.b, color.a);
        buffer.addVertex(maxX, maxY, minZ).setColor(color.r, color.g, color.b, color.a);

        // South side
        buffer.addVertex(minX, minY, maxZ).setColor(color.r, color.g, color.b, color.a);
        buffer.addVertex(maxX, minY, maxZ).setColor(color.r, color.g, color.b, color.a);
        buffer.addVertex(maxX, maxY, maxZ).setColor(color.r, color.g, color.b, color.a);
        buffer.addVertex(minX, maxY, maxZ).setColor(color.r, color.g, color.b, color.a);
    }

    /**
     * Assumes a BufferBuilder in GL_QUADS mode has been initialized
     */
    public static void drawBoxTopBatchedQuads(float minX, float minZ, float maxX, float maxY, float maxZ, Color4f color, BufferBuilder buffer)
    {
        // Top side
        buffer.addVertex(minX, maxY, maxZ).setColor(color.r, color.g, color.b, color.a);
        buffer.addVertex(maxX, maxY, maxZ).setColor(color.r, color.g, color.b, color.a);
        buffer.addVertex(maxX, maxY, minZ).setColor(color.r, color.g, color.b, color.a);
        buffer.addVertex(minX, maxY, minZ).setColor(color.r, color.g, color.b, color.a);
    }

    /**
     * Assumes a BufferBuilder in GL_QUADS mode has been initialized
     */
    public static void drawBoxBottomBatchedQuads(float minX, float minY, float minZ, float maxX, float maxZ, Color4f color, BufferBuilder buffer)
    {
        // Bottom side
        buffer.addVertex(maxX, minY, maxZ).setColor(color.r, color.g, color.b, color.a);
        buffer.addVertex(minX, minY, maxZ).setColor(color.r, color.g, color.b, color.a);
        buffer.addVertex(minX, minY, minZ).setColor(color.r, color.g, color.b, color.a);
        buffer.addVertex(maxX, minY, minZ).setColor(color.r, color.g, color.b, color.a);
    }

    /**
     * Assumes a BufferBuilder in GL_LINES mode has been initialized
	 *
	 * Requires a Pipeline with a LINE_WIDTH param.
     */
    public static void drawBoxAllEdgesBatchedLines(float minX, float minY, float minZ, float maxX, float maxY, float maxZ,
                                                   Color4f color, float lineWidth,
                                                   BufferBuilder buffer)
    {
        // West side
        buffer.addVertex(minX, minY, minZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);
        buffer.addVertex(minX, minY, maxZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);

        buffer.addVertex(minX, minY, maxZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);
        buffer.addVertex(minX, maxY, maxZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);

        buffer.addVertex(minX, maxY, maxZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);
        buffer.addVertex(minX, maxY, minZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);

        buffer.addVertex(minX, maxY, minZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);
        buffer.addVertex(minX, minY, minZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);

        // East side
        buffer.addVertex(maxX, minY, maxZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);
        buffer.addVertex(maxX, minY, minZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);

        buffer.addVertex(maxX, minY, minZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);
        buffer.addVertex(maxX, maxY, minZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);

        buffer.addVertex(maxX, maxY, minZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);
        buffer.addVertex(maxX, maxY, maxZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);

        buffer.addVertex(maxX, maxY, maxZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);
        buffer.addVertex(maxX, minY, maxZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);

        // North side (don't repeat the vertical lines that are done by the east/west sides)
        buffer.addVertex(maxX, minY, minZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);
        buffer.addVertex(minX, minY, minZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);

        buffer.addVertex(minX, maxY, minZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);
        buffer.addVertex(maxX, maxY, minZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);

        // South side (don't repeat the vertical lines that are done by the east/west sides)
        buffer.addVertex(minX, minY, maxZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);
        buffer.addVertex(maxX, minY, maxZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);

        buffer.addVertex(maxX, maxY, maxZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);
        buffer.addVertex(minX, maxY, maxZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);
    }

    public static void drawBox(IntBoundingBox bb, Vec3 cameraPos, Color4f color, float lineWidth,
                               BufferBuilder bufferQuads, BufferBuilder bufferLines)
    {
        float minX = (float) (bb.minX() - cameraPos.x);
        float minY = (float) (bb.minY() - cameraPos.y);
        float minZ = (float) (bb.minZ() - cameraPos.z);
        float maxX = (float) (bb.maxX() + 1 - cameraPos.x);
        float maxY = (float) (bb.maxY() + 1 - cameraPos.y);
        float maxZ = (float) (bb.maxZ() + 1 - cameraPos.z);

        drawBoxAllSidesBatchedQuads(minX, minY, minZ, maxX, maxY, maxZ, color, bufferQuads);
        drawBoxAllEdgesBatchedLines(minX, minY, minZ, maxX, maxY, maxZ, color, lineWidth, bufferLines);
    }

    public static void drawBoxNoOutlines(IntBoundingBox bb, Vec3 cameraPos, Color4f color,
                                         BufferBuilder bufferQuads)
    {
        float minX = (float) (bb.minX() - cameraPos.x);
        float minY = (float) (bb.minY() - cameraPos.y);
        float minZ = (float) (bb.minZ() - cameraPos.z);
        float maxX = (float) (bb.maxX() + 1 - cameraPos.x);
        float maxY = (float) (bb.maxY() + 1 - cameraPos.y);
        float maxZ = (float) (bb.maxZ() + 1 - cameraPos.z);

        drawBoxAllSidesBatchedQuads(minX, minY, minZ, maxX, maxY, maxZ, color, bufferQuads);
    }

	/**
	 * @deprecated (Use the Tick Progress version to remove the "jumpy" text problem)<br>
	 * -
	 * Renders a text plate/billboard, similar to the player name plate.<br>
	 * The plate will always face towards the viewer.
	 *
	 * @param text  List of strings
	 * @param x     xPos
	 * @param y     yPos
	 * @param z     zPos
	 * @param scale FontScale
	 */
	@Deprecated
	public static void drawTextPlate(List<String> text, double x, double y, double z, float scale)
	{
		Entity entity = mc().getCameraEntity();

		if (entity != null)
		{
			drawTextPlate(text, x, y, z, entity.getYRot(), entity.getXRot(), scale, 0xFFFFFFFF, 0x40000000, true);
		}
	}

	/**
	 * Renders a text plate/billboard, similar to the player name plate.<br>
	 * The plate will always face towards the viewer.
	 *
	 * @param text  List of strings
	 * @param x     xPos
	 * @param y     yPos
	 * @param z     zPos
	 * @param scale FontScale
	 * @param delta Tick Progress for Lerping the Camera Entity Rotations
	 */
	public static void drawTextPlate(List<String> text, double x, double y, double z, float scale, float delta)
	{
		Entity entity = mc().getCameraEntity();

		if (entity != null)
		{
			drawTextPlate(text, x, y, z, entity.getYRot(delta), entity.getXRot(delta), scale, 0xFFFFFFFF, 0x40000000, true);
		}
	}

	/**
	 * Renders a text plate/billboard, similar to the player name plate.<br>
	 * The plate will always face towards the viewer.
	 *
	 * @param text  List of strings
	 * @param x     xPos
	 * @param y     yPos
	 * @param z     zPos
	 * @param yaw       Camera Yaw / YRot
	*  @param pitch     Camera Pitch / XRot
	 * @param scale     FontScale
	 * @param textColor     Text Color
	 * @param bgColor       Background Color of the Rectangle
	 * @param disableDepth  Disable Depth Test (renderThrough)
	 */
	public static void drawTextPlate(List<String> text,
                                     double x, double y, double z,
                                     float yaw, float pitch,
                                     float scale, int textColor, int bgColor,
                                     boolean disableDepth)
    {
        Vec3 cameraPos = camPos();
        double cx = cameraPos.x;
        double cy = cameraPos.y;
        double cz = cameraPos.z;
        Font textRenderer = mc().font;

        Matrix4fStack global4fStack = RenderSystem.getModelViewStack();

        global4fStack.pushMatrix();
        global4fStack.translate((float) (x - cx), (float) (y - cy), (float) (z - cz));
        //  Wrap it with matrix4fRotateFix() if rotation errors are found.
        global4fStack.rotateYXZ((-yaw) * ((float) (Math.PI / 180.0)), pitch * ((float) (Math.PI / 180.0)), 0.0F);
        global4fStack.scale((-scale), (-scale), scale);

        RenderContext ctx = new RenderContext(() -> "malilib:drawTextPlate", disableDepth ? MaLiLibPipelines.TEXT_PLATE_MASA_NO_DEPTH : MaLiLibPipelines.TEXT_PLATE_MASA);
        BufferBuilder buffer = ctx.getBuilder();
        int maxLineLen = 0;

        for (String line : text)
        {
            maxLineLen = MathUtils.max(maxLineLen, textRenderer.width(line));
        }

        int strLenHalf = maxLineLen / 2;
        int textHeight = textRenderer.lineHeight * text.size() - 1;
        int bga = ((bgColor >>> 24) & 0xFF);
        int bgr = ((bgColor >>> 16) & 0xFF);
        int bgg = ((bgColor >>> 8) & 0xFF);
        int bgb = (bgColor & 0xFF);

        buffer.addVertex((float) (-strLenHalf - 1), (float) -1, 0.0F).setColor(bgr, bgg, bgb, bga);
        buffer.addVertex((float) (-strLenHalf - 1), (float) textHeight, 0.0F).setColor(bgr, bgg, bgb, bga);
        buffer.addVertex((float) strLenHalf, (float) textHeight, 0.0F).setColor(bgr, bgg, bgb, bga);
        buffer.addVertex((float) strLenHalf, (float) -1, 0.0F).setColor(bgr, bgg, bgb, bga);

        try
        {
            MeshData meshData = buffer.build();

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

        Matrix4f modelMatrix = new Matrix4f();
        modelMatrix.identity();

        ByteBufferBuilder allocator = new ByteBufferBuilder(RenderType.TRANSIENT_BUFFER_SIZE);

        for (String line : text)
        {
			// f = x
	        // g = y
	        // i = color
	        // bl = shadow
	        // j = bgColor
	        // k = light
	        // bl2 = incl empty

            MultiBufferSource.BufferSource immediate = MultiBufferSource.immediate(allocator);

            textRenderer.drawInBatch(line, -strLenHalf, textY,
                                     disableDepth ? (0x20000000 | (textColor & 0xFFFFFFFF)) : textColor,
                                     false, modelMatrix, immediate,
                                     disableDepth ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.POLYGON_OFFSET,
                                     0, 15728880
            );

            immediate.endBatch();
            textY += textRenderer.lineHeight;
        }

        allocator.close();
        global4fStack.popMatrix();
    }

    public static void renderBlockTargetingOverlay(Entity entity, BlockPos pos, Direction side, Vec3 hitVec,
                                                   Color4f color, Matrix4f posMatrix)
    {
        Direction playerFacing = entity.getDirection();
        PositionUtils.HitPart part = PositionUtils.getHitPart(side, playerFacing, pos, hitVec);
        Vec3 cameraPos = camPos();

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
        buffer.addVertex((float) (x - 0.5), (float) (y - 0.5), (float) z).setColor(c, c, c, quadAlpha);
        buffer.addVertex((float) (x + 0.5), (float) (y - 0.5), (float) z).setColor(c, c, c, quadAlpha);
        buffer.addVertex((float) (x + 0.5), (float) (y + 0.5), (float) z).setColor(c, c, c, quadAlpha);
        buffer.addVertex((float) (x - 0.5), (float) (y + 0.5), (float) z).setColor(c, c, c, quadAlpha);

        switch (part)
        {
            case CENTER:
                buffer.addVertex((float) (x - 0.25), (float) (y - 0.25), (float) z).setColor(hr, hg, hb, ha);
                buffer.addVertex((float) (x + 0.25), (float) (y - 0.25), (float) z).setColor(hr, hg, hb, ha);
                buffer.addVertex((float) (x + 0.25), (float) (y + 0.25), (float) z).setColor(hr, hg, hb, ha);
                buffer.addVertex((float) (x - 0.25), (float) (y + 0.25), (float) z).setColor(hr, hg, hb, ha);
                break;
            case LEFT:
                buffer.addVertex((float) (x - 0.50), (float) (y - 0.50), (float) z).setColor(hr, hg, hb, ha);
                buffer.addVertex((float) (x - 0.25), (float) (y - 0.25), (float) z).setColor(hr, hg, hb, ha);
                buffer.addVertex((float) (x - 0.25), (float) (y + 0.25), (float) z).setColor(hr, hg, hb, ha);
                buffer.addVertex((float) (x - 0.50), (float) (y + 0.50), (float) z).setColor(hr, hg, hb, ha);
                break;
            case RIGHT:
                buffer.addVertex((float) (x + 0.50), (float) (y - 0.50), (float) z).setColor(hr, hg, hb, ha);
                buffer.addVertex((float) (x + 0.25), (float) (y - 0.25), (float) z).setColor(hr, hg, hb, ha);
                buffer.addVertex((float) (x + 0.25), (float) (y + 0.25), (float) z).setColor(hr, hg, hb, ha);
                buffer.addVertex((float) (x + 0.50), (float) (y + 0.50), (float) z).setColor(hr, hg, hb, ha);
                break;
            case TOP:
                buffer.addVertex((float) (x - 0.50), (float) (y + 0.50), (float) z).setColor(hr, hg, hb, ha);
                buffer.addVertex((float) (x - 0.25), (float) (y + 0.25), (float) z).setColor(hr, hg, hb, ha);
                buffer.addVertex((float) (x + 0.25), (float) (y + 0.25), (float) z).setColor(hr, hg, hb, ha);
                buffer.addVertex((float) (x + 0.50), (float) (y + 0.50), (float) z).setColor(hr, hg, hb, ha);
                break;
            case BOTTOM:
                buffer.addVertex((float) (x - 0.50), (float) (y - 0.50), (float) z).setColor(hr, hg, hb, ha);
                buffer.addVertex((float) (x - 0.25), (float) (y - 0.25), (float) z).setColor(hr, hg, hb, ha);
                buffer.addVertex((float) (x + 0.25), (float) (y - 0.25), (float) z).setColor(hr, hg, hb, ha);
                buffer.addVertex((float) (x + 0.50), (float) (y - 0.50), (float) z).setColor(hr, hg, hb, ha);
                break;
            default:
        }

        try
        {
            MeshData meshData = buffer.build();

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
		float lineWidth = 1.6f;

        // Target "Center" -->
        // MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_NO_DEPTH_NO_CULL
        buffer = ctx.start(() -> "malilib:renderBlockTargetingOverlay/center", MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_NO_DEPTH_NO_CULL);

        // Middle small rectangle
        buffer.addVertex((float) (x - 0.25), (float) (y - 0.25), (float) z).setColor(c, c, c, c).setLineWidth(lineWidth);
        buffer.addVertex((float) (x + 0.25), (float) (y - 0.25), (float) z).setColor(c, c, c, c).setLineWidth(lineWidth);
        buffer.addVertex((float) (x + 0.25), (float) (y + 0.25), (float) z).setColor(c, c, c, c).setLineWidth(lineWidth);
        buffer.addVertex((float) (x - 0.25), (float) (y + 0.25), (float) z).setColor(c, c, c, c).setLineWidth(lineWidth);
        buffer.addVertex((float) (x - 0.25), (float) (y - 0.25), (float) z).setColor(c, c, c, c).setLineWidth(lineWidth);

        try
        {
            MeshData meshData = buffer.build();

            if (meshData != null)
            {
                ctx.color(wireColor);
//                ctx.lineWidth(1.6f);
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
        buffer.addVertex((float) (x - 0.50), (float) (y - 0.50), (float) z).setColor(c, c, c, c).setLineWidth(lineWidth);
        buffer.addVertex((float) (x - 0.25), (float) (y - 0.25), (float) z).setColor(c, c, c, c).setLineWidth(lineWidth);

        // Top left
        buffer.addVertex((float) (x - 0.50), (float) (y + 0.50), (float) z).setColor(c, c, c, c).setLineWidth(lineWidth);
        buffer.addVertex((float) (x - 0.25), (float) (y + 0.25), (float) z).setColor(c, c, c, c).setLineWidth(lineWidth);

        // Bottom right
        buffer.addVertex((float) (x + 0.50), (float) (y - 0.50), (float) z).setColor(c, c, c, c).setLineWidth(lineWidth);
        buffer.addVertex((float) (x + 0.25), (float) (y - 0.25), (float) z).setColor(c, c, c, c).setLineWidth(lineWidth);

        // Top right
        buffer.addVertex((float) (x + 0.50), (float) (y + 0.50), (float) z).setColor(c, c, c, c).setLineWidth(lineWidth);
        buffer.addVertex((float) (x + 0.25), (float) (y + 0.25), (float) z).setColor(c, c, c, c).setLineWidth(lineWidth);

        try
        {
            MeshData meshData = buffer.build();

            if (meshData != null)
            {
                ctx.color(wireColor);
//                ctx.lineWidth(1.6f);
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
        Direction playerFacing = entity.getDirection();
        Vec3 cameraPos = camPos();

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
        buffer.addVertex((float) (x - 0.5), (float) (y - 0.5), (float) z).setColor(r, g, b, a);
        buffer.addVertex((float) (x + 0.5), (float) (y - 0.5), (float) z).setColor(r, g, b, a);
        buffer.addVertex((float) (x + 0.5), (float) (y + 0.5), (float) z).setColor(r, g, b, a);
        buffer.addVertex((float) (x - 0.5), (float) (y + 0.5), (float) z).setColor(r, g, b, a);

        try
        {
            MeshData meshData = buffer.build();

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

		float lineWidth = 1.6f;

        // MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_NO_DEPTH_NO_CULL
        buffer = ctx.start(() -> "malilib:renderBlockTargetingOverlaySimple/lines", MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_NO_DEPTH_NO_CULL);

        // Middle rectangle
        buffer.addVertex((float) (x - 0.375), (float) (y - 0.375), (float) z).setColor(c, c, c, c).setLineWidth(lineWidth);
        buffer.addVertex((float) (x + 0.375), (float) (y - 0.375), (float) z).setColor(c, c, c, c).setLineWidth(lineWidth);
        buffer.addVertex((float) (x + 0.375), (float) (y + 0.375), (float) z).setColor(c, c, c, c).setLineWidth(lineWidth);
        buffer.addVertex((float) (x - 0.375), (float) (y + 0.375), (float) z).setColor(c, c, c, c).setLineWidth(lineWidth);

        try
        {
            MeshData meshData = buffer.build();

            if (meshData != null)
            {
//                ctx.lineWidth(1.6f);
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
                matrix4fStack.rotateY(matrix4fRotateFix(180f - playerFacing.toYRot()));
                matrix4fStack.rotateX(matrix4fRotateFix(90f));
                break;
            case UP:
                matrix4fStack.rotateY(matrix4fRotateFix(180f - playerFacing.toYRot()));
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

    public static void renderMapPreview(GuiContext ctx,
                                        ItemStack stack,
                                        int x, int y, int dimensions)
    {
        renderMapPreview(ctx, stack, x, y, dimensions, true);
    }

    public static void renderMapPreview(GuiContext ctx,
                                        ItemStack stack,
                                        int x, int y, int dimensions, boolean requireShift)
    {
        if (stack.getItem() instanceof MapItem && (!requireShift || GuiBase.isShiftDown()))
        {
            int y1 = y - dimensions - 20;
            int y2 = y1 + dimensions;
            int x1 = x + 8;
            int x2 = x1 + dimensions;
            int z = 300;
            int uv = 0xF000F0;

            MapItemSavedData mapState = MapItem.getSavedData(stack, mc().level);
            DataComponentMap data = stack.getComponents();
            MapId mapId = data.get(DataComponents.MAP_ID);

            Identifier bgTexture = mapState == null ? TEXTURE_MAP_BACKGROUND : TEXTURE_MAP_BACKGROUND_CHECKERBOARD;
            Pair<GpuTextureView, GpuSampler> pair = ctx.bindTexture(bgTexture);

            if (pair == null)
            {
                MaLiLib.LOGGER.error("renderMapPreview(): Failed to bind GpuTexture!");
                return;
            }

	        ctx.addSimpleElement(new MaLiLibLightTexturedGuiElement(
					RenderPipelines.GUI_TEXTURED,
					ctx.setupTexture(pair),
					new Matrix3x2f(ctx.pose()),
					x1, y1, x2, y2,
					0.0f, 1.0f, 0.0f, 1.0f,
					-1, uv,
					ctx.peekLastScissor())
            );

            if (mapId != null && mapState != null)
            {
                x1 += 8;
                y1 += 8;
//                z = 310;

                double scale = (double) (dimensions - 16) / 128.0D;

	            ctx.pose().pushMatrix();
	            ctx.pose().translate(x1, y1);
	            ctx.pose().scale((float) scale, (float) scale);

                MapRenderState mapRenderState = new MapRenderState();
                mc().getMapRenderer().extractRenderState(mapId, mapState, mapRenderState);
	            ctx.submitMapRenderState(mapRenderState);
	            ctx.pose().popMatrix();
            }
        }
    }

    public static void renderShulkerBoxPreview(GuiContext ctx,
                                               ItemStack stack,
                                               int baseX, int baseY, boolean useBgColors)
    {
        NonNullList<ItemStack> items;

        if (stack.getComponents().has(DataComponents.CONTAINER))
        {
            //items = InventoryUtils.getStoredItems(stack, ShulkerBoxBlockEntity.INVENTORY_SIZE);
            items = InventoryUtils.getStoredItems(stack, -1);

            if (items.isEmpty())
            {
                return;
            }

            CompoundTag nbt = InventoryUtils.getStoredBlockEntityNbt(stack);
            Set<Integer> lockedSlots = new HashSet<>();
            Container inv = InventoryUtils.getAsInventory(items);
            InventoryOverlayType type = InventoryOverlay.getInventoryType(stack);
            InventoryOverlay.InventoryProperties props = InventoryOverlay.getInventoryPropsTemp(type, items.size());

            int screenWidth = GuiUtils.getScaledWindowWidth();
            int screenHeight = GuiUtils.getScaledWindowHeight();
            int height = props.height + 18;
            int x = MathUtils.clamp(baseX + 8, 0, screenWidth - props.width);
            int y = MathUtils.clamp(baseY - height, 0, screenHeight - height);
            int color;

            if (stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() instanceof ShulkerBoxBlock)
            {
                color = setShulkerboxBackgroundTintColor((ShulkerBoxBlock) ((BlockItem) stack.getItem()).getBlock(), useBgColors);
            }
            else
            {
                color = CommonColors.WHITE;
            }

            Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
            matrix4fStack.pushMatrix();
            matrix4fStack.translate(0, 0, 500);

            InventoryOverlay.renderInventoryBackground(ctx, type, x, y, props.slotsPerRow, props.totalSlots, color);
            color = CommonColors.WHITE;

            if (type == InventoryOverlayType.BREWING_STAND)
            {
                InventoryOverlay.renderBrewerBackgroundSlots(ctx, inv, x, y);
            }

            if (type == InventoryOverlayType.CRAFTER && !nbt.isEmpty())
            {
                lockedSlots = NbtBlockUtils.getDisabledSlotsFromNbt(nbt);
                InventoryOverlay.renderInventoryStacks(ctx, type, inv, x + props.slotOffsetX, y + props.slotOffsetY, props.slotsPerRow, 0, -1, lockedSlots);
            }
            else
            {
                InventoryOverlay.renderInventoryStacks(ctx, type, inv, x + props.slotOffsetX, y + props.slotOffsetY, props.slotsPerRow, 0, -1);
            }

            matrix4fStack.popMatrix();
        }
    }

    public static void renderBundlePreview(GuiContext ctx,
                                           ItemStack stack,
                                           int baseX, int baseY, boolean useBgColors)
    {
        // Default is 9 to make the default display the same as Shulker Boxes
        renderBundlePreview(ctx, stack, baseX, baseY, 9, useBgColors);
    }

    public static void renderBundlePreview(GuiContext ctx,
                                           ItemStack stack,
                                           int baseX, int baseY, int slotsPerRow, boolean useBgColors)
    {
        NonNullList<ItemStack> items;

        if (stack.getComponents().has(DataComponents.BUNDLE_CONTENTS))
        {
            int count = InventoryUtils.bundleCountItems(stack);
            items = InventoryUtils.getBundleItems(stack, count);
            slotsPerRow = slotsPerRow != 9 ? MathUtils.clamp(slotsPerRow, 6, 9) : 9;

            if (items.isEmpty())
            {
                return;
            }

            Container inv = InventoryUtils.getAsInventory(items);
	        InventoryOverlayType type = InventoryOverlay.getInventoryType(stack);
            InventoryOverlay.InventoryProperties props = InventoryOverlay.getInventoryPropsTemp(type, count, slotsPerRow);

            int screenWidth = GuiUtils.getScaledWindowWidth();
            int screenHeight = GuiUtils.getScaledWindowHeight();
            int height = props.height + 18;
            int x = MathUtils.clamp(baseX + 8, 0, screenWidth - props.width);
            int y = MathUtils.clamp(baseY - height, 0, screenHeight - height);

            int color = setBundleBackgroundTintColor(stack, useBgColors);

            Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
            matrix4fStack.pushMatrix();
            matrix4fStack.translate(0, 0, 500);

            InventoryOverlay.renderInventoryBackground(ctx, type, x, y, props.slotsPerRow, props.totalSlots, color);
            InventoryOverlay.renderInventoryStacks(ctx, type, inv, x + props.slotOffsetX, y + props.slotOffsetY, props.slotsPerRow, 0, count);

            matrix4fStack.popMatrix();
        }
    }

	/**
	 * @deprecated Please consider using <b>renderDataItemsPreview()</b>
	 * <br>
	 * Render's the Inventory Overlay using an NbtCompound Items[] List format instead of the Item Container Component,
	 * Such as for a Crafter, etc.  This is meant to be simillar to the 1.20.4 behavior, minus the "BlockEntityTag";
	 * since it no longer exists; but this can be used as such, if the "BlockEntityTag" or its eqivalent, is read in first.
	 * -
	 *
	 * @param ctx
	 * @param stackIn     (Stack of the Entity for selecting the right textures)
	 * @param itemsTag    (Nbt Items[] list)
	 * @param baseX
	 * @param baseY
	 * @param useBgColors
	 */
	@Deprecated
	public static void renderNbtItemsPreview(GuiContext ctx,
	                                         ItemStack stackIn, @Nonnull CompoundTag itemsTag,
	                                         int baseX, int baseY, boolean useBgColors)
	{
		if (InventoryUtils.hasNbtItems(itemsTag))
		{
			if (mc().level == null)
			{
				return;
			}

			CompoundData data = DataConverterNbt.fromVanillaCompound(itemsTag);
			NonNullList<ItemStack> items = InventoryUtils.getDataItems(data, -1, mc().level.registryAccess());

			if (items.size() == 0)
			{
				return;
			}

			InventoryOverlayType type = InventoryOverlay.getInventoryType(stackIn);
			InventoryOverlay.InventoryProperties props = InventoryOverlay.getInventoryPropsTemp(type, items.size());

			int screenWidth = GuiUtils.getScaledWindowWidth();
			int screenHeight = GuiUtils.getScaledWindowHeight();
			int height = props.height + 18;
			int x = MathUtils.clamp(baseX + 8, 0, screenWidth - props.width);
			int y = MathUtils.clamp(baseY - height, 0, screenHeight - height);

			int color = CommonColors.WHITE;

			Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
			matrix4fStack.pushMatrix();
			matrix4fStack.translate(0, 0, 500);

			InventoryOverlay.renderInventoryBackground(ctx, type, x, y, props.slotsPerRow, items.size(), color);

			Container inv = InventoryUtils.getAsInventory(items);
			InventoryOverlay.renderInventoryStacks(ctx, type, inv, x + props.slotOffsetX, y + props.slotOffsetY, props.slotsPerRow, 0, -1);

			matrix4fStack.popMatrix();
		}
	}

	/**
	 * Render's the Inventory Overlay using an NbtCompound Items[] List format instead of the Item Container Component,
	 * Such as for a Crafter, etc.  This is meant to be simillar to the 1.20.4 behavior, minus the "BlockEntityTag";
	 * since it no longer exists; but this can be used as such, if the "BlockEntityTag" or its eqivalent, is read in first.
	 * -
	 *
	 * @param ctx
	 * @param stackIn     (Stack of the Entity for selecting the right textures)
	 * @param data        (Nbt Items[] list)
	 * @param baseX
	 * @param baseY
	 * @param useBgColors
	 */
	public static void renderDataItemsPreview(GuiContext ctx,
	                                          ItemStack stackIn, @Nonnull CompoundData data,
	                                          int baseX, int baseY, boolean useBgColors)
	{
		if (InventoryUtils.hasDataItems(data))
		{
			if (mc().level == null)
			{
				return;
			}

			NonNullList<ItemStack> items = InventoryUtils.getDataItems(data, -1, mc().level.registryAccess());

			if (items.size() == 0)
			{
				return;
			}

			InventoryOverlayType type = InventoryOverlay.getInventoryType(stackIn);
			InventoryOverlay.InventoryProperties props = InventoryOverlay.getInventoryPropsTemp(type, items.size());

			int screenWidth = GuiUtils.getScaledWindowWidth();
			int screenHeight = GuiUtils.getScaledWindowHeight();
			int height = props.height + 18;
			int x = MathUtils.clamp(baseX + 8, 0, screenWidth - props.width);
			int y = MathUtils.clamp(baseY - height, 0, screenHeight - height);

			int color = CommonColors.WHITE;

			Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
			matrix4fStack.pushMatrix();
			matrix4fStack.translate(0, 0, 500);

			InventoryOverlay.renderInventoryBackground(ctx, type, x, y, props.slotsPerRow, items.size(), color);

			Container inv = InventoryUtils.getAsInventory(items);
			InventoryOverlay.renderInventoryStacks(ctx, type, inv, x + props.slotOffsetX, y + props.slotOffsetY, props.slotsPerRow, 0, -1);

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
            final float[] colors = getColorComponents(block.getColor() != null ? block.getColor().getTextureDiffuseColor() : 0xFF875F87);
            return ARGB.colorFromFloat(1f, colors[0], colors[1], colors[2]);
        }
        else
        {
            return CommonColors.WHITE;
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
        if (bundle.is(ItemTags.BUNDLES) && useBgColors)
        {
            // In 1.17+ there is the uncolored Bundle variant, which returns null from getColor()
//            final DyeColor dye = getBundleColor(bundle);
//            final float[] colors = getColorComponents(dye != null ? dye.getEntityColor() : 0xFFA6572C);
//            return ColorHelper.fromFloats(1f, colors[0], colors[1], colors[2]);

			// Requires Alpha value
			return getBundleColor(bundle);
        }

        return CommonColors.WHITE;
    }

    // returns real colors now instead of Dye Colors.
    public static int getBundleColor(ItemStack bundle)
    {
        Item item = bundle.getItem();

        if (item == null)
        {
            return CommonColors.WHITE;
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
            return 0xFFA6572C;                          // #FFA6572C
        }
    }

    public static int setVillagerBackgroundTintColor(VillagerData data, boolean useBgColors)
    {
        if (useBgColors && data != null)
        {
            Holder<VillagerProfession> profession = data != null ? data.profession() : null;
	        Holder<VillagerType> type = data != null ? data.type() : null;

            return setVillagerBackgroundTintColor(profession, data.type(), data.level(), useBgColors);
        }

        return CommonColors.WHITE;
    }

    public static int setVillagerBackgroundTintColor(Holder<VillagerProfession> profession,
                                                     Holder<VillagerType> type,
                                                     int level, boolean useBgColors)
    {
        if (useBgColors)
        {
//            final DyeColor dye = getVillagerColor(profession);
			final int professionColor = getVillagerProfessionColor(profession);

//            if (dye != null)
//            {
//                final float[] colors = getColorComponents(dye.getTextureDiffuseColor());
//                return ARGB.colorFromFloat(1f, colors[0], colors[1], colors[2]);
//            }

			return professionColor;
        }

        return CommonColors.WHITE;
    }

	public static int getVillagerLevelColor(int level)
	{
		switch (level)
		{
			case 1 ->       // Stone
			{
				return 0xFFB3B1AF;
			}
			case 2 ->       // Iron
			{
				return 0xFFECC1A6;
			}
			case 3 ->       // Gold
			{
				return 0xFFFDFF76;
			}
			case 4 ->       // Emerald
			{
				return 0xFF41F384;
			}
			case 5 ->       // Diamond
			{
				return 0xFFA4FDF0;
			}
		}

		return -1;
	}

	public static int getVillagerTypeColor(Holder<VillagerType> type)
	{
		if (type == null) return -1;

		if (type.is(VillagerType.DESERT))
		{
			return 0xFFD75601;      // Orangeish color of robes
		}
		else if (type.is(VillagerType.JUNGLE))
		{
			return 0xFFEAC03F;      // Yellowish color of shirt
		}
		else if (type.is(VillagerType.PLAINS))
		{
			return 0xFF71544D;      // Brownish Color of overalls
		}
		else if (type.is(VillagerType.SAVANNA))
		{
			return 0xFFAA2A2A;      // Reddish Color of top
		}
		else if (type.is(VillagerType.SNOW))
		{
			return 0xFF5E8F83;      // Cyanish Color of coat
		}
		else if (type.is(VillagerType.SWAMP))
		{
			return 0xFF412D56;      // Purpleish color of shirt
		}
		else if (type.is(VillagerType.TAIGA))
		{
			return 0xFFE3E0C2;      // Off-White color of shirt
		}

		return -1;
	}

	public static int getVillagerProfessionColor(Holder<VillagerProfession> profession)
	{
		if (profession == null) return -1;

		if (profession.is(VillagerProfession.NONE))
		{
//			return 0xFFBE886C;          // Skin-like color
			return 0xFF5F44B6;          // Skin-like + Blue
		}
		else if (profession.is(VillagerProfession.ARMORER))
		{
			return 0xFF858078;          // A Gray face mask color
//			return 0xFF615E58;          // A Gray + hint of Charcoal
		}
		else if (profession.is(VillagerProfession.BUTCHER))
		{
//			return 0xFFAE574F;          // Reddish/Orange Headband color
			return 0xFFCE6D9F;          // Reddish/Orange + Pink
		}
		else if (profession.is(VillagerProfession.CARTOGRAPHER))
		{
			return 0xFF97CAF6;          // Light Blue Monicle glass color
		}
		else if (profession.is(VillagerProfession.CLERIC))
		{
//			return 0xFF793C5B;          // Purpleish robes color
			return 0xFF6A2868;          // Purpleish + a hint Purple
		}
		else if (profession.is(VillagerProfession.FARMER))
		{
			return 0xFFDBC549;          // Yellowish hat color
		}
		else if (profession.is(VillagerProfession.FISHERMAN))
		{
			return 0xFF6B9F93;          // Cyanish "Fish" color
		}
		else if (profession.is(VillagerProfession.FLETCHER))
		{
			return 0xFF9A5030;          // Orangish belt color
		}
		else if (profession.is(VillagerProfession.LEATHERWORKER))
		{
			return 0xFF855636;          // Brownish apron color
		}
		else if (profession.is(VillagerProfession.LIBRARIAN))
		{
			return 0xFF9A2323;          // Red hat color
		}
		else if (profession.is(VillagerProfession.MASON))
		{
			return 0xFF363230;          // Dark Gray apron color
//			return 0xFF5B1958;          // Dark Gray + Magenta
		}
		else if (profession.is(VillagerProfession.NITWIT))
		{
			return 0xFF5D744F;          // Greenish shirt color
		}
		else if (profession.is(VillagerProfession.SHEPHERD))
		{
			return 0xFFF4F4E1;          // Off-White vest color
		}
		else if (profession.is(VillagerProfession.TOOLSMITH))
		{
			return 0xFF615026;          // Wood-Brownish Hammer handle color
//			return 0xFF82765C;          // Wood-Brown + Light Gray
		}
		else if (profession.is(VillagerProfession.WEAPONSMITH))
		{
			return 0xFF191919;          // Charcoalish hat color
//			return 0xFF232121;          // Charcoal + hint of Mason Dark Gray
		}

//		return 0xFF32CD32;              // Lime
		return 0xFF4EB349;              // Lime + hint of gray
	}

	// todo - return real colors based on the Villager, not Dye Colors
	@Deprecated(forRemoval = true)
	public static DyeColor getVillagerColor(Holder<VillagerProfession> profession)
	{
		if (profession == null)
		{
			return null;
		}

		if (profession.equals(VillagerProfession.NONE))
		{
			return DyeColor.BLUE;           // 0xFFBE886C (Skin-Like Color)
		}
		else if (profession.is(VillagerProfession.ARMORER))
		{
			return DyeColor.GRAY;           // 0xFF5C5A57
		}
		else if (profession.is(VillagerProfession.BUTCHER))
		{
			return DyeColor.PINK;           // 0xFFAE574F
		}
		else if (profession.is(VillagerProfession.CARTOGRAPHER))
		{
			return DyeColor.LIGHT_BLUE;     // 0xFF97CAF6
		}
		else if (profession.is(VillagerProfession.CLERIC))
		{
			return DyeColor.PURPLE;         // 0xFF864E6A
		}
		else if (profession.is(VillagerProfession.FARMER))
		{
			return DyeColor.YELLOW;         // 0xFFDBC549
		}
		else if (profession.is(VillagerProfession.FISHERMAN))
		{
			return DyeColor.CYAN;           // 0xFF6B9F93
		}
		else if (profession.is(VillagerProfession.FLETCHER))
		{
			return DyeColor.ORANGE;         // 0xFFC26A44
		}
		else if (profession.is(VillagerProfession.LEATHERWORKER))
		{
			return DyeColor.BROWN;          // 0xFF855636
		}
		else if (profession.is(VillagerProfession.LIBRARIAN))
		{
			return DyeColor.RED;            // 0xFF9A2323
		}
		else if (profession.is(VillagerProfession.MASON))
		{
			return DyeColor.MAGENTA;        // 0xFF989696
		}
		else if (profession.is(VillagerProfession.NITWIT))
		{
			return DyeColor.GREEN;          // 0xFF5D744F
		}
		else if (profession.is(VillagerProfession.SHEPHERD))
		{
			return DyeColor.WHITE;          // 0xFFE5E0CB
		}
		else if (profession.is(VillagerProfession.TOOLSMITH))
		{
			return DyeColor.LIGHT_GRAY;     // 0xFFA29C91
		}
		else if (profession.is(VillagerProfession.WEAPONSMITH))
		{
			return DyeColor.BLACK;          // 0xFF191919
		}
		else
		{
			// Unhandled Profession
			return DyeColor.LIME;
		}
	}

    public static boolean stateModelHasQuads(BlockState state)
    {
        return modelHasQuads(Objects.requireNonNull(Minecraft.getInstance().getBlockRenderer().getBlockModel(state)));
    }

    public static boolean modelHasQuads(@Nonnull BlockStateModel model)
    {
        return hasQuads(model.collectParts(new SingleThreadedRandomSource(0)));
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

    public static void renderModelInGui(GuiContext ctx, int x, int y, BlockState state)
    {
        renderModelInGui(ctx, x, y, 16, state, 0.75F, 0.50F);
		// scale: 0.625f ?
    }

	public static void renderModelInGui(GuiContext ctx, int x, int y, BlockState state, float scale)
	{
		renderModelInGui(ctx, x, y, 16, state, scale, 0.0F);
		// scale: 0.625f ?
	}

	public static void renderModelInGui(GuiContext ctx, int x, int y, int size, BlockState state, float scale, float yOffset)
    {
        if (state.getBlock() == Blocks.AIR)
        {
            return;
        }

	    ctx.addSpecialElement(
				new MaLiLibBlockStateGuiElement(
						state,
//						new Vector3f((float) (x + 8.0), (float) (y + 8.0), (float) (z + 100.0)),
						new Quaternionf().rotationXYZ(30 * (float) (Math.PI / 180.0), 225 * (float) (Math.PI / 180.0), 0.0F),
						x, y,
						size,
						scale,
						yOffset,
						ctx.peekLastScissor()
				)
        );
    }

    public static Minecraft mc()
    {
        return Minecraft.getInstance();
    }

    public static RenderTarget fb()
    {
        return mc().getMainRenderTarget();
    }

    public static Vec3 camPos()
    {
        return mc().gameRenderer.getMainCamera().position();
    }

    public static TextureManager tex()
    {
        return mc().getTextureManager();
    }

    public static LightTexture lightmap()
    {
        return mc().gameRenderer.lightTexture();
    }

	public static Font textRenderer()
	{
		return mc().font;
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

        drawBlockBoundingBoxOutlinesBatchedLinesSimple(pos, color, expand, lineWidth, buffer);

        try
        {
            MeshData meshData = buffer.build();

            if (meshData != null)
            {
//                ctx.lineWidth(lineWidth);
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
                                                                      double expand,
																	  float lineWidth,
                                                                      BufferBuilder buffer)
    {
        Vec3 cameraPos = camPos();
        final double dx = cameraPos.x;
        final double dy = cameraPos.y;
        final double dz = cameraPos.z;

        float minX = (float) (pos.getX() - dx - expand);
        float minY = (float) (pos.getY() - dy - expand);
        float minZ = (float) (pos.getZ() - dz - expand);
        float maxX = (float) (pos.getX() - dx + expand + 1);
        float maxY = (float) (pos.getY() - dy + expand + 1);
        float maxZ = (float) (pos.getZ() - dz + expand + 1);

        drawBoxAllEdgesBatchedLines(minX, minY, minZ, maxX, maxY, maxZ, color, lineWidth, buffer);
    }

    public static void drawConnectingLineBatchedLines(BlockPos pos1, BlockPos pos2, boolean center,
                                                      Color4f color,
													  float lineWidth,
                                                      BufferBuilder buffer)
    {
        Vec3 cameraPos = camPos();
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

        buffer.addVertex(x1, y1, z1).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);
        buffer.addVertex(x2, y2, z2).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);
    }

    public static void renderBlockOutlineOverlapping(BlockPos pos, float expand, float lineWidth,
                                                     Color4f color1, Color4f color2, Color4f color3,
                                                     Matrix4f matrix4f)
    {
        renderBlockOutlineOverlapping(pos, expand, lineWidth, color1, color2, color3, matrix4f, false);
    }

    public static void renderBlockOutlineOverlapping(BlockPos pos, float expand, float lineWidth,
                                                     Color4f color1, Color4f color2, Color4f color3, Matrix4f matrix4f,
                                                     boolean renderThrough)
    {
        Vec3 cameraPos = camPos();
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
        buffer.addVertex(minX, minY, minZ).setColor(color1.r, color1.g, color1.b, color1.a).setLineWidth(lineWidth);
        buffer.addVertex(maxX, minY, minZ).setColor(color1.r, color1.g, color1.b, color1.a).setLineWidth(lineWidth);

        buffer.addVertex(minX, minY, minZ).setColor(color1.r, color1.g, color1.b, color1.a).setLineWidth(lineWidth);
        buffer.addVertex(minX, maxY, minZ).setColor(color1.r, color1.g, color1.b, color1.a).setLineWidth(lineWidth);

        buffer.addVertex(minX, minY, minZ).setColor(color1.r, color1.g, color1.b, color1.a).setLineWidth(lineWidth);
        buffer.addVertex(minX, minY, maxZ).setColor(color1.r, color1.g, color1.b, color1.a).setLineWidth(lineWidth);

        // Max corner
        buffer.addVertex(minX, maxY, maxZ).setColor(color2.r, color2.g, color2.b, color2.a).setLineWidth(lineWidth);
        buffer.addVertex(maxX, maxY, maxZ).setColor(color2.r, color2.g, color2.b, color2.a).setLineWidth(lineWidth);

        buffer.addVertex(maxX, minY, maxZ).setColor(color2.r, color2.g, color2.b, color2.a).setLineWidth(lineWidth);
        buffer.addVertex(maxX, maxY, maxZ).setColor(color2.r, color2.g, color2.b, color2.a).setLineWidth(lineWidth);

        buffer.addVertex(maxX, maxY, minZ).setColor(color2.r, color2.g, color2.b, color2.a).setLineWidth(lineWidth);
        buffer.addVertex(maxX, maxY, maxZ).setColor(color2.r, color2.g, color2.b, color2.a).setLineWidth(lineWidth);

        // The rest of the edges
        buffer.addVertex(minX, maxY, minZ).setColor(color3.r, color3.g, color3.b, color3.a).setLineWidth(lineWidth);
        buffer.addVertex(maxX, maxY, minZ).setColor(color3.r, color3.g, color3.b, color3.a).setLineWidth(lineWidth);

        buffer.addVertex(minX, minY, maxZ).setColor(color3.r, color3.g, color3.b, color3.a).setLineWidth(lineWidth);
        buffer.addVertex(maxX, minY, maxZ).setColor(color3.r, color3.g, color3.b, color3.a).setLineWidth(lineWidth);

        buffer.addVertex(maxX, minY, minZ).setColor(color3.r, color3.g, color3.b, color3.a).setLineWidth(lineWidth);
        buffer.addVertex(maxX, maxY, minZ).setColor(color3.r, color3.g, color3.b, color3.a).setLineWidth(lineWidth);

        buffer.addVertex(minX, minY, maxZ).setColor(color3.r, color3.g, color3.b, color3.a).setLineWidth(lineWidth);
        buffer.addVertex(minX, maxY, maxZ).setColor(color3.r, color3.g, color3.b, color3.a).setLineWidth(lineWidth);

        buffer.addVertex(maxX, minY, minZ).setColor(color3.r, color3.g, color3.b, color3.a).setLineWidth(lineWidth);
        buffer.addVertex(maxX, minY, maxZ).setColor(color3.r, color3.g, color3.b, color3.a).setLineWidth(lineWidth);

        buffer.addVertex(minX, maxY, minZ).setColor(color3.r, color3.g, color3.b, color3.a).setLineWidth(lineWidth);
        buffer.addVertex(minX, maxY, maxZ).setColor(color3.r, color3.g, color3.b, color3.a).setLineWidth(lineWidth);

        try
        {
            MeshData meshData = buffer.build();

            if (meshData != null)
            {
//                ctx.lineWidth(lineWidth);
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
        Vec3 cameraPos = camPos();
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

        drawBoundingBoxLinesX(buffer, minX, minY, minZ, maxX, maxY, maxZ, colorX, lineWidth);
        drawBoundingBoxLinesY(buffer, minX, minY, minZ, maxX, maxY, maxZ, colorY, lineWidth);
        drawBoundingBoxLinesZ(buffer, minX, minY, minZ, maxX, maxY, maxZ, colorZ, lineWidth);

        try
        {
            MeshData meshData = buffer.build();

            if (meshData != null)
            {
//                ctx.lineWidth(lineWidth);
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
                                              Color4f color, float lineWidth)
    {
        buffer.addVertex(minX, minY, minZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);
        buffer.addVertex(maxX, minY, minZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);

        buffer.addVertex(minX, maxY, minZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);
        buffer.addVertex(maxX, maxY, minZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);

        buffer.addVertex(minX, minY, maxZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);
        buffer.addVertex(maxX, minY, maxZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);

        buffer.addVertex(minX, maxY, maxZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);
        buffer.addVertex(maxX, maxY, maxZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);
    }

    private static void drawBoundingBoxLinesY(BufferBuilder buffer, float minX, float minY, float minZ, float maxX, float maxY, float maxZ,
                                              Color4f color, float lineWidth)
    {
        buffer.addVertex(minX, minY, minZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);
        buffer.addVertex(minX, maxY, minZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);

        buffer.addVertex(maxX, minY, minZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);
        buffer.addVertex(maxX, maxY, minZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);

        buffer.addVertex(minX, minY, maxZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);
        buffer.addVertex(minX, maxY, maxZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);

        buffer.addVertex(maxX, minY, maxZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);
        buffer.addVertex(maxX, maxY, maxZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);
    }

    private static void drawBoundingBoxLinesZ(BufferBuilder buffer, float minX, float minY, float minZ, float maxX, float maxY, float maxZ,
                                              Color4f color, float lineWidth)
    {
        buffer.addVertex(minX, minY, minZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);
        buffer.addVertex(minX, minY, maxZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);

        buffer.addVertex(maxX, minY, minZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);
        buffer.addVertex(maxX, minY, maxZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);

        buffer.addVertex(minX, maxY, minZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);
        buffer.addVertex(minX, maxY, maxZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);

        buffer.addVertex(maxX, maxY, minZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);
        buffer.addVertex(maxX, maxY, maxZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);
    }

    public static void renderAreaSides(BlockPos pos1, BlockPos pos2, Color4f color, Matrix4f matrix4f)
    {
        renderAreaSides(pos1, pos2, color, matrix4f, false);
    }

    public static void renderAreaSides(BlockPos pos1, BlockPos pos2, Color4f color, Matrix4f matrix4f, boolean shouldResort)
    {
	    boolean insideOf = isCameraInsideOf(pos1, pos2);
        // MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_LEQUAL_DEPTH_NO_CULL
        RenderContext ctx = new RenderContext(() -> "malilib:renderAreaSides", insideOf ? MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_3 : MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_LEQUAL_DEPTH);
        BufferBuilder buffer = ctx.getBuilder();

        renderAreaSidesBatched(pos1, pos2, color, 0.002, buffer);

        try
        {
            MeshData meshData = buffer.build();

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
        Vec3 cameraPos = camPos();
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
        Vec3 cameraPos = camPos();
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
            buffer.addVertex(start + dxMin, minY, minZ).setColor(colorX.r, colorX.g, colorX.b, colorX.a).setLineWidth(lineWidth);
            buffer.addVertex(end + dxMax, minY, minZ).setColor(colorX.r, colorX.g, colorX.b, colorX.a).setLineWidth(lineWidth);
        }

        start = (pos1.getX() == xMin && pos1.getY() == yMax && pos1.getZ() == zMin) || (pos2.getX() == xMin && pos2.getY() == yMax && pos2.getZ() == zMin) ? xMin + 1 : xMin;
        end = (pos1.getX() == xMax && pos1.getY() == yMax && pos1.getZ() == zMin) || (pos2.getX() == xMax && pos2.getY() == yMax && pos2.getZ() == zMin) ? xMax : xMax + 1;

        if (end > start)
        {
            buffer.addVertex(start + dxMin, maxY + 1, minZ).setColor(colorX.r, colorX.g, colorX.b, colorX.a).setLineWidth(lineWidth);
            buffer.addVertex(end + dxMax, maxY + 1, minZ).setColor(colorX.r, colorX.g, colorX.b, colorX.a).setLineWidth(lineWidth);
        }

        start = (pos1.getX() == xMin && pos1.getY() == yMin && pos1.getZ() == zMax) || (pos2.getX() == xMin && pos2.getY() == yMin && pos2.getZ() == zMax) ? xMin + 1 : xMin;
        end = (pos1.getX() == xMax && pos1.getY() == yMin && pos1.getZ() == zMax) || (pos2.getX() == xMax && pos2.getY() == yMin && pos2.getZ() == zMax) ? xMax : xMax + 1;

        if (end > start)
        {
            buffer.addVertex(start + dxMin, minY, maxZ + 1).setColor(colorX.r, colorX.g, colorX.b, colorX.a).setLineWidth(lineWidth);
            buffer.addVertex(end + dxMax, minY, maxZ + 1).setColor(colorX.r, colorX.g, colorX.b, colorX.a).setLineWidth(lineWidth);
        }

        start = (pos1.getX() == xMin && pos1.getY() == yMax && pos1.getZ() == zMax) || (pos2.getX() == xMin && pos2.getY() == yMax && pos2.getZ() == zMax) ? xMin + 1 : xMin;
        end = (pos1.getX() == xMax && pos1.getY() == yMax && pos1.getZ() == zMax) || (pos2.getX() == xMax && pos2.getY() == yMax && pos2.getZ() == zMax) ? xMax : xMax + 1;

        if (end > start)
        {
            buffer.addVertex(start + dxMin, maxY + 1, maxZ + 1).setColor(colorX.r, colorX.g, colorX.b, colorX.a).setLineWidth(lineWidth);
            buffer.addVertex(end + dxMax, maxY + 1, maxZ + 1).setColor(colorX.r, colorX.g, colorX.b, colorX.a).setLineWidth(lineWidth);
        }

        // Edges along the Y-axis
        start = (pos1.getX() == xMin && pos1.getY() == yMin && pos1.getZ() == zMin) || (pos2.getX() == xMin && pos2.getY() == yMin && pos2.getZ() == zMin) ? yMin + 1 : yMin;
        end = (pos1.getX() == xMin && pos1.getY() == yMax && pos1.getZ() == zMin) || (pos2.getX() == xMin && pos2.getY() == yMax && pos2.getZ() == zMin) ? yMax : yMax + 1;

        if (end > start)
        {
            buffer.addVertex(minX, start + dyMin, minZ).setColor(colorY.r, colorY.g, colorY.b, colorY.a).setLineWidth(lineWidth);
            buffer.addVertex(minX, end + dyMax, minZ).setColor(colorY.r, colorY.g, colorY.b, colorY.a).setLineWidth(lineWidth);
        }

        start = (pos1.getX() == xMax && pos1.getY() == yMin && pos1.getZ() == zMin) || (pos2.getX() == xMax && pos2.getY() == yMin && pos2.getZ() == zMin) ? yMin + 1 : yMin;
        end = (pos1.getX() == xMax && pos1.getY() == yMax && pos1.getZ() == zMin) || (pos2.getX() == xMax && pos2.getY() == yMax && pos2.getZ() == zMin) ? yMax : yMax + 1;

        if (end > start)
        {
            buffer.addVertex(maxX + 1, start + dyMin, minZ).setColor(colorY.r, colorY.g, colorY.b, colorY.a).setLineWidth(lineWidth);
            buffer.addVertex(maxX + 1, end + dyMax, minZ).setColor(colorY.r, colorY.g, colorY.b, colorY.a).setLineWidth(lineWidth);
        }

        start = (pos1.getX() == xMin && pos1.getY() == yMin && pos1.getZ() == zMax) || (pos2.getX() == xMin && pos2.getY() == yMin && pos2.getZ() == zMax) ? yMin + 1 : yMin;
        end = (pos1.getX() == xMin && pos1.getY() == yMax && pos1.getZ() == zMax) || (pos2.getX() == xMin && pos2.getY() == yMax && pos2.getZ() == zMax) ? yMax : yMax + 1;

        if (end > start)
        {
            buffer.addVertex(minX, start + dyMin, maxZ + 1).setColor(colorY.r, colorY.g, colorY.b, colorY.a).setLineWidth(lineWidth);
            buffer.addVertex(minX, end + dyMax, maxZ + 1).setColor(colorY.r, colorY.g, colorY.b, colorY.a).setLineWidth(lineWidth);
        }

        start = (pos1.getX() == xMax && pos1.getY() == yMin && pos1.getZ() == zMax) || (pos2.getX() == xMax && pos2.getY() == yMin && pos2.getZ() == zMax) ? yMin + 1 : yMin;
        end = (pos1.getX() == xMax && pos1.getY() == yMax && pos1.getZ() == zMax) || (pos2.getX() == xMax && pos2.getY() == yMax && pos2.getZ() == zMax) ? yMax : yMax + 1;

        if (end > start)
        {
            buffer.addVertex(maxX + 1, start + dyMin, maxZ + 1).setColor(colorY.r, colorY.g, colorY.b, colorY.a).setLineWidth(lineWidth);
            buffer.addVertex(maxX + 1, end + dyMax, maxZ + 1).setColor(colorY.r, colorY.g, colorY.b, colorY.a).setLineWidth(lineWidth);
        }

        // Edges along the Z-axis
        start = (pos1.getX() == xMin && pos1.getY() == yMin && pos1.getZ() == zMin) || (pos2.getX() == xMin && pos2.getY() == yMin && pos2.getZ() == zMin) ? zMin + 1 : zMin;
        end = (pos1.getX() == xMin && pos1.getY() == yMin && pos1.getZ() == zMax) || (pos2.getX() == xMin && pos2.getY() == yMin && pos2.getZ() == zMax) ? zMax : zMax + 1;

        if (end > start)
        {
            buffer.addVertex(minX, minY, start + dzMin).setColor(colorZ.r, colorZ.g, colorZ.b, colorZ.a).setLineWidth(lineWidth);
            buffer.addVertex(minX, minY, end + dzMax).setColor(colorZ.r, colorZ.g, colorZ.b, colorZ.a).setLineWidth(lineWidth);
        }

        start = (pos1.getX() == xMax && pos1.getY() == yMin && pos1.getZ() == zMin) || (pos2.getX() == xMax && pos2.getY() == yMin && pos2.getZ() == zMin) ? zMin + 1 : zMin;
        end = (pos1.getX() == xMax && pos1.getY() == yMin && pos1.getZ() == zMax) || (pos2.getX() == xMax && pos2.getY() == yMin && pos2.getZ() == zMax) ? zMax : zMax + 1;

        if (end > start)
        {
            buffer.addVertex(maxX + 1, minY, start + dzMin).setColor(colorZ.r, colorZ.g, colorZ.b, colorZ.a).setLineWidth(lineWidth);
            buffer.addVertex(maxX + 1, minY, end + dzMax).setColor(colorZ.r, colorZ.g, colorZ.b, colorZ.a).setLineWidth(lineWidth);
        }

        start = (pos1.getX() == xMin && pos1.getY() == yMax && pos1.getZ() == zMin) || (pos2.getX() == xMin && pos2.getY() == yMax && pos2.getZ() == zMin) ? zMin + 1 : zMin;
        end = (pos1.getX() == xMin && pos1.getY() == yMax && pos1.getZ() == zMax) || (pos2.getX() == xMin && pos2.getY() == yMax && pos2.getZ() == zMax) ? zMax : zMax + 1;

        if (end > start)
        {
            buffer.addVertex(minX, maxY + 1, start + dzMin).setColor(colorZ.r, colorZ.g, colorZ.b, colorZ.a).setLineWidth(lineWidth);
            buffer.addVertex(minX, maxY + 1, end + dzMax).setColor(colorZ.r, colorZ.g, colorZ.b, colorZ.a).setLineWidth(lineWidth);
        }

        start = (pos1.getX() == xMax && pos1.getY() == yMax && pos1.getZ() == zMin) || (pos2.getX() == xMax && pos2.getY() == yMax && pos2.getZ() == zMin) ? zMin + 1 : zMin;
        end = (pos1.getX() == xMax && pos1.getY() == yMax && pos1.getZ() == zMax) || (pos2.getX() == xMax && pos2.getY() == yMax && pos2.getZ() == zMax) ? zMax : zMax + 1;

        if (end > start)
        {
            buffer.addVertex(maxX + 1, maxY + 1, start + dzMin).setColor(colorZ.r, colorZ.g, colorZ.b, colorZ.a).setLineWidth(lineWidth);
            buffer.addVertex(maxX + 1, maxY + 1, end + dzMax).setColor(colorZ.r, colorZ.g, colorZ.b, colorZ.a).setLineWidth(lineWidth);
        }

        try
        {
            MeshData meshData = buffer.build();

            if (meshData != null)
            {
//                ctx.lineWidth(lineWidth);
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

	/**
	 * I really don't like this.
	 *
	 * @param pos1
	 * @param pos2
	 * @return
	 */
	public static boolean isCameraInsideOf(BlockPos pos1, BlockPos pos2)
	{
		// Fix Bottom Y Border offset
		if (pos1.getY() < pos2.getY())
		{
			pos1 = pos1.mutable().setY(pos1.getY() - 1).immutable();
		}
		else if (pos2.getY() < pos1.getY())
		{
			pos2 = pos2.mutable().setY(pos2.getY() - 1).immutable();
		}

		return isCameraInsideOf(AABB.encapsulatingFullBlocks(pos1, pos2));
	}

	public static boolean isCameraInsideOf(Vec3 pos1, Vec3 pos2)
	{
		return isCameraInsideOf(new AABB(pos1, pos2));
	}

	/**
	 * The POSITION_COLOR Bottom Side causes Z fighting at certain distances;
	 * If and only if the Side collides with a Block surface.
	 * Calculate `withCull` if and only if the bottom side
	 * is Air that meets with Non-Air.  We are only considering the Center Block Pos here.
	 * NOTE that this causes a noticable "shift" in how the selection box appears when
	 * Enabling culling; so we should only do so in this case; and only to stop "Z Fighting" .
	 *
	 * @param bb
	 * @return
	 */
	public static boolean isCameraInsideOf(AABB bb)
	{
		Entity camera = mc().getCameraEntity();
		Vec3 pos = camera.position();

		// Mark culling if the camera is outside of the bounding box (Walls overlapping, etc)
		return bb.contains(pos);
	}
}
