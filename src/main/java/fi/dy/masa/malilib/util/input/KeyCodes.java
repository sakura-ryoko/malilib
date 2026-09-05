package fi.dy.masa.malilib.util.input;

import java.lang.reflect.Field;
import javax.annotation.Nullable;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.lwjgl.sdl.SDLKeycode;
import org.lwjgl.sdl.SDLMouse;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibConfigs;

/**
 * @implSpec You should use {@link ScanCodes}.
 * Vanilla from 26.3+ flipped using scanCodes vs keyCodes.
 * This data is still valid; but more or less it is for reference purposes.
 */
public class KeyCodes
{
    private static final Int2ObjectOpenHashMap<String> MAP_KEY_TO_NAME = new Int2ObjectOpenHashMap<>();
    private static final Object2IntOpenHashMap<String> MAP_NAME_TO_KEY = new Object2IntOpenHashMap<>();

    public static final int KEY_UNKNOWN         = SDLKeycode.SDLK_UNKNOWN;

    // Control Keys
    public static final int KEY_ENTER           = SDLKeycode.SDLK_RETURN;
    public static final int KEY_ESCAPE          = SDLKeycode.SDLK_ESCAPE;
    public static final int KEY_BACKSPACE       = SDLKeycode.SDLK_BACKSPACE;
    public static final int KEY_TAB             = SDLKeycode.SDLK_TAB;
    public static final int KEY_SPACE           = SDLKeycode.SDLK_SPACE;
    public static final int KEY_EXCLAIM         = SDLKeycode.SDLK_EXCLAIM;
    public static final int KEY_DBL_APOSTROPHE  = SDLKeycode.SDLK_DBLAPOSTROPHE;
    public static final int KEY_HASH            = SDLKeycode.SDLK_HASH;
    public static final int KEY_DOLLAR          = SDLKeycode.SDLK_DOLLAR;
    public static final int KEY_PERCENT         = SDLKeycode.SDLK_PERCENT;
    public static final int KEY_AMPERSAND       = SDLKeycode.SDLK_AMPERSAND;
    public static final int KEY_APOSTROPHE      = SDLKeycode.SDLK_APOSTROPHE;
    public static final int KEY_LEFT_PARENTHESIS= SDLKeycode.SDLK_LEFTPAREN;
    public static final int KEY_RIGHT_PARENTHESIS= SDLKeycode.SDLK_RIGHTPAREN;
    public static final int KEY_ASTERISK        = SDLKeycode.SDLK_ASTERISK;
    public static final int KEY_PLUS            = SDLKeycode.SDLK_PLUS;
    public static final int KEY_COMMA           = SDLKeycode.SDLK_COMMA;
    public static final int KEY_MINUS           = SDLKeycode.SDLK_MINUS;
    public static final int KEY_PERIOD          = SDLKeycode.SDLK_PERIOD;
    public static final int KEY_SLASH           = SDLKeycode.SDLK_SLASH;

    public static final int KEY_0               = SDLKeycode.SDLK_0;
    public static final int KEY_1               = SDLKeycode.SDLK_1;
    public static final int KEY_2               = SDLKeycode.SDLK_2;
    public static final int KEY_3               = SDLKeycode.SDLK_3;
    public static final int KEY_4               = SDLKeycode.SDLK_4;
    public static final int KEY_5               = SDLKeycode.SDLK_5;
    public static final int KEY_6               = SDLKeycode.SDLK_6;
    public static final int KEY_7               = SDLKeycode.SDLK_7;
    public static final int KEY_8               = SDLKeycode.SDLK_8;
    public static final int KEY_9               = SDLKeycode.SDLK_9;
    public static final int KEY_COLON           = SDLKeycode.SDLK_COLON;
    public static final int KEY_SEMICOLON       = SDLKeycode.SDLK_SEMICOLON;
    public static final int KEY_LESS            = SDLKeycode.SDLK_LESS;
    public static final int KEY_EQUAL           = SDLKeycode.SDLK_EQUALS;
    public static final int KEY_GREATER         = SDLKeycode.SDLK_GREATER;
    public static final int KEY_QUESTION        = SDLKeycode.SDLK_QUESTION;
    public static final int KEY_AT              = SDLKeycode.SDLK_AT;
    public static final int KEY_LEFT_BRACKET    = SDLKeycode.SDLK_LEFTBRACKET;
    public static final int KEY_BACKSLASH       = SDLKeycode.SDLK_BACKSLASH;
    public static final int KEY_RIGHT_BRACKET   = SDLKeycode.SDLK_RIGHTBRACKET;
    public static final int KEY_CARET           = SDLKeycode.SDLK_CARET;
    public static final int KEY_UNDERSCORE      = SDLKeycode.SDLK_UNDERSCORE;
    public static final int KEY_GRAVE_ACCENT    = SDLKeycode.SDLK_GRAVE;

