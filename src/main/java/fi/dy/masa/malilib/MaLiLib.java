package fi.dy.masa.malilib;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;

import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.malilib.util.data.ModInfo;
import fi.dy.masa.malilib.util.log.AnsiLogger;

public class MaLiLib implements ModInitializer
{
    public static final Logger LOGGER = LogManager.getLogger(MaLiLibReference.MOD_ID);
    private static final AnsiLogger ANSI_LOGGER = new AnsiLogger(MaLiLib.class);

    @Override
    public void onInitialize()
    {
        if (MaLiLibReference.DEBUG_MODE) { ANSI_LOGGER.debug("DEBUG_MODE: Active"); }
        MaLiLibFabricData.onInitialize();
        InitializationHandler.getInstance().registerInitializationHandler(new MaLiLibInitHandler());
        Registry.CONFIG_SCREEN.registerConfigScreenFactory(
                new ModInfo(MaLiLibReference.MOD_ID, MaLiLibReference.MOD_NAME, MaLiLibConfigGui::new)
        );
    }

    public static void debugLog(String key, Object... args)
    {
        if (MaLiLibReference.DEBUG_MODE || MaLiLibConfigs.Debug.DEBUG_MESSAGES.getBooleanValue())
        {
            LOGGER.info(key, args);
        }
    }
}
