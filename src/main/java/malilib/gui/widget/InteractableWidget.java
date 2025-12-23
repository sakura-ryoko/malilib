package malilib.gui.widget;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;

import malilib.MaLiLibConfigs;
import malilib.gui.BaseScreen;
import malilib.gui.icon.Icon;
import malilib.gui.util.ScreenContext;
import malilib.listener.EventListener;
import malilib.render.TextRenderUtils;
import malilib.render.text.OrderedStringListFactory;
import malilib.render.text.StyledTextLine;
import malilib.util.StringUtils;
import malilib.util.position.Vec2i;

public abstract class InteractableWidget extends BackgroundWidget
{
    protected OrderedStringListFactory hoverInfoFactory;
    protected String hoverHelpTranslationKey = "malilib.hover.misc.hold_shift_for_info";
    @Nullable protected BooleanSupplier enabledStatusSupplier;
    @Nullable protected EventListener clickListener;
    @Nullable protected BaseWidget hoverInfoWidget;
    @Nullable protected ImmutableList<StyledTextLine> hoverHelp;
    @Nullable protected HoverChecker renderHoverChecker;
    @Nullable protected Consumer<Runnable> taskQueue;
    @Nullable protected FocusChangeListener focusChangeListener;
    protected boolean blockHoverContentFromBelow;
    protected boolean canBeFocused;
    protected boolean canReceiveMouseClicks;
    protected boolean canReceiveMouseMoves;
    protected boolean canReceiveMouseScrolls;
    protected boolean downScaleIcon;
    protected boolean enabled = true;
    protected boolean enabledLast = true;
    /** Set this to true if the widget has other hover content than text from the hoverInfoFactory.
     * Alternatively override {@link InteractableWidget#hasHoverContent()} to return true. */
    protected boolean hasHoverContent;
    protected boolean hoverInfoRequiresShift;
    protected boolean isFocused;
    protected boolean shouldReceiveOutsideClicks;
    protected boolean shouldReceiveOutsideScrolls;

    public InteractableWidget(int width, int height)
    {
        this(0, 0, width, height);
    }

    public InteractableWidget(int x, int y, int width, int height)
    {
        super(x, y, width, height);

        int maxHoverTextWidth = MaLiLibConfigs.Generic.HOVER_TEXT_MAX_WIDTH.getIntegerValue();
        this.hoverInfoFactory = new OrderedStringListFactory(maxHoverTextWidth);
    }

    /**
     * @return true if this widget can be "focused".
     *         Focusing is mainly meant for handling keyboard input priority.
     */
    public boolean canBeFocused()
    {
        return this.canBeFocused;
    }

    public boolean isFocused()
    {
        return this.isFocused && this.isEnabled();
    }

    public List<BaseTextFieldWidget> getAllTextFields()
    {
        return Collections.emptyList();
    }

    public void setTaskQueue(@Nullable Consumer<Runnable> taskQueue)
    {
        this.taskQueue = taskQueue;
    }

    public void setFocusChangeListener(@Nullable FocusChangeListener focusChangeListener)
    {
        this.focusChangeListener = focusChangeListener;
    }

    public void setRenderHoverChecker(@Nullable HoverChecker checker)
    {
        this.renderHoverChecker = checker;
    }

    public void setCanBeFocused(boolean canBeFocused)
    {
        this.canBeFocused = canBeFocused;
    }

    public void setFocused(boolean isFocused)
    {
        boolean wasFocused = this.isFocused;
        this.isFocused = isFocused && this.isEnabled();

        if (wasFocused != this.isFocused && this.focusChangeListener != null)
        {
            this.focusChangeListener.onFocusChanged(this, this.isFocused);
        }
    }

    public void setClickListener(@Nullable EventListener listener)
    {
        this.clickListener = listener;
        this.canReceiveMouseClicks |= (listener != null);
    }

    public void setShouldReceiveOutsideClicks(boolean shouldReceiveOutsideClicks)
    {
        this.shouldReceiveOutsideClicks = shouldReceiveOutsideClicks;
    }

    public void setShouldReceiveOutsideScrolls(boolean shouldReceiveOutsideScrolls)
    {
        this.shouldReceiveOutsideScrolls = shouldReceiveOutsideScrolls;
    }

    public void setHoverInfoRequiresShift(boolean requireShift)
    {
        this.hoverInfoRequiresShift = requireShift;
    }

    public boolean getShouldReceiveOutsideClicks()
    {
        return this.shouldReceiveOutsideClicks;
    }

