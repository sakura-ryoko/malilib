package fi.dy.masa.malilib.sync.fbe;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.inventory.ContainerLock;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import fi.dy.masa.malilib.sync.fc.IFakeBlockProvider;

public class FakeLockableContainer extends FakeContainer implements IFakeBlockProvider
{
    private ContainerLock lock;
    @Nullable
    private Text customName;

    public FakeLockableContainer(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public FakeLockableContainer(BlockEntityType<?> type, BlockPos pos, BlockState state, int maxSlots)
    {
        super(type, pos, state, maxSlots);
    }

    @Override
    public FakeBlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new FakeLockableContainer(BlockEntityType.CRAFTER, pos, state);
    }

    @Nullable
    public Text getCustomName() {
        return this.customName;
    }

    public ContainerLock getLock()
    {
        return this.lock;
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
