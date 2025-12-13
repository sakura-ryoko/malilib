package fi.dy.masa.malilib.util.nbt;

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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
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

public class NbtEntityUtils
{
    /**
     * Attempt to Invoke a custom version of writeData() without any passenger data.
     * @param entity ()
     * @param id ()
     * @return ()
     */
    public static NbtCompound invokeEntityNbtDataNoPassengers(Entity entity, final int id)
    {
        return ((INbtEntityInvoker) entity).malilib$getNbtDataWithId(id).orElseGet(NbtCompound::new);
    }

	/**
     * Get an EntityType from NBT.
     *
     * @param nbt ()
     * @return ()
     */
    public static @Nullable EntityType<?> getEntityTypeFromNbt(@Nonnull NbtCompound nbt)
    {
        if (nbt.contains(NbtKeys.ID))
        {
            return Registries.ENTITY_TYPE.getOptionalValue(Identifier.tryParse(nbt.getString(NbtKeys.ID, ""))).orElse(null);
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
    public NbtCompound setEntityTypeToNbt(EntityType<?> type, @Nullable NbtCompound nbtIn)
    {
        NbtCompound nbt = new NbtCompound();
        Identifier id = EntityType.getId(type);

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
     * Get the AttributeContainer from NBT
     *
     * @param nbt ()
     * @return ()
     */
    @SuppressWarnings("unchecked")
    public static @Nullable AttributeContainer getAttributesFromNbt(@Nonnull NbtCompound nbt)
    {
        EntityType<?> type = getEntityTypeFromNbt(nbt);

        if (type != null && nbt.contains(NbtKeys.ATTRIB))
        {
            AttributeContainer container = new AttributeContainer(DefaultAttributeRegistry.get((EntityType<? extends LivingEntity>) type));
//            container.readNbt(nbt.getListOrEmpty(NbtKeys.ATTRIB));
            container.unpack(EntityAttributeInstance.Packed.LIST_CODEC.parse(NbtOps.INSTANCE, nbt.getListOrEmpty(NbtKeys.ATTRIB)).getPartialOrThrow());
            return container;
        }

        return null;
    }

    public static double getAttributeBaseValueFromNbt(@Nonnull NbtCompound nbt, RegistryEntry<EntityAttribute> attribute)
    {
        AttributeContainer attributes = getAttributesFromNbt(nbt);

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
    public static double getAttributeValueFromNbt(@Nonnull NbtCompound nbt, RegistryEntry<EntityAttribute> attribute)
    {
        AttributeContainer attributes = getAttributesFromNbt(nbt);

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
    public static Pair<Double, Double> getHealthFromNbt(@Nonnull NbtCompound nbt)
    {
        double health = 0;
        double maxHealth;

        if (nbt.contains(NbtKeys.HEALTH))
        {
            health = nbt.getFloat(NbtKeys.HEALTH, 0f);
        }

        maxHealth = getAttributeValueFromNbt(nbt, EntityAttributes.MAX_HEALTH);

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
    public static Pair<Double, Double> getSpeedAndJumpStrengthFromNbt(@Nonnull NbtCompound nbt)
    {
        AttributeContainer container = getAttributesFromNbt(nbt);
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
     * Read the CustomName from NBT
     *
     * @param nbt ()
     * @param registry ()
     * @return ()
     */
    public static @Nullable Text getCustomNameFromNbt(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
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

            return nbt.get(NbtKeys.CUSTOM_NAME, TextCodecs.CODEC, registry.getOps(NbtOps.INSTANCE)).orElse(null);
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
    public static NbtCompound setCustomNameToNbt(@Nonnull Text name, @Nonnull DynamicRegistryManager registry, @Nullable NbtCompound nbtIn, String key)
    {
        NbtCompound nbt = nbtIn != null ? nbtIn.copy() : new NbtCompound();

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

        nbt.put(key, TextCodecs.CODEC, registry.getOps(NbtOps.INSTANCE), name);

        return nbt;
    }

    /**
     * Get a Map of all active Status Effects via NBT.
     *
     * @param nbt ()
     * @return ()
     */
    public static Map<RegistryEntry<StatusEffect>, StatusEffectInstance> getActiveStatusEffectsFromNbt(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
    {
        Map<RegistryEntry<StatusEffect>, StatusEffectInstance> statusEffects = Maps.newHashMap();

        if (nbt.contains(NbtKeys.EFFECTS))
        {
            List<StatusEffectInstance> list = nbt.get(NbtKeys.EFFECTS, StatusEffectInstance.CODEC.listOf(), registry.getOps(NbtOps.INSTANCE)).orElse(List.of());

            for (StatusEffectInstance instance : list)
            {
                statusEffects.put(instance.getEffectType(), instance);
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
	public static @Nullable EntityEquipment getEquipmentSlotsFromNbt(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
	{
		if (nbt.contains(NbtKeys.EQUIPMENT))
		{
			Optional<EntityEquipment> opt = EntityEquipment.CODEC.parse(registry.getOps(NbtOps.INSTANCE), nbt.get(NbtKeys.EQUIPMENT)).result();

			if (opt.isPresent())
			{
				return opt.get();
			}
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
	public static @Nullable NbtElement setEquipmentSlotsToNbt(@Nonnull EntityEquipment equipment, @Nonnull DynamicRegistryManager registry)
	{
		try
		{
			return EntityEquipment.CODEC.encodeStart(registry.getOps(NbtOps.INSTANCE), equipment).getOrThrow();
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
    public static DefaultedList<ItemStack> getHandItemsFromNbt(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
    {
        DefaultedList<ItemStack> list = DefaultedList.ofSize(2, ItemStack.EMPTY);
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
    public static DefaultedList<ItemStack> getHumanoidArmorFromNbt(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
    {
        DefaultedList<ItemStack> list = DefaultedList.ofSize(4, ItemStack.EMPTY);
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
    public static DefaultedList<ItemStack> getHorseEquipmentFromNbt(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
    {
        DefaultedList<ItemStack> list = DefaultedList.ofSize(2, ItemStack.EMPTY);
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
    public static DefaultedList<ItemStack> getAllEquipmentFromNbt(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
    {
        DefaultedList<ItemStack> list = DefaultedList.ofSize(8, ItemStack.EMPTY);
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
    public static Pair<UUID, Boolean> getTamableOwner(@Nonnull NbtCompound nbt)
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
    public static Pair<Integer, Integer> getAgeFromNbt(@Nonnull NbtCompound nbt)
    {
        int breedingAge = 0;
        int forcedAge = 0;

        if (nbt.contains(NbtKeys.AGE))
        {
            breedingAge = nbt.getInt(NbtKeys.AGE, 0);
        }

        if (nbt.contains(NbtKeys.FORCED_AGE))
        {
            forcedAge = nbt.getInt(NbtKeys.FORCED_AGE, 0);
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
    public static @Nullable TradeOfferList getTradeOffersFromNbt(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
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

            return nbt.get(NbtKeys.OFFERS, TradeOfferList.CODEC, registry.getOps(NbtOps.INSTANCE)).orElse(null);
        }

        return null;
    }

    /**
     * Get the Villager Data object from NBT
     *
     * @param nbt ()
     * @return ()
     */
    public static @Nullable VillagerData getVillagerDataFromNbt(@Nonnull NbtCompound nbt)
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

            return nbt.get(NbtKeys.VILLAGER, VillagerData.CODEC).orElse(null);
        }

        return null;
    }

    /**
     * Get the Zombie Villager cure timer.
     *
     * @param nbt ()
     * @return ()
     */
    public static Pair<Integer, UUID> getZombieConversionTimerFromNbt(@Nonnull NbtCompound nbt)
    {
        int timer = -1;
        UUID player = Util.NIL_UUID;

        if (nbt.contains(NbtKeys.ZOMBIE_CONVERSION))
        {
            timer = nbt.getInt(NbtKeys.ZOMBIE_CONVERSION, -1);
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
    public static Pair<Integer, Integer> getDrownedConversionTimerFromNbt(@Nonnull NbtCompound nbt)
    {
        int drowning = -1;
        int inWater = -1;

        if (nbt.contains(NbtKeys.DROWNED_CONVERSION))
        {
            drowning = nbt.getInt(NbtKeys.DROWNED_CONVERSION, -1);
        }
        if (nbt.contains(NbtKeys.IN_WATER))
        {
            inWater = nbt.getInt(NbtKeys.IN_WATER, -1);
        }

        return Pair.of(drowning, inWater);
    }

    /**
     * Get Stray Conversion Timer from being in Powered Snow
     *
     * @param nbt ()
     * @return ()
     */
    public static int getStrayConversionTimeFromNbt(@Nonnull NbtCompound nbt)
    {
        if (nbt.contains(NbtKeys.STRAY_CONVERSION))
        {
            return nbt.getInt(NbtKeys.STRAY_CONVERSION, -1);
        }

        return -1;
    }

    /**
     * Try to get the Leash Data from NBT using LeashData (Not Fake)
     * @param nbt ()
     * @return ()
     */
    public static @Nullable Leashable.LeashData getLeashDataFromNbt(@Nonnull NbtCompound nbt)
    {
        if (nbt.contains(NbtKeys.LEASH))
        {
            return nbt.get(NbtKeys.LEASH, Leashable.LeashData.CODEC).orElse(null);
        }

        return null;
    }

    /**
     * Get the Panda Gene's from NBT
     *
     * @param nbt ()
     * @return ()
     */
    public static Pair<PandaEntity.Gene, PandaEntity.Gene> getPandaGenesFromNbt(@Nonnull NbtCompound nbt)
    {
        PandaEntity.Gene mainGene = null;
        PandaEntity.Gene hiddenGene = null;

        if (nbt.contains(NbtKeys.MAIN_GENE))
        {
            mainGene = nbt.get(NbtKeys.MAIN_GENE, PandaEntity.Gene.CODEC).orElse(PandaEntity.Gene.NORMAL);
        }
        if (nbt.contains(NbtKeys.HIDDEN_GENE))
        {
            hiddenGene = nbt.get(NbtKeys.HIDDEN_GENE, PandaEntity.Gene.CODEC).orElse(PandaEntity.Gene.NORMAL);
        }

        return Pair.of(mainGene, hiddenGene);
    }

    /**
     * Get an Item Frame's Rotation and Facing Directions from NBT.
     *
     * @param nbt ()
     * @return ()
     */
    public static Pair<Direction, Direction> getItemFrameDirectionsFromNbt(@Nonnull NbtCompound nbt)
    {
        Direction facing = null;
        Direction rotation = null;

        if (nbt.contains(NbtKeys.FACING_2))
        {
	        facing = NbtUtils.readDirectionFromTag(nbt, NbtKeys.FACING_2);
        }
        if (nbt.contains(NbtKeys.ITEM_ROTATION))
        {
            rotation = Direction.byIndex(nbt.getByte(NbtKeys.ITEM_ROTATION, (byte) 0));
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
    public static Pair<Direction, PaintingVariant> getPaintingDataFromNbt(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
    {
        Direction facing = null;
        RegistryEntry<PaintingVariant> variant = null;

        if (nbt.contains(NbtKeys.FACING))
        {
			facing = NbtUtils.readDirectionFromTag(nbt, NbtKeys.FACING);
        }
        if (nbt.contains(NbtKeys.VARIANT))
        {
            variant = PaintingVariant.ENTRY_CODEC.fieldOf(NbtKeys.VARIANT).codec()
                                     .parse(registry.getOps(NbtOps.INSTANCE), nbt)
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
    public static @Nullable AxolotlEntity.Variant getAxolotlVariantFromNbt(@Nonnull NbtCompound nbt)
    {
        if (nbt.contains(NbtKeys.VARIANT_2))
        {
            return nbt.get(NbtKeys.VARIANT_2, AxolotlEntity.Variant.INDEX_CODEC).orElse(AxolotlEntity.Variant.LUCY);
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
    public static Pair<RegistryKey<CatVariant>, DyeColor> getCatVariantFromNbt(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
    {
        RegistryKey<CatVariant> variantKey = null;
        DyeColor collar = null;

        if (nbt.contains(NbtKeys.VARIANT))
        {
            Optional<RegistryEntry<CatVariant>> variant = CatVariant.ENTRY_CODEC
                    .fieldOf(NbtKeys.VARIANT).codec()
                    .parse(registry.getOps(NbtOps.INSTANCE), nbt)
                    .resultOrPartial();

            variantKey = variant.map(entry -> entry.getKey().orElseThrow()).orElse(CatVariants.BLACK);
        }
        if (nbt.contains(NbtKeys.COLLAR))
        {
            collar = nbt.get(NbtKeys.COLLAR, DyeColor.INDEX_CODEC).orElse(DyeColor.RED);
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
    public static @Nullable RegistryKey<ChickenVariant> getChickenVariantFromNbt(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
    {
        if (nbt.contains(NbtKeys.VARIANT))
        {
            Optional<RegistryEntry<ChickenVariant>> variant = ChickenVariant.ENTRY_CODEC
                    .fieldOf(NbtKeys.VARIANT).codec()
                    .parse(registry.getOps(NbtOps.INSTANCE), nbt)
                    .resultOrPartial();

            return variant.map(entry -> entry.getKey().orElseThrow()).orElse(ChickenVariants.DEFAULT);
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
    public static @Nullable RegistryKey<CowVariant> getCowVariantFromNbt(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
    {
        if (nbt.contains(NbtKeys.VARIANT))
        {
            Optional<RegistryEntry<CowVariant>> variant = CowVariant.ENTRY_CODEC
                    .fieldOf(NbtKeys.VARIANT).codec()
                    .parse(registry.getOps(NbtOps.INSTANCE), nbt)
                    .resultOrPartial();

            return variant.map(entry -> entry.getKey().orElseThrow()).orElse(CowVariants.DEFAULT);
        }

        return null;
    }

    /**
     * Get a Mooshroom Variant from NBT.
     *
     * @param nbt ()
     * @return ()
     */
    public static @Nullable MooshroomEntity.Variant getMooshroomVariantFromNbt(@Nonnull NbtCompound nbt)
    {
        if (nbt.contains(NbtKeys.TYPE_2))
        {
            return nbt.get(NbtKeys.TYPE_2, MooshroomEntity.Variant.CODEC).orElse(MooshroomEntity.Variant.RED);
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
    public static @Nullable RegistryKey<FrogVariant> getFrogVariantFromNbt(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
    {
        if (nbt.contains(NbtKeys.VARIANT))
        {
            Optional<RegistryEntry<FrogVariant>> variant = FrogVariant.ENTRY_CODEC
                    .fieldOf(NbtKeys.VARIANT).codec()
                    .parse(registry.getOps(NbtOps.INSTANCE), nbt)
                    .resultOrPartial();

            return variant.map(entry -> entry.getKey().orElseThrow()).orElse(FrogVariants.TEMPERATE);
        }

        return null;
    }

    /**
     * Get a Horse's Variant (Color, Markings) from NBT.
     * @param nbt ()
     * @return ()
     */
    public static Pair<HorseColor, HorseMarking> getHorseVariantFromNbt(@Nonnull NbtCompound nbt)
    {
        HorseColor color = null;
        HorseMarking marking = null;

        if (nbt.contains(NbtKeys.VARIANT_2))
        {
            int variant = nbt.getInt(NbtKeys.VARIANT_2, 0);
            color = HorseColor.byIndex(variant & 0xFF);
            marking = HorseMarking.byIndex((variant & 0xFF00) >> 8);
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
    public static @Nullable ParrotEntity.Variant getParrotVariantFromNbt(@Nonnull NbtCompound nbt)
    {
        if (nbt.contains(NbtKeys.VARIANT_2))
        {
            return nbt.get(NbtKeys.VARIANT_2, ParrotEntity.Variant.INDEX_CODEC).orElse(ParrotEntity.Variant.RED_BLUE);
        }

        return null;
    }

    /**
     * Get a Tropical Fish Variant from NBT.
     *
     * @param nbt ()
     * @return ()
     */
    public static @Nullable TropicalFishEntity.Pattern getFishVariantFromNbt(@Nonnull NbtCompound nbt)
    {
        if (nbt.contains(NbtKeys.VARIANT_2))
        {
            TropicalFishEntity.Variant variant = nbt.get(NbtKeys.VARIANT_2, TropicalFishEntity.Variant.CODEC).orElse(TropicalFishEntity.DEFAULT_VARIANT);
        }
        else if (nbt.contains(NbtKeys.BUCKET_VARIANT))
        {
            return TropicalFishEntity.Pattern.byIndex(nbt.getInt(NbtKeys.BUCKET_VARIANT, 0) & '\uffff');
        }

        return null;
    }

	/**
	 * Get a Tropical Fish Pattern from NBT.
	 *
	 * @param nbt ()
	 * @return ()
	 */
	public static @Nullable TropicalFishEntity.Pattern getFishPatternFromNbt(@Nonnull NbtCompound nbt)
	{
		if (nbt.contains(NbtKeys.VARIANT_2))
		{
			return nbt.get(NbtKeys.VARIANT_2, TropicalFishEntity.Variant.CODEC).orElse(TropicalFishEntity.DEFAULT_VARIANT).pattern();
		}
		else if (nbt.contains(NbtKeys.BUCKET_VARIANT))
		{
			return TropicalFishEntity.Pattern.byIndex(nbt.getInt(NbtKeys.BUCKET_VARIANT, 0) & '\uffff');
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
    public static Pair<RegistryKey<WolfVariant>, DyeColor> getWolfVariantFromNbt(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
    {
        RegistryKey<WolfVariant> variantKey = null;
        DyeColor collar = null;

        if (nbt.contains(NbtKeys.VARIANT))
        {
            Optional<RegistryEntry<WolfVariant>> variant = WolfVariant.ENTRY_CODEC
                    .fieldOf(NbtKeys.VARIANT).codec()
                    .parse(registry.getOps(NbtOps.INSTANCE), nbt)
                    .resultOrPartial();

            variantKey = variant.map(entry -> entry.getKey().orElseThrow()).orElse(WolfVariants.DEFAULT);
        }
        if (nbt.contains(NbtKeys.COLLAR))
        {
            collar = nbt.get(NbtKeys.COLLAR, DyeColor.INDEX_CODEC).orElse(DyeColor.RED);
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
    public static @Nullable RegistryKey<WolfSoundVariant> getWolfSoundTypeFromNbt(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
    {
        if (nbt.contains(NbtKeys.SOUND_VARIANT))
        {
            RegistryEntry.Reference<WolfSoundVariant> soundVariant = registry.getOrThrow(RegistryKeys.WOLF_SOUND_VARIANT).getEntry(Identifier.tryParse(nbt.getString(NbtKeys.SOUND_VARIANT, ""))).orElse(null);

            if (soundVariant != null)
            {
                return soundVariant.registryKey();
            }
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
    public static @Nullable DyeColor getSheepColorFromNbt(@Nonnull NbtCompound nbt)
    {
        if (nbt.contains(NbtKeys.COLOR))
        {
            return nbt.get(NbtKeys.COLOR, DyeColor.INDEX_CODEC).orElse(DyeColor.WHITE);
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
    public static @Nullable RabbitEntity.Variant getRabbitTypeFromNbt(@Nonnull NbtCompound nbt)
    {
        if (nbt.contains(NbtKeys.RABBIT_TYPE))
        {
            return nbt.get(NbtKeys.RABBIT_TYPE, RabbitEntity.Variant.INDEX_CODEC).orElse(RabbitEntity.Variant.BROWN);
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
    public static Pair<LlamaEntity.Variant, Integer> getLlamaTypeFromNbt(@Nonnull NbtCompound nbt)
    {
        LlamaEntity.Variant variant = null;
        int strength = -1;

        if (nbt.contains(NbtKeys.VARIANT_2))
        {
            variant = nbt.get(NbtKeys.VARIANT_2, LlamaEntity.Variant.INDEX_CODEC).orElse(LlamaEntity.Variant.CREAMY);
        }

        if (nbt.contains(NbtKeys.STRENGTH))
        {
            strength = nbt.getInt(NbtKeys.STRENGTH, -1);
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
    public static @Nullable RegistryKey<PigVariant> getPigVariantFromNbt(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
    {
        if (nbt.contains(NbtKeys.VARIANT))
        {
			Optional<RegistryEntry.Reference<PigVariant>> opt = registry.getOrThrow(RegistryKeys.PIG_VARIANT).getEntry(Identifier.tryParse(nbt.getString(NbtKeys.VARIANT, "")));

            if (opt.isPresent())
            {
                return opt.get().registryKey();
            }

            return PigVariants.DEFAULT;
        }

        return null;
    }

    /**
     * Get a Fox's Variant type from NBT.
     *
     * @param nbt ()
     * @return ()
     */
    public static @Nullable FoxEntity.Variant getFoxVariantFromNbt(@Nonnull NbtCompound nbt)
    {
        if (nbt.contains(NbtKeys.TYPE_2))
        {
            return nbt.get(NbtKeys.TYPE_2, FoxEntity.Variant.CODEC).orElse(FoxEntity.Variant.RED);
        }

        return null;
    }

    /**
     * Get a Salmon's Variant type from NBT.
     *
     * @param nbt ()
     * @return ()
     */
    public static @Nullable SalmonEntity.Variant getSalmonVariantFromNbt(@Nonnull NbtCompound nbt)
    {
        if (nbt.contains(NbtKeys.TYPE))
        {
            return nbt.get(NbtKeys.TYPE, SalmonEntity.Variant.CODEC).orElse(SalmonEntity.Variant.MEDIUM);
        }

        return null;
    }

    /**
     * Get a Dolphin's TreasurePos and other data from NBT.
     *
     * @param nbt ()
     * @return ()
     */
    public static Pair<Integer, Boolean> getDolphinDataFromNbt(@Nonnull NbtCompound nbt)
    {
        boolean hasFish = false;
        int moist = -1;

        if (nbt.contains(NbtKeys.MOISTNESS))
        {
            moist = nbt.getInt(NbtKeys.MOISTNESS, -1);
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
    public static Triple<Integer, Integer, Float> getPlayerExpFromNbt(@Nonnull NbtCompound nbt)
    {
        int level = -1;
        int total = -1;
        float progress = 0.0f;

        if (nbt.contains(NbtKeys.EXP_LEVEL))
        {
            level = nbt.getInt(NbtKeys.EXP_LEVEL, -1);
        }
        if (nbt.contains(NbtKeys.EXP_TOTAL))
        {
            total = nbt.getInt(NbtKeys.EXP_TOTAL, -1);
        }
        if (nbt.contains(NbtKeys.EXP_PROGRESS))
        {
            progress = nbt.getFloat(NbtKeys.EXP_PROGRESS, 0.0f);
        }

        return Triple.of(level, total, progress);
    }

    /**
     * Get a Player's Hunger Manager from NBT.
     *
     * @param nbt ()
     * @return ()
     */
    public static @Nullable HungerManager getPlayerHungerFromNbt(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
    {
        HungerManager hunger = null;

        if (nbt.contains(NbtKeys.FOOD_LEVEL))
        {
            hunger = new HungerManager();
            NbtView view = NbtView.getReader(nbt, registry);
//            hunger.readNbt(nbt);
            hunger.readData(view.getReader());
        }

        return hunger;
    }

    /**
     * Get a Players' Unlocked Recipe Book from NBT.  (Server Side only)
     * @param nbt ()
     * @param manager ()
     * @return ()
     */
    public static @Nullable ServerRecipeBook getPlayerRecipeBookFromNbt(@Nonnull NbtCompound nbt, @Nonnull ServerRecipeManager manager)
    {
        ServerRecipeBook book = null;

        if (nbt.contains(NbtKeys.RECIPE_BOOK))
        {
            book = new ServerRecipeBook(manager::forEachRecipeDisplay);
            book.unpack(ServerRecipeBook.Packed.CODEC
                                .parse(NbtOps.INSTANCE, nbt.getCompoundOrEmpty(NbtKeys.RECIPE_BOOK)).getOrThrow(),
                        (key) -> manager.get(key).isPresent()
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
    public static Pair<BlockPos, Integer> getHomePosFromNbt(@Nonnull NbtCompound nbt)
    {
        BlockPos pos = BlockPos.ORIGIN;
        int radius = -1;

        if (nbt.contains(NbtKeys.HOME_POS))
        {
            pos = nbt.get(NbtKeys.HOME_POS, BlockPos.CODEC).orElse(BlockPos.ORIGIN);
        }

        if (nbt.contains(NbtKeys.HOME_RADIUS))
        {
            radius = nbt.getInt(NbtKeys.HOME_RADIUS, -1);
        }

        return Pair.of(pos, radius);
    }

	/**
	 * Get a Copper Golem's Weathering Data from NBT
	 * @param nbt ()
	 * @return ()
	 */
	public static Pair<Oxidizable.OxidationLevel, Long> getWeatheringDataFromNbt(@Nonnull NbtCompound nbt)
	{
		Oxidizable.OxidationLevel level = Oxidizable.OxidationLevel.UNAFFECTED;
		long age = -1L;

		if (nbt.contains(NbtKeys.WEATHER_STATE))
		{
			level = nbt.get(NbtKeys.WEATHER_STATE, Oxidizable.OxidationLevel.CODEC).orElse(Oxidizable.OxidationLevel.UNAFFECTED);
		}

		if (nbt.contains(NbtKeys.NEXT_WEATHER_AGE))
		{
			age = nbt.getLong(NbtKeys.NEXT_WEATHER_AGE, -1L);
		}

		return Pair.of(level, age);
	}
}
