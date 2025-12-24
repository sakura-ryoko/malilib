package fi.dy.masa.malilib.util.data;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import net.minecraft.block.Oxidizable;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.ServerRecipeManager;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerRecipeBook;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.VillagerData;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.util.data.tag.BaseData;
import fi.dy.masa.malilib.util.data.tag.CompoundData;
import fi.dy.masa.malilib.util.data.tag.ListData;
import fi.dy.masa.malilib.util.data.tag.util.DataOps;
import fi.dy.masa.malilib.util.data.tag.util.DataTypeUtils;
import fi.dy.masa.malilib.util.nbt.INbtEntityInvoker;
import fi.dy.masa.malilib.util.nbt.NbtKeys;
import fi.dy.masa.malilib.util.nbt.NbtView;

public class DataEntityUtils
{
	/**
	 * Attempt to Invoke a custom version of writeData() without any passenger data.
	 * @param entity ()
	 * @param id ()
	 * @return ()
	 */
	public static CompoundData invokeEntityDataTagNoPassengers(Entity entity, final int id)
	{
		return ((INbtEntityInvoker) entity).malilib$getDataTagWithId(id).orElseGet(CompoundData::new);
	}

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
			ListData list = data.getList(NbtKeys.ATTRIB);

			container.unpack(EntityAttributeInstance.Packed.LIST_CODEC.parse(DataOps.INSTANCE, list).getPartialOrThrow());
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
	 * Read the CustomName from Data Tag
	 *
	 * @param data ()
	 * @param registry ()
	 * @return ()
	 */
	public static @Nullable Text getCustomName(@Nonnull CompoundData data, @Nonnull DynamicRegistryManager registry)
	{
		if (data.contains(NbtKeys.CUSTOM_NAME, Constants.NBT.TAG_COMPOUND))
		{
			return data.getCodec(NbtKeys.CUSTOM_NAME, TextCodecs.CODEC, registry.getOps(DataOps.INSTANCE)).orElse(null);
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

		if (key == null || key.isEmpty())
		{
			key = NbtKeys.CUSTOM_NAME;
		}

		return data.putCodec(key, TextCodecs.CODEC, registry.getOps(DataOps.INSTANCE), name);
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
			List<StatusEffectInstance> list = data.getCodec(NbtKeys.EFFECTS, StatusEffectInstance.CODEC.listOf(), registry.getOps(DataOps.INSTANCE)).orElse(List.of());

			for (StatusEffectInstance instance : list)
			{
				statusEffects.put(instance.getEffectType(), instance);
			}
		}

		return statusEffects;
	}

	/**
	 * Decode Equipment Slot values from Data Tag.
	 *
	 * @param data ()
	 * @param registry ()
	 * @return ()
	 */
	public static @Nullable EntityEquipment getEquipmentSlots(@Nonnull CompoundData data, @Nonnull DynamicRegistryManager registry)
	{
		if (data.contains(NbtKeys.EQUIPMENT, Constants.NBT.TAG_COMPOUND))
		{
			CompoundData comp = data.getCompound(NbtKeys.EQUIPMENT);
			Optional<EntityEquipment> opt = EntityEquipment.CODEC.parse(registry.getOps(DataOps.INSTANCE), comp).result();

			if (opt.isPresent())
			{
				return opt.get();
			}
		}

		return null;
	}

	/**
	 * Encode Equipment Slots to Data Tag.
	 *
	 * @param equipment ()
	 * @param registry ()
	 * @return ()
	 */
	public static @Nullable BaseData setEquipmentSlotsToDataTag(@Nonnull EntityEquipment equipment, @Nonnull DynamicRegistryManager registry)
	{
		try
		{
			return EntityEquipment.CODEC.encodeStart(registry.getOps(DataOps.INSTANCE), equipment).getOrThrow();
		}
		catch (Exception err)
		{
			MaLiLib.LOGGER.warn("setEquipmentSlotsToNbt(): Failed to parse Equipment Slots Object; {}", err.getMessage());
			return null;
		}
	}

