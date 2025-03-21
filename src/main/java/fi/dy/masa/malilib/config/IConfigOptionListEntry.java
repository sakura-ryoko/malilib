package fi.dy.masa.malilib.config;

public interface IConfigOptionListEntry
{
//    @ApiStatus.Experimental
//    Codec<? extends IConfigOptionListEntry> codec();

    String getStringValue();

    String getDisplayName();

    IConfigOptionListEntry cycle(boolean forward);

    IConfigOptionListEntry fromString(String value);

    static IConfigOptionListEntry empty() { return null; }
}
