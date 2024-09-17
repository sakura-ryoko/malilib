package fi.dy.masa.malilib.sync.fbe;

import java.util.List;
import com.google.common.collect.Lists;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.util.math.BlockPos;

public class FakeBees extends FakeBlockEntity
{
    private final List<FakeBee> bees = Lists.newArrayList();

    public FakeBees(BlockPos pos, BlockState state)
    {
        super(BlockEntityType.BEEHIVE, pos, state);
    }

    public FakeBlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new FakeBees(pos, state);
    }

    private void addBee(BeehiveBlockEntity.BeeData bee)
    {
        this.bees.add(new FakeBee(bee));
    }

    private List<BeehiveBlockEntity.BeeData> createBeesData()
    {
        return this.bees.stream().map(FakeBee::createData).toList();
    }

    protected void readComponents(ComponentsAccess components)
    {
        super.readComponents(components);
        this.bees.clear();
        List<BeehiveBlockEntity.BeeData> list = components.getOrDefault(DataComponentTypes.BEES, List.of());
        list.forEach(this::addBee);
    }

    protected void addComponents(ComponentMap.Builder builder)
    {
        super.addComponents(builder);
        builder.add(DataComponentTypes.BEES, this.createBeesData());
    }

    static class FakeBee
    {
        private final BeehiveBlockEntity.BeeData data;
        private int ticksInHive;

        FakeBee(BeehiveBlockEntity.BeeData data)
        {
            this.data = data;
            this.ticksInHive = data.ticksInHive();
        }

        public BeehiveBlockEntity.BeeData getData()
        {
            return this.data;
        }

        public int getTicksInHive()
        {
            return this.ticksInHive;
        }

        public BeehiveBlockEntity.BeeData createData()
        {
            return new BeehiveBlockEntity.BeeData(this.data.entityData(), this.ticksInHive, this.data.minTicksInHive());
        }
    }
}
