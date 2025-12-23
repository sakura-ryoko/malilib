package malilib.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import malilib.MaLiLibConfigs;
import malilib.gui.icon.DefaultIcons;
import malilib.gui.util.GuiUtils;
import malilib.gui.util.ScreenContext;
import malilib.gui.widget.BaseTextFieldWidget;
import malilib.gui.widget.BaseWidget;
import malilib.gui.widget.InteractableWidget;
import malilib.gui.widget.button.GenericButton;
import malilib.input.ActionResult;
import malilib.input.Keys;
import malilib.render.ShapeRenderUtils;
import malilib.render.text.StyledText;
import malilib.render.text.StyledTextLine;
import malilib.render.text.TextRenderer;
import malilib.render.text.TextStyle;
import malilib.util.StringUtils;
import malilib.util.game.wrap.GameWrap;
import malilib.util.game.wrap.RenderWrap;
import malilib.util.position.Vec2i;

public abstract class BaseScreen extends GuiScreen
{
    protected final Minecraft mc = GameWrap.getClient();
    protected final TextRenderer textRenderer = TextRenderer.INSTANCE;
    protected final List<Runnable> tasks = new ArrayList<>();
    protected final List<Runnable> preInitListeners = new ArrayList<>();
    protected final List<Runnable> postInitListeners = new ArrayList<>();
    protected final List<Runnable> preScreenCloseListeners = new ArrayList<>();
    // This is private, because the same list is used for different things at different points in time: clicks vs. scrolls
    private final List<InteractableWidget> mouseActionHandlers = new ArrayList<>(32);
    private final List<InteractableWidget> widgets = new ArrayList<>();
    private String titleString = "";
    @Nullable protected StyledTextLine titleText;
    @Nullable private GuiScreen parent;
    @Nullable protected InteractableWidget focusedWidget;
    @Nullable protected InteractableWidget hoveredWidgetForContext;
    @Nullable protected InteractableWidget hoveredWidgetForHoverInfo;
    @Nullable protected ScreenContext context;
    protected GenericButton closeButton;
    protected Vec2i dragStartOffset = Vec2i.ZERO;
    protected int backgroundColor = 0xB0000000;
    protected int borderColor = 0xFF999999;
    protected int customScreenScale;
    protected int x;
    protected int y;
    protected float z;
    protected int lastMouseX = -1;
    protected int lastMouseY = -1;
    protected int screenWidth;
    protected int screenHeight;
    protected int titleColor = 0xFFFFFFFF;
    protected int titleX = 10;
    protected int titleY = 6;
    protected boolean addCloseButton = true;
    protected boolean canDragMove;
    protected boolean dragging;
    protected boolean renderBorder;
    protected boolean shouldCenter;
    protected boolean shouldRenderParent;
    protected boolean boldTitle;
    protected boolean underlineTitle;
    protected boolean useCustomScreenScaling;
    protected boolean useTitleHierarchy = true;
    /** This indicates that the screen should be automatically resized to cover the entire window.
     * Any draggable smaller popup type screens should set this to false. */
    protected boolean useWindowDimensions = true;

    public BaseScreen()
    {
        int customScale = MaLiLibConfigs.Generic.CUSTOM_SCREEN_SCALE.getIntegerValue();
        this.useCustomScreenScaling = customScale != GameWrap.getVanillaOptionsScreenScale() && customScale > 0;
        this.closeButton = GenericButton.create(DefaultIcons.CLOSE_BUTTON_9, this::closeScreenOrShowParent);
        this.closeButton.translateAndAddHoverString("malilib.hover.misc.close_screen");
        this.closeButton.setPlayClickSound(false);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.initScreen();
    }

    @Override
    public void onGuiClosed()
    {
        this.onScreenClosed();
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height)
    {
        if (this.getParent() != null)
        {
            this.getParent().setWorldAndResolution(mc, width, height);
        }

        this.onScreenResolutionSet(width, height);

        if (this.useCustomScreenScaling)
        {
            width = this.getTotalWidth();
            height = this.getTotalHeight();
        }

        super.setWorldAndResolution(mc, width, height);
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return this.getParent() != null && this.getParent().doesGuiPauseGame();
    }

