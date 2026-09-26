package fi.dy.masa.malilib.util.input;

import org.lwjgl.sdl.SDLEvents;

public class EventCodes
{
	public static final int EVENT_KEY_PRESS                     = SDLEvents.SDL_EVENT_KEY_DOWN;
	public static final int EVENT_KEY_RELEASE                   = SDLEvents.SDL_EVENT_KEY_UP;
	public static final int EVENT_TEXT_EDIT                     = SDLEvents.SDL_EVENT_TEXT_EDITING;
	public static final int EVENT_TEXT_INPUT                    = SDLEvents.SDL_EVENT_TEXT_INPUT;
	public static final int EVENT_KEYMAP_CHANGED                = SDLEvents.SDL_EVENT_KEYMAP_CHANGED;

	public static final int EVENT_MOUSE_MOVED                   = SDLEvents.SDL_EVENT_MOUSE_MOTION;
	public static final int EVENT_MOUSE_PRESS                   = SDLEvents.SDL_EVENT_MOUSE_BUTTON_DOWN;
	public static final int EVENT_MOUSE_RELEASE                 = SDLEvents.SDL_EVENT_MOUSE_BUTTON_UP;
	public static final int EVENT_MOUSE_SCROLL                  = SDLEvents.SDL_EVENT_MOUSE_WHEEL;

	public static final int EVENT_DROP_FILE                     = SDLEvents.SDL_EVENT_DROP_FILE;
	public static final int EVENT_DROP_TEXT                     = SDLEvents.SDL_EVENT_DROP_TEXT;
	public static final int EVENT_DROP_START                    = SDLEvents.SDL_EVENT_DROP_BEGIN;
	public static final int EVENT_DROP_FINISH                   = SDLEvents.SDL_EVENT_DROP_COMPLETE;
	public static final int EVENT_DROP_POSITION                 = SDLEvents.SDL_EVENT_DROP_POSITION;

	public static final int EVENT_GAMEPAD_AXIS_MOTION           = SDLEvents.SDL_EVENT_GAMEPAD_AXIS_MOTION;
	public static final int EVENT_GAMEPAD_BUTTON_DOWN           = SDLEvents.SDL_EVENT_GAMEPAD_BUTTON_DOWN;
	public static final int EVENT_GAMEPAD_BUTTON_UP             = SDLEvents.SDL_EVENT_GAMEPAD_BUTTON_UP;
	public static final int EVENT_GAMEPAD_ADDED                 = SDLEvents.SDL_EVENT_GAMEPAD_ADDED;
	public static final int EVENT_GAMEPAD_REMOVED               = SDLEvents.SDL_EVENT_GAMEPAD_REMOVED;
	public static final int EVENT_GAMEPAD_REMAPPED              = SDLEvents.SDL_EVENT_GAMEPAD_REMAPPED;
	public static final int EVENT_GAMEPAD_TOUCHPAD_DOWN         = SDLEvents.SDL_EVENT_GAMEPAD_TOUCHPAD_DOWN;
	public static final int EVENT_GAMEPAD_TOUCHPAD_MOTION       = SDLEvents.SDL_EVENT_GAMEPAD_TOUCHPAD_MOTION;
	public static final int EVENT_GAMEPAD_TOUCHPAD_UP           = SDLEvents.SDL_EVENT_GAMEPAD_TOUCHPAD_UP;
	public static final int EVENT_GAMEPAD_SENSOR_UPDATE         = SDLEvents.SDL_EVENT_GAMEPAD_SENSOR_UPDATE;
	public static final int EVENT_GAMEPAD_UPDATE_COMPLETE       = SDLEvents.SDL_EVENT_GAMEPAD_UPDATE_COMPLETE;
	public static final int EVENT_GAMEPAD_STEAM_HANDLE_UPDATED  = SDLEvents.SDL_EVENT_GAMEPAD_STEAM_HANDLE_UPDATED;
}
