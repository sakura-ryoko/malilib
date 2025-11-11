package fi.dy.masa.malilib.util.position;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

/**
 * Post-ReWrite code
 */
public class PositionUtils
{
    public static final Direction[] ALL_DIRECTIONS = new Direction[] { Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST };
    public static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[] { Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST };
    public static final Direction[] VERTICAL_DIRECTIONS = new Direction[] { Direction.DOWN, Direction.UP };

    public static final int SIZE_BITS_X = 25 + 1; // minimum bits for 30M + sign bit
    public static final int SIZE_BITS_Z = SIZE_BITS_X;
    public static final int SIZE_BITS_Y = 64 - SIZE_BITS_X - SIZE_BITS_Z;
    public static final long BITMASK_X = (1L << SIZE_BITS_X) - 1L;
    public static final long BITMASK_Y = (1L << SIZE_BITS_Y) - 1L;
    public static final long BITMASK_Z = (1L << SIZE_BITS_Z) - 1L;
    public static final int BIT_SHIFT_Z = 0;
    public static final int BIT_SHIFT_Y = SIZE_BITS_Z;
    public static final int BIT_SHIFT_X = SIZE_BITS_Y + SIZE_BITS_Z;

    public static long blockPosToLong(int x, int y, int z)
    {
        return (((long) x & BITMASK_X) << BIT_SHIFT_X) | (((long) y & BITMASK_Y) << BIT_SHIFT_Y) | (((long) z & BITMASK_Z) << BIT_SHIFT_Z);
    }

    public static int unpackX(long packedPos)
    {
        return (int) (packedPos << (64 - BIT_SHIFT_X - SIZE_BITS_X) >> (64 - SIZE_BITS_X));
    }

    public static int unpackY(long packedPos)
    {
        return (int) (packedPos << (64 - BIT_SHIFT_Y - SIZE_BITS_Y) >> (64 - SIZE_BITS_Y));
    }

    public static int unpackZ(long packedPos)
    {
        return (int) (packedPos << (64 - BIT_SHIFT_Z - SIZE_BITS_Z) >> (64 - SIZE_BITS_Z));
    }

    public static int getPackedChunkRelativePosition(BlockPos pos)
    {
        return (pos.getY() << 8) | ((pos.getZ() & 0xF) << 4) | (pos.getX() & 0xF);
    }

    public static long getPackedAbsolutePosition(long chunkPos, int chunkRelativeBlockPos)
    {
        int chunkX = (int) chunkPos;
        int chunkZ = (int) (chunkPos >> 32L);
        int x = (chunkX << 4) + (chunkRelativeBlockPos & 0xF);
        int y = chunkRelativeBlockPos >> 8;
        int z = (chunkZ << 4) + ((chunkRelativeBlockPos >> 4) & 0xF);

        return blockPosToLong(x, y, z);
    }

    public static int getChunkPosX(long chunkPosLong)
    {
        return (int) chunkPosLong;
    }

    public static int getChunkPosZ(long chunkPosLong)
    {
        return (int) (chunkPosLong >> 32);
    }

    public static ChunkPos chunkPosFromLong(long chunkPosLong)
    {
        return new ChunkPos(getChunkPosX(chunkPosLong), getChunkPosZ(chunkPosLong));
    }

    public static BlockPos getMinCorner(BlockPos pos1, BlockPos pos2)
    {
        return new BlockPos(Math.min(pos1.getX(), pos2.getX()),
                            Math.min(pos1.getY(), pos2.getY()),
                            Math.min(pos1.getZ(), pos2.getZ()));
    }

    public static BlockPos getMinCorner(BlockPos pos1, BlockPos pos2, BlockPos pos3)
    {
        return new BlockPos(Math.min(pos1.getX(), Math.min(pos2.getX(), pos3.getX())),
                            Math.min(pos1.getY(), Math.min(pos2.getY(), pos3.getY())),
                            Math.min(pos1.getZ(), Math.min(pos2.getZ(), pos3.getZ())));
    }

