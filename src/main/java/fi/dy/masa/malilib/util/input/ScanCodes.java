package fi.dy.masa.malilib.util.input;

import java.lang.reflect.Field;
import javax.annotation.Nullable;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.lwjgl.sdl.SDLMouse;
import org.lwjgl.sdl.SDLScancode;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibConfigs;

public class ScanCodes
{
	private static final Int2ObjectOpenHashMap<String> MAP_SCAN_CODE_TO_NAME = new Int2ObjectOpenHashMap<>();
	private static final Object2IntOpenHashMap<String> MAP_NAME_TO_SCAN_CODE = new Object2IntOpenHashMap<>();

	public static final int SCAN_UNKNOWN         = SDLScancode.SDL_SCANCODE_UNKNOWN;

	// Printable
	public static final int SCAN_A               = SDLScancode.SDL_SCANCODE_A;
	public static final int SCAN_B               = SDLScancode.SDL_SCANCODE_B;
	public static final int SCAN_C               = SDLScancode.SDL_SCANCODE_C;
	public static final int SCAN_D               = SDLScancode.SDL_SCANCODE_D;
	public static final int SCAN_E               = SDLScancode.SDL_SCANCODE_E;
	public static final int SCAN_F               = SDLScancode.SDL_SCANCODE_F;
	public static final int SCAN_G               = SDLScancode.SDL_SCANCODE_G;
	public static final int SCAN_H               = SDLScancode.SDL_SCANCODE_H;
	public static final int SCAN_I               = SDLScancode.SDL_SCANCODE_I;
	public static final int SCAN_J               = SDLScancode.SDL_SCANCODE_J;
	public static final int SCAN_K               = SDLScancode.SDL_SCANCODE_K;
	public static final int SCAN_L               = SDLScancode.SDL_SCANCODE_L;
	public static final int SCAN_M               = SDLScancode.SDL_SCANCODE_M;
	public static final int SCAN_N               = SDLScancode.SDL_SCANCODE_N;
	public static final int SCAN_O               = SDLScancode.SDL_SCANCODE_O;
	public static final int SCAN_P               = SDLScancode.SDL_SCANCODE_P;
	public static final int SCAN_Q               = SDLScancode.SDL_SCANCODE_Q;
	public static final int SCAN_R               = SDLScancode.SDL_SCANCODE_R;
	public static final int SCAN_S               = SDLScancode.SDL_SCANCODE_S;
	public static final int SCAN_T               = SDLScancode.SDL_SCANCODE_T;
	public static final int SCAN_U               = SDLScancode.SDL_SCANCODE_U;
	public static final int SCAN_V               = SDLScancode.SDL_SCANCODE_V;
	public static final int SCAN_W               = SDLScancode.SDL_SCANCODE_W;
	public static final int SCAN_X               = SDLScancode.SDL_SCANCODE_X;
	public static final int SCAN_Y               = SDLScancode.SDL_SCANCODE_Y;
	public static final int SCAN_Z               = SDLScancode.SDL_SCANCODE_Z;
	public static final int SCAN_1               = SDLScancode.SDL_SCANCODE_1;
	public static final int SCAN_2               = SDLScancode.SDL_SCANCODE_2;
	public static final int SCAN_3               = SDLScancode.SDL_SCANCODE_3;
	public static final int SCAN_4               = SDLScancode.SDL_SCANCODE_4;
	public static final int SCAN_5               = SDLScancode.SDL_SCANCODE_5;
	public static final int SCAN_6               = SDLScancode.SDL_SCANCODE_6;
	public static final int SCAN_7               = SDLScancode.SDL_SCANCODE_7;
	public static final int SCAN_8               = SDLScancode.SDL_SCANCODE_8;
	public static final int SCAN_9               = SDLScancode.SDL_SCANCODE_9;
	public static final int SCAN_0               = SDLScancode.SDL_SCANCODE_0;

