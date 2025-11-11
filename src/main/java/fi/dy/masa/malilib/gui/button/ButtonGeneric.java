package fi.dy.masa.malilib.gui.button;

import javax.annotation.Nullable;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import org.apache.commons.lang3.StringUtils;
import fi.dy.masa.malilib.gui.LeftRight;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.render.RenderUtils;

public class ButtonGeneric extends ButtonBase
{
    @Nullable
    protected final IGuiIcon icon;
    protected LeftRight alignment = LeftRight.LEFT;
    protected boolean textCentered;
    protected boolean renderDefaultBackground = true;

    public ButtonGeneric(int x, int y, int width, boolean rightAlign, String translationKey, Object... args)
    {
        this(x, y, width, 20, fi.dy.masa.malilib.util.StringUtils.translate(translationKey, args));

        if (rightAlign)
        {
            this.x = x - this.width;
        }
    }

    public ButtonGeneric(int x, int y, int width, int height, String text, String... hoverStrings)
    {
        this(x, y, width, height, text, null, hoverStrings);

        this.textCentered = true;
    }

    public ButtonGeneric(int x, int y, int width, int height, String text, @Nullable IGuiIcon icon, String... hoverStrings)
    {
        super(x, y, width, height, text);

        this.icon = icon;

        if (width == -1 && icon != null)
        {
            this.width += icon.getWidth() + 8;
        }

        if (hoverStrings.length > 0)
        {
            this.setHoverStrings(hoverStrings);
        }
    }

    public ButtonGeneric(int x, int y, IGuiIcon icon, String... hoverStrings)
    {
        this(x, y, icon.getWidth(), icon.getHeight(), "", icon, hoverStrings);
        this.setRenderDefaultBackground(false);
    }

    @Override
    public ButtonGeneric setActionListener(@Nullable IButtonActionListener actionListener)
    {
        this.actionListener = actionListener;
        return this;
    }

    public ButtonGeneric setTextCentered(boolean centered)
    {
        this.textCentered = centered;
        return this;
    }

    /**
     * Set the icon aligment.<br>
     * Note: Only LEFT and RIGHT alignments work properly.
     * @param alignment ()
     * @return ()
     */
    public ButtonGeneric setIconAlignment(LeftRight alignment)
    {
        this.alignment = alignment;
        return this;
    }

    public ButtonGeneric setRenderDefaultBackground(boolean render)
    {
        this.renderDefaultBackground = render;
        return this;
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, boolean selected)
    {
        super.render(drawContext, mouseX, mouseY, selected);

        if (this.visible)
        {
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

            this.drawBackground(drawContext);
            this.drawIcon(drawContext);
            this.drawText(drawContext);
        }
    }

    private void drawBackground(DrawContext drawContext)
    {
        if (this.renderDefaultBackground)
        {
            drawContext.drawGuiTexture(RenderPipelines.GUI_TEXTURED, this.getTexture(this.hovered), this.x, this.y, this.width, this.height);
        }
    }

    private void drawIcon(DrawContext drawContext)
    {
        if (this.icon != null)
        {
            int offset = this.renderDefaultBackground ? 4 : 0;
            //int x = this.alignment == LeftRight.LEFT ? this.x + offset : this.x + this.width - this.icon.getWidth() - offset;
            int x = this.alignment.getLeft(this.x, this.x + this.width, this.icon.getWidth(), offset);
            int y = this.y + (this.height - this.icon.getHeight()) / 2;
            int u = this.icon.getU() + this.getTextureOffset(this.hovered) * this.icon.getWidth(); // FIXME: What happened here.

            RenderUtils.drawTexturedRect(drawContext, this.icon.getTexture(), x, y, u, this.icon.getV(), this.icon.getWidth(), this.icon.getHeight());
        }
    }

    private void drawText(DrawContext drawContext)
    {
        if (StringUtils.isBlank(this.displayString) == false)
        {
            int y = this.y + (this.height - 8) / 2;
            int color = 0xFFE0E0E0;

            if (this.enabled == false)
            {
                color = 0xFFA0A0A0;
            }
            else if (this.hovered)
            {
                color = 0xFFFFFFFF;
            }

            if (this.textCentered)
            {
                int x = this.x + this.width / 2;

                this.drawCenteredStringWithShadow(drawContext, x, y, color, this.displayString);
            }
            else
            {
                int x = this.x + 6;

                if (this.icon != null && this.alignment == LeftRight.LEFT)
                {
                    x += this.icon.getWidth() + 2;
                }

                this.drawStringWithShadow(drawContext, x, y, color, this.displayString);
            }
        }
    }
}
