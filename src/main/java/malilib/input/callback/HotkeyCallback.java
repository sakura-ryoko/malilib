package malilib.input.callback;

import malilib.action.Action;
import malilib.action.NamedAction;
import malilib.input.ActionResult;
import malilib.input.KeyAction;
import malilib.input.KeyBind;

public interface HotkeyCallback
{
    /**
     * Called when a hotkey is triggered.
     * @param action the key action this callback is being called for (either press or release)
     * @param key the keybind that this callback is being called for,
     *            in case the same callback instance is used for multiple different hotkeys
     * @return the action result of executing the callback
     */
    ActionResult onKeyAction(KeyAction action, KeyBind key);

    /**
     * @return An overridden ActivateOn condition for the higher level keybind handling code.
     * This can be used for example in scroll adjustable hotkey callbacks to widen the
     * activation to both edges at the keybind handling level, and then let the scroll adjustable
     * handler to take care of calling the actual nested callback only on the correct edge.
     */
    default KeyAction getOuterActivateOnCondition(KeyBind key)
    {
        return key.getSettings().getActivateOn();
    }

    /**
     * Wraps an Action as a HotkeyCallback
     */
    static HotkeyCallback of(Action action)
    {
        return new ActionHotkeyCallback(action);
    }

    /**
     * Wraps an NamedAction as a HotkeyCallback
     */
    static HotkeyCallback of(NamedAction action)
    {
        return new NamedActionHotkeyCallback(action);
    }
}
