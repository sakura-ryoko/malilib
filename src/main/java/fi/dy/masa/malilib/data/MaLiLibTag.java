package fi.dy.masa.malilib.data;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibReference;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

/**
 * Replaced in future builds by the {@link CachedTagManager} system
 */
@Deprecated(forRemoval = true)
public class MaLiLibTag
{
    public static class Blocks
    {
        public static final TagKey<Block> ALL_SIGNS_FIX = createBlockTag("all_signs_fix");
        public static final TagKey<Block> ALL_BANNERS_FIX = createBlockTag("all_banners_fix");
        public static final TagKey<Block> CONCRETE_POWDER_FIX = createBlockTag("concrete_powder_fix");
        public static final TagKey<Block> CORAL_FANS_FIX = createBlockTag("coral_fans_fix");
        public static final TagKey<Block> LEAVES_FIX = createBlockTag("leaves_fix");
        public static final TagKey<Block> WOOL_BLOCKS_FIX = createBlockTag("wool_blocks_fix");

        public static final TagKey<Block> CONCRETE_BLOCKS = createBlockTag("concrete_blocks");
        public static final TagKey<Block> GLASS_BLOCKS = createBlockTag("glass_blocks");            // BlockTags.IMPERMEABLE (?)
        public static final TagKey<Block> GLASS_PANES = createBlockTag("glass_panes");
        public static final TagKey<Block> GLAZED_TERRACOTTA_BLOCKS = createBlockTag("glazed_terracotta_blocks");
        public static final TagKey<Block> SCULK_BLOCKS = createBlockTag("sculk_blocks");
        public static final TagKey<Block> ORE_BLOCKS = createBlockTag("ore_blocks");

        public static final TagKey<Block> GRAVITY_BLOCKS = createBlockTag("gravity_blocks");
        public static final TagKey<Block> IMMOVABLE_BLOCKS = createBlockTag("immovable_blocks");
        public static final TagKey<Block> NEEDS_SILK_TOUCH = createBlockTag("needs_silk_touch");
        public static final TagKey<Block> NEEDS_SHEARS = createBlockTag("needs_shears");

        public static final ImmutableList<TagKey<Block>> REPLACEABLE_GROUPS = ImmutableList.of(
                BlockTags.ANVIL,
                BlockTags.BEDS,
                BlockTags.BUTTONS,
                BlockTags.CANDLE_CAKES,
                BlockTags.CANDLES,
                BlockTags.CEILING_HANGING_SIGNS,
                BlockTags.CONCRETE_POWDER,
                BlockTags.CORAL_PLANTS,
                BlockTags.DOORS,
                BlockTags.FENCE_GATES,
                BlockTags.FENCES,
                BlockTags.FLOWER_POTS,
                BlockTags.FLOWERS,
                BlockTags.LEAVES,
                BlockTags.LOGS,
                BlockTags.PLANKS,
                BlockTags.PRESSURE_PLATES,
                BlockTags.SAPLINGS,
                BlockTags.SHULKER_BOXES,
                BlockTags.SLABS,
                BlockTags.STAIRS,
                BlockTags.STANDING_SIGNS,
                BlockTags.TERRACOTTA,
                BlockTags.TRAPDOORS,
                BlockTags.WALL_HANGING_SIGNS,
                BlockTags.WALL_SIGNS,
                BlockTags.WALLS,
                BlockTags.WOOL,
                BlockTags.WOOL_CARPETS,
				BlockTags.IMPERMEABLE
//				,
//
//                CONCRETE_BLOCKS,
//                CORAL_FANS_FIX,
//                GLASS_BLOCKS,
//                GLASS_PANES,
//                GLAZED_TERRACOTTA_BLOCKS
        );

        private static TagKey<Block> createBlockTag(String name)
        {
            return TagKey.of(RegistryKeys.BLOCK, buildIdentifier(name));
        }
    }

    public static class Items
    {
        private static TagKey<Item> createItemTag(String name)
        {
            return TagKey.of(RegistryKeys.ITEM, buildIdentifier(name));
        }
    }

    private static Identifier buildIdentifier(String name)
    {
        return Identifier.of(MaLiLibReference.MOD_ID, name);
    }

    public static void register()
    {
        MaLiLib.debugLog("MaLiLibTag: init()");
    }
}