	// Control Keys
	public static final int SCAN_ENTER           = SDLScancode.SDL_SCANCODE_RETURN;
	public static final int SCAN_ESCAPE          = SDLScancode.SDL_SCANCODE_ESCAPE;
	public static final int SCAN_BACKSPACE       = SDLScancode.SDL_SCANCODE_BACKSPACE;
	public static final int SCAN_TAB             = SDLScancode.SDL_SCANCODE_TAB;
	public static final int SCAN_SPACE           = SDLScancode.SDL_SCANCODE_SPACE;
	public static final int SCAN_MINUS           = SDLScancode.SDL_SCANCODE_MINUS;
	public static final int SCAN_EQUAL           = SDLScancode.SDL_SCANCODE_EQUALS;
	public static final int SCAN_LEFT_BRACKET    = SDLScancode.SDL_SCANCODE_LEFTBRACKET;
	public static final int SCAN_RIGHT_BRACKET   = SDLScancode.SDL_SCANCODE_RIGHTBRACKET;
	public static final int SCAN_BACKSLASH       = SDLScancode.SDL_SCANCODE_BACKSLASH;
	public static final int SCAN_NON_US_HASH     = SDLScancode.SDL_SCANCODE_NONUSHASH;
	public static final int SCAN_SEMICOLON       = SDLScancode.SDL_SCANCODE_SEMICOLON;
	public static final int SCAN_APOSTROPHE      = SDLScancode.SDL_SCANCODE_APOSTROPHE;
	public static final int SCAN_GRAVE_ACCENT    = SDLScancode.SDL_SCANCODE_GRAVE;
	public static final int SCAN_COMMA           = SDLScancode.SDL_SCANCODE_COMMA;
	public static final int SCAN_PERIOD          = SDLScancode.SDL_SCANCODE_PERIOD;
	public static final int SCAN_SLASH           = SDLScancode.SDL_SCANCODE_SLASH;
	public static final int SCAN_CAPS_LOCK       = SDLScancode.SDL_SCANCODE_CAPSLOCK;

	// Function Keys
	public static final int SCAN_F1              = SDLScancode.SDL_SCANCODE_F1;
	public static final int SCAN_F2              = SDLScancode.SDL_SCANCODE_F2;
	public static final int SCAN_F3              = SDLScancode.SDL_SCANCODE_F3;
	public static final int SCAN_F4              = SDLScancode.SDL_SCANCODE_F4;
	public static final int SCAN_F5              = SDLScancode.SDL_SCANCODE_F5;
	public static final int SCAN_F6              = SDLScancode.SDL_SCANCODE_F6;
	public static final int SCAN_F7              = SDLScancode.SDL_SCANCODE_F7;
	public static final int SCAN_F8              = SDLScancode.SDL_SCANCODE_F8;
	public static final int SCAN_F9              = SDLScancode.SDL_SCANCODE_F9;
	public static final int SCAN_F10             = SDLScancode.SDL_SCANCODE_F10;
	public static final int SCAN_F11             = SDLScancode.SDL_SCANCODE_F11;
	public static final int SCAN_F12             = SDLScancode.SDL_SCANCODE_F12;

	public static final int SCAN_PRINT_SCREEN    = SDLScancode.SDL_SCANCODE_PRINTSCREEN;
	public static final int SCAN_SCROLL_LOCK     = SDLScancode.SDL_SCANCODE_SCROLLLOCK;
	public static final int SCAN_PAUSE           = SDLScancode.SDL_SCANCODE_PAUSE;
	public static final int SCAN_INSERT          = SDLScancode.SDL_SCANCODE_INSERT;
	public static final int SCAN_HOME            = SDLScancode.SDL_SCANCODE_HOME;
	public static final int SCAN_PAGE_UP         = SDLScancode.SDL_SCANCODE_PAGEUP;
	public static final int SCAN_DELETE          = SDLScancode.SDL_SCANCODE_DELETE;
	public static final int SCAN_END             = SDLScancode.SDL_SCANCODE_END;
	public static final int SCAN_PAGE_DOWN       = SDLScancode.SDL_SCANCODE_PAGEDOWN;
	public static final int SCAN_RIGHT           = SDLScancode.SDL_SCANCODE_RIGHT;
	public static final int SCAN_LEFT            = SDLScancode.SDL_SCANCODE_LEFT;
	public static final int SCAN_DOWN            = SDLScancode.SDL_SCANCODE_DOWN;
	public static final int SCAN_UP              = SDLScancode.SDL_SCANCODE_UP;

