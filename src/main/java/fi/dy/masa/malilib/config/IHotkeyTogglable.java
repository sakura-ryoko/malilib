package fi.dy.masa.malilib.config;

import org.apache.commons.lang3.tuple.Pair;

import fi.dy.masa.malilib.hotkeys.IHotkey;

public interface IHotkeyTogglable extends IConfigBoolean, IHotkey
{
	default Pair<Boolean, String> getBooleanHotkeyValue() { return this.getDefaultBooleanHotkeyValue(); }

	default Pair<Boolean, String> getDefaultBooleanHotkeyValue() { return Pair.of(false, ""); }

	default void setBooleanHotkeyValue(Pair<Boolean, String> value) {}

	default Pair<Boolean, String> getLastBooleanHotkeyValue() { return this.getDefaultBooleanHotkeyValue(); }

	default void updateLastBooleanHotkeyValue() {}
}
