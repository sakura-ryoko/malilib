package fi.dy.masa.malilib;

import java.nio.file.Path;

import net.minecraft.SharedConstants;
import net.fabricmc.loader.api.FabricLoader;

import fi.dy.masa.malilib.util.StringUtils;

public class MaLiLibReference
{
    public static final String MOD_ID = "malilib";
    public static final String MOD_NAME = "MaLiLib";
    public static final String MOD_VERSION = StringUtils.getModVersionString(MOD_ID);
    public static final String MC_VERSION = SharedConstants.getGameVersion().id();
    public static final int MC_DATA_VERSION = SharedConstants.getGameVersion().dataVersion().id();
	public static final Path GAME_DIR = FabricLoader.getInstance().getGameDir();
    public static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir();
    public static final boolean DEBUG_MODE = false;
    public static final boolean ANSI_MODE = DEBUG_MODE;
    public static final boolean EXPERIMENTAL_MODE = false;
}
