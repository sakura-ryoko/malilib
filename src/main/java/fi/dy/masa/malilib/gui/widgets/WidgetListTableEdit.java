package fi.dy.masa.malilib.gui.widgets;

import fi.dy.masa.malilib.config.IConfigTable;
import fi.dy.masa.malilib.gui.GuiTableEdit;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WidgetListTableEdit extends WidgetListConfigOptionsBase<List<Object>, WidgetTableEditEntry> {

    protected final IConfigTable config;

    public WidgetListTableEdit(int x, int y, int width, int height, int configWidth, GuiTableEdit parent) {
        super(x, y, width, height, configWidth);

        this.config = parent.getConfig();
    }

    public IConfigTable getConfig() {
        return this.config;
    }

    @Override
    protected Collection<List<Object>> getAllEntries() {
        return this.config.getTable();
    }

    @Override
    protected void reCreateListEntryWidgets() {
        if (this.listContents.isEmpty()) {
            this.listWidgets.clear();
            this.maxVisibleBrowserEntries = 1;

            int x = this.posX + 2;
            int y = this.posY + 4 + this.browserEntriesOffsetY;

            this.listWidgets.add(this.createListEntryWidget(x, y, -1, false, getDummy(config)));
            this.scrollBar.setMaxValue(0);
        } else {
            super.reCreateListEntryWidgets();
        }
    }

    @Override
    protected WidgetTableEditEntry createListEntryWidget(int x, int y, int listIndex, boolean isOdd, List<Object> entry) {
        IConfigTable config = this.config;

        if (listIndex >= 0 && listIndex < config.getTable().size()) {
            List<Object> defaultValue = listIndex < config.getDefaultTable().size() ? config.getDefaultTable().get(listIndex) : getDummy(config);

            return new WidgetTableEditEntry(x, y, this.browserEntryWidth, this.browserEntryHeight,
                    listIndex, isOdd, config.getTable().get(listIndex), defaultValue, this, config.getTypes());
        } else {
            return new WidgetTableEditEntry(x, y, this.browserEntryWidth, this.browserEntryHeight,
                    listIndex, isOdd, getDummy(config), getDummy(config), this, config.getTypes());
        }
    }

    private static @NotNull List<Object> getDummy(IConfigTable config) {
        List<Object> dummy = new ArrayList<>();
        for (Class<?> type : config.getTypes()) {
            if (type == String.class) {
                dummy.add("");
            } else if (type == Integer.class) {
                dummy.add(0);
            } else if (type == Double.class) {
                dummy.add(0.0);
            } else {
                throw new IllegalStateException("Unsupported type: " + type.getName());
            }
        }
        return dummy;
    }
}