    // Printable
    public static final int KEY_A               = SDLKeycode.SDLK_A;
    public static final int KEY_B               = SDLKeycode.SDLK_B;
    public static final int KEY_C               = SDLKeycode.SDLK_C;
    public static final int KEY_D               = SDLKeycode.SDLK_D;
    public static final int KEY_E               = SDLKeycode.SDLK_E;
    public static final int KEY_F               = SDLKeycode.SDLK_F;
    public static final int KEY_G               = SDLKeycode.SDLK_G;
    public static final int KEY_H               = SDLKeycode.SDLK_H;
    public static final int KEY_I               = SDLKeycode.SDLK_I;
    public static final int KEY_J               = SDLKeycode.SDLK_J;
    public static final int KEY_K               = SDLKeycode.SDLK_K;
    public static final int KEY_L               = SDLKeycode.SDLK_L;
    public static final int KEY_M               = SDLKeycode.SDLK_M;
    public static final int KEY_N               = SDLKeycode.SDLK_N;
    public static final int KEY_O               = SDLKeycode.SDLK_O;
    public static final int KEY_P               = SDLKeycode.SDLK_P;
    public static final int KEY_Q               = SDLKeycode.SDLK_Q;
    public static final int KEY_R               = SDLKeycode.SDLK_R;
    public static final int KEY_S               = SDLKeycode.SDLK_S;
    public static final int KEY_T               = SDLKeycode.SDLK_T;
    public static final int KEY_U               = SDLKeycode.SDLK_U;
    public static final int KEY_V               = SDLKeycode.SDLK_V;
    public static final int KEY_W               = SDLKeycode.SDLK_W;
    public static final int KEY_X               = SDLKeycode.SDLK_X;
    public static final int KEY_Y               = SDLKeycode.SDLK_Y;
    public static final int KEY_Z               = SDLKeycode.SDLK_Z;

    public static final int KEY_LEFT_BRACE      = SDLKeycode.SDLK_LEFTBRACE;
    public static final int KEY_PIPE            = SDLKeycode.SDLK_PIPE;
    public static final int KEY_RIGHT_BRACE     = SDLKeycode.SDLK_RIGHTBRACE;
    public static final int KEY_TILDE           = SDLKeycode.SDLK_TILDE;
    public static final int KEY_DELETE          = SDLKeycode.SDLK_DELETE;
    public static final int KEY_PLUS_MINUS      = SDLKeycode.SDLK_PLUSMINUS;
    public static final int KEY_CAPS_LOCK       = SDLKeycode.SDLK_CAPSLOCK;

    // Function Keys
    public static final int KEY_F1              = SDLKeycode.SDLK_F1;
    public static final int KEY_F2              = SDLKeycode.SDLK_F2;
    public static final int KEY_F3              = SDLKeycode.SDLK_F3;
    public static final int KEY_F4              = SDLKeycode.SDLK_F4;
    public static final int KEY_F5              = SDLKeycode.SDLK_F5;
    public static final int KEY_F6              = SDLKeycode.SDLK_F6;
    public static final int KEY_F7              = SDLKeycode.SDLK_F7;
    public static final int KEY_F8              = SDLKeycode.SDLK_F8;
    public static final int KEY_F9              = SDLKeycode.SDLK_F9;
    public static final int KEY_F10             = SDLKeycode.SDLK_F10;
    public static final int KEY_F11             = SDLKeycode.SDLK_F11;
    public static final int KEY_F12             = SDLKeycode.SDLK_F12;

