package fi.dy.masa.malilib.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.util.data.Color4f;

import java.util.List;

public interface IConfigColorList extends IConfigBase
{
    List<Color4f> getColors();

    ImmutableList<Color4f> getDefaultColors();

    void setColors(List<Color4f> colors);

    void setModified();
}
