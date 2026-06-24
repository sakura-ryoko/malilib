package fi.dy.masa.malilib.gui.widgets;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;


import net.minecraft.block.BlockState;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

import fi.dy.masa.malilib.config.IConfigBlockState;
import fi.dy.masa.malilib.config.options.ConfigBlockState;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiBlockStateEditor;
import fi.dy.masa.malilib.interfaces.IStringConsumer;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.StringUtils;

@ApiStatus.Experimental
public class WidgetBlockStateIcon extends WidgetBase
{
	protected final IConfigBlockState config;
	protected final ImmutableList<@NotNull String> hoverText;

	public WidgetBlockStateIcon(int x, int y, int width, int height, BlockState state, IStringConsumer consumer)
	{
		this(x, y, width, height, new ConfigBlockState("block_state_icon_widget", state));

		((ConfigBlockState) this.config).setValueChangeCallback(cfg -> consumer.setString(cfg.getStringValue()));
	}

	public WidgetBlockStateIcon(int x, int y, int width, int height, IConfigBlockState config)
	{
		super(x, y, width, height);
		this.config = config;
		this.hoverText = ImmutableList.of(StringUtils.translate("malilib.hover.block_state_icon.open_block_state_editor"));
	}

	@Override
	protected boolean onMouseClickedImpl(Click click, boolean doubleClick)
	{
		GuiBlockStateEditor gui = new GuiBlockStateEditor(this.config, this.config.getName(), null, GuiUtils.getCurrentScreen());
		GuiBase.openGui(gui);
		return true;
	}

	@Override
	public void postRenderHovered(DrawContext ctx, int mouseX, int mouseY, boolean selected)
	{
		super.postRenderHovered(ctx, mouseX, mouseY, selected);
		RenderUtils.drawHoverText(ctx, mouseX, mouseY, this.hoverText);
	}

	@Override
	public void render(DrawContext ctx, int mouseX, int mouseY, boolean selected)
	{
		super.render(ctx, mouseX, mouseY, selected);
		int x = this.getX();
		int y = this.getY();
		int width = this.getWidth();
		int height = this.getHeight();

		RenderUtils.drawRect(ctx, x    , y    , width    , height    , 0xFFFFFFFF);
		RenderUtils.drawRect(ctx, x + 1, y + 1, width - 2, height - 2, 0xFF000000);

		final ItemStack stack = this.config.getBlockStateValue().getBlock().asItem().getDefaultStack();

		ctx.getMatrices().pushMatrix();
		ctx.getMatrices().translate(x + 1, y + 1);
		ctx.getMatrices().scale(1, 1);
		ctx.drawItem(stack.copy(), 0, 0);
		ctx.drawStackOverlay(this.textRenderer, stack.copy(), 0, 0);
		ctx.getMatrices().popMatrix();
	}
}
