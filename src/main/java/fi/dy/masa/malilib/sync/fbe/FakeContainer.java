package fi.dy.masa.malilib.sync.fbe;

import java.util.Objects;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class FakeContainer extends FakeBlockEntity implements Inventory
{
    private static final int MAX_SLOTS = 256;
    private final DefaultedList<ItemStack> stacks;

    public FakeContainer(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.stacks = DefaultedList.ofSize(MAX_SLOTS, ItemStack.EMPTY);
    }

    public FakeContainer(BlockEntityType<?> type, BlockPos pos, BlockState state, int maxSlots)
    {
        super(type, pos, state);
        this.stacks = DefaultedList.ofSize(maxSlots, ItemStack.EMPTY);
    }

    public FakeBlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new FakeContainer(BlockEntityType.CHEST, pos, state);
    }

    public DefaultedList<ItemStack> getStacks()
    {
        return this.stacks;
    }

    public int size()
    {
        return this.stacks.size();
    }

    public boolean isEmpty()
    {
        return this.stacks.stream().allMatch(ItemStack::isEmpty);
    }

    public ItemStack getStack(int slot)
    {
        return this.stacks.get(slot);
    }

    public ItemStack removeStack(int slot, int amount)
    {
        ItemStack itemStack = Objects.requireNonNullElse(this.stacks.get(slot), ItemStack.EMPTY);
        this.stacks.set(slot, ItemStack.EMPTY);
        return itemStack;
    }

    public ItemStack removeStack(int slot)
    {
        return this.removeStack(slot, 1);
    }

    public void setStack(int slot, ItemStack stack)
    {
        if (stack.isEmpty())
        {
            this.removeStack(slot, 1);
        }
        else
        {
            this.stacks.set(slot, stack);
        }
    }

    public void markDirty()
    {
        // NO-OP
    }

    public boolean canPlayerUse(PlayerEntity player)
    {
        return true;
    }

    public void clear()
    {
        this.stacks.clear();
    }

    protected void readComponents(ComponentsAccess components)
    {
        super.readComponents(components);
        components.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT).copyTo(this.stacks);
    }

    protected void addComponents(ComponentMap.Builder builder)
    {
        super.addComponents(builder);
        builder.add(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(this.stacks));
    }
}
