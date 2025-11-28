package fi.dy.masa.malilib.gui.interfaces;

import fi.dy.masa.malilib.render.GuiContext;
import net.minecraft.resources.Identifier;

public interface IGuiIcon
{
    int getWidth();

    int getHeight();

    int getU();

    int getV();

    void renderAt(GuiContext ctx, int x, int y, float zLevel, boolean enabled, boolean selected);

    Identifier getTexture();
}
