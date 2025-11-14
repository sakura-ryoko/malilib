package fi.dy.masa.malilib.data;

import fi.dy.masa.malilib.MaLiLibReference;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;

/**
 * Caches Block/Item Tags as if they are real Vanilla Block/Item tags.
 */
public class CachedTagManager
{
    public static final CachedTagKey CORAL_FANS_KEY               = new CachedTagKey(MaLiLibReference.MOD_ID, "coral_fans_fix");
    public static final CachedTagKey CONCRETE_BLOCKS_KEY          = new CachedTagKey(MaLiLibReference.MOD_ID, "concrete_blocks");
    public static final CachedTagKey GLASS_PANES_KEY              = new CachedTagKey(MaLiLibReference.MOD_ID, "glass_panes");
    public static final CachedTagKey GLAZED_TERRACOTTA_BLOCKS_KEY = new CachedTagKey(MaLiLibReference.MOD_ID, "glazed_terracotta_blocks");
    public static final CachedTagKey SCULK_BLOCKS_KEY             = new CachedTagKey(MaLiLibReference.MOD_ID, "sculk_blocks");
    public static final CachedTagKey REPLACEABLE_BLOCKS_KEY       = new CachedTagKey(MaLiLibReference.MOD_ID, "replaceable_blocks");
//    public static final CachedTagKey ORE_BLOCKS_KEY               = new CachedTagKey(MaLiLibReference.MOD_ID, "ore_blocks");

    public static List<CachedTagKey> getKeys()
    {
        List<CachedTagKey> list = new ArrayList<>();

        list.add(CORAL_FANS_KEY);
        list.add(CONCRETE_BLOCKS_KEY);
        list.add(GLASS_PANES_KEY);
        list.add(GLAZED_TERRACOTTA_BLOCKS_KEY);
        list.add(SCULK_BLOCKS_KEY);
        list.add(REPLACEABLE_BLOCKS_KEY);
//        list.add(ORE_BLOCKS_KEY);

        return list;
    }

    public static void startCache()
	{
        clearCache();

        CachedBlockTags.getInstance().build(CORAL_FANS_KEY, buildAllCoralFansCache());
        CachedBlockTags.getInstance().build(CONCRETE_BLOCKS_KEY, buildConcreteCache());
        CachedBlockTags.getInstance().build(GLASS_PANES_KEY, buildGlassPanesCache());
        CachedBlockTags.getInstance().build(GLAZED_TERRACOTTA_BLOCKS_KEY, buildGlazedTerracottaCache());
        CachedBlockTags.getInstance().build(SCULK_BLOCKS_KEY, buildSculkCache());
        CachedBlockTags.getInstance().build(REPLACEABLE_BLOCKS_KEY, buildReplaceableCache());
//        CachedBlockTags.getInstance().build(ORE_BLOCKS_KEY, buildOreCache());
	}

    private static void clearCache()
	{
        CachedBlockTags.getInstance().clearEntry(CORAL_FANS_KEY);
        CachedBlockTags.getInstance().clearEntry(CONCRETE_BLOCKS_KEY);
        CachedBlockTags.getInstance().clearEntry(GLASS_PANES_KEY);
        CachedBlockTags.getInstance().clearEntry(GLAZED_TERRACOTTA_BLOCKS_KEY);
        CachedBlockTags.getInstance().clearEntry(SCULK_BLOCKS_KEY);
        CachedBlockTags.getInstance().clearEntry(REPLACEABLE_BLOCKS_KEY);
//        CachedBlockTags.getInstance().clearEntry(ORE_BLOCKS_KEY);
	}

