package fi.dy.masa.malilib;

import java.util.HashMap;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;

public class MaLiLibFabricData
{
	public static HashMap<String, String> ALL_MOD_VERSIONS = collectAllModIds();

	protected static void onInitialize()
	{
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
