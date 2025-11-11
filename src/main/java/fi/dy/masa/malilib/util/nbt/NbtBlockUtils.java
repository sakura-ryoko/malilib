package fi.dy.masa.malilib.util.nbt;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import org.apache.commons.lang3.tuple.Pair;

import com.mojang.serialization.Codec;
import net.minecraft.block.entity.*;
import net.minecraft.block.spawner.TrialSpawnerData;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.Vibrations;

public class NbtBlockUtils
{
    /**
     * Get the Block Entity Type from the NBT Tag.
     *
     * @param nbt ()
     * @return ()
     */
    public static @Nullable BlockEntityType<?> getBlockEntityTypeFromNbt(@Nonnull NbtCompound nbt)
    {
        if (nbt.contains(NbtKeys.ID))
        {
            return Registries.BLOCK_ENTITY_TYPE.getOptionalValue(Identifier.tryParse(nbt.getString(NbtKeys.ID, ""))).orElse(null);
        }

        return null;
    }

    public static @Nullable Text getCustomNameFromNbt(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry, String key)
    {
        NbtView view = NbtView.getReader(nbt, registry);
        return BlockEntity.tryParseCustomName(Objects.requireNonNull(view.getReader()), key);
    }

    /**
     * Write the Block Entity ID tag.
     *
     * @param type ()
     * @param nbtIn ()
     * @return ()
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
     * @param nbt ()
     * @return ()
     */
    public static Set<Integer> getDisabledSlotsFromNbt(@Nonnull NbtCompound nbt)
    {
        Set<Integer> list = new HashSet<>();

        if (nbt.contains(NbtKeys.DISABLED_SLOTS))
        {
            int[] is = nbt.getIntArray(NbtKeys.DISABLED_SLOTS).orElse(new int[0]);

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
     * @param nbt  ()
     * @return ()
     */
    public static Pair<RegistryEntry<StatusEffect>, RegistryEntry<StatusEffect>> getBeaconEffectsFromNbt(@Nonnull NbtCompound nbt)
    {
        Set<RegistryEntry<StatusEffect>> effects = BeaconBlockEntity.EFFECTS_BY_LEVEL.stream().flatMap(Collection::stream).collect(Collectors.toSet());
        RegistryEntry<StatusEffect> primary = null;
        RegistryEntry<StatusEffect> secondary = null;

        if (nbt.contains(NbtKeys.PRIMARY_EFFECT))
        {
            primary = nbt.get(NbtKeys.PRIMARY_EFFECT, Registries.STATUS_EFFECT.getEntryCodec()).filter(effects::contains).orElse(null);
        }

        if (nbt.contains(NbtKeys.SECONDARY_EFFECT))
        {
	        secondary = nbt.get(NbtKeys.SECONDARY_EFFECT, Registries.STATUS_EFFECT.getEntryCodec()).filter(effects::contains).orElse(null);
        }

        return Pair.of(primary, secondary);
    }

    /**
     * Get the Beehive data from NBT.
     * @param nbt ()
     * @return ()
     */
    public static Pair<List<BeehiveBlockEntity.BeeData>, BlockPos> getBeesDataFromNbt(@Nonnull NbtCompound nbt)
    {
        List<BeehiveBlockEntity.BeeData> bees = new ArrayList<>();
        BlockPos flower = BlockPos.ORIGIN;

        if (nbt.contains(NbtKeys.BEES))
        {
            bees = nbt.get(NbtKeys.BEES, BeehiveBlockEntity.BeeData.LIST_CODEC).orElse(List.of());
        }

        if (nbt.contains(NbtKeys.FLOWER))
        {
            flower = NbtUtils.getPosCodec(nbt, NbtKeys.FLOWER);
        }

        return Pair.of(bees, flower);
    }

    /**
     * Get the Skulk Sensor Vibration / Listener data from NBT.
     *
     * @param nbt ()
     * @param registry ()
     * @return ()
     */
    public static Pair<Integer, Vibrations.ListenerData> getSkulkSensorVibrationsFromNbt(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
    {
        Vibrations.ListenerData data = null;
        int lastFreq = -1;

        if (nbt.contains(NbtKeys.VIBRATION))
        {
            lastFreq = nbt.getInt(NbtKeys.VIBRATION, 0);
        }

        if (nbt.contains(NbtKeys.LISTENER))
        {
            data = nbt.get(NbtKeys.LISTENER, Vibrations.ListenerData.CODEC, registry.getOps(NbtOps.INSTANCE)).orElseGet(Vibrations.ListenerData::new);
        }

        return Pair.of(lastFreq, data);
    }

    /**
     * Get the End Gateway's Exit Portal from NBT.
     * @param nbt ()
     * @return ()
     */
    public static Pair<Long, BlockPos> getExitPortalFromNbt(@Nonnull NbtCompound nbt)
    {
        long age = -1;
        BlockPos pos = BlockPos.ORIGIN;

        if (nbt.contains(NbtKeys.AGE))
        {
            age = nbt.getLong(NbtKeys.AGE, -1L);
        }

        if (nbt.contains(NbtKeys.EXIT))
        {
            pos = NbtUtils.getPosCodec(nbt, NbtKeys.EXIT);
        }

        return Pair.of(age, pos);
    }

    /**
     * Get a Sign's Text from NBT.
     *
     * @param nbt ()
     * @param registry ()
     * @return ()
     */
    public static Pair<Pair<SignText, SignText>, Boolean> getSignTextFromNbt(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
    {
        AtomicReference<SignText> front = new AtomicReference<>(null);
        AtomicReference<SignText> back = new AtomicReference<>(null);
        boolean waxed = false;

        if (nbt.contains(NbtKeys.FRONT_TEXT))
        {
            SignText.CODEC.parse(registry.getOps(NbtOps.INSTANCE), nbt.get(NbtKeys.FRONT_TEXT)).resultOrPartial().ifPresent(front::set);
        }

        if (nbt.contains(NbtKeys.BACK_TEXT))
        {
            SignText.CODEC.parse(registry.getOps(NbtOps.INSTANCE), nbt.get(NbtKeys.BACK_TEXT)).resultOrPartial().ifPresent(back::set);
        }

        if (nbt.contains(NbtKeys.WAXED))
        {
            waxed = nbt.getBoolean(NbtKeys.WAXED).orElse(false);
        }

        return Pair.of(Pair.of(front.get(), back.get()), waxed);
    }

    /**
     * Get a Lectern's Book and Page number.
     *
     * @param nbt ()
     * @param registry ()
     * @return ()
     */
    public static Pair<ItemStack, Integer> getBookFromNbt(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
    {
        ItemStack book = ItemStack.EMPTY;
        int current = -1;

        if (nbt.contains(NbtKeys.BOOK))
        {
            book = nbt.get(NbtKeys.BOOK, ItemStack.CODEC, registry.getOps(NbtOps.INSTANCE)).orElse(ItemStack.EMPTY);
        }

        if (nbt.contains(NbtKeys.PAGE))
        {
            current = nbt.getInt(NbtKeys.PAGE, -1);
        }

        return Pair.of(book, current);
    }

    /**
     * Get a Skull's Profile Data Component from NBT, and Custom Name.
     *
     * @param nbt ()
     * @param registry ()
     * @return ()
     */
    public static Pair<ProfileComponent, Pair<Identifier, Text>> getSkullDataFromNbt(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
    {
        ProfileComponent profile = null;
        Identifier note = null;
        Text name = null;

        if (nbt.contains(NbtKeys.NOTE))
        {
            note = nbt.get(NbtKeys.NOTE, Identifier.CODEC).orElse(null);
        }

        if (nbt.contains(NbtKeys.SKULL_NAME))
        {
            /*
            String str = nbt.getString(NbtKeys.SKULL_NAME);

            try
            {
                name = Text.Serialization.fromJson(str, registry);
            }
            catch (Exception ignored) {}
             */

            name = getCustomNameFromNbt(nbt, registry, NbtKeys.SKULL_NAME);
        }

        if (nbt.contains(NbtKeys.PROFILE))
        {
            //ProfileComponent.CODEC.parse(NbtOps.INSTANCE, nbt.get(NbtKeys.PROFILE)).resultOrPartial().ifPresent(profile::set);
            profile = nbt.get(NbtKeys.PROFILE, ProfileComponent.CODEC).orElse(null);
        }

        return Pair.of(profile, Pair.of(note, name));
    }

    /**
     * Get a Furnaces 'Used Recipes' from NBT.
     *
     * @param nbt ()
     * @return ()
     */
    public static Reference2IntOpenHashMap<RegistryKey<Recipe<?>>> getRecipesUsedFromNbt(@Nonnull NbtCompound nbt)
    {
        Codec<Map<RegistryKey<Recipe<?>>, Integer>> CODEC = Codec.unboundedMap(Recipe.KEY_CODEC, Codec.INT);
        Reference2IntOpenHashMap<RegistryKey<Recipe<?>>> list = new Reference2IntOpenHashMap<>();

        if (nbt.contains(NbtKeys.RECIPES_USED))
        {
            /*
            NbtCompound compound = nbt.getCompound(NbtKeys.RECIPES_USED);

            for (String key : compound.getKeys())
            {
                list.put(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of(key)), compound.getInt(key));
            }
             */

            list.putAll(nbt.get(NbtKeys.RECIPES_USED, CODEC).orElse(Map.of()));
        }

        return list;
    }

    /**
     * Get the Redstone Output Signal from a Repeater
     * @param nbt ()
     * @return ()
     */
    public static int getOutputSignalFromNbt(@Nonnull NbtCompound nbt)
    {
        if (nbt.contains(NbtKeys.OUTPUT_SIGNAL))
        {
            return nbt.getInt(NbtKeys.OUTPUT_SIGNAL, 0);
        }

        return 0;
    }

	/**
	 * Get Trial Spawner Data from NBT
	 * @param nbt ()
	 * @return ()
	 */
    public static Optional<TrialSpawnerData.Packed> getTrialSpawnerDataFromNbt(@Nonnull NbtCompound nbt)
    {
        return NbtUtils.readFlatMap(nbt, TrialSpawnerData.Packed.CODEC);
    }
}
