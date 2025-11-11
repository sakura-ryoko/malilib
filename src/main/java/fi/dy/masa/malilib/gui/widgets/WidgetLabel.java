package fi.dy.masa.malilib.gui.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.gui.DrawContext;
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

    public WidgetLabel(int x, int y, int width, int height, int textColor, String... text)
    {
        this(x, y, width, height, textColor, Arrays.asList(text));
    }

    public WidgetLabel(int x, int y, int width, int height, int textColor, List<String> lines)
    {
        super(x, y, width, height);

        this.textColor = textColor;

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
            this.drawLabelBackground(drawContext);
            this.drawText(drawContext);
        }
    }

    protected void drawText(DrawContext drawContext)
    {
        int fontHeight = this.fontHeight;
        int yCenter = this.y + this.height / 2 + this.borderSize / 2;
        int yTextStart = yCenter - 1 - this.labels.size() * fontHeight / 2;

        for (int i = 0; i < this.labels.size(); ++i)
        {
            String text = this.labels.get(i);

            if (this.centered)
            {
                this.drawCenteredStringWithShadow(drawContext, this.x + this.width / 2, yTextStart + i * fontHeight, this.textColor, text);
            }
            else
            {
                this.drawStringWithShadow(drawContext, this.x, yTextStart + i * fontHeight, this.textColor, text);
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

            RenderUtils.drawRect(drawContext, xStart, yStart, bgWidth, bgHeight, this.backgroundColor);

            RenderUtils.drawHorizontalLine(drawContext, xStart, yStart           , bgWidth, this.borderULColor);
            RenderUtils.drawHorizontalLine(drawContext, xStart, yStart + bgHeight, bgWidth, this.borderBRColor);
            RenderUtils.drawVerticalLine(drawContext, xStart          , yStart, bgHeight, this.borderULColor);
            RenderUtils.drawVerticalLine(drawContext, xStart + bgWidth, yStart, bgHeight, this.borderBRColor);
        }
    }
}
