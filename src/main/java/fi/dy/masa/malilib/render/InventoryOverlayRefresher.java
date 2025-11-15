package fi.dy.masa.malilib.render;

import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;

public interface InventoryOverlayRefresher
{
	InventoryOverlayContext onContextRefresh(InventoryOverlayContext data, World world);
}
