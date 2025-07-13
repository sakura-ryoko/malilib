package malilib.input;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import it.unimi.dsi.fastutil.ints.IntArrayList;

import net.minecraft.client.settings.KeyBinding;

import malilib.MaLiLib;
import malilib.MaLiLibConfigs;
import malilib.MinecraftClientAccessor;
import malilib.config.value.KeybindDisplayMode;
import malilib.gui.util.GuiUtils;
import malilib.input.callback.AdjustableValueHotkeyCallback;
import malilib.input.callback.HotkeyCallback;
import malilib.overlay.message.MessageDispatcher;
import malilib.overlay.message.MessageOutput;
import malilib.overlay.message.MessageUtils;
import malilib.render.text.StyledText;
import malilib.util.StringUtils;
import malilib.util.data.ModInfo;
import malilib.util.data.json.JsonUtils;
import malilib.util.game.wrap.GameWrap;

public class KeyBindImpl implements KeyBind
{
    private static final IntArrayList PRESSED_KEYS = new IntArrayList();

    public static final KeyUpdateResult NO_ACTION = new KeyUpdateResult(false, false);

    private static int triggeredCount;

    private final KeyBindSettings defaultSettings;
    private final IntArrayList defaultKeyCodes = new IntArrayList(4);
    private final IntArrayList keyCodes = new IntArrayList(4);
    private final IntArrayList lastSavedKeyCodes = new IntArrayList(4);
    @Nullable private HotkeyCallback callback;
    private KeyBindSettings lastSavedSettings;
    private KeyBindSettings settings;
    private ModInfo modInfo;
    private String nameTranslationKey = "";
    private boolean pressed;
    private boolean pressedToggle;
    private boolean pressedLastForWasTriggered;

    private KeyBindImpl(String defaultStorageString, KeyBindSettings settings)
    {
        this.defaultSettings = settings;
        this.defaultKeyCodes.addAll(Keys.readKeysFromStorageString(defaultStorageString));
        this.keyCodes.addAll(this.defaultKeyCodes);
        this.settings = settings;

        this.cacheSavedValue();
    }

    @Override
    public ModInfo getModInfo()
    {
        return this.modInfo;
    }

    @Override
    public void setModInfo(ModInfo modInfo)
    {
        this.modInfo = modInfo;
    }

    @Override
    public void setNameTranslationKey(String nameTranslationKey)
    {
        this.nameTranslationKey = nameTranslationKey;
    }

    @Override
    public KeyBindSettings getSettings()
    {
        return this.settings;
    }

    @Override
    public KeyBindSettings getDefaultSettings()
    {
        return this.defaultSettings;
    }

    @Override
    public void setSettings(KeyBindSettings settings)
    {
        this.settings = settings;
    }

    @Override
    public void setCallback(@Nullable HotkeyCallback callback)
    {
        this.callback = callback;
    }

    @Override
    public void clearKeys()
    {
        this.keyCodes.clear();
        this.clearPressed();
    }

    @Override
    public boolean hasKeys()
    {
        return this.keyCodes.isEmpty() == false;
    }

    @Override
    public void setKeys(IntArrayList newKeys)
    {
        this.keyCodes.clear();
        this.keyCodes.addAll(newKeys);
    }

    @Override
    public void getKeysToList(IntArrayList list)
    {
        list.addAll(this.keyCodes);
    }

    @Override
    public String getKeysDisplayString()
    {
        return Keys.writeKeysToString(this.keyCodes, " + ", Keys::charAsCharacter);
    }

    @Override
    public String getDefaultKeysDisplayString()
    {
        return Keys.writeKeysToString(this.defaultKeyCodes, " + ", Keys::charAsCharacter);
    }

    /**
     * Returns true if the keybind has been changed from the default value
     */
    @Override
    public boolean isModified()
    {
        return this.keyCodes.equals(this.defaultKeyCodes) == false;
    }

    @Override
    public boolean isDirty()
    {
        return this.lastSavedKeyCodes.equals(this.keyCodes) == false ||
               this.lastSavedSettings.equals(this.settings) == false;
    }

