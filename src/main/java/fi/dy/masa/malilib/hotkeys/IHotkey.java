package fi.dy.masa.malilib.hotkeys;

import fi.dy.masa.malilib.config.IConfigValue;

public interface IHotkey extends IConfigValue
{
    /**
     * Returns the keybind used by this hotkey
     * @return ()
     */
    IKeybind getKeybind();

    /**
     * Returns the String representation of the value of this config. Used in the config GUI to
     * fill in the text field contents.
     * @return the String representation of the current value
     */
    @Override
    default String getStringValue()
    {
        return this.getKeybind().getStringValue();
    }

    @Override
    default String getDefaultStringValue()
    {
        return this.getKeybind().getDefaultStringValue();
    }

    /**
     * Parses the value of this config from a String. Used for example to get the new value from
     * the config GUI text field.
     * @param value ()
     */
    @Override
    default void setValueFromString(String value)
    {
        this.getKeybind().setValueFromString(value);
    }

    /**
     * Returns true if the value has been changed from the default value
     * @return ()
     */
    @Override
    default boolean isModified()
    {
        return this.getKeybind().isModified();
    }

    /**
     * Checks whether the given value would be modified from the default value.
     * @param newValue ()
     * @return ()
     */
    @Override
    default boolean isModified(String newValue)
    {
        return this.getKeybind().isModified(newValue);
    }

    /**
     * Resets the value back to the default value
     */
    @Override
    default void resetToDefault()
    {
        this.getKeybind().resetToDefault();
    }
}
