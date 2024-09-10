package fi.dy.masa.malilib.config;

import java.util.List;
import com.google.common.collect.ImmutableList;

public interface IConfigLockedList extends IConfigBase
{
    List<IConfigLockedListEntry> getEntries();

    List<String> getConfigKeys();

    ImmutableList<IConfigLockedListEntry> getDefaultEntries();

    void setEntries(List<IConfigLockedListEntry> entries);

    IConfigLockedListEntry getEmpty();

    IConfigLockedListEntry getEntry(String key);

    int getEntryIndex(IConfigLockedListEntry entry);
}
