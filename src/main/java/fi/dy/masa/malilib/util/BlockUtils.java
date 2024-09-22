package fi.dy.masa.malilib.util;

import java.util.*;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.CrafterBlockEntity;
import net.minecraft.block.enums.Orientation;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.*;
import net.minecraft.util.math.Direction;

import fi.dy.masa.malilib.gui.GuiBase;

public class BlockUtils
{
    /**
     * Returns the first PropertyDirection property from the provided state, if any.
     * @param state
     * @return the first PropertyDirection, or null if there are no such properties
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public static EnumProperty<Direction> getFirstDirectionProperty(BlockState state)
    {
        for (Property<?> prop : state.getProperties())
        {
            if (prop instanceof EnumProperty<?> enumProperty)
            {
                if (enumProperty.getType().equals(Direction.class))
                {
                    return (EnumProperty<Direction>) enumProperty;
                }
            }
        }

        return null;
    }

    /**
     * Returns the EnumFacing value of the first found PropertyDirection
     * type blockstate property in the given state, if any.
     * If there are no PropertyDirection properties, then null is returned.
     * @param state
     * @return
     */
    @Nullable
    public static Direction getFirstPropertyFacingValue(BlockState state)
    {
        return getPropertyFacingValue(state);
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

            for (Property<?> prop : properties)
            {
                Comparable<?> val = state.get(prop);

                if (prop instanceof BooleanProperty)
                {
                    String pre = val.equals(Boolean.TRUE) ? GuiBase.TXT_GREEN : GuiBase.TXT_RED;
                    lines.add(prop.getName() + separator + pre + val.toString());
                }
                else if (prop instanceof EnumProperty<?> enumProperty)
                {
                    if (enumProperty.getType().equals(Direction.class))
                    {
                        lines.add(prop.getName() + separator + GuiBase.TXT_GOLD + val.toString());
                    }
                    else if (enumProperty.getType().equals(Orientation.class))
                    {
                        lines.add(prop.getName() + separator + GuiBase.TXT_LIGHT_PURPLE + val.toString());
                    }
                }
                else if (prop instanceof IntProperty)
                {
                    lines.add(prop.getName() + separator + GuiBase.TXT_AQUA + val.toString());
                }
                else
                {
                    lines.add(prop.getName() + separator + val.toString());
                }
            }

            return lines;
        }

        return Collections.emptyList();
    }

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

    public static Set<Integer> getDisabledSlots(NbtCompound nbt)
    {
        Set<Integer> list = new HashSet<>();

        if (nbt != null && !nbt.isEmpty())
        {
            if (nbt.contains("disabled_slots", Constants.NBT.TAG_INT_ARRAY))
            {
                int[] is = nbt.getIntArray("disabled_slots");

                for (int j : is)
                {
                    list.add(j);
                }
            }
        }

        return list;
    }
}