	public static final int SCAN_NUM_LOCK        = SDLScancode.SDL_SCANCODE_NUMLOCKCLEAR;
	public static final int SCAN_KP_DIVIDE       = SDLScancode.SDL_SCANCODE_KP_DIVIDE;
	public static final int SCAN_KP_MULTIPLY     = SDLScancode.SDL_SCANCODE_KP_MULTIPLY;
	public static final int SCAN_KP_SUBTRACT     = SDLScancode.SDL_SCANCODE_KP_MINUS;
	public static final int SCAN_KP_ADD          = SDLScancode.SDL_SCANCODE_KP_PLUS;
	public static final int SCAN_KP_ENTER        = SDLScancode.SDL_SCANCODE_KP_ENTER;
	public static final int SCAN_KP_1            = SDLScancode.SDL_SCANCODE_KP_1;
	public static final int SCAN_KP_2            = SDLScancode.SDL_SCANCODE_KP_2;
	public static final int SCAN_KP_3            = SDLScancode.SDL_SCANCODE_KP_3;
	public static final int SCAN_KP_4            = SDLScancode.SDL_SCANCODE_KP_4;
	public static final int SCAN_KP_5            = SDLScancode.SDL_SCANCODE_KP_5;
	public static final int SCAN_KP_6            = SDLScancode.SDL_SCANCODE_KP_6;
	public static final int SCAN_KP_7            = SDLScancode.SDL_SCANCODE_KP_7;
	public static final int SCAN_KP_8            = SDLScancode.SDL_SCANCODE_KP_8;
	public static final int SCAN_KP_9            = SDLScancode.SDL_SCANCODE_KP_9;
	public static final int SCAN_KP_0            = SDLScancode.SDL_SCANCODE_KP_0;
	public static final int SCAN_KP_PERIOD       = SDLScancode.SDL_SCANCODE_KP_PERIOD;
	public static final int SCAN_NON_US_BACKSLASH= SDLScancode.SDL_SCANCODE_NONUSBACKSLASH;
	public static final int SCAN_APPLICATION     = SDLScancode.SDL_SCANCODE_APPLICATION;
	public static final int SCAN_POWER           = SDLScancode.SDL_SCANCODE_POWER;
	public static final int SCAN_KP_EQUAL        = SDLScancode.SDL_SCANCODE_KP_EQUALS;

	public static final int SCAN_F13             = SDLScancode.SDL_SCANCODE_F13;
	public static final int SCAN_F14             = SDLScancode.SDL_SCANCODE_F14;
	public static final int SCAN_F15             = SDLScancode.SDL_SCANCODE_F15;
	public static final int SCAN_F16             = SDLScancode.SDL_SCANCODE_F16;
	public static final int SCAN_F17             = SDLScancode.SDL_SCANCODE_F17;
	public static final int SCAN_F18             = SDLScancode.SDL_SCANCODE_F18;
	public static final int SCAN_F19             = SDLScancode.SDL_SCANCODE_F19;
	public static final int SCAN_F20             = SDLScancode.SDL_SCANCODE_F20;
	public static final int SCAN_F21             = SDLScancode.SDL_SCANCODE_F21;
	public static final int SCAN_F22             = SDLScancode.SDL_SCANCODE_F22;
	public static final int SCAN_F23             = SDLScancode.SDL_SCANCODE_F23;
	public static final int SCAN_F24             = SDLScancode.SDL_SCANCODE_F24;

