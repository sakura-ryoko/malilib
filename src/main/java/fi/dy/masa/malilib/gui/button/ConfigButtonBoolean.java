package fi.dy.masa.malilib.gui.button;

import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.client.input.MouseButtonEvent;

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
    protected boolean onMouseClickedImpl(MouseButtonEvent click, boolean doubleClick)
    {
        this.config.toggleBooleanValue();
        this.updateDisplayString();

        return super.onMouseClickedImpl(click, doubleClick);
    }

    @Override
    public void updateDisplayString()
    {
        this.displayString = this.config.getBooleanValue()
                             ? StringUtils.translate("malilib.gui.button.true")
                             : StringUtils.translate("malilib.gui.button.false");
    }
}
