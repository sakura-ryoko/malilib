package fi.dy.masa.malilib;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.IConfigValue;
import fi.dy.masa.malilib.config.options.*;
import fi.dy.masa.malilib.config.options.table.ConfigTable;
import fi.dy.masa.malilib.config.options.table.Label;
import fi.dy.masa.malilib.config.options.table.TableRow;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.test.ConfigTestEnum;
import fi.dy.masa.malilib.test.ConfigTestLockedList;
import fi.dy.masa.malilib.test.ConfigTestOptList;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.malilib.util.time.DurationFormat;
import fi.dy.masa.malilib.util.time.TimeFormat;

import static fi.dy.masa.malilib.config.options.table.type.EntryTypes.*;

public class MaLiLibConfigs implements IConfigHandler
{
    private static final String CONFIG_FILE_NAME = MaLiLibReference.MOD_ID + ".json";

    private static final String GENERIC_KEY = MaLiLibReference.MOD_ID+".config.generic";
    public static class Generic
    {
        public static final ConfigHotkey            IGNORED_KEYS                = new ConfigHotkey            ("ignoredKeys",      "").apply(GENERIC_KEY);
        public static final ConfigHotkey            OPEN_GUI_CONFIGS            = new ConfigHotkey            ("openGuiConfigs",   "A,C").apply(GENERIC_KEY);
        public static final ConfigBooleanHotkeyed   ENABLE_ACTIONBAR_MESSAGES   = new ConfigBooleanHotkeyed   ("enableActionbarMessages", true, "").apply(GENERIC_KEY);
        public static final ConfigInteger           ACTIONBAR_HUD_TICKS         = new ConfigInteger           ("actionbarHudTicks",       60, 1, 240).apply(GENERIC_KEY);
        public static final ConfigFloat             IN_GAME_MESSAGE_TIMEOUT     = new ConfigFloat             ("inGameMessageTimeout",    5.0f, 0.5f, 15.0f).apply(GENERIC_KEY);
        public static final ConfigBooleanHotkeyed   ENABLE_CONFIG_SWITCHER      = new ConfigBooleanHotkeyed   ("enableConfigSwitcher",    true, "").apply(GENERIC_KEY);
//        public static final ConfigBooleanHotkeyed   RENDER_TRANSPARENCY_FIX     = new ConfigBooleanHotkeyed   ("renderTransparencyFix",   true, "").apply(GENERIC_KEY);
        public static final ConfigBoolean           REALMS_COMMON_CONFIG        = new ConfigBoolean           ("realmsCommonConfig",      true).apply(GENERIC_KEY);

        public static final ImmutableList<IConfigValue> OPTIONS = ImmutableList.of(
                IGNORED_KEYS,
                OPEN_GUI_CONFIGS,
                ENABLE_ACTIONBAR_MESSAGES,
                ACTIONBAR_HUD_TICKS,
                IN_GAME_MESSAGE_TIMEOUT,
                ENABLE_CONFIG_SWITCHER,
//                RENDER_TRANSPARENCY_FIX,
                REALMS_COMMON_CONFIG
        );

        // Can't add OPEN_GUI_CONFIGS here, because things will break
        public static final List<IHotkey> HOTKEY_LIST = ImmutableList.of(
                OPEN_GUI_CONFIGS,
                ENABLE_ACTIONBAR_MESSAGES,
                ENABLE_CONFIG_SWITCHER
//                RENDER_TRANSPARENCY_FIX
        );
    }

