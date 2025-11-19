package fi.dy.masa.malilib.render;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.GpuSampler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.*;
import net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.texture.TextureSetup;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

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
public class GuiContext extends DrawContext
{
	public GuiContext(MinecraftClient client, GuiRenderState state, int mouseX, int mouseY)
	{
		super(client, state, mouseX, mouseY);
	}

	/**
	 * Create from GuiGraphics
	 * @param context ()
	 * @return ()
	 */
	public static GuiContext fromGuiGraphics(DrawContext context)
	{
		return new GuiContext(
				((IMixinDrawContext) context).malilib_getClient(),
				((IMixinDrawContext) context).malilib_getRenderState(),
				((IMixinDrawContext) context).malilib_getMouseX(),
				((IMixinDrawContext) context).malilib_getMouseY()
		);
	}

	/**
	 * Get as GuiGraphics
	 * @return ()
	 */
	public DrawContext getGuiGraphics()
	{
		return (DrawContext) this;
	}

	public MinecraftClient mc()
	{
		return MinecraftClient.getInstance();
	}

	public TextRenderer textRenderer()
	{
		return MinecraftClient.getInstance().textRenderer;
	}

	/**
	 * Bind a GUI Texture
	 * @param id ()
	 * @return ()
	 */
	public Pair<GpuTextureView, GpuSampler> bindTexture(@Nullable Identifier id)
	{
		if (id == null) return null;
		ResourceTexture tex = (ResourceTexture) this.mc().getTextureManager().getTexture(id);

		if (tex != null && ((IMixinAbstractTexture) tex).malilib_getGlTextureView() != null)
		{
			return Pair.of(tex.getGlTextureView(), tex.getSampler());
		}

		MaLiLib.LOGGER.error("bindTexture: Texture Result is null for texture [{}]", id.toString());
		return null;
	}

	/**
	 * Render Item Tooltips Immediately without a focused screen.
	 * @param stack ()
	 * @return ()
	 */
	public List<Text> itemTooltips(ItemStack stack)
	{
//		return stack.getTooltip(ctx, mc.player, mc.options.advancedItemTooltips ? TooltipType.ADVANCED : TooltipType.BASIC);
		MinecraftClient mc = MinecraftClient.getInstance();
		if (mc.world == null || mc.player == null) return List.of();
		Item.TooltipContext ctx = Item.TooltipContext.create(WorldUtils.getBestWorld(mc));
		TooltipDisplayComponent displayComp = stack.getOrDefault(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplayComponent.DEFAULT);
		List<Text> list = new ArrayList<>();

		list.add(stack.getFormattedName());
		stack.appendTooltip(ctx, displayComp, mc.player, mc.options.advancedItemTooltips ? TooltipType.ADVANCED : TooltipType.BASIC, list::add);

		return list;
	}

	/**
	 * Add a Basic GUI Element
	 * @param element ()
	 */
	public void addSimpleElement(SimpleGuiElementRenderState element)
	{
		((IMixinDrawContext) this).malilib_getRenderState().addSimpleElement(element);
	}

	/**
	 * Add a Special GUI Element
	 * @param specialElement ()
	 */
	public void addSpecialElement(SpecialGuiElementRenderState specialElement)
	{
		((IMixinDrawContext) this).malilib_getRenderState().addSpecialElement(specialElement);
	}

	/**
	 * Add a Item GUI Element
	 * @param itemElement ()
	 */
	public void addItemElement(ItemGuiElementRenderState itemElement)
	{
		((IMixinDrawContext) this).malilib_getRenderState().addItem(itemElement);
	}

	/**
	 * Add a Text GUI Element
	 * @param textElement ()
	 */
	public void addTextElement(TextGuiElementRenderState textElement)
	{
		((IMixinDrawContext) this).malilib_getRenderState().addText(textElement);
	}

	/**
	 * Add a 'prepared' Text Element
	 * @param element ()
	 */
	public void addPreparedTextElement(SimpleGuiElementRenderState element)
	{
		((IMixinDrawContext) this).malilib_getRenderState().addPreparedTextElement(element);
	}

	/**
	 * Add a Textured Quad GUI Element
	 * @param element ()
	 */
	public void addSimpleElementToCurrentLayer(TexturedQuadGuiElementRenderState element)
	{
		((IMixinDrawContext) this).malilib_getRenderState().addSimpleElementToCurrentLayer(element);
	}

	/**
	 * Push the Scissor Stack
	 * @param rect ()
	 */
	public void pushScissor(@Nonnull ScreenRect rect)
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
		return ((IMixinDrawContext) this).malilib_getScissorStack().contains(x, y);
	}

	/**
	 * Peek the last Scissor Stack
	 * @return ()
	 */
	public ScreenRect peekLastScissor()
	{
		return ((IMixinDrawContext) this).malilib_getScissorStack().peekLast();
	}

	/**
	 * Pop the last Scissor Stack
	 * @return ()
	 */
	public ScreenRect popScissor()
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
		return TextureSetup.of(pair.getLeft(), pair.getRight());
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
		if (pair == null) return TextureSetup.empty();
		return setupTexture(pair);
	}
}
