package fi.dy.masa.malilib.util.position;

import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

public enum Coordinate
{
    X((v) -> v.x, Vec3i::getX, Vec2d::getX, Vec2i::getX, (n, o) -> new Vec3d(n  , o.y, o.z), (n, o) -> new Vec3i(n       , o.getY(), o.getZ()), (n, o) -> new BlockPos(n       , o.getY(), o.getZ()), (n, o) -> new Vec2d(n, o.y), (n, o) -> new Vec2i(n, o.y)),
    Y((v) -> v.y, Vec3i::getY, Vec2d::getY, Vec2i::getY, (n, o) -> new Vec3d(o.x, n  , o.z), (n, o) -> new Vec3i(o.getX(), n       , o.getZ()), (n, o) -> new BlockPos(o.getX(), n       , o.getZ()), (n, o) -> new Vec2d(o.x, n), (n, o) -> new Vec2i(o.x, n)),
    Z((v) -> v.z, Vec3i::getZ, v -> 0     , v -> 0     , (n, o) -> new Vec3d(o.x, o.y, n  ), (n, o) -> new Vec3i(o.getX(), o.getY(), n       ), (n, o) -> new BlockPos(o.getX(), o.getY(), n       ), (n, o) -> o                , (n, o) -> o);

    private final ToDoubleFunction<Vec2d> vec2dToDoubleFunction;
    private final ToDoubleFunction<Vec3d> vec3dToDoubleFunction;
    private final ToIntFunction<Vec2i> vec2iToIntFunction;
    private final ToIntFunction<Vec3i> vec3iToIntFunction;
    private final BlockPosModifier blockPosModifier;
    private final Vec3dModifier vec3dModifier;
    private final Vec3iModifier vec3iModifier;
    private final Vec2dModifier vec2dModifier;
    private final Vec2iModifier vec2iModifier;

    Coordinate(ToDoubleFunction<Vec3d> vec3dToDoubleFunction,
               ToIntFunction<Vec3i> vec3iToIntFunction,
               ToDoubleFunction<Vec2d> vec2dToDoubleFunction,
               ToIntFunction<Vec2i> vec2iToIntFunction,
               Vec3dModifier vec3dModifier,
               Vec3iModifier vec3iModifier,
               BlockPosModifier blockPosModifier,
               Vec2dModifier vec2dModifier,
               Vec2iModifier vec2iModifier)
    {
        this.vec2dToDoubleFunction = vec2dToDoubleFunction;
        this.vec2iToIntFunction = vec2iToIntFunction;
        this.vec3dToDoubleFunction = vec3dToDoubleFunction;
        this.vec3iToIntFunction = vec3iToIntFunction;
        this.vec3dModifier = vec3dModifier;
        this.vec3iModifier = vec3iModifier;
        this.blockPosModifier = blockPosModifier;
        this.vec2dModifier = vec2dModifier;
        this.vec2iModifier = vec2iModifier;
    }

    public int asInt(Vec2i pos)
    {
        return this.vec2iToIntFunction.applyAsInt(pos);
    }

    public int asInt(Vec3i pos)
    {
        return this.vec3iToIntFunction.applyAsInt(pos);
    }

    public double asDouble(Vec2d pos)
    {
        return this.vec2dToDoubleFunction.applyAsDouble(pos);
    }

    public double asDouble(Vec3d pos)
    {
        return this.vec3dToDoubleFunction.applyAsDouble(pos);
    }

    public Vec2d modifyVec2d(double newValue, Vec2d oldVec)
    {
        return this.vec2dModifier.modify(newValue, oldVec);
    }

    public Vec2i modifyVec2i(int newValue, Vec2i oldVec)
    {
        return this.vec2iModifier.modify(newValue, oldVec);
    }

    public Vec3d modifyVec3d(double newValue, Vec3d oldVec)
    {
        return this.vec3dModifier.modify(newValue, oldVec);
    }

    public Vec3i modifyVec3i(int newValue, Vec3i oldVec)
    {
        return this.vec3iModifier.modify(newValue, oldVec);
    }

    public BlockPos modifyBlockPos(int newValue, Vec3i oldVec)
    {
        return this.blockPosModifier.modify(newValue, oldVec);
    }

    public Vec2d offsetVec2d(double offset, Vec2d oldVec)
    {
        double newValue = this.vec2dToDoubleFunction.applyAsDouble(oldVec) + offset;
        return this.vec2dModifier.modify(newValue, oldVec);
    }

    public Vec2i offsetVec2i(int offset, Vec2i oldVec)
    {
        int newValue = this.vec2iToIntFunction.applyAsInt(oldVec) + offset;
        return this.vec2iModifier.modify(newValue, oldVec);
    }

    public Vec3i offsetVec3i(int offset, Vec3i oldVec)
    {
        int newValue = this.vec3iToIntFunction.applyAsInt(oldVec) + offset;
        return this.vec3iModifier.modify(newValue, oldVec);
    }

    public Vec3d offsetVec3d(double offset, Vec3d oldVec)
    {
        double newValue = this.vec3dToDoubleFunction.applyAsDouble(oldVec) + offset;
        return this.vec3dModifier.modify(newValue, oldVec);
    }

    public BlockPos offsetBlockPos(int offset, BlockPos oldPos)
    {
        int newValue = this.vec3iToIntFunction.applyAsInt(oldPos) + offset;
        return this.blockPosModifier.modify(newValue, oldPos);
    }

    public String asIntString(Vec3i pos)
    {
        return String.valueOf(this.asInt(pos));
    }

    public String asDoubleString(Vec3d pos)
    {
        return String.valueOf(this.asDouble(pos));
    }

    public interface Vec3iModifier
    {
        Vec3i modify(int newValue, Vec3i oldVec);
    }

    public interface Vec3dModifier
    {
        Vec3d modify(double newValue, Vec3d oldVec);
    }

    public interface BlockPosModifier
    {
        BlockPos modify(int newValue, Vec3i oldPos);
    }

    public interface Vec2iModifier
    {
        Vec2i modify(int newValue, Vec2i oldVec);
    }

    public interface Vec2dModifier
    {
        Vec2d modify(double newValue, Vec2d oldVec);
    }
}
