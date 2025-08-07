package malilib.util.game;

import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import malilib.util.MathUtils;
import malilib.util.game.wrap.EntityWrap;
import malilib.util.position.BlockPos;
import malilib.util.position.Direction;
import malilib.util.position.HitResult;
import malilib.util.position.LayerRange;
import malilib.util.position.Vec3d;

public class RayTraceUtils
{
    /**
     * Get a ray trace from the point of view of the given entity (along its look vector)
     * @param world the world in which the ray trace is performed
     * @param entity the entity from whose view point the ray trace is performed
     * @param fluidHandling determines if the ray trace should hit fluid blocks
     * @param includeEntities determines if the ray trace should include entities, or only blocks
     * @param range the maximum distance to ray trace from the entity's eye position
     * @return the trace result, with type = MISS if the trace didn't hit anything
     */
    public static HitResult getRayTraceFromEntity(World world, Entity entity,
                                                  RayTraceFluidHandling fluidHandling,
                                                  boolean includeEntities, double range)
    {
        Vec3d eyesPos = EntityWrap.getEntityEyePos(entity);
        Vec3d rangedLook = EntityWrap.getScaledLookVector(entity, range);
        Vec3d lookEndPos = eyesPos.add(rangedLook);

        HitResult result = rayTraceBlocks(world, eyesPos, lookEndPos, fluidHandling, false, false, null, 1000);

        if (includeEntities)
        {
            AxisAlignedBB bb = entity.getEntityBoundingBox()
                                .expand(rangedLook.x, rangedLook.y, rangedLook.z).expand(1d, 1d, 1d);
            List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(entity, bb);

            double closest = result != null && result.type == HitResult.Type.BLOCK ?
                             eyesPos.getSquaredDistanceTo(result.pos) : Double.MAX_VALUE;
            RayTraceResult entityTrace = null;
            Entity targetEntity = null;

            for (Entity entityTmp : list)
            {
                bb = entityTmp.getEntityBoundingBox();
                RayTraceResult traceTmp = bb.calculateIntercept(eyesPos.toVanilla(), lookEndPos.toVanilla());

                if (traceTmp != null)
                {
                    double distance = eyesPos.getSquaredDistanceTo(traceTmp.hitVec);

                    if (distance < closest)
                    {
                        targetEntity = entityTmp;
                        entityTrace = traceTmp;
                        closest = distance;
                    }
                }
            }

            if (targetEntity != null)
            {
                result = HitResult.entity(targetEntity, Vec3d.of(entityTrace.hitVec));
            }
        }

        if (result == null || eyesPos.getDistanceTo(result.pos) > range)
        {
            result = HitResult.miss();
        }

        return result;
    }

    /**
     * Ray trace to blocks along the given vector
     * @param world
     * @param start The start position of the trace
     * @param end The end position of the trace
     * @param fluidMode Whether or not to trace to fluids
     * @param ignoreNonCollidable If true, then blocks without a hard collision box are ignored
     * @param returnLastUncollidableBlock If true, then the last block position without a hard collision
     *                                    box is returned, if no other blocks were hit
     * @param layerRange The LayerRange within which to ray trace. Set to null if the trace should
     *                   not care about layer ranges.
     * @param maxSteps the maximum number of advance loops. Should be larger than the maximum
     *                 requested trace length in blocks.
     * @return the ray trace result, or null if the trace didn't hit any blocks
     */
    @Nullable
    public static HitResult rayTraceBlocks(World world, Vec3d start, Vec3d end, RayTraceFluidHandling fluidMode,
                                           boolean ignoreNonCollidable, boolean returnLastUncollidableBlock,
                                           @Nullable LayerRange layerRange, int maxSteps)
    {
        return rayTraceBlocks(world, start, end, RayTraceCalculationData::checkRayCollision, fluidMode, BLOCK_FILTER_ANY,
                ignoreNonCollidable, returnLastUncollidableBlock, layerRange, maxSteps);
    }

