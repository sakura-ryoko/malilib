package fi.dy.masa.malilib.config.options;

import java.util.Objects;
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
import fi.dy.masa.malilib.util.StringUtils;

public class ConfigString extends ConfigBase<ConfigString> implements IConfigValue, IConfigString
{
    public static final Codec<ConfigString> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            PrimitiveCodec.STRING.fieldOf("name").forGetter(ConfigBase::getName),
                            PrimitiveCodec.STRING.fieldOf("defaultValue").forGetter(get -> get.defaultValue),
                            PrimitiveCodec.STRING.fieldOf("value").forGetter(get -> get.value),
                            PrimitiveCodec.STRING.fieldOf("previousValue").forGetter(get -> get.previousValue),
                            PrimitiveCodec.STRING.fieldOf("comment").forGetter(get -> get.comment),
                            PrimitiveCodec.STRING.fieldOf("prettyName").forGetter(get -> get.prettyName),
                            PrimitiveCodec.STRING.fieldOf("translatedName").forGetter(get -> get.translatedName)
                    )
                    .apply(instance, ConfigString::new)
    );
    private final String defaultValue;
    private String value;
    private String previousValue;

    public ConfigString(String name, String defaultValue)
    {
        this(name, defaultValue, name+" Comment?", StringUtils.splitCamelCase(name), name);
    }

    public ConfigString(String name, String defaultValue, String comment)
    {
        this(name, defaultValue, comment, StringUtils.splitCamelCase(name), name);
    }

    public ConfigString(String name, String defaultValue, String comment, String prettyName)
    {
        this(name, defaultValue, comment, prettyName, name);
    }

    public ConfigString(String name, String defaultValue, String comment, String prettyName, String translatedName)
    {
        super(ConfigType.STRING, name, comment, prettyName, translatedName);

        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.updateLastStringValue();
    }

    private ConfigString(String name, String defaultValue, String value, String previousValue, String comment, String prettyName, String translatedName)
    {
        this(name, defaultValue, comment, prettyName, translatedName);
        this.value = value;
        this.updateLastStringValue();
    }

    @Override
    public String getStringValue()
    {
        return this.value;
    }

    @Override
    public String getDefaultStringValue()
    {
        return this.defaultValue;
    }

    @Override
    public String getLastStringValue()
    {
        return this.previousValue;
    }

    @Override
    public void setStringValue(String value)
    {
        this.setValueFromString(value);
    }

    @Override
    public void setValueFromString(String value)
    {
        this.updateLastStringValue();
        this.value = value;

        if (this.previousValue.equals(this.value) == false)
        {
            this.onValueChanged();
        }
    }

    @Override
    public void updateLastStringValue()
    {
        this.previousValue = this.value;
    }

    @Override
    public void resetToDefault()
    {
        this.setValueFromString(this.defaultValue);
    }

    @Override
    public boolean isModified()
    {
        return this.value.equals(this.defaultValue) == false;
    }

    @Override
    public boolean isModified(String newValue)
    {
        return this.defaultValue.equals(newValue) == false;
    }

    @Override
    public void setValueFromJsonElement(JsonElement element)
    {
        final String oldValue = this.value;

        try
        {
            if (element.isJsonPrimitive())
            {
                String temp = element.getAsString();
                this.value = temp != null ? temp : this.defaultValue;
            }
            else
            {
                MaLiLib.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", this.getName(), element);
            }

            if (!this.value.equals(oldValue) || this.isDirty())
            {
                this.markClean();

                if (!Objects.equals(this.getLastStringValue(), this.getStringValue()))
                {
//                    MaLiLib.LOGGER.error("[STRING/{}]: setValueFromJsonElement(): LV: [{}], OV: [{}], NV: [{}]", this.getName(),
//                                         this.getLastStringValue(), oldValue, this.getStringValue()
//                    );

                    this.onValueChanged();
                }
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
    
    @Override public void addConfigOption(int x, int y, int labelWidth, int configWidth, WidgetConfigOption configOption) {
        
        final ConfigType type = ConfigType.STRING;
        
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
		
		TextFieldType textType = TextFieldType.STRING.setMaxLength(configOption.maxTextfieldTextLength);
		
		configOption.addConfigTextFieldEntry(x, y, resetX, configWidth, configHeight, this, textType);
		
		if (this instanceof IConfigSlider)
		{
			IGuiIcon icon = ((IConfigSlider) this).shouldUseSlider() ? MaLiLibIcons.BTN_TXTFIELD : MaLiLibIcons.BTN_SLIDER;
			ButtonGeneric toggleBtn = new ButtonGeneric(configOption.colorDisplayPosX, y + 2, icon);
			configOption.addButton(toggleBtn, new WidgetConfigOption.ListenerSliderToggle((IConfigSlider) this));
		}
	}
}
