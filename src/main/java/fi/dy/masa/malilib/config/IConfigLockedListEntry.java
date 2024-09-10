package fi.dy.masa.malilib.config;

public interface IConfigLockedListEntry
{
    static IConfigLockedListEntry empty() { return null; }

    String getStringValue();

    String getDisplayName();
}
