package fi.dy.masa.malilib.util.position;

import javax.annotation.Nullable;

import org.jspecify.annotations.NonNull;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public record HitResult(Type type, @Nullable BlockPos blockPos,
                        @Nullable Direction side, @Nullable Vec3d pos,
                        @Nullable Entity entity)
{
    public net.minecraft.world.phys.HitResult toVanilla()
    {
	    return switch (this.type)
	    {
		    case BLOCK ->
				    new BlockHitResult(this.pos.toVanilla(), this.side.getVanillaDirection(), this.blockPos.toVanillaPos(), false);
		    case ENTITY ->
                    new EntityHitResult(this.entity, this.pos.toVanilla());
		    default ->
				    BlockHitResult.miss(Vec3.ZERO, Direction.DOWN.getVanillaDirection(), net.minecraft.core.BlockPos.ZERO);
	    };
    }

    @Override
    public @NonNull String toString()
    {
        return "HitResult{type=" + this.type + ", blockPos=" + this.blockPos + ", side=" + this.side +
                ", pos=" + this.pos + ", entity=" + this.entity + "}";
    }

    public enum Type
    {
        MISS,
        BLOCK,
        ENTITY;
    }

    public static HitResult miss()
    {
        return new HitResult(Type.MISS, null, null, null, null);
    }

    public static HitResult block(BlockPos pos, Direction side, Vec3d exactPos)
    {
        return new HitResult(Type.BLOCK, pos, side, exactPos, null);
    }

    public static HitResult entity(Entity entity, Vec3d exactPos)
    {
        return new HitResult(Type.ENTITY, null, null, exactPos, entity);
    }

    public static HitResult of(@Nullable net.minecraft.world.phys.HitResult trace)
    {
        if (trace == null)
        {
            return miss();
        }

        return switch (trace.getType())
        {
            case BLOCK ->
            {
                BlockHitResult bhr = (BlockHitResult) trace;
                yield block(BlockPos.of(bhr.getBlockPos()), Direction.of(bhr.getDirection()), Vec3d.of(bhr.getLocation()));
            }
            case ENTITY ->
            {
                EntityHitResult ehr = (EntityHitResult) trace;
                yield entity(ehr.getEntity(), Vec3d.of(ehr.getLocation()));
            }
            default -> miss();
        };
    }
}
