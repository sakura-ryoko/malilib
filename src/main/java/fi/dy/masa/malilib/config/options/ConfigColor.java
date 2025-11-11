package fi.dy.masa.malilib.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.IConfigColor;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.Color4f;
import net.minecraft.util.math.MathHelper;

public class ConfigColor extends ConfigBase<ConfigColor> implements IConfigColor
{
    public static final Codec<ConfigColor> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    PrimitiveCodec.STRING.fieldOf("name").forGetter(ConfigBase::getName),
                    Color4f.CODEC.fieldOf("defaultValue").forGetter(get -> get.defaultValue),
                    Color4f.CODEC.fieldOf("value").forGetter(get -> get.color),
                    PrimitiveCodec.STRING.fieldOf("comment").forGetter(get -> get.comment),
                    PrimitiveCodec.STRING.fieldOf("prettyName").forGetter(get -> get.prettyName),
                    PrimitiveCodec.STRING.fieldOf("translatedName").forGetter(get -> get.translatedName)
            ).apply(inst, ConfigColor::new)
    );

    private Color4f defaultValue;
    protected final int minValue;
    protected final int maxValue;
    private Color4f color;

    public ConfigColor(String name, Color4f defaultValue)
    {
        super(ConfigType.COLOR, name, name+" Comment?", StringUtils.splitCamelCase(name), name);

        this.minValue = Integer.MIN_VALUE;
        this.maxValue = Integer.MAX_VALUE;
        this.defaultValue = defaultValue;
        this.color = this.defaultValue;
    }

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
        super(ConfigType.COLOR, name, comment, prettyName, translatedName);

        int value = StringUtils.getColor(defaultValue, 0);
        this.minValue = Integer.MIN_VALUE;
        this.maxValue = Integer.MAX_VALUE;
        this.defaultValue = Color4f.fromColor(value);
        this.color = this.defaultValue;
    }

    private ConfigColor(String name, Color4f defaultValue, Color4f color, String comment, String prettyName, String translatedName)
    {
        this(name, defaultValue.toString(), comment, prettyName, translatedName);
        this.defaultValue = defaultValue;
        this.color = color;
    }

    @Override
    public ConfigType getType()
    {
        return ConfigType.COLOR;
    }

    @Override
    public Color4f getColor()
    {
        return this.color;
    }

    @Override
    public ConfigColor translatedName(String translatedName)
    {
        return super.translatedName(translatedName);
    }

    @Override
    public ConfigColor apply(String translationPrefix)
    {
        return super.apply(translationPrefix);
    }

    @Override
    public int getIntegerValue()
    {
        return this.color.getIntValue();
    }

    @Override
    public int getDefaultIntegerValue()
    {
        return this.defaultValue.getIntValue();
    }

    @Override
    public String getStringValue()
    {
//        return String.format("#%08X", this.getIntegerValue());
        return this.color.toString();
    }

    @Override
    public String getDefaultStringValue()
    {
//        return String.format("#%08X", this.getDefaultIntegerValue());
        return this.defaultValue.toString();
    }

    @Override
    public void setValueFromString(String value)
    {
		Color4f oldColor = this.color;
        this.color = Color4f.fromString(value);

		if (oldColor.getIntValue() != this.color.getIntValue())
		{
			this.onValueChanged();
		}
    }

    @Override
    public void setIntegerValue(int value)
    {
        int clamp = this.getClampedValue(value);
        int oldValue = this.color.getIntValue();

        this.color = Color4f.fromColor(clamp);

        if (oldValue != clamp ||
            this.color.getIntValue() != oldValue)
        {
            this.onValueChanged();
        }
    }

    @Override
    public int getMinIntegerValue()
    {
        return this.minValue;
    }

    @Override
    public int getMaxIntegerValue()
    {
        return this.maxValue;
    }

    protected int getClampedValue(int value)
    {
        return MathHelper.clamp(value, this.minValue, this.maxValue);
    }

    @Override
    public boolean isModified()
    {
        return this.color.getIntValue() != this.defaultValue.getIntValue();
    }

    @Override
    public void resetToDefault()
    {
        Color4f oldColor = this.color;
        this.color = this.defaultValue;

        if (oldColor.getIntValue() != this.color.getIntValue())
        {
            this.onValueChanged();
        }
    }

    @Override
    public boolean isModified(String newValue)
    {
        try
        {
            return StringUtils.getColor(newValue, 0) != this.getDefaultIntegerValue();
        }
        catch (Exception ignored)
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
                this.setValueFromString(element.getAsString());
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
        return new JsonPrimitive(this.getStringValue());
    }
}
