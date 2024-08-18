package fi.dy.masa.malilib.hotkeys;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.hotkeys.KeybindSettings.Context;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.IF3KeyStateSetter;
import fi.dy.masa.malilib.util.InfoUtils;
import fi.dy.masa.malilib.util.KeyCodes;

public class KeybindMulti implements IKeybind
{
    private static final ArrayList<Integer> PRESSED_KEYS = new ArrayList<>();
    private static int triggeredCount;

    private final String defaultStorageString;
    private final KeybindSettings defaultSettings;
    private List<Integer> keyCodes = new ArrayList<>(4);
    private KeybindSettings settings;
    private boolean pressed;
    private boolean pressedLast;
    private int heldTime;
    @Nullable
    private IHotkeyCallback callback;

    private KeybindMulti(String defaultStorageString, KeybindSettings settings)
    {
        this.defaultStorageString = defaultStorageString;
        this.defaultSettings = settings;
        this.settings = settings;
    }

    @Override
    public KeybindSettings getSettings()
    {
        return this.settings;
    }

    @Override
    public void setSettings(KeybindSettings settings)
    {
        this.settings = settings;
    }

    @Override
    public void setCallback(@Nullable IHotkeyCallback callback)
    {
        this.callback = callback;
    }

    @Override
    public boolean isValid()
    {
        return this.keyCodes.isEmpty() == false || this.settings.getAllowEmpty();
    }

    /**
     * Checks if this keybind is now active but previously was not active,
     * and then updates the cached state.
     * @return true if this keybind just became pressed
     */
    @Override
    public boolean isPressed()
    {
        return this.pressed && this.pressedLast == false && this.heldTime == 0;
    }

    @Override
    public boolean isKeybindHeld()
    {
        return this.pressed || (this.settings.getAllowEmpty() && this.keyCodes.isEmpty());
    }

    /**
     * NOT PUBLIC API - DO NOT CALL FROM MOD CODE!!!
     */
    @Override
    public boolean updateIsPressed()
    {
        if (this.keyCodes.isEmpty() ||
            (this.settings.getContext() != KeybindSettings.Context.ANY &&
            ((this.settings.getContext() == KeybindSettings.Context.INGAME) != (GuiUtils.getCurrentScreen() == null))))
        {
            this.pressed = false;
            return false;
        }

        boolean allowExtraKeys = this.settings.getAllowExtraKeys();
        boolean allowOutOfOrder = this.settings.isOrderSensitive() == false;
        boolean pressedLast = this.pressed;
        final int sizePressed = PRESSED_KEYS.size();
        final int sizeRequired = this.keyCodes.size();

        if (sizePressed >= sizeRequired && (allowExtraKeys || sizePressed == sizeRequired))
        {
            int keyCodeIndex = 0;
            this.pressed = PRESSED_KEYS.containsAll(this.keyCodes);

            for (int i = 0; i < sizePressed; ++i)
            {
                Integer keyCodeObj = PRESSED_KEYS.get(i);

                if (this.keyCodes.get(keyCodeIndex).equals(keyCodeObj))
                {
                    // Fully matched keybind
                    if (++keyCodeIndex >= sizeRequired)
                    {
                        break;
                    }
                }
                else if ((allowOutOfOrder == false && (keyCodeIndex > 0 || sizePressed == sizeRequired)) ||
                         (this.keyCodes.contains(keyCodeObj) == false && allowExtraKeys == false))
                {
                    /*
                    System.out.printf("km fail: key: %s, ae: %s, aoo: %s, cont: %s, keys: %s, pressed: %s, triggeredCount: %d\n",
                            keyCodeObj, allowExtraKeys, allowOutOfOrder, this.keyCodes.contains(keyCodeObj), this.keyCodes, pressedKeys, triggeredCount);
                    */
                    this.pressed = false;
                    break;
                }
            }
        }
        else
        {
            this.pressed = false;
        }

        KeyAction activateOn = this.settings.getActivateOn();

        if (this.pressed != pressedLast &&
            (triggeredCount == 0 || this.settings.isExclusive() == false) &&
            (activateOn == KeyAction.BOTH || this.pressed == (activateOn == KeyAction.PRESS)))
        {
            boolean cancel = this.triggerKeyAction(pressedLast) && this.settings.shouldCancel();
            //System.out.printf("triggered, cancel: %s, triggeredCount: %d\n", cancel, triggeredCount);

            if (cancel)
            {
                ++triggeredCount;
            }

            return cancel;
        }

        return false;
    }