	public static final int SCAN_EXECUTE         = SDLScancode.SDL_SCANCODE_EXECUTE;
	public static final int SCAN_HELP            = SDLScancode.SDL_SCANCODE_HELP;
	public static final int SCAN_MENU            = SDLScancode.SDL_SCANCODE_MENU;
	public static final int SCAN_SELECT          = SDLScancode.SDL_SCANCODE_SELECT;
	public static final int SCAN_STOP            = SDLScancode.SDL_SCANCODE_STOP;
	public static final int SCAN_AGAIN           = SDLScancode.SDL_SCANCODE_AGAIN;
	public static final int SCAN_UNDO            = SDLScancode.SDL_SCANCODE_UNDO;
	public static final int SCAN_CUT             = SDLScancode.SDL_SCANCODE_CUT;
	public static final int SCAN_COPY            = SDLScancode.SDL_SCANCODE_COPY;
	public static final int SCAN_PASTE           = SDLScancode.SDL_SCANCODE_PASTE;
	public static final int SCAN_FIND            = SDLScancode.SDL_SCANCODE_FIND;
	public static final int SCAN_MUTE            = SDLScancode.SDL_SCANCODE_MUTE;
	public static final int SCAN_VOLUME_UP       = SDLScancode.SDL_SCANCODE_VOLUMEUP;
	public static final int SCAN_VOLUME_DOWN     = SDLScancode.SDL_SCANCODE_VOLUMEDOWN;

	public static final int SCAN_KP_COMMA        = SDLScancode.SDL_SCANCODE_KP_COMMA;
	public static final int SCAN_KP_EQUALS_AS400 = SDLScancode.SDL_SCANCODE_KP_EQUALSAS400;

	public static final int SCAN_INTERNATIONAL_1 = SDLScancode.SDL_SCANCODE_INTERNATIONAL1;
	public static final int SCAN_INTERNATIONAL_2 = SDLScancode.SDL_SCANCODE_INTERNATIONAL2;
	public static final int SCAN_INTERNATIONAL_3 = SDLScancode.SDL_SCANCODE_INTERNATIONAL3;
	public static final int SCAN_INTERNATIONAL_4 = SDLScancode.SDL_SCANCODE_INTERNATIONAL4;
	public static final int SCAN_INTERNATIONAL_5 = SDLScancode.SDL_SCANCODE_INTERNATIONAL5;
	public static final int SCAN_INTERNATIONAL_6 = SDLScancode.SDL_SCANCODE_INTERNATIONAL6;
	public static final int SCAN_INTERNATIONAL_7 = SDLScancode.SDL_SCANCODE_INTERNATIONAL7;
	public static final int SCAN_INTERNATIONAL_8 = SDLScancode.SDL_SCANCODE_INTERNATIONAL8;
	public static final int SCAN_INTERNATIONAL_9 = SDLScancode.SDL_SCANCODE_INTERNATIONAL9;
	public static final int SCAN_LANG_1          = SDLScancode.SDL_SCANCODE_LANG1;
	public static final int SCAN_LANG_2          = SDLScancode.SDL_SCANCODE_LANG2;
	public static final int SCAN_LANG_3          = SDLScancode.SDL_SCANCODE_LANG3;
	public static final int SCAN_LANG_4          = SDLScancode.SDL_SCANCODE_LANG4;
	public static final int SCAN_LANG_5          = SDLScancode.SDL_SCANCODE_LANG5;
	public static final int SCAN_LANG_6          = SDLScancode.SDL_SCANCODE_LANG6;
	public static final int SCAN_LANG_7          = SDLScancode.SDL_SCANCODE_LANG7;
	public static final int SCAN_LANG_8          = SDLScancode.SDL_SCANCODE_LANG8;
	public static final int SCAN_LANG_9          = SDLScancode.SDL_SCANCODE_LANG9;

