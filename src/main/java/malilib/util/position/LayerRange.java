package malilib.util.position;

import javax.annotation.Nullable;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.minecraft.entity.Entity;

import malilib.config.value.BaseOptionConfigValue;
import malilib.config.value.LayerMode;
import malilib.listener.LayerRangeChangeListener;
import malilib.overlay.message.MessageDispatcher;
import malilib.util.ListUtils;
import malilib.util.MathUtils;
import malilib.util.data.json.JsonUtils;
import malilib.util.game.wrap.EntityWrap;
import malilib.util.game.wrap.GameWrap;

public class LayerRange
{
    public static final int WORLD_VERTICAL_SIZE_MAX = 255;
    public static final int WORLD_VERTICAL_SIZE_MIN = 0;

    protected final LayerRangeChangeListener listener;
    protected LayerMode layerMode = LayerMode.ALL;
    protected Direction.Axis axis = Direction.Axis.Y;
    protected int layerSingle = 0;
    protected int layerAbove = 0;
    protected int layerBelow = 0;
    protected int layerRangeMin = 0;
    protected int layerRangeMax = 0;
    protected int playerFollowOffset = 0;
    protected boolean hotkeyRangeMin;
    protected boolean hotkeyRangeMax;
    protected boolean followPlayer;

    public LayerRange(LayerRangeChangeListener listener)
    {
        this.listener = listener;
    }

    public LayerMode getLayerMode()
    {
        return this.layerMode;
    }

    public Direction.Axis getAxis()
    {
        return this.axis;
    }

    public boolean shouldFollowPlayer()
    {
        return this.followPlayer;
    }

    public boolean getMoveLayerRangeMin()
    {
        return this.hotkeyRangeMin;
    }

    public boolean getMoveLayerRangeMax()
    {
        return this.hotkeyRangeMax;
    }

    public void setMoveLayerRangeMin(boolean move)
    {
        this.hotkeyRangeMin = move;
    }

    public void setMoveLayerRangeMax(boolean move)
    {
        this.hotkeyRangeMax = move;
    }

    public void toggleHotkeyMoveRangeMin()
    {
        this.hotkeyRangeMin = ! this.hotkeyRangeMin;
    }

    public void toggleHotkeyMoveRangeMax()
    {
        this.hotkeyRangeMax = ! this.hotkeyRangeMax;
    }

    public void toggleShouldFollowPlayer()
    {
        this.followPlayer = ! this.followPlayer;
    }

    public int getPlayerFollowOffset()
    {
        return this.playerFollowOffset;
    }

    public int getSingleLayer()
    {
        return this.layerSingle;
    }

    public int getAboveLayerBoundary()
    {
        return this.layerAbove;
    }

    public int getBelowLayerBoundary()
    {
        return this.layerBelow;
    }

    public int getLayerRangeMin()
    {
        return this.layerRangeMin;
    }

    public int getLayerRangeMax()
    {
        return this.layerRangeMax;
    }

    public int getMinLayerBoundary()
    {
        switch (this.layerMode)
        {
            case ALL:
            case ALL_BELOW:     return Integer.MIN_VALUE;
            case SINGLE_LAYER:  return this.layerSingle;
            case ALL_ABOVE:     return this.layerAbove;
            case LAYER_RANGE:   return this.layerRangeMin;
        }

        return 0;
    }

    public int getMaxLayerBoundary()
    {
        switch (this.layerMode)
        {
            case ALL:
            case ALL_ABOVE:     return Integer.MAX_VALUE;
            case SINGLE_LAYER:  return this.layerSingle;
            case ALL_BELOW:     return this.layerBelow;
            case LAYER_RANGE:   return this.layerRangeMax;
        }

        return 0;
    }

    public int getCurrentLayerValue(boolean isSecondValue)
    {
        switch (this.layerMode)
        {
            case SINGLE_LAYER:  return this.layerSingle;
            case ALL_ABOVE:     return this.layerAbove;
            case ALL_BELOW:     return this.layerBelow;
            case LAYER_RANGE:   return isSecondValue ? this.layerRangeMax : this.layerRangeMin;
            default:            return 0;
        }
    }

