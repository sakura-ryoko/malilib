package malilib.gui.widget.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.IntArrayList;

import malilib.config.value.SortDirection;
import malilib.gui.widget.InteractableWidget;
import malilib.gui.widget.MenuEntryWidget;
import malilib.gui.widget.list.entry.BaseListEntryWidget;
import malilib.gui.widget.list.entry.DataListEntryWidgetData;
import malilib.gui.widget.list.entry.DataListEntryWidgetFactory;
import malilib.gui.widget.list.header.DataColumn;
import malilib.gui.widget.list.header.DataListHeaderWidget;
import malilib.render.text.StyledTextLine;

public class DataListWidget<DATATYPE> extends BaseListWidget
{
    protected final Supplier<List<DATATYPE>> entrySupplier;
    protected final ArrayList<DATATYPE> fullDataList;
    protected final ArrayList<DATATYPE> filteredDataList = new ArrayList<>();
    protected final IntArrayList filteredIndices = new IntArrayList();
    protected final ArrayList<BaseListEntryWidget> entryWidgets = new ArrayList<>();
    protected final ArrayList<DataColumn<DATATYPE>> columns = new ArrayList<>();
    protected SortDirection sortDirection = SortDirection.ASCENDING;
    protected EntryFilter<DATATYPE> entryFilter;
    protected Function<DATATYPE, List<String>> entrySearchStringFunction = (e) -> Collections.singletonList(e.toString());
    @Nullable protected DataListEntrySelectionHandler<DATATYPE> selectionHandler;
    @Nullable protected Function<DataListWidget<DATATYPE>, DataListHeaderWidget<DATATYPE>> headerWidgetFactory;
    @Nullable protected Function<DataListWidget<DATATYPE>, DataListHeaderWidget<DATATYPE>> defaultHeaderWidgetFactory;
    @Nullable protected DataListEntryWidgetFactory<DATATYPE> dataListEntryWidgetFactory;
    @Nullable protected ListEntryWidgetInitializer<DATATYPE> widgetInitializer;
    @Nullable protected Supplier<List<DataColumn<DATATYPE>>> columnSupplier;
    @Nullable protected DataColumn<DATATYPE> activeSortColumn;
    @Nullable protected DataColumn<DATATYPE> defaultSortColumn;
    @Nullable protected Comparator<DATATYPE> activeListSortComparator;
    @Nullable protected Comparator<DATATYPE> defaultListSortComparator;

    protected boolean fetchFromSupplierOnRefresh;
    protected boolean filterMatchesEmptyEntry;
    protected boolean hasDataColumns;
    protected boolean shouldSortList;

    public DataListWidget(Supplier<List<DATATYPE>> entrySupplier, boolean fetchFromSupplierOnRefresh)
    {
        this(entrySupplier, null, fetchFromSupplierOnRefresh);
    }

    public DataListWidget(Supplier<List<DATATYPE>> entrySupplier,
                          @Nullable DataListEntryWidgetFactory<DATATYPE> dataListEntryWidgetFactory,
                          boolean fetchFromSupplierOnRefresh)
    {
        super(320, 320);

        this.dataListEntryWidgetFactory = dataListEntryWidgetFactory;
        this.fetchFromSupplierOnRefresh = fetchFromSupplierOnRefresh;
        this.entrySupplier = entrySupplier;
        this.fullDataList = new ArrayList<>(entrySupplier.get());
        this.selectionHandler = new DataListEntrySelectionHandler<>(this::getFilteredDataList);
        this.entryFilter = this::defaultEntryFilter;

        this.getBorderRenderer().getNormalSettings().setBorderWidth(1);
    }

    public DataListWidget<DATATYPE> setEntrySelectionHandler(@Nullable DataListEntrySelectionHandler<DATATYPE> selectionHandler)
    {
        this.selectionHandler = selectionHandler;
        return this;
    }

