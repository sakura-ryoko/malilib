package fi.dy.masa.malilib.render;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.world.World;

@ApiStatus.Experimental
public interface InventoryOverlayRefresher
{
	InventoryOverlayContext onContextRefresh(InventoryOverlayContext data, World world);
}