    public void cycleLayerMode(boolean reverse)
    {
        this.setLayerMode(ListUtils.getNextEntry(LayerMode.VALUES, this.layerMode, reverse));
    }

    public void setLayerMode(LayerMode mode)
    {
        this.setLayerMode(mode, true);
    }

    public void setLayerMode(LayerMode mode, boolean printMessage)
    {
        this.layerMode = mode;
        this.listener.updateAll();

        if (printMessage)
        {
            this.sendMessage("malilib.message.info.set_layer_mode_to", mode.getDisplayName());
        }
    }

    public void cycleAxis(boolean reverse)
    {
        Direction.Axis axis = this.axis;
        int index = axis.ordinal();
        int next = reverse ? (index == 0 ? 2 : index - 1) : ((index + 1) % 3);
        axis = Direction.Axis.values()[next % 3];
        this.setAxis(axis);
    }

    public void setAxis(Direction.Axis axis)
    {
        this.axis = axis;
        this.listener.updateAll();
        this.sendMessage("malilib.message.info.set_layer_axis_to", axis.getName());
    }

    public void setPlayerFollowOffset(int offset)
    {
        this.playerFollowOffset = offset;
    }

    public void setLayerSingle(int layer)
    {
        int old = this.layerSingle;
        //layer = this.getWorldLimitsClampedValue(layer, worldLimits);

        if (layer != old)
        {
            this.layerSingle = layer;
            this.updateLayersBetween(old, old);
            this.updateLayersBetween(layer, layer);
        }
    }

    public void setLayerAbove(int layer)
    {
        int old = this.layerAbove;
        //layer = this.getWorldLimitsClampedValue(layer, worldLimits);

        if (layer != old)
        {
            this.layerAbove = layer;
            this.updateLayersBetween(old, layer);
        }
    }

    public void setLayerBelow(int layer)
    {
        int old = this.layerBelow;
        //layer = this.getWorldLimitsClampedValue(layer, worldLimits);

        if (layer != old)
        {
            this.layerBelow = layer;
            this.updateLayersBetween(old, layer);
        }
    }

    public boolean setLayerRangeMin(int layer)
    {
        return this.setLayerRangeMin(layer, false);
    }

    public boolean setLayerRangeMax(int layer)
    {
        return this.setLayerRangeMax(layer, false);
    }

    protected boolean setLayerRangeMin(int layer, boolean force)
    {
        int old = this.layerRangeMin;
        //layer = this.getWorldLimitsClampedValue(layer, worldLimits);

        if (force == false)
        {
            layer = Math.min(layer, this.layerRangeMax);
        }

        if (layer != old)
        {
            this.layerRangeMin = layer;
            this.updateLayersBetween(old, layer);
        }

        return layer != old;
    }

    protected boolean setLayerRangeMax(int layer, boolean force)
    {
        int old = this.layerRangeMax;
        //layer = this.getWorldLimitsClampedValue(layer, worldLimits);

        if (force == false)
        {
            layer = Math.max(layer, this.layerRangeMin);
        }

        if (layer != old)
        {
            this.layerRangeMax = layer;
            this.updateLayersBetween(old, layer);
        }

        return layer != old;
    }

    protected int getPositionFromEntity(Entity entity)
    {
        switch (this.axis)
        {
            case X: return MathUtils.floor(EntityWrap.getX(entity));
            case Y: return MathUtils.floor(EntityWrap.getY(entity));
            case Z: return MathUtils.floor(EntityWrap.getZ(entity));
        }

        return 0;
    }

    public void setToPosition(Entity entity)
    {
        if (this.layerMode == LayerMode.LAYER_RANGE)
        {
            this.setLayerRangeToPosition(entity);
        }
        else
        {
            this.setSingleBoundaryToPosition(entity);
        }
    }

    public void setSingleBoundaryToPosition(Entity entity)
    {
        int pos = this.getPositionFromEntity(entity);
        this.setSingleBoundaryToPosition(pos);
    }

    public void setSingleBoundaryToPosition(int pos)
    {
        switch (this.layerMode)
        {
            case SINGLE_LAYER:
                this.setLayerSingle(pos);
                break;
            case ALL_ABOVE:
                this.setLayerAbove(pos);
                break;
            case ALL_BELOW:
                this.setLayerBelow(pos);
                break;
            default:
        }
    }

