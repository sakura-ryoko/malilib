package fi.dy.masa.malilib.sync.fbe;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.inventory.ContainerLock;
import net.minecraft.util.math.BlockPos;

public class FakeLockable extends FakeNamed
{
    private ContainerLock lock;

    public FakeLockable(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public FakeBlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new FakeContainer(BlockEntityType.BEACON, pos, state);
    }

    public ContainerLock getLock()
    {
        return this.lock;
    }

    protected void readComponents(ComponentsAccess components)
    {
        super.readComponents(components);
        this.lock = components.getOrDefault(DataComponentTypes.LOCK, ContainerLock.EMPTY);
    }

    protected void addComponents(ComponentMap.Builder builder)
    {
        super.addComponents(builder);
        if (!this.lock.equals(ContainerLock.EMPTY))
        {
            builder.add(DataComponentTypes.LOCK, this.lock);
        }
    }
}
