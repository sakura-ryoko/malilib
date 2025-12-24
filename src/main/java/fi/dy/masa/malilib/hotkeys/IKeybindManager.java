package fi.dy.masa.malilib.hotkeys;

import java.util.List;

public interface IKeybindManager
{
    /**
     * Registers a keybind provider, which will want to register
     * some keybinds whenever the key to keybind maps need to be rebuilt.
     * @param provider ()
     */
    void registerKeybindProvider(IKeybindProvider provider);

    /**
     * Un-registers a previously registered keybind provider
     * @param provider ()
     */
    void unregisterKeybindProvider(IKeybindProvider provider);

    /**
     * Returns a list of all the currently registered keybinds, grouped by categories
     * @return ()
     */
    List<KeybindCategory> getKeybindCategories();

    /**
     * Forces the keys -> keybinds map to be rebuilt, and causes
     * {@link IKeybindProvider#addKeysToMap(IKeybindManager)} to be called
     * for each registered IKeybindProvider.
     */
    void updateUsedKeys();

    /**
     * Adds the provided IKeybind to the map of keys -> keybinds,
     * which is used in the input event handler to distribute the events/actions
     * from a key event to any keybinds using that key.
     * @param keybind ()
     */
    void addKeybindToMap(IKeybind keybind);

    /**
     * Adds hotkeys for the given mod and category to the master list.
     * These are used to show a combined view of all registered keybinds
     * from all mods using this library.
     * @param modName ()
     * @param keyCategory ()
     * @param hotkeys ()
     */
    void addHotkeysForCategory(String modName, String keyCategory, List<? extends IHotkey> hotkeys);
}
