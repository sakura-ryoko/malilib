package fi.dy.masa.malilib.config.options;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigNotifiable;
import fi.dy.masa.malilib.config.IConfigResettable;
import fi.dy.masa.malilib.interfaces.IValueChangeCallback;
import fi.dy.masa.malilib.util.StringUtils;

public abstract class ConfigBase<T extends IConfigBase> implements IConfigBase, IConfigResettable, IConfigNotifiable<T>
{
    private final ConfigType type;
    private final String name;
    private final String prettyName;
    private String comment;
    private String translatedName;
    @Nullable
    private IValueChangeCallback<T> callback;

    public ConfigBase(ConfigType type, String name, String comment)
    {
        this(type, name, comment, name);
    }

    public ConfigBase(ConfigType type, String name, String comment, String prettyName)
    {
        this.type = type;
        this.name = name;
        this.prettyName = prettyName;
        this.comment = comment;
        this.translatedName = name;
    }

    @Override
    public ConfigType getType()
    {
        return this.type;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public String getPrettyName()
    {
        return StringUtils.translate(this.prettyName);
    }

    @Override
    @Nullable
    public String getComment()
    {
        return StringUtils.getTranslatedOrFallback("config.comment." + this.getName().toLowerCase(), this.comment);
    }

    @SuppressWarnings("unchecked")
    public T translatedName(String translatedName)
    {
        this.translatedName = translatedName;
        return (T) this;
    }

    @Override
    @Nullable
    public String getTranslatedName()
    {
        return this.translatedName;
    }

    @Override
    public void setTranslatedName(String translatedName)
    {
        this.translatedName = translatedName;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    @Override
    public void setValueChangeCallback(IValueChangeCallback<T> callback)
    {
        this.callback = callback;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onValueChanged()
    {
        if (this.callback != null)
        {
            this.callback.onValueChanged((T) this);
        }
    }
}
