package fi.dy.masa.malilib.util;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.llamalad7.mixinextras.lib.apache.commons.tuple.Pair;

import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
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
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
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
     * Get the Block Entity Type from the NBT Tag.
     *
     * @param nbt
     * @return
     */
    public static @Nullable BlockEntityType<?> getBlockEntityTypeFromNbt(@Nonnull NbtCompound nbt)
    {
        if (nbt.contains(NbtKeys.ID, Constants.NBT.TAG_STRING))
        {
            return Registries.BLOCK_ENTITY_TYPE.getOptionalValue(Identifier.tryParse(nbt.getString(NbtKeys.ID))).orElse(null);
        }

        return null;
    }

    /**
     * Write the Block Entity ID tag.
     *
     * @param type
     * @param nbtIn
     * @return
     */
    public static NbtCompound setBlockEntityTypeToNbt(BlockEntityType<?> type, @Nullable NbtCompound nbtIn)
    {
        NbtCompound nbt = new NbtCompound();
        Identifier id = BlockEntityType.getId(type);

        if (id != null)
        {
            if (nbtIn != null)
            {
                nbtIn.putString(NbtKeys.ID, id.toString());
                return nbtIn;
            }
            else
            {
                nbt.putString(NbtKeys.ID, id.toString());
            }
        }

        return nbt;
    }

    /**
     * Read the Crafter's "locked slots" from NBT
     *
     * @param nbt
     * @return
     */
    public static Set<Integer> getDisabledSlotsFromNbt(@Nonnull NbtCompound nbt)
    {
        Set<Integer> list = new HashSet<>();

        if (nbt.contains(NbtKeys.DISABLED_SLOTS, Constants.NBT.TAG_INT_ARRAY))
        {
            int[] is = nbt.getIntArray(NbtKeys.DISABLED_SLOTS);

            for (int j : is)
            {
                list.add(j);
            }
        }

        return list;
    }

    /**
     * Get the Beacon's Effects from NBT.
     *
     * @param nbt
     * @return
     */
    public static Pair<RegistryEntry<StatusEffect>, RegistryEntry<StatusEffect>> getBeaconEffectsFromNbt(@Nonnull NbtCompound nbt)
    {
        RegistryEntry<StatusEffect> primary = null;
        RegistryEntry<StatusEffect> secondary = null;

        if (nbt.contains(NbtKeys.PRIMARY_EFFECT, Constants.NBT.TAG_STRING))
        {
            Identifier id = Identifier.tryParse(nbt.getString(NbtKeys.PRIMARY_EFFECT));
            if (id != null)
            {
                primary = Registries.STATUS_EFFECT.getEntry(id).orElse(null);
            }
        }
        if (nbt.contains(NbtKeys.SECONDARY_EFFECT, Constants.NBT.TAG_STRING))
        {
            Identifier id = Identifier.tryParse(nbt.getString(NbtKeys.SECONDARY_EFFECT));
            if (id != null)
            {
                secondary = Registries.STATUS_EFFECT.getEntry(id).orElse(null);
            }
        }

        return Pair.of(primary, secondary);
    }

    /**
     * Get the Beehive data from NBT.
     * @param nbt
     * @return
     */
    public static Pair<List<BeehiveBlockEntity.BeeData>, BlockPos> getBeesDataFromNbt(@Nonnull NbtCompound nbt)
    {
        List<BeehiveBlockEntity.BeeData> bees = new ArrayList<>();
        BlockPos flower = BlockPos.ORIGIN;

        if (nbt.contains(NbtKeys.FLOWER))
        {
            flower = NBTUtils.readBlockPosFromIntArray(nbt, NbtKeys.FLOWER);
        }
        if (nbt.contains(NbtKeys.BEES, Constants.NBT.TAG_LIST))
        {
            BeehiveBlockEntity.BeeData.LIST_CODEC.parse(NbtOps.INSTANCE, nbt.get(NbtKeys.BEES)).resultOrPartial().ifPresent(bees::addAll);
        }

        return Pair.of(bees, flower);
    }

    /**
     * Get the Skulk Sensor Vibration / Listener data from NBT.
     *
     * @param nbt
     * @param registry
     * @return
     */
    public static Pair<Integer, Vibrations.ListenerData> getSkulkSensorVibrationsFromNbt(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
    {
        AtomicReference<Vibrations.ListenerData> data = new AtomicReference<>(null);
        int lastFreq = -1;

        if (nbt.contains(NbtKeys.VIBRATION, Constants.NBT.TAG_INT))
        {
            lastFreq = nbt.getInt(NbtKeys.VIBRATION);
        }
        if (nbt.contains(NbtKeys.LISTENER, Constants.NBT.TAG_COMPOUND))
        {
            Vibrations.ListenerData.CODEC.parse(registry.getOps(NbtOps.INSTANCE), nbt.getCompound(NbtKeys.LISTENER)).resultOrPartial().ifPresent(data::set);
        }

        return Pair.of(lastFreq, data.get());
    }

    /**
     * Get the End Gateway's Exit Portal from NBT.
     * @param nbt
     * @return
     */
    public static Pair<Long, BlockPos> getExitPortalFromNbt(@Nonnull NbtCompound nbt)
    {
        long age = -1;
        BlockPos pos = BlockPos.ORIGIN;

        if (nbt.contains(NbtKeys.AGE, Constants.NBT.TAG_LONG))
        {
            age = nbt.getLong(NbtKeys.AGE);
        }
        if (nbt.contains(NbtKeys.EXIT, Constants.NBT.TAG_INT_ARRAY))
        {
            pos = NBTUtils.readBlockPosFromIntArray(nbt, NbtKeys.EXIT);
        }

        return Pair.of(age, pos);
    }

    /**
     * Get a Sign's Text from NBT.
     *
     * @param nbt
     * @param registry
     * @return
     */
    public static Pair<Pair<SignText, SignText>, Boolean> getSignTextFromNbt(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
    {
        AtomicReference<SignText> front = new AtomicReference<>(null);
        AtomicReference<SignText> back = new AtomicReference<>(null);
        boolean waxed = false;

        if (nbt.contains(NbtKeys.FRONT_TEXT))
        {
            SignText.CODEC.parse(registry.getOps(NbtOps.INSTANCE), nbt.getCompound(NbtKeys.FRONT_TEXT)).resultOrPartial().ifPresent(front::set);
        }
        if (nbt.contains(NbtKeys.BACK_TEXT))
        {
            SignText.CODEC.parse(registry.getOps(NbtOps.INSTANCE), nbt.getCompound(NbtKeys.BACK_TEXT)).resultOrPartial().ifPresent(back::set);
        }
        if (nbt.contains(NbtKeys.WAXED))
        {
            waxed = nbt.getBoolean(NbtKeys.WAXED);
        }

        return Pair.of(Pair.of(front.get(), back.get()), waxed);
    }

    /**
     * Get a Lectern's Book and Page number.
     *
     * @param nbt
     * @param registry
     * @return
     */
    public static Pair<ItemStack, Integer> getBookFromNbt(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
    {
        ItemStack book = ItemStack.EMPTY;
        int current = -1;

        if (nbt.contains(NbtKeys.BOOK, Constants.NBT.TAG_COMPOUND))
        {
            book = ItemStack.fromNbtOrEmpty(registry, nbt.getCompound(NbtKeys.BOOK));
        }
        if (nbt.contains(NbtKeys.PAGE, Constants.NBT.TAG_INT))
        {
            current = nbt.getInt(NbtKeys.PAGE);
        }

        return Pair.of(book, current);
    }

    /**
     * Get a Skull's Profile Data Component from NBT, and Custom Name.
     *
     * @param nbt
     * @param registry
     * @return
     */
    public static Pair<ProfileComponent, Pair<Identifier, Text>> getSkullDataFromNbt(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
    {
        AtomicReference<ProfileComponent> profile = new AtomicReference<>(null);
        Identifier note = null;
        Text name = Text.empty();

        if (nbt.contains(NbtKeys.NOTE, Constants.NBT.TAG_STRING))
        {
            note = Identifier.tryParse(nbt.getString(NbtKeys.NOTE));
        }
        if (nbt.contains(NbtKeys.SKULL_NAME, Constants.NBT.TAG_STRING))
        {
            String str = nbt.getString(NbtKeys.SKULL_NAME);

            try
            {
                name = Text.Serialization.fromJson(str, registry);
            }
            catch (Exception ignored) {}
        }
        if (nbt.contains(NbtKeys.PROFILE))
        {
            ProfileComponent.CODEC.parse(NbtOps.INSTANCE, nbt.get(NbtKeys.PROFILE)).resultOrPartial().ifPresent(profile::set);
        }

        return Pair.of(profile.get(), Pair.of(note, name));
    }

    /**
     * Get a Furnaces 'Used Recipes' from NBT.
     *
     * @param nbt
     * @return
     */
    public static Reference2IntOpenHashMap<RegistryKey<Recipe<?>>> getRecipesUsedFromNbt(@Nonnull NbtCompound nbt)
    {
        Reference2IntOpenHashMap<RegistryKey<Recipe<?>>> list = new Reference2IntOpenHashMap<>();

        if (nbt.contains(NbtKeys.RECIPES_USED, Constants.NBT.TAG_COMPOUND))
        {
            NbtCompound compound = nbt.getCompound(NbtKeys.RECIPES_USED);

            for (String key : compound.getKeys())
            {
                list.put(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of(key)), compound.getInt(key));
            }
        }

        return list;
    }

    /**
     * Get a Block's Regitry Entry.
     *
     * @param id
     * @param registry
     * @return
     */
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
