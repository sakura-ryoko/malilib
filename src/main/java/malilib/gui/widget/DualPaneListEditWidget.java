package malilib.gui.widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;

import malilib.gui.icon.DefaultIcons;
import malilib.gui.widget.button.GenericButton;
import malilib.gui.widget.list.DataListWidget;
import malilib.gui.widget.list.entry.DataListEntryWidgetFactory;
import malilib.render.text.StyledTextLine;

public class DualPaneListEditWidget<T> extends ContainerWidget
{
    protected final ImmutableList<T> allPossibleValues;
    protected final ArrayList<T> currentValues = new ArrayList<>();
    protected final ArrayList<T> shownAvailableValues = new ArrayList<>();

    protected final DataListWidget<T> currentValuesListWidget;
    protected final DataListWidget<T> availableValuesListWidget;
    protected final GenericButton addValuesButton;
    protected final GenericButton removeValuesButton;
    protected final GenericButton removeAllButton;
    protected final LabelWidget selectedEntriesLabel;
    protected final LabelWidget availableEntriesLabel;

    public DualPaneListEditWidget(int entryHeight,
                                  Collection<T> initialValues,
                                  Collection<T> allPossibleValues,
                                  DataListEntryWidgetFactory<T> entryWidgetFactory,
                                  @Nullable Function<T, List<String>> searchStringFunction)
    {
        super(100, 100); // Temporary values

        this.currentValues.addAll(initialValues);
        this.allPossibleValues = ImmutableList.copyOf(allPossibleValues);

        this.currentValuesListWidget = new DataListWidget<>(() -> this.currentValues, true);
        this.availableValuesListWidget = new DataListWidget<>(() -> this.shownAvailableValues, true);

        this.selectedEntriesLabel = new LabelWidget();
        this.availableEntriesLabel = new LabelWidget();

        this.addValuesButton = GenericButton.create(18, DefaultIcons.MEDIUM_ARROW_LEFT, this::addValues);
        this.removeValuesButton = GenericButton.create(18, DefaultIcons.MEDIUM_ARROW_RIGHT, this::removeValues);
        this.removeAllButton = GenericButton.create(14, "litematica.button.select_entries.remove_all", this::removeAll);

        this.addValuesButton.getBorderRenderer().getNormalSettings().setBorderWidthAndColor(1, 0xFFFFFFFF);
        this.removeValuesButton.getBorderRenderer().getNormalSettings().setBorderWidthAndColor(1, 0xFFFFFFFF);
        this.addValuesButton.translateAndAddHoverString("malilib.hover.button.dual_pane_list_edit.add_entries");
        this.removeValuesButton.translateAndAddHoverString("malilib.hover.button.dual_pane_list_edit.remove_entries");

        this.currentValuesListWidget.setDataListEntryWidgetFactory(entryWidgetFactory);
        this.currentValuesListWidget.setListEntryWidgetFixedHeight(entryHeight);

        this.availableValuesListWidget.setDataListEntryWidgetFactory(entryWidgetFactory);
        this.availableValuesListWidget.setListEntryWidgetFixedHeight(entryHeight);

        this.currentValuesListWidget.getEntrySelectionHandler().setAllowSelection(true);
        this.currentValuesListWidget.getEntrySelectionHandler().setAllowMultiSelection(true);
        this.availableValuesListWidget.getEntrySelectionHandler().setAllowSelection(true);
        this.availableValuesListWidget.getEntrySelectionHandler().setAllowMultiSelection(true);

        if (searchStringFunction != null)
        {
            this.currentValuesListWidget.setEntryFilterStringFunction(searchStringFunction);
            this.availableValuesListWidget.setEntryFilterStringFunction(searchStringFunction);
            this.currentValuesListWidget.addDefaultSearchBar();
            this.availableValuesListWidget.addDefaultSearchBar();
        }

        this.updateListTitles();
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.selectedEntriesLabel);
        this.addWidget(this.availableEntriesLabel);
        this.addWidget(this.removeAllButton);

