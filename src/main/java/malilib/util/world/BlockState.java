package malilib.util.world;

import java.util.Optional;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;

import malilib.util.data.Constants;
import malilib.util.data.Identifier;
import malilib.util.data.tag.CompoundData;
import malilib.util.game.BlockUtils;
import malilib.util.game.MinecraftVersion;
import malilib.util.game.wrap.RegistryUtils;
import malilib.util.position.BlockMirror;
import malilib.util.position.BlockRotation;

public class BlockState
{
    private static final Object2ObjectOpenHashMap<IBlockState, BlockState> MAP_BY_VANILLA_STATE = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<String, BlockState> MAP_BY_STATE_STRING = new Object2ObjectOpenHashMap<>();

    private static IBlockState fallbackState = Blocks.DIAMOND_BLOCK.getDefaultState();

    protected final IBlockState vanillaState;
    protected final String registryName;
    protected final String fullStateString;
    protected final int dataVersion;
    protected final MinecraftVersion mcVersion;
    protected final int blockId;
    protected final boolean valid;

    protected BlockState(String registryName, String fullStateString, int dataVersion, IBlockState vanillaState, int blockId)
    {
        this.vanillaState = vanillaState;
        this.registryName = registryName;
        this.fullStateString = fullStateString;
        this.dataVersion = dataVersion;
        this.mcVersion = MinecraftVersion.getOrCreateVersionFromDataVersion(dataVersion);
        this.blockId = blockId;
        this.valid = true;
    }

    protected BlockState(String registryName, String fullStateString, int dataVersion)
    {
        this.vanillaState = Blocks.AIR.getDefaultState();
        this.registryName = registryName;
        this.fullStateString = fullStateString;
        this.dataVersion = dataVersion;
        this.mcVersion = MinecraftVersion.getOrCreateVersionFromDataVersion(dataVersion);
        this.blockId = Block.REGISTRY.getIDForObject(Blocks.AIR);
        this.valid = false;
    }

    public IBlockState vanillaState()
    {
        return this.valid ? this.vanillaState : fallbackState;
    }

    public Block getBlock()
    {
        return this.vanillaState().getBlock();
    }

    public String getRegistryName()
    {
        return this.registryName;
    }

    public String getRegistryNameStr()
    {
        return this.registryName.toString();
    }

    public String getFullStateString()
    {
        return this.fullStateString;
    }

    public int getDataVersion()
    {
        return this.dataVersion;
    }

    public MinecraftVersion getMcVersion()
    {
        return this.mcVersion;
    }

    // TODO This needs to go away for 1.13+ compatibility
    public int getOldBlockId()
    {
        return this.valid ? this.blockId : AIR.getOldBlockId();
    }

    public boolean isValid()
    {
        return this.valid;
    }

    public boolean isAirMaterial()
    {
        return this.vanillaState.getMaterial() == Material.AIR;
    }

    public BlockState withRotation(BlockRotation rotation)
    {
        return BlockState.of(this.vanillaState().withRotation(rotation.getVanillaRotation()));
    }

    public BlockState withMirror(BlockMirror mirror)
    {
        return BlockState.of(this.vanillaState().withMirror(mirror.getVanillaMirror()));
    }

    public <T extends Comparable<T>> T getValue(IProperty<T> property)
    {
        return this.vanillaState().getValue(property);
    }

    public CompoundData serialize()
    {
        return BlockUtils.writeBlockState(new CompoundData(), this);
    }

    public static void setFallbackState(IBlockState state)
    {
        if (state != null)
        {
            fallbackState = state;
        }
    }

    public static BlockState ofData(CompoundData data, int dataVersion)
    {
        if (data.contains("Name", Constants.NBT.TAG_STRING) == false)
        {
            return BlockState.AIR;
        }

        String registryName = data.getString("Name");

        return BlockState.of(registryName, BlockUtils.blockStateDataToString(data), dataVersion);
    }

    public static BlockState of(String fullStateString, int dataVersion)
    {
        int index = fullStateString.indexOf("["); // mod:block[prop=value,foo=bar]
        String blockName = index != -1 ? fullStateString.substring(0, index) : fullStateString;

        return of(blockName, fullStateString, dataVersion);
    }

    public static BlockState of(String registryName, String fullStateString, int dataVersion)
    {
        BlockState state = MAP_BY_STATE_STRING.get(fullStateString);

        if (state == null)
        {
            if (Block.REGISTRY.containsKey(new Identifier(registryName)))
            {
                // TODO Track if all properties are read successfully, and if not, then use the fallback state?
                Optional<IBlockState> stateOpt = BlockUtils.getVanillaBlockStateFromString(fullStateString);

                if (stateOpt.isPresent())
                {
                    IBlockState vanillaState = stateOpt.get();
                    int blockId = Block.REGISTRY.getIDForObject(vanillaState.getBlock());
                    state = new BlockState(registryName, fullStateString, dataVersion, vanillaState, blockId);

                    MAP_BY_VANILLA_STATE.put(state.vanillaState(), state);
                }
            }

            if (state == null)
            {
                state = new BlockState(registryName, fullStateString, dataVersion);
            }

            MAP_BY_STATE_STRING.put(fullStateString, state);
        }

        return state;
    }

    public static BlockState of(IBlockState vanillaState)
    {
        BlockState state = MAP_BY_VANILLA_STATE.get(vanillaState);

        if (state == null)
        {
            String fullStateString = vanillaState.toString();
            state = BlockState.createState(fullStateString, vanillaState);
            MAP_BY_VANILLA_STATE.put(vanillaState, state);
            MAP_BY_STATE_STRING.put(fullStateString, state);
        }

        return state;
    }

    public static BlockState fromRegistryName(Identifier id)
    {
        return of(RegistryUtils.getBlockById(id).getDefaultState());
    }

    public static BlockState fromRegistryNameStr(String idStr)
    {
        return fromRegistryName(new Identifier(idStr));
    }

    protected static BlockState createState(String fullStateString, IBlockState vanillaState)
    {
        int index = fullStateString.indexOf("["); // mod:block[prop=value,foo=bar]
        String registryName = index > 0 ? fullStateString.substring(0, index) : fullStateString;
        ResourceLocation rl = RegistryUtils.getBlockId(vanillaState);
        int dataVersion = MinecraftVersion.CURRENT_VERSION.dataVersion;

        if (rl != null)
        {
            int blockId = Block.REGISTRY.getIDForObject(vanillaState.getBlock());

            return new BlockState(registryName, vanillaState.toString(), dataVersion, vanillaState, blockId);
        }

        return new BlockState(registryName, fullStateString, dataVersion);
    }

    public static final BlockState AIR = of(Blocks.AIR.getDefaultState());
}