    private boolean triggerKeyAction(boolean pressedLast)
    {
        boolean cancel = false;

        if (this.pressed == false)
        {
            this.heldTime = 0;
            KeyAction activateOn = this.settings.getActivateOn();

            if (pressedLast && this.callback != null && (activateOn == KeyAction.RELEASE || activateOn == KeyAction.BOTH))
            {
                cancel = this.callback.onKeyAction(KeyAction.RELEASE, this);
            }
        }
        else if (pressedLast == false && this.heldTime == 0)
        {
            if (this.keyCodes.contains(KeyCodes.KEY_F3))
            {
                // Prevent the debug GUI from opening after the F3 key is released
                ((IF3KeyStateSetter) MinecraftClient.getInstance().keyboard).malilib$setF3KeyState(true);
            }

            KeyAction activateOn = this.settings.getActivateOn();

            if (this.callback != null && (activateOn == KeyAction.PRESS || activateOn == KeyAction.BOTH))
            {
                cancel = this.callback.onKeyAction(KeyAction.PRESS, this);
            }
        }

        if (cancel && MaLiLibConfigs.Debug.INPUT_CANCELLATION_DEBUG.getBooleanValue())
        {
            String msg = String.format("Cancel requested by callback '%s'", this.callback.getClass().getName());
            InfoUtils.showInGameMessage(Message.MessageType.INFO, msg);
            MaLiLib.logger.info(msg);
        }

        return cancel;
    }

    @Override
    public void clearKeys()
    {
        this.keyCodes.clear();
        this.pressed = false;
        this.heldTime = 0;
    }

    @Override
    public void addKey(int keyCode)
    {
        if (this.keyCodes.contains(keyCode) == false)
        {
            this.keyCodes.add(keyCode);
        }
    }

    @Override
    public void tick()
    {
        if (this.pressed)
        {
            this.heldTime++;
        }

        this.pressedLast = this.pressed;
    }

    @Override
    public void removeKey(int keyCode)
    {
        this.keyCodes.remove(keyCode);
    }

    @Override
    public List<Integer> getKeys()
    {
        return this.keyCodes;
    }

    @Override
    public String getKeysDisplayString()
    {
        return this.getStringValue().replaceAll(",", " + ");
    }

    /**
     * Returns true if the keybind has been changed from the default value
     */
    @Override
    public boolean isModified()
    {
        return this.getStringValue().equals(this.defaultStorageString) == false;
    }

    @Override
    public boolean isModified(String newValue)
    {
        return this.defaultStorageString.equals(newValue) == false;
    }

    @Override
    public void resetToDefault()
    {
        this.setValueFromString(this.defaultStorageString);
    }

    @Override
    public boolean areSettingsModified()
    {
        return this.settings.equals(this.defaultSettings) == false;
    }

    @Override
    public void resetSettingsToDefaults()
    {
        this.settings = this.defaultSettings;
    }

    @Override
    public String getStringValue()
    {
        StringBuilder sb = new StringBuilder(32);

        for (int i = 0; i < this.keyCodes.size(); ++i)
        {
            if (i > 0)
            {
                sb.append(",");
            }

            int keyCode = this.keyCodes.get(i).intValue();
            String name = getStorageStringForKeyCode(keyCode);

            if (name != null)
            {
                sb.append(name);
            }
        }

        return sb.toString();
    }

    @Override
    public String getDefaultStringValue()
    {
        return this.defaultStorageString;
    }

    @Override
    public void setValueFromString(String str)
    {
        this.clearKeys();
        String[] keys = str.split(",");

        for (String keyName : keys)
        {
            keyName = keyName.trim();

            if (keyName.isEmpty() == false)
            {
                int keyCode = KeyCodes.getKeyCodeFromName(keyName);

                if (keyCode != KeyCodes.KEY_NONE)
                {
                    this.addKey(keyCode);
                }
            }
        }
    }

    @Override
    public boolean matches(int keyCode)
    {
        return this.keyCodes.size() == 1 && this.keyCodes.get(0) == keyCode;
    }

    public static int getKeyCode(KeyBinding keybind)
    {
        InputUtil.Key input = InputUtil.fromTranslationKey(keybind.getBoundKeyTranslationKey());
        return input.getCategory() == InputUtil.Type.MOUSE ? input.getCode() - 100 : input.getCode();
    }

    public static boolean hotkeyMatchesKeybind(IHotkey hotkey, KeyBinding keybind)
    {
        int keyCode = getKeyCode(keybind);
        return hotkey.getKeybind().matches(keyCode);
    }

    @Override
    public boolean overlaps(IKeybind other)
    {
        if (other == this || other.getKeys().size() > this.getKeys().size())
        {
            return false;
        }

        if (this.contextOverlaps(other))
        {
            KeybindSettings settingsOther = other.getSettings();
            boolean o1 = this.settings.isOrderSensitive();
            boolean o2 = settingsOther.isOrderSensitive();
            List<Integer> keys1 = this.getKeys();
            List<Integer> keys2 = other.getKeys();
            int l1 = keys1.size();
            int l2 = keys2.size();

            if (l1 == 0 || l2 == 0)
            {
                return false;
            }

            if ((this.settings.getAllowExtraKeys() == false && l1 < l2 && keys1.get(0) != keys2.get(0)) ||
                (settingsOther.getAllowExtraKeys() == false && l2 < l1 && keys1.get(0) != keys2.get(0)))
            {
                return false;
            }

            // Both are order sensitive, try to "slide the shorter sequence over the longer sequence" to find a match
            if (o1 && o2)
            {
                return l1 < l2 ? Collections.indexOfSubList(keys2, keys1) != -1 : Collections.indexOfSubList(keys1, keys2) != -1;
            }
            // At least one of the keybinds is not order sensitive
            else
            {
                return l1 <= l2 ? keys2.containsAll(keys1) : keys1.containsAll(keys2);
            }
        }

        return false;
    }

