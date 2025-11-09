package fi.dy.masa.malilib.hotkeys;

import net.minecraft.client.input.KeyEvent;

public interface IKeyboardInputHandler
{
    /**
     * Called on keyboard events with the keyCode and scanCode and modifiers, and whether the key was pressed or released.
     *
	 * @param input ()
     * @param eventKeyState ()
     * @return ()
     */
    default boolean onKeyInput(KeyEvent input, boolean eventKeyState)
    {
        return false;
    }
}
