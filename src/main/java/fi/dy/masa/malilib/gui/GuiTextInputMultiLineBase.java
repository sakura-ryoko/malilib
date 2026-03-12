package fi.dy.masa.malilib.gui;

import javax.annotation.Nullable;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.render.GuiContext;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.KeyCodes;
import fi.dy.masa.malilib.util.MathUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.Color4f;

@ApiStatus.Experimental
public abstract class GuiTextInputMultiLineBase extends GuiDialogBase
{
    protected final GuiTextFieldMultiLine textField;
    protected final String originalText;
    protected final int displayLines;
    protected final int buttonHeight;
    protected final int totalLines;
    protected final int totalHeight;
    protected final int totalWidth;

    public GuiTextInputMultiLineBase(int maxTextLength, int displayLines, int maxLines, String titleKey, String defaultText, @Nullable Screen parent)
    {
        this(maxTextLength, displayLines, maxLines, titleKey, defaultText, parent, Color4f.WHITE, Color4f.WHITE, true, true, true);
    }

    public GuiTextInputMultiLineBase(int maxTextLength, int displayLines, int maxLines, String titleKey, String defaultText, @Nullable Screen parent,
                                     Color4f textColor, boolean withShadow)
    {
        this(maxTextLength, displayLines, maxLines, titleKey, defaultText, parent, textColor, Color4f.WHITE, withShadow, true, true);
    }

    public GuiTextInputMultiLineBase(int maxTextLength, int displayLines, int maxLines, String titleKey, String defaultText, @Nullable Screen parent,
                                     Color4f textColor, Color4f cursorColor,
                                     boolean withShadow, boolean withBackground, boolean withDecorations)
    {
        this.setParent(parent);
        this.title = StringUtils.translate(titleKey);
        this.useTitleHierarchy = false;
        this.originalText = defaultText;
        this.displayLines = MathUtils.clamp(displayLines, 1, this.dialogHeight);
        this.totalLines = MathUtils.clamp(maxLines, 1, Integer.MAX_VALUE);
        this.buttonHeight = 20;
        this.totalHeight = displayLines * 20;
        this.totalWidth = MathUtils.min(maxTextLength * 10, 240);

        boolean hasScrollbar = displayLines < maxLines;
        this.setWidthAndHeight(this.totalWidth + 20, this.totalHeight + this.buttonHeight + 40);
        this.centerOnScreen();

        GuiTextFieldMultiLine.Builder builder = new GuiTextFieldMultiLine.Builder();
        this.textField = builder.setX(this.dialogLeft + 12).setY(this.dialogTop + this.buttonHeight)
                                .setWidth(this.totalWidth).setHeight(this.totalHeight)
                                .setTextColor(textColor).setCursorColor(cursorColor)
                                .setBackground(withBackground).setShadow(withShadow).setDecorations(withDecorations).setScrollbar(hasScrollbar)
                                .build(this.font, defaultText);
//        this.textField = new GuiTextFieldGeneric(this.dialogLeft + 12, this.dialogTop + 20, width, 20, this.font);
        this.textField.setFocused(true);
        this.textField.setLineLimit(maxLines);
        this.textField.setValue(this.originalText);
    }

    @Override
    public void initGui()
    {
        int x = this.dialogLeft + 10;
        int y = this.dialogTop + this.totalHeight + this.buttonHeight + 10;

        x += this.createButton(x, y, ButtonType.OK) + 2;
        x += this.createButton(x, y, ButtonType.RESET) + 2;
        this.createButton(x, y, ButtonType.CANCEL);
    }

    protected int createButton(int x, int y, ButtonType type)
    {
        ButtonGeneric button = new ButtonGeneric(x, y, -1, this.buttonHeight, type.getDisplayName());
        button.setWidth(MathUtils.max(40, button.getWidth()));
        return this.addButton(button, this.createActionListener(type)).getWidth();
    }

    @Override
    public boolean isPauseScreen()
    {
        return this.getParent() != null && this.getParent().isPauseScreen();
    }