    public DataListWidget<DATATYPE> setHeaderWidgetFactory(@Nullable Function<DataListWidget<DATATYPE>, DataListHeaderWidget<DATATYPE>> factory)
    {
        this.headerWidgetFactory = factory;
        this.defaultHeaderWidgetFactory = factory;
        return this;
    }

    public DataListWidget<DATATYPE> setDataListEntryWidgetFactory(@Nullable DataListEntryWidgetFactory<DATATYPE> dataListEntryWidgetFactory)
    {
        this.dataListEntryWidgetFactory = dataListEntryWidgetFactory;
        return this;
    }

    public DataListWidget<DATATYPE> setWidgetInitializer(@Nullable ListEntryWidgetInitializer<DATATYPE> widgetInitializer)
    {
        this.widgetInitializer = widgetInitializer;
        return this;
    }

    /**
     * This sets the data to be re-fetched from the list supplier to the
     * currentContents list any time the refreshEntries method is called.<br><br>
     * If this is false, then the entrySupplier is only used once, in the constructor,
     * to fetch the contents to the list.<br><br>
     * The no-re-fetch case allows the currentContents list (returned by the {@link #getNonFilteredDataList()} method)
     * to be used as a backing data list for other things.
     * For example the {@link malilib.gui.widget.list.entry.BaseOrderableListEditEntryWidget}
     * uses the list returned by getCurrentEntries() to store the edited list,
     * before the changes are stored back to the actual underlying config option later on
     * in {@link malilib.gui.config.BaseValueListEditScreen#onGuiClosed()}.
     */
    public DataListWidget<DATATYPE> setFetchFromSupplierOnRefresh(boolean fetch)
    {
        this.fetchFromSupplierOnRefresh = fetch;
        return this;
    }

    public DataListWidget<DATATYPE> setFilterMatchesEmptyEntry(boolean matchesEmpty)
    {
        this.filterMatchesEmptyEntry = matchesEmpty;
        return this;
    }

    /**
     * Sets the entry filter function that is used to search for entries from the list.
     * The default filter just compares the {@link Object#toString()} value of an
     * entry to the search terms.
     */
    public DataListWidget<DATATYPE> setEntryFilter(EntryFilter<DATATYPE> filter)
    {
        this.entryFilter = filter;
        return this;
    }

    /**
     * Sets the function that outputs the strings to match the search terms against.
     * The default function is just a singleton list of {@link Object#toString()} of the entry.
     */
    public DataListWidget<DATATYPE> setEntryFilterStringFunction(Function<DATATYPE, List<String>> function)
    {
        this.entrySearchStringFunction = function;
        return this;
    }

    public void setColumnSupplier(@Nullable Supplier<List<DataColumn<DATATYPE>>> columnSupplier)
    {
        this.columnSupplier = columnSupplier;
    }

    public void setDefaultSortColumn(@Nullable DataColumn<DATATYPE> defaultSortColumn)
    {
        this.defaultSortColumn = defaultSortColumn;
    }

    public void setHasDataColumns(boolean hasDataColumns)
    {
        this.hasDataColumns = hasDataColumns;
    }

    /**
     * Sets the comparator used for sorting the list contents.
     * A header widget which wants to sort the list contents based on the column
     * that was clicked on, should use this to set the comparator at least once
     * and then update it based on the column that is clicked on, or even to
     * switch the comparator each time.
     */
    public void setListSortComparator(@Nullable Comparator<DATATYPE> comparator)
    {
        this.activeListSortComparator = comparator;
        this.defaultListSortComparator = comparator;
    }

    public DataListWidget<DATATYPE> setShouldSortList(boolean shouldSort)
    {
        this.shouldSortList = shouldSort;
        return this;
    }

    @Override
    protected void addNewEntryWidget(BaseListEntryWidget widget)
    {
        this.entryWidgets.add(widget);
        this.onSubWidgetAdded(widget);
    }

