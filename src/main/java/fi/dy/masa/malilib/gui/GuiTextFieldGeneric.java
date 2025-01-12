package fi.dy.masa.malilib.gui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;

public class GuiTextFieldGeneric extends TextFieldWidget
{
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected int zLevel;

    public GuiTextFieldGeneric(int x, int y, int width, int height, TextRenderer textRenderer)
    {
        super(textRenderer, x, y, width, height, ScreenTexts.EMPTY);

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.setMaxLength(256);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        boolean ret = super.mouseClicked(mouseX, mouseY, mouseButton);

        if (this.isMouseOver((int) mouseX, (int) mouseY))
        {
            if (mouseButton == 1)
            {
                this.setText("");
            }

            this.setFocused(true);

            return true;
        }
        else
        {
            this.setFocused(false);
        }

        return ret;
    }

    public int getX()
    {
        return this.x;
    }

    public int getY()
    {
        return this.y;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public boolean isMouseOver(int mouseX, int mouseY)
    {
        return mouseX >= this.x && mouseX < this.x + this.width &&
               mouseY >= this.y && mouseY < this.y + this.height;
    }

    public GuiTextFieldGeneric setZLevel(int zLevel)
    {
        this.zLevel = zLevel;
        return this;
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta)
    {
        if (this.zLevel != 0)
        {
            MatrixStack matrixStack = context.getMatrices();
            matrixStack.push();
            matrixStack.translate(0, 0, this.zLevel);

            super.renderWidget(context, mouseX, mouseY, delta);

            matrixStack.pop();
        }
        else
        {
            super.renderWidget(context, mouseX, mouseY, delta);
        }
    }

    /**
     * For Compat/Crash prevention reasons
     * @param text ()
     */
    public void setTextWrapper(String text)
    {
        this.setText(text);
    }

    /**
     * For Compat/Crash prevention reasons
     * @return ()
     */
    public String getTextWrapper()
    {
        return this.getText();
    }

    /**
     * For Compat/Crash prevention reasons
     * @param length ()
     */
    public void setMaxLengthWrapper(int length)
    {
        this.setMaxLength(length);
    }

    /**
     * For Compat/Crash prevention reasons
     * @return ()
     */
    public int getCursorWrapper()
    {
        return this.getCursor();
    }

    /**
     * For Compat/Crash prevention reasons
     * @param focus ()
     */
    public void setFocusedWrapper(boolean focus)
    {
        this.setFocused(focus);
    }

    /**
     * For Compat/Crash prevention reasons
     * @return ()
     */
    public boolean isFocusedWrapper()
    {
        return this.isFocused();
    }

    /**
     * For Compat/Crash prevention reasons
     * @return ()
     */
    public int getWidthWrapper()
    {
        return this.getWidth();
    }

    /**
     * For Compat/Crash prevention reasons
     * @param context ()
     * @param mouseX ()
     * @param mouseY ()
     * @param delta ()
     */
    public void renderWrapper(DrawContext context, int mouseX, int mouseY, float delta)
    {
        this.render(context, mouseX, mouseY, delta);
    }

    /**
     * For Compat/Crash prevention reasons
     * @param keyCode ()
     * @param scanCode ()
     * @param modifiers ()
     * @return ()
     */
    public boolean keyPressedWrapper(int keyCode, int scanCode, int modifiers)
    {
        return this.keyPressed(keyCode, scanCode, modifiers);
    }

    /**
     * For Compat/Crash prevention reasons
     * @param chr ()
     * @param modifiers ()
     * @return ()
     */
    public boolean charTypedWrapper(char chr, int modifiers)
    {
        return this.charTyped(chr, modifiers);
    }
}