    private static List<String> buildAllCoralFansCache()
    {
        List<String> list = new ArrayList<>();

        list.add("#" + BlockTags.WALL_CORALS.id().toString());
        list.add(Registries.BLOCK.getId(Blocks.BRAIN_CORAL_FAN).toString());
        list.add(Registries.BLOCK.getId(Blocks.FIRE_CORAL_FAN).toString());
        list.add(Registries.BLOCK.getId(Blocks.BUBBLE_CORAL_FAN).toString());
        list.add(Registries.BLOCK.getId(Blocks.TUBE_CORAL_FAN).toString());
        list.add(Registries.BLOCK.getId(Blocks.HORN_CORAL_FAN).toString());

        list.add(Registries.BLOCK.getId(Blocks.DEAD_BRAIN_CORAL_FAN).toString());
        list.add(Registries.BLOCK.getId(Blocks.DEAD_FIRE_CORAL_FAN).toString());
        list.add(Registries.BLOCK.getId(Blocks.DEAD_BUBBLE_CORAL_FAN).toString());
        list.add(Registries.BLOCK.getId(Blocks.DEAD_TUBE_CORAL_FAN).toString());
        list.add(Registries.BLOCK.getId(Blocks.DEAD_HORN_CORAL_FAN).toString());

        list.add(Registries.BLOCK.getId(Blocks.DEAD_BRAIN_CORAL_WALL_FAN).toString());
        list.add(Registries.BLOCK.getId(Blocks.DEAD_FIRE_CORAL_WALL_FAN).toString());
        list.add(Registries.BLOCK.getId(Blocks.DEAD_BUBBLE_CORAL_WALL_FAN).toString());
        list.add(Registries.BLOCK.getId(Blocks.DEAD_TUBE_CORAL_WALL_FAN).toString());
        list.add(Registries.BLOCK.getId(Blocks.DEAD_HORN_CORAL_WALL_FAN).toString());

        return list;
    }

    private static List<String> buildConcreteCache()
    {
        List<String> list = new ArrayList<>();

        list.add(Registries.BLOCK.getId(Blocks.BLACK_CONCRETE).toString());
        list.add(Registries.BLOCK.getId(Blocks.BLUE_CONCRETE).toString());
        list.add(Registries.BLOCK.getId(Blocks.BROWN_CONCRETE).toString());
        list.add(Registries.BLOCK.getId(Blocks.CYAN_CONCRETE).toString());
        list.add(Registries.BLOCK.getId(Blocks.GRAY_CONCRETE).toString());
        list.add(Registries.BLOCK.getId(Blocks.GREEN_CONCRETE).toString());
        list.add(Registries.BLOCK.getId(Blocks.LIGHT_BLUE_CONCRETE).toString());
        list.add(Registries.BLOCK.getId(Blocks.LIGHT_GRAY_CONCRETE).toString());
        list.add(Registries.BLOCK.getId(Blocks.LIME_CONCRETE).toString());
        list.add(Registries.BLOCK.getId(Blocks.MAGENTA_CONCRETE).toString());
        list.add(Registries.BLOCK.getId(Blocks.ORANGE_CONCRETE).toString());
        list.add(Registries.BLOCK.getId(Blocks.PINK_CONCRETE).toString());
        list.add(Registries.BLOCK.getId(Blocks.PURPLE_CONCRETE).toString());
        list.add(Registries.BLOCK.getId(Blocks.RED_CONCRETE).toString());
        list.add(Registries.BLOCK.getId(Blocks.YELLOW_CONCRETE).toString());
        list.add(Registries.BLOCK.getId(Blocks.WHITE_CONCRETE).toString());

        return list;
    }

