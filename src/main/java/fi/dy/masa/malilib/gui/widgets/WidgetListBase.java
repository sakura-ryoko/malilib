package fi.dy.masa.malilib.gui.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiScrollBar;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.KeyCodes;

public abstract class WidgetListBase<TYPE, WIDGET extends WidgetListEntryBase<TYPE>> extends GuiBase
{
    protected final List<TYPE> listContents = new ArrayList<>();
    protected final List<WIDGET> listWidgets = new ArrayList<>();
    protected final GuiScrollBar scrollBar = new GuiScrollBar();
    protected final Set<TYPE> selectedEntries = new HashSet<>();
    protected final int posX;
    protected final int posY;
    protected int totalWidth;
    protected int totalHeight;
    protected int browserWidth;
    protected int browserHeight;
    protected int entryHeight;
    protected int browserEntriesStartX;
    protected int browserEntriesStartY;
    protected int browserEntriesOffsetY;
    protected int browserEntryWidth;
    protected int browserEntryHeight;
    protected int browserPaddingX;
    protected int browserPaddingY;
    protected int maxVisibleBrowserEntries;
    protected int lastSelectedEntryIndex = -1;
    protected int lastScrollbarPosition = -1;
    protected boolean allowKeyboardNavigation;
    protected boolean allowMultiSelection;
    protected boolean shouldSortList;
    @Nullable private TYPE lastSelectedEntry;
    @Nullable private ISelectionListener<TYPE> selectionListener;
    @Nullable protected WidgetSearchBar widgetSearchBar;

    public WidgetListBase(int x, int y, int width, int height, @Nullable ISelectionListener<TYPE> selectionListener)
    {
        this.posX = x;
        this.posY = y;
        this.selectionListener = selectionListener;
        this.browserEntryHeight = 14;

        this.setSize(width, height);
    }

    protected void setSelectionListener(ISelectionListener<TYPE> listener)
    {
        this.selectionListener = listener;
    }

    @Override
    public void initGui()
    {
        super.initGui();

        this.refreshEntries();
    }

    @Override
    public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (mouseButton == 0 && this.scrollBar.wasMouseOver())
        {
            this.scrollBar.setIsDragging(true);
            return true;
        }

        if (this.onMouseClickedSearchBar(mouseX, mouseY, mouseButton))
        {
            return true;
        }

        final int relativeY = mouseY - this.browserEntriesStartY - this.browserEntriesOffsetY;

        if (relativeY >= 0 &&
            mouseX >= this.browserEntriesStartX &&
            mouseX < this.browserEntriesStartX + this.browserEntryWidth)
        {
            for (int i = 0; i < this.listWidgets.size(); ++i)
            {
                WIDGET widget = this.listWidgets.get(i);

                if (widget.isMouseOver(mouseX, mouseY))
                {
                    if (widget.canSelectAt(mouseX, mouseY, mouseButton))
                    {
                        int entryIndex = widget.getListIndex();

                        if (entryIndex >= 0 && entryIndex < this.listContents.size())
                        {
                            this.onEntryClicked(this.listContents.get(entryIndex), entryIndex);
                        }
                    }

                    return widget.onMouseClicked(mouseX, mouseY, mouseButton);
                }
            }
        }

        return super.onMouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean onMouseReleased(int mouseX, int mouseY, int mouseButton)
    {
        if (mouseButton == 0)
        {
            this.scrollBar.setIsDragging(false);
        }

        for (int i = 0; i < this.listWidgets.size(); ++i)
        {
            this.listWidgets.get(i).onMouseReleased(mouseX, mouseY, mouseButton);
        }

        return super.onMouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean onMouseScrolled(int mouseX, int mouseY, double horizontalAmount, double verticalAmount)
    {
        if (super.onMouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount))
        {
            return true;
        }

        // The scroll event could be/should be distributed to the entry widgets here
        // It's not done (for now?) to prevent accidentally messing up stuff when scrolling over lists that have buttons

        if (mouseX >= this.posX && mouseX <= this.posX + this.browserWidth &&
            mouseY >= this.posY && mouseY <= this.posY + this.browserHeight)
        {
            this.offsetSelectionOrScrollbar(verticalAmount < 0 ? 3 : -3, false);
            return true;
        }

        return false;
    }

