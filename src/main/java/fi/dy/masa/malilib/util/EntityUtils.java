package fi.dy.masa.malilib.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.chicken.ChickenVariant;
import net.minecraft.world.entity.animal.chicken.ChickenVariants;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.entity.animal.cow.CowVariant;
import net.minecraft.world.entity.animal.cow.CowVariants;
import net.minecraft.world.entity.animal.cow.MushroomCow;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.entity.animal.equine.Llama;
import net.minecraft.world.entity.animal.equine.Markings;
import net.minecraft.world.entity.animal.equine.Variant;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.feline.CatVariant;
import net.minecraft.world.entity.animal.feline.CatVariants;
import net.minecraft.world.entity.animal.fish.Salmon;
import net.minecraft.world.entity.animal.fish.TropicalFish;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.FrogVariant;
import net.minecraft.world.entity.animal.frog.FrogVariants;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.animal.pig.PigVariant;
import net.minecraft.world.entity.animal.pig.PigVariants;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.animal.wolf.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.EitherHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

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
        Minecraft mc = Minecraft.getInstance();
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
    public static boolean hasTurtleHelmetEquipped(Player player)
    {
        if (player == null)
        {
            return false;
        }

        ItemStack stack = player.getItemBySlot(EquipmentSlot.HEAD);

        return !stack.isEmpty() && stack.is(Items.TURTLE_HELMET);
    }

    /**
     * Get an Axolotl's Variant from Data Components.
     *
     * @param entity ()
     * @return ()
     */
    public static @Nullable Axolotl.Variant getAxolotlVariantFromComponents(@Nonnull Axolotl entity)
    {
        return entity.get(DataComponents.AXOLOTL_VARIANT);
    }

    /**
     * Get a Cat's Variant, and Collar Color from Components.
     *
     * @param entity ()
     * @return ()
     */
    public static Pair<ResourceKey<@NotNull CatVariant>, DyeColor> getCatVariantFromComponents(@Nonnull Cat entity)
    {
        Holder<@NotNull CatVariant> entry = entity.get(DataComponents.CAT_VARIANT);
        DyeColor collar = entity.get((DataComponents.CAT_COLLAR));
        ResourceKey<@NotNull CatVariant> key = entry != null ? entry.unwrapKey().orElse(CatVariants.BLACK) : CatVariants.BLACK;

        return Pair.of(key, collar);
    }

    /**
     * Get a Chicken's Variant type from Components.
     *
     * @param entity ()
     * @return ()
     */
    public static @NotNull ResourceKey<@NotNull ChickenVariant> getChickenVariantFromComponents(@Nonnull Chicken entity)
    {
        EitherHolder<@NotNull ChickenVariant> entry = entity.get(DataComponents.CHICKEN_VARIANT);
        return entry != null ? entry.key().orElse(ChickenVariants.DEFAULT) : ChickenVariants.DEFAULT;
    }

    /**
     * Get a Cow's Variant type from Components.
     *
     * @param entity ()
     * @return ()
     */
    public static @NotNull ResourceKey<@NotNull CowVariant> getCowVariantFromComponents(@Nonnull Cow entity)
    {
        Holder<@NotNull CowVariant> entry = entity.get(DataComponents.COW_VARIANT);
        return entry != null ? entry.unwrapKey().orElse(CowVariants.DEFAULT) : CowVariants.DEFAULT;
    }

    /**
     * Get a Mooshroom Variant type from Components.
     *
     * @param entity ()
     * @return ()
     */
    public static @Nullable MushroomCow.Variant getMooshroomVariantFromComponents(@Nonnull MushroomCow entity)
    {
        return entity.get(DataComponents.MOOSHROOM_VARIANT);
    }

    /**
     * Get a Fox's Variant type from Components.
     *
     * @param entity ()
     * @return ()
     */
    public static @Nullable Fox.Variant getFoxVariantFromComponents(@Nonnull Fox entity)
    {
        return entity.get(DataComponents.FOX_VARIANT);
    }

    /**
     * Get a Frog's Variant from Components.
     *
     * @param entity ()
     * @return ()
     */
    public static ResourceKey<@NotNull FrogVariant> getFrogVariantFromComponents(@Nonnull Frog entity)
    {
        Holder<@NotNull FrogVariant> entry = entity.get(DataComponents.FROG_VARIANT);
        return entry != null ? entry.unwrapKey().orElse(FrogVariants.TEMPERATE) : FrogVariants.TEMPERATE;
    }

    /**
     * Get a Horse's Variant (Color, Markings) from Components.
     *
     * @param entity ()
     * @return ()
     */
    public static Pair<Variant, Markings> getHorseVariantFromComponents(@Nonnull Horse entity)
    {
        Variant color = entity.get(DataComponents.HORSE_VARIANT);

        if (color == null)
        {
            color = Variant.WHITE;
        }

        Markings marking = Markings.byId((color.getId() & '\uff00') >> 8);

        return Pair.of(color, marking);
    }

    /**
     * Get a Parrot's Variant type from Components.
     *
     * @param entity ()
     * @return ()
     */
    public static @Nullable Parrot.Variant getParrotVariantFromComponents(@Nonnull Parrot entity)
    {
        return entity.get(DataComponents.PARROT_VARIANT);
    }

    /**
     * Get a Pig's Variant type from Components.
     *
     * @param entity ()
     * @return ()
     */
    public static ResourceKey<@NotNull PigVariant> getPigVariantFromComponents(@Nonnull Pig entity)
    {
        Holder<@NotNull PigVariant> entry = entity.get(DataComponents.PIG_VARIANT);
        return entry != null ? entry.unwrapKey().orElse(PigVariants.DEFAULT) : PigVariants.DEFAULT;
    }

    /**
     * Get a Rabbit's Variant type from Components.
     *
     * @param entity ()
     * @return ()
     */
    public static @Nullable Rabbit.Variant getRabbitVariantFromComponents(@Nonnull Rabbit entity)
    {
        return entity.get(DataComponents.RABBIT_VARIANT);
    }

    /**
     * Get a Llama's Variant type from Components.
     *
     * @param entity ()
     * @return ()
     */
    public static @Nullable Llama.Variant getLlamaVariantFromComponents(@Nonnull Llama entity)
    {
        return entity.get(DataComponents.LLAMA_VARIANT);
    }

    /**
     * Get a Tropical Fish Variant type from Components.
     *
     * @param entity ()
     * @return ()
     */
    public static @Nullable TropicalFish.Pattern getFishVariantFromComponents(@Nonnull TropicalFish entity)
    {
        return entity.get(DataComponents.TROPICAL_FISH_PATTERN);
    }

    /**
     * Get a Wolves' Variant and Collar Color from NBT.
     *
     * @param entity ()
     * @return ()
     */
    public static Pair<ResourceKey<@NotNull WolfVariant>, DyeColor> getWolfVariantFromComponents(@Nonnull Wolf entity)
    {
        Holder<@NotNull WolfVariant> entry = entity.get(DataComponents.WOLF_VARIANT);
        DyeColor collar = entity.get(DataComponents.WOLF_COLLAR);
        ResourceKey<@NotNull WolfVariant> variantKey = entry != null ? entry.unwrapKey().orElse(WolfVariants.DEFAULT) : WolfVariants.DEFAULT;

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
    public static ResourceKey<@NotNull WolfSoundVariant> getWolfSoundTypeFromComponents(@Nonnull Wolf entity)
    {
        Holder<@NotNull WolfSoundVariant> entry = entity.get(DataComponents.WOLF_SOUND_VARIANT);
        return entry != null ? entry.unwrapKey().orElse(WolfSoundVariants.CLASSIC) : WolfSoundVariants.CLASSIC;
    }

    /**
     * Get a Salmon Variant type from Components.
     *
     * @param entity ()
     * @return ()
     */
    public static @Nullable Salmon.Variant getSalmonVariantFromComponents(@Nonnull Salmon entity)
    {
        return entity.get(DataComponents.SALMON_SIZE);
    }

    /**
     * Get a Sheep Color from Components.
     *
     * @param entity ()
     * @return ()
     */
    public static @Nullable DyeColor getSheepVariantFromComponents(@Nonnull Sheep entity)
    {
        return entity.get(DataComponents.SHEEP_COLOR);
    }
}
