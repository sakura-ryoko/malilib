package fi.dy.masa.malilib.test.config;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import net.minecraft.util.StringRepresentable;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.malilib.config.IEnumBooleanHotkey;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyCallbackToggleBooleanConfigWithMessage;
import fi.dy.masa.malilib.hotkeys.KeybindMulti;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.interfaces.IValueChangeCallback;
import fi.dy.masa.malilib.test.render.TestRenderWalls;
import fi.dy.masa.malilib.util.StringUtils;

@ApiStatus.Experimental
public enum ConfigTestEnum implements IEnumBooleanHotkey, StringRepresentable
{
    TEST_ENUM_CONFIG                ("testEnumConfig",              false,""),
    TEST_ENUM_SINGLE_PLAYER         ("testEnumSinglePlayer",        false,true, ""),
    TEST_SELECTOR_HOTKEY            ("testSelectorHotkey",          false,""),
    TEST_TEXT_LINES                 ("testTestLines",               false,""),
    TEST_WALLS_HOTKEY               ("testWallsHotkey",             false,""),
    TEST_WALLS_USE_COLOR            ("testWallsUseColor",           false, ""),
    ;

    public static final StringRepresentable.EnumCodec<@NotNull ConfigTestEnum> CODEC = StringRepresentable.fromEnum(ConfigTestEnum::values);
    private final static String TEST_ENUM_KEY = MaLiLibReference.MOD_ID + ".config.test_enum";
	public static final ImmutableList<@NotNull ConfigTestEnum> VALUES = ImmutableList.copyOf(values());

	private final String name;
    private String comment;
    private String prettyName;
    private String translatedName;
    private final IKeybind keybind;
    private final boolean defaultValueBoolean;
    private final boolean singlePlayer;
    private boolean valueBoolean;
    private IValueChangeCallback<IConfigBoolean> callback;
    private boolean dirty = false;
    private Pair<Boolean, String> lastBooleanHotkey;

    ConfigTestEnum(String name, boolean defaultValue, String defaultHotkey)
    {
        this(name, defaultValue, false, defaultHotkey, KeybindSettings.DEFAULT,
             buildTranslateName(name, "comment"),
             buildTranslateName(name, "prettyName"),
             buildTranslateName(name, "name"));
    }

    ConfigTestEnum(String name, boolean defaultValue, String defaultHotkey, KeybindSettings settings)
    {
        this(name, defaultValue, false, defaultHotkey, settings,
             buildTranslateName(name, "comment"),
             buildTranslateName(name, "prettyName"),
             buildTranslateName(name, "name"));
    }

    ConfigTestEnum(String name, boolean defaultValue, boolean singlePlayer, String defaultHotkey)
    {
        this(name, defaultValue, singlePlayer, defaultHotkey, KeybindSettings.DEFAULT,
             buildTranslateName(name, "comment"),
             buildTranslateName(name, "prettyName"),
             buildTranslateName(name, "name"));
    }

    ConfigTestEnum(String name, boolean defaultValue, String defaultHotkey, String comment, String prettyName, String translatedName)
    {
        this(name, defaultValue, false, defaultHotkey,
             comment,
             prettyName,
             translatedName);
    }

    ConfigTestEnum(String name, boolean defaultValue, boolean singlePlayer, String defaultHotkey, String comment, String prettyName, String translatedName)
    {
        this(name, defaultValue, singlePlayer, defaultHotkey, KeybindSettings.DEFAULT,
             comment,
             prettyName,
             translatedName);
    }

    ConfigTestEnum(String name, boolean defaultValue, boolean singlePlayer, String defaultHotkey, KeybindSettings settings, String comment, String prettyName, String translatedName)
    {
        this.name = name;
        this.valueBoolean = defaultValue;
        this.defaultValueBoolean = defaultValue;
        this.singlePlayer = singlePlayer;
        this.comment = comment;
        this.prettyName = prettyName;
        this.translatedName = translatedName;
        this.keybind = KeybindMulti.fromStorageString(defaultHotkey, settings);
        this.keybind.setCallback(new KeyCallbackToggleBooleanConfigWithMessage(this));
        this.updateLastBooleanHotkeyValue();
    }

