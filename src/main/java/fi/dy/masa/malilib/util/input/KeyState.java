package fi.dy.masa.malilib.util.input;

import javax.annotation.Nullable;

public enum KeyState
{
	KEY_PRESSED,
	KEY_RELEASED,
	TEXT_EDIT,
	TEXT_INPUT,
	KEYMAP_CHANGED,
	MOUSE_MOVED,
	MOUSE_PRESSED,
	MOUSE_RELEASED,
	MOUSE_SCROLLED,
	DROP_FILE,
	DROP_TEXT,
	DROP_START,
	DROP_FINISH,
	DROP_POSITION
	;

	@Nullable
	public static KeyState fromEventType(final int eventType)
	{
		return switch (eventType)
		{
			case EventCodes.EVENT_KEY_PRESS -> KEY_PRESSED;
			case EventCodes.EVENT_KEY_RELEASE -> KEY_RELEASED;
			case EventCodes.EVENT_TEXT_EDIT -> TEXT_EDIT;
			case EventCodes.EVENT_TEXT_INPUT -> TEXT_INPUT;
			case EventCodes.EVENT_KEYMAP_CHANGED -> KEYMAP_CHANGED;
			case EventCodes.EVENT_MOUSE_MOVED -> MOUSE_MOVED;
			case EventCodes.EVENT_MOUSE_PRESS ->  MOUSE_PRESSED;
			case EventCodes.EVENT_MOUSE_RELEASE -> MOUSE_RELEASED;
			case EventCodes.EVENT_MOUSE_SCROLL ->  MOUSE_SCROLLED;
			case EventCodes.EVENT_DROP_FILE -> DROP_FILE;
			case EventCodes.EVENT_DROP_TEXT -> DROP_TEXT;
			case EventCodes.EVENT_DROP_START -> DROP_START;
			case EventCodes.EVENT_DROP_FINISH -> DROP_FINISH;
			case EventCodes.EVENT_DROP_POSITION -> DROP_POSITION;
			default -> null;
		};
	}
}
