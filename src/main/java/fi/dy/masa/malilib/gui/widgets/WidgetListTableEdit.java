package fi.dy.masa.malilib.gui.widgets;

import fi.dy.masa.malilib.config.IConfigTable;
import fi.dy.masa.malilib.config.options.ConfigTable;
import fi.dy.masa.malilib.gui.GuiTableEdit;

import java.util.Collection;

public class WidgetListTableEdit extends WidgetListConfigOptionsBase<ConfigTable.Entry, WidgetTableEditEntry> {

    protected final IConfigTable config;

    public WidgetListTableEdit(int x, int y, int width, int height, int configWidth, GuiTableEdit parent) {
        super(x, y, width, height, configWidth);

        this.config = parent.getConfig();
    }

    public IConfigTable getConfig() {
        return this.config;
    }

    @Override
    protected Collection<ConfigTable.Entry> getAllEntries() {
        return this.config.getTable();
    }

    @Override
    protected void reCreateListEntryWidgets() {
        if (this.listContents.isEmpty()) {
            this.listWidgets.clear();
            this.maxVisibleBrowserEntries = 1;

            int x = this.posX + 2;
            int y = this.posY + 4 + this.browserEntriesOffsetY;

            this.listWidgets.add(this.createListEntryWidget(x, y, -1, false, ConfigTable.getDummy(config.getTypes())));
            this.scrollBar.setMaxValue(0);
        } else {
            super.reCreateListEntryWidgets();
        }
    }

    @Override
    protected WidgetTableEditEntry createListEntryWidget(int x, int y, int listIndex, boolean isOdd, ConfigTable.Entry entry) {
        IConfigTable config = this.config;

        if (listIndex >= 0 && listIndex < config.getTable().size()) {
            ConfigTable.Entry defaultValue = listIndex < config.getDefaultTable().size() ? config.getDefaultTable().get(listIndex) : ConfigTable.getDummy(config.getTypes());

            return new WidgetTableEditEntry(x, y, this.browserEntryWidth, this.browserEntryHeight,
                    listIndex, isOdd, config.getTable().get(listIndex), defaultValue, this, config.getTypes());
        } else {
            return new WidgetTableEditEntry(x, y, this.browserEntryWidth, this.browserEntryHeight,
                    listIndex, isOdd, ConfigTable.getDummy(config.getTypes()), ConfigTable.getDummy(config.getTypes()), this, config.getTypes());
        }
    }
}