    protected void initScreen()
    {
        for (Runnable listener : this.preInitListeners)
        {
            listener.run();
        }

        this.reAddActiveWidgets();
        this.updateWidgetPositions();
        this.updateTitleText();
        Keyboard.enableRepeatEvents(true);

        for (Runnable listener : this.postInitListeners)
        {
            listener.run();
        }
    }

    protected void onScreenClosed()
    {
        for (Runnable listener : this.preScreenCloseListeners)
        {
            listener.run();
        }

        Keyboard.enableRepeatEvents(false);
    }

    protected void onScreenResolutionSet(int width, int height)
    {
        this.updateCustomScreenScale();

        if (this.useCustomScreenScaling)
        {
            this.setWidthAndHeightForScale(this.customScreenScale);
        }
        // Only set the screen size if this is not a smaller (pop-up?) screen.
        else if (this.useWindowDimensions)
        {
            this.setScreenWidthAndHeight(width, height);
        }

        if (this.shouldCenter)
        {
            this.centerOnScreen();
        }
    }

    protected void updateCustomScreenScale()
    {
        int currentValue = MaLiLibConfigs.Generic.CUSTOM_SCREEN_SCALE.getIntegerValue();

        if (currentValue != this.customScreenScale)
        {
            boolean oldUseCustomScale = this.useCustomScreenScaling;
            this.useCustomScreenScaling = currentValue > 0 && currentValue != GameWrap.getVanillaOptionsScreenScale();
            this.customScreenScale = currentValue;

            if ((oldUseCustomScale || this.useCustomScreenScaling) && currentValue > 0)
            {
                this.setWidthAndHeightForScale(currentValue);
            }
        }
    }

    protected void setWidthAndHeightForScale(double scaleFactor)
    {
        int width = (int) Math.ceil((double) GuiUtils.getDisplayWidth() / scaleFactor);
        int height = (int) Math.ceil((double) GuiUtils.getDisplayHeight() / scaleFactor);

        if (this.getTotalWidth() != width || this.getTotalHeight() != height)
        {
            this.width = width;
            this.height = height;
        }

        // Only set the screen size if this is not a smaller (pop-up?) screen.
        if (this.useWindowDimensions)
        {
            this.setScreenWidthAndHeight(width, height);
        }
    }

    public void setUseWindowDimensions(boolean useWindowDimensions)
    {
        this.useWindowDimensions = useWindowDimensions;
    }

    public void setScreenWidthAndHeight(int width, int height)
    {
        this.screenWidth = width;
        this.screenHeight = height;
    }

    public void setScreenWidth(int screenWidth)
    {
        this.screenWidth = screenWidth;
    }

    public void setScreenHeight(int screenHeight)
    {
        this.screenHeight = screenHeight;
    }

    public int getTotalWidth()
    {
        return this.width;
    }

    public int getTotalHeight()
    {
        return this.height;
    }

    public void centerOnScreen()
    {
        int x;
        int y;
        GuiScreen parent = this.getParent();
        GuiScreen current = GuiUtils.getCurrentScreen();

        if (parent instanceof BaseScreen)
        {
            BaseScreen parentBaseScreen = (BaseScreen) parent;
            x = parentBaseScreen.x + parentBaseScreen.getScreenWidth() / 2;
            y = parentBaseScreen.y + parentBaseScreen.getScreenHeight() / 2;
        }
        else if (parent != null)
        {
            x = parent.width / 2;
            y = parent.height / 2;
        }
        else if (current != null)
        {
            x = current.width / 2;
            y = current.height / 2;
        }
        else
        {
            x = GuiUtils.getScaledWindowWidth() / 2;
            y = GuiUtils.getScaledWindowHeight() / 2;
        }

        x -= this.getScreenWidth() / 2;
        y -= this.getScreenHeight() / 2;

        this.setPosition(x, y);
    }

