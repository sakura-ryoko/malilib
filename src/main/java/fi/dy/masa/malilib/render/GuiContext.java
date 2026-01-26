package fi.dy.masa.malilib.render;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.*;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.mixin.render.IMixinAbstractTexture;
import fi.dy.masa.malilib.mixin.render.IMixinDrawContext;
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

	/**
	 * Create from GuiGraphics
	 * @param guiGraphics ()
	 * @return ()
	 */
	public static GuiContext fromGuiGraphics(GuiGraphics guiGraphics)
	{
		GuiContext ctx = new GuiContext(
				((IMixinDrawContext) guiGraphics).malilib_getClient(),
				((IMixinDrawContext) guiGraphics).malilib_getRenderState(),
				((IMixinDrawContext) guiGraphics).malilib_getMouseX(),
				((IMixinDrawContext) guiGraphics).malilib_getMouseY()
		);

		// Store the proper reference
		ctx.guiGraphics = guiGraphics;
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
		SimpleTexture tex = (SimpleTexture) this.mc().getTextureManager().getTexture(id);

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
	 * Add a Basic GUI Element
	 * @param element ()
	 */
	public void addSimpleElement(GuiElementRenderState element)
	{
		((IMixinDrawContext) this).malilib_getRenderState().submitGuiElement(element);
	}

	/**
	 * Add a Special GUI Element
	 * @param specialElement ()
	 */
	public void addSpecialElement(PictureInPictureRenderState specialElement)
	{
		((IMixinDrawContext) this).malilib_getRenderState().submitPicturesInPictureState(specialElement);
	}

	/**
	 * Add a Item GUI Element
	 * @param itemElement ()
	 */
	public void addItemElement(GuiItemRenderState itemElement)
	{
		((IMixinDrawContext) this).malilib_getRenderState().submitItem(itemElement);
	}

	/**
	 * Add a Text GUI Element
	 * @param textElement ()
	 */
	public void addTextElement(GuiTextRenderState textElement)
	{
		((IMixinDrawContext) this).malilib_getRenderState().submitText(textElement);
	}

	/**
	 * Add a 'prepared' Text Element
	 * @param element ()
	 */
	public void addPreparedTextElement(GuiElementRenderState element)
	{
		((IMixinDrawContext) this).malilib_getRenderState().submitGlyphToCurrentLayer(element);
	}

	/**
	 * Add a Textured Quad GUI Element
	 * @param element ()
	 */
	public void addSimpleElementToCurrentLayer(BlitRenderState element)
	{
		((IMixinDrawContext) this).malilib_getRenderState().submitBlitToCurrentLayer(element);
	}

	/**
	 * Push the Scissor Stack
	 * @param rect ()
	 */
	public void pushScissor(@Nonnull ScreenRectangle rect)
	{
		((IMixinDrawContext) this).malilib_getScissorStack().push(rect);
	}

	/**
	 * Return if (X, Y) is contained in a Scissor Stack
	 * @param x ()
	 * @param y ()
	 * @return ()
	 */
	public boolean containsScissor(int x, int y)
	{
		return ((IMixinDrawContext) this).malilib_getScissorStack().containsPoint(x, y);
	}

	/**
	 * Peek the last Scissor Stack
	 * @return ()
	 */
	public ScreenRectangle peekLastScissor()
	{
		return ((IMixinDrawContext) this).malilib_getScissorStack().peek();
	}

	/**
	 * Pop the last Scissor Stack
	 * @return ()
	 */
	public ScreenRectangle popScissor()
	{
		return ((IMixinDrawContext) this).malilib_getScissorStack().pop();
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
