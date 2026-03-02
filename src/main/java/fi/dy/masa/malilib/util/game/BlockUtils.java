package fi.dy.masa.malilib.util.game;

import java.util.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.TagValueOutput;

import fi.dy.masa.malilib.data.CachedTagUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.game.wrap.RegistryUtils;
import fi.dy.masa.malilib.util.nbt.NbtView;

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
        Identifier id = Identifier.tryParse(blockName);

        if (id == null)
        {
            return Optional.empty();
        }

        if (RegistryUtils.getBlockById(id) != null)
        {
            Block block = RegistryUtils.getBlockById(id);
            BlockState state = block.defaultBlockState();
            StateDefinition<@NotNull Block, @NotNull BlockState> stateManager = block.getStateDefinition();

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
    public static CompoundTag getBlockStateTagFromString(String stateString)
    {
        int index = stateString.indexOf("["); // [f=b]
        String blockName = index != -1 ? stateString.substring(0, index) : stateString;
        CompoundTag tag = new CompoundTag();

        tag.putString("Name", blockName);

        if (index != -1 && stateString.length() > (index + 4) && stateString.charAt(stateString.length() - 1) == ']')
        {
            CompoundTag propsTag = new CompoundTag();
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

                propsTag.putString(propName, valStr);
            }

            tag.put("Properties", propsTag);
        }

        return tag;
    }

    /**
     * Parses the input tag representing a block state, and produces a string
     * in the same format as the toString() method in the vanilla block state.
     * This string format is what the Sponge schematic format uses in the palette.
     * @return an equivalent of BlockState.toString() of the given tag representing a block state
     */
    public static String getBlockStateStringFromTag(CompoundTag stateTag)
    {
        String name = stateTag.getStringOr("Name", "");

        if (stateTag.contains("Properties") == false)
        {
            return name;
        }

        CompoundTag propTag = stateTag.getCompoundOrEmpty("Properties");
        ArrayList<Pair<String, String>> props = new ArrayList<>();

        for (String key : propTag.keySet())
        {
            props.add(Pair.of(key, propTag.getStringOr(key, "")));
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
    public static <T extends Comparable<T>> BlockState getBlockStateWithProperty(BlockState state, Property<@NotNull T> prop, Comparable<?> value)
    {
        return state.setValue(prop, (T) value);
    }

    @Nullable
    public static <T extends Comparable<T>> T getPropertyValueByName(Property<@NotNull T> prop, String valStr)
    {
        return prop.getValue(valStr).orElse(null);
    }

    /**
     * Returns the Direction value of the first found PropertyDirection
     * type block state property in the given state, if any.
     * If there are no PropertyDirection properties, then empty() is returned.
     */
    public static Optional<Direction> getFirstPropertyFacingValue(BlockState state)
    {
        Optional<EnumProperty<@NotNull Direction>> propOptional = getFirstDirectionProperty(state);
        return propOptional.map(directionProperty -> Direction.byName(state.getValue(directionProperty).getName()));
    }

    /**
     * Returns the first PropertyDirection property from the provided state, if any.
     * @return the first PropertyDirection, or empty() if there are no such properties
     */
    @SuppressWarnings("unchecked")
    public static Optional<EnumProperty<@NotNull Direction>> getFirstDirectionProperty(BlockState state)
    {
        for (Property<?> prop : state.getProperties())
        {
            if (prop instanceof EnumProperty<?> ep && ep.getValueClass().equals(Direction.class))
            {
                return Optional.of((EnumProperty<@NotNull Direction>) ep);
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
                    Comparable<?> val = state.getValue(prop);
                    String key;

                    if (prop instanceof BooleanProperty)
                    {
                        key = val.equals(Boolean.TRUE) ? "malilib.label.block_state_properties.boolean.true" :
                                                         "malilib.label.block_state_properties.boolean.false";
                    }
                    else if (prop instanceof EnumProperty<?> enumProperty)
                    {
                        if (enumProperty.getValueClass().equals(Direction.class))
                        {
                            key = "malilib.label.block_state_properties.direction";
                        }
                        else if (enumProperty.getValueClass().equals(FrontAndTop.class))
                        {
                            key = "malilib.label.block_state_properties.orientation";
                        }
                        else
                        {
                            key = "malilib.label.block_state_properties.enum";
                        }
                    }
                    else if (prop instanceof IntegerProperty)
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
        return !state.getFluidState().equals(Fluids.EMPTY.defaultFluidState());
    }

    public static boolean isFluidSourceBlock(BlockState state)
    {
        return state.getBlock() instanceof LiquidBlock && state.getFluidState().getAmount() == 8;
    }

    @Nullable
    public static Direction getPropertyFacingValue(BlockState state)
    {
        return state.hasProperty(BlockStateProperties.FACING) ? state.getValue(BlockStateProperties.FACING) : null;
    }

    @Nullable
    public static Direction getPropertyHopperFacingValue(BlockState state)
    {
        return state.hasProperty(BlockStateProperties.FACING_HOPPER) ? state.getValue(BlockStateProperties.FACING_HOPPER) : null;
    }

    @Nullable
    public static Direction getPropertyHorizontalFacingValue(BlockState state)
    {
        return state.hasProperty(BlockStateProperties.HORIZONTAL_FACING) ? state.getValue(BlockStateProperties.HORIZONTAL_FACING) : null;
    }

    @Nullable
    public static FrontAndTop getPropertyOrientationValue(BlockState state)
    {
        return state.hasProperty(BlockStateProperties.ORIENTATION) ? state.getValue(BlockStateProperties.ORIENTATION) : null;
    }

    @Nullable
    public static Direction getPropertyOrientationFacing(BlockState state)
    {
        FrontAndTop o = getPropertyOrientationValue(state);

        return o != null ? o.front() : null;
    }

    @Nullable
    public static Direction getPropertyOrientationRotation(BlockState state)
    {
        FrontAndTop o = getPropertyOrientationValue(state);

        return o != null ? o.top() : null;
    }

    public static boolean isFacingValidForDirection(ItemStack stack, Direction facing)
    {
        Item item = stack.getItem();

        if (stack.isEmpty() == false && item instanceof BlockItem)
        {
            Block block = ((BlockItem) item).getBlock();
            BlockState state = block.defaultBlockState();

            if (state.hasProperty(BlockStateProperties.FACING))
            {
                return true;
            }
            else if (state.hasProperty(BlockStateProperties.FACING_HOPPER) &&
                    facing.equals(Direction.UP) == false)
            {
                return true;
            }
            else if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING) &&
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
            return facing.get3DDataValue();
        }

        return -1;
    }

    public static boolean isFacingValidForOrientation(ItemStack stack, Direction facing)
    {
        Item item = stack.getItem();

        if (stack.isEmpty() == false && item instanceof BlockItem)
        {
            Block block = ((BlockItem) item).getBlock();
            BlockState state = block.defaultBlockState();

            return state.hasProperty(BlockStateProperties.ORIENTATION);
        }

        return false;
    }

    public static int getOrientationFacingIndex(ItemStack stack, Direction facing)
    {
        if (stack.getItem() instanceof BlockItem blockItem)
        {
            BlockState defaultState = blockItem.getBlock().defaultBlockState();

            if (defaultState.hasProperty(BlockStateProperties.ORIENTATION))
            {
                List<FrontAndTop> list = Arrays.stream(FrontAndTop.values()).toList();

                for (int i = 0; i < list.size(); i++)
                {
                    FrontAndTop o = list.get(i);

                    if (o.front().equals(facing))
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
     * @param ce ()
     * @return ()
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
     * @param stack ()
     * @param be ()
     * @param registry ()
     */
    public static void setStackNbt(@Nonnull ItemStack stack, @Nonnull BlockEntity be, @Nonnull RegistryAccess registry)
    {
        if (stack.isEmpty()) return;
//        NbtCompound nbt = be.createComponentlessNbt(registry);
        NbtView view = NbtView.getWriter(registry);
//        view = view.writeNbt(nbt);
        be.saveCustomOnly(view.getWriter());
        BlockItem.setBlockEntityData(stack, be.getType(), (TagValueOutput) view.getWriter());
        stack.applyComponents(be.collectComponents());
    }

    /**
     * Return if the two block states contains blocks in the same BlockTags listed under REPLACEABLE_GROUPS
     *
     * @param left ()
     * @param right ()
     * @return ()
     */
    public static boolean isInSameGroup(BlockState left, BlockState right)
    {
//        for (TagKey<Block> tagKey : MaLiLibTag.Blocks.REPLACEABLE_GROUPS)
//        {
//            if (left.isIn(tagKey) && right.isIn(tagKey))
//            {
//                return true;
//            }
//        }

        Pair<HolderSet<@NotNull Block>, Holder<@NotNull Block>> pairLeft = CachedTagUtils.matchReplaceableBlockTag(left);
        Pair<HolderSet<@NotNull Block>, Holder<@NotNull Block>> pairRight = CachedTagUtils.matchReplaceableBlockTag(right);

        // Do not check the block tag (getRight())
        return pairLeft.getLeft() != null && pairRight.getLeft() != null &&
               pairLeft.getLeft().unwrapKey().equals(pairRight.getLeft().unwrapKey());
    }

    /**
     * Match the properties list only of two block states, ignoring the block type
     *
     * @param left ()
     * @param right ()
     * @return ()
     */
    public static boolean matchPropertiesOnly(BlockState left, BlockState right)
    {
        return compareProperties(left, right) && compareProperties(right, left);
    }

    public static <T extends Comparable<T>> boolean compareProperties(BlockState state, BlockState otherState)
    {
        Collection<Property<?>> props = state.getProperties();

        for (Property<?> entry : props)
        {
            @SuppressWarnings("unchecked")
            Property<@NotNull T> p = (Property<@NotNull T>) entry;

            if (otherState.hasProperty(p))
            {
                T value = state.getValue(p);

                if (!value.equals(otherState.getValue(p)))
                {
                    return false;
                }
            }
            else
            {
                return false;
            }
        }

        return true;
    }

    @SuppressWarnings("deprecation")
    public static boolean matchSolidFullCubes(BlockState left, BlockState right)
    {
        if (left.isSolid() && right.isSolid())
        {
            return left.isSolidRender() && right.isSolidRender();
        }

        return false;
    }

    public static boolean matchMapColors(Level world, BlockPos pos, BlockState left, BlockState right)
    {
        return left.getMapColor(world, pos) == right.getMapColor(world, pos);
    }
}
