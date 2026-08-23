package fi.dy.masa.malilib.gui.widgets;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;

import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;

public class WidgetInfoIconComponent extends WidgetHoverComponent
{
    protected final IGuiIcon icon;

    public WidgetInfoIconComponent(int x, int y, IGuiIcon icon, MutableText text)
    {
        super(x, y, icon.getWidth(), icon.getHeight(),text);

        this.icon = icon;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, boolean selected)
    {
        super.render(ctx, mouseX, mouseY, selected);
        this.icon.renderAt(ctx, this.x, this.y, this.zLevel, false, selected);
    }
}
