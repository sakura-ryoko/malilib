package fi.dy.masa.malilib.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.*;
import fi.dy.masa.malilib.gui.MaLiLibIcons;
import fi.dy.masa.malilib.gui.button.*;
import fi.dy.masa.malilib.gui.interfaces.IConfigInfoProvider;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.wrappers.TextFieldType;
import fi.dy.masa.malilib.util.MathUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class ConfigDouble extends ConfigBase<ConfigDouble> implements IConfigDouble
{
    public static final Codec<ConfigDouble> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            PrimitiveCodec.STRING.fieldOf("name").forGetter(ConfigBase::getName),
                            PrimitiveCodec.DOUBLE.fieldOf("defaultValue").forGetter(get -> get.defaultValue),
                            PrimitiveCodec.DOUBLE.fieldOf("minValue").forGetter(get -> get.minValue),
                            PrimitiveCodec.DOUBLE.fieldOf("maxValue").forGetter(get -> get.maxValue),
                            PrimitiveCodec.DOUBLE.fieldOf("value").forGetter(get -> get.value),
                            PrimitiveCodec.BOOL.fieldOf("useSlider").forGetter(get -> get.useSlider),
                            PrimitiveCodec.STRING.fieldOf("comment").forGetter(get -> get.comment),
                            PrimitiveCodec.STRING.fieldOf("prettyName").forGetter(get -> get.prettyName),
                            PrimitiveCodec.STRING.fieldOf("translatedName").forGetter(get -> get.translatedName)
                    )
                    .apply(instance, ConfigDouble::new)
    );
    private final double minValue;
    private final double maxValue;
    private final double defaultValue;
    private double value;
    private boolean useSlider;
    private double lastValue;

    public ConfigDouble(String name, double defaultValue)
    {
        this(name, defaultValue, Double.MIN_VALUE, Double.MAX_VALUE, name+" Comment?", StringUtils.splitCamelCase(name), name);
    }

    public ConfigDouble(String name, double defaultValue, String comment)
    {
        this(name, defaultValue, Double.MIN_VALUE, Double.MAX_VALUE, comment, StringUtils.splitCamelCase(name), name);
    }

    public ConfigDouble(String name, double defaultValue, String comment, String prettyName)
    {
        this(name, defaultValue, Double.MIN_VALUE, Double.MAX_VALUE, comment, prettyName, name);
    }

    public ConfigDouble(String name, double defaultValue, String comment, String prettyName, String translatedName)
    {
        this(name, defaultValue, Double.MIN_VALUE, Double.MAX_VALUE, comment, prettyName, translatedName);
    }

    public ConfigDouble(String name, double defaultValue, double minValue, double maxValue)
    {
        this(name, defaultValue, minValue, maxValue, false, name+" Comment?", StringUtils.splitCamelCase(name), name);
    }

    public ConfigDouble(String name, double defaultValue, double minValue, double maxValue, String comment)
    {
        this(name, defaultValue, minValue, maxValue, false, comment, StringUtils.splitCamelCase(name), name);
    }

    public ConfigDouble(String name, double defaultValue, double minValue, double maxValue, String comment, String prettyName)
    {
        this(name, defaultValue, minValue, maxValue, false, comment, prettyName, name);
    }

    public ConfigDouble(String name, double defaultValue, double minValue, double maxValue, String comment, String prettyName, String translatedName)
    {
        this(name, defaultValue, minValue, maxValue, false, comment, prettyName, translatedName);
    }

    public ConfigDouble(String name, double defaultValue, double minValue, double maxValue, boolean useSlider)
    {
        this(name, defaultValue, minValue, maxValue, useSlider, name+" Comment?", StringUtils.splitCamelCase(name), name);
    }

    public ConfigDouble(String name, double defaultValue, double minValue, double maxValue, boolean useSlider, String comment)
    {
        this(name, defaultValue, minValue, maxValue, useSlider, comment, StringUtils.splitCamelCase(name), name);
    }

    public ConfigDouble(String name, double defaultValue, double minValue, double maxValue, boolean useSlider, String comment, String prettyName)
    {
        this(name, defaultValue, minValue, maxValue, useSlider, comment, prettyName, name);
    }

    public ConfigDouble(String name, double defaultValue, double minValue, double maxValue, boolean useSlider, String comment, String prettyName, String translatedName)
    {
        super(ConfigType.DOUBLE, name, comment, prettyName, translatedName);

        this.minValue = minValue;
        this.maxValue = maxValue;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.useSlider = useSlider;
        this.updateLastDoubleValue();
    }

    private ConfigDouble(String name, Double defaultValue, Double minValue, Double maxValue, Double value, Boolean useSlider, String comment, String prettyName, String translatedName)
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
    public double getDoubleValue()
    {
        return this.value;
    }

    @Override
    public double getDefaultDoubleValue()
    {
        return this.defaultValue;
    }

    @Override
    public void setDoubleValue(double value)
    {
        this.updateLastDoubleValue();
        double oldValue = this.value;
        this.value = this.getClampedValue(value);

        if (oldValue != this.value)
        {
            this.onValueChanged();
        }
    }

    @Override
    public double getMinDoubleValue()
    {
        return this.minValue;
    }

    @Override
    public double getMaxDoubleValue()
    {
        return this.maxValue;
    }

    @Override
    public double getLastDoubleValue()
    {
        return this.lastValue;
    }

    protected double getClampedValue(double value)
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
            return Double.parseDouble(newValue) != this.defaultValue;
        }
        catch (Exception ignored) { }

        return true;
    }

    @Override
    public void resetToDefault()
    {
        this.setDoubleValue(this.defaultValue);
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
            this.setDoubleValue(Double.parseDouble(value));
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to set config value for {} from the string '{}'; {}", this.getName(), value, e.getLocalizedMessage());
        }
    }

    @Override
    public void updateLastDoubleValue()
    {
        this.lastValue = this.value;
    }

    @Override
    public void setValueFromJsonElement(JsonElement element)
    {
        double oldValue = this.value;

        try
        {
            if (element.isJsonPrimitive())
            {
                double temp = element.getAsDouble();
                this.value = this.getClampedValue(temp);
            }
            else
            {
                MaLiLib.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", this.getName(), element);
            }

            if (oldValue != this.value || this.isDirty())
            {
                this.markClean();

                if (this.getLastDoubleValue() != this.getDoubleValue())
                {
//                    MaLiLib.LOGGER.error("[DOUBLE/{}]: setValueFromJsonElement(): LV: [{}], OV: [{}], NV: [{}]", this.getName(),
//                                         this.getLastDoubleValue(), oldValue, this.getDoubleValue()
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
    
    @Override public void addConfigOption(int x, int y, int labelWidth, int configWidth, WidgetConfigOption configOption) {
        
        final ConfigType type = ConfigType.DOUBLE;
        
        y += 1;
        int configHeight = 20;
        
        String configName = getConfigGuiDisplayName();
        
        configOption.addLabel(x, y + 7, labelWidth, 8, 0xFFFFFFFF, configName);
        
        String comment;
        IConfigInfoProvider infoProvider = configOption.host.getHoverInfoProvider();
        
        if (infoProvider != null)
        {
            comment = infoProvider.getHoverInfo(this);
        }
        else
        {
            comment = getComment();
        }
        
        if (comment != null)
        {
            configOption.addConfigComment(x, y + 5, labelWidth, 12, comment);
        }
        
        x += labelWidth + 10;
		
		int resetX = x + configWidth + 2;
		
		configWidth -= 18;
		configOption.colorDisplayPosX = x + configWidth + 2;
		
		if (this.shouldUseSlider())
		{
			configOption.addConfigSliderEntry(x, y, resetX, configWidth, configHeight, this);
		}
		else
		{
			TextFieldType.STRING.setMaxLength(configOption.maxTextfieldTextLength);
			
			configOption.addConfigTextFieldEntry(x, y, resetX, configWidth, configHeight, this, TextFieldType.DOUBLE);
		}
		
		IGuiIcon icon = this.shouldUseSlider() ? MaLiLibIcons.BTN_TXTFIELD : MaLiLibIcons.BTN_SLIDER;
		ButtonGeneric toggleBtn = new ButtonGeneric(configOption.colorDisplayPosX, y + 2, icon);
		configOption.addButton(toggleBtn, new WidgetConfigOption.ListenerSliderToggle(this));
	}
}