    private static final String DEBUG_KEY = MaLiLibReference.MOD_ID+".config.debug";
    public static class Debug
    {
        public static final ConfigBoolean DEBUG_MESSAGES            = new ConfigBoolean("debugMessages",false).apply(DEBUG_KEY);
        public static final ConfigBoolean CONFIG_ELEMENT_DEBUG      = new ConfigBoolean("configElementDebug", false).apply(DEBUG_KEY);
        public static final ConfigBoolean INPUT_CANCELLATION_DEBUG  = new ConfigBoolean("inputCancellationDebugging", false).apply(DEBUG_KEY);
        public static final ConfigBoolean KEYBIND_DEBUG             = new ConfigBoolean("keybindDebugging", false).apply(DEBUG_KEY);
        public static final ConfigBoolean KEYBIND_DEBUG_ACTIONBAR   = new ConfigBoolean("keybindDebuggingIngame", false).apply(DEBUG_KEY);
        public static final ConfigBoolean MOUSE_SCROLL_DEBUG        = new ConfigBoolean("mouseScrollDebug", false).apply(DEBUG_KEY);
        public static final ConfigBoolean PRINT_TRANSLATION_KEYS    = new ConfigBoolean("printTranslationKeys", false).apply(DEBUG_KEY);

        public static final ImmutableList<IConfigValue> OPTIONS = ImmutableList.of(
                DEBUG_MESSAGES,
                CONFIG_ELEMENT_DEBUG,
                INPUT_CANCELLATION_DEBUG,
                KEYBIND_DEBUG,
                KEYBIND_DEBUG_ACTIONBAR,
                MOUSE_SCROLL_DEBUG,
                PRINT_TRANSLATION_KEYS
        );

        public static final List<IHotkey> HOTKEY_LIST = ImmutableList.of(
        );
    }

