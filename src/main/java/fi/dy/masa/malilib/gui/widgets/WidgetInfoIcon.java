package fi.dy.masa.malilib.gui.widgets;

import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.render.GuiContext;

public class WidgetInfoIcon extends WidgetHoverInfo
{
    protected final IGuiIcon icon;

    public WidgetInfoIcon(int x, int y, IGuiIcon icon, String key, Object... args)
    {
        super(x, y, icon.getWidth(), icon.getHeight(), key, args);

        this.icon = icon;
    }

    @Override
    public void render(GuiContext ctx, int mouseX, int mouseY, boolean selected)
    {
        super.render(ctx, mouseX, mouseY, selected);
        this.icon.renderAt(ctx, this.x, this.y, this.zLevel, false, selected);
    }
}
