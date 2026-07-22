package malilib.input;

import com.google.common.collect.ImmutableList;

import malilib.config.value.BaseOptionConfigValue;

public class KeyAction extends BaseOptionConfigValue
{
    public static final KeyAction PRESS   = new KeyAction("press",   "malilib.name.key_action.press", 0);
    public static final KeyAction RELEASE = new KeyAction("release", "malilib.name.key_action.release", 1);
    public static final KeyAction BOTH    = new KeyAction("both",    "malilib.name.key_action.both", 2);

    public static final ImmutableList<KeyAction> VALUES = ImmutableList.of(PRESS, RELEASE, BOTH);

    protected final int iconIndex;

    public KeyAction(String name, String translationKey, int iconIndex)
    {
        super(name, translationKey);

        this.iconIndex = iconIndex;
    }

    public int getIconIndex()
    {
        return this.iconIndex;
    }
}
