package fi.dy.masa.malilib.config.gui;

import java.util.List;
import net.minecraft.client.gui.DrawContext;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;

public class GuiModConfigs extends GuiConfigsBase
{
    protected final List<ConfigOptionWrapper> configs;

    public GuiModConfigs(String modId, List<? extends IConfigBase> configs, String titleKey, Object... args)
    {
        this(modId, ConfigOptionWrapper.createFor(configs), false, titleKey, args);
    }

    public GuiModConfigs(String modId, List<ConfigOptionWrapper> wrappers, boolean unused, String titleKey, Object... args)
    {
        super(10, 0, modId, null, titleKey, args);

        this.configs = wrappers;
    }

    @Override
    protected int getBrowserHeight()
    {
        return this.height - 70;
    }

    @Override
    public List<ConfigOptionWrapper> getConfigs()
    {
        return this.configs;
    }

    @Override
    protected void drawTitle(DrawContext drawContext, int mouseX, int mouseY, float partialTicks)
    {
        // NO-OP
    }
}
