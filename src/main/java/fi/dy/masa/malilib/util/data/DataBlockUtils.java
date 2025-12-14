package fi.dy.masa.malilib.util.data;

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

import fi.dy.masa.malilib.util.data.tag.CompoundData;
import fi.dy.masa.malilib.util.data.tag.converter.DataConverterNbt;
import fi.dy.masa.malilib.util.data.tag.util.DataOps;
import fi.dy.masa.malilib.util.data.tag.util.DataTypeUtils;
import fi.dy.masa.malilib.util.nbt.NbtKeys;
import fi.dy.masa.malilib.util.nbt.NbtView;

public class DataBlockUtils
{
	/**
	 * Get the Block Entity Type from the Data Tag Tag.
	 *
	 * @param data ()
	 * @return ()
	 */
	public static @Nullable BlockEntityType<?> getBlockEntityType(@Nonnull CompoundData data)
	{
		if (data.contains(NbtKeys.ID, Constants.NBT.TAG_STRING))
		{
			return BuiltInRegistries.BLOCK_ENTITY_TYPE.getOptional(Identifier.tryParse(data.getString(NbtKeys.ID))).orElse(null);
		}

		return null;
	}

	public static @Nullable Component getCustomName(@Nonnull CompoundData data, @Nonnull RegistryAccess registry, String key)
	{
		NbtView view = NbtView.getReader(data, registry);
		return BlockEntity.parseCustomNameSafe(Objects.requireNonNull(view.getReader()), key);
	}

	/**
	 * Write the Block Entity ID tag.
	 *
	 * @param type ()
	 * @param dataIn ()
	 * @return ()
	 */
	public static CompoundData setBlockEntityType(BlockEntityType<?> type, @Nullable CompoundData dataIn)
	{
		CompoundData data = new CompoundData();
		Identifier id = BlockEntityType.getKey(type);

		if (id != null)
		{
			return Objects.requireNonNullElse(dataIn, data).putString(NbtKeys.ID, id.toString());
		}

		return data;
	}

	/**
	 * Read the Crafter's "locked slots" from Data Tag
	 *
	 * @param data ()
	 * @return ()
	 */
	public static Set<Integer> getDisabledSlots(@Nonnull CompoundData data)
	{
		Set<Integer> list = new HashSet<>();

		if (data.contains(NbtKeys.DISABLED_SLOTS, Constants.NBT.TAG_INT_ARRAY))
		{
			int[] is = data.getIntArray(NbtKeys.DISABLED_SLOTS);

			for (int j : is)
			{
				list.add(j);
			}
		}

		return list;
	}

	/**
	 * Get the Beacon's Effects from Data Tag.
	 *
	 * @param data ()
	 * @return ()
	 */
	public static Pair<Holder<@NotNull MobEffect>, Holder<@NotNull MobEffect>> getBeaconEffects(@Nonnull CompoundData data)
	{
		Set<Holder<@NotNull MobEffect>> effects = BeaconBlockEntity.BEACON_EFFECTS.stream().flatMap(Collection::stream).collect(Collectors.toSet());
		Holder<@NotNull MobEffect> primary = null;
		Holder<@NotNull MobEffect> secondary = null;

		if (data.contains(NbtKeys.PRIMARY_EFFECT, Constants.NBT.TAG_STRING))
		{
			primary = data.getCodec(NbtKeys.PRIMARY_EFFECT, BuiltInRegistries.MOB_EFFECT.holderByNameCodec()).filter(effects::contains).orElse(null);
		}

		if (data.contains(NbtKeys.SECONDARY_EFFECT, Constants.NBT.TAG_STRING))
		{
			secondary = data.getCodec(NbtKeys.SECONDARY_EFFECT, BuiltInRegistries.MOB_EFFECT.holderByNameCodec()).filter(effects::contains).orElse(null);
		}

		return Pair.of(primary, secondary);
	}

	/**
	 * Get the Beehive data from Data Tag.
	 * @param data ()
	 * @return ()
	 */
	public static Pair<List<BeehiveBlockEntity.Occupant>, BlockPos> getBeesData(@Nonnull CompoundData data)
	{
		List<BeehiveBlockEntity.Occupant> bees = new ArrayList<>();
		BlockPos flower = BlockPos.ZERO;

		if (data.contains(NbtKeys.BEES, Constants.NBT.TAG_LIST))
		{
			bees = data.getCodec(NbtKeys.BEES, BeehiveBlockEntity.Occupant.LIST_CODEC).orElse(List.of());
		}

		if (data.containsLenient(NbtKeys.FLOWER))
		{
			flower = DataTypeUtils.getPosCodec(data, NbtKeys.FLOWER);
		}

		return Pair.of(bees, flower);
	}

	/**
	 * Get the Skulk Sensor Vibration / Listener data from Data Tag.
	 *
	 * @param data ()
	 * @param registry ()
	 * @return ()
	 */
	public static Pair<Integer, VibrationSystem.Data> getSkulkSensorVibrations(@Nonnull CompoundData data, @Nonnull RegistryAccess registry)
	{
		VibrationSystem.Data listener = null;
		int lastFreq = -1;

		if (data.contains(NbtKeys.VIBRATION, Constants.NBT.TAG_INT))
		{
			lastFreq = data.getInt(NbtKeys.VIBRATION);
		}

		if (data.contains(NbtKeys.LISTENER, Constants.NBT.TAG_COMPOUND))
		{
			listener = data.getCodec(NbtKeys.LISTENER, VibrationSystem.Data.CODEC, registry.createSerializationContext(DataOps.INSTANCE)).orElseGet(VibrationSystem.Data::new);
		}

		return Pair.of(lastFreq, listener);
	}

