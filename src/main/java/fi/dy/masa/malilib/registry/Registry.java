package fi.dy.masa.malilib.registry;

import fi.dy.masa.malilib.gui.config.registry.ConfigScreenRegistry;
import fi.dy.masa.malilib.interoperation.BlockPlacementPositionHandler;
import fi.dy.masa.malilib.util.data_syncer.EntityDataRegistry;
import fi.dy.masa.malilib.util.i18n.i18nRegistry;

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

    public static final i18nRegistry TRANSLATION_OVERRIDE_MANAGER = new i18nRegistry();
    public static final EntityDataRegistry ENTITY_DATA_REGISTRY = new EntityDataRegistry();
}
