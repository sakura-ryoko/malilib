package fi.dy.masa.malilib.gui.interfaces;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.client.gui.widget.EditBoxWidget;

@ApiStatus.Experimental
public interface ITextFieldMultiLineListener<T extends EditBoxWidget>
{
    default boolean onGuiClosed(T textField)
    {
        return false;
    }

    boolean onTextChange(T textField);
}
