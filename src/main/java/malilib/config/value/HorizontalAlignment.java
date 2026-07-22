package malilib.config.value;

import java.util.function.IntUnaryOperator;
import com.google.common.collect.ImmutableList;

import malilib.gui.widget.BaseWidget;
import malilib.util.data.EdgeInt;

public class HorizontalAlignment extends BaseOptionConfigValue
{
    public static final HorizontalAlignment LEFT   = new HorizontalAlignment("left",   "malilib.name.horizontal_alignment.left",   (w) -> 0, (cw, vw, o) -> o);
    public static final HorizontalAlignment RIGHT  = new HorizontalAlignment("right",  "malilib.name.horizontal_alignment.right",  (w) -> -w, (cw, vw, o) -> vw - cw - o);
    public static final HorizontalAlignment CENTER = new HorizontalAlignment("center", "malilib.name.horizontal_alignment.center", (w) -> -w / 2, (cw, vw, o) -> vw / 2 - cw / 2 + o);

    public static final ImmutableList<HorizontalAlignment> VALUES = ImmutableList.of(LEFT, RIGHT, CENTER);

    protected final IntUnaryOperator xOffsetSource;
    protected final StartXPositionSource startXPositionSource;

    public HorizontalAlignment(String name, String translationKey,
                               IntUnaryOperator xOffsetSource, StartXPositionSource startXPositionSource)
    {
        super(name, translationKey);

        this.xOffsetSource = xOffsetSource;
        this.startXPositionSource = startXPositionSource;
    }

    public int getStartX(int contentWidth, int viewportWidth, int offset)
    {
        return this.startXPositionSource.getStartX(contentWidth, viewportWidth, offset);
    }

    public int getStartX(BaseWidget widget, int viewportWidth)
    {
        int offset = this.getMargin(widget.getMargin());
        return this.startXPositionSource.getStartX(widget.getWidth(), viewportWidth, offset);
    }

    public int getMargin(EdgeInt margin)
    {
        return this == LEFT ? margin.getLeft() : (this == RIGHT ? margin.getRight() : 0);
    }

    public int getXStartOffsetForEdgeAlignment(int contentWidth)
    {
        return this.xOffsetSource.applyAsInt(contentWidth);
    }

    public interface StartXPositionSource
    {
        int getStartX(int contentWidth, int viewportWidth, int offset);
    }
}
