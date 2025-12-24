package fi.dy.masa.malilib.hotkeys;

import java.util.List;
import javax.annotation.Nullable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import com.mojang.serialization.Codec;

import fi.dy.masa.malilib.config.IConfigResettable;
import fi.dy.masa.malilib.config.IStringRepresentable;
import fi.dy.masa.malilib.util.JsonUtils;

public interface IKeybind extends IConfigResettable, IStringRepresentable
{
    boolean isValid();

	/**
	 * Return if something has recently changed for this Keybind
	 * @return (True|False)
	 */
	boolean isDirty();

	/**
	 * Marks this keybind as "dirty", ie, something has changed; and signals that onValueChanged() should be triggered.
	 */
	void markDirty();

	/**
	 * Marks this keybind as "clean", after the onValueChanged() has been triggered.
	 */
	void markClean();

    /**
     * Returns true if the keybind was pressed down during the current game tick.
     */
    boolean isPressed();

    /**
     * Returns true if the keybind is currently being held down.
     */
    boolean isKeybindHeld();

    /**
     * Checks and updated the pressed status, and fires the callback, if one is set.
     * Returns true if further processing of the just pressed key should be cancelled.
     * This return value can be determined by the callback, if one has been set.
     * Without a callback, the return value will be false to not cancel further processing.
     * @return ()
     */
    boolean updateIsPressed();

    KeybindSettings getSettings();

    /**
     * Set the settings for this keybind.
     * @param settings ()
     */
    void setSettings(KeybindSettings settings);

    void clearKeys();

    void addKey(int keyCode);

    void removeKey(int keyCode);

    /**
     * Check if this keybind is only a single key, matching the given key code.
     * This is mainly meant for checking equality against vanilla keybinds.
     * @param keyCode ()
     * @return ()
     */
    boolean matches(int keyCode);

    boolean overlaps(IKeybind other);

    void tick();

    String getKeysDisplayString();

    List<Integer> getKeys();

    void setCallback(@Nullable IHotkeyCallback callback);

    boolean areSettingsModified();

    void resetSettingsToDefaults();

    default JsonElement getAsJsonElement()
    {
        JsonObject obj = new JsonObject();
        obj.add("keys", new JsonPrimitive(this.getStringValue()));

        if (this.areSettingsModified())
        {
            obj.add("settings", this.getSettings().toJson());
        }

        return obj;
    }

    default void setValueFromJsonElement(JsonElement element)
    {
        if (element.isJsonObject())
        {
            JsonObject obj = element.getAsJsonObject();

            if (JsonUtils.hasString(obj, "keys"))
            {
                this.setValueFromString(obj.get("keys").getAsString());
            }

            if (JsonUtils.hasObject(obj, "settings"))
            {
                this.setSettings(KeybindSettings.fromJson(obj.getAsJsonObject("settings")));
            }
        }
    }
}