	public static final int SCAN_ALTER_ASE       = SDLScancode.SDL_SCANCODE_ALTERASE;
	public static final int SCAN_SYS_REQ         = SDLScancode.SDL_SCANCODE_SYSREQ;
	public static final int SCAN_CANCEL          = SDLScancode.SDL_SCANCODE_CANCEL;
	public static final int SCAN_CLEAR           = SDLScancode.SDL_SCANCODE_CLEAR;
	public static final int SCAN_PRIOR           = SDLScancode.SDL_SCANCODE_PRIOR;
	public static final int SCAN_RETURN2         = SDLScancode.SDL_SCANCODE_RETURN2;
	public static final int SCAN_SEPARATOR       = SDLScancode.SDL_SCANCODE_SEPARATOR;
	public static final int SCAN_OUT             = SDLScancode.SDL_SCANCODE_OUT;
	public static final int SCAN_OPER            = SDLScancode.SDL_SCANCODE_OPER;
	public static final int SCAN_CLEAR_AGAIN     = SDLScancode.SDL_SCANCODE_CLEARAGAIN;
	public static final int SCAN_CR_SEL          = SDLScancode.SDL_SCANCODE_CRSEL;
	public static final int SCAN_EX_SEL          = SDLScancode.SDL_SCANCODE_EXSEL;

	public static final int SCAN_KP_00           = SDLScancode.SDL_SCANCODE_KP_00;
	public static final int SCAN_KP_000          = SDLScancode.SDL_SCANCODE_KP_000;

	public static final int SCAN_THOUSANDS_SEPARATOR= SDLScancode.SDL_SCANCODE_THOUSANDSSEPARATOR;
	public static final int SCAN_DECIMAL_SEPARATOR  = SDLScancode.SDL_SCANCODE_DECIMALSEPARATOR;
	public static final int SCAN_CURRENCY_UNIT      = SDLScancode.SDL_SCANCODE_CURRENCYUNIT;
	public static final int SCAN_CURRENCY_SUB_UNIT  = SDLScancode.SDL_SCANCODE_CURRENCYSUBUNIT;

	public static final int SCAN_KP_LEFT_PAREN   = SDLScancode.SDL_SCANCODE_KP_LEFTPAREN;
	public static final int SCAN_KP_RIGHT_PAREN  = SDLScancode.SDL_SCANCODE_KP_RIGHTPAREN;
	public static final int SCAN_KP_LEFT_BRACE   = SDLScancode.SDL_SCANCODE_KP_LEFTBRACE;
	public static final int SCAN_KP_RIGHT_BRACE  = SDLScancode.SDL_SCANCODE_KP_RIGHTBRACE;
	public static final int SCAN_KP_TAB          = SDLScancode.SDL_SCANCODE_KP_TAB;
	public static final int SCAN_KP_BACKSPACE    = SDLScancode.SDL_SCANCODE_KP_BACKSPACE;
	public static final int SCAN_KP_A            = SDLScancode.SDL_SCANCODE_KP_A;
	public static final int SCAN_KP_B            = SDLScancode.SDL_SCANCODE_KP_B;
	public static final int SCAN_KP_C            = SDLScancode.SDL_SCANCODE_KP_C;
	public static final int SCAN_KP_D            = SDLScancode.SDL_SCANCODE_KP_D;
	public static final int SCAN_KP_E            = SDLScancode.SDL_SCANCODE_KP_E;
	public static final int SCAN_KP_F            = SDLScancode.SDL_SCANCODE_KP_F;
	public static final int SCAN_KP_XOR          = SDLScancode.SDL_SCANCODE_KP_XOR;
	public static final int SCAN_KP_POWER        = SDLScancode.SDL_SCANCODE_KP_POWER;
	public static final int SCAN_KP_PERCENT      = SDLScancode.SDL_SCANCODE_KP_PERCENT;
	public static final int SCAN_KP_LESS         = SDLScancode.SDL_SCANCODE_KP_LESS;
	public static final int SCAN_KP_GREATER      = SDLScancode.SDL_SCANCODE_KP_GREATER;
	public static final int SCAN_KP_AMPERSAND    = SDLScancode.SDL_SCANCODE_KP_AMPERSAND;
	public static final int SCAN_KP_DBL_AMPERSAND= SDLScancode.SDL_SCANCODE_KP_DBLAMPERSAND;
	public static final int SCAN_KP_VERTICAL_BAR = SDLScancode.SDL_SCANCODE_KP_VERTICALBAR;
	public static final int SCAN_KP_DBL_VERTICAL_BAR= SDLScancode.SDL_SCANCODE_KP_DBLVERTICALBAR;
	public static final int SCAN_KP_COLON        = SDLScancode.SDL_SCANCODE_KP_COLON;
	public static final int SCAN_KP_HASH         = SDLScancode.SDL_SCANCODE_KP_HASH;
	public static final int SCAN_KP_SPACE        = SDLScancode.SDL_SCANCODE_KP_SPACE;

