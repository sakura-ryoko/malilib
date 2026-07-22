package malilib.gui.edit.overlay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;

import malilib.MaLiLibConfigScreen;
import malilib.MaLiLibReference;
import malilib.config.value.OptionConfigValue;
import malilib.config.value.ScreenLocation;
import malilib.gui.BaseImportExportEntriesListScreen;
import malilib.gui.BaseListScreen;
import malilib.gui.BaseScreen;
import malilib.gui.ExportEntriesListScreen;
import malilib.gui.ImportEntriesListScreen;
import malilib.gui.tab.ScreenTab;
import malilib.gui.widget.DropDownListWidget;
import malilib.gui.widget.button.GenericButton;
import malilib.gui.widget.list.DataListWidget;
import malilib.gui.widget.list.entry.BaseInfoRendererWidgetEntryWidget;
import malilib.gui.widget.list.entry.BaseListEntryWidget;
import malilib.gui.widget.list.entry.DataListEntryWidgetData;
import malilib.overlay.message.MessageDispatcher;
import malilib.overlay.widget.InfoRendererWidget;
import malilib.registry.Registry;
import malilib.util.data.AppendOverwrite;

public class InfoRendererWidgetListScreen<WIDGET extends InfoRendererWidget> extends BaseListScreen<DataListWidget<WIDGET>>
{
    protected final Supplier<List<WIDGET>> widgetSupplier;
    protected final EntryWidgetFactory entryWidgetFactory;
    @Nullable protected final Supplier<WIDGET> widgetFactory;

    protected final DropDownListWidget<ScreenLocation> locationDropdownWidget;
    protected final GenericButton createWidgetButton;
    protected final GenericButton exportButton;
    protected final GenericButton importButton;
    protected boolean canCreateNewWidgets;
    protected boolean canImportExport = true;

    public InfoRendererWidgetListScreen(Supplier<List<WIDGET>> widgetSupplier,
                                        @Nullable Supplier<WIDGET> widgetFactory,
                                        EntryWidgetFactory entryWidgetFactory)
    {
        this(10, 74, 20, 80,
             MaLiLibReference.MOD_ID, MaLiLibConfigScreen.ALL_TABS, MaLiLibConfigScreen.GENERIC,
             widgetSupplier, widgetFactory, entryWidgetFactory);
    }

    public InfoRendererWidgetListScreen(int listX, int listY, int totalListMarginX, int totalListMarginY,
                                        String screenId, List<ScreenTab> tabs, @Nullable ScreenTab defaultTab,
                                        Supplier<List<WIDGET>> widgetSupplier,
                                        @Nullable Supplier<WIDGET> widgetFactory,
                                        EntryWidgetFactory entryWidgetFactory)
    {
        super(listX, listY, totalListMarginX, totalListMarginY, screenId, tabs, defaultTab);

        this.widgetSupplier = widgetSupplier;
        this.widgetFactory = widgetFactory;
        this.entryWidgetFactory = entryWidgetFactory;

        this.locationDropdownWidget = new DropDownListWidget<>(16, 10, ScreenLocation.VALUES, OptionConfigValue::getDisplayName);
        this.createWidgetButton = GenericButton.create(16, "malilib.button.csi_edit.add_csi_widget", this::createInfoRendererWidget);
        this.exportButton  = GenericButton.create(16, "malilib.button.misc.export", this::openExportScreen);
        this.importButton  = GenericButton.create(16, "malilib.button.misc.import", this::openImportScreen);


        this.addPreInitListener(this::setListPosition);
        // Unconditionally save here, since we don't track the individual info container widget changes
        this.addPreScreenCloseListener(Registry.INFO_WIDGET_MANAGER::saveToFile);

        this.createSwitchModConfigScreenDropDown(MaLiLibReference.MOD_INFO);
    }

    @Override
    protected void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();

        if (this.canCreateNewWidgets)
        {
            this.addWidget(this.locationDropdownWidget);
            this.addWidget(this.createWidgetButton);
        }

        if (this.canImportExport)
        {
            this.addWidget(this.importButton);
            this.addWidget(this.exportButton);
        }
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        int x = this.x + 10;
        int y = this.y + 57;

        this.locationDropdownWidget.setPosition(x, y);
        this.createWidgetButton.setPosition(this.locationDropdownWidget.getRight() + 4, y);

