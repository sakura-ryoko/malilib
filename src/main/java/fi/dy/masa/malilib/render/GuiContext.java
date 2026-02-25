package fi.dy.masa.malilib.render;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Matrix3x2fStack;

import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.*;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.mixin.render.IMixinAbstractTexture;
import fi.dy.masa.malilib.util.WorldUtils;

/**
 * Wrapper around GuiGraphics to make the AW calls, and @Accessor Mixins easier to manage from one place.
 * It is meant to manage adding GUI Elements and Binding GUI textures;
 * and to reduce the need to be passing around the MC.getInstance() Object as a param.
 * -
 * When you need a GuiGraphics, you can just use this in its place and move on.
 */
public class GuiContext extends GuiGraphics
{
	private GuiGraphics guiGraphics;

	public GuiContext(Minecraft client, GuiRenderState state, int mouseX, int mouseY)
	{
		super(client, state, mouseX, mouseY);
	}

	public GuiContext(Minecraft client, Matrix3x2fStack pose, GuiRenderState state, int mouseX, int mouseY)
	{
		super(client, pose, state, mouseX, mouseY);
	}

	/**
	 * Create from GuiGraphics
	 * @param gui ()
	 * @return ()
	 */
	public static GuiContext fromGuiGraphics(GuiGraphics gui)
	{
		// Copy with Pose Stack
		GuiContext ctx = new GuiContext(
				gui.minecraft,
				gui.pose, gui.guiRenderState,
				gui.mouseX, gui.mouseY
		);

		ctx.pendingCursor = gui.pendingCursor;
		ctx.deferredTooltip = gui.deferredTooltip;
		ctx.hoveredTextStyle = gui.hoveredTextStyle;
		ctx.clickableTextStyle = gui.clickableTextStyle;

		// Store the proper reference
		ctx.guiGraphics = gui;
		return ctx;
	}

	/**
	 * Get as GuiGraphics
	 * @return ()
	 */
	public GuiGraphics getGuiGraphics()
	{
		if (this.guiGraphics != null)
		{
			return this.guiGraphics;
		}

		return (GuiGraphics) this;
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
		AbstractTexture tex = (AbstractTexture) this.mc().getTextureManager().getTexture(id);

		if (tex != null && ((IMixinAbstractTexture) tex).malilib_getGlTextureView() != null)
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
//		return stack.getTooltip(ctx, mc.player, mc.options.advancedItemTooltips ? TooltipType.ADVANCED : TooltipType.BASIC);
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
		this.guiRenderState.up();
	}

	/**
	 * Add a Basic GUI Element
	 * @param element ()
	 */
	public void addSimpleElement(GuiElementRenderState element)
	{
//		((IMixinGuiGraphics) this).malilib_getRenderState().submitGuiElement(element);
		this.guiRenderState.submitGuiElement(element);
	}

	/**
	 * Add a Special GUI Element
	 * @param specialElement ()
	 */
	public void addSpecialElement(PictureInPictureRenderState specialElement)
	{
//		((IMixinGuiGraphics) this).malilib_getRenderState().submitPicturesInPictureState(specialElement);
		this.guiRenderState.submitPicturesInPictureState(specialElement);
	}

	/**
	 * Add a Item GUI Element
	 * @param itemElement ()
	 */
	public void addItemElement(GuiItemRenderState itemElement)
	{
//		((IMixinGuiGraphics) this).malilib_getRenderState().submitItem(itemElement);
		this.guiRenderState.submitItem(itemElement);
	}

	/**
	 * Add a Text GUI Element
	 * @param textElement ()
	 */
	public void addTextElement(GuiTextRenderState textElement)
	{
//		((IMixinGuiGraphics) this).malilib_getRenderState().submitText(textElement);
		this.guiRenderState.submitText(textElement);
	}

	/**
	 * Add a 'prepared' Text Element
	 * @param element ()
	 */
	public void addPreparedTextElement(GuiElementRenderState element)
	{
//		((IMixinGuiGraphics) this).malilib_getRenderState().submitGlyphToCurrentLayer(element);
		this.guiRenderState.submitGlyphToCurrentLayer(element);
	}

	/**
	 * Add a Textured Quad GUI Element
	 * @param element ()
	 */
	public void addSimpleElementToCurrentLayer(BlitRenderState element)
	{
//		((IMixinGuiGraphics) this).malilib_getRenderState().submitBlitToCurrentLayer(element);
		this.guiRenderState.submitBlitToCurrentLayer(element);
	}

	/**
	 * Push the Scissor Stack
	 * @param rect ()
	 */
	public void pushScissor(@Nonnull ScreenRectangle rect)
	{
//		((IMixinGuiGraphics) this).malilib_getScissorStack().push(rect);
		this.scissorStack.push(rect);
	}

	/**
	 * Return if (X, Y) is contained in a Scissor Stack
	 * @param x ()
	 * @param y ()
	 * @return ()
	 */
	public boolean containsScissor(int x, int y)
	{
//		return ((IMixinGuiGraphics) this).malilib_getScissorStack().containsPoint(x, y);
		return this.scissorStack.containsPoint(x, y);
	}

	/**
	 * Peek the last Scissor Stack
	 * @return ()
	 */
	public ScreenRectangle peekLastScissor()
	{
//		return ((IMixinGuiGraphics) this).malilib_getScissorStack().peek();
		return this.scissorStack.peek();
	}

	/**
	 * Pop the last Scissor Stack
	 * @return ()
	 */
	public ScreenRectangle popScissor()
	{
//		return ((IMixinGuiGraphics) this).malilib_getScissorStack().pop();
		return this.scissorStack.pop();
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
}
