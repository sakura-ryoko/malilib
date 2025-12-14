package fi.dy.masa.malilib.util.nbt;

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
import net.minecraft.nbt.Tag;
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

public class NbtEntityUtils
{
    /**
     * Attempt to Invoke a custom version of writeData() without any passenger data.
     * @param entity ()
     * @param id ()
     * @return ()
     */
    public static CompoundTag invokeEntityNbtDataNoPassengers(Entity entity, final int id)
    {
        return ((INbtEntityInvoker) entity).malilib$getNbtDataWithId(id).orElseGet(CompoundTag::new);
    }

	/**
     * Get an EntityType from NBT.
     *
     * @param nbt ()
     * @return ()
     */
    public static @Nullable EntityType<?> getEntityTypeFromNbt(@Nonnull CompoundTag nbt)
    {
        if (nbt.contains(NbtKeys.ID))
        {
            return BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.tryParse(nbt.getStringOr(NbtKeys.ID, ""))).orElse(null);
        }

        return null;
    }

	/**
     * Write an EntityType to NBT
     *
     * @param type ()
     * @param nbtIn ()
     * @return ()
     */
    public CompoundTag setEntityTypeToNbt(EntityType<?> type, @Nullable CompoundTag nbtIn)
    {
        CompoundTag nbt = new CompoundTag();
        Identifier id = EntityType.getKey(type);

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
     * Get the AttributeContainer from NBT
     *
     * @param nbt ()
     * @return ()
     */
    @SuppressWarnings("unchecked")
    public static @Nullable AttributeMap getAttributesFromNbt(@Nonnull CompoundTag nbt)
    {
        EntityType<?> type = getEntityTypeFromNbt(nbt);

        if (type != null && nbt.contains(NbtKeys.ATTRIB))
        {
            AttributeMap container = new AttributeMap(DefaultAttributes.getSupplier((EntityType<? extends @NotNull LivingEntity>) type));
//            container.readNbt(nbt.getListOrEmpty(NbtKeys.ATTRIB));
            container.apply(AttributeInstance.Packed.LIST_CODEC.parse(NbtOps.INSTANCE, nbt.getListOrEmpty(NbtKeys.ATTRIB)).getPartialOrThrow());
            return container;
        }

        return null;
    }

    public static double getAttributeBaseValueFromNbt(@Nonnull CompoundTag nbt, Holder<@NotNull Attribute> attribute)
    {
        AttributeMap attributes = getAttributesFromNbt(nbt);

        if (attributes != null)
        {
            return attributes.getBaseValue(attribute);
        }

        return -1;
    }

    /** Get a specified Attribute Value from NBT
     *
     * @param nbt ()
     * @param attribute ()
     * @return ()
     */
    public static double getAttributeValueFromNbt(@Nonnull CompoundTag nbt, Holder<@NotNull Attribute> attribute)
    {
        AttributeMap attributes = getAttributesFromNbt(nbt);

        if (attributes != null)
        {
            return attributes.getValue(attribute);
        }

        return -1;
    }

    /**
     * Get an entities' Health / Max Health from NBT.
     *
     * @param nbt ()
     * @return ()
     */
    public static Pair<Double, Double> getHealthFromNbt(@Nonnull CompoundTag nbt)
    {
        double health = 0;
        double maxHealth;

        if (nbt.contains(NbtKeys.HEALTH))
        {
            health = nbt.getFloatOr(NbtKeys.HEALTH, 0f);
        }

        maxHealth = getAttributeValueFromNbt(nbt, Attributes.MAX_HEALTH);

        if (maxHealth < 0)
        {
            maxHealth = 20;
        }

        return Pair.of(health, maxHealth);
    }

    /**
     * Get an entities Movement Speed, and Jump Strength attributes from NBT.
     *
     * @param nbt ()
     * @return ()
     */
    public static Pair<Double, Double> getSpeedAndJumpStrengthFromNbt(@Nonnull CompoundTag nbt)
    {
        AttributeMap container = getAttributesFromNbt(nbt);
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
     * Read the CustomName from NBT
     *
     * @param nbt ()
     * @param registry ()
     * @return ()
     */
    public static @Nullable Component getCustomNameFromNbt(@Nonnull CompoundTag nbt, @Nonnull RegistryAccess registry)
    {
        if (nbt.contains(NbtKeys.CUSTOM_NAME))
        {
            /*
            String string = nbt.getString(NbtKeys.CUSTOM_NAME);

            try
            {
                return Text.Serialization.fromJson(string, registry);
            }
            catch (Exception ignored) { }
             */

            return nbt.read(NbtKeys.CUSTOM_NAME, ComponentSerialization.CODEC, registry.createSerializationContext(NbtOps.INSTANCE)).orElse(null);
        }

        return null;
    }

    /**
     * Write a CustomName to NBT.
     *
     * @param name ()
     * @param registry ()
     * @param nbtIn ()
     * @param key ()
     * @return (Nbt Out)
     */
    public static CompoundTag setCustomNameToNbt(@Nonnull Component name, @Nonnull RegistryAccess registry, @Nullable CompoundTag nbtIn, String key)
    {
        CompoundTag nbt = nbtIn != null ? nbtIn.copy() : new CompoundTag();

        /*
        try
        {
            if (nbtIn != null)
            {
                nbtIn.putString(NbtKeys.CUSTOM_NAME, Text.Serialization.toJsonString(name, registry));
                return nbtIn;
            }
            else
            {
                nbt.putString(NbtKeys.CUSTOM_NAME, Text.Serialization.toJsonString(name, registry));
            }
        }
        catch (Exception ignored) {}
         */

        if (key == null || key.isEmpty())
        {
            key = NbtKeys.CUSTOM_NAME;
        }

        nbt.store(key, ComponentSerialization.CODEC, registry.createSerializationContext(NbtOps.INSTANCE), name);

        return nbt;
    }

    /**
     * Get a Map of all active Status Effects via NBT.
     *
     * @param nbt ()
     * @return ()
     */
    public static Map<Holder<@NotNull MobEffect>, MobEffectInstance> getActiveStatusEffectsFromNbt(@Nonnull CompoundTag nbt, @Nonnull RegistryAccess registry)
    {
        Map<Holder<@NotNull MobEffect>, MobEffectInstance> statusEffects = Maps.newHashMap();

        if (nbt.contains(NbtKeys.EFFECTS))
        {
            List<MobEffectInstance> list = nbt.read(NbtKeys.EFFECTS, MobEffectInstance.CODEC.listOf(), registry.createSerializationContext(NbtOps.INSTANCE)).orElse(List.of());

            for (MobEffectInstance instance : list)
            {
                statusEffects.put(instance.getEffect(), instance);
            }
        }

        return statusEffects;
    }

	/**
	 * Decode Equipment Slot values from NBT.
	 *
	 * @param nbt ()
	 * @param registry ()
	 * @return ()
	 */
	public static @Nullable EntityEquipment getEquipmentSlotsFromNbt(@Nonnull CompoundTag nbt, @Nonnull RegistryAccess registry)
	{
		if (nbt.contains(NbtKeys.EQUIPMENT))
		{
			return EntityEquipment.CODEC.parse(registry.createSerializationContext(NbtOps.INSTANCE), Objects.requireNonNull(nbt.get(NbtKeys.EQUIPMENT))).result().orElse(null);

//			if (opt.isPresent())
//			{
//				return opt.get();
//			}
		}

		return null;
	}

	/**
	 * Encode Equipment Slots to NBT.
	 *
	 * @param equipment ()
	 * @param registry ()
	 * @return ()
	 */
	public static @Nullable Tag setEquipmentSlotsToNbt(@Nonnull EntityEquipment equipment, @Nonnull RegistryAccess registry)
	{
		try
		{
			return EntityEquipment.CODEC.encodeStart(registry.createSerializationContext(NbtOps.INSTANCE), equipment).getOrThrow();
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
     * @param nbt ()
     * @param registry ()
     * @return ()
     */
    public static NonNullList<@NotNull ItemStack> getHandItemsFromNbt(@Nonnull CompoundTag nbt, @Nonnull RegistryAccess registry)
    {
        NonNullList<@NotNull ItemStack> list = NonNullList.withSize(2, ItemStack.EMPTY);
        EntityEquipment equipment = getEquipmentSlotsFromNbt(nbt, registry);

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
     * @param nbt ()
     * @param registry ()
     * @return ()
     */
    public static NonNullList<@NotNull ItemStack> getHumanoidArmorFromNbt(@Nonnull CompoundTag nbt, @Nonnull RegistryAccess registry)
    {
        NonNullList<@NotNull ItemStack> list = NonNullList.withSize(4, ItemStack.EMPTY);
        EntityEquipment equipment = getEquipmentSlotsFromNbt(nbt, registry);

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
     * @param nbt ()
     * @param registry ()
     * @return ()
     */
    public static NonNullList<@NotNull ItemStack> getHorseEquipmentFromNbt(@Nonnull CompoundTag nbt, @Nonnull RegistryAccess registry)
    {
        NonNullList<@NotNull ItemStack> list = NonNullList.withSize(2, ItemStack.EMPTY);
        EntityEquipment equipment = getEquipmentSlotsFromNbt(nbt, registry);

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
     * @param nbt ()
     * @param registry ()
     * @return ()
     */
    public static NonNullList<@NotNull ItemStack> getAllEquipmentFromNbt(@Nonnull CompoundTag nbt, @Nonnull RegistryAccess registry)
    {
        NonNullList<@NotNull ItemStack> list = NonNullList.withSize(8, ItemStack.EMPTY);
        EntityEquipment equipment = getEquipmentSlotsFromNbt(nbt, registry);

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
     * @param nbt ()
     * @return ()
     */
    public static Pair<UUID, Boolean> getTamableOwner(@Nonnull CompoundTag nbt)
    {
        UUID owner = Util.NIL_UUID;
        boolean sitting = false;

        if (nbt.contains(NbtKeys.OWNER))
        {
            owner = NbtUtils.getUUIDCodec(nbt, NbtKeys.OWNER);
        }

        if (nbt.contains(NbtKeys.SITTING))
        {
            sitting = nbt.getBoolean(NbtKeys.SITTING).orElse(false);
        }

        return Pair.of(owner, sitting);
    }

    /**
     * Get the Common Age / ForcedAge data from NBT
     *
     * @param nbt ()
     * @return ()
     */
    public static Pair<Integer, Integer> getAgeFromNbt(@Nonnull CompoundTag nbt)
    {
        int breedingAge = 0;
        int forcedAge = 0;

        if (nbt.contains(NbtKeys.AGE))
        {
            breedingAge = nbt.getIntOr(NbtKeys.AGE, 0);
        }

        if (nbt.contains(NbtKeys.FORCED_AGE))
        {
            forcedAge = nbt.getIntOr(NbtKeys.FORCED_AGE, 0);
        }

        return Pair.of(breedingAge, forcedAge);
    }

    /**
     * Get the Merchant Trade Offer's Object from NBT
     *
     * @param nbt ()
     * @param registry ()
     * @return ()
     */
    public static @Nullable MerchantOffers getTradeOffersFromNbt(@Nonnull CompoundTag nbt, @Nonnull RegistryAccess registry)
    {
        if (nbt.contains(NbtKeys.OFFERS))
        {
            /*
            Optional<TradeOfferList> opt = TradeOfferList.CODEC.parse(registry.getOps(NbtOps.INSTANCE), nbt.get(NbtKeys.OFFERS)).resultOrPartial();

            if (opt.isPresent())
            {
                return opt.get();
            }
             */

            return nbt.read(NbtKeys.OFFERS, MerchantOffers.CODEC, registry.createSerializationContext(NbtOps.INSTANCE)).orElse(null);
        }

        return null;
    }

    /**
     * Get the Villager Data object from NBT
     *
     * @param nbt ()
     * @return ()
     */
    public static @Nullable VillagerData getVillagerDataFromNbt(@Nonnull CompoundTag nbt)
    {
        if (nbt.contains(NbtKeys.VILLAGER))
        {
            /*
            Optional<VillagerData> opt = VillagerData.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, nbt.get(NbtKeys.VILLAGER))).resultOrPartial();

            if (opt.isPresent())
            {
                return opt.get();
            }
             */

            return nbt.read(NbtKeys.VILLAGER, VillagerData.CODEC).orElse(null);
        }

        return null;
    }

    /**
     * Get the Zombie Villager cure timer.
     *
     * @param nbt ()
     * @return ()
     */
    public static Pair<Integer, UUID> getZombieConversionTimerFromNbt(@Nonnull CompoundTag nbt)
    {
        int timer = -1;
        UUID player = Util.NIL_UUID;

        if (nbt.contains(NbtKeys.ZOMBIE_CONVERSION))
        {
            timer = nbt.getIntOr(NbtKeys.ZOMBIE_CONVERSION, -1);
        }
        if (nbt.contains(NbtKeys.CONVERSION_PLAYER))
        {
            player = NbtUtils.getUUIDCodec(nbt, NbtKeys.CONVERSION_PLAYER);
        }

        return Pair.of(timer, player);
    }

    /**
     * Get Drowned conversion timer from a Zombie being in Water
     *
     * @param nbt ()
     * @return ()
     */
    public static Pair<Integer, Integer> getDrownedConversionTimerFromNbt(@Nonnull CompoundTag nbt)
    {
        int drowning = -1;
        int inWater = -1;

        if (nbt.contains(NbtKeys.DROWNED_CONVERSION))
        {
            drowning = nbt.getIntOr(NbtKeys.DROWNED_CONVERSION, -1);
        }
        if (nbt.contains(NbtKeys.IN_WATER))
        {
            inWater = nbt.getIntOr(NbtKeys.IN_WATER, -1);
        }

        return Pair.of(drowning, inWater);
    }

    /**
     * Get Stray Conversion Timer from being in Powered Snow
     *
     * @param nbt ()
     * @return ()
     */
    public static int getStrayConversionTimeFromNbt(@Nonnull CompoundTag nbt)
    {
        if (nbt.contains(NbtKeys.STRAY_CONVERSION))
        {
            return nbt.getIntOr(NbtKeys.STRAY_CONVERSION, -1);
        }

        return -1;
    }

    /**
     * Try to get the Leash Data from NBT using LeashData (Not Fake)
     * @param nbt ()
     * @return ()
     */
    public static @Nullable Leashable.LeashData getLeashDataFromNbt(@Nonnull CompoundTag nbt)
    {
        if (nbt.contains(NbtKeys.LEASH))
        {
            return nbt.read(NbtKeys.LEASH, Leashable.LeashData.CODEC).orElse(null);
        }

        return null;
    }

    /**
     * Get the Panda Gene's from NBT
     *
     * @param nbt ()
     * @return ()
     */
    public static Pair<Panda.Gene, Panda.Gene> getPandaGenesFromNbt(@Nonnull CompoundTag nbt)
    {
        Panda.Gene mainGene = null;
        Panda.Gene hiddenGene = null;

        if (nbt.contains(NbtKeys.MAIN_GENE))
        {
            mainGene = nbt.read(NbtKeys.MAIN_GENE, Panda.Gene.CODEC).orElse(Panda.Gene.NORMAL);
        }
        if (nbt.contains(NbtKeys.HIDDEN_GENE))
        {
            hiddenGene = nbt.read(NbtKeys.HIDDEN_GENE, Panda.Gene.CODEC).orElse(Panda.Gene.NORMAL);
        }

        return Pair.of(mainGene, hiddenGene);
    }

    /**
     * Get an Item Frame's Rotation and Facing Directions from NBT.
     *
     * @param nbt ()
     * @return ()
     */
    public static Pair<Direction, Direction> getItemFrameDirectionsFromNbt(@Nonnull CompoundTag nbt)
    {
        Direction facing = null;
        Direction rotation = null;

        if (nbt.contains(NbtKeys.FACING_2))
        {
	        facing = NbtUtils.readDirectionFromTag(nbt, NbtKeys.FACING_2);
        }
        if (nbt.contains(NbtKeys.ITEM_ROTATION))
        {
            rotation = Direction.from3DDataValue(nbt.getByteOr(NbtKeys.ITEM_ROTATION, (byte) 0));
        }

        return Pair.of(facing, rotation);
    }

    /**
     * Get a Painting's Direction and Variant from BNT.
     *
     * @param nbt ()
     * @param registry ()
     * @return ()
     */
    public static Pair<Direction, PaintingVariant> getPaintingDataFromNbt(@Nonnull CompoundTag nbt, @Nonnull RegistryAccess registry)
    {
        Direction facing = null;
        Holder<@NotNull PaintingVariant> variant = null;

        if (nbt.contains(NbtKeys.FACING))
        {
			facing = NbtUtils.readDirectionFromTag(nbt, NbtKeys.FACING);
        }
        if (nbt.contains(NbtKeys.VARIANT))
        {
            variant = PaintingVariant.CODEC.fieldOf(NbtKeys.VARIANT).codec()
                                     .parse(registry.createSerializationContext(NbtOps.INSTANCE), nbt)
                                     .resultOrPartial().orElse(null);
        }

        return Pair.of(facing, variant != null ? variant.value() : null);
    }

    /**
     * Get an Axolotl's Variant from NBT.
     *
     * @param nbt ()
     * @return ()
     */
    @SuppressWarnings("deprecation")
    public static @Nullable Axolotl.Variant getAxolotlVariantFromNbt(@Nonnull CompoundTag nbt)
    {
        if (nbt.contains(NbtKeys.VARIANT_2))
        {
            return nbt.read(NbtKeys.VARIANT_2, Axolotl.Variant.LEGACY_CODEC).orElse(Axolotl.Variant.LUCY);
        }

        return null;
    }

    /**
     * Get a Cat's Variant, and Collar Color from NBT.
     *
     * @param nbt ()
     * @param registry ()
     * @return ()
     */
    @SuppressWarnings("deprecation")
    public static Pair<ResourceKey<@NotNull CatVariant>, DyeColor> getCatVariantFromNbt(@Nonnull CompoundTag nbt, @Nonnull RegistryAccess registry)
    {
        ResourceKey<@NotNull CatVariant> variantKey = null;
        DyeColor collar = null;

        if (nbt.contains(NbtKeys.VARIANT))
        {
            Optional<Holder<@NotNull CatVariant>> variant = CatVariant.CODEC
                    .fieldOf(NbtKeys.VARIANT).codec()
                    .parse(registry.createSerializationContext(NbtOps.INSTANCE), nbt)
                    .resultOrPartial();

            variantKey = variant.map(entry -> entry.unwrapKey().orElseThrow()).orElse(CatVariants.BLACK);
        }
        if (nbt.contains(NbtKeys.COLLAR))
        {
            collar = nbt.read(NbtKeys.COLLAR, DyeColor.LEGACY_ID_CODEC).orElse(DyeColor.RED);
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
     * Get a Chicken's Variant from NBT.
     *
     * @param nbt ()
     * @param registry ()
     * @return ()
     */
    public static @Nullable ResourceKey<@NotNull ChickenVariant> getChickenVariantFromNbt(@Nonnull CompoundTag nbt, @Nonnull RegistryAccess registry)
    {
        if (nbt.contains(NbtKeys.VARIANT))
        {
            Optional<Holder<@NotNull ChickenVariant>> variant = ChickenVariant.CODEC
                    .fieldOf(NbtKeys.VARIANT).codec()
                    .parse(registry.createSerializationContext(NbtOps.INSTANCE), nbt)
                    .resultOrPartial();

            return variant.map(entry -> entry.unwrapKey().orElseThrow()).orElse(ChickenVariants.DEFAULT);
        }

        return null;
    }

    /**
     * Get a Cow's Variant from NBT.
     *
     * @param nbt ()
     * @param registry ()
     * @return ()
     */
    public static @Nullable ResourceKey<@NotNull CowVariant> getCowVariantFromNbt(@Nonnull CompoundTag nbt, @Nonnull RegistryAccess registry)
    {
        if (nbt.contains(NbtKeys.VARIANT))
        {
            Optional<Holder<@NotNull CowVariant>> variant = CowVariant.CODEC
                    .fieldOf(NbtKeys.VARIANT).codec()
                    .parse(registry.createSerializationContext(NbtOps.INSTANCE), nbt)
                    .resultOrPartial();

            return variant.map(entry -> entry.unwrapKey().orElseThrow()).orElse(CowVariants.DEFAULT);
        }

        return null;
    }

    /**
     * Get a Mooshroom Variant from NBT.
     *
     * @param nbt ()
     * @return ()
     */
    public static @Nullable MushroomCow.Variant getMooshroomVariantFromNbt(@Nonnull CompoundTag nbt)
    {
        if (nbt.contains(NbtKeys.TYPE_2))
        {
            return nbt.read(NbtKeys.TYPE_2, MushroomCow.Variant.CODEC).orElse(MushroomCow.Variant.RED);
        }

        return null;
    }

    /**
     * Get a Frog's Variant from NBT.
     *
     * @param nbt ()
     * @param registry ()
     * @return ()
     */
    public static @Nullable ResourceKey<@NotNull FrogVariant> getFrogVariantFromNbt(@Nonnull CompoundTag nbt, @Nonnull RegistryAccess registry)
    {
        if (nbt.contains(NbtKeys.VARIANT))
        {
            Optional<Holder<@NotNull FrogVariant>> variant = FrogVariant.CODEC
                    .fieldOf(NbtKeys.VARIANT).codec()
                    .parse(registry.createSerializationContext(NbtOps.INSTANCE), nbt)
                    .resultOrPartial();

            return variant.map(entry -> entry.unwrapKey().orElseThrow()).orElse(FrogVariants.TEMPERATE);
        }

        return null;
    }

    /**
     * Get a Zombie Nautilus's Variant from NBT.
     *
     * @param nbt ()
     * @param registry ()
     * @return ()
     */
    public static @Nullable ResourceKey<@NotNull ZombieNautilusVariant> getZombieNautilusVariantFromNbt(@Nonnull CompoundTag nbt, @Nonnull RegistryAccess registry)
    {
        if (nbt.contains(NbtKeys.VARIANT))
        {
            Optional<Holder<@NotNull ZombieNautilusVariant>> variant = ZombieNautilusVariant.CODEC
                    .fieldOf(NbtKeys.VARIANT).codec()
                    .parse(registry.createSerializationContext(NbtOps.INSTANCE), nbt)
                    .resultOrPartial();

            return variant.map(entry -> entry.unwrapKey().orElseThrow()).orElse(ZombieNautilusVariants.DEFAULT);
        }

        return null;
    }

    /**
     * Get a Horse's Variant (Color, Markings) from NBT.
     * @param nbt ()
     * @return ()
     */
    public static Pair<Variant, Markings> getHorseVariantFromNbt(@Nonnull CompoundTag nbt)
    {
        Variant color = null;
        Markings marking = null;

        if (nbt.contains(NbtKeys.VARIANT_2))
        {
            int variant = nbt.getIntOr(NbtKeys.VARIANT_2, 0);
            color = Variant.byId(variant & 0xFF);
            marking = Markings.byId((variant & 0xFF00) >> 8);
        }

        return Pair.of(color, marking);
    }

    /**
     * Get a Parrot's Variant from NBT.
     *
     * @param nbt ()
     * @return ()
     */
    @SuppressWarnings("deprecation")
    public static @Nullable Parrot.Variant getParrotVariantFromNbt(@Nonnull CompoundTag nbt)
    {
        if (nbt.contains(NbtKeys.VARIANT_2))
        {
            return nbt.read(NbtKeys.VARIANT_2, Parrot.Variant.LEGACY_CODEC).orElse(Parrot.Variant.RED_BLUE);
        }

        return null;
    }

    /**
     * Get a Tropical Fish Variant from NBT.
     *
     * @param nbt ()
     * @return ()
     */
    public static @Nullable TropicalFish.Pattern getFishVariantFromNbt(@Nonnull CompoundTag nbt)
    {
        if (nbt.contains(NbtKeys.VARIANT_2))
        {
            TropicalFish.Variant variant = nbt.read(NbtKeys.VARIANT_2, TropicalFish.Variant.CODEC).orElse(TropicalFish.DEFAULT_VARIANT);
        }
        else if (nbt.contains(NbtKeys.BUCKET_VARIANT))
        {
            return TropicalFish.Pattern.byId(nbt.getIntOr(NbtKeys.BUCKET_VARIANT, 0) & '\uffff');
        }

        return null;
    }

	/**
	 * Get a Tropical Fish Pattern from NBT.
	 *
	 * @param nbt ()
	 * @return ()
	 */
	public static @Nullable TropicalFish.Pattern getFishPatternFromNbt(@Nonnull CompoundTag nbt)
	{
		if (nbt.contains(NbtKeys.VARIANT_2))
		{
			return nbt.read(NbtKeys.VARIANT_2, TropicalFish.Variant.CODEC).orElse(TropicalFish.DEFAULT_VARIANT).pattern();
		}
		else if (nbt.contains(NbtKeys.BUCKET_VARIANT))
		{
			return TropicalFish.Pattern.byId(nbt.getIntOr(NbtKeys.BUCKET_VARIANT, 0) & '\uffff');
		}

		return null;
	}

	/**
     * Get a Wolves' Variant and Collar Color from NBT.
     *
     * @param nbt ()
     * @return ()
     */
    @SuppressWarnings("deprecation")
    public static Pair<ResourceKey<@NotNull WolfVariant>, DyeColor> getWolfVariantFromNbt(@Nonnull CompoundTag nbt, @Nonnull RegistryAccess registry)
    {
        ResourceKey<@NotNull WolfVariant> variantKey = null;
        DyeColor collar = null;

        if (nbt.contains(NbtKeys.VARIANT))
        {
            Optional<Holder<@NotNull WolfVariant>> variant = WolfVariant.CODEC
                    .fieldOf(NbtKeys.VARIANT).codec()
                    .parse(registry.createSerializationContext(NbtOps.INSTANCE), nbt)
                    .resultOrPartial();

            variantKey = variant.map(entry -> entry.unwrapKey().orElseThrow()).orElse(WolfVariants.DEFAULT);
        }
        if (nbt.contains(NbtKeys.COLLAR))
        {
            collar = nbt.read(NbtKeys.COLLAR, DyeColor.LEGACY_ID_CODEC).orElse(DyeColor.RED);
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
     * Get a Wolves' Sound Type Variant from NBT.
     *
     * @param nbt ()
     * @return ()
     */
    public static @Nullable ResourceKey<@NotNull WolfSoundVariant> getWolfSoundTypeFromNbt(@Nonnull CompoundTag nbt, @Nonnull RegistryAccess registry)
    {
        if (nbt.contains(NbtKeys.SOUND_VARIANT))
        {
			try
			{
	            Holder.Reference<@NotNull WolfSoundVariant> soundVariant = registry.lookupOrThrow(Registries.WOLF_SOUND_VARIANT).get(Objects.requireNonNull(Identifier.tryParse(nbt.getStringOr(NbtKeys.SOUND_VARIANT, "")))).orElse(null);

	            if (soundVariant != null)
	            {
	                return soundVariant.key();
	            }
			}
			catch (Exception ignored) { }
        }

        return null;
    }

    /**
     * Get a Sheep's Color from NBT.
     *
     * @param nbt ()
     * @return ()
     */
    @SuppressWarnings("deprecation")
    public static @Nullable DyeColor getSheepColorFromNbt(@Nonnull CompoundTag nbt)
    {
        if (nbt.contains(NbtKeys.COLOR))
        {
            return nbt.read(NbtKeys.COLOR, DyeColor.LEGACY_ID_CODEC).orElse(DyeColor.WHITE);
        }

        return null;
    }

    /**
     * Get a Rabbit's Variant type from NBT.
     *
     * @param nbt ()
     * @return ()
     */
    @SuppressWarnings("deprecation")
    public static @Nullable Rabbit.Variant getRabbitTypeFromNbt(@Nonnull CompoundTag nbt)
    {
        if (nbt.contains(NbtKeys.RABBIT_TYPE))
        {
            return nbt.read(NbtKeys.RABBIT_TYPE, Rabbit.Variant.LEGACY_CODEC).orElse(Rabbit.Variant.BROWN);
        }

        return null;
    }

    /**
     * Get a Llama's Variant type from NBT.
     *
     * @param nbt ()
     * @return ()
     */
    @SuppressWarnings("deprecation")
    public static Pair<Llama.Variant, Integer> getLlamaTypeFromNbt(@Nonnull CompoundTag nbt)
    {
        Llama.Variant variant = null;
        int strength = -1;

        if (nbt.contains(NbtKeys.VARIANT_2))
        {
            variant = nbt.read(NbtKeys.VARIANT_2, Llama.Variant.LEGACY_CODEC).orElse(Llama.Variant.CREAMY);
        }

        if (nbt.contains(NbtKeys.STRENGTH))
        {
            strength = nbt.getIntOr(NbtKeys.STRENGTH, -1);
        }

        return Pair.of(variant, strength);
    }

    /**
     * Get a Pig's Variant type from NBT.
     *
     * @param nbt ()
     * @param registry ()
     * @return ()
     */
    public static @Nullable ResourceKey<@NotNull PigVariant> getPigVariantFromNbt(@Nonnull CompoundTag nbt, @Nonnull RegistryAccess registry)
    {
        if (nbt.contains(NbtKeys.VARIANT))
        {
			try
			{
				Optional<Holder.Reference<@NotNull PigVariant>> opt = registry.lookupOrThrow(Registries.PIG_VARIANT).get(Objects.requireNonNull(Identifier.tryParse(nbt.getStringOr(NbtKeys.VARIANT, ""))));

				return opt.map(Holder.Reference::key).orElse(PigVariants.DEFAULT);
			}
			catch (Exception ignored) { }
        }

        return null;
    }

    /**
     * Get a Fox's Variant type from NBT.
     *
     * @param nbt ()
     * @return ()
     */
    public static @Nullable Fox.Variant getFoxVariantFromNbt(@Nonnull CompoundTag nbt)
    {
        if (nbt.contains(NbtKeys.TYPE_2))
        {
            return nbt.read(NbtKeys.TYPE_2, Fox.Variant.CODEC).orElse(Fox.Variant.RED);
        }

        return null;
    }

    /**
     * Get a Salmon's Variant type from NBT.
     *
     * @param nbt ()
     * @return ()
     */
    public static @Nullable Salmon.Variant getSalmonVariantFromNbt(@Nonnull CompoundTag nbt)
    {
        if (nbt.contains(NbtKeys.TYPE))
        {
            return nbt.read(NbtKeys.TYPE, Salmon.Variant.CODEC).orElse(Salmon.Variant.MEDIUM);
        }

        return null;
    }

    /**
     * Get a Dolphin's TreasurePos and other data from NBT.
     *
     * @param nbt ()
     * @return ()
     */
    public static Pair<Integer, Boolean> getDolphinDataFromNbt(@Nonnull CompoundTag nbt)
    {
        boolean hasFish = false;
        int moist = -1;

        if (nbt.contains(NbtKeys.MOISTNESS))
        {
            moist = nbt.getIntOr(NbtKeys.MOISTNESS, -1);
        }

        if (nbt.contains(NbtKeys.GOT_FISH))
        {
            hasFish = nbt.getBoolean(NbtKeys.GOT_FISH).orElse(false);
        }

        return Pair.of(moist, hasFish);
    }

    /**
     * Get a player's Experience values from NBT.
     *
     * @param nbt ()
     * @return ()
     */
    public static Triple<Integer, Integer, Float> getPlayerExpFromNbt(@Nonnull CompoundTag nbt)
    {
        int level = -1;
        int total = -1;
        float progress = 0.0f;

        if (nbt.contains(NbtKeys.EXP_LEVEL))
        {
            level = nbt.getIntOr(NbtKeys.EXP_LEVEL, -1);
        }
        if (nbt.contains(NbtKeys.EXP_TOTAL))
        {
            total = nbt.getIntOr(NbtKeys.EXP_TOTAL, -1);
        }
        if (nbt.contains(NbtKeys.EXP_PROGRESS))
        {
            progress = nbt.getFloatOr(NbtKeys.EXP_PROGRESS, 0.0f);
        }

        return Triple.of(level, total, progress);
    }

    /**
     * Get a Player's Hunger Manager from NBT.
     *
     * @param nbt ()
     * @return ()
     */
    public static @Nullable FoodData getPlayerHungerFromNbt(@Nonnull CompoundTag nbt, @Nonnull RegistryAccess registry)
    {
        FoodData hunger = null;

        if (nbt.contains(NbtKeys.FOOD_LEVEL))
        {
            hunger = new FoodData();
            NbtView view = NbtView.getReader(nbt, registry);
//            hunger.readNbt(nbt);
            hunger.readAdditionalSaveData(view.getReader());
        }

        return hunger;
    }

    /**
     * Get a Players' Unlocked Recipe Book from NBT.  (Server Side only)
     * @param nbt ()
     * @param manager ()
     * @return ()
     */
    public static @Nullable ServerRecipeBook getPlayerRecipeBookFromNbt(@Nonnull CompoundTag nbt, @Nonnull RecipeManager manager)
    {
        ServerRecipeBook book = null;

        if (nbt.contains(NbtKeys.RECIPE_BOOK))
        {
            book = new ServerRecipeBook(manager::listDisplaysForRecipe);
            book.loadUntrusted(ServerRecipeBook.Packed.CODEC
                                .parse(NbtOps.INSTANCE, nbt.getCompoundOrEmpty(NbtKeys.RECIPE_BOOK)).getOrThrow(),
                        (key) -> manager.byKey(key).isPresent()
            );

//            book.readNbt(nbt.getCompoundOrEmpty(NbtKeys.RECIPE_BOOK), (key) -> manager.get(key).isPresent());
        }

        return book;
    }

    /**
     * Get a Mob's Home Pos and Radius from NBT
     * @param nbt ()
     * @return ()
     */
    public static Pair<BlockPos, Integer> getHomePosFromNbt(@Nonnull CompoundTag nbt)
    {
        BlockPos pos = BlockPos.ZERO;
        int radius = -1;

        if (nbt.contains(NbtKeys.HOME_POS))
        {
            pos = nbt.read(NbtKeys.HOME_POS, BlockPos.CODEC).orElse(BlockPos.ZERO);
        }

        if (nbt.contains(NbtKeys.HOME_RADIUS))
        {
            radius = nbt.getIntOr(NbtKeys.HOME_RADIUS, -1);
        }

        return Pair.of(pos, radius);
    }

	/**
	 * Get a Copper Golem's Weathering Data from NBT
	 * @param nbt ()
	 * @return ()
	 */
	public static Pair<WeatheringCopper.WeatherState, Long> getWeatheringDataFromNbt(@Nonnull CompoundTag nbt)
	{
		WeatheringCopper.WeatherState level = WeatheringCopper.WeatherState.UNAFFECTED;
		long age = -1L;

		if (nbt.contains(NbtKeys.WEATHER_STATE))
		{
			level = nbt.read(NbtKeys.WEATHER_STATE, WeatheringCopper.WeatherState.CODEC).orElse(WeatheringCopper.WeatherState.UNAFFECTED);
		}

		if (nbt.contains(NbtKeys.NEXT_WEATHER_AGE))
		{
			age = nbt.getLongOr(NbtKeys.NEXT_WEATHER_AGE, -1L);
		}

		return Pair.of(level, age);
	}
}
