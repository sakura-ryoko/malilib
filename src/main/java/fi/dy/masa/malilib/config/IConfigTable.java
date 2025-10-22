package fi.dy.masa.malilib.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.options.table.TableRow;
import fi.dy.masa.malilib.config.options.table.type.EntryTypes;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IConfigTable extends IConfigBase {
    List<TableRow> getTable();
    List<List<Object>> getRawTable();

    ImmutableList<TableRow> getDefaultTable();
    ImmutableList<List<Object>> getDefaultRawTable();

    void setTable(List<TableRow> newTable);

    void setModified();

    @Nullable String getDisplayString();

    List<EntryTypes> getTypes();
    List<String> getLabels();
    boolean allowNewEntry();
    boolean showEntryNumbers();
}
