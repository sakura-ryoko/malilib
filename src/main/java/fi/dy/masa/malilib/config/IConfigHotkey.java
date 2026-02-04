package fi.dy.masa.malilib.config;

public interface IConfigHotkey
{
	default String getHotkeyStringValue() { return this.getDefaultHotkeyStringValue(); }

	default String getDefaultHotkeyStringValue() { return ""; }

	default void setHotkeyStringValue(String value) {}

	default String getLastHotkeyStringValue()
	{
		return this.getDefaultHotkeyStringValue();
	}

	default void updateLastHotkeyStringValue() {}
}
