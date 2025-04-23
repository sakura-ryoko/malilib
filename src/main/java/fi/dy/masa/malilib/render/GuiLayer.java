package fi.dy.masa.malilib.render;

/**
 * Gui Rendering "Layers" heavily used across this MaLiLib for 1.21.6+
 * I highly recommend starting with using `NONE` and then making adjustments either DOWN or UP first.
 * -
 * You should only TOP with objects such as Hover Text or `TOP` overlay screens; such as with
 * the GuiColorEditorHSV / GuiKeybindSettings screens.
 * -
 * Vanilla should have more than 4 layers; I would suggest if they added a `BOTTOM` below DOWN,
 * sort of like the reverse of using TOP.
 */
public enum GuiLayer
{
    BLUR        (0),        // Apply's the "BLUR" effect, can be used only once.
    POP         (1),        // Pop's the current Layer
    DOWN        (2),        // Sets the layer as `DOWN`
    NONE        (3),        // Does not apply a Layer
    UP          (4),        // Sets the layer as `UP`
    TOP         (5),        // Sets the layer as 'TOP'
    ;

    private final int index;

    GuiLayer(int index)
    {
        this.index = index;
    }

    public GuiLayer byIndex(int index)
    {
        for (GuiLayer layer : values())
        {
            if (layer.getIndex() == index)
            {
                return layer;
            }
        }

        return GuiLayer.NONE;
    }

    public int getIndex()
    {
        return this.index;
    }

    public GuiLayer goUp()
    {
        if (this.index < 5 && this.index > 1)
        {
            return this.byIndex(this.index + 1);
        }

        return this;
    }

    public GuiLayer goDown()
    {
        if (this.index > 3 && this.index < 6)
        {
            return this.byIndex(this.index - 1);
        }

        return this;
    }
}
