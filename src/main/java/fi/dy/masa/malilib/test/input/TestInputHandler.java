package fi.dy.masa.malilib.test.input;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.ApiStatus;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.hotkeys.*;
import fi.dy.masa.malilib.render.InventoryOverlayScreen;
import fi.dy.masa.malilib.test.config.ConfigTestEnum;
import fi.dy.masa.malilib.test.config.TestHotkeys;
import fi.dy.masa.malilib.test.gui.GuiTestFileBrowser;
import fi.dy.masa.malilib.test.gui.GuiTestPosEditor;
import fi.dy.masa.malilib.test.gui.GuiTestTextFields;
import fi.dy.masa.malilib.test.render.TestInventoryOverlayHandler;
import fi.dy.masa.malilib.util.time.TimeTestExample;

@ApiStatus.Experimental
public class TestInputHandler implements IKeybindProvider
{
    private static final TestInputHandler INSTANCE = new TestInputHandler();

    private final Callbacks callback;

    private TestInputHandler()
    {
        super();
        this.callback = new Callbacks();
        this.init();
    }

    public static TestInputHandler getInstance()
    {
        return INSTANCE;
    }

    public Callbacks getCallback()
    {
        return this.callback;
    }

    public void init()
    {
        if (!MaLiLibReference.DEBUG_MODE) return;
        MaLiLibConfigs.Test.TEST_INVENTORY_OVERLAY.getKeybind().setCallback(this.callback);
        TestHotkeys.TEST_INVENTORY_OVERLAY_TOGGLE.getKeybind().setCallback(this.callback);
        TestHotkeys.TEST_GUI_KEYBIND.getKeybind().setCallback(this.callback);
        TestHotkeys.TEST_GUI_EDITOR_KEYBIND.getKeybind().setCallback(this.callback);
        TestHotkeys.TEST_GUI_FILE_BROWSER_KEYBIND.getKeybind().setCallback(this.callback);
        TestHotkeys.TEST_RUN_DATETIME_TEST.getKeybind().setCallback(this.callback);
    }

    @Override
    public void addKeysToMap(IKeybindManager manager)
    {
        if (!MaLiLibReference.DEBUG_MODE) return;
        for (IHotkey hotkey : MaLiLibConfigs.Test.HOTKEY_LIST)
        {
            manager.addKeybindToMap(hotkey.getKeybind());
        }

        for (IHotkey hotkey : TestHotkeys.HOTKEY_LIST)
        {
            manager.addKeybindToMap(hotkey.getKeybind());
        }

        for (ConfigTestEnum toggle : ConfigTestEnum.values())
        {
            manager.addKeybindToMap(toggle.getKeybind());
        }
    }

    @Override
    public void addHotkeys(IKeybindManager manager)
    {
        if (!MaLiLibReference.DEBUG_MODE) return;
        manager.addHotkeysForCategory(MaLiLibReference.MOD_NAME, MaLiLibReference.MOD_ID + ".hotkeys.category.test_option_hotkeys", MaLiLibConfigs.Test.HOTKEY_LIST);
        manager.addHotkeysForCategory(MaLiLibReference.MOD_NAME, MaLiLibReference.MOD_ID + ".hotkeys.category.test_hotkeys", MaLiLibConfigs.Test.HOTKEY_LIST);
        manager.addHotkeysForCategory(MaLiLibReference.MOD_NAME, MaLiLibReference.MOD_ID + ".hotkeys.category.test_enum_hotkeys", ImmutableList.copyOf(ConfigTestEnum.values()));
    }

    public static class Callbacks implements IHotkeyCallback
    {
        @Override
        public boolean onKeyAction(KeyAction action, IKeybind key)
        {
            Minecraft mc = Minecraft.getInstance();

            if (mc.player == null || !MaLiLibReference.DEBUG_MODE)
            {
                return false;
            }

            if (key == MaLiLibConfigs.Test.TEST_INVENTORY_OVERLAY.getKeybind())
            {
                // No message
                return true;
            }
            else if (key == TestHotkeys.TEST_INVENTORY_OVERLAY_TOGGLE.getKeybind())
            {
                if (mc.screen instanceof InventoryOverlayScreen)
                {
                    mc.setScreen(null);
                }
                else if (MaLiLibConfigs.Test.TEST_INVENTORY_OVERLAY.getBooleanValue() &&
                         MaLiLibConfigs.Test.TEST_INVENTORY_OVERLAY.getKeybind().isKeybindHeld())
                {
                    TestInventoryOverlayHandler.getInstance().refreshInventoryOverlay(mc, true, true);
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else if (key == TestHotkeys.TEST_GUI_KEYBIND.getKeybind())
            {
                System.out.printf("testGuiKeybind Callback Action: [%s] (Cancel = false)\n", action.getStringValue());
//                GuiBase.openGui(new GuiTestBlockStateList());
                GuiBase.openGui(new GuiTestTextFields());
            }
            else if (key == TestHotkeys.TEST_GUI_EDITOR_KEYBIND.getKeybind())
            {
	            System.out.printf("testGuiEditorKeybind Callback Action: [%s] (Cancel = false)\n", action.getStringValue());
	            GuiBase.openGui(new GuiTestPosEditor());
            }
            else if (key == TestHotkeys.TEST_GUI_FILE_BROWSER_KEYBIND.getKeybind())
            {
	            System.out.printf("testGuiFileBrowserKeybind Callback Action: [%s] (Cancel = false)\n", action.getStringValue());
	            GuiBase.openGui(new GuiTestFileBrowser());
            }
            else if (key == TestHotkeys.TEST_RUN_DATETIME_TEST.getKeybind())
            {
                mc.gui.getChat().addMessage(Component.nullToEmpty(TimeTestExample.runTimeDateTest()));
                mc.gui.getChat().addMessage(Component.nullToEmpty(TimeTestExample.runDurationTest()));
                return true;
            }

            return false;
        }
    }
}
