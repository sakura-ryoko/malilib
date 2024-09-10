package fi.dy.masa.malilib.config.options;

import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

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
    public List<IConfigLockedListEntry> getEntries()
    {
        return this.values;
    }

    @Override
    public List<String> getConfigKeys()
    {
        return this.handler.getConfigKeys(this.getEntries());
    }

    @Override
    public ImmutableList<IConfigLockedListEntry> getDefaultEntries()
    {
        return this.defaultList;
    }

    @Override
    public void setEntries(List<IConfigLockedListEntry> entries)
    {
        if (this.values.equals(entries) == false)
        {
            this.values.clear();
            this.values = this.handler.setEntries(entries);
            this.onValueChanged();
        }
    }

    @Override
    public IConfigLockedListEntry getEmpty()
    {
        return this.handler.getEmpty();
    }

    @Override
    public IConfigLockedListEntry getEntry(String key)
    {
        return this.handler.getEntry(key);
    }

    @Override
    public int getEntryIndex(IConfigLockedListEntry entry)
    {
        return this.handler.getEntryIndex(this.values, entry);
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
                JsonArray arr = element.getAsJsonArray();
                this.setEntries(this.handler.fromJsonArray(arr));
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
        JsonArray arr = new JsonArray();

        this.handler.toJsonArray(this.values, arr);

        return arr;
    }
}
