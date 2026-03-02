package fi.dy.masa.malilib.gui.widgets;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import fi.dy.masa.malilib.config.IConfigBlockState;
import fi.dy.masa.malilib.config.options.ConfigBlockState;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiBlockStateEditor;
import fi.dy.masa.malilib.interfaces.IStringConsumer;
import fi.dy.masa.malilib.render.GuiContext;
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
	protected boolean onMouseClickedImpl(MouseButtonEvent click, boolean doubleClick)
	{
		GuiBlockStateEditor gui = new GuiBlockStateEditor(this.config, this.config.getName(), null, GuiUtils.getCurrentScreen());
		GuiBase.openGui(gui);
		return true;
	}

	@Override
	public void postRenderHovered(GuiContext ctx, int mouseX, int mouseY, boolean selected)
	{
		super.postRenderHovered(ctx, mouseX, mouseY, selected);
		RenderUtils.drawHoverText(ctx, mouseX, mouseY, this.hoverText);
	}

	@Override
	public void render(GuiContext ctx, int mouseX, int mouseY, boolean selected)
	{
		super.render(ctx, mouseX, mouseY, selected);
		int x = this.getX();
		int y = this.getY();
		int width = this.getWidth();
		int height = this.getHeight();

		RenderUtils.drawRect(ctx, x    , y    , width    , height    , 0xFFFFFFFF);
		RenderUtils.drawRect(ctx, x + 1, y + 1, width - 2, height - 2, 0xFF000000);

		final ItemStack stack = this.config.getBlockStateValue().getBlock().asItem().getDefaultInstance();

		ctx.pose().pushMatrix();
		ctx.pose().translate(x + 1, y + 1);
		ctx.pose().scale(1, 1);
		ctx.renderItem(stack.copy(), 0, 0);
		ctx.renderItemDecorations(ctx.fontRenderer(), stack.copy(), 0, 0);
		ctx.pose().popMatrix();
	}
}
