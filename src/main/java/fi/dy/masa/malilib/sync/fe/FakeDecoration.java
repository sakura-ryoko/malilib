package fi.dy.masa.malilib.sync.fe;

import java.util.Objects;
import org.apache.commons.lang3.Validate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FakeDecoration extends FakeAttached implements IFakeDecoration
{
    protected Direction facing;

    public FakeDecoration(EntityType<?> type, World world, int entityId)
    {
        super(type, world, entityId);
        this.facing = Direction.SOUTH;
    }

    public FakeDecoration(EntityType<?> type, World world, int entityId, BlockPos pos)
    {
        this(type, world, entityId);
        this.attachedBlockPos = pos;
    }

    public FakeDecoration(Entity input)
    {
        super(input);

        if (input instanceof AbstractDecorationEntity ade)
        {
            this.facing = ade.getFacing();
            this.attachedBlockPos = ade.getAttachedBlockPos();
            this.readCustomDataFromNbt(this.getNbt());
        }
    }

    protected void setFacing(Direction facing)
    {
        Objects.requireNonNull(facing);
        Validate.isTrue(facing.getAxis().isHorizontal());
        this.facing = facing;
        this.updateAttachmentPosition();
    }

    @Override
    public void updateAttachmentPosition()
    {
        if (this.facing != null)
        {
            Box box = this.calculateBoundingBox(this.attachedBlockPos, this.facing);
            Vec3d vec3d = box.getCenter();
            this.setPos(vec3d);
            this.setBoundingBox(box);
        }
    }

    public Box calculateBoundingBox(BlockPos pos, Direction side)
    {
        return this.getBoundingBox();
    }

    public void onPlace()
    {
        // NO-OP
    }

    public boolean canStayAttached() { return true; }

    public Direction getHorizontalFacing() {
        return this.facing;
    }
}
