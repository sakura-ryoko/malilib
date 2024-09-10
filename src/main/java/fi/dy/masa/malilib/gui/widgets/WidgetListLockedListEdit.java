package fi.dy.masa.malilib.gui.widgets;

import java.util.Collection;

import fi.dy.masa.malilib.config.IConfigLockedList;
import fi.dy.masa.malilib.config.IConfigLockedListEntry;
import fi.dy.masa.malilib.gui.GuiLockedListEdit;

public class WidgetListLockedListEdit extends WidgetListConfigOptionsBase<String, WidgetLockedListEditEntry>
{
    protected final IConfigLockedList config;

    public WidgetListLockedListEdit(int x, int y, int width, int height, int configWidth, GuiLockedListEdit parent)
    {
        super(x, y, width, height, configWidth);

        this.config = parent.getConfig();
    }

    public IConfigLockedList getConfig()
    {
        return this.config;
    }

    @Override
    protected Collection<String> getAllEntries()
    {
        return this.config.getConfigKeys();
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
    protected WidgetLockedListEditEntry createListEntryWidget(int x, int y, int listIndex, boolean isOdd, String entry)
    {
        IConfigLockedList config = this.config;

        if (listIndex >= 0 && listIndex < config.getEntries().size())
        {
            IConfigLockedListEntry defaultValue = config.getDefaultEntries().size() > listIndex ? config.getDefaultEntries().get(listIndex) : IConfigLockedListEntry.empty();

            return new WidgetLockedListEditEntry(x, y, this.browserEntryWidth, this.browserEntryHeight,
                    listIndex, isOdd, config.getEntries().get(listIndex), defaultValue, this);
        }
        else
        {
            return new WidgetLockedListEditEntry(x, y, this.browserEntryWidth, this.browserEntryHeight,
                                                 listIndex, isOdd, IConfigLockedListEntry.empty(), IConfigLockedListEntry.empty(), this);
        }
    }
}
