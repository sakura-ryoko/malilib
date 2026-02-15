package fi.dy.masa.malilib.gui;

import javax.annotation.Nullable;
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
import fi.dy.masa.malilib.util.StringUtils;

public abstract class GuiTextInputBase extends GuiDialogBase
{
    protected final GuiTextFieldGeneric textField;
    protected final String originalText;
    protected final int textHeight;
    protected final int buttonHeight;
    protected final int totalHeight;
    protected final int totalWidth;

    public GuiTextInputBase(int maxTextLength, String titleKey, String defaultText, @Nullable Screen parent)
    {
        this.setParent(parent);
        this.title = StringUtils.translate(titleKey);
        this.useTitleHierarchy = false;
        this.originalText = defaultText;
        this.textHeight = 20;
        this.buttonHeight = 20;
        this.totalHeight = this.textHeight;
        this.totalWidth = Math.min(maxTextLength * 10, 240);

        this.setWidthAndHeight(this.totalWidth + 20, this.totalHeight + this.buttonHeight + 40);
        this.centerOnScreen();

        this.textField = new GuiTextFieldGeneric(this.dialogLeft + 12, this.dialogTop + this.buttonHeight, this.totalWidth, this.totalHeight, this.font);
        this.textField.setMaxLength(maxTextLength);
        this.textField.setFocused(true);
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
        button.setWidth(Math.max(40, button.getWidth()));
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
            this.getParent().render(ctx.getGuiGraphics(), mouseX, mouseY, partialTicks);
        }

	    ctx.pose().pushMatrix();
        // 1.f
	    ctx.pose().translate(0, 0);

        RenderUtils.drawOutlinedBox(ctx, this.dialogLeft, this.dialogTop, this.dialogWidth, this.dialogHeight, 0xE0000000, COLOR_HORIZONTAL_BAR);

        // Draw the title
        this.drawStringWithShadow(ctx, this.getTitleString(), this.dialogLeft + 10, this.dialogTop + 4, COLOR_WHITE);

        //super.drawScreen(mouseX, mouseY, partialTicks);
        this.textField.render(ctx.getGuiGraphics(), mouseX, mouseY, partialTicks);

        this.drawButtons(ctx, mouseX, mouseY, partialTicks);
	    ctx.pose().popMatrix();
    }

    @Override
    public boolean onKeyTyped(KeyEvent input)
    {
        if (input.key() == KeyCodes.KEY_ENTER)
        {
            // Only close the GUI if the value was successfully applied
            if (this.applyValue(this.textField.getValue()))
            {
                GuiBase.openGui(this.getParent());
            }

            return true;
        }
        else if (input.key() == KeyCodes.KEY_ESCAPE)
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
        private final GuiTextInputBase gui;
        private final ButtonType type;

        public ButtonListener(ButtonType type, GuiTextInputBase gui)
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
