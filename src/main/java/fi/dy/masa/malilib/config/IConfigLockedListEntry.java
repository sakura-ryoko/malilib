package fi.dy.masa.malilib.config;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.serialization.Codec;

public interface IConfigLockedListEntry
{
    @ApiStatus.Experimental
    Codec<? extends IConfigLockedListEntry> codec();

    static IConfigLockedListEntry empty() { return null; }

    String getStringValue();

    String getDisplayName();
}
