package fi.dy.masa.malilib.gui.widgets;

import fi.dy.masa.malilib.config.IConfigColorList;
import fi.dy.masa.malilib.gui.GuiColorListEdit;
import fi.dy.masa.malilib.util.data.Color4f;

import java.util.Collection;

public class WidgetColorListEdit extends WidgetListConfigOptionsBase<Color4f, WidgetColorListEditEntry>
{
    protected final IConfigColorList config;

    public WidgetColorListEdit(int x, int y, int width, int height, int configWidth, GuiColorListEdit parent)
    {
        super(x, y, width, height, configWidth);

        this.config = parent.getConfig();
    }

    public IConfigColorList getConfig()
    {
        return this.config;
    }

    @Override
    protected Collection<Color4f> getAllEntries()
    {
        return this.config.getColors();
    }

    @Override
    protected void reCreateListEntryWidgets()
    {
        // Add a dummy entry that allows adding the first actual string to the list
        if (this.listContents.size() == 0)
        {
            this.listWidgets.clear();
            this.maxVisibleBrowserEntries = 1;

            int x = this.posX + 2;
            int y = this.posY + 4 + this.browserEntriesOffsetY;

            this.listWidgets.add(this.createListEntryWidget(x, y, -1, false, Color4f.ZERO));
            this.scrollBar.setMaxValue(0);
        }
        else
        {
            super.reCreateListEntryWidgets();
        }
    }

    @Override
    protected WidgetColorListEditEntry createListEntryWidget(int x, int y, int listIndex, boolean isOdd, Color4f entry)
    {
        IConfigColorList config = this.config;

        if (listIndex >= 0 && listIndex < config.getColors().size())
        {
            Color4f defaultValue = config.getDefaultColors().size() > listIndex ? config.getDefaultColors().get(listIndex) : Color4f.ZERO;

            return new WidgetColorListEditEntry(x, y, this.browserEntryWidth, this.browserEntryHeight, listIndex, isOdd, config.getColors().get(listIndex), defaultValue, this);
        }
        else
        {
            return new WidgetColorListEditEntry(x, y, this.browserEntryWidth, this.browserEntryHeight, listIndex, isOdd, Color4f.ZERO, Color4f.ZERO, this);
        }
    }
}