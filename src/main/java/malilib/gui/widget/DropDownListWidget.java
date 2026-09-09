package malilib.gui.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;

import malilib.MaLiLibConfigs;
import malilib.gui.icon.DefaultIcons;
import malilib.gui.icon.Icon;
import malilib.gui.util.ElementOffset;
import malilib.gui.util.GuiUtils;
import malilib.gui.util.ScreenContext;
import malilib.gui.widget.button.GenericButton;
import malilib.gui.widget.list.SelectionListener;
import malilib.input.Keys;
import malilib.render.ShapeRenderUtils;
import malilib.render.text.SingleTextLineRenderer;
import malilib.render.text.StyledTextLine;
import malilib.util.data.ToBooleanFunction;

public class DropDownListWidget<T> extends ContainerWidget
{
    protected final List<T> filteredEntries = new ArrayList<>();
    protected final EntryWidgetFactory<T> entryWidgetFactory;
    @Nullable protected final Function<T, String> stringFactory;
    protected final ScrollBarWidget scrollBar;
    protected final BaseTextFieldWidget searchTextField;
    protected final GenericButton openCloseButton;
    protected final StyledTextLine searchTipText;
    protected final int lineHeight;
    protected final int maxVisibleEntries;

    protected ImmutableList<T> entries;
    protected SelectionHandler<T> selectionHandler;
    protected String multiSelectionTranslationKey = "malilib.label.misc.dropdown.multiple_entries_selected";
    @Nullable protected Supplier<List<StyledTextLine>> multiSelectionHoverTextSupplier;
    @Nullable protected SelectionListener<T> selectionListener;
    @Nullable protected InteractableWidget currentEntryBarWidget;
    protected boolean closeOnSelect = true;
    protected boolean isOpen;
    protected boolean searchOpen;
    protected boolean useCurrentEntryBar = true;
    protected float baseZLevel;
    protected int borderColorOpen = 0xFF40F0F0;
    protected int currentMaxVisibleEntries;
    protected int dropdownHeight;
    protected int textColor = 0xFFF0F0F0;
    protected int totalHeight;
    protected int keyboardSelectionIndex = -1;

    public DropDownListWidget(int height, int maxVisibleEntries, List<T> entries,
                              @Nullable Function<T, String> stringFactory)
    {
        this(height, maxVisibleEntries, entries, stringFactory, (IconWidgetFactory<T>) null);
    }

    public DropDownListWidget(int height, int maxVisibleEntries, List<T> entries,
                              @Nullable Function<T, String> stringFactory,
                              @Nullable IconWidgetFactory<T> iconWidgetFactory)
    {
        this(height, maxVisibleEntries, entries, stringFactory,
             new DefaultEntryWidgetFactory<>(stringFactory, iconWidgetFactory));
    }

    public DropDownListWidget(int height, int maxVisibleEntries, List<T> entries,
                              @Nullable Function<T, String> stringFactory,
                              EntryWidgetFactory<T> entryWidgetFactory)
    {
        super(-1, height);

        this.canReceiveMouseClicks = true;
        this.canReceiveMouseScrolls = true;
        this.blockHoverContentFromBelow = true;

        this.entries = ImmutableList.copyOf(entries);
        this.lineHeight = height;
        this.stringFactory = stringFactory;
        this.entryWidgetFactory = entryWidgetFactory;
        this.maxVisibleEntries = maxVisibleEntries;
        this.selectionHandler = new DefaultSingleEntrySelectionHandler<>();

        // The position gets updated in updateSubWidgetsToGeometryChanges
        this.scrollBar = new ScrollBarWidget(8, 100);
        this.scrollBar.setValueChangeListener(this::onScrolled);
        this.scrollBar.setBackgroundColor(0xFF202020);

        this.searchTextField = new BaseTextFieldWidget(100, 16); // The width will get updated later
        this.searchTextField.setUpdateListenerAlways(true);
        this.searchTextField.setUpdateListenerFromTextSet(true);
        this.searchTextField.setListener(this::onSearchTextChange);
        this.searchTextField.setColorFocused(0xFFFFFF20);

        this.openCloseButton = GenericButton.create((Supplier<Icon>) null, this::toggleOpen);
        this.openCloseButton.setPlayClickSound(false);

        // Raise the z-level, so it's likely to be on top of all other widgets in the same screen
        this.zLevelIncrement = 20;
        this.searchTipText = StyledTextLine.translateFirstLine("malilib.label.misc.dropdown.type_to_search");

        this.setShouldReceiveOutsideClicks(true);
        this.setHoverInfoRequiresShift(true);
        this.setWidthNoUpdate(120); // The width will get updated later
        this.updateCurrentEntryBar();
        this.updateDropDownHeight();
        this.updateFilteredEntries("");
        this.updateWidth();
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.currentEntryBarWidget);
        this.addWidgetIf(this.openCloseButton, this.useCurrentEntryBar == false);

