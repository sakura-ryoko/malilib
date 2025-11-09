package fi.dy.masa.malilib.util.game.wrap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jetbrains.annotations.ApiStatus;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

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
            return getBlockById(ResourceLocation.parse(name));
        }
        catch (Exception e)
        {
            return Blocks.AIR;
        }
    }

    public static Block getBlockById(ResourceLocation id)
    {
        return BuiltInRegistries.BLOCK.getValue(id);
    }

    public static @Nonnull ResourceLocation getBlockId(Block block)
    {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    public static @Nonnull ResourceLocation getBlockId(BlockState state)
    {
        return getBlockId(state.getBlock());
    }

    public static String getBlockIdStr(Block block)
    {
        ResourceLocation id = getBlockId(block);
        return id.toString();
    }

    /**
     * Get a Block's Registry Entry.
     *
     * @param id ()
     * @param registry ()
     * @return ()
     */
    public static Holder<Block> getBlockEntry(ResourceLocation id, @Nonnull RegistryAccess registry)
    {
        try
        {
            return registry.lookupOrThrow(BuiltInRegistries.BLOCK.key()).get(id).orElseThrow();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    @Nullable
    public static Holder<BlockEntityType<?>> getBlockEntityType(ResourceLocation id, @Nonnull RegistryAccess registry)
    {
        try
        {
            return registry.lookupOrThrow(BuiltInRegistries.BLOCK_ENTITY_TYPE.key()).get(id).orElse(null);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    @Nullable
    public static Holder<EntityType<?>> getEntityType(ResourceLocation id, @Nonnull RegistryAccess registry)
    {
        try
        {
            return registry.lookupOrThrow(BuiltInRegistries.ENTITY_TYPE.key()).get(id).orElse(null);
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

    public static Collection<ResourceLocation> getRegisteredBlockIds()
    {
        return new ArrayList<>(BuiltInRegistries.BLOCK.keySet());
    }

    public static List<Block> getSortedBlockList()
    {
        List<Block> blocks = new ArrayList<>(BuiltInRegistries.BLOCK.stream().toList());

        blocks.sort(Comparator.comparing(RegistryUtils::getBlockIdStr));

        return blocks;
    }

    public static Item getItemByIdStr(String name)
    {
        try
        {
            return getItemById(ResourceLocation.parse(name));
        }
        catch (Exception e)
        {
            return Items.AIR;
        }
    }

    public static Item getItemById(ResourceLocation id)
    {
        return BuiltInRegistries.ITEM.getValue(id);
    }

    public static ResourceLocation getItemId(Item item)
    {
        return BuiltInRegistries.ITEM.getKey(item);
    }

    public static String getItemIdStr(Item item)
    {
        ResourceLocation id = getItemId(item);
        return id.toString();
    }

    public static Collection<ResourceLocation> getRegisteredItemIds()
    {
        return new ArrayList<>(BuiltInRegistries.ITEM.keySet());
    }

    public static List<Item> getSortedItemList()
    {
        List<Item> items = new ArrayList<>(BuiltInRegistries.ITEM.stream().toList());

        items.sort(Comparator.comparing(RegistryUtils::getItemIdStr));

        return items;
    }
}
