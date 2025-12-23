package malilib.gui.widget;

import java.util.Arrays;
import java.util.List;

import malilib.config.value.HorizontalAlignment;
import malilib.gui.util.BackgroundSettings;
import malilib.gui.util.BorderSettings;
import malilib.gui.util.ScreenContext;
import malilib.render.text.StringListRenderer;
import malilib.render.text.StyledText;
import malilib.render.text.StyledTextLine;

public class LabelWidget extends InteractableWidget
{
    protected final StringListRenderer stringListRenderer = new StringListRenderer();
    protected boolean useBackgroundForHoverOverflow = true;
    protected int totalHeight;
    protected int totalWidth;

    public LabelWidget()
    {
        this(-1, -1, 0xFFFFFFFF);
    }

    public LabelWidget(String translationKey, Object... args)
    {
        this(0xFFFFFFFF, translationKey, args);
    }

    public LabelWidget(int textColor, String translationKey, Object... args)
    {
        this(-1, -1, textColor);

        this.translateSetLines(translationKey, args);
    }

    public LabelWidget(int textColor)
    {
        this(-1, -1, textColor);
    }

    public LabelWidget(int width, int height)
    {
        this(width, height, 0xFFFFFFFF);
    }

    public LabelWidget(int width, int height, int textColor)
    {
        super(width, height);

        this.setNormalTextColor(textColor);
        this.setHoverTextColor(textColor);
    }

    public int getTotalWidth()
    {
        return this.totalWidth;
    }

    public int getTotalHeight()
    {
        return this.totalHeight;
    }

    public StringListRenderer getStringListRenderer()
    {
        return this.stringListRenderer;
    }

    public void clearText()
    {
        this.stringListRenderer.clearText();
    }

    public LabelWidget translateAddLine(String translationKey, Object... args)
    {
        this.stringListRenderer.addLine(translationKey, args);
        this.updateLabelWidgetSize();
        return this;
    }

    public LabelWidget addLine(StyledTextLine line)
    {
        this.stringListRenderer.addStyledTextLine(line);
        this.updateLabelWidgetSize();
        return this;
    }

    /**
     * Add the text without trying to translate the string
     */
    public LabelWidget addLines(String text)
    {
        this.stringListRenderer.parseAndAddLine(text);
        this.updateLabelWidgetSize();
        return this;
    }

    public LabelWidget translateSetLines(String translationKey, Object... args)
    {
        this.stringListRenderer.setText(translationKey, args);
        this.updateLabelWidgetSize();
        return this;
    }

    /**
     * Set the text without trying to translate the string
     */
    public LabelWidget setLines(String text)
    {
        this.stringListRenderer.parseAndSetLines(text);
        this.updateLabelWidgetSize();
        return this;
    }

    public LabelWidget translateSetLines(List<String> lines)
    {
        this.stringListRenderer.setText(lines);
        this.updateLabelWidgetSize();
        return this;
    }

    public LabelWidget setLines(StyledText text)
    {
        return this.setLines(text.lines);
    }

    public LabelWidget setLines(StyledTextLine... lines)
    {
        return this.setLines(Arrays.asList(lines));
    }

    public LabelWidget setLines(List<StyledTextLine> lines)
    {
        this.stringListRenderer.setStyledTextLines(lines);
        this.updateLabelWidgetSize();
        return this;
    }

    public LabelWidget setHorizontalAlignment(HorizontalAlignment alignment)
    {
        this.stringListRenderer.setHorizontalAlignment(alignment);
        return this;
    }

    @Override
    public void setLineHeight(int lineHeight)
    {
        super.setLineHeight(lineHeight);
        this.stringListRenderer.setLineHeight(lineHeight);
        this.updateLabelWidgetSize();
    }
    
    public LabelWidget setNormalTextColor(int color)
    {
        this.stringListRenderer.getNormalTextSettings().setTextColor(color);
        return this;
    }

    public LabelWidget setHoverTextColor(int color)
    {
        this.stringListRenderer.getHoverTextSettings().setTextColor(color);
        return this;
    }

