package fi.dy.masa.malilib.config;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IConfigTable extends IConfigBase {
    List<List<Object>> getTable();

    ImmutableList<List<Object>> getDefaultTable();

    void setTable(List<List<Object>> newTable);

    void setModified();

    @Nullable String getDisplayString();

    List<Class<?>> getTypes();
    List<String> getLabels();
    boolean allowNewEntry();
    boolean showEntryNumbers();
}
