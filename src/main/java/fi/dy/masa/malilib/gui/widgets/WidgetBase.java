package fi.dy.masa.malilib.gui.widgets;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.render.GuiContext;
import fi.dy.masa.malilib.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

public abstract class WidgetBase
{
    protected final Minecraft mc;
    protected final Font textRenderer;
    protected final int fontHeight;
    protected GuiContext guiContext;
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected int zLevel;

    public WidgetBase(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.mc = Minecraft.getInstance();
        this.textRenderer = this.mc.font;
        this.fontHeight = this.textRenderer.lineHeight;
    }

    public int getX()
    {
        return this.x;
    }

    public int getY()
    {
        return this.y;
    }

    public void setPosition(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public void setZLevel(int zLevel)
    {
        this.zLevel = zLevel;
    }

    public int getWidth()
    {
        return this.width;
    }

    public int getHeight()
    {
        return this.height;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    public boolean isMouseOver(int mouseX, int mouseY)
    {
        return mouseX >= this.x && mouseX < this.x + this.width &&
               mouseY >= this.y && mouseY < this.y + this.height;
    }

    public boolean onMouseClicked(MouseButtonEvent click, boolean doubleClick)
    {
        if (this.isMouseOver((int) click.x(), (int) click.y()))
        {
            return this.onMouseClickedImpl(click, doubleClick);
        }

        return false;
    }

    protected boolean onMouseClickedImpl(MouseButtonEvent click, boolean doubleClick)
    {
        return false;
    }

    public void onMouseReleased(MouseButtonEvent click)
    {
        this.onMouseReleasedImpl(click);
    }

    public void onMouseReleasedImpl(MouseButtonEvent click)
    {
    }

    public boolean onMouseDragged(MouseButtonEvent click, double dragXAmount, double dragYAmount)
    {
        if (this.isMouseOver((int) click.x(), (int) click.y()))
        {
            return this.onMouseDraggedImpl(click, dragXAmount, dragYAmount);
        }

        return false;
    }

    public boolean onMouseDraggedImpl(MouseButtonEvent click, double dragXAmount, double dragYAmount)
    {
        return false;
    }

    public boolean onMouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount)
    {
        if (this.isMouseOver((int) mouseX, (int) mouseY))
        {
            return this.onMouseScrolledImpl(mouseX, mouseY, horizontalAmount, verticalAmount);
        }

        return false;
    }

    public boolean onMouseScrolledImpl(double mouseX, double mouseY, double horizontalAmount, double verticalAmount)
    {
        return false;
    }

    public boolean onKeyTyped(KeyEvent input)
    {
        return this.onKeyTypedImpl(input);
    }

    protected boolean onKeyTypedImpl(KeyEvent input)
    {
        return false;
    }

    public boolean onCharTyped(CharacterEvent input)
    {
        return this.onCharTypedImpl(input);
    }

    protected boolean onCharTypedImpl(CharacterEvent input)
    {
        return false;
    }

    /**
     * Returns true if this widget can be selected by clicking at the given point
     */
    public boolean canSelectAt(MouseButtonEvent click)
    {
        return this.isMouseOver((int) click.x(), (int) click.y());
    }

    public int getStringWidth(String text)
    {
        return this.textRenderer.width(text);
    }

    public void drawString(GuiContext ctx, int x, int y, int color, String text)
    {
	    ctx.drawString(this.textRenderer, text, x, y, color, false);
    }

    public void drawCenteredString(GuiContext ctx, int x, int y, int color, String text)
    {
	    ctx.drawString(this.textRenderer, text, x - this.getStringWidth(text) / 2, y, color, false);
    }

    public void drawStringWithShadow(GuiContext ctx, int x, int y, int color, String text)
    {
	    ctx.drawString(this.textRenderer, text, x, y, color);
    }

    public void drawCenteredStringWithShadow(GuiContext ctx, int x, int y, int color, String text)
    {
	    ctx.drawCenteredString(this.textRenderer, text, x, y, color);
    }

    public void drawBackgroundMask(GuiContext ctx)
    {
        RenderUtils.drawTexturedRect(ctx, GuiBase.BG_TEXTURE, this.x + 1, this.y + 1, 0, 0, this.width - 2, this.height - 2);
    }

    public void render(GuiContext ctx, int mouseX, int mouseY, boolean selected)
    {
        if (this.guiContext == null || !this.guiContext.equals(ctx))
        {
            this.guiContext = ctx;
        }
    }

    public void postRenderHovered(GuiContext ctx, int mouseX, int mouseY, boolean selected)
    {
        if (this.guiContext == null || !this.guiContext.equals(ctx))
        {
            this.guiContext = ctx;
        }
    }
}