    @Override
    public void drawContents(GuiContext ctx, int mouseX, int mouseY, float partialTicks)
    {
        if (this.getParent() != null)
        {
            this.getParent().extractRenderState(ctx.getGuiGraphics(), mouseX, mouseY, partialTicks);
        }

	    ctx.pose().pushMatrix();
        // 1.f
	    ctx.pose().translate(0, 0);

        RenderUtils.drawOutlinedBox(ctx, this.dialogLeft, this.dialogTop, this.dialogWidth, this.dialogHeight, 0xE0000000, COLOR_HORIZONTAL_BAR);

        // Draw the title
        this.drawStringWithShadow(ctx, this.getTitleString(), this.dialogLeft + 10, this.dialogTop + 4, COLOR_WHITE);

        //super.drawScreen(mouseX, mouseY, partialTicks);
        this.textField.extractRenderState(ctx.getGuiGraphics(), mouseX, mouseY, partialTicks);

        this.drawButtons(ctx, mouseX, mouseY, partialTicks);
	    ctx.pose().popMatrix();
    }

    @Override
    public boolean onKeyTyped(KeyEvent input)
    {
//        if (input.key() == KeyCodes.KEY_ENTER)
//        {
//            // Only close the GUI if the value was successfully applied
//            if (this.applyValue(this.textField.getValue()))
//            {
//                GuiBase.openGui(this.getParent());
//            }
//
//            return true;
//        }
//        else
        if (input.key() == KeyCodes.KEY_ESCAPE)
        {
            GuiBase.openGui(this.getParent());
            return true;
        }

        if (this.textField.isFocused())
        {
            return this.textField.keyPressed(input);
        }

        return super.onKeyTyped(input);
    }

    @Override
    public boolean onCharTyped(CharacterEvent input)
    {
        if (this.textField.isFocused())
        {
            return this.textField.charTyped(input);
        }

        return super.onCharTyped(input);
    }

    @Override
    public boolean onMouseClicked(MouseButtonEvent click, boolean doubleClick)
    {
        if (this.textField.mouseClicked(click, doubleClick))
        {
            return true;
        }

        return super.onMouseClicked(click, doubleClick);
    }

    @Override
    public boolean onMouseDragged(@NonNull MouseButtonEvent click, double dragXAmount, double dragYAmount)
    {
        if (this.textField.mouseDragged(click, dragXAmount, dragYAmount))
        {
            return true;
        }

        return super.onMouseDragged(click, dragXAmount, dragYAmount);
    }

    @Override
    public boolean onMouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount)
    {
        if (this.textField.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount))
        {
            return true;
        }

        return super.onMouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    protected ButtonListener createActionListener(ButtonType type)
    {
        return new ButtonListener(type, this);
    }

    protected abstract boolean applyValue(String string);

    protected static class ButtonListener implements IButtonActionListener
    {
        private final GuiTextInputMultiLineBase gui;
        private final ButtonType type;

        public ButtonListener(ButtonType type, GuiTextInputMultiLineBase gui)
        {
            this.type = type;
            this.gui = gui;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton)
        {
            if (this.type == ButtonType.OK)
            {
                // Only close the GUI if the value was successfully applied
                if (this.gui.applyValue(this.gui.textField.getValue()))
                {
                    GuiBase.openGui(this.gui.getParent());
                }
            }
            else if (this.type == ButtonType.CANCEL)
            {
                GuiBase.openGui(this.gui.getParent());
            }
            else if (this.type == ButtonType.RESET)
            {
                this.gui.textField.setValue(this.gui.originalText);
                this.gui.textField.setFocused(true);
            }
        }
    }

    protected enum ButtonType
    {
        OK      ("malilib.gui.button.ok"),
        CANCEL  ("malilib.gui.button.cancel"),
        RESET   ("malilib.gui.button.reset");

        private final String labelKey;

        ButtonType(String labelKey)
        {
            this.labelKey = labelKey;
        }

        public String getDisplayName()
        {
            return StringUtils.translate(this.labelKey);
        }
    }
}
