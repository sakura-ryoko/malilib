package fi.dy.masa.malilib.gui.wrappers;

import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.interfaces.ITextFieldListener;
import fi.dy.masa.malilib.util.KeyCodes;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;

public class TextFieldWrapper<T extends GuiTextFieldGeneric>
{
    private final T textField;
    private final ITextFieldListener<T> listener;
    
    public TextFieldWrapper(T textField, ITextFieldListener<T> listener)
    {
        this.textField = textField;
        this.listener = listener;
    }

    public T getTextField()
    {
        return this.textField;
    }

    public ITextFieldListener<T> getListener()
    {
        return this.listener;
    }

    public boolean isFocused()
    {
        return this.textField.isFocused();
    }

    public void setFocused(boolean isFocused)
    {
        this.textField.setFocused(isFocused);
    }

    public void onGuiClosed()
    {
        if (this.listener != null)
        {
            this.listener.onGuiClosed(this.textField);
        }
    }

    public void draw(DrawContext drawContext, int mouseX, int mouseY)
    {
        this.textField.render(drawContext, mouseX, mouseY, 0f);
    }

//    private void renderWidgetFix(DrawContext drawContext, int mouseX, int mouseY, float deltaTicks)
//    {
//        if (this.textField.visible)
//        {
////            this.textField.renderWidget(drawContext, mouseX, mouseY, deltaTicks);
//
//            int i = this.textField.getX() + this.textField.getWidth() - this.textField.textureWidth - 2;
//            int j = this.textField.getY() + this.textField.getHeight() / 2 - this.textField.textureHeight / 2;
////        context.goUpLayer();
//            drawContext.drawGuiTexture(RenderPipelines.GUI_TEXTURED, this.textField.texture, i, j, this.textField.textureWidth, this.textField.textureHeight);
////        context.popLayer();
//        }
//    }

    public boolean mouseClicked(Click click, boolean doubleClick)
    {
        if (this.textField.mouseClicked(click, doubleClick))
        {
            return true;
        }

        if (this.textField.isMouseOver(click.x(), click.y()) == false)
        {
            this.textField.setFocused(false);
        }

        return false;
    }

    public boolean onKeyTyped(KeyInput input)
    {
        String textPre = this.textField.getText();

        if (this.textField.isFocused() && this.textField.keyPressed(input))
        {
            if (this.listener != null &&
                (input.key() == KeyCodes.KEY_ENTER || input.key() == KeyCodes.KEY_TAB ||
                 this.textField.getText().equals(textPre) == false))
            {
                this.listener.onTextChange(this.textField);
            }

            return true;
        }

        return false;
    }

    public boolean onCharTyped(CharInput input)
    {
        String textPre = this.textField.getText();

        if (this.textField.isFocused() && this.textField.charTyped(input))
        {
            if (this.listener != null && this.textField.getText().equals(textPre) == false)
            {
                this.listener.onTextChange(this.textField);
            }

            return true;
        }

        return false;
    }
}