    public boolean getShouldReceiveOutsideScrolls()
    {
        return this.shouldReceiveOutsideScrolls;
    }

    protected int getTextColorForRender(boolean hovered)
    {
        if (this.enabled == false)
        {
            return this.getTextSettings().getTextColor();
        }

        return this.getTextSettings().getEffectiveTextColor(hovered);
    }

    public void setHoverInfoWidget(@Nullable BaseWidget hoverInfoWidget)
    {
        this.hoverInfoWidget = hoverInfoWidget;
    }

    /**
     * Schedules a task to run after any widget iterations are finished, to not cause CMEs
     * if the task needs to modify the data list or the widget list in some way.
     */
    protected void scheduleTask(Runnable task)
    {
        if (this.taskQueue != null)
        {
            this.taskQueue.accept(task);
        }
    }

    /**
     * Sets a supplier that provides the enabled status for the button.
     * An existing enabled status supplier overrides the
     * enabled field's value in the isEnabled() getter method.
     */
    public void setEnabledStatusSupplier(@Nullable BooleanSupplier enabledStatusSupplier)
    {
        this.enabledStatusSupplier = enabledStatusSupplier;
    }

    protected void onEnabledStateChanged(boolean isEnabled)
    {
    }

    public void setEnabled(boolean enabled)
    {
        boolean wasEnabled = this.enabled;
        this.enabled = enabled;

        if (enabled != wasEnabled)
        {
            this.onEnabledStateChanged(enabled);
            this.enabledLast = enabled;
        }
    }

    public void toggleEnabled()
    {
        this.setEnabled(! this.isEnabled());
    }

    public boolean isEnabled()
    {
        boolean enabled = this.enabledStatusSupplier != null ? this.enabledStatusSupplier.getAsBoolean() : this.enabled;

        if (enabled != this.enabledLast)
        {
            this.onEnabledStateChanged(enabled);
            this.enabledLast = enabled;
        }

        return enabled;
    }

    /**
     * Collects all widgets passing the Predicate test to the {@code outputList}.
     * The Predicate is checked first, and if it passes, then this widget
     * will be added to the list at a position based on its priority,
     * as given by {@code priorityFunction}.
     * Widgets with a higher priority (higher int value) will be first on the list.
     */
    public void collectMatchingWidgets(Predicate<InteractableWidget> predicate,
                                       ToIntFunction<InteractableWidget> priorityFunction,
                                       List<InteractableWidget> outputList)
    {
        this.collectWidgetIfMatches(this, predicate, priorityFunction, outputList);
    }

    protected void collectWidgetIfMatches(InteractableWidget widget,
                                          Predicate<InteractableWidget> predicate,
                                          ToIntFunction<InteractableWidget> priorityFunction,
                                          List<InteractableWidget> outputList)
    {
        if (predicate.test(widget))
        {
            int size = outputList.size();
            int priority = priorityFunction.applyAsInt(widget);

            for (int i = 0; i < size; ++i)
            {
                if (priority > priorityFunction.applyAsInt(outputList.get(i)))
                {
                    outputList.add(i, widget);
                    return;
                }
            }

            outputList.add(widget);
        }
    }

    public int getTopHoveredWidgetPriority(int mouseX, int mouseY)
    {
        return (int) this.getZ();
    }

    public int getMouseClickHandlingPriority(int mouseX, int mouseY)
    {
        return (int) this.getZ();
    }

    public int getMouseScrollHandlingPriority(int mouseX, int mouseY)
    {
        return (int) this.getZ();
    }

    public boolean isMouseOver(int mouseX, int mouseY)
    {
        int x = this.getX();
        int y = this.getY();

        return mouseX >= x && mouseX < x + this.getWidth() &&
               mouseY >= y && mouseY < y + this.getHeight();
    }

    public boolean canHandleMouseClickAt(int mouseX, int mouseY)
    {
        return this.canReceiveMouseClicks &&
               (this.getShouldReceiveOutsideClicks() || this.isMouseOver(mouseX, mouseY));
    }

    public boolean canHandleMouseScrollAt(int mouseX, int mouseY)
    {
        return this.canReceiveMouseScrolls &&
               (this.getShouldReceiveOutsideScrolls() || this.isMouseOver(mouseX, mouseY));
    }

    public boolean canHandleMouseMoves()
    {
        return this.canReceiveMouseMoves;
    }