	/**
	 * Get a ItemStack List of all Equipped Hand Items.
	 * 0/1 [{MainHand}, {OffHand}]
	 *
	 * @param data ()
	 * @param registry ()
	 * @return ()
	 */
	public static DefaultedList<ItemStack> getHandItems(@Nonnull CompoundData data, @Nonnull DynamicRegistryManager registry)
	{
		DefaultedList<ItemStack> list = DefaultedList.ofSize(2, ItemStack.EMPTY);
		EntityEquipment equipment = getEquipmentSlots(data, registry);

		if (equipment != null)
		{
			ItemStack mainHand = equipment.get(EquipmentSlot.MAINHAND);
			ItemStack offHand = equipment.get(EquipmentSlot.OFFHAND);

			if (mainHand != null && !mainHand.isEmpty())
			{
				list.set(0, mainHand.copy());
			}

			if (offHand != null && !offHand.isEmpty())
			{
				list.set(1, offHand.copy());
			}
		}

		return list;
	}

	/**
	 * Get a ItemStack List of all Equipped Humanoid Armor Slots
	 * 0/1/2/3 [{Head}, {Chest}, {Legs}, {Feet}]
	 *
	 * @param data ()
	 * @param registry ()
	 * @return ()
	 */
	public static DefaultedList<ItemStack> getHumanoidArmor(@Nonnull CompoundData data, @Nonnull DynamicRegistryManager registry)
	{
		DefaultedList<ItemStack> list = DefaultedList.ofSize(4, ItemStack.EMPTY);
		EntityEquipment equipment = getEquipmentSlots(data, registry);

		if (equipment != null)
		{
			ItemStack head = equipment.get(EquipmentSlot.HEAD);
			ItemStack chest = equipment.get(EquipmentSlot.CHEST);
			ItemStack legs = equipment.get(EquipmentSlot.LEGS);
			ItemStack feet = equipment.get(EquipmentSlot.FEET);

			if (head != null && !head.isEmpty())
			{
				list.set(0, head.copy());
			}

			if (chest != null && !chest.isEmpty())
			{
				list.set(1, chest.copy());
			}

			if (legs != null && !legs.isEmpty())
			{
				list.set(2, legs.copy());
			}

			if (feet != null && !feet.isEmpty())
			{
				list.set(3, feet.copy());
			}
		}

		return list;
	}

	/**
	 * Get a ItemStack List of all Equipped Horse/Wolf/Llama/Camel/Etc Slots
	 * 0/1 [{BodyArmor}, {Saddle}]
	 *
	 * @param data ()
	 * @param registry ()
	 * @return ()
	 */
	public static DefaultedList<ItemStack> getHorseEquipment(@Nonnull CompoundData data, @Nonnull DynamicRegistryManager registry)
	{
		DefaultedList<ItemStack> list = DefaultedList.ofSize(2, ItemStack.EMPTY);
		EntityEquipment equipment = getEquipmentSlots(data, registry);

		if (equipment != null)
		{
			ItemStack bodyArmor = equipment.get(EquipmentSlot.BODY);
			ItemStack saddle = equipment.get(EquipmentSlot.SADDLE);

			if (bodyArmor != null && !bodyArmor.isEmpty())
			{
				list.set(0, bodyArmor.copy());
			}

			if (saddle != null && !saddle.isEmpty())
			{
				list.set(1, saddle.copy());
			}
		}

		return list;
	}

	/**
	 * Get a ItemStack List of all Equipment Slots
	 *   0/1   [{MainHand}, {OffHand}]
	 * 2/3/4/5 [{Head}, {Chest}, {Legs}, {Feet}]
	 *   6/7   [{BodyArmor}, {Saddle}]
	 *
	 * @param data ()
	 * @param registry ()
	 * @return ()
	 */
	public static DefaultedList<ItemStack> getAllEquipment(@Nonnull CompoundData data, @Nonnull DynamicRegistryManager registry)
	{
		DefaultedList<ItemStack> list = DefaultedList.ofSize(8, ItemStack.EMPTY);
		EntityEquipment equipment = getEquipmentSlots(data, registry);

		if (equipment != null)
		{
			ItemStack mainHand = equipment.get(EquipmentSlot.MAINHAND);
			ItemStack offHand = equipment.get(EquipmentSlot.OFFHAND);
			ItemStack head = equipment.get(EquipmentSlot.HEAD);
			ItemStack chest = equipment.get(EquipmentSlot.CHEST);
			ItemStack legs = equipment.get(EquipmentSlot.LEGS);
			ItemStack feet = equipment.get(EquipmentSlot.FEET);
			ItemStack bodyArmor = equipment.get(EquipmentSlot.BODY);
			ItemStack saddle = equipment.get(EquipmentSlot.SADDLE);

			// Hand Items
			if (mainHand != null && !mainHand.isEmpty())
			{
				list.set(0, mainHand.copy());
			}

			if (offHand != null && !offHand.isEmpty())
			{
				list.set(1, offHand.copy());
			}

			// ArmorItems
			if (head != null && !head.isEmpty())
			{
				list.set(2, head.copy());
			}

			if (chest != null && !chest.isEmpty())
			{
				list.set(3, chest.copy());
			}

			if (legs != null && !legs.isEmpty())
			{
				list.set(4, legs.copy());
			}

			if (feet != null && !feet.isEmpty())
			{
				list.set(5, feet.copy());
			}

			// HorseArmor
			if (bodyArmor != null && !bodyArmor.isEmpty())
			{
				list.set(6, bodyArmor.copy());
			}

			// SaddleItem
			if (saddle != null && !saddle.isEmpty())
			{
				list.set(7, saddle.copy());
			}
		}

		return list;
	}

