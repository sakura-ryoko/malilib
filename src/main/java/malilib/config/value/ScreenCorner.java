package malilib.config.value;

import com.google.common.collect.ImmutableList;

public class ScreenCorner extends BaseOptionConfigValue
{
    public static final ScreenCorner TOP_LEFT     = new ScreenCorner("top_left",        "malilib.name.screen_location.top_left");
    public static final ScreenCorner TOP_RIGHT    = new ScreenCorner("top_right",       "malilib.name.screen_location.top_right");
    public static final ScreenCorner BOTTOM_LEFT  = new ScreenCorner("bottom_left",     "malilib.name.screen_location.bottom_left");
    public static final ScreenCorner BOTTOM_RIGHT = new ScreenCorner("bottom_right",    "malilib.name.screen_location.bottom_right");

    public static final ImmutableList<ScreenCorner> VALUES = ImmutableList.of(TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT);

    private ScreenCorner(String name, String translationKey)
    {
        super(name, translationKey);
    }
}
