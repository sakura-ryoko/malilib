package fi.dy.masa.malilib.util.nbt;

import java.util.HashMap;

public class NbtOverrides
{
	public static final HashMap<String, String> ID_OVERRIDES = new HashMap<>();

	public static boolean hasIDOverride(String id)
	{
		return ID_OVERRIDES.containsKey(id);
	}

	public static String getIDOverride(String id)
	{
		return ID_OVERRIDES.get(id);
	}

	static
	{
		ID_OVERRIDES.put("minecraft:grass", "minecraft:short_grass");
		ID_OVERRIDES.put("minecraft:scute", "minecraft:armadillo_scute");
		ID_OVERRIDES.put("minecraft:chain", "minecraft:iron_chain");
	}
}