    public static final int KEY_PRINT_SCREEN    = SDLKeycode.SDLK_PRINTSCREEN;
    public static final int KEY_SCROLL_LOCK     = SDLKeycode.SDLK_SCROLLLOCK;
    public static final int KEY_PAUSE           = SDLKeycode.SDLK_PAUSE;
    public static final int KEY_INSERT          = SDLKeycode.SDLK_INSERT;
    public static final int KEY_HOME            = SDLKeycode.SDLK_HOME;
    public static final int KEY_PAGE_UP         = SDLKeycode.SDLK_PAGEUP;
    public static final int KEY_END             = SDLKeycode.SDLK_END;
    public static final int KEY_PAGE_DOWN       = SDLKeycode.SDLK_PAGEDOWN;
    public static final int KEY_RIGHT           = SDLKeycode.SDLK_RIGHT;
    public static final int KEY_LEFT            = SDLKeycode.SDLK_LEFT;
    public static final int KEY_DOWN            = SDLKeycode.SDLK_DOWN;
    public static final int KEY_UP              = SDLKeycode.SDLK_UP;

    public static final int KEY_NUM_LOCK        = SDLKeycode.SDLK_NUMLOCKCLEAR;
    public static final int KEY_KP_DIVIDE       = SDLKeycode.SDLK_KP_DIVIDE;
    public static final int KEY_KP_MULTIPLY     = SDLKeycode.SDLK_KP_MULTIPLY;
    public static final int KEY_KP_SUBTRACT     = SDLKeycode.SDLK_KP_MINUS;
    public static final int KEY_KP_ADD          = SDLKeycode.SDLK_KP_PLUS;
    public static final int KEY_KP_ENTER        = SDLKeycode.SDLK_KP_ENTER;
    public static final int KEY_KP_1            = SDLKeycode.SDLK_KP_1;
    public static final int KEY_KP_2            = SDLKeycode.SDLK_KP_2;
    public static final int KEY_KP_3            = SDLKeycode.SDLK_KP_3;
    public static final int KEY_KP_4            = SDLKeycode.SDLK_KP_4;
    public static final int KEY_KP_5            = SDLKeycode.SDLK_KP_5;
    public static final int KEY_KP_6            = SDLKeycode.SDLK_KP_6;
    public static final int KEY_KP_7            = SDLKeycode.SDLK_KP_7;
    public static final int KEY_KP_8            = SDLKeycode.SDLK_KP_8;
    public static final int KEY_KP_9            = SDLKeycode.SDLK_KP_9;
    public static final int KEY_KP_0            = SDLKeycode.SDLK_KP_0;
    public static final int KEY_KP_PERIOD       = SDLKeycode.SDLK_KP_PERIOD;
    public static final int KEY_APPLICATION     = SDLKeycode.SDLK_APPLICATION;
    public static final int KEY_POWER           = SDLKeycode.SDLK_POWER;
    public static final int KEY_KP_EQUALS       = SDLKeycode.SDLK_KP_EQUALS;

    public static final int KEY_F13             = SDLKeycode.SDLK_F13;
    public static final int KEY_F14             = SDLKeycode.SDLK_F14;
    public static final int KEY_F15             = SDLKeycode.SDLK_F15;
    public static final int KEY_F16             = SDLKeycode.SDLK_F16;
    public static final int KEY_F17             = SDLKeycode.SDLK_F17;
    public static final int KEY_F18             = SDLKeycode.SDLK_F18;
    public static final int KEY_F19             = SDLKeycode.SDLK_F19;
    public static final int KEY_F20             = SDLKeycode.SDLK_F20;
    public static final int KEY_F21             = SDLKeycode.SDLK_F21;
    public static final int KEY_F22             = SDLKeycode.SDLK_F22;
    public static final int KEY_F23             = SDLKeycode.SDLK_F23;
    public static final int KEY_F24             = SDLKeycode.SDLK_F24;

    public static final int KEY_EXECUTE         = SDLKeycode.SDLK_EXECUTE;
    public static final int KEY_HELP            = SDLKeycode.SDLK_HELP;
    public static final int KEY_MENU            = SDLKeycode.SDLK_MENU;
    public static final int KEY_SELECT          = SDLKeycode.SDLK_SELECT;
    public static final int KEY_STOP            = SDLKeycode.SDLK_STOP;
    public static final int KEY_AGAIN           = SDLKeycode.SDLK_AGAIN;
    public static final int KEY_UNDO            = SDLKeycode.SDLK_UNDO;
    public static final int KEY_CUT             = SDLKeycode.SDLK_CUT;
    public static final int KEY_COPY            = SDLKeycode.SDLK_COPY;
    public static final int KEY_PASTE           = SDLKeycode.SDLK_PASTE;
    public static final int KEY_FIND            = SDLKeycode.SDLK_FIND;
    public static final int KEY_MUTE            = SDLKeycode.SDLK_MUTE;
    public static final int KEY_VOLUME_UP       = SDLKeycode.SDLK_VOLUMEUP;
    public static final int KEY_VOLUME_DOWN     = SDLKeycode.SDLK_VOLUMEDOWN;

