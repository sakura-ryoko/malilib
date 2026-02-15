package fi.dy.masa.malilib.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.IMessageConsumer;
import fi.dy.masa.malilib.gui.interfaces.ITextFieldListener;
import fi.dy.masa.malilib.gui.interfaces.ITextFieldMultiLineListener;
import fi.dy.masa.malilib.gui.widgets.WidgetBase;
import fi.dy.masa.malilib.gui.widgets.WidgetLabel;
import fi.dy.masa.malilib.gui.wrappers.TextFieldMultiLineWrapper;
import fi.dy.masa.malilib.gui.wrappers.TextFieldType;
import fi.dy.masa.malilib.gui.wrappers.TextFieldWrapper;
import fi.dy.masa.malilib.interfaces.IStringConsumer;
import fi.dy.masa.malilib.render.GuiContext;
import fi.dy.masa.malilib.render.MessageRenderer;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.KeyCodes;

public abstract class GuiBase extends Screen implements IMessageConsumer, IStringConsumer
{
    public static final String TXT_AQUA = ChatFormatting.AQUA.toString();
    public static final String TXT_BLACK = ChatFormatting.BLACK.toString();
    public static final String TXT_BLUE = ChatFormatting.BLUE.toString();
    public static final String TXT_GOLD = ChatFormatting.GOLD.toString();
    public static final String TXT_GRAY = ChatFormatting.GRAY.toString();
    public static final String TXT_GREEN = ChatFormatting.GREEN.toString();
    public static final String TXT_RED = ChatFormatting.RED.toString();
    public static final String TXT_WHITE = ChatFormatting.WHITE.toString();
    public static final String TXT_YELLOW = ChatFormatting.YELLOW.toString();

    public static final String TXT_BOLD = ChatFormatting.BOLD.toString();
    public static final String TXT_ITALIC = ChatFormatting.ITALIC.toString();
    public static final String TXT_RST = ChatFormatting.RESET.toString();
    public static final String TXT_STRIKETHROUGH = ChatFormatting.STRIKETHROUGH.toString();
    public static final String TXT_UNDERLINE = ChatFormatting.UNDERLINE.toString();

    public static final String TXT_DARK_AQUA = ChatFormatting.DARK_AQUA.toString();
    public static final String TXT_DARK_BLUE = ChatFormatting.DARK_BLUE.toString();
    public static final String TXT_DARK_GRAY = ChatFormatting.DARK_GRAY.toString();
    public static final String TXT_DARK_GREEN = ChatFormatting.DARK_GREEN.toString();
    public static final String TXT_DARK_PURPLE = ChatFormatting.DARK_PURPLE.toString();
    public static final String TXT_DARK_RED = ChatFormatting.DARK_RED.toString();

    public static final String TXT_LIGHT_PURPLE = ChatFormatting.LIGHT_PURPLE.toString();

    protected static final String BUTTON_LABEL_ADD = TXT_DARK_GREEN + "+" + TXT_RST;
    protected static final String BUTTON_LABEL_REMOVE = TXT_DARK_RED + "-" + TXT_RST;

    public static final Identifier BG_TEXTURE = Identifier.withDefaultNamespace("textures/gui/inworld_menu_list_background.png");

    public static final int COLOR_WHITE          = 0xFFFFFFFF;
    public static final int TOOLTIP_BACKGROUND   = 0xB0000000;
    public static final int COLOR_HORIZONTAL_BAR = 0xFF999999;
    protected static final int LEFT         = 20;
    protected static final int TOP          = 10;
    public final Minecraft mc = Minecraft.getInstance();
    public final Font font = this.mc.font;
    public final int fontHeight = this.font.lineHeight;
    private final List<ButtonBase> buttons = new ArrayList<>();
    private final List<WidgetBase> widgets = new ArrayList<>();
    private final List<TextFieldWrapper<? extends GuiTextFieldGeneric>> textFields = new ArrayList<>();
    private final List<TextFieldMultiLineWrapper<? extends GuiTextFieldMultiLine>> textFieldsMultiLine = new ArrayList<>();
    private final MessageRenderer messageRenderer = new MessageRenderer(0xDD000000, COLOR_HORIZONTAL_BAR);
    private long openTime;
    protected WidgetBase hoveredWidget = null;
    protected String title = "";
    protected boolean useTitleHierarchy = true;
    private int keyInputCount;
    private double mouseWheelHorizontalDeltaSum;
    private double mouseWheelVerticalDeltaSum;
    @Nullable
    private Screen parent;

