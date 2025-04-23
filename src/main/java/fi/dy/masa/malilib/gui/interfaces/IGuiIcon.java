package fi.dy.masa.malilib.gui.interfaces;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import fi.dy.masa.malilib.render.GuiLayer;

public interface IGuiIcon
{
    int getWidth();

    int getHeight();

    int getU();

    int getV();

    void renderAt(DrawContext drawContext, GuiLayer type, int x, int y, float zLevel, boolean enabled, boolean selected);

    Identifier getTexture();
}