    public static final int KEY_KP_COMMA        = SDLKeycode.SDLK_KP_COMMA;
    public static final int KEY_KP_EQUALS_AS400 = SDLKeycode.SDLK_KP_EQUALSAS400;

    public static final int KEY_ALTER_ASE       = SDLKeycode.SDLK_ALTERASE;
    public static final int KEY_SYS_REQ         = SDLKeycode.SDLK_SYSREQ;
    public static final int KEY_CANCEL          = SDLKeycode.SDLK_CANCEL;
    public static final int KEY_CLEAR           = SDLKeycode.SDLK_CLEAR;
    public static final int KEY_PRIOR           = SDLKeycode.SDLK_PRIOR;
    public static final int KEY_RETURN2         = SDLKeycode.SDLK_RETURN2;
    public static final int KEY_SEPARATOR       = SDLKeycode.SDLK_SEPARATOR;
    public static final int KEY_OUT             = SDLKeycode.SDLK_OUT;
    public static final int KEY_OPER            = SDLKeycode.SDLK_OPER;
    public static final int KEY_CLEAR_AGAIN     = SDLKeycode.SDLK_CLEARAGAIN;
    public static final int KEY_CR_SEL          = SDLKeycode.SDLK_CRSEL;
    public static final int KEY_EX_SEL          = SDLKeycode.SDLK_EXSEL;

    public static final int KEY_KP_00           = SDLKeycode.SDLK_KP_00;
    public static final int KEY_KP_000          = SDLKeycode.SDLK_KP_000;

    public static final int KEY_THOUSANDS_SEPARATOR= SDLKeycode.SDLK_THOUSANDSSEPARATOR;
    public static final int KEY_DECIMAL_SEPARATOR  = SDLKeycode.SDLK_DECIMALSEPARATOR;
    public static final int KEY_CURRENCY_UNIT      = SDLKeycode.SDLK_CURRENCYUNIT;
    public static final int KEY_CURRENCY_SUB_UNIT  = SDLKeycode.SDLK_CURRENCYSUBUNIT;

    public static final int KEY_KP_LEFT_PAREN   = SDLKeycode.SDLK_KP_LEFTPAREN;
    public static final int KEY_KP_RIGHT_PAREN  = SDLKeycode.SDLK_KP_RIGHTPAREN;
    public static final int KEY_KP_LEFT_BRACE   = SDLKeycode.SDLK_KP_LEFTBRACE;
    public static final int KEY_KP_RIGHT_BRACE  = SDLKeycode.SDLK_KP_RIGHTBRACE;
    public static final int KEY_KP_TAB          = SDLKeycode.SDLK_KP_TAB;
    public static final int KEY_KP_BACKSPACE    = SDLKeycode.SDLK_KP_BACKSPACE;
    public static final int KEY_KP_A            = SDLKeycode.SDLK_KP_A;
    public static final int KEY_KP_B            = SDLKeycode.SDLK_KP_B;
    public static final int KEY_KP_C            = SDLKeycode.SDLK_KP_C;
    public static final int KEY_KP_D            = SDLKeycode.SDLK_KP_D;
    public static final int KEY_KP_E            = SDLKeycode.SDLK_KP_E;
    public static final int KEY_KP_F            = SDLKeycode.SDLK_KP_F;
    public static final int KEY_KP_XOR          = SDLKeycode.SDLK_KP_XOR;
    public static final int KEY_KP_POWER        = SDLKeycode.SDLK_KP_POWER;
    public static final int KEY_KP_PERCENT      = SDLKeycode.SDLK_KP_PERCENT;
    public static final int KEY_KP_LESS         = SDLKeycode.SDLK_KP_LESS;
    public static final int KEY_KP_GREATER      = SDLKeycode.SDLK_KP_GREATER;
    public static final int KEY_KP_AMPERSAND    = SDLKeycode.SDLK_KP_AMPERSAND;
    public static final int KEY_KP_DBL_AMPERSAND= SDLKeycode.SDLK_KP_DBLAMPERSAND;
    public static final int KEY_KP_VERTICAL_BAR = SDLKeycode.SDLK_KP_VERTICALBAR;
    public static final int KEY_KP_DBL_VERTICAL_BAR= SDLKeycode.SDLK_KP_DBLVERTICALBAR;
    public static final int KEY_KP_COLON        = SDLKeycode.SDLK_KP_COLON;
    public static final int KEY_KP_HASH         = SDLKeycode.SDLK_KP_HASH;
    public static final int KEY_KP_SPACE        = SDLKeycode.SDLK_KP_SPACE;

