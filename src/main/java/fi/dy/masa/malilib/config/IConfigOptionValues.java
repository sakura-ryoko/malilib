package fi.dy.masa.malilib.config;

import java.util.Collection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import fi.dy.masa.malilib.config.value.OptionListConfigValue;

public interface IConfigOptionValues<T extends OptionListConfigValue> extends IConfigValue
{
	T getOptionValue();

	T getDefaultOptionValue();

	ImmutableList<T> getAllValues();

	ImmutableSet<T> getAllowedValues();

	void setAllowedValues(Collection<T> allowedValues);

	void addAllowedValues(Collection<T> newAllowedValues);

	void removeAllowedValues(Collection<T> nonAllowedValues);

	void setOptionValue(T value);

	void setOptionValueFromString(String value);

	void cycleValue(boolean reverse);

	void resetToDefault();

	default T getLastOptionValue() { return this.getDefaultOptionValue(); }

	default void updateLastOptionValue() {}
}
