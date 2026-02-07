package malilib.config.option;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

import malilib.config.ValueChangeCallback;
import malilib.config.ValueLoadCallback;
import malilib.listener.EventListener;

public interface ConfigOption<T> extends ConfigInfo
{
    /**
     * 
     * Returns the "pretty name" for this config.
     * This is used in the possible toggle messages.
     * @return
     */
    default String getPrettyName()
    {
        return this.getDisplayName();
    }

    /**
     * @return the comment translation/localization key for this config
     */
    String getCommentTranslationKey();

    /**
     * Returns true if the value of this config has been changed since
     * it was last saved to file.
     * @return true if the config has been modified since last saving
     */
    boolean isDirty();

    /**
     * Cache the current value as the last saved value for the dirty checks
     */
    void cacheSavedValue();

    /**
     * Returns the value of this config.
     * Note that for primitive config types you should generally prefer the
     * type-specific methods to avoid boxing/unboxing.
     * This method is meant more for some of the config screen and other config
     * system related code, to make things a bit more generic there.
     * @return the current value of the config
     */
    T getValue();

    /**
     * This method is called when the config is loaded from file
     * @param newValue the new value that was just loaded
     */
    void onValueLoaded(T newValue);

    /**
     * Returns a list of old internal names this config used to go by, if any.
     * This allows reading the user-set values from config files that still use
     * the old name, if the config has since been renamed.
     * @return a list of old names to fall back to if the current name does not exist in the config file
     */
    default List<String> getOldNames()
    {
        return Collections.emptyList();
    }

    /**
     * Set the value change callback. Can be null.
     * @param callback
     */
    void setValueChangeCallback(@Nullable ValueChangeCallback<T> callback);

    /**
     * Set the value load callback. Can be null.
     * @param callback
     */
    void setValueLoadCallback(@Nullable ValueLoadCallback<T> callback);

    /**
     * Adds a simple value change listener, which does not get the current value as an argument.
     * There can be multiple listeners added simultaneously.
     * @param listener the listener to add to the list
     */
    void addValueChangeListener(EventListener listener);

    /**
     * Whether or not this config is currently locked to its current value,
     * and can not be changed without unlocking.
     * @return true if the config is locked
     */
    boolean isLocked();

    /**
     * Set whether or not this config should be locked to its current value.
     * Note: This does not prevent the methods that load the values from configs
     * from changing the value! It only prevents the normal setValue() methods
     * from changing it.
     * @param isLocked
     */
    void setLocked(boolean isLocked);
}
