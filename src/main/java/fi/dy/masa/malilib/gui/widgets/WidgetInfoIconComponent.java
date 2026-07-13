package fi.dy.masa.malilib.gui.widgets;

import net.minecraft.network.chat.MutableComponent;

import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.render.GuiContext;

public class WidgetInfoIconComponent extends WidgetHoverComponent
{
    protected final IGuiIcon icon;

    public WidgetInfoIconComponent(int x, int y, IGuiIcon icon, MutableComponent text)
    {
        super(x, y, icon.getWidth(), icon.getHeight(),text);

        this.icon = icon;
    }

    @Override
    public void render(GuiContext ctx, int mouseX, int mouseY, boolean selected)
    {
        super.render(ctx, mouseX, mouseY, selected);
        this.icon.renderAt(ctx, this.x, this.y, this.zLevel, false, selected);
    }
}
