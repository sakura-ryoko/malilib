package fi.dy.masa.malilib.gui.widgets;

import fi.dy.masa.malilib.gui.interfaces.ISliderCallback;
import fi.dy.masa.malilib.render.GuiContext;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class WidgetSlider extends WidgetBase
{
    public static final Identifier BUTTON_TEXTURE = Identifier.withDefaultNamespace("widget/button");
    public static final Identifier BUTTON_DISABLE_TEXTURE = Identifier.withDefaultNamespace("widget/button_disabled");

    protected final ISliderCallback callback;
    protected int sliderWidth;
    protected boolean dragging;

    public WidgetSlider(int x, int y, int width, int height, ISliderCallback callback)
    {
        super(x, y, width, height);

        this.callback = callback;
        int usableWidth = this.width - 4;
        this.sliderWidth = Mth.clamp(usableWidth / callback.getMaxSteps(), 8, usableWidth / 2);
    }

    @Override
    protected boolean onMouseClickedImpl(MouseButtonEvent click, boolean doubleClick)
    {
        this.dragging = true;
        this.callback.setValueRelative(this.getRelativePosition((int) click.x()));

        return true;
    }

    @Override
    public boolean onMouseDragged(MouseButtonEvent click, double dragXAmount, double dragYAmount) {
        if (this.dragging)
        {
            this.callback.setValueRelative(this.getRelativePosition((int) click.x()));

            return true;
        }

        return false;
    }

    @Override
    public void onMouseReleasedImpl(MouseButtonEvent click) {
        this.dragging = false;
    }

    @Override
    public void render(GuiContext ctx, int mouseX, int mouseY, boolean selected)
    {
        super.render(ctx, mouseX, mouseY, selected);

	    ctx.blitSprite(RenderPipelines.GUI_TEXTURED, WidgetSlider.BUTTON_DISABLE_TEXTURE, this.x + 1, this.y, this.width - 3, this.height);

        double relPos = this.callback.getValueRelative();
        int sw = this.sliderWidth;
        int usableWidth = this.width - 4 - sw;

	    ctx.blitSprite(RenderPipelines.GUI_TEXTURED, WidgetSlider.BUTTON_TEXTURE, this.x + 2 + (int) (relPos * usableWidth), this.y, sw, this.height);

        String str = this.callback.getFormattedDisplayValue();
        int w = this.getStringWidth(str);
        this.drawString(ctx, this.x + (this.width / 2) - w / 2, this.y + 6, 0xFFFFFFA0, str);
    }

    protected double getRelativePosition(int mouseX)
    {
        int relPos = mouseX - this.x;
        return Mth.clamp((double) relPos / (double) (this.width - this.sliderWidth - 4), 0, 1);
    }
}
