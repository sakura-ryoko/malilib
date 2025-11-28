package fi.dy.masa.malilib.util.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.display.*;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.mixin.recipe.IMixinClientRecipeBook;
import fi.dy.masa.malilib.mixin.recipe.IMixinIngredient;
import fi.dy.masa.malilib.util.log.AnsiLogger;

/**
 * Transferred from ItemScroller & Expanded functionality
 */
public class RecipeBookUtils
{
    private static final AnsiLogger LOGGER = new AnsiLogger(RecipeBookUtils.class, MaLiLibReference.DEBUG_MODE, true);
    public static ContextMap map;
    private static final int refreshTime = 300;
    private static long lastRefresh = -1L;
    // Cache a ContextParameterMap, because Minecraft hates it when you spam this function;
    // so here we are creating a timer with how often that we could refresh it (in seconds).

    /**
     * Enables Debug mode.
     * @param toggle (Enable|Disable)
     */
    public static void toggleDebugLog(boolean toggle)
    {
        LOGGER.toggleDebug(toggle);
    }

    /**
     * Enables Debug Ansi Colors.
     * @param toggle (Enable|Disable)
     */
    public static void toggleAnsiColorLog(boolean toggle)
    {
        LOGGER.toggleAnsiColor(toggle);
    }

    /**
     * Get RecipeBookCategory as a string
     * @param category (RecipeBookCategory)
     * @return (Identifier as a String)
     */
    public static String getRecipeCategoryId(RecipeBookCategory category)
    {
        ResourceKey<@NotNull RecipeBookCategory> key = BuiltInRegistries.RECIPE_BOOK_CATEGORY.getResourceKey(category).orElse(null);

        if (key != null)
        {
            return key.identifier().toString();
        }

        return "";
    }

    /**
     * Get RecipeBookCategory from a string or return Null
     * @param id (Identifier as a string)
     * @return (RecipeBookCategory|Null)
     */
    public static @Nullable RecipeBookCategory getRecipeCategoryFromId(String id)
    {
		try
		{
			Holder.Reference<@NotNull RecipeBookCategory> catReference = BuiltInRegistries.RECIPE_BOOK_CATEGORY.get(Objects.requireNonNull(Identifier.tryParse(id))).orElse(null);

			if (catReference != null && catReference.isBound())
			{
				return catReference.value();
			}
		}
		catch (Exception ignored) { }

        return null;
    }

    /**
     * Get a cached Parameter Map; because Minecraft hates it when you call this constantly.
     * @param mc ()
     * @return ()
     */
    public static @Nullable ContextMap getMap(Minecraft mc)
    {
        if (mc.level == null) return null;

        if (map == null || (System.currentTimeMillis() - lastRefresh) > (refreshTime * 1000L))
        {
            map = SlotDisplayContext.fromLevel(mc.level);
            lastRefresh = System.currentTimeMillis();
        }

        return map;
    }

    /**
     * Clear it upon exiting a world.
     */
    public static void clearMap()
    {
        map = null;
        lastRefresh = -1L;
    }

    /**
     * Get all matching RecipeBook Display Entries for a Crafting Result, and filter by Recipe Types.
     * @param result (Crafting Result Stack)
     * @param types (Recipe Type list)
     * @return (List of all matching recipe's and their corresponding NetworkRecipeId)
     */
    public static List<Pair<RecipeDisplayId, RecipeDisplayEntry>> getDisplayEntryFromRecipeBook(ItemStack result, List<Type> types)
    {
        Minecraft mc = Minecraft.getInstance();

        if (mc.level == null || mc.player == null)
        {
            return null;
        }

        ClientRecipeBook recipeBook = mc.player.getRecipeBook();
        Map<RecipeDisplayId, RecipeDisplayEntry> recipeMap = ((IMixinClientRecipeBook) recipeBook).malilib_getRecipeMap();
        List<Pair<RecipeDisplayId, RecipeDisplayEntry>> list = new ArrayList<>();
        FeatureFlagSet features = mc.level.enabledFeatures();
        ContextMap map = getMap(mc);

        if (map == null) return null;
//        LOGGER.debug("getDisplayEntryFromRecipeBook(): Checking [{}] recipes", recipeMap.size());

        for (RecipeDisplayId id : recipeMap.keySet())
        {
            RecipeDisplayEntry entry = recipeMap.get(id);
            Type type = Type.fromRecipeDisplay(entry.display());

            // The SmithingTrimSlotDisplay causes crashes here; for some reason.
            if (entry.craftingRequirements().isPresent() &&
                types.contains(type) &&
                entry.display().isEnabled(features) &&
                !(entry.display().result() instanceof SlotDisplay.SmithingTrimDemoSlotDisplay))
            {
                ItemStack resultSlot = entry.display().result().resolveForFirstStack(map);

                if (resultSlot.isEmpty())
                {
                    continue;
                }

                if (ItemStack.isSameItem(result, resultSlot))
                {
//                    LOGGER.debug("ID[{}]: type: [{}], resultStack: [{}] --> MATCHED", id.index(), type.name(), resultSlot.getRegistryEntry().getIdAsString());
                    list.add(Pair.of(id, entry));

                    // Don't return more than 3 results.
                    if (list.size() > 2)
                    {
                        return list;
                    }
                }
            }
        }

//        LOGGER.debug("getDisplayEntryFromRecipeBook(): Matched [{}] recipes", list.size());
        return list;
    }

