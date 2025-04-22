package malilib.input;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import malilib.MaLiLib;

@SuppressWarnings("unused")
public class Keys
{
    private static final Pattern PATTERN_CHAR_CODE = Pattern.compile("^CHAR_(?<code>[0-9]+)$");

    private static final Object2IntOpenHashMap<String> NAMES_TO_KEYCODES = new Object2IntOpenHashMap<>();
    private static final Int2ObjectOpenHashMap<String> KEYCODES_TO_NAMES = new Int2ObjectOpenHashMap<>();

    public static final int KEY_NONE            = Keyboard.KEY_NONE;
    public static final int KEY_0               = Keyboard.KEY_0;
    public static final int KEY_1               = Keyboard.KEY_1;
    public static final int KEY_2               = Keyboard.KEY_2;
    public static final int KEY_3               = Keyboard.KEY_3;
    public static final int KEY_4               = Keyboard.KEY_4;
    public static final int KEY_5               = Keyboard.KEY_5;
    public static final int KEY_6               = Keyboard.KEY_6;
    public static final int KEY_7               = Keyboard.KEY_7;
    public static final int KEY_8               = Keyboard.KEY_8;
    public static final int KEY_9               = Keyboard.KEY_9;
    public static final int KEY_A               = Keyboard.KEY_A;
    public static final int KEY_B               = Keyboard.KEY_B;
    public static final int KEY_C               = Keyboard.KEY_C;
    public static final int KEY_D               = Keyboard.KEY_D;
    public static final int KEY_E               = Keyboard.KEY_E;
    public static final int KEY_F               = Keyboard.KEY_F;
    public static final int KEY_G               = Keyboard.KEY_G;
    public static final int KEY_H               = Keyboard.KEY_H;
    public static final int KEY_I               = Keyboard.KEY_I;
    public static final int KEY_J               = Keyboard.KEY_J;
    public static final int KEY_K               = Keyboard.KEY_K;
    public static final int KEY_L               = Keyboard.KEY_L;
    public static final int KEY_M               = Keyboard.KEY_M;
    public static final int KEY_N               = Keyboard.KEY_N;
    public static final int KEY_O               = Keyboard.KEY_O;
    public static final int KEY_P               = Keyboard.KEY_P;
    public static final int KEY_Q               = Keyboard.KEY_Q;
    public static final int KEY_R               = Keyboard.KEY_R;
    public static final int KEY_S               = Keyboard.KEY_S;
    public static final int KEY_T               = Keyboard.KEY_T;
    public static final int KEY_U               = Keyboard.KEY_U;
    public static final int KEY_V               = Keyboard.KEY_V;
    public static final int KEY_W               = Keyboard.KEY_W;
    public static final int KEY_X               = Keyboard.KEY_X;
    public static final int KEY_Y               = Keyboard.KEY_Y;
    public static final int KEY_Z               = Keyboard.KEY_Z;
    public static final int KEY_SPACE           = Keyboard.KEY_SPACE;
    public static final int KEY_APOSTROPHE      = Keyboard.KEY_APOSTROPHE;
    public static final int KEY_COMMA           = Keyboard.KEY_COMMA;
    public static final int KEY_MINUS           = Keyboard.KEY_MINUS;
    public static final int KEY_PERIOD          = Keyboard.KEY_PERIOD;
    public static final int KEY_SLASH           = Keyboard.KEY_SLASH;
    public static final int KEY_SEMICOLON       = Keyboard.KEY_SEMICOLON;
    public static final int KEY_EQUAL           = Keyboard.KEY_EQUALS;
    public static final int KEY_BACKSLASH       = Keyboard.KEY_BACKSLASH;
    public static final int KEY_ESCAPE          = Keyboard.KEY_ESCAPE;
    public static final int KEY_ENTER           = Keyboard.KEY_RETURN;
    public static final int KEY_TAB             = Keyboard.KEY_TAB;
    public static final int KEY_BACKSPACE       = Keyboard.KEY_BACK;
    public static final int KEY_INSERT          = Keyboard.KEY_INSERT;
    public static final int KEY_DELETE          = Keyboard.KEY_DELETE;
    public static final int KEY_RIGHT           = Keyboard.KEY_RIGHT;
    public static final int KEY_LEFT            = Keyboard.KEY_LEFT;
    public static final int KEY_DOWN            = Keyboard.KEY_DOWN;
    public static final int KEY_UP              = Keyboard.KEY_UP;
    public static final int KEY_PAGE_UP         = Keyboard.KEY_PRIOR;
    public static final int KEY_PAGE_DOWN       = Keyboard.KEY_NEXT;
    public static final int KEY_HOME            = Keyboard.KEY_HOME;
    public static final int KEY_END             = Keyboard.KEY_END;
    public static final int KEY_LEFT_BRACKET    = Keyboard.KEY_LBRACKET;
    public static final int KEY_RIGHT_BRACKET   = Keyboard.KEY_RBRACKET;
    public static final int KEY_F1              = Keyboard.KEY_F1;
    public static final int KEY_F2              = Keyboard.KEY_F2;
    public static final int KEY_F3              = Keyboard.KEY_F3;
    public static final int KEY_F4              = Keyboard.KEY_F4;
    public static final int KEY_F5              = Keyboard.KEY_F5;
    public static final int KEY_F6              = Keyboard.KEY_F6;
    public static final int KEY_F7              = Keyboard.KEY_F7;
    public static final int KEY_F8              = Keyboard.KEY_F8;
    public static final int KEY_F9              = Keyboard.KEY_F9;
    public static final int KEY_F10             = Keyboard.KEY_F10;
    public static final int KEY_F11             = Keyboard.KEY_F11;
    public static final int KEY_F12             = Keyboard.KEY_F12;
    public static final int KEY_F13             = Keyboard.KEY_F13;
    public static final int KEY_F14             = Keyboard.KEY_F14;
    public static final int KEY_F15             = Keyboard.KEY_F15;
    public static final int KEY_F16             = Keyboard.KEY_F16;
    public static final int KEY_F17             = Keyboard.KEY_F17;
    public static final int KEY_F18             = Keyboard.KEY_F18;
    public static final int KEY_F19             = Keyboard.KEY_F19;
    /*
    public static final int KEY_F20             = Keyboard.KEY_F20;
    public static final int KEY_F21             = Keyboard.KEY_F21;
    public static final int KEY_F22             = Keyboard.KEY_F22;
    public static final int KEY_F23             = Keyboard.KEY_F23;
    public static final int KEY_F24             = Keyboard.KEY_F24;
    public static final int KEY_F25             = Keyboard.KEY_F25;
    public static final int KEY_KP_0            = Keyboard.KEY_KP_0;
    public static final int KEY_KP_1            = Keyboard.KEY_KP_1;
    public static final int KEY_KP_2            = Keyboard.KEY_KP_2;
    public static final int KEY_KP_3            = Keyboard.KEY_KP_3;
    public static final int KEY_KP_4            = Keyboard.KEY_KP_4;
    public static final int KEY_KP_5            = Keyboard.KEY_KP_5;
    public static final int KEY_KP_6            = Keyboard.KEY_KP_6;
    public static final int KEY_KP_7            = Keyboard.KEY_KP_7;
    public static final int KEY_KP_8            = Keyboard.KEY_KP_8;
    public static final int KEY_KP_9            = Keyboard.KEY_KP_9;
    public static final int KEY_KP_DECIMAL      = Keyboard.KEY_KP_DECIMAL;
    */
    public static final int KEY_KP_DIVIDE       = Keyboard.KEY_DIVIDE;
    public static final int KEY_KP_MULTIPLY     = Keyboard.KEY_MULTIPLY;
    public static final int KEY_KP_SUBTRACT     = Keyboard.KEY_SUBTRACT;
    public static final int KEY_KP_ADD          = Keyboard.KEY_ADD;
    public static final int KEY_KP_ENTER        = Keyboard.KEY_NUMPADENTER;
    public static final int KEY_KP_EQUAL        = Keyboard.KEY_EQUALS; // ?
    public static final int KEY_LEFT_SHIFT      = Keyboard.KEY_LSHIFT;
    public static final int KEY_LEFT_CONTROL    = Keyboard.KEY_LCONTROL;
    public static final int KEY_LEFT_ALT        = Keyboard.KEY_LMENU;
    public static final int KEY_LEFT_SUPER      = Keyboard.KEY_LMETA;
    public static final int KEY_RIGHT_SHIFT     = Keyboard.KEY_RSHIFT;
    public static final int KEY_RIGHT_CONTROL   = Keyboard.KEY_RCONTROL;
    public static final int KEY_RIGHT_ALT       = Keyboard.KEY_RMENU;
    public static final int KEY_RIGHT_SUPER     = Keyboard.KEY_RMETA;
    public static final int KEY_GRAVE_ACCENT    = Keyboard.KEY_GRAVE;
    public static final int KEY_CAPS_LOCK       = Keyboard.KEY_CAPITAL; // ?
    public static final int KEY_SCROLL_LOCK     = Keyboard.KEY_SCROLL; // ?
    public static final int KEY_NUM_LOCK        = Keyboard.KEY_NUMLOCK;
    public static final int KEY_PAUSE           = Keyboard.KEY_PAUSE;
    /*
    public static final int KEY_WORLD_1         = Keyboard.KEY_WORLD_1;
    public static final int KEY_WORLD_2         = Keyboard.KEY_WORLD_2;
    public static final int KEY_PRINT_SCREEN    = Keyboard.KEY_PRINT_SCREEN;
    public static final int KEY_MENU            = Keyboard.KEY_MENU;
    */

