package fi.dy.masa.malilib.gui;

import java.util.Collections;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyInput;

import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.config.IConfigTable;
import fi.dy.masa.malilib.config.options.table.Label;
import fi.dy.masa.malilib.config.options.table.TableRow;
import fi.dy.masa.malilib.gui.interfaces.IConfigGui;
import fi.dy.masa.malilib.gui.interfaces.IDialogHandler;
import fi.dy.masa.malilib.gui.widgets.WidgetListTableEdit;
import fi.dy.masa.malilib.gui.widgets.WidgetTableEditEntry;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.KeyCodes;
import fi.dy.masa.malilib.util.StringUtils;

@ApiStatus.Experimental
public class GuiTableEdit extends GuiListBase<TableRow, WidgetTableEditEntry, WidgetListTableEdit>
{
	protected final IConfigTable config;
	protected final IConfigGui configGui;
	protected int dialogWidth;
	protected int dialogHeight;
	protected int dialogLeft;
	protected int dialogTop;
	@Nullable
	protected final IDialogHandler dialogHandler;

	public GuiTableEdit(IConfigTable config, IConfigGui configGui, @Nullable IDialogHandler dialogHandler, Screen parent)
	{
		super(0, 0);

		this.config = config;
		this.configGui = configGui;
		this.dialogHandler = dialogHandler;
//		this.title = "Edit table for '" + config.getName() + "'";
		this.title = StringUtils.translate("malilib.gui.title.table_edit", config.getName());

		if (this.dialogHandler == null)
		{
			this.setParent(parent);
		}
	}

	protected void setWidthAndHeight()
	{
		this.dialogWidth = GuiUtils.getScaledWindowWidth() - 100;
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

	public IConfigTable getConfig()
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
		return this.dialogHeight - 40;
	}

	@Override
	protected WidgetListTableEdit createListWidget(int listX, int listY)
	{
		return new WidgetListTableEdit(this.dialogLeft + 10, this.dialogTop + 30, this.getBrowserWidth(), this.getBrowserHeight(), this.dialogWidth - 100, this);
	}

	@Override
	public void removed()
	{
		if (this.getListWidget() != null && this.getListWidget().wereConfigsModified())
		{
			this.getListWidget().applyPendingModifications();
			ConfigManager.getInstance().onConfigsChanged(this.configGui.getModId());
		}

		super.removed();
	}

	@Override
	public void render(DrawContext drawContext, int mouseX, int mouseY, float partialTicks)
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

		for (int i = 0; i < this.config.getLabels().size(); i++)
		{
			Label label = this.config.getLabels().get(i);

			int x = dialogLeft + 18;
			if (this.config.showEntryNumbers())
			{
				x += 15;
			}
			if (this.config.allowNewEntry())
			{
				x = x + i * ((dialogWidth - 170) / this.config.getTypes().size()) + 2;
			}
			else
			{
				x = x + i * ((dialogWidth - 130) / this.config.getTypes().size()) + 2;
			}

			this.drawStringWithShadow(ctx, label.label(), x, this.dialogTop + 25, COLOR_WHITE);
            int labelWidth = MinecraftClient.getInstance().textRenderer.getWidth(label.label());

            final int leniency = 2;
            int minLabelX = x - leniency;
            int maxLabelX = x + labelWidth + leniency;
            int minLabelY = this.dialogTop + 25 - leniency;
            int maxLabelY = minLabelY + MinecraftClient.getInstance().textRenderer.fontHeight + leniency;

            if (label.comment().isEmpty() == false && (mouseX >= minLabelX && mouseX <= maxLabelX && mouseY >= minLabelY && mouseY <= maxLabelY))
            {
                RenderUtils.drawHoverText(ctx, mouseX, mouseY, Collections.singletonList(label.comment()));
            }
		}
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
