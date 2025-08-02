package malilib.util.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import javax.annotation.Nullable;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;

import malilib.MaLiLib;
import malilib.render.text.StyledTextLine;
import malilib.util.StringUtils;
import malilib.util.data.Constants;
import malilib.util.data.Identifier;
import malilib.util.data.tag.CompoundData;
import malilib.util.game.wrap.NbtWrap;
import malilib.util.game.wrap.RegistryUtils;
import malilib.util.position.Direction;
import malilib.util.world.BlockState;

public class BlockUtils
{
    private static final Splitter COMMA_SPLITTER = Splitter.on(',');
    private static final Splitter EQUAL_SPLITTER = Splitter.on('=').limit(2);

    /**
     * Parses the provided string into the full block state.<br>
     * The string should be in either one of the following formats:<br>
     * 'minecraft:stone' or 'minecraft:smooth_stone_slab[half=top,waterlogged=false]'
     */
    public static Optional<BlockState> getBlockStateFromString(String str)
    {
        int index = str.indexOf("["); // [prop=value]
        String blockName = index != -1 ? str.substring(0, index) : str;
        Identifier id = new Identifier(blockName);

        if (Block.REGISTRY.containsKey(id))
        {
            Block block = RegistryUtils.getBlockById(id);
            IBlockState state = block.getDefaultState();

            if (index != -1 && str.length() > (index + 4) && str.charAt(str.length() - 1) == ']')
            {
                BlockStateContainer blockState = block.getBlockState();
                String propStr = str.substring(index + 1, str.length() - 1);

                for (String propAndVal : COMMA_SPLITTER.split(propStr))
                {
                    Iterator<String> valIter = EQUAL_SPLITTER.split(propAndVal).iterator();

                    if (valIter.hasNext() == false)
                    {
                        continue;
                    }

                    IProperty<?> prop = blockState.getProperty(valIter.next());

                    if (prop == null || valIter.hasNext() == false)
                    {
                        continue;
                    }

                    Comparable<?> val = getPropertyValueByName(prop, valIter.next());

                    if (val != null)
                    {
                        state = getBlockStateWithProperty(state, prop, val);
                    }
                }
            }

            return Optional.of(BlockState.of(state));
        }

        return Optional.empty();
    }

    /**
     * Parses the provided string into a compound tag representing the block state.<br>
     * The tag is in the format that the vanilla util class uses for reading/writing states to NBT
     * data, for example in the Chunk block state palette.<br>
     * The string should be in either one of the following formats:<br>
     * 'minecraft:stone' or 'minecraft:smooth_stone_slab[half=top,waterlogged=false]'.<br>
     * None of the values are checked for validity here, and this can be used for
     * parsing strings for states from another Minecraft version, such as 1.12 <-> 1.13+.
     */
    public static NBTTagCompound getBlockStateTagFromString(String stateString)
    {
        int index = stateString.indexOf("["); // [f=b]
        String blockName = index != -1 ? stateString.substring(0, index) : stateString;
        NBTTagCompound tag = new NBTTagCompound();

        NbtWrap.putString(tag, "Name", blockName);

        if (index != -1 && stateString.length() > (index + 4) && stateString.charAt(stateString.length() - 1) == ']')
        {
            NBTTagCompound propsTag = new NBTTagCompound();
            String propStr = stateString.substring(index + 1, stateString.length() - 1);

            for (String propAndVal : COMMA_SPLITTER.split(propStr))
            {
                Iterator<String> valIter = EQUAL_SPLITTER.split(propAndVal).iterator();

                if (valIter.hasNext() == false)
                {
                    continue;
                }

                String propName = valIter.next();

                if (valIter.hasNext() == false)
                {
                    continue;
                }

                String valStr = valIter.next();

                NbtWrap.putString(propsTag, propName, valStr);
            }

            NbtWrap.putTag(tag, "Properties", propsTag);
        }

        return tag;
    }

