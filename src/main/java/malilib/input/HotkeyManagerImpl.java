package malilib.input;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;

import malilib.MaLiLib;

public class HotkeyManagerImpl implements HotkeyManager
{
    protected final List<HotkeyCategory> hotkeyCategories = new ArrayList<>();
    protected final List<HotkeyProvider> keyBindProviders = new ArrayList<>();
    protected final Map<Hotkey, String> lockedHotkeys = new HashMap<>();
    protected Int2ObjectOpenHashMap<ArrayList<KeyBind>> hotkeyMap = new Int2ObjectOpenHashMap<>();
    @Nullable protected ImmutableList<HotkeyCategory> immutableHotkeyCategories;

    @Override
    public void registerHotkeyProvider(HotkeyProvider provider)
    {
        if (this.keyBindProviders.contains(provider) == false)
        {
            this.keyBindProviders.add(provider);
        }

        for (HotkeyCategory category : provider.getHotkeysByCategories())
        {
            this.addKeyBindCategory(category);
        }
    }

    @Override
    public void unregisterHotkeyProvider(HotkeyProvider provider)
    {
        this.keyBindProviders.remove(provider);
    }

    @Override
    public ImmutableList<HotkeyCategory> getHotkeyCategories()
    {
        if (this.immutableHotkeyCategories == null)
        {
            this.immutableHotkeyCategories = ImmutableList.copyOf(this.hotkeyCategories);
        }

        return this.immutableHotkeyCategories;
    }

    @Override
    public void updateUsedKeys()
    {
        // Create a new map to avoid a CME in checkKeyBindsForChanges(),
        // if the update is triggered from a keybind callback
        Int2ObjectOpenHashMap<ArrayList<KeyBind>> hotkeyMap = new Int2ObjectOpenHashMap<>();

        for (HotkeyProvider handler : this.keyBindProviders)
        {
            for (Hotkey hotkey : handler.getEnabledHotkeys())
            {
                this.addHotkeyToMap(hotkey, hotkeyMap);
            }
        }

        hotkeyMap.values().forEach((list) -> list.sort(Comparator.comparingInt((v) -> v.getSettings().getPriority())));
        this.hotkeyMap = hotkeyMap;
    }

    public void clearHotkeyLocks()
    {
        this.lockedHotkeys.clear();
        this.updateUsedKeys();
    }

    public void setLockedHotkeys(Map<Hotkey, String> lockedHotkeys)
    {
        this.lockedHotkeys.clear();
        this.lockedHotkeys.putAll(lockedHotkeys);
        this.updateUsedKeys();
    }

    protected void addHotkeyToMap(Hotkey hotkey, Int2ObjectOpenHashMap<ArrayList<KeyBind>> hotkeyMap)
    {
        if (this.lockedHotkeys.containsKey(hotkey))
        {
            hotkey.setLocked(true);
            hotkey.setLockMessage(this.lockedHotkeys.get(hotkey));
            MaLiLib.debugLog("HotkeyManagerImpl#addHotkeyToMap(): Ignoring locked hotkey '{}'", hotkey.getName());
            return;
        }

        if (hotkey.isLocked())
        {
            hotkey.setLocked(false);
        }

        KeyBind keybind = hotkey.getKeyBind();
        IntArrayList keys = new IntArrayList();

        keybind.getKeysToList(keys);

        final int size = keys.size();

        for (int i = 0; i < size; ++i)
        {
            int key = keys.getInt(i);
            hotkeyMap.computeIfAbsent(key, (k) -> new ArrayList<>()).add(keybind);
        }
    }

    protected void addKeyBindCategory(HotkeyCategory category)
    {
        // Remove a previous entry, if any (matched based on the modName and keyCategory only!)
        this.hotkeyCategories.remove(category);
        this.hotkeyCategories.add(category);
        this.immutableHotkeyCategories = null; // mark for rebuild
    }

    /**
     * NOT PUBLIC API - DO NOT CALL FROM MOD CODE
     */
    boolean checkKeyBindsForChanges(int eventKey)
    {
        boolean cancel = false;
        boolean isFirst = true;
        List<KeyBind> keyBinds = this.hotkeyMap.get(eventKey);

        if (keyBinds != null && keyBinds.isEmpty() == false)
        {
            for (KeyBind keyBind : keyBinds)
            {
                // Note: updateIsPressed() has to be called for key releases too, to reset the state
                KeyUpdateResult result = keyBind.updateIsPressed(isFirst);

                if (result.triggered)
                {
                    isFirst = false;
                }

                cancel |= result.cancel;
            }
        }

        return cancel;
    }
}
