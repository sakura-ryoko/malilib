package fi.dy.masa.malilib.interfaces;

import net.minecraft.world.level.block.state.BlockState;

public interface IBlockStateConsumer
{
    void setState(BlockState state);
}
