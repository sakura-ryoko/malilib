package fi.dy.masa.malilib.config;

import java.util.List;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;

public interface IConfigLockedListType
{
    @Nullable IConfigLockedListEntry getEmpty();

    @Nullable IConfigLockedListEntry getEntry(String key);

    ImmutableList<IConfigLockedListEntry> getDefaultEntries();

    List<String> getConfigKeys(List<IConfigLockedListEntry> values);

    List<IConfigLockedListEntry> setEntries(List<IConfigLockedListEntry> entries);

    int getEntryIndex(List<IConfigLockedListEntry> entries, IConfigLockedListEntry entry);

    List<IConfigLockedListEntry> fromJsonArray(JsonArray array);

    void toJsonArray(List<IConfigLockedListEntry> values, JsonArray array);
}