    public void setLayerRangeToPosition(Entity entity)
    {
        int pos = this.getPositionFromEntity(entity);
        this.setLayerRangeToPosition(pos);
    }

    public void setLayerRangeToPosition(int pos)
    {
        int oldMin = this.layerRangeMin;
        int oldMax = this.layerRangeMax;

        this.layerRangeMin = pos;
        this.layerRangeMax = pos;

        this.updateLayersBetween(oldMin, oldMax);
        this.updateLayersBetween(pos, pos);
    }

    public void followPlayerIfEnabled(Entity entity)
    {
        if (this.followPlayer)
        {
            int newPos = this.getPositionFromEntity(entity) + this.playerFollowOffset;

            if (this.layerMode == LayerMode.LAYER_RANGE)
            {
                int rangeSize = this.layerRangeMax - this.layerRangeMin;

                if (this.layerRangeIsMinClosest(entity))
                {
                    this.setLayerRangeMax(newPos + rangeSize, true);
                    this.setLayerRangeMin(newPos, true);
                }
                else
                {
                    this.setLayerRangeMin(newPos - rangeSize, true);
                    this.setLayerRangeMax(newPos, true);
                }
            }
            else
            {
                this.setSingleBoundaryToPosition(newPos);
            }
        }
    }

    public void markVisibleLayersForRenderUpdate(IntBoundingBox limits)
    {
        int val1;
        int val2;

        switch (this.layerMode)
        {
            case ALL:
                this.listener.updateAll();
                return;
            case SINGLE_LAYER:
            {
                val1 = this.layerSingle;
                val2 = this.layerSingle;
                break;
            }
            case ALL_ABOVE:
            {
                val1 = this.layerAbove;
                val2 = limits.getMaxValueForAxis(this.axis);
                break;
            }
            case ALL_BELOW:
            {
                val1 = limits.getMinValueForAxis(this.axis);
                val2 = this.layerBelow;
                break;
            }
            case LAYER_RANGE:
            {
                val1 = this.layerRangeMin;
                val2 = this.layerRangeMax;
                break;
            }
            default:
                return;
        }

        this.updateLayersBetween(val1, val2);
    }

    protected void updateLayersBetween(int layer1, int layer2)
    {
        int layerMin = Math.min(layer1, layer2);
        int layerMax = Math.max(layer1, layer2);

        switch (this.axis)
        {
            case X:
                this.listener.updateBetweenX(layerMin, layerMax);
                break;
            case Y:
                this.listener.updateBetweenY(layerMin, layerMax);
                break;
            case Z:
                this.listener.updateBetweenZ(layerMin, layerMax);
                break;
        }
    }

    public boolean moveLayer(int amount)
    {
        String axisName = this.axis.getName().toLowerCase();

        switch (this.layerMode)
        {
            case ALL:
                return false;
            case SINGLE_LAYER:
            {
                this.setLayerSingle(this.layerSingle + amount);
                this.sendMessage("malilib.message.info.set_layer_to", axisName, this.layerSingle);
                break;
            }
            case ALL_ABOVE:
            {
                this.setLayerAbove(this.layerAbove + amount);
                this.sendMessage("malilib.message.info.moved_min_layer_to", axisName, this.layerAbove);
                break;
            }
            case ALL_BELOW:
            {
                this.setLayerBelow(this.layerBelow + amount);
                this.sendMessage("malilib.message.info.moved_max_layer_to", axisName, this.layerBelow);
                break;
            }
            case LAYER_RANGE:
            {
                Entity entity = GameWrap.getCameraEntity();

                if (entity != null)
                {
                    boolean minBoundaryClosest = this.layerRangeIsMinClosest(entity);
                    this.moveLayerRange(amount, minBoundaryClosest);
                }

                break;
            }
            default:
        }

        return true;
    }

