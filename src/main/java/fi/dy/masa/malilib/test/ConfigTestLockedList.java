package fi.dy.masa.malilib.test;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;

import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.config.IConfigLockedListEntry;
import fi.dy.masa.malilib.config.IConfigLockedListType;
import fi.dy.masa.malilib.util.StringUtils;

public class ConfigTestLockedList implements IConfigLockedListType
{
    public static final ConfigTestLockedList INSTANCE = new ConfigTestLockedList();
    public ImmutableList<Entry> VALUES = ImmutableList.copyOf(Entry.values());

    @Override
    public @Nullable IConfigLockedListEntry getEmpty()
    {
        return null;
    }

    @Override
    public @Nullable IConfigLockedListEntry getEntry(String key)
    {
        return Entry.fromString(key);
    }

    @Override
    public ImmutableList<IConfigLockedListEntry> getDefaultEntries()
    {
        ImmutableList.Builder<IConfigLockedListEntry> list = ImmutableList.builder();

        VALUES.forEach((list::add));

        return list.build();
    }

    @Override
    public List<String> getConfigKeys(List<IConfigLockedListEntry> values)
    {
        List<String> list = new ArrayList<>();

        for (IConfigLockedListEntry entry : values)
        {
            list.add(entry.getDisplayName());
        }

        return list;
    }

    @Override
    public List<IConfigLockedListEntry> setEntries(List<IConfigLockedListEntry> entires)
    {
        List<IConfigLockedListEntry> list = new ArrayList<>();

        entires.forEach((v) ->
        {
            Entry entry = Entry.fromString(v.getStringValue());

            if (entry != null)
            {
                list.add(entry);
            }
        });

        return list;
    }

    @Override
    public int getEntryIndex(List<IConfigLockedListEntry> entries, IConfigLockedListEntry entry)
    {
        for (int i = 0; i < entries.size(); i++)
        {
            if (entries.get(i).equals(entry))
            {
                return i;
            }
        }

        return -1;
    }

    @Override
    public List<IConfigLockedListEntry> fromJsonArray(JsonArray array)
    {
        List<IConfigLockedListEntry> list = new ArrayList<>();

        for (int i = 0; i < array.size(); i++)
        {
            Entry entry = Entry.fromString(array.get(i).getAsString());

            if (entry != null)
            {
                list.add(entry);
            }
        }

        return list;
    }

    @Override
    public void toJsonArray(List<IConfigLockedListEntry> values, JsonArray array)
    {
        for (IConfigLockedListEntry val : values)
        {
            array.add(new JsonPrimitive(val.getStringValue()));
        }
    }

    public enum Entry implements IConfigLockedListEntry
    {
        TEST1 ("test1", "test1"),
        TEST2 ("test2", "test2");

        private final String configKey;
        private final String translationKey;

        Entry(String configKey, String translationKey)
        {
            this.configKey = configKey;
            this.translationKey = MaLiLibReference.MOD_ID+".gui.label.locked_test."+translationKey;
        }

        @Override
        public String getStringValue()
        {
            return this.configKey;
        }

        @Override
        public String getDisplayName()
        {
            return StringUtils.getTranslatedOrFallback(this.translationKey, this.configKey);
        }

        @Nullable
        public static Entry fromString(String key)
        {
            for (Entry entry : values())
            {
                if (entry.configKey.equalsIgnoreCase(key))
                {
                    return entry;
                }
                else if (entry.translationKey.equalsIgnoreCase(key))
                {
                    return entry;
                }
                else if (StringUtils.hasTranslation(entry.translationKey) && StringUtils.translate(entry.translationKey).equalsIgnoreCase(key))
                {
                    return entry;
                }
            }

            return null;
        }
    }
}