    protected boolean onMouseClickedSearchBar(int mouseX, int mouseY, int mouseButton)
    {
        if (this.widgetSearchBar != null)
        {
            boolean searchOpenPre = this.widgetSearchBar.isSearchOpen();

            if (this.widgetSearchBar.onMouseClickedImpl(mouseX, mouseY, mouseButton))
            {
                // Toggled the search bar on or off
                if (this.widgetSearchBar.isSearchOpen() != searchOpenPre)
                {
                    this.clearSelection();
                    this.refreshBrowserEntries();
                    this.resetScrollbarPosition();
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers)
    {
        if (this.onKeyTypedSearchBar(keyCode, scanCode, modifiers))
        {
            return true;
        }
        else if (this.allowKeyboardNavigation)
        {
                 if (keyCode == KeyCodes.KEY_UP)        this.offsetSelectionOrScrollbar(-1, true);
            else if (keyCode == KeyCodes.KEY_DOWN)      this.offsetSelectionOrScrollbar( 1, true);
            else if (keyCode == KeyCodes.KEY_PAGE_UP)   this.offsetSelectionOrScrollbar(-this.maxVisibleBrowserEntries / 2, true);
            else if (keyCode == KeyCodes.KEY_PAGE_DOWN) this.offsetSelectionOrScrollbar( this.maxVisibleBrowserEntries / 2, true);
            else if (keyCode == KeyCodes.KEY_HOME)      this.offsetSelectionOrScrollbar(-this.listContents.size(), true);
            else if (keyCode == KeyCodes.KEY_END)       this.offsetSelectionOrScrollbar( this.listContents.size(), true);
            else return false;

            return true;
        }

        return false;
    }

    @Override
    public boolean onCharTyped(char charIn, int modifiers)
    {
        if (this.onCharTypedSearchBar(charIn, modifiers))
        {
            return true;
        }

        for (WIDGET widget : this.listWidgets)
        {
            if (widget.onCharTyped(charIn, modifiers))
            {
                return true;
            }
        }

        return super.onCharTyped(charIn, modifiers);
    }

    protected boolean onKeyTypedSearchBar(int keyCode, int scanCode, int modifiers)
    {
        if (this.widgetSearchBar != null && this.widgetSearchBar.onKeyTyped(keyCode, scanCode, modifiers))
        {
            this.clearSelection();
            this.refreshBrowserEntries();
            this.resetScrollbarPosition();
            return true;
        }

        return false;
    }

    protected boolean onCharTypedSearchBar(char charIn, int modifiers)
    {
        if (this.widgetSearchBar != null && this.widgetSearchBar.onCharTyped(charIn, modifiers))
        {
            this.clearSelection();
            this.refreshBrowserEntries();
            this.resetScrollbarPosition();
            return true;
        }

        return false;
    }

    protected boolean getShouldSortList()
    {
        return this.shouldSortList;
    }

    protected boolean hasFilter()
    {
        return this.widgetSearchBar != null && this.widgetSearchBar.hasFilter();
    }

    @Nullable
    public WidgetSearchBar getSearchBarWidget()
    {
        return this.widgetSearchBar;
    }

    public List<TYPE> getCurrentEntries()
    {
        return this.listContents;
    }

    protected Collection<TYPE> getAllEntries()
    {
        return Collections.emptyList();
    }

    protected Comparator<TYPE> getComparator()
    {
        return null;
    }

    protected void refreshBrowserEntries()
    {
        this.listContents.clear();

        Collection<TYPE> entries = this.getAllEntries();

        if (this.hasFilter())
        {
            this.addFilteredContents(entries);
        }
        else
        {
            this.addNonFilteredContents(entries);
        }

        if (this.getShouldSortList())
        {
            Collections.sort(this.listContents, this.getComparator());
        }

        this.reCreateListEntryWidgets();
    }

    protected boolean filterMatchesEmptyEntry(TYPE entry)
    {
        return true;
    }

    protected String getFilterText()
    {
        return this.widgetSearchBar != null ? this.widgetSearchBar.getFilter() : "";
    }

    protected boolean entryMatchesFilter(TYPE entry, String filterText)
    {
        List<String> entryStrings = this.getEntryStringsForFilter(entry);

        if (entryStrings.isEmpty())
        {
            return this.filterMatchesEmptyEntry(entry);
        }

        return this.matchesFilter(entryStrings, filterText);
    }

    protected boolean matchesFilter(List<String> entryStrings, String filterText)
    {
        if (filterText.isEmpty())
        {
            return true;
        }

        for (String str : entryStrings)
        {
            if (this.matchesFilter(str, filterText))
            {
                return true;
            }
        }

        return false;
    }

    protected boolean matchesFilter(String entryString, String filterText)
    {
        if (filterText.isEmpty())
        {
            return true;
        }

        for (String filter : filterText.split("\\|"))
        {
            if (entryString.contains(filter))
            {
                return true;
            }
        }

        return false;
    }

    protected List<String> getEntryStringsForFilter(TYPE entry)
    {
        return Collections.emptyList();
    }

    protected void addNonFilteredContents(Collection<TYPE> placements)
    {
        this.listContents.addAll(placements);
    }

    protected void addFilteredContents(Collection<TYPE> entries)
    {
        String filterText = this.getFilterText();

        for (TYPE entry : entries)
        {
            if (filterText.isEmpty() || this.entryMatchesFilter(entry, filterText))
            {
                this.listContents.add(entry);
            }
        }
    }

    @Override
    protected boolean shouldRenderHoverStuff()
    {
        // Don't do the "is current screen" check for widgets - the container screen does that already
        return true;
    }

    @Override
    public void drawContents(DrawContext drawContext, int mouseX, int mouseY, float partialTicks)
    {
        RenderUtils.color(1f, 1f, 1f, 1f);

        if (this.widgetSearchBar != null)
        {
            this.widgetSearchBar.render(mouseX, mouseY, false, drawContext);
        }

        WidgetBase hovered = null;
        int scrollbarHeight = this.browserHeight - this.browserEntriesOffsetY - 8;
        int totalHeight = 0;

        for (TYPE listContent : this.listContents)
        {
            totalHeight += this.getBrowserEntryHeightFor(listContent);
        }

        totalHeight = Math.max(totalHeight, scrollbarHeight);

        int scrollBarX = this.posX + this.browserWidth - 9;
        int scrollBarY = this.browserEntriesStartY + this.browserEntriesOffsetY;
        this.scrollBar.render(mouseX, mouseY, partialTicks, scrollBarX, scrollBarY, 8, scrollbarHeight, totalHeight, drawContext);

        // The value gets updated in the drawScrollBar() method above, if dragging
        if (this.scrollBar.getValue() != this.lastScrollbarPosition)
        {
            this.lastScrollbarPosition = this.scrollBar.getValue();
            this.reCreateListEntryWidgets();
        }

        // Draw the currently visible directory entries
        for (WIDGET widget : this.listWidgets)
        {
            TYPE entry = widget.getEntry();
            boolean isSelected = this.allowMultiSelection ? this.selectedEntries.contains(entry) : entry != null && entry.equals(this.getLastSelectedEntry());
            widget.render(mouseX, mouseY, isSelected, drawContext);

            if (widget.isMouseOver(mouseX, mouseY))
            {
                hovered = widget;
            }
        }

        if (hovered == null && this.widgetSearchBar != null && this.widgetSearchBar.isMouseOver(mouseX, mouseY))
        {
            hovered = this.widgetSearchBar;
        }

        this.hoveredWidget = hovered;

        RenderUtils.color(1f, 1f, 1f, 1f);
    }

    public void setSize(int width, int height)
    {
        this.totalWidth = width;
        this.totalHeight = height;
        this.browserWidth = width;
        this.browserHeight = height;
        this.browserPaddingX = 3;
        this.browserPaddingY = 4;
        this.browserEntriesStartX = this.posX + this.browserPaddingX;
        this.browserEntriesStartY = this.posY + this.browserPaddingY;
        this.browserEntryWidth = this.browserWidth - 14;
    }

    protected int getBrowserEntryHeightFor(@Nullable TYPE type)
    {
        return this.browserEntryHeight;
    }

    protected void reCreateListEntryWidgets()
    {
        this.listWidgets.clear();
        this.maxVisibleBrowserEntries = 0;

        final int numEntries = this.listContents.size();
        int usableHeight = this.browserHeight - this.browserPaddingY - this.browserEntriesOffsetY;
        int usedHeight = 0;
        int x = this.posX + 2;
        int y = this.posY + 4 + this.browserEntriesOffsetY;
        int index = this.scrollBar.getValue();
        WIDGET widget = this.createHeaderWidget(x, y, index, usableHeight, usedHeight);

        if (widget != null)
        {
            this.listWidgets.add(widget);
            //this.maxVisibleBrowserEntries++;

            usedHeight += widget.getHeight();
            y += widget.getHeight();
        }

        for ( ; index < numEntries; ++index)
        {
            widget = this.createListEntryWidgetIfSpace(x, y, index, usableHeight, usedHeight);

            if (widget == null)
            {
                break;
            }

            this.listWidgets.add(widget);
            this.maxVisibleBrowserEntries++;

            usedHeight += widget.getHeight();
            y += widget.getHeight();
        }

        this.scrollBar.setMaxValue(this.listContents.size() - this.maxVisibleBrowserEntries);
    }

    @Nullable
    protected WIDGET createListEntryWidgetIfSpace(int x, int y, int listIndex, int usableHeight, int usedHeight)
    {
        TYPE entry = this.listContents.get(listIndex);
        int height = this.getBrowserEntryHeightFor(entry);

        if ((usedHeight + height) > usableHeight)
        {
            return null;
        }

        return this.createListEntryWidget(x, y, listIndex, (listIndex & 0x1) != 0, entry);
    }

    /**
     * Create a header widget, that will always be displayed as the first entry of the list.
     * If no such header should be used, then return null,
     * @param x
     * @param y
     * @param listIndexStart the listContents index of the first visible entry
     * @param usableHeight the total usable height available for the list entry widgets
     * @param usedHeight the currently used up height. Check that (usedHeight + widgetHeight) <= usableHeight before adding an entry widget.
     * @return the created header widget, or null if there is no separate header widget
     */
    @Nullable
    protected WIDGET createHeaderWidget(int x, int y, int listIndexStart, int usableHeight, int usedHeight)
    {
        return null;
    }

    public void refreshEntries()
    {
        this.refreshBrowserEntries();
    }

    protected abstract WIDGET createListEntryWidget(int x, int y, int listIndex, boolean isOdd, TYPE entry);

    @Nullable
    public TYPE getLastSelectedEntry()
    {
        return this.lastSelectedEntry;
    }

    public Set<TYPE> getSelectedEntries()
    {
        return this.selectedEntries;
    }

    protected boolean onEntryClicked(@Nullable TYPE entry, int index)
    {
        this.setLastSelectedEntry(entry, index);
        return true;
    }

    public void setLastSelectedEntry(@Nullable TYPE entry, int index)
    {
        this.lastSelectedEntry = entry;
        this.lastSelectedEntryIndex = index;

        if (this.allowMultiSelection && entry != null)
        {
            if (this.selectedEntries.contains(entry))
            {
                this.selectedEntries.remove(entry);
            }
            else
            {
                this.selectedEntries.add(entry);
            }
        }

        if (entry != null && this.selectionListener != null)
        {
            this.selectionListener.onSelectionChange(entry);
        }
    }

    public void clearSelection()
    {
        this.setLastSelectedEntry(null, -1);
    }

    public void clearAllSelections()
    {
        this.clearSelection();
        this.selectedEntries.clear();
    }

    protected void offsetSelectionOrScrollbar(int amount, boolean changeSelection)
    {
        if (changeSelection == false)
        {
            this.scrollBar.offsetValue(amount);
        }
        else if (this.lastSelectedEntryIndex >= 0 && this.listContents.size() > 0)
        {
            int index = MathHelper.clamp(this.lastSelectedEntryIndex + amount, 0, this.listContents.size() - 1);

            if (index != this.lastSelectedEntryIndex)
            {
                if (index < this.scrollBar.getValue() || index >= this.scrollBar.getValue() + this.maxVisibleBrowserEntries)
                {
                    this.scrollBar.offsetValue(index - this.lastSelectedEntryIndex);
                }

                this.setLastSelectedEntry(this.listContents.get(index), index);
            }
        }
        else
        {
            if (this.lastSelectedEntryIndex >= 0)
            {
                this.scrollBar.offsetValue(amount);
            }

            int index = this.scrollBar.getValue();

            if (index >= 0 && index < this.listContents.size())
            {
                this.setLastSelectedEntry(this.listContents.get(index), index);
            }
        }

        this.reCreateListEntryWidgets();
    }

    public void resetScrollbarPosition()
    {
        this.scrollBar.setValue(0);
    }

    public GuiScrollBar getScrollbar()
    {
        return this.scrollBar;
    }
}