        if (this.canImportExport)
        {
            y = this.getListWidget().getY() - 1;
            this.exportButton.setRight(this.getListWidget().getRight());
            this.exportButton.setBottom(y);
            this.importButton.setRight(this.exportButton.getX() - 2);
            this.importButton.setBottom(y);
        }
    }

    @Override
    protected DataListWidget<WIDGET> createListWidget()
    {
        DataListWidget<WIDGET> listWidget = new DataListWidget<>(this.widgetSupplier, true);

        listWidget.setDataListEntryWidgetFactory(this.entryWidgetFactory::create);
        listWidget.addDefaultSearchBar();
        listWidget.setEntryFilterStringFunction(w -> Collections.singletonList(w.getName()));

        return listWidget;
    }

    protected void setListPosition()
    {
        if (this.canCreateNewWidgets == false)
        {
            this.totalListMarginY = 64;
            this.updateListPosition(10, 60);
        }
    }

    protected void createInfoRendererWidget()
    {
        if (this.widgetFactory != null)
        {
            this.addInfoRendererWidget(this.widgetFactory.get());
        }
    }

    protected void addInfoRendererWidget(InfoRendererWidget widget)
    {
        ScreenLocation location = this.locationDropdownWidget.getSelectedEntry();

        if (location == null)
        {
            MessageDispatcher.error("malilib.message.error.select_screen_location_in_dropdown");
            return;
        }

        widget.setLocation(location);
        Registry.INFO_WIDGET_MANAGER.addWidget(widget);
        this.getListWidget().refreshEntries();
    }

    protected void openExportScreen()
    {
        this.initAndOpenExportOrImportScreen(new ExportEntriesListScreen<>(new ArrayList<>(this.widgetSupplier.get()),
                                                                           InfoRendererWidget::toJson));
    }

    protected void openImportScreen()
    {
        this.initAndOpenExportOrImportScreen(new ImportEntriesListScreen<>(InfoRendererWidget::createFromJson,
                                                                           this::importEntries));
    }

    protected void initAndOpenExportOrImportScreen(BaseImportExportEntriesListScreen<InfoRendererWidget> screen)
    {
        screen.setWidgetFactory(this::createImportExportEntryWidget);
        BaseScreen.openScreenWithParent(screen);
    }

    protected BaseListEntryWidget createImportExportEntryWidget(InfoRendererWidget data,
                                                                DataListEntryWidgetData constructData)
    {
        BaseInfoRendererWidgetEntryWidget widget = this.entryWidgetFactory.create(data, constructData);
        widget.setCanConfigure(false);
        widget.setCanRemove(false);
        widget.setCanToggle(false);
        return widget;
    }

    protected void importOverwriteRemoveOldWidgets()
    {
        Registry.INFO_WIDGET_MANAGER.clearWidgets();
    }

    protected void importEntries(List<InfoRendererWidget> list, AppendOverwrite mode)
    {
        if (mode == AppendOverwrite.OVERWRITE)
        {
            this.importOverwriteRemoveOldWidgets();
        }

        for (InfoRendererWidget widget : list)
        {
            Registry.INFO_WIDGET_MANAGER.addWidget(widget);
        }

        int count = list.size();

        if (count > 0)
        {
            MessageDispatcher.success("malilib.message.info.successfully_imported_n_entries", count);
        }
        else
        {
            MessageDispatcher.warning("malilib.message.warn.import_entries.didnt_import_any_entries");
        }
    }

    public interface EntryWidgetFactory
    {
        BaseInfoRendererWidgetEntryWidget create(InfoRendererWidget data, DataListEntryWidgetData constructData);
    }

    public static <WIDGET extends InfoRendererWidget> Supplier<List<WIDGET>> createSupplierFromInfoManagerForExactType(final Class<WIDGET> clazz)
    {
        return () -> Registry.INFO_WIDGET_MANAGER.getAllWidgetsOfExactType(clazz);
    }

    public static <WIDGET extends InfoRendererWidget> Supplier<List<WIDGET>> createSupplierFromInfoManagerForSubtypes(final Class<WIDGET> clazz)
    {
        return () -> Registry.INFO_WIDGET_MANAGER.getAllWidgetsExtendingType(clazz);
    }
}
