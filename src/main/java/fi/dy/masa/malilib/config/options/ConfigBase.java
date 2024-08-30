package fi.dy.masa.malilib.config.options;

import javax.annotation.Nullable;

import fi.dy.masa.malilib.MaLiLib;
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
    private String prettyName;
    private String comment;
    private String translatedName;
    private String translationPrefix = "";
    @Nullable
    private IValueChangeCallback<T> callback;

    public static final String COMMENT_KEY = "comment";
    public static final String PRETTY_NAME_KEY = "prettyName";
    public static final String TRANSLATED_NAME_KEY = "name";

    public ConfigBase(ConfigType type, String name)
    {
        this(type, name,
                name+" Comment ?",
                StringUtils.splitCamelCase(name),
                name);
    }

    public ConfigBase(ConfigType type, String name, String comment)
    {
        this(type, name, comment, StringUtils.splitCamelCase(name), name);
    }

    public ConfigBase(ConfigType type, String name, String comment, String prettyName)
    {
        this(type, name, comment, prettyName, name);
    }

    public ConfigBase(ConfigType type, String name, String comment, String prettyName, String translatedName)
    {
        this.type = type;
        this.name = name;
        this.comment = comment;
        this.prettyName = prettyName;
        this.translatedName = translatedName;
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
        if (this.prettyName.isEmpty())
        {
            return StringUtils.splitCamelCase(this.getName());
        }
        if (this.translationPrefix.isEmpty())
        {
            return this.prettyName;
        }

        return StringUtils.getTranslatedOrFallback(this.prettyName, this.prettyName);
    }

    @Override
    @Nullable
    public String getComment()
    {
        if (this.comment.isEmpty())
        {
            return StringUtils.splitCamelCase(this.getName())+" Comment?";
        }
        if (this.translationPrefix.isEmpty())
        {
            return StringUtils.getTranslatedOrFallback("config.comment." + this.getName().toLowerCase(), this.comment);
        }

        return StringUtils.getTranslatedOrFallback(this.comment, this.comment);
    }

    @SuppressWarnings("unchecked")
    public T translatedName(String translatedName)
    {
        this.translatedName = translatedName;
        return (T) this;
    }

    /**
     * Apply i18n translations based on a prefix containing the MOD_ID
     * @param translationPrefix (Such as 'malilib.config')
     * @return (The i18n translation key version of this ConfigBase)
     */
    @SuppressWarnings("unchecked")
    public T apply(String translationPrefix)
    {
        if (translationPrefix.isEmpty() == false &&
            translationPrefix.contains(" ") == false &&
            translationPrefix.contains("."))
        {
            // Apply translation keys
            this.translationPrefix = translationPrefix;
            this.comment = translationPrefix + "." + COMMENT_KEY + "." + this.getCleanName();
            this.prettyName = translationPrefix + "." + PRETTY_NAME_KEY + "." + this.getCleanName();
            this.translatedName = translationPrefix + "." + TRANSLATED_NAME_KEY + "." + this.getCleanName();
        }
        else
        {
            this.translationPrefix = "";
            MaLiLib.logger.error("ConfigBase: Failed to apply Translations Prefix for config named [{}].", this.getName());
        }

        return (T) this;
    }

    @Override
    @Nullable
    public String getTranslatedName()
    {
        if (this.translatedName.isEmpty())
        {
            return this.getPrettyName();
        }

        return this.translatedName;
    }

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

    @Override
    public String toString()
    {
        return "ConfigBase{type=['"+this.type.name()+"'], name=['"+this.name+"'],prettyName=['"+this.prettyName+"'], translatedName=['"+this.translatedName+"'], translationPrefix=['"+this.translationPrefix+"'],comment=['"+this.comment+"']";
    }
}