    private static final String TEST_KEY = MaLiLibReference.MOD_ID+".config.test";
    private static final KeybindSettings OVERLAY_TOGGLE = KeybindSettings.create(KeybindSettings.Context.ANY, KeyAction.PRESS, true, true, false, true);
    //private static final KeybindSettings GUI_RELAXED = KeybindSettings.create(KeybindSettings.Context.GUI, KeyAction.PRESS, true, false, false, false);
    private static final KeybindSettings GUI_RELAXED_CANCEL = KeybindSettings.create(KeybindSettings.Context.GUI, KeyAction.PRESS, true, false, false, true);
    //private static final KeybindSettings GUI_NO_ORDER = KeybindSettings.create(KeybindSettings.Context.GUI, KeyAction.PRESS, false, false, false, true);
    public static class Test
    {
        public static final ConfigBoolean           TEST_CONFIG_BOOLEAN             = new ConfigBoolean("testBoolean", false, "Test Boolean").apply(TEST_KEY);
        public static final ConfigBooleanHotkeyed   TEST_CONFIG_BOOLEAN_HOTKEYED    = new ConfigBooleanHotkeyed("testBooleanHotkeyed", false, "A,K").apply(TEST_KEY);
        public static final ConfigColor             TEST_CONFIG_COLOR               = new ConfigColor("testColor", "0x3022FFFF", "Test Color").apply(TEST_KEY);
        public static final ConfigColorList         TEST_CONFIG_COLOR_LIST          = new ConfigColorList("testColorList", ImmutableList.of(new Color4f(0, 0, 0), new Color4f(255, 255, 255, 255)), "Test Color List").apply(TEST_KEY);
        public static final ConfigDouble            TEST_CONFIG_DOUBLE              = new ConfigDouble("testDouble", 0.5, 0, 1, true, "Test Double").apply(TEST_KEY);
        public static final ConfigFloat             TEST_CONFIG_FLOAT               = new ConfigFloat("testFloat", 0.5f, 0.0f, 1.0f, true, "Test Float").apply(TEST_KEY);
        public static final ConfigHotkey            TEST_CONFIG_HOTKEY              = new ConfigHotkey("testHotkey", "", "Test Hotkey").apply(TEST_KEY);
        public static final ConfigInteger           TEST_CONFIG_INTEGER             = new ConfigInteger("testInteger", 5, 1, 10, "Test Integer").apply(TEST_KEY);
        public static final ConfigOptionList        TEST_CONFIG_OPTIONS_LIST        = new ConfigOptionList("testOptionList", ConfigTestOptList.TEST1, "Test Option List").apply(TEST_KEY);
        public static final ConfigString            TEST_CONFIG_STRING              = new ConfigString("testString", "testString", "Test String").apply(TEST_KEY);
        public static final ConfigStringList        TEST_CONFIG_STRING_LIST         = new ConfigStringList("testStringList", ImmutableList.of("testString1", "testString2"), "Test String List").apply(TEST_KEY);
        public static final ConfigLockedList        TEST_CONFIG_LOCKED_LIST         = new ConfigLockedList("testLockedConfigList", ConfigTestLockedList.INSTANCE, "Test Locked List").apply(TEST_KEY);
        public static final ConfigTable             TEST_CONFIG_TABLE_1             =
                new ConfigTable.Builder("testTable1", STRING, INTEGER, BOOLEAN, LABEL, DOUBLE)
                        .build(true).apply(TEST_KEY);
        public static final ConfigTable             TEST_CONFIG_TABLE_2             =
                new ConfigTable.Builder("testTable2", LABEL, DOUBLE, DOUBLE, DOUBLE)
                        .setEntryCount(4)
                        .setDefaultValue(
                                T(L("cat:"), 0.0, 0.0, 0.0),
                                T(L("dog:"), 0.0, 0.0, 0.0),
                                T(L("cow:"), 0.0, 0.0, 0.0),
                                T(L("fox:"), 0.0, 0.0, 0.0)
                        )
                        .setAllowAddNewEntry(false)
                        .setDisplayString("Display string")
                        .setLabels("", "X position", "Y position", "Z position")
                        .build().apply(TEST_KEY);
        public static final ConfigTable             TEST_CONFIG_TABLE_3             =
                new ConfigTable.Builder("testTable3", STRING, INTEGER, INTEGER)
                        .setShowEntryNumbers(false)
                        .build().apply(TEST_KEY);
        public static final ConfigTable             TEST_CONFIG_TABLE_4             =
                new ConfigTable.Builder("testTable4", DOUBLE, INTEGER, STRING, BOOLEAN)
                        .setDefaultValue(T(0.0, 1, "2", true), T(1.0, 3, "5", false))
                        .setLabels("Label 1", L("Label 2", "With a comment!"), L("Label 3", "With a big\n\nand scary\n\ncomment >:3"))
                        .setComment("Comment")
                        .build().apply(TEST_KEY);
        public static final ConfigTable             TEST_CONFIG_TABLE_5             =
                new ConfigTable.Builder("testTable5", LABEL, DOUBLE, INTEGER, STRING)
                        .setComment("Another comment")
                        .setDefaultValue(T(L("Horizontal label!", "With comments too!"), 213.0, 43, "22"))
                        .setEntryCount(5)
                        .setAllowAddNewEntry(false)
                        .setLabels(List.of("Label 1", "Label 2"))
                        .build().apply(TEST_KEY);

        private static TableRow T(Object... objects)
        {
            return TableRow.of(objects);
        }

        private static Label L(String... strings)
        {
            return switch (strings.length)
            {
                case 0 -> Label.of();
                case 1 -> Label.of(strings[0]);
                case 2 -> Label.of(strings[0], strings[1]);
                default -> throw new IllegalArgumentException("Must be < 2 entries");
            };
        }