    protected void moveLayerRange(int amount, boolean minBoundaryClosest)
    {
        boolean moveMin = this.getMoveMin(minBoundaryClosest);
        boolean moveMax = this.getMoveMax(minBoundaryClosest);
        boolean moved = false;
        boolean force = moveMin && moveMax;

        if (moveMin)
        {
            moved |= this.setLayerRangeMin(this.layerRangeMin + amount, force);
        }

        if (moveMax)
        {
            moved |= this.setLayerRangeMax(this.layerRangeMax + amount, force);
        }

        if (moved)
        {
            String axisName = this.axis.getName().toLowerCase();

            if (moveMin && moveMax)
            {
                this.sendMessage("malilib.message.info.moved_layer_range", amount, axisName);
            }
            else
            {
                if (moveMin)
                {
                    this.sendMessage("malilib.message.info.moved_layer_range_min_boundary", amount, axisName);
                }
                else
                {
                    this.sendMessage("malilib.message.info.moved_layer_range_max_boundary", amount, axisName);
                }
            }
        }
    }

    protected boolean getMoveMax(boolean minBoundaryClosest)
    {
        return this.hotkeyRangeMax || (minBoundaryClosest == false && this.hotkeyRangeMin == false);
    }

    protected boolean getMoveMin(boolean minBoundaryClosest)
    {
        return this.hotkeyRangeMin || (minBoundaryClosest && this.hotkeyRangeMax == false);
    }

    protected boolean layerRangeIsMinClosest(Entity entity)
    {
        double x = EntityWrap.getX(entity);
        double y = EntityWrap.getY(entity);
        double z = EntityWrap.getZ(entity);
        double playerPos = this.axis == Direction.Axis.Y ? y : (this.axis == Direction.Axis.X ? x : z);
        double min = this.layerRangeMin + 0.5D;
        double max = this.layerRangeMax + 0.5D;

        return playerPos < min || (Math.abs(playerPos - min) < Math.abs(playerPos - max));
    }

    public String getCurrentLayerString()
    {
        switch (this.layerMode)
        {
            case SINGLE_LAYER:  return String.valueOf(this.layerSingle);
            case ALL_ABOVE:     return String.valueOf(this.layerAbove);
            case ALL_BELOW:     return String.valueOf(this.layerBelow);
            case LAYER_RANGE:   return String.format("%d ... %s", this.layerRangeMin, this.layerRangeMax);
            default:            return "";
        }
    }

    protected int getLimitsClampedValue(int value, IntBoundingBox limits)
    {
        return MathUtils.clamp(value,
                               limits.getMinValueForAxis(this.axis),
                               limits.getMaxValueForAxis(this.axis));
    }

    public boolean isPositionWithinRange(BlockPos pos)
    {
        return this.isPositionWithinRange(pos.getX(), pos.getY(), pos.getZ());
    }

    public boolean isPositionWithinRange(int x, int y, int z)
    {
        switch (this.layerMode)
        {
            case ALL:           return true;
            case SINGLE_LAYER:  return this.isPositionWithinSingleLayerRange(x, y, z);
            case ALL_ABOVE:     return this.isPositionWithinAboveRange(x, y, z);
            case ALL_BELOW:     return this.isPositionWithinBelowRange(x, y, z);
            case LAYER_RANGE:   return this.isPositionWithinLayerRangeRange(x, y, z);
        }

        return false;
    }

    protected boolean isPositionWithinSingleLayerRange(int x, int y, int z)
    {
        switch (this.axis)
        {
            case X: return x == this.layerSingle;
            case Y: return y == this.layerSingle;
            case Z: return z == this.layerSingle;
        }

        return false;
    }

    protected boolean isPositionWithinAboveRange(int x, int y, int z)
    {
        switch (this.axis)
        {
            case X: return x >= this.layerAbove;
            case Y: return y >= this.layerAbove;
            case Z: return z >= this.layerAbove;
        }

        return false;
    }

    protected boolean isPositionWithinBelowRange(int x, int y, int z)
    {
        switch (this.axis)
        {
            case X: return x <= this.layerBelow;
            case Y: return y <= this.layerBelow;
            case Z: return z <= this.layerBelow;
        }

        return false;
    }

    protected boolean isPositionWithinLayerRangeRange(int x, int y, int z)
    {
        switch (this.axis)
        {
            case X: return x >= this.layerRangeMin && x <= this.layerRangeMax;
            case Y: return y >= this.layerRangeMin && y <= this.layerRangeMax;
            case Z: return z >= this.layerRangeMin && z <= this.layerRangeMax;
        }

        return false;
    }