    private static String buildTranslateName(String name, String type)
    {
        return TEST_ENUM_KEY + "." + type + "." + name;
    }

    @Override
    public @Nonnull String getSerializedName()
    {
        return this.name;
    }

    @Override
    public String getStringValue()
    {
        return String.valueOf(this.valueBoolean);
    }

    @Override
    public String getDefaultStringValue()
    {
        return String.valueOf(this.defaultValueBoolean);
    }

    @Override
    public void setValueFromString(String value)
    {
        this.updateLastBooleanHotkeyValue();
        boolean oldValue = this.valueBoolean;

        switch (value)
        {
            case "true" -> this.valueBoolean = true;
            case "false" -> this.valueBoolean = false;
            default -> {}
        }

        if (oldValue != this.valueBoolean)
        {
            this.markClean();
            this.onValueChanged();
        }
    }

    @Override
    public boolean getBooleanValue()
    {
        return this.valueBoolean;
    }

    @Override
    public boolean getDefaultBooleanValue()
    {
        return this.defaultValueBoolean;
    }

    @Override
    public void setBooleanValue(boolean value)
    {
        this.updateLastBooleanHotkeyValue();
        boolean oldValue = this.valueBoolean;
        this.valueBoolean = value;

        if (oldValue != this.valueBoolean)
        {
            this.markClean();
            this.onValueChanged();
        }
    }

    @Override
    public boolean getLastBooleanValue()
    {
        return this.lastBooleanHotkey.getLeft();
    }

    @Override
    public void onValueChanged()
    {
        if (MaLiLibReference.DEBUG_MODE || (MaLiLibConfigs.Debug.CONFIG_ELEMENT_DEBUG != null && MaLiLibConfigs.Debug.CONFIG_ELEMENT_DEBUG.getBooleanValue()))
        {
            MaLiLib.LOGGER.warn("TEST-ENUM: onValueChanged() -> name [{}], enumConfig {}", this.name, this.getBooleanHotkeyValue().toString());
        }

        if (this.equals(TEST_WALLS_USE_COLOR))
        {
            TestRenderWalls.INSTANCE.setNeedsUpdate();
        }

        if (this.callback != null)
        {
            this.callback.onValueChanged(this);
        }
    }

    @Override
    public void setValueChangeCallback(IValueChangeCallback<IConfigBoolean> callback)
    {
        this.callback = callback;
    }

    @Override
    public IKeybind getKeybind()
    {
        return this.keybind;
    }

    @Override
    public ConfigType getType()
    {
        return ConfigType.HOTKEY;
    }

    @Override
    public String getName()
    {
        if (this.singlePlayer)
        {
            return GuiBase.TXT_GOLD + this.name + GuiBase.TXT_RST;
        }

        return this.name;
    }

    @Override
    public String getPrettyName()
    {
        return StringUtils.getTranslatedOrFallback(this.prettyName,
                                                   !this.prettyName.isEmpty() ? this.prettyName : StringUtils.splitCamelCase(this.name));
    }

    @Override
    public @Nullable String getComment()
    {
        String comment = StringUtils.getTranslatedOrFallback(this.comment, this.comment);

        if (comment != null && this.singlePlayer)
        {
            return comment + "\n" + StringUtils.translate(MaLiLibReference.MOD_ID + ".label.config_comment.single_player_only");
        }

        return comment;
    }

    @Override
    public String getTranslatedName()
    {
        return StringUtils.getTranslatedOrFallback(this.translatedName, this.name);
    }

    @Override
    public String getConfigGuiDisplayName()
    {
        String name = StringUtils.getTranslatedOrFallback(this.translatedName, this.name);

        if (this.singlePlayer)
        {
            name = GuiBase.TXT_GOLD + name + GuiBase.TXT_RST;
        }

        //System.out.printf("FeatureToggle#getConfigGuiDisplayName(): translatedName [%s] // test [%s]\n", this.translatedName, name);
        return name;
    }

