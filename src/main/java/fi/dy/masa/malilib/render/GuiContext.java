package fi.dy.masa.malilib.render;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
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

public class GuiContext extends DrawContext
{
	public GuiContext(MinecraftClient client, GuiRenderState state, int mouseX, int mouseY)
	{
		super(client, state, mouseX, mouseY);
	}

	public static GuiContext fromGuiGraphics(DrawContext context)
	{
		return new GuiContext(
				((IMixinDrawContext) context).malilib_getClient(),
				((IMixinDrawContext) context).malilib_getRenderState(),
				((IMixinDrawContext) context).malilib_getMouseX(),
				((IMixinDrawContext) context).malilib_getMouseY()
		);
	}

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

	public Pair<GpuTextureView, GpuSampler> bindTexture(Identifier id)
	{
		ResourceTexture tex = (ResourceTexture) this.mc().getTextureManager().getTexture(id);

		if (tex != null && ((IMixinAbstractTexture) tex).malilib_getGlTextureView() != null)
		{
			return Pair.of(tex.getGlTextureView(), tex.getSampler());
		}

		MaLiLib.LOGGER.error("bindTexture: Result is null!");
		return null;
	}

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

	public void addSimpleElement(SimpleGuiElementRenderState element)
	{
		((IMixinDrawContext) this).malilib_getRenderState().addSimpleElement(element);
	}

	public void addSpecialElement(SpecialGuiElementRenderState specialElement)
	{
		((IMixinDrawContext) this).malilib_getRenderState().addSpecialElement(specialElement);
	}

	public void addItemElement(ItemGuiElementRenderState itemElement)
	{
		((IMixinDrawContext) this).malilib_getRenderState().addItem(itemElement);
	}

	public void addTextElement(TextGuiElementRenderState textElement)
	{
		((IMixinDrawContext) this).malilib_getRenderState().addText(textElement);
	}

	public void addPreparedTextElement(SimpleGuiElementRenderState element)
	{
		((IMixinDrawContext) this).malilib_getRenderState().addPreparedTextElement(element);
	}

	public void addSimpleElementToCurrentLayer(TexturedQuadGuiElementRenderState element)
	{
		((IMixinDrawContext) this).malilib_getRenderState().addSimpleElementToCurrentLayer(element);
	}

	public void pushScissor(@Nonnull ScreenRect rect)
	{
		((IMixinDrawContext) this).malilib_getScissorStack().push(rect);
	}

	public boolean containsScissor(int x, int y)
	{
		return ((IMixinDrawContext) this).malilib_getScissorStack().contains(x, y);
	}

	public ScreenRect peekLastScissor()
	{
		return ((IMixinDrawContext) this).malilib_getScissorStack().peekLast();
	}

	public ScreenRect popScissor()
	{
		return ((IMixinDrawContext) this).malilib_getScissorStack().pop();
	}

	public TextureSetup setupTexture(Pair<GpuTextureView, GpuSampler> pair)
	{
		return TextureSetup.of(pair.getLeft(), pair.getRight());
	}
}
