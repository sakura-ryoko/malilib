package fi.dy.masa.malilib.gui.button;

import javax.annotation.Nullable;

import net.minecraft.client.input.MouseButtonEvent;

import fi.dy.masa.malilib.config.IConfigBlockStateList;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiBlockStateListEdit;
import fi.dy.masa.malilib.gui.interfaces.IConfigGui;
import fi.dy.masa.malilib.gui.interfaces.IDialogHandler;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class ConfigButtonBlockStateList extends ButtonGeneric
{
    private final IConfigBlockStateList config;
    private final IConfigGui configGui;
    @Nullable
    private final IDialogHandler dialogHandler;

    public ConfigButtonBlockStateList(int x, int y, int width, int height, IConfigBlockStateList config, IConfigGui configGui, @Nullable IDialogHandler dialogHandler)
    {
        super(x, y, width, height, "");

        this.config = config;
        this.configGui = configGui;
        this.dialogHandler = dialogHandler;

        this.updateDisplayString();
    }

    @Override
    protected boolean onMouseClickedImpl(MouseButtonEvent click, boolean doubleClick)
    {
        super.onMouseClickedImpl(click, doubleClick);

        if (this.dialogHandler != null)
        {
            this.dialogHandler.openDialog(new GuiBlockStateListEdit(this.config, this.configGui, this.dialogHandler, null));
        }
        else
        {
            GuiBase.openGui(new GuiBlockStateListEdit(this.config, this.configGui, null, GuiUtils.getCurrentScreen()));
        }

        return true;
    }

    @Override
    public void updateDisplayString()
    {
        this.displayString = "[ "+StringUtils.translate("malilib.gui.button.block_states", this.config.getBlockStates().size())+" ]";
    }
}
