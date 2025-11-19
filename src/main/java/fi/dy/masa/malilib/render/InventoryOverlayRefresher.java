package fi.dy.masa.malilib.render;

import net.minecraft.world.World;

/**
 * Replaces the old / ugly method.
 */
public interface InventoryOverlayRefresher
{
	InventoryOverlayContext onContextRefresh(InventoryOverlayContext data, World world);
}
