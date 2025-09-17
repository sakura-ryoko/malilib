package fi.dy.masa.malilib.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.*;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.interfaces.IValueChangeCallback;
import fi.dy.masa.malilib.util.data.Color4f;

public class ConfigTypeWrapper implements IConfigBoolean, IConfigColor, IConfigDouble, IConfigFloat, IConfigInteger, IConfigOptionList, IHotkey, IConfigNotifiable<IConfigBase>
{
    /*
    public static final Codec<ConfigTypeWrapper> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    ConfigType.CODEC.fieldOf("wrappedType").forGetter(get -> get.wrappedType),
                    Codec.RecursiveCodec.EMPTY.forGetter(get -> Unit.INSTANCE)
            ).apply(inst, ConfigTypeWrapper::new)
    );
     */
    private final ConfigType wrappedType;
    private final IConfigBase wrappedConfig;
    private boolean dirty = false;

    public ConfigTypeWrapper(ConfigType wrappedType, IConfigBase wrappedConfig)
    {
        this.wrappedType = wrappedType;
        this.wrappedConfig = wrappedConfig;
    }

    /*
    public ConfigTypeWrapper(ConfigType configType, Unit unit)
    {
        this(configType, Unit.valueOf(T.getClass))
    }
     */

    @Override
    public boolean shouldUseSlider()
    {
        if (this.wrappedConfig instanceof IConfigInteger)
        {
            return ((IConfigInteger) this.wrappedConfig).shouldUseSlider();
        }
        else if (this.wrappedConfig instanceof IConfigDouble)
        {
            return ((IConfigDouble) this.wrappedConfig).shouldUseSlider();
        }
        else if (this.wrappedConfig instanceof IConfigFloat)
        {
            return ((IConfigFloat) this.wrappedConfig).shouldUseSlider();
        }

        return false;
    }

    @Override
    public void toggleUseSlider()
    {
        if (this.wrappedConfig instanceof IConfigInteger)
        {
            ((IConfigInteger) this.wrappedConfig).toggleUseSlider();
        }
        else if (this.wrappedConfig instanceof IConfigDouble)
        {
            ((IConfigDouble) this.wrappedConfig).toggleUseSlider();
        }
        else if (this.wrappedConfig instanceof IConfigFloat)
        {
            ((IConfigFloat) this.wrappedConfig).toggleUseSlider();
        }
    }

    @Override
    public ConfigType getType()
    {
        return this.wrappedType;
    }

    @Override
    public String getName()
    {
        return this.wrappedConfig.getName();
    }

    @Override
    public String getLowerName()
    {
        return this.wrappedConfig.getLowerName();
    }

    @Override
    public String getComment()
    {
        return this.wrappedConfig.getComment();
    }

    @Override
    public String getPrettyName()
    {
        return this.wrappedConfig.getPrettyName();
    }

    @Override
    public String getConfigGuiDisplayName()
    {
        return this.wrappedConfig.getConfigGuiDisplayName();
    }

    @Override
    public String getTranslatedName()
    {
        return this.wrappedConfig.getTranslatedName();
    }

    @Override
    public void setPrettyName(String prettyName)
    {
        this.wrappedConfig.setPrettyName(prettyName);
    }

    @Override
    public void setTranslatedName(String translatedName)
    {
        this.wrappedConfig.setTranslatedName(translatedName);
    }

    @Override
    public void setComment(String comment)
    {
        this.wrappedConfig.setComment(comment);
    }

    @Override
    public boolean isDirty()
    {
        return this.dirty;
    }

    @Override
    public void markDirty()
    {
        this.dirty = true;
    }

    @Override
    public void markClean()
    {
        this.dirty = false;
    }

