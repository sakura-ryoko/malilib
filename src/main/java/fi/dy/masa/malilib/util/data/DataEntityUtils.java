package fi.dy.masa.malilib.util.data;

import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.ApiStatus;

import com.mojang.datafixers.util.Either;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.recipe.ServerRecipeManager;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerRecipeBook;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.VillagerData;

import fi.dy.masa.malilib.util.EntityUtils;
import fi.dy.masa.malilib.util.data.tag.CompoundData;
import fi.dy.masa.malilib.util.data.tag.ListData;
import fi.dy.masa.malilib.util.data.tag.converter.DataConverterNbt;
import fi.dy.masa.malilib.util.data.tag.util.DataOps;
import fi.dy.masa.malilib.util.data.tag.util.DataTypeUtils;
import fi.dy.masa.malilib.util.nbt.NbtKeys;

@ApiStatus.Experimental
public class DataEntityUtils
{
//	/**
//	 * Attempt to Invoke a custom version of writeData() without any passenger data.
//	 * @param entity ()
//	 * @param id ()
//	 * @return ()
//	 */
//	public static CompoundData invokeEntityDataTagNoPassengers(Entity entity, final int id)
//	{
//		return ((INbtEntityInvoker) entity).malilib$getDataTagWithId(id).orElseGet(CompoundData::new);
//	}

	/**
	 * Get an EntityType from Data Tag.
	 *
	 * @param data ()
	 * @return ()
	 */
	public static @Nullable EntityType<?> getEntityType(@Nonnull CompoundData data)
	{
		if (data.contains(NbtKeys.ID, Constants.NBT.TAG_STRING))
		{
			return Registries.ENTITY_TYPE.getOptionalValue(Identifier.tryParse(data.getString(NbtKeys.ID))).orElse(null);
		}

		return null;
	}

	/**
	 * Write an EntityType to Data Tag
	 *
	 * @param type ()
	 * @param dataIn ()
	 * @return ()
	 */
	public CompoundData setEntityType(EntityType<?> type, @Nullable CompoundData dataIn)
	{
		CompoundData data = new CompoundData();
		Identifier id = EntityType.getId(type);

		if (id != null)
		{
			if (dataIn != null)
			{
				dataIn.putString(NbtKeys.ID, id.toString());
				return dataIn;
			}
			else
			{
				data.putString(NbtKeys.ID, id.toString());
			}
		}

		return data;
	}

	/**
	 * Get EntityType Registry Reference
	 *
	 * @param id (id)
	 * @param registry (registry)
	 * @return ()
	 */
	public static RegistryEntry.Reference<EntityType<?>> getEntityTypeEntry(Identifier id, @Nonnull DynamicRegistryManager registry)
	{
		try
		{
			return registry.getOrThrow(Registries.ENTITY_TYPE.getKey()).getEntry(id).orElseThrow();
		}
		catch (Exception e)
		{
			return null;
		}
	}

	/**
	 * Get the AttributeContainer from Data Tag
	 *
	 * @param data ()
	 * @return ()
	 */
	@SuppressWarnings("unchecked")
	public static @Nullable AttributeContainer getAttributes(@Nonnull CompoundData data)
	{
		EntityType<?> type = getEntityType(data);

		if (type != null && data.contains(NbtKeys.ATTRIB, Constants.NBT.TAG_LIST))
		{
			AttributeContainer container = new AttributeContainer(DefaultAttributeRegistry.get((EntityType<? extends LivingEntity>) type));
			container.readNbt(DataConverterNbt.toVanillaList(data.getList(NbtKeys.ATTRIB)));
			return container;
		}

		return null;
	}

	public static double getAttributeBaseValue(@Nonnull CompoundData data, RegistryEntry<EntityAttribute> attribute)
	{
		AttributeContainer attributes = getAttributes(data);

		if (attributes != null)
		{
			return attributes.getBaseValue(attribute);
		}

		return -1;
	}

	/** Get a specified Attribute Value from Data Tag
	 *
	 * @param data ()
	 * @param attribute ()
	 * @return ()
	 */
	public static double getAttributeValue(@Nonnull CompoundData data, RegistryEntry<EntityAttribute> attribute)
	{
		AttributeContainer attributes = getAttributes(data);

		if (attributes != null)
		{
			return attributes.getValue(attribute);
		}

		return -1;
	}

