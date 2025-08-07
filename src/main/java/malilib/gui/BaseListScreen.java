package malilib.gui;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

import malilib.gui.tab.ScreenTab;
import malilib.gui.widget.list.BaseListWidget;

public abstract class BaseListScreen<LISTWIDGET extends BaseListWidget> extends BaseTabbedScreen
{
    private LISTWIDGET listWidget;
    protected int listX;
    protected int listY;
    protected int totalListMarginX;
    protected int totalListMarginY;

    protected BaseListScreen(int listX, int listY, int totalListMarginX, int totalListMarginY)
    {
        this(listX, listY, totalListMarginX, totalListMarginY, "N/A", Collections.emptyList(), null);
    }

    protected BaseListScreen(int listX, int listY, int totalListMarginX, int totalListMarginY,
                             String screenId, List<? extends ScreenTab> screenTabs, @Nullable ScreenTab defaultTab)
    {
        super(screenId, screenTabs, defaultTab);

        this.totalListMarginX = totalListMarginX;
        this.totalListMarginY = totalListMarginY;
        this.shouldCreateTabButtons = screenTabs.isEmpty() == false;

        this.addPostInitListener(() -> this.getListWidget().refreshEntries());
        this.addPreScreenCloseListener(() -> this.getListWidget().onScreenClosed());
        this.setListPosition(listX, listY);
    }

    @Override
    protected void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();
        this.addWidget(this.getListWidget());
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();
        this.getListWidget().setPositionAndSize(this.getListX(), this.getListY(), this.getListWidth(), this.getListHeight());
    }

    public LISTWIDGET getListWidget()
    {
        if (this.listWidget == null)
        {
            this.listWidget = this.createListWidget();
            this.listWidget.setTaskQueue(this::addTask);
            this.listWidget.setZ(this.z + 10);
            this.listWidget.initListWidget();
        }

        return this.listWidget;
    }

    protected abstract LISTWIDGET createListWidget();

    protected int getListX()
    {
        return this.x + this.listX;
    }

    protected int getListY()
    {
        return this.y + this.listY;
    }

    protected int getListWidth()
    {
        return this.screenWidth - this.totalListMarginX;
    }

    protected int getListHeight()
    {
        return this.screenHeight - this.totalListMarginY;
    }

    protected int getListRight()
    {
        return this.getListX() + this.getListWidth();
    }

    protected int getListBottom()
    {
        return this.getListY() + this.getListHeight();
    }

    protected void setListPosition(int listX, int listY)
    {
        this.listX = listX;
        this.listY = listY;
    }

    protected void updateListPosition(int listX, int listY)
    {
        this.setListPosition(listX, listY);
        this.getListWidget().setPositionAndSize(listX, listY, this.getListWidth(), this.getListHeight());
    }

    public boolean isSearchOpen()
    {
        return this.getListWidget().isSearchOpen();
    }

    @Override
    protected int getCurrentScrollbarPosition()
    {
        return this.getListWidget().getScrollbar().getValue();
    }

    @Override
    protected void setCurrentScrollbarPosition(int position)
    {
        this.getListWidget().setRequestedScrollBarPosition(position);
    }
}
