package fi.dy.masa.malilib.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.util.Color4f;
import fi.dy.masa.malilib.util.StringUtils;

public class ConfigColor extends ConfigInteger
{
    private Color4f color;

    public ConfigColor(String name, String defaultValue)
    {
        this(name, defaultValue, name+" Comment?", StringUtils.splitCamelCase(name), name);
    }

    public ConfigColor(String name, String defaultValue, String comment)
    {
        this(name, defaultValue, comment, StringUtils.splitCamelCase(name), name);
    }

    public ConfigColor(String name, String defaultValue, String comment, String prettyName)
    {
        this(name, defaultValue, comment, prettyName, name);
    }

    public ConfigColor(String name, String defaultValue, String comment, String prettyName, String translatedName)
    {
        super(name, StringUtils.getColor(defaultValue, 0), comment, prettyName, translatedName);

        this.color = Color4f.fromColor(this.getIntegerValue());
    }

    @Override
    public ConfigType getType()
    {
        return ConfigType.COLOR;
    }

    public Color4f getColor()
    {
        return this.color;
    }

    @Override
    public ConfigColor translatedName(String translatedName)
    {
        return (ConfigColor) super.translatedName(translatedName);
    }

    @Override
    public ConfigColor apply(String translationPrefix)
    {
        return (ConfigColor) super.apply(translationPrefix);
    }

    @Override
    public String getStringValue()
    {
        return String.format("#%08X", this.getIntegerValue());
    }

    @Override
    public String getDefaultStringValue()
    {
        return String.format("#%08X", this.getDefaultIntegerValue());
    }

    @Override
    public void setValueFromString(String value)
    {
        this.setIntegerValue(StringUtils.getColor(value, 0));
    }

    @Override
    public void setIntegerValue(int value)
    {
        this.color = Color4f.fromColor(value);

        super.setIntegerValue(value); // This also calls the callback, if set
    }

    @Override
    public boolean isModified(String newValue)
    {
        try
        {
            return StringUtils.getColor(newValue, 0) != this.getDefaultIntegerValue();
        }
        catch (Exception e)
        {
        }

        return true;
    }

    @Override
    public void setValueFromJsonElement(JsonElement element)
    {
        try
        {
            if (element.isJsonPrimitive())
            {
                this.value = this.getClampedValue(StringUtils.getColor(element.getAsString(), 0));
                this.color = Color4f.fromColor(this.value);
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
        return new JsonPrimitive(this.getStringValue());
    }
}
