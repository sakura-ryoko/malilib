package fi.dy.masa.malilib.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.options.ConfigTable;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IConfigTable extends IConfigBase {
    List<ConfigTable.Entry> getTable();
    List<List<Object>> getRawTable();

    ImmutableList<ConfigTable.Entry> getDefaultTable();
    ImmutableList<List<Object>> getDefaultRawTable();

    void setTable(List<ConfigTable.Entry> newTable);

    void setModified();

    @Nullable String getDisplayString();

    List<Class<?>> getTypes();
    List<String> getLabels();
    boolean allowNewEntry();
    boolean showEntryNumbers();
}
