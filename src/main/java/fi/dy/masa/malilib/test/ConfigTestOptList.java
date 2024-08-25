package fi.dy.masa.malilib.test;

import com.google.common.collect.ImmutableList;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;

public enum ConfigTestOptList implements IConfigOptionListEntry
{
    TEST1 ("test1"),
    TEST2 ("test2");

    public static final ImmutableList<ConfigTestOptList> VALUES = ImmutableList.copyOf(values());

    private final String name;

    ConfigTestOptList(String name)
    {
        this.name = name;
    }

    @Override
    public String getStringValue()
    {
        return this.name;
    }

    @Override
    public String getDisplayName()
    {
        return this.getStringValue();
    }

    @Override
    public IConfigOptionListEntry cycle(boolean forward)
    {
        int id = this.ordinal();

        if (forward)
        {
            if (++id >= values().length)
            {
                id = 0;
            }
        }
        else
        {
            if (--id < 0)
            {
                id = values().length - 1;
            }
        }

        return values()[id % values().length];
    }

    @Override
    public ConfigTestOptList fromString(String value)
    {
        return fromStringStatic(value);
    }

    public static ConfigTestOptList fromStringStatic(String name)
    {
        for (ConfigTestOptList val : VALUES)
        {
            if (val.name.equalsIgnoreCase(name))
            {
                return val;
            }
        }

        return ConfigTestOptList.TEST1;
    }
}
