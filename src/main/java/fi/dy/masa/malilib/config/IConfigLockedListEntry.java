package fi.dy.masa.malilib.config;

public interface IConfigLockedListEntry
{
//    @ApiStatus.Experimental
//    default Codec<? extends IConfigLockedListEntry> codec() { return null; }

    static IConfigLockedListEntry empty() { return null; }

    String getStringValue();

    String getDisplayName();
}