    protected void reAddActiveWidgets()
    {
        this.clearElements();
        this.addScreenCloseButtonIfEnabled();
    }

    protected void addScreenCloseButtonIfEnabled()
    {
        if (this.addCloseButton && this.closeButton != null)
        {
            this.addWidget(this.closeButton);
        }
    }

    protected void updateWidgetPositions()
    {
        if (this.closeButton != null)
        {
            int x = this.getRight() - this.closeButton.getWidth() - 2;
            this.closeButton.setPosition(x, this.y + 2);
        }
    }

    public int getX()
    {
        return this.x;
    }

    public int getY()
    {
        return this.y;
    }

    public int getRight()
    {
        return this.x + this.screenWidth;
    }

    public int getBottom()
    {
        return this.y + this.screenHeight;
    }

    public int getScreenWidth()
    {
        return this.screenWidth;
    }

    public int getScreenHeight()
    {
        return this.screenHeight;
    }

    public void setPosition(int x, int y)
    {
        int oldX = this.x;
        int oldY = this.y;

        this.x = x;
        this.y = y;

        if (this.x != oldX || this.y != oldY)
        {
            this.updateWidgetPositions();
        }
    }

    @Nullable
    public GuiScreen getParent()
    {
        return this.parent;
    }

    public String getTitleString()
    {
        if (this.useTitleHierarchy && this.parent instanceof BaseScreen)
        {
            return ((BaseScreen) this.parent).getTitleString() + " => " + this.titleString;
        }

        return this.titleString;
    }

    public void setTitle(@Nullable String titleKey, Object... args)
    {
        if (org.apache.commons.lang3.StringUtils.isBlank(titleKey))
        {
            this.titleString = "";
            this.titleText = null;
        }
        else
        {
            this.titleString = StringUtils.translate(titleKey, args);
            this.updateTitleText();
        }
    }

    public void updateTitleText()
    {
        this.titleText = this.buildTitle();
    }

    protected StyledTextLine buildTitle()
    {
        if (this.underlineTitle || this.boldTitle)
        {
            TextStyle style = TextStyle.builder().fromStyle(TextStyle.DEFAULT).withUnderline(this.underlineTitle).withBold(this.boldTitle).build();
            return StyledTextLine.parseFirstLine(this.getTitleString(), style);
        }
        else
        {
            return StyledTextLine.parseFirstLine(this.getTitleString());
        }
    }

    public BaseScreen setParent(@Nullable GuiScreen parent)
    {
        // Don't allow nesting the GUI with itself...
        if (parent != this)
        {
            this.parent = parent;
        }

        return this;
    }

    public void addPreInitListener(Runnable listener)
    {
        this.preInitListeners.add(listener);
    }

    public void addPostInitListener(Runnable listener)
    {
        this.postInitListeners.add(listener);
    }

    public void addPreScreenCloseListener(Runnable listener)
    {
        this.preScreenCloseListeners.add(listener);
    }

    public void setRenderBorder(boolean renderBorder)
    {
        this.renderBorder = renderBorder;
    }

    public void setShouldRenderParent(boolean render)
    {
        this.shouldRenderParent = render;
    }

    public void setCanDragMove(boolean canDragMove)
    {
        this.canDragMove = canDragMove;
    }

    protected int getPopupGuiZLevelIncrement()
    {
        return 50;
    }

    /**
     * Shows the parent screen (if one is set), unless the shift key is held,
     * in which case the screen is closed entirely.
     */
    protected void closeScreenOrShowParent()
    {
        if (isShiftDown())
        {
            this.closeScreen();
        }
        else
        {
            this.openParentScreen();
        }
    }

    protected void closeScreen()
    {
        openScreen(null);
    }

    protected void openParentScreen()
    {
        openScreen(this.getParent());
    }

    public boolean isActiveScreen()
    {
        return GuiUtils.getCurrentScreen() == this;
    }

