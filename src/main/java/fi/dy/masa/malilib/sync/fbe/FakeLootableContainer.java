package fi.dy.masa.malilib.sync.fbe;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerLootComponent;
import net.minecraft.inventory.LootableInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FakeLootableContainer extends FakeLockableContainer implements LootableInventory
{
    @Nullable
    protected RegistryKey<LootTable> lootTable;
    protected long lootTableSeed = 0L;

    public FakeLootableContainer(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public FakeLootableContainer(BlockEntity be, World world)
    {
        this(be.getType(), be.getPos(), be.getCachedState());
        //this.setWorld(world);
        this.copyFromBlockEntityInternal(be, world.getRegistryManager());
    }

    public FakeLootableContainer createBlockEntity(BlockPos pos, BlockState state)
    {
        return new FakeLootableContainer(BlockEntityType.BARREL, pos, state);
    }

    @Nullable
    public RegistryKey<LootTable> getLootTable()
    {
        return this.lootTable;
    }

    public void setLootTable(@Nullable RegistryKey<LootTable> lootTable)
    {
        this.lootTable = lootTable;
    }

    public long getLootTableSeed()
    {
        return this.lootTableSeed;
    }

    public void setLootTableSeed(long lootTableSeed)
    {
        this.lootTableSeed = lootTableSeed;
    }

    public boolean isEmpty()
    {
        this.generateLoot(null);
        return super.isEmpty();
    }

    public ItemStack getStack(int slot)
    {
        this.generateLoot(null);
        return super.getStack(slot);
    }

    public ItemStack removeStack(int slot, int amount)
    {
        this.generateLoot(null);
        return super.removeStack(slot, amount);
    }

    public ItemStack removeStack(int slot)
    {
        this.generateLoot(null);
        return super.removeStack(slot);
    }

    public void setStack(int slot, ItemStack stack)
    {
        this.generateLoot(null);
        super.setStack(slot, stack);
    }

    protected void readComponents(FakeBlockEntity.ComponentsAccess components)
    {
        super.readComponents(components);
        ContainerLootComponent containerLootComponent = components.get(DataComponentTypes.CONTAINER_LOOT);
        if (containerLootComponent != null)
        {
            this.lootTable = containerLootComponent.lootTable();
            this.lootTableSeed = containerLootComponent.seed();
        }
    }

    protected void addComponents(ComponentMap.Builder builder)
    {
        super.addComponents(builder);
        if (this.lootTable != null)
        {
            builder.add(DataComponentTypes.CONTAINER_LOOT, new ContainerLootComponent(this.lootTable, this.lootTableSeed));
        }
    }
}
