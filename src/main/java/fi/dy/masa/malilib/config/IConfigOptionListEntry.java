package fi.dy.masa.malilib.config;

import java.util.List;

public interface IConfigOptionListEntry
{
    String getStringValue();

    String getDisplayName();

    default List<String> getHoverText() { return List.of(); }

    IConfigOptionListEntry cycle(boolean forward);

    IConfigOptionListEntry fromString(String value);

    static IConfigOptionListEntry empty() { return null; }
}
