package fi.dy.masa.malilib.config;

import java.util.List;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.config.options.ConfigTypeWrapper;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.util.JsonUtils;

public class ConfigUtils
{
    public static void readConfigBase(JsonObject root, String category, List<? extends IConfigBase> options)
    {
        JsonObject obj = JsonUtils.getNestedObject(root, category, false);

        if (obj != null)
        {
            for (IConfigBase option : options)
            {
                String name = option.getName();

                if (obj.has(name))
                {
                    option.setValueFromJsonElement(obj.get(name));
                }
            }
        }
    }

    public static void readHotkeys(JsonObject root, String keyHotkey, List<? extends IHotkey> hotkeys)
    {
        JsonObject objHotkeys = JsonUtils.getNestedObject(root, keyHotkey, false);

        if (objHotkeys != null)
        {
            for (IHotkey hotkey : hotkeys)
            {
                String name = hotkey.getName();
                JsonObject objKeybind = JsonUtils.getNestedObject(objHotkeys, name, false);

                if (objKeybind != null)
                {
                    hotkey.getKeybind().setValueFromJsonElement(objKeybind);
                }
                // Backwards compatibility for reading the old simple keybinds
                else if (JsonUtils.hasString(objHotkeys, name))
                {
                    hotkey.getKeybind().setValueFromString(JsonUtils.getString(objHotkeys, name));
                }
            }
        }
    }

    public static void readHotkeyToggleOptions(JsonObject root, String keyHotkey, String keyBoolean, List<? extends IHotkeyTogglable> options)
    {
        if (JsonUtils.hasObject(root, keyHotkey))
        {
            readHotkeys(root, keyHotkey, options);
        }

        if (JsonUtils.hasObject(root, keyBoolean))
        {
            readConfigBase(root, keyBoolean, options);
        }
    }

    public static void writeConfigBase(JsonObject root, String category, List<? extends IConfigBase> options)
    {
        JsonObject obj = JsonUtils.getNestedObject(root, category, true);

		if (obj == null)
		{
			obj = new JsonObject();
		}

        for (IConfigBase option : options)
        {
            obj.add(option.getName(), option.getAsJsonElement());
        }
    }

    public static void writeHotkeys(JsonObject root, String category, List<? extends IHotkey> hotkeys)
    {
        // Note: This method can't just call writeConfigBase, as the base config type might
        // not serialize the hotkey, but instead some other config data.
        // But of course all of this is just a mess in this old code base...

        JsonObject objHotkeys = JsonUtils.getNestedObject(root, category, true);

		if (objHotkeys == null)
		{
			objHotkeys = new JsonObject();
		}

        for (IHotkey hotkey : hotkeys)
        {
            IKeybind keybind = hotkey.getKeybind();
            JsonObject obj = new JsonObject();

            obj.add("keys", new JsonPrimitive(keybind.getStringValue()));

            if (keybind.areSettingsModified())
            {
                obj.add("settings", keybind.getSettings().toJson());
            }

			objHotkeys.add(hotkey.getName(), obj);
        }
    }

    public static void writeHotkeyToggleOptions(JsonObject root, String keyHotkey, String keyBoolean, List<? extends IHotkeyTogglable> options)
    {
        writeConfigBase(root, keyBoolean, options);
        writeHotkeys(root, keyHotkey, options);
    }

    /**
     * Creates a wrapper for the provided configs that pretends to be another type.<br>
     * This is useful for example for enum configs, which may contain two values per entry.<br>
     * <b>** WARNING **</b>: The configs in toWrap are assumed to actually implement the
     * interface that the wrapped type is of!! Otherwise, things will crash!
     * -
     * @param wrappedType ()
     * @param toWrap ()
     * @return ()
     */
    public static List<ConfigTypeWrapper> createConfigWrapperForType(ConfigType wrappedType, List<? extends IConfigValue> toWrap)
    {
		// Do not use Config Table.
		if (wrappedType == ConfigType.TABLE)
		{
			return List.of();
		}

        ImmutableList.Builder<ConfigTypeWrapper> builder = ImmutableList.builder();

	    for (IConfigValue iConfigValue : toWrap)
	    {
		    builder.add(new ConfigTypeWrapper(wrappedType, iConfigValue));
	    }

        return builder.build();
    }
}
