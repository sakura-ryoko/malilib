package fi.dy.masa.malilib;

import java.nio.file.Path;

import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.SharedConstants;
import net.fabricmc.loader.api.FabricLoader;

public class MaLiLibReference
{
    public static final String MOD_ID = "malilib";
    public static final String MOD_NAME = "MaLiLib";
    public static final String MOD_VERSION = StringUtils.getModVersionString(MOD_ID);
    public static final String MC_VERSION = SharedConstants.getGameVersion().id();
    public static final int MC_DATA_VERSION = SharedConstants.getGameVersion().dataVersion().id();
	public static final Path GAME_DIR = FabricLoader.getInstance().getGameDir();
	public static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir();
    public static final boolean DEBUG_MODE = isDebug();
    public static final boolean LOCAL_DEBUG = false;
    public static final boolean ANSI_MODE = DEBUG_MODE;
    public static final boolean EXPERIMENTAL_MODE = false;

    /**
     * This is so that MaLiLib's DEBUG_MODE is toggle-able using Vanilla's Debug Flag(s).
     * @return ()
     */
    private static boolean isDebug()
    {
        if (SharedConstants.DEBUG_ENABLED || SharedConstants.isDevelopment)
        {
            return true;
        }

        return LOCAL_DEBUG;
    }
}
