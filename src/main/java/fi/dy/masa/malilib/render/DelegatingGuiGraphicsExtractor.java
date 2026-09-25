package fi.dy.masa.malilib.render;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.cursor.CursorType;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.textures.GpuSampler;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.object.banner.BannerFlagModel;
import net.minecraft.client.model.object.book.BookModel;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.profiling.ResultField;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import org.joml.Matrix3x2fStack;
import org.joml.Quaternionfc;
import org.joml.Vector3fc;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class DelegatingGuiGraphicsExtractor extends GuiGraphicsExtractor
{
    protected final GuiGraphicsExtractor guiGraphics;

    public DelegatingGuiGraphicsExtractor(GuiGraphicsExtractor context)
    {
        this.guiGraphics = context;
        super(context.minecraft, context.pose, context.guiRenderState, context.mouseX, context.mouseY);
    }

    @Override
    public void requestCursor(@NonNull CursorType cursorType)
    {
        this.guiGraphics.requestCursor(cursorType);
    }

    @Override
    public void applyCursor(@NonNull Window window)
    {
        this.guiGraphics.applyCursor(window);
    }

    @Override
    public int guiWidth()
    {
        return this.guiGraphics.guiWidth();
    }

    @Override
    public int guiHeight()
    {
        return this.guiGraphics.guiHeight();
    }

    @Override
    public @NonNull Matrix3x2fStack pose()
    {
        return this.guiGraphics.pose();
    }

    @Override
    public void nextStratum()
    {
        this.guiGraphics.nextStratum();
    }

    @Override
    public void blurBeforeThisStratum()
    {
        this.guiGraphics.blurBeforeThisStratum();
    }

    @Override
    public void enableScissor(int x0, int y0, int x1, int y1)
    {
        this.guiGraphics.enableScissor(x0, y0, x1, y1);
    }

    @Override
    public void disableScissor()
    {
        this.guiGraphics.disableScissor();
    }

    @Override
    public boolean containsPointInScissor(int x, int y)
    {
        return this.guiGraphics.containsPointInScissor(x, y);
    }

    @Override
    public void horizontalLine(int x0, int x1, int y, int col)
    {
        this.guiGraphics.horizontalLine(x0, x1, y, col);
    }

    @Override
    public void verticalLine(int x, int y0, int y1, int col)
    {
        this.guiGraphics.verticalLine(x, y0, y1, col);
    }

    @Override
    public void fill(int x0, int y0, int x1, int y1, int col)
    {
        this.guiGraphics.fill(x0, y0, x1, y1, col);
    }

    @Override
    public void fill(@NonNull RenderPipeline pipeline, int x0, int y0, int x1, int y1, int col)
    {
        this.guiGraphics.fill(pipeline, x0, y0, x1, y1, col);
    }

    @Override
    public void fillGradient(int x0, int y0, int x1, int y1, int col1, int col2)
    {
        this.guiGraphics.fillGradient(x0, y0, x1, y1, col1, col2);
    }

    @Override
    public void fill(@NonNull RenderPipeline renderPipeline, @NonNull TextureSetup textureSetup, int x0, int y0, int x1, int y1)
    {
        this.guiGraphics.fill(renderPipeline, textureSetup, x0, y0, x1, y1);
    }

    @Override
    public void outline(int x, int y, int width, int height, int color)
    {
        this.guiGraphics.outline(x, y, width, height, color);
    }

    @Override
    public void textHighlight(int x0, int y0, int x1, int y1, boolean invertText)
    {
        this.guiGraphics.textHighlight(x0, y0, x1, y1, invertText);
    }

    @Override
    public void text(@NonNull Font font, @org.jspecify.annotations.Nullable String str, int x, int y, int color)
    {
        this.guiGraphics.text(font, str, x, y, color);
    }

    @Override
    public void text(@NonNull Font font, @org.jspecify.annotations.Nullable String str, int x, int y, int color, boolean dropShadow)
    {
        this.guiGraphics.text(font, str, x, y, color, dropShadow);
    }

    @Override
    public void text(@NonNull Font font, @NonNull FormattedCharSequence str, int x, int y, int color)
    {
        this.guiGraphics.text(font, str, x, y, color);
    }

    @Override
    public void text(@NonNull Font font, @NonNull FormattedCharSequence str, int x, int y, int color, boolean dropShadow)
    {
        this.guiGraphics.text(font, str, x, y, color, dropShadow);
    }

    @Override
    public void text(@NonNull Font font, @NonNull Component str, int x, int y, int color)
    {
        this.guiGraphics.text(font, str, x, y, color);
    }

    @Override
    public void text(@NonNull Font font, @NonNull Component str, int x, int y, int color, boolean dropShadow)
    {
        this.guiGraphics.text(font, str, x, y, color, dropShadow);
    }

    @Override
    public void centeredText(@NonNull Font font, @NonNull String str, int x, int y, int color)
    {
        this.guiGraphics.centeredText(font, str, x, y, color);
    }

    @Override
    public void centeredText(@NonNull Font font, @NonNull Component text, int x, int y, int color)
    {
        this.guiGraphics.centeredText(font, text, x, y, color);
    }

    @Override
    public void centeredText(@NonNull Font font, @NonNull FormattedCharSequence text, int x, int y, int color)
    {
        this.guiGraphics.centeredText(font, text, x, y, color);
    }

    @Override
    public int textWithWordWrap(@NonNull Font font, @NonNull FormattedText string, int x, int y, int width, int col)
    {
        return this.guiGraphics.textWithWordWrap(font, string, x, y, width, col);
    }

    @Override
    public int textWithWordWrap(@NonNull Font font, @NonNull FormattedText string, int x, int y, int width, int col, boolean dropShadow)
    {
        return this.guiGraphics.textWithWordWrap(font, string, x, y, width, col, dropShadow);
    }

    @Override
    public void textWithBackdrop(@NonNull Font font, @NonNull Component str, int textX, int textY, int textWidth, int textColor)
    {
        this.guiGraphics.textWithBackdrop(font, str, textX, textY, textWidth, textColor);
    }

    @Override
    public void blit(@NonNull RenderPipeline renderPipeline, @NonNull Identifier texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight, int color)
    {
        this.guiGraphics.blit(renderPipeline, texture, x, y, u, v, width, height, textureWidth, textureHeight, color);
    }

    @Override
    public void blit(@NonNull RenderPipeline renderPipeline, @NonNull Identifier texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight)
    {
        this.guiGraphics.blit(renderPipeline, texture, x, y, u, v, width, height, textureWidth, textureHeight);
    }

    @Override
    public void blit(@NonNull RenderPipeline renderPipeline, @NonNull Identifier texture, int x, int y, float u, float v, int width, int height, int srcWidth, int srcHeight, int textureWidth, int textureHeight)
    {
        this.guiGraphics.blit(renderPipeline, texture, x, y, u, v, width, height, srcWidth, srcHeight, textureWidth, textureHeight);
    }

    @Override
    public void blit(@NonNull RenderPipeline renderPipeline, @NonNull Identifier texture, int x, int y, float u, float v, int width, int height, int srcWidth, int srcHeight, int textureWidth, int textureHeight, int color)
    {
        this.guiGraphics.blit(renderPipeline, texture, x, y, u, v, width, height, srcWidth, srcHeight, textureWidth, textureHeight, color);
    }

    @Override
    public void blit(@NonNull Identifier location, int x0, int y0, int x1, int y1, float u0, float u1, float v0, float v1)
    {
        this.guiGraphics.blit(location, x0, y0, x1, y1, u0, u1, v0, v1);
    }

    @Override
    public void blit(@NonNull GpuTextureView textureView, @NonNull GpuSampler sampler, int x0, int y0, int x1, int y1, float u0, float u1, float v0, float v1)
    {
        this.guiGraphics.blit(textureView, sampler, x0, y0, x1, y1, u0, u1, v0, v1);
    }

    @Override
    public void blitSprite(@NonNull RenderPipeline renderPipeline, @NonNull Identifier location, int x, int y, int width, int height)
    {
        this.guiGraphics.blitSprite(renderPipeline, location, x, y, width, height);
    }

    @Override
    public void blitSprite(@NonNull RenderPipeline renderPipeline, @NonNull Identifier location, int x, int y, int width, int height, float alpha)
    {
        this.guiGraphics.blitSprite(renderPipeline, location, x, y, width, height, alpha);
    }

    @Override
    public void blitSprite(@NonNull RenderPipeline renderPipeline, @NonNull Identifier location, int x, int y, int width, int height, int color)
    {
        this.guiGraphics.blitSprite(renderPipeline, location, x, y, width, height, color);
    }

    @Override
    public void blitSprite(@NonNull RenderPipeline renderPipeline, @NonNull Identifier location, int spriteWidth, int spriteHeight, int textureX, int textureY, int x, int y, int width, int height)
    {
        this.guiGraphics.blitSprite(renderPipeline, location, spriteWidth, spriteHeight, textureX, textureY, x, y, width, height);
    }

    @Override
    public void blitSprite(@NonNull RenderPipeline renderPipeline, @NonNull Identifier location, int spriteWidth, int spriteHeight, int textureX, int textureY, int x, int y, int width, int height, int color)
    {
        this.guiGraphics.blitSprite(renderPipeline, location, spriteWidth, spriteHeight, textureX, textureY, x, y, width, height, color);
    }

    @Override
    public void blitSprite(@NonNull RenderPipeline renderPipeline, @NonNull TextureAtlasSprite sprite, int x, int y, int width, int height)
    {
        this.guiGraphics.blitSprite(renderPipeline, sprite, x, y, width, height);
    }

    @Override
    public void blitSprite(@NonNull RenderPipeline renderPipeline, @NonNull TextureAtlasSprite sprite, int x, int y, int width, int height, int color)
    {
        this.guiGraphics.blitSprite(renderPipeline, sprite, x, y, width, height, color);
    }

    @Override
    public void item(@NonNull ItemStack itemStack, int x, int y)
    {
        this.guiGraphics.item(itemStack, x, y);
    }

    @Override
    public void item(@NonNull ItemStack itemStack, int x, int y, int seed)
    {
        this.guiGraphics.item(itemStack, x, y, seed);
    }

    @Override
    public void item(@NonNull LivingEntity owner, @NonNull ItemStack itemStack, int x, int y, int seed)
    {
        this.guiGraphics.item(owner, itemStack, x, y, seed);
    }

    @Override
    public void fakeItem(@NonNull ItemStack itemStack, int x, int y)
    {
        this.guiGraphics.fakeItem(itemStack, x, y);
    }

    @Override
    public void fakeItem(@NonNull ItemStack itemStack, int x, int y, int seed)
    {
        this.guiGraphics.fakeItem(itemStack, x, y, seed);
    }

    @Override
    public void itemDecorations(@NonNull Font font, @NonNull ItemStack itemStack, int x, int y)
    {
        this.guiGraphics.itemDecorations(font, itemStack, x, y);
    }

    @Override
    public void itemDecorations(@NonNull Font font, @NonNull ItemStack itemStack, int x, int y, @org.jspecify.annotations.Nullable String countText)
    {
        this.guiGraphics.itemDecorations(font, itemStack, x, y, countText);
    }

    @Override
    public void map(@NonNull MapRenderState mapRenderState)
    {
        this.guiGraphics.map(mapRenderState);
    }

    @Override
    public void entity(@NonNull EntityRenderState renderState, float scale, @NonNull Vector3fc translation, @NonNull Quaternionfc rotation, @org.jspecify.annotations.Nullable Quaternionfc overrideCameraAngle, int x0, int y0, int x1, int y1)
    {
        this.guiGraphics.entity(renderState, scale, translation, rotation, overrideCameraAngle, x0, y0, x1, y1);
    }

    @Override
    public void skin(Model.@NonNull Simple playerModel, @NonNull Identifier texture, float scale, float rotationX, float rotationY, float pivotY, int x0, int y0, int x1, int y1)
    {
        this.guiGraphics.skin(playerModel, texture, scale, rotationX, rotationY, pivotY, x0, y0, x1, y1);
    }

    @Override
    public void book(@NonNull BookModel bookModel, @NonNull Identifier texture, float scale, float open, float flip, int x0, int y0, int x1, int y1)
    {
        this.guiGraphics.book(bookModel, texture, scale, open, flip, x0, y0, x1, y1);
    }

    @Override
    public void bannerPattern(@NonNull BannerFlagModel flag, @NonNull DyeColor baseColor, @NonNull BannerPatternLayers resultBannerPatterns, int x0, int y0, int x1, int y1)
    {
        this.guiGraphics.bannerPattern(flag, baseColor, resultBannerPatterns, x0, y0, x1, y1);
    }

    @Override
    public void profilerChart(@NonNull List<ResultField> chartData, int x0, int y0, int x1, int y1)
    {
        this.guiGraphics.profilerChart(chartData, x0, y0, x1, y1);
    }

    @Override
    public void setTooltipForNextFrame(@NonNull Component component, int x, int y)
    {
        this.guiGraphics.setTooltipForNextFrame(component, x, y);
    }

    @Override
    public void setTooltipForNextFrame(@NonNull List<FormattedCharSequence> formattedCharSequences, int x, int y)
    {
        this.guiGraphics.setTooltipForNextFrame(formattedCharSequences, x, y);
    }

    @Override
    public void setTooltipForNextFrame(@NonNull Font font, @NonNull ItemStack itemStack, int xo, int yo)
    {
        this.guiGraphics.setTooltipForNextFrame(font, itemStack, xo, yo);
    }

    @Override
    public void setTooltipForNextFrame(@NonNull Font font, @NonNull List<Component> texts, @NonNull Optional<TooltipComponent> optionalImage, int xo, int yo)
    {
        this.guiGraphics.setTooltipForNextFrame(font, texts, optionalImage, xo, yo);
    }

    @Override
    public void setTooltipForNextFrame(@NonNull Font font, @NonNull List<Component> texts, @NonNull Optional<TooltipComponent> optionalImage, int xo, int yo, @org.jspecify.annotations.Nullable Identifier style)
    {
        this.guiGraphics.setTooltipForNextFrame(font, texts, optionalImage, xo, yo, style);
    }

    @Override
    public void setTooltipForNextFrame(@NonNull Font font, @NonNull List<Component> texts, @NonNull Optional<TooltipComponent> optionalImage, int xo, int yo, @org.jspecify.annotations.Nullable Identifier style, boolean extraSpaceAfterFirstLine)
    {
        this.guiGraphics.setTooltipForNextFrame(font, texts, optionalImage, xo, yo, style, extraSpaceAfterFirstLine);
    }

    @Override
    public void setTooltipForNextFrame(@NonNull Font font, @NonNull List<FormattedCharSequence> tooltip, @NonNull Optional<TooltipComponent> component, @NonNull ClientTooltipPositioner positioner, int xo, int yo, boolean replaceExisting, @org.jspecify.annotations.Nullable Identifier style)
    {
        this.guiGraphics.setTooltipForNextFrame(font, tooltip, component, positioner, xo, yo, replaceExisting, style);
    }

    @Override
    public void setTooltipForNextFrame(@NonNull Font font, @NonNull Component text, int xo, int yo)
    {
        this.guiGraphics.setTooltipForNextFrame(font, text, xo, yo);
    }

    @Override
    public void setTooltipForNextFrame(@NonNull Font font, @NonNull Component text, int xo, int yo, @org.jspecify.annotations.Nullable Identifier style)
    {
        this.guiGraphics.setTooltipForNextFrame(font, text, xo, yo, style);
    }

    @Override
    public void setComponentTooltipForNextFrame(@NonNull Font font, @NonNull List<Component> lines, int xo, int yo)
    {
        this.guiGraphics.setComponentTooltipForNextFrame(font, lines, xo, yo);
    }

    @Override
    public void setComponentTooltipForNextFrame(@NonNull Font font, @NonNull List<Component> lines, int xo, int yo, @org.jspecify.annotations.Nullable Identifier style)
    {
        this.guiGraphics.setComponentTooltipForNextFrame(font, lines, xo, yo, style);
    }

    @Override
    public void setTooltipForNextFrame(@NonNull Font font, @NonNull List<? extends FormattedCharSequence> lines, int xo, int yo)
    {
        this.guiGraphics.setTooltipForNextFrame(font, lines, xo, yo);
    }

    @Override
    public void setTooltipForNextFrame(@NonNull Font font, @NonNull List<? extends FormattedCharSequence> lines, int xo, int yo, @org.jspecify.annotations.Nullable Identifier style)
    {
        this.guiGraphics.setTooltipForNextFrame(font, lines, xo, yo, style);
    }

    @Override
    public void setTooltipForNextFrame(@NonNull Font font, @NonNull List<FormattedCharSequence> tooltip, @NonNull ClientTooltipPositioner positioner, int xo, int yo, boolean replaceExisting)
    {
        this.guiGraphics.setTooltipForNextFrame(font, tooltip, positioner, xo, yo, replaceExisting);
    }

    @Override
    public void tooltip(@NonNull Font font, @NonNull List<ClientTooltipComponent> lines, int xo, int yo, @NonNull ClientTooltipPositioner positioner, @org.jspecify.annotations.Nullable Identifier style, boolean extraSpaceAfterFirstLine)
    {
        this.guiGraphics.tooltip(font, lines, xo, yo, positioner, style, extraSpaceAfterFirstLine);
    }

    @Override
    public void setPreeditOverlay(@NonNull Renderable preeditOverlay)
    {
        this.guiGraphics.setPreeditOverlay(preeditOverlay);
    }

    @Override
    public void extractDeferredElements(int mouseX, int mouseY, float a)
    {
        this.guiGraphics.extractDeferredElements(mouseX, mouseY, a);
    }

    @Override
    public @NonNull TextureAtlasSprite getSprite(@NonNull SpriteId sprite)
    {
        return this.guiGraphics.getSprite(sprite);
    }

    @Override
    public @NonNull ActiveTextCollector textRendererForWidget(@NonNull AbstractWidget owner, @NonNull HoveredTextEffects hoveredTextEffects)
    {
        return this.guiGraphics.textRendererForWidget(owner, hoveredTextEffects);
    }

    @Override
    public @NonNull ActiveTextCollector textRenderer()
    {
        return this.guiGraphics.textRenderer();
    }

    @Override
    public @NonNull ActiveTextCollector textRenderer(@NonNull HoveredTextEffects hoveredTextEffects)
    {
        return this.guiGraphics.textRenderer(hoveredTextEffects);
    }

    @Override
    public @NonNull ActiveTextCollector textRenderer(@NonNull HoveredTextEffects hoveredTextEffects, @org.jspecify.annotations.Nullable Consumer<Style> additionalHoverStyleConsumer)
    {
        return this.guiGraphics.textRenderer(hoveredTextEffects, additionalHoverStyleConsumer);
    }
}
