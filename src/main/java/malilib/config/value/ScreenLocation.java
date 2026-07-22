package malilib.config.value;

import com.google.common.collect.ImmutableList;

import malilib.gui.widget.BaseWidget;
import malilib.util.data.EdgeInt;

public class ScreenLocation extends BaseOptionConfigValue implements HorizontalAlignment.StartXPositionSource, VerticalAlignment.StartYPositionSource
{
    public static final ScreenLocation BOTTOM_CENTER = new ScreenLocation(VerticalAlignment.BOTTOM, HorizontalAlignment.CENTER, "bottom_center", "malilib.name.screen_location.bottom_center");
    public static final ScreenLocation BOTTOM_LEFT   = new ScreenLocation(VerticalAlignment.BOTTOM, HorizontalAlignment.LEFT,   "bottom_left",   "malilib.name.screen_location.bottom_left");
    public static final ScreenLocation BOTTOM_RIGHT  = new ScreenLocation(VerticalAlignment.BOTTOM, HorizontalAlignment.RIGHT,  "bottom_right",  "malilib.name.screen_location.bottom_right");
    public static final ScreenLocation CENTER        = new ScreenLocation(VerticalAlignment.CENTER, HorizontalAlignment.CENTER, "center",        "malilib.name.screen_location.center");
    public static final ScreenLocation CENTER_LEFT   = new ScreenLocation(VerticalAlignment.CENTER, HorizontalAlignment.LEFT,   "center_left",   "malilib.name.screen_location.center_left");
    public static final ScreenLocation CENTER_RIGHT  = new ScreenLocation(VerticalAlignment.CENTER, HorizontalAlignment.RIGHT,  "center_right",  "malilib.name.screen_location.center_right");
    public static final ScreenLocation TOP_CENTER    = new ScreenLocation(VerticalAlignment.TOP, HorizontalAlignment.CENTER,    "top_center",    "malilib.name.screen_location.top_center");
    public static final ScreenLocation TOP_LEFT      = new ScreenLocation(VerticalAlignment.TOP, HorizontalAlignment.LEFT,      "top_left",      "malilib.name.screen_location.top_left");
    public static final ScreenLocation TOP_RIGHT     = new ScreenLocation(VerticalAlignment.TOP, HorizontalAlignment.RIGHT,     "top_right",     "malilib.name.screen_location.top_right");

    public static final ImmutableList<ScreenLocation> VALUES = ImmutableList.of(TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT,
                                                                                CENTER, TOP_CENTER, BOTTOM_CENTER, CENTER_LEFT, CENTER_RIGHT);

    public final VerticalAlignment verticalLocation;
    public final HorizontalAlignment horizontalLocation;

    public ScreenLocation(VerticalAlignment verticalLocation, HorizontalAlignment horizontalLocation, String name, String translationKey)
    {
        super(name, translationKey);

        this.verticalLocation = verticalLocation;
        this.horizontalLocation = horizontalLocation;
    }

    public int getMarginX(EdgeInt margin)
    {
        return this.horizontalLocation.getMargin(margin);
    }

    public int getMarginY(EdgeInt margin)
    {
        return this.verticalLocation.getMargin(margin);
    }

    @Override
    public int getStartX(int contentWidth, int viewportWidth, int offset)
    {
        return this.horizontalLocation.getStartX(contentWidth, viewportWidth, offset);
    }

    public int getStartX(BaseWidget widget, int viewportWidth)
    {
        return this.horizontalLocation.getStartX(widget, viewportWidth);
    }

    @Override
    public int getStartY(int contentHeight, int viewportHeight, int offset)
    {
        return this.verticalLocation.getStartY(contentHeight, viewportHeight, offset);
    }

    public int getStartY(BaseWidget widget, int viewportHeight)
    {
        return this.verticalLocation.getStartY(widget, viewportHeight);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || this.getClass() != o.getClass()) { return false; }

        ScreenLocation that = (ScreenLocation) o;

        if (!this.verticalLocation.equals(that.verticalLocation)) { return false; }
        return this.horizontalLocation.equals(that.horizontalLocation);
    }

    @Override
    public int hashCode()
    {
        int result = this.verticalLocation.hashCode();
        result = 31 * result + this.horizontalLocation.hashCode();
        return result;
    }
}
