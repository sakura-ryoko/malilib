package fi.dy.masa.malilib.gui.widgets;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.render.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;

public abstract class WidgetBase
{
    protected final MinecraftClient mc;
    protected final TextRenderer textRenderer;
    protected final int fontHeight;
    protected DrawContext drawContext;
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
        this.mc = MinecraftClient.getInstance();
        this.textRenderer = this.mc.textRenderer;
        this.fontHeight = this.textRenderer.fontHeight;
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

    public boolean onMouseClicked(Click click, boolean doubleClick)
    {
        if (this.isMouseOver((int) click.x(), (int) click.y()))
        {
            return this.onMouseClickedImpl(click, doubleClick);
        }

        return false;
    }

    protected boolean onMouseClickedImpl(Click click, boolean doubleClick)
    {
        return false;
    }

    public void onMouseReleased(Click click)
    {
        this.onMouseReleasedImpl(click);
    }

    public void onMouseReleasedImpl(Click click)
    {
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

    public boolean onKeyTyped(KeyInput input)
    {
        return this.onKeyTypedImpl(input);
    }

    protected boolean onKeyTypedImpl(KeyInput input)
    {
        return false;
    }

    public boolean onCharTyped(CharInput input)
    {
        return this.onCharTypedImpl(input);
    }

    protected boolean onCharTypedImpl(CharInput input)
    {
        return false;
    }

    /**
     * Returns true if this widget can be selected by clicking at the given point
     */
    public boolean canSelectAt(Click click)
    {
        return this.isMouseOver((int) click.x(), (int) click.y());
    }

    public int getStringWidth(String text)
    {
        return this.textRenderer.getWidth(text);
    }

    public void drawString(DrawContext drawContext, int x, int y, int color, String text)
    {
        drawContext.drawText(this.textRenderer, text, x, y, color, false);
    }

    public void drawCenteredString(DrawContext drawContext, int x, int y, int color, String text)
    {
        drawContext.drawText(this.textRenderer, text, x - this.getStringWidth(text) / 2, y, color, false);
    }

    public void drawStringWithShadow(DrawContext drawContext, int x, int y, int color, String text)
    {
        drawContext.drawTextWithShadow(this.textRenderer, text, x, y, color);
    }

    public void drawCenteredStringWithShadow(DrawContext drawContext, int x, int y, int color, String text)
    {
//        final int startX = x + 2;
//        final int endX = x + width - 2;
//        final int endY = y + height;
//        final int centerX = (startX + endX) / 2;
//        final int textWidth = this.getStringWidth(text);
////        final int xAdj = endX - startX;
//        final int yAdj = (y + endY - 9) / 2 + 1;
//        final int centerAdj = MathHelper.clamp(centerX, startX + textWidth / 2, endX - textWidth / 2);

        drawContext.drawCenteredTextWithShadow(this.textRenderer, text, x, y, color);
    }

    public void drawBackgroundMask(DrawContext drawContext)
    {
        RenderUtils.drawTexturedRect(drawContext, GuiBase.BG_TEXTURE, this.x + 1, this.y + 1, 0, 0, this.width - 2, this.height - 2);
    }

    public void render(DrawContext drawContext, int mouseX, int mouseY, boolean selected)
    {
        if (this.drawContext == null || !this.drawContext.equals(drawContext))
        {
            this.drawContext = drawContext;
        }
    }

    public void postRenderHovered(DrawContext drawContext, int mouseX, int mouseY, boolean selected)
    {
        if (this.drawContext == null || !this.drawContext.equals(drawContext))
        {
            this.drawContext = drawContext;
        }
    }
}
