package fi.dy.masa.malilib.test.gui;

import java.nio.file.Path;
import javax.annotation.Nullable;

import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.gui.*;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetDirectoryEntry;
import fi.dy.masa.malilib.gui.widgets.WidgetFileBrowserBase;
import fi.dy.masa.malilib.test.gui.widgets.WidgetTestBrowser;
import fi.dy.masa.malilib.util.*;

public class GuiTestFileBrowser extends GuiListBase<WidgetFileBrowserBase.DirectoryEntry, WidgetDirectoryEntry, WidgetTestBrowser>
	implements ISelectionListener<WidgetFileBrowserBase.DirectoryEntry>
{
	protected GuiTextFieldGeneric textField;
	protected String lastText;
//	protected String defaultText;

	public GuiTestFileBrowser()
	{
		super(12, 24);
		this.title = StringUtils.translate("malilib.gui.title.test_file_browser");
		this.textField = new GuiTextFieldGeneric(10, 32, 160, 20, this.textRenderer);
		this.textField.setMaxLengthWrapper(256);
		this.textField.setFocusedWrapper(true);
		this.lastText = "";
//		this.defaultText = "test_file.txt";
	}

	@Override
	protected WidgetTestBrowser createListWidget(int listX, int listY)
	{
		return new WidgetTestBrowser(listX, listY, 100, 100, this, this.getSelectionListener());
	}

	public String getBrowserContext()
	{
		return "test_file_browser";
	}

	public Path getDefaultDirectory()
	{
		return MaLiLibReference.GAME_DIR.resolve("logs");
	}

	@Override
	@Nullable
	protected ISelectionListener<WidgetFileBrowserBase.DirectoryEntry> getSelectionListener()
	{
		return this;
	}

	@Override
	protected int getBrowserWidth()
	{
		return this.getScreenWidth() - 20;
	}

	@Override
	protected int getBrowserHeight()
	{
		return this.getScreenHeight() - 70;
	}

	@Override
	public void initGui()
	{
		super.initGui();

		int x = 10;
		int y = this.getScreenHeight() - 26;

		x += this.createButton(x, y, ButtonType.COPY);
		x += this.createButton(x, y, ButtonType.RENAME);
		x += this.createButton(x, y, ButtonType.DELETE);
	}

	protected int createButton(int x, int y, ButtonType type)
	{
		ButtonGeneric button = new ButtonGeneric(x, y, -1, 20, type.getDisplayName());
		this.addButton(button, this.createActionListener(type));
		return button.getWidth() + 2;
	}

	protected ButtonListener createActionListener(ButtonType type)
	{
		return new ButtonListener(type, this);
	}

	protected void setTextFieldText(String text)
	{
		this.lastText = text;
		this.textField.setTextWrapper(text);
	}

	protected String getTextFieldText()
	{
		return this.textField.getTextWrapper();
	}

	@Override
	public void onSelectionChange(@Nullable WidgetFileBrowserBase.DirectoryEntry entry)
	{
		if (entry != null && entry.getType() != WidgetFileBrowserBase.DirectoryEntryType.DIRECTORY &&
			entry.getType() != WidgetFileBrowserBase.DirectoryEntryType.INVALID)
		{
			this.setTextFieldText(FileNameUtils.getFileNameWithoutExtension(entry.getName()));
		}
	}

	protected record ButtonListener(ButtonType type, GuiTestFileBrowser gui) implements IButtonActionListener
	{
		@Override
		public void actionPerformedWithButton(ButtonBase button, int mouseButton)
		{
			WidgetFileBrowserBase.DirectoryEntry entry = this.gui.getListWidget().getLastSelectedEntry();

			if (entry == null)
			{
				if (this.gui.getListWidget().getCurrentDirectory() != null)
				{
					Path target = this.gui.getListWidget().getCurrentDirectory();
	//					MaLiLib.LOGGER.error("DELETE-CD: [{}]", target.toAbsolutePath());
					FileDeleter deleter = new FileDeleter(target, this.gui.getListWidget(), true);
					GuiBase.openGui(new GuiConfirmAction(180, "malilib.gui.title.delete_confirm", deleter, this.gui, "malilib.message.delete_confirm", target.getFileName().toString()));
				}
			}
			else if (entry.getType() == WidgetFileBrowserBase.DirectoryEntryType.INVALID)
			{
				// Ignored
				return;
			}
			else
			{
				Path target = entry.getFullPath();

				if (this.type == ButtonType.COPY)
				{
//					MaLiLib.LOGGER.error("COPY-ENTRY: [{}] // [{}]", entry.getName(), entry.getFullPath().toString());
					FileCopier copier = new FileCopier(target, this.gui.getListWidget(), true);
					GuiBase.openGui(new GuiTextInputFeedback(256, "malilib.gui.title.copy_file", entry.getName(), this.gui, copier));
				}
				else if (this.type == ButtonType.RENAME)
				{
//					MaLiLib.LOGGER.error("RENAME-ENTRY: [{}] // [{}]", entry.getName(), entry.getFullPath().toString());
					FileRenamer renamer = new FileRenamer(target, this.gui.getListWidget(), true);
					GuiBase.openGui(new GuiTextInputFeedback(256, "malilib.gui.title.rename_file_or_directory", entry.getName(), this.gui, renamer));
				}
				else if (this.type == ButtonType.DELETE)
				{
//					MaLiLib.LOGGER.error("DELETE-ENTRY: [{}] // [{}]", entry.getName(), entry.getFullPath().toString());
					FileDeleter deleter = new FileDeleter(target, this.gui.getListWidget(), true);
					GuiBase.openGui(new GuiConfirmAction(180, "malilib.gui.title.delete_confirm", deleter, this.gui, "malilib.message.delete_confirm", target.getFileName().toString()));
				}
			}
		}
	}

	protected enum ButtonType
	{
		COPY		("malilib.gui.button.copy"),
		RENAME      ("malilib.gui.button.rename"),
		DELETE      ("malilib.gui.button.delete"),
		;

		private final String labelKey;

		ButtonType(String labelKey)
		{
			this.labelKey = labelKey;
		}

		public String getDisplayName()
		{
			return StringUtils.translate(this.labelKey);
		}
	}
}
