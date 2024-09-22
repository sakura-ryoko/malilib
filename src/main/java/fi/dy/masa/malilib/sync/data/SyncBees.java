package fi.dy.masa.malilib.sync.data;

import java.util.List;
import javax.annotation.Nonnull;
import com.google.common.collect.Lists;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SyncBees extends SyncData
{
    private static final String BEES_KEY = "bees";
    private static final String FLOWER_POS_KEY = "flower_pos";
    private final List<FakeBee> bees = Lists.newArrayList();
    private BlockPos flowerPos;

    public SyncBees(BlockEntityType<?> type, BlockPos pos, BlockState state, World world)
    {
        super(type, pos, state, world);
        this.flowerPos = BlockPos.ORIGIN;
    }

    public SyncBees(BlockEntity be)
    {
        super(be);
        this.flowerPos = BlockPos.ORIGIN;
        this.copyNbtFromBlockEntity(be);
        //this.readCustomDataFromNbt(this.getNbt());
    }

    protected void initInventory()
    {
        // NO-OP
    }

    protected void initAttributes(Entity entity)
    {
        // NO-OP
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

    public void readNbt(@Nonnull NbtCompound nbt)
    {
        this.readCustomDataFromNbt(nbt);
        super.readNbt(nbt);
    }

    public void writeNbt(@Nonnull NbtCompound nbt)
    {
        this.writeCustomDataToNbt(nbt);
        super.writeNbt(nbt);
    }

    protected void readCustomDataFromNbt(NbtCompound nbt)
    {
        this.bees.clear();
        if (nbt.contains(BEES_KEY))
        {
            BeehiveBlockEntity.BeeData.LIST_CODEC.parse(NbtOps.INSTANCE, nbt.get(BEES_KEY)).resultOrPartial((string) ->
                                       LOGGER.error("Failed to parse bees: '{}'", string)).ifPresent((list) -> list.forEach(this::addBee));
        }

        this.flowerPos = NbtHelper.toBlockPos(nbt, FLOWER_POS_KEY).orElse(null);
    }

    protected void writeCustomDataToNbt(NbtCompound nbt)
    {
        nbt.put(BEES_KEY, BeehiveBlockEntity.BeeData.LIST_CODEC.encodeStart(NbtOps.INSTANCE, this.createBeesData()).getOrThrow());

        if (this.hasFlowerPos())
        {
            nbt.put(FLOWER_POS_KEY, NbtHelper.fromBlockPos(this.flowerPos));
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
