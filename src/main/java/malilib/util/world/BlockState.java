package malilib.util.world;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;

import malilib.MaLiLibReference;
import malilib.util.data.Identifier;
import malilib.util.data.tag.CompoundData;
import malilib.util.game.BlockUtils;
import malilib.util.game.wrap.RegistryUtils;
import malilib.util.position.BlockMirror;
import malilib.util.position.BlockRotation;

public class BlockState
{
    public static final BlockState INVALID = new BlockState(Blocks.AIR.getDefaultState(),
                                                            new Identifier(MaLiLibReference.MOD_ID, "invalid_block"),
                                                            Block.REGISTRY.getIDForObject(Blocks.AIR));

    private static final Object2ObjectOpenHashMap<IBlockState, BlockState> MAP = new Object2ObjectOpenHashMap<>();

    public static BlockState of(IBlockState vanillaState)
    {
        return MAP.computeIfAbsent(vanillaState, BlockState::createState);
    }

    public static BlockState fromRegistryName(ResourceLocation id)
    {
        return of(RegistryUtils.getBlockById(id).getDefaultState());
    }

    public static BlockState fromRegistryNameStr(String idStr)
    {
        return of(RegistryUtils.getBlockById(new Identifier(idStr)).getDefaultState());
    }

    protected static BlockState createState(IBlockState vanillaState)
    {
        ResourceLocation rl = RegistryUtils.getBlockId(vanillaState);
        Identifier registryName;
        int blockId;

        if (rl != null)
        {
            registryName = new Identifier(rl);
            blockId = Block.REGISTRY.getIDForObject(vanillaState.getBlock());
        }
        else
        {
            registryName = new Identifier(MaLiLibReference.MOD_ID, "invalid_block");
            blockId = Block.REGISTRY.getIDForObject(Blocks.AIR);
        }

        return new BlockState(vanillaState, registryName, blockId);
    }

    protected final IBlockState vanillaState;
    protected final Identifier registryName;
    protected final int blockId;

    protected BlockState(IBlockState vanillaState, Identifier registryName, int blockId)
    {
        this.vanillaState = vanillaState;
        this.registryName = registryName;
        this.blockId = blockId;
    }

    protected BlockState()
    {
        this.vanillaState = Blocks.AIR.getDefaultState();
        this.registryName = new Identifier(MaLiLibReference.MOD_ID, "invalid_block");
        this.blockId = Block.REGISTRY.getIDForObject(this.vanillaState.getBlock());
    }

    public IBlockState vanillaState()
    {
        return this.vanillaState;
    }

    public Block getBlock()
    {
        return this.vanillaState.getBlock();
    }

    public Identifier getRegistryName()
    {
        return this.registryName;
    }

    public String getRegistryNameStr()
    {
        return this.registryName.toString();
    }

    // TODO This needs to go away for 1.13+ compatibility
    public int getOldBlockId()
    {
        return this.blockId;
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