    public ScreenContext getContext()
    {
        int mouseX = GuiUtils.getMouseScreenX(this.getTotalWidth());
        int mouseY = GuiUtils.getMouseScreenY(this.getTotalHeight());
        boolean isActiveScreen = this.isActiveScreen();

        InteractableWidget topWidget = isActiveScreen ? this.hoveredWidgetForContext : null;
        int hoveredWidgetId = topWidget != null ? topWidget.getId() : -1;

        if (this.context == null ||
            this.context.matches(mouseX, mouseY, isActiveScreen, hoveredWidgetId) == false)
        {
            this.context = new ScreenContext(mouseX, mouseY, hoveredWidgetId, isActiveScreen);
        }

        return this.context;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.runTasks();

        if (this.shouldRenderParent && this.getParent() != null)
        {
            this.getParent().drawScreen(mouseX, mouseY, partialTicks);
        }

        RenderWrap.color(1f, 1f, 1f, 1f);
        RenderWrap.setupBlendSeparate();
        ScreenContext ctx = this.getContext();

        // These are after the parent rendering, because the parent
        // can/will also both enable and disable the custom scale,
        // so it needs to be enabled here again in any case after
        // rendering the parent screen.
        if (this.useCustomScreenScaling)
        {
            RenderWrap.setupScaledScreenRendering(this.customScreenScale, ctx);
        }

        this.renderScreenBackground(ctx);
        this.renderScreenTitle(ctx);

        // Draw base widgets
        this.renderWidgets(ctx);
        this.renderCustomContents(ctx);
        this.renderHoveredWidget(ctx);

        if (ctx.getRenderDebug())
        {
            this.renderDebug(ctx);
        }

        BaseWidget.renderDebugTextAndClear(ctx);

        if (this.useCustomScreenScaling)
        {
            RenderWrap.setupScaledScreenRendering(GuiUtils.getVanillaScreenScale(), ctx);
        }
    }

    protected void updateTopHoveredWidgetForHoverInfo(int mouseX, int mouseY)
    {
        Predicate<InteractableWidget> predicate = w -> w.hasHoverContentToRender(mouseX, mouseY) ||
                                                       (w.getBlockHoverContentFromBelow() &&
                                                        w.hasHoverContent() == false &&
                                                        w.isMouseOver(mouseX, mouseY));
        ToIntFunction<InteractableWidget> priorityFunction = w -> (int) w.getZ();
        this.hoveredWidgetForHoverInfo = this.getTopHoveredWidget(predicate, priorityFunction);
    }

    protected void updateTopHoveredWidgetForContext(int mouseX, int mouseY)
    {
        Predicate<InteractableWidget> predicate = w -> w.isMouseOver(mouseX, mouseY);
        ToIntFunction<InteractableWidget> priorityFunction = w -> w.getTopHoveredWidgetPriority(mouseX, mouseY);
        this.hoveredWidgetForContext = this.getTopHoveredWidget(predicate, priorityFunction);
    }

    @Nullable
    protected InteractableWidget getTopHoveredWidget(Predicate<InteractableWidget> predicate,
                                                     ToIntFunction<InteractableWidget> priorityFunction)
    {
        if (this.isActiveScreen())
        {
            ArrayList<InteractableWidget> list = new ArrayList<>();
            this.widgets.forEach(w -> w.collectMatchingWidgets(predicate, priorityFunction, list));
            return list.isEmpty() ? null : list.get(0);
        }

        return null;
    }

    private void updateMouseActionHandlers(Predicate<InteractableWidget> predicate,
                                           ToIntFunction<InteractableWidget> priorityFunction)
    {
        this.mouseActionHandlers.clear();

        if (this.isActiveScreen())
        {
            this.widgets.forEach(w -> w.collectMatchingWidgets(predicate, priorityFunction, this.mouseActionHandlers));
        }
    }

