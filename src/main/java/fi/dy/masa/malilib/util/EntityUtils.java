package fi.dy.masa.malilib.util;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.google.common.collect.Maps;
import com.llamalad7.mixinextras.lib.apache.commons.tuple.Pair;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Dynamic;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.VillagerData;

public class EntityUtils
{
    /**
     * Returns the camera entity, if it's not null, otherwise returns the client player entity.
     *
     * @return ()
     */
    @Nullable
    public static Entity getCameraEntity()
    {
        MinecraftClient mc = MinecraftClient.getInstance();
        Entity entity = mc.getCameraEntity();

        if (entity == null)
        {
            entity = mc.player;
        }

        return entity;
    }

    /**
     * Returns weather or not the Entity has a Turtle Helmet equipped
     *
     * @param player (The Player)
     * @return (True / False)
     */
    public static boolean hasTurtleHelmetEquipped(PlayerEntity player)
    {
        if (player == null)
        {
            return false;
        }

        ItemStack stack = player.getEquippedStack(EquipmentSlot.HEAD);

        return !stack.isEmpty() && stack.isOf(Items.TURTLE_HELMET);
    }

    /**
     * Get an EntityType from NBT.
     *
     * @param nbt ()
     * @return ()
     */
    public static @Nullable EntityType<?> getEntityTypeFromNbt(@Nonnull NbtCompound nbt)
    {
        if (nbt.contains(NbtKeys.ID, Constants.NBT.TAG_STRING))
        {
            return Registries.ENTITY_TYPE.getOptionalValue(Identifier.tryParse(nbt.getString(NbtKeys.ID))).orElse(null);
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
     * Get the AttributeContainer from NBT
     *
     * @param nbt ()
     * @return ()
     */
    @SuppressWarnings("unchecked")
    public static @Nullable AttributeContainer getAttributesFromNbt(@Nonnull NbtCompound nbt)
    {
        EntityType<?> type = getEntityTypeFromNbt(nbt);

        if (type != null && nbt.contains(NbtKeys.ATTRIB, Constants.NBT.TAG_LIST))
        {
            return new AttributeContainer(DefaultAttributeRegistry.get((EntityType<? extends LivingEntity>) type));
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
    public static Pair<Float, Float> getHealthFromNbt(@Nonnull NbtCompound nbt)
    {
        float health = 0;
        double maxHealth;

        if (nbt.contains(NbtKeys.HEALTH, Constants.NBT.TAG_ANY_NUMERIC))
        {
            health = nbt.getFloat(NbtKeys.HEALTH);
        }
        maxHealth = getAttributeValueFromNbt(nbt, EntityAttributes.MAX_HEALTH);
        if (maxHealth < 0)
        {
            maxHealth = (float) 20;
        }

        return Pair.of(health, (float) maxHealth);
    }

    /**
     * Get an entities Movement Speed, and Jump Strength attributes from NBT.
     *
     * @param nbt ()
     * @return ()
     */
    public static Pair<Float, Float> getSpeedAndJumpStrengthFromNbt(@Nonnull NbtCompound nbt)
    {
        float moveSpeed = (float) getAttributeValueFromNbt(nbt, EntityAttributes.MOVEMENT_SPEED);
        float jumpStrength = (float) getAttributeValueFromNbt(nbt, EntityAttributes.JUMP_STRENGTH);

        return Pair.of(moveSpeed, jumpStrength);
    }

    /**
     * Get the Entity's UUID from NBT.
     *
     * @param nbt ()
     * @return ()
     */
    public static @Nullable UUID getUUIDFromNbt(@Nonnull NbtCompound nbt)
    {
        if (nbt.containsUuid(NbtKeys.UUID))
        {
            return nbt.getUuid(NbtKeys.UUID);
        }

        return null;
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
        if (nbt.contains(NbtKeys.CUSTOM_NAME, Constants.NBT.TAG_STRING))
        {
            String string = nbt.getString(NbtKeys.CUSTOM_NAME);

            try
            {
                return Text.Serialization.fromJson(string, registry);
            }
            catch (Exception ignored) { }
        }

        return null;
    }

    /**
     * Write a CustomName to NBT.
     *
     * @param name ()
     * @param registry ()
     * @param nbtIn ()
     * @return (Nbt Out)
     */
    public static NbtCompound setCustomNameToNbt(@Nonnull Text name, @Nonnull DynamicRegistryManager registry, @Nullable NbtCompound nbtIn)
    {
        NbtCompound nbt = new NbtCompound();

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

        return nbt;
    }

    /**
     * Get a Map of all active Status Effects via NBT.
     *
     * @param nbt ()
     * @return ()
     */
    public static Map<RegistryEntry<StatusEffect>, StatusEffectInstance> getActiveStatusEffectsFromNbt(@Nonnull NbtCompound nbt)
    {
        Map<RegistryEntry<StatusEffect>, StatusEffectInstance> statusEffects = Maps.newHashMap();

        if (nbt.contains(NbtKeys.EFFECTS, Constants.NBT.TAG_LIST))
        {
            NbtList list = nbt.getList(NbtKeys.EFFECTS, Constants.NBT.TAG_COMPOUND);

            for (int i = 0; i < list.size(); i++)
            {
                NbtCompound data = list.getCompound(i);
                StatusEffectInstance instance = StatusEffectInstance.fromNbt(data);

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
     * @param nbt ()
     * @param registry ()
     * @return ()
     */
    public static DefaultedList<ItemStack> getHandItemsFromNbt(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
    {
        DefaultedList<ItemStack> list = DefaultedList.ofSize(2, ItemStack.EMPTY);

        if (nbt.contains(NbtKeys.HAND_ITEMS, Constants.NBT.TAG_LIST))
        {
            NbtList nbtList = nbt.getList(NbtKeys.HAND_ITEMS, Constants.NBT.TAG_COMPOUND);

            for (int i = 0; i < list.size(); i++)
            {
                list.set(i, ItemStack.fromNbtOrEmpty(registry, nbtList.getCompound(i)));
            }
        }

        return list;
    }

    /**
     * Get a ItemStack List of all Equipped Armor pieces.
     *
     * @param nbt ()
     * @param registry ()
     * @return ()
     */
    public static DefaultedList<ItemStack> getArmorItemsFromNbt(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
    {
        DefaultedList<ItemStack> list = DefaultedList.ofSize(4, ItemStack.EMPTY);

        if (nbt.contains(NbtKeys.ARMOR_ITEMS, Constants.NBT.TAG_LIST))
        {
            NbtList nbtList = nbt.getList(NbtKeys.ARMOR_ITEMS, Constants.NBT.TAG_COMPOUND);

            for (int i = 0; i < list.size(); i++)
            {
                list.set(i, ItemStack.fromNbtOrEmpty(registry, nbtList.getCompound(i)));
            }
        }

        return list;
    }

    /**
     * Get the 'Body Armor Item' for the Horse or Wolf Armor.
     *
     * @param nbt ()
     * @param registry ()
     * @return ()
     */
    public static ItemStack getBodyArmorFromNbt(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
    {
        if (nbt.contains(NbtKeys.BODY_ARMOR, Constants.NBT.TAG_COMPOUND))
        {
            return ItemStack.fromNbtOrEmpty(registry, nbt.getCompound(NbtKeys.BODY_ARMOR));
        }

        return ItemStack.EMPTY;
    }

    /**
     * Get the Tamable Entity's Owner and if they have a Saddle Equipped.
     *
     * @param nbt ()
     * @param registry ()
     * @return ()
     */
    public static Pair<UUID, ItemStack> getOwnerAndSaddle(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
    {
        UUID owner = Util.NIL_UUID;
        ItemStack saddle = ItemStack.EMPTY;

        if (nbt.containsUuid(NbtKeys.OWNER))
        {
            owner = nbt.getUuid(NbtKeys.OWNER);
        }
        if (nbt.contains(NbtKeys.SADDLE, Constants.NBT.TAG_COMPOUND))
        {
            saddle = ItemStack.fromNbtOrEmpty(registry, nbt.getCompound(NbtKeys.SADDLE));
        }

        return Pair.of(owner, saddle);
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
            breedingAge = nbt.getInt(NbtKeys.AGE);
        }
        if (nbt.contains(NbtKeys.FORCED_AGE))
        {
            forcedAge = nbt.getInt(NbtKeys.FORCED_AGE);
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
            Optional<TradeOfferList> opt = TradeOfferList.CODEC.parse(registry.getOps(NbtOps.INSTANCE), nbt.get(NbtKeys.OFFERS)).resultOrPartial();

            if (opt.isPresent())
            {
                return opt.get();
            }
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
        if (nbt.contains(NbtKeys.VILLAGER, Constants.NBT.TAG_COMPOUND))
        {
            Optional<VillagerData> opt = VillagerData.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, nbt.get(NbtKeys.VILLAGER))).resultOrPartial();

            if (opt.isPresent())
            {
                return opt.get();
            }
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

        if (nbt.contains(NbtKeys.ZOMBIE_CONVERSION, Constants.NBT.TAG_ANY_NUMERIC))
        {
            timer = nbt.getInt(NbtKeys.ZOMBIE_CONVERSION);
        }
        if (nbt.containsUuid(NbtKeys.CONVERSION_PLAYER))
        {
            player = nbt.getUuid(NbtKeys.CONVERSION_PLAYER);
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

        if (nbt.contains(NbtKeys.DROWNED_CONVERSION, Constants.NBT.TAG_ANY_NUMERIC))
        {
            drowning = nbt.getInt(NbtKeys.DROWNED_CONVERSION);
        }
        if (nbt.contains(NbtKeys.IN_WATER, Constants.NBT.TAG_INT))
        {
            inWater = nbt.getInt(NbtKeys.IN_WATER);
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
        if (nbt.contains(NbtKeys.STRAY_CONVERSION, Constants.NBT.TAG_ANY_NUMERIC))
        {
            return nbt.getInt(NbtKeys.STRAY_CONVERSION);
        }

        return -1;
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
     * Try to get the Leash Data from NBT using 'FakeLeashData' because LeashData is package-private
     * @param nbt ()
     * @return ()
     */
    @SuppressWarnings("unchecked")
    public static @Nullable FakeLeashData getLeashDataFromNbt(@Nonnull NbtCompound nbt)
    {
        FakeLeashData data = null;

        if (nbt.contains(NbtKeys.LEASH, Constants.NBT.TAG_COMPOUND))
        {
            data = new FakeLeashData(-1, null, Either.left(nbt.getCompound(NbtKeys.LEASH).getUuid(NbtKeys.UUID)));
        }
        else if (nbt.contains(NbtKeys.LEASH, Constants.NBT.TAG_INT_ARRAY))
        {
            Either<UUID, BlockPos> either = (Either) NbtHelper.toBlockPos(nbt, NbtKeys.LEASH).map(Either::right).orElse(null);

            if (either != null)
            {
                return new FakeLeashData(-1, null, either);
            }
        }

        return data;
    }

    /**
     * Fake "LeashData" record.  To change the values, just make a new one.
     *
     * @param unresolvedLeashHolderId
     * @param leashHolder
     * @param unresolvedLeashData
     */
    public record FakeLeashData(int unresolvedLeashHolderId, @Nullable Entity leashHolder, @Nullable Either<UUID, BlockPos> unresolvedLeashData) {}

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

        if (nbt.contains(NbtKeys.MAIN_GENE, Constants.NBT.TAG_STRING))
        {
            mainGene = PandaEntity.Gene.byName(nbt.getString(NbtKeys.MAIN_GENE));
        }
        if (nbt.contains(NbtKeys.HIDDEN_GENE, Constants.NBT.TAG_STRING))
        {
            hiddenGene = PandaEntity.Gene.byName(nbt.getString(NbtKeys.HIDDEN_GENE));
        }

        return Pair.of(mainGene, hiddenGene);
    }

    public static Pair<Direction, Direction> getItemFrameDirectionsFromNbt(@Nonnull NbtCompound nbt)
    {
        Direction facing = null;
        Direction rotation = null;

        if (nbt.contains(NbtKeys.FACING_2, Constants.NBT.TAG_BYTE))
        {
            facing = Direction.byId(nbt.getByte(NbtKeys.FACING_2));
        }
        if (nbt.contains(NbtKeys.ITEM_ROTATION, Constants.NBT.TAG_BYTE))
        {
            rotation = Direction.byId(nbt.getByte(NbtKeys.ITEM_ROTATION));
        }

        return Pair.of(facing, rotation);
    }

    public static Pair<Direction, PaintingVariant> getPaintingDataFromNbt(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
    {
        Direction facing = null;
        RegistryEntry<PaintingVariant> variant = null;

        if (nbt.contains(NbtKeys.FACING, Constants.NBT.TAG_BYTE))
        {
            facing = Direction.fromHorizontal(nbt.getByte(NbtKeys.FACING));
        }
        if (nbt.contains(NbtKeys.VARIANT, Constants.NBT.TAG_COMPOUND))
        {
            variant = PaintingEntity.VARIANT_ENTRY_CODEC.parse(registry.getOps(NbtOps.INSTANCE), nbt).resultOrPartial().orElse(null);
        }

        return Pair.of(facing, variant != null ? variant.value() : null);
    }

    public static @Nullable AxolotlEntity.Variant getAxolotlVariantFromNbt(@Nonnull NbtCompound nbt)
    {
        if (nbt.contains(NbtKeys.VARIANT_2, Constants.NBT.TAG_INT))
        {
            return AxolotlEntity.Variant.byId(nbt.getInt(NbtKeys.VARIANT_2));
        }

        return null;
    }

    public static Pair<CatVariant, DyeColor> getCatVariantFromNbt(@Nonnull NbtCompound nbt)
    {
        CatVariant variant = null;
        DyeColor collar = null;

        if (nbt.contains(NbtKeys.VARIANT, Constants.NBT.TAG_STRING))
        {
            RegistryEntry<CatVariant> entry = Registries.CAT_VARIANT.getEntry(Identifier.tryParse(nbt.getString(NbtKeys.VARIANT))).orElse(null);

            if (entry != null && entry.hasKeyAndValue())
            {
                variant = entry.value();
            }
        }
        if (nbt.contains(NbtKeys.COLLAR, Constants.NBT.TAG_ANY_NUMERIC))
        {
            collar = DyeColor.byId(nbt.getInt(NbtKeys.COLLAR));
        }

        return Pair.of(variant, collar);
    }

    public static @Nullable FrogVariant getFrogVariantFromNbt(@Nonnull NbtCompound nbt)
    {
        if (nbt.contains(NbtKeys.VARIANT, Constants.NBT.TAG_STRING))
        {
            RegistryEntry<FrogVariant> entry = Registries.FROG_VARIANT.getEntry(Identifier.tryParse(nbt.getString(NbtKeys.VARIANT))).orElse(null);

            if (entry != null && entry.hasKeyAndValue())
            {
                return entry.value();
            }
        }

        return null;
    }

    public static @Nullable HorseColor getHorseVariantFromNbt(@Nonnull NbtCompound nbt)
    {
        if (nbt.contains(NbtKeys.VARIANT_2, Constants.NBT.TAG_INT))
        {
            return HorseColor.byId(nbt.getInt(NbtKeys.VARIANT_2) & 255);
        }

        return null;
    }

    public static @Nullable ParrotEntity.Variant getParrotVariantFromNbt(@Nonnull NbtCompound nbt)
    {
        if (nbt.contains(NbtKeys.VARIANT_2, Constants.NBT.TAG_INT))
        {
            return ParrotEntity.Variant.byIndex(nbt.getInt(NbtKeys.VARIANT_2));
        }

        return null;
    }

    public @Nullable TropicalFishEntity.Variety getFishVariantFromNbt(@Nonnull NbtCompound nbt)
    {
        if (nbt.contains(NbtKeys.VARIANT_2, Constants.NBT.TAG_INT))
        {
            return TropicalFishEntity.Variety.fromId(nbt.getInt(NbtKeys.VARIANT_2) & '\uffff');
        }
        else if (nbt.contains(NbtKeys.BUCKET_VARIANT, Constants.NBT.TAG_INT))
        {
            return TropicalFishEntity.Variety.fromId(nbt.getInt(NbtKeys.BUCKET_VARIANT) & '\uffff');
        }

        return null;
    }

    public static Pair<WolfVariant, DyeColor> getWolfVariantFromNbt(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
    {
        WolfVariant variant = null;
        DyeColor collar = null;

        if (nbt.contains(NbtKeys.VARIANT, Constants.NBT.TAG_STRING))
        {
            RegistryKey<WolfVariant> key = RegistryKey.of(RegistryKeys.WOLF_VARIANT, Identifier.tryParse(nbt.getString(NbtKeys.VARIANT)));

            if (key != null)
            {
                RegistryEntry.Reference<WolfVariant> opt = registry.getOrThrow(RegistryKeys.WOLF_VARIANT).getOptional(key).orElse(null);

                if (opt != null && opt.hasKeyAndValue())
                {
                    variant = opt.value();
                }
            }
        }
        if (nbt.contains(NbtKeys.COLLAR, Constants.NBT.TAG_ANY_NUMERIC))
        {
            collar = DyeColor.byId(nbt.getInt(NbtKeys.COLLAR));
        }

        return Pair.of(variant, collar);
    }
}