    static
    {
        initKeys();
    }

    /**
     * @return the run-time key code for the given key name,
     *         or Keyboard.KEY_NONE if no valid id could be determined.
     */
    public static int getKeyCodeForStorageString(String keyName)
    {
        int keyCode = NAMES_TO_KEYCODES.getInt(keyName);

        if (keyCode == Keys.KEY_NONE)
        {
            keyCode = Keyboard.getKeyIndex(keyName);
        }

        if (keyCode == Keys.KEY_NONE)
        {
            Matcher matcher = PATTERN_CHAR_CODE.matcher(keyName);

            if (matcher.matches())
            {
                try
                {
                    keyCode = Integer.parseInt(matcher.group("code")) + 256;
                }
                catch (Exception ignore) {}
            }
        }

        if (keyCode == Keys.KEY_NONE)
        {
            keyCode = Mouse.getButtonIndex(keyName);

            if (keyCode >= 0 && keyCode < Mouse.getButtonCount())
            {
                keyCode -= 100;
            }
            else
            {
                keyCode = Keys.KEY_NONE;
            }
        }

        return keyCode;
    }

    /**
     * @param keyCode the key code for which the name is being requested
     * @param charEncoder the character encoder to use in case they key code value is 256 or greater. In such a case the value the encoder gets is keyCode - 256.
     * @return Returns the string to store in the config files for the given key code.
     *         If no valid mapping could be determined, then null is returned.
     */
    @Nullable
    public static String getStorageStringForKeyCode(int keyCode, Function<Integer, String> charEncoder)
    {
        String name = KEYCODES_TO_NAMES.get(keyCode);

        if (name != null)
        {
            return name;
        }

        if (keyCode > 0 && keyCode < 256)
        {
            return Keyboard.getKeyName(keyCode);
        }
        else if (keyCode >= 256)
        {
            return charEncoder.apply(keyCode - 256);
        }
        else if (keyCode < 0)
        {
            keyCode += 100;

            if (keyCode >= 0 && keyCode < Mouse.getButtonCount())
            {
                return Mouse.getButtonName(keyCode);
            }
        }

        return null;
    }

