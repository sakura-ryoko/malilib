package fi.dy.masa.malilib.gui.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import fi.dy.masa.malilib.render.GuiContext;
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
    public void render(GuiContext ctx, int mouseX, int mouseY, boolean selected)
    {
        super.render(ctx, mouseX, mouseY, selected);

        if (this.visible)
        {
            this.drawLabelBackground(ctx);
            this.drawText(ctx);
        }
    }

    protected void drawText(GuiContext ctx)
    {
        int fontHeight = this.fontHeight;
        int yCenter = this.y + this.height / 2 + this.borderSize / 2;
        int yTextStart = yCenter - 1 - this.labels.size() * fontHeight / 2;

        for (int i = 0; i < this.labels.size(); ++i)
        {
            String text = this.labels.get(i);

            if (this.centered)
            {
                this.drawCenteredStringWithShadow(ctx, this.x + this.width / 2, yTextStart + i * fontHeight, this.textColor, text);
            }
            else
            {
                this.drawStringWithShadow(ctx, this.x, yTextStart + i * fontHeight, this.textColor, text);
            }
        }
    }

    protected void drawLabelBackground(GuiContext ctx)
    {
        if (this.backgroundEnabled)
        {
            int bgWidth = this.width + this.borderSize * 2;
            int bgHeight = this.height + this.borderSize * 2;
            int xStart = this.x - this.borderSize;
            int yStart = this.y - this.borderSize;

            RenderUtils.drawRect(ctx, xStart, yStart, bgWidth, bgHeight, this.backgroundColor);

            RenderUtils.drawHorizontalLine(ctx, xStart, yStart           , bgWidth, this.borderULColor);
            RenderUtils.drawHorizontalLine(ctx, xStart, yStart + bgHeight, bgWidth, this.borderBRColor);
            RenderUtils.drawVerticalLine(ctx, xStart          , yStart, bgHeight, this.borderULColor);
            RenderUtils.drawVerticalLine(ctx, xStart + bgWidth, yStart, bgHeight, this.borderBRColor);
        }
    }
}
