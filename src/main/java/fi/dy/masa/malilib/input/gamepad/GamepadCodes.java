package fi.dy.masa.malilib.input.gamepad;

import org.lwjgl.sdl.SDLGamepad;

public class GamepadCodes
{
	// Types
	public static final int TYPE_UNKNOWN             = SDLGamepad.SDL_GAMEPAD_TYPE_UNKNOWN;
	public static final int TYPE_STANDARD            = SDLGamepad.SDL_GAMEPAD_TYPE_STANDARD;
	public static final int TYPE_XBOX_360            = SDLGamepad.SDL_GAMEPAD_TYPE_XBOX360;
	public static final int TYPE_XBOX_ONE            = SDLGamepad.SDL_GAMEPAD_TYPE_XBOXONE;
	public static final int TYPE_PS_3                = SDLGamepad.SDL_GAMEPAD_TYPE_PS3;
	public static final int TYPE_PS_4                = SDLGamepad.SDL_GAMEPAD_TYPE_PS4;
	public static final int TYPE_PS_5                = SDLGamepad.SDL_GAMEPAD_TYPE_PS5;
	public static final int TYPE_SWITCH_PRO          = SDLGamepad.SDL_GAMEPAD_TYPE_NINTENDO_SWITCH_PRO;
	public static final int TYPE_SWITCH_JOY_LEFT     = SDLGamepad.SDL_GAMEPAD_TYPE_NINTENDO_SWITCH_JOYCON_LEFT;
	public static final int TYPE_SWITCH_JOY_RIGHT    = SDLGamepad.SDL_GAMEPAD_TYPE_NINTENDO_SWITCH_JOYCON_RIGHT;
	public static final int TYPE_SWITCH_JOY_PAIR     = SDLGamepad.SDL_GAMEPAD_TYPE_NINTENDO_SWITCH_JOYCON_PAIR;
	public static final int TYPE_GAME_CUBE           = SDLGamepad.SDL_GAMEPAD_TYPE_GAMECUBE;
	public static final int TYPE_COUNT               = SDLGamepad.SDL_GAMEPAD_TYPE_COUNT;

	// Labels
	public static final int LABEL_UNKNOWN            = SDLGamepad.SDL_GAMEPAD_BUTTON_LABEL_UNKNOWN;
	public static final int LABEL_A                  = SDLGamepad.SDL_GAMEPAD_BUTTON_LABEL_A;
	public static final int LABEL_B                  = SDLGamepad.SDL_GAMEPAD_BUTTON_LABEL_B;
	public static final int LABEL_X                  = SDLGamepad.SDL_GAMEPAD_BUTTON_LABEL_X;
	public static final int LABEL_Y                  = SDLGamepad.SDL_GAMEPAD_BUTTON_LABEL_Y;
	public static final int LABEL_CROSS              = SDLGamepad.SDL_GAMEPAD_BUTTON_LABEL_CROSS;
	public static final int LABEL_CIRCLE             = SDLGamepad.SDL_GAMEPAD_BUTTON_LABEL_CIRCLE;
	public static final int LABEL_SQUARE             = SDLGamepad.SDL_GAMEPAD_BUTTON_LABEL_SQUARE;
	public static final int LABEL_TRIANGLE           = SDLGamepad.SDL_GAMEPAD_BUTTON_LABEL_TRIANGLE;

	// Binds
	public static final int BIND_NONE                = SDLGamepad.SDL_GAMEPAD_BINDTYPE_NONE;
	public static final int BIND_BUTTON              = SDLGamepad.SDL_GAMEPAD_BINDTYPE_BUTTON;
	public static final int BIND_AXIS                = SDLGamepad.SDL_GAMEPAD_BINDTYPE_AXIS;
	public static final int BIND_HAT                 = SDLGamepad.SDL_GAMEPAD_BINDTYPE_HAT;
}
