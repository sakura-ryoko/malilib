package fi.dy.masa.malilib.data;

import fi.dy.masa.malilib.MaLiLibReference;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;

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

        list.add("#" + BlockTags.WALL_CORALS.location().toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.BRAIN_CORAL_FAN).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.FIRE_CORAL_FAN).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.BUBBLE_CORAL_FAN).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.TUBE_CORAL_FAN).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.HORN_CORAL_FAN).toString());

        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.DEAD_BRAIN_CORAL_FAN).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.DEAD_FIRE_CORAL_FAN).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.DEAD_BUBBLE_CORAL_FAN).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.DEAD_TUBE_CORAL_FAN).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.DEAD_HORN_CORAL_FAN).toString());

        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.DEAD_BRAIN_CORAL_WALL_FAN).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.DEAD_FIRE_CORAL_WALL_FAN).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.DEAD_BUBBLE_CORAL_WALL_FAN).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.DEAD_TUBE_CORAL_WALL_FAN).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.DEAD_HORN_CORAL_WALL_FAN).toString());

        return list;
    }

    private static List<String> buildConcreteCache()
    {
        List<String> list = new ArrayList<>();

        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.BLACK_CONCRETE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.BLUE_CONCRETE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.BROWN_CONCRETE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.CYAN_CONCRETE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.GRAY_CONCRETE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.GREEN_CONCRETE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.LIGHT_BLUE_CONCRETE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.LIGHT_GRAY_CONCRETE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.LIME_CONCRETE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.MAGENTA_CONCRETE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.ORANGE_CONCRETE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.PINK_CONCRETE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.PURPLE_CONCRETE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.RED_CONCRETE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.YELLOW_CONCRETE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.WHITE_CONCRETE).toString());

        return list;
    }

    private static List<String> buildGlassPanesCache()
    {
        List<String> list = new ArrayList<>();

        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.GLASS_PANE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.BLACK_STAINED_GLASS_PANE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.BLUE_STAINED_GLASS_PANE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.BROWN_STAINED_GLASS_PANE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.CYAN_STAINED_GLASS_PANE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.GRAY_STAINED_GLASS_PANE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.GREEN_STAINED_GLASS_PANE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.LIME_STAINED_GLASS_PANE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.MAGENTA_STAINED_GLASS_PANE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.ORANGE_STAINED_GLASS_PANE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.PINK_STAINED_GLASS_PANE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.PURPLE_STAINED_GLASS_PANE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.RED_STAINED_GLASS_PANE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.YELLOW_STAINED_GLASS_PANE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.WHITE_STAINED_GLASS_PANE).toString());

        return list;
    }

    private static List<String> buildGlazedTerracottaCache()
    {
        List<String> list = new ArrayList<>();

        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.BLACK_GLAZED_TERRACOTTA).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.BLUE_GLAZED_TERRACOTTA).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.BROWN_GLAZED_TERRACOTTA).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.CYAN_GLAZED_TERRACOTTA).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.GRAY_GLAZED_TERRACOTTA).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.GREEN_GLAZED_TERRACOTTA).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.LIME_GLAZED_TERRACOTTA).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.MAGENTA_GLAZED_TERRACOTTA).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.ORANGE_GLAZED_TERRACOTTA).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.PINK_GLAZED_TERRACOTTA).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.PURPLE_GLAZED_TERRACOTTA).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.RED_GLAZED_TERRACOTTA).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.YELLOW_GLAZED_TERRACOTTA).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.WHITE_GLAZED_TERRACOTTA).toString());

        return list;
    }

    private static List<String> buildSculkCache()
    {
        List<String> list = new ArrayList<>();

        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.CALIBRATED_SCULK_SENSOR).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.SCULK).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.SCULK_CATALYST).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.SCULK_SENSOR).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.SCULK_SHRIEKER).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.SCULK_VEIN).toString());

        return list;
    }

    private static List<String> buildReplaceableCache()
    {
        List<String> list = new ArrayList<>();

        list.add("#"+BlockTags.ANVIL.location().toString());
        list.add("#"+BlockTags.BEDS.location().toString());
        list.add("#"+BlockTags.BUTTONS.location().toString());
        list.add("#"+BlockTags.CANDLE_CAKES.location().toString());
        list.add("#"+BlockTags.CANDLES.location().toString());
        list.add("#"+BlockTags.CEILING_HANGING_SIGNS.location().toString());
        list.add("#"+BlockTags.CONCRETE_POWDER.location().toString());
        list.add("#"+BlockTags.CORAL_PLANTS.location().toString());
        list.add("#"+BlockTags.DOORS.location().toString());
        list.add("#"+BlockTags.FENCE_GATES.location().toString());
        list.add("#"+BlockTags.FENCES.location().toString());
        list.add("#"+BlockTags.FLOWER_POTS.location().toString());
        list.add("#"+BlockTags.FLOWERS.location().toString());
        list.add("#"+BlockTags.LEAVES.location().toString());
        list.add("#"+BlockTags.LOGS.location().toString());
        list.add("#"+BlockTags.PLANKS.location().toString());
        list.add("#"+BlockTags.PRESSURE_PLATES.location().toString());
        list.add("#"+BlockTags.SAPLINGS.location().toString());
        list.add("#"+BlockTags.SHULKER_BOXES.location().toString());
        list.add("#"+BlockTags.SLABS.location().toString());
        list.add("#"+BlockTags.STAIRS.location().toString());
        list.add("#"+BlockTags.STANDING_SIGNS.location().toString());
        list.add("#"+BlockTags.TERRACOTTA.location().toString());
        list.add("#"+BlockTags.TRAPDOORS.location().toString());
        list.add("#"+BlockTags.WALL_HANGING_SIGNS.location().toString());
        list.add("#"+BlockTags.WALL_SIGNS.location().toString());
        list.add("#"+BlockTags.WALLS.location().toString());
        list.add("#"+BlockTags.WOOL.location().toString());
        list.add("#"+BlockTags.WOOL_CARPETS.location().toString());
        list.add("#"+BlockTags.IMPERMEABLE.location().toString());

        return list;
    }

    private static List<String> buildOreCache()
    {
        List<String> list = new ArrayList<>();

        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.COAL_ORE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.COPPER_ORE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.DEEPSLATE_COAL_ORE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.DEEPSLATE_COPPER_ORE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.DEEPSLATE_DIAMOND_ORE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.DEEPSLATE_EMERALD_ORE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.DEEPSLATE_GOLD_ORE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.DEEPSLATE_IRON_ORE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.DEEPSLATE_LAPIS_ORE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.DEEPSLATE_REDSTONE_ORE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.DIAMOND_ORE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.EMERALD_ORE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.GOLD_ORE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.IRON_ORE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.LAPIS_ORE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.NETHER_GOLD_ORE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.NETHER_QUARTZ_ORE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.REDSTONE_ORE).toString());

        return list;
    }
}
