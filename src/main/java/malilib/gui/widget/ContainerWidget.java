package malilib.gui.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import javax.annotation.Nullable;

import malilib.gui.BaseScreen;
import malilib.gui.util.ScreenContext;

public class ContainerWidget extends InteractableWidget
{
    protected final List<InteractableWidget> subWidgets = new ArrayList<>(4);
    @Nullable protected MenuWidget activeContextMenuWidget;

    public ContainerWidget(int width, int height)
    {
        this(0, 0, width, height);
    }

    public ContainerWidget(int x, int y, int width, int height)
    {
        super(x, y, width, height);

        this.blockHoverContentFromBelow = true;
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);

        for (InteractableWidget widget : this.subWidgets)
        {
            widget.setEnabled(enabled);
        }
    }

    public void clearWidgets()
    {
        this.subWidgets.clear();
    }

    /**
     * Adds the widget if it's not null, and then returns it
     */
    @Nullable
    public <T extends InteractableWidget> T addWidget(@Nullable T widget)
    {
        if (widget != null)
        {
            this.subWidgets.add(widget);
            this.onSubWidgetAdded(widget);
        }

        return widget;
    }

    /**
     * Only adds the widget if the condition boolean is true.
     * This is just a small convenience helper to reduce the if-statement clutter in some cases
     */
    public <T extends InteractableWidget> T addWidgetIf(T widget, boolean condition)
    {
        if (condition)
        {
            this.addWidget(widget);
        }

        return widget;
    }

    public void removeWidget(InteractableWidget widget)
    {
        this.subWidgets.remove(widget);
    }

    /**
     * This method should be overridden to add any widgets from
     * (final) fields to the ContainerWidget lists.
     * This should be called whenever the collection of (active)
     * sub widgets should change for whatever reason (such as a widget
     * becoming active or inactive and maybe covering other widgets).
     * Overriding classes should generally call super to clear the old widgets
     * and to allow the parent classes to add their sub widgets.
     */
    public void reAddSubWidgets()
    {
        this.clearWidgets();
    }

    public void onSubWidgetAdded(InteractableWidget widget)
    {
        widget.setTaskQueue(this.taskQueue);
        widget.setFocusChangeListener(this.focusChangeListener);
        widget.setZLevelBasedOnParent(this.getZ());
        widget.onWidgetAdded();
        widget.updateWidgetState();
    }

    public void openContextMenu(MenuWidget widget)
    {
        this.closeCurrentContextMenu();
        this.activeContextMenuWidget = widget;
        this.addWidget(widget);
    }

    public boolean hasContextMenuOpen()
    {
        return this.activeContextMenuWidget != null;
    }

    public void closeCurrentContextMenu()
    {
        if (this.activeContextMenuWidget != null)
        {
            this.removeWidget(this.activeContextMenuWidget);
            this.activeContextMenuWidget = null;
        }
    }

    public void createAndOpenContextMenu(int mouseX, int mouseY, List<MenuEntryWidget> entries)
    {
        MenuWidget menuWidget = new MenuWidget(mouseX + 4, mouseY);
        menuWidget.setMenuCloseHook(this::closeCurrentContextMenu);
        menuWidget.setMenuEntries(entries);

        this.openContextMenu(menuWidget);
    }

    public void closeOrOpenContextMenu(int mouseX, int mouseY, Supplier<List<MenuEntryWidget>> entrySupplier)
    {
        if (this.hasContextMenuOpen())
        {
            this.closeCurrentContextMenu();
        }
        else
        {
            this.createAndOpenContextMenu(mouseX, mouseY, entrySupplier.get());
        }
    }

    @Override
    protected void onPositionChanged(int oldX, int oldY)
    {
        int diffX = this.getX() - oldX;
        int diffY = this.getY() - oldY;

        if (diffX != 0 || diffY != 0)
        {
            this.moveSubWidgets(diffX, diffY);
        }
    }

    @Override
    protected void onSizeChanged()
    {
        this.updateSubWidgetPositions();
    }

    @Override
    protected void onPositionOrSizeChanged(int oldX, int oldY)
    {
        int diffX = this.getX() - oldX;
        int diffY = this.getY() - oldY;

        if (diffX != 0 || diffY != 0)
        {
            this.moveSubWidgets(diffX, diffY);
        }

        this.updateSubWidgetPositions();
    }

    @Override
    public void setZ(float z)
    {
        for (InteractableWidget widget : this.subWidgets)
        {
            widget.setZLevelBasedOnParent(z);
        }

        super.setZ(z);
    }

    /**
     * Moves all the sub widgets by the specified amount.
     * Used for example when the window is resized or maybe some
     * widgets are dragged around.
     * @param diffX the relative change to the x-coordinate
     * @param diffY the relative change to the y-coordinate
     */
    public void moveSubWidgets(int diffX, int diffY)
    {
        for (InteractableWidget widget : this.subWidgets)
        {
            widget.moveBy(diffX, diffY);
        }
    }

    /**
     * This method should be overridden to update any sub widget
     * positions to the current position of this container widget,
     * or to any other changes such as width or height changes.
     */
    public void updateSubWidgetPositions()
    {
        this.onContainerGeometryChanged();
    }

    @Override
    public void onWidgetAdded()
    {
        super.onWidgetAdded();
        this.reAddSubWidgets();
        this.updateSubWidgetPositions();
    }

    @Override
    public void onContainerGeometryChanged()
    {
        for (InteractableWidget widget : this.subWidgets)
        {
            widget.onContainerGeometryChanged();
        }
    }

    @Override
    public void setTaskQueue(@Nullable Consumer<Runnable> taskQueue)
    {
        super.setTaskQueue(taskQueue);

        for (InteractableWidget widget : this.subWidgets)
        {
            widget.setTaskQueue(taskQueue);
        }
    }

    @Override
    public void setFocusChangeListener(@Nullable FocusChangeListener focusChangeListener)
    {
        super.setFocusChangeListener(focusChangeListener);

        for (InteractableWidget widget : this.subWidgets)
        {
            widget.setFocusChangeListener(focusChangeListener);
        }
    }

    @Override
    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers)
    {
        for (InteractableWidget widget : this.subWidgets)
        {
            if (widget.onKeyTyped(keyCode, scanCode, modifiers))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onCharTyped(char charIn, int modifiers)
    {
        for (InteractableWidget widget : this.subWidgets)
        {
            if (widget.onCharTyped(charIn, modifiers))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public void collectMatchingWidgets(Predicate<InteractableWidget> predicate, ToIntFunction<InteractableWidget> priorityFunction, List<InteractableWidget> outputList)
    {
        super.collectMatchingWidgets(predicate, priorityFunction, outputList);
        this.subWidgets.forEach(w -> w.collectMatchingWidgets(predicate, priorityFunction, outputList));
    }

    @Override
    public List<BaseTextFieldWidget> getAllTextFields()
    {
        List<BaseTextFieldWidget> textFields = new ArrayList<>();

        for (InteractableWidget widget : this.subWidgets)
        {
            textFields.addAll(widget.getAllTextFields());
        }

        return textFields;
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        super.renderAt(x, y, z, ctx);

        this.renderSubWidgets(x, y, z, ctx);
    }

    protected void renderSubWidgets(int x, int y, float z, ScreenContext ctx)
    {
        int xOffset = x - this.getX();
        int yOffset = y - this.getY();
        float zOffset = z - this.getZ();

        for (InteractableWidget widget : this.subWidgets)
        {
            widget.renderAtOffset(xOffset, yOffset, zOffset, ctx);
        }
    }

    @Override
    public void renderDebug(boolean hovered, ScreenContext ctx)
    {
        super.renderDebug(hovered, ctx);

        BaseScreen.renderWidgetDebug(this.subWidgets, ctx);
    }
}
