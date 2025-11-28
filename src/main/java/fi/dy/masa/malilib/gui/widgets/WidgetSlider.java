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
    protected int lastMouseX;
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
        this.callback.setValueRelative(this.getRelativePosition((int) click.x()));
        this.lastMouseX = (int) click.x();
        this.dragging = true;

        return true;
    }

    @Override
    public void onMouseReleasedImpl(MouseButtonEvent click)
    {
        this.dragging = false;
    }

    @Override
    public void render(GuiContext ctx, int mouseX, int mouseY, boolean selected)
    {
        super.render(ctx, mouseX, mouseY, selected);

        if (this.dragging && mouseX != this.lastMouseX)
        {
            this.callback.setValueRelative(this.getRelativePosition(mouseX));
            this.lastMouseX = mouseX;
        }

	    ctx.blitSprite(RenderPipelines.GUI_TEXTURED, WidgetSlider.BUTTON_DISABLE_TEXTURE, this.x + 1, this.y, this.width - 3, 20);

        double relPos = this.callback.getValueRelative();
        int sw = this.sliderWidth;
        int usableWidth = this.width - 4 - sw;
        int s = sw / 2;

	    ctx.blitSprite(RenderPipelines.GUI_TEXTURED, WidgetSlider.BUTTON_TEXTURE, this.x + 2 + (int) (relPos * usableWidth), this.y, sw, 20);

        String str = this.callback.getFormattedDisplayValue();
        int w = this.getStringWidth(str);
        this.drawString(ctx, this.x + (this.width / 2) - w / 2, this.y + 6, 0xFFFFFFA0, str);
    }

    protected double getRelativePosition(int mouseX)
    {
        int relPos = mouseX - this.x - this.sliderWidth / 2;
        return Mth.clamp((double) relPos / (double) (this.width - this.sliderWidth - 4), 0, 1);
    }
}