    public DataListWidget<DATATYPE> setAllowSelection(boolean allowSelection)
    {
        DataListEntrySelectionHandler<DATATYPE> handler = this.getEntrySelectionHandler();

        if (handler != null)
        {
            handler.setAllowSelection(allowSelection);
        }

        return this;
    }

    @Override
    public int getTotalListWidgetCount()
    {
        return this.getDataListSize();
    }

    public int getDataListSize()
    {
        return this.getFilteredDataList().size();
    }

    protected boolean shouldSortList()
    {
        return this.shouldSortList;
    }

    @Nullable
    public DataListEntrySelectionHandler<DATATYPE> getEntrySelectionHandler()
    {
        return this.selectionHandler;
    }

    @Override
    public ArrayList<BaseListEntryWidget> getEntryWidgetList()
    {
        return this.entryWidgets;
    }

    @Nullable
    public Comparator<DATATYPE> getComparator()
    {
        return this.activeListSortComparator;
    }

    /**
     * @return the current full list of data entries.
     * This may be different from the original list of data,
     * if this list widget/screen allows modifying the contents
     * by adding or removing entries or re-ordering them (i.e. if fetchFromSupplierOnRefresh is false).
     */
    public ArrayList<DATATYPE> getNonFilteredDataList()
    {
        return this.fullDataList;
    }

    /**
     * @return the current list of data entries that is shown,
     * meaning that any possible search/filter conditions have been
     * applied to the original list of data.
     */
    public ArrayList<DATATYPE> getFilteredDataList()
    {
        return this.filteredDataList;
    }

    @Nullable
    public DATATYPE getLastSelectedEntry()
    {
        DataListEntrySelectionHandler<DATATYPE> handler = this.getEntrySelectionHandler();
        return handler != null ? handler.getLastSelectedEntry() : null;
    }

    public List<DATATYPE> getSelectedEntries()
    {
        DataListEntrySelectionHandler<DATATYPE> handler = this.getEntrySelectionHandler();
        return handler != null ? handler.getSelectedEntries() : Collections.emptyList();
    }

    @Nullable
    public DATATYPE getKeyboardNavigationEntry()
    {
        int index = this.getKeyboardNavigationIndex();
        List<DATATYPE> list = this.getFilteredDataList();
        return index >= 0 && index < list.size() ? list.get(index) : null;
    }

    @Override
    protected void createAndSetHeaderWidget()
    {
        if (this.headerWidgetFactory != null)
        {
            this.headerWidget = this.headerWidgetFactory.apply(this);
        }
        else
        {
            this.headerWidget = null;
        }

        if (this.headerWidget != null)
        {
            this.headerWidget.setHeaderWidgetSize(this.entryWidgetWidth, -1);
        }
    }

    @Override
    public void moveSubWidgets(int diffX, int diffY)
    {
        super.moveSubWidgets(diffX, diffY);

        for (BaseListEntryWidget widget : this.entryWidgets)
        {
            widget.moveBy(diffX, diffY);
        }
    }

    @Override
    protected int getKeyboardNavigationIndex()
    {
        if (this.getEntrySelectionHandler() != null)
        {
            return this.getEntrySelectionHandler().getKeyboardNavigationIndex();
        }

        return super.getKeyboardNavigationIndex();
    }

    @Override
    public void setKeyboardNavigationIndex(int listIndex)
    {
        if (this.getEntrySelectionHandler() != null)
        {
            this.getEntrySelectionHandler().setKeyboardNavigationIndex(listIndex);
        }
        else
        {
            super.setKeyboardNavigationIndex(listIndex);
        }
    }

    @Override
    public void toggleKeyboardNavigationPositionSelection()
    {
        if (this.getEntrySelectionHandler() != null)
        {
            this.getEntrySelectionHandler().toggleKeyboardNavigationPositionSelection();
        }
    }

    @Override
    protected void updateWidgetInitializer()
    {
        if (this.widgetInitializer != null)
        {
            this.widgetInitializer.onListContentsRefreshed(this, this.entryWidgetWidth);
        }
    }

