package fi.dy.masa.malilib.gui.button;

import javax.annotation.Nullable;

import fi.dy.masa.malilib.config.IConfigLockedStringList;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiLockedStringListEdit;
import fi.dy.masa.malilib.gui.interfaces.IConfigGui;
import fi.dy.masa.malilib.gui.interfaces.IDialogHandler;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class ConfigButtonLockedStringList extends ButtonGeneric
{
    private final IConfigLockedStringList config;
    private final IConfigGui configGui;
    @Nullable private final IDialogHandler dialogHandler;

    public ConfigButtonLockedStringList(int x, int y, int width, int height, IConfigLockedStringList config, IConfigGui configGui, @Nullable IDialogHandler dialogHandler)
    {
        super(x, y, width, height, "");

        this.config = config;
        this.configGui = configGui;
        this.dialogHandler = dialogHandler;

        this.updateDisplayString();
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        super.onMouseClickedImpl(mouseX, mouseY, mouseButton);

        if (this.dialogHandler != null)
        {
            this.dialogHandler.openDialog(new GuiLockedStringListEdit(this.config, this.configGui, this.dialogHandler, null));
        }
        else
        {
            GuiBase.openGui(new GuiLockedStringListEdit(this.config, this.configGui, null, GuiUtils.getCurrentScreen()));
        }

        return true;
    }

    @Override
    public void updateDisplayString()
    {
        this.displayString = StringUtils.getClampedDisplayStringRenderlen(this.config.getStrings(), this.width - 10, "[ ", " ]");
    }
}
