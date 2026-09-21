package fi.dy.masa.malilib.compat.sodium;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibFabricData;
import fi.dy.masa.malilib.compat.ModIds;

public class SodiumCompat
{
	private static boolean isSodiumLoaded = false;
	private static String sodiumVersion = "";

	public static void register()
	{
		if (MaLiLibFabricData.ALL_MOD_VERSIONS.containsKey(ModIds.sodium))
		{
			sodiumVersion = MaLiLibFabricData.ALL_MOD_VERSIONS.get(ModIds.sodium);
			isSodiumLoaded = true;
		}

		MaLiLib.LOGGER.info("Sodium: [{}]", isSodiumLoaded ? sodiumVersion : "N/F");
	}

	public static boolean hasSodium()
	{
		return isSodiumLoaded;
	}
}
