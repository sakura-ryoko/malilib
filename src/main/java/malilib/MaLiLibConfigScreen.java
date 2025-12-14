package malilib;

import java.util.ArrayList;
import com.google.common.collect.ImmutableList;

import malilib.config.option.ConfigInfo;
import malilib.config.util.ConfigUtils;
import malilib.gui.BaseScreen;
import malilib.gui.BaseTabbedScreen;
import malilib.gui.action.ActionListScreen;
import malilib.gui.config.BaseConfigScreen;
import malilib.gui.config.BaseConfigTab;
import malilib.gui.config.ConfigTab;
import malilib.gui.edit.CustomHotkeysListScreen;
import malilib.gui.edit.CustomIconListScreen;
import malilib.gui.edit.overlay.AllInfoWidgetsListScreen;
import malilib.gui.edit.overlay.ConfigStatusIndicatorWidgetListScreen;
import malilib.gui.tab.BaseScreenTab;
import malilib.gui.tab.ScreenTab;
import malilib.util.data.ModInfo;

public class MaLiLibConfigScreen
{
    public static final ModInfo MOD_INFO = MaLiLibReference.MOD_INFO;

    public static final BaseConfigTab GENERIC           = new BaseConfigTab(MOD_INFO, "generic", 120, getGenericConfigs(), MaLiLibConfigScreen::create);
    public static final BaseConfigTab HOTKEYS           = new BaseConfigTab(MOD_INFO, "hotkeys", 160, MaLiLibConfigs.Hotkeys.HOTKEYS, MaLiLibConfigScreen::create);
    public static final BaseConfigTab DEBUG             = new BaseConfigTab(MOD_INFO, "debug",   120, MaLiLibConfigs.Debug.OPTIONS,   MaLiLibConfigScreen::create);
    public static final BaseScreenTab ACTIONS           = new BaseScreenTab(MOD_INFO, "actions", (scr) -> scr instanceof ActionListScreen, ActionListScreen::createActionListScreen);
    public static final BaseScreenTab CSI               = new BaseScreenTab(MOD_INFO, "config_status_indicator.abbr", (scr) -> scr instanceof ConfigStatusIndicatorWidgetListScreen, MaLiLibConfigScreen::createConfigStatusIndicatorListScreen).setHoverText("malilib.hover.button.config_status_indicator_menu");
    public static final BaseScreenTab ICONS             = new BaseScreenTab(MOD_INFO, "custom_icons", (scr) -> scr instanceof CustomIconListScreen, CustomIconListScreen::openCustomIconListScreen).setHoverText("malilib.hover.button.custom_icons_menu");
    public static final BaseScreenTab CUSTOM_HOTKEYS    = new BaseScreenTab(MOD_INFO, "custom_hotkeys", (scr) -> scr instanceof CustomHotkeysListScreen, MaLiLibConfigScreen::createCustomHotkeysEditScreen).setHoverText("malilib.hover.button.custom_hotkeys_menu");
    public static final BaseScreenTab INFO_RENDERERS    = new BaseScreenTab(MOD_INFO, "info_renderers", (scr) -> scr instanceof AllInfoWidgetsListScreen, MaLiLibConfigScreen::createInfoRendererWidgetsListScreen).setHoverText("malilib.hover.button.info_renderers_menu");

    private static final ImmutableList<ConfigTab> CONFIG_TABS = ImmutableList.of(
            GENERIC,
            HOTKEYS,
            DEBUG
    );

    public static final ImmutableList<ScreenTab> ALL_TABS = ImmutableList.of(
            GENERIC,
            HOTKEYS,
            DEBUG,
            ACTIONS,
            CUSTOM_HOTKEYS,
            CSI,
            INFO_RENDERERS,
            ICONS
    );

    public static void open()
    {
        BaseScreen.openScreen(create());
    }

    public static BaseConfigScreen create()
    {
        // The parent screen should not be set here, to prevent infinite recursion via
        // the call to the parent's setWorldAndResolution -> initScreen -> switch tab -> etc.
        return BaseConfigScreen.withExtensionModTabs(MOD_INFO, ALL_TABS, GENERIC,
                                                     "malilib.title.screen.configs", MaLiLibReference.MOD_VERSION);
    }

    public static BaseTabbedScreen createConfigStatusIndicatorListScreen()
    {
        return new ConfigStatusIndicatorWidgetListScreen();
    }

    public static BaseTabbedScreen createInfoRendererWidgetsListScreen()
    {
        return new AllInfoWidgetsListScreen();
    }

    public static BaseTabbedScreen createCustomHotkeysEditScreen()
    {
        return new CustomHotkeysListScreen();
    }

    public static ImmutableList<ConfigTab> getConfigTabs()
    {
        return CONFIG_TABS;
    }

    private static ImmutableList<ConfigInfo> getGenericConfigs()
    {
        ArrayList<ConfigInfo> list = new ArrayList<>(MaLiLibConfigs.Generic.OPTIONS);

        list.add(ConfigUtils.extractOptionsToExpandableGroup(list, MOD_INFO, "appearance",
                                                             MaLiLibConfigs.Generic.CONFIG_TOOLTIP_MAX_LINES_DIRECT,
                                                             MaLiLibConfigs.Generic.CONFIG_WIDGET_BACKGROUND,
                                                             MaLiLibConfigs.Generic.COORDINATE_DECIMAL_CLAMPING,
                                                             MaLiLibConfigs.Generic.FILE_BROWSER_DATE_FORMAT,
                                                             MaLiLibConfigs.Generic.HOVERED_LIST_ENTRY_COLOR,
                                                             MaLiLibConfigs.Generic.HOVER_TEXT_MAX_WIDTH,
                                                             MaLiLibConfigs.Generic.MESSAGE_FADE_OUT_TIME,
                                                             MaLiLibConfigs.Generic.OPTION_LIST_CONFIG_USE_DROPDOWN,
                                                             MaLiLibConfigs.Generic.SELECTED_LIST_ENTRY_COLOR,
                                                             MaLiLibConfigs.Generic.SHOW_INTERNAL_CONFIG_NAME,
                                                             MaLiLibConfigs.Generic.SORT_CONFIGS_BY_NAME,
                                                             MaLiLibConfigs.Generic.SORT_EXTENSION_MOD_OPTIONS,
                                                             MaLiLibConfigs.Generic.TRANSLATION_OVERRIDES
        ));
        ConfigUtils.sortConfigsInPlaceByDisplayName(list);

        return ImmutableList.copyOf(list);
    }
}
