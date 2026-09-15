package fi.dy.masa.malilib.render;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import fi.dy.masa.malilib.config.HudAlignment;
import fi.dy.masa.malilib.render.element.MaLiLibBasicRectGuiElement;
import fi.dy.masa.malilib.render.element.MaLiLibGradientRectGuiElement;
import fi.dy.masa.malilib.render.element.MaLiLibTexturedGuiElement;
import fi.dy.masa.malilib.render.element.MaLiLibTexturedRectGuiElement;
import fi.dy.masa.malilib.render.special.MaLiLibBlockStateGuiElement;
import fi.dy.masa.malilib.util.GuiUtils;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;

import com.mojang.renderpearl.api.textures.GpuSampler;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.renderer.state.gui.*;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.mixin.render.IMixinAbstractTexture;
import fi.dy.masa.malilib.util.WorldUtils;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix3x2f;
import org.joml.Quaternionf;

/**
 * Wrapper around GuiGraphics to make the AW calls, and @Accessor Mixins easier to manage from one place.
 * It is meant to manage adding GUI Elements and Binding GUI textures;
 * and to reduce the need to be passing around the MC.getInstance() Object as a param.
 * -
 * When you need a GuiGraphics, you can just use this in its place and move on.
 */
@SuppressWarnings({"unused", "resource"})
public class GuiContext extends DelegatingGuiGraphicsExtractor
{
	public GuiContext(GuiGraphicsExtractor context)
	{
		super(context);
	}

	/**
	 * Create from GuiGraphics
	 * @param gui ()
	 * @return ()
	 */
	public static GuiContext fromGuiGraphics(GuiGraphicsExtractor gui)
	{
		return new GuiContext(gui);
	}

	/**
	 * Get as GuiGraphics
	 * @return ()
	 */
	public GuiGraphicsExtractor getGuiGraphics()
	{
        return this.guiGraphics;
    }

	public Minecraft mc()
	{
		return Minecraft.getInstance();
	}

	public Font fontRenderer()
	{
		return Minecraft.getInstance().font;
	}

	/**
	 * Bind a GUI Texture
	 * @param id ()
	 * @return ()
	 */
	public Pair<GpuTextureView, GpuSampler> bindTexture(@Nullable Identifier id)
	{
		if (id == null) return null;
		AbstractTexture tex = this.mc().getTextureManager().getTexture(id);

		if (((IMixinAbstractTexture) tex).malilib_getGlTextureView() != null)
		{
			return Pair.of(tex.getTextureView(), tex.getSampler());
		}

		MaLiLib.LOGGER.error("bindTexture: Texture Result is null for texture [{}]", id.toString());
		return null;
	}

	/**
	 * Render Item Tooltips Immediately without a focused screen.
	 * @param stack ()
	 * @return ()
	 */
	public List<Component> itemTooltips(ItemStack stack)
	{
		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null || mc.player == null) return List.of();
		Item.TooltipContext ctx = Item.TooltipContext.of(WorldUtils.getBestWorld(mc));
		TooltipDisplay displayComp = stack.getOrDefault(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT);
		List<Component> list = new ArrayList<>();

		list.add(stack.getStyledHoverName());
		stack.addDetailsToTooltip(ctx, displayComp, mc.player, mc.options.advancedItemTooltips ? TooltipFlag.ADVANCED : TooltipFlag.NORMAL, list::add);