	/**
	 * Get the Tamable Entity's Owner
	 *
	 * @param data ()
	 * @return ()
	 */
	public static Pair<UUID, Boolean> getTamableOwner(@Nonnull CompoundData data)
	{
		UUID owner = Util.NIL_UUID;
		boolean sitting = false;

		if (data.contains(NbtKeys.OWNER, Constants.NBT.TAG_INT_ARRAY))
		{
			owner = DataTypeUtils.getUUIDCodec(data, NbtKeys.OWNER);
		}

		if (data.contains(NbtKeys.SITTING, Constants.NBT.TAG_BYTE))
		{
			sitting = data.getBoolean(NbtKeys.SITTING);
		}

		return Pair.of(owner, sitting);
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
	public static @Nullable Leashable.LeashData getLeashData(@Nonnull CompoundData data)
	{
		if (data.contains(NbtKeys.LEASH, Constants.NBT.TAG_COMPOUND))
		{
			return data.getCodec(NbtKeys.LEASH, Leashable.LeashData.CODEC).orElse(null);
		}

		return null;
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
			rotation = Direction.byIndex(data.getByte(NbtKeys.ITEM_ROTATION));
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

		if (data.contains(NbtKeys.VARIANT, Constants.NBT.TAG_STRING))
		{
			variant = PaintingVariant.ENTRY_CODEC.fieldOf(NbtKeys.VARIANT).codec()
			                                     .parse(registry.getOps(DataOps.INSTANCE), data)
			                                     .resultOrPartial().orElse(null);
		}

		return Pair.of(facing, variant != null ? variant.value() : null);
	}

	/**
	 * Get an Axolotl's Variant from Data Tag.
	 *
	 * @param data ()
	 * @return ()
	 */
	@SuppressWarnings("deprecation")
	public static @Nullable AxolotlEntity.Variant getAxolotlVariant(@Nonnull CompoundData data)
	{
		if (data.contains(NbtKeys.VARIANT_2, Constants.NBT.TAG_INT))
		{
			return data.getCodec(NbtKeys.VARIANT_2, AxolotlEntity.Variant.INDEX_CODEC).orElse(AxolotlEntity.Variant.LUCY);
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
	@SuppressWarnings("deprecation")
	public static Pair<RegistryKey<CatVariant>, DyeColor> getCatVariant(@Nonnull CompoundData data, @Nonnull DynamicRegistryManager registry)
	{
		RegistryKey<CatVariant> variantKey = null;
		DyeColor collar = null;

		if (data.contains(NbtKeys.VARIANT, Constants.NBT.TAG_STRING))
		{
			Optional<RegistryEntry<CatVariant>> variant = CatVariant.ENTRY_CODEC
					.fieldOf(NbtKeys.VARIANT).codec()
					.parse(registry.getOps(DataOps.INSTANCE), data)
					.resultOrPartial();

			variantKey = variant.map(entry -> entry.getKey().orElseThrow()).orElse(CatVariants.BLACK);
		}
		if (data.containsLenient(NbtKeys.COLLAR))
		{
			collar = data.getCodec(NbtKeys.COLLAR, DyeColor.INDEX_CODEC).orElse(DyeColor.RED);
		}

		if (variantKey == null)
		{
			variantKey = CatVariants.BLACK;
		}

		if (collar == null)
		{
			collar = DyeColor.RED;
		}

		return Pair.of(variantKey, collar);
	}

	/**
	 * Get a Chicken's Variant from Data Tag.
	 *
	 * @param data ()
	 * @param registry ()
	 * @return ()
	 */
	public static @Nullable RegistryKey<ChickenVariant> getChickenVariant(@Nonnull CompoundData data, @Nonnull DynamicRegistryManager registry)
	{
		if (data.contains(NbtKeys.VARIANT, Constants.NBT.TAG_STRING))
		{
			Optional<RegistryEntry<ChickenVariant>> variant = ChickenVariant.ENTRY_CODEC
					.fieldOf(NbtKeys.VARIANT).codec()
					.parse(registry.getOps(DataOps.INSTANCE), data)
					.resultOrPartial();

			return variant.map(entry -> entry.getKey().orElseThrow()).orElse(ChickenVariants.DEFAULT);
		}

		return null;
	}

	/**
	 * Get a Cow's Variant from Data Tag.
	 *
	 * @param data ()
	 * @param registry ()
	 * @return ()
	 */
	public static @Nullable RegistryKey<CowVariant> getCowVariant(@Nonnull CompoundData data, @Nonnull DynamicRegistryManager registry)
	{
		if (data.contains(NbtKeys.VARIANT, Constants.NBT.TAG_STRING))
		{
			Optional<RegistryEntry<CowVariant>> variant = CowVariant.ENTRY_CODEC
					.fieldOf(NbtKeys.VARIANT).codec()
					.parse(registry.getOps(DataOps.INSTANCE), data)
					.resultOrPartial();

			return variant.map(entry -> entry.getKey().orElseThrow()).orElse(CowVariants.DEFAULT);
		}

		return null;
	}

	/**
	 * Get a Mooshroom Variant from Data Tag.
	 *
	 * @param data ()
	 * @return ()
	 */
	public static @Nullable MooshroomEntity.Variant getMooshroomVariant(@Nonnull CompoundData data)
	{
		if (data.contains(NbtKeys.TYPE_2, Constants.NBT.TAG_STRING))
		{
			return data.getCodec(NbtKeys.TYPE_2, MooshroomEntity.Variant.CODEC).orElse(MooshroomEntity.Variant.RED);
		}

		return null;
	}

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
			Optional<RegistryEntry<FrogVariant>> variant = FrogVariant.ENTRY_CODEC
					.fieldOf(NbtKeys.VARIANT).codec()
					.parse(registry.getOps(DataOps.INSTANCE), data)
					.resultOrPartial();

			return variant.map(entry -> entry.getKey().orElseThrow()).orElse(FrogVariants.TEMPERATE);
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
			color = HorseColor.byIndex(variant & 0xFF);
			marking = HorseMarking.byIndex((variant & 0xFF00) >> 8);
		}

		return Pair.of(color, marking);
	}

	/**
	 * Get a Parrot's Variant from Data Tag.
	 *
	 * @param data ()
	 * @return ()
	 */
	@SuppressWarnings("deprecation")
	public static @Nullable ParrotEntity.Variant getParrotVariant(@Nonnull CompoundData data)
	{
		if (data.contains(NbtKeys.VARIANT_2, Constants.NBT.TAG_INT))
		{
			return data.getCodec(NbtKeys.VARIANT_2, ParrotEntity.Variant.INDEX_CODEC).orElse(ParrotEntity.Variant.RED_BLUE);
		}

		return null;
	}

	/**
	 * Get a Tropical Fish Variant from Data Tag.
	 *
	 * @param data ()
	 * @return ()
	 */
	public static @Nullable TropicalFishEntity.Variant getFishVariant(@Nonnull CompoundData data)
	{
		if (data.contains(NbtKeys.VARIANT_2, Constants.NBT.TAG_INT))
		{
			return data.getCodec(NbtKeys.VARIANT_2, TropicalFishEntity.Variant.CODEC).orElse(TropicalFishEntity.DEFAULT_VARIANT);
		}

		return null;
	}

	/**
	 * Get a Tropical Fish Pattern from Data Tag.
	 *
	 * @param data ()
	 * @return ()
	 */
	public static @Nullable TropicalFishEntity.Pattern getFishPattern(@Nonnull CompoundData data)
	{
		if (data.contains(NbtKeys.VARIANT_2, Constants.NBT.TAG_INT))
		{
			return data.getCodec(NbtKeys.VARIANT_2, TropicalFishEntity.Variant.CODEC).orElse(TropicalFishEntity.DEFAULT_VARIANT).pattern();
		}
		else if (data.contains(NbtKeys.BUCKET_VARIANT, Constants.NBT.TAG_INT))
		{
			return TropicalFishEntity.Pattern.byIndex(data.getInt(NbtKeys.BUCKET_VARIANT) & '\uffff');
		}

		return null;
	}

	/**
	 * Get a Wolves' Variant and Collar Color from Data Tag.
	 *
	 * @param data ()
	 * @return ()
	 */
	@SuppressWarnings("deprecation")
	public static Pair<RegistryKey<WolfVariant>, DyeColor> getWolfVariant(@Nonnull CompoundData data, @Nonnull DynamicRegistryManager registry)
	{
		RegistryKey<WolfVariant> variantKey = null;
		DyeColor collar = null;

		if (data.contains(NbtKeys.VARIANT, Constants.NBT.TAG_STRING))
		{
			Optional<RegistryEntry<WolfVariant>> variant = WolfVariant.ENTRY_CODEC
					.fieldOf(NbtKeys.VARIANT).codec()
					.parse(registry.getOps(DataOps.INSTANCE), data)
					.resultOrPartial();

			variantKey = variant.map(entry -> entry.getKey().orElseThrow()).orElse(WolfVariants.DEFAULT);
		}
		if (data.containsLenient(NbtKeys.COLLAR))
		{
			collar = data.getCodec(NbtKeys.COLLAR, DyeColor.INDEX_CODEC).orElse(DyeColor.RED);
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

	/**
	 * Get a Wolves' Sound Type Variant from Data Tag.
	 *
	 * @param data ()
	 * @return ()
	 */
	public static @Nullable RegistryKey<WolfSoundVariant> getWolfSoundType(@Nonnull CompoundData data, @Nonnull DynamicRegistryManager registry)
	{
		if (data.contains(NbtKeys.SOUND_VARIANT, Constants.NBT.TAG_STRING))
		{
			RegistryEntry.Reference<WolfSoundVariant> soundVariant = registry.getOrThrow(RegistryKeys.WOLF_SOUND_VARIANT).getEntry(Identifier.tryParse(data.getString(NbtKeys.SOUND_VARIANT))).orElse(null);

			if (soundVariant != null)
			{
				return soundVariant.registryKey();
			}
		}

		return null;
	}

	/**
	 * Get a Sheep's Color from Data Tag.
	 *
	 * @param data ()
	 * @return ()
	 */
	@SuppressWarnings("deprecation")
	public static @Nullable DyeColor getSheepColor(@Nonnull CompoundData data)
	{
		if (data.contains(NbtKeys.COLOR, Constants.NBT.TAG_INT))
		{
			return data.getCodec(NbtKeys.COLOR, DyeColor.INDEX_CODEC).orElse(DyeColor.WHITE);
		}

		return null;
	}

	/**
	 * Get a Rabbit's Variant type from Data Tag.
	 *
	 * @param data ()
	 * @return ()
	 */
	@SuppressWarnings("deprecation")
	public static @Nullable RabbitEntity.Variant getRabbitType(@Nonnull CompoundData data)
	{
		if (data.contains(NbtKeys.RABBIT_TYPE, Constants.NBT.TAG_INT))
		{
			return data.getCodec(NbtKeys.RABBIT_TYPE, RabbitEntity.Variant.INDEX_CODEC).orElse(RabbitEntity.Variant.BROWN);
		}

		return null;
	}

	/**
	 * Get a Llama's Variant type from Data Tag.
	 *
	 * @param data ()
	 * @return ()
	 */
	@SuppressWarnings("deprecation")
	public static Pair<LlamaEntity.Variant, Integer> getLlamaType(@Nonnull CompoundData data)
	{
		LlamaEntity.Variant variant = null;
		int strength = -1;

		if (data.contains(NbtKeys.VARIANT_2, Constants.NBT.TAG_INT))
		{
			variant = data.getCodec(NbtKeys.VARIANT_2, LlamaEntity.Variant.INDEX_CODEC).orElse(LlamaEntity.Variant.CREAMY);
		}

		if (data.contains(NbtKeys.STRENGTH, Constants.NBT.TAG_INT))
		{
			strength = data.getInt(NbtKeys.STRENGTH);
		}

		return Pair.of(variant, strength);
	}

	/**
	 * Get a Pig's Variant type from Data Tag.
	 *
	 * @param data ()
	 * @param registry ()
	 * @return ()
	 */
	public static @Nullable RegistryKey<PigVariant> getPigVariant(@Nonnull CompoundData data, @Nonnull DynamicRegistryManager registry)
	{
		if (data.contains(NbtKeys.VARIANT, Constants.NBT.TAG_STRING))
		{
			Optional<RegistryEntry.Reference<PigVariant>> opt = registry.getOrThrow(RegistryKeys.PIG_VARIANT).getEntry(Identifier.tryParse(data.getString(NbtKeys.VARIANT)));

			if (opt.isPresent())
			{
				return opt.get().registryKey();
			}

			return PigVariants.DEFAULT;
		}

		return null;
	}

	/**
	 * Get a Fox's Variant type from Data Tag.
	 *
	 * @param data ()
	 * @return ()
	 */
	public static @Nullable FoxEntity.Variant getFoxVariant(@Nonnull CompoundData data)
	{
		if (data.contains(NbtKeys.TYPE_2, Constants.NBT.TAG_STRING))
		{
			return data.getCodec(NbtKeys.TYPE_2, FoxEntity.Variant.CODEC).orElse(FoxEntity.Variant.RED);
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
		if (data.contains(NbtKeys.TYPE, Constants.NBT.TAG_STRING))
		{
			return data.getCodec(NbtKeys.TYPE, SalmonEntity.Variant.CODEC).orElse(SalmonEntity.Variant.MEDIUM);
		}

		return null;
	}

	/**
	 * Get a Dolphin's TreasurePos and other data from Data Tag.
	 *
	 * @param data ()
	 * @return ()
	 */
	public static Pair<Integer, Boolean> getDolphinData(@Nonnull CompoundData data)
	{
		boolean hasFish = false;
		int moist = -1;

		if (data.contains(NbtKeys.MOISTNESS, Constants.NBT.TAG_INT))
		{
			moist = data.getInt(NbtKeys.MOISTNESS);
		}

		if (data.contains(NbtKeys.GOT_FISH, Constants.NBT.TAG_BYTE))
		{
			hasFish = data.getBoolean(NbtKeys.GOT_FISH);
		}

		return Pair.of(moist, hasFish);
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
			NbtView view = NbtView.getReader(data, registry);
			hunger.readData(view.getReader());
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
			CompoundData entry = data.getCompoundOrDefault(NbtKeys.RECIPE_BOOK, new CompoundData());
			book.unpack(ServerRecipeBook.Packed.CODEC
					            .parse(DataOps.INSTANCE, entry).getOrThrow(),
			            (key) -> manager.get(key).isPresent()
			);
		}

		return book;
	}

	/**
	 * Get a Mob's Home Pos and Radius from Data Tag
	 * @param data ()
	 * @return ()
	 */
	public static Pair<BlockPos, Integer> getHomePos(@Nonnull CompoundData data)
	{
		BlockPos pos = BlockPos.ORIGIN;
		int radius = -1;

		if (data.containsLenient(NbtKeys.HOME_POS))
		{
			pos = data.getCodec(NbtKeys.HOME_POS, BlockPos.CODEC).orElse(BlockPos.ORIGIN);
		}

		if (data.contains(NbtKeys.HOME_RADIUS, Constants.NBT.TAG_INT))
		{
			radius = data.getInt(NbtKeys.HOME_RADIUS);
		}

		return Pair.of(pos, radius);
	}

	/**
	 * Get a Copper Golem's Weathering Data from Data Tag
	 * @param data ()
	 * @return ()
	 */
	public static Pair<Oxidizable.OxidationLevel, Long> getWeatheringData(@Nonnull CompoundData data)
	{
		Oxidizable.OxidationLevel level = Oxidizable.OxidationLevel.UNAFFECTED;
		long age = -1L;

		if (data.contains(NbtKeys.WEATHER_STATE, Constants.NBT.TAG_STRING))
		{
			level = data.getCodec(NbtKeys.WEATHER_STATE, Oxidizable.OxidationLevel.CODEC).orElse(Oxidizable.OxidationLevel.UNAFFECTED);
		}

		if (data.contains(NbtKeys.NEXT_WEATHER_AGE, Constants.NBT.TAG_LONG))
		{
			age = data.getLong(NbtKeys.NEXT_WEATHER_AGE);
		}

		return Pair.of(level, age);
	}
}
