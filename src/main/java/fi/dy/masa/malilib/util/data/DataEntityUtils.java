package fi.dy.masa.malilib.util.data;

import java.util.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.util.Util;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.chicken.ChickenVariant;
import net.minecraft.world.entity.animal.chicken.ChickenVariants;
import net.minecraft.world.entity.animal.cow.CowVariant;
import net.minecraft.world.entity.animal.cow.CowVariants;
import net.minecraft.world.entity.animal.cow.MushroomCow;
import net.minecraft.world.entity.animal.equine.Llama;
import net.minecraft.world.entity.animal.equine.Markings;
import net.minecraft.world.entity.animal.equine.Variant;
import net.minecraft.world.entity.animal.feline.CatVariant;
import net.minecraft.world.entity.animal.feline.CatVariants;
import net.minecraft.world.entity.animal.fish.Salmon;
import net.minecraft.world.entity.animal.fish.TropicalFish;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.entity.animal.frog.FrogVariant;
import net.minecraft.world.entity.animal.frog.FrogVariants;
import net.minecraft.world.entity.animal.nautilus.ZombieNautilusVariant;
import net.minecraft.world.entity.animal.nautilus.ZombieNautilusVariants;
import net.minecraft.world.entity.animal.panda.Panda;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.animal.pig.PigVariant;
import net.minecraft.world.entity.animal.pig.PigVariants;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.entity.animal.wolf.WolfSoundVariant;
import net.minecraft.world.entity.animal.wolf.WolfVariant;
import net.minecraft.world.entity.animal.wolf.WolfVariants;
import net.minecraft.world.entity.decoration.painting.PaintingVariant;
import net.minecraft.world.entity.npc.villager.VillagerData;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.block.WeatheringCopper;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.util.data.tag.BaseData;
import fi.dy.masa.malilib.util.data.tag.CompoundData;
import fi.dy.masa.malilib.util.data.tag.ListData;
import fi.dy.masa.malilib.util.data.tag.converter.DataConverterNbt;
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
			return BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.tryParse(data.getString(NbtKeys.ID))).orElse(null);
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
		Identifier id = EntityType.getKey(type);

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
	public static Holder.Reference<@NotNull EntityType<?>> getEntityTypeEntry(Identifier id, @Nonnull RegistryAccess registry)
	{
		try
		{
			return registry.lookupOrThrow(BuiltInRegistries.ENTITY_TYPE.key()).get(id).orElseThrow();
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
	public static @Nullable AttributeMap getAttributes(@Nonnull CompoundData data)
	{
		EntityType<?> type = getEntityType(data);

		if (type != null && data.contains(NbtKeys.ATTRIB, Constants.NBT.TAG_LIST))
		{
			AttributeMap container = new AttributeMap(DefaultAttributes.getSupplier((EntityType<? extends LivingEntity>) type));
			ListData list = data.getList(NbtKeys.ATTRIB);

			container.apply(AttributeInstance.Packed.LIST_CODEC.parse(NbtOps.INSTANCE, DataConverterNbt.toVanillaList(list)).getPartialOrThrow());
			return container;
		}

		return null;
	}

	public static double getAttributeBaseValue(@Nonnull CompoundData data, Holder<Attribute> attribute)
	{
		AttributeMap attributes = getAttributes(data);

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
	public static double getAttributeValue(@Nonnull CompoundData data, Holder<@NotNull Attribute> attribute)
	{
		AttributeMap attributes = getAttributes(data);

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

		maxHealth = getAttributeValue(data, Attributes.MAX_HEALTH);

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
		AttributeMap container = getAttributes(data);
		double moveSpeed = 0d;
		double jumpStrength = 0d;

		if (container != null)
		{
			moveSpeed = container.getValue(Attributes.MOVEMENT_SPEED);
			jumpStrength = container.getValue(Attributes.JUMP_STRENGTH);
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
	public static @Nullable Component getCustomName(@Nonnull CompoundData data, @Nonnull RegistryAccess registry)
	{
		if (data.contains(NbtKeys.CUSTOM_NAME, Constants.NBT.TAG_COMPOUND))
		{
			return data.getCodec(NbtKeys.CUSTOM_NAME, ComponentSerialization.CODEC, registry.createSerializationContext(DataOps.INSTANCE)).orElse(null);
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
	public static CompoundData setCustomNameToDataTag(@Nonnull Component name, @Nonnull RegistryAccess registry, @Nullable CompoundData dataIn, String key)
	{
		CompoundData data = dataIn != null ? dataIn.copy() : new CompoundData();

		if (key == null || key.isEmpty())
		{
			key = NbtKeys.CUSTOM_NAME;
		}

		return data.putCodec(key, ComponentSerialization.CODEC, registry.createSerializationContext(DataOps.INSTANCE), name);
	}

	/**
	 * Get a Map of all active Status Effects via Data Tag.
	 *
	 * @param data ()
	 * @return ()
	 */
	public static Map<Holder<@NotNull MobEffect>, MobEffectInstance> getActiveStatusEffects(@Nonnull CompoundData data, @Nonnull RegistryAccess registry)
	{
		Map<Holder<@NotNull MobEffect>, MobEffectInstance> statusEffects = Maps.newHashMap();

		if (data.contains(NbtKeys.EFFECTS, Constants.NBT.TAG_LIST))
		{
			List<MobEffectInstance> list = data.getCodec(NbtKeys.EFFECTS, MobEffectInstance.CODEC.listOf(), registry.createSerializationContext(DataOps.INSTANCE)).orElse(List.of());

			for (MobEffectInstance instance : list)
			{
				statusEffects.put(instance.getEffect(), instance);
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
	public static @Nullable EntityEquipment getEquipmentSlots(@Nonnull CompoundData data, @Nonnull RegistryAccess registry)
	{
		if (data.contains(NbtKeys.EQUIPMENT, Constants.NBT.TAG_COMPOUND))
		{
			CompoundData comp = data.getCompound(NbtKeys.EQUIPMENT);
			Optional<EntityEquipment> opt = EntityEquipment.CODEC.parse(registry.createSerializationContext(NbtOps.INSTANCE), DataConverterNbt.toVanillaCompound(comp)).result();

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
	public static @Nullable BaseData setEquipmentSlotsToDataTag(@Nonnull EntityEquipment equipment, @Nonnull RegistryAccess registry)
	{
		try
		{
			return EntityEquipment.CODEC.encodeStart(registry.createSerializationContext(DataOps.INSTANCE), equipment).getOrThrow();
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
	public static NonNullList<@NotNull ItemStack> getHandItems(@Nonnull CompoundData data, @Nonnull RegistryAccess registry)
	{
		NonNullList<@NotNull ItemStack> list = NonNullList.withSize(2, ItemStack.EMPTY);
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
	public static NonNullList<@NotNull ItemStack> getHumanoidArmor(@Nonnull CompoundData data, @Nonnull RegistryAccess registry)
	{
		NonNullList<@NotNull ItemStack> list = NonNullList.withSize(4, ItemStack.EMPTY);
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
	public static NonNullList<@NotNull ItemStack> getHorseEquipment(@Nonnull CompoundData data, @Nonnull RegistryAccess registry)
	{
		NonNullList<@NotNull ItemStack> list = NonNullList.withSize(2, ItemStack.EMPTY);
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
	public static NonNullList<@NotNull ItemStack> getAllEquipment(@Nonnull CompoundData data, @Nonnull RegistryAccess registry)
	{
		NonNullList<@NotNull ItemStack> list = NonNullList.withSize(8, ItemStack.EMPTY);
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
	public static @Nullable MerchantOffers getTradeOffers(@Nonnull CompoundData data, @Nonnull RegistryAccess registry)
	{
		if (data.containsLenient(NbtKeys.OFFERS))
		{
			return data.getCodec(NbtKeys.OFFERS, MerchantOffers.CODEC, registry.createSerializationContext(DataOps.INSTANCE)).orElse(null);
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
	public static Pair<Panda.Gene, Panda.Gene> getPandaGenes(@Nonnull CompoundData data)
	{
		Panda.Gene mainGene = null;
		Panda.Gene hiddenGene = null;

		if (data.contains(NbtKeys.MAIN_GENE, Constants.NBT.TAG_STRING))
		{
			mainGene = data.getCodec(NbtKeys.MAIN_GENE, Panda.Gene.CODEC).orElse(Panda.Gene.NORMAL);
		}
		if (data.contains(NbtKeys.HIDDEN_GENE, Constants.NBT.TAG_STRING))
		{
			hiddenGene = data.getCodec(NbtKeys.HIDDEN_GENE, Panda.Gene.CODEC).orElse(Panda.Gene.NORMAL);
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
			rotation = Direction.from3DDataValue(data.getByte(NbtKeys.ITEM_ROTATION));
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
	public static Pair<Direction, PaintingVariant> getPaintingData(@Nonnull CompoundData data, @Nonnull RegistryAccess registry)
	{
		Direction facing = DataTypeUtils.readDirectionFromTag(data, NbtKeys.FACING);
		Holder<@NotNull PaintingVariant> variant = null;

		if (data.contains(NbtKeys.VARIANT, Constants.NBT.TAG_STRING))
		{
			variant = PaintingVariant.CODEC.fieldOf(NbtKeys.VARIANT).codec()
			                                     .parse(registry.createSerializationContext(NbtOps.INSTANCE), DataConverterNbt.toVanillaCompound(data))
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
	public static @Nullable Axolotl.Variant getAxolotlVariant(@Nonnull CompoundData data)
	{
		if (data.contains(NbtKeys.VARIANT_2, Constants.NBT.TAG_INT))
		{
			return data.getCodec(NbtKeys.VARIANT_2, Axolotl.Variant.LEGACY_CODEC).orElse(Axolotl.Variant.LUCY);
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
	public static Pair<ResourceKey<@NotNull CatVariant>, DyeColor> getCatVariant(@Nonnull CompoundData data, @Nonnull RegistryAccess registry)
	{
		ResourceKey<@NotNull CatVariant> variantKey = null;
		DyeColor collar = null;

		if (data.contains(NbtKeys.VARIANT, Constants.NBT.TAG_STRING))
		{
			Optional<Holder<@NotNull CatVariant>> variant = CatVariant.CODEC
					.fieldOf(NbtKeys.VARIANT).codec()
					.parse(registry.createSerializationContext(NbtOps.INSTANCE), DataConverterNbt.toVanillaCompound(data))
					.resultOrPartial();

			variantKey = variant.map(entry -> entry.unwrapKey().orElseThrow()).orElse(CatVariants.BLACK);
		}
		if (data.containsLenient(NbtKeys.COLLAR))
		{
			collar = data.getCodec(NbtKeys.COLLAR, DyeColor.LEGACY_ID_CODEC).orElse(DyeColor.RED);
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
	public static @Nullable ResourceKey<@NotNull ChickenVariant> getChickenVariant(@Nonnull CompoundData data, @Nonnull RegistryAccess registry)
	{
		if (data.contains(NbtKeys.VARIANT, Constants.NBT.TAG_STRING))
		{
			Optional<Holder<@NotNull ChickenVariant>> variant = ChickenVariant.CODEC
					.fieldOf(NbtKeys.VARIANT).codec()
					.parse(registry.createSerializationContext(NbtOps.INSTANCE), DataConverterNbt.toVanillaCompound(data))
					.resultOrPartial();

			return variant.map(entry -> entry.unwrapKey().orElseThrow()).orElse(ChickenVariants.DEFAULT);
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
	public static @Nullable ResourceKey<@NotNull CowVariant> getCowVariant(@Nonnull CompoundData data, @Nonnull RegistryAccess registry)
	{
		if (data.contains(NbtKeys.VARIANT, Constants.NBT.TAG_STRING))
		{
			Optional<Holder<@NotNull CowVariant>> variant = CowVariant.CODEC
					.fieldOf(NbtKeys.VARIANT).codec()
					.parse(registry.createSerializationContext(NbtOps.INSTANCE), DataConverterNbt.toVanillaCompound(data))
					.resultOrPartial();

			return variant.map(entry -> entry.unwrapKey().orElseThrow()).orElse(CowVariants.DEFAULT);
		}

		return null;
	}

	/**
	 * Get a Mooshroom Variant from Data Tag.
	 *
	 * @param data ()
	 * @return ()
	 */
	public static @Nullable MushroomCow.Variant getMooshroomVariant(@Nonnull CompoundData data)
	{
		if (data.contains(NbtKeys.TYPE_2, Constants.NBT.TAG_STRING))
		{
			return data.getCodec(NbtKeys.TYPE_2, MushroomCow.Variant.CODEC).orElse(MushroomCow.Variant.RED);
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
	public static @Nullable ResourceKey<@NotNull FrogVariant> getFrogVariant(@Nonnull CompoundData data, @Nonnull RegistryAccess registry)
	{
		if (data.contains(NbtKeys.VARIANT, Constants.NBT.TAG_STRING))
		{
			Optional<Holder<@NotNull FrogVariant>> variant = FrogVariant.CODEC
					.fieldOf(NbtKeys.VARIANT).codec()
					.parse(registry.createSerializationContext(NbtOps.INSTANCE), DataConverterNbt.toVanillaCompound(data))
					.resultOrPartial();

			return variant.map(entry -> entry.unwrapKey().orElseThrow()).orElse(FrogVariants.TEMPERATE);
		}

		return null;
	}

	/**
	 * Get a Zombie Nautilus's Variant from NBT.
	 *
	 * @param data ()
	 * @param registry ()
	 * @return ()
	 */
	public static @Nullable ResourceKey<@NotNull ZombieNautilusVariant> getZombieNautilusVariantFromNbt(@Nonnull CompoundData data, @Nonnull RegistryAccess registry)
	{
		if (data.contains(NbtKeys.VARIANT, Constants.NBT.TAG_STRING))
		{
			Optional<Holder<@NotNull ZombieNautilusVariant>> variant = ZombieNautilusVariant.CODEC
					.fieldOf(NbtKeys.VARIANT).codec()
					.parse(registry.createSerializationContext(NbtOps.INSTANCE), DataConverterNbt.toVanillaCompound(data))
					.resultOrPartial();

			return variant.map(entry -> entry.unwrapKey().orElseThrow()).orElse(ZombieNautilusVariants.DEFAULT);
		}

		return null;
	}

	/**
	 * Get a Horse's Variant (Color, Markings) from Data Tag.
	 * @param data ()
	 * @return ()
	 */
	public static Pair<Variant, Markings> getHorseVariant(@Nonnull CompoundData data)
	{
		Variant color = null;
		Markings marking = null;

		if (data.contains(NbtKeys.VARIANT_2, Constants.NBT.TAG_INT))
		{
			int variant = data.getInt(NbtKeys.VARIANT_2);
			color = Variant.byId(variant & 0xFF);
			marking = Markings.byId((variant & 0xFF00) >> 8);
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
	public static @Nullable Parrot.Variant getParrotVariant(@Nonnull CompoundData data)
	{
		if (data.contains(NbtKeys.VARIANT_2, Constants.NBT.TAG_INT))
		{
			return data.getCodec(NbtKeys.VARIANT_2, Parrot.Variant.LEGACY_CODEC).orElse(Parrot.Variant.RED_BLUE);
		}

		return null;
	}

	/**
	 * Get a Tropical Fish Variant from Data Tag.
	 *
	 * @param data ()
	 * @return ()
	 */
	public static @Nullable TropicalFish.Variant getFishVariant(@Nonnull CompoundData data)
	{
		if (data.contains(NbtKeys.VARIANT_2, Constants.NBT.TAG_INT))
		{
			return data.getCodec(NbtKeys.VARIANT_2, TropicalFish.Variant.CODEC).orElse(TropicalFish.DEFAULT_VARIANT);
		}

		return null;
	}

	/**
	 * Get a Tropical Fish Pattern from Data Tag.
	 *
	 * @param data ()
	 * @return ()
	 */
	public static @Nullable TropicalFish.Pattern getFishPattern(@Nonnull CompoundData data)
	{
		if (data.contains(NbtKeys.VARIANT_2, Constants.NBT.TAG_INT))
		{
			return data.getCodec(NbtKeys.VARIANT_2, TropicalFish.Variant.CODEC).orElse(TropicalFish.DEFAULT_VARIANT).pattern();
		}
		else if (data.contains(NbtKeys.BUCKET_VARIANT, Constants.NBT.TAG_INT))
		{
			return TropicalFish.Pattern.byId(data.getInt(NbtKeys.BUCKET_VARIANT) & '\uffff');
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
	public static Pair<ResourceKey<@NotNull WolfVariant>, DyeColor> getWolfVariant(@Nonnull CompoundData data, @Nonnull RegistryAccess registry)
	{
		ResourceKey<@NotNull WolfVariant> variantKey = null;
		DyeColor collar = null;

		if (data.contains(NbtKeys.VARIANT, Constants.NBT.TAG_STRING))
		{
			Optional<Holder<@NotNull WolfVariant>> variant = WolfVariant.CODEC
					.fieldOf(NbtKeys.VARIANT).codec()
					.parse(registry.createSerializationContext(NbtOps.INSTANCE), DataConverterNbt.toVanillaCompound(data))
					.resultOrPartial();

			variantKey = variant.map(entry -> entry.unwrapKey().orElseThrow()).orElse(WolfVariants.DEFAULT);
		}
		if (data.containsLenient(NbtKeys.COLLAR))
		{
			collar = data.getCodec(NbtKeys.COLLAR, DyeColor.LEGACY_ID_CODEC).orElse(DyeColor.RED);
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
	public static @Nullable ResourceKey<@NotNull WolfSoundVariant> getWolfSoundType(@Nonnull CompoundData data, @Nonnull RegistryAccess registry)
	{
		if (data.contains(NbtKeys.SOUND_VARIANT, Constants.NBT.TAG_STRING))
		{
			Holder.Reference<@NotNull WolfSoundVariant> soundVariant = registry.lookupOrThrow(Registries.WOLF_SOUND_VARIANT).get(Identifier.tryParse(data.getString(NbtKeys.SOUND_VARIANT))).orElse(null);

			if (soundVariant != null)
			{
				return soundVariant.key();
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
			return data.getCodec(NbtKeys.COLOR, DyeColor.LEGACY_ID_CODEC).orElse(DyeColor.WHITE);
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
	public static @Nullable Rabbit.Variant getRabbitType(@Nonnull CompoundData data)
	{
		if (data.contains(NbtKeys.RABBIT_TYPE, Constants.NBT.TAG_INT))
		{
			return data.getCodec(NbtKeys.RABBIT_TYPE, Rabbit.Variant.LEGACY_CODEC).orElse(Rabbit.Variant.BROWN);
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
	public static Pair<Llama.Variant, Integer> getLlamaType(@Nonnull CompoundData data)
	{
		Llama.Variant variant = null;
		int strength = -1;

		if (data.contains(NbtKeys.VARIANT_2, Constants.NBT.TAG_INT))
		{
			variant = data.getCodec(NbtKeys.VARIANT_2, Llama.Variant.LEGACY_CODEC).orElse(Llama.Variant.CREAMY);
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
	public static @Nullable ResourceKey<@NotNull PigVariant> getPigVariant(@Nonnull CompoundData data, @Nonnull RegistryAccess registry)
	{
		if (data.contains(NbtKeys.VARIANT, Constants.NBT.TAG_STRING))
		{
			try
			{
				Optional<Holder.Reference<@NotNull PigVariant>> opt = registry.lookupOrThrow(Registries.PIG_VARIANT).get(Objects.requireNonNull(Identifier.tryParse(data.getString(NbtKeys.VARIANT))));

				return opt.map(Holder.Reference::key).orElse(PigVariants.DEFAULT);
			}
			catch (Exception ignored) { }
		}

		return null;
	}

	/**
	 * Get a Fox's Variant type from Data Tag.
	 *
	 * @param data ()
	 * @return ()
	 */
	public static @Nullable Fox.Variant getFoxVariant(@Nonnull CompoundData data)
	{
		if (data.contains(NbtKeys.TYPE_2, Constants.NBT.TAG_STRING))
		{
			return data.getCodec(NbtKeys.TYPE_2, Fox.Variant.CODEC).orElse(Fox.Variant.RED);
		}

		return null;
	}

	/**
	 * Get a Salmon's Variant type from Data Tag.
	 *
	 * @param data ()
	 * @return ()
	 */
	public static @Nullable Salmon.Variant getSalmonVariant(@Nonnull CompoundData data)
	{
		if (data.contains(NbtKeys.TYPE, Constants.NBT.TAG_STRING))
		{
			return data.getCodec(NbtKeys.TYPE, Salmon.Variant.CODEC).orElse(Salmon.Variant.MEDIUM);
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
	public static @Nullable FoodData getPlayerHunger(@Nonnull CompoundData data, @Nonnull RegistryAccess registry)
	{
		FoodData hunger = null;

		if (data.containsLenient(NbtKeys.FOOD_LEVEL))
		{
			hunger = new FoodData();
			NbtView view = NbtView.getReader(data, registry);
			hunger.readAdditionalSaveData(view.getReader());
		}

		return hunger;
	}

	/**
	 * Get a Players' Unlocked Recipe Book from Data Tag.  (Server Side only)
	 * @param data ()
	 * @param manager ()
	 * @return ()
	 */
	public static @Nullable ServerRecipeBook getPlayerRecipeBook(@Nonnull CompoundData data, @Nonnull RecipeManager manager)
	{
		ServerRecipeBook book = null;

		if (data.contains(NbtKeys.RECIPE_BOOK, Constants.NBT.TAG_COMPOUND))
		{
			book = new ServerRecipeBook(manager::listDisplaysForRecipe);
			CompoundTag nbt = DataConverterNbt.toVanillaCompound(data.getCompoundOrDefault(NbtKeys.RECIPE_BOOK, new CompoundData()));
			book.loadUntrusted(ServerRecipeBook.Packed.CODEC
					            .parse(NbtOps.INSTANCE, nbt).getOrThrow(),
			            (key) -> manager.byKey(key).isPresent()
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
		BlockPos pos = BlockPos.ZERO;
		int radius = -1;

		if (data.containsLenient(NbtKeys.HOME_POS))
		{
			pos = data.getCodec(NbtKeys.HOME_POS, BlockPos.CODEC).orElse(BlockPos.ZERO);
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
	public static Pair<WeatheringCopper.WeatherState, Long> getWeatheringData(@Nonnull CompoundData data)
	{
		WeatheringCopper.WeatherState level = WeatheringCopper.WeatherState.UNAFFECTED;
		long age = -1L;

		if (data.contains(NbtKeys.WEATHER_STATE, Constants.NBT.TAG_STRING))
		{
			level = data.getCodec(NbtKeys.WEATHER_STATE, WeatheringCopper.WeatherState.CODEC).orElse(WeatheringCopper.WeatherState.UNAFFECTED);
		}

		if (data.contains(NbtKeys.NEXT_WEATHER_AGE, Constants.NBT.TAG_LONG))
		{
			age = data.getLong(NbtKeys.NEXT_WEATHER_AGE);
		}

		return Pair.of(level, age);
	}
}
