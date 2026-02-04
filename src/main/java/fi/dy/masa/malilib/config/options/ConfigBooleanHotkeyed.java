package fi.dy.masa.malilib.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.tuple.Pair;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.IHotkeyTogglable;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyCallbackToggleBooleanConfigWithMessage;
import fi.dy.masa.malilib.hotkeys.KeybindMulti;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class ConfigBooleanHotkeyed extends ConfigBoolean implements IHotkeyTogglable
{
    public static final Codec<ConfigBooleanHotkeyed> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                                        PrimitiveCodec.STRING.fieldOf("name").forGetter(ConfigBase::getName),
                                        PrimitiveCodec.BOOL.fieldOf("defaultValue").forGetter(ConfigBoolean::getDefaultBooleanValue),
                                        PrimitiveCodec.BOOL.fieldOf("value").forGetter(ConfigBoolean::getBooleanValue),
                                        PrimitiveCodec.STRING.fieldOf("defaultHotkey").forGetter(get -> get.keybind.getDefaultStringValue()),
                                        KeybindSettings.CODEC.fieldOf("keybindSettings").forGetter(get -> get.keybind.getSettings()),
                                        PrimitiveCodec.STRING.fieldOf("comment").forGetter(get -> get.comment),
                                        PrimitiveCodec.STRING.fieldOf("prettyName").forGetter(get -> get.prettyName),
                                        PrimitiveCodec.STRING.fieldOf("translatedName").forGetter(get -> get.translatedName)
                                )
                                .apply(instance, ConfigBooleanHotkeyed::new)
    );
    protected final IKeybind keybind;
    private Pair<Boolean, String> lastBooleanHotkey;

    public ConfigBooleanHotkeyed(String name, boolean defaultValue, String defaultHotkey)
    {
        this(name, defaultValue, defaultHotkey, KeybindSettings.DEFAULT, name+" Comment?", StringUtils.splitCamelCase(name), name);
    }

    public ConfigBooleanHotkeyed(String name, boolean defaultValue, String defaultHotkey, String comment)
    {
        this(name, defaultValue, defaultHotkey, KeybindSettings.DEFAULT, comment, StringUtils.splitCamelCase(name), name);
    }

    public ConfigBooleanHotkeyed(String name, boolean defaultValue, String defaultHotkey, String comment, String prettyName)
    {
        this(name, defaultValue, defaultHotkey, KeybindSettings.DEFAULT, comment, prettyName, name);
    }

    public ConfigBooleanHotkeyed(String name, boolean defaultValue, String defaultHotkey, String comment, String prettyName, String translatedName)
    {
        this(name, defaultValue, defaultHotkey, KeybindSettings.DEFAULT, comment, prettyName, translatedName);
    }

    public ConfigBooleanHotkeyed(String name, boolean defaultValue, String defaultHotkey, KeybindSettings settings)
    {
        this(name, defaultValue, defaultHotkey, settings, name+" Comment?", StringUtils.splitCamelCase(name), name);
    }

    public ConfigBooleanHotkeyed(String name, boolean defaultValue, String defaultHotkey, KeybindSettings settings, String comment)
    {
        this(name, defaultValue, defaultHotkey, settings, comment, StringUtils.splitCamelCase(name), name);
    }

    public ConfigBooleanHotkeyed(String name, boolean defaultValue, String defaultHotkey, KeybindSettings settings, String comment, String prettyName)
    {
        this(name, defaultValue, defaultHotkey, settings, comment, prettyName, name);
    }

    public ConfigBooleanHotkeyed(String name, boolean defaultValue, String defaultHotkey, KeybindSettings settings, String comment, String prettyName, String translatedName)
    {
        super(name, defaultValue, comment, prettyName, translatedName);

        this.keybind = KeybindMulti.fromStorageString(defaultHotkey, settings);
        this.keybind.setCallback(new KeyCallbackToggleBooleanConfigWithMessage(this));
        this.updateLastBooleanHotkeyValue();
    }

    private ConfigBooleanHotkeyed(String name, boolean defaultValue, boolean value, String defaultHotkey, KeybindSettings settings, String comment, String prettyName, String translatedName)
    {
        this(name, defaultValue, defaultHotkey, settings, comment, prettyName, translatedName);
        this.setBooleanValue(value);
    }

    @Override
    public IKeybind getKeybind()
    {
        return this.keybind;
    }

    public String getDefaultHotkey()
    {
        return this.keybind.getDefaultStringValue();
    }

    @Override
    public ConfigBooleanHotkeyed translatedName(String translatedName)
    {
        return (ConfigBooleanHotkeyed) super.translatedName(translatedName);
    }

    @Override
    public ConfigBooleanHotkeyed apply(String translationPrefix)
    {
        return (ConfigBooleanHotkeyed) super.apply(translationPrefix);
    }

    @Override
    public boolean isModified()
    {
        // Note: calling isModified() for the IHotkey here directly would not work
        // with multi-type configs like the FeatureToggle in Tweakeroo!
        // Thus we need to get the IKeybind and call it for that specifically.
        return super.isModified() || this.getKeybind().isModified();
    }

    @Override
    public void toggleBooleanValue()
    {
        this.updateLastBooleanHotkeyValue();
        super.toggleBooleanValue();
    }

    @Override
    public void resetToDefault()
    {
        this.updateLastBooleanHotkeyValue();
        this.keybind.resetToDefault();
        super.resetToDefault();
	}

    @Override
    public Pair<Boolean, String> getBooleanHotkeyValue()
    {
        return Pair.of(super.getBooleanValue(), this.getKeybind().getStringValue());
    }

    @Override
    public Pair<Boolean, String> getDefaultBooleanHotkeyValue()
    {
        return Pair.of(this.getDefaultBooleanValue(), this.getKeybind().getDefaultStringValue());
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
        this.lastBooleanHotkey = Pair.of(this.getBooleanValue(), this.getKeybind().getStringValue());
    }

    @Override
    public boolean isDirty()
    {
        return this.getKeybind().isDirty() || super.isDirty();
    }

    @Override
    public void markDirty()
    {
        super.markDirty();
        this.getKeybind().markDirty();
    }

    @Override
    public void markClean()
    {
        super.markClean();
        this.getKeybind().markClean();
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
    public void setValueFromJsonElement(JsonElement element)
    {
        final boolean oldBool = this.getBooleanValue();
        final String oldKeybind = this.getKeybind().getStringValue();

        try
        {
            if (element.isJsonObject())
            {
                JsonObject obj = element.getAsJsonObject();

                if (JsonUtils.hasBoolean(obj, "enabled"))
                {
                    super.setValueFromJsonElement(obj.get("enabled"));
                }

                if (JsonUtils.hasObject(obj, "hotkey"))
                {
                    JsonObject hotkeyObj = obj.getAsJsonObject("hotkey");
                    this.keybind.setValueFromJsonElement(hotkeyObj);
                }
            }
            // Backwards compatibility with the old bugged serialization that only serialized the boolean value
            else
            {
                super.setValueFromJsonElement(element);
            }

            final Pair<Boolean, String> oldValue = Pair.of(oldBool, oldKeybind);

            if (!oldValue.equals(this.getBooleanHotkeyValue()) || this.isDirty())
            {
                this.markClean();

                if (!this.getLastBooleanHotkeyValue().equals(this.getBooleanHotkeyValue()))
                {
//                    MaLiLib.LOGGER.error("[BOOL-HOTKEY/{}]: setValueFromJsonElement(): LV: [{}], OV: [{}], NV: [{}]", this.getName(),
//                                         this.getLastBooleanHotkeyValue().toString(),
//                                         oldValue,
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

    @Override
    public JsonElement getAsJsonElement()
    {
        JsonObject obj = new JsonObject();
        obj.add("enabled", super.getAsJsonElement());
        obj.add("hotkey", this.getKeybind().getAsJsonElement());
        return obj;
    }
}