	/**
	 * Get an entities' Health / Max Health from Data Tag.
	 *
	 * @param data ()
	 * @return ()
	 */
	public static Pair<Double, Double> getHealth(@Nonnull CompoundData data)
	{
		double health = 0f;
		double maxHealth;

		if (data.contains(NbtKeys.HEALTH, Constants.NBT.TAG_FLOAT))
		{
			health = data.getFloat(NbtKeys.HEALTH);
		}

		maxHealth = getAttributeValue(data, EntityAttributes.MAX_HEALTH);

		if (maxHealth < 0)
		{
			maxHealth = 20;
		}

		return Pair.of(health, maxHealth);
	}

	/**
	 * Get an entities Movement Speed, and Jump Strength attributes from Data Tag.
	 *
	 * @param data ()
	 * @return ()
	 */
	public static Pair<Double, Double> getSpeedAndJumpStrength(@Nonnull CompoundData data)
	{
		AttributeContainer container = getAttributes(data);
		double moveSpeed = 0d;
		double jumpStrength = 0d;

		if (container != null)
		{
			moveSpeed = container.getValue(EntityAttributes.MOVEMENT_SPEED);
			jumpStrength = container.getValue(EntityAttributes.JUMP_STRENGTH);
		}

		return Pair.of(moveSpeed, jumpStrength);
	}

	/**
	 * Get the Entity's UUID from NBT.
	 *
	 * @param data ()
	 * @return ()
	 */
	public static @Nullable UUID getUUIDFromNbt(@Nonnull CompoundData data)
	{
		NbtCompound nbt = DataConverterNbt.toVanillaCompound(data);

		if (nbt.containsUuid(NbtKeys.UUID))
		{
			return nbt.getUuid(NbtKeys.UUID);
		}

		return null;
	}

	/**
	 * Read the CustomName from Data Tag
	 *
	 * @param data ()
	 * @param registry ()
	 * @return ()
	 */
	public static @Nullable Text getCustomName(@Nonnull CompoundData data, @Nonnull DynamicRegistryManager registry)
	{
		if (data.contains(NbtKeys.CUSTOM_NAME, Constants.NBT.TAG_STRING))
		{
			String string = data.getString(NbtKeys.CUSTOM_NAME);

			try
			{
				return Text.Serialization.fromJson(string, registry);
			}
			catch (Exception ignored) { }
		}

		return null;
	}

	/**
	 * Write a CustomName to Data Tag.
	 *
	 * @param name ()
	 * @param registry ()
	 * @param dataIn ()
	 * @param key ()
	 * @return (Data Tag Out)
	 */
	public static CompoundData setCustomNameToDataTag(@Nonnull Text name, @Nonnull DynamicRegistryManager registry, @Nullable CompoundData dataIn, String key)
	{
		CompoundData data = dataIn != null ? dataIn.copy() : new CompoundData();

		try
		{
			if (dataIn != null)
			{
				dataIn.putString(NbtKeys.CUSTOM_NAME, Text.Serialization.toJsonString(name, registry));
				return dataIn;
			}
			else
			{
				data.putString(NbtKeys.CUSTOM_NAME, Text.Serialization.toJsonString(name, registry));
			}
		}
        catch (Exception ignored) {}

		return data;
	}

	/**
	 * Get a Map of all active Status Effects via Data Tag.
	 *
	 * @param data ()
	 * @return ()
	 */
	public static Map<RegistryEntry<StatusEffect>, StatusEffectInstance> getActiveStatusEffects(@Nonnull CompoundData data, @Nonnull DynamicRegistryManager registry)
	{
		Map<RegistryEntry<StatusEffect>, StatusEffectInstance> statusEffects = Maps.newHashMap();

		if (data.contains(NbtKeys.EFFECTS, Constants.NBT.TAG_LIST))
		{
			ListData list = data.getList(NbtKeys.EFFECTS);

			for (int i = 0; i < list.size(); i++)
			{
				StatusEffectInstance instance = StatusEffectInstance.fromNbt(DataConverterNbt.toVanillaCompound(list.getCompoundAt(i)));

				if (instance != null)
				{
					statusEffects.put(instance.getEffectType(), instance);
				}
			}
		}

		return statusEffects;
	}

	/**
	 * Get a ItemStack List of all Equipped Hand Items pieces.
	 *
	 * @param data ()
	 * @param registry ()
	 * @return ()
	 */
	public static DefaultedList<ItemStack> getHandItemsFromNbt(@Nonnull CompoundData data, @Nonnull DynamicRegistryManager registry)
	{
		DefaultedList<ItemStack> list = DefaultedList.ofSize(2, ItemStack.EMPTY);

		if (data.contains(NbtKeys.HAND_ITEMS, Constants.NBT.TAG_LIST))
		{
			ListData nbtList = data.getList(NbtKeys.HAND_ITEMS);

			for (int i = 0; i < list.size(); i++)
			{
				list.set(i, ItemStack.fromNbtOrEmpty(registry, DataConverterNbt.toVanillaCompound(nbtList.getCompoundAt(i))));
			}
		}

		return list;
	}

