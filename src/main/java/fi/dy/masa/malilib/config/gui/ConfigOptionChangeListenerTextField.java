package fi.dy.masa.malilib.config.gui;

import fi.dy.masa.malilib.config.IStringRepresentable;
import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.interfaces.ITextFieldListener;

public class ConfigOptionChangeListenerTextField implements ITextFieldListener<GuiTextFieldGeneric>
{
    protected final IStringRepresentable config;
    protected final GuiTextFieldGeneric textField;
    protected final ButtonBase buttonReset;

    public ConfigOptionChangeListenerTextField(IStringRepresentable config, GuiTextFieldGeneric textField, ButtonBase buttonReset)
    {
        this.config = config;
        this.textField = textField;
        this.buttonReset = buttonReset;
    }

    @Override
    public boolean onTextChange(GuiTextFieldGeneric textField)
    {
        this.buttonReset.setEnabled(this.config.isModified(this.textField.getText()));
        return false;
    }
}