    protected GuiBase()
    {
        super(CommonComponents.EMPTY);
//        this.client = mc;
    }

    public GuiBase setParent(@Nullable Screen parent)
    {
        // Don't allow nesting the GUI with itself...
        if (parent == null || parent.getClass() != this.getClass())
        {
            this.parent = parent;
        }

        return this;
    }

    @Nullable
    public Screen getParent()
    {
        return this.parent;
    }

    public String getTitleString()
    {
        return (this.useTitleHierarchy && this.parent instanceof GuiBase) ? (((GuiBase) this.parent).getTitleString() + " => " + this.title) : this.title;
    }

    @Override
    public @NotNull Component getTitle()
    {
        return Component.nullToEmpty(this.getTitleString());
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }

    @Override
    public void resize(int width, int height)
    {
        if (this.getParent() != null)
        {
            this.getParent().resize(width, height);
        }

        super.resize(width, height);
    }

    @Override
    public void init()
    {
        super.init();

        this.initGui();
        this.openTime = System.nanoTime();
    }

    public void initGui()
    {
        this.clearElements();
    }

    protected void closeGui(boolean showParent)
    {
        if (showParent)
        {
            this.mc.setScreen(this.parent);
        }
        else
        {
            this.onClose();
        }
    }

    /**
     * For Compat / Crash prevention reasons
     * @return ()
     */
    public int getScreenHeight()
    {
        return this.height;
    }

    /**
     * For Compat / Crash prevention reasons
     * @return ()
     */
    public int getScreenWidth()
    {
        return this.width;
    }

