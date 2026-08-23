package fi.dy.masa.malilib;

import java.nio.file.Path;
import java.util.HashMap;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;

public class MaLiLibFabricData
{
	protected static final Path GAME_DIR = FabricLoader.getInstance().getGameDir();
	protected static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir();
	public static HashMap<String, String> ALL_MOD_VERSIONS = collectAllModIds();
	protected static boolean RUNNING_IN_IDE = false;

	protected static void onInitialize()
	{
		RUNNING_IN_IDE = FabricLoader.getInstance().isDevelopmentEnvironment();
		collectAllModIds();
	}

	private static HashMap<String, String> collectAllModIds()
	{
		final HashMap<String, String> map = new HashMap<>();

		FabricLoader.getInstance().getAllMods()
		            .stream().toList()
		            .forEach(mc ->
		                     {
			                     ModMetadata meta = mc.getMetadata();
			                     map.put(meta.getId(), meta.getVersion().getFriendlyString());
		                     }
		            );

		return map;
	}
}
