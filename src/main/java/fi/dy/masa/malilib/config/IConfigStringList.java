package fi.dy.masa.malilib.config;

import java.util.List;
import com.google.common.collect.ImmutableList;

public interface IConfigStringList extends IConfigBase
{
    List<String> getStrings();

    ImmutableList<String> getDefaultStrings();

    void setStrings(List<String> strings);

    void setModified();
}