    @Override
    public void cacheSavedValue()
    {
        this.lastSavedKeyCodes.clear();
        this.lastSavedKeyCodes.addAll(this.keyCodes);
        this.lastSavedSettings = this.settings;
    }

    @Override
    public void resetToDefault()
    {
        this.keyCodes.clear();
        this.keyCodes.addAll(this.defaultKeyCodes);
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
    public void setValueFromString(String str)
    {
        this.clearKeys();
        this.keyCodes.addAll(Keys.readKeysFromStorageString(str));
    }

    @Override
    public boolean containsKey(int keyCode)
    {
        return this.keyCodes.contains(keyCode);
    }

    @Override
    public boolean matches(int keyCode)
    {
        return this.keyCodes.size() == 1 && this.keyCodes.getInt(0) == keyCode;
    }

    @Override
    public boolean matches(IntArrayList keys)
    {
        return this.keyCodes.equals(keys);
    }

    @Override
    public boolean isKeyBindHeld()
    {
        boolean active;

        if (this.settings.isToggle())
        {
            active = this.pressedToggle;
        }
        else
        {
            active = this.pressed || (this.settings.getAllowEmpty() && this.keyCodes.isEmpty());
        }

        return active != this.settings.getInvertHeld();
    }

    @Override
    public boolean isPhysicallyHeld()
    {
        return this.pressed;
    }

    @Override
    public boolean wasTriggered()
    {
        boolean triggered = this.pressed && this.pressedLastForWasTriggered == false;
        this.pressedLastForWasTriggered = this.pressed;
        return triggered;
    }

    private void clearPressed()
    {
        this.pressed = false;
        this.pressedLastForWasTriggered = false;
    }

    /**
     * NOT PUBLIC API - DO NOT CALL FROM MOD CODE!!!
     */
    @Override
    public KeyUpdateResult updateIsPressed(boolean isFirst)
    {
        if (this.keyCodes.isEmpty() ||
            (this.settings.getContext() != Context.ANY &&
            ((this.settings.getContext() == Context.INGAME) != GuiUtils.noScreenOpen())))
        {
            this.clearPressed();
            return NO_ACTION;
        }

        final boolean allowExtraKeys = this.settings.getAllowExtraKeys();
        final boolean pressedLast = this.pressed;
        final int sizePressed = PRESSED_KEYS.size();
        final int sizeRequired = this.keyCodes.size();

        if (sizePressed >= sizeRequired && (allowExtraKeys || sizePressed == sizeRequired))
        {
            this.pressed = PRESSED_KEYS.containsAll(this.keyCodes);
            final int pressedSize = PRESSED_KEYS.size();
            int keyCodeIndex = 0;

            for (int i = 0; i < pressedSize; ++i)
            {
                int keyCode = PRESSED_KEYS.getInt(i);

                if (this.keyCodes.getInt(keyCodeIndex) == keyCode)
                {
                    // Fully matched keybind
                    if (++keyCodeIndex >= sizeRequired)
                    {
                        break;
                    }
                }
                else if ((this.settings.isOrderSensitive() && (keyCodeIndex > 0 || sizePressed == sizeRequired)) ||
                         (this.keyCodes.contains(keyCode) == false && allowExtraKeys == false))
                {
                    /*
                    System.out.printf("km fail: key: %s, ae: %s, aoo: %s, cont: %s, keys: %s, pressed: %s, triggeredCount: %d\n",
                            keyCodeObj, allowExtraKeys, allowOutOfOrder, this.keyCodes.contains(keyCodeObj), this.keyCodes, pressedKeys, triggeredCount);
                    */
                    this.clearPressed();
                    break;
                }
            }
        }
        else
        {
            this.clearPressed();
        }

        if (this.pressed && pressedLast == false)
        {
            this.pressedToggle = ! this.pressedToggle;
        }

        KeyAction activateOn = this.settings.getActivateOn();

        if (this.pressed != pressedLast &&
            (isFirst || this.settings.getFirstOnly() == false) &&
            (triggeredCount == 0 || this.settings.isExclusive() == false) &&
            (activateOn == KeyAction.BOTH || this.pressed == (activateOn == KeyAction.PRESS)))
        {
            KeyUpdateResult result = this.triggerKeyAction(pressedLast);
            //System.out.printf("triggered, cancel: %s, triggeredCount: %d\n", cancel, triggeredCount);

            if (result.triggered)
            {
                ++triggeredCount;
            }

            return result;
        }

        return NO_ACTION;
    }

    protected KeyUpdateResult triggerKeyAction(boolean pressedLast)
    {
        KeyAction activateOn = this.settings.getActivateOn();

        if (this.pressed == false)
        {
            if (pressedLast && (activateOn == KeyAction.RELEASE || activateOn == KeyAction.BOTH))
            {
                return this.triggerKeyCallback(KeyAction.RELEASE);
            }
        }
        else if (pressedLast == false)
        {
            if (this.keyCodes.contains(Keys.KEY_F3))
            {
                // Prevent the debug GUI from opening after the F3 key is released
                ((MinecraftClientAccessor) GameWrap.getClient()).setActionKeyF3(true);
            }

            if (activateOn == KeyAction.PRESS || activateOn == KeyAction.BOTH)
            {
                return this.triggerKeyCallback(KeyAction.PRESS);
            }
        }

        return NO_ACTION;
    }

    protected KeyUpdateResult triggerKeyCallback(KeyAction action)
    {
        boolean cancel;

        if (this.callback == null)
        {
            cancel = action == KeyAction.PRESS && this.settings.getCancelCondition() == CancelCondition.ALWAYS;
        }
        else
        {
            ActionResult result = this.callback.onKeyAction(action, this);
            CancelCondition condition = this.settings.getCancelCondition();
            cancel = action == KeyAction.PRESS && (condition == CancelCondition.ALWAYS
                     || (result == ActionResult.SUCCESS && condition == CancelCondition.ON_SUCCESS)
                     || (result == ActionResult.FAIL    && condition == CancelCondition.ON_FAILURE));
        }

        this.addToastMessage(action, this.callback != null, cancel);
        this.addCancellationDebugMessage(this.callback, cancel);

        return new KeyUpdateResult(cancel, true);
    }

    protected void addToastMessage(KeyAction action, boolean hasCallback, boolean cancelled)
    {
        KeybindDisplayMode mode = MaLiLibConfigs.Generic.KEYBIND_DISPLAY.getValue();
        boolean showCallbackOnly = MaLiLibConfigs.Generic.KEYBIND_DISPLAY_CALLBACK_ONLY.getBooleanValue();
        boolean showCancelledOnly = MaLiLibConfigs.Generic.KEYBIND_DISPLAY_CANCEL_ONLY.getBooleanValue();
        boolean isBoth = this.settings.activateOn == KeyAction.BOTH;

        // FIXME This check is not great here, but should reduce some spam from the scroll adjustable hotkeys
        boolean isAdjustable = isBoth && this.callback instanceof AdjustableValueHotkeyCallback;
        boolean canShowAdjustable = isAdjustable == false || action == KeyAction.RELEASE;

        if (this.settings.getShowToast() &&
            mode != KeybindDisplayMode.NONE &&
            (showCancelledOnly == false || cancelled || (isAdjustable && action == KeyAction.RELEASE)) &&
            (showCallbackOnly == false || hasCallback) &&
            canShowAdjustable)
        {
            List<String> lines = new ArrayList<>();

            if (mode == KeybindDisplayMode.KEYS || mode == KeybindDisplayMode.KEYS_ACTIONS)
            {
                lines.add(StringUtils.translate("malilib.toast.keybind_display.keys", this.getKeysDisplayString()));
            }

            if ((mode == KeybindDisplayMode.ACTIONS || mode == KeybindDisplayMode.KEYS_ACTIONS) && this.modInfo != null)
            {
                String name = StringUtils.translate(this.nameTranslationKey);
                lines.add(StringUtils.translate("malilib.toast.keybind_display.action", this.modInfo.getModName(), name));
            }

            int displayTimeMs = MaLiLibConfigs.Generic.KEYBIND_DISPLAY_DURATION.getIntegerValue();
            MessageDispatcher.generic(displayTimeMs).type(MessageOutput.TOAST)
                             .messageMarker("keybind_display").append(true).send(StyledText.parseList(lines));
        }
    }

    protected void addCancellationDebugMessage(HotkeyCallback callback, boolean cancelled)
    {
        if (MaLiLibConfigs.Debug.INPUT_CANCEL_DEBUG.getBooleanValue() && cancelled)
        {
            String nameKey = this.nameTranslationKey;
            String mod = this.modInfo != null ? this.modInfo.getModName() : "-";
            String name = org.apache.commons.lang3.StringUtils.isBlank(nameKey) ? "-" : StringUtils.translate(nameKey);
            String keysStr = this.getKeysDisplayString();

            if (callback != null)
            {
                String key = "malilib.message.debug.input_handling_cancel_by_hotkey_callback";
                String className = callback.getClass().getName();
                MessageDispatcher.generic().console().type(MessageOutput.MESSAGE_OVERLAY)
                        .translate(key, mod, name, keysStr, className);
            }
            else
            {
                String key = "malilib.message.debug.input_handling_cancel_by_hotkey_without_callback";
                MessageDispatcher.generic().console().type(MessageOutput.MESSAGE_OVERLAY)
                        .translate(key, mod, name, keysStr);
            }
        }
    }

    @Override
    public boolean overlaps(KeyBind other)
    {
        if (other == this)
        {
            return false;
        }

        if (this.contextOverlaps(other) && this.hasKeys() && other.hasKeys())
        {
            IntArrayList thisKeys = this.keyCodes;
            IntArrayList otherKeys = new IntArrayList();
            other.getKeysToList(otherKeys);

            int thisKeyCount = thisKeys.size();
            int otherKeyCount = otherKeys.size();

            if (otherKeyCount > thisKeyCount)
            {
                return false;
            }

            KeyBindSettings otherSettings = other.getSettings();
            boolean firstMatches = thisKeys.getInt(0) == otherKeys.getInt(0);

            if (firstMatches == false &&
                ((this.settings.getAllowExtraKeys() == false && thisKeyCount < otherKeyCount) ||
                 (otherSettings.getAllowExtraKeys() == false && otherKeyCount < thisKeyCount)))
            {
                return false;
            }

            boolean o1 = this.settings.isOrderSensitive();
            boolean o2 = otherSettings.isOrderSensitive();

            // Both are order sensitive, try to "slide the shorter sequence over the longer sequence" to find a match
            if (o1 && o2)
            {
                return thisKeyCount < otherKeyCount ? Collections.indexOfSubList(otherKeys, thisKeys) != -1 : Collections.indexOfSubList(thisKeys, otherKeys) != -1;
            }
            // At least one of the keybinds is not order sensitive
            else
            {
                return thisKeyCount <= otherKeyCount ? otherKeys.containsAll(thisKeys) : thisKeys.containsAll(otherKeys);
            }
        }

        return false;
    }

    public boolean contextOverlaps(KeyBind other)
    {
        KeyBindSettings settingsOther = other.getSettings();
        Context c1 = this.settings.getContext();
        Context c2 = settingsOther.getContext();

        if (c1 == Context.ANY || c2 == Context.ANY || c1 == c2)
        {
            KeyAction a1 = this.settings.getActivateOn();
            KeyAction a2 = settingsOther.getActivateOn();

            return a1 == KeyAction.BOTH || a2 == KeyAction.BOTH || a1 == a2;
        }

        return false;
    }

    @Override
    public void setValueFromJsonElement(JsonElement element, String hotkeyName)
    {
        try
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
                    this.setSettings(KeyBindSettings.fromJson(obj.getAsJsonObject("settings")));
                }
            }
            // Backwards compatibility with some old hotkeys
            else if (element.isJsonPrimitive())
            {
                this.setValueFromString(element.getAsString());
            }
            else
            {
                MaLiLib.LOGGER.warn("Failed to set the hotkey '{}' from the JSON element '{}'", hotkeyName, element);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to set the hotkey '{}' from the JSON element '{}'", hotkeyName, element, e);
        }

