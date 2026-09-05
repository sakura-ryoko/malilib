package fi.dy.masa.malilib.gui.interfaces;

import net.minecraft.client.gui.components.MultiLineEditBox;

public interface ITextFieldMultiLineListener<T extends MultiLineEditBox>
{
    default boolean onGuiClosed(T textField)
    {
        return false;
    }

    boolean onTextChange(T textField);
}
