package fi.dy.masa.malilib.sync.fbe;

import java.util.List;
import javax.annotation.Nonnull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.Sherds;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FakeSherds extends FakeLootableContainer implements SingleStackInventory.SingleStackBlockEntityInventory
{
    private ItemStack stack;
    private Sherds sherds;

    public FakeSherds(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.stack = ItemStack.EMPTY;
        this.sherds = Sherds.DEFAULT;
    }

    public FakeSherds(BlockEntity be, World world)
    {
        this(be.getType(), be.getPos(), be.getCachedState());
        this.setWorld(world);
        this.copyFromBlockEntity(be, world.getRegistryManager());
    }

    public FakeBlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new FakeSherds(BlockEntityType.DECORATED_POT, pos, state);
    }

    public ItemStack getStack()
    {
        this.generateLoot(null);
        return this.stack;
    }

    public void setStack(ItemStack stack)
    {
        this.generateLoot(null);
        this.stack = stack;
    }

    public Sherds getSherds()
    {
        return this.sherds;
    }

    @Nullable
    public BlockEntity asBlockEntity()
    {
        return null;
    }

    @Override
    public void markDirty()
    {
        // NO-OP
    }

    public void writeNbt(@Nonnull NbtCompound nbt, RegistryWrapper.WrapperLookup registries)
    {
        super.writeNbt(nbt, registries);
        this.sherds.toNbt(nbt);

        if (!this.writeLootTable(nbt) && !this.stack.isEmpty())
        {
            nbt.put("item", this.stack.toNbt(registries));
        }
    }

    public void readNbt(@Nonnull NbtCompound nbt, RegistryWrapper.WrapperLookup registries)
    {
        super.readNbt(nbt, registries);
        this.sherds = Sherds.fromNbt(nbt);

        if (!this.readLootTable(nbt))
        {
            if (nbt.contains("item", 10))
            {
                this.stack = ItemStack.fromNbt(registries, nbt.getCompound("item")).orElse(ItemStack.EMPTY);
            }
            else
            {
                this.stack = ItemStack.EMPTY;
            }
        }
    }

    protected void readComponents(ComponentsAccess components)
    {
        super.readComponents(components);
        this.sherds = components.getOrDefault(DataComponentTypes.POT_DECORATIONS, Sherds.DEFAULT);
        this.stack = components.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT).copyFirstStack();
    }

    protected void addComponents(ComponentMap.Builder builder)
    {
        super.addComponents(builder);
        builder.add(DataComponentTypes.POT_DECORATIONS, this.sherds);
        builder.add(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(List.of(this.stack)));
    }
}