    @Override
    protected void applyWidgetInitializer()
    {
        if (this.widgetInitializer != null)
        {
            this.widgetInitializer.applyToEntryWidgets(this);
        }
    }

    @Override
    protected void fetchCurrentEntries()
    {
        if (this.fetchFromSupplierOnRefresh)
        {
            this.fetchCurrentEntriesFromSupplier();
        }
    }

    public void fetchCurrentEntriesFromSupplier()
    {
        this.fullDataList.clear();
        this.fullDataList.addAll(this.entrySupplier.get());
    }

    @Override
    protected void reAddFilteredEntries()
    {
        this.filteredDataList.clear();
        this.filteredIndices.clear();

        List<DATATYPE> entries = this.getNonFilteredDataList();

        if (this.hasFilter())
        {
            this.addFilteredContents(entries);
        }
        else
        {
            this.addNonFilteredContents(entries);
        }

        if (this.shouldSortList())
        {
            this.sortEntryList(this.filteredDataList);
        }
    }

    protected void addNonFilteredContents(List<DATATYPE> entries)
    {
        this.filteredDataList.addAll(entries);
    }

    protected void addFilteredContents(List<DATATYPE> entries)
    {
        List<String> searchTerms = this.getSearchTerms();
        final int size = entries.size();

        for (int i = 0; i < size; ++i)
        {
            DATATYPE entry = entries.get(i);

            if (this.entryMatchesFilter(entry, searchTerms))
            {
                this.filteredDataList.add(entry);
                this.filteredIndices.add(i);
            }
        }
    }

    public int getOriginalListIndexFor(int filteredListIndex)
    {
        return filteredListIndex < this.filteredIndices.size() ? this.filteredIndices.getInt(filteredListIndex) : filteredListIndex;
    }

    @Override
    @Nullable
    protected BaseListEntryWidget createListEntryWidget(int x, int y, int listIndex)
    {
        List<DATATYPE> list = this.getFilteredDataList();

        if (this.dataListEntryWidgetFactory != null && listIndex < list.size())
        {
            DATATYPE entryData = list.get(listIndex);
            int originalDataIndex = this.getOriginalListIndexFor(listIndex);
            int height = this.getHeightForListEntryWidgetCreation(listIndex);
            DataListEntryWidgetData constructData = new DataListEntryWidgetData(x, y,
                                        this.entryWidgetWidth, height, listIndex, originalDataIndex, this);

            return this.dataListEntryWidgetFactory.createWidget(entryData, constructData);
        }

        return null;
    }

    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (mouseButton == 1 && this.getEntrySelectionHandler().isNonModifierMultiSelection())
        {
            this.closeOrOpenContextMenu(mouseX, mouseY, this::getSelectionContextMenuEntries);
            return true;
        }

