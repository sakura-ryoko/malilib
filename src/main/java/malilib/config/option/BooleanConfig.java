package malilib.config.option;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

import malilib.listener.EventListener;

public class BooleanConfig extends BaseGenericConfig<Boolean> implements BooleanContainingConfig<Boolean>
{
    protected final List<EventListener> enableListeners = new ArrayList<>(0);
    protected boolean effectiveBooleanValue;

    public BooleanConfig(String name, boolean defaultValue)
    {
        this(name, defaultValue, name);
    }

    public BooleanConfig(String name, boolean defaultValue,
                         @Nullable String commentTranslationKey, Object... commentArgs)
    {
        this(name, defaultValue, name, commentTranslationKey, commentArgs);
    }

    public BooleanConfig(String name, boolean defaultValue, String prettyNameTranslationKey,
                         @Nullable String commentTranslationKey, Object... commentArgs)
    {
        super(name, defaultValue, name, prettyNameTranslationKey, commentTranslationKey, commentArgs);

        this.updateEffectiveValue();
    }

    @Override
    public boolean getBooleanValue()
    {
        return this.effectiveBooleanValue;
    }

    @Override
    public boolean getDefaultBooleanValue()
    {
        return this.defaultValue;
    }

    @Override
    public boolean setBooleanValue(boolean newValue)
    {
        if (this.isLocked() == false && newValue != this.value)
        {
            super.setValue(newValue);
            return true;
        }

        return false;
    }

    @Override
    public boolean toggleBooleanValue()
    {
        return this.setBooleanValue(! this.value);
    }

    @Override
    protected void updateEffectiveValue()
    {
        super.updateEffectiveValue();
        this.effectiveBooleanValue = this.effectiveValue;
    }

    @Override
    public void onValueChanged(Boolean newValue, Boolean oldValue)
    {
        super.onValueChanged(newValue, oldValue);

        if (newValue)
        {
            for (EventListener listener : this.enableListeners)
            {
                listener.onEvent();
            }
        }
    }

    /**
     * Adds a listener that will get called when the value changes to true.
     * Note that these are not called when the value is deserialized from a config file.
     */
    public void addEnableListener(EventListener listener)
    {
        this.enableListeners.add(listener);
    }
}
