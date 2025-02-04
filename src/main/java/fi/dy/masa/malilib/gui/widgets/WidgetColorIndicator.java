package fi.dy.masa.malilib.gui.widgets;

import java.util.function.IntConsumer;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.DrawContext;
import fi.dy.masa.malilib.config.IConfigInteger;
import fi.dy.masa.malilib.config.options.ConfigInteger;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiColorEditorHSV;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.Color4f;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.StringUtils;

import com.mojang.blaze3d.systems.RenderSystem;

public class WidgetColorIndicator extends WidgetBase
{
    protected final IConfigInteger config;
    protected final ImmutableList<String> hoverText;

    public WidgetColorIndicator(int x, int y, int width, int height, Color4f color, IntConsumer consumer)
    {
        this(x, y, width, height, new ConfigInteger("", color.intValue, ""));

        ((ConfigInteger) this.config).setValueChangeCallback((cfg) -> consumer.accept(cfg.getIntegerValue()) );
    }

    public WidgetColorIndicator(int x, int y, int width, int height, IConfigInteger config)
    {
        super(x, y, width, height);

        this.config = config;
        this.hoverText = ImmutableList.of(StringUtils.translate("malilib.hover.color_indicator.open_color_editor"));
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        GuiColorEditorHSV gui = new GuiColorEditorHSV(this.config, null, GuiUtils.getCurrentScreen());
        GuiBase.openGui(gui);
        return true;
    }

    @Override
    public void postRenderHovered(int mouseX, int mouseY, boolean selected, DrawContext drawContext)
    {
        RenderUtils.drawHoverText(mouseX, mouseY, this.hoverText, drawContext);
    }

    @Override
    public void render(int mouseX, int mouseY, boolean selected, DrawContext drawContext)
    {
        int x = this.getX();
        int y = this.getY();
        int z = this.zLevel;
        int width = this.getWidth();
        int height = this.getHeight();

        RenderSystem.enableDepthTest();
        RenderUtils.drawRect(x    , y    , width    , height    , 0xFFFFFFFF, z);
        RenderUtils.drawRect(x + 1, y + 1, width - 2, height - 2, 0xFF000000, z);
        RenderUtils.drawRect(x + 2, y + 2, width - 4, height - 4, 0xFF000000 | this.config.getIntegerValue(), z);
        RenderSystem.disableDepthTest();
    }
}
