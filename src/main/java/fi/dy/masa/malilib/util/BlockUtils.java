package fi.dy.masa.malilib.util;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.llamalad7.mixinextras.lib.apache.commons.tuple.Pair;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.CrafterBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.block.enums.Orientation;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.*;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.event.Vibrations;

import fi.dy.masa.malilib.gui.GuiBase;

public class BlockUtils
{
    /**
     * Returns the first PropertyDirection property from the provided state, if any.
     *
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
     *
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

    public static @Nullable BlockEntityType<?> getBlockEntityTypeFromNbt(@Nonnull NbtCompound nbt)
    {
        if (nbt.contains("id", Constants.NBT.TAG_STRING))
        {
            return Registries.BLOCK_ENTITY_TYPE.getOptionalValue(Identifier.tryParse(nbt.getString("id"))).orElse(null);
        }

        return null;
    }

    public static NbtCompound setBlockEntityTypeToNbt(BlockEntityType<?> type, @Nullable NbtCompound nbtIn)
    {
        NbtCompound nbt = new NbtCompound();
        Identifier id = BlockEntityType.getId(type);

        if (id != null)
        {
            if (nbtIn != null)
            {
                nbtIn.putString("id", id.toString());
                return nbtIn;
            }
            else
            {
                nbt.putString("id", id.toString());
            }
        }

        return nbt;
    }

    public static Set<Integer> getDisabledSlotsFromNbt(@Nonnull NbtCompound nbt)
    {
        Set<Integer> list = new HashSet<>();

        if (nbt.contains("disabled_slots", Constants.NBT.TAG_INT_ARRAY))
        {
            int[] is = nbt.getIntArray("disabled_slots");

            for (int j : is)
            {
                list.add(j);
            }
        }

        return list;
    }

    public static Pair<RegistryEntry<StatusEffect>, RegistryEntry<StatusEffect>> getBeaconEffectsFromNbt(@Nonnull NbtCompound nbt)
    {
        RegistryEntry<StatusEffect> primary = null;
        RegistryEntry<StatusEffect> secondary = null;

        if (nbt.contains("primary_effect", Constants.NBT.TAG_STRING))
        {
            Identifier id = Identifier.tryParse(nbt.getString("primary_effect"));
            if (id != null)
            {
                primary = Registries.STATUS_EFFECT.getEntry(id).orElse(null);
            }
        }
        if (nbt.contains("secondary_effect", Constants.NBT.TAG_STRING))
        {
            Identifier id = Identifier.tryParse(nbt.getString("secondary_effect"));
            if (id != null)
            {
                secondary = Registries.STATUS_EFFECT.getEntry(id).orElse(null);
            }
        }

        return Pair.of(primary, secondary);
    }

    public static Pair<List<BeehiveBlockEntity.BeeData>, BlockPos> getBeesDataFromNbt(@Nonnull NbtCompound nbt)
    {
        List<BeehiveBlockEntity.BeeData> bees = new ArrayList<>();
        BlockPos flower = BlockPos.ORIGIN;

        if (nbt.contains("flower_pos"))
        {
            flower = NBTUtils.readBlockPosFromIntArray(nbt, "flower_pos");
        }
        if (nbt.contains("bees", Constants.NBT.TAG_LIST))
        {
            BeehiveBlockEntity.BeeData.LIST_CODEC.parse(NbtOps.INSTANCE, nbt.get("bees")).resultOrPartial().ifPresent(bees::addAll);
        }

        return Pair.of(bees, flower);
    }

    public static Pair<Integer, Vibrations.ListenerData> getSkulkSensorVibrationsFromNbt(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
    {
        AtomicReference<Vibrations.ListenerData> data = new AtomicReference<>(null);
        int lastFreq = -1;

        if (nbt.contains("last_vibration_frequency", Constants.NBT.TAG_INT))
        {
            lastFreq = nbt.getInt("last_vibration_frequency");
        }
        if (nbt.contains("listener", Constants.NBT.TAG_COMPOUND))
        {
            Vibrations.ListenerData.CODEC.parse(registry.getOps(NbtOps.INSTANCE), nbt.getCompound("listener")).resultOrPartial().ifPresent(data::set);
        }

        return Pair.of(lastFreq, data.get());
    }

    public static Pair<Long, BlockPos> getExitPortalFromNbt(@Nonnull NbtCompound nbt)
    {
        long age = -1;
        BlockPos pos = BlockPos.ORIGIN;

        if (nbt.contains("Age", Constants.NBT.TAG_LONG))
        {
            age = nbt.getLong("Age");
        }
        if (nbt.contains("exit_portal", Constants.NBT.TAG_INT_ARRAY))
        {
            pos = NBTUtils.readBlockPosFromIntArray(nbt, "exit_portal");
        }

        return Pair.of(age, pos);
    }

    public static Pair<Pair<SignText, SignText>, Boolean> getSignTextFromNbt(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
    {
        AtomicReference<SignText> front = new AtomicReference<>(null);
        AtomicReference<SignText> back = new AtomicReference<>(null);
        boolean waxed = false;

        if (nbt.contains("front_text"))
        {
            SignText.CODEC.parse(registry.getOps(NbtOps.INSTANCE), nbt.getCompound("front_text")).resultOrPartial().ifPresent(front::set);
        }
        if (nbt.contains("back_text"))
        {
            SignText.CODEC.parse(registry.getOps(NbtOps.INSTANCE), nbt.getCompound("back_text")).resultOrPartial().ifPresent(back::set);
        }
        if (nbt.contains("is_waxed"))
        {
            waxed = nbt.getBoolean("is_waxed");
        }

        return Pair.of(Pair.of(front.get(), back.get()), waxed);
    }

    public static Pair<ItemStack, Integer> getBookFromNbt(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
    {
        ItemStack book = ItemStack.EMPTY;
        int current = -1;

        if (nbt.contains("Book", Constants.NBT.TAG_COMPOUND))
        {
            book = ItemStack.fromNbtOrEmpty(registry, nbt.getCompound("Book"));
        }
        if (nbt.contains("Page", Constants.NBT.TAG_INT))
        {
            current = nbt.getInt("Page");
        }

        return Pair.of(book, current);
    }

    public static Pair<ProfileComponent, Pair<Identifier, Text>> getSkullDataFromNbt(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
    {
        AtomicReference<ProfileComponent> profile = new AtomicReference<>(null);
        Identifier note = null;
        Text name = Text.empty();

        if (nbt.contains("note_block_sound", Constants.NBT.TAG_STRING))
        {
            note = Identifier.tryParse(nbt.getString("note_block_sound"));
        }
        if (nbt.contains("custom_name", Constants.NBT.TAG_STRING))
        {
            String str = nbt.getString("custom_name");

            try
            {
                name = Text.Serialization.fromJson(str, registry);
            }
            catch (Exception ignored) {}
        }
        if (nbt.contains("profile"))
        {
            ProfileComponent.CODEC.parse(NbtOps.INSTANCE, nbt.get("profile")).resultOrPartial().ifPresent(profile::set);
        }

        return Pair.of(profile.get(), Pair.of(note, name));
    }

    public static RegistryEntry<Block> getBlockEntry(Identifier id, @Nonnull DynamicRegistryManager registry)
    {
        try
        {
            return registry.getOrThrow(Registries.BLOCK.getKey()).getEntry(id).orElseThrow();
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
