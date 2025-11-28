package fi.dy.masa.malilib.gui.widgets;

import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.render.GuiContext;
import fi.dy.masa.malilib.render.RenderUtils;

public class WidgetIcon extends WidgetBase
{
    protected final IGuiIcon icon;

    public WidgetIcon(int x, int y, IGuiIcon icon)
    {
        super(x, y, icon.getWidth(), icon.getHeight());

        this.icon = icon;
    }

    public void render(GuiContext ctx, boolean enabled, boolean selected)
    {
        this.icon.renderAt(ctx, this.x, this.y, this.zLevel, enabled, selected);

        if (selected)
        {
            RenderUtils.drawOutlinedBox(ctx, this.x, this.y, this.width, this.height, 0x20C0C0C0, 0xE0FFFFFF);
        }
    }
}