        return super.onMouseClicked(mouseX, mouseY, mouseButton);
    }

    protected List<MenuEntryWidget> getSelectionContextMenuEntries()
    {
        StyledTextLine text = StyledTextLine.translateFirstLine("malilib.label.list_widget.clear_selection");
        return ImmutableList.of(new MenuEntryWidget(text, this.getEntrySelectionHandler()::clearSelection));
    }

    public void updateActiveColumns()
    {
        if (this.hasDataColumns)
        {
            this.createColumns();
            this.activeSortColumn = this.defaultSortColumn;
            this.headerWidgetFactory = this.defaultHeaderWidgetFactory;
        }
        else
        {
            this.columns.clear();
            this.activeSortColumn = null;
            this.headerWidgetFactory = null;
        }

        if (this.activeSortColumn != null)
        {
            this.sortDirection = this.activeSortColumn.getDefaultSortDirection();
            this.setActiveSortComparator(this.activeSortColumn.getComparator(), this.sortDirection);
        }
        else
        {
            this.activeListSortComparator = this.defaultListSortComparator;
        }
    }

    public void updateActiveColumnsAndRefresh()
    {
        this.updateActiveColumns();
        this.initListWidget();
        this.refreshEntries();
    }

    protected void createColumns()
    {
        this.columns.clear();

        if (this.columnSupplier != null)
        {
            this.columns.addAll(this.columnSupplier.get());
        }
    }

    public Optional<SortDirection> getSortDirectionForColumn(DataColumn<DATATYPE> column)
    {
        if (this.activeSortColumn == column)
        {
            return Optional.ofNullable(this.sortDirection);
        }

        return Optional.empty();
    }

    public void toggleSortByColumn(DataColumn<DATATYPE> column)
    {
        if (column.getCanSortBy())
        {
            if (this.activeSortColumn == column)
            {
                this.sortDirection = this.sortDirection.getOpposite();
            }
            else
            {
                this.sortDirection = column.getDefaultSortDirection();
            }

            this.setActiveSortComparator(column.getComparator(), this.sortDirection);
            this.activeSortColumn = column;

            this.refreshFilteredEntries();
        }
    }

    protected void setActiveSortComparator(Optional<Comparator<DATATYPE>> optional, SortDirection direction)
    {
        if (optional.isPresent())
        {
            if (direction == SortDirection.DESCENDING)
            {
                this.activeListSortComparator = optional.get().reversed();
            }
            else
            {
                this.activeListSortComparator = optional.get();
            }
        }
    }

    protected List<String> getSearchTerms()
    {
        return Arrays.asList(this.getFilterText().split("\\|"));
    }

    protected boolean entryMatchesFilter(DATATYPE entry, List<String> searchTerms)
    {
        return searchTerms.isEmpty() || this.entryFilter.matches(entry, searchTerms);
    }

    protected boolean defaultEntryFilter(DATATYPE entry, List<String> searchTerms)
    {
        List<String> entrySearchTerms = this.entrySearchStringFunction.apply(entry);

        for (String entrySearchTerm : entrySearchTerms)
        {
            entrySearchTerm = entrySearchTerm.toLowerCase(Locale.ROOT);

            for (String searchTerm : searchTerms)
            {
                if (entrySearchTerm.contains(searchTerm))
                {
                    return true;
                }
            }
        }

        return false;
    }

    public void updateEntryWidgetStates()
    {
        for (InteractableWidget widget : this.getEntryWidgetList())
        {
            widget.updateWidgetState();
        }
    }

    protected void sortEntryList(List<DATATYPE> list)
    {
        // FIXME how to get the indices in the same order?
        Comparator<DATATYPE> comparator = this.getComparator();

        if (comparator != null)
        {
            list.sort(comparator);
        }
    }

    public void clearSelection()
    {
        if (this.getEntrySelectionHandler() != null)
        {
            this.getEntrySelectionHandler().clearSelection();
            this.getEntrySelectionHandler().notifyListener();
        }
    }

    public void notifySelectionListener()
    {
        if (this.getEntrySelectionHandler() != null)
        {
            this.getEntrySelectionHandler().notifyListener();
        }
    }

    public boolean clickEntry(int listIndex)
    {
        if (this.getEntrySelectionHandler() != null)
        {
            this.getEntrySelectionHandler().clickEntry(listIndex);
            return true;
        }

        return false;
    }

    @Override
    protected boolean onEntryWidgetClicked(BaseListEntryWidget widget, int mouseX, int mouseY, int mouseButton)
    {
        if (widget.canSelectAt(mouseX, mouseY, mouseButton))
        {
            int listIndex = widget.getDataListIndex();

            if (listIndex >= 0 && listIndex < this.getDataListSize())
            {
                return this.clickEntry(listIndex);
            }
        }

        return super.onEntryWidgetClicked(widget, mouseX, mouseY, mouseButton);
    }

    public interface EntryFilter<T>
    {
        boolean matches(T entry, List<String> searchTerms);
    }
}
