package fi.dy.masa.malilib.gui.widgets;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import fi.dy.masa.malilib.render.RenderUtils;

public class WidgetHoverComponent extends WidgetBase
{
    protected MutableText text;

    public WidgetHoverComponent(int x, int y, int width, int height, MutableText text)
    {
        super(x, y, width, height);

        this.setText(text);
    }

    protected void setText(MutableText text)
    {
        this.text = text;
    }

    protected void setStyle(Style style)
    {
        this.text.setStyle(style);
    }

    protected void append(Text append)
    {
        this.text.append(append);
    }

    public MutableText getText()
    {
        return this.text;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, boolean selected)
    {
        super.render(ctx, mouseX, mouseY, selected);
    }

    @Override
    public void postRenderHovered(DrawContext ctx, int mouseX, int mouseY, boolean selected)
    {
        super.postRenderHovered(ctx, mouseX, mouseY, selected);
        RenderUtils.drawHoverText(ctx, mouseX, mouseY, this.text);
    }
}
