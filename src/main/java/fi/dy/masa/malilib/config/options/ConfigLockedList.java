package fi.dy.masa.malilib.config.options;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.IConfigLockedList;
import fi.dy.masa.malilib.config.IConfigLockedListEntry;
import fi.dy.masa.malilib.config.IConfigLockedListType;
import fi.dy.masa.malilib.util.StringUtils;

public class ConfigLockedList extends ConfigBase<ConfigLockedList> implements IConfigLockedList
{
    IConfigLockedListType handler;
    ImmutableList<IConfigLockedListEntry> defaultList;
    List<IConfigLockedListEntry> values = new ArrayList<>();

    public ConfigLockedList(String name, IConfigLockedListType handler)
    {
        this(name, handler, name+" Comment?", StringUtils.splitCamelCase(name), name);
    }

    public ConfigLockedList(String name, IConfigLockedListType handler, String comment)
    {
        this(name, handler, comment, StringUtils.splitCamelCase(name), name);
    }

    public ConfigLockedList(String name, IConfigLockedListType handler, String comment, String prettyName)
    {
        this(name, handler, comment, prettyName, name);
    }

    public ConfigLockedList(String name, IConfigLockedListType handler, String comment, String prettyName, String translatedName)
    {
        super(ConfigType.LOCKED_LIST, name, comment, prettyName, translatedName);

        this.handler = handler;
        this.defaultList = handler.getDefaultEntries();
        this.values.addAll(this.defaultList);
    }

    @Override
    public ImmutableList<IConfigLockedListEntry> getDefaultEntries()
    {
        return this.defaultList;
    }

    @Override
    public List<IConfigLockedListEntry> getEntries()
    {
        return this.values;
    }

    @Override
    public List<String> getConfigKeys()
    {
        List<String> list = new ArrayList<>();

        for (IConfigLockedListEntry entry : values)
        {
            list.add(entry.getDisplayName());
        }

        return list;
    }

    @Override
    public void setEntries(List<IConfigLockedListEntry> entries)
    {
        if (this.values.equals(entries) == false)
        {
            this.values.clear();
            entries.forEach((v) ->
            {
                IConfigLockedListEntry entry = this.handler.fromString(v.getStringValue());

                if (entry != null)
                {
                    this.values.add(entry);
                }
            });

            this.onValueChanged();
        }
    }

    @Override
    @Nullable
    public IConfigLockedListEntry getEmpty()
    {
        return null;
    }

    @Override
    @Nullable
    public IConfigLockedListEntry getEntry(String key)
    {
        return this.handler.fromString(key);
    }

    @Override
    public int getEntryIndex(IConfigLockedListEntry entry)
    {
        for (int i = 0; i < this.values.size(); i++)
        {
            if (this.values.get(i).equals(entry))
            {
                return i;
            }
        }

        return -1;
    }

    @Override
    public void resetToDefault()
    {
        this.setEntries(this.defaultList);
    }

    @Override
    public boolean isModified()
    {
        return this.values.equals(this.defaultList) == false;
    }

    @Override
    public void setValueFromJsonElement(JsonElement element)
    {
        this.values.clear();

        try
        {
            if (element.isJsonArray())
            {
                JsonArray array = element.getAsJsonArray();

                List<IConfigLockedListEntry> defList = new ArrayList<>(this.getDefaultEntries().stream().toList());
                List<IConfigLockedListEntry> list = new ArrayList<>();

                // Only add matches ONCE & compare with Defaults.
                for (int i = 0; i < array.size(); i++)
                {
                    IConfigLockedListEntry entry = this.handler.fromString(array.get(i).getAsString());

                    if (entry != null && list.contains(entry) == false)
                    {
                        list.add(entry);
                        defList.remove(entry);
                    }
                }

                // Default entries are missing
                if (defList.isEmpty() == false)
                {
                    list.addAll(defList);
                }

                this.setEntries(list);
            }
            else
            {
                MaLiLib.logger.warn("Failed to set config value for '{}' from the JSON element '{}'", this.getName(), element);
            }
        }
        catch (Exception e)
        {
            MaLiLib.logger.warn("Failed to set config value for '{}' from the JSON element '{}'", this.getName(), element, e);
        }
    }

    @Override
    public JsonElement getAsJsonElement()
    {
        List<IConfigLockedListEntry> list = new ArrayList<>(this.getDefaultEntries().stream().toList());
        JsonArray array = new JsonArray();

        // Should only save 1 instance of each config
        for (IConfigLockedListEntry val : this.values)
        {
            if (list.contains(val))
            {
                array.add(new JsonPrimitive(val.getStringValue()));
                list.remove(val);
            }
        }

        // Default settings are missing
        if (list.isEmpty() == false)
        {
            for (IConfigLockedListEntry entry : list)
            {
                array.add(new JsonPrimitive(entry.getStringValue()));
            }
        }

        return array;
    }
}