	public static final int SCAN_KP_AT           = SDLScancode.SDL_SCANCODE_KP_AT;
	public static final int SCAN_KP_EXCLAIM      = SDLScancode.SDL_SCANCODE_KP_EXCLAM;
	public static final int SCAN_KP_MEM_STORE    = SDLScancode.SDL_SCANCODE_KP_MEMSTORE;
	public static final int SCAN_KP_MEM_RECALL   = SDLScancode.SDL_SCANCODE_KP_MEMRECALL;
	public static final int SCAN_KP_MEM_CLEAR    = SDLScancode.SDL_SCANCODE_KP_MEMCLEAR;
	public static final int SCAN_KP_MEM_ADD      = SDLScancode.SDL_SCANCODE_KP_MEMADD;
	public static final int SCAN_KP_MEM_SUBTRACT = SDLScancode.SDL_SCANCODE_KP_MEMSUBTRACT;
	public static final int SCAN_KP_MEM_MULTIPLY = SDLScancode.SDL_SCANCODE_KP_MEMMULTIPLY;
	public static final int SCAN_KP_MEM_DIVIDE   = SDLScancode.SDL_SCANCODE_KP_MEMDIVIDE;
	public static final int SCAN_KP_PLUS_MINUS   = SDLScancode.SDL_SCANCODE_KP_PLUSMINUS;
	public static final int SCAN_KP_CLEAR        = SDLScancode.SDL_SCANCODE_KP_CLEAR;
	public static final int SCAN_KP_CLEAR_ENTRY  = SDLScancode.SDL_SCANCODE_KP_CLEARENTRY;
	public static final int SCAN_KP_BINARY       = SDLScancode.SDL_SCANCODE_KP_BINARY;
	public static final int SCAN_KP_OCTAL        = SDLScancode.SDL_SCANCODE_KP_OCTAL;
	public static final int SCAN_KP_DECIMAL      = SDLScancode.SDL_SCANCODE_KP_DECIMAL;      // KP_COMMA
	public static final int SCAN_KP_HEXADECIMAL  = SDLScancode.SDL_SCANCODE_KP_HEXADECIMAL;

	public static final int SCAN_LEFT_CONTROL    = SDLScancode.SDL_SCANCODE_LCTRL;
	public static final int SCAN_LEFT_SHIFT      = SDLScancode.SDL_SCANCODE_LSHIFT;
	public static final int SCAN_LEFT_ALT        = SDLScancode.SDL_SCANCODE_LALT;
	public static final int SCAN_LEFT_SUPER      = SDLScancode.SDL_SCANCODE_LGUI;
	public static final int SCAN_RIGHT_CONTROL   = SDLScancode.SDL_SCANCODE_RCTRL;
	public static final int SCAN_RIGHT_SHIFT     = SDLScancode.SDL_SCANCODE_RSHIFT;
	public static final int SCAN_RIGHT_ALT       = SDLScancode.SDL_SCANCODE_RALT;
	public static final int SCAN_RIGHT_SUPER     = SDLScancode.SDL_SCANCODE_RGUI;