    @Override
    public void checkIfClean()
    {
        if (this.isDirty())
        {
            this.markClean();
            this.onValueChanged();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onValueChanged()
    {
        if (this.wrappedConfig instanceof IConfigNotifiable)
        {
            ((IConfigNotifiable<IConfigBase>) this.wrappedConfig).onValueChanged();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setValueChangeCallback(IValueChangeCallback<IConfigBase> callback)
    {
        if (this.wrappedConfig instanceof IConfigNotifiable)
        {
            ((IConfigNotifiable<IConfigBase>) this.wrappedConfig).setValueChangeCallback(callback);
        }
    }

    @Override
    public String getStringValue()
    {
        return switch (this.wrappedType)
        {
            case BOOLEAN -> String.valueOf(((IConfigBoolean) this.wrappedConfig).getBooleanValue());
            case DOUBLE -> String.valueOf(((IConfigDouble) this.wrappedConfig).getDoubleValue());
            case FLOAT -> String.valueOf(((IConfigFloat) this.wrappedConfig).getFloatValue());
            case INTEGER -> String.valueOf(((IConfigInteger) this.wrappedConfig).getIntegerValue());
            case COLOR -> String.format("#%08X", ((IConfigColor) this.wrappedConfig).getIntegerValue());
            case OPTION_LIST -> ((IConfigOptionList) this.wrappedConfig).getOptionListValue().getStringValue();
            case HOTKEY -> ((IHotkey) this.wrappedConfig).getKeybind().getStringValue();
            default -> ((IStringRepresentable) this.wrappedConfig).getStringValue();
        };
    }

    @Override
    public String getDefaultStringValue()
    {
        return switch (this.wrappedType)
        {
            case BOOLEAN -> String.valueOf(((IConfigBoolean) this.wrappedConfig).getDefaultBooleanValue());
            case DOUBLE -> String.valueOf(((IConfigDouble) this.wrappedConfig).getDefaultDoubleValue());
            case FLOAT -> String.valueOf(((IConfigFloat) this.wrappedConfig).getDefaultFloatValue());
            case INTEGER -> String.valueOf(((IConfigInteger) this.wrappedConfig).getDefaultIntegerValue());
            case COLOR -> String.format("#%08X", ((IConfigColor) this.wrappedConfig).getDefaultIntegerValue());
            case OPTION_LIST -> ((IConfigOptionList) this.wrappedConfig).getDefaultOptionListValue().getStringValue();
            case HOTKEY -> ((IHotkey) this.wrappedConfig).getKeybind().getDefaultStringValue();
            default -> ((IStringRepresentable) this.wrappedConfig).getDefaultStringValue();
        };
    }

    @Override
    public void setValueFromString(String value)
    {
        try
        {
            String oldValue = this.getStringValue();

            switch (this.wrappedType)
            {
                case HOTKEY:
                    ((IHotkey) this.wrappedConfig).getKeybind().setValueFromString(value);
                    break;
                case BOOLEAN:
                    ((IConfigBoolean) this.wrappedConfig).setBooleanValue(Boolean.parseBoolean(value));
                    break;
                case DOUBLE:
                    ((IConfigDouble) this.wrappedConfig).setDoubleValue(Double.parseDouble(value));
                    break;
                case FLOAT:
                    ((IConfigFloat) this.wrappedConfig).setFloatValue(Float.parseFloat(value));
                    break;
                case INTEGER:
                    ((IConfigInteger) this.wrappedConfig).setIntegerValue(Integer.parseInt(value));
                    break;
                case STRING:
                    ((IStringRepresentable) this.wrappedConfig).setValueFromString(value);
                    break;
                case COLOR:
                    ((IConfigColor) this.wrappedConfig).setValueFromString(value);
                    break;
                case OPTION_LIST:
                    IConfigOptionList option = (IConfigOptionList) this.wrappedConfig;
                    option.setOptionListValue(option.getOptionListValue().fromString(value));
                    break;
                default:
            }

            if (!oldValue.equals(this.getStringValue()))
            {
                this.onValueChanged();
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to set the config value for '{}' from string '{}'", this.getName(), value, e);
        }
    }

    @Override
    public boolean isModified()
    {
        return switch (this.wrappedType)
        {
            case HOTKEY -> ((IHotkey) this.wrappedConfig).getKeybind().isModified();
            case BOOLEAN ->
            {
                IConfigBoolean config = (IConfigBoolean) this.wrappedConfig;
                yield config.getBooleanValue() != config.getDefaultBooleanValue();
            }
            case DOUBLE ->
            {
                IConfigDouble config = (IConfigDouble) this.wrappedConfig;
                yield config.getDoubleValue() != config.getDefaultDoubleValue();
            }
            case FLOAT ->
            {
                IConfigFloat config = (IConfigFloat) this.wrappedConfig;
                yield config.getFloatValue() != config.getDefaultFloatValue();
            }
            case INTEGER ->
            {
                IConfigInteger config = (IConfigInteger) this.wrappedConfig;
                yield config.getIntegerValue() != config.getDefaultIntegerValue();
            }
            case COLOR ->
            {
                IConfigColor config = (IConfigColor) this.wrappedConfig;
                yield config.getIntegerValue() != config.getDefaultIntegerValue();
            }
            case OPTION_LIST ->
            {
                IConfigOptionList config = (IConfigOptionList) this.wrappedConfig;
                yield config.getOptionListValue() != config.getDefaultOptionListValue();
            }
            case STRING ->
            {
                IStringRepresentable config = (IStringRepresentable) this.wrappedConfig;
                yield config.getStringValue().equals(config.getDefaultStringValue()) == false;
            }
            default -> false;
        };
    }

    @Override
    public boolean isModified(String newValue)
    {
        return switch (this.wrappedType)
        {
            case HOTKEY -> ((IHotkey) this.wrappedConfig).getKeybind().isModified(newValue);
            case BOOLEAN -> String.valueOf(((IConfigBoolean) this.wrappedConfig).getBooleanValue()).equals(newValue) == false;
            case DOUBLE -> String.valueOf(((IConfigDouble) this.wrappedConfig).getDoubleValue()).equals(newValue) == false;
            case FLOAT -> String.valueOf(((IConfigFloat) this.wrappedConfig).getFloatValue()).equals(newValue) == false;
            case INTEGER -> String.valueOf(((IConfigInteger) this.wrappedConfig).getIntegerValue()).equals(newValue) == false;
            case COLOR -> ((ConfigColor) this.wrappedConfig).getStringValue().equals(newValue) == false;
            case OPTION_LIST -> ((IConfigOptionList) this.wrappedConfig).getOptionListValue().getStringValue().equals(newValue) == false;
            default -> ((IStringRepresentable) this.wrappedConfig).getStringValue().equals(newValue) == false;
        };
    }

    @Override
    public void resetToDefault()
    {
        try
        {
            String oldValue = this.getStringValue();

            switch (this.wrappedType)
            {
                case HOTKEY:
                    ((IHotkey) this.wrappedConfig).getKeybind().resetToDefault();
                    break;
                case BOOLEAN:
                {
                    IConfigBoolean config = (IConfigBoolean) this.wrappedConfig;
                    config.setBooleanValue(config.getDefaultBooleanValue());
                    break;
                }
                case DOUBLE:
                {
                    IConfigDouble config = (IConfigDouble) this.wrappedConfig;
                    config.setDoubleValue(config.getDefaultDoubleValue());
                    break;
                }
                case FLOAT:
                {
                    IConfigFloat config = (IConfigFloat) this.wrappedConfig;
                    config.setFloatValue(config.getDefaultFloatValue());
                    break;
                }
                case INTEGER:
                {
                    IConfigInteger config = (IConfigInteger) this.wrappedConfig;
                    config.setIntegerValue(config.getDefaultIntegerValue());
                    break;
                }
                case COLOR:
                {
                    IConfigColor config = (IConfigColor) this.wrappedConfig;
                    config.setIntegerValue(config.getDefaultIntegerValue());
                    break;
                }
                case OPTION_LIST:
                {
                    IConfigOptionList config = (IConfigOptionList) this.wrappedConfig;
                    config.setOptionListValue(config.getDefaultOptionListValue());
                    break;
                }
                case STRING:
                default:
                {
                    IStringRepresentable config = (IStringRepresentable) this.wrappedConfig;
                    config.setValueFromString(config.getDefaultStringValue());
                    break;
                }
            }

            if (!oldValue.equals(this.getStringValue()))
            {
                this.onValueChanged();
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to reset config value for {}", this.getName(), e);
        }
    }

    @Override
    public boolean getBooleanValue()
    {
        return this.wrappedType == ConfigType.BOOLEAN ? ((IConfigBoolean) this.wrappedConfig).getBooleanValue() : false;
    }

    @Override
    public boolean getDefaultBooleanValue()
    {
        return this.wrappedType == ConfigType.BOOLEAN ? ((IConfigBoolean) this.wrappedConfig).getDefaultBooleanValue() : false;
    }

    @Override
    public void setBooleanValue(boolean value)
    {
        boolean oldValue = this.getBooleanValue();

        if (this.wrappedType == ConfigType.BOOLEAN)
        {
            ((IConfigBoolean) this.wrappedConfig).setBooleanValue(value);
        }

        if (oldValue != this.getBooleanValue())
        {
            this.onValueChanged();
        }
    }

    @Override
    public Color4f getColor()
    {
        return this.wrappedType == ConfigType.COLOR ? ((IConfigColor) this.wrappedConfig).getColor() : Color4f.ZERO;
    }

    @Override
    public int getIntegerValue()
    {
        if (this.wrappedType == ConfigType.INTEGER)
        {
            return ((IConfigInteger) this.wrappedConfig).getIntegerValue();
        }
        else if (this.wrappedType == ConfigType.COLOR)
        {
            return ((IConfigColor) this.wrappedConfig).getIntegerValue();
        }

        return 0;
    }

    @Override
    public int getDefaultIntegerValue()
    {
        if (this.wrappedType == ConfigType.INTEGER)
        {
            return ((IConfigInteger) this.wrappedConfig).getDefaultIntegerValue();
        }
        else if (this.wrappedType == ConfigType.COLOR)
        {
            return ((IConfigColor) this.wrappedConfig).getDefaultIntegerValue();
        }

        return 0;
    }

    @Override
    public void setIntegerValue(int value)
    {
        int oldValue = this.getIntegerValue();

        if (this.wrappedType == ConfigType.INTEGER)
        {
            ((IConfigInteger) this.wrappedConfig).setIntegerValue(value);
        }
        else if (this.wrappedType == ConfigType.COLOR)
        {
            ((IConfigColor) this.wrappedConfig).setIntegerValue(value);
        }

        if (oldValue != this.getIntegerValue())
        {
            this.onValueChanged();
        }
    }

    @Override
    public int getMinIntegerValue()
    {
        if (this.wrappedType == ConfigType.INTEGER)
        {
            return ((IConfigInteger) this.wrappedConfig).getMinIntegerValue();
        }
        else if (this.wrappedType == ConfigType.COLOR)
        {
            return ((IConfigColor) this.wrappedConfig).getMinIntegerValue();
        }

        return 0;
    }

    @Override
    public int getMaxIntegerValue()
    {
        if (this.wrappedType == ConfigType.INTEGER)
        {
            return ((IConfigInteger) this.wrappedConfig).getMaxIntegerValue();
        }
        else if (this.wrappedType == ConfigType.COLOR)
        {
            return ((IConfigColor) this.wrappedConfig).getMaxIntegerValue();
        }

        return 0;
    }

    @Override
    public double getDoubleValue()
    {
        return this.wrappedType == ConfigType.DOUBLE ? ((IConfigDouble) this.wrappedConfig).getDoubleValue() : 0;
    }

    @Override
    public double getDefaultDoubleValue()
    {
        return this.wrappedType == ConfigType.DOUBLE ? ((IConfigDouble) this.wrappedConfig).getDefaultDoubleValue() : 0;
    }

    @Override
    public void setDoubleValue(double value)
    {
        double oldValue = this.getDoubleValue();

        if (this.wrappedType == ConfigType.DOUBLE)
        {
            ((IConfigDouble) this.wrappedConfig).setDoubleValue(value);
        }

        if (oldValue != this.getDoubleValue())
        {
            this.onValueChanged();
        }
    }

    @Override
    public double getMinDoubleValue()
    {
        return this.wrappedType == ConfigType.DOUBLE ? ((IConfigDouble) this.wrappedConfig).getMinDoubleValue() : 0;
    }

    @Override
    public double getMaxDoubleValue()
    {
        return this.wrappedType == ConfigType.DOUBLE ? ((IConfigDouble) this.wrappedConfig).getMaxDoubleValue() : 0;
    }

    @Override
    public float getFloatValue()
    {
        return this.wrappedType == ConfigType.FLOAT ? ((IConfigFloat) this.wrappedConfig).getFloatValue() : 0;
    }

    @Override
    public float getDefaultFloatValue()
    {
        return this.wrappedType == ConfigType.FLOAT ? ((IConfigFloat) this.wrappedConfig).getDefaultFloatValue() : 0;
    }

    @Override
    public void setFloatValue(float value)
    {
        float oldValue = this.getFloatValue();

        if (this.wrappedType == ConfigType.FLOAT)
        {
            ((IConfigFloat) this.wrappedConfig).setFloatValue(value);
        }

        if (oldValue != this.getFloatValue())
        {
            this.onValueChanged();
        }
    }

    @Override
    public float getMinFloatValue()
    {
        return this.wrappedType == ConfigType.FLOAT ? ((IConfigFloat) this.wrappedConfig).getMinFloatValue() : 0;
    }

    @Override
    public float getMaxFloatValue()
    {
        return this.wrappedType == ConfigType.FLOAT ? ((IConfigFloat) this.wrappedConfig).getMaxFloatValue() : 0;
    }

    @Override
    public IConfigOptionListEntry getOptionListValue()
    {
        return this.wrappedType == ConfigType.OPTION_LIST ? ((IConfigOptionList) this.wrappedConfig).getOptionListValue() : null;
    }

    @Override
    public IConfigOptionListEntry getDefaultOptionListValue()
    {
        return this.wrappedType == ConfigType.OPTION_LIST ? ((IConfigOptionList) this.wrappedConfig).getDefaultOptionListValue() : null;
    }

    @Override
    public void setOptionListValue(IConfigOptionListEntry value)
    {
        IConfigOptionListEntry oldValue = this.getOptionListValue();

        if (this.wrappedType == ConfigType.OPTION_LIST)
        {
            ((IConfigOptionList) this.wrappedConfig).setOptionListValue(value);
        }

        if (oldValue != this.getOptionListValue())
        {
            this.onValueChanged();
        }
    }

    @Override
    public IKeybind getKeybind()
    {
        return this.wrappedType == ConfigType.HOTKEY ? ((IHotkey) this.wrappedConfig).getKeybind() : null;
    }

    @Override
    public void setValueFromJsonElement(JsonElement element)
    {
        try
        {
            String oldValue = this.getStringValue();

            switch (this.wrappedType)
            {
                case BOOLEAN:
                    ((IConfigBoolean) this.wrappedConfig).setBooleanValue(element.getAsBoolean());
                    break;
                case DOUBLE:
                    ((IConfigDouble) this.wrappedConfig).setDoubleValue(element.getAsDouble());
                    break;
                case FLOAT:
                    ((IConfigFloat) this.wrappedConfig).setFloatValue(element.getAsFloat());
                    break;
                case INTEGER:
                    ((IConfigInteger) this.wrappedConfig).setIntegerValue(element.getAsInt());
                    break;
                case STRING:
                    ((IConfigValue) this.wrappedConfig).setValueFromString(element.getAsString());
                    break;
                case COLOR:
                    ((IConfigColor) this.wrappedConfig).setValueFromString(element.getAsString());
                    break;
                case OPTION_LIST:
                    IConfigOptionList option = (IConfigOptionList) this.wrappedConfig;
                    option.setOptionListValue(option.getOptionListValue().fromString(element.getAsString()));
                    break;
                case HOTKEY:
                    ((IHotkey) this.wrappedConfig).setValueFromJsonElement(element);
                    break;
                default:
            }

            if (!oldValue.equals(this.getStringValue()))
            {
                this.onValueChanged();
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to read config value for {} from the JSON config", this.getName(), e);
        }
    }

    @Override
    public JsonElement getAsJsonElement()
    {
        return switch (this.wrappedType)
        {
            case BOOLEAN -> new JsonPrimitive(((IConfigBoolean) this.wrappedConfig).getBooleanValue());
            case DOUBLE -> new JsonPrimitive(((IConfigDouble) this.wrappedConfig).getDoubleValue());
            case FLOAT -> new JsonPrimitive(((IConfigFloat) this.wrappedConfig).getFloatValue());
            case INTEGER -> new JsonPrimitive(((IConfigInteger) this.wrappedConfig).getIntegerValue());
            case STRING -> new JsonPrimitive(((IConfigValue) this.wrappedConfig).getStringValue());
            case COLOR -> new JsonPrimitive(((IConfigColor) this.wrappedConfig).getStringValue());
            case OPTION_LIST ->
                    new JsonPrimitive(((IConfigOptionList) this.wrappedConfig).getOptionListValue().getStringValue());
            case HOTKEY -> ((IHotkey) this.wrappedConfig).getAsJsonElement();
            default -> new JsonPrimitive(this.getStringValue());
        };
    }
}
