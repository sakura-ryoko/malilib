package fi.dy.masa.malilib.config.gui;

import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.IButtonActionHandler;
import fi.dy.masa.malilib.util.input.ScanCodes;

public class ButtonPressDirtyListenerSimple implements IButtonActionHandler
{
    private boolean dirty;

    @Override
    public boolean handleAction(ButtonBase button, int mouseButton)
    {
        if (mouseButton != ScanCodes.OFFSET_MOUSE_LEFT) return false;
        this.dirty = true;
        return true;
    }

    public boolean isDirty()
    {
        return this.dirty;
    }

    public void resetDirty()
    {
        this.dirty = false;
    }
}
