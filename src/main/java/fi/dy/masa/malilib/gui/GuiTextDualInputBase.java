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

@ApiStatus.Experimental
public abstract class GuiTextDualInputBase extends GuiDialogBase
{
    protected final GuiTextFieldGeneric textField1;
    protected final GuiTextFieldGeneric textField2;
    protected final String originalText1;
    protected final String originalText2;
    protected int selectedBox;
    protected final int text1Height;
    protected final int text2Height;
    protected final int buttonHeight;
    protected final int totalHeight;
    protected final int totalWidth;

    public GuiTextDualInputBase(int maxTextLength, String titleKey, String defaultText1, String defaultText2, @Nullable Screen parent)
    {
        this.setParent(parent);
        this.title = StringUtils.translate(titleKey);
        this.useTitleHierarchy = false;
        this.originalText1 = defaultText1;
        this.originalText2 = defaultText2;
        this.text1Height = 20;
        this.text2Height = 20;
        this.buttonHeight = 20;
        this.totalHeight = this.text1Height + this.text2Height + 2;
        this.totalWidth = MathUtils.min(maxTextLength * 10, 240);

        this.setWidthAndHeight(this.totalWidth + 20, this.totalHeight + this.buttonHeight + 40);
        this.centerOnScreen();

        this.textField1 = new GuiTextFieldGeneric(this.dialogLeft + 12, this.dialogTop + this.buttonHeight, this.totalWidth, this.text1Height, this.font);
        this.textField1.setMaxLength(maxTextLength);
        this.textField1.setValue(this.originalText1);

        this.textField2 = new GuiTextFieldGeneric(this.dialogLeft + 12, this.dialogTop + this.text1Height + this.buttonHeight + 2, this.totalWidth, this.text2Height, this.font);
        this.textField2.setMaxLength(maxTextLength);
        this.textField2.setValue(this.originalText2);

        this.textField1.setFocused(true);
        this.selectedBox = 1;
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

    protected int getSelectedBox()
    {
        return this.selectedBox;
    }

    @Nullable
    protected GuiTextFieldGeneric getSelectedTextField()
    {
        if (this.getSelectedBox() == 1)
        {
            return this.textField1;
        }
        else if (this.getSelectedBox() == 2)
        {
            return this.textField2;
        }

        return null;
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
        this.textField1.render(ctx.getGuiGraphics(), mouseX, mouseY, partialTicks);
        this.textField2.render(ctx.getGuiGraphics(), mouseX, mouseY, partialTicks);

        this.drawButtons(ctx, mouseX, mouseY, partialTicks);
	    ctx.pose().popMatrix();
    }

    @Override
    public boolean onKeyTyped(KeyEvent input)
    {
        if (input.key() == KeyCodes.KEY_ENTER)
        {
            // Only close the GUI if the value was successfully applied
            if (this.applyValues(this.textField1.getValue(), this.textField2.getValue()))
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

        if (this.textField1.isFocused())
        {
            this.selectedBox = 1;
            return this.textField1.keyPressed(input);
        }
        else if (this.textField2.isFocused())
        {
            this.selectedBox = 2;
            return this.textField2.keyPressed(input);
        }

        return super.onKeyTyped(input);
    }

    @Override
    public boolean onCharTyped(CharacterEvent input)
    {
        if (this.textField1.isFocused())
        {
            this.selectedBox = 1;
            return this.textField1.charTyped(input);
        }
        else if (this.textField2.isFocused())
        {
            this.selectedBox = 2;
            return this.textField2.charTyped(input);
        }

        return super.onCharTyped(input);
    }

    @Override
    public boolean onMouseClicked(MouseButtonEvent click, boolean doubleClick)
    {
        if (this.textField1.mouseClicked(click, doubleClick))
        {
            this.selectedBox = 1;
            return true;
        }
        else if (this.textField2.mouseClicked(click, doubleClick))
        {
            this.selectedBox = 2;
            return true;
        }

        return super.onMouseClicked(click, doubleClick);
    }

    @Override
    public boolean onMouseDragged(@NonNull MouseButtonEvent click, double dragXAmount, double dragYAmount)
    {
        if (this.textField1.mouseDragged(click, dragXAmount, dragYAmount))
        {
            this.selectedBox = 1;
            return true;
        }
        else if (this.textField2.mouseDragged(click, dragXAmount, dragYAmount))
        {
            this.selectedBox = 2;
            return true;
        }

        return super.onMouseDragged(click, dragXAmount, dragYAmount);
    }

    @Override
    public boolean onMouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount)
    {
        if (this.textField1.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount))
        {
            this.selectedBox = 1;
            return true;
        }
        else if (this.textField2.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount))
        {
            this.selectedBox = 2;
            return true;
        }

        return super.onMouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    protected ButtonListener createActionListener(ButtonType type)
    {
        return new ButtonListener(type, this);
    }

    protected abstract boolean applyValues(String value1, String value2);

    protected static class ButtonListener implements IButtonActionListener
    {
        private final GuiTextDualInputBase gui;
        private final ButtonType type;

        public ButtonListener(ButtonType type, GuiTextDualInputBase gui)
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
                if (this.gui.applyValues(this.gui.textField1.getValue(), this.gui.textField2.getValue()))
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
                this.gui.textField1.setValue(this.gui.originalText1);
                this.gui.textField2.setValue(this.gui.originalText2);
                this.gui.textField1.setFocused(true);
                this.gui.selectedBox = 1;
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
