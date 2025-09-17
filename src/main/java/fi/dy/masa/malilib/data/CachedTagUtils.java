package fi.dy.masa.malilib.data;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.List;

public class CachedTagUtils
{
    /**
     * Match Cached Block Tags
     * @param key (Tag List Key)
     * @param block (Block Entry)
     * @return ()
     */
    public static boolean matchBlockTag(CachedTagKey key, RegistryEntry<Block> block)
    {
        return CachedBlockTags.getInstance().match(key, block);
    }

    /**
     * Match Cached Block Tags
     * @param key (Tag List Key)
     * @param block (Block)
     * @return ()
     */
    public static boolean matchBlockTag(CachedTagKey key, Block block)
    {
        return CachedBlockTags.getInstance().match(key, block);
    }

    /**
     * Match Cached Block Tags
     * @param key (Tag List Key)
     * @param state (Block State)
     * @return ()
     */
    public static boolean matchBlockTag(CachedTagKey key, BlockState state)
    {
        return CachedBlockTags.getInstance().match(key, state);
    }

    /**
     * Match Cached Block Tags
     * @param key (Tag List Key)
     * @param item (Item Entry)
     * @return ()
     */
    public static boolean matchItemTag(CachedTagKey key, RegistryEntry<Item> item)
    {
        return CachedItemTags.getInstance().match(key, item);
    }

    /**
     * Match Cached Block Tags
     * @param key (Tag List Key)
     * @param item (Item)
     * @return ()
     */
    public static boolean matchItemTag(CachedTagKey key, Item item)
    {
        return CachedItemTags.getInstance().match(key, item);
    }

    /**
     * Match Cached Block Tags (MULTI-MATCH)
     * @param keys (Tag List Key)
     * @param block (Block Entry)
     * @return ()
     */
    public static boolean matchBlockTagMulti(List<CachedTagKey> keys, RegistryEntry<Block> block)
    {
        for (CachedTagKey key : keys)
        {
            if (CachedBlockTags.getInstance().match(key, block))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Match Cached Block Tags (MULTI-MATCH)
     * @param keys (Tag List Key)
     * @param block (Block)
     * @return ()
     */
    public static boolean matchBlockTagMulti(List<CachedTagKey> keys, Block block)
    {
        for (CachedTagKey key : keys)
        {
            if (CachedBlockTags.getInstance().match(key, block))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Match Cached Block Tags (MULTI-MATCH)
     * @param keys (Tag List Key)
     * @param state (Block State)
     * @return ()
     */
    public static boolean matchBlockTagMulti(List<CachedTagKey> keys, BlockState state)
    {
        for (CachedTagKey key : keys)
        {
            if (CachedBlockTags.getInstance().match(key, state))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Match Cached Block Tags (MULTI-MATCH)
     * @param keys (Tag List Key)
     * @param item (Item Entry)
     * @return ()
     */
    public static boolean matchItemTagMulti(List<CachedTagKey> keys, RegistryEntry<Item> item)
    {
        for (CachedTagKey key : keys)
        {
            if (CachedItemTags.getInstance().match(key, item))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Match Cached Block Tags (MULTI-MATCH)
     * @param keys (Tag List Key)
     * @param item (Item)
     * @return ()
     */
    public static boolean matchItemTagMulti(List<CachedTagKey> keys, Item item)
    {
        for (CachedTagKey key : keys)
        {
            if (CachedItemTags.getInstance().match(key, item))
            {
                return true;
            }
        }

        return false;
    }
}