    @Override
    public void render(@NotNull GuiGraphics drawContext, int mouseX, int mouseY, float partialTicks)
    {
		GuiContext ctx = GuiContext.fromGuiGraphics(drawContext);
	    ctx.nextStratum();

        // Draw Background / Title
        this.drawScreenBackground(ctx, mouseX, mouseY);
        this.drawTitle(ctx, mouseX, mouseY, partialTicks);

        // Draw base widgets
        this.drawWidgets(ctx, mouseX, mouseY);
        this.drawButtons(ctx, mouseX, mouseY, partialTicks);
        this.drawContents(ctx, mouseX, mouseY, partialTicks);
        this.drawTextFields(ctx, mouseX, mouseY);
        this.drawTextFieldsMultiLine(ctx, mouseX, mouseY);
        this.drawHoveredWidget(ctx, mouseX, mouseY);
        this.drawButtonHoverTexts(ctx, mouseX, mouseY, partialTicks);
        this.drawGuiMessages(ctx);
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics context, int mouseX, int mouseY, float deltaTicks)
    {
        // NO BLUR / MASKING
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount)
    {
        if (this.mouseWheelHorizontalDeltaSum != 0.0 &&
            Math.signum(horizontalAmount) != Math.signum(this.mouseWheelHorizontalDeltaSum))
        {
            this.mouseWheelHorizontalDeltaSum = 0.0;
        }

        if (this.mouseWheelVerticalDeltaSum != 0.0 &&
            Math.signum(verticalAmount) != Math.signum(this.mouseWheelVerticalDeltaSum))
        {
            this.mouseWheelVerticalDeltaSum = 0.0;
        }

        this.mouseWheelHorizontalDeltaSum += horizontalAmount;
        this.mouseWheelVerticalDeltaSum += verticalAmount;

        horizontalAmount = (int) this.mouseWheelHorizontalDeltaSum;
        verticalAmount = (int) this.mouseWheelVerticalDeltaSum;

        if (horizontalAmount != 0.0 || verticalAmount != 0.0)
        {
            this.mouseWheelHorizontalDeltaSum -= horizontalAmount;
            this.mouseWheelVerticalDeltaSum -= verticalAmount;

            if (this.onMouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount))
            {
                return true;
            }
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean mouseClicked(@NotNull MouseButtonEvent click, boolean doubleClick)
    {
        if (this.onMouseClicked(click, doubleClick) == false)
        {
            return super.mouseClicked(click, doubleClick);
        }

        return false;
    }

    @Override
    public boolean mouseReleased(@NotNull MouseButtonEvent click)
    {
        if (this.onMouseReleased(click) == false)
        {
            return super.mouseReleased(click);
        }

        return false;
    }

    @Override
    public boolean mouseDragged(@NonNull MouseButtonEvent click, double dragX, double dragY)
    {
        if (this.onMouseDragged(click, dragX, dragY) == false)
        {
            return super.mouseDragged(click, dragX, dragY);
        }

        return false;
    }

    @Override
    public boolean keyPressed(@NotNull KeyEvent input)
    {
        this.keyInputCount++;

        if (this.onKeyTyped(input))
        {
            return true;
        }

        return super.keyPressed(input);
    }

    @Override
    public boolean charTyped(@NotNull CharacterEvent input)
    {
        // This is an ugly fix for the issue that the key press from the hotkey that
        // opens a GUI would then also get into any text fields or search bars, as the
        // charTyped() event always fires after the keyPressed() event in any case >_>
        // The 100ms timeout is to not indefinitely block the first character,
        // as otherwise IME methods wouldn't work at all, as they don't trigger a key press.
        if (this.keyInputCount <= 0 && System.nanoTime() - this.openTime <= 100000000)
        {
            this.keyInputCount++;
            return true;
        }

        if (this.onCharTyped(input))
        {
            return true;
        }

        return super.charTyped(input);
    }

    public boolean onMouseClicked(MouseButtonEvent click, boolean doubleClick)
    {
        for (ButtonBase button : this.buttons)
        {
            if (button.onMouseClicked(click, doubleClick))
            {
                // Don't call super if the button press got handled
                return true;
            }
        }

        boolean handled = false;

        for (TextFieldWrapper<?> entry : this.textFields)
        {
            if (entry.mouseClicked(click, doubleClick))
            {
                // Don't call super if the button press got handled
                handled = true;
            }
        }

        if (handled == false)
        {
            for (TextFieldMultiLineWrapper<? extends GuiTextFieldMultiLine> entry : this.textFieldsMultiLine)
            {
                if (entry.mouseClicked(click, doubleClick))
                {
                    // Don't call super if the button press got handled
                    handled = true;
                }
            }
        }

        if (handled == false)
        {
            for (WidgetBase widget : this.widgets)
            {
                if (widget.isMouseOver((int) click.x(), (int) click.y()) && widget.onMouseClicked(click, doubleClick))
                {
                    // Don't call super if the button press got handled
                    handled = true;
                    break;
                }
            }
        }

        return handled;
    }

    public boolean onMouseReleased(MouseButtonEvent click)
    {
		for (WidgetBase widget : this.widgets)
        {
            widget.onMouseReleased(click);
        }

        return false;
    }

    public boolean onMouseDragged(@NonNull MouseButtonEvent click, double dragXAmount, double dragYAmount)
    {
        for (ButtonBase button : this.buttons)
        {
            if (button.onMouseDragged(click, dragXAmount, dragYAmount))
            {
                // Don't call super if the button press got handled
                return true;
            }
        }

        for (TextFieldWrapper<? extends GuiTextFieldGeneric> entry : this.textFields)
        {
            if (entry.onMouseDragged(click, dragXAmount, dragYAmount))
            {
                // Don't call super if the button press got handled
                return true;
            }
        }

        for (TextFieldMultiLineWrapper<? extends GuiTextFieldMultiLine> entry : this.textFieldsMultiLine)
        {
            if (entry.onMouseDragged(click, dragXAmount, dragYAmount))
            {
                // Don't call super if the button press got handled
                return true;
            }
        }

        for (WidgetBase widget : this.widgets)
        {
            if (widget.onMouseDragged(click, dragXAmount, dragYAmount))
            {
                // Don't call super if the action got handled
                return true;
            }
        }

        return false;
    }

    public boolean onMouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount)
    {
        for (ButtonBase button : this.buttons)
        {
            if (button.onMouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount))
            {
                // Don't call super if the button press got handled
                return true;
            }
        }

