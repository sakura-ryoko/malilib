package fi.dy.masa.malilib.render;

import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.Inventory;
import org.jetbrains.annotations.ApiStatus;
import fi.dy.masa.malilib.util.data.tag.CompoundData;

@ApiStatus.Experimental
public record InventoryOverlayContext(InventoryOverlayType type, @Nullable Inventory inv, @Nullable BlockEntity be, @Nullable LivingEntity entity, @Nullable CompoundData data, InventoryOverlayRefresher refresher)
{
}
