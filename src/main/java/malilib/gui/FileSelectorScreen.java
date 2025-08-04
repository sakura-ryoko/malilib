package malilib.gui;

import java.nio.file.Path;
import java.util.function.Predicate;
import javax.annotation.Nullable;

import malilib.gui.widget.BaseTextFieldWidget;
import malilib.gui.widget.button.GenericButton;
import malilib.gui.widget.list.BaseFileBrowserWidget;
import malilib.overlay.message.MessageDispatcher;
import malilib.util.FileNameUtils;
import malilib.util.FileUtils;
import malilib.util.StringUtils;
import malilib.util.data.ToBooleanFunction;

public class FileSelectorScreen extends BaseListScreen<BaseFileBrowserWidget>
{
    protected final GenericButton confirmButton;
    protected final BaseTextFieldWidget fileNameTextField;
    protected final Path rootDirectory;
    protected final Path currentDirectory;
    protected final ToBooleanFunction<Path> fileConsumer;
    protected Predicate<Path> fileFilter = FileUtils.ANY_FILE_FILEFILTER;
    protected String fileNameExtension = "json";
    protected boolean allowCreatingFiles;

    public FileSelectorScreen(Path currentDirectory, Path rootDirectory, ToBooleanFunction<Path> fileConsumer)
    {
        super(10, 28, 20, 58);

        this.currentDirectory = currentDirectory;
        this.rootDirectory = rootDirectory;
        this.fileConsumer = fileConsumer;
        this.confirmButton = GenericButton.create(16, this.getButtonLabel());
        this.confirmButton.setActionListener(this::onConfirm);

        this.fileNameTextField = new BaseTextFieldWidget(240, 16);
        this.fileNameTextField.setEmptyValueDisplayString(StringUtils.translate("malilib.label.file_selector_screen.file_name"));

        this.setTitle("malilib.title.screen.file_browser");
    }

    @Override
    protected void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();

        this.addWidget(this.confirmButton);
        this.addWidgetIf(this.fileNameTextField, this.allowCreatingFiles);
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        int x = this.getListX();
        int listBottom = this.getListWidget().getBottom() + 4;

        if (this.allowCreatingFiles)
        {
            this.fileNameTextField.setPosition(x, listBottom);
            this.confirmButton.setPosition(this.fileNameTextField.getRight() + 4, listBottom);
        }
        else
        {
            this.confirmButton.setPosition(x, listBottom);
        }
    }

    protected Predicate<Path> getFileFilter()
    {
        return this.fileFilter;
    }

    public void setFileFilter(Predicate<Path> fileFilter)
    {
        this.fileFilter = fileFilter;
        //this.getListWidget().setFileFilter(fileFilter);
    }

    public void setAllowCreatingFilesWithExtension(String fileNameExtension)
    {
        this.allowCreatingFiles = true;
        this.fileNameExtension = fileNameExtension;
        //this.getListWidget().getEntrySelectionHandler().setSelectionListener(this::onSelectionChange);
    }

    public void setConfirmButtonLabel(String translationKey)
    {
        this.confirmButton.setAutomaticWidth(true);
        this.confirmButton.setDisplayString(StringUtils.translate(translationKey));
    }

    protected String getButtonLabel()
    {
        return "malilib.button.config.use_selected_file";
    }

    protected void onConfirm()
    {
        BaseFileBrowserWidget.DirectoryEntry entry = this.getListWidget().getEntrySelectionHandler().getLastSelectedEntry();

        if (this.allowCreatingFiles)
        {
            String name = this.fileNameTextField.getText();

            if (org.apache.commons.lang3.StringUtils.isBlank(name))
            {
                MessageDispatcher.error("malilib.message.error.no_file_name_given");
                return;
            }

            Path dir = this.getListWidget().getCurrentDirectory();

            if (org.apache.commons.lang3.StringUtils.isBlank(this.fileNameExtension) == false &&
                name.endsWith("." + this.fileNameExtension) == false)
            {
                name += "." + this.fileNameExtension;
            }

            if (this.fileConsumer.applyAsBoolean(dir.resolve(name)))
            {
                this.openParentScreen();
            }
        }
        else if (entry != null && entry.getType() == BaseFileBrowserWidget.DirectoryEntryType.FILE)
        {
            if (this.fileConsumer.applyAsBoolean(entry.getFullPath()))
            {
                this.openParentScreen();
            }
        }
        else
        {
            MessageDispatcher.error("malilib.message.error.no_file_selected");
        }
    }

    public void onSelectionChange(@Nullable BaseFileBrowserWidget.DirectoryEntry entry)
    {
        if (this.allowCreatingFiles && entry != null &&
            entry.getType() == BaseFileBrowserWidget.DirectoryEntryType.FILE)
        {
            this.fileNameTextField.setText(FileNameUtils.getFileNameWithoutExtension(entry.getName()));
        }
    }

    @Override
    protected BaseFileBrowserWidget createListWidget()
    {
        BaseFileBrowserWidget listWidget = new BaseFileBrowserWidget(this.currentDirectory, this.rootDirectory, null, null);

        listWidget.setFileFilter(this.getFileFilter());
        listWidget.setParentScreen(this.getParent());

        if (this.allowCreatingFiles)
        {
            listWidget.getEntrySelectionHandler().setSelectionListener(this::onSelectionChange);
        }

        return listWidget;
    }
}