		return list;
	}

	/**
	 * Send 'up()' to GuiRenderState()
	 */
	public void elementUp()
	{
		this.guiGraphics.guiRenderState.up();
	}

	/**
	 * Add a Basic GUI Element
	 * @param element ()
	 */
	public void addSimpleElement(GuiElementRenderState element)
	{
		this.guiGraphics.guiRenderState.addGuiElement(element);
	}

	/**
	 * Add a Special GUI Element
	 * @param specialElement ()
	 */
	public void addSpecialElement(PictureInPictureRenderState specialElement)
	{
		this.guiGraphics.guiRenderState.addPicturesInPictureState(specialElement);
	}

	/**
	 * Add an Item GUI Element
	 * @param itemElement ()
	 */
	public void addItemElement(GuiItemRenderState itemElement)
	{
		this.guiGraphics.guiRenderState.addItem(itemElement);
	}

	/**
	 * Add a Text GUI Element
	 * @param textElement ()
	 */
	public void addTextElement(GuiTextRenderState textElement)
	{
		this.guiGraphics.guiRenderState.addText(textElement);
	}

	/**
	 * Add a 'prepared' Text Element
	 * @param element ()
	 */
	public void addPreparedTextElement(GuiElementRenderState element)
	{
		this.guiGraphics.guiRenderState.addGlyphToCurrentLayer(element);
	}

	/**
	 * Add a Textured Quad GUI Element
	 * @param element ()
	 */
	public void addSimpleElementToCurrentLayer(BlitRenderState element)
	{
		this.guiGraphics.guiRenderState.addBlitToCurrentLayer(element);
	}

	/**
	 * Push the Scissor Stack
	 * @param rect ()
	 */
	public void pushScissor(@Nonnull ScreenRectangle rect)
	{
		this.guiGraphics.scissorStack.push(rect);
	}

	/**
	 * Return if (X, Y) is contained in a Scissor Stack
	 * @param x ()
	 * @param y ()
	 * @return ()
	 */
	public boolean containsScissor(int x, int y)
	{
		return this.guiGraphics.scissorStack.containsPoint(x, y);
	}

	/**
	 * Peek the last Scissor Stack
	 * @return ()
	 */
	public ScreenRectangle peekLastScissor()
	{
		return this.guiGraphics.scissorStack.peek();
	}

	/**
	 * Pop the last Scissor Stack
	 */
	public void popScissor()
	{
		this.guiGraphics.scissorStack.pop();
	}

	/**
	 * Get a Texture Setup from the Texture/Sampler Pair
	 *
	 * @param pair ()
	 * @return ()
	 */
	public TextureSetup setupTexture(Pair<GpuTextureView, GpuSampler> pair)
	{
		return TextureSetup.singleTexture(pair.getLeft(), pair.getRight());
	}

	/**
	 * Get a Texture Setup from a texture id, or
	 * return an empty instance if the texture bind fails.
	 *
	 * @param texture ()
	 * @return ()
	 */
	public TextureSetup setupTextureOrEmpty(@Nullable Identifier texture)
	{
		Pair<GpuTextureView, GpuSampler> pair = this.bindTexture(texture);
		if (pair == null) return TextureSetup.noTexture();
		return setupTexture(pair);
	}

	public void drawOutlinedBox(int x, int y, int width, int height, int colorBg, int colorBorder)
	{
		// Draw the background
		drawRect(x, y, width, height, colorBg);

		// Draw the border
		drawOutline(x - 1, y - 1, width + 2, height + 2, colorBorder);
	}

	public void drawOutlinedBox(int x, int y, int width, int height, float scale, int colorBg, int colorBorder)
	{
		// Draw the background
		drawRect(x, y, width, height, colorBg, scale);

		// Draw the border
		drawOutline(x - 1, y - 1, width + 2, height + 2, scale, colorBorder);
	}

	public void drawOutline(int x, int y, int width, int height, int colorBorder)
	{
		drawOutline(x, y, width, height, 1, colorBorder);
	}

	public void drawOutline(int x, int y, int width, int height, float scale, int colorBorder)
	{
		drawOutline(x, y, width, height, scale, 1, colorBorder);
	}

	@SuppressWarnings("SuspiciousNameCombination")
    public void drawOutline(int x, int y, int width, int height, int borderWidth, int colorBorder)
	{
		drawRect(x, y, borderWidth, height, colorBorder); // left edge
		drawRect(x + width - borderWidth, y, borderWidth, height, colorBorder); // right edge
		drawRect(x + borderWidth, y, width - 2 * borderWidth, borderWidth, colorBorder); // top edge
		drawRect(x + borderWidth, y + height - borderWidth, width - 2 * borderWidth, borderWidth, colorBorder); // bottom edge
	}

	@SuppressWarnings("SuspiciousNameCombination")
	public void drawOutline(int x, int y, int width, int height, float scale, int borderWidth, int colorBorder)
	{
		drawRect(x, y, borderWidth, height, colorBorder, scale); // left edge
		drawRect(x + width - borderWidth, y, borderWidth, height, colorBorder, scale); // right edge
		drawRect(x + borderWidth, y, width - 2 * borderWidth, borderWidth, colorBorder, scale); // top edge
		drawRect(x + borderWidth, y + height - borderWidth, width - 2 * borderWidth, borderWidth, colorBorder, scale); // bottom edge
	}
	
	public void drawRect(int x, int y, int width, int height, int color)
	{
		drawRect(x, y, width, height, color, 1.0f);
	}
	
	public void drawRect(int x, int y, int width, int height, int color, float scale)
	{
		this.addSimpleElement(new MaLiLibBasicRectGuiElement(
				RenderPipelines.GUI,
				TextureSetup.noTexture(),
				new Matrix3x2f(this.pose()),
				x, y,
				width, height,
				scale, color,
				this.peekLastScissor())
		);
	}

	public void drawTexturedRect(Identifier texture, int x, int y, int u, int v, int width, int height)
	{
		drawTexturedRect(texture, x, y, u, v, width, height, -1);
	}

	public void drawTexturedRect(Identifier texture, int x, int y, int u, int v, int width, int height, int argb)
	{
		float pixelWidth = 0.00390625F;
		Pair<GpuTextureView, GpuSampler> pair = this.bindTexture(texture);

		if (pair == null)
		{
			MaLiLib.LOGGER.error("drawTexturedRect(): GpuTextureView for '{}' is null!", texture.toString());
			return;
		}

		this.addSimpleElement(new MaLiLibTexturedGuiElement(
				RenderPipelines.GUI_TEXTURED,
				this.setupTexture(pair),
				new Matrix3x2f(this.pose()),
				x, y, x + width, y + height,
				u * pixelWidth, (u + width) * pixelWidth,
				v * pixelWidth, (v + height) * pixelWidth,
				argb,
				this.peekLastScissor())
		);
	}

	/**
	 * New GuiGraphics-based DrawTexturedBatched
	 */
	public void drawTexturedRectBatched(@Nonnull Pair<GpuTextureView, GpuSampler> pair, int x, int y, int u, int v, int width, int height)
	{
		drawTexturedRectBatched(pair, x, y, u, v, width, height, -1);
	}

	/**
	 * New GuiGraphics-based DrawTexturedBatched
	 */
	public void drawTexturedRectBatched(@Nonnull Pair<GpuTextureView, GpuSampler> pair, int x, int y, int u, int v, int width, int height, int argb)
	{
		this.addSimpleElement(new MaLiLibTexturedRectGuiElement(
				RenderPipelines.GUI_TEXTURED,
				this.setupTexture(pair),
				new Matrix3x2f(this.pose()),
				x, y, u, v,
				width, height, argb,
				this.peekLastScissor())
		);
	}

	/**
	 * Draw a 'Hover Text' Bubble object, simillar to Vanilla.
	 */
	public void drawHoverText(int x, int y, List<String> textLines)
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

			this.pose().pushMatrix();
			this.pose().translate(0, 0);

			int borderColor = 0xF0100010;
			drawGradientRectBatched(textStartX - 3, textStartY - 4, textStartX + maxLineLength + 3, textStartY - 3, borderColor, borderColor);
			drawGradientRectBatched(textStartX - 3, textStartY + textHeight + 3, textStartX + maxLineLength + 3, textStartY + textHeight + 4, borderColor, borderColor);
			drawGradientRectBatched(textStartX - 3, textStartY - 3, textStartX + maxLineLength + 3, textStartY + textHeight + 3, borderColor, borderColor);
			drawGradientRectBatched(textStartX - 4, textStartY - 3, textStartX - 3, textStartY + textHeight + 3, borderColor, borderColor);
			drawGradientRectBatched(textStartX + maxLineLength + 3, textStartY - 3, textStartX + maxLineLength + 4, textStartY + textHeight + 3, borderColor, borderColor);

			int fillColor1 = 0x505000FF;
			int fillColor2 = 0x5028007F;
			drawGradientRectBatched(textStartX - 3, textStartY - 3 + 1, textStartX - 3 + 1, textStartY + textHeight + 3 - 1, fillColor1, fillColor2);
			drawGradientRectBatched(textStartX + maxLineLength + 2, textStartY - 3 + 1, textStartX + maxLineLength + 3, textStartY + textHeight + 3 - 1, fillColor1, fillColor2);
			drawGradientRectBatched(textStartX - 3, textStartY - 3, textStartX + maxLineLength + 3, textStartY - 3 + 1, fillColor1, fillColor1);
			drawGradientRectBatched(textStartX - 3, textStartY + textHeight + 2, textStartX + maxLineLength + 3, textStartY + textHeight + 3, fillColor2, fillColor2);

            for (String str : textLines)
            {
                this.text(font, str, textStartX, textStartY, 0xFFFFFFFF, false);
                textStartY += lineHeight;
            }

			this.pose().popMatrix();
		}
	}

	/**
	 * Draw a 'Hover Text' Bubble object, similar to Vanilla.
	 */
	@ApiStatus.Experimental
	public void drawHoverText(int x, int y, Component text)
	{
		if (text != null && GuiUtils.getCurrentScreen() != null)
		{
			Font font = mc().font;
			int maxLineLength = font.width(text);
			int maxWidth = GuiUtils.getCurrentScreen().width;

			final int lineHeight = font.lineHeight + 1;
			int textHeight = lineHeight - 2;
			int textStartX = x + 4;
			int textStartY = Math.max(8, y - textHeight - 6);

			if (textStartX + maxLineLength + 6 > maxWidth)
			{
				textStartX = Math.max(2, maxWidth - maxLineLength - 8);
			}

			this.pose().pushMatrix();
			this.pose().translate(0, 0);

			int borderColor = 0xF0100010;
			drawGradientRectBatched(textStartX - 3, textStartY - 4, textStartX + maxLineLength + 3, textStartY - 3, borderColor, borderColor);
			drawGradientRectBatched(textStartX - 3, textStartY + textHeight + 3, textStartX + maxLineLength + 3, textStartY + textHeight + 4, borderColor, borderColor);
			drawGradientRectBatched(textStartX - 3, textStartY - 3, textStartX + maxLineLength + 3, textStartY + textHeight + 3, borderColor, borderColor);
			drawGradientRectBatched(textStartX - 4, textStartY - 3, textStartX - 3, textStartY + textHeight + 3, borderColor, borderColor);
			drawGradientRectBatched(textStartX + maxLineLength + 3, textStartY - 3, textStartX + maxLineLength + 4, textStartY + textHeight + 3, borderColor, borderColor);

			int fillColor1 = 0x505000FF;
			int fillColor2 = 0x5028007F;
			drawGradientRectBatched(textStartX - 3, textStartY - 3 + 1, textStartX - 3 + 1, textStartY + textHeight + 3 - 1, fillColor1, fillColor2);
			drawGradientRectBatched(textStartX + maxLineLength + 2, textStartY - 3 + 1, textStartX + maxLineLength + 3, textStartY + textHeight + 3 - 1, fillColor1, fillColor2);
			drawGradientRectBatched(textStartX - 3, textStartY - 3, textStartX + maxLineLength + 3, textStartY - 3 + 1, fillColor1, fillColor1);
			drawGradientRectBatched(textStartX - 3, textStartY + textHeight + 2, textStartX + maxLineLength + 3, textStartY + textHeight + 3, fillColor2, fillColor2);

			this.text(font, text, textStartX, textStartY, 0xFFFFFFFF, false);

			this.pose().popMatrix();
		}
	}

	/**
	 * Draw a Gradient Rect Element
	 */
	public void drawGradientRectBatched(float left, float top, float right, float bottom, int startColor, int endColor)
	{
		this.addSimpleElement(new MaLiLibGradientRectGuiElement(
				RenderPipelines.GUI,
				TextureSetup.noTexture(),
				new Matrix3x2f(this.pose()),
				left, top, right, bottom,
				startColor, endColor,
				this.peekLastScissor())
		);
	}

	/**
	 * Render a Centered String (GUI)
	 */
	public void drawCenteredString(int x, int y, int color, String text)
	{
		this.centeredText(mc().font, text, x, y, color);
	}

	/**
	 * Render a Horizontal Line (GUI)
	 */
	public void drawHorizontalLine(int x, int y, int width, int color)
	{
		drawRect(x, y, width, 1, color);
	}

	/**
	 * Render a Vertical Line (GUI)
	 */
	public void drawVerticalLine(int x, int y, int height, int color)
	{
		drawRect(x, y, 1, height, color);
	}

	/**
	 * Render a Texture Atlas Sprite (GUI)
	 */
	public void renderSprite(Identifier atlas, Identifier texture, int x, int y, int width, int height)
	{
		if (texture != null)
		{
			TextureAtlasSprite sprite = mc().getAtlasManager().getAtlasOrThrow(atlas).getSprite(texture);

            this.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, width, height, -1);
        }
	}

	/**
	 * Render Text (GUI)
	 */
	public void renderText(int x, int y, int color, String text)
	{
		String[] parts = text.split("\\\\n");
		Font textRenderer = mc().font;

		for (String line : parts)
		{
			this.text(textRenderer, line, x, y, color, true);
			y += textRenderer.lineHeight + 1;
		}
	}

	/**
	 * Render Text (GUI)
	 */
	@ApiStatus.Experimental
	public void renderText(int x, int y, int color, Component text)
	{
		this.text(mc().font, text, x, y, color, true);
	}

	/**
	 * Render Text (GUI)
	 */
	public void renderText(int x, int y, int color, List<String> lines)
	{
		if (lines.isEmpty() == false)
		{
			Font textRenderer = mc().font;

			for (String line : lines)
			{
				this.text(textRenderer, line, x, y, color, false);
				y += textRenderer.lineHeight + 2;
			}
		}
	}

	/**
	 * Render Scaled Text with a background (GUI)
	 */
	public int renderText(int xOff, int yOff, double scale,
								 int textColor, int bgColor, HudAlignment alignment,
								 boolean useBackground, boolean useShadow,
								 List<String> lines)
	{
		return renderText(xOff, yOff, scale,
				textColor, bgColor, alignment,
				useBackground, useShadow, true,
				lines);
	}

	/**
	 * Render Scaled Text with a background (GUI)
	 */
	public int renderText(
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

			this.pose().pushMatrix();
			this.pose().scale((float) scale, (float) scale);      // z = 1.0f
		}

		double posX = xOff + bgMargin;
		double posY = yOff + bgMargin;

		posY = RenderUtils.getHudPosY((int) posY, yOff, contentHeight, scale, alignment);

		if (useStatusShift && mc().player != null)
		{
			posY += RenderUtils.getHudOffsetForPotions(alignment, scale, mc().player);
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
//                drawRect(drawContext, x - bgMargin, y - bgMargin, width + bgMargin, bgMargin + fontRenderer.fontHeight, backgroundColor, (float) (scale * 2));
				drawRect(x - bgMargin, y - bgMargin, width + bgMargin, bgMargin + fontRenderer.lineHeight, bgColor);
			}

			this.text(fontRenderer, line, x, y, textColor, useShadow);
		}

		if (scaled)
		{
			this.pose().popMatrix();
		}

		return contentHeight + bgMargin * 2;
	}

	/**
	 * Render Scaled Text with a background (GUI)
	 */
	@ApiStatus.Experimental
	public int renderText(int xOff, int yOff, double scale,
								 int textColor, int bgColor, HudAlignment alignment,
								 boolean useBackground, boolean useShadow,
								 Component text)
	{
		return renderText(xOff, yOff, scale,
				textColor, bgColor, alignment,
				useBackground, useShadow, true,
				text);
	}

	/**
	 * Render Scaled Text with a background (GUI)
	 */
	@ApiStatus.Experimental
	public int renderText(int xOff, int yOff, double scale, 
						  int textColor, int bgColor, HudAlignment alignment, 
						  boolean useBackground, boolean useShadow, boolean useStatusShift, 
						  Component text)
	{
		Font fontRenderer = mc().font;
		final int scaledWidth = GuiUtils.getScaledWindowWidth();
		final int lineHeight = fontRenderer.lineHeight + 2;
		final int contentHeight = lineHeight - 2;
		final int bgMargin = 2;

		// Only Chuck Norris can divide by zero
		if (scale < 0.0125)
		{
			return 0;
		}

		boolean scaled = scale != 1.0;

		if (scaled)
		{
			this.pose().pushMatrix();
			this.pose().scale((float) scale, (float) scale);      // z = 1.0f
		}

		double posX = xOff + bgMargin;
		double posY = yOff + bgMargin;

		posY = RenderUtils.getHudPosY((int) posY, yOff, contentHeight, scale, alignment);

		if (useStatusShift && mc().player != null)
		{
			posY += RenderUtils.getHudOffsetForPotions(alignment, scale, mc().player);
		}

		final int width = fontRenderer.width(text);

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

		if (useBackground)
		{
			drawRect(x - bgMargin, y - bgMargin, width + bgMargin, bgMargin + fontRenderer.lineHeight, bgColor);
		}

		this.text(fontRenderer, text, x, y, textColor, useShadow);

		if (scaled)
		{
			this.pose().popMatrix();
		}

		return contentHeight + bgMargin * 2;
	}

	public void renderModel(int x, int y, BlockState state)
	{
		renderModel(x, y, 16, state, 0.75F, 0.50F);
		// scale: 0.625f ?
	}

	public void renderModel(int x, int y, BlockState state, float scale)
	{
		renderModel(x, y, 16, state, scale, 0.0F);
		// scale: 0.625f ?
	}

	public void renderModel(int x, int y, int size, BlockState state, float scale, float yOffset)
	{
		renderModel(x, y, size, state, scale, yOffset, 30 * (float) (Math.PI / 180), 225 * (float) (Math.PI / 180), 0);
	}

	public void renderModel(int x, int y, int size, BlockState state, float scale, float yOffset, float angleX, float angleY, float angleZ)
	{
		if (state.getBlock() == Blocks.AIR)
		{
			return;
		}

		this.addSpecialElement(
				new MaLiLibBlockStateGuiElement(
						state,
//						new Vector3f((float) (x + 8.0), (float) (y + 8.0), (float) (z + 100.0)),
						new Quaternionf().rotationXYZ(angleX, angleY, angleZ),
						x, y,
						size,
						scale,
						yOffset,
						this.peekLastScissor()
				)
		);
	}

	/**
	 * ==========================================================================================>>>
	 * WRAPPERS FOR PORTABILITY
	 * ==========================================================================================>>>
	 */

	public void drawString(final Font font, @Nullable final String str, final int x, final int y, final int color)
	{
		this.text(font, str, x, y, color, true);
	}

	public void drawString(final Font font, @Nullable final String str, final int x, final int y, final int color, final boolean shadow)
	{
		this.text(font, str, x, y, color, shadow);
	}

	public void drawString(final Font font, final FormattedCharSequence str, final int x, final int y, final int color)
	{
		this.text(font, str, x, y, color, true);
	}

	public void drawString(final Font font, final FormattedCharSequence str, final int x, final int y, final int color, final boolean shadow)
	{
		this.text(font, str, x, y, color, shadow);
	}

	public void drawString(final Font font, final Component str, final int x, final int y, final int color)
	{
		this.text(font, str, x, y, color, true);
	}

	public void drawString(final Font font, final Component str, final int x, final int y, final int color, final boolean shadow)
	{
		this.text(font, str, x, y, color, shadow);
	}

	public void drawCenteredString(final Font font, final String str, final int x, final int y, final int color)
	{
		this.centeredText(font, str, x, y, color);
	}

	public void drawCenteredString(final Font font, final Component text, final int x, final int y, final int color)
	{
		this.centeredText(font, text, x, y, color);
	}

	public void drawCenteredString(final Font font, final FormattedCharSequence text, final int x, final int y, final int color)
	{
		this.centeredText(font, text, x, y, color);
	}

	public void renderItem(final ItemStack stack, final int x, final int y)
	{
		this.item(stack, x, y);
	}

	public void renderItemDecorations(final Font font, final ItemStack stack, final int x, final int y)
	{
		this.itemDecorations(font, stack, x, y);
	}

	public void renderTooltip(final Font font, final Component text, final int xo, final int yo)
	{
		this.setTooltipForNextFrame(font, text, xo, yo, null);
	}

	public void renderTooltip(final Font font, final Component text, final int xo, final int yo, @Nullable final Identifier style)
	{
		this.setTooltipForNextFrame(font, text, xo, yo, style);
	}

	public void renderTooltip(final Font font, final List<Component> lines, final int xo, final int yo)
	{
		this.setComponentTooltipForNextFrame(font, lines, xo, yo, null);
	}

	public void renderTooltip(final Font font, final List<Component> lines, final int xo, final int yo, @Nullable final Identifier style)
	{
		this.setComponentTooltipForNextFrame(font, lines, xo, yo, style);
	}

	public void renderTooltip(final Font font, final List<ClientTooltipComponent> lines, final int xo, final int yo, final ClientTooltipPositioner positioner, @Nullable final Identifier style)
	{
		this.tooltip(font, lines, xo, yo, positioner, style, false);
	}

	public void renderTooltip(final Font font, final List<ClientTooltipComponent> lines, final int xo, final int yo, final ClientTooltipPositioner positioner, @Nullable final Identifier style, boolean extraSpace)
	{
		this.tooltip(font, lines, xo, yo, positioner, style, extraSpace);
	}
}
