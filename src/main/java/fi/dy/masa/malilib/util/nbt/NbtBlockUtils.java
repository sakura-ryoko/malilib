package fi.dy.masa.malilib.util.nbt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SignText;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.Vibrations;

import fi.dy.masa.malilib.util.data.Constants;

public class NbtBlockUtils
{
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

        if (nbt.contains(NbtKeys.BEES))
        {
            BeehiveBlockEntity.BeeData.LIST_CODEC.parse(NbtOps.INSTANCE, nbt.get(NbtKeys.BEES)).resultOrPartial().ifPresent(bees::addAll);
        }
        if (nbt.contains(NbtKeys.FLOWER, Constants.NBT.TAG_INT_ARRAY))
        {
            flower = NbtUtils.readBlockPosFromIntArray(nbt, NbtKeys.FLOWER);
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
            pos = NbtUtils.readBlockPosFromIntArray(nbt, NbtKeys.EXIT);
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

    public static int getOutputSignalFromNbt(@Nonnull NbtCompound nbt)
    {
        if (nbt.contains(NbtKeys.OUTPUT_SIGNAL, Constants.NBT.TAG_INT))
        {
            return nbt.getInt(NbtKeys.OUTPUT_SIGNAL);
        }

        return 0;
    }
}
