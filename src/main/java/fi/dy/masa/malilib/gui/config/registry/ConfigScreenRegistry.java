package fi.dy.masa.malilib.gui.config.registry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.util.data.ModInfo;

/**
 * Post-ReWrite code
 */
public class ConfigScreenRegistry
{
    protected final Map<String, ModInfo> modsMap = new HashMap<>();
    protected ImmutableList<ModInfo> mods = ImmutableList.of();

    public ConfigScreenRegistry()
    {
    }

    public void registerConfigScreenFactory(ModInfo modInfo)
    {
        this.modsMap.put(modInfo.getModId(), modInfo);
        ArrayList<ModInfo> list = new ArrayList<>(this.modsMap.values());
        list.sort(Comparator.comparing(ModInfo::getModName));
        this.mods = ImmutableList.copyOf(list);
    }

    @Nullable
    public Supplier<GuiBase> getConfigScreenFactoryFor(ModInfo modInfo)
    {
        return this.modsMap.get(modInfo.getModId()).getConfigScreenSupplier();
    }

    public ImmutableList<ModInfo> getAllModsWithConfigScreens()
    {
        return this.mods;
    }

    public @Nullable ModInfo getModInfoFromConfigScreen(Class<? extends GuiBase> clazz)
    {
        return this.modsMap.values().stream()
                .filter(mod -> mod.getConfigScreenSupplier() != null)
                .filter(mod -> mod.getConfigScreenSupplier().get().getClass() == clazz)
                .findFirst()
                .orElse(null);
    }
}
