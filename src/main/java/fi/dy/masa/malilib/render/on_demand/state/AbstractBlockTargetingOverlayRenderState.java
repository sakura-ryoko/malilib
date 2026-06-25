package fi.dy.masa.malilib.render.on_demand.state;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import fi.dy.masa.malilib.interfaces.IOnDemandRenderState;
import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.malilib.util.position.PositionUtils;
import fi.dy.masa.malilib.util.position.Vec3d;

public abstract class AbstractBlockTargetingOverlayRenderState implements IOnDemandRenderState
{
	protected BlockPos pos;
	protected Vec3d camPos;
	protected Color4f sideColor;
	protected Color4f lineColor;
	protected float lineWidth;
	protected Direction side;
	protected Direction facing;
	protected @Nullable PositionUtils.HitPart part;

	protected AbstractBlockTargetingOverlayRenderState(BlockPos pos,
	                                                   Vec3d camPos,
	                                                   Color4f sideColor, Color4f lineColor,
	                                                   float lineWidth,
	                                                   Direction side, Direction facing,
	                                                   @Nullable PositionUtils.HitPart part)
	{
		this.pos = pos;
		this.camPos = camPos;
		this.sideColor = sideColor;
		this.lineColor = lineColor;
		this.lineWidth = lineWidth;
		this.side = side;
		this.facing = facing;
		this.part = part;
	}

	@Override
	public double x() { return this.camPos.x(); }

	@Override
	public double y() { return this.camPos.y(); }

	@Override
	public double z() { return this.camPos.z(); }

	public Color4f sideColor() { return this.sideColor; }

	public Color4f lineColor() { return this.lineColor; }

	public float lineWidth() { return this.lineWidth; }

	public Direction side() { return this.side; }

	public Direction facing() { return this.facing; }

	public boolean isSimple()
	{
		return false;
	}

	public @Nullable PositionUtils.HitPart part() { return this.part; }

	public void updateCameraOffset(Vec3d cameraPos)
	{
		this.camPos = new Vec3d(
				(this.pos.getX() + 0.5d - cameraPos.x()),
				(this.pos.getY() + 0.5d - cameraPos.y()),
				(this.pos.getZ() + 0.5d - cameraPos.z())
		);
	}
}
