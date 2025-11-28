package fi.dy.masa.malilib.gui.interfaces;

import net.minecraft.client.gui.components.EditBox;

public interface ITextFieldListener<T extends EditBox>
{
    default boolean onGuiClosed(T textField)
    {
        return false;
    }

    boolean onTextChange(T textField);
}
