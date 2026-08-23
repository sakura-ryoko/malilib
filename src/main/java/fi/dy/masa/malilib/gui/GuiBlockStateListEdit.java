package fi.dy.masa.malilib.gui;

import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyInput;

import fi.dy.masa.malilib.config.IConfigBlockStateList;
import fi.dy.masa.malilib.gui.interfaces.IConfigGui;
import fi.dy.masa.malilib.gui.interfaces.IDialogHandler;
import fi.dy.masa.malilib.gui.widgets.WidgetListBlockStateListEdit;
import fi.dy.masa.malilib.gui.widgets.WidgetListBlockStateListEditEntry;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.KeyCodes;
import fi.dy.masa.malilib.util.StringUtils;

public class GuiBlockStateListEdit extends GuiListBase<BlockState, WidgetListBlockStateListEditEntry, WidgetListBlockStateListEdit>
{
	protected final IConfigBlockStateList config;
	protected final IConfigGui configGui;
	protected int dialogWidth;
	protected int dialogHeight;
	protected int dialogLeft;
	protected int dialogTop;
	protected int labelWidth;
	protected int textFieldWidth;
	@Nullable
	protected final IDialogHandler dialogHandler;
	protected final BlockState defaultBlockState = Blocks.AIR.getDefaultState();

	public GuiBlockStateListEdit(IConfigBlockStateList config, IConfigGui configGui, @Nullable IDialogHandler dialogHandler, Screen parent)
	{
		super(0, 0);

		this.config = config;
		this.configGui = configGui;
		this.dialogHandler = dialogHandler;
		this.title = StringUtils.translate("malilib.gui.title.block_state_list_edit", config.getName());

		if (this.dialogHandler == null)
		{
			this.setParent(parent);
		}
	}

	protected void setWidthAndHeight()
	{
		this.dialogWidth = 470;
		this.dialogHeight = GuiUtils.getScaledWindowHeight() - 90;
	}

	protected void centerOnScreen()
	{
		if (this.getParent() != null)
		{
			this.dialogLeft = this.getParent().width / 2 - this.dialogWidth / 2;
			this.dialogTop = this.getParent().height / 2 - this.dialogHeight / 2;
		}
		else
		{
			this.dialogLeft = 20;
			this.dialogTop = 20;
		}
	}

	@Override
	public void initGui()
	{
		this.setWidthAndHeight();
		this.centerOnScreen();
		this.reCreateListWidget();

		super.initGui();
	}

	public IConfigBlockStateList getConfig()
	{
		return this.config;
	}

	@Override
	protected int getBrowserWidth()
	{
		return this.dialogWidth - 14;
	}

	@Override
	protected int getBrowserHeight()
	{
		return this.dialogHeight - 30;
	}

	@Override
	protected WidgetListBlockStateListEdit createListWidget(int listX, int listY)
	{
		return new WidgetListBlockStateListEdit(this.dialogLeft + 10, this.dialogTop + 20, this.getBrowserWidth(), this.getBrowserHeight(), this.dialogWidth - 100, this);
	}

	@Override
	public void render(@NotNull DrawContext drawContext, int mouseX, int mouseY, float partialTicks)
	{
		if (this.getParent() != null)
		{
			this.getParent().render(drawContext, mouseX, mouseY, partialTicks);
		}

		super.render(drawContext, mouseX, mouseY, partialTicks);
	}

	@Override
	protected void drawScreenBackground(DrawContext ctx, int mouseX, int mouseY)
	{
		RenderUtils.drawOutlinedBox(ctx, this.dialogLeft, this.dialogTop, this.dialogWidth, this.dialogHeight, 0xFF000000, COLOR_HORIZONTAL_BAR);
	}

	@Override
	protected void drawTitle(DrawContext ctx, int mouseX, int mouseY, float partialTicks)
	{
		this.drawStringWithShadow(ctx, this.title, this.dialogLeft + 10, this.dialogTop + 6, COLOR_WHITE);
	}

	@Override
	public boolean onKeyTyped(KeyInput input)
	{
		if (input.key() == KeyCodes.KEY_ESCAPE && this.dialogHandler != null)
		{
			this.dialogHandler.closeDialog();
			return true;
		}
		else
		{
			return super.onKeyTyped(input);
		}
	}
}
