package malilib.util.world;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import net.minecraft.block.state.IBlockState;

import malilib.util.data.tag.CompoundData;
import malilib.util.game.BlockUtils;

public class BlockState
{
    private static final Object2ObjectOpenHashMap<IBlockState, BlockState> MAP = new Object2ObjectOpenHashMap<>();

    public static BlockState of(IBlockState vanillaState)
    {
        return MAP.computeIfAbsent(vanillaState, BlockState::new);
    }

    protected final IBlockState vanillaState;

    protected BlockState(IBlockState vanillaState)
    {
        this.vanillaState = vanillaState;
    }

    public IBlockState toVanilla()
    {
        return this.vanillaState;
    }

    public CompoundData serialize()
    {
        return BlockUtils.writeBlockState(new CompoundData(), this);
    }
}
