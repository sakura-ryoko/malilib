package fi.dy.masa.malilib.render;

import javax.annotation.Nullable;
import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import fi.dy.masa.malilib.util.data.tag.CompoundData;

/**
 * Replaces the old / ugly method.
 */
public record InventoryOverlayContext(InventoryOverlayType type, @Nullable Container inv, @Nullable BlockEntity be, @Nullable LivingEntity entity, @Nullable CompoundData data, InventoryOverlayRefresher refresher)
{
}
