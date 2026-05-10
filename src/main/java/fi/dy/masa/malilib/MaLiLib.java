package fi.dy.masa.malilib;

import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;

import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.malilib.util.data.ModInfo;
import fi.dy.masa.malilib.util.i18n.i18nManager;

public class MaLiLib implements ModInitializer
{
    public static final Logger LOGGER = LogManager.getLogger(MaLiLibReference.MOD_ID);
    public static i18nManager LANG = null;

    @Override
    public void onInitialize()
    {
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
            String message = "[DEBUG] "+key;
            LOGGER.info(message, args);
        }
    }

    static
    {
        try
        {
            LANG = i18nManager.create(MaLiLibReference.MOD_ID);
            Registry.TRANSLATION_OVERRIDE_MANAGER.registerTranslationManager(MaLiLibReference.MOD_ID, LANG);
        }
        catch (IOException e)
        {
            LOGGER.error("Exception building i18n Manager; {}", e.getLocalizedMessage());
        }
    }
}