    public static BlockPos getMaxCorner(BlockPos pos1, BlockPos pos2)
    {
        return new BlockPos(Math.max(pos1.getX(), pos2.getX()),
                            Math.max(pos1.getY(), pos2.getY()),
                            Math.max(pos1.getZ(), pos2.getZ()));
    }

    public static BlockPos getMaxCorner(BlockPos pos1, BlockPos pos2, BlockPos pos3)
    {
        return new BlockPos(Math.max(pos1.getX(), Math.max(pos2.getX(), pos3.getX())),
                            Math.max(pos1.getY(), Math.max(pos2.getY(), pos3.getY())),
                            Math.max(pos1.getZ(), Math.max(pos2.getZ(), pos3.getZ())));
    }

    public static boolean isPositionInsideArea(BlockPos pos, BlockPos posMin, BlockPos posMax)
    {
        return pos.getX() >= posMin.getX() && pos.getX() <= posMax.getX() &&
               pos.getY() >= posMin.getY() && pos.getY() <= posMax.getY() &&
               pos.getZ() >= posMin.getZ() && pos.getZ() <= posMax.getZ();
    }

    /**
     * Returns the closest side direction to the entity's yaw facing that is 90 degrees from the entity's forward direction
     */
    public static Direction getClosestSideDirection(Entity entity)
    {
        Direction forwardDirection = entity.getHorizontalFacing();
        float entityYaw = ((entity.getYaw() % 360.0F) + 360.0F) % 360.0F;
        float forwardYaw = forwardDirection.getPositiveHorizontalDegrees();

        if (entityYaw < forwardYaw || (forwardYaw == 0.0F && entityYaw > 270.0F))
        {
            return forwardDirection.rotateYCounterclockwise();
        }
        else
        {
            return forwardDirection.rotateYClockwise();
        }
    }

    /**
     * Returns the closest block position directly in front of the
     * given entity that is not colliding with it.
     */
    public static BlockPos getPositionInFrontOfEntity(Entity entity)
    {
        return getPositionInFrontOfEntity(entity, 60);
    }

    /**
     * Returns the closest block position directly in front of the
     * given entity that is not colliding with it.
     */
    public static BlockPos getPositionInFrontOfEntity(Entity entity, float verticalThreshold)
    {
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        float pitch = entity.getPitch();


        if (pitch >= verticalThreshold)
        {
            return BlockPos.ofFloored(x, y - 1.0, z);
        }
        else if (pitch <= -verticalThreshold)
        {
            return BlockPos.ofFloored(x, Math.ceil(entity.getBoundingBox().maxY), z);
        }

        double width = entity.getWidth();
        y = Math.floor(y + entity.getEyeHeight(EntityPose.STANDING));

	    return switch (entity.getHorizontalFacing())
	    {
		    case EAST -> new BlockPos((int) Math.ceil(x + width / 2), (int) y, (int) Math.floor(z));
		    case WEST -> new BlockPos((int) Math.floor(x - width / 2) - 1, (int) y, (int) Math.floor(z));
		    case SOUTH -> new BlockPos((int) Math.floor(x), (int) y, (int) Math.ceil(z + width / 2));
		    case NORTH -> new BlockPos((int) Math.floor(x), (int) y, (int) Math.floor(z - width / 2) - 1);
		    default -> BlockPos.ofFloored(x, y, z);
	    };

    }

    /**
     * Get the rotation that will go from facingOriginal to facingRotated, if possible.
     * If it's not possible to rotate between the given facings
     * (at least one of them is vertical, but they are not the same), then null is returned.
     */
    @Nullable
    public static BlockRotation getRotation(Direction directionFrom, Direction directionTo)
    {
        if (directionFrom == directionTo)
        {
            return BlockRotation.NONE;
        }

        if (directionFrom.getAxis() == Direction.Axis.Y || directionTo.getAxis() == Direction.Axis.Y)
        {
            return null;
        }

        if (directionTo == directionFrom.getOpposite())
        {
            return BlockRotation.CW_180;
        }

        return directionTo == directionFrom.rotateYClockwise() ? BlockRotation.CW_90 : BlockRotation.CCW_90;
    }

