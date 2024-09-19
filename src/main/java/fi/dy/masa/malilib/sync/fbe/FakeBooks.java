package fi.dy.masa.malilib.sync.fbe;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.component.type.WritableBookContentComponent;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Objects;

public class FakeBooks extends FakeBlockEntity implements Inventory
{
    private static final int MAX_SLOTS = 6;
    private DefaultedList<ItemStack> inventory;
    private int lastInteractedSlot;
    private ItemStack book;
    private int currentPage;
    private int pageCount;

    public FakeBooks(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.inventory = DefaultedList.ofSize(MAX_SLOTS, ItemStack.EMPTY);
        this.lastInteractedSlot = -1;

        this.book = ItemStack.EMPTY;
        this.currentPage = 0;
        this.pageCount = 0;
    }

    public FakeBooks(BlockEntity be, World world)
    {
        this(be.getType(), be.getPos(), be.getCachedState());
        this.copyFromBlockEntityInternal(be, world.getRegistryManager());
    }

    // Bookshelf
    public int size()
    {
        return this.inventory.size();
    }

    public boolean isEmpty()
    {
        return this.inventory.stream().allMatch(ItemStack::isEmpty);
    }

    public ItemStack getStack(int slot)
    {
        return this.inventory.get(slot);
    }

    public ItemStack removeStack(int slot, int amount)
    {
        ItemStack itemStack = Objects.requireNonNullElse(this.inventory.get(slot), ItemStack.EMPTY);
        this.inventory.set(slot, ItemStack.EMPTY);

        return itemStack;
    }

    public ItemStack removeStack(int slot)
    {
        return this.removeStack(slot, 1);
    }

    public void setStack(int slot, ItemStack stack)
    {
        if (stack.isIn(ItemTags.BOOKSHELF_BOOKS))
        {
            this.inventory.set(slot, stack);
        }
        else if (stack.isEmpty())
        {
            this.removeStack(slot, 1);
        }
    }

    public void markDirty() {}

    public boolean canPlayerUse(PlayerEntity player)
    {
        return false;
    }

    public int getLastInteractedSlot()
    {
        return this.lastInteractedSlot;
    }

    public void setLastInteractedSlot(int slot)
    {
        this.lastInteractedSlot = slot;
    }

    // Lectern
    public ItemStack getBook() {
        return this.book;
    }

    public boolean hasBook()
    {
        if (this.book.isEmpty())
        {
            return false;
        }

        return this.book.contains(DataComponentTypes.WRITABLE_BOOK_CONTENT) || this.book.contains(DataComponentTypes.WRITTEN_BOOK_CONTENT);
    }

    public void setBook(ItemStack book)
    {
        this.book = book;
    }

    public int getCurrentPage()
    {
        return this.currentPage;
    }

    public void setCurrentPage(int currentPage)
    {
        this.currentPage = currentPage;
    }

    public int getPageCount()
    {
        return this.pageCount;
    }

    private static int getPageCount(ItemStack stack)
    {
        WrittenBookContentComponent writtenBookContentComponent = stack.get(DataComponentTypes.WRITTEN_BOOK_CONTENT);
        if (writtenBookContentComponent != null)
        {
            return writtenBookContentComponent.pages().size();
        }
        else
        {
            WritableBookContentComponent writableBookContentComponent = stack.get(DataComponentTypes.WRITABLE_BOOK_CONTENT);
            return writableBookContentComponent != null ? writableBookContentComponent.pages().size() : 0;
        }
    }

    public void setPageCount(int pageCount)
    {
        this.pageCount = pageCount;
    }

    public void clear()
    {
        this.setBook(ItemStack.EMPTY);
    }

    public void readNbt(@Nonnull NbtCompound nbt, RegistryWrapper.WrapperLookup registry)
    {
        super.readNbt(nbt, registry);
        if (nbt.contains("Book", 10))
        {
           this.setBook(ItemStack.fromNbt(registry, nbt.getCompound("Book")).orElse(ItemStack.EMPTY));
           this.pageCount = getPageCount(this.book);
           this.currentPage = MathHelper.clamp(nbt.getInt("Page"), 0, this.pageCount - 1);
        }
        else
        {
            this.book = ItemStack.EMPTY;
        }

        if (this.inventory == null)
        {
            this.inventory = DefaultedList.ofSize(MAX_SLOTS, ItemStack.EMPTY);
        }
        if (nbt.contains("Items"))
        {
            this.inventory.clear();
            Inventories.readNbt(nbt, this.inventory, registry);
        }

        if (nbt.contains("last_interacted_slot"))
        {
            this.lastInteractedSlot = nbt.getInt("last_interacted_slot");
        }
    }

    public void writeNbt(@Nonnull NbtCompound nbt, RegistryWrapper.WrapperLookup registry)
    {
        super.writeNbt(nbt, registry);
        if (!this.getBook().isEmpty())
        {
            nbt.put("Book", this.getBook().toNbt(registry));
            nbt.putInt("Page", this.currentPage);
        }
    }

    protected void readComponents(FakeBlockEntity.ComponentsAccess components)
    {
        super.readComponents(components);
        components.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT).copyTo(this.inventory);
    }

    protected void addComponents(ComponentMap.Builder builder)
    {
        super.addComponents(builder);
        builder.add(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(this.inventory));
    }
}
