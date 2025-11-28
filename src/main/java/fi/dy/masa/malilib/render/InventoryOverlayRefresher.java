package fi.dy.masa.malilib.render;

import net.minecraft.world.level.Level;

/**
 * Replaces the old / ugly method.
 */
public interface InventoryOverlayRefresher
{
	InventoryOverlayContext onContextRefresh(InventoryOverlayContext data, Level world);
}
