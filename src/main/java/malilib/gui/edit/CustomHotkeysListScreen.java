package malilib.gui.edit;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import com.google.common.collect.ImmutableList;

import malilib.MaLiLibConfigScreen;
import malilib.MaLiLibReference;
import malilib.gui.BaseImportExportEntriesListScreen;
import malilib.gui.BaseListScreen;
import malilib.gui.BaseScreen;
import malilib.gui.ExportEntriesListScreen;
import malilib.gui.ImportEntriesListScreen;
import malilib.gui.TextInputScreen;
import malilib.gui.widget.button.GenericButton;
import malilib.gui.widget.list.DataListWidget;
import malilib.gui.widget.list.entry.CustomHotkeyDefinitionEntryWidget;
import malilib.input.CustomHotkeyDefinition;
import malilib.input.CustomHotkeyManager;
import malilib.input.KeyBind;
import malilib.input.KeyBindImpl;
import malilib.input.KeyBindSettings;
import malilib.overlay.message.MessageDispatcher;
import malilib.util.data.AppendOverwrite;

public class CustomHotkeysListScreen extends BaseListScreen<DataListWidget<CustomHotkeyDefinition>>
{
    protected final GenericButton addHotkeyButton;
    protected final GenericButton exportButton;
    protected final GenericButton importButton;
    protected final GenericButton toggleAllOnButton;
    protected final GenericButton toggleAllOffButton;

    public CustomHotkeysListScreen()
    {
        super(10, 68, 20, 74, MaLiLibReference.MOD_ID, MaLiLibConfigScreen.ALL_TABS, MaLiLibConfigScreen.GENERIC);

        this.setTitle("malilib.title.screen.configs.custom_hotkeys", MaLiLibReference.MOD_VERSION);

        this.exportButton  = GenericButton.create(16, "malilib.button.misc.export", this::openExportScreen);
        this.importButton  = GenericButton.create(16, "malilib.button.misc.import", this::openImportScreen);
        this.addHotkeyButton = GenericButton.create(16, "malilib.button.custom_hotkeys.add_hotkey", this::openAddHotkeyScreen);
        this.toggleAllOnButton = GenericButton.create(16, "malilib.button.custom_hotkeys.all_on", () -> CustomHotkeyManager.INSTANCE.getAllCustomHotkeys().forEach(k -> k.setEnabled(true)));
        this.toggleAllOffButton = GenericButton.create(16, "malilib.button.custom_hotkeys.all_off", () -> CustomHotkeyManager.INSTANCE.getAllCustomHotkeys().forEach(k -> k.setEnabled(false)));
        this.addHotkeyButton.translateAndAddHoverString("malilib.hover.button.custom_hotkeys.add_new_hotkey");

        this.addPreScreenCloseListener(CustomHotkeyManager.INSTANCE::checkDirtySaveAndUpdate);
        this.createSwitchModConfigScreenDropDown(MaLiLibReference.MOD_INFO);
    }

    @Override
    protected void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();

        this.addWidget(this.addHotkeyButton);
        this.addWidget(this.importButton);
        this.addWidget(this.exportButton);
        this.addWidget(this.toggleAllOnButton);
        this.addWidget(this.toggleAllOffButton);

        this.getListWidget().refreshEntries();
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        int y = this.getListWidget().getY() - 18;

        this.addHotkeyButton.setPosition(this.x + 10, y);
        this.toggleAllOnButton.setPosition(this.addHotkeyButton.getRight() + 2, y);
        this.toggleAllOffButton.setPosition(this.toggleAllOnButton.getRight() + 2, y);

        this.exportButton.setRight(this.getListWidget().getRight());
        this.exportButton.setY(y);
        this.importButton.setRight(this.exportButton.getX() - 2);
        this.importButton.setY(y);
    }

    protected void openAddHotkeyScreen()
    {
        String title = "malilib.title.screen.custom_hotkey_create";
        TextInputScreen screen = new TextInputScreen(title, "", this::openEditHotkeyScreen);
        screen.setParent(this);
        screen.setLabelText("malilib.label.custom_hotkeys.create.hotkey_name");
        screen.setInfoText("malilib.info.custom_hotkey.name_immutable");
        BaseScreen.openPopupScreen(screen);
    }

    protected boolean openEditHotkeyScreen(String name)
    {
        if (name.isEmpty())
        {
            return false;
        }

        KeyBind keyBind = KeyBindImpl.fromStorageString("", KeyBindSettings.INGAME_DEFAULT);
        CustomHotkeyDefinition hotkey = new CustomHotkeyDefinition(name, keyBind, ImmutableList.of());
        CustomHotkeyManager.INSTANCE.addCustomHotkey(hotkey);
        CustomHotkeyEditScreen screen = new CustomHotkeyEditScreen(hotkey);
        screen.setParent(this);
        BaseScreen.openScreen(screen);

        return true;
    }

    protected List<CustomHotkeyDefinition> getCustomHotkeyDefinitions()
    {
        List<CustomHotkeyDefinition> hotkeys = new ArrayList<>(CustomHotkeyManager.INSTANCE.getAllCustomHotkeys());
        hotkeys.sort(Comparator.comparing(CustomHotkeyDefinition::getName));
        return hotkeys;
    }

    @Override
    protected DataListWidget<CustomHotkeyDefinition> createListWidget()
    {
        DataListWidget<CustomHotkeyDefinition> listWidget = new DataListWidget<>(this::getCustomHotkeyDefinitions, true);

        listWidget.setListEntryWidgetFixedHeight(22);
        listWidget.setDataListEntryWidgetFactory(CustomHotkeyDefinitionEntryWidget::new);
        listWidget.setEntryFilterStringFunction(CustomHotkeyDefinition::getSearchStrings);
        listWidget.addDefaultSearchBar();

        return listWidget;
    }

    protected void openExportScreen()
    {
        this.initAndOpenExportOrImportScreen(new ExportEntriesListScreen<>(this.getCustomHotkeyDefinitions(),
                                                                           CustomHotkeyDefinition::toJson));
    }

    protected void openImportScreen()
    {
        this.initAndOpenExportOrImportScreen(new ImportEntriesListScreen<>(CustomHotkeyDefinition::fromJson,
                                                                           this::importEntries));
    }

    protected void initAndOpenExportOrImportScreen(BaseImportExportEntriesListScreen<CustomHotkeyDefinition> screen)
    {
        screen.setEntryFilterStringFunction(CustomHotkeyDefinition::getSearchStrings);
        screen.setWidgetFactory((d, cd) -> {
            CustomHotkeyDefinitionEntryWidget widget = new CustomHotkeyDefinitionEntryWidget(d, cd);
            widget.setAddEditElements(false);
            return widget;
        });

        BaseScreen.openScreenWithParent(screen);
    }

    protected void importEntries(List<CustomHotkeyDefinition> list, AppendOverwrite mode)
    {
        if (mode == AppendOverwrite.OVERWRITE)
        {
            CustomHotkeyManager.INSTANCE.clear();
        }

        for (CustomHotkeyDefinition hotkey : list)
        {
            CustomHotkeyManager.INSTANCE.addCustomHotkey(hotkey);
        }

        MessageDispatcher.success("malilib.message.info.successfully_imported_n_entries", list.size());
    }
}