    public boolean isPositionAtRenderEdgeOnSide(BlockPos pos, Direction side)
    {
        switch (this.axis)
        {
            case X: return (side == Direction.WEST  && pos.getX() == this.getMinLayerBoundary()) || (side == Direction.EAST  && pos.getX() == this.getMaxLayerBoundary());
            case Y: return (side == Direction.DOWN  && pos.getY() == this.getMinLayerBoundary()) || (side == Direction.UP    && pos.getY() == this.getMaxLayerBoundary());
            case Z: return (side == Direction.NORTH && pos.getZ() == this.getMinLayerBoundary()) || (side == Direction.SOUTH && pos.getZ() == this.getMaxLayerBoundary());
        }

        return false;
    }

    public boolean intersects(ChunkSectionPos pos)
    {
        switch (this.axis)
        {
            case X:
            {
                final int xMin = (pos.getX() << 4);
                final int xMax = (pos.getX() << 4) + 15;
                return (xMax < this.getMinLayerBoundary() || xMin > this.getMaxLayerBoundary()) == false;
            }
            case Y:
            {
                final int yMin = (pos.getY() << 4);
                final int yMax = (pos.getY() << 4) + 15;
                return (yMax < this.getMinLayerBoundary() || yMin > this.getMaxLayerBoundary()) == false;
            }
            case Z:
            {
                final int zMin = (pos.getZ() << 4);
                final int zMax = (pos.getZ() << 4) + 15;
                return (zMax < this.getMinLayerBoundary() || zMin > this.getMaxLayerBoundary()) == false;
            }
            default:
                return false;
        }
    }

    public boolean intersects(IntBoundingBox box)
    {
        return this.intersectsBox(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
    }

    public boolean intersectsBox(BlockPos pos1, BlockPos pos2)
    {
        BlockPos posMin = PositionUtils.getMinCorner(pos1, pos2);
        BlockPos posMax = PositionUtils.getMaxCorner(pos1, pos2);
        return this.intersectsBox(posMin.getX(), posMin.getY(), posMin.getZ(), posMax.getX(), posMax.getY(), posMax.getZ());
    }

    public boolean intersectsBox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ)
    {
        switch (this.axis)
        {
            case X: return (maxX < this.getMinLayerBoundary() || minX > this.getMaxLayerBoundary()) == false;
            case Y: return (maxY < this.getMinLayerBoundary() || minY > this.getMaxLayerBoundary()) == false;
            case Z: return (maxZ < this.getMinLayerBoundary() || minZ > this.getMaxLayerBoundary()) == false;
            default: return false;
        }
    }

    public int getClampedValue(int value, Direction.Axis axis)
    {
        if (this.axis == axis)
        {
            return MathUtils.clamp(value, this.getMinLayerBoundary(), this.getMaxLayerBoundary());
        }

        //return MathUtils.clamp(value, limits.getMinValueForAxis(axis), limits.getMaxValueForAxis(axis));
        return value;
    }

