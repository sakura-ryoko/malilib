package fi.dy.masa.malilib.data;

import java.util.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.MaLiLib;
import org.apache.commons.lang3.tuple.Pair;

public class CachedBlockTags
{
    private static final CachedBlockTags INSTANCE = new CachedBlockTags();
    public static CachedBlockTags getInstance() { return INSTANCE; }
    private final HashMap<CachedTagKey, Entry> entries;

    private CachedBlockTags()
    {
        this.entries = new HashMap<>();
    }

    public void build(CachedTagKey key, @Nonnull List<String> list)
    {
        if (list.isEmpty())
        {
            MaLiLib.LOGGER.warn("CachedBlockTags#build: list for '{}' is empty.", key.toString());
            return;
        }

        Entry entry = new Entry(list);
        Entry oldEntry = this.entries.put(key, entry);

        if (oldEntry != null)
        {
            oldEntry.clear();
        }

        MaLiLib.debugLog("CachedBlockTags#build: New tag list: '{}'", key.toString());
    }

    public @Nullable Entry get(CachedTagKey key)
    {
        if (this.entries.containsKey(key))
        {
            return this.entries.get(key);
        }

        return null;
    }

	public void clearEntry(CachedTagKey key)
	{
		if (this.entries.containsKey(key))
		{
			this.entries.get(key).clear();
            MaLiLib.debugLog("CachedBlockTags#clearEntry: Clear tag list Entry: '{}'", key.toString());
        }
	}

    public void clear()
    {
        this.entries.forEach(
                (name, entry) -> entry.clear()
        );

        MaLiLib.debugLog("CachedBlockTags#clear: Clear all");
    }

    public List<CachedTagKey> matchAny(RegistryEntry<Block> block)
    {
        List<CachedTagKey> list = new ArrayList<>();

        this.entries.forEach(
                (key, entry) ->
                {
                    if (entry.contains(block))
                    {
                        list.add(key);
                    }
                }
        );

        return list;
    }

    public List<CachedTagKey> matchAny(Block block)
    {
        List<CachedTagKey> list = new ArrayList<>();

        this.entries.forEach(
                (key, entry) ->
                {
                    if (entry.contains(block))
                    {
                        list.add(key);
                    }
                }
        );

        return list;
    }

    public List<CachedTagKey> matchAny(BlockState state)
    {
        List<CachedTagKey> list = new ArrayList<>();

        this.entries.forEach(
                (key, entry) ->
                {
                    if (entry.contains(state))
                    {
                        list.add(key);
                    }
                }
        );

        return list;
    }

    public boolean match(CachedTagKey key, RegistryEntry<Block> block)
    {
        Entry entry = this.get(key);

        if (entry != null)
        {
            return entry.contains(block);
        }
        else
        {
            MaLiLib.LOGGER.warn("CachedBlockTags#match(BlockEntry): Invalid tag list '{}'", key.toString());
        }

        return false;
    }

    public boolean match(CachedTagKey key, Block block)
    {
        Entry entry = this.get(key);

        if (entry != null)
        {
            return entry.contains(block);
        }
        else
        {
            MaLiLib.LOGGER.warn("CachedBlockTags#match(Block): Invalid tag list '{}'", key.toString());
        }

        return false;
    }

    public boolean match(CachedTagKey key, BlockState state)
    {
        Entry entry = this.get(key);

        if (entry != null)
        {
            return entry.contains(state);
        }
        else
        {
            MaLiLib.LOGGER.warn("CachedBlockTags#match(State): Invalid tag list '{}'", key.toString());
        }

        return false;
    }

    public Optional<Pair<RegistryEntryList<Block>, RegistryEntry<Block>>> matchPair(CachedTagKey key, RegistryEntry<Block> block)
    {
        Entry entry = this.get(key);

        if (entry != null)
        {
            Pair <RegistryEntryList<Block>, RegistryEntry<Block>> pair = entry.matchPair(block);

            if (pair.getLeft() == null && pair.getRight() == null)
            {
                return Optional.empty();
            }

            return Optional.of(pair);
        }
        else
        {
            MaLiLib.LOGGER.warn("CachedBlockTags#matchPair(BlockEntry): Invalid tag list '{}'", key.toString());
        }

        return Optional.empty();
    }

    public Optional<Pair<RegistryEntryList<Block>, RegistryEntry<Block>>> matchPair(CachedTagKey key, Block block)
    {
        Entry entry = this.get(key);

        if (entry != null)
        {
            Pair <RegistryEntryList<Block>, RegistryEntry<Block>> pair = entry.matchPair(block);

            if (pair.getLeft() == null && pair.getRight() == null)
            {
                return Optional.empty();
            }

            return Optional.of(pair);
        }
        else
        {
            MaLiLib.LOGGER.warn("CachedBlockTags#matchPair(Block): Invalid tag list '{}'", key.toString());
        }

        return Optional.empty();
    }

    public Optional<Pair<RegistryEntryList<Block>, RegistryEntry<Block>>> matchPair(CachedTagKey key, BlockState state)
    {
        Entry entry = this.get(key);

        if (entry != null)
        {
            Pair <RegistryEntryList<Block>, RegistryEntry<Block>> pair = entry.matchPair(state);

            if (pair.getLeft() == null && pair.getRight() == null)
            {
                return Optional.empty();
            }

            return Optional.of(pair);
        }
        else
        {
            MaLiLib.LOGGER.warn("CachedBlockTags#matchPair(State): Invalid tag list '{}'", key.toString());
        }

        return Optional.empty();
    }