    protected void updateMouseHandlersForClickOrRelease(int mouseX, int mouseY)
    {
        /*
         * By default, the priority is directly the widget's z-position.
         * But some widgets like open dropdown widgets might want to have a higher
         * priority, so that a click outside an open dropdown widget will close it
         * and not do anything else, even if the click is on top of another widget
         * that would normally have a higher priority by its z-position.
         */
        ToIntFunction<InteractableWidget> priorityFunction = w -> w.getMouseClickHandlingPriority(mouseX, mouseY);
        this.updateMouseActionHandlers(w -> w.canHandleMouseClickAt(mouseX, mouseY), priorityFunction);
    }

    protected void updateMouseHandlersForScroll(int mouseX, int mouseY)
    {
        ToIntFunction<InteractableWidget> priorityFunction = w -> w.getMouseScrollHandlingPriority(mouseX, mouseY);
        this.updateMouseActionHandlers(w -> w.canHandleMouseScrollAt(mouseX, mouseY), priorityFunction);
    }

    protected void updateMouseHandlersForMove()
    {
        ToIntFunction<InteractableWidget> priorityFunction = w -> (int) w.getZ();
        this.updateMouseActionHandlers(InteractableWidget::canHandleMouseMoves, priorityFunction);
    }

    @Override
    public void handleMouseInput()
    {
        int mouseX = GuiUtils.getMouseScreenX(this.getTotalWidth());
        int mouseY = GuiUtils.getMouseScreenY(this.getTotalHeight());
        int mouseWheelDelta = Mouse.getEventDWheel();
        int mouseButton = Mouse.getEventButton();
        boolean buttonState = Mouse.getEventButtonState();

        if (mouseX != this.lastMouseX || mouseY != this.lastMouseY)
        {
            this.onMouseMoved(mouseX, mouseY);
            this.lastMouseX = mouseX;
            this.lastMouseY = mouseY;
        }

        if (buttonState)
        {
            this.onMouseClicked(mouseX, mouseY, mouseButton);
        }
        else if (mouseButton != -1)
        {
            this.onMouseReleased(mouseX, mouseY, mouseButton);
        }

        if (mouseWheelDelta != 0)
        {
            this.onMouseScrolled(mouseX, mouseY, mouseWheelDelta, 0.0);
        }

        // Update after the input is handled
        this.updateTopHoveredWidgetForHoverInfo(mouseX, mouseY);
        this.updateTopHoveredWidgetForContext(mouseX, mouseY);
    }

    public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        this.checkForFocusedWidgetUnFocus(mouseX, mouseY, mouseButton);
        this.updateMouseHandlersForClickOrRelease(mouseX, mouseY);

        for (InteractableWidget widget : this.mouseActionHandlers)
        {
            if (widget.tryMouseClick(mouseX, mouseY, mouseButton))
            {
                return true;
            }
        }

        if (this.canDragMove && this.isMouseOver(mouseX, mouseY))
        {
            this.dragStartOffset = new Vec2i(mouseX - this.getX(),  mouseY - this.getY());
            this.dragging = true;
            return true;
        }

