package fi.dy.masa.malilib.config;

import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fi.dy.masa.malilib.MaLiLib;

public class ConfigManager implements IConfigManager
{
    private static final ConfigManager INSTANCE = new ConfigManager();

    private final Map<String, IConfigHandler> configHandlers = new HashMap<>();

    public static IConfigManager getInstance()
    {
        return INSTANCE;
    }

    @Override
    public void registerConfigHandler(String modId, IConfigHandler handler)
    {
        this.configHandlers.put(modId, handler);
    }

    @Override
    public void onConfigsChanged(String modId)
    {
        IConfigHandler handler = this.configHandlers.get(modId);

        if (handler != null)
        {
            handler.onConfigsChanged();
        }
    }

    @ApiStatus.Internal
    public Set<String> modIdSet()
    {
        return this.configHandlers.keySet();
    }

    @ApiStatus.Internal
    public void loadAllConfigs()
    {
        MaLiLib.debugLog("loadAllConfigs()");
        for (IConfigHandler handler : this.configHandlers.values())
        {
            handler.load();
        }
    }

    @ApiStatus.Internal
    public void saveAllConfigs()
    {
        MaLiLib.debugLog("saveAllConfigs()");
        for (IConfigHandler handler : this.configHandlers.values())
        {
            handler.save();
        }
    }

    @ApiStatus.Internal
    public void onVanillaSetLanguage(String string)
    {
        MaLiLib.debugLog("onVanillaSetLanguage()");
        for (IConfigHandler handler : this.configHandlers.values())
        {
            handler.onLanguageChanged(string);
        }
    }
}