    public static final int KEY_KP_AT           = SDLKeycode.SDLK_KP_AT;
    public static final int KEY_KP_EXCLAIM      = SDLKeycode.SDLK_KP_EXCLAM;
    public static final int KEY_KP_MEM_STORE    = SDLKeycode.SDLK_KP_MEMSTORE;
    public static final int KEY_KP_MEM_RECALL   = SDLKeycode.SDLK_KP_MEMRECALL;
    public static final int KEY_KP_MEM_CLEAR    = SDLKeycode.SDLK_KP_MEMCLEAR;
    public static final int KEY_KP_MEM_ADD      = SDLKeycode.SDLK_KP_MEMADD;
    public static final int KEY_KP_MEM_SUBTRACT = SDLKeycode.SDLK_KP_MEMSUBTRACT;
    public static final int KEY_KP_MEM_MULTIPLY = SDLKeycode.SDLK_KP_MEMMULTIPLY;
    public static final int KEY_KP_MEM_DIVIDE   = SDLKeycode.SDLK_KP_MEMDIVIDE;
    public static final int KEY_KP_PLUS_MINUS   = SDLKeycode.SDLK_KP_PLUSMINUS;
    public static final int KEY_KP_CLEAR        = SDLKeycode.SDLK_KP_CLEAR;
    public static final int KEY_KP_CLEAR_ENTRY  = SDLKeycode.SDLK_KP_CLEARENTRY;
    public static final int KEY_KP_BINARY       = SDLKeycode.SDLK_KP_BINARY;
    public static final int KEY_KP_OCTAL        = SDLKeycode.SDLK_KP_OCTAL;
    public static final int KEY_KP_DECIMAL      = SDLKeycode.SDLK_KP_DECIMAL;
    public static final int KEY_KP_HEXADECIMAL  = SDLKeycode.SDLK_KP_HEXADECIMAL;

    public static final int KEY_LEFT_CONTROL    = SDLKeycode.SDLK_LCTRL;
    public static final int KEY_LEFT_SHIFT      = SDLKeycode.SDLK_LSHIFT;
    public static final int KEY_LEFT_ALT        = SDLKeycode.SDLK_LALT;
    public static final int KEY_LEFT_SUPER      = SDLKeycode.SDLK_LGUI;
    public static final int KEY_RIGHT_CONTROL   = SDLKeycode.SDLK_RCTRL;
    public static final int KEY_RIGHT_SHIFT     = SDLKeycode.SDLK_RSHIFT;
    public static final int KEY_RIGHT_ALT       = SDLKeycode.SDLK_RALT;
    public static final int KEY_RIGHT_SUPER     = SDLKeycode.SDLK_RGUI;

    public static final int KEY_MODE                = SDLKeycode.SDLK_MODE;
    public static final int KEY_SLEEP               = SDLKeycode.SDLK_SLEEP;
    public static final int KEY_WAKE                = SDLKeycode.SDLK_WAKE;
    public static final int KEY_CHANNEL_INCREMENT   = SDLKeycode.SDLK_CHANNEL_INCREMENT;
    public static final int KEY_CHANNEL_DECREMENT   = SDLKeycode.SDLK_CHANNEL_DECREMENT;

    public static final int KEY_MEDIA_PLAY          = SDLKeycode.SDLK_MEDIA_PLAY;
    public static final int KEY_MEDIA_PAUSE         = SDLKeycode.SDLK_MEDIA_PAUSE;
    public static final int KEY_MEDIA_RECORD        = SDLKeycode.SDLK_MEDIA_RECORD;
    public static final int KEY_MEDIA_FAST_FORWARD  = SDLKeycode.SDLK_MEDIA_FAST_FORWARD;
    public static final int KEY_MEDIA_REWIND        = SDLKeycode.SDLK_MEDIA_REWIND;
    public static final int KEY_MEDIA_NEXT_TRACK    = SDLKeycode.SDLK_MEDIA_NEXT_TRACK;
    public static final int KEY_MEDIA_PREVIOUS_TRACK= SDLKeycode.SDLK_MEDIA_PREVIOUS_TRACK;
    public static final int KEY_MEDIA_STOP          = SDLKeycode.SDLK_MEDIA_STOP;
    public static final int KEY_MEDIA_EJECT         = SDLKeycode.SDLK_MEDIA_EJECT;
    public static final int KEY_MEDIA_PLAY_PAUSE    = SDLKeycode.SDLK_MEDIA_PLAY_PAUSE;
    public static final int KEY_MEDIA_SELECT        = SDLKeycode.SDLK_MEDIA_SELECT;

