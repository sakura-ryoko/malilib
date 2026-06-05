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
    public static final CachedTagKey GLASS_PANES_KEY              = new CachedTagKey(MaLiLibReference.MOD_ID, "glass_panes");
    public static final CachedTagKey SCULK_BLOCKS_KEY             = new CachedTagKey(MaLiLibReference.MOD_ID, "sculk_blocks");
    public static final CachedTagKey REPLACEABLE_BLOCKS_KEY       = new CachedTagKey(MaLiLibReference.MOD_ID, "replaceable_blocks");
    public static final CachedTagKey ORE_BLOCKS_KEY               = new CachedTagKey(MaLiLibReference.MOD_ID, "ore_blocks");

    public static List<CachedTagKey> getKeys()
    {
        List<CachedTagKey> list = new ArrayList<>();

        list.add(CORAL_FANS_KEY);
        list.add(GLASS_PANES_KEY);
        list.add(SCULK_BLOCKS_KEY);
        list.add(REPLACEABLE_BLOCKS_KEY);
        list.add(ORE_BLOCKS_KEY);

        return list;
    }

    public static void startCache()
	{
        clearCache();

        CachedBlockTags.getInstance().build(CORAL_FANS_KEY, buildAllCoralFansCache());
        CachedBlockTags.getInstance().build(GLASS_PANES_KEY, buildGlassPanesCache());
        CachedBlockTags.getInstance().build(SCULK_BLOCKS_KEY, buildSculkCache());
        CachedBlockTags.getInstance().build(REPLACEABLE_BLOCKS_KEY, buildReplaceableCache());
        CachedBlockTags.getInstance().build(ORE_BLOCKS_KEY, buildOreCache());
	}

    private static void clearCache()
	{
        CachedBlockTags.getInstance().clearEntry(CORAL_FANS_KEY);
        CachedBlockTags.getInstance().clearEntry(GLASS_PANES_KEY);
        CachedBlockTags.getInstance().clearEntry(SCULK_BLOCKS_KEY);
        CachedBlockTags.getInstance().clearEntry(REPLACEABLE_BLOCKS_KEY);
        CachedBlockTags.getInstance().clearEntry(ORE_BLOCKS_KEY);
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

    private static List<String> buildGlassPanesCache()
    {
        List<String> list = new ArrayList<>();

        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.GLASS_PANE).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.STAINED_GLASS_PANE.black()).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.STAINED_GLASS_PANE.blue()).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.STAINED_GLASS_PANE.brown()).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.STAINED_GLASS_PANE.cyan()).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.STAINED_GLASS_PANE.gray()).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.STAINED_GLASS_PANE.green()).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.STAINED_GLASS_PANE.lightBlue()).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.STAINED_GLASS_PANE.lightGray()).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.STAINED_GLASS_PANE.lime()).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.STAINED_GLASS_PANE.magenta()).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.STAINED_GLASS_PANE.orange()).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.STAINED_GLASS_PANE.pink()).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.STAINED_GLASS_PANE.purple()).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.STAINED_GLASS_PANE.red()).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.STAINED_GLASS_PANE.yellow()).toString());
        list.add(BuiltInRegistries.BLOCK.getKey(Blocks.STAINED_GLASS_PANE.white()).toString());

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

        list.add("#"+BlockTags.AIR.location().toString());
        list.add("#"+BlockTags.ANVIL.location().toString());
        list.add("#"+BlockTags.BARS.location().toString());
        list.add("#"+BlockTags.BEDS.location().toString());
        list.add("#"+BlockTags.BUTTONS.location().toString());
        list.add("#"+BlockTags.CANDLE_CAKES.location().toString());
        list.add("#"+BlockTags.CANDLES.location().toString());
        list.add("#"+BlockTags.CEILING_HANGING_SIGNS.location().toString());
        list.add("#"+BlockTags.CHAINS.location().toString());
        list.add("#"+BlockTags.CONCRETE.location().toString());
        list.add("#"+BlockTags.CONCRETE_POWDERS.location().toString());
        list.add("#"+BlockTags.COPPER.location().toString());
        list.add("#"+BlockTags.COPPER_CHESTS.location().toString());
        list.add("#"+BlockTags.COPPER_GOLEM_STATUES.location().toString());
        list.add("#"+BlockTags.CORAL_PLANTS.location().toString());
        list.add("#"+BlockTags.DOORS.location().toString());
        list.add("#"+BlockTags.FENCE_GATES.location().toString());
        list.add("#"+BlockTags.FENCES.location().toString());
        list.add("#"+BlockTags.FLOWER_POTS.location().toString());
        list.add("#"+BlockTags.FLOWERS.location().toString());
        list.add("#"+BlockTags.GLAZED_TERRACOTTA.location().toString());
        list.add("#"+BlockTags.IMPERMEABLE.location().toString());
        list.add("#"+BlockTags.LANTERNS.location().toString());
        list.add("#"+BlockTags.LEAVES.location().toString());
        list.add("#"+BlockTags.LIGHTNING_RODS.location().toString());
        list.add("#"+BlockTags.LOGS.location().toString());
        list.add("#"+BlockTags.PLANKS.location().toString());
        list.add("#"+BlockTags.PRESSURE_PLATES.location().toString());
        list.add("#"+BlockTags.SAND.location().toString());
        list.add("#"+BlockTags.SHULKER_BOXES.location().toString());
        list.add("#"+BlockTags.SLABS.location().toString());
        list.add("#"+BlockTags.STAIRS.location().toString());
        list.add("#"+BlockTags.STANDING_SIGNS.location().toString());
        list.add("#"+BlockTags.STONE_BRICKS.location().toString());
        list.add("#"+BlockTags.STONE_BUTTONS.location().toString());
        list.add("#"+BlockTags.TERRACOTTA.location().toString());
        list.add("#"+BlockTags.TRAPDOORS.location().toString());
        list.add("#"+BlockTags.WALL_HANGING_SIGNS.location().toString());
        list.add("#"+BlockTags.WALL_SIGNS.location().toString());
        list.add("#"+BlockTags.WALLS.location().toString());
        list.add("#"+BlockTags.WOODEN_BUTTONS.location().toString());
        list.add("#"+BlockTags.WOODEN_SHELVES.location().toString());
        list.add("#"+BlockTags.WOOL.location().toString());
        list.add("#"+BlockTags.WOOL_CARPETS.location().toString());

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