    public JsonElement toJson()
    {
        JsonObject obj = new JsonObject();

        this.entries.forEach(
                (key, entry) ->
                        obj.add(key.toString(), entry.toJson())
        );

        return obj;
    }

    public void fromJson(JsonObject obj)
    {
        this.entries.clear();

        for (String key : obj.keySet())
        {
            if (obj.isJsonArray())
            {
                Entry entry = Entry.fromJson(obj.get(key));
                CachedTagKey tagKey = CachedTagKey.fromString(key);

                if (entry != null)
                {
                    this.entries.put(tagKey, entry);
                }
            }
        }
    }

    public static class Entry
    {
        private final HashSet<RegistryEntry<Block>> blocks;
        private final HashSet<RegistryEntryList<Block>> tags;

        public Entry()
        {
            this.blocks = new HashSet<>();
            this.tags = new HashSet<>();
        }

        public Entry(List<String> list)
        {
            this();
            this.insertFromList(list);
        }

        public void insertBlock(RegistryEntry<Block> block)
        {
            this.blocks.add(block);
        }

        public void insertBlock(Block block)
        {
            this.insertBlock(Registries.BLOCK.getEntry(block));
        }

        public void insertTag(TagKey<Block> tag)
        {
            if (MinecraftClient.getInstance().world != null)
            {
                RegistryWrapper<Block> wrapper = MinecraftClient.getInstance().world.getRegistryManager().getOrThrow(Registries.BLOCK.getKey());
                wrapper.getOptional(tag).ifPresent(this.tags::add);
            }
        }

        public void insertFromString(String entry)
        {
            if (entry.startsWith("#"))
            {
                Identifier id = Identifier.tryParse(entry.substring(1));

                if (id != null)
                {
                    TagKey<Block> tag = TagKey.of(RegistryKeys.BLOCK, id);

                    if (tag != null)
                    {
                        this.insertTag(tag);
                    }
                    else
                    {
                        MaLiLib.LOGGER.warn("CachedBlockTags.Entry#insertFromString: Invalid block tag '{}'", entry);
                    }
                }
                else
                {
                    MaLiLib.LOGGER.warn("CachedBlockTags.Entry#insertFromString: Invalid block tag id '{}'", entry);
                }
            }
            else
            {
                Identifier id = Identifier.tryParse(entry);

                if (id != null)
                {
                    Block block = Registries.BLOCK.get(id);

                    if (block != null)
                    {
                        this.insertBlock(block);
                    }
                    else
                    {
                        MaLiLib.LOGGER.warn("CachedBlockTags.Entry#insertFromString: Invalid block '{}'", entry);
                    }
                }
                else
                {
                    MaLiLib.LOGGER.warn("CachedBlockTags.Entry#insertFromString: Invalid block id '{}'", entry);
                }
            }
        }

        public void insertFromList(List<String> list)
        {
            if (list.isEmpty())
            {
                MaLiLib.LOGGER.warn("CachedBlockTags.Entry#insertFromList: List is empty.");
                return;
            }

            for (String entry : list)
            {
                this.insertFromString(entry);
            }
        }

        public boolean contains(RegistryEntry<Block> entry)
        {
            for (RegistryEntryList<Block> listEntry : this.tags)
            {
                if (listEntry.contains(entry))
                {
                    return true;
                }
            }

            return this.blocks.contains(entry);
        }

        public boolean contains(Block block)
        {
            return this.contains(Registries.BLOCK.getEntry(block));
        }

        public boolean contains(BlockState state)
        {
            return this.contains(state.getBlock());
        }

        public Pair<RegistryEntryList<Block>, RegistryEntry<Block>> matchPair(RegistryEntry<Block> entry)
        {
            for (RegistryEntryList<Block> listEntry : this.tags)
            {
                if (listEntry.contains(entry))
                {
                    return Pair.of(listEntry, null);
                }
            }

            if (this.blocks.contains(entry))
            {
                return Pair.of(null, entry);
            }

            return Pair.of(null, null);
        }

        public Pair<RegistryEntryList<Block>, RegistryEntry<Block>> matchPair(Block block)
        {
            return this.matchPair(Registries.BLOCK.getEntry(block));
        }

        public Pair<RegistryEntryList<Block>, RegistryEntry<Block>> matchPair(BlockState state)
        {
            return this.matchPair(state.getBlock());
        }

        public List<String> toList()
        {
            List<String> list = new ArrayList<>();

            this.blocks.forEach(
                    (entry) ->
                            list.add(entry.getIdAsString())
            );
            this.tags.forEach(
                    (entry) ->
                            list.add("#" + entry.getTagKey().toString())
            );

            return list;
        }

        public JsonElement toJson()
        {
            JsonArray arr = new JsonArray();

            this.blocks.forEach(
                    (entry) ->
                            arr.add(new JsonPrimitive(entry.getIdAsString()))
            );
            this.tags.forEach(
                    (entry) ->
                            arr.add(new JsonPrimitive("#" + entry.getTagKey().toString()))
            );

            return arr;
        }

        public static @Nullable Entry fromJson(JsonElement element)
        {
            if (element.isJsonArray())
            {
                JsonArray arr = element.getAsJsonArray();
                List<String> list = new ArrayList<>();

                for (int i = 0; i < arr.size(); i++)
                {
                    list.add(arr.get(i).getAsString());
                }

                Entry entry = new Entry();

                entry.insertFromList(list);

                return entry;
            }

            return null;
        }

        public void clear()
        {
            this.blocks.clear();
            this.tags.clear();
        }
    }
}
