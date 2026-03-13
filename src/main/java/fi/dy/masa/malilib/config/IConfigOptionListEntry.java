package fi.dy.masa.malilib.config;

public interface IConfigOptionListEntry
{
    String getStringValue();

    String getDisplayName();

    IConfigOptionListEntry cycle(boolean forward);

    IConfigOptionListEntry fromString(String value);

    static IConfigOptionListEntry empty() { return null; }
}
