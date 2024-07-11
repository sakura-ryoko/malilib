package fi.dy.masa.malilib.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
    protected final IKeybind keybind;

    public ConfigBooleanHotkeyed(String name, boolean defaultValue, String defaultHotkey, String comment)
    {
        this(name, defaultValue, defaultHotkey, comment, StringUtils.splitCamelCase(name));
    }

    public ConfigBooleanHotkeyed(String name, boolean defaultValue, String defaultHotkey, String comment, String prettyName)
    {
        this(name, defaultValue, defaultHotkey, KeybindSettings.DEFAULT, comment, prettyName, name);
    }

    public ConfigBooleanHotkeyed(String name, boolean defaultValue, String defaultHotkey, KeybindSettings settings, String comment, String prettyName, String translatedName)
    {
        super(name, defaultValue, comment, prettyName, translatedName);

        this.keybind = KeybindMulti.fromStorageString(defaultHotkey, settings);
        this.keybind.setCallback(new KeyCallbackToggleBooleanConfigWithMessage(this));
    }

    @Override
    public IKeybind getKeybind()
    {
        return this.keybind;
    }

    @Override
    public ConfigBooleanHotkeyed translatedName(String translatedName)
    {
        return (ConfigBooleanHotkeyed) super.translatedName(translatedName);
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
    public void resetToDefault()
    {
        super.resetToDefault();
        this.keybind.resetToDefault();
    }

    @Override
    public void setValueFromJsonElement(JsonElement element)
    {
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
        }
        catch (Exception e)
        {
            MaLiLib.logger.warn("Failed to set config value for '{}' from the JSON element '{}'", this.getName(), element, e);
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
