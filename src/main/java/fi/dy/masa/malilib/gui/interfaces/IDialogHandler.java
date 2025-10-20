package fi.dy.masa.malilib.gui.interfaces;

import fi.dy.masa.malilib.gui.GuiBase;

public interface IDialogHandler
{
    /**
     * Open the provided GUI as a "dialog window"
     * @param gui ()
     */
    void openDialog(GuiBase gui);

    /**
     * Close the previously opened "dialog window"
     */
    void closeDialog();
}