        if (this.isOpen())
        {
            this.addWidget(this.scrollBar);

            if (this.searchOpen)
            {
                this.addWidget(this.searchTextField);
            }

            this.createEntryWidgets();
        }
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        this.scrollBar.setRight(this.getRight() - 1);
        this.scrollBar.setY(this.getDropDownY() + 1);

        // If the dropdown is above, then the search bar should be below
        if (this.shouldMoveDropDownAbove())
        {
            this.searchTextField.setY(this.getY() + this.lineHeight);
        }
        else
        {
            int y = this.getY() - this.searchTextField.getHeight();

            if (y < 0)
            {
                y = this.getDropDownY() + this.dropdownHeight;
            }

            this.searchTextField.setY(y);
        }
    }

    @Override
    public void moveSubWidgets(int diffX, int diffY)
    {
        super.moveSubWidgets(diffX, diffY);

        // If the dropdown is not open and thus the widgets are not in the widget list,
        // then the super call will not move these
        if (this.isOpen() == false)
        {
            this.scrollBar.moveBy(diffX, diffY);
            this.searchTextField.moveBy(diffX, diffY);
        }
    }

    @Override
    public int getHeight()
    {
        if (this.isOpen())
        {
            return this.totalHeight;
        }

        return this.useCurrentEntryBar ? this.lineHeight : 0;
    }

    @Override
    public void updateWidth()
    {
        if (this.automaticWidth)
        {
            int width = this.getRequiredWidth(this.entries, this.entryWidgetFactory,
                                              this.getSelectionHandler().supportsMultiSelection()) + 20;

            if (this.hasMaxWidth())
            {
                width = Math.min(width, this.maxWidth);
            }

            this.setWidth(width);
        }
    }

    @Override
    public void updateWidgetState()
    {
        this.updateWidth();
    }

    @Override
    protected void onSizeChanged()
    {
        super.onSizeChanged();
        this.updateCurrentEntryBar();
        this.searchTextField.setWidth(this.getWidth());
    }

    @Override
    public void setZLevelBasedOnParent(float parentZLevel)
    {
        super.setZLevelBasedOnParent(parentZLevel);

        this.baseZLevel = this.getZ();
    }

    protected int getDropDownY()
    {
        int y = this.getY();

        if (this.shouldMoveDropDownAbove())
        {
            y -= this.dropdownHeight;
        }
        else if (this.useCurrentEntryBar)
        {
            y += this.lineHeight;
        }

        return y;
    }

    @Override
    public boolean getShouldReceiveOutsideClicks()
    {
        return this.isOpen() && super.getShouldReceiveOutsideClicks();
    }

    @Override
    public boolean isMouseOver(int mouseX, int mouseY)
    {
        if (this.useCurrentEntryBar == false && this.isOpen() == false)
        {
            return this.openCloseButton.isMouseOver(mouseX, mouseY);
        }

        if (this.isOpen() && this.searchOpen && this.searchTextField.isMouseOver(mouseX, mouseY))
        {
            return true;
        }

        if (this.currentEntryBarWidget != null && this.currentEntryBarWidget.isMouseOver(mouseX, mouseY))
        {
            return true;
        }

        int ddY = this.getDropDownY();

        return this.isOpen() &&
               mouseX >= this.getX() && mouseX < this.getRight() &&
               mouseY >= ddY && mouseY < ddY + this.dropdownHeight;
    }

    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        // This handles the open/close button in the no-entry-bar case, plus the entry bar clicks 
        if (super.onMouseClicked(mouseX, mouseY, mouseButton))
        {
            return true;
        }

        if (this.isOpen() && mouseY >= this.getDropDownY())
        {
            this.scrollBar.onMouseClicked(mouseX, mouseY, mouseButton);
        }

        return true;
    }

    @Override
    public boolean onMouseClickedOutside(int mouseX, int mouseY, int mouseButton)
    {
        // Close the dropdown when clicking outside the widget
        if (this.isOpen())
        {
            this.setOpen(false);
            return true;
        }

        return false;
    }

    @Override
    public void onMouseReleased(int mouseX, int mouseY, int mouseButton)
    {
        this.scrollBar.onMouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    protected boolean onMouseScrolled(int mouseX, int mouseY, double verticalWheelDelta, double horizontalWheelDelta)
    {
        if (this.isOpen())
        {
            if (this.searchOpen && this.searchTextField.tryMouseScroll(mouseX, mouseY, verticalWheelDelta, horizontalWheelDelta))
            {
                return true;
            }

            int amount = verticalWheelDelta < 0.0 ? 1 : -1;
            this.scrollBar.offsetValue(amount);
            return true;
        }

        return false;
    }

    @Override
    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers)
    {
        if (this.isOpen())
        {
            if (keyCode == Keys.KEY_ESCAPE)
            {
                if (this.searchOpen && this.searchTextField.isFocused())
                {
                    this.setSearchOpen(false);
                }
                else
                {
                    this.setOpen(false);
                }

                return true;
            }
            else if (keyCode == Keys.KEY_SPACE || keyCode == Keys.KEY_ENTER)
            {
                if (this.keyboardSelectionIndex >= 0 &&
                    this.keyboardSelectionIndex < this.filteredEntries.size())
                {
                    this.onEntryClicked(this.filteredEntries.get(this.keyboardSelectionIndex));
                }

                return true;
            }
            else if (keyCode == Keys.KEY_UP ||
                     keyCode == Keys.KEY_DOWN ||
                     keyCode == Keys.KEY_PAGE_UP ||
                     keyCode == Keys.KEY_PAGE_DOWN)
            {
                int changeAmount = 0;

                if (keyCode == Keys.KEY_UP)
                    changeAmount = -1;
                else if (keyCode == Keys.KEY_DOWN)
                    changeAmount = 1;
                else if (keyCode == Keys.KEY_PAGE_UP)
                    changeAmount = -5;
                else if (keyCode == Keys.KEY_PAGE_DOWN)
                    changeAmount = 5;

                this.onKeyboardNavigationChange(changeAmount);

                return true;
            }

            return this.searchTextField.onKeyTyped(keyCode, scanCode, modifiers);
        }

        return super.onKeyTyped(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean onCharTyped(char charIn, int modifiers)
    {
        if (this.isOpen())
        {
            if (this.searchOpen == false && this.searchTextField.isUsableCharacter(charIn, modifiers))
            {
                this.setSearchOpen(true);
            }

            return this.searchTextField.onCharTyped(charIn, modifiers);
        }

        return super.onCharTyped(charIn, modifiers);
    }

    protected void onKeyboardNavigationChange(int changeAmount)
    {
        this.keyboardSelectionIndex += changeAmount;

        if (this.keyboardSelectionIndex < 0)
        {
            this.keyboardSelectionIndex = 0;
        }
        else if (this.keyboardSelectionIndex >= this.filteredEntries.size())
        {
            this.keyboardSelectionIndex = this.filteredEntries.size() - 1;
        }

        int maxEntries = this.currentMaxVisibleEntries;

        if (this.keyboardSelectionIndex < this.scrollBar.getValue())
        {
            this.scrollBar.setValue(this.keyboardSelectionIndex - maxEntries + 1);
        }
        else if (this.keyboardSelectionIndex >= this.scrollBar.getValue() + maxEntries)
        {
            this.scrollBar.setValue(this.keyboardSelectionIndex);
        }

        this.reAddSubWidgets();
        this.updateSubWidgetPositions();
    }

    public void setNoEntryBar(int buttonX, int buttonY, Supplier<Icon> iconSupplier)
    {
        this.useCurrentEntryBar = false;
        this.currentEntryBarWidget = null;
        this.openCloseButton.setButtonIconSupplier(iconSupplier);
        this.openCloseButton.setPosition(buttonX, buttonY);
        this.reAddSubWidgets();
    }

    public void setCloseOnSelect(boolean closeOnSelect)
    {
        this.closeOnSelect = closeOnSelect;
    }

    public void setSelectionListener(@Nullable SelectionListener<T> selectionListener)
    {
        this.selectionListener = selectionListener;
    }

    public SelectionHandler<T> getSelectionHandler()
    {
        return this.selectionHandler;
    }

    /**
     * Sets a custom selection handler. This can be used to support multi-selection.
     * The {@link #setCloseOnSelect(boolean)} method is probably also relevant for multi-selections.
     */
    public void setSelectionHandler(SelectionHandler<T> selectionHandler)
    {
        this.selectionHandler = selectionHandler;
        this.updateCurrentEntryBar();
    }

    /**
     * Sets the translation key used for the current selection bar, when there is more than one entry selected
     */
    public void setMultiSelectionTranslationKey(String multiSelectionTranslationKey)
    {
        this.multiSelectionTranslationKey = multiSelectionTranslationKey;
        this.updateWidth();
        this.updateCurrentEntryBar();
    }

    /**
     * Sets the hover text supplier that will be used for multi-selection dropdowns if the
     * selected entry count is larger than 1.
     */
    public void setMultiSelectionHoverTextSupplier(@Nullable Supplier<List<StyledTextLine>> multiSelectionHoverTextSupplier)
    {
        this.multiSelectionHoverTextSupplier = multiSelectionHoverTextSupplier;
        this.updateCurrentEntryBar();
    }

    public void replaceEntryList(List<T> newEntries)
    {
        this.entries = ImmutableList.copyOf(newEntries);

        this.updateWidth();
        this.updateFilteredEntries("");
        this.updateCurrentEntryBar();
    }

    protected boolean shouldMoveDropDownAbove()
    {
        int y = this.getY();
        int bottom = y + this.lineHeight;
        int spaceBelow = GuiUtils.getScaledWindowHeight() - bottom;
        int maxEntries = Math.min(this.maxVisibleEntries, this.filteredEntries.size());
        int requiredHeight = maxEntries * this.lineHeight;

        return spaceBelow < requiredHeight && y > spaceBelow;
    }

    protected int getMaxDropDownHeight()
    {
        int y = this.getY();
        int bottom = y + this.lineHeight;
        int spaceBelow = GuiUtils.getScaledWindowHeight() - bottom;

        return Math.max(y, spaceBelow);
    }

    protected void updateDropDownHeight()
    {
        int maxEntries = Math.min(this.maxVisibleEntries, this.filteredEntries.size());
        maxEntries = Math.min(maxEntries, (this.getMaxDropDownHeight() - 2) / this.lineHeight);
        this.currentMaxVisibleEntries = Math.max(1, maxEntries);
        this.dropdownHeight = this.currentMaxVisibleEntries * this.lineHeight + 2;
        this.totalHeight = this.dropdownHeight + this.lineHeight;

        this.updateScrollBarHeight();
    }

    protected void updateScrollBarHeight()
    {
        int totalHeight = this.filteredEntries.size() * this.lineHeight;
        this.scrollBar.setHeight(this.dropdownHeight - 2); // minus the border
        this.scrollBar.setTotalHeight(totalHeight);
    }

    public boolean isOpen()
    {
        return this.isOpen;
    }

    protected void toggleOpen()
    {
        if (this.isEnabled())
        {
            this.setOpen(! this.isOpen);
        }
    }

    protected void setOpen(boolean isOpen)
    {
        // Reset the index when closing or opening, so that it defaults to the currently selected entry after open
        this.keyboardSelectionIndex = -1;
        this.isOpen = isOpen && this.isEnabled();

        this.clearWidgets();
        this.setZ(this.isOpen ? this.baseZLevel + 50 : this.baseZLevel);

        if (this.isOpen)
        {
            this.updateFilteredEntries("");

            T selected = this.getSelectedEntry();

            if (selected != null)
            {
                this.scrollBar.setValueNoNotify(this.entries.indexOf(selected));
            }
        }
        else
        {
            this.searchOpen = false;
            this.searchTextField.setTextNoNotify("");
        }

        // This needs to happen before any of the widgets are re-created or the positions updated,
        // as this will update the max size of the dropdown part, and if it's located below or above
        this.updateDropDownHeight();

        this.updateCurrentEntryBar();
        this.updateSubWidgetPositions();

        // Add/remove the sub widgets as needed
        this.reAddSubWidgets();
    }

    protected void setSearchOpen(boolean isOpen)
    {
        this.searchOpen = isOpen;

        if (isOpen)
        {
            this.searchTextField.setFocused(true);
            this.reAddSubWidgets();
        }
        else
        {
            this.searchTextField.setFocused(false);
            this.searchTextField.setText("");
        }
    }

    protected void onSearchTextChange(String searchText)
    {
        if (MaLiLibConfigs.Generic.DROP_DOWN_SEARCH_TIP.getBooleanValue())
        {
            MaLiLibConfigs.Generic.DROP_DOWN_SEARCH_TIP.setValue(false);
        }

        this.updateFilteredEntries(searchText);
        this.keyboardSelectionIndex = 0;
        this.reAddSubWidgets();
    }

    protected void updateFilteredEntries(String searchText)
    {
        this.filteredEntries.clear();

        if (this.searchOpen && searchText.isEmpty() == false)
        {
            for (T entry : this.entries)
            {
                if (this.entryMatchesFilter(entry, searchText))
                {
                    this.filteredEntries.add(entry);
                }
            }

            this.scrollBar.setValue(0);
        }
        else
        {
            this.filteredEntries.addAll(this.entries);
        }

        this.scrollBar.setMaxValue(this.filteredEntries.size() - this.currentMaxVisibleEntries);
        this.updateDropDownHeight();
        this.updateSubWidgetPositions();
    }

    protected boolean entryMatchesFilter(T entry, String filterText)
    {
        return filterText.isEmpty() ||
               this.getDisplayString(entry).toLowerCase(Locale.ROOT).contains(filterText.toLowerCase(Locale.ROOT));
    }

    protected int getRequiredWidth(List<T> entriesIn,
                                   EntryWidgetFactory<T> entryFactory,
                                   boolean supportsMultiSelection)
    {
        int maxWidth = 0;

        if (supportsMultiSelection)
        {
            String key = this.multiSelectionTranslationKey;
            maxWidth = StyledTextLine.translateFirstLine(key, 99).renderWidth + 4;
        }

        for (T entry : entriesIn)
        {
            @Nullable StyledTextLine text = entryFactory.getText(entry);
            @Nullable InteractableWidget iconWidget = entryFactory.createIconWidget(entry);
            int textWidth = text != null ? text.renderWidth + 4 : 0;
            int iconWidth = iconWidget != null ? Math.min(iconWidget.getWidth(), this.lineHeight) + 4 : 0;
            int width = textWidth + iconWidth;

            maxWidth = Math.max(width, maxWidth);
        }

        return maxWidth;
    }

    protected String getDisplayString(T entry)
    {
        return getDisplayString(entry, this.stringFactory);
    }

    /**
     * @return the currently selected entry, if there is exactly one entry selected
     */
    @Nullable
    public T getSelectedEntry()
    {
        return this.selectionHandler.getSelectedEntryIfSingle();
    }

    protected void onEntryClicked(T entry)
    {
        if (this.closeOnSelect)
        {
            this.setOpen(false);
        }

        this.setSelectedEntry(entry);
    }

    public void setSelectedEntry(@Nullable T entry)
    {
        this.selectionHandler.onEntrySelected(entry);
        this.updateCurrentEntryBar();
        this.reAddSubWidgets();

        if (entry != null)
        {
            this.scrollBar.setValue(this.entries.indexOf(entry));
        }

        if (this.selectionListener != null)
        {
            this.selectionListener.onSelectionChange(entry);
        }
    }

    protected void onScrolled()
    {
        this.reAddSubWidgets();
    }

    protected void createEntryWidgets()
    {
        int startIndex = this.scrollBar.getValue();
        int endIndex = Math.min(startIndex + this.currentMaxVisibleEntries, this.filteredEntries.size());
        int borderWidth = this.getBorderRenderer().getNormalSettings().getBorderWidth() * 2;
        int width = this.getWidth() - borderWidth - this.scrollBar.getWidth();
        int height = this.lineHeight;
        int x = this.getX() + 1;
        int y = this.getDropDownY() + 1;
        int colorOdd = 0xFF303030;
        int colorEven = 0xFF404040;

        for (int i = startIndex; i < endIndex; ++i)
        {
            boolean isEven = (i & 0x1) == 0;
            T entry = this.filteredEntries.get(i);
            InteractableWidget widget = this.entryWidgetFactory.createWidget(width - 4, height, entry);

            if (this.selectionHandler.isEntrySelected(entry))
            {
                // If not set/changed yet, then initialize to the currently selected entry
                if (this.keyboardSelectionIndex == -1)
                {
                    this.keyboardSelectionIndex = i;
                }

                widget.getTextSettings().setTextColor(0xFFFFFF50);
                widget.getBorderRenderer().getNormalSettings().setBorderWidth(2);
                widget.getBorderRenderer().getNormalSettings().getColor().setTopBottom(0);
                widget.getBorderRenderer().getNormalSettings().getColor().setLeftRight(0xFF00FF90);
            }
            else if (this.keyboardSelectionIndex == i)
            {
                widget.getBorderRenderer().getNormalSettings().setBorderWidth(2);
                widget.getBorderRenderer().getNormalSettings().getColor().setTopBottom(0);
                widget.getBorderRenderer().getNormalSettings().getColor().setLeftRight(0xFFFF9000);
            }

            widget.setPosition(x + 2, y);
            widget.setClickListener(() -> this.onEntryClicked(entry));
            int bgColor = isEven ? colorEven : colorOdd;
            widget.getBackgroundRenderer().getNormalSettings().setEnabledAndColor(true, bgColor);
            widget.getBackgroundRenderer().getHoverSettings().setEnabledAndColor(true, bgColor + 0x202020);
            y += height;

            this.addWidget(widget);
        }
    }

    protected void updateCurrentEntryBar()
    {
        if (this.useCurrentEntryBar)
        {
            if (this.currentEntryBarWidget != null)
            {
                this.removeWidget(this.currentEntryBarWidget);
            }

            int count = this.selectionHandler.getSelectedEntryCount();
            T selected = this.selectionHandler.getSelectedEntryIfSingle();
            InteractableWidget widget;

            if (count == 1 && selected != null)
            {
                EntryWidget entryWidget = this.entryWidgetFactory.createWidget(this.getWidth(), this.lineHeight, selected);
                int unusableWidth = entryWidget.textOffset.getXOffset() + entryWidget.getPadding().getHorizontalTotal() + 16;
                entryWidget.getTextLineRenderer().setMaxWidth(entryWidget.getWidth() - unusableWidth);
                widget = entryWidget;
            }
            else
            {
                widget = new ContainerWidget(this.getWidth(), this.lineHeight);
                widget.getTextOffset().setYOffset(1);

                if (count > 1)
                {
                    String key = this.multiSelectionTranslationKey;
                    widget.setText(StyledTextLine.translateFirstLine(key, count));

                    if (this.multiSelectionHoverTextSupplier != null)
                    {
                        widget.getHoverInfoFactory().setTextLineProvider("entries", this.multiSelectionHoverTextSupplier);
                    }
                }
                else
                {
                    widget.setText(StyledTextLine.parseFirstLine("-"));
                }
            }

            Icon icon = this.isOpen() ? DefaultIcons.ARROW_UP : DefaultIcons.ARROW_DOWN;
            int borderColor = this.isOpen() ? this.borderColorOpen : 0xFFC0C0C0;
            widget.getBorderRenderer().getNormalSettings().setBorderWidthAndColor(1, borderColor);
            widget.getBackgroundRenderer().getNormalSettings().setEnabledAndColor(true, 0xFF000000);
            widget.setIcon(icon);
            widget.getIconOffset().setXOffset(widget.getWidth() - 15);
            widget.getIconOffset().setYOffset(1);
            widget.setClickListener(this::toggleOpen);
            widget.getHoverInfoFactory().setTextLineProvider("delegate", this.getHoverInfoFactory()::getStyledLines);
            widget.setHoverInfoRequiresShift(true);
            widget.setPosition(this.getX(), this.getY());

            this.currentEntryBarWidget = widget;
            this.addWidget(widget);
        }
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        // Render the open dropdown list
        if (this.isOpen())
        {
            int diffX = x - this.getX();
            int diffY = y - this.getY();
            int ddY = diffY + this.getDropDownY();
            int bgColor = 0xFF404040;
            int height = this.dropdownHeight;

            ShapeRenderUtils.renderOutlinedRectangle(x, ddY, z, this.getWidth(), height, bgColor, this.borderColorOpen, ctx);

            this.renderSearchTip(diffX, diffY, z, ctx);
        }

        super.renderAt(x, y, z, ctx);
    }

    protected void renderSearchTip(int diffX, int diffY, float z, ScreenContext ctx)
    {
        if (this.searchOpen == false && MaLiLibConfigs.Generic.DROP_DOWN_SEARCH_TIP.getBooleanValue())
        {
            int tx = this.searchTextField.getX() + diffX;
            int ty = this.searchTextField.getY() + diffY;
            int sw = this.searchTipText.renderWidth + 10;
            int right = tx + sw;
            int windowWidth = GuiUtils.getScaledWindowWidth();
            int searchRight = this.searchTextField.getRight();

            if (right > windowWidth)
            {
                tx = (searchRight - sw);
            }

            if (ty < 0)
            {
                ty = Math.max(this.getY() + this.lineHeight, this.getDropDownY() + this.dropdownHeight);
                ty = Math.max(ty, this.searchTextField.getBottom());
            }

            ShapeRenderUtils.renderOutlinedRectangle(tx, ty, z, sw, 16, 0xFF000000, 0xFFFFFF20, ctx);
            this.renderTextLine(tx + 4, ty + 4, z + 0.1f, 0xFFFFC000, false, this.searchTipText, ctx);
        }
    }

    public static class EntryWidget extends ContainerWidget
    {
        protected final SingleTextLineRenderer textLineRenderer;
        @Nullable protected InteractableWidget iconWidget;

        public EntryWidget(int width, int height, @Nullable InteractableWidget iconWidget)
        {
            super(width, height);

            this.textLineRenderer = new SingleTextLineRenderer(this::getTextSettings);
            this.iconWidget = iconWidget;
            this.textLineRenderer.getBorderRenderer().getHoverSettings().setBorderWidthAndColor(1, 0xFF30C0C0);
            this.textLineRenderer.getBackgroundRenderer().getHoverSettings().setEnabledAndColor(true, 0xFF000000);
            this.textLineRenderer.getPadding().setAll(3, 3, 1, 3);
        }

        @Override
        public void reAddSubWidgets()
        {
            super.reAddSubWidgets();
            this.addWidget(this.iconWidget);
        }

        @Override
        public void updateSubWidgetPositions()
        {
            super.updateSubWidgetPositions();

            if (this.iconWidget != null)
            {
                this.iconWidget.setX(this.getX() + this.getPadding().getLeft());
                this.iconWidget.centerVerticallyInside(this);
            }
        }

        public SingleTextLineRenderer getTextLineRenderer()
        {
            return this.textLineRenderer;
        }

        @Override
        public void setText(StyledTextLine text)
        {
            this.textLineRenderer.setStyledTextLine(text);
        }

        @Override
        public void renderAt(int x, int y, float z, ScreenContext ctx)
        {
            super.renderAt(x, y, z, ctx);

            int h = this.textLineRenderer.getTotalHeight();
            int tx = x + this.textOffset.getXOffset();
            int ty = y + ElementOffset.getCenteredElementOffset(this.getHeight(), h);

            boolean highlight = this.textLineRenderer.hasClampedContent() && this.isHoveredForRender(ctx);
            this.textLineRenderer.renderAt(tx, ty, z + 0.125f, highlight, ctx);
        }
    }

    public static class DefaultEntryWidgetFactory<T> implements EntryWidgetFactory<T>
    {
        @Nullable protected final Function<T, String> stringFactory;
        @Nullable protected final IconWidgetFactory<T> iconWidgetFactory;

        public DefaultEntryWidgetFactory(@Nullable Function<T, String> stringFactory, @Nullable IconWidgetFactory<T> iconWidgetFactory)
        {
            this.stringFactory = stringFactory;
            this.iconWidgetFactory = iconWidgetFactory;
        }

        @Nullable
        @Override
        public StyledTextLine getText(T data)
        {
            return this.stringFactory != null ? StyledTextLine.parseFirstLine(this.stringFactory.apply(data)) : null;
        }

        @Nullable
        @Override
        public InteractableWidget createIconWidget(T data)
        {
            return this.iconWidgetFactory != null ? this.iconWidgetFactory.create(data) : null;
        }

        @Override
        public EntryWidget createWidget(int width, int height, T entry)
        {
            InteractableWidget iconWidget = null;
            int textOffset = 0;

            if (this.iconWidgetFactory != null)
            {
                iconWidget = this.iconWidgetFactory.create(entry);
                int maxSize = height;
                iconWidget.setAutomaticWidth(false);
                iconWidget.setAutomaticHeight(false);
                iconWidget.setMaxWidth(maxSize);
                iconWidget.setMaxHeight(maxSize);
                iconWidget.updateSize();
                textOffset += Math.min(maxSize, iconWidget.getWidth()) + 4;
            }

            EntryWidget widget = new EntryWidget(width, height, iconWidget);
            widget.getPadding().setLeft(4);

            if (this.stringFactory != null)
            {
                widget.getTextOffset().setXOffset(textOffset);
                widget.getTextOffset().setYOffset(0);
                int unusableWidth = textOffset + 6;
                widget.getTextLineRenderer().setMaxWidth(width - unusableWidth);
                widget.setText(StyledTextLine.parseFirstLine(getDisplayString(entry, this.stringFactory)));
            }

            return widget;
        }
    }

    public static class DefaultSingleEntrySelectionHandler<T> implements SelectionHandler<T>
    {
        @Nullable protected T selectedEntry;

        public DefaultSingleEntrySelectionHandler()
        {
        }

        @Nullable
        @Override
        public T getSelectedEntryIfSingle()
        {
            return this.selectedEntry;
        }

        @Override
        public int getSelectedEntryCount()
        {
            return this.selectedEntry != null ? 1 : 0;
        }

        @Override
        public boolean isEntrySelected(T entry)
        {
            return this.selectedEntry == entry;
        }

        @Override
        public void onEntrySelected(T entry)
        {
            this.selectedEntry = entry;
        }
    }

    public static class DelegatingSingleEntrySelectionHandler<T> implements SelectionHandler<T>
    {
        protected final ToBooleanFunction<T> selectionChecker;
        protected final SelectionListener<T> selectionListener;
        @Nullable protected T selectedEntry;

        public DelegatingSingleEntrySelectionHandler(ToBooleanFunction<T> selectionChecker,
                                                     SelectionListener<T> selectionListener)
        {
            this.selectionChecker = selectionChecker;
            this.selectionListener = selectionListener;
        }

        @Nullable
        @Override
        public T getSelectedEntryIfSingle()
        {
            return this.selectedEntry;
        }

        @Override
        public int getSelectedEntryCount()
        {
            return 1;
        }

        @Override
        public boolean isEntrySelected(T entry)
        {
            return this.selectionChecker.applyAsBoolean(entry);
        }

        @Override
        public void onEntrySelected(T entry)
        {
            this.selectionListener.onSelectionChange(entry);

            if (this.selectionChecker.applyAsBoolean(entry))
            {
                this.selectedEntry = entry;
            }
            else
            {
                this.selectedEntry = null;
            }
        }
    }

    public static class SimpleMultiEntrySelectionHandler<T> implements SelectionHandler<T>
    {
        protected final ToBooleanFunction<T> selectionChecker;
        protected final SelectionListener<T> selectionListener;
        protected final IntSupplier selectionCountSupplier;
        @Nullable protected T selectedEntry;

        public SimpleMultiEntrySelectionHandler(ToBooleanFunction<T> selectionChecker,
                                                SelectionListener<T> selectionListener,
                                                IntSupplier selectionCountSupplier)
        {
            this.selectionChecker = selectionChecker;
            this.selectionListener = selectionListener;
            this.selectionCountSupplier = selectionCountSupplier;
        }

        @Override
        public boolean supportsMultiSelection()
        {
            return true;
        }

        @Nullable
        @Override
        public T getSelectedEntryIfSingle()
        {
            return this.getSelectedEntryCount() == 1 ? this.selectedEntry : null;
        }

        @Override
        public int getSelectedEntryCount()
        {
            return this.selectionCountSupplier.getAsInt();
        }

        @Override
        public boolean isEntrySelected(T entry)
        {
            return this.selectionChecker.applyAsBoolean(entry);
        }

        @Override
        public void onEntrySelected(T entry)
        {
            this.selectionListener.onSelectionChange(entry);

            if (this.getSelectedEntryCount() == 1)
            {
                this.selectedEntry = entry;
            }
            else
            {
                this.selectedEntry = null;
            }
        }
    }

    public interface IconWidgetFactory<T>
    {
        InteractableWidget create(T data);
    }

    public interface EntryWidgetFactory<T>
    {
        @Nullable StyledTextLine getText(T data);

        @Nullable InteractableWidget createIconWidget(T data);

        EntryWidget createWidget(int width, int height, T entry);
    }

    public interface SelectionHandler<T>
    {
        default boolean supportsMultiSelection()
        {
            return false;
        }

        int getSelectedEntryCount();

        @Nullable T getSelectedEntryIfSingle();

        boolean isEntrySelected(T entry);

        void onEntrySelected(T entry);
    }

    protected static <T> String getDisplayString(T entry, @Nullable Function<T, String> stringFactory)
    {
        if (stringFactory != null)
        {
            return stringFactory.apply(entry);
        }

        return entry.toString();
    }
}