    /**
     * Clamps the given box to the layer range bounds.
     * @param box
     * @return the clamped box, or null, if the range does not intersect the original box
     */
    @Nullable
    public IntBoundingBox getClampedBox(IntBoundingBox box)
    {
        return this.getClampedArea(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
    }

    /**
     * Clamps the given box to the layer range bounds.
     * @return the clamped box, or null, if the range does not intersect the original box
     */
    @Nullable
    public IntBoundingBox getClampedArea(BlockPos posMin, BlockPos posMax)
    {
        return this.getClampedArea(posMin.getX(), posMin.getY(), posMin.getZ(),
                                   posMax.getX(), posMax.getY(), posMax.getZ());
    }

    /**
     * Clamps the given box to the layer range bounds.
     * @return the clamped box, or null, if the range does not intersect the original box
     */
    @Nullable
    public IntBoundingBox getClampedArea(int minX, int minY, int minZ, int maxX, int maxY, int maxZ)
    {
        if (this.intersectsBox(minX, minY, minZ, maxX, maxY, maxZ) == false)
        {
            return null;
        }

        switch (this.axis)
        {
            case X:
            {
                final int clampedMinX = Math.max(minX, this.getMinLayerBoundary());
                final int clampedMaxX = Math.min(maxX, this.getMaxLayerBoundary());
                return IntBoundingBox.createProper(clampedMinX, minY, minZ, clampedMaxX, maxY, maxZ);
            }
            case Y:
            {
                final int clampedMinY = Math.max(minY, this.getMinLayerBoundary());
                final int clampedMaxY = Math.min(maxY, this.getMaxLayerBoundary());
                return IntBoundingBox.createProper(minX, clampedMinY, minZ, maxX, clampedMaxY, maxZ);
            }
            case Z:
            {
                final int clampedMinZ = Math.max(minZ, this.getMinLayerBoundary());
                final int clampedMaxZ = Math.min(maxZ, this.getMaxLayerBoundary());
                return IntBoundingBox.createProper(minX, minY, clampedMinZ, maxX, maxY, clampedMaxZ);
            }
            default:
                return null;
        }
    }

    protected void sendMessage(String translationKey, Object... args)
    {
        MessageDispatcher.generic().customHotbar().translate(translationKey, args);
    }

    public LayerRange copy()
    {
        LayerRange newRange = new LayerRange(this.listener);

        newRange.layerMode = this.layerMode;
        newRange.axis = this.axis;
        newRange.layerSingle = this.layerSingle;
        newRange.layerAbove = this.layerAbove;
        newRange.layerBelow = this.layerBelow;
        newRange.layerRangeMin = this.layerRangeMin;
        newRange.layerRangeMax = this.layerRangeMax;
        newRange.hotkeyRangeMin = this.hotkeyRangeMin;
        newRange.hotkeyRangeMax = this.hotkeyRangeMax;

        return newRange;
    }

    public JsonObject toJson()
    {
        JsonObject obj = new JsonObject();

        obj.add("mode", new JsonPrimitive(this.layerMode.name()));
        obj.add("axis", new JsonPrimitive(this.axis.getName()));
        obj.add("follow_player", new JsonPrimitive(this.followPlayer));
        obj.add("layer_single", new JsonPrimitive(this.layerSingle));
        obj.add("layer_above", new JsonPrimitive(this.layerAbove));
        obj.add("layer_below", new JsonPrimitive(this.layerBelow));
        obj.add("layer_range_min", new JsonPrimitive(this.layerRangeMin));
        obj.add("layer_range_max", new JsonPrimitive(this.layerRangeMax));
        obj.add("player_follow_offset", new JsonPrimitive(this.playerFollowOffset));
        obj.add("hotkey_range_min", new JsonPrimitive(this.hotkeyRangeMin));
        obj.add("hotkey_range_max", new JsonPrimitive(this.hotkeyRangeMax));

        return obj;
    }

    public void fromJson(JsonObject obj)
    {
        this.layerMode = BaseOptionConfigValue.findValueByName(JsonUtils.getString(obj, "mode"), LayerMode.VALUES);
        this.axis = Direction.Axis.byName(JsonUtils.getString(obj, "axis"));
        if (this.axis == null) { this.axis = Direction.Axis.Y; }

        this.followPlayer = JsonUtils.getBoolean(obj, "follow_player");
        this.layerSingle = JsonUtils.getInteger(obj, "layer_single");
        this.layerAbove = JsonUtils.getInteger(obj, "layer_above");
        this.layerBelow = JsonUtils.getInteger(obj, "layer_below");
        this.layerRangeMin = JsonUtils.getInteger(obj, "layer_range_min");
        this.layerRangeMax = JsonUtils.getInteger(obj, "layer_range_max");
        this.playerFollowOffset = JsonUtils.getInteger(obj, "player_follow_offset");
        this.hotkeyRangeMin = JsonUtils.getBoolean(obj, "hotkey_range_min");
        this.hotkeyRangeMax = JsonUtils.getBoolean(obj, "hotkey_range_max");
    }

    public static LayerRange createFromJson(JsonObject obj, LayerRangeChangeListener refresher)
    {
        LayerRange range = new LayerRange(refresher);
        range.fromJson(obj);
        return range;
    }
}