	/**
	 * Get a ItemStack List of all Equipped Armor pieces.
	 *
	 * @param data ()
	 * @param registry ()
	 * @return ()
	 */
	public static DefaultedList<ItemStack> getArmorItemsFromNbt(@Nonnull CompoundData data, @Nonnull DynamicRegistryManager registry)
	{
		DefaultedList<ItemStack> list = DefaultedList.ofSize(4, ItemStack.EMPTY);

		if (data.contains(NbtKeys.ARMOR_ITEMS, Constants.NBT.TAG_LIST))
		{
			ListData nbtList = data.getList(NbtKeys.ARMOR_ITEMS);

			for (int i = 0; i < list.size(); i++)
			{
				list.set(i, ItemStack.fromNbtOrEmpty(registry, DataConverterNbt.toVanillaCompound(nbtList.getCompoundAt(i))));
			}
		}

		return list;
	}

	/**
	 * Get the 'Body Armor Item' for the Horse or Wolf Armor.
	 *
	 * @param data ()
	 * @param registry ()
	 * @return ()
	 */
	public static ItemStack getBodyArmorFromNbt(@Nonnull CompoundData data, @Nonnull DynamicRegistryManager registry)
	{
		if (data.contains(NbtKeys.BODY_ARMOR, Constants.NBT.TAG_COMPOUND))
		{
			return ItemStack.fromNbtOrEmpty(registry, DataConverterNbt.toVanillaCompound(data.getCompound(NbtKeys.BODY_ARMOR)));
		}

		return ItemStack.EMPTY;
	}

	/**
	 * Get the Tamable Entity's Owner and if they have a Saddle Equipped.
	 *
	 * @param data ()
	 * @param registry ()
	 * @return ()
	 */
	public static Pair<UUID, ItemStack> getOwnerAndSaddle(@Nonnull CompoundData data, @Nonnull DynamicRegistryManager registry)
	{
		UUID owner = Util.NIL_UUID;
		ItemStack saddle = ItemStack.EMPTY;
		NbtCompound nbt = DataConverterNbt.toVanillaCompound(data);

		if (nbt.containsUuid(NbtKeys.OWNER))
		{
			owner = nbt.getUuid(NbtKeys.OWNER);
		}
		if (data.contains(NbtKeys.SADDLE, Constants.NBT.TAG_COMPOUND))
		{
			saddle = ItemStack.fromNbtOrEmpty(registry, nbt.getCompound(NbtKeys.SADDLE));
		}

		return Pair.of(owner, saddle);
	}

	/**
	 * Get the Common Age / ForcedAge data from Data Tag
	 *
	 * @param data ()
	 * @return ()
	 */
	public static Pair<Integer, Integer> getAge(@Nonnull CompoundData data)
	{
		int breedingAge = 0;
		int forcedAge = 0;

		if (data.contains(NbtKeys.AGE, Constants.NBT.TAG_INT))
		{
			breedingAge = data.getInt(NbtKeys.AGE);
		}

		if (data.contains(NbtKeys.FORCED_AGE, Constants.NBT.TAG_INT))
		{
			forcedAge = data.getInt(NbtKeys.FORCED_AGE);
		}

		return Pair.of(breedingAge, forcedAge);
	}

	/**
	 * Get the Merchant Trade Offer's Object from Data Tag
	 *
	 * @param data ()
	 * @param registry ()
	 * @return ()
	 */
	public static @Nullable TradeOfferList getTradeOffers(@Nonnull CompoundData data, @Nonnull DynamicRegistryManager registry)
	{
		if (data.contains(NbtKeys.OFFERS, Constants.NBT.TAG_LIST))
		{
			return data.getCodec(NbtKeys.OFFERS, TradeOfferList.CODEC, registry.getOps(DataOps.INSTANCE)).orElse(null);
		}

		return null;
	}

	/**
	 * Get the Villager Data object from Data Tag
	 *
	 * @param data ()
	 * @return ()
	 */
	public static @Nullable VillagerData getVillagerData(@Nonnull CompoundData data)
	{
		if (data.contains(NbtKeys.VILLAGER, Constants.NBT.TAG_COMPOUND))
		{
			return data.getCodec(NbtKeys.VILLAGER, VillagerData.CODEC).orElse(null);
		}

		return null;
	}

