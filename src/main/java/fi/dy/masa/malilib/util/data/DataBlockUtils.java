package fi.dy.masa.malilib.util.data;

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
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.Vibrations;

import fi.dy.masa.malilib.util.data.tag.CompoundData;
import fi.dy.masa.malilib.util.data.tag.util.DataOps;
import fi.dy.masa.malilib.util.data.tag.util.DataTypeUtils;
import fi.dy.masa.malilib.util.nbt.NbtKeys;
import fi.dy.masa.malilib.util.nbt.NbtView;

public class DataBlockUtils
{
	/**
	 * Get the Block Entity Type from the Data Tag.
	 *
	 * @param data ()
	 * @return ()
	 */
	public static @Nullable BlockEntityType<?> getBlockEntityType(@Nonnull CompoundData data)
	{
		if (data.contains(NbtKeys.ID, Constants.NBT.TAG_STRING))
		{
			return Registries.BLOCK_ENTITY_TYPE.getOptionalValue(Identifier.tryParse(data.getString(NbtKeys.ID))).orElse(null);
		}

		return null;
	}

	public static @Nullable Text getCustomName(@Nonnull CompoundData data, @Nonnull DynamicRegistryManager registry, String key)
	{
		NbtView view = NbtView.getReader(data, registry);
		return BlockEntity.tryParseCustomName(Objects.requireNonNull(view.getReader()), key);
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
		Identifier id = BlockEntityType.getId(type);

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
	public static Pair<RegistryEntry<StatusEffect>, RegistryEntry<StatusEffect>> getBeaconEffects(@Nonnull CompoundData data)
	{
		Set<RegistryEntry<StatusEffect>> effects = BeaconBlockEntity.EFFECTS_BY_LEVEL.stream().flatMap(Collection::stream).collect(Collectors.toSet());
		RegistryEntry<StatusEffect> primary = null;
		RegistryEntry<StatusEffect> secondary = null;

		if (data.contains(NbtKeys.PRIMARY_EFFECT, Constants.NBT.TAG_STRING))
		{
			primary = data.getCodec(NbtKeys.PRIMARY_EFFECT, Registries.STATUS_EFFECT.getEntryCodec()).filter(effects::contains).orElse(null);
		}

		if (data.contains(NbtKeys.SECONDARY_EFFECT, Constants.NBT.TAG_STRING))
		{
			secondary = data.getCodec(NbtKeys.SECONDARY_EFFECT, Registries.STATUS_EFFECT.getEntryCodec()).filter(effects::contains).orElse(null);
		}

		return Pair.of(primary, secondary);
	}

	/**
	 * Get the Beehive data from Data Tag.
	 * @param data ()
	 * @return ()
	 */
	public static Pair<List<BeehiveBlockEntity.BeeData>, BlockPos> getBeesData(@Nonnull CompoundData data)
	{
		List<BeehiveBlockEntity.BeeData> bees = new ArrayList<>();
		BlockPos flower = BlockPos.ORIGIN;

		if (data.contains(NbtKeys.BEES, Constants.NBT.TAG_LIST))
		{
			bees = data.getCodec(NbtKeys.BEES, BeehiveBlockEntity.BeeData.LIST_CODEC).orElse(List.of());
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
	public static Pair<Integer, Vibrations.ListenerData> getSkulkSensorVibrations(@Nonnull CompoundData data, @Nonnull DynamicRegistryManager registry)
	{
		Vibrations.ListenerData listener = null;
		int lastFreq = -1;

		if (data.contains(NbtKeys.VIBRATION, Constants.NBT.TAG_INT))
		{
			lastFreq = data.getInt(NbtKeys.VIBRATION);
		}

		if (data.contains(NbtKeys.LISTENER, Constants.NBT.TAG_COMPOUND))
		{
			listener = data.getCodec(NbtKeys.LISTENER, Vibrations.ListenerData.CODEC, registry.getOps(DataOps.INSTANCE)).orElseGet(Vibrations.ListenerData::new);
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
		BlockPos pos = BlockPos.ORIGIN;

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
	public static Pair<Pair<SignText, SignText>, Boolean> getSignText(@Nonnull CompoundData data, @Nonnull DynamicRegistryManager registry)
	{
		AtomicReference<SignText> front = new AtomicReference<>(null);
		AtomicReference<SignText> back = new AtomicReference<>(null);
		boolean waxed = false;

		if (data.contains(NbtKeys.FRONT_TEXT, Constants.NBT.TAG_COMPOUND))
		{
			CompoundData comp = data.getCompound(NbtKeys.FRONT_TEXT);
			SignText.CODEC.parse(registry.getOps(DataOps.INSTANCE), comp).resultOrPartial().ifPresent(front::set);
		}

		if (data.contains(NbtKeys.BACK_TEXT, Constants.NBT.TAG_COMPOUND))
		{
			CompoundData comp = data.getCompound(NbtKeys.BACK_TEXT);
			SignText.CODEC.parse(registry.getOps(DataOps.INSTANCE), comp).resultOrPartial().ifPresent(back::set);
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
	public static Pair<ItemStack, Integer> getBook(@Nonnull CompoundData data, @Nonnull DynamicRegistryManager registry)
	{
		ItemStack book = ItemStack.EMPTY;
		int current = -1;

		if (data.contains(NbtKeys.BOOK, Constants.NBT.TAG_COMPOUND))
		{
			book = data.getCodec(NbtKeys.BOOK, ItemStack.CODEC, registry.getOps(DataOps.INSTANCE)).orElse(ItemStack.EMPTY);
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
	public static Pair<ProfileComponent, Pair<Identifier, Text>> getSkullData(@Nonnull CompoundData data, @Nonnull DynamicRegistryManager registry)
	{
		ProfileComponent profile = null;
		Identifier note = null;
		Text name = null;

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
			profile = data.getCodec(NbtKeys.PROFILE, ProfileComponent.CODEC).orElse(null);
		}

		return Pair.of(profile, Pair.of(note, name));
	}

	/**
	 * Get a Furnaces 'Used Recipes' from Data Tag.
	 *
	 * @param data ()
	 * @return ()
	 */
	public static Reference2IntOpenHashMap<RegistryKey<Recipe<?>>> getRecipesUsed(@Nonnull CompoundData data)
	{
		Codec<Map<RegistryKey<Recipe<?>>, Integer>> CODEC = Codec.unboundedMap(Recipe.KEY_CODEC, Codec.INT);
		Reference2IntOpenHashMap<RegistryKey<Recipe<?>>> list = new Reference2IntOpenHashMap<>();

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
	public static Optional<TrialSpawnerData.Packed> getTrialSpawnerData(@Nonnull CompoundData data)
	{
		return DataTypeUtils.readFlatMap(data, TrialSpawnerData.Packed.CODEC);
	}
}