	public static final int SCAN_MODE            = SDLScancode.SDL_SCANCODE_MODE;
	public static final int SCAN_SLEEP           = SDLScancode.SDL_SCANCODE_SLEEP;
	public static final int SCAN_WAKE            = SDLScancode.SDL_SCANCODE_WAKE;
	public static final int SCAN_CHANNEL_INCREMENT= SDLScancode.SDL_SCANCODE_CHANNEL_INCREMENT;
	public static final int SCAN_CHANNEL_DECREMENT= SDLScancode.SDL_SCANCODE_CHANNEL_DECREMENT;

	public static final int SCAN_MEDIA_PLAY      = SDLScancode.SDL_SCANCODE_MEDIA_PLAY;
	public static final int SCAN_MEDIA_PAUSE     = SDLScancode.SDL_SCANCODE_MEDIA_PAUSE;
	public static final int SCAN_MEDIA_RECORD    = SDLScancode.SDL_SCANCODE_MEDIA_RECORD;
	public static final int SCAN_MEDIA_FAST_FORWARD= SDLScancode.SDL_SCANCODE_MEDIA_FAST_FORWARD;
	public static final int SCAN_MEDIA_REWIND    = SDLScancode.SDL_SCANCODE_MEDIA_REWIND;
	public static final int SCAN_MEDIA_NEXT_TRACK= SDLScancode.SDL_SCANCODE_MEDIA_NEXT_TRACK;
	public static final int SCAN_MEDIA_PREVIOUS_TRACK= SDLScancode.SDL_SCANCODE_MEDIA_PREVIOUS_TRACK;
	public static final int SCAN_MEDIA_STOP      = SDLScancode.SDL_SCANCODE_MEDIA_STOP;
	public static final int SCAN_MEDIA_EJECT     = SDLScancode.SDL_SCANCODE_MEDIA_EJECT;
	public static final int SCAN_MEDIA_PLAY_PAUSE= SDLScancode.SDL_SCANCODE_MEDIA_PLAY_PAUSE;
	public static final int SCAN_MEDIA_SELECT    = SDLScancode.SDL_SCANCODE_MEDIA_SELECT;

	public static final int SCAN_AC_NEW          = SDLScancode.SDL_SCANCODE_AC_NEW;
	public static final int SCAN_AC_OPEN         = SDLScancode.SDL_SCANCODE_AC_OPEN;
	public static final int SCAN_AC_CLOSE        = SDLScancode.SDL_SCANCODE_AC_CLOSE;
	public static final int SCAN_AC_EXIT         = SDLScancode.SDL_SCANCODE_AC_EXIT;
	public static final int SCAN_AC_SAVE         = SDLScancode.SDL_SCANCODE_AC_SAVE;
	public static final int SCAN_AC_PRINT        = SDLScancode.SDL_SCANCODE_AC_PRINT;
	public static final int SCAN_AC_PROPERTIES   = SDLScancode.SDL_SCANCODE_AC_PROPERTIES;
	public static final int SCAN_AC_SEARCH       = SDLScancode.SDL_SCANCODE_AC_SEARCH;
	public static final int SCAN_AC_HOME         = SDLScancode.SDL_SCANCODE_AC_HOME;
	public static final int SCAN_AC_BACK         = SDLScancode.SDL_SCANCODE_AC_BACK;
	public static final int SCAN_AC_FORWARD      = SDLScancode.SDL_SCANCODE_AC_FORWARD;
	public static final int SCAN_AC_STOP         = SDLScancode.SDL_SCANCODE_AC_STOP;
	public static final int SCAN_AC_REFRESH      = SDLScancode.SDL_SCANCODE_AC_REFRESH;
	public static final int SCAN_AC_BOOKMARKS    = SDLScancode.SDL_SCANCODE_AC_BOOKMARKS;

	public static final int SCAN_SOFT_LEFT       = SDLScancode.SDL_SCANCODE_SOFTLEFT;
	public static final int SCAN_SOFT_RIGHT      = SDLScancode.SDL_SCANCODE_SOFTRIGHT;
	public static final int SCAN_CALL            = SDLScancode.SDL_SCANCODE_CALL;
	public static final int SCAN_END_CALL        = SDLScancode.SDL_SCANCODE_ENDCALL;
	public static final int SCAN_RESERVED        = SDLScancode.SDL_SCANCODE_RESERVED;
	public static final int SCAN_COUNT           = SDLScancode.SDL_SCANCODE_COUNT;

