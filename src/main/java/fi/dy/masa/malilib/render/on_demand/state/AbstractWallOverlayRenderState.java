package fi.dy.masa.malilib.render.on_demand.state;

import java.util.ArrayList;
import java.util.List;
import org.jspecify.annotations.NonNull;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

import fi.dy.masa.malilib.interfaces.IOnDemandRenderState;
import fi.dy.masa.malilib.util.MathUtils;
import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.malilib.util.position.Vec2d;
import fi.dy.masa.malilib.util.position.Vec3d;

public abstract class AbstractWallOverlayRenderState implements IOnDemandRenderState
{
	protected Vec3d camPos;
	protected BlockPos pos1;
	protected BlockPos pos2;
	protected Vec2d lineIntervals;
	protected boolean alignLinesToModulo;
	protected Color4f quadsColor;
	protected Color4f linesColor;
	protected float linesWidth;
	protected List<AABB> boxes;

	protected AbstractWallOverlayRenderState(BlockPos posStart, BlockPos posEnd,
	                                         Vec3d camPos,
	                                         Vec2d lineIntervals,
	                                         boolean alignLinesToModulo,
	                                         Color4f quadsColor, Color4f linesColor,
	                                         float linesWidth)
	{
		this.pos1 = posStart;
		this.pos2 = posEnd;
		this.camPos = camPos;
		this.lineIntervals = lineIntervals;
		this.alignLinesToModulo = alignLinesToModulo;
		this.quadsColor = quadsColor;
		this.linesColor = linesColor;
		this.linesWidth = linesWidth;
		this.boxes = this.calculateBoxes();
	}

	@Override
	public double x()
	{
		return this.pos1.getX();
	}

	@Override
	public double y()
	{
		return this.pos1.getY();
	}

	@Override
	public double z()
	{
		return this.pos1.getZ();
	}

	@Override
	public @NonNull Color4f color()
	{
		return this.quadsColor;
	}

	public BlockPos posStart()
	{
		return this.pos1;
	}

	public BlockPos posEnd()
	{
		return this.pos2;
	}

	public Vec3d camPos()
	{
		return this.camPos;
	}

	public List<AABB> boxes()
	{
		return this.boxes;
	}

	public Vec2d lineIntervals()
	{
		return this.lineIntervals;
	}

	public double lineIntervalH()
	{
		return this.lineIntervals.getX();
	}

	public double lineIntervalV()
	{
		return this.lineIntervals.getY();
	}

	public boolean alignLinesToModulo()
	{
		return this.alignLinesToModulo;
	}

	public Color4f quadsColor()
	{
		return this.quadsColor;
	}

	public Color4f linesColor()
	{
		return this.linesColor;
	}

	public float linesWidth()
	{
		return this.linesWidth;
	}

	protected List<AABB> calculateBoxes()
	{
		Minecraft mc = Minecraft.getInstance();
		final int renderDistance = mc.options.renderDistance().get();
		final int boxMinX = MathUtils.min(this.posStart().getX(), this.posEnd().getX());
		final int boxMinZ = MathUtils.min(this.posStart().getZ(), this.posEnd().getZ());
		final int boxMaxX = MathUtils.max(this.posStart().getX(), this.posEnd().getX());
		final int boxMaxZ = MathUtils.max(this.posStart().getZ(), this.posEnd().getZ());
		final int centerX = (int) MathUtils.floor(this.camPos().getX());
		final int centerZ = (int) MathUtils.floor(this.camPos().getZ());
		final int maxDist = renderDistance * 32; // double the view distance in blocks
		final int rangeMinX = centerX - maxDist;
		final int rangeMinZ = centerZ - maxDist;
		final int rangeMaxX = centerX + maxDist;
		final int rangeMaxZ = centerZ + maxDist;
		final double minY = MathUtils.min(this.posStart().getY(), this.posEnd().getY());
		final double maxY = MathUtils.max(this.posStart().getY(), this.posEnd().getY()) + 1;
		double minX, minZ, maxX, maxZ;

		List<AABB> boxes = new ArrayList<>();

		// The sides of the box along the x-axis can be at least partially inside the range
		if (rangeMinX <= boxMaxX && rangeMaxX >= boxMinX)
		{
			minX = Math.max(boxMinX, rangeMinX);
			maxX = Math.min(boxMaxX, rangeMaxX) + 1;

			if (rangeMinZ <= boxMinZ && rangeMaxZ >= boxMinZ)
			{
				minZ = maxZ = boxMinZ;
				boxes.add(new AABB(minX, minY, minZ, maxX, maxY, maxZ));
			}

			if (rangeMinZ <= boxMaxZ && rangeMaxZ >= boxMaxZ)
			{
				minZ = maxZ = boxMaxZ + 1;
				boxes.add(new AABB(minX, minY, minZ, maxX, maxY, maxZ));
			}
		}

		// The sides of the box along the z-axis can be at least partially inside the range
		if (rangeMinZ <= boxMaxZ && rangeMaxZ >= boxMinZ)
		{
			minZ = Math.max(boxMinZ, rangeMinZ);
			maxZ = Math.min(boxMaxZ, rangeMaxZ) + 1;

			if (rangeMinX <= boxMinX && rangeMaxX >= boxMinX)
			{
				minX = maxX = boxMinX;
				boxes.add(new AABB(minX, minY, minZ, maxX, maxY, maxZ));
			}

			if (rangeMinX <= boxMaxX && rangeMaxX >= boxMaxX)
			{
				minX = maxX = boxMaxX + 1;
				boxes.add(new AABB(minX, minY, minZ, maxX, maxY, maxZ));
			}
		}

		return boxes;
	}
}