    /**
     * Match the provided RecipeBookEntry to the crafting result, and input stacks, and filter by Recipe Type.
     * @param result (Crafting Result Stack)
     * @param recipeStacks (Crafting Input Stacks (Shaped requires Empty slots in this))
     * @param entry (RecipeDisplayEntry to match)
     * @param allowed (List of allowed Recipe Types.)
     * @param mc ()
     * @return (True|False)
     */
    public static boolean matchClientRecipeBookEntry(ItemStack result, List<ItemStack> recipeStacks, RecipeDisplayEntry entry, List<Type> allowed, Minecraft mc)
    {
        if (mc.level == null || result.isEmpty())
        {
            return false;
        }

        // Mojang breaks their own player recipe book.  Verifying the Category here can cause problems.
        /*
        if (this.getRecipeCategory() != null && !entry.category().equals(this.getRecipeCategory()))
        {
            return false;
        }
         */
        ContextMap map = getMap(mc);
        if (map == null) return false;
        List<ItemStack> stacks = entry.resultItems(map);

        LOGGER.debug("matchClientRecipeBookEntry() --> [{}] vs [{}]", recipeStacks, stacks.getFirst().toString());

        if (stacks.isEmpty())
        {
            // And why would that be? *cries without essential data*
            MaLiLib.LOGGER.warn("matchClientRecipeBookEntry(): Failed receiving crafting stacks for NetworkRecipeId: [{}] -- is it even a valid recipe?", entry.id().index());
            return false;
        }

        if (areStacksEqual(result, stacks.getFirst()))
        {
            if (entry.craftingRequirements().isPresent())
            {
                return compareStacksAndIngredients(recipeStacks,
                                                   entry.craftingRequirements().get(),
                                                   Type.fromRecipeDisplay(entry.display()),
                                                   allowed
                );
            }

            return true;
        }

        return false;
    }

    /**
     * Match a list of Crafting stacks to a list of Crafting Ingredients, filtered by a list of Recipe Types.
     * @param left (Crafting Input Stacks)
     * @param right (Crafting Recipe Ingredients (Each Ingredient contains a list of possible inputs))
     * @param type (RecipeDisplayEntry type)
     * @param allowed (List of allowed recipe types)
     * @return (True|False)
     */
    public static boolean compareStacksAndIngredients(List<ItemStack> left, List<Ingredient> right, Type type, List<Type> allowed)
    {
        if (left.isEmpty() || right.isEmpty())
        {
            LOGGER.debug("compareStacksAndIngredients() --> EMPTY!!!");
            return false;
        }

        LOGGER.debug("compareStacksAndIngredients() Type: [{}] --> START", type.toString());
        if (LOGGER.isDebug())
        {
            dumpStacks(left, "LF");
            dumpIngs(right, "RT");
        }

        if (type == Type.SHAPELESS && allowed.contains(type))
        {
            return compareShapelessRecipe(left, right);
        }
        else if (type == Type.SHAPED && allowed.contains(type))
        {
            return compareShapedRecipe(left, right);
        }
        else if (type == Type.STONECUTTER && allowed.contains(type))
        {
            // TODO --> Check functionality
            return compareStonecutterRecipe(left, right);
        }
        else if (type == Type.FURNACE && allowed.contains(type))
        {
            // TODO --> Add Fuel types
            return compareFurnaceRecipe(left, right);
        }
        else if (type == Type.SMITHING && allowed.contains(type))
        {
            // TODO --> Add template, base, addition types
            return compareSmithingRecipe(left, right);
        }

        // Other recipe type
        return false;
    }

