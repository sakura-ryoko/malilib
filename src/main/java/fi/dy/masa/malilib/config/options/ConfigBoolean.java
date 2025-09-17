package fi.dy.masa.malilib.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.malilib.util.StringUtils;

public class ConfigBoolean extends ConfigBase<ConfigBoolean> implements IConfigBoolean
{
    public static final Codec<ConfigBoolean> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                        PrimitiveCodec.STRING.fieldOf("name").forGetter(ConfigBase::getName),
                        PrimitiveCodec.BOOL.fieldOf("defaultValue").forGetter(get -> get.defaultValue),
                        PrimitiveCodec.BOOL.fieldOf("value").forGetter(get -> get.value),
                        PrimitiveCodec.STRING.fieldOf("comment").forGetter(get -> get.comment),
                        PrimitiveCodec.STRING.fieldOf("prettyName").forGetter(get -> get.prettyName),
                        PrimitiveCodec.STRING.fieldOf("translatedName").forGetter(get -> get.translatedName)
                    )
                    .apply(instance, ConfigBoolean::new)
    );
    private final boolean defaultValue;
    private boolean value;

    public ConfigBoolean(String name, boolean defaultValue)
    {
        this(name, defaultValue, name+" Comment?", StringUtils.splitCamelCase(name), name);
    }

    public ConfigBoolean(String name, boolean defaultValue, String comment)
    {
        this(name, defaultValue, comment, StringUtils.splitCamelCase(name), name);
    }

    public ConfigBoolean(String name, boolean defaultValue, String comment, String prettyName)
    {
        this(name, defaultValue, comment, prettyName, name);
    }

    public ConfigBoolean(String name, boolean defaultValue, String comment, String prettyName, String translatedName)
    {
        super(ConfigType.BOOLEAN, name, comment, prettyName, translatedName);

        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    private ConfigBoolean(String name, Boolean defaultValue, Boolean value, String comment, String prettyName, String translatedName)
    {
        this(name, defaultValue, comment, prettyName, translatedName);
        this.value = value;
    }

    @Override
    public boolean getBooleanValue()
    {
        return this.value;
    }

    @Override
    public boolean getDefaultBooleanValue()
    {
        return this.defaultValue;
    }

    @Override
    public void setBooleanValue(boolean value)
    {
        boolean oldValue = this.value;
        this.value = value;

        if (oldValue != this.value)
        {
            this.onValueChanged();
        }
    }

    @Override
    public boolean isModified()
    {
        return this.value != this.defaultValue;
    }

    @Override
    public boolean isModified(String newValue)
    {
        return Boolean.parseBoolean(newValue) != this.defaultValue;
    }

    @Override
    public void resetToDefault()
    {
        this.setBooleanValue(this.defaultValue);
    }

    @Override
    public String getStringValue()
    {
        return String.valueOf(this.value);
    }

    @Override
    public String getDefaultStringValue()
    {
        return String.valueOf(this.defaultValue);
    }

    @Override
    public void setValueFromString(String value)
    {
		boolean oldValue = this.value;
        this.value = Boolean.parseBoolean(value);

		if (oldValue != this.value)
		{
			this.onValueChanged();
		}
    }

    @Override
    public void setValueFromJsonElement(JsonElement element)
    {
        try
        {
            if (element.isJsonPrimitive())
            {
				this.setBooleanValue(element.getAsBoolean());
            }
            else
            {
                MaLiLib.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", this.getName(), element);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", this.getName(), element, e);
        }
    }

    @Override
    public JsonElement getAsJsonElement()
    {
        return new JsonPrimitive(this.value);
    }
}
