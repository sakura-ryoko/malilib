package fi.dy.masa.malilib.gui.widgets;

import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import net.minecraft.client.gui.DrawContext;

public class WidgetInfoIcon extends WidgetHoverInfo
{
    protected final IGuiIcon icon;

    public WidgetInfoIcon(int x, int y, IGuiIcon icon, String key, Object... args)
    {
        super(x, y, icon.getWidth(), icon.getHeight(), key, args);

        this.icon = icon;
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, boolean selected)
    {
        super.render(drawContext, mouseX, mouseY, selected);
        this.icon.renderAt(drawContext, this.x, this.y, this.zLevel, false, selected);
    }
}