	/**
	 * Get the Zombie Villager cure timer.
	 *
	 * @param data ()
	 * @return ()
	 */
	public static Pair<Integer, UUID> getZombieConversionTimer(@Nonnull CompoundData data)
	{
		int timer = -1;
		UUID player = Util.NIL_UUID;

		if (data.contains(NbtKeys.ZOMBIE_CONVERSION, Constants.NBT.TAG_INT))
		{
			timer = data.getInt(NbtKeys.ZOMBIE_CONVERSION);
		}
		if (data.contains(NbtKeys.CONVERSION_PLAYER, Constants.NBT.TAG_INT_ARRAY))
		{
			player = DataTypeUtils.getUUIDCodec(data, NbtKeys.CONVERSION_PLAYER);
		}

		return Pair.of(timer, player);
	}

	/**
	 * Get Drowned conversion timer from a Zombie being in Water
	 *
	 * @param data ()
	 * @return ()
	 */
	public static Pair<Integer, Integer> getDrownedConversionTimer(@Nonnull CompoundData data)
	{
		int drowning = -1;
		int inWater = -1;

		if (data.contains(NbtKeys.DROWNED_CONVERSION, Constants.NBT.TAG_INT))
		{
			drowning = data.getInt(NbtKeys.DROWNED_CONVERSION);
		}
		if (data.contains(NbtKeys.IN_WATER, Constants.NBT.TAG_INT))
		{
			inWater = data.getInt(NbtKeys.IN_WATER);
		}

		return Pair.of(drowning, inWater);
	}

	/**
	 * Get Stray Conversion Timer from being in Powered Snow
	 *
	 * @param data ()
	 * @return ()
	 */
	public static int getStrayConversionTime(@Nonnull CompoundData data)
	{
		if (data.contains(NbtKeys.STRAY_CONVERSION, Constants.NBT.TAG_INT))
		{
			return data.getInt(NbtKeys.STRAY_CONVERSION);
		}

		return -1;
	}

	/**
	 * Try to get the Leash Data from Data Tag using LeashData (Not Fake)
	 * @param data ()
	 * @return ()
	 */
	@SuppressWarnings("unchecked")
	public static @Nullable EntityUtils.FakeLeashData getLeashData(@Nonnull CompoundData data)
	{
		EntityUtils.FakeLeashData leashData = null;

		if (data.contains(NbtKeys.LEASH, Constants.NBT.TAG_COMPOUND))
		{
			leashData = new EntityUtils.FakeLeashData(-1, null, Either.left(DataConverterNbt.toVanillaCompound(data.getCompound(NbtKeys.LEASH)).getUuid(NbtKeys.UUID)));
		}
		else if (data.contains(NbtKeys.LEASH, Constants.NBT.TAG_INT_ARRAY))
		{
			Either<UUID, BlockPos> either = (Either) NbtHelper.toBlockPos(DataConverterNbt.toVanillaCompound(data), NbtKeys.LEASH).map(Either::right).orElse(null);

			if (either != null)
			{
				return new EntityUtils.FakeLeashData(-1, null, either);
			}
		}

		return leashData;
	}

	/**
	 * Get the Panda Gene's from Data Tag
	 *
	 * @param data ()
	 * @return ()
	 */
	public static Pair<PandaEntity.Gene, PandaEntity.Gene> getPandaGenes(@Nonnull CompoundData data)
	{
		PandaEntity.Gene mainGene = null;
		PandaEntity.Gene hiddenGene = null;

		if (data.contains(NbtKeys.MAIN_GENE, Constants.NBT.TAG_STRING))
		{
			mainGene = data.getCodec(NbtKeys.MAIN_GENE, PandaEntity.Gene.CODEC).orElse(PandaEntity.Gene.NORMAL);
		}
		if (data.contains(NbtKeys.HIDDEN_GENE, Constants.NBT.TAG_STRING))
		{
			hiddenGene = data.getCodec(NbtKeys.HIDDEN_GENE, PandaEntity.Gene.CODEC).orElse(PandaEntity.Gene.NORMAL);
		}

		return Pair.of(mainGene, hiddenGene);
	}

	/**
	 * Get an Item Frame's Rotation and Facing Directions from Data Tag.
	 *
	 * @param data ()
	 * @return ()
	 */
	public static Pair<Direction, Direction> getItemFrameDirections(@Nonnull CompoundData data)
	{
		Direction facing = DataTypeUtils.readDirectionFromTag(data, NbtKeys.FACING_2);
		Direction rotation = null;

		if (data.contains(NbtKeys.ITEM_ROTATION, Constants.NBT.TAG_BYTE))
		{
			rotation = Direction.byId(data.getByte(NbtKeys.ITEM_ROTATION));
		}

		return Pair.of(facing, rotation);
	}