    /**
     * Parses the input tag representing a block state, and produces a string
     * in the same format as the toString() method in the vanilla block state.
     * This string format is what the Sponge schematic format uses in the palette.
     * @return an equivalent of IBlockState.toString() of the given tag representing a block state
     */
    public static String getBlockStateStringFromTag(NBTTagCompound stateTag)
    {
        String name = NbtWrap.getString(stateTag, "Name");

        if (NbtWrap.containsCompound(stateTag, "Properties") == false)
        {
            return name;
        }

        NBTTagCompound propTag = NbtWrap.getCompound(stateTag, "Properties");
        ArrayList<Pair<String, String>> props = new ArrayList<>();

        for (String key : NbtWrap.getKeys(propTag))
        {
            props.add(Pair.of(key, NbtWrap.getString(propTag, key)));
        }

        final int size = props.size();

        if (size > 0)
        {
            props.sort(Comparator.comparing(Pair::getLeft));

            StringBuilder sb = new StringBuilder();
            sb.append(name).append('[');
            Pair<String, String> pair = props.get(0);

            sb.append(pair.getLeft()).append('=').append(pair.getRight());

            for (int i = 1; i < size; ++i)
            {
                pair = props.get(i);
                sb.append(',').append(pair.getLeft()).append('=').append(pair.getRight());
            }

            sb.append(']');

            return sb.toString();
        }

        return name;
    }

    public static BlockState readBlockState(CompoundData data)
    {
        if (data.contains("Name", Constants.NBT.TAG_STRING) == false)
        {
            return BlockState.AIR;
        }
        else
        {
            Block block = Block.REGISTRY.getObject(new Identifier(data.getString("Name")));
            IBlockState vanillaState = block.getDefaultState();

            if (data.contains("Properties", Constants.NBT.TAG_COMPOUND))
            {
                CompoundData propData = data.getCompound("Properties");
                BlockStateContainer blockStateContainer = block.getBlockState();

                for (String string : propData.getKeys())
                {
                    IProperty<?> prop = blockStateContainer.getProperty(string);

                    if (prop != null)
                    {
                        vanillaState = setValueHelper(vanillaState, prop, string, propData, data);
                    }
                }
            }

            return BlockState.of(vanillaState);
        }
    }

    private static <T extends Comparable<T>>
    IBlockState setValueHelper(IBlockState state,
                               IProperty<T> prop,
                               String valStr,
                               CompoundData propData,
                               CompoundData fullData)
    {
        com.google.common.base.Optional<T> optional = prop.parseValue(propData.getString(valStr));

        if (optional.isPresent())
        {
            return state.withProperty(prop, optional.get());
        }
        else
        {
            MaLiLib.LOGGER.warn("Unable to read property: '{}' with value: '{}' for block state: '{}'",
                                valStr, propData.getString(valStr), fullData.toString());
            return state;
        }
    }

