package malilib.config.value;

import java.util.List;
import javax.annotation.Nullable;

import malilib.util.StringUtils;

public class BaseOptionConfigValue implements OptionConfigValue
{
    protected final String name;
    protected final String translationKey;

    public BaseOptionConfigValue(String name, String translationKey)
    {
        this.name = name;
        this.translationKey = translationKey;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public String getDisplayName()
    {
        return StringUtils.translate(this.translationKey);
    }

    @Override
    public String toString()
    {
        return this.getName();
    }

    /**
     * Finds the value by the given name from the provided list.
     * If none of the entries match, then the first entry is returned as a fallback.
     */
    public static <T extends OptionConfigValue> T findValueByName(String name, List<T> values)
    {
        return findValueByName(name, values, values.get(0));
    }

    /**
     * Finds the value by the given name from the provided list.
     * If none of the entries match, then the fallback value is returned.
     */
    @Nullable
    public static <T extends OptionConfigValue> T findValueByName(String name, List<T> values, @Nullable T fallback)
    {
        for (T val : values)
        {
            if (val.getName().equalsIgnoreCase(name))
            {
                return val;
            }
        }

        return fallback;
    }
}
