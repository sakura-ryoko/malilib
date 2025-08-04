package malilib.input;

import java.util.List;

public interface HotkeyProvider
{
    /**
     * @return A list of all hotkeys in this provider.
     */
    List<? extends Hotkey> getAllHotkeys();

    /**
     * @return A list of all currently enabled hotkeys in this provider that should be registered.
     * This is called when the master hotkey list in malilib is being rebuilt.
     * Any hotkeys not on the returned list of any provider will not trigger/function!
     */
    default List<? extends Hotkey> getEnabledHotkeys()
    {
        return this.getAllHotkeys();
    }

    /**
     * Returns a list of all the hotkeys, grouped in categories.
     * This is mostly just used for the keybind overlap info hover text.
     */
    List<HotkeyCategory> getHotkeysByCategories();
}
