package malilib.config;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nullable;
import malilib.MaLiLib;
import malilib.util.data.ModInfo;

public class ConfigManagerImpl implements ConfigManager
{
    // This is a linked map to keep the malilib configs as the first entry. Kinda janky, but works for now?
    protected final Map<ModInfo, ModConfig> configHandlers = new LinkedHashMap<>();

    @Override
    public void registerConfigHandler(ModConfig handler)
    {
        final ModInfo modInfo = handler.getModInfo();

        if (this.configHandlers.containsKey(modInfo))
        {
            MaLiLib.LOGGER.warn("Tried to override an existing config handler for mod ID '{}'", modInfo);
            return;
        }

        MaLiLib.debugLog("Registering config handler for mod {}, containing {} categories", modInfo.getModId(), handler.getConfigOptionCategories().size());
        handler.getConfigOptionCategories().forEach((category) -> category.getConfigOptions().forEach((config) -> config.setModInfo(modInfo)));

        this.configHandlers.put(modInfo, handler);
    }

    @Override
    @Nullable
    public ModConfig getConfigHandler(ModInfo modInfo)
    {
        return this.configHandlers.get(modInfo);
    }

    @Override
    public boolean saveConfigsIfChanged(ModInfo modInfo)
    {
        ModConfig handler = this.configHandlers.get(modInfo);

        if (handler != null)
        {
            return handler.onConfigsPotentiallyChanged();
        }

        return false;
    }

    public ArrayList<ModConfig> getAllModConfigs()
    {
        return new ArrayList<>(this.configHandlers.values());
    }

    public ArrayList<ModConfig> getAllModConfigsSorted()
    {
        ArrayList<ModConfig> list = this.getAllModConfigs();
        list.sort(Comparator.comparing(v -> v.getModInfo().getModName()));
        return list;
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void loadAllConfigs()
    {
        for (ModConfig handler : this.getAllModConfigs())
        {
            MaLiLib.debugLog("Loading configs for mod {}", handler.getModInfo().getModId());
            handler.loadFromFile();
        }
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void saveAllConfigs()
    {
        for (ModConfig handler : this.getAllModConfigs())
        {
            handler.saveToFile();
        }
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public boolean saveIfDirty()
    {
        boolean savedSomething = false;

        for (ModConfig handler : this.getAllModConfigs())
        {
            savedSomething |= handler.saveIfDirty();
        }

        return savedSomething;
    }
}
