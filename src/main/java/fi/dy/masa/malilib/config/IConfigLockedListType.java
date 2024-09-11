package fi.dy.masa.malilib.config;

import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;

public interface IConfigLockedListType
{
    ImmutableList<IConfigLockedListEntry> getDefaultEntries();

    @Nullable IConfigLockedListEntry fromString(String string);
}
