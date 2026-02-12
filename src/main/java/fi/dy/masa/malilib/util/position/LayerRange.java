package fi.dy.masa.malilib.util.position;

import javax.annotation.Nullable;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.interfaces.IRangeChangeListener;
import fi.dy.masa.malilib.util.*;
import fi.dy.masa.malilib.util.data.json.JsonUtils;

public class LayerRange
{
    public static final Codec<LayerRange> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    LayerMode.CODEC.fieldOf("mode").forGetter(get -> get.layerMode),
                    Direction.Axis.CODEC.fieldOf("axis").forGetter(get -> get.axis),
                    PrimitiveCodec.INT.fieldOf("layer_single").forGetter(get -> get.layerSingle),
                    PrimitiveCodec.INT.fieldOf("layer_above").forGetter(get -> get.layerAbove),
                    PrimitiveCodec.INT.fieldOf("layer_below").forGetter(get -> get.layerBelow),
                    PrimitiveCodec.INT.fieldOf("layer_range_min").forGetter(get -> get.layerRangeMin),
                    PrimitiveCodec.INT.fieldOf("layer_range_max").forGetter(get -> get.layerRangeMax),
                    PrimitiveCodec.INT.fieldOf("follow_player_offset").forGetter(get -> get.playerFollowOffset),
                    PrimitiveCodec.BOOL.fieldOf("hotkey_range_min").forGetter(get -> get.hotkeyRangeMin),
                    PrimitiveCodec.BOOL.fieldOf("hotkey_range_max").forGetter(get -> get.hotkeyRangeMax),
                    PrimitiveCodec.BOOL.fieldOf("follow_player").forGetter(get -> get.followPlayer)
            ).apply(inst, LayerRange::new)
    );
    public static final StreamCodec<@NotNull ByteBuf, @NotNull LayerRange> PACKET_CODEC = new StreamCodec<>()
    {
	    @Override
	    public void encode(ByteBuf buf, LayerRange value)
	    {
		    LayerMode.PACKET_CODEC.encode(buf, value.layerMode);
		    ByteBufCodecs.STRING_UTF8.encode(buf, value.axis.getSerializedName());
		    ByteBufCodecs.INT.encode(buf, value.layerSingle);
		    ByteBufCodecs.INT.encode(buf, value.layerAbove);
		    ByteBufCodecs.INT.encode(buf, value.layerBelow);
		    ByteBufCodecs.INT.encode(buf, value.layerRangeMin);
		    ByteBufCodecs.INT.encode(buf, value.layerRangeMax);
            ByteBufCodecs.INT.encode(buf, value.playerFollowOffset);
		    ByteBufCodecs.BOOL.encode(buf, value.hotkeyRangeMin);
		    ByteBufCodecs.BOOL.encode(buf, value.hotkeyRangeMax);
            ByteBufCodecs.BOOL.encode(buf, value.followPlayer);
	    }

	    @Override
	    public LayerRange decode(ByteBuf buf)
	    {
		    return new LayerRange(
                    LayerMode.PACKET_CODEC.decode(buf),
                    Direction.Axis.byName(ByteBufCodecs.STRING_UTF8.decode(buf)),
                    ByteBufCodecs.INT.decode(buf),
                    ByteBufCodecs.INT.decode(buf),
                    ByteBufCodecs.INT.decode(buf),
                    ByteBufCodecs.INT.decode(buf),
                    ByteBufCodecs.INT.decode(buf),
                    ByteBufCodecs.INT.decode(buf),
                    ByteBufCodecs.BOOL.decode(buf),
                    ByteBufCodecs.BOOL.decode(buf),
                    ByteBufCodecs.BOOL.decode(buf)
		    );
	    }
    };

    public static final int WORLD_VERTICAL_SIZE_MAX = 255;
    public static final int WORLD_VERTICAL_SIZE_MIN = 0;

    protected IRangeChangeListener refresher;
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

    private LayerRange(LayerMode mode, Direction.Axis axis, int single, int above, int below, int min, int max, int playerFollowOffset, boolean minRange, boolean maxRange, boolean followPlayer)
    {
        this.refresher = null;
        this.layerMode = mode;
        this.axis = axis;
        this.layerSingle = single;
        this.layerAbove = above;
        this.layerBelow = below;
        this.layerRangeMin = min;
        this.layerRangeMax = max;
        this.playerFollowOffset = playerFollowOffset;
        this.hotkeyRangeMin = minRange;
        this.hotkeyRangeMax = maxRange;
        this.followPlayer = followPlayer;
    }

    public LayerRange(IRangeChangeListener refresher)
    {
        this.refresher = refresher;
    }

    public LayerRange setRefresher(IRangeChangeListener refresher)
    {
        this.refresher = refresher;
        return this;
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

    public int getLayerSingle()
    {
        return this.layerSingle;
    }

    public int getLayerAbove()
    {
        return this.layerAbove;
    }

    public int getLayerBelow()
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
            case ALL_BELOW:     return -30000000;
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
            case ALL_ABOVE:     return 30000000;
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

    public void setLayerMode(LayerMode mode)
    {
        this.setLayerMode(mode, true);
    }

    public void setLayerMode(LayerMode mode, boolean printMessage)
    {
        this.layerMode = mode;

        this.refresher.updateAll();

        if (printMessage)
        {
            String val = GuiBase.TXT_GREEN + mode.getDisplayName();
            InfoUtils.printActionbarMessage("malilib.message.set_layer_mode_to", val);
        }
    }

    public void setAxis(Direction.Axis axis)
    {
        this.axis = axis;

        this.refresher.updateAll();
        String val = GuiBase.TXT_GREEN + axis.name();
        InfoUtils.printActionbarMessage("malilib.message.set_layer_axis_to", val);
    }

    public void setPlayerFollowOffset(int offset)
    {
        this.playerFollowOffset = offset;
    }

    public void setLayerSingle(int layer)
    {
        int old = this.layerSingle;
        //layer = this.getWorldLimitsClampedValue(layer);

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
        //layer = this.getWorldLimitsClampedValue(layer);

        if (layer != old)
        {
            this.layerAbove = layer;
            this.updateLayersBetween(old, layer);
        }
    }

    public void setLayerBelow(int layer)
    {
        int old = this.layerBelow;
        //layer = this.getWorldLimitsClampedValue(layer);

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
        //layer = this.getWorldLimitsClampedValue(layer);

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
        //layer = this.getWorldLimitsClampedValue(layer);

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
            case X: return MathUtils.floor(entity.getX());
            case Y: return MathUtils.floor(entity.getY());
            case Z: return MathUtils.floor(entity.getZ());
        }

        return 0;
    }

    public void setToPosition(Entity entity)
    {
        if (this.layerMode == LayerMode.LAYER_RANGE)
        {
            int pos = this.getPositionFromEntity(entity);
            this.setLayerRangeMin(pos, true);
            this.setLayerRangeMax(pos, true);
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

    protected void setSingleBoundaryToPosition(int pos)
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

    protected void markAffectedLayersForRenderUpdate(IntBoundingBox limits)
    {
        int val1;
        int val2;

        switch (this.layerMode)
        {
            case ALL:
                this.refresher.updateAll();
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
                val2 = limits.getMaxValueForAxis(this.axis);;
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
                this.refresher.updateBetweenX(layerMin, layerMax);
                break;
            case Y:
                this.refresher.updateBetweenY(layerMin, layerMax);
                break;
            case Z:
                this.refresher.updateBetweenZ(layerMin, layerMax);
                break;
        }
    }

    public boolean moveLayer(int amount)
    {
        String axisName = this.axis.name().toLowerCase();
        String strTo = GuiBase.TXT_GREEN + axisName + " = ";

        switch (this.layerMode)
        {
            case ALL:
                return false;
            case SINGLE_LAYER:
            {
                this.setLayerSingle(this.layerSingle + amount);
                String val = strTo + this.layerSingle;
                InfoUtils.printActionbarMessage("malilib.message.set_layer_to", val);
                break;
            }
            case ALL_ABOVE:
            {
                this.setLayerAbove(this.layerAbove + amount);
                String val = strTo + this.layerAbove;
                InfoUtils.printActionbarMessage("malilib.message.moved_min_layer_to", val);
                break;
            }
            case ALL_BELOW:
            {
                this.setLayerBelow(this.layerBelow + amount);
                String val = strTo + this.layerBelow;
                InfoUtils.printActionbarMessage("malilib.message.moved_max_layer_to", val);
                break;
            }
            case LAYER_RANGE:
            {
                Entity entity = EntityUtils.getCameraEntity();

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
            String axisName = this.axis.name().toLowerCase();

            if (moveMin && moveMax)
            {
                InfoUtils.printActionbarMessage("malilib.message.moved_layer_range", String.valueOf(amount), axisName);
            }
            else
            {
                String val1;

                if (moveMin)
                {
                    val1 = StringUtils.translate("malilib.message.layer_range.range_min");
                }
                else
                {
                    val1 = StringUtils.translate("malilib.message.layer_range.range_max");
                }

                InfoUtils.printActionbarMessage("malilib.message.moved_layer_range_boundary", val1, String.valueOf(amount), axisName);
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
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
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

    protected int getWorldLimitsClampedValue(int value, IntBoundingBox limits)
    {
        return MathUtils.clamp(value,
                                limits.getMinValueForAxis(this.axis),
                                limits.getMaxValueForAxis(this.axis));
    }

    public boolean isPositionWithinRange(BlockPos pos)
    {
        return this.isPositionWithinRange(pos.getX(), pos.getY(), pos.getZ());
    }

    public boolean isPositionWithinRange(long posLong)
    {
        int x = BlockPos.getX(posLong);
        int y = BlockPos.getY(posLong);
        int z = BlockPos.getZ(posLong);

        return this.isPositionWithinRange(x, y, z);
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

    public boolean intersects(SubChunkPos pos)
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

    // FIXME
    public boolean intersectsBox(BlockPos pos1, BlockPos pos2)
    {
        BlockPos posMin = BlockPos.of(PositionUtils.getMinCorner(pos1.toVanillaPos(), pos2.toVanillaPos()));
        BlockPos posMax = BlockPos.of(PositionUtils.getMaxCorner(pos1.toVanillaPos(), pos2.toVanillaPos()));
        return this.intersectsBox(posMin.getX(), posMin.getY(), posMin.getZ(), posMax.getX(), posMax.getY(), posMax.getZ());
    }

    // FIXME
    public boolean intersectsBox(net.minecraft.core.BlockPos pos1, net.minecraft.core.BlockPos pos2)
    {
        net.minecraft.core.BlockPos posMin = PositionUtils.getMinCorner(pos1, pos2);
        net.minecraft.core.BlockPos posMax = PositionUtils.getMaxCorner(pos1, pos2);
        return this.intersectsBox(posMin.getX(), posMin.getY(), posMin.getZ(), posMax.getX(), posMax.getY(), posMax.getZ());
    }

    public boolean intersectsBox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ)
    {
        switch (this.axis)
        {
            case X: return (maxX < this.getMinLayerBoundary() || minX > this.getMaxLayerBoundary()) == false;
            case Y: return (maxY < this.getMinLayerBoundary() || minY > this.getMaxLayerBoundary()) == false;
            case Z: return (maxZ < this.getMinLayerBoundary() || minZ > this.getMaxLayerBoundary()) == false;
        }

        return false;
    }

    public int getClampedValue(int value, Direction.Axis axis)
    {
        if (this.axis == axis)
        {
            return MathUtils.clamp(value, this.getMinLayerBoundary(), this.getMaxLayerBoundary());
        }

        //return MathHelper.clamp(value, limits.getMinValueForAxis(axis), limits.getMaxValueForAxis(axis));
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

    @Nullable
    public IntBoundingBox getClampedRenderBoundingBox(IntBoundingBox box)
    {
        if (this.intersects(box) == false)
        {
            return null;
        }

        switch (this.axis)
        {
            case X:
            {
                final int xMin = Math.max(box.minX, this.getMinLayerBoundary());
                final int xMax = Math.min(box.maxX, this.getMaxLayerBoundary());
                return IntBoundingBox.createProper(xMin, box.minY, box.minZ, xMax, box.maxY, box.maxZ);
            }
            case Y:
            {
                final int yMin = Math.max(box.minY, this.getMinLayerBoundary());
                final int yMax = Math.min(box.maxY, this.getMaxLayerBoundary());
                return IntBoundingBox.createProper(box.minX, yMin, box.minZ, box.maxX, yMax, box.maxZ);
            }
            case Z:
            {
                final int zMin = Math.max(box.minZ, this.getMinLayerBoundary());
                final int zMax = Math.min(box.maxZ, this.getMaxLayerBoundary());
                return IntBoundingBox.createProper(box.minX, box.minY, zMin, box.maxX, box.maxY, zMax);
            }
            default:
                return null;
        }
    }

    /**
     * Returns a box clamped by the world bounds and this LayerRange,
     * which is expanded by the expandAmount (if possible) in both
     * directions on the axis that this LayerRange is set to.
     */
    public IntBoundingBox getExpandedBox(Level world, int expandAmount)
    {
        int worldMinH = -30000000;
        int worldMaxH =  30000000;
        int worldMinY = world != null ? world.getMinY() : -64;
        int worldMaxY = world != null ? world.getMaxY() : 319;
        int minX = worldMinH;
        int minY = worldMinY;
        int minZ = worldMinH;
        int maxX = worldMaxH;
        int maxY = worldMaxY;
        int maxZ = worldMaxH;

        switch (this.axis)
        {
            case X:
                minX = Math.max(minX, this.getMinLayerBoundary() - expandAmount);
                maxX = Math.min(maxX, this.getMaxLayerBoundary() + expandAmount);
                break;

            case Y:
                minY = Math.max(minY, this.getMinLayerBoundary() - expandAmount);
                maxY = Math.min(maxY, this.getMaxLayerBoundary() + expandAmount);
                break;

            case Z:
                minZ = Math.max(minZ, this.getMinLayerBoundary() - expandAmount);
                maxZ = Math.min(maxZ, this.getMaxLayerBoundary() + expandAmount);
                break;
        }

        return IntBoundingBox.createProper(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public LayerRange copy()
    {
        LayerRange newRange = new LayerRange(this.refresher);

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
        this.layerMode = LayerMode.fromStringStatic(JsonUtils.getString(obj, "mode"));
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

    public static LayerRange createFromJson(JsonObject obj, IRangeChangeListener refresher)
    {
        LayerRange range = new LayerRange(refresher);
        range.fromJson(obj);
        return range;
    }
}
