package fi.dy.masa.malilib.gui.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.gui.DrawContext;

import fi.dy.masa.malilib.render.GuiLayer;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class WidgetLabel extends WidgetBase
{
    protected final List<String> labels = new ArrayList<>();
    protected final int textColor;
    protected boolean visible = true;
    protected boolean centered;
    protected boolean backgroundEnabled;
    protected int backgroundColor;
    protected int borderULColor;
    protected int borderBRColor;
    protected int borderSize;
    protected GuiLayer layer;

    public WidgetLabel(GuiLayer type, int x, int y, int width, int height, int textColor, String... text)
    {
        this(type, x, y, width, height, textColor, Arrays.asList(text));
    }

    public WidgetLabel(GuiLayer type, int x, int y, int width, int height, int textColor, List<String> lines)
    {
        super(x, y, width, height);

        this.textColor = textColor;
        this.layer = type;

        for (String str : lines)
        {
            this.addLine(str);
        }
    }

    public void addLine(String key, Object... args)
    {
        this.labels.add(StringUtils.translate(key, args));
    }

    public void setCentered(boolean centered)
    {
        this.centered = centered;
    }

    public void setBackgroundProperties(int borderSize, int backgroundColor, int borderULColor, int borderBRColor)
    {
        this.borderSize = borderSize;
        this.backgroundColor = backgroundColor;
        this.borderULColor = borderULColor;
        this.borderBRColor = borderBRColor;
        this.backgroundEnabled = true;
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, boolean selected)
    {
        super.render(drawContext, mouseX, mouseY, selected);

        if (this.visible)
        {
//            RenderUtils.blend(true);
            this.drawLabelBackground(drawContext);

            int fontHeight = this.fontHeight;
            int yCenter = this.y + this.height / 2 + this.borderSize / 2;
            int yTextStart = yCenter - 1 - this.labels.size() * fontHeight / 2;

            for (int i = 0; i < this.labels.size(); ++i)
            {
                String text = this.labels.get(i);

                if (this.centered)
                {
                    this.drawCenteredStringWithShadow(drawContext, this.layer, this.x + this.width / 2, yTextStart + i * fontHeight, this.width, this.height, this.textColor, text);
                }
                else
                {
                    this.drawStringWithShadow(drawContext, this.layer, this.x, yTextStart + i * fontHeight, this.width, this.height, this.textColor, text);
                }
            }
        }
    }

    protected void drawLabelBackground(DrawContext drawContext)
    {
        if (this.backgroundEnabled)
        {
            int bgWidth = this.width + this.borderSize * 2;
            int bgHeight = this.height + this.borderSize * 2;
            int xStart = this.x - this.borderSize;
            int yStart = this.y - this.borderSize;
            GuiLayer type = this.layer != GuiLayer.NONE ? this.layer.goDown() : GuiLayer.NONE;

            RenderUtils.drawRect(drawContext, type, xStart, yStart, bgWidth, bgHeight, this.backgroundColor);

            RenderUtils.drawHorizontalLine(drawContext, type, xStart, yStart           , bgWidth, this.borderULColor);
            RenderUtils.drawHorizontalLine(drawContext, type, xStart, yStart + bgHeight, bgWidth, this.borderBRColor);
            RenderUtils.drawVerticalLine(drawContext, type, xStart          , yStart, bgHeight, this.borderULColor);
            RenderUtils.drawVerticalLine(drawContext, type, xStart + bgWidth, yStart, bgHeight, this.borderBRColor);
        }
    }
}
