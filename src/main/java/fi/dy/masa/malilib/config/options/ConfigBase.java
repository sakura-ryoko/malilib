package fi.dy.masa.malilib.config.options;

import javax.annotation.Nullable;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.MaLiLibReference;
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
    protected String prettyName;
    protected String comment;
    protected String translatedName;
    private String translationPrefix = "";
    @Nullable
    private IValueChangeCallback<T> callback;
	protected boolean dirty = false;

    public static final String COMMENT_KEY = "comment";
    public static final String PRETTY_NAME_KEY = "prettyName";
    public static final String TRANSLATED_NAME_KEY = "name";

    // Boolean override debug toggle, because it's not smart to use the Config to debug the Config.
    private static final boolean CONFIG_TYPE_DEBUG = MaLiLibReference.DEBUG_MODE;

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

        if (CONFIG_TYPE_DEBUG)
        {
            MaLiLib.LOGGER.info("NEW CONFIG: [{}]", this.toString());
        }
    }

    public ConfigType getType()
    {
        return this.type;
    }

    public String getName()
    {
        return this.name;
    }

    public String getPrettyName()
    {
        String result;

        if (this.prettyName.isEmpty())
        {
            result = StringUtils.splitCamelCase(this.getName());
        }
        else if (this.prettyName.contains(PRETTY_NAME_KEY + ".") || this.translationPrefix.isEmpty())
        {
            result = StringUtils.getTranslatedOrFallback(this.prettyName, StringUtils.splitCamelCase(this.getName()));
        }
        else
        {
            result = StringUtils.getTranslatedOrFallback(this.prettyName, this.prettyName);
        }

        this.printConfigElementDebug(this.type, "prettyName", this.prettyName, result);
        return result;
    }

    @Nullable
    public String getComment()
    {
        String result;

        if (this.comment.isEmpty())
        {
            result = StringUtils.splitCamelCase(this.getName())+" Comment?";
        }
        else if (this.translationPrefix.isEmpty())
        {
            if (this.comment.contains(COMMENT_KEY + "."))
            {
                result = StringUtils.getTranslatedOrFallback(this.comment, StringUtils.splitCamelCase(this.getName())+" Comment?");
            }
            else
            {
                result = StringUtils.getTranslatedOrFallback("config.comment." + this.getName().toLowerCase(), this.comment);
            }
        }
        else
        {
            result = StringUtils.getTranslatedOrFallback(this.comment, this.comment);
        }

        this.printConfigElementDebug(this.type, "comment", this.comment, result);
        return result;
    }

    @SuppressWarnings("unchecked")
    public T translatedName(String translatedName)
    {
        this.printConfigElementDebug(this.type, "translatedName", this.translatedName, translatedName);
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
            MaLiLib.LOGGER.error("ConfigBase: Failed to apply Translations Prefix for config named [{}].", this.getName());
        }

        this.printConfigElementDebug(this.type, "apply", "", this.translationPrefix);
        return (T) this;
    }

    @Nullable
    public String getTranslatedName()
    {
        String result;

        if (this.translatedName.isEmpty())
        {
            result = this.getName();
        }
        else if (this.translationPrefix.isEmpty())
        {
            if (this.translatedName.contains(TRANSLATED_NAME_KEY + "."))
            {
                result = StringUtils.getTranslatedOrFallback(this.translatedName, this.getName());
            }
            else
            {
                result = this.translatedName;
            }
        }
        else
        {
            result = StringUtils.getTranslatedOrFallback(this.translatedName, this.translatedName);
        }

        this.printConfigElementDebug(this.type, "translatedName", this.translatedName, result);
        return result;
    }

    public void setPrettyName(String prettyName)
    {
        this.prettyName = prettyName;
    }

    public void setTranslatedName(String translatedName)
    {
        this.translatedName = translatedName;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public void setValueChangeCallback(IValueChangeCallback<T> callback)
    {
        this.callback = callback;
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
    public void onValueChanged()
    {
        if (CONFIG_TYPE_DEBUG || (MaLiLibConfigs.Debug.CONFIG_ELEMENT_DEBUG != null && MaLiLibConfigs.Debug.CONFIG_ELEMENT_DEBUG.getBooleanValue()))
        {
            MaLiLib.LOGGER.info("CONFIG: onValueChanged() -> type [{}], name [{}], hasCallback [{}]", type.name(), this.name, this.callback != null);
        }

        if (this.callback != null)
        {
            this.callback.onValueChanged((T) this);
        }
    }

    protected void printConfigElementDebug(ConfigType type, String element, String oldStr, String newStr)
    {
        if (CONFIG_TYPE_DEBUG || (MaLiLibConfigs.Debug.CONFIG_ELEMENT_DEBUG != null && MaLiLibConfigs.Debug.CONFIG_ELEMENT_DEBUG.getBooleanValue()))
        {
            MaLiLib.LOGGER.info("CONFIG: type [{}], element [{}], oldStr [{}], newStr [{}]", type != null ? type.name() : "<NUL>", element, oldStr, newStr);
        }
    }

    @Override
    public String toString()
    {
        return "ConfigBase{type=['"+this.type != null ? this.type.name() : "<NUL>"+"'],"+
		        "name=['"+this.name+"'],"+
		        "prettyName=['"+this.prettyName+"'],"+
		        "translatedName=['"+this.translatedName+"'],"+
		        "translationPrefix=['"+this.translationPrefix+"'],"+
		        "comment=['"+this.comment+"']";
    }
}