    public boolean contextOverlaps(IKeybind other)
    {
        KeybindSettings settingsOther = other.getSettings();
        Context c1 = this.settings.getContext();
        Context c2 = settingsOther.getContext();

        if (c1 == Context.ANY || c2 == Context.ANY || c1 == c2)
        {
            KeyAction a1 = this.settings.getActivateOn();
            KeyAction a2 = settingsOther.getActivateOn();

            if (a1 == KeyAction.BOTH || a2 == KeyAction.BOTH || a1 == a2)
            {
                return true;
            }
        }

        return false;
    }

    public static KeybindMulti fromStorageString(String str, KeybindSettings settings)
    {
        KeybindMulti keybind = new KeybindMulti(str, settings);
        keybind.setValueFromString(str);
        return keybind;
    }

    public static boolean isKeyDown(int keyCode)
    {
        if (keyCode == -1)
        {
            return false;
        }

        long window = MinecraftClient.getInstance().getWindow().getHandle();

        if (keyCode >= 0)
        {
            return GLFW.glfwGetKey(window, keyCode) == GLFW.GLFW_PRESS;
        }

        keyCode += 100;

        return keyCode >= 0 && GLFW.glfwGetMouseButton(window, keyCode) == GLFW.GLFW_PRESS;
    }

    /**
     * NOT PUBLIC API - DO NOT CALL FROM MOD CODE!!!
     */
    public static void onKeyInputPre(int keyCode, int scanCode, int modifiers, int action)
    {
        if (keyCode != -1)
        {
            Integer valObj = keyCode;
            boolean state = action != GLFW.GLFW_RELEASE;

            if (state)
            {
                if (PRESSED_KEYS.contains(valObj) == false)
                {
                    Collection<Integer> ignored = MaLiLibConfigs.Generic.IGNORED_KEYS.getKeybind().getKeys();

                    if (ignored.size() == 0 || ignored.contains(valObj) == false)
                    {
                        PRESSED_KEYS.add(valObj);
                    }
                }
            }
            else
            {
                PRESSED_KEYS.remove(valObj);
            }
        }

        if (MaLiLibConfigs.Debug.KEYBIND_DEBUG.getBooleanValue())
        {
            printKeybindDebugMessage(keyCode, scanCode, modifiers, action);
        }
    }

    /**
     * NOT PUBLIC API - DO NOT CALL FROM MOD CODE!!!
     */
    public static void reCheckPressedKeys()
    {
        Iterator<Integer> iter = PRESSED_KEYS.iterator();

        while (iter.hasNext())
        {
            int keyCode = iter.next().intValue();

            if (isKeyDown(keyCode) == false)
            {
                iter.remove();
            }
        }

        // Clear the triggered count after all keys have been released
        if (PRESSED_KEYS.size() == 0)
        {
            triggeredCount = 0;
        }
    }

    private static void printKeybindDebugMessage(int keyCode, int scanCode, int modifiers, int action)
    {
        String keyName = keyCode != KeyCodes.KEY_NONE ? KeyCodes.getNameForKey(keyCode) : "<unknown>";
        String type = action == GLFW.GLFW_PRESS ? "PRESS" : (action == GLFW.GLFW_RELEASE ? "RELEASE" : "REPEAT");
        String held = getActiveKeysString();
        String msg = String.format("%s %s (%d, m: %d), held: %s", type, keyName, keyCode, modifiers, held);
        String msgConsole = String.format("%s %s (keyCode: %d, scanCode: %d, modifiers: %d), held keys: %s",
                                          type, keyName, keyCode, scanCode, modifiers, held);

        if (action != GLFW.GLFW_REPEAT)
        {
            MaLiLib.logger.info(msgConsole);
        }

        if (MaLiLibConfigs.Debug.KEYBIND_DEBUG_ACTIONBAR.getBooleanValue())
        {
            InfoUtils.printActionbarMessage(msg);
        }
    }

    public static String getActiveKeysString()
    {
        if (PRESSED_KEYS.isEmpty() == false)
        {
            StringBuilder sb = new StringBuilder(128);
            int i = 0;

            for (int key : PRESSED_KEYS)
            {
                if (i > 0)
                {
                    sb.append(" + ");
                }

                String name = getStorageStringForKeyCode(key);

                if (name != null)
                {
                    sb.append(String.format("%s (%d)", name, key));
                }

                i++;
            }

            return sb.toString();
        }

        return "<none>";
    }

    @Nullable
    public static String getStorageStringForKeyCode(int keyCode)
    {
        return KeyCodes.getNameForKey(keyCode);
    }

    public static int getTriggeredCount()
    {
        return triggeredCount;
    }

    public IHotkeyCallback getCallback() {
        return callback;
    }
}
