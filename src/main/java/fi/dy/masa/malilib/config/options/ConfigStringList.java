package fi.dy.masa.malilib.config.options;

import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.IConfigStringList;

public class ConfigStringList extends ConfigBase<ConfigStringList> implements IConfigStringList
{
    private final ImmutableList<String> defaultValue;
    private final List<String> strings = new ArrayList<>();

    public ConfigStringList(String name, ImmutableList<String> defaultValue, String comment)
    {
        this(name, defaultValue, comment, name);
    }

    public ConfigStringList(String name, ImmutableList<String> defaultValue, String comment, String prettyName)
    {
        this(name, defaultValue, comment, prettyName, name);
    }

    public ConfigStringList(String name, ImmutableList<String> defaultValue, String comment, String prettyName, String translatedName)
    {
        super(ConfigType.STRING_LIST, name, comment, prettyName, translatedName);

        this.defaultValue = defaultValue;
        this.strings.addAll(defaultValue);
    }

    @Override
    public List<String> getStrings()
    {
        return this.strings;
    }

    @Override
    public ImmutableList<String> getDefaultStrings()
    {
        return this.defaultValue;
    }

    @Override
    public void setStrings(List<String> strings)
    {
        if (this.strings.equals(strings) == false)
        {
            this.strings.clear();
            this.strings.addAll(strings);
            this.onValueChanged();
        }
    }

    @Override
    public void resetToDefault()
    {
        this.setStrings(this.defaultValue);
    }

    @Override
    public boolean isModified()
    {
        return this.strings.equals(this.defaultValue) == false;
    }

    @Override
    public void setValueFromJsonElement(JsonElement element)
    {
        this.strings.clear();

        try
        {
            if (element.isJsonArray())
            {
                JsonArray arr = element.getAsJsonArray();
                final int count = arr.size();

                for (int i = 0; i < count; ++i)
                {
                    this.strings.add(arr.get(i).getAsString());
                }
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

        for (String str : this.strings)
        {
            arr.add(new JsonPrimitive(str));
        }

        return arr;
    }
}
