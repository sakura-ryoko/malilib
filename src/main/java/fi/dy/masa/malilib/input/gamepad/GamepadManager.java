package fi.dy.masa.malilib.input.gamepad;

import java.nio.IntBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.sdl.SDLGamepad;
import org.lwjgl.sdl.SDLInit;
import org.lwjgl.sdl.SDL_Event;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.util.input.ActionCodes;

public class GamepadManager
{
//	private static final AnsiLogger LOGGER = new AnsiLogger(GamepadManager.class);
	public static final GamepadManager INSTANCE = new GamepadManager();
	private final ConcurrentHashMap<Integer, Gamepad> gamepads;

	private GamepadManager()
	{
		this.gamepads = new ConcurrentHashMap<>();
	}

	// Register SDL's Gamepad event polling
	public void onInitialize()
	{
		if (SDLInit.SDL_InitSubSystem(SDLInit.SDL_INIT_GAMEPAD))
		{
			MaLiLib.LOGGER.info("SDL Gamepad subsystem initialized");
		}
	}

	@ApiStatus.Internal
	public void scanForMissingGamepads()
	{
		if (SDLGamepad.SDL_HasGamepad())
		{
			IntBuffer ids = SDLGamepad.SDL_GetGamepads();

			if (ids != null)
			{
				while (ids.hasRemaining())
				{
					final int id = ids.get();

					if (!this.hasId(id))
					{
						long ptr = SDLGamepad.SDL_OpenGamepad(id);

						if (ptr != 0L)
						{
							GamepadType type = GamepadType.fromTypeCode(SDLGamepad.SDL_GetGamepadTypeForID(id));
							Gamepad gp = Gamepad.build(id, ptr, type != null ? type : GamepadType.UNKNOWN);
							this.addId(id, gp);
						}
					}
				}
			}
		}
	}

	@ApiStatus.Internal
	private void addId(final int id, Gamepad gp)
	{
		this.gamepads.put(id, gp);
		MaLiLib.LOGGER.info("Gamepad ID {} connected: {} [type: {}]", id, gp.name(), gp.type().getName());
	}

	@ApiStatus.Internal
	private void removeId(int id)
	{
		Gamepad gp = this.gamepads.remove(id);

		if (gp != null)
		{
			gp.close();
			MaLiLib.LOGGER.info("Gamepad ID {} disconnected: {} [type: {}]", id, gp.name(), gp.type().getName());
		}
	}

	@ApiStatus.Internal
	private boolean hasId(int id)
	{
		return this.gamepads.containsKey(id);
	}

	@ApiStatus.Internal
	private void updateId(int id, Gamepad gp)
	{
		Gamepad oldGp = this.gamepads.replace(id, gp);

		if (oldGp != null)
		{
			MaLiLib.LOGGER.info("Gamepad ID {} updated: {} [type: {} -> {}]", id, gp.name(), oldGp.type().getName(), gp.type().getName());
		}
	}

	@Nullable
	public Gamepad getFirstGamepad()
	{
		if (this.gamepads.isEmpty())
		{
			return null;
		}

		return this.gamepads.values().iterator().next();
	}

	public String checkForLabelTransform(final int code, @Nonnull String fallback)
	{
		Gamepad gp = this.getFirstGamepad();

		if (gp != null && gp.ptr() != 0L)
		{
			int label = SDLGamepad.SDL_GetGamepadButtonLabel(gp.ptr(), code);
			return this.transformLabel(label, fallback);
		}
		else
		{
			MaLiLib.LOGGER.info("transform: Gamepad {} has no ptr", gp != null ? gp.name() : "null");
		}

		return fallback;
	}

	@ApiStatus.Internal
	public String transformLabel(final int label, String fallback)
	{
		return switch (label)
		{
			case GamepadCodes.LABEL_A -> "A";
			case GamepadCodes.LABEL_B -> "B";
			case GamepadCodes.LABEL_X -> "X";
			case GamepadCodes.LABEL_Y -> "Y";
			case GamepadCodes.LABEL_CROSS -> "✕";
			case GamepadCodes.LABEL_CIRCLE -> "○";
			case GamepadCodes.LABEL_SQUARE -> "□";
			case GamepadCodes.LABEL_TRIANGLE -> "△";
			default -> fallback;
		};
	}

	@ApiStatus.Internal
	public void onGamepadAdded(final SDL_Event event)
	{
		final int id = event.gdevice().which();

		if (!this.hasId(id))
		{
			long ptr = SDLGamepad.SDL_OpenGamepad(id);

			if (ptr != 0L)
			{
				GamepadType type = GamepadType.fromTypeCode(event.gdevice().type());
				Gamepad gp = Gamepad.build(id, ptr, type != null ? type : GamepadType.UNKNOWN);
				this.addId(id, gp);
			}
		}
	}

	@ApiStatus.Internal
	public void onGamepadRemoved(final SDL_Event event)
	{
		this.removeId(event.gdevice().which());
	}

	@ApiStatus.Internal
	public void onGamepadRemapped(final SDL_Event event)
	{
		final int id = event.gdevice().which();
		GamepadType type = GamepadType.fromTypeCode(event.gdevice().type());

		if (this.hasId(id))
		{
			Gamepad gp = this.gamepads.get(id);

			if (gp != null && gp.type() != type)
			{
				this.updateId(id, Gamepad.build(id, gp.ptr(), type));
			}
		}
	}

	@ApiStatus.Internal
	public boolean onGamepadButton(final SDL_Event event)
	{
		final int id = event.gbutton().which();
		boolean cancel = false;

		if (this.hasId(id))
		{
			cancel = ((InputEventHandler) InputEventHandler.getInputManager()).onGamepadButton(
					new GamepadButtonEvent(event.gbutton().button(), event.gbutton().down() ? ActionCodes.PRESSED : ActionCodes.RELEASED, event.gbutton().timestamp())
			);
		}

		return cancel;
	}

	@ApiStatus.Internal
	public boolean onGamepadAxisMotion(final SDL_Event event)
	{
		final int id = event.gaxis().which();
		final int axis = event.gaxis().axis();
		final short amount = event.gaxis().value();
		boolean cancel = false;

		if (this.hasId(id))
		{
			if (axis == SDLGamepad.SDL_GAMEPAD_AXIS_LEFT_TRIGGER || axis == SDLGamepad.SDL_GAMEPAD_AXIS_RIGHT_TRIGGER)
			{
				boolean pressed = amount > 16000;
				cancel = ((InputEventHandler) InputEventHandler.getInputManager()).onGamepadAxisMotion(
						new GamepadButtonEvent(axis, pressed ? ActionCodes.PRESSED : ActionCodes.RELEASED, event.gaxis().timestamp()),
						amount
				);
			}
		}

		return cancel;
	}

	public boolean isButtonDown(final int button)
	{
		for (Map.Entry<Integer, Gamepad> entry : this.gamepads.entrySet())
		{
			if (SDLGamepad.SDL_GetGamepadButton(entry.getValue().ptr(), button))
			{
				return true;
			}
		}

		return false;
	}

	public boolean isAxisTriggerDown(final int axis)
	{
		for (Map.Entry<Integer, Gamepad> entry : this.gamepads.entrySet())
		{
			final short amount = SDLGamepad.SDL_GetGamepadAxis(entry.getValue().ptr(), axis);

			if (amount > 16000)
			{
				return true;
			}
		}

		return false;
	}
}
