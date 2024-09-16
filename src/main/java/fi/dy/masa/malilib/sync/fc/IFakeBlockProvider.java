package fi.dy.masa.malilib.sync.fc;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import fi.dy.masa.malilib.sync.fbe.FakeBlockEntity;

public interface IFakeBlockProvider
{
    @Nullable
    FakeBlockEntity createBlockEntity(BlockPos pos, BlockState state);
}
