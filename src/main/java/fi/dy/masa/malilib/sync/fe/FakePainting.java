package fi.dy.masa.malilib.sync.fe;

import com.mojang.serialization.Codec;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.VariantHolder;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class FakePainting extends FakeDecoration implements VariantHolder<RegistryEntry<PaintingVariant>>
{
    private final static Codec<RegistryEntry<PaintingVariant>> CODEC = PaintingVariant.ENTRY_CODEC.fieldOf("variant").codec();
    private RegistryEntry<PaintingVariant> variant;

    public FakePainting(EntityType<?> type, World world, int entityId)
    {
        super(type, world, entityId);
    }

    private FakePainting(World world, int entityId, BlockPos pos)
    {
        super(EntityType.PAINTING, world, entityId, pos);
    }

    public FakePainting(World world, int entityId, BlockPos pos, Direction facing, RegistryEntry<PaintingVariant> variant)
    {
        this(world, entityId, pos);
        this.setVariant(variant);
        this.setFacing(facing);
    }

    public FakePainting(Entity input)
    {
        super(input);

        if (input instanceof PaintingEntity pe)
        {
            this.setVariant(pe.getVariant());
            this.setFacing(pe.getFacing());
            this.readCustomDataFromNbt(this.getNbt());
        }
    }

    public void setVariant(RegistryEntry<PaintingVariant> variant)
    {
        this.variant = variant;
    }

    public RegistryEntry<PaintingVariant> getVariant()
    {
        return this.variant;
    }

    public void writeCustomDataToNbt(NbtCompound nbt)
    {
        CODEC.encodeStart(this.getRegistryManager().getOps(NbtOps.INSTANCE), this.getVariant()).ifSuccess((nbtElement) ->
                          nbt.copyFrom((NbtCompound) nbtElement));
        nbt.putByte("facing", (byte) this.facing.getHorizontal());
        super.writeCustomDataToNbt(nbt);
    }

    public void readCustomDataFromNbt(NbtCompound nbt)
    {
        super.readCustomDataFromNbt(nbt);
        CODEC.parse(this.getRegistryManager().getOps(NbtOps.INSTANCE), nbt).ifSuccess(this::setVariant);
        this.facing = Direction.fromHorizontal(nbt.getByte("facing"));
        this.setFacing(this.facing);
    }
}