        // Add the available values list first so that it grabs keyboard input
        // first (to give search bar open priority to it)
        this.addWidget(this.availableValuesListWidget);
        this.addWidget(this.currentValuesListWidget);

        this.addWidget(this.addValuesButton);
        this.addWidget(this.removeValuesButton);
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        this.localUpdateWidgetPositions();
    }

    @Override
    protected void onPositionOrSizeChanged(int oldX, int oldY)
    {
        super.onPositionOrSizeChanged(oldX, oldY);

        this.localUpdateWidgetPositions();
    }

    protected void localUpdateWidgetPositions()
    {
        int x = this.getX() + 5;
        int y = this.getY() + 5;

        int w = (this.getWidth() - 34) / 2;
        int h = this.getHeight() - 20;

        this.selectedEntriesLabel.setPosition(x, y);

        this.currentValuesListWidget.setSize(w, h);
        this.availableValuesListWidget.setSize(w, h);

        y += 10;

        this.removeAllButton.setY(y - 15);
        this.removeAllButton.setRight(this.currentValuesListWidget.getRight());

        this.currentValuesListWidget.setPosition(x, y);

        x = this.getRight() - this.availableValuesListWidget.getWidth() - 5;
        this.availableEntriesLabel.setPosition(x, this.selectedEntriesLabel.getY());
        this.availableValuesListWidget.setPosition(x, y);

        this.addValuesButton.setPosition(this.currentValuesListWidget.getRight() + 3, y + 20);
        this.removeValuesButton.setPosition(this.currentValuesListWidget.getRight() + 3, y + 60);
    }

    public void rebuildAndRefreshEntries()
    {
        this.rebuildAvailableValues();
        this.updateListTitles();

        this.currentValuesListWidget.refreshEntries();
        this.availableValuesListWidget.refreshEntries();
    }

    protected void removeAll()
    {
        this.currentValuesListWidget.getEntrySelectionHandler().clearSelection();
        this.currentValues.clear();
        this.rebuildAndRefreshEntries();
    }

    protected void rebuildAvailableValues()
    {
        HashSet<T> currentValuesSet = new HashSet<>(this.currentValues);

        this.shownAvailableValues.clear();

        for (T val : this.allPossibleValues)
        {
            if (currentValuesSet.contains(val) == false)
            {
                this.shownAvailableValues.add(val);
            }
        }
    }

    public List<T> getCurrentValues()
    {
        return this.currentValues;
    }

    protected void addValues()
    {
        List<T> toAdd = this.availableValuesListWidget.getEntrySelectionHandler().getSelectedEntries();
        this.currentValues.addAll(toAdd);
        this.availableValuesListWidget.getEntrySelectionHandler().clearSelection();
        this.rebuildAndRefreshEntries();
    }

    protected void removeValues()
    {
        HashSet<T> toRemove = new HashSet<>(this.currentValuesListWidget.getEntrySelectionHandler().getSelectedEntries());
        this.currentValues.removeAll(toRemove);
        this.currentValuesListWidget.getEntrySelectionHandler().clearSelection();
        this.rebuildAndRefreshEntries();
    }

    protected void updateListTitles()
    {
        this.selectedEntriesLabel.setLines(StyledTextLine.translate("litematica.label.select_entries.selected",
                                                                    this.currentValues.size()));
        this.availableEntriesLabel.setLines(StyledTextLine.translate("litematica.label.select_entries.available",
                                                                     this.shownAvailableValues.size()));
    }

    public void setListSortComparator(Comparator<T> comparator)
    {
        this.currentValuesListWidget.setListSortComparator(comparator);
        //this.availableValuesListWidget.setListSortComparator(comparator);

        this.currentValuesListWidget.setShouldSortList(comparator != null);
        //this.availableValuesListWidget.setShouldSortList(comparator != null);
    }
}
