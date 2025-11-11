package fi.dy.masa.malilib.data;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;

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

    /**
     * Match "Replaceable" Cached Block Tags
     * @param block (Block Entry)
     * @return ()
     */
    public static Pair<RegistryEntryList<Block>, RegistryEntry<Block>> matchReplaceableBlockTag(RegistryEntry<Block> block)
    {
        Optional<Pair<RegistryEntryList<Block>, RegistryEntry<Block>>> pair = CachedBlockTags.getInstance().matchPair(CachedTagManager.REPLACEABLE_BLOCKS_KEY, block);

        if (pair.isPresent())
        {
            return pair.get();
        }

        pair = CachedBlockTags.getInstance().matchPair(CachedTagManager.CONCRETE_BLOCKS_KEY, block);

        if (pair.isPresent())
        {
            return pair.get();
        }

        pair = CachedBlockTags.getInstance().matchPair(CachedTagManager.CORAL_FANS_KEY, block);

        if (pair.isPresent())
        {
            return pair.get();
        }

        pair = CachedBlockTags.getInstance().matchPair(CachedTagManager.GLASS_PANES_KEY, block);

        if (pair.isPresent())
        {
            return pair.get();
        }

        pair = CachedBlockTags.getInstance().matchPair(CachedTagManager.GLAZED_TERRACOTTA_BLOCKS_KEY, block);

        return pair.orElseGet(() -> Pair.of(null, null));
    }

    /**
     * Match "Replaceable" Cached Block Tags
     * @param block (Block)
     * @return ()
     */
    public static Pair<RegistryEntryList<Block>, RegistryEntry<Block>> matchReplaceableBlockTag(Block block)
    {
        Optional<Pair<RegistryEntryList<Block>, RegistryEntry<Block>>> pair = CachedBlockTags.getInstance().matchPair(CachedTagManager.REPLACEABLE_BLOCKS_KEY, block);

        if (pair.isPresent())
        {
            return pair.get();
        }

        pair = CachedBlockTags.getInstance().matchPair(CachedTagManager.CONCRETE_BLOCKS_KEY, block);

        if (pair.isPresent())
        {
            return pair.get();
        }

        pair = CachedBlockTags.getInstance().matchPair(CachedTagManager.CORAL_FANS_KEY, block);

        if (pair.isPresent())
        {
            return pair.get();
        }

        pair = CachedBlockTags.getInstance().matchPair(CachedTagManager.GLASS_PANES_KEY, block);

        if (pair.isPresent())
        {
            return pair.get();
        }

        pair = CachedBlockTags.getInstance().matchPair(CachedTagManager.GLAZED_TERRACOTTA_BLOCKS_KEY, block);

        return pair.orElseGet(() -> Pair.of(null, null));
    }

    /**
     * Match "Replaceable" Cached Block Tags
     * @param state (Block State)
     * @return ()
     */
    public static Pair<RegistryEntryList<Block>, RegistryEntry<Block>> matchReplaceableBlockTag(BlockState state)
    {
        Optional<Pair<RegistryEntryList<Block>, RegistryEntry<Block>>> pair = CachedBlockTags.getInstance().matchPair(CachedTagManager.REPLACEABLE_BLOCKS_KEY, state);

        if (pair.isPresent())
        {
            return pair.get();
        }

        pair = CachedBlockTags.getInstance().matchPair(CachedTagManager.CONCRETE_BLOCKS_KEY, state);

        if (pair.isPresent())
        {
            return pair.get();
        }

        pair = CachedBlockTags.getInstance().matchPair(CachedTagManager.CORAL_FANS_KEY, state);

        if (pair.isPresent())
        {
            return pair.get();
        }

        pair = CachedBlockTags.getInstance().matchPair(CachedTagManager.GLASS_PANES_KEY, state);

        if (pair.isPresent())
        {
            return pair.get();
        }

        pair = CachedBlockTags.getInstance().matchPair(CachedTagManager.GLAZED_TERRACOTTA_BLOCKS_KEY, state);

        return pair.orElseGet(() -> Pair.of(null, null));
    }
}