    public static final int KEY_AC_NEW          = SDLKeycode.SDLK_AC_NEW;
    public static final int KEY_AC_OPEN         = SDLKeycode.SDLK_AC_OPEN;
    public static final int KEY_AC_CLOSE        = SDLKeycode.SDLK_AC_CLOSE;
    public static final int KEY_AC_EXIT         = SDLKeycode.SDLK_AC_EXIT;
    public static final int KEY_AC_SAVE         = SDLKeycode.SDLK_AC_SAVE;
    public static final int KEY_AC_PRINT        = SDLKeycode.SDLK_AC_PRINT;
    public static final int KEY_AC_PROPERTIES   = SDLKeycode.SDLK_AC_PROPERTIES;
    public static final int KEY_AC_SEARCH       = SDLKeycode.SDLK_AC_SEARCH;
    public static final int KEY_AC_HOME         = SDLKeycode.SDLK_AC_HOME;
    public static final int KEY_AC_BACK         = SDLKeycode.SDLK_AC_BACK;
    public static final int KEY_AC_FORWARD      = SDLKeycode.SDLK_AC_FORWARD;
    public static final int KEY_AC_STOP         = SDLKeycode.SDLK_AC_STOP;
    public static final int KEY_AC_REFRESH      = SDLKeycode.SDLK_AC_REFRESH;
    public static final int KEY_AC_BOOKMARKS    = SDLKeycode.SDLK_AC_BOOKMARKS;

    public static final int KEY_SOFT_LEFT       = SDLKeycode.SDLK_SOFTLEFT;
    public static final int KEY_SOFT_RIGHT      = SDLKeycode.SDLK_SOFTRIGHT;
    public static final int KEY_CALL            = SDLKeycode.SDLK_CALL;
    public static final int KEY_END_CALL        = SDLKeycode.SDLK_ENDCALL;

    public static final int KEY_LEFT_TAB        = SDLKeycode.SDLK_LEFT_TAB;
    public static final int KEY_LEVEL5_SHIFT    = SDLKeycode.SDLK_LEVEL5_SHIFT;
    public static final int KEY_MULTI_KEY_COMPOSE= SDLKeycode.SDLK_MULTI_KEY_COMPOSE;
    public static final int KEY_LEFT_META       = SDLKeycode.SDLK_LMETA;
    public static final int KEY_RIGHT_META      = SDLKeycode.SDLK_RMETA;
    public static final int KEY_LEFT_HYPER      = SDLKeycode.SDLK_LHYPER;
    public static final int KEY_RIGHT_HYPER     = SDLKeycode.SDLK_RHYPER;

    // Mods
    public static final int KMOD_NONE           = SDLKeycode.SDL_KMOD_NONE;
    public static final int KMOD_LEFT_SHIFT     = SDLKeycode.SDL_KMOD_LSHIFT;
    public static final int KMOD_RIGHT_SHIFT    = SDLKeycode.SDL_KMOD_RSHIFT;
    public static final int KMOD_LEVEL5         = SDLKeycode.SDL_KMOD_LEVEL5;
    public static final int KMOD_LEFT_CONTROL   = SDLKeycode.SDL_KMOD_LCTRL;
    public static final int KMOD_RIGHT_CONTROL  = SDLKeycode.SDL_KMOD_RCTRL;
    public static final int KMOD_LEFT_ALT       = SDLKeycode.SDL_KMOD_LALT;
    public static final int KMOD_RIGHT_ALT      = SDLKeycode.SDL_KMOD_RALT;
    public static final int KMOD_LEFT_SUPER     = SDLKeycode.SDL_KMOD_LGUI;
    public static final int KMOD_RIGHT_SUPER    = SDLKeycode.SDL_KMOD_RGUI;
    public static final int KMOD_NUM_LOCK       = SDLKeycode.SDL_KMOD_NUM;
    public static final int KMOD_CAPS_LOCK      = SDLKeycode.SDL_KMOD_CAPS;
    public static final int KMOD_MODE           = SDLKeycode.SDL_KMOD_MODE;
    public static final int KMOD_SCROLL         = SDLKeycode.SDL_KMOD_SCROLL;
    public static final int KMOD_CONTROL        = SDLKeycode.SDL_KMOD_CTRL;
    public static final int KMOD_SHIFT          = SDLKeycode.SDL_KMOD_SHIFT;
    public static final int KMOD_ALT            = SDLKeycode.SDL_KMOD_ALT;
    public static final int KMOD_SUPER          = SDLKeycode.SDL_KMOD_GUI;

