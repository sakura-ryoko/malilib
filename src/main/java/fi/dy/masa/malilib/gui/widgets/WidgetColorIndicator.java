package fi.dy.masa.malilib.gui.widgets;

import java.util.function.IntConsumer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.IConfigColor;
import fi.dy.masa.malilib.config.options.ConfigColor;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiColorEditorHSV;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.Color4f;

public class WidgetColorIndicator extends WidgetBase
{
    protected final IConfigColor config;
    protected final ImmutableList<String> hoverText;

    public WidgetColorIndicator(int x, int y, int width, int height, Color4f color, IntConsumer consumer)
    {
        this(x, y, width, height, new ConfigColor("color_indicator_widget", color));

        ((ConfigColor) this.config).setValueChangeCallback((cfg) -> consumer.accept(cfg.getIntegerValue()) );
    }

    public WidgetColorIndicator(int x, int y, int width, int height, IConfigColor config)
    {
        super(x, y, width, height);

        this.config = config;
        this.hoverText = ImmutableList.of(StringUtils.translate("malilib.hover.color_indicator.open_color_editor"));
    }

    @Override
    protected boolean onMouseClickedImpl(Click click, boolean doubleClick)
    {
        GuiColorEditorHSV gui = new GuiColorEditorHSV(this.config, null, GuiUtils.getCurrentScreen());
        GuiBase.openGui(gui);
        return true;
    }

    @Override
    public void postRenderHovered(DrawContext drawContext, int mouseX, int mouseY, boolean selected)
    {
        super.postRenderHovered(drawContext, mouseX, mouseY, selected);
        RenderUtils.drawHoverText(drawContext, mouseX, mouseY, this.hoverText);
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, boolean selected)
    {
        super.render(drawContext, mouseX, mouseY, selected);
        int x = this.getX();
        int y = this.getY();
        int z = this.zLevel;
        int width = this.getWidth();
        int height = this.getHeight();

        RenderUtils.drawRect(drawContext, x    , y    , width    , height    , 0xFFFFFFFF);
        RenderUtils.drawRect(drawContext, x + 1, y + 1, width - 2, height - 2, 0xFF000000);
        RenderUtils.drawRect(drawContext, x + 2, y + 2, width - 4, height - 4, 0xFF000000 | this.config.getIntegerValue());
    }
}
