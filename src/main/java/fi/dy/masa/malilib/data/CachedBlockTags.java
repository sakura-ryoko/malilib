package fi.dy.masa.malilib.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

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

import fi.dy.masa.malilib.MaLiLib;

public class CachedBlockTags
{
    private static final CachedBlockTags INSTANCE = new CachedBlockTags();
    public static CachedBlockTags getInstance() { return INSTANCE; }
    private final HashMap<String, Entry> entries;

    private CachedBlockTags()
    {
        this.entries = new HashMap<>();
    }

    public void build(String name, @Nonnull List<String> list)
    {
        if (name.isEmpty())
        {
            MaLiLib.LOGGER.error("CachedBlockTags#build: Invalid list name.");
            return;
        }

        if (list.isEmpty())
        {
            MaLiLib.LOGGER.warn("CachedBlockTags#build: list '{}' is empty.", name);
            return;
        }

        Entry entry = new Entry(list);
        Entry oldEntry = this.entries.put(name, entry);

        if (oldEntry != null)
        {
            oldEntry.clear();
        }
    }

    public @Nullable Entry get(String name)
    {
        if (this.entries.containsKey(name))
        {
            return this.entries.get(name);
        }

        return null;
    }

	public void clearEntry(String name)
	{
		if (this.entries.containsKey(name))
		{
			this.entries.get(name).clear();
		}
	}

    public void clear()
    {
        this.entries.forEach(
                (name, entry) -> entry.clear()
        );
    }

    public List<String> matchAny(RegistryEntry<Block> block)
    {
        List<String> list = new ArrayList<>();

        this.entries.forEach(
                (name, entry) ->
                {
                    if (entry.contains(block))
                    {
                        list.add(name);
                    }
                }
        );

        return list;
    }

    public List<String> matchAny(Block block)
    {
        List<String> list = new ArrayList<>();

        this.entries.forEach(
                (name, entry) ->
                {
                    if (entry.contains(block))
                    {
                        list.add(name);
                    }
                }
        );

        return list;
    }

    public List<String> matchAny(BlockState state)
    {
        List<String> list = new ArrayList<>();

        this.entries.forEach(
                (name, entry) ->
                {
                    if (entry.contains(state))
                    {
                        list.add(name);
                    }
                }
        );

        return list;
    }

    public boolean match(String name, RegistryEntry<Block> block)
    {
        Entry entry = this.get(name);

        if (entry != null)
        {
            return entry.contains(block);
        }
        else
        {
            MaLiLib.LOGGER.warn("CachedBlockTags#match(BlockEntry): Invalid tag list '{}'", name);
        }

        return false;
    }

    public boolean match(String name, Block block)
    {
        Entry entry = this.get(name);

        if (entry != null)
        {
            return entry.contains(block);
        }
        else
        {
            MaLiLib.LOGGER.warn("CachedBlockTags#match(Block): Invalid tag list '{}'", name);
        }

        return false;
    }

    public boolean match(String name, BlockState state)
    {
        Entry entry = this.get(name);

        if (entry != null)
        {
            return entry.contains(state);
        }
        else
        {
            MaLiLib.LOGGER.warn("CachedBlockTags#match(State): Invalid tag list '{}'", name);
        }

        return false;
    }

    public JsonElement toJson()
    {
        JsonObject obj = new JsonObject();

        this.entries.forEach(
                (name, entry) ->
                        obj.add(name, entry.toJson())
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

                if (entry != null)
                {
                    this.entries.put(key, entry);
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