        public static final ConfigInteger           TEST_BUNDLE_PREVIEW_WIDTH       = new ConfigInteger("testBundlePreviewWidth", 9, 6, 9, "Test Bundle Preview Width").apply(TEST_KEY);
        public static final ConfigBooleanHotkeyed   TEST_INVENTORY_OVERLAY          = new ConfigBooleanHotkeyed("testInventoryOverlay", false, "LEFT_ALT").apply(TEST_KEY);
        public static final ConfigBooleanHotkeyed   TEST_INVENTORY_OVERLAY_OG       = new ConfigBooleanHotkeyed("testInventoryOverlayOG", false, "").apply(TEST_KEY);
        public static final ConfigHotkey            TEST_INVENTORY_OVERLAY_TOGGLE   = new ConfigHotkey("testInventoryOverlayToggle", "BUTTON_3", OVERLAY_TOGGLE).apply(TEST_KEY);
        public static final ConfigHotkey            TEST_GUI_KEYBIND                = new ConfigHotkey("testGuiKeybind", "").apply(TEST_KEY);
	    public static final ConfigHotkey            TEST_GUI_EDITOR_KEYBIND         = new ConfigHotkey("testGuiEditorKeybind", "").apply(TEST_KEY);
	    public static final ConfigHotkey            TEST_GUI_FILE_BROWSER_KEYBIND   = new ConfigHotkey("testGuiFileBrowserKeybind", "").apply(TEST_KEY);
        public static final ConfigOptionList        TEST_DATE_TIME_OPTION           = new ConfigOptionList("testDateTimeList", TimeFormat.RFC1123).apply(TEST_KEY);
        public static final ConfigOptionList        TEST_DURATION_OPTION            = new ConfigOptionList("testDurationList", DurationFormat.PRETTY).apply(TEST_KEY);
        public static final ConfigHotkey            TEST_RUN_DATETIME_TEST          = new ConfigHotkey("testRunDateTimeTest", "").apply(TEST_KEY);

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                TEST_CONFIG_BOOLEAN,
                TEST_CONFIG_BOOLEAN_HOTKEYED,
                TEST_CONFIG_COLOR,
                TEST_CONFIG_COLOR_LIST,
                TEST_CONFIG_DOUBLE,
                TEST_CONFIG_FLOAT,
                TEST_CONFIG_HOTKEY,
                TEST_CONFIG_INTEGER,
                TEST_CONFIG_OPTIONS_LIST,
                TEST_CONFIG_STRING,
                TEST_CONFIG_STRING_LIST,
                TEST_CONFIG_LOCKED_LIST,
                TEST_CONFIG_TABLE_1,
                TEST_CONFIG_TABLE_2,
                TEST_CONFIG_TABLE_3,
                TEST_CONFIG_TABLE_4,
                TEST_CONFIG_TABLE_5,
                TEST_BUNDLE_PREVIEW_WIDTH,
                TEST_INVENTORY_OVERLAY,
                TEST_INVENTORY_OVERLAY_OG,
                TEST_INVENTORY_OVERLAY_TOGGLE,
                TEST_GUI_KEYBIND,
                TEST_GUI_EDITOR_KEYBIND,
                TEST_GUI_FILE_BROWSER_KEYBIND,
                TEST_DATE_TIME_OPTION,
                TEST_DURATION_OPTION,
                TEST_RUN_DATETIME_TEST
        );

