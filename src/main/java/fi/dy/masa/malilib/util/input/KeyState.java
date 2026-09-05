package fi.dy.masa.malilib.util.input;

import javax.annotation.Nullable;

public enum KeyState
{
	KEY_PRESSED,
	KEY_RELEASED,
	MOUSE_MOVED,
	MOUSE_PRESSED,
	MOUSE_RELEASED,
	MOUSE_SCROLLED,
	;

	@Nullable
	public static KeyState fromEventType(final int eventType)
	{
		return switch (eventType)
		{
			case EventCodes.EVENT_KEY_PRESS -> KEY_PRESSED;
			case EventCodes.EVENT_KEY_RELEASE -> KEY_RELEASED;
			case EventCodes.EVENT_MOUSE_MOVED -> MOUSE_MOVED;
			case EventCodes.EVENT_MOUSE_PRESS ->  MOUSE_PRESSED;
			case EventCodes.EVENT_MOUSE_RELEASE -> MOUSE_RELEASED;
			case EventCodes.EVENT_MOUSE_SCROLL ->  MOUSE_SCROLLED;
			default -> null;
		};
	}
}
