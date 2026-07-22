package malilib.config.value;

import com.google.common.collect.ImmutableList;

public class KeybindDisplayMode extends BaseOptionConfigValue
{
    public static final KeybindDisplayMode NONE         = new KeybindDisplayMode("none",                "malilib.name.keybind_display_mode.none");
    public static final KeybindDisplayMode KEYS         = new KeybindDisplayMode("keys",                "malilib.name.keybind_display_mode.keys");
    public static final KeybindDisplayMode ACTIONS      = new KeybindDisplayMode("actions",             "malilib.name.keybind_display_mode.actions");
    public static final KeybindDisplayMode KEYS_ACTIONS = new KeybindDisplayMode("keys_and_actions",    "malilib.name.keybind_display_mode.keys_and_actions");

    public static final ImmutableList<KeybindDisplayMode> VALUES = ImmutableList.of(NONE, KEYS, ACTIONS, KEYS_ACTIONS);

    private KeybindDisplayMode(String name, String translationKey)
    {
        super(name, translationKey);
    }
}