    /**
     * Returns the hit vector at the center point of the given side/face of the given block position.
     */
    public static Vec3d getHitVecCenter(BlockPos basePos, Direction facing)
    {
        int x = basePos.getX();
        int y = basePos.getY();
        int z = basePos.getZ();

	    return switch (facing)
	    {
		    case UP -> new Vec3d(x + 0.5, y + 1, z + 0.5);
		    case DOWN -> new Vec3d(x + 0.5, y, z + 0.5);
		    case NORTH -> new Vec3d(x + 0.5, y + 0.5, z);
		    case SOUTH -> new Vec3d(x + 0.5, y + 0.5, z + 1);
		    case WEST -> new Vec3d(x, y + 0.5, z);
		    case EAST -> new Vec3d(x + 1, y + 0.5, z + 1);
		    default -> new Vec3d(x, y, z);
	    };
    }

    /**
     * Returns the part of the block face the player is currently targeting.
     * The block face is divided into four side segments and a center segment.
     */
    public static HitPart getHitPart(Direction originalSide, Direction playerFacingH, BlockPos pos, Vec3d hitVec)
    {
        Vec3d positions = getHitPartPositions(originalSide, playerFacingH, pos, hitVec);
        double posH = positions.x;
        double posV = positions.y;
        double offH = Math.abs(posH - 0.5d);
        double offV = Math.abs(posV - 0.5d);

        if (offH > 0.25d || offV > 0.25d)
        {
            if (offH > offV)
            {
                return posH < 0.5d ? HitPart.LEFT : HitPart.RIGHT;
            }
            else
            {
                return posV < 0.5d ? HitPart.BOTTOM : HitPart.TOP;
            }
        }
        else
        {
            return HitPart.CENTER;
        }
    }

    private static Vec3d getHitPartPositions(Direction originalSide, Direction playerFacingH, BlockPos pos, Vec3d hitVec)
    {
        double x = hitVec.x - pos.getX();
        double y = hitVec.y - pos.getY();
        double z = hitVec.z - pos.getZ();
        double posH = 0;
        double posV = 0;

        switch (originalSide)
        {
            case DOWN:
            case UP:
                switch (playerFacingH)
                {
                    case NORTH:
                        posH = x;
                        posV = 1.0d - z;
                        break;
                    case SOUTH:
                        posH = 1.0d - x;
                        posV = z;
                        break;
                    case WEST:
                        posH = 1.0d - z;
                        posV = 1.0d - x;
                        break;
                    case EAST:
                        posH = z;
                        posV = x;
                        break;
                    default:
                }

                if (originalSide == Direction.DOWN)
                {
                    posV = 1.0d - posV;
                }

                break;
            case NORTH:
            case SOUTH:
                posH = originalSide.getDirection() == Direction.AxisDirection.POSITIVE ? x : 1.0d - x;
                posV = y;
                break;
            case WEST:
            case EAST:
                posH = originalSide.getDirection() == Direction.AxisDirection.NEGATIVE ? z : 1.0d - z;
                posV = y;
                break;
        }

        return new Vec3d(posH, posV, 0);
    }

    /**
     * Returns the direction the targeted part of the targeting overlay is pointing towards.
     */
    public static Direction getTargetedDirection(Direction side, Direction playerFacingH, BlockPos pos, Vec3d hitVec)
    {
        Vec3d positions = getHitPartPositions(side, playerFacingH, pos, hitVec);
        double posH = positions.x;
        double posV = positions.y;
        double offH = Math.abs(posH - 0.5d);
        double offV = Math.abs(posV - 0.5d);

        if (offH > 0.25d || offV > 0.25d)
        {
            if (side.getAxis() == Direction.Axis.Y)
            {
                if (offH > offV)
                {
                    return posH < 0.5d ? playerFacingH.rotateYCounterclockwise() : playerFacingH.rotateYClockwise();
                }
                else
                {
                    if (side == Direction.DOWN)
                    {
                        return posV > 0.5d ? playerFacingH.getOpposite() : playerFacingH;
                    }
                    else
                    {
                        return posV < 0.5d ? playerFacingH.getOpposite() : playerFacingH;
                    }
                }
            }
            else
            {
                if (offH > offV)
                {
                    return posH < 0.5d ? side.rotateYClockwise() : side.rotateYCounterclockwise();
                }
                else
                {
                    return posV < 0.5d ? Direction.DOWN : Direction.UP;
                }
            }
        }

        return side;
    }

