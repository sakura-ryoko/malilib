package malilib.config.value;

import com.google.common.collect.ImmutableList;

public class HudAlignment extends BaseOptionConfigValue
{
    public static final HudAlignment TOP_LEFT     = new HudAlignment("top_left",        "malilib.name.screen_location.top_left");
    public static final HudAlignment TOP_RIGHT    = new HudAlignment("top_right",       "malilib.name.screen_location.top_right");
    public static final HudAlignment BOTTOM_LEFT  = new HudAlignment("bottom_left",     "malilib.name.screen_location.bottom_left");
    public static final HudAlignment BOTTOM_RIGHT = new HudAlignment("bottom_right",    "malilib.name.screen_location.bottom_right");
    public static final HudAlignment CENTER       = new HudAlignment("center",          "malilib.name.screen_location.center");

    public static final ImmutableList<HudAlignment> VALUES = ImmutableList.of(TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, CENTER);

    private HudAlignment(String name, String translationKey)
    {
        super(name, translationKey);
    }
}