    /**
     * Compare a Shaped Recipe Input to a List of Ingredients
     * @param left (Crafting Input Stacks)
     * @param right (Crafting Recipe Ingredients (Each Ingredient contains a list of possible inputs))
     * @return (True|False)
     */
    public static boolean compareShapedRecipe(List<ItemStack> left, List<Ingredient> right)
    {
        LOGGER.debug("compareShapedRecipe() --> size left [{}], right [{}]\n", left.size(), right.size());
        int lPos = 0;

        for (int i = 0; i < right.size(); i++)
        {
            ItemStack lStack = left.get(lPos);

            while (lStack.isEmpty())
            {
                lPos++;

                if (lPos < 9)
                {
                    lStack = left.get(lPos);
                    LOGGER.debug(" compareShapedRecipe() [{}] left [{}] (Advance Left), right [{}]", lPos, lStack.toString(), i);
                }
                else
                {
                    break;
                }
            }

            if (!checkMatchingItemsEach(lStack, lPos, i, right.get(i)))
            {
                LOGGER.debug(" FAIL (Shaped)");
                return false;
            }

            lPos++;
        }

        LOGGER.debug(" PASS (Shaped)");
        return true;
    }

    /**
     * Compare a Shapeless Recipe Input to a List of Ingredients
     * @param left (Crafting Input Stacks)
     * @param right (Crafting Recipe Ingredients (Each Ingredient contains a list of possible inputs))
     * @return (True|False)
     */
    public static boolean compareShapelessRecipe(List<ItemStack> left, List<Ingredient> right)
    {
        LOGGER.debug("compareShapelessRecipe() --> size left [{}], right [{}]", left.size(), right.size());

        for (int i = 0; i < left.size(); i++)
        {
            ItemStack lStack = left.get(i);
            boolean pass = false;

            LOGGER.debug(" compareShapelessRecipe() [{}] left [{}] -->", i, lStack.toString());
            if (lStack.isEmpty()) continue;

            for (int rPos = 0; rPos < right.size(); rPos++)
            {
                if (checkMatchingItemsEach(lStack, i, rPos, right.get(rPos)))
                {
                    LOGGER.debug(" PASS-EACH");
                    pass = true;
                }
            }

            if (!pass)
            {
                LOGGER.debug(" FAIL (Shapeless)");
                return false;
            }
        }

        LOGGER.debug(" PASS (Shapeless)");
        return true;
    }

    /**
     * Compare a Stonecutter Recipe Input to a List of Ingredients
     * @param left (Crafting Input Stacks)
     * @param right (Crafting Recipe Ingredients (Each Ingredient contains a list of possible inputs))
     * @return (True|False)
     */
    @ApiStatus.Experimental
    public static boolean compareStonecutterRecipe(List<ItemStack> left, List<Ingredient> right)
    {
        LOGGER.debug("compareStonecutterRecipe() --> size left [{}], right [{}]", left.size(), right.size());

        for (int i = 0; i < left.size(); i++)
        {
            ItemStack lStack = left.get(i);
            boolean pass = false;

            LOGGER.debug(" compareStonecutterRecipe() [{}] left [{}] -->", i, lStack.toString());
            if (lStack.isEmpty()) continue;

            for (int rPos = 0; rPos < right.size(); rPos++)
            {
                if (checkMatchingItemsEach(lStack, i, rPos, right.get(rPos)))
                {
                    LOGGER.debug(" PASS-EACH");
                    pass = true;
                }
            }

            if (!pass)
            {
                LOGGER.debug(" FAIL (Stonecutter)");
                return false;
            }
        }

        LOGGER.debug(" PASS (Stonecutter)");
        return true;
    }

    /**
     * Compare a Furnace Recipe Input to a List of Ingredients
     * @param left (Crafting Input Stacks)
     * @param right (Crafting Recipe Ingredients (Each Ingredient contains a list of possible inputs))
     * @return (True|False)
     */
    @ApiStatus.Experimental
    public static boolean compareFurnaceRecipe(List<ItemStack> left, List<Ingredient> right)
    {
        LOGGER.debug("compareFurnaceRecipe() --> size left [{}], right [{}]", left.size(), right.size());

        for (int i = 0; i < left.size(); i++)
        {
            ItemStack lStack = left.get(i);
            boolean pass = false;

            LOGGER.debug(" compareFurnaceRecipe() [{}] left [{}] -->", i, lStack.toString());
            if (lStack.isEmpty()) continue;

            for (int rPos = 0; rPos < right.size(); rPos++)
            {
                if (checkMatchingItemsEach(lStack, i, rPos, right.get(rPos)))
                {
                    LOGGER.debug(" PASS-EACH");
                    pass = true;
                }
            }

            if (!pass)
            {
                LOGGER.debug(" FAIL (Furnace)");
                return false;
            }
        }

        LOGGER.debug(" PASS (Furnace)");
        return true;
    }

    /**
     * Compare a Smithing Recipe Input to a List of Ingredients
     * @param left (Crafting Input Stacks)
     * @param right (Crafting Recipe Ingredients (Each Ingredient contains a list of possible inputs))
     * @return (True|False)
     */