        for (TextFieldWrapper<? extends GuiTextFieldGeneric> entry : this.textFields)
        {
            if (entry.onMouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount))
            {
                // Don't call super if the button press got handled
                return true;
            }
        }

        for (TextFieldMultiLineWrapper<? extends GuiTextFieldMultiLine> entry : this.textFieldsMultiLine)
        {
            if (entry.onMouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount))
            {
                // Don't call super if the button press got handled
                return true;
            }
        }

        for (WidgetBase widget : this.widgets)
        {
            if (widget.onMouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount))
            {
                // Don't call super if the action got handled
                return true;
            }
        }

        return false;
    }

    public boolean onKeyTyped(KeyEvent input)
    {
        boolean handled = false;
        int selected = -1;
        int selectedMultiLine = -1;

        for (int i = 0; i < this.textFields.size(); ++i)
        {
            TextFieldWrapper<?> entry = this.textFields.get(i);

            if (entry.isFocused())
            {
                if (input.key() == KeyCodes.KEY_TAB)
                {
                    entry.setFocused(false);
                    selected = i;
                }
                else
                {
                    entry.onKeyTyped(input);
                }

                handled = input.key() != KeyCodes.KEY_ESCAPE;
                break;
            }
        }

        if (handled == false)
        {
            for (int i = 0; i < this.textFieldsMultiLine.size(); ++i)
            {
                TextFieldMultiLineWrapper<? extends GuiTextFieldMultiLine> entry = this.textFieldsMultiLine.get(i);

                if (entry.isFocused())
                {
                    if (input.key() == KeyCodes.KEY_TAB)
                    {
                        entry.setFocused(false);
                        selectedMultiLine = i;
                    }
                    else
                    {
                        entry.onKeyTyped(input);
                    }

                    handled = input.key() != KeyCodes.KEY_ESCAPE;
                    break;
                }
            }
        }

        if (handled == false)
        {
            for (WidgetBase widget : this.widgets)
            {
                if (widget.onKeyTyped(input))
                {
                    // Don't call super if the button press got handled
                    handled = true;
                    break;
                }
            }
        }

        if (handled == false)
        {
            if (input.key() == KeyCodes.KEY_ESCAPE)
            {
                this.closeGui(input.hasShiftDown() == false);

                return true;
            }
        }

        if (selected >= 0)
        {
            if (input.hasShiftDown())
            {
                selected = selected > 0 ? selected - 1 : this.textFields.size() - 1;
            }
            else
            {
                selected = (selected + 1) % this.textFields.size();
            }

            this.textFields.get(selected).setFocused(true);
        }

        if (selectedMultiLine >= 0)
        {
            if (input.hasShiftDown())
            {
                selectedMultiLine = selectedMultiLine > 0 ? selectedMultiLine - 1 : this.textFieldsMultiLine.size() - 1;
            }
            else
            {
                selectedMultiLine = (selectedMultiLine + 1) % this.textFieldsMultiLine.size();
            }

            this.textFieldsMultiLine.get(selectedMultiLine).setFocused(true);
        }

        return handled;
    }

    public boolean onCharTyped(CharacterEvent input)
    {
        boolean handled = false;

        for (TextFieldWrapper<?> entry : this.textFields)
        {
            if (entry.onCharTyped(input))
            {
                handled = true;
                break;
            }
        }

        if (handled == false)
        {
            for (TextFieldMultiLineWrapper<? extends GuiTextFieldMultiLine> entry : this.textFieldsMultiLine)
            {
                if (entry.onCharTyped(input))
                {
                    handled = true;
                    break;
                }
            }
        }

        if (handled == false)
        {
            for (WidgetBase widget : this.widgets)
            {
                if (widget.onCharTyped(input))
                {
                    // Don't call super if the button press got handled
                    handled = true;
                    break;
                }
            }
        }

        return handled;
    }

    @Override
    public void setString(String string)
    {
        this.messageRenderer.addMessage(3000, string);
    }

    @Override
    public void addMessage(MessageType type, String messageKey, Object... args)
    {
        this.addGuiMessage(type, 5000, messageKey, args);
    }

    @Override
    public void addMessage(MessageType type, int lifeTime, String messageKey, Object... args)
    {
        this.addGuiMessage(type, lifeTime, messageKey, args);
    }

    public void addGuiMessage(MessageType type, int displayTimeMs, String messageKey, Object... args)
    {
        this.messageRenderer.addMessage(type, displayTimeMs, messageKey, args);
    }

    public void setNextMessageType(MessageType type)
    {
        this.messageRenderer.setNextMessageType(type);
    }

    protected void drawGuiMessages(GuiContext ctx)
    {
        this.messageRenderer.drawMessages(ctx, this.width / 2, this.height / 2);
    }

    public <T extends ButtonBase> T addButton(T button, IButtonActionListener listener)
    {
        button.setActionListener(listener);
        this.buttons.add(button);
        return button;
    }

    public <T extends GuiTextFieldGeneric> TextFieldWrapper<T> addTextField(T textField, @Nullable ITextFieldListener<T> listener)
    {
        return this.addTextField(textField, listener, TextFieldType.STRING);
    }

    public <T extends GuiTextFieldGeneric> TextFieldWrapper<T> addTextField(T textField, @Nullable ITextFieldListener<T> listener, TextFieldType type)
    {
        TextFieldWrapper<T> wrapper = new TextFieldWrapper<>(textField, listener, type);
        this.textFields.add(wrapper);
        return wrapper;
    }

    public <T extends GuiTextFieldMultiLine> TextFieldMultiLineWrapper<T> addTextFieldMultiLine(T textField, int lines, @Nullable ITextFieldMultiLineListener<T> listener)
    {
        TextFieldMultiLineWrapper<T> wrapper = new TextFieldMultiLineWrapper<>(textField, lines, listener);
        this.textFieldsMultiLine.add(wrapper);
        return wrapper;
    }

    public <T extends WidgetBase> T addWidget(T widget)
    {
        this.widgets.add(widget);
        return widget;
    }

    public WidgetLabel addLabel(int x, int y, int width, int height, int textColor, String... lines)
    {
        return this.addLabel(x, y, width, height, textColor, Arrays.asList(lines));
    }

    public WidgetLabel addLabel(int x, int y, int width, int height, int textColor, List<String> lines)
    {
        if (lines.size() > 0)
        {
            if (width == -1)
            {
                for (String line : lines)
                {
                    width = Math.max(width, this.getStringWidth(line));
                }
            }
        }

        return this.addWidget(new WidgetLabel(x, y, width, height, textColor, lines));
    }

    protected boolean removeWidget(WidgetBase widget)
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
        this.clearWidgets();
        this.clearButtons();
        this.clearTextFields();
        this.clearTextFieldsMultiLine();
    }

    protected void clearWidgets()
    {
        this.widgets.clear();
    }

    protected void clearButtons()
    {
        this.buttons.clear();
    }

    protected void clearTextFields()
    {
        this.textFields.clear();
    }

    protected void clearTextFieldsMultiLine()
    {
        this.textFieldsMultiLine.clear();
    }

    /**
     * Draw's an Screen Tooltip Background
     * @param ctx ()
     * @param mouseX ()
     * @param mouseY ()
     */
    protected void drawScreenBackground(GuiContext ctx, int mouseX, int mouseY)
    {
        // Draw the dark background
        RenderUtils.drawRect(ctx, 0, 0, this.width, this.height, TOOLTIP_BACKGROUND);
    }

    /**
     * Draw's a [Optional] blurred out Background, and masking texture the same size as the widget.
     * This helps with sub-menu widgets not displaying correctly, such as with the Advanced keybinds menu.
     *
     * @param ctx ()
     * @param topX ()
     * @param topY ()
     * @param width ()
     * @param height ()
     * @param blur ()
     */
    protected void drawTexturedBG(GuiContext ctx, int topX, int topY, int width, int height, boolean blur)
    {
        if (blur)
        {
            super.renderBlurredBackground(ctx.getGuiGraphics());
        }

//        RenderUtils.drawTexturedRect(ctx, GuiBase.BG_TEXTURE, topX, topY, 0, 0, width, height, true);
        super.renderMenuBackground(ctx.getGuiGraphics(), topX, topY, width, height);
    }

    protected void drawTitle(GuiContext ctx, int mouseX, int mouseY, float partialTicks)
    {
        this.drawString(ctx, this.getTitleString(), LEFT, TOP, COLOR_WHITE);
    }

    protected void drawContents(GuiContext ctx, int mouseX, int mouseY, float partialTicks)
    {
    }

    protected void drawButtons(GuiContext ctx, int mouseX, int mouseY, float partialTicks)
    {
        for (ButtonBase button : this.buttons)
        {
            button.render(ctx, mouseX, mouseY, button.isMouseOver());
        }
    }

    protected void drawTextFields(GuiContext ctx, int mouseX, int mouseY)
    {
        for (TextFieldWrapper<?> entry : this.textFields)
        {
            entry.draw(ctx, mouseX, mouseY);
        }
    }

    protected void drawTextFieldsMultiLine(GuiContext ctx, int mouseX, int mouseY)
    {
        for (TextFieldMultiLineWrapper<? extends GuiTextFieldMultiLine> entry : this.textFieldsMultiLine)
        {
            entry.draw(ctx, mouseX, mouseY);
        }
    }

    protected void drawWidgets(GuiContext ctx, int mouseX, int mouseY)
    {
        this.hoveredWidget = null;

        if (this.widgets.isEmpty() == false)
        {
            for (WidgetBase widget : this.widgets)
            {
                widget.render(ctx, mouseX, mouseY, false);

                if (widget.isMouseOver(mouseX, mouseY))
                {
                    this.hoveredWidget = widget;
                }
            }
        }
    }

    protected void drawButtonHoverTexts(GuiContext ctx, int mouseX, int mouseY, float partialTicks)
    {
        if (this.shouldRenderHoverStuff() == false)
        {
            return;
        }

        for (ButtonBase button : this.buttons)
        {
            if (button.hasHoverText() && button.isMouseOver())
            {
                RenderUtils.drawHoverText(ctx, mouseX, mouseY, button.getHoverStrings());
            }
        }
    }

    protected boolean shouldRenderHoverStuff()
    {
        return this.mc.screen == this;
    }

    protected void drawHoveredWidget(GuiContext ctx, int mouseX, int mouseY)
    {
        if (this.shouldRenderHoverStuff() == false)
        {
            return;
        }

        if (this.hoveredWidget != null)
        {
            this.hoveredWidget.postRenderHovered(ctx, mouseX, mouseY, false);
        }
    }

    public static boolean isMouseOver(int mouseX, int mouseY, int x, int y, int width, int height)
    {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    public int getStringWidth(String text)
    {
        return this.font.width(text);
    }

    public void drawString(GuiContext ctx, String text, int x, int y, int color)
    {
	    ctx.drawString(ctx.fontRenderer(), text, x, y, color, false);
    }

    public void drawStringWithShadow(GuiContext ctx, String text, int x, int y, int color)
    {
        ctx.drawString(ctx.fontRenderer(), text, x, y, color);
    }

    public int getMaxPrettyNameLength(List<? extends IConfigBase> configs)
    {
        int width = 0;

        for (IConfigBase config : configs)
        {
            width = Math.max(width, this.getStringWidth(config.getConfigGuiDisplayName()));
        }

        return width;
    }

    public static void openGui(Screen gui)
    {
        Minecraft.getInstance().setScreen(gui);
    }

	public static boolean isShiftDown()
	{
		return InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT)
				|| InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), GLFW.GLFW_KEY_RIGHT_SHIFT);
	}

	public static boolean isCtrlDown()
	{
		return Util.getPlatform() == Util.OS.OSX
			   ? InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), GLFW.GLFW_KEY_LEFT_SUPER)
					   || InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), GLFW.GLFW_KEY_RIGHT_SUPER)
			   : InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), GLFW.GLFW_KEY_LEFT_CONTROL)
					   || InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), GLFW.GLFW_KEY_RIGHT_CONTROL);
	}

	public static boolean isAltDown()
	{
		return InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), GLFW.GLFW_KEY_LEFT_ALT)
				|| InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), GLFW.GLFW_KEY_RIGHT_ALT);
	}
}