    @Override
    public void setPrettyName(String prettyName)
    {
        this.prettyName = prettyName;
    }

    @Override
    public void setTranslatedName(String translatedName)
    {
        this.translatedName = translatedName;
    }

    @Override
    public void setComment(String comment)
    {
        this.comment = comment;
    }

    @Override
    public boolean isDirty()
    {
        return this.getKeybind().isDirty() || this.dirty;
    }

    @Override
    public void markDirty()
    {
        this.getKeybind().markDirty();
        this.dirty = true;
    }

    @Override
    public void markClean()
    {
        this.getKeybind().markClean();
        this.dirty = false;
    }

    @Override
    public void checkIfClean()
    {
        if (this.isDirty())
        {
            this.markClean();
            this.onValueChanged();
        }
    }

    @Override
    public boolean isModified()
    {
        return this.valueBoolean != this.defaultValueBoolean;
    }

    @Override
    public boolean isModified(String newValue)
    {
        return Boolean.parseBoolean(newValue) != this.defaultValueBoolean;
    }

    @Override
    public void toggleBooleanValue()
    {
        this.updateLastBooleanHotkeyValue();
        this.valueBoolean = !this.valueBoolean;
        this.markClean();
        this.onValueChanged();
    }

    @Override
    public void resetToDefault()
    {
        this.updateLastBooleanHotkeyValue();
        boolean oldValue = this.valueBoolean;
        this.valueBoolean = this.defaultValueBoolean;

        if (oldValue != this.valueBoolean)
        {
            this.markClean();
            this.onValueChanged();
        }
    }

    @Override
    public Pair<Boolean, String> getBooleanHotkeyValue()
    {
        return Pair.of(this.valueBoolean, this.keybind.getStringValue());
    }

    @Override
    public Pair<Boolean, String> getDefaultBooleanHotkeyValue()
    {
        return Pair.of(this.defaultValueBoolean, this.keybind.getDefaultStringValue());
    }

    @Override
    public void setBooleanHotkeyValue(Pair<Boolean, String> value)
    {
        this.updateLastBooleanHotkeyValue();
        this.setBooleanValue(value.getLeft());
        this.getKeybind().setValueFromString(value.getRight());
    }

    @Override
    public Pair<Boolean, String> getLastBooleanHotkeyValue()
    {
        return this.lastBooleanHotkey;
    }

    @Override
    public void updateLastBooleanHotkeyValue()
    {
        this.lastBooleanHotkey = Pair.of(this.valueBoolean, this.keybind.getStringValue());
    }

    @Override
    public JsonElement getAsJsonElement()
    {
        return new JsonPrimitive(this.valueBoolean);
    }

    @Override
    public void setValueFromJsonElement(JsonElement element)
    {
        final boolean oldBool = this.valueBoolean;
        final String oldKeybind = this.keybind.getStringValue();

        try
        {
            if (element.isJsonPrimitive())
            {
                boolean temp = element.getAsBoolean();
                this.valueBoolean = temp;       // This seems redundant, but this makes it safer from corruption
            }
            else
            {
                MaLiLib.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", this.getName(), element);
            }

            if (oldBool != this.valueBoolean ||
                oldKeybind != null && !oldKeybind.equals(this.keybind.getStringValue()) ||
                this.isDirty())
            {
                this.markClean();

                if (!this.getLastBooleanHotkeyValue().equals(this.getBooleanHotkeyValue()))
                {
//                    MaLiLib.LOGGER.error("[TEST-ENUM/{}]: setValueFromJsonElement(): LV: [{}], OV: [{}], NV: [{}]", this.getName(),
//                                         this.getLastBooleanHotkeyValue().toString(),
//                                         Pair.of(oldBool, oldKeybind).toString(),
//                                         this.getBooleanHotkeyValue().toString()
//                    );

                    this.onValueChanged();
                }
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", this.getName(), element, e);
        }
    }
}
