package malilib.gui.widget;

import malilib.gui.callback.SliderCallbackWithText;
import malilib.gui.icon.DefaultIcons;
import malilib.gui.util.GuiUtils;
import malilib.gui.util.ScreenContext;
import malilib.util.MathUtils;

public class HorizontalSliderWidget extends BaseSliderWidget<SliderCallbackWithText>
{
    protected int sliderThickness;

    public HorizontalSliderWidget(int width, int height, SliderCallbackWithText callback)
    {
        super(width, height, callback);

        int usableWidth = this.getWidth() - 4;
        this.sliderThickness = MathUtils.clamp(usableWidth / callback.getMaxSteps(), 8, usableWidth / 2);
        this.textOffset.setCenterHorizontally(true);
        this.textOffset.setXOffset(0);
    }

    @Override
    public void updateWidgetState()
    {
        this.getHoverInfoFactory().updateList();
        this.callback.updateDisplayText();
    }

    @Override
    protected int getSliderTravelDistance()
    {
        return this.getWidth() - this.sliderThickness;
    }

    @Override
    protected double getRelativePosition(int mouseX, int mouseY)
    {
        int relPos = mouseX - this.getX() - this.sliderThickness / 2;
        return MathUtils.clamp((double) relPos / (double) (this.getWidth() - this.sliderThickness - 4), 0, 1);
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        int width = this.getWidth();
        int height = this.getHeight();

        // Render the background texture
        DefaultIcons.BUTTON_BACKGROUND.renderFourSlicedAt(x + 1, y, z, width - 2, height, ctx);

        // Render the slider bar texture
        double relPos = this.callback.getRelativeValue();
        int sw = this.sliderThickness;
        int usableWidth = width - sw - 4;
        int sx = x + 2 + (int) (relPos * usableWidth);
        boolean enabled = this.isEnabled();
        boolean hovered = GuiUtils.isMouseInRegion(ctx.mouseX, ctx.mouseY, sx, y, sw, height);
        DefaultIcons.BUTTON_BACKGROUND.renderFourSlicedAt(sx, y, z, sw, height, IconWidget.getVariantIndex(enabled, hovered), ctx);

        int textColor = enabled ? 0xFFFFFFA0 : 0xFF909090;
        this.renderTextLine(x, y, z, textColor, this.callback.getDisplayText(), ctx);
    }
}
