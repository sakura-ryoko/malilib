package fi.dy.masa.malilib.gui.widgets;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import fi.dy.masa.malilib.render.GuiContext;
import fi.dy.masa.malilib.render.RenderUtils;

public class WidgetHoverComponent extends WidgetBase
{
    protected MutableComponent text;

    public WidgetHoverComponent(int x, int y, int width, int height, MutableComponent text)
    {
        super(x, y, width, height);

        this.setText(text);
    }

    protected void setText(MutableComponent text)
    {
        this.text = text;
    }

    protected void setStyle(Style style)
    {
        this.text.setStyle(style);
    }

    protected void append(Component append)
    {
        this.text.append(append);
    }

    public MutableComponent getText()
    {
        return this.text;
    }

    @Override
    public void render(GuiContext ctx, int mouseX, int mouseY, boolean selected)
    {
        super.render(ctx, mouseX, mouseY, selected);
    }

    @Override
    public void postRenderHovered(GuiContext ctx, int mouseX, int mouseY, boolean selected)
    {
        super.postRenderHovered(ctx, mouseX, mouseY, selected);
        RenderUtils.drawHoverText(ctx, mouseX, mouseY, this.text);
    }
}
