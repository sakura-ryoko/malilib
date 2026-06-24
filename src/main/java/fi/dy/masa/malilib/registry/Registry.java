package fi.dy.masa.malilib.registry;

import fi.dy.masa.malilib.gui.config.registry.ConfigScreenRegistry;
import fi.dy.masa.malilib.interoperation.BlockPlacementPositionHandler;

/**
 * Post-ReWrite code
 */
public class Registry
{
    // Registries
    //public static final ConfigTabRegistry CONFIG_TAB = new ConfigTabRegistryImpl();
    //public static final ConfigTabExtensionRegistry CONFIG_TAB_EXTENSION = new ConfigTabExtensionRegistry();

    // Event dispatchers and handlers
    public static final BlockPlacementPositionHandler BLOCK_PLACEMENT_POSITION_HANDLER = new BlockPlacementPositionHandler();
    public static final ConfigScreenRegistry CONFIG_SCREEN = new ConfigScreenRegistry();
}