        public static final List<IHotkey> HOTKEY_LIST = ImmutableList.of(
                TEST_CONFIG_BOOLEAN_HOTKEYED,
                TEST_INVENTORY_OVERLAY,
                TEST_INVENTORY_OVERLAY_OG,
                TEST_INVENTORY_OVERLAY_TOGGLE,
                TEST_GUI_KEYBIND,
                TEST_GUI_EDITOR_KEYBIND,
                TEST_GUI_FILE_BROWSER_KEYBIND,
                TEST_RUN_DATETIME_TEST
        );
    }

    // Stuff used by any Post-Rewrite Code
    private static final String EXPERIMENTAL_KEY = MaLiLibReference.MOD_ID+".config.experimental";
    public static class Experimental
    {
        // Generic
        public static final ConfigBoolean           SORT_CONFIGS_BY_NAME            = new ConfigBoolean("sortConfigsByName", false).apply(EXPERIMENTAL_KEY);
        public static final ConfigBoolean           SORT_EXTENSION_MOD_OPTIONS      = new ConfigBoolean("sortExtensionModOptions", false).apply(EXPERIMENTAL_KEY);

        // Internal
        public static final ConfigString            ACTIVE_CONFIG_PROFILE           = new ConfigString("activeConfigProfile", "").apply(EXPERIMENTAL_KEY);

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                // Generic
                SORT_CONFIGS_BY_NAME,
                SORT_EXTENSION_MOD_OPTIONS,

                // Internal
                ACTIVE_CONFIG_PROFILE
        );
    }

    public static void loadFromFile()
    {
        Path configFile = FileUtils.getConfigDirectoryAsPath().resolve(CONFIG_FILE_NAME);

        if (Files.exists(configFile) && Files.isReadable(configFile))
        {
            JsonElement element = JsonUtils.parseJsonFileAsPath(configFile);

            if (element != null && element.isJsonObject())
            {
                JsonObject root = element.getAsJsonObject();

                ConfigUtils.readConfigBase(root, "Generic", Generic.OPTIONS);
                ConfigUtils.readConfigBase(root, "Debug", Debug.OPTIONS);

                if (MaLiLibReference.DEBUG_MODE)
                {
                    ConfigUtils.readConfigBase(root, "Test", Test.OPTIONS);
                    ConfigUtils.readHotkeyToggleOptions(root, "TestEnumHotkeys", "TestEnumToggles", ConfigTestEnum.VALUES);
                }

                if (MaLiLibReference.EXPERIMENTAL_MODE)
                {
                    ConfigUtils.readConfigBase(root, "Experimental", Experimental.OPTIONS);
                }

                //MaLiLib.debugLog("loadFromFile(): Successfully loaded config file '{}'.", configFile.toAbsolutePath());
            }
            else
            {
                MaLiLib.LOGGER.error("loadFromFile(): Failed to parse config file '{}' as a JSON element.", configFile.toAbsolutePath());
            }
        }
        /*
        else
        {
            MaLiLib.LOGGER.error("loadFromFile(): Failed to load config file '{}'.", configFile.toAbsolutePath());
        }
         */
    }

    public static void saveToFile()
    {
        Path dir = FileUtils.getConfigDirectoryAsPath();

        if (!Files.exists(dir))
        {
            FileUtils.createDirectoriesIfMissing(dir);
            //MaLiLib.debugLog("saveToFile(): Creating directory '{}'.", dir.toAbsolutePath());
        }

        if (Files.isDirectory(dir))
        {
            JsonObject root = new JsonObject();

            ConfigUtils.writeConfigBase(root, "Generic", Generic.OPTIONS);
            ConfigUtils.writeConfigBase(root, "Debug", Debug.OPTIONS);

            if (MaLiLibReference.DEBUG_MODE)
            {
                ConfigUtils.writeConfigBase(root, "Test", Test.OPTIONS);
                ConfigUtils.writeHotkeyToggleOptions(root, "TestEnumHotkeys", "TestEnumToggles", ConfigTestEnum.VALUES);
            }

            if (MaLiLibReference.EXPERIMENTAL_MODE)
            {
                ConfigUtils.writeConfigBase(root, "Experimental", Experimental.OPTIONS);
            }

            JsonUtils.writeJsonToFileAsPath(root, dir.resolve(CONFIG_FILE_NAME));

            /*
            if (JsonUtils.writeJsonToFileAsPath(root, config))
            {
                MaLiLib.debugLog("saveToFile(): Successfully saved config file '{}'.", config.toAbsolutePath());
            }
            else
            {
                MaLiLib.LOGGER.error("saveToFile(): Failed to save config file '{}'.", config.toAbsolutePath());
            }
             */
        }
        /*
        else
        {
            MaLiLib.LOGGER.error("saveToFile(): Config Folder '{}' does not exist!", dir.toAbsolutePath());
        }
         */
    }

    @Override
    public void onConfigsChanged()
    {
        saveToFile();
        loadFromFile();
    }

    @Override
    public void load()
    {
        loadFromFile();
    }

    @Override
    public void save()
    {
        saveToFile();
    }
}
