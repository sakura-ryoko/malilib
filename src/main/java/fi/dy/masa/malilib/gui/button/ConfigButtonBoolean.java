package fi.dy.masa.malilib.gui.button;

import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.malilib.gui.GuiBase;
import net.minecraft.client.gui.Click;

public class ConfigButtonBoolean extends ButtonGeneric
{
    private final IConfigBoolean config;

    public ConfigButtonBoolean(int x, int y, int width, int height, IConfigBoolean config)
    {
        super(x, y, width, height, "");
        this.config = config;

        this.updateDisplayString();
    }

    @Override
    protected boolean onMouseClickedImpl(Click click, boolean doubleClick)
    {
        this.config.toggleBooleanValue();
        this.updateDisplayString();

        return super.onMouseClickedImpl(click, doubleClick);
    }

    @Override
    public void updateDisplayString()
    {
        String valueStr = String.valueOf(this.config.getBooleanValue());

        if (this.config.getBooleanValue())
        {
            this.displayString = GuiBase.TXT_DARK_GREEN + valueStr + GuiBase.TXT_RST;
        }
        else
        {
            this.displayString = GuiBase.TXT_DARK_RED + valueStr + GuiBase.TXT_RST;
        }
    }
}
