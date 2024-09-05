package fi.dy.masa.malilib.gui.widgets;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import fi.dy.masa.malilib.gui.interfaces.ISliderCallback;
import fi.dy.masa.malilib.render.RenderUtils;

public class WidgetSlider extends WidgetBase
{
    public static final Identifier BUTTON_TEXTURE = Identifier.ofVanilla("widget/button");
    public static final Identifier BUTTON_DISABLE_TEXTURE = Identifier.ofVanilla("widget/button_disabled");

    protected final ISliderCallback callback;
    protected int sliderWidth;
    protected int lastMouseX;
    protected boolean dragging;

    public WidgetSlider(int x, int y, int width, int height, ISliderCallback callback)
    {
        super(x, y, width, height);

        this.callback = callback;
        int usableWidth = this.width - 4;
        this.sliderWidth = MathHelper.clamp(usableWidth / callback.getMaxSteps(), 8, usableWidth / 2);
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        this.callback.setValueRelative(this.getRelativePosition(mouseX));
        this.lastMouseX = mouseX;
        this.dragging = true;

        return true;
    }

    @Override
    public void onMouseReleasedImpl(int mouseX, int mouseY, int mouseButton)
    {
        this.dragging = false;
    }

    @Override
    public void render(int mouseX, int mouseY, boolean selected, DrawContext drawContext)
    {
        if (this.dragging && mouseX != this.lastMouseX)
        {
            this.callback.setValueRelative(this.getRelativePosition(mouseX));
            this.lastMouseX = mouseX;
        }

        RenderUtils.color(1f, 1f, 1f, 1f);

        drawContext.drawGuiTexture(RenderLayer::getGuiTextured, WidgetSlider.BUTTON_DISABLE_TEXTURE, this.x + 1, this.y, this.width - 3, 20);

        double relPos = this.callback.getValueRelative();
        int sw = this.sliderWidth;
        int usableWidth = this.width - 4 - sw;
        int s = sw / 2;


        drawContext.drawGuiTexture(RenderLayer::getGuiTextured, WidgetSlider.BUTTON_TEXTURE, this.x + 2 + (int) (relPos * usableWidth), this.y, sw, 20);

        String str = this.callback.getFormattedDisplayValue();
        int w = this.getStringWidth(str);
        this.drawString(this.x + (this.width / 2) - w / 2, this.y + 6, 0xFFFFFFA0, str, drawContext);
    }

    protected double getRelativePosition(int mouseX)
    {
        int relPos = mouseX - this.x - this.sliderWidth / 2;
        return MathHelper.clamp((double) relPos / (double) (this.width - this.sliderWidth - 4), 0, 1);
    }
}
