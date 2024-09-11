package fi.dy.masa.malilib.config;

import java.util.List;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;

public interface IConfigLockedList extends IConfigBase
{
    ImmutableList<IConfigLockedListEntry> getDefaultEntries();

    List<IConfigLockedListEntry> getEntries();

    List<String> getConfigKeys();

    void setEntries(List<IConfigLockedListEntry> entries);

    @Nullable
    IConfigLockedListEntry getEmpty();

    @Nullable
    IConfigLockedListEntry getEntry(String key);

    int getEntryIndex(IConfigLockedListEntry entry);
}
