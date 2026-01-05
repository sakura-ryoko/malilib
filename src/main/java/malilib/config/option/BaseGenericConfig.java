package malilib.config.option;

import java.util.Objects;
import javax.annotation.Nullable;

import malilib.util.StringUtils;

public abstract class BaseGenericConfig<T> extends BaseConfigOption<T> implements OverridableConfig<T>
{
    protected final T defaultValue;
    protected T value;
    protected T effectiveValue;
    protected T lastSavedValue;
    protected T overrideValue;
    @Nullable protected String overrideMessage;
    protected boolean hasOverride;

    public BaseGenericConfig(String name, T defaultValue)
    {
        this(name, defaultValue, name, name, name);
    }

    public BaseGenericConfig(String name,
                             T defaultValue,
                             @Nullable String commentTranslationKey, Object... commentArgs)
    {
        this(name, defaultValue, name, name, commentTranslationKey, commentArgs);
    }

    public BaseGenericConfig(String name,
                             T defaultValue,
                             String nameTranslationKey,
                             String prettyNameTranslationKey,
                             @Nullable String commentTranslationKey, Object... commentArgs)
    {
        super(name, nameTranslationKey, prettyNameTranslationKey, commentTranslationKey, commentArgs);

        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.effectiveValue = defaultValue;
        this.lastSavedValue = defaultValue;
    }

    @Override
    public T getValue()
    {
        return this.effectiveValue;
    }

    public T getDefaultValue()
    {
        return this.defaultValue;
    }

    public boolean setValue(T newValue)
    {
        if (this.isLocked() == false && Objects.equals(this.value, newValue) == false)
        {
            this.value = newValue;
            this.updateEffectiveValueAndNotify();
            return true;
        }

        return false;
    }

    protected void updateEffectiveValue()
    {
        this.effectiveValue = this.hasOverride() ? this.overrideValue : this.value;
    }

    protected void updateEffectiveValueAndNotify()
    {
        T oldValue = this.getValue();

        this.updateEffectiveValue();

        // Re-fetch the current value, to take into account a possible value override
        T newValue = this.getValue();

        if (Objects.equals(oldValue, newValue) == false)
        {
            this.onValueChanged(newValue, oldValue);
        }
    }

    @Override
    public boolean isLocked()
    {
        return super.isLocked() || this.hasOverride();
    }

    @Override
    public boolean hasOverride()
    {
        return this.hasOverride;
    }

    @Override
    public void enableOverrideWithValue(T overrideValue)
    {
        this.hasOverride = true;
        this.overrideValue = overrideValue;
        this.updateEffectiveValueAndNotify();
        this.rebuildLockOverrideMessages();
    }

    @Override
    public void disableOverride()
    {
        this.hasOverride = false;
        this.updateEffectiveValueAndNotify();
        this.rebuildLockOverrideMessages();
    }

    @Override
    public void setOverrideMessage(@Nullable String translationKey)
    {
        this.overrideMessage = translationKey;
        this.rebuildLockOverrideMessages();
    }

    @Override
    protected void rebuildLockOverrideMessages()
    {
        super.rebuildLockOverrideMessages();

        if (this.hasOverride() && this.overrideMessage != null)
        {
            StringUtils.translateAndLineSplit(this.lockOverrideMessages::add, this.overrideMessage);
        }
    }

    @Override
    public boolean isModified()
    {
        return Objects.equals(this.value, this.defaultValue) == false;
    }

    @Override
    public boolean isDirty()
    {
        return Objects.equals(this.value, this.lastSavedValue) == false;
    }

    @Override
    public void cacheSavedValue()
    {
        this.lastSavedValue = this.value;
    }

    @Override
    public void resetToDefault()
    {
        this.setValue(this.defaultValue);
    }

    /**
     * Sets the value of this config option, when loaded from a config file.
     * This should set the value without calling the ValueChangeCallback,
     * but instead call the ValueLoadCallback and cache the current value
     * for the dirty checks by the {@link ConfigOption#isDirty()} method later on.
     * @param value the value being loaded
     */
    public void loadValue(T value)
    {
        this.value = value;
        this.cacheSavedValue();
        this.updateEffectiveValue();
        this.onValueLoaded(value);
    }

    /**
     * @return the actual user-set underlying value, used for config serialization to file.
     * This is needed if there are active config overrides, as the normal {@link #getValue()}
     * method will then return the override value.
     */
    public T getValueForSerialization()
    {
        return this.value;
    }
}