    public boolean tryMouseClick(int mouseX, int mouseY, int mouseButton)
    {
        if (this.canHandleMouseClickAt(mouseX, mouseY))
        {
            if (this.isMouseOver(mouseX, mouseY))
            {
                return this.onMouseClicked(mouseX, mouseY, mouseButton);
            }
            else if (this.getShouldReceiveOutsideClicks())
            {
                return this.onMouseClickedOutside(mouseX, mouseY, mouseButton);
            }
        }

        return false;
    }

    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.clickListener != null)
        {
            this.clickListener.onEvent();
            return true;
        }

        return false;
    }

    public boolean onMouseClickedOutside(int mouseX, int mouseY, int mouseButton)
    {
        return false;
    }

    public void onMouseReleased(int mouseX, int mouseY, int mouseButton)
    {
    }

    public boolean tryMouseScroll(int mouseX, int mouseY, double verticalWheelDelta, double horizontalWheelDelta)
    {
        if (this.canHandleMouseScrollAt(mouseX, mouseY))
        {
            return this.onMouseScrolled(mouseX, mouseY, verticalWheelDelta, horizontalWheelDelta);
        }

        return false;
    }

    protected boolean onMouseScrolled(int mouseX, int mouseY, double verticalWheelDelta, double horizontalWheelDelta)
    {
        return false;
    }

    public boolean onMouseMoved(int mouseX, int mouseY)
    {
        return false;
    }

    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers)
    {
        return false;
    }

    public boolean onCharTyped(char charIn, int modifiers)
    {
        return false;
    }

    /**
     * Returns true if this widget can be hovered (for hover info etc.) at the given point
     */
    public boolean canHoverAt(int mouseX, int mouseY)
    {
        return this.isMouseOver(mouseX, mouseY);
    }

    /**
     * @return true if hover text from widgets below this widget should not be allowed to render.
     * This is mainly meant for the ContainerWidget to block other widgets below it from rendering 
     * their hover text, in case nothing inside the container widget has any hover text.
     */
    public boolean getBlockHoverContentFromBelow()
    {
        return this.blockHoverContentFromBelow;
    }

    public boolean hasHoverContentToRender(int mouseX, int mouseY)
    {
        return this.canHoverAt(mouseX, mouseY) && this.hasHoverContent();
    }

    public boolean hasHoverContent()
    {
        return this.hasHoverContent || this.hoverInfoWidget != null || this.hasHoverText();
    }

    public boolean hasHoverText()
    {
        return this.hoverInfoFactory.hasNoStrings() == false;
    }

    public ImmutableList<StyledTextLine> getHoverHelp()
    {
        if (this.hoverHelp == null)
        {
            this.hoverHelp = StyledTextLine.translate(this.hoverHelpTranslationKey);
        }

        return this.hoverHelp;
    }

    public ImmutableList<StyledTextLine> getHoverText()
    {
        ImmutableList<StyledTextLine> lines = this.hoverInfoFactory.getStyledLines();

        if (this.hoverInfoRequiresShift &&
            BaseScreen.isShiftDown() == false &&
            lines.isEmpty() == false)
        {
            return this.getHoverHelp();
        }

        return lines;
    }

    public OrderedStringListFactory getHoverInfoFactory()
    {
        return this.hoverInfoFactory;
    }

    public void setHoverInfoFactory(OrderedStringListFactory hoverInfoFactory)
    {
        this.hoverInfoFactory = hoverInfoFactory;
    }

    public void setHoverHelpTranslationKey(String hoverHelpTranslationKey)
    {
        this.hoverHelpTranslationKey = hoverHelpTranslationKey;
        this.hoverHelp = null;
    }

    public void updateHoverStrings()
    {
        this.hoverInfoFactory.updateList();
    }

    /**
     * <b>Note:</b> The strings should be localized already.
     */
    public void addHoverStrings(String... hoverStrings)
    {
        this.hoverInfoFactory.addStrings(Arrays.asList(hoverStrings));
    }

    public void translateAndAddHoverString(String translationKey, Object... args)
    {
        this.hoverInfoFactory.addStrings(StringUtils.translate(translationKey, args));
    }

    /**
     * Adds the provided hover string supplier, by using the provided key.<br>
     * The key can be used to remove this string provider later.<br>
     * <b>Note:</b> The returned strings should be localized already.
     */
    public void setHoverStringProvider(String key, Supplier<List<String>> supplier)
    {
        this.hoverInfoFactory.setStringListProvider(key, supplier);
    }

    @Override
    public boolean isHoveredForRender(ScreenContext ctx)
    {
        if (this.renderHoverChecker != null)
        {
            return this.renderHoverChecker.isHovered(ctx);
        }

        return ctx.hoveredWidgetId == this.getId();
    }

    public boolean shouldRenderHoverInfo(ScreenContext ctx)
    {
        return this.canHoverAt(ctx.mouseX, ctx.mouseY) && ctx.getRenderDebug() == false;
    }

    protected void renderIcon(int x, int y, float z, boolean enabled, boolean hovered, ScreenContext ctx)
    {
        Icon icon = this.getIcon();

        if (icon != null)
        {
            int usableWidth = this.getWidth() - this.padding.getHorizontalTotal();
            int usableHeight = this.getHeight() - this.padding.getVerticalTotal();
            x = this.getIconPositionX(x, usableWidth, icon.getWidth());
            y = this.getIconPositionY(y, usableHeight, icon.getHeight());

            icon.renderAt(x, y, z + 0.0125f, IconWidget.getVariantIndex(enabled, hovered), ctx);
        }
    }

    protected void renderDownScaledIcon(int x, int y, float z, boolean enabled, boolean hovered, ScreenContext ctx)
    {
        Icon icon = this.getIcon();

        if (icon != null)
        {
            int width = icon.getWidth();
            int height = icon.getHeight();
            int maxSize = this.getHeight() - 2;

            if (width > maxSize || height > maxSize)
            {
                double scale = (double) maxSize / (double) Math.max(width, height);
                width = (int) Math.floor(scale * width);
                height = (int) Math.floor(scale * height);
            }

            int usableWidth = this.getWidth() - this.padding.getHorizontalTotal();
            int usableHeight = this.getHeight() - this.padding.getVerticalTotal();
            x = this.getIconPositionX(x, usableWidth, width);
            y = this.getIconPositionY(y, usableHeight, height);

            icon.renderScaledAt(x, y, z + 0.025f, width, height, IconWidget.getVariantIndex(enabled, hovered), ctx);
        }
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        this.renderWidgetBackgroundAndBorder(x, y, z, ctx);

        boolean hovered = this.isHoveredForRender(ctx);
        int color = this.getTextColorForRender(hovered);

        if (this.downScaleIcon)
        {
            this.renderDownScaledIcon(x, y, z, true, false, ctx);
        }
        else
        {
            this.renderIcon(x, y, z, true, false, ctx);
        }

        this.renderText(x, y, z, color, ctx);
    }

    public void postRenderHovered(ScreenContext ctx)
    {
        if (this.shouldRenderHoverInfo(ctx))
        {
            if (this.hoverInfoWidget != null)
            {
                renderHoverInfoWidget(this.hoverInfoWidget, this.getZ() + 100f, ctx);
            }

            if (this.hasHoverText())
            {
                TextRenderUtils.renderStyledHoverText(ctx.mouseX, ctx.mouseY, this.getZ() + 100f,
                                                      this.getHoverText(), ctx);
            }
        }
    }

    @Override
    public void renderDebug(int x, int y, float z, boolean hovered, ScreenContext ctx)
    {
        super.renderDebug(x, y, z, hovered, ctx);

        if ((hovered || ctx.getDebugRenderAll()) &&
            this.hoverInfoWidget != null)
        {
            Vec2i pos = getHoverInfoWidgetRenderPosition(this.hoverInfoWidget, ctx);
            this.hoverInfoWidget.renderDebug(pos.x, pos.y, z, true, ctx);
        }
    }

    public static void renderHoverInfoWidget(BaseWidget widget, float z, ScreenContext ctx)
    {
        Vec2i pos = getHoverInfoWidgetRenderPosition(widget, ctx);
        widget.renderAt(pos.x, pos.y, z, ctx);
    }

    public static Vec2i getHoverInfoWidgetRenderPosition(BaseWidget widget, ScreenContext ctx)
    {
        int w = widget.getWidth();
        int h = widget.getHeight();
        return TextRenderUtils.getScreenClampedHoverTextStartPosition(ctx.mouseX, ctx.mouseY, w, h);
    }

    public interface HoverChecker
    {
        boolean isHovered(ScreenContext ctx);
    }

    public interface MouseInputPriorityFunction
    {
        int getPriority(InteractableWidget widget, int mouseX, int mouseY);
    }

    public interface FocusChangeListener
    {
        void onFocusChanged(InteractableWidget widget, boolean isFocused);
    }
}
