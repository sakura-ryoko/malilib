package fi.dy.masa.malilib.config;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.serialization.Codec;

public interface IConfigOptionListEntry
{
    @ApiStatus.Experimental
    default Codec<? extends IConfigOptionListEntry> codec() { return null; }

    String getStringValue();

    String getDisplayName();

    IConfigOptionListEntry cycle(boolean forward);

    IConfigOptionListEntry fromString(String value);
}
