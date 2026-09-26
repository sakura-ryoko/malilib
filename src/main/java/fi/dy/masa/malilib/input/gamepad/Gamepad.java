package fi.dy.masa.malilib.input.gamepad;

import javax.annotation.Nonnull;
import org.lwjgl.sdl.SDLGamepad;

public record Gamepad(String name, int instanceId, long ptr, GamepadType type)
{
	public static Gamepad build(final int instanceId, long ptr, @Nonnull GamepadType type)
	{
		String name = SDLGamepad.SDL_GetGamepadName(ptr);
		return new Gamepad(name != null ? name : GamepadType.UNKNOWN.getDisplayName(), instanceId, ptr, type);
	}

	public void close()
	{
		if (this.ptr != 0)
		{
			SDLGamepad.SDL_CloseGamepad(this.ptr);
		}
	}
}
