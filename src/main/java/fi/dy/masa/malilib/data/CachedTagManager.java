package fi.dy.masa.malilib.data;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.registry.entry.RegistryEntry;

/**
 * Caches Block/Item Tags as if they are real Vanilla Block/Item tags.
 */
public class CachedTagManager
{
	public static void startCache()
	{
        clearCache();
	}

	private static void clearCache()
	{
	}

	/**
	 * Match Cached Block Tags
	 * @param key (Tag List Key)
	 * @param block (Block Entry)
	 * @return ()
	 */
	public static boolean matchBlockTag(String key, RegistryEntry<Block> block)
	{
		return CachedBlockTags.getInstance().match(key, block);
	}

	/**
	 * Match Cached Block Tags
	 * @param key (Tag List Key)
	 * @param block (Block)
	 * @return ()
	 */
	public static boolean matchBlockTag(String key, Block block)
	{
		return CachedBlockTags.getInstance().match(key, block);
	}

	/**
	 * Match Cached Block Tags
	 * @param key (Tag List Key)
	 * @param state (Block State)
	 * @return ()
	 */
	public static boolean matchBlockTag(String key, BlockState state)
	{
		return CachedBlockTags.getInstance().match(key, state);
	}

	/**
	 * Match Cached Block Tags
	 * @param key (Tag List Key)
	 * @param item (Item Entry)
	 * @return ()
	 */
	public static boolean matchItemTag(String key, RegistryEntry<Item> item)
	{
		return CachedItemTags.getInstance().match(key, item);
	}

	/**
	 * Match Cached Block Tags
	 * @param key (Tag List Key)
	 * @param item (Item)
	 * @return ()
	 */
	public static boolean matchItemTag(String key, Item item)
	{
		return CachedItemTags.getInstance().match(key, item);
	}
}
