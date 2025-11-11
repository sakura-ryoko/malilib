package fi.dy.masa.malilib.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.LazyRegistryEntryReference;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.DyeColor;

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
     * Returns if the Entity has a Turtle Helmet equipped
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
     * Get an Axolotl's Variant from Data Components.
     *
     * @param entity ()
     * @return ()
     */
    public static @Nullable AxolotlEntity.Variant getAxolotlVariantFromComponents(@Nonnull AxolotlEntity entity)
    {
        return entity.get(DataComponentTypes.AXOLOTL_VARIANT);
    }

    /**
     * Get a Cat's Variant, and Collar Color from Components.
     *
     * @param entity ()
     * @return ()
     */
    public static Pair<RegistryKey<CatVariant>, DyeColor> getCatVariantFromComponents(@Nonnull CatEntity entity)
    {
        RegistryEntry<CatVariant> entry = entity.get(DataComponentTypes.CAT_VARIANT);
        DyeColor collar = entity.get((DataComponentTypes.CAT_COLLAR));
        RegistryKey<CatVariant> key = entry != null ? entry.getKey().orElse(CatVariants.BLACK) : CatVariants.BLACK;

        return Pair.of(key, collar);
    }

    /**
     * Get a Chicken's Variant type from Components.
     *
     * @param entity ()
     * @return ()
     */
    public static @Nullable RegistryKey<ChickenVariant> getChickenVariantFromComponents(@Nonnull ChickenEntity entity)
    {
        LazyRegistryEntryReference<ChickenVariant> entry = entity.get(DataComponentTypes.CHICKEN_VARIANT);
        return entry != null ? entry.getKey().orElse(ChickenVariants.DEFAULT) : ChickenVariants.DEFAULT;
    }

    /**
     * Get a Cow's Variant type from Components.
     *
     * @param entity ()
     * @return ()
     */
    public static @Nullable RegistryKey<CowVariant> getCowVariantFromComponents(@Nonnull CowEntity entity)
    {
        RegistryEntry<CowVariant> entry = entity.get(DataComponentTypes.COW_VARIANT);
        return entry != null ? entry.getKey().orElse(CowVariants.DEFAULT) : CowVariants.DEFAULT;
    }

    /**
     * Get a Mooshroom Variant type from Components.
     *
     * @param entity ()
     * @return ()
     */
    public static @Nullable MooshroomEntity.Variant getMooshroomVariantFromComponents(@Nonnull MooshroomEntity entity)
    {
        return entity.get(DataComponentTypes.MOOSHROOM_VARIANT);
    }

    /**
     * Get a Fox's Variant type from Components.
     *
     * @param entity ()
     * @return ()
     */
    public static @Nullable FoxEntity.Variant getFoxVariantFromComponents(@Nonnull FoxEntity entity)
    {
        return entity.get(DataComponentTypes.FOX_VARIANT);
    }

    /**
     * Get a Frog's Variant from Components.
     *
     * @param entity ()
     * @return ()
     */
    public static RegistryKey<FrogVariant> getFrogVariantFromComponents(@Nonnull FrogEntity entity)
    {
        RegistryEntry<FrogVariant> entry = entity.get(DataComponentTypes.FROG_VARIANT);
        return entry != null ? entry.getKey().orElse(FrogVariants.TEMPERATE) : FrogVariants.TEMPERATE;
    }

    /**
     * Get a Horse's Variant (Color, Markings) from Components.
     *
     * @param entity ()
     * @return ()
     */
    public static Pair<HorseColor, HorseMarking> getHorseVariantFromComponents(@Nonnull HorseEntity entity)
    {
        HorseColor color = entity.get(DataComponentTypes.HORSE_VARIANT);

        if (color == null)
        {
            color = HorseColor.WHITE;
        }

        HorseMarking marking = HorseMarking.byIndex((color.getIndex() & '\uff00') >> 8);

        return Pair.of(color, marking);
    }

    /**
     * Get a Parrot's Variant type from Components.
     *
     * @param entity ()
     * @return ()
     */
    public static @Nullable ParrotEntity.Variant getParrotVariantFromComponents(@Nonnull ParrotEntity entity)
    {
        return entity.get(DataComponentTypes.PARROT_VARIANT);
    }

    /**
     * Get a Pig's Variant type from Components.
     *
     * @param entity ()
     * @return ()
     */
    public static RegistryKey<PigVariant> getPigVariantFromComponents(@Nonnull PigEntity entity)
    {
        RegistryEntry<PigVariant> entry = entity.get(DataComponentTypes.PIG_VARIANT);
        return entry != null ? entry.getKey().orElse(PigVariants.DEFAULT) : PigVariants.DEFAULT;
    }

    /**
     * Get a Rabbit's Variant type from Components.
     *
     * @param entity ()
     * @return ()
     */
    public static @Nullable RabbitEntity.Variant getRabbitVariantFromComponents(@Nonnull RabbitEntity entity)
    {
        return entity.get(DataComponentTypes.RABBIT_VARIANT);
    }

    /**
     * Get a Llama's Variant type from Components.
     *
     * @param entity ()
     * @return ()
     */
    public static @Nullable LlamaEntity.Variant getLlamaVariantFromComponents(@Nonnull LlamaEntity entity)
    {
        return entity.get(DataComponentTypes.LLAMA_VARIANT);
    }

    /**
     * Get a Tropical Fish Variant type from Components.
     *
     * @param entity ()
     * @return ()
     */
    public static @Nullable TropicalFishEntity.Pattern getFishVariantFromComponents(@Nonnull TropicalFishEntity entity)
    {
        return entity.get(DataComponentTypes.TROPICAL_FISH_PATTERN);
    }

    /**
     * Get a Wolves' Variant and Collar Color from NBT.
     *
     * @param entity ()
     * @return ()
     */
    public static Pair<RegistryKey<WolfVariant>, DyeColor> getWolfVariantFromComponents(@Nonnull WolfEntity entity)
    {
        RegistryEntry<WolfVariant> entry = entity.get(DataComponentTypes.WOLF_VARIANT);
        DyeColor collar = entity.get(DataComponentTypes.WOLF_COLLAR);
        RegistryKey<WolfVariant> variantKey = entry != null ? entry.getKey().orElse(WolfVariants.DEFAULT) : WolfVariants.DEFAULT;

        if (collar == null)
        {
            collar = DyeColor.RED;
        }

        return Pair.of(variantKey, collar);
    }

    /**
     * Get a Wolves' Sound Variant and Collar Color from NBT.
     *
     * @param entity ()
     * @return ()
     */
    public static RegistryKey<WolfSoundVariant> getWolfSoundTypeFromComponents(@Nonnull WolfEntity entity)
    {
        RegistryEntry<WolfSoundVariant> entry = entity.get(DataComponentTypes.WOLF_SOUND_VARIANT);
        return entry != null ? entry.getKey().orElse(WolfSoundVariants.CLASSIC) : WolfSoundVariants.CLASSIC;
    }

    /**
     * Get a Salmon Variant type from Components.
     *
     * @param entity ()
     * @return ()
     */
    public static @Nullable SalmonEntity.Variant getSalmonVariantFromComponents(@Nonnull SalmonEntity entity)
    {
        return entity.get(DataComponentTypes.SALMON_SIZE);
    }

    /**
     * Get a Sheep Color from Components.
     *
     * @param entity ()
     * @return ()
     */
    public static @Nullable DyeColor getSheepVariantFromComponents(@Nonnull SheepEntity entity)
    {
        return entity.get(DataComponentTypes.SHEEP_COLOR);
    }
}
