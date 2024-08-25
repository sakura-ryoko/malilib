package fi.dy.masa.malilib.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.render.RenderUtils;

public enum MaLiLibIcons implements IGuiIcon
{
    ARROW_UP            (108,   0, 15, 15),
    ARROW_DOWN          (108,  15, 15, 15),
    PLUS                (108,  30, 15, 15),
    MINUS               (108,  45, 15, 15),
    BTN_SLIDER          (153,   0, 16, 16),
    BTN_TXTFIELD        (153,  16, 16, 16),
    BTN_PLUSMINUS_16    (153,  32, 16, 16),
    SEARCH              (201,   0, 12, 12, 0, 0);

    public static final Identifier TEXTURE = Identifier.of(MaLiLibReference.MOD_ID, "textures/gui/gui_widgets.png");

    private final int u;
    private final int v;
    private final int w;
    private final int h;
    private final int hoverOffU;
    private final int hoverOffV;

    MaLiLibIcons(int u, int v, int w, int h)
    {
        this(u, v, w, h, w, 0);
    }

    MaLiLibIcons(int u, int v, int w, int h, int hoverOffU, int hoverOffV)
    {
        this.u = u;
        this.v = v;
        this.w = w;
        this.h = h;
        this.hoverOffU = hoverOffU;
        this.hoverOffV = hoverOffV;
    }

    @Override
    public int getWidth()
    {
        return this.w;
    }

    @Override
    public int getHeight()
    {
        return this.h;
    }

    @Override
    public int getU()
    {
        return this.u;
    }

    @Override
    public int getV()
    {
        return this.v;
    }

    @Override
    public void renderAt(int x, int y, float zLevel, boolean enabled, boolean selected, DrawContext drawContext)
    {
        int u = this.u;
        int v = this.v;

        if (enabled)
        {
            u += this.hoverOffU;
            v += this.hoverOffV;
        }

        if (selected)
        {
            u += this.hoverOffU;
            v += this.hoverOffV;
        }

        //RenderUtils.drawTexturedRect(x, y, u, v, this.w, this.h, zLevel);
        RenderUtils.drawTexturedRect(this.getTexture(), x, y, u, v, this.w, this.h, zLevel, drawContext);
        RenderUtils.forceDraw(drawContext);
    }

    @Override
    public Identifier getTexture()
    {
        return TEXTURE;
    }
}
