package fi.dy.masa.malilib.util.nbt;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerStateData;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;

public class NbtBlockUtils
{
    /**
     * Get the Block Entity Type from the NBT Tag.
     *
     * @param nbt ()
     * @return ()
     */
    public static @Nullable BlockEntityType<?> getBlockEntityTypeFromNbt(@Nonnull CompoundTag nbt)
    {
        if (nbt.contains(NbtKeys.ID))
        {
            return BuiltInRegistries.BLOCK_ENTITY_TYPE.getOptional(Identifier.tryParse(nbt.getStringOr(NbtKeys.ID, ""))).orElse(null);
        }

        return null;
    }

    public static @Nullable Component getCustomNameFromNbt(@Nonnull CompoundTag nbt, @Nonnull RegistryAccess registry, String key)
    {
        NbtView view = NbtView.getReader(nbt, registry);
        return BlockEntity.parseCustomNameSafe(Objects.requireNonNull(view.getReader()), key);
    }

    /**
     * Write the Block Entity ID tag.
     *
     * @param type ()
     * @param nbtIn ()
     * @return ()
     */
    public static CompoundTag setBlockEntityTypeToNbt(BlockEntityType<?> type, @Nullable CompoundTag nbtIn)
    {
        CompoundTag nbt = new CompoundTag();
        Identifier id = BlockEntityType.getKey(type);

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
    public static Set<Integer> getDisabledSlotsFromNbt(@Nonnull CompoundTag nbt)
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
    public static Pair<Holder<@NotNull MobEffect>, Holder<@NotNull MobEffect>> getBeaconEffectsFromNbt(@Nonnull CompoundTag nbt)
    {
        Set<Holder<@NotNull MobEffect>> effects = BeaconBlockEntity.BEACON_EFFECTS.stream().flatMap(Collection::stream).collect(Collectors.toSet());
        Holder<@NotNull MobEffect> primary = null;
        Holder<@NotNull MobEffect> secondary = null;

        if (nbt.contains(NbtKeys.PRIMARY_EFFECT))
        {
            primary = nbt.read(NbtKeys.PRIMARY_EFFECT, BuiltInRegistries.MOB_EFFECT.holderByNameCodec()).filter(effects::contains).orElse(null);
        }

        if (nbt.contains(NbtKeys.SECONDARY_EFFECT))
        {
	        secondary = nbt.read(NbtKeys.SECONDARY_EFFECT, BuiltInRegistries.MOB_EFFECT.holderByNameCodec()).filter(effects::contains).orElse(null);
        }

        return Pair.of(primary, secondary);
    }

    /**
     * Get the Beehive data from NBT.
     * @param nbt ()
     * @return ()
     */
    public static Pair<List<BeehiveBlockEntity.Occupant>, BlockPos> getBeesDataFromNbt(@Nonnull CompoundTag nbt)
    {
        List<BeehiveBlockEntity.Occupant> bees = new ArrayList<>();
        BlockPos flower = BlockPos.ZERO;

        if (nbt.contains(NbtKeys.BEES))
        {
            bees = nbt.read(NbtKeys.BEES, BeehiveBlockEntity.Occupant.LIST_CODEC).orElse(List.of());
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
    public static Pair<Integer, VibrationSystem.Data> getSkulkSensorVibrationsFromNbt(@Nonnull CompoundTag nbt, @Nonnull RegistryAccess registry)
    {
        VibrationSystem.Data data = null;
        int lastFreq = -1;

        if (nbt.contains(NbtKeys.VIBRATION))
        {
            lastFreq = nbt.getIntOr(NbtKeys.VIBRATION, 0);
        }

        if (nbt.contains(NbtKeys.LISTENER))
        {
            data = nbt.read(NbtKeys.LISTENER, VibrationSystem.Data.CODEC, registry.createSerializationContext(NbtOps.INSTANCE)).orElseGet(VibrationSystem.Data::new);
        }

        return Pair.of(lastFreq, data);
    }

    /**
     * Get the End Gateway's Exit Portal from NBT.
     * @param nbt ()
     * @return ()
     */
    public static Pair<Long, BlockPos> getExitPortalFromNbt(@Nonnull CompoundTag nbt)
    {
        long age = -1;
        BlockPos pos = BlockPos.ZERO;

        if (nbt.contains(NbtKeys.AGE))
        {
            age = nbt.getLongOr(NbtKeys.AGE, -1L);
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
    public static Pair<Pair<SignText, SignText>, Boolean> getSignTextFromNbt(@Nonnull CompoundTag nbt, @Nonnull RegistryAccess registry)
    {
        AtomicReference<SignText> front = new AtomicReference<>(null);
        AtomicReference<SignText> back = new AtomicReference<>(null);
        boolean waxed = false;

		try
		{
	        if (nbt.contains(NbtKeys.FRONT_TEXT))
	        {
	            SignText.DIRECT_CODEC.parse(registry.createSerializationContext(NbtOps.INSTANCE), Objects.requireNonNull(nbt.get(NbtKeys.FRONT_TEXT))).resultOrPartial().ifPresent(front::set);
	        }

	        if (nbt.contains(NbtKeys.BACK_TEXT))
	        {
	            SignText.DIRECT_CODEC.parse(registry.createSerializationContext(NbtOps.INSTANCE), Objects.requireNonNull(nbt.get(NbtKeys.BACK_TEXT))).resultOrPartial().ifPresent(back::set);
	        }
		}
		catch (Exception ignored) { }

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
    public static Pair<ItemStack, Integer> getBookFromNbt(@Nonnull CompoundTag nbt, @Nonnull RegistryAccess registry)
    {
        ItemStack book = ItemStack.EMPTY;
        int current = -1;

        if (nbt.contains(NbtKeys.BOOK))
        {
            book = nbt.read(NbtKeys.BOOK, ItemStack.CODEC, registry.createSerializationContext(NbtOps.INSTANCE)).orElse(ItemStack.EMPTY);
        }

        if (nbt.contains(NbtKeys.PAGE))
        {
            current = nbt.getIntOr(NbtKeys.PAGE, -1);
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
    public static Pair<ResolvableProfile, Pair<Identifier, Component>> getSkullDataFromNbt(@Nonnull CompoundTag nbt, @Nonnull RegistryAccess registry)
    {
        ResolvableProfile profile = null;
        Identifier note = null;
        Component name = null;

        if (nbt.contains(NbtKeys.NOTE))
        {
            note = nbt.read(NbtKeys.NOTE, Identifier.CODEC).orElse(null);
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
            profile = nbt.read(NbtKeys.PROFILE, ResolvableProfile.CODEC).orElse(null);
        }

        return Pair.of(profile, Pair.of(note, name));
    }

    /**
     * Get a Furnaces 'Used Recipes' from NBT.
     *
     * @param nbt ()
     * @return ()
     */
    public static Reference2IntOpenHashMap<ResourceKey<@NotNull Recipe<?>>> getRecipesUsedFromNbt(@Nonnull CompoundTag nbt)
    {
        Codec<Map<ResourceKey<@NotNull Recipe<?>>, Integer>> CODEC = Codec.unboundedMap(Recipe.KEY_CODEC, Codec.INT);
        Reference2IntOpenHashMap<ResourceKey<@NotNull Recipe<?>>> list = new Reference2IntOpenHashMap<>();

        if (nbt.contains(NbtKeys.RECIPES_USED))
        {
            /*
            NbtCompound compound = nbt.getCompound(NbtKeys.RECIPES_USED);

            for (String key : compound.getKeys())
            {
                list.put(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of(key)), compound.getInt(key));
            }
             */

            list.putAll(nbt.read(NbtKeys.RECIPES_USED, CODEC).orElse(Map.of()));
        }

        return list;
    }

    /**
     * Get the Redstone Output Signal from a Repeater
     * @param nbt ()
     * @return ()
     */
    public static int getOutputSignalFromNbt(@Nonnull CompoundTag nbt)
    {
        if (nbt.contains(NbtKeys.OUTPUT_SIGNAL))
        {
            return nbt.getIntOr(NbtKeys.OUTPUT_SIGNAL, 0);
        }

        return 0;
    }

	/**
	 * Get Trial Spawner Data from NBT
	 * @param nbt ()
	 * @return ()
	 */
    public static Optional<TrialSpawnerStateData.Packed> getTrialSpawnerDataFromNbt(@Nonnull CompoundTag nbt)
    {
        return NbtUtils.readFlatMap(nbt, TrialSpawnerStateData.Packed.MAP_CODEC);
    }
}
