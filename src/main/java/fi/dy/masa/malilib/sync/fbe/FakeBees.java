package fi.dy.masa.malilib.sync.fbe;

import java.util.List;
import javax.annotation.Nonnull;
import com.google.common.collect.Lists;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FakeBees extends FakeBlockEntity
{
    private final List<FakeBee> bees = Lists.newArrayList();
    private BlockPos flowerPos;

    public FakeBees(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.flowerPos = BlockPos.ORIGIN;
    }

    public FakeBees(BlockPos pos, BlockState state)
    {
        this(BlockEntityType.BEEHIVE, pos, state);
    }

    public FakeBees(BlockEntity be, World world)
    {
        this(be.getType(), be.getPos(), be.getCachedState());
        //this.setWorld(world);
        this.copyFromBlockEntityInternal(be, world.getRegistryManager());
    }

    public FakeBees createBlockEntity(BlockPos pos, BlockState state)
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

    public boolean hasFlowerPos()
    {
        return this.flowerPos != null;
    }

    public BlockPos getFlowerPos()
    {
        return this.flowerPos;
    }

    public void setFlowerPos(BlockPos pos)
    {
        this.flowerPos = pos;
    }

    public void readNbt(@Nonnull NbtCompound nbt, RegistryWrapper.WrapperLookup registries)
    {
        super.readNbt(nbt, registries);
        this.bees.clear();
        if (nbt.contains("bees"))
        {
            BeehiveBlockEntity.BeeData.LIST_CODEC.parse(NbtOps.INSTANCE, nbt.get("bees")).resultOrPartial((string) ->
                          LOGGER.error("Failed to parse bees: '{}'", string)).ifPresent((list) ->
                            list.forEach(this::addBee));
        }

        this.flowerPos = NbtHelper.toBlockPos(nbt, "flower_pos").orElse(null);
    }

    public void writeNbt(@Nonnull NbtCompound nbt, RegistryWrapper.WrapperLookup registries)
    {
        super.writeNbt(nbt, registries);
        nbt.put("bees", BeehiveBlockEntity.BeeData.LIST_CODEC.encodeStart(NbtOps.INSTANCE, this.createBeesData()).getOrThrow());

        if (this.hasFlowerPos())
        {
            nbt.put("flower_pos", NbtHelper.fromBlockPos(this.flowerPos));
        }
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
        private final int ticksInHive;

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
