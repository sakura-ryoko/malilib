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
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.VillagerData;

public class EntityUtils
{
    /**
     * Returns the camera entity, if it's not null, otherwise returns the client player entity.
     *
     * @return
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

    public static @Nullable EntityType<?> getEntityTypeFromNbt(@Nonnull NbtCompound nbt)
    {
        if (nbt.contains("id", Constants.NBT.TAG_STRING))
        {
            return Registries.ENTITY_TYPE.getOptionalValue(Identifier.tryParse(nbt.getString("id"))).orElse(null);
        }

        return null;
    }

    public NbtCompound setEntityTypeToNbt(EntityType<?> type, @Nullable NbtCompound nbtIn)
    {
        NbtCompound nbt = new NbtCompound();
        Identifier id = EntityType.getId(type);

        if (id != null)
        {
            if (nbtIn != null)
            {
                nbtIn.putString("id", id.toString());
                return nbtIn;
            }
            else
            {
                nbt.putString("id", id.toString());
            }
        }

        return nbt;
    }

    @SuppressWarnings("unchecked")
    public static @Nullable AttributeContainer getAttributesFromNbt(@Nonnull NbtCompound nbt)
    {
        EntityType<?> type = getEntityTypeFromNbt(nbt);

        if (type != null && nbt.contains("attributes", Constants.NBT.TAG_LIST))
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

    public static double getAttributeValueFromNbt(@Nonnull NbtCompound nbt, RegistryEntry<EntityAttribute> attribute)
    {
        AttributeContainer attributes = getAttributesFromNbt(nbt);

        if (attributes != null)
        {
            return attributes.getValue(attribute);
        }

        return -1;
    }

    public static Pair<Float, Float> getHealthFromNbt(@Nonnull NbtCompound nbt)
    {
        float health = 0;
        double maxHealth;

        if (nbt.contains("Health", Constants.NBT.TAG_ANY_NUMERIC))
        {
            health = nbt.getFloat("Health");
        }
        maxHealth = getAttributeValueFromNbt(nbt, EntityAttributes.MAX_HEALTH);
        if (maxHealth < 0)
        {
            maxHealth = (float) 20;
        }

        return Pair.of(health, (float) maxHealth);
    }

    public static Pair<Float, Float> getSpeedAndJumpStrengthFromNbt(@Nonnull NbtCompound nbt)
    {
        float moveSpeed = (float) getAttributeValueFromNbt(nbt, EntityAttributes.MOVEMENT_SPEED);
        float jumpStrength = (float) getAttributeValueFromNbt(nbt, EntityAttributes.JUMP_STRENGTH);

        return Pair.of(moveSpeed, jumpStrength);
    }

    public static @Nullable UUID getUUIDFromNbt(@Nonnull NbtCompound nbt)
    {
        if (nbt.containsUuid("UUID"))
        {
            return nbt.getUuid("UUID");
        }

        return null;
    }

    public static @Nullable Text getCustomNameFromNbt(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
    {
        if (nbt.contains("CustomName", Constants.NBT.TAG_STRING))
        {
            String string = nbt.getString("CustomName");

            try
            {
                return Text.Serialization.fromJson(string, registry);
            }
            catch (Exception ignored)
            {
            }
        }

        return null;
    }

    public static NbtCompound setCustomNameToNbt(@Nonnull Text name, @Nonnull DynamicRegistryManager registry, @Nullable NbtCompound nbtIn)
    {
        NbtCompound nbt = new NbtCompound();

        try
        {
            if (nbtIn != null)
            {
                nbtIn.putString("CustomName", Text.Serialization.toJsonString(name, registry));
                return nbtIn;
            }
            else
            {
                nbt.putString("CustomName", Text.Serialization.toJsonString(name, registry));
            }
        }
        catch (Exception ignored) {}

        return nbt;
    }

    public static @Nullable Map<RegistryEntry<StatusEffect>, StatusEffectInstance> getActiveStatusEffectsFromNbt(@Nonnull NbtCompound nbt)
    {
        Map<RegistryEntry<StatusEffect>, StatusEffectInstance> statusEffects = Maps.newHashMap();

        if (nbt.contains("active_effects", Constants.NBT.TAG_LIST))
        {
            NbtList list = nbt.getList("active_effects", Constants.NBT.TAG_COMPOUND);

            for (int i = 0; i < list.size(); i++)
            {
                NbtCompound data = list.getCompound(i);
                StatusEffectInstance instance = StatusEffectInstance.fromNbt(data);
                if (instance != null)
                {
                    statusEffects.put(instance.getEffectType(), instance);
                }
            }

            return statusEffects;
        }

        return null;
    }

    public static DefaultedList<ItemStack> getHandItemsFromNbt(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
    {
        DefaultedList<ItemStack> list = DefaultedList.ofSize(2, ItemStack.EMPTY);

        if (nbt.contains("HandItems", Constants.NBT.TAG_LIST))
        {
            NbtList nbtList = nbt.getList("HandItems", Constants.NBT.TAG_COMPOUND);

            for (int i = 0; i < list.size(); i++)
            {
                list.set(i, ItemStack.fromNbtOrEmpty(registry, nbtList.getCompound(i)));
            }
        }

        return list;
    }
    public static DefaultedList<ItemStack> getArmorItemsFromNbt(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
    {
        DefaultedList<ItemStack> list = DefaultedList.ofSize(4, ItemStack.EMPTY);

        if (nbt.contains("ArmorItems", Constants.NBT.TAG_LIST))
        {
            NbtList nbtList = nbt.getList("ArmorItems", Constants.NBT.TAG_COMPOUND);

            for (int i = 0; i < list.size(); i++)
            {
                list.set(i, ItemStack.fromNbtOrEmpty(registry, nbtList.getCompound(i)));
            }
        }

        return list;
    }

    public static ItemStack getBodyArmorFromNbt(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
    {
        if (nbt.contains("body_armor_item", Constants.NBT.TAG_COMPOUND))
        {
            return ItemStack.fromNbtOrEmpty(registry, nbt);
        }

        return ItemStack.EMPTY;
    }

    public static Pair<UUID, ItemStack> getOwnerAndSaddle(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
    {
        UUID owner = Util.NIL_UUID;
        ItemStack saddle = ItemStack.EMPTY;

        if (nbt.containsUuid("Owner"))
        {
            owner = nbt.getUuid("Owner");
        }
        if (nbt.contains("SaddleItem", Constants.NBT.TAG_COMPOUND))
        {
            saddle = ItemStack.fromNbtOrEmpty(registry, nbt.getCompound("SaddleItem"));
        }

        return Pair.of(owner, saddle);
    }

    public static Pair<Integer, Integer> getAgeFromNbt(@Nonnull NbtCompound nbt)
    {
        int breedingAge = 0;
        int forcedAge = 0;

        if (nbt.contains("Age"))
        {
            breedingAge = nbt.getInt("Age");
        }
        if (nbt.contains("ForcedAge"))
        {
            breedingAge = nbt.getInt("ForcedAge");
        }

        return Pair.of(breedingAge, forcedAge);
    }

    public static @Nullable TradeOfferList getTradeOffersFromNbt(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
    {
        if (nbt.contains("Offers"))
        {
            Optional<TradeOfferList> opt = TradeOfferList.CODEC.parse(registry.getOps(NbtOps.INSTANCE), nbt.get("Offers")).resultOrPartial();

            if (opt.isPresent())
            {
                return opt.get();
            }
        }

        return null;
    }

    public static @Nullable VillagerData getVillagerDataFromNbt(@Nonnull NbtCompound nbt)
    {
        if (nbt.contains("VillagerData", Constants.NBT.TAG_COMPOUND))
        {
            Optional<VillagerData> opt = VillagerData.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, nbt.get("VillagerData"))).resultOrPartial();

            if (opt.isPresent())
            {
                return opt.get();
            }
        }

        return null;
    }

    public static Pair<Integer, UUID> getZombieConversionTimerFromNbt(@Nonnull NbtCompound nbt)
    {
        int timer = 0;
        UUID player = Util.NIL_UUID;

        if (nbt.contains("ConversionTime", Constants.NBT.TAG_ANY_NUMERIC))
        {
            timer = nbt.getInt("ConversionTime");
        }
        if (nbt.containsUuid("ConversionPlayer"))
        {
            player = nbt.getUuid("ConversionPlayer");
        }

        return Pair.of(timer, player);
    }

    public static Pair<Integer, Integer> getDrownedConversionTimerFromNbt(@Nonnull NbtCompound nbt)
    {
        int drowning = -1;
        int inWater = -1;

        if (nbt.contains("DrownedConversionTime", Constants.NBT.TAG_ANY_NUMERIC))
        {
            drowning = nbt.getInt("DrownedConversionTime");
        }
        if (nbt.contains("InWaterTime", Constants.NBT.TAG_INT))
        {
            inWater = nbt.getInt("InWaterTime");
        }

        return Pair.of(drowning, inWater);
    }

    public static int getStrayConversionTimeFromNbt(@Nonnull NbtCompound nbt)
    {
        if (nbt.contains("StrayConversionTime", Constants.NBT.TAG_ANY_NUMERIC))
        {
            return nbt.getInt("StrayConversionTime");
        }

        return -1;
    }

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

        if (nbt.contains("leash", Constants.NBT.TAG_COMPOUND))
        {
            data = new FakeLeashData(-1, null, Either.left(nbt.getCompound("leash").getUuid("UUID")));
        }
        else if (nbt.contains("leash", Constants.NBT.TAG_INT_ARRAY))
        {
            Either<UUID, BlockPos> either = (Either) NbtHelper.toBlockPos(nbt, "leash").map(Either::right).orElse(null);

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
}
