package malilib.config.value;

import java.util.function.IntUnaryOperator;
import com.google.common.collect.ImmutableList;

import malilib.gui.widget.BaseWidget;
import malilib.util.data.EdgeInt;

public class VerticalAlignment extends BaseOptionConfigValue
{
    public static final VerticalAlignment TOP    = new VerticalAlignment("top",    "malilib.name.vertical_alignment.top",    (h) -> 0, (ch, vh, o) -> o);
    public static final VerticalAlignment BOTTOM = new VerticalAlignment("bottom", "malilib.name.vertical_alignment.bottom", (h) -> -h, (ch, vh, o) -> vh - ch - o);
    public static final VerticalAlignment CENTER = new VerticalAlignment("center", "malilib.name.vertical_alignment.center", (h) -> -h / 2, (ch, vh, o) -> vh / 2 - ch / 2 + o);

    public static final ImmutableList<VerticalAlignment> VALUES = ImmutableList.of(TOP, BOTTOM, CENTER);

    protected final IntUnaryOperator yOffsetSource;
    protected final StartYPositionSource startYPositionSource;

    public VerticalAlignment(String name, String translationKey,
                             IntUnaryOperator yOffsetSource, StartYPositionSource startYPositionSource)
    {
        super(name, translationKey);

        this.yOffsetSource = yOffsetSource;
        this.startYPositionSource = startYPositionSource;
    }

    public int getStartY(int contentHeight, int viewportHeight, int offset)
    {
        return this.startYPositionSource.getStartY(contentHeight, viewportHeight, offset);
    }

    public int getStartY(BaseWidget widget, int viewportHeight)
    {
        int offset = this.getMargin(widget.getMargin());
        return this.startYPositionSource.getStartY(widget.getHeight(), viewportHeight, offset);
    }

    public int getMargin(EdgeInt margin)
    {
        return this == TOP ? margin.getTop() : (this == BOTTOM ? margin.getBottom() : 0);
    }

    public int getYStartOffsetForEdgeAlignment(int contentHeight)
    {
        return this.yOffsetSource.applyAsInt(contentHeight);
    }

    public interface StartYPositionSource
    {
        int getStartY(int contentHeight, int viewportHeight, int offset);
    }
}