	/**
	 * Get a Painting's Direction and Variant from BNT.
	 *
	 * @param data ()
	 * @param registry ()
	 * @return ()
	 */
	public static Pair<Direction, PaintingVariant> getPaintingData(@Nonnull CompoundData data, @Nonnull DynamicRegistryManager registry)
	{
		Direction facing = DataTypeUtils.readDirectionFromTag(data, NbtKeys.FACING);
		RegistryEntry<PaintingVariant> variant = null;

		if (data.contains(NbtKeys.VARIANT, Constants.NBT.TAG_COMPOUND))
		{
			variant = PaintingEntity.VARIANT_ENTRY_CODEC.parse(registry.getOps(DataOps.INSTANCE), data).resultOrPartial().orElse(null);
		}

		return Pair.of(facing, variant != null ? variant.value() : null);
	}

	/**
	 * Get an Axolotl's Variant from Data Tag.
	 *
	 * @param data ()
	 * @return ()
	 */
	public static @Nullable AxolotlEntity.Variant getAxolotlVariant(@Nonnull CompoundData data)
	{
		if (data.contains(NbtKeys.VARIANT_2, Constants.NBT.TAG_INT))
		{
			return AxolotlEntity.Variant.byId(data.getInt(NbtKeys.VARIANT_2));
		}

		return null;
	}

	/**
	 * Get a Cat's Variant, and Collar Color from Data Tag.
	 *
	 * @param data ()
	 * @param registry ()
	 * @return ()
	 */
	public static Pair<RegistryKey<CatVariant>, DyeColor> getCatVariant(@Nonnull CompoundData data, @Nonnull DynamicRegistryManager registry)
	{
		RegistryKey<CatVariant> variantKey = null;
		DyeColor collar = null;

		if (data.contains(NbtKeys.VARIANT, Constants.NBT.TAG_STRING))
		{
			variantKey = RegistryKey.of(RegistryKeys.CAT_VARIANT, Identifier.tryParse(data.getString(NbtKeys.VARIANT)));

			if (variantKey == null)
			{
				variantKey = CatVariant.ALL_BLACK;
			}
		}
		if (data.contains(NbtKeys.COLLAR, Constants.NBT.TAG_ANY_NUMERIC))
		{
			collar = DyeColor.byId(data.getInt(NbtKeys.COLLAR));
		}

		return Pair.of(variantKey, collar);
	}

//	/**
//	 * Get a Chicken's Variant from Data Tag.
//	 *
//	 * @param data ()
//	 * @param registry ()
//	 * @return ()
//	 */
//	public static @Nullable RegistryKey<ChickenVariant> getChickenVariant(@Nonnull CompoundData data, @Nonnull DynamicRegistryManager registry)
//	{
//		if (data.contains(NbtKeys.VARIANT, Constants.NBT.TAG_STRING))
//		{
//			Optional<RegistryEntry<ChickenVariant>> variant = ChickenVariant.ENTRY_CODEC
//					.fieldOf(NbtKeys.VARIANT).codec()
//					.parse(registry.getOps(DataOps.INSTANCE), data)
//					.resultOrPartial();
//
//			return variant.map(entry -> entry.getKey().orElseThrow()).orElse(ChickenVariants.DEFAULT);
//		}
//
//		return null;
//	}
//
//	/**
//	 * Get a Cow's Variant from Data Tag.
//	 *
//	 * @param data ()
//	 * @param registry ()
//	 * @return ()
//	 */
//	public static @Nullable RegistryKey<CowVariant> getCowVariant(@Nonnull CompoundData data, @Nonnull DynamicRegistryManager registry)
//	{
//		if (data.contains(NbtKeys.VARIANT, Constants.NBT.TAG_STRING))
//		{
//			Optional<RegistryEntry<CowVariant>> variant = CowVariant.ENTRY_CODEC
//					.fieldOf(NbtKeys.VARIANT).codec()
//					.parse(registry.getOps(DataOps.INSTANCE), data)
//					.resultOrPartial();
//
//			return variant.map(entry -> entry.getKey().orElseThrow()).orElse(CowVariants.DEFAULT);
//		}
//
//		return null;
//	}
//
//	/**
//	 * Get a Mooshroom Variant from Data Tag.
//	 *
//	 * @param data ()
//	 * @return ()
//	 */
//	public static @Nullable MooshroomEntity.Variant getMooshroomVariant(@Nonnull CompoundData data)
//	{
//		if (data.contains(NbtKeys.TYPE_2, Constants.NBT.TAG_STRING))
//		{
//			return data.getCodec(NbtKeys.TYPE_2, MooshroomEntity.Variant.CODEC).orElse(MooshroomEntity.Variant.RED);
//		}
//
//		return null;
//	}

