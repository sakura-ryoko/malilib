package fi.dy.masa.malilib.util.game.wrap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

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
            return getBlockById(Identifier.parse(name));
        }
        catch (Exception e)
        {
            return Blocks.AIR;
        }
    }

    public static Block getBlockById(Identifier id)
    {
        return BuiltInRegistries.BLOCK.getValue(id);
    }

    public static @Nonnull Identifier getBlockId(Block block)
    {
        return BuiltInRegistries.BLOCK.getKey(block);
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
    public static Holder<@NotNull Block> getBlockEntry(Identifier id, @Nonnull RegistryAccess registry)
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
    public static Holder<@NotNull BlockEntityType<?>> getBlockEntityType(Identifier id, @Nonnull RegistryAccess registry)
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
    public static Holder<@NotNull EntityType<?>> getEntityType(Identifier id, @Nonnull RegistryAccess registry)
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

    public static Collection<Identifier> getRegisteredBlockIds()
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
            return getItemById(Identifier.parse(name));
        }
        catch (Exception e)
        {
            return Items.AIR;
        }
    }

    public static Item getItemById(Identifier id)
    {
        return BuiltInRegistries.ITEM.getValue(id);
    }

    public static Identifier getItemId(Item item)
    {
        return BuiltInRegistries.ITEM.getKey(item);
    }

    public static String getItemIdStr(Item item)
    {
        Identifier id = getItemId(item);
        return id.toString();
    }

    public static Collection<Identifier> getRegisteredItemIds()
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
