package fi.dy.masa.malilib;

import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.IKeybindManager;
import fi.dy.masa.malilib.hotkeys.IKeybindProvider;

public class MaLiLibInputHandler implements IKeybindProvider
{
    private static final MaLiLibInputHandler INSTANCE = new MaLiLibInputHandler();

    private MaLiLibInputHandler()
    {
        super();
    }

    public static MaLiLibInputHandler getInstance()
    {
        return INSTANCE;
    }

    @Override
    public void addKeysToMap(IKeybindManager manager)
    {
//        manager.addKeybindToMap(MaLiLibConfigs.Generic.OPEN_GUI_CONFIGS.getKeybind());

        // These are probably empty, but I am just building the ability to
        // add Hotkey configs here without much extra effort
        for (IHotkey hotkey : MaLiLibConfigs.Generic.HOTKEY_LIST)
        {
            manager.addKeybindToMap(hotkey.getKeybind());
        }

        for (IHotkey hotkey : MaLiLibConfigs.Debug.HOTKEY_LIST)
        {
            manager.addKeybindToMap(hotkey.getKeybind());
        }
    }

    @Override
    public void addHotkeys(IKeybindManager manager)
    {
//        List<? extends IHotkey> hotkeys = ImmutableList.of( MaLiLibConfigs.Generic.OPEN_GUI_CONFIGS );
//        manager.addHotkeysForCategory(MaLiLibReference.MOD_NAME, MaLiLibReference.MOD_ID + ".hotkeys.category.generic_hotkeys", hotkeys);
        manager.addHotkeysForCategory(MaLiLibReference.MOD_NAME, MaLiLibReference.MOD_ID + ".hotkeys.category.generic_hotkeys", MaLiLibConfigs.Generic.HOTKEY_LIST);
        manager.addHotkeysForCategory(MaLiLibReference.MOD_NAME, MaLiLibReference.MOD_ID + ".hotkeys.category.debug_hotkeys",   MaLiLibConfigs.Debug.HOTKEY_LIST);
    }
}
