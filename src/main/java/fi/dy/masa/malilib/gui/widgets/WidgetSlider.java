package fi.dy.masa.malilib.gui.widgets;

import fi.dy.masa.malilib.gui.interfaces.ISliderCallback;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class WidgetSlider extends WidgetBase
{
    public static final ResourceLocation BUTTON_TEXTURE = ResourceLocation.withDefaultNamespace("widget/button");
    public static final ResourceLocation BUTTON_DISABLE_TEXTURE = ResourceLocation.withDefaultNamespace("widget/button_disabled");

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
    public void render(GuiGraphics drawContext, int mouseX, int mouseY, boolean selected)
    {
        super.render(drawContext, mouseX, mouseY, selected);

        if (this.dragging && mouseX != this.lastMouseX)
        {
            this.callback.setValueRelative(this.getRelativePosition(mouseX));
            this.lastMouseX = mouseX;
        }

//        RenderUtils.color(1f, 1f, 1f, 1f);

        drawContext.blitSprite(RenderPipelines.GUI_TEXTURED, WidgetSlider.BUTTON_DISABLE_TEXTURE, this.x + 1, this.y, this.width - 3, 20);

        double relPos = this.callback.getValueRelative();
        int sw = this.sliderWidth;
        int usableWidth = this.width - 4 - sw;
        int s = sw / 2;

        drawContext.blitSprite(RenderPipelines.GUI_TEXTURED, WidgetSlider.BUTTON_TEXTURE, this.x + 2 + (int) (relPos * usableWidth), this.y, sw, 20);

        String str = this.callback.getFormattedDisplayValue();
        int w = this.getStringWidth(str);
        this.drawString(drawContext, this.x + (this.width / 2) - w / 2, this.y + 6, 0xFFFFFFA0, str);
    }

    protected double getRelativePosition(int mouseX)
    {
        int relPos = mouseX - this.x - this.sliderWidth / 2;
        return Mth.clamp((double) relPos / (double) (this.width - this.sliderWidth - 4), 0, 1);
    }
}
