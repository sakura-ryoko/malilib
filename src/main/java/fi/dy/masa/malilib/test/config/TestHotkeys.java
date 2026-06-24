package fi.dy.masa.malilib.test.config;

import java.util.List;
import com.google.common.collect.ImmutableList;

import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;

public class TestHotkeys
{
	private static final String HOTKEYS_KEY = MaLiLibReference.MOD_ID+".config.test_hotkeys";
	private static final KeybindSettings OVERLAY_TOGGLE = KeybindSettings.create(KeybindSettings.Context.ANY, KeyAction.PRESS, true, true, false, true);
	private static final KeybindSettings GUI_RELAXED = KeybindSettings.create(KeybindSettings.Context.GUI, KeyAction.PRESS, true, false, false, false);
	private static final KeybindSettings GUI_RELAXED_CANCEL = KeybindSettings.create(KeybindSettings.Context.GUI, KeyAction.PRESS, true, false, false, true);
	private static final KeybindSettings GUI_NO_ORDER = KeybindSettings.create(KeybindSettings.Context.GUI, KeyAction.PRESS, false, false, false, true);

	public static final ConfigHotkey    TEST_CONFIG_HOTKEY              = new ConfigHotkey("testHotkey", "", "Test Hotkey").apply(HOTKEYS_KEY);
	public static final ConfigHotkey    TEST_INVENTORY_OVERLAY_TOGGLE   = new ConfigHotkey("testInventoryOverlayToggle", "BUTTON_3", OVERLAY_TOGGLE).apply(HOTKEYS_KEY);
	public static final ConfigHotkey    TEST_GUI_KEYBIND                = new ConfigHotkey("testGuiKeybind", "").apply(HOTKEYS_KEY);
	public static final ConfigHotkey    TEST_GUI_EDITOR_KEYBIND         = new ConfigHotkey("testGuiEditorKeybind", "").apply(HOTKEYS_KEY);
	public static final ConfigHotkey    TEST_GUI_FILE_BROWSER_KEYBIND   = new ConfigHotkey("testGuiFileBrowserKeybind", "").apply(HOTKEYS_KEY);
	public static final ConfigHotkey    TEST_RUN_DATETIME_TEST          = new ConfigHotkey("testRunDateTimeTest", "").apply(HOTKEYS_KEY);

	public static final List<ConfigHotkey> HOTKEY_LIST = ImmutableList.of(
			TEST_CONFIG_HOTKEY,
			TEST_INVENTORY_OVERLAY_TOGGLE,
			TEST_GUI_KEYBIND,
			TEST_GUI_EDITOR_KEYBIND,
			TEST_GUI_FILE_BROWSER_KEYBIND,
			TEST_RUN_DATETIME_TEST
	);
}
