package fi.dy.masa.malilib.gui.widgets;

import java.util.Collection;
import fi.dy.masa.malilib.config.IConfigStringList;
import fi.dy.masa.malilib.gui.GuiStringListEdit;

public class WidgetListStringListEdit extends WidgetListConfigOptionsBase<String, WidgetStringListEditEntry>
{
    protected final IConfigStringList config;

    public WidgetListStringListEdit(int x, int y, int width, int height, int configWidth, GuiStringListEdit parent)
    {
        super(x, y, width, height, configWidth);

        this.config = parent.getConfig();
    }

    public IConfigStringList getConfig()
    {
        return this.config;
    }

    @Override
    protected Collection<String> getAllEntries()
    {
        return this.config.getStrings();
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

            this.listWidgets.add(this.createListEntryWidget(x, y, -1, false, ""));
            this.scrollBar.setMaxValue(0);
        }
        else
        {
            super.reCreateListEntryWidgets();
        }
    }

    @Override
    protected WidgetStringListEditEntry createListEntryWidget(int x, int y, int listIndex, boolean isOdd, String entry)
    {
        IConfigStringList config = this.config;

        if (listIndex >= 0 && listIndex < config.getStrings().size())
        {
            String defaultValue = config.getDefaultStrings().size() > listIndex ? config.getDefaultStrings().get(listIndex) : "";

            return new WidgetStringListEditEntry(x, y, this.browserEntryWidth, this.browserEntryHeight,
                    listIndex, isOdd, config.getStrings().get(listIndex), defaultValue, this);
        }
        else
        {
            return new WidgetStringListEditEntry(x, y, this.browserEntryWidth, this.browserEntryHeight,
                                                 listIndex, isOdd, "", "", this);
        }
    }
}