    public static boolean isKeyDown(int keyCode)
    {
        if (keyCode > 0)
        {
            return keyCode < Keyboard.getKeyCount() && Keyboard.isKeyDown(keyCode);
        }

        keyCode += 100;

        return keyCode >= 0 && keyCode < Mouse.getButtonCount() && Mouse.isButtonDown(keyCode);
    }

    public static IntArrayList readKeysFromStorageString(String str)
    {
        IntArrayList keyCodes = new IntArrayList();
        String[] keys = str.split(",");

        for (String keyName : keys)
        {
            keyName = keyName.trim();

            if (keyName.isEmpty() == false)
            {
                int keyCode = getKeyCodeForStorageString(keyName);

                if (keyCode != Keys.KEY_NONE && keyCodes.contains(keyCode) == false)
                {
                    keyCodes.add(keyCode);
                }
            }
        }

        return keyCodes;
    }

    public static String writeKeysToString(IntArrayList keyCodes, String separator, Function<Integer, String> charEncoder)
    {
        StringBuilder sb = new StringBuilder(32);
        final int size = keyCodes.size();

        for (int i = 0; i < size; ++i)
        {
            if (i > 0)
            {
                sb.append(separator);
            }

            int keyCode = keyCodes.getInt(i);
            String name = getStorageStringForKeyCode(keyCode, charEncoder);

            if (name != null)
            {
                sb.append(name);
            }
        }

        return sb.toString();
    }