        return false;
    }

    public boolean onMouseReleased(int mouseX, int mouseY, int mouseButton)
    {
        this.updateMouseHandlersForClickOrRelease(mouseX, mouseY);
        this.dragging = false;

        // Call this for all widgets, not only for the hovered or "receives outside clicks" widgets - outdated comment?
        for (InteractableWidget widget : this.mouseActionHandlers)
        {
            widget.onMouseReleased(mouseX, mouseY, mouseButton);
        }

        return false;
    }

    public boolean onMouseScrolled(int mouseX, int mouseY, double verticalWheelDelta, double horizontalWheelDelta)
    {
        this.updateMouseHandlersForScroll(mouseX, mouseY);

        for (InteractableWidget widget : this.mouseActionHandlers)
        {
            if (widget.tryMouseScroll(mouseX, mouseY, verticalWheelDelta, horizontalWheelDelta))
            {
                return true;
            }
        }

        return false;
    }

    public boolean onMouseMoved(int mouseX, int mouseY)
    {
        if (this.dragging)
        {
            int x = mouseX - this.dragStartOffset.x;
            int y = mouseY - this.dragStartOffset.y;

            this.setPosition(x, y);

            return true;
        }

        this.updateMouseHandlersForMove();

        for (InteractableWidget widget : this.mouseActionHandlers)
        {
            if (widget.onMouseMoved(mouseX, mouseY))
            {
                return true;
            }
        }

        return false;
    }

    public boolean isMouseOver(int mouseX, int mouseY)
    {
        return mouseX >= this.x && mouseX <= this.x + this.screenWidth &&
               mouseY >= this.y && mouseY <= this.y + this.screenHeight;
    }

    @Override
    public void handleKeyboardInput()
    {
        if (Keyboard.getEventKeyState() == false &&
            this.onKeyReleased(Keyboard.getEventKey(), 0, 0))
        {
            return;
        }

        super.handleKeyboardInput();

        // Update after the input is handled
        //this.updateTopHoveredWidgetForHoverInfo(this.lastMouseX, this.lastMouseY);
        //this.updateTopHoveredWidgetForContext(this.lastMouseX, this.lastMouseY);
    }

    @Override
    protected void keyTyped(char charIn, int keyCode)
    {
        if (keyCode == 0 && charIn >= ' ')
        {
            keyCode = (int) charIn + 256;
        }

        this.onKeyTyped(keyCode, 0, 0);

        if (charIn >= ' ')
        {
            this.onCharTyped(charIn, 0);
        }
    }

    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == Keys.KEY_TAB &&
            GuiUtils.changeTextFieldFocus(this.getAllTextFields(), isShiftDown()))
        {
            return true;
        }

        // The focused widget has priority
        if (this.focusedWidget != null && this.focusedWidget.onKeyTyped(keyCode, scanCode, modifiers))
        {
            return true;
        }

        for (InteractableWidget widget : this.widgets)
        {
            if (widget.onKeyTyped(keyCode, scanCode, modifiers))
            {
                // Don't call super if the button press got handled
                return true;
            }
        }

        if (MaLiLibConfigs.Hotkeys.SCREEN_GO_BACK.getKeyBind().wasTriggered() && this.getParent() != null)
        {
            this.openParentScreen();
            return true;
        }

        if (keyCode == Keys.KEY_ESCAPE)
        {
            this.closeScreenOrShowParent();
            return true;
        }

        return false;
    }

    public boolean onKeyReleased(int keyCode, int scanCode, int modifiers)
    {
        return false;
    }

    public boolean onCharTyped(char charIn, int modifiers)
    {
        // The focused widget has priority
        if (this.focusedWidget != null && this.focusedWidget.onCharTyped(charIn, modifiers))
        {
            return true;
        }

        for (InteractableWidget widget : this.widgets)
        {
            if (widget.onCharTyped(charIn, modifiers))
            {
                return true;
            }
        }

        return false;
    }

    protected void checkForFocusedWidgetUnFocus(int mouseX, int mouseY, int mouseButton)
    {
        if (this.focusedWidget != null && this.focusedWidget.isMouseOver(mouseX, mouseY) == false)
        {
            this.focusedWidget.onMouseClickedOutside(mouseX, mouseY, mouseButton);
        }
    }

    protected void setFocusedWidget(@Nullable InteractableWidget widget)
    {
        if (this.focusedWidget != null && this.focusedWidget != widget)
        {
            if (this.focusedWidget.isFocused())
            {
                this.focusedWidget.setFocused(false);
            }

            this.focusedWidget = null;
        }

        if (widget != null && widget.canBeFocused())
        {
            this.focusedWidget = widget;

            if (widget.isFocused() == false)
            {
                widget.setFocused(true);
            }
        }
    }

    protected void onWidgetFocusChanged(InteractableWidget widget, boolean isFocused)
    {
        if (this.focusedWidget == widget && isFocused == false)
        {
            this.setFocusedWidget(null);
        }
        else if (this.focusedWidget != widget && isFocused)
        {
            this.setFocusedWidget(widget);
        }
    }

    protected List<BaseTextFieldWidget> getAllTextFields()
    {
        List<BaseTextFieldWidget> textFields = new ArrayList<>();

        for (InteractableWidget widget : this.widgets)
        {
            textFields.addAll(widget.getAllTextFields());
        }

        return textFields;
    }

    public BaseScreen setZ(float z)
    {
        this.z = z;

        for (InteractableWidget widget : this.widgets)
        {
            widget.setZLevelBasedOnParent(z);
        }

        return this;
    }

    public BaseScreen setPopupGuiZLevelBasedOn(@Nullable GuiScreen gui)
    {
        if (gui instanceof BaseScreen)
        {
            this.setZ(((BaseScreen) gui).z + this.getPopupGuiZLevelIncrement());
        }

        return this;
    }

    /**
     * Adds the widget if it's not null, and then returns it
     */
    @Nullable
    public <T extends InteractableWidget> T addWidget(@Nullable T widget)
    {
        if (widget != null)
        {
            this.widgets.add(widget);
            widget.setTaskQueue(this::addTask);
            widget.setFocusChangeListener(this::onWidgetFocusChanged);
            widget.setZLevelBasedOnParent(this.z);
            widget.onWidgetAdded();
            widget.updateWidgetState();
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

    protected boolean removeWidget(InteractableWidget widget)
    {
        if (widget != null && this.widgets.contains(widget))
        {
            this.widgets.remove(widget);
            return true;
        }

        return false;
    }

    protected void clearElements()
    {
        this.setFocusedWidget(null);
        this.clearWidgets();
    }

    protected void clearWidgets()
    {
        this.widgets.clear();
    }

    protected void addTask(Runnable task)
    {
        this.tasks.add(task);
    }

    protected void runTasks()
    {
        if (this.tasks.isEmpty() == false)
        {
            for (Runnable task : this.tasks)
            {
                task.run();
            }

            this.tasks.clear();
        }
    }

    public void updateWidgetStates()
    {
        for (InteractableWidget widget : this.widgets)
        {
            widget.updateWidgetState();
        }
    }

    protected void renderScreenBackground(ScreenContext ctx)
    {
        if (this.renderBorder)
        {
            ShapeRenderUtils.renderOutlinedRectangle(this.x, this.y, this.z,
                                                     this.screenWidth, this.screenHeight,
                                                     this.backgroundColor, this.borderColor, ctx);
        }
        else
        {
            ShapeRenderUtils.renderRectangle(this.x, this.y, this.z,
                                             this.screenWidth, this.screenHeight,
                                             this.backgroundColor, ctx);
        }
    }

    protected void renderScreenTitle(ScreenContext ctx)
    {
        if (this.titleText != null)
        {
            int x = this.x + this.titleX;
            int y = this.y + this.titleY;

            this.textRenderer.renderLine(x, y, this.z + 0.125f, this.titleColor, true, this.titleText, ctx);
        }
    }

    protected void renderCustomContents(ScreenContext ctx)
    {
    }

    protected void renderWidgets(ScreenContext ctx)
    {
        for (InteractableWidget widget : this.widgets)
        {
            widget.render(ctx);
        }
    }

    protected void renderHoveredWidget(ScreenContext ctx)
    {
        if (this.hoveredWidgetForHoverInfo != null)
        {
            this.hoveredWidgetForHoverInfo.postRenderHovered(ctx);
            RenderWrap.disableItemLighting();
        }
    }

    public int getStringWidth(String text)
    {
        return StringUtils.getStringWidth(text);
    }

    public static boolean openScreen(@Nullable GuiScreen screen)
    {
        GameWrap.getClient().displayGuiScreen(screen);
        return true;
    }

    public static boolean openScreenWithParent(BaseScreen screen)
    {
        screen.setParent(GuiUtils.getCurrentScreen());
        return openScreen(screen);
    }

    public static ActionResult openScreenAction(@Nullable GuiScreen screen)
    {
        openScreen(screen);
        return ActionResult.SUCCESS;
    }

    /**
     * Opens a popup screen, which is meant to open on top of another screen.
     * This will set the Z level of that screen based on the current screen,
     * and the screen will also be marked as draggable.
     */
    public static boolean openPopupScreen(BaseScreen screen)
    {
        return openPopupScreen(screen, true);
    }

    public static boolean openPopupScreenWithParent(BaseScreen screen)
    {
        screen.setParent(GuiUtils.getCurrentScreen());
        return openPopupScreen(screen);
    }

    /**
     * Opens a popup screen, which is meant to open on top of another screen.
     * This will set the Z level of that screen based on the current screen,
     * and the screen will also be marked as draggable.
     */
    public static boolean openPopupScreen(BaseScreen screen, boolean shouldRenderParent)
    {
        screen.setUseWindowDimensions(false);
        screen.setCanDragMove(true);
        screen.setShouldRenderParent(shouldRenderParent);
        screen.setPopupGuiZLevelBasedOn(GuiUtils.getCurrentScreen());
        screen.boldTitle = true;

        return openScreen(screen);
    }

    public static boolean openPopupScreenWithCurrentScreenAsParent(BaseScreen screen)
    {
        screen.setParent(GuiUtils.getCurrentScreen());
        return openPopupScreen(screen, true);
    }

    public static boolean openPopupScreenWithCurrentScreenAsParent(BaseScreen screen, boolean shouldRenderParent)
    {
        screen.setParent(GuiUtils.getCurrentScreen());
        return openPopupScreen(screen, shouldRenderParent);
    }

    public static void applyCustomScreenScaleChange()
    {
        GuiScreen screen = GuiUtils.getCurrentScreen();

        if (screen instanceof BaseScreen)
        {
            ((BaseScreen) screen).updateCustomScreenScale();
            screen.initGui();
        }
    }

    public static boolean isShiftDown()
    {
        return isShiftKeyDown();
    }

    public static boolean isCtrlDown()
    {
        return isCtrlKeyDown();
    }

    public static boolean isAltDown()
    {
        return isAltKeyDown();
    }

    public static void setStringToClipboard(String str)
    {
        setClipboardString(str);
    }

    public static String getStringFromClipboard()
    {
        return getClipboardString();
    }

    public void renderDebug(ScreenContext ctx)
    {
        if (ctx.isActiveScreen)
        {
            String str = String.format("%s @ x: %d, y: %d, w: %d, h: %d\nmouseX: %d, mouseY: %d",
                                       this.getClass().getName(),
                                       this.x, this.y, this.screenWidth, this.screenHeight, ctx.mouseX, ctx.mouseY);
            StyledText text = StyledText.parse(str);

            int x = this.x + 1;
            int y = this.y + 1;
            float z = this.z + 20;
            int w = text.getRenderWidth() + 4;
            int h = 24;

            // if this is a popup-screen or other screen that does not extend
            // to the bottom of the display, then render the info bar below the
            // screen area, to not obstruct other widgets.
            if (this.y + this.screenHeight + h < this.getTotalHeight())
            {
                x = this.x;
                y = this.y + this.screenHeight + 1;
            }
            else if (this.y >= h)
            {
                x = this.x;
                y = this.y - h;
            }
            else if (ctx.mouseY < this.getTotalHeight() / 2)
            {
                y = this.y + this.screenHeight - h;
            }

            ShapeRenderUtils.renderOutlinedRectangle(x, y, z, w, h, 0xFF000000, 0xFFA0A0A0, ctx);
            this.textRenderer.renderText(x + 2, y + 3, z + 0.00125f, 0xFF00FFFF, true, text, ctx);

            renderWidgetDebug(this.widgets, ctx);
        }
    }

    public static void renderWidgetDebug(List<? extends InteractableWidget> widgets, ScreenContext ctx)
    {
        for (InteractableWidget widget : widgets)
        {
            boolean hovered = widget.isMouseOver(ctx.mouseX, ctx.mouseY);
            widget.renderDebug(hovered, ctx);
        }
    }
}