        this.cacheSavedValue();
    }

    @Override
    public JsonElement getAsJsonElement()
    {
        JsonObject obj = new JsonObject();

        String str = Keys.writeKeysToString(this.keyCodes, ",", Keys::charAsStorageString);
        obj.add("keys", new JsonPrimitive(str));

        if (this.areSettingsModified())
        {
            obj.add("settings", this.getSettings().toJson());
        }

        return obj;
    }

    public static boolean hotkeyMatchesKeyBind(Hotkey hotkey, KeyBinding keybind)
    {
        return hotkey.getKeyBind().matches(keybind.getKeyCode());
    }

    public static KeyBindImpl fromStorageString(String storageString, KeyBindSettings settings)
    {
        return new KeyBindImpl(storageString, settings);
    }

    /**
     * NOT PUBLIC API - DO NOT CALL FROM MOD CODE!!!
     */
    public static void onKeyInputPre(int keyCode, int scanCode, int modifiers, char charIn, boolean keyState)
    {
        if (keyState)
        {
            KeyBind ignoredKeys = MaLiLibConfigs.Hotkeys.IGNORED_KEYS.getKeyBind();

            if (PRESSED_KEYS.contains(keyCode) == false &&
                ignoredKeys.containsKey(keyCode) == false)
            {
                PRESSED_KEYS.add(keyCode);
            }
        }
        else
        {
            PRESSED_KEYS.rem(keyCode);
        }

        if (MaLiLibConfigs.Debug.PRESSED_KEYS_TOAST.getBooleanValue())
        {
            String heldKeys;

            if (PRESSED_KEYS.isEmpty())
            {
                heldKeys = StringUtils.translate("malilib.label.misc.none.brackets");
            }
            else
            {
                heldKeys = Keys.writeKeysToString(PRESSED_KEYS, " + ", Keys::charAsStorageString);
            }

            MessageDispatcher.generic(2000).type(MessageOutput.TOAST).messageMarker("pressed_keys")
                             .translate("malilib.label.toast.pressed_keys", heldKeys);
        }

        if (MaLiLibConfigs.Debug.KEYBIND_DEBUG.getBooleanValue())
        {
            printKeyBindDebugMessage(keyCode, scanCode, modifiers, charIn, keyState);
        }
    }

    /**
     * NOT PUBLIC API - DO NOT CALL FROM MOD CODE!!!
     */
    public static void reCheckPressedKeys()
    {
        PRESSED_KEYS.removeIf((v) -> Keys.isKeyDown(v) == false);

        // Clear the triggered count after all keys have been released
        if (PRESSED_KEYS.size() == 0)
        {
            triggeredCount = 0;
        }
    }

    public static int getCurrentlyPressedKeysCount()
    {
        return PRESSED_KEYS.size();
    }

    public static int getTriggeredCount()
    {
        return triggeredCount;
    }

    public static void printKeyBindDebugMessage(int keyCode, int scanCode, int modifiers, char charIn, boolean keyState)
    {
        String action = keyState ? "PRE" : "REL";
        String keyName = Keys.getStorageStringForKeyCode(keyCode, Keys::charAsStorageString);
        String held = getActiveKeysString();
        String msg = String.format("%s '%s' (k: 0x%02X (%d), s: 0x%02X (%d), m: %d, c: '%c' = %d), held: %s",
                                   action, keyName, keyCode, keyCode, scanCode, scanCode, modifiers, charIn, (int) charIn, held);

        MaLiLib.LOGGER.info(msg);

        if (MaLiLibConfigs.Debug.KEYBIND_DEBUG_ACTIONBAR.getBooleanValue())
        {
            MessageUtils.printCustomActionbarMessage(msg);
        }

        if (MaLiLibConfigs.Debug.KEYBIND_DEBUG_TOAST.getBooleanValue())
        {
            MessageDispatcher.generic(5000).type(MessageOutput.TOAST).messageMarker("keybind_debug").send(msg);
        }
    }

    public static String getActiveKeysString()
    {
        if (PRESSED_KEYS.isEmpty() == false)
        {
            StringBuilder sb = new StringBuilder(128);
            final int size = PRESSED_KEYS.size();;

            for (int i = 0; i < size; ++i)
            {
                int key = PRESSED_KEYS.getInt(i);

                if (i > 0)
                {
                    sb.append(" + ");
                }

                String name = Keys.getStorageStringForKeyCode(key, Keys::charAsCharacter);

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
}