	/**
	 * Get a Frog's Variant from Data Tag.
	 *
	 * @param data ()
	 * @param registry ()
	 * @return ()
	 */
	public static @Nullable RegistryKey<FrogVariant> getFrogVariant(@Nonnull CompoundData data, @Nonnull DynamicRegistryManager registry)
	{
		if (data.contains(NbtKeys.VARIANT, Constants.NBT.TAG_STRING))
		{
			RegistryKey<FrogVariant> variantKey = RegistryKey.of(RegistryKeys.FROG_VARIANT, Identifier.tryParse(data.getString(NbtKeys.VARIANT)));

			if (variantKey == null)
			{
				variantKey = FrogVariant.TEMPERATE;
			}

			return variantKey;
		}

		return null;
	}

	/**
	 * Get a Horse's Variant (Color, Markings) from Data Tag.
	 * @param data ()
	 * @return ()
	 */
	public static Pair<HorseColor, HorseMarking> getHorseVariant(@Nonnull CompoundData data)
	{
		HorseColor color = null;
		HorseMarking marking = null;

		if (data.contains(NbtKeys.VARIANT_2, Constants.NBT.TAG_INT))
		{
			int variant = data.getInt(NbtKeys.VARIANT_2);
			color = HorseColor.byId(variant & 255);
			marking = HorseMarking.byIndex((variant & '\uff00') >> 8);
		}

		return Pair.of(color, marking);
	}

	/**
	 * Get a Parrot's Variant from Data Tag.
	 *
	 * @param data ()
	 * @return ()
	 */
	public static @Nullable ParrotEntity.Variant getParrotVariant(@Nonnull CompoundData data)
	{
		if (data.contains(NbtKeys.VARIANT_2, Constants.NBT.TAG_INT))
		{
			return ParrotEntity.Variant.byIndex(data.getInt(NbtKeys.VARIANT_2));
		}

		return null;
	}

	/**
	 * Get a Tropical Fish Variant from Data Tag.
	 *
	 * @param data ()
	 * @return ()
	 */
	public static @Nullable TropicalFishEntity.Variety getFishVariantFromNbt(@Nonnull CompoundData data)
	{
		if (data.contains(NbtKeys.VARIANT_2, Constants.NBT.TAG_INT))
		{
			return TropicalFishEntity.Variety.fromId(data.getInt(NbtKeys.VARIANT_2) & '\uffff');
		}
		else if (data.contains(NbtKeys.BUCKET_VARIANT, Constants.NBT.TAG_INT))
		{
			return TropicalFishEntity.Variety.fromId(data.getInt(NbtKeys.BUCKET_VARIANT) & '\uffff');
		}

		return null;
	}

	/**
	 * Get a Wolves' Variant and Collar Color from Data Tag.
	 *
	 * @param data ()
	 * @return ()
	 */
	public static Pair<RegistryKey<WolfVariant>, DyeColor> getWolfVariant(@Nonnull CompoundData data, @Nonnull DynamicRegistryManager registry)
	{
		RegistryKey<WolfVariant> variantKey = null;
		DyeColor collar = null;

		if (data.contains(NbtKeys.VARIANT, Constants.NBT.TAG_STRING))
		{
			variantKey = RegistryKey.of(RegistryKeys.WOLF_VARIANT, Identifier.tryParse(data.getString(NbtKeys.VARIANT)));
		}
		if (data.contains(NbtKeys.COLLAR, Constants.NBT.TAG_ANY_NUMERIC))
		{
			collar = DyeColor.byId(data.getInt(NbtKeys.COLLAR));
		}

		if (variantKey == null)
		{
			variantKey = WolfVariants.DEFAULT;
		}

		if (collar == null)
		{
			collar = DyeColor.RED;
		}

		return Pair.of(variantKey, collar);
	}

//	/**
//	 * Get a Wolves' Sound Type Variant from Data Tag.
//	 *
//	 * @param data ()
//	 * @return ()
//	 */
//	public static @Nullable RegistryKey<WolfSoundVariant> getWolfSoundType(@Nonnull CompoundData data, @Nonnull DynamicRegistryManager registry)
//	{
//		if (data.contains(NbtKeys.SOUND_VARIANT, Constants.NBT.TAG_STRING))
//		{
//			RegistryEntry.Reference<WolfSoundVariant> soundVariant = registry.getOrThrow(RegistryKeys.WOLF_SOUND_VARIANT).getEntry(Identifier.tryParse(data.getString(NbtKeys.SOUND_VARIANT))).orElse(null);
//
//			if (soundVariant != null)
//			{
//				return soundVariant.registryKey();
//			}
//		}
//
//		return null;
//	}