	/**
	 * Get the End Gateway's Exit Portal from Data Tag.
	 * @param data ()
	 * @return ()
	 */
	public static Pair<Long, BlockPos> getExitPortal(@Nonnull CompoundData data)
	{
		long age = -1;
		BlockPos pos = BlockPos.ZERO;

		if (data.contains(NbtKeys.AGE, Constants.NBT.TAG_LONG))
		{
			age = data.getLong(NbtKeys.AGE);
		}

		if (data.containsLenient(NbtKeys.EXIT))
		{
			pos = DataTypeUtils.getPosCodec(data, NbtKeys.EXIT);
		}

		return Pair.of(age, pos);
	}

	/**
	 * Get a Sign's Text from Data Tag.
	 *
	 * @param data ()
	 * @param registry ()
	 * @return ()
	 */
	public static Pair<Pair<SignText, SignText>, Boolean> getSignText(@Nonnull CompoundData data, @Nonnull RegistryAccess registry)
	{
		AtomicReference<SignText> front = new AtomicReference<>(null);
		AtomicReference<SignText> back = new AtomicReference<>(null);
		boolean waxed = false;

		if (data.contains(NbtKeys.FRONT_TEXT, Constants.NBT.TAG_COMPOUND))
		{
			CompoundData comp = data.getCompound(NbtKeys.FRONT_TEXT);
			SignText.DIRECT_CODEC.parse(registry.createSerializationContext(NbtOps.INSTANCE), DataConverterNbt.toVanillaCompound(comp)).resultOrPartial().ifPresent(front::set);
		}

		if (data.contains(NbtKeys.BACK_TEXT, Constants.NBT.TAG_COMPOUND))
		{
			CompoundData comp = data.getCompound(NbtKeys.BACK_TEXT);
			SignText.DIRECT_CODEC.parse(registry.createSerializationContext(NbtOps.INSTANCE), DataConverterNbt.toVanillaCompound(comp)).resultOrPartial().ifPresent(back::set);
		}

		if (data.contains(NbtKeys.WAXED, Constants.NBT.TAG_BYTE))
		{
			waxed = data.getBoolean(NbtKeys.WAXED);
		}

		return Pair.of(Pair.of(front.get(), back.get()), waxed);
	}

	/**
	 * Get a Lectern's Book and Page number.
	 *
	 * @param data ()
	 * @param registry ()
	 * @return ()
	 */
	public static Pair<ItemStack, Integer> getBook(@Nonnull CompoundData data, @Nonnull RegistryAccess registry)
	{
		ItemStack book = ItemStack.EMPTY;
		int current = -1;

		if (data.contains(NbtKeys.BOOK, Constants.NBT.TAG_COMPOUND))
		{
			book = data.getCodec(NbtKeys.BOOK, ItemStack.CODEC, registry.createSerializationContext(DataOps.INSTANCE)).orElse(ItemStack.EMPTY);
		}

		if (data.contains(NbtKeys.PAGE, Constants.NBT.TAG_INT))
		{
			current = data.getInt(NbtKeys.PAGE);
		}

		return Pair.of(book, current);
	}

	/**
	 * Get a Skull's Profile Data Component from Data Tag, and Custom Name.
	 *
	 * @param data ()
	 * @param registry ()
	 * @return ()
	 */
	public static Pair<ResolvableProfile, Pair<Identifier, Component>> getSkullData(@Nonnull CompoundData data, @Nonnull RegistryAccess registry)
	{
		ResolvableProfile profile = null;
		Identifier note = null;
		Component name = null;

		if (data.contains(NbtKeys.NOTE, Constants.NBT.TAG_STRING))
		{
			note = data.getCodec(NbtKeys.NOTE, Identifier.CODEC).orElse(null);
		}

		if (data.contains(NbtKeys.SKULL_NAME, Constants.NBT.TAG_COMPOUND))
		{
			name = getCustomName(data, registry, NbtKeys.SKULL_NAME);
		}

		if (data.contains(NbtKeys.PROFILE, Constants.NBT.TAG_COMPOUND))
		{
			profile = data.getCodec(NbtKeys.PROFILE, ResolvableProfile.CODEC).orElse(null);
		}

		return Pair.of(profile, Pair.of(note, name));
	}

	/**
	 * Get a Furnaces 'Used Recipes' from Data Tag.
	 *
	 * @param data ()
	 * @return ()
	 */
	public static Reference2IntOpenHashMap<ResourceKey<@NotNull Recipe<?>>> getRecipesUsed(@Nonnull CompoundData data)
	{
		Codec<Map<ResourceKey<@NotNull Recipe<?>>, Integer>> CODEC = Codec.unboundedMap(Recipe.KEY_CODEC, Codec.INT);
		Reference2IntOpenHashMap<ResourceKey<@NotNull Recipe<?>>> list = new Reference2IntOpenHashMap<>();

		if (data.containsLenient(NbtKeys.RECIPES_USED))
		{
			list.putAll(data.getCodec(NbtKeys.RECIPES_USED, CODEC).orElse(Map.of()));
		}

		return list;
	}

	/**
	 * Get the Redstone Output Signal from a Repeater
	 * @param data ()
	 * @return ()
	 */
	public static int getOutputSignal(@Nonnull CompoundData data)
	{
		if (data.contains(NbtKeys.OUTPUT_SIGNAL, Constants.NBT.TAG_INT))
		{
			return data.getInt(NbtKeys.OUTPUT_SIGNAL);
		}

		return 0;
	}

	/**
	 * Get Trial Spawner Data from Data Tag
	 * @param data ()
	 * @return ()
	 */
	public static Optional<TrialSpawnerStateData.Packed> getTrialSpawnerData(@Nonnull CompoundData data)
	{
		return DataTypeUtils.readFlatMap(data, TrialSpawnerStateData.Packed.MAP_CODEC);
	}
}
