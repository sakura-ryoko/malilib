package fi.dy.masa.malilib.interoperation;

import javax.annotation.Nullable;

import net.minecraft.util.math.BlockPos;

/**
 * Post-ReWrite code
 */
public interface IBlockPlacementPositionProvider
{
    /**
     * Returns the block placement position that should be used
     * for the current situation.
     * This allows mods that want to modify the normal vanilla block placement
     * position to indicate that change to other mods that want to do something
     * with the placement position.
     * If the implementer does not currently want to modify the normal placement
     * position, then it should return null.
     * @return The overridden block placement position, or null if no changes should occur at the moment
     */
    @Nullable
    BlockPos getPlacementPosition();
}
