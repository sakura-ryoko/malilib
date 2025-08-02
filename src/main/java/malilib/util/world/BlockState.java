package malilib.util.world;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

import malilib.util.data.tag.CompoundData;
import malilib.util.game.BlockUtils;
import malilib.util.position.BlockMirror;
import malilib.util.position.BlockRotation;

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

    public IBlockState vanillaState()
    {
        return this.vanillaState;
    }

    public Block getBlock()
    {
        return this.vanillaState.getBlock();
    }

    public BlockState withRotation(BlockRotation rotation)
    {
        return BlockState.of(this.vanillaState.withRotation(rotation.getVanillaRotation()));
    }

    public BlockState withMirror(BlockMirror mirror)
    {
        return BlockState.of(this.vanillaState.withMirror(mirror.getVanillaMirror()));
    }

    public <T extends Comparable<T>> T getValue(IProperty<T> property)
    {
        return this.vanillaState.getValue(property);
    }

    public CompoundData serialize()
    {
        return BlockUtils.writeBlockState(new CompoundData(), this);
    }

    public static final BlockState AIR = of(Blocks.AIR.getDefaultState());
}