	/**
	 * Get a Sheep's Color from Data Tag.
	 *
	 * @param data ()
	 * @return ()
	 */
	public static @Nullable DyeColor getSheepColor(@Nonnull CompoundData data)
	{
		if (data.contains(NbtKeys.COLOR, Constants.NBT.TAG_BYTE))
		{
			return DyeColor.byId(data.getByte(NbtKeys.COLOR));
		}

		return null;
	}

	/**
	 * Get a Rabbit's Variant type from Data Tag.
	 *
	 * @param data ()
	 * @return ()
	 */
	public static @Nullable RabbitEntity.RabbitType getRabbitType(@Nonnull CompoundData data)
	{
		if (data.contains(NbtKeys.RABBIT_TYPE, Constants.NBT.TAG_INT))
		{
			return RabbitEntity.RabbitType.byId(data.getInt(NbtKeys.RABBIT_TYPE));
		}

		return null;
	}

	/**
	 * Get a Llama's Variant type from Data Tag.
	 *
	 * @param data ()
	 * @return ()
	 */
	public static Pair<LlamaEntity.Variant, Integer> getLlamaType(@Nonnull CompoundData data)
	{
		LlamaEntity.Variant variant = null;
		int strength = -1;

		if (data.contains(NbtKeys.VARIANT_2, Constants.NBT.TAG_INT))
		{
			variant = LlamaEntity.Variant.byId(data.getInt(NbtKeys.VARIANT_2));
		}

		if (data.contains(NbtKeys.STRENGTH, Constants.NBT.TAG_INT))
		{
			strength = data.getInt(NbtKeys.STRENGTH);
		}

		return Pair.of(variant, strength);
	}

//	/**
//	 * Get a Pig's Variant type from Data Tag.
//	 *
//	 * @param data ()
//	 * @param registry ()
//	 * @return ()
//	 */
//	public static @Nullable RegistryKey<PigVariant> getPigVariant(@Nonnull CompoundData data, @Nonnull DynamicRegistryManager registry)
//	{
//		if (data.contains(NbtKeys.VARIANT, Constants.NBT.TAG_STRING))
//		{
//			Optional<RegistryEntry.Reference<PigVariant>> opt = registry.getOrThrow(RegistryKeys.PIG_VARIANT).getEntry(Identifier.tryParse(data.getString(NbtKeys.VARIANT)));
//
//			if (opt.isPresent())
//			{
//				return opt.get().registryKey();
//			}
//
//			return PigVariants.DEFAULT;
//		}
//
//		return null;
//	}

	/**
	 * Get a Fox's Variant type from Data Tag.
	 *
	 * @param data ()
	 * @return ()
	 */
	public static @Nullable FoxEntity.Type getFoxVariant(@Nonnull CompoundData data)
	{
		if (data.contains(NbtKeys.FOX_TYPE, Constants.NBT.TAG_STRING))
		{
			return FoxEntity.Type.byName(data.getString(NbtKeys.FOX_TYPE));
		}

		return null;
	}

	/**
	 * Get a Salmon's Variant type from Data Tag.
	 *
	 * @param data ()
	 * @return ()
	 */
	public static @Nullable SalmonEntity.Variant getSalmonVariant(@Nonnull CompoundData data)
	{
		if (data.contains(NbtKeys.SALMON_TYPE, Constants.NBT.TAG_STRING))
		{
			return SalmonEntity.Variant.CODEC.byId(data.getString(NbtKeys.SALMON_TYPE), SalmonEntity.Variant.MEDIUM);
		}

		return null;
	}

	/**
	 * Get a Dolphin's TreasurePos and other data from Data Tag.
	 *
	 * @param data ()
	 * @return ()
	 */
	public static Triple<BlockPos, Integer, Boolean> getDolphinDataFromNbt(@Nonnull CompoundData data)
	{
		BlockPos treasure = BlockPos.ORIGIN;
		int moist = -1;
		boolean hasFish = false;

		if (data.contains(NbtKeys.TREASURE_X, Constants.NBT.TAG_INT) &&
			data.contains(NbtKeys.TREASURE_Y, Constants.NBT.TAG_INT) &&
			data.contains(NbtKeys.TREASURE_Z, Constants.NBT.TAG_INT))
		{
			treasure = new BlockPos(data.getInt(NbtKeys.TREASURE_X), data.getInt(NbtKeys.TREASURE_Y), data.getInt(NbtKeys.TREASURE_Z));
		}

		if (data.contains(NbtKeys.MOISTNESS, Constants.NBT.TAG_INT))
		{
			moist = data.getInt(NbtKeys.MOISTNESS);
		}

		if (data.contains(NbtKeys.GOT_FISH, Constants.NBT.TAG_BYTE))
		{
			hasFish = data.getBoolean(NbtKeys.GOT_FISH);
		}

		return Triple.of(treasure, moist, hasFish);
	}

