package fi.dy.masa.malilib.config;

import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.ApiStatus;

import com.mojang.serialization.Codec;

public interface IConfigLockedListType
{
    @ApiStatus.Experimental
    default Codec<? extends IConfigLockedListType> codec() { return null; }

    ImmutableList<IConfigLockedListEntry> getDefaultEntries();

    @Nullable IConfigLockedListEntry fromString(String string);
}