    @ApiStatus.Experimental
    public static boolean compareSmithingRecipe(List<ItemStack> left, List<Ingredient> right)
    {
        LOGGER.debug("compareSmithingRecipe() --> size left [{}], right [{}]", left.size(), right.size());

        for (int i = 0; i < left.size(); i++)
        {
            ItemStack lStack = left.get(i);
            boolean pass = false;

            LOGGER.debug(" compareSmithingRecipe() [{}] left [{}] -->", i, lStack.toString());
            if (lStack.isEmpty()) continue;

            for (int rPos = 0; rPos < right.size(); rPos++)
            {
                if (checkMatchingItemsEach(lStack, i, rPos, right.get(rPos)))
                {
                    LOGGER.debug(" PASS-EACH");
                    pass = true;
                }
            }

            if (!pass)
            {
                LOGGER.debug(" FAIL (Smithing)");
                return false;
            }
        }

        LOGGER.debug(" PASS (Smithing)");
        return true;
    }

    private static boolean checkMatchingItemsEach(ItemStack lStack, int lPos, int i, Ingredient ri)
    {
        List<Holder<@NotNull Item>> rItems = ((IMixinIngredient) (Object) ri).malilib_getEntries().stream().toList();

        for (Holder<@NotNull Item> rItem : rItems)
        {
            LOGGER.debug(" checkMatchingItemsEach() [{}] left [{}] / [{}] right [{}] -->", lPos, lStack, i, rItem.getRegisteredName());

            if (ri.test(lStack))
            {
                LOGGER.debug(" valid (Test test)");
                return true;
            }
            else if (areStacksEqual(lStack, new ItemStack(rItem)))
            {
                LOGGER.debug(" valid (Stack test)");
                return true;
            }
        }

        LOGGER.debug(" !not valid (Default)");
        return false;
    }

    /**
     * Compare two item stacks, and return if they are equal.
     * This method ignores Components, but also considers stack sizes.
     * @param left (Left Side)
     * @param right (Right Side)
     * @return (True|False)
     */
    public static boolean areStacksEqual(ItemStack left, ItemStack right)
    {
        return ItemStack.isSameItem(left, right) && left.getCount() == right.getCount();
    }

    private static void dumpStacks(List<ItemStack> stacks, String side)
    {
        int i = 0;

        LOGGER.info("DUMP [{}] -->", side);

        for (ItemStack stack : stacks)
        {
            LOGGER.info(" {}[{}] // [{}]", side, i, stack.toString());
            i++;
        }

        LOGGER.info("DUMP END [{}]\n", side);
    }

    private static void dumpIngs(List<Ingredient> ings, String side)
    {
        int i = 0;

        LOGGER.info("DUMP [{}] -->", side);

        for (Ingredient ing : ings)
        {
            List<Holder<Item>> items = ((IMixinIngredient) (Object) ing).malilib_getEntries().stream().toList();
            List<String> list = new ArrayList<>();

            for (Holder<Item> item : items)
            {
                list.add(item.getRegisteredName());
            }

            LOGGER.info(" {}[{}] // {}", i, side, list.toString());
            i++;
        }

        LOGGER.info("DUMP END [{}]", side);
    }

    /**
     * Crafting Recipe Types -- This provides an easier way to filter and organize Recipe Book Display
     * results by Crafting Type; without the complexity of the Vanilla methods for doing this.
     */
    public enum Type implements StringRepresentable
    {
        FURNACE,
        SHAPED,
        SHAPELESS,
        SMITHING,
        STONECUTTER,
        UNKNOWN;

        public static final StringRepresentable.EnumCodec<@NotNull Type> CODEC = StringRepresentable.fromEnum(Type::values);
        public static final StreamCodec<@NotNull ByteBuf, @NotNull Type> PACKET_CODEC = ByteBufCodecs.STRING_UTF8.map(Type::fromStringStatic, Type::getSerializedName);
        public static final ImmutableList<@NotNull Type> VALUES = ImmutableList.copyOf(values());

        public static Type fromRecipeDisplay(RecipeDisplay type)
        {
            return switch (type)
            {
                case FurnaceRecipeDisplay ignored -> FURNACE;
                case ShapelessCraftingRecipeDisplay ignored -> SHAPELESS;
                case ShapedCraftingRecipeDisplay ignored -> SHAPED;
                case SmithingRecipeDisplay ignored -> SMITHING;
                case StonecutterRecipeDisplay ignored -> STONECUTTER;
                case null, default -> UNKNOWN;
            };
        }

        public static @Nullable Type fromStringStatic(String input)
        {
            for (Type type : values())
            {
                if (type.name().equalsIgnoreCase(input))
                {
                    return type;
                }
            }

            return null;
        }

        @Override
        public @Nonnull String getSerializedName()
        {
            return this.name().toLowerCase();
        }
    }
}
