package fi.dy.masa.malilib.sync.fe;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

public interface IFakeDecoration
{
    Box calculateBoundingBox(BlockPos pos, Direction side);

    void onPlace();
}
