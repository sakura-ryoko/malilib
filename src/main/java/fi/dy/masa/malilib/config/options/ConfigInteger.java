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

public class ConfigInteger extends ConfigBase<ConfigInteger> implements IConfigInteger
{
    public static final Codec<ConfigInteger> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            PrimitiveCodec.STRING.fieldOf("name").forGetter(ConfigBase::getName),
                            PrimitiveCodec.INT.fieldOf("defaultValue").forGetter(get -> get.defaultValue),
                            PrimitiveCodec.INT.fieldOf("minValue").forGetter(get -> get.minValue),
                            PrimitiveCodec.INT.fieldOf("maxValue").forGetter(get -> get.maxValue),
                            PrimitiveCodec.INT.fieldOf("value").forGetter(get -> get.value),
                            PrimitiveCodec.BOOL.fieldOf("useSlider").forGetter(get -> get.useSlider),
                            PrimitiveCodec.STRING.fieldOf("comment").forGetter(get -> get.comment),
                            PrimitiveCodec.STRING.fieldOf("prettyName").forGetter(get -> get.prettyName),
                            PrimitiveCodec.STRING.fieldOf("translatedName").forGetter(get -> get.translatedName)
                    )
                    .apply(instance, ConfigInteger::new)
    );
    protected final int minValue;
    protected final int maxValue;
    protected final int defaultValue;
    protected int value;
    private boolean useSlider;
    private int lastValue;

    public ConfigInteger(String name, int defaultValue)
    {
        this(name, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE, name+" Comment?", StringUtils.splitCamelCase(name), name);
    }

    public ConfigInteger(String name, int defaultValue, String comment)
    {
        this(name, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE, comment, StringUtils.splitCamelCase(name), name);
    }

    public ConfigInteger(String name, int defaultValue, String comment, String prettyName)
    {
        this(name, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE, comment, prettyName, name);
    }

    public ConfigInteger(String name, int defaultValue, String comment, String prettyName, String translatedName)
    {
        this(name, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE, comment, prettyName, translatedName);
    }

    public ConfigInteger(String name, int defaultValue, int minValue, int maxValue)
    {
        this(name, defaultValue, minValue, maxValue, false, name+" Comment?", StringUtils.splitCamelCase(name), name);
    }

    public ConfigInteger(String name, int defaultValue, int minValue, int maxValue, String comment)
    {
        this(name, defaultValue, minValue, maxValue, false, comment, StringUtils.splitCamelCase(name), name);
    }

    public ConfigInteger(String name, int defaultValue, int minValue, int maxValue, String comment, String prettyName)
    {
        this(name, defaultValue, minValue, maxValue, false, comment, prettyName, name);
    }

    public ConfigInteger(String name, int defaultValue, int minValue, int maxValue, String comment, String prettyName, String translatedName)
    {
        this(name, defaultValue, minValue, maxValue, false, comment, prettyName, translatedName);
    }

    public ConfigInteger(String name, int defaultValue, int minValue, int maxValue, boolean useSlider)
    {
        this(name, defaultValue, minValue, maxValue, useSlider, name+" Comment?", StringUtils.splitCamelCase(name), name);
    }

    public ConfigInteger(String name, int defaultValue, int minValue, int maxValue, boolean useSlider, String comment)
    {
        this(name, defaultValue, minValue, maxValue, useSlider, comment, StringUtils.splitCamelCase(name), name);
    }

    public ConfigInteger(String name, int defaultValue, int minValue, int maxValue, boolean useSlider, String comment, String prettyName)
    {
        this(name, defaultValue, minValue, maxValue, useSlider, comment, prettyName, name);
    }

    public ConfigInteger(String name, int defaultValue, int minValue, int maxValue, boolean useSlider, String comment, String prettyName, String translatedName)
    {
        super(ConfigType.INTEGER, name, comment, prettyName, translatedName);

        this.minValue = minValue;
        this.maxValue = maxValue;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.useSlider = useSlider;
        this.updateLastIntegerValue();
    }

    private ConfigInteger(String name, Integer defaultValue, Integer minValue, Integer maxValue, Integer value, Boolean useSlider, String comment, String prettyName, String translatedName)
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
    public int getIntegerValue()
    {
        return this.value;
    }

    @Override
    public int getDefaultIntegerValue()
    {
        return this.defaultValue;
    }

    @Override
    public void setIntegerValue(int value)
    {
        this.updateLastIntegerValue();
        int oldValue = this.value;
        this.value = this.getClampedValue(value);

        if (oldValue != this.value)
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

    @Override
    public int getLastIntegerValue()
    {
        return this.lastValue;
    }

    protected int getClampedValue(int value)
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
            return Integer.parseInt(newValue) != this.defaultValue;
        }
        catch (Exception ignored)
        {
        }

        return true;
    }

    @Override
    public void resetToDefault()
    {
        this.setIntegerValue(this.defaultValue);
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
            this.setIntegerValue(Integer.parseInt(value));
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to set config value for {} from the string '{}'; {}", this.getName(), value, e.getLocalizedMessage());
        }
    }

    @Override
    public void updateLastIntegerValue()
    {
        this.lastValue = this.value;
    }

    @Override
    public void setValueFromJsonElement(JsonElement element)
    {
        int oldValue = this.value;

        try
        {
            if (element.isJsonPrimitive())
            {
                int temp = element.getAsInt();
                this.value = this.getClampedValue(temp);
            }
            else
            {
                MaLiLib.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", this.getName(), element);
            }

            if (oldValue != this.value || this.isDirty())
            {
                this.markClean();

                if (this.getLastIntegerValue() != this.getIntegerValue())
                {
//                    MaLiLib.LOGGER.error("[INT/{}]: setValueFromJsonElement(): LV: [{}], OV: [{}], NV: [{}]", this.getName(),
//                                         this.getLastIntegerValue(), oldValue, this.getIntegerValue()
//                    );

                    this.onValueChanged();
                    this.updateLastIntegerValue();
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
			configOption.addConfigTextFieldEntry(x, y, resetX, configWidth, configHeight, this, TextFieldType.INTEGER);
		}
		
		IGuiIcon icon = this.shouldUseSlider() ? MaLiLibIcons.BTN_TXTFIELD : MaLiLibIcons.BTN_SLIDER;
		ButtonGeneric toggleBtn = new ButtonGeneric(configOption.colorDisplayPosX, y + 2, icon);
		configOption.addButton(toggleBtn, new WidgetConfigOption.ListenerSliderToggle(this));
	}
}