    /**
     * Ray trace to blocks along the given vector
     * @param world
     * @param start The start position of the trace
     * @param end The end position of the trace
     * @param fluidMode Whether or not to trace to fluids
     * @param blockFilter A test to check if the block is valid for a hit result
     * @param ignoreNonCollidable If true, then blocks without a hard collision box are ignored
     * @param returnLastUncollidableBlock If true, then the last block position without a hard collision box is returned, if no other blocks were hit
     * @param layerRange The LayerRange within which to ray trace. Set to null if the trace should not care about layer ranges.
     * @param maxSteps the maximum number of advance loops. Should be larger than the maximum desired maximum ray trace length in blocks.
     * @return the ray trace result, or null if the trace didn't hit any blocks
     */
    @Nullable
    public static HitResult rayTraceBlocks(World world, Vec3d start, Vec3d end, IRayPositionHandler handler,
                                           RayTraceFluidHandling fluidMode, BlockStatePredicate blockFilter,
                                           boolean ignoreNonCollidable, boolean returnLastUncollidableBlock,
                                           @Nullable LayerRange layerRange, int maxSteps)
    {
        if (Double.isNaN(start.x) || Double.isNaN(start.y) || Double.isNaN(start.z) ||
            Double.isNaN(end.x) || Double.isNaN(end.y) || Double.isNaN(end.z))
        {
            return null;
        }

        RayTraceCalculationData data = new RayTraceCalculationData(start, end, fluidMode, blockFilter, layerRange);

        while (--maxSteps >= 0)
        {
            if (handler.handleRayTracePosition(data, world, ignoreNonCollidable))
            {
                //System.out.printf("checkCollision() - steps: %d, trace: %s\n", maxSteps, data.trace);
                return HitResult.of(data.trace);
            }

            if (rayTraceAdvance(data))
            {
                //System.out.printf("rayTraceStep() - steps: %d, trace: %s\n", maxSteps, data.trace);
                break;
            }
        }

        if (returnLastUncollidableBlock)
        {
            Vec3d pos = new Vec3d(data.currentX, data.currentY, data.currentZ);
            return new HitResult(HitResult.Type.MISS, data.mutablePos.toImmutable(), data.facing, pos, null);
        }

        return null;
    }

