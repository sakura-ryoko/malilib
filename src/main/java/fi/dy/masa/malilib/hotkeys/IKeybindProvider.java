package fi.dy.masa.malilib.hotkeys;

public interface IKeybindProvider
{
    /**
     * Called when the keybind map is refreshed/recreated.
     * Classes implementing this interface should add all of their keybinds
     * using the {@link IKeybindManager#addKeybindToMap(IKeybind)} method when this method is called.
     * Assume any previously added keybinds have been cleared just before this method is called.
     * @param manager ()
     */
    void addKeysToMap(IKeybindManager manager);

    /**
     * Called when the event handler is registered.
     * Any mod that wants all their keybinds to appear in the master/combined list of all
     * keybinds, should add them here using the {@link IKeybindManager#addHotkeysForCategory(String, String, java.util.List)} method).
     * @param manager ()
     */
    void addHotkeys(IKeybindManager manager);
}
