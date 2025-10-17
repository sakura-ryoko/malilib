package fi.dy.masa.malilib.config;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.Pair;

import java.util.List;

public interface IConfigStringMap extends IConfigBase
{
    List<Pair<String, String>> getMap();

    ImmutableList<Pair<String, String>> getDefaultMap();

    void setMap(List<Pair<String, String>> newMap);

    void setModified();

}