    public static boolean checkRayCollision(RayTraceCalculationData data, World world, boolean ignoreNonCollidable)
    {
        if (data.isPositionWithinRange())
        {
            IBlockState state = world.getBlockState(data.mutablePos);

            if (data.isValidBlock(state) &&
                ((ignoreNonCollidable == false && state.getMaterial() != Material.AIR)
                    || state.getCollisionBoundingBox(world, data.mutablePos) != Block.NULL_AABB))
            {
                if (state.getBlock().canCollideCheck(state, false) || data.fluidMode.handled(state))
                {
                    RayTraceResult traceTmp = state.collisionRayTrace(world, data.mutablePos.toImmutable(),
                                                                      data.start.toVanilla(), data.end.toVanilla());

                    if (traceTmp != null)
                    {
                        data.trace = traceTmp;
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /*// 1.15.2 version
    @Nullable
    private static boolean traceLoopSteps(RayTraceCalcsData data, World world,
            BlockState blockState, FluidState fluidState, boolean ignoreBlockWithoutBoundingBox)
    {
        if (data.isPositionWithinRange() &&
            (ignoreBlockWithoutBoundingBox == false || blockState.getMaterial() == Material.PORTAL ||
             blockState.getCollisionShape(world, data.blockPos).isEmpty() == false))
        {
            VoxelShape blockShape = blockState.getOutlineShape(world, data.blockPos);
            boolean blockCollidable = ! blockShape.isEmpty();
            boolean fluidCollidable = data.fluidMode.handled(blockState);

            if (blockCollidable == false && fluidCollidable == false)
            {
                Vec3d pos = new Vec3d(data.currentX, data.currentY, data.currentZ);
                data.trace = BlockHitResult.createMissed(pos, data.facing, data.blockPos);
            }
            else
            {
                BlockHitResult traceTmp = null;

                if (blockCollidable)
                {
                    traceTmp = blockShape.rayTrace(data.start, data.end, data.blockPos);
                }

                if (traceTmp == null && fluidCollidable)
                {
                    traceTmp = fluidState.getShape(world, data.blockPos).rayTrace(data.start, data.end, data.blockPos);
                }

                if (traceTmp != null)
                {
                    data.trace = traceTmp;
                    return true;
                }
            }
        }

        return false;
    }
    */

    public static boolean rayTraceAdvance(RayTraceCalculationData data)
    {
        boolean hasDistToEndX = true;
        boolean hasDistToEndY = true;
        boolean hasDistToEndZ = true;
        double nextX = 999.0D;
        double nextY = 999.0D;
        double nextZ = 999.0D;

        if (Double.isNaN(data.currentX) || Double.isNaN(data.currentY) || Double.isNaN(data.currentZ))
        {
            data.trace = null;
            return true;
        }

        if (data.blockX == data.endBlockX && data.blockY == data.endBlockY && data.blockZ == data.endBlockZ)
        {
            return true;
        }

        if (data.endBlockX > data.blockX)
        {
            nextX = data.blockX + 1.0D;
        }
        else if (data.endBlockX < data.blockX)
        {
            nextX = data.blockX + 0.0D;
        }
        else
        {
            hasDistToEndX = false;
        }

        if (data.endBlockY > data.blockY)
        {
            nextY = data.blockY + 1.0D;
        }
        else if (data.endBlockY < data.blockY)
        {
            nextY = data.blockY + 0.0D;
        }
        else
        {
            hasDistToEndY = false;
        }

        if (data.endBlockZ > data.blockZ)
        {
            nextZ = data.blockZ + 1.0D;
        }
        else if (data.endBlockZ < data.blockZ)
        {
            nextZ = data.blockZ + 0.0D;
        }
        else
        {
            hasDistToEndZ = false;
        }

        double relStepX = 999.0D;
        double relStepY = 999.0D;
        double relStepZ = 999.0D;
        double distToEndX = data.end.x - data.currentX;
        double distToEndY = data.end.y - data.currentY;
        double distToEndZ = data.end.z - data.currentZ;

        if (hasDistToEndX)
        {
            relStepX = (nextX - data.currentX) / distToEndX;
        }

        if (hasDistToEndY)
        {
            relStepY = (nextY - data.currentY) / distToEndY;
        }

        if (hasDistToEndZ)
        {
            relStepZ = (nextZ - data.currentZ) / distToEndZ;
        }

        if (relStepX == -0.0D)
        {
            relStepX = -1.0E-4D;
        }

        if (relStepY == -0.0D)
        {
            relStepY = -1.0E-4D;
        }

        if (relStepZ == -0.0D)
        {
            relStepZ = -1.0E-4D;
        }

        if (relStepX < relStepY && relStepX < relStepZ)
        {
            data.facing = data.endBlockX > data.blockX ? Direction.WEST : Direction.EAST;
            data.currentX = nextX;
            data.currentY += distToEndY * relStepX;
            data.currentZ += distToEndZ * relStepX;
        }
        else if (relStepY < relStepZ)
        {
            data.facing = data.endBlockY > data.blockY ? Direction.DOWN : Direction.UP;
            data.currentX += distToEndX * relStepY;
            data.currentY = nextY;
            data.currentZ += distToEndZ * relStepY;
        }
        else
        {
            data.facing = data.endBlockZ > data.blockZ ? Direction.NORTH : Direction.SOUTH;
            data.currentX += distToEndX * relStepZ;
            data.currentY += distToEndY * relStepZ;
            data.currentZ = nextZ;
        }

        int x = MathUtils.floor(data.currentX) - (data.facing == Direction.EAST  ? 1 : 0);
        int y = MathUtils.floor(data.currentY) - (data.facing == Direction.UP    ? 1 : 0);
        int z = MathUtils.floor(data.currentZ) - (data.facing == Direction.SOUTH ? 1 : 0);
        data.setBlockPos(x, y, z);

        return false;
    }

    public static class RayTraceCalculationData
    {
        @Nullable protected final LayerRange range;
        public final RayTraceFluidHandling fluidMode;
        public final BlockStatePredicate blockFilter;
        public final BlockPos.MutBlockPos mutablePos = new BlockPos.MutBlockPos();
        public final Vec3d start;
        public final Vec3d end;
        public final int endBlockX;
        public final int endBlockY;
        public final int endBlockZ;
        public int blockX;
        public int blockY;
        public int blockZ;
        public double currentX;
        public double currentY;
        public double currentZ;
        public Direction facing;
        @Nullable public RayTraceResult trace;

        public RayTraceCalculationData(Vec3d start, Vec3d end, RayTraceFluidHandling fluidMode,
                                       BlockStatePredicate blockFilter, @Nullable LayerRange range)
        {
            this.start = start;
            this.end = end;
            this.fluidMode = fluidMode;
            this.blockFilter = blockFilter;
            this.range = range;
            this.currentX = start.x;
            this.currentY = start.y;
            this.currentZ = start.z;
            this.endBlockX = MathUtils.floor(end.x);
            this.endBlockY = MathUtils.floor(end.y);
            this.endBlockZ = MathUtils.floor(end.z);
            this.setBlockPos(MathUtils.floor(start.x), MathUtils.floor(start.y), MathUtils.floor(start.z));
        }

        public void setBlockPos(int x, int y, int z)
        {
            this.blockX = x;
            this.blockY = y;
            this.blockZ = z;
            this.mutablePos.set(this.blockX, this.blockY, this.blockZ);
        }

        public boolean isValidBlock(IBlockState state)
        {
            return this.blockFilter.test(state);
        }

        public boolean isPositionWithinRange()
        {
            return this.range == null || this.range.isPositionWithinRange(this.blockX, this.blockY, this.blockZ);
        }

        public boolean checkRayCollision(World world, boolean ignoreNonCollidable)
        {
            if (this.isPositionWithinRange() == false)
            {
                return false;
            }

            IBlockState state = world.getBlockState(this.mutablePos);

            if (state.getMaterial() == Material.AIR ||
                this.isValidBlock(state) == false ||
                (ignoreNonCollidable == false && state.getCollisionBoundingBox(world, this.mutablePos) == Block.NULL_AABB))
            {
                return false;
            }

            if (state.getBlock().canCollideCheck(state, false) || this.fluidMode.handled(state))
            {
                RayTraceResult traceTmp = state.collisionRayTrace(world, this.mutablePos,
                                                                  this.start.toVanilla(), this.end.toVanilla());

                if (traceTmp != null)
                {
                    this.trace = traceTmp;
                    return true;
                }
            }

            return false;
        }
    }

    public static final BlockStatePredicate BLOCK_FILTER_ANY = (state) -> true;
    public static final BlockStatePredicate BLOCK_FILTER_NON_AIR = (state) -> state.getMaterial() != Material.AIR;

    public enum RayTraceFluidHandling
    {
        NONE((blockState) -> BlockUtils.isFluidBlock(blockState) == false),
        SOURCE_ONLY(BlockUtils::isFluidSourceBlock),
        ANY(BlockUtils::isFluidBlock);

        private final BlockStatePredicate predicate;

        RayTraceFluidHandling(BlockStatePredicate predicate)
        {
            this.predicate = predicate;
        }

        public boolean handled(IBlockState blockState)
        {
            return this.predicate.test(blockState);
        }
     }

    public interface IRayPositionHandler
    {
        /**
         * A handler method, usually for checking for a collision at the given position along the ray trace
         * @return true if the ray should stop here and the current trace result from the RayTraceCalcsData should be returned
         */
        boolean handleRayTracePosition(RayTraceCalculationData data, World world, boolean ignoreNonCollidable);
    }

    public interface BlockStatePredicate
    {
        boolean test(IBlockState state);
    }
}
