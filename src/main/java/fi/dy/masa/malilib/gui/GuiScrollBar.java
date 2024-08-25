package fi.dy.masa.malilib.gui;

import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.render.RenderUtils;

public class GuiScrollBar
{
    @Nullable protected final IGuiIcon barTexture;
    protected final MinecraftClient mc = MinecraftClient.getInstance();
    protected boolean mouseOver = false;
    protected boolean dragging = false;
    protected boolean renderScrollbarBackground = true;
    protected int currentValue = 0;
    protected int maxValue = 100;
    protected int backgroundColor = 0x44FFFFFF;
    protected int foregroundColor = 0xFFFFFFFF;
    protected int dragStartValue = 0;
    protected int dragStartY = 0;

    public GuiScrollBar()
    {
        this(null);
    }

    public GuiScrollBar(@Nullable IGuiIcon barTexture)
    {
        this.barTexture = barTexture;
    }

    public GuiScrollBar setRenderBarBackground(boolean render)
    {
        this.renderScrollbarBackground = render;
        return this;
    }

    public int getValue()
    {
        return this.currentValue;
    }

    public void setValue(int value)
    {
        this.currentValue = MathHelper.clamp(value, 0, this.maxValue);
    }

    public void offsetValue(int offset)
    {
        this.setValue(this.currentValue + offset);
    }

    public int getMaxValue()
    {
        return this.maxValue;
    }

    public void setMaxValue(int maxValue)
    {
        this.maxValue = Math.max(0, maxValue);
        this.currentValue = Math.min(this.currentValue, this.maxValue);
    }

    public boolean wasMouseOver()
    {
        return this.mouseOver;
    }

    public void setIsDragging(boolean isDragging)
    {
        this.dragging = isDragging;
    }

    public void render(int mouseX, int mouseY, float partialTicks, int xPosition, int yPosition, int width, int height, int totalHeight, DrawContext drawContext)
    {
        if (this.renderScrollbarBackground)
        {
            RenderUtils.drawRect(xPosition, yPosition, width, height, this.backgroundColor);
        }

        if (totalHeight > 0)
        {
            int slideHeight = height - 2;
            float relative = Math.min(1.0F, (float) slideHeight / (float) totalHeight);
            int barHeight = (int) (relative * slideHeight);
            int barTravel = slideHeight - barHeight;
            int barPosition = yPosition + 1 + (this.maxValue > 0 ? (int) ((this.currentValue / (float) this.maxValue) * barTravel) : 0);

            if (this.barTexture != null && barHeight >= 4)
            {
                RenderUtils.color(1f, 1f, 1f, 1f);
                RenderUtils.bindTexture(this.barTexture.getTexture());
                int u = this.barTexture.getU();
                int v = this.barTexture.getV();
                int w = this.barTexture.getWidth();
                int h = this.barTexture.getHeight();

                RenderUtils.drawTexturedRect(xPosition + 1, barPosition                , u, v        , w, barHeight - 2);
                RenderUtils.drawTexturedRect(xPosition + 1, barPosition + barHeight - 2, u, v + h - 2, w, 2            );
            }
            else
            {
                RenderUtils.drawRect(xPosition + 1, barPosition, width - 2, barHeight, this.foregroundColor);
            }

            RenderUtils.forceDraw(drawContext);

            this.mouseOver = mouseX > xPosition && mouseX < xPosition + width && mouseY > barPosition && mouseY < barPosition + barHeight;
            this.handleDrag(mouseY, barTravel);
        }
    }

    public void handleDrag(int mouseY, int barTravel)
    {
        if (this.dragging)
        {
            float valuePerPixel = (float) this.maxValue / barTravel;
            this.setValue((int) (this.dragStartValue + ((mouseY - this.dragStartY) * valuePerPixel)));
        }
        else
        {
            this.dragStartY = mouseY;
            this.dragStartValue = this.currentValue;
        }
    }
}
