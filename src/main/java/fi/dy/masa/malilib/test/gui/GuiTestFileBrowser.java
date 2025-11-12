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
import fi.dy.masa.malilib.util.FileDeleter;
import fi.dy.masa.malilib.util.FileNameUtils;
import fi.dy.masa.malilib.util.FileRenamer;
import fi.dy.masa.malilib.util.StringUtils;

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
			if (this.type == ButtonType.RENAME)
			{
				WidgetFileBrowserBase.DirectoryEntry entry = this.gui.getListWidget().getLastSelectedEntry();

				if (entry != null && entry.getType() != WidgetFileBrowserBase.DirectoryEntryType.INVALID)
				{
//					MaLiLib.LOGGER.error("RENAME-ENTRY: [{}] // [{}]", entry.getName(), entry.getFullPath().toString());
					String title = "malilib.gui.title.rename_file_or_directory";
					Path target = entry.getFullPath();
					FileRenamer renamer = new FileRenamer(target, this.gui.getListWidget());
					GuiTextInputFeedback textInputFeedback = new GuiTextInputFeedback(256, title, entry.getName(), this.gui, renamer);
					GuiBase.openGui(textInputFeedback);
				}
			}
			else if (this.type == ButtonType.DELETE)
			{
				WidgetFileBrowserBase.DirectoryEntry entry = this.gui.getListWidget().getLastSelectedEntry();
				String title = "malilib.gui.title.delete_confirm";
				String message = "malilib.message.delete_confirm";

				if (entry != null && entry.getType() != WidgetFileBrowserBase.DirectoryEntryType.INVALID)
				{
//					MaLiLib.LOGGER.error("DELETE-ENTRY: [{}] // [{}]", entry.getName(), entry.getFullPath().toString());
					Path target = entry.getFullPath();
					FileDeleter deleter = new FileDeleter(target, this.gui.getListWidget());
					GuiConfirmAction confirmAction = new GuiConfirmAction(180, title, deleter, this.gui, message, target.getFileName().toString());
					GuiBase.openGui(confirmAction);
				}
				else if (this.gui.getListWidget().getCurrentDirectory() != null)
				{
					Path target = this.gui.getListWidget().getCurrentDirectory();
//					MaLiLib.LOGGER.error("DELETE-CD: [{}]", target.toAbsolutePath());
					FileDeleter deleter = new FileDeleter(target, this.gui.getListWidget());
					GuiConfirmAction confirmAction = new GuiConfirmAction(180, title, deleter, this.gui, message, target.getFileName().toString());
					GuiBase.openGui(confirmAction);
				}
			}
		}
	}

	protected enum ButtonType
	{
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
