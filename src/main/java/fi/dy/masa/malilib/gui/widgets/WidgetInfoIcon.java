package fi.dy.masa.malilib.gui.widgets;

import net.minecraft.client.gui.DrawContext;

import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.render.RenderUtils;

public class WidgetInfoIcon extends WidgetHoverInfo
{
    protected final IGuiIcon icon;

    public WidgetInfoIcon(int x, int y, IGuiIcon icon, String key, Object... args)
    {
        super(x, y, icon.getWidth(), icon.getHeight(), key, args);

        this.icon = icon;
    }

    @Override
    public void render(int mouseX, int mouseY, boolean selected, DrawContext drawContext)
    {
        RenderUtils.color(1f, 1f, 1f, 1f);
        this.bindTexture(this.icon.getTexture());
        this.icon.renderAt(this.x, this.y, this.zLevel, false, selected, drawContext);
    }
}
