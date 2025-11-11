package fi.dy.masa.malilib.gui.interfaces;

import net.minecraft.client.gui.widget.TextFieldWidget;

public interface ITextFieldListener<T extends TextFieldWidget>
{
    default boolean onGuiClosed(T textField)
    {
        return false;
    }

    boolean onTextChange(T textField);
}
