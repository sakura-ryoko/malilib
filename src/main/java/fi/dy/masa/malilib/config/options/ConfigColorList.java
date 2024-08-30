package fi.dy.masa.malilib.config.options;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.IConfigColorList;
import fi.dy.masa.malilib.util.Color4f;
import fi.dy.masa.malilib.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ConfigColorList extends ConfigBase<ConfigColorList> implements IConfigColorList
{
    private final ImmutableList<Color4f> defaultValue;
    private final List<Color4f> colors = new ArrayList<>();

    public ConfigColorList(String name, ImmutableList<Color4f> defaultValue)
    {
        this(name, defaultValue, name+" Comment?", StringUtils.splitCamelCase(name), name);
    }

    public ConfigColorList(String name, ImmutableList<Color4f> defaultValue, String comment)
    {
        this(name, defaultValue, comment, StringUtils.splitCamelCase(name), name);
    }

    public ConfigColorList(String name, ImmutableList<Color4f> defaultValue, String comment, String prettyName)
    {
        this(name, defaultValue, comment, prettyName, name);
    }

    public ConfigColorList(String name, ImmutableList<Color4f> defaultValue, String comment, String prettyName, String translationName)
    {
        super(ConfigType.COLOR_LIST, name, comment, prettyName, translationName);

        this.defaultValue = defaultValue;
        this.colors.addAll(defaultValue);
    }

    @Override
    public List<Color4f> getColors()
    {
        return this.colors;
    }

    @Override
    public ImmutableList<Color4f> getDefaultColors()
    {
        return this.defaultValue;
    }

    @Override
    public void setColors(List<Color4f> colors)
    {
        if (!this.colors.equals(colors))
        {
            this.colors.clear();
            this.colors.addAll(colors);
            this.onValueChanged();
        }
    }

    @Override
    public void resetToDefault()
    {
        this.setColors(this.defaultValue);
    }

    @Override
    public boolean isModified()
    {
        return !this.colors.equals(this.defaultValue);
    }

    @Override
    public void setValueFromJsonElement(JsonElement element)
    {
        this.colors.clear();
        try
        {
            if (element.isJsonArray())
            {
                JsonArray arr = element.getAsJsonArray();
                final int count = arr.size();
                for (int i = 0; i < count; ++i)
                {
                    this.colors.add(Color4f.fromColor(StringUtils.getColor(arr.get(i).getAsString(), 0)));
                }
            }
            else
            {
                MaLiLib.logger.warn("Failed to set config value for '{}' from the JSON element '{}'", this.getName(), element);
            }
        } catch (Exception e)
        {
            MaLiLib.logger.warn("Failed to set config value for '{}' from the JSON element '{}'", this.getName(), element, e);
        }
    }

    @Override
    public JsonElement getAsJsonElement()
    {
        JsonArray arr = new JsonArray();

        for (Color4f color4f : this.colors)
        {
            arr.add(new JsonPrimitive(color4f.toString()));
        }

        return arr;
    }
}
