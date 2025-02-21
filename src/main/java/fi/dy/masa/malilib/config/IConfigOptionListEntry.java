package fi.dy.masa.malilib.config;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.serialization.Codec;

public interface IConfigOptionListEntry
{
    @ApiStatus.Experimental
    Codec<? extends IConfigOptionListEntry> codec();

    String getStringValue();

    String getDisplayName();

    IConfigOptionListEntry cycle(boolean forward);

    IConfigOptionListEntry fromString(String value);
}
