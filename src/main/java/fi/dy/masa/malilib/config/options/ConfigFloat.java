package fi.dy.masa.malilib.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import net.minecraft.util.math.MathHelper;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.IConfigFloat;
import fi.dy.masa.malilib.util.StringUtils;

public class ConfigFloat extends ConfigBase<ConfigFloat> implements IConfigFloat
{
    protected final float minValue;
    protected final float maxValue;
    protected final float defaultValue;
    protected float value;
    protected boolean useSlider;

    public ConfigFloat(String name, float defaultValue)
    {
        this(name, defaultValue, Float.MIN_VALUE, Float.MAX_VALUE, false, name+" Comment?", StringUtils.splitCamelCase(name), name);
    }

    public ConfigFloat(String name, float defaultValue, String comment)
    {
        this(name, defaultValue, Float.MIN_VALUE, Float.MAX_VALUE, false, comment, StringUtils.splitCamelCase(name), name);
    }

    public ConfigFloat(String name, float defaultValue, String comment, String prettyName)
    {
        this(name, defaultValue, Float.MIN_VALUE, Float.MAX_VALUE, false, comment, prettyName, name);
    }

    public ConfigFloat(String name, float defaultValue, String comment, String prettyName, String translatedName)
    {
        this(name, defaultValue, Float.MIN_VALUE, Float.MAX_VALUE, false, comment, prettyName, translatedName);
    }

    public ConfigFloat(String name, float defaultValue, float minValue, float maxValue)
    {
        this(name, defaultValue, minValue, maxValue, false, name+" Comment?", StringUtils.splitCamelCase(name), name);
    }

    public ConfigFloat(String name, float defaultValue, float minValue, float maxValue, String comment)
    {
        this(name, defaultValue, minValue, maxValue, false, comment, StringUtils.splitCamelCase(name), name);
    }

    public ConfigFloat(String name, float defaultValue, float minValue, float maxValue, String comment, String prettyName)
    {
        this(name, defaultValue, minValue, maxValue, false, comment, prettyName, name);
    }

    public ConfigFloat(String name, float defaultValue, float minValue, float maxValue, boolean useSlider)
    {
        this(name, defaultValue, minValue, maxValue, useSlider, name+" Comment?", StringUtils.splitCamelCase(name), name);
    }

    public ConfigFloat(String name, float defaultValue, float minValue, float maxValue, boolean useSlider, String comment)
    {
        this(name, defaultValue, minValue, maxValue, useSlider, comment, StringUtils.splitCamelCase(name), name);
    }

    public ConfigFloat(String name, float defaultValue, float minValue, float maxValue, boolean useSlider, String comment, String prettyName)
    {
        this(name, defaultValue, minValue, maxValue, useSlider, comment, prettyName, name);
    }

    public ConfigFloat(String name, float defaultValue, float minValue, float maxValue, boolean useSlider, String comment, String prettyName, String translatedName)
    {
        super(ConfigType.FLOAT, name, comment, prettyName, translatedName);

        this.minValue = minValue;
        this.maxValue = maxValue;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.useSlider = useSlider;
    }

    @Override
    public boolean shouldUseSlider()
    {
        return this.useSlider;
    }

    @Override
    public void toggleUseSlider()
    {
        this.useSlider = ! this.useSlider;
    }

    @Override
    public float getFloatValue()
    {
        return this.value;
    }

    @Override
    public float getDefaultFloatValue()
    {
        return this.defaultValue;
    }

    @Override
    public void setFloatValue(float value)
    {
        float oldValue = this.value;
        this.value = this.getClampedValue(value);

        if (oldValue != this.value)
        {
            this.onValueChanged();
        }
    }

    @Override
    public float getMinFloatValue()
    {
        return this.minValue;
    }

    @Override
    public float getMaxFloatValue()
    {
        return this.maxValue;
    }

    protected float getClampedValue(float value)
    {
        return MathHelper.clamp(value, this.minValue, this.maxValue);
    }

    @Override
    public boolean isModified()
    {
        return this.value != this.defaultValue;
    }

    @Override
    public boolean isModified(String newValue)
    {
        try
        {
            return Float.parseFloat(newValue) != this.defaultValue;
        }
        catch (Exception e)
        {
        }

        return true;
    }

    @Override
    public void resetToDefault()
    {
        this.setFloatValue(this.defaultValue);
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
        try
        {
            this.setFloatValue(Float.parseFloat(value));
        }
        catch (Exception e)
        {
            MaLiLib.logger.warn("Failed to set config value for {} from the string '{}'", this.getName(), value, e);
        }
    }

    @Override
    public void setValueFromJsonElement(JsonElement element)
    {
        try
        {
            if (element.isJsonPrimitive())
            {
                this.value = this.getClampedValue(element.getAsFloat());
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
        return new JsonPrimitive(this.value);
    }
}