    public static CompoundData writeBlockState(CompoundData data, BlockState state)
    {
        IBlockState vanillaState = state.vanillaState();

        data.putString("Name", Block.REGISTRY.getNameForObject(vanillaState.getBlock()).toString());

        if (state.vanillaState().getProperties().isEmpty() == false)
        {
            CompoundData propTag = new CompoundData();

            for (Entry<IProperty<?>, Comparable<?>> entry : vanillaState.getProperties().entrySet())
            {
                IProperty<?> prop = entry.getKey();
                propTag.putString(prop.getName(), getName(prop, entry.getValue()));
            }

            data.put("Properties", propTag);
        }

        return data;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> String getName(IProperty<T> prop, Comparable<?> comparable)
    {
        return prop.getName((T) comparable);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Comparable<T>> IBlockState getBlockStateWithProperty(IBlockState state, IProperty<T> prop, Comparable<?> value)
    {
        return state.withProperty(prop, (T) value);
    }

    @Nullable
    public static <T extends Comparable<T>> T getPropertyValueByName(IProperty<T> prop, String valStr)
    {
        return prop.parseValue(valStr).orNull();
    }

    /**
     * Returns the first PropertyDirection property from the provided state, if any.
     * @return the first PropertyDirection, or empty() if there are no such properties
     */
    public static Optional<PropertyDirection> getFirstDirectionProperty(IBlockState state)
    {
        for (IProperty<?> prop : state.getProperties().keySet())
        {
            if (prop instanceof PropertyDirection)
            {
                return Optional.of((PropertyDirection) prop);
            }
        }

        return Optional.empty();
    }

    /**
     * Returns the Direction value of the first found PropertyDirection
     * type block state property in the given state, if any.
     * If there are no PropertyDirection properties, then empty() is returned.
     */
    public static Optional<Direction> getFirstPropertyFacingValue(IBlockState state)
    {
        Optional<PropertyDirection> propOptional = getFirstDirectionProperty(state);
        return propOptional.isPresent() ? Optional.ofNullable(Direction.byIndex(state.getValue(propOptional.get()).getIndex())) : Optional.empty();
    }

    public static List<String> getFormattedBlockStateProperties(IBlockState state)
    {
        return getFormattedBlockStateProperties(state, ": ");
    }

    public static List<String> getFormattedBlockStateProperties(IBlockState state, String separator)
    {
        Collection<IProperty<?>> properties = state.getPropertyKeys();

        if (properties.size() > 0)
        {
            List<String> lines = new ArrayList<>();

            try
            {
                for (IProperty<?> prop : properties)
                {
                    Comparable<?> val = state.getValue(prop);
                    String key;

                    if (prop instanceof PropertyBool)
                    {
                        key = val.equals(Boolean.TRUE) ? "malilib.label.block_state_properties.boolean.true" :
                                                         "malilib.label.block_state_properties.boolean.false";
                    }
                    else if (prop instanceof PropertyDirection)
                    {
                        key = "malilib.label.block_state_properties.direction";
                    }
                    else if (prop instanceof PropertyEnum)
                    {
                        key = "malilib.label.block_state_properties.enum";
                    }
                    else if (prop instanceof PropertyInteger)
                    {
                        key = "malilib.label.block_state_properties.integer";
                    }
                    else
                    {
                        key = "malilib.label.block_state_properties.generic";
                    }

                    lines.add(StringUtils.translate(key, prop.getName(), separator, val.toString()));
                }
            }
            catch (Exception ignore) {}

            return lines;
        }

        return Collections.emptyList();
    }


    public static List<StyledTextLine> getBlockStatePropertyStyledTextLines(IBlockState state, String separator)
    {
        Collection<IProperty<?>> properties = state.getPropertyKeys();

        if (properties.size() > 0)
        {
            List<StyledTextLine> lines = new ArrayList<>();

            try
            {
                for (IProperty<?> prop : properties)
                {
                    Comparable<?> val = state.getValue(prop);
                    String key;

                    if (prop instanceof PropertyBool)
                    {
                        key = val.equals(Boolean.TRUE) ? "malilib.label.block_state_properties.boolean.true" :
                                                         "malilib.label.block_state_properties.boolean.false";
                    }
                    else if (prop instanceof PropertyDirection)
                    {
                        key = "malilib.label.block_state_properties.direction";
                    }
                    else if (prop instanceof PropertyEnum)
                    {
                        key = "malilib.label.block_state_properties.enum";
                    }
                    else if (prop instanceof PropertyInteger)
                    {
                        key = "malilib.label.block_state_properties.integer";
                    }
                    else
                    {
                        key = "malilib.label.block_state_properties.generic";
                    }

                    StyledTextLine.translate(lines, key, prop.getName(), separator, val.toString());
                }
            }
            catch (Exception ignore) {}

            return lines;
        }

        return Collections.emptyList();
    }

    public static boolean isFluidBlock(IBlockState state)
    {
        return state.getMaterial().isLiquid();
    }

    public static boolean isFluidSourceBlock(IBlockState state)
    {
        return state.getBlock() instanceof BlockLiquid && state.getValue(BlockLiquid.LEVEL).intValue() == 0;
    }
}