    // Mouse
    public static final int MOUSE_BUTTON_1      = SDLMouse.SDL_BUTTON_LEFT      - 100;
    public static final int MOUSE_BUTTON_2      = SDLMouse.SDL_BUTTON_MIDDLE    - 100;
    public static final int MOUSE_BUTTON_3      = SDLMouse.SDL_BUTTON_RIGHT     - 100;
    public static final int MOUSE_BUTTON_4      = SDLMouse.SDL_BUTTON_X1        - 100;
    public static final int MOUSE_BUTTON_5      = SDLMouse.SDL_BUTTON_X2        - 100;
    public static final int MOUSE_BUTTON_6      = SDLMouse.SDL_BUTTON_X2 + 1    - 100;
    public static final int MOUSE_BUTTON_7      = SDLMouse.SDL_BUTTON_X2 + 2    - 100;
    public static final int MOUSE_BUTTON_8      = SDLMouse.SDL_BUTTON_X2 + 3    - 100;

    @Nullable
    public static String getNameForKey(int keyCode)
    {
        // Transform to AZERTY if a Transform is found
        KeyboardType type = (KeyboardType) MaLiLibConfigs.Generic.KEYBOARD_TYPE.getOptionListValue();

        if (type == KeyboardType.AZERTY)
        {
            KeyCodesAzerty transform = KeyCodesAzerty.fromKeyCode(keyCode);

            if (transform != null)
            {
                return transform.getName();
            }
        }

        return MAP_KEY_TO_NAME.get(keyCode);
    }

    public static int getKeyCodeFromName(String name)
    {
        // Transform to AZERTY if a Transform is found;
        // Might cause a few problems if converting a config
        KeyboardType type = (KeyboardType) MaLiLibConfigs.Generic.KEYBOARD_TYPE.getOptionListValue();

        if (type == KeyboardType.AZERTY)
        {
            KeyCodesAzerty transform = KeyCodesAzerty.fromName(name);

            if (transform != null)
            {
                return transform.keyCode();
            }
        }
        // For backwards reading of configs if the mapping isn't found;
        // Might cause a few problems if converting a config
        else if (type == KeyboardType.QWERTY && !MAP_NAME_TO_KEY.containsKey(name))
        {
            KeyCodesAzerty transform = KeyCodesAzerty.fromName(name);

            if (transform != null)
            {
                return transform.keyCode();
            }
        }

        return MAP_NAME_TO_KEY.getInt(name);
    }

    @Nullable
    public static KeyCodesAzerty getTransformFromAzerty(String name)
    {
        return KeyCodesAzerty.fromName(name);
    }

    @Nullable
    public static KeyCodesAzerty getTransformFromQwerty(String name)
    {
        return KeyCodesAzerty.fromQwertyName(name);
    }

    static
    {
        MAP_NAME_TO_KEY.defaultReturnValue(KEY_UNKNOWN);

        for (Field field : KeyCodes.class.getDeclaredFields())
        {
            try
            {
                String name = field.getName();
                int keyCode = KEY_UNKNOWN;

                if (name.startsWith("KEY_"))
                {
                    name = name.substring(4);
                    keyCode = field.getInt(null);
                }
                else if (name.startsWith("MOUSE_"))
                {
                    name = name.substring(6);
                    keyCode = field.getInt(null);
                }

                if (keyCode != KEY_UNKNOWN)
                {
                    MAP_KEY_TO_NAME.put(keyCode, name);
                    MAP_NAME_TO_KEY.put(name, keyCode);
                }
            }
            catch (Exception e)
            {
                MaLiLib.LOGGER.error("Failed to initialize the key name lookup!", e);
            }
        }
    }
}
