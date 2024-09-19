package fi.dy.masa.malilib.sync.fbe;

import java.util.Iterator;
import javax.annotation.Nonnull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ContainerLock;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FakeLockableContainer extends FakeContainer implements Inventory, IFakeContainer
{
    private static final int MAX_SLOTS = 256;
    protected DefaultedList<ItemStack> inventory;
    private ContainerLock lock;
    @Nullable
    private Text customName;

    public FakeLockableContainer(BlockEntityType<?> type, BlockPos pos, BlockState state, int maxSlots)
    {
        super(type, pos, state, maxSlots);
        this.inventory = DefaultedList.ofSize(maxSlots, ItemStack.EMPTY);
        this.lock = ContainerLock.EMPTY;
    }

    public FakeLockableContainer(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        this(type, pos, state, MAX_SLOTS);
    }

    public FakeLockableContainer(BlockEntity be, World world)
    {
        this(be.getType(), be.getPos(), be.getCachedState());
        //this.setWorld(world);
        this.copyFromBlockEntityInternal(be, world.getRegistryManager());
    }

    public FakeLockableContainer createBlockEntity(BlockPos pos, BlockState state)
    {
        return new FakeLockableContainer(BlockEntityType.BARREL, pos, state);
    }

    @Nullable
    public Text getCustomName()
    {
        return this.customName;
    }

    public ContainerLock getLock()
    {
        return this.lock;
    }

    public int size() {
        return this.inventory.size();
    }

    @Override
    public DefaultedList<ItemStack> getHeldStacks()
    {
        return this.inventory;
    }

    @Override
    public void setHeldStacks(DefaultedList<ItemStack> inventory)
    {
        this.inventory = inventory;
    }

    public boolean isEmpty()
    {
        Iterator<ItemStack> iter = this.getHeldStacks().iterator();

        ItemStack itemStack;
        do
        {
            if (!iter.hasNext())
            {
                return true;
            }

            itemStack = iter.next();
        }
        while (itemStack.isEmpty());

        return false;
    }

    public ItemStack getStack(int slot)
    {
        return this.getHeldStacks().get(slot);
    }

    public ItemStack removeStack(int slot, int amount)
    {
        ItemStack itemStack = Inventories.splitStack(this.getHeldStacks(), slot, amount);
        if (!itemStack.isEmpty())
        {
            this.markDirty();
        }

        return itemStack;
    }

    public ItemStack removeStack(int slot)
    {
        return Inventories.removeStack(this.getHeldStacks(), slot);
    }

    public void setStack(int slot, ItemStack stack)
    {
        this.getHeldStacks().set(slot, stack);
        stack.capCount(this.getMaxCount(stack));
        this.markDirty();
    }

    public boolean isValid(int slot, ItemStack stack)
    {
        return true;
    }

    public boolean canPlayerUse(PlayerEntity player)
    {
        return false;
    }

    public void clear()
    {
        this.getHeldStacks().clear();
    }

    public void readNbt(@Nonnull NbtCompound nbt, RegistryWrapper.WrapperLookup registry)
    {
        super.readNbt(nbt, registry);
        this.lock = ContainerLock.fromNbt(nbt);
        if (nbt.contains("CustomName", 8))
        {
            this.customName = tryParseCustomName(nbt.getString("CustomName"), registry);
        }
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.readNbt(nbt, this.inventory, registry);
    }

    public void writeNbt(@Nonnull NbtCompound nbt, RegistryWrapper.WrapperLookup registry)
    {
        super.writeNbt(nbt, registry);
        this.lock.writeNbt(nbt);
        if (this.customName != null)
        {
            nbt.putString("CustomName", Text.Serialization.toJsonString(this.customName, registry));
        }
        Inventories.writeNbt(nbt, this.inventory, registry);
    }

    protected void readComponents(ComponentsAccess components)
    {
        super.readComponents(components);
        this.customName = components.get(DataComponentTypes.CUSTOM_NAME);
        this.lock = components.getOrDefault(DataComponentTypes.LOCK, ContainerLock.EMPTY);
    }

    protected void addComponents(ComponentMap.Builder builder)
    {
        super.addComponents(builder);
        builder.add(DataComponentTypes.CUSTOM_NAME, this.customName);
        if (!this.lock.equals(ContainerLock.EMPTY))
        {
            builder.add(DataComponentTypes.LOCK, this.lock);
        }
    }
}
