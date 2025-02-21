package fi.dy.masa.malilib.config;

import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.ApiStatus;

import com.mojang.serialization.Codec;

public interface IConfigLockedListType
{
    @ApiStatus.Experimental
    Codec<? extends IConfigLockedListType> codec();

    ImmutableList<IConfigLockedListEntry> getDefaultEntries();

    @Nullable IConfigLockedListEntry fromString(String string);
}
