package fi.dy.masa.malilib.config.gui;

import fi.dy.masa.malilib.gui.button.*;
import fi.dy.masa.malilib.gui.interfaces.IKeybindConfigGui;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.util.input.ScanCodes;

public class ConfigOptionChangeListenerKeybind implements IButtonActionHandler
{
    private final IKeybindConfigGui host;
    private final ConfigButtonKeybind buttonHotkey;
    private final ButtonGeneric button;
    private final IKeybind keybind;

    public ConfigOptionChangeListenerKeybind(IKeybind keybind, ConfigButtonKeybind buttonHotkey, ButtonGeneric button, IKeybindConfigGui host)
    {
        this.buttonHotkey = buttonHotkey;
        this.button = button;
        this.keybind = keybind;
        this.host = host;
    }

    @Override
    public boolean handleAction(ButtonBase button, int mouseButton)
    {
        if (mouseButton != ScanCodes.OFFSET_MOUSE_LEFT) return false;

        this.keybind.resetToDefault();
        this.updateButtons();
        this.host.getButtonPressListener().handleAction(button, mouseButton);

        return true;
    }

    public void updateButtons()
    {
        this.button.setEnabled(this.keybind.isModified());
        this.buttonHotkey.updateDisplayString();
    }
}
