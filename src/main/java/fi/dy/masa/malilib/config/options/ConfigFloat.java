package fi.dy.masa.malilib.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.IConfigFloat;
import fi.dy.masa.malilib.util.MathUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class ConfigFloat extends ConfigBase<ConfigFloat> implements IConfigFloat
{
    public static final Codec<ConfigFloat> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            PrimitiveCodec.STRING.fieldOf("name").forGetter(ConfigBase::getName),
                            PrimitiveCodec.FLOAT.fieldOf("defaultValue").forGetter(get -> get.defaultValue),
                            PrimitiveCodec.FLOAT.fieldOf("minValue").forGetter(get -> get.minValue),
                            PrimitiveCodec.FLOAT.fieldOf("maxValue").forGetter(get -> get.maxValue),
                            PrimitiveCodec.FLOAT.fieldOf("value").forGetter(get -> get.value),
                            PrimitiveCodec.BOOL.fieldOf("useSlider").forGetter(get -> get.useSlider),
                            PrimitiveCodec.STRING.fieldOf("comment").forGetter(get -> get.comment),
                            PrimitiveCodec.STRING.fieldOf("prettyName").forGetter(get -> get.prettyName),
                            PrimitiveCodec.STRING.fieldOf("translatedName").forGetter(get -> get.translatedName)
                    )
                    .apply(instance, ConfigFloat::new)
    );
    protected final float minValue;
    protected final float maxValue;
    protected final float defaultValue;
    protected float value;
    protected boolean useSlider;
    private float lastFloat;

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
        this.updateLastFloatValue();
    }

    private ConfigFloat(String name, Float defaultValue, Float minValue, Float maxValue, Float value, Boolean useSlider, String comment, String prettyName, String translatedName)
    {
        this(name, defaultValue, minValue, maxValue, useSlider, comment, prettyName, translatedName);
        this.value = value;
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
        this.updateLastFloatValue();
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

    @Override
    public float getLastFloatValue()
    {
        return this.lastFloat;
    }

    protected float getClampedValue(float value)
    {
        return MathUtils.clamp(value, this.minValue, this.maxValue);
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
        catch (Exception ignored) { }

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
            MaLiLib.LOGGER.warn("Failed to set config value for {} from the string '{}'; {}", this.getName(), value, e.getLocalizedMessage());
        }
    }

    @Override
    public void updateLastFloatValue()
    {
        this.lastFloat = this.value;
    }

    @Override
    public void setValueFromJsonElement(JsonElement element)
    {
        float oldValue = this.value;

        try
        {
            if (element.isJsonPrimitive())
            {
                float temp = element.getAsFloat();
                this.value = this.getClampedValue(temp);
            }
            else
            {
                MaLiLib.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", this.getName(), element);
            }

            if (oldValue != this.value || this.isDirty())
            {
                this.markClean();

                if (this.getLastFloatValue() != this.getFloatValue())
                {
//                    MaLiLib.LOGGER.error("[FLOAT/{}]: setValueFromJsonElement(): LV: [{}], OV: [{}], NV: [{}]", this.getName(),
//                                         this.getLastFloatValue(), oldValue, this.getFloatValue()
//                    );

                    this.onValueChanged();
                }
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'; {}", this.getName(), element, e.getLocalizedMessage());
        }
    }

    @Override
    public JsonElement getAsJsonElement()
    {
        return new JsonPrimitive(this.value);
    }
}
