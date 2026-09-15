package fi.dy.masa.malilib.gui.interfaces;

import fi.dy.masa.malilib.render.GuiContext;
import net.minecraft.resources.Identifier;

public interface IGuiIcon
{
    int getWidth();

    int getHeight();

    int getU();

    int getV();

    void renderAt(GuiContext ctx, int x, int y, boolean enabled, boolean selected);

    @Deprecated(forRemoval = true)
    default void renderAt(GuiContext ctx, int x, int y, float z, boolean enabled, boolean selected)
    {
        renderAt(ctx, x, y, enabled, selected);
    }

    Identifier getTexture();
}
