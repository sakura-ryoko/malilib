package fi.dy.masa.malilib.gui.widgets;

import net.minecraft.client.gui.DrawContext;

import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;

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
//        RenderUtils.color(1f, 1f, 1f, 1f);
//        this.bindTexture(this.icon.getTexture(), drawContext);
        this.icon.renderAt(drawContext, this.x, this.y, this.zLevel, false, selected);
    }
}
