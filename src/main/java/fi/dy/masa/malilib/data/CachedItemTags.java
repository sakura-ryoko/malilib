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

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import fi.dy.masa.malilib.MaLiLib;

public class CachedItemTags
{
    private static final CachedItemTags INSTANCE = new CachedItemTags();
    public static CachedItemTags getInstance() { return INSTANCE; }
    private final HashMap<String, Entry> entries;

    private CachedItemTags()
    {
        this.entries = new HashMap<>();
    }

    public void build(String name, @Nonnull List<String> list)
    {
        if (name.isEmpty())
        {
            MaLiLib.LOGGER.error("CachedItemTags#build: Invalid list name.");
            return;
        }

        if (list.isEmpty())
        {
            MaLiLib.LOGGER.warn("CachedItemTags#build: list '{}' is empty.", name);
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

    public void clear()
    {
        this.entries.forEach(
                (name, entry) -> entry.clear()
        );
    }

    public List<String> matchAny(Item item)
    {
        List<String> list = new ArrayList<>();

        this.entries.forEach(
                (name, entry) ->
                {
                    if (entry.contains(item))
                    {
                        list.add(name);
                    }
                }
        );

        return list;
    }

    public List<String> matchAny(RegistryEntry<Item> item)
    {
        List<String> list = new ArrayList<>();

        this.entries.forEach(
                (name, entry) ->
                {
                    if (entry.contains(item))
                    {
                        list.add(name);
                    }
                }
        );

        return list;
    }

    public boolean match(String name, Item item)
    {
        Entry entry = this.get(name);

        if (entry != null)
        {
            return entry.contains(item);
        }
        else
        {
            MaLiLib.LOGGER.warn("CachedItemTags#match(Item): Invalid tag list '{}'", name);
        }

        return false;
    }

    public boolean match(String name, RegistryEntry<Item> item)
    {
        Entry entry = this.get(name);

        if (entry != null)
        {
            return entry.contains(item);
        }
        else
        {
            MaLiLib.LOGGER.warn("CachedItemTags#match(RegistryEntry): Invalid tag list '{}'", name);
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