    /**
     * Adjusts the (usually ray traced) position so that the provided entity
     * will not clip inside the presumable block side.
     */
    public static Vec3d adjustPositionToSideOfEntity(Vec3d pos, Entity entity, Direction side)
    {
        double x = pos.x;
        double y = pos.y;
        double z = pos.z;

        if (side == Direction.DOWN)
        {
            y -= entity.getHeight();
        }
        else if (side.getAxis().isHorizontal())
        {
            x += side.getOffsetX() * (entity.getWidth() / 2 + 1.0E-4D);
            z += side.getOffsetZ() * (entity.getWidth() / 2 + 1.0E-4D);
        }

        return new Vec3d(x, y, z);
    }

    public enum HitPart
    {
        CENTER,
        LEFT,
        RIGHT,
        BOTTOM,
        TOP
    }

    /**
     * Pre-ReWrite Functions
     */
    public static Vec3d modifyValue(CoordinateType type, Vec3d valueIn, double amount)
    {
	    return switch (type)
	    {
		    case X -> new Vec3d(valueIn.x + amount, valueIn.y, valueIn.z);
		    case Y -> new Vec3d(valueIn.x, valueIn.y + amount, valueIn.z);
		    case Z -> new Vec3d(valueIn.x, valueIn.y, valueIn.z + amount);
	    };

    }

    public static BlockPos modifyValue(CoordinateType type, BlockPos valueIn, int amount)
    {
	    return switch (type)
	    {
		    case X -> BlockPos.ofFloored(valueIn.getX() + amount, valueIn.getY(), valueIn.getZ());
		    case Y -> BlockPos.ofFloored(valueIn.getX(), valueIn.getY() + amount, valueIn.getZ());
		    case Z -> BlockPos.ofFloored(valueIn.getX(), valueIn.getY(), valueIn.getZ() + amount);
	    };

    }

    public static Vec3d setValue(CoordinateType type, Vec3d valueIn, double newValue)
    {
	    return switch (type)
	    {
		    case X -> new Vec3d(newValue, valueIn.y, valueIn.z);
		    case Y -> new Vec3d(valueIn.x, newValue, valueIn.z);
		    case Z -> new Vec3d(valueIn.x, valueIn.y, newValue);
	    };

    }

    public static BlockPos setValue(CoordinateType type, BlockPos valueIn, int newValue)
    {
	    return switch (type)
	    {
		    case X -> BlockPos.ofFloored(newValue, valueIn.getY(), valueIn.getZ());
		    case Y -> BlockPos.ofFloored(valueIn.getX(), newValue, valueIn.getZ());
		    case Z -> BlockPos.ofFloored(valueIn.getX(), valueIn.getY(), newValue);
	    };

    }

    /**
     * Returns the closest direction the given entity is looking towards,
     * with a vertical/pitch threshold of 60 degrees.
     * @param entity ()
     * @return ()
     */
    @Deprecated
    public static Direction getClosestLookingDirection(Entity entity)
    {
        return getClosestLookingDirection(entity, 60);
    }

    /**
     * Returns the closest direction the given entity is looking towards.
     * @param entity ()
     * @param verticalThreshold the pitch threshold to return the up or down facing instead of horizontals
     * @return ()
     */
    @Deprecated
    public static Direction getClosestLookingDirection(Entity entity, float verticalThreshold)
    {
        if (entity.getPitch() >= verticalThreshold)
        {
            return Direction.DOWN;
        }
        else if (entity.getYaw() <= -verticalThreshold)
        {
            return Direction.UP;
        }

        return entity.getHorizontalFacing();
    }

    public static BlockPos getEntityBlockPos(Entity entity)
    {
        return BlockPos.ofFloored(Math.floor(entity.getX()), Math.floor(entity.getY()), Math.floor(entity.getZ()));
    }

    public enum CoordinateType
    {
        X,
        Y,
        Z
    }
}
