package fi.dy.masa.malilib.gui.interfaces;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.client.gui.components.MultiLineEditBox;

@ApiStatus.Experimental
public interface ITextFieldMultiLineListener<T extends MultiLineEditBox>
{
    default boolean onGuiClosed(T textField)
    {
        return false;
    }

    boolean onTextChange(T textField);
}
