package fi.dy.masa.malilib.util.game;

import java.util.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CrafterBlockEntity;
import net.minecraft.block.enums.Orientation;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.*;
import net.minecraft.util.math.Direction;

import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.ResourceLocation;
import fi.dy.masa.malilib.util.game.wrap.NbtWrap;
import fi.dy.masa.malilib.util.game.wrap.RegistryUtils;

/**
 * Post-ReWrite code
 */
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
        ResourceLocation id = ResourceLocation.of(blockName);

        if (RegistryUtils.getBlockById(id) != null)
        {
            Block block = RegistryUtils.getBlockById(id);
            BlockState state = block.getDefaultState();
            StateManager<Block, BlockState> stateManager = block.getStateManager();

            if (index != -1 && str.length() > (index + 4) && str.charAt(str.length() - 1) == ']')
            {
                String propStr = str.substring(index + 1, str.length() - 1);

                for (String propAndVal : COMMA_SPLITTER.split(propStr))
                {
                    Iterator<String> valIter = EQUAL_SPLITTER.split(propAndVal).iterator();

                    if (valIter.hasNext() == false)
                    {
                        continue;
                    }

                    Property<?> prop = stateManager.getProperty(valIter.next());

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

            return Optional.of(state);
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
    public static NbtCompound getBlockStateTagFromString(String stateString)
    {
        int index = stateString.indexOf("["); // [f=b]
        String blockName = index != -1 ? stateString.substring(0, index) : stateString;
        NbtCompound tag = new NbtCompound();

        NbtWrap.putString(tag, "Name", blockName);

        if (index != -1 && stateString.length() > (index + 4) && stateString.charAt(stateString.length() - 1) == ']')
        {
            NbtCompound propsTag = new NbtCompound();
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
     * @return an equivalent of BlockState.toString() of the given tag representing a block state
     */
    public static String getBlockStateStringFromTag(NbtCompound stateTag)
    {
        String name = NbtWrap.getString(stateTag, "Name");

        if (NbtWrap.containsCompound(stateTag, "Properties") == false)
        {
            return name;
        }

        NbtCompound propTag = NbtWrap.getCompound(stateTag, "Properties");
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

    @SuppressWarnings("unchecked")
    public static <T extends Comparable<T>> BlockState getBlockStateWithProperty(BlockState state, Property<T> prop, Comparable<?> value)
    {
        return state.with(prop, (T) value);
    }

    @Nullable
    public static <T extends Comparable<T>> T getPropertyValueByName(Property<T> prop, String valStr)
    {
        return prop.parse(valStr).orElse(null);
    }

    /**
     * Returns the Direction value of the first found PropertyDirection
     * type block state property in the given state, if any.
     * If there are no PropertyDirection properties, then empty() is returned.
     */
    public static Optional<Direction> getFirstPropertyFacingValue(BlockState state)
    {
        Optional<EnumProperty<Direction>> propOptional = getFirstDirectionProperty(state);
        return propOptional.map(directionProperty -> Direction.byId(state.get(directionProperty).getId()));
    }

    /**
     * Returns the first PropertyDirection property from the provided state, if any.
     * @return the first PropertyDirection, or empty() if there are no such properties
     */
    @SuppressWarnings("unchecked")
    public static Optional<EnumProperty<Direction>> getFirstDirectionProperty(BlockState state)
    {
        for (Property<?> prop : state.getProperties())
        {
            if (prop instanceof EnumProperty<?> ep && ep.getType().equals(Direction.class))
            {
                return Optional.of((EnumProperty<Direction>) ep);
            }
        }

        return Optional.empty();
    }

    public static List<String> getFormattedBlockStateProperties(BlockState state)
    {
        return getFormattedBlockStateProperties(state, ": ");
    }

    public static List<String> getFormattedBlockStateProperties(BlockState state, String separator)
    {
        Collection<Property<?>> properties = state.getProperties();

        if (properties.size() > 0)
        {
            List<String> lines = new ArrayList<>();

            try
            {
                for (Property<?> prop : properties)
                {
                    Comparable<?> val = state.get(prop);
                    String key;

                    if (prop instanceof BooleanProperty)
                    {
                        key = val.equals(Boolean.TRUE) ? "malilib.label.block_state_properties.boolean.true" :
                                                         "malilib.label.block_state_properties.boolean.false";
                    }
                    else if (prop instanceof EnumProperty<?> enumProperty)
                    {
                        if (enumProperty.getType().equals(Direction.class))
                        {
                            key = "malilib.label.block_state_properties.direction";
                        }
                        else if (enumProperty.getType().equals(Orientation.class))
                        {
                            key = "malilib.label.block_state_properties.orientation";
                        }
                        else
                        {
                            key = "malilib.label.block_state_properties.enum";
                        }
                    }
                    else if (prop instanceof IntProperty)
                    {
                        key = "malilib.label.block_state_properties.integer";
                    }
                    else
                    {
                        key = "malilib.label.block_state_properties.generic";
                    }

                    lines.add(StringUtils.translate(key, prop.getName(), separator, val.toString().toLowerCase()));
                }
            }
            catch (Exception ignore) {}

            return lines;
        }

        return Collections.emptyList();
    }

    // TODO after adding `StyledTextLine`
    /*
    public static List<StyledTextLine> getBlockStatePropertyStyledTextLines(BlockState state, String separator)
    {
        Collection<Property<?>> properties = state.getProperties();

        if (properties.size() > 0)
        {
            List<StyledTextLine> lines = new ArrayList<>();

            try
            {
                for (Property<?> prop : properties)
                {
                    Comparable<?> val = state.get(prop);
                    String key;

                    if (prop instanceof BooleanProperty)
                    {
                        key = val.equals(Boolean.TRUE) ? "malilib.label.block_state_properties.boolean.true" :
                                                         "malilib.label.block_state_properties.boolean.false";
                    }
                    else if (prop instanceof BooleanProperty)
                    {
                        key = "malilib.label.block_state_properties.direction";
                    }
                    else if (prop instanceof EnumProperty<?>)
                    {
                        key = "malilib.label.block_state_properties.enum";
                    }
                    else if (prop instanceof IntProperty)
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
    */

    public static boolean isFluidBlock(BlockState state)
    {
        return !state.getFluidState().equals(Fluids.EMPTY.getDefaultState());
    }

    public static boolean isFluidSourceBlock(BlockState state)
    {
        return state.getBlock() instanceof FluidBlock && state.getFluidState().getLevel() == 8;
    }

    @Nullable
    public static Direction getPropertyFacingValue(BlockState state)
    {
        return state.contains(Properties.FACING) ? state.get(Properties.FACING) : null;
    }

    @Nullable
    public static Direction getPropertyHopperFacingValue(BlockState state)
    {
        return state.contains(Properties.HOPPER_FACING) ? state.get(Properties.HOPPER_FACING) : null;
    }

    @Nullable
    public static Direction getPropertyHorizontalFacingValue(BlockState state)
    {
        return state.contains(Properties.HORIZONTAL_FACING) ? state.get(Properties.HORIZONTAL_FACING) : null;
    }

    @Nullable
    public static Orientation getPropertyOrientationValue(BlockState state)
    {
        return state.contains(Properties.ORIENTATION) ? state.get(Properties.ORIENTATION) : null;
    }

    @Nullable
    public static Direction getPropertyOrientationFacing(BlockState state)
    {
        Orientation o = getPropertyOrientationValue(state);

        return o != null ? o.getFacing() : null;
    }

    @Nullable
    public static Direction getPropertyOrientationRotation(BlockState state)
    {
        Orientation o = getPropertyOrientationValue(state);

        return o != null ? o.getRotation() : null;
    }

    public static boolean isFacingValidForDirection(ItemStack stack, Direction facing)
    {
        Item item = stack.getItem();

        if (stack.isEmpty() == false && item instanceof BlockItem)
        {
            Block block = ((BlockItem) item).getBlock();
            BlockState state = block.getDefaultState();

            if (state.contains(Properties.FACING))
            {
                return true;
            }
            else if (state.contains(Properties.HOPPER_FACING) &&
                    facing.equals(Direction.UP) == false)
            {
                return true;
            }
            else if (state.contains(Properties.HORIZONTAL_FACING) &&
                    facing.equals(Direction.UP) == false &&
                    facing.equals(Direction.DOWN) == false)
            {
                return true;
            }
        }

        return false;
    }

    public static int getDirectionFacingIndex(ItemStack stack, Direction facing)
    {
        if (isFacingValidForDirection(stack, facing))
        {
            return facing.getId();
        }

        return -1;
    }

    public static boolean isFacingValidForOrientation(ItemStack stack, Direction facing)
    {
        Item item = stack.getItem();

        if (stack.isEmpty() == false && item instanceof BlockItem)
        {
            Block block = ((BlockItem) item).getBlock();
            BlockState state = block.getDefaultState();

            return state.contains(Properties.ORIENTATION);
        }

        return false;
    }

    public static int getOrientationFacingIndex(ItemStack stack, Direction facing)
    {
        if (stack.getItem() instanceof BlockItem blockItem)
        {
            BlockState defaultState = blockItem.getBlock().getDefaultState();

            if (defaultState.contains(Properties.ORIENTATION))
            {
                List<Orientation> list = Arrays.stream(Orientation.values()).toList();

                for (int i = 0; i < list.size(); i++)
                {
                    Orientation o = list.get(i);

                    if (o.getFacing().equals(facing))
                    {
                        return i;
                    }
                }
            }
        }

        return -1;
    }

    /**
     * Get a Crafter's "locked slots" from the Block Entity by iterating all 9 slots.
     *
     * @param ce
     * @return
     */
    public static Set<Integer> getDisabledSlots(CrafterBlockEntity ce)
    {
        Set<Integer> list = new HashSet<>();

        if (ce != null)
        {
            for (int i = 0; i < 9; i++)
            {
                if (ce.isSlotDisabled(i))
                {
                    list.add(i);
                }
            }
        }

        return list;
    }

    /**
     * Write a Block Entity's Data to an ItemStack (Removed from Vanilla, why?)
     *
     * @param stack
     * @param be
     * @param registry
     */
    public static void setStackNbt(@Nonnull ItemStack stack, @Nonnull BlockEntity be, @Nonnull DynamicRegistryManager registry)
    {
        NbtCompound nbt = be.createComponentlessNbt(registry);
        BlockItem.setBlockEntityData(stack, be.getType(), nbt);
        stack.applyComponentsFrom(be.createComponentMap());
    }
}
