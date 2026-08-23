package fi.dy.masa.malilib.config.value;

import java.util.List;

/**
 * Post-ReWrite code
 */
public interface OptionListConfigValue
{
    String getName();

    String getDisplayName();

    default List<String> getHoverText() { return List.of(); }
}