	/**
	 * Get a player's Experience values from Data Tag.
	 *
	 * @param data ()
	 * @return ()
	 */
	public static Triple<Integer, Integer, Float> getPlayerExp(@Nonnull CompoundData data)
	{
		int level = -1;
		int total = -1;
		float progress = 0.0f;

		if (data.contains(NbtKeys.EXP_LEVEL, Constants.NBT.TAG_INT))
		{
			level = data.getInt(NbtKeys.EXP_LEVEL);
		}
		if (data.contains(NbtKeys.EXP_TOTAL, Constants.NBT.TAG_INT))
		{
			total = data.getInt(NbtKeys.EXP_TOTAL);
		}
		if (data.contains(NbtKeys.EXP_PROGRESS, Constants.NBT.TAG_FLOAT))
		{
			progress = data.getFloat(NbtKeys.EXP_PROGRESS);
		}

		return Triple.of(level, total, progress);
	}

	/**
	 * Get a Player's Hunger Manager from Data Tag.
	 *
	 * @param data ()
	 * @return ()
	 */
	public static @Nullable HungerManager getPlayerHunger(@Nonnull CompoundData data, @Nonnull DynamicRegistryManager registry)
	{
		HungerManager hunger = null;

		if (data.containsLenient(NbtKeys.FOOD_LEVEL))
		{
			hunger = new HungerManager();
			hunger.readNbt(DataConverterNbt.toVanillaCompound(data));
		}

		return hunger;
	}

	/**
	 * Get a Players' Unlocked Recipe Book from Data Tag.  (Server Side only)
	 * @param data ()
	 * @param manager ()
	 * @return ()
	 */
	public static @Nullable ServerRecipeBook getPlayerRecipeBook(@Nonnull CompoundData data, @Nonnull ServerRecipeManager manager)
	{
		ServerRecipeBook book = null;

		if (data.contains(NbtKeys.RECIPE_BOOK, Constants.NBT.TAG_COMPOUND))
		{
			book = new ServerRecipeBook(manager::forEachRecipeDisplay);
			NbtCompound nbt = DataConverterNbt.toVanillaCompound(data.getCompoundOrDefault(NbtKeys.RECIPE_BOOK, new CompoundData()));
			book.readNbt(nbt.getCompound(NbtKeys.RECIPE_BOOK), (key) -> manager.get(key).isPresent());
		}

		return book;
	}

//	/**
//	 * Get a Mob's Home Pos and Radius from Data Tag
//	 * @param data ()
//	 * @return ()
//	 */
//	public static Pair<BlockPos, Integer> getHomePos(@Nonnull CompoundData data)
//	{
//		BlockPos pos = BlockPos.ORIGIN;
//		int radius = -1;
//
//		if (data.containsLenient(NbtKeys.HOME_POS))
//		{
//			pos = data.getCodec(NbtKeys.HOME_POS, BlockPos.CODEC).orElse(BlockPos.ORIGIN);
//		}
//
//		if (data.contains(NbtKeys.HOME_RADIUS, Constants.NBT.TAG_INT))
//		{
//			radius = data.getInt(NbtKeys.HOME_RADIUS);
//		}
//
//		return Pair.of(pos, radius);
//	}

//	/**
//	 * Get a Copper Golem's Weathering Data from Data Tag
//	 * @param data ()
//	 * @return ()
//	 */
//	public static Pair<Oxidizable.OxidationLevel, Long> getWeatheringData(@Nonnull CompoundData data)
//	{
//		Oxidizable.OxidationLevel level = Oxidizable.OxidationLevel.UNAFFECTED;
//		long age = -1L;
//
//		if (data.contains(NbtKeys.WEATHER_STATE, Constants.NBT.TAG_STRING))
//		{
//			level = data.getCodec(NbtKeys.WEATHER_STATE, Oxidizable.OxidationLevel.CODEC).orElse(Oxidizable.OxidationLevel.UNAFFECTED);
//		}
//
//		if (data.contains(NbtKeys.NEXT_WEATHER_AGE, Constants.NBT.TAG_LONG))
//		{
//			age = data.getLong(NbtKeys.NEXT_WEATHER_AGE);
//		}
//
//		return Pair.of(level, age);
//	}
}
