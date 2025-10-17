package fi.dy.masa.malilib.gui.widgets;

import fi.dy.masa.malilib.config.IConfigStringMap;
import fi.dy.masa.malilib.gui.GuiStringMapEdit;
import net.minecraft.util.Pair;

import java.util.Collection;

public class WidgetListStringMapEdit extends WidgetListConfigOptionsBase<Pair<String, String>, WidgetStringMapEditEntry> {

    protected final IConfigStringMap config;

    public WidgetListStringMapEdit(int x, int y, int width, int height, int configWidth, GuiStringMapEdit parent) {
        super(x, y, width, height, configWidth);

        this.config = parent.getConfig();
    }

    public IConfigStringMap getConfig() {
        return this.config;
    }

    @Override
    protected Collection<Pair<String, String>> getAllEntries() {
        return this.config.getMap();
    }

    @Override
    protected void reCreateListEntryWidgets() {
        if (this.listContents.isEmpty()) {
            this.listWidgets.clear();
            this.maxVisibleBrowserEntries = 1;

            int x = this.posX + 2;
            int y = this.posY + 4 + this.browserEntriesOffsetY;

            this.listWidgets.add(this.createListEntryWidget(x, y, -1, false, new Pair<>("", "")));
            this.scrollBar.setMaxValue(0);
        } else {
            super.reCreateListEntryWidgets();
        }
    }

    @Override
    protected WidgetStringMapEditEntry createListEntryWidget(int x, int y, int listIndex, boolean isOdd, Pair<String, String> entry) {
        IConfigStringMap config = this.config;

        if (listIndex >= 0 && listIndex < config.getMap().size()) {
            Pair<String, String> defaultValue = config.getDefaultMap().size() > listIndex ? config.getDefaultMap().get(listIndex) : new Pair<>("", "");

            return new WidgetStringMapEditEntry(x, y, this.browserEntryWidth, this.browserEntryHeight,
                    listIndex, isOdd, config.getMap().get(listIndex), defaultValue, this);
        } else {
            return new WidgetStringMapEditEntry(x, y, this.browserEntryWidth, this.browserEntryHeight,
                    listIndex, isOdd, new Pair<>("", ""), new Pair<>("", ""), this);
        }
    }
}
