package fi.dy.masa.malilib.config;

import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

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
}
