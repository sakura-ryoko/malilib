package fi.dy.masa.malilib.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.options.table.Label;
import fi.dy.masa.malilib.config.options.table.TableRow;
import fi.dy.masa.malilib.config.options.table.type.EntryTypes;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@ApiStatus.Experimental
public interface IConfigTable extends IConfigBase
{
    List<TableRow> getTable();
    List<List<Object>> getRawTable();

    // whats this 'Warning:(20, 19) Non-null type argument is expected' mean? it wasnt here before
    ImmutableList<TableRow> getDefaultTable();
    ImmutableList<List<Object>> getDefaultRawTable();

    void setTable(List<TableRow> newTable);

    void setModified();

    @Nullable String getDisplayString();

    List<EntryTypes> getTypes();
    List<Label> getLabels();
    boolean allowNewEntry();
    boolean showEntryNumbers();
}
