package fi.dy.masa.malilib.hotkeys;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.IConfigBoolean;

public record KeyCallbackAdjustable(IConfigBoolean config,
                                    @Nullable IHotkeyCallback callback) implements IHotkeyCallback
{
	private static boolean valueChanged;

	public static void setValueChanged()
	{
		valueChanged = true;
	}

	@Override
	public boolean onKeyAction(KeyAction action, IKeybind key)
	{
		KeybindSettings settings = key.getSettings();

		// For keybinds that activate on both edges, the press action activates the
		// "adjust mode", and we just cancel further processing of the key presses here.
		if (settings.getActivateOn() == KeyAction.BOTH)
		{
			if (action == KeyAction.PRESS)
			{
				return true;
			}

			// Don't toggle the state if a value was adjusted
			if (valueChanged)
			{
				valueChanged = false;

				return true;
			}
		}
		else if (valueChanged)
		{
			valueChanged = false;
		}

		if (this.callback != null)
		{
			return this.callback.onKeyAction(action, key);
		}
		else
		{
			this.config.toggleBooleanValue();

			return true;
		}
	}
}
