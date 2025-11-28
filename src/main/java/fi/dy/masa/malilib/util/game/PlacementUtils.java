package fi.dy.masa.malilib.util.game;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

/**
 * Post-ReWrite code
 */
@ApiStatus.Experimental
public class PlacementUtils
{
    /**
     * The checkMaterial flag is provided to deal with the vanilla inconsistency of checking the replaceability
     * of the block versus that of the material. ItemBlock offsets the position based on the replaceability
     * of the block, and then later checks if the block can be placed in the new offset position
     * based on the replaceability of the material instead. If <b>checkMaterial</b> is true, then the
     * replaceability of the material can override the non-replaceability of the block for the return value.
     */
    public static boolean isReplaceable(Level world, BlockPos pos, boolean checkMaterial)
    {
        BlockState state = world.getBlockState(pos);

        return state.canSurvive(world, pos) ||
                (checkMaterial && state.canBeReplaced());
    }
}