    private static List<String> buildGlassPanesCache()
    {
        List<String> list = new ArrayList<>();

        list.add(Registries.BLOCK.getId(Blocks.GLASS_PANE).toString());
        list.add(Registries.BLOCK.getId(Blocks.BLACK_STAINED_GLASS_PANE).toString());
        list.add(Registries.BLOCK.getId(Blocks.BLUE_STAINED_GLASS_PANE).toString());
        list.add(Registries.BLOCK.getId(Blocks.BROWN_STAINED_GLASS_PANE).toString());
        list.add(Registries.BLOCK.getId(Blocks.CYAN_STAINED_GLASS_PANE).toString());
        list.add(Registries.BLOCK.getId(Blocks.GRAY_STAINED_GLASS_PANE).toString());
        list.add(Registries.BLOCK.getId(Blocks.GREEN_STAINED_GLASS_PANE).toString());
        list.add(Registries.BLOCK.getId(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE).toString());
        list.add(Registries.BLOCK.getId(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE).toString());
        list.add(Registries.BLOCK.getId(Blocks.LIME_STAINED_GLASS_PANE).toString());
        list.add(Registries.BLOCK.getId(Blocks.MAGENTA_STAINED_GLASS_PANE).toString());
        list.add(Registries.BLOCK.getId(Blocks.ORANGE_STAINED_GLASS_PANE).toString());
        list.add(Registries.BLOCK.getId(Blocks.PINK_STAINED_GLASS_PANE).toString());
        list.add(Registries.BLOCK.getId(Blocks.PURPLE_STAINED_GLASS_PANE).toString());
        list.add(Registries.BLOCK.getId(Blocks.RED_STAINED_GLASS_PANE).toString());
        list.add(Registries.BLOCK.getId(Blocks.YELLOW_STAINED_GLASS_PANE).toString());
        list.add(Registries.BLOCK.getId(Blocks.WHITE_STAINED_GLASS_PANE).toString());

        return list;
    }

    private static List<String> buildGlazedTerracottaCache()
    {
        List<String> list = new ArrayList<>();

        list.add(Registries.BLOCK.getId(Blocks.BLACK_GLAZED_TERRACOTTA).toString());
        list.add(Registries.BLOCK.getId(Blocks.BLUE_GLAZED_TERRACOTTA).toString());
        list.add(Registries.BLOCK.getId(Blocks.BROWN_GLAZED_TERRACOTTA).toString());
        list.add(Registries.BLOCK.getId(Blocks.CYAN_GLAZED_TERRACOTTA).toString());
        list.add(Registries.BLOCK.getId(Blocks.GRAY_GLAZED_TERRACOTTA).toString());
        list.add(Registries.BLOCK.getId(Blocks.GREEN_GLAZED_TERRACOTTA).toString());
        list.add(Registries.BLOCK.getId(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA).toString());
        list.add(Registries.BLOCK.getId(Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA).toString());
        list.add(Registries.BLOCK.getId(Blocks.LIME_GLAZED_TERRACOTTA).toString());
        list.add(Registries.BLOCK.getId(Blocks.MAGENTA_GLAZED_TERRACOTTA).toString());
        list.add(Registries.BLOCK.getId(Blocks.ORANGE_GLAZED_TERRACOTTA).toString());
        list.add(Registries.BLOCK.getId(Blocks.PINK_GLAZED_TERRACOTTA).toString());
        list.add(Registries.BLOCK.getId(Blocks.PURPLE_GLAZED_TERRACOTTA).toString());
        list.add(Registries.BLOCK.getId(Blocks.RED_GLAZED_TERRACOTTA).toString());
        list.add(Registries.BLOCK.getId(Blocks.YELLOW_GLAZED_TERRACOTTA).toString());
        list.add(Registries.BLOCK.getId(Blocks.WHITE_GLAZED_TERRACOTTA).toString());

        return list;
    }

    private static List<String> buildSculkCache()
    {
        List<String> list = new ArrayList<>();

        list.add(Registries.BLOCK.getId(Blocks.CALIBRATED_SCULK_SENSOR).toString());
        list.add(Registries.BLOCK.getId(Blocks.SCULK).toString());
        list.add(Registries.BLOCK.getId(Blocks.SCULK_CATALYST).toString());
        list.add(Registries.BLOCK.getId(Blocks.SCULK_SENSOR).toString());
        list.add(Registries.BLOCK.getId(Blocks.SCULK_SHRIEKER).toString());
        list.add(Registries.BLOCK.getId(Blocks.SCULK_VEIN).toString());

        return list;
    }

