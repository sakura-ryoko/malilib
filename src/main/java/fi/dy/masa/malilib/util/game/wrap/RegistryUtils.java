package fi.dy.masa.malilib.util.game.wrap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

/**
 * Post-ReWrite code
 */
@ApiStatus.Experimental
public class RegistryUtils
{
    public static Block getBlockByIdStr(String name)
    {
        try
        {
            return getBlockById(Identifier.of(name));
        }
        catch (Exception e)
        {
            return Blocks.AIR;
        }
    }

    public static Block getBlockById(Identifier id)
    {
        return Registries.BLOCK.get(id);
    }

    public static @Nonnull Identifier getBlockId(Block block)
    {
        return Registries.BLOCK.getId(block);
    }

    public static @Nonnull Identifier getBlockId(BlockState state)
    {
        return getBlockId(state.getBlock());
    }

    public static String getBlockIdStr(Block block)
    {
        Identifier id = getBlockId(block);
        return id.toString();
    }

    /**
     * Get a Block's Registry Entry.
     *
     * @param id ()
     * @param registry ()
     * @return ()
     */
    public static RegistryEntry<Block> getBlockEntry(Identifier id, @Nonnull DynamicRegistryManager registry)
    {
        try
        {
            return registry.getOrThrow(Registries.BLOCK.getKey()).getEntry(id).orElseThrow();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    @Nullable
    public static RegistryEntry<BlockEntityType<?>> getBlockEntityType(Identifier id, @Nonnull DynamicRegistryManager registry)
    {
        try
        {
            return registry.getOrThrow(Registries.BLOCK_ENTITY_TYPE.getKey()).getEntry(id).orElse(null);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    @Nullable
    public static RegistryEntry<EntityType<?>> getEntityType(Identifier id, @Nonnull DynamicRegistryManager registry)
    {
        try
        {
            return registry.getOrThrow(Registries.ENTITY_TYPE.getKey()).getEntry(id).orElse(null);
        }
            catch (Exception e)
        {
            return null;
        }
    }

    public static String getBlockIdStr(BlockState state)
    {
        return getBlockIdStr(state.getBlock());
    }

    public static Collection<Identifier> getRegisteredBlockIds()
    {
        return new ArrayList<>(Registries.BLOCK.getIds());
    }

    public static List<Block> getSortedBlockList()
    {
        List<Block> blocks = new ArrayList<>(Registries.BLOCK.stream().toList());

        blocks.sort(Comparator.comparing(RegistryUtils::getBlockIdStr));

        return blocks;
    }

    public static Item getItemByIdStr(String name)
    {
        try
        {
            return getItemById(Identifier.of(name));
        }
        catch (Exception e)
        {
            return Items.AIR;
        }
    }

    public static Item getItemById(Identifier id)
    {
        return Registries.ITEM.get(id);
    }

    public static Identifier getItemId(Item item)
    {
        return Registries.ITEM.getId(item);
    }

    public static String getItemIdStr(Item item)
    {
        Identifier id = getItemId(item);
        return id.toString();
    }

    public static Collection<Identifier> getRegisteredItemIds()
    {
        return new ArrayList<>(Registries.ITEM.getIds());
    }

    public static List<Item> getSortedItemList()
    {
        List<Item> items = new ArrayList<>(Registries.ITEM.stream().toList());

        items.sort(Comparator.comparing(RegistryUtils::getItemIdStr));

        return items;
    }
}
