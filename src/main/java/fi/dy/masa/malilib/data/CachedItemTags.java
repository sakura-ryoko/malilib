package fi.dy.masa.malilib.data;

import java.util.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
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

public class CachedItemTags
{
    private static final CachedItemTags INSTANCE = new CachedItemTags();
    public static CachedItemTags getInstance() { return INSTANCE; }
    private final HashMap<CachedTagKey, Entry> entries;

    private CachedItemTags()
    {
        this.entries = new HashMap<>();
    }

    public void build(CachedTagKey key, @Nonnull List<String> list)
    {
        if (list.isEmpty())
        {
            MaLiLib.LOGGER.warn("CachedItemTags#build: list '{}' is empty.", key.toString());
            return;
        }

        Entry entry = new Entry(list);
        Entry oldEntry = this.entries.put(key, entry);

        if (oldEntry != null)
        {
            oldEntry.clear();
        }

        MaLiLib.debugLog("CachedItemTags#build: New tag list: '{}'", key.toString());
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
            MaLiLib.debugLog("CachedItemTags#clearEntry: Clear tag list Entry: '{}'", key.toString());
		}
	}

	public void clear()
    {
        this.entries.forEach(
                (key, entry) -> entry.clear()
        );

        MaLiLib.debugLog("CachedItemTags#clear: Clear all");
    }

    public List<CachedTagKey> matchAny(Item item)
    {
        List<CachedTagKey> list = new ArrayList<>();

        this.entries.forEach(
                (key, entry) ->
                {
                    if (entry.contains(item))
                    {
                        list.add(key);
                    }
                }
        );

        return list;
    }

    public List<CachedTagKey> matchAny(RegistryEntry<Item> item)
    {
        List<CachedTagKey> list = new ArrayList<>();

        this.entries.forEach(
                (key, entry) ->
                {
                    if (entry.contains(item))
                    {
                        list.add(key);
                    }
                }
        );

        return list;
    }

    public boolean match(CachedTagKey key, Item item)
    {
        Entry entry = this.get(key);

        if (entry != null)
        {
            return entry.contains(item);
        }
        else
        {
            MaLiLib.LOGGER.warn("CachedItemTags#match(Item): Invalid tag list '{}'", key.toString());
        }

        return false;
    }

    public boolean match(CachedTagKey key, RegistryEntry<Item> item)
    {
        Entry entry = this.get(key);

        if (entry != null)
        {
            return entry.contains(item);
        }
        else
        {
            MaLiLib.LOGGER.warn("CachedItemTags#match(RegistryEntry): Invalid tag list '{}'", key.toString());
        }

        return false;
    }

    public Optional<Pair<RegistryEntryList<Item>, RegistryEntry<Item>>> matchPair(CachedTagKey key, Item item)
    {
        Entry entry = this.get(key);

        if (entry != null)
        {
            Pair <RegistryEntryList<Item>, RegistryEntry<Item>> pair = entry.matchPair(item);

            if (pair.getLeft() == null && pair.getRight() == null)
            {
                return Optional.empty();
            }

            return Optional.of(pair);
        }
        else
        {
            MaLiLib.LOGGER.warn("CachedItemTags#matchPair(Item): Invalid tag list '{}'", key.toString());
        }

        return Optional.empty();
    }

    public Optional<Pair<RegistryEntryList<Item>, RegistryEntry<Item>>> matchPair(CachedTagKey key, RegistryEntry<Item> item)
    {
        Entry entry = this.get(key);

        if (entry != null)
        {
            Pair <RegistryEntryList<Item>, RegistryEntry<Item>> pair = entry.matchPair(item);

            if (pair.getLeft() == null && pair.getRight() == null)
            {
                return Optional.empty();
            }

            return Optional.of(pair);
        }
        else
        {
            MaLiLib.LOGGER.warn("CachedItemTags#matchPair(RegistryEntry): Invalid tag list '{}'", key.toString());
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
        private final HashSet<RegistryEntry<Item>> items;
        private final HashSet<RegistryEntryList<Item>> tags;

        public Entry()
        {
            this.items = new HashSet<>();
            this.tags = new HashSet<>();
        }

        public Entry(List<String> list)
        {
            this();
            this.insertFromList(list);
        }

        public void insertItem(Item item)
        {
            this.items.add(Registries.ITEM.getEntry(item));
        }

        public void insertItem(RegistryEntry<Item> item)
        {
            this.items.add(item);
        }

        public void insertTag(TagKey<Item> tag)
        {
            if (MinecraftClient.getInstance().world != null)
            {
                RegistryWrapper<Item> wrapper = MinecraftClient.getInstance().world.getRegistryManager().getOrThrow(Registries.ITEM.getKey());
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
                    TagKey<Item> tag = TagKey.of(RegistryKeys.ITEM, id);

                    if (tag != null)
                    {
                        this.insertTag(tag);
                    }
                    else
                    {
                        MaLiLib.LOGGER.warn("CachedItemTags.Entry#insertFromString: Invalid block tag '{}'", entry);
                    }
                }
                else
                {
                    MaLiLib.LOGGER.warn("CachedItemTags.Entry#insertFromString: Invalid block tag id '{}'", entry);
                }
            }
            else
            {
                Identifier id = Identifier.tryParse(entry);

                if (id != null)
                {
                    Item item = Registries.ITEM.get(id);

                    if (item != null)
                    {
                        this.insertItem(item);
                    }
                    else
                    {
                        MaLiLib.LOGGER.warn("CachedItemTags.Entry#insertFromString: Invalid block '{}'", entry);
                    }
                }
                else
                {
                    MaLiLib.LOGGER.warn("CachedItemTags.Entry#insertFromString: Invalid block id '{}'", entry);
                }
            }
        }

        public void insertFromList(List<String> list)
        {
            if (list.isEmpty())
            {
                MaLiLib.LOGGER.warn("CachedItemTags.Entry#insertFromList: List is empty.");
                return;
            }

            for (String entry : list)
            {
                this.insertFromString(entry);
            }
        }

        public boolean contains(Item item)
        {
            RegistryEntry<Item> entry = Registries.ITEM.getEntry(item);

            for (RegistryEntryList<Item> listEntry : this.tags)
            {
                if (listEntry.contains(entry))
                {
                    return true;
                }
            }

            return this.items.contains(entry);
        }

        public boolean contains(RegistryEntry<Item> item)
        {
            return this.contains(item.value());
        }

        public List<String> toList()
        {
            List<String> list = new ArrayList<>();

            this.items.forEach(
                    (entry) ->
                            list.add(entry.getIdAsString())
            );
            this.tags.forEach(
                    (entry) ->
                            list.add("#" + entry.getTagKey().toString())
            );

            return list;
        }

        public Pair<RegistryEntryList<Item>, RegistryEntry<Item>> matchPair(RegistryEntry<Item> entry)
        {
            for (RegistryEntryList<Item> listEntry : this.tags)
            {
                if (listEntry.contains(entry))
                {
                    return Pair.of(listEntry, null);
                }
            }

            if (this.items.contains(entry))
            {
                return Pair.of(null, entry);
            }

            return Pair.of(null, null);
        }

        public Pair<RegistryEntryList<Item>, RegistryEntry<Item>> matchPair(Item item)
        {
            return this.matchPair(Registries.ITEM.getEntry(item));
        }

        public JsonElement toJson()
        {
            JsonArray arr = new JsonArray();

            this.items.forEach(
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
            this.items.clear();
            this.tags.clear();
        }
    }
}
