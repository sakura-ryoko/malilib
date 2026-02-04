package fi.dy.masa.malilib;

import java.nio.file.Path;

import net.minecraft.SharedConstants;

import fi.dy.masa.malilib.util.StringUtils;

public class MaLiLibReference
{
	public static final Path GAME_DIR = MaLiLibFabricData.GAME_DIR;
	public static final Path CONFIG_DIR = MaLiLibFabricData.CONFIG_DIR;
    public static final String MOD_ID = "malilib";
    public static final String MOD_NAME = "MaLiLib";
    public static final String MOD_VERSION = StringUtils.getModVersionString(MOD_ID);
    public static final String MC_VERSION = SharedConstants.getCurrentVersion().id();
    public static final int MC_DATA_VERSION = SharedConstants.getCurrentVersion().dataVersion().version();
	public static final boolean LOCAL_DEBUG = false;
	public static final boolean EXPERIMENTAL_MODE = false;
    public static final boolean DEBUG_MODE = isDebug();
    public static final boolean ANSI_MODE = isAnsiColor();
	public static boolean RUNNING_IN_IDE = MaLiLibFabricData.RUNNING_IN_IDE;

	private static boolean isDebug()
	{
		return LOCAL_DEBUG || RUNNING_IN_IDE || EXPERIMENTAL_MODE;
	}

	private static boolean isAnsiColor()
	{
		return (RUNNING_IN_IDE && DEBUG_MODE) || LOCAL_DEBUG || EXPERIMENTAL_MODE;
	}
}