    public static String charAsStorageString(int charIn)
    {
        return String.format("CHAR_%d", charIn);
    }

    public static String charAsCharacter(int charIn)
    {
        return String.valueOf((char) (charIn & 0xFF));
    }

    public static void initKeys()
    {
        NAMES_TO_KEYCODES.defaultReturnValue(Keys.KEY_NONE);

        addNameOverride(Keys.KEY_LEFT_ALT, "L_ALT");
        addNameOverride(Keys.KEY_RIGHT_ALT, "R_ALT");
        addNameOverride(Keys.KEY_LEFT_SHIFT, "L_SHIFT");
        addNameOverride(Keys.KEY_RIGHT_SHIFT, "R_SHIFT");
        addNameOverride(Keys.KEY_LEFT_CONTROL, "L_CTRL");
        addNameOverride(Keys.KEY_RIGHT_CONTROL, "R_CTRL");
        addNameOverride(Keys.KEY_PAGE_UP, "PAGE_UP");
        addNameOverride(Keys.KEY_PAGE_DOWN, "PAGE_DOWN");
        addNameOverride(-100, "MOUSE_LEFT");
        addNameOverride( -99, "MOUSE_RIGHT");
        addNameOverride( -98, "MOUSE_MIDDLE");
        addNameOverride(-199, "SCROLL_UP");
        addNameOverride(-201, "SCROLL_DOWN");

        addLoadableNames(Keys.KEY_LEFT_ALT, "LMENU", "L_MENU", "LALT", "LEFT_ALT");
        addLoadableNames(Keys.KEY_RIGHT_ALT, "RMENU", "R_MENU", "RALT", "RIGHT_ALT");
        addLoadableNames(Keys.KEY_LEFT_SHIFT, "LSHIFT", "LEFT_SHIFT");
        addLoadableNames(Keys.KEY_RIGHT_SHIFT, "RSHIFT", "RIGHT_SHIFT");
        addLoadableNames(Keys.KEY_LEFT_CONTROL, "LCTRL", "LEFT_CTRL", "LCONTROL", "L_CONTROL", "LEFT_CONTROL");
        addLoadableNames(Keys.KEY_RIGHT_CONTROL, "RCTRL", "RIGHT_CTRL", "RCONTROL", "R_CONTROL", "RIGHT_CONTROL");
        addLoadableNames(-100, "BUTTON0", "MOUSE0", "LMB", "LEFT_MOUSE");
        addLoadableNames( -99, "BUTTON1", "MOUSE1", "RMB", "RIGHT_MOUSE");
        addLoadableNames( -98, "BUTTON2", "MOUSE2", "MMB", "MIDDLE_MOUSE");
    }

    /**
     * Adds names for the given keyCode, which can be loaded from the config file.
     * This is basically the name -> id mapping used when loading the config files.
     * @param keyCode the key code that the provided names will be loaded as
     * @param names one or more names that should be loaded as the given keyCode value
     */
    public static void addLoadableNames(int keyCode, String... names)
    {
        for (String name : names)
        {
            if (NAMES_TO_KEYCODES.containsKey(name))
            {
                MaLiLib.LOGGER.warn("Duplicate key fallback name '{}' => {}", name, keyCode);
                continue;
            }

            NAMES_TO_KEYCODES.put(name, keyCode);
        }
    }

    /**
     * Adds an id -> name override, which determines what the config file will contain for a given keyCode.
     * Also adds the reverse mapping, name -> id for being able to load the key by the given name.
     * @param name the name to use for this keyCode in the config files
     */
    public static void addNameOverride(int keyCode, String name)
    {
        if (KEYCODES_TO_NAMES.containsKey(keyCode))
        {
            MaLiLib.LOGGER.warn("Duplicate key override name {} => '{}'", keyCode, name);
        }

        KEYCODES_TO_NAMES.put(keyCode, name);
        NAMES_TO_KEYCODES.put(name, keyCode);
    }
}