	// Mouse
	public static final int OFFSET_MOUSE        = 100;
	public static final int MOUSE_BUTTON_1      = SDLMouse.SDL_BUTTON_LEFT      - OFFSET_MOUSE;
	public static final int MOUSE_BUTTON_2      = SDLMouse.SDL_BUTTON_MIDDLE    - OFFSET_MOUSE;
	public static final int MOUSE_BUTTON_3      = SDLMouse.SDL_BUTTON_RIGHT     - OFFSET_MOUSE;
	public static final int MOUSE_BUTTON_4      = SDLMouse.SDL_BUTTON_X1        - OFFSET_MOUSE;
	public static final int MOUSE_BUTTON_5      = SDLMouse.SDL_BUTTON_X2        - OFFSET_MOUSE;
	public static final int MOUSE_BUTTON_6      = SDLMouse.SDL_BUTTON_X2 + 1    - OFFSET_MOUSE;      // ???
	public static final int MOUSE_BUTTON_7      = SDLMouse.SDL_BUTTON_X2 + 2    - OFFSET_MOUSE;      // ???
	public static final int MOUSE_BUTTON_8      = SDLMouse.SDL_BUTTON_X2 + 3    - OFFSET_MOUSE;      // ???

	@Nullable
	public static String getNameForScanCode(final int scanCode)
	{
		// Transform to AZERTY if a Transform is found
		KeyboardType type = (KeyboardType) MaLiLibConfigs.Generic.KEYBOARD_TYPE.getOptionListValue();

		if (type == KeyboardType.AZERTY)
		{
			KeyCodesAzerty transform = KeyCodesAzerty.fromScanCode(scanCode);

			if (transform != null)
			{
				return transform.getName();
			}
		}

		return MAP_SCAN_CODE_TO_NAME.get(scanCode);
	}

	public static int getScanCodeFromName(String name)
	{
		// Transform to AZERTY if a Transform is found;
		// Might cause a few problems if converting a config
		KeyboardType type = (KeyboardType) MaLiLibConfigs.Generic.KEYBOARD_TYPE.getOptionListValue();

		if (type == KeyboardType.AZERTY)
		{
			KeyCodesAzerty transform = KeyCodesAzerty.fromName(name);

			if (transform != null)
			{
				return transform.scanCode();
			}
		}
		// For backwards reading of configs if the mapping isn't found;
		// Might cause a few problems if converting a config
		else if (type == KeyboardType.QWERTY && !MAP_NAME_TO_SCAN_CODE.containsKey(name))
		{
			KeyCodesAzerty transform = KeyCodesAzerty.fromName(name);

			if (transform != null)
			{
				return transform.scanCode();
			}
		}

		return MAP_NAME_TO_SCAN_CODE.getInt(name);
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
		MAP_NAME_TO_SCAN_CODE.defaultReturnValue(SCAN_UNKNOWN);

		for (Field field : ScanCodes.class.getDeclaredFields())
		{
			try
			{
				String name = field.getName();
				int scanCode = SCAN_UNKNOWN;

				if (name.startsWith("SCAN_"))
				{
					name = name.substring(5);
					scanCode = field.getInt(null);
				}
				else if (name.startsWith("MOUSE_"))
				{
					name = name.substring(6);
					scanCode = field.getInt(null);
				}

				if (scanCode != SCAN_UNKNOWN)
				{
					MAP_SCAN_CODE_TO_NAME.put(scanCode, name);
					MAP_NAME_TO_SCAN_CODE.put(name, scanCode);
				}
			}
			catch (Exception e)
			{
				MaLiLib.LOGGER.error("(ScanCodes) Failed to initialize the key name lookup!", e);
			}
		}
	}
}
