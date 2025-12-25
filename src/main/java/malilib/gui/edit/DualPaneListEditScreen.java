package malilib.gui.edit;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;

import malilib.gui.BaseScreen;
import malilib.gui.widget.DualPaneListEditWidget;
import malilib.gui.widget.list.entry.DataListEntryWidgetFactory;

public class DualPaneListEditScreen<T> extends BaseScreen
{
    protected final DualPaneListEditWidget<T> editWidget;
    protected final Consumer<Collection<T>> resultListener;

    public DualPaneListEditScreen(int entryHeight,
                                  Collection<T> initialValues,
                                  Collection<T> allPossibleValues,
                                  Consumer<Collection<T>> resultListener,
                                  DataListEntryWidgetFactory<T> entryWidgetFactory,
                                  @Nullable Function<T, List<String>> searchStringFunction,
                                  @Nullable Comparator<T> listSortComparator)
    {
        this.resultListener = resultListener;

        this.backgroundColor = 0xE0000000;
        this.useTitleHierarchy = false;

        this.editWidget = new DualPaneListEditWidget<>(entryHeight, initialValues, allPossibleValues,
                                                       entryWidgetFactory, searchStringFunction);

        if (listSortComparator != null)
        {
            this.editWidget.setListSortComparator(listSortComparator);
        }

        this.addPostInitListener(this.editWidget::rebuildAndRefreshEntries);
        this.addPreScreenCloseListener(this::setResultToListener);
    }

    @Override
    protected void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();

        this.addWidget(this.editWidget);
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        int width = this.screenWidth - 10;
        int height = this.screenHeight - 18;
        this.editWidget.setPositionAndSize(this.x + 5, this.y + 18, width, height);
    }

    protected void setResultToListener()
    {
        this.resultListener.accept(this.editWidget.getCurrentValues());
    }
}