    private static List<String> buildReplaceableCache()
    {
        List<String> list = new ArrayList<>();

        list.add("#"+BlockTags.ANVIL.id().toString());
        list.add("#"+BlockTags.BEDS.id().toString());
        list.add("#"+BlockTags.BUTTONS.id().toString());
        list.add("#"+BlockTags.CANDLE_CAKES.id().toString());
        list.add("#"+BlockTags.CANDLES.id().toString());
        list.add("#"+BlockTags.CEILING_HANGING_SIGNS.id().toString());
        list.add("#"+BlockTags.CONCRETE_POWDER.id().toString());
        list.add("#"+BlockTags.CORAL_PLANTS.id().toString());
        list.add("#"+BlockTags.DOORS.id().toString());
        list.add("#"+BlockTags.FENCE_GATES.id().toString());
        list.add("#"+BlockTags.FENCES.id().toString());
        list.add("#"+BlockTags.FLOWER_POTS.id().toString());
        list.add("#"+BlockTags.FLOWERS.id().toString());
        list.add("#"+BlockTags.LEAVES.id().toString());
        list.add("#"+BlockTags.LOGS.id().toString());
        list.add("#"+BlockTags.PLANKS.id().toString());
        list.add("#"+BlockTags.PRESSURE_PLATES.id().toString());
        list.add("#"+BlockTags.SAPLINGS.id().toString());
        list.add("#"+BlockTags.SHULKER_BOXES.id().toString());
        list.add("#"+BlockTags.SLABS.id().toString());
        list.add("#"+BlockTags.STAIRS.id().toString());
        list.add("#"+BlockTags.STANDING_SIGNS.id().toString());
        list.add("#"+BlockTags.TERRACOTTA.id().toString());
        list.add("#"+BlockTags.TRAPDOORS.id().toString());
        list.add("#"+BlockTags.WALL_HANGING_SIGNS.id().toString());
        list.add("#"+BlockTags.WALL_SIGNS.id().toString());
        list.add("#"+BlockTags.WALLS.id().toString());
        list.add("#"+BlockTags.WOOL.id().toString());
        list.add("#"+BlockTags.WOOL_CARPETS.id().toString());
        list.add("#"+BlockTags.IMPERMEABLE.id().toString());

        return list;
    }

    private static List<String> buildOreCache()
    {
        List<String> list = new ArrayList<>();

        list.add(Registries.BLOCK.getId(Blocks.COAL_ORE).toString());
        list.add(Registries.BLOCK.getId(Blocks.COPPER_ORE).toString());
        list.add(Registries.BLOCK.getId(Blocks.DEEPSLATE_COAL_ORE).toString());
        list.add(Registries.BLOCK.getId(Blocks.DEEPSLATE_COPPER_ORE).toString());
        list.add(Registries.BLOCK.getId(Blocks.DEEPSLATE_DIAMOND_ORE).toString());
        list.add(Registries.BLOCK.getId(Blocks.DEEPSLATE_EMERALD_ORE).toString());
        list.add(Registries.BLOCK.getId(Blocks.DEEPSLATE_GOLD_ORE).toString());
        list.add(Registries.BLOCK.getId(Blocks.DEEPSLATE_IRON_ORE).toString());
        list.add(Registries.BLOCK.getId(Blocks.DEEPSLATE_LAPIS_ORE).toString());
        list.add(Registries.BLOCK.getId(Blocks.DEEPSLATE_REDSTONE_ORE).toString());
        list.add(Registries.BLOCK.getId(Blocks.DIAMOND_ORE).toString());
        list.add(Registries.BLOCK.getId(Blocks.EMERALD_ORE).toString());
        list.add(Registries.BLOCK.getId(Blocks.GOLD_ORE).toString());
        list.add(Registries.BLOCK.getId(Blocks.IRON_ORE).toString());
        list.add(Registries.BLOCK.getId(Blocks.LAPIS_ORE).toString());
        list.add(Registries.BLOCK.getId(Blocks.NETHER_GOLD_ORE).toString());
        list.add(Registries.BLOCK.getId(Blocks.NETHER_QUARTZ_ORE).toString());
        list.add(Registries.BLOCK.getId(Blocks.REDSTONE_ORE).toString());

        return list;
    }
}