    public LabelWidget setUseTextShadow(boolean useShadow)
    {
        this.stringListRenderer.getNormalTextSettings().setTextShadowEnabled(useShadow);
        return this;
    }

    public LabelWidget setUseBackgroundForHoverOverflow(boolean useBackground)
    {
        this.useBackgroundForHoverOverflow = useBackground;
        return this;
    }

    protected void updateLabelWidgetSize()
    {
        this.updateWidth();
        this.updateHeight();
        this.updateStringRendererSize();
    }

    @Override
    protected void onSizeChanged()
    {
        this.updateStringRendererSize();
    }

    protected void updateStringRendererSize()
    {
        int width = this.hasMaxWidth() ? this.maxWidth : this.getWidth();
        int height = this.hasMaxHeight() ? this.maxHeight : this.getHeight();
        int bw = this.getBorderRenderer().getNormalSettings().getActiveBorderWidth() * 2;

        this.stringListRenderer.setMaxWidth(width - this.padding.getHorizontalTotal() - bw);
        this.stringListRenderer.setMaxHeight(height - this.padding.getVerticalTotal() - bw);
        this.stringListRenderer.reAddLines();
    }

    @Override
    public void updateWidth()
    {
        this.totalWidth = this.stringListRenderer.getTotalTextWidth() + this.padding.getHorizontalTotal();
        this.totalWidth += this.getBorderRenderer().getNormalSettings().getActiveBorderWidth() * 2;

        if (this.automaticWidth)
        {
            int width = this.totalWidth;

            if (this.hasMaxWidth())
            {
                width = Math.min(width, this.maxWidth);
            }

            this.setWidth(width);
        }
    }

    @Override
    public void updateHeight()
    {
        this.totalHeight = this.stringListRenderer.getTotalTextHeight() + this.padding.getVerticalTotal();
        this.totalHeight += this.getBorderRenderer().getNormalSettings().getActiveBorderWidth() * 2;

        if (this.automaticHeight)
        {
            int height = this.totalHeight;

            if (this.hasMaxHeight())
            {
                height = Math.min(height, this.maxHeight);
            }

            this.setHeight(height);
        }
    }

    @Override
    protected int getBackgroundWidth(boolean hovered, ScreenContext ctx)
    {
        if (this.hasMaxWidth() && ctx.isActiveScreen && hovered)
        {
            return this.totalWidth;
        }

        return super.getBackgroundWidth(hovered, ctx);
    }

    @Override
    protected int getBackgroundHeight(boolean hovered, ScreenContext ctx)
    {
        if (this.hasMaxHeight() && ctx.isActiveScreen && hovered)
        {
            return this.totalHeight;
        }

        return super.getBackgroundHeight(hovered, ctx);
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        int width = this.totalWidth;

        if (this.getBackgroundRenderer().getNormalSettings().isEnabled() == false &&
            this.useBackgroundForHoverOverflow &&
            this.stringListRenderer.hasClampedContent() &&
            this.isHoveredForRender(ctx))
        {
            z += 20;
            int height = this.totalHeight;
            BorderSettings borderSettings = this.getBorderRenderer().getHoverSettings();
            BackgroundSettings bgSettings = this.getBackgroundRenderer().getHoverSettings();
            this.getBackgroundRenderer().renderBackground(x, y, z, width, height, bgSettings, ctx);
            this.getBorderRenderer().renderBorder(x, y, z, width, height, borderSettings, ctx);
        }
        else
        {
            super.renderAt(x, y, z, ctx);
        }

        int bw = this.getBorderRenderer().getNormalSettings().getActiveBorderWidth();
        x += this.padding.getLeft() + bw;
        y += this.padding.getTop() + bw;

        if (this.automaticWidth == false && this.getWidth() > this.totalWidth &&
            this.stringListRenderer.hasClampedContent() == false &&
            this.stringListRenderer.getHorizontalAlignment() == HorizontalAlignment.RIGHT)
        {
            x = this.getRight() - this.stringListRenderer.getTotalRenderWidth() - this.padding.getRight() - bw;
        }

        boolean hovered = this.enabled && this.isHoveredForRender(ctx);
        this.stringListRenderer.renderAt(x, y, z, hovered, ctx);
    }
}
