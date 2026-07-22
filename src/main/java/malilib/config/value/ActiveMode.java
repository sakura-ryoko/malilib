package malilib.config.value;

import com.google.common.collect.ImmutableList;

public class ActiveMode extends BaseOptionConfigValue
{
    public static final ActiveMode NEVER    = new ActiveMode("never",    "malilib.name.active_mode.never");
    public static final ActiveMode WITH_KEY = new ActiveMode("with_key", "malilib.name.active_mode.with_key");
    public static final ActiveMode ALWAYS   = new ActiveMode("always",   "malilib.name.active_mode.always");

    public static final ImmutableList<ActiveMode> VALUES = ImmutableList.of(NEVER, WITH_KEY, ALWAYS);

    private ActiveMode(String name, String translationKey)
    {
        super(name, translationKey);
    }
}
