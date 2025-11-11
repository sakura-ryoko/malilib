package fi.dy.masa.malilib.gui.button;

import javax.annotation.Nullable;
import net.minecraft.client.gui.Click;
import fi.dy.masa.malilib.config.IConfigStringList;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiStringListEdit;
import fi.dy.masa.malilib.gui.interfaces.IConfigGui;
import fi.dy.masa.malilib.gui.interfaces.IDialogHandler;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class ConfigButtonStringList extends ButtonGeneric
{
    private final IConfigStringList config;
    private final IConfigGui configGui;
    @Nullable private final IDialogHandler dialogHandler;

    public ConfigButtonStringList(int x, int y, int width, int height, IConfigStringList config, IConfigGui configGui, @Nullable IDialogHandler dialogHandler)
    {
        super(x, y, width, height, "");

        this.config = config;
        this.configGui = configGui;
        this.dialogHandler = dialogHandler;

        this.updateDisplayString();
    }

    @Override
    protected boolean onMouseClickedImpl(Click click, boolean doubleClick)
    {
        super.onMouseClickedImpl(click, doubleClick);

        if (this.dialogHandler != null)
        {
            this.dialogHandler.openDialog(new GuiStringListEdit(this.config, this.configGui, this.dialogHandler, null));
        }
        else
        {
            GuiBase.openGui(new GuiStringListEdit(this.config, this.configGui, null, GuiUtils.getCurrentScreen()));
        }

        return true;
    }

    @Override
    public void updateDisplayString()
    {
        this.displayString = StringUtils.getClampedDisplayStringRenderlen(this.config.getStrings(), this.width - 10, "[ ", " ]");
    }
}
