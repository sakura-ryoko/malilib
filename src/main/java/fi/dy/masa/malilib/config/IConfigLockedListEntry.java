package fi.dy.masa.malilib.config;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.serialization.Codec;

public interface IConfigLockedListEntry
{
    @ApiStatus.Experimental
    default Codec<? extends IConfigLockedListEntry> codec() { return null; }

    static IConfigLockedListEntry empty() { return null; }

    String getStringValue();

    String getDisplayName();
}
