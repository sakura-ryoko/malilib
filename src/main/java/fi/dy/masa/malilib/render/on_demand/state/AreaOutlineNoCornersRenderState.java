package fi.dy.masa.malilib.render.on_demand.state;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.pipeline.RenderPipeline;

import net.minecraft.client.render.VertexConsumer;

import fi.dy.masa.malilib.render.MaLiLibPipelines;
import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.malilib.util.position.BlockPos;
import fi.dy.masa.malilib.util.position.Vec3d;

public class AreaOutlineNoCornersRenderState extends AbstractSelectionBoxRenderState
{
	public static final AreaOutlineNoCornersRenderState EMPTY = new AreaOutlineNoCornersRenderState(Vec3d.ZERO, BlockPos.ORIGIN, BlockPos.ORIGIN, 3f, Color4f.ZERO, Color4f.ZERO, Color4f.ZERO)
	{
		@Override
		public boolean isEmpty() { return true; }
	};

	public AreaOutlineNoCornersRenderState(Vec3d camPos,
	                                       BlockPos pos1, BlockPos pos2,
	                                       float lineWidth,
	                                       Color4f colorX,
	                                       Color4f colorY,
	                                       Color4f colorZ)
	{
		super(camPos, pos1, pos2, null,
		      0.001f, lineWidth,
		      colorX, colorY, colorZ,
		      Color4f.ZERO,
		      false, false
		);
	}

	@Override
	public boolean isAreaOutlineNoCorners()
	{
		return true;
	}

	@Override
	public boolean isEmpty()
	{
		return false;
	}

	@Override
	public @Nonnull RenderPipeline pipeline()
	{
		return MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_LEQUAL_DEPTH;
	}

	@Override
	public double x()
	{
		return this.pos1().getX();
	}

	@Override
	public double y()
	{
		return this.pos1().getY();
	}

	@Override
	public double z()
	{
		return this.pos1().getZ();
	}

	@Override
	public void update(VertexConsumer buffer)
	{
		final BlockPos pos1 = this.pos1();
		final BlockPos pos2 = this.pos2();
		final double dx = this.camPos().x;
		final double dy = this.camPos().y;
		final double dz = this.camPos().z;

		final int xMin = Math.min(pos1.getX(), pos2.getX());
		final int yMin = Math.min(pos1.getY(), pos2.getY());
		final int zMin = Math.min(pos1.getZ(), pos2.getZ());
		final int xMax = Math.max(pos1.getX(), pos2.getX());
		final int yMax = Math.max(pos1.getY(), pos2.getY());
		final int zMax = Math.max(pos1.getZ(), pos2.getZ());

		final float dxMin = (float) (-dx - this.expand());
		final float dyMin = (float) (-dy - this.expand());
		final float dzMin = (float) (-dz - this.expand());
		final float dxMax = (float) (-dx + this.expand());
		final float dyMax = (float) (-dy + this.expand());
		final float dzMax = (float) (-dz + this.expand());

		final float minX = xMin + dxMin;
		final float minY = yMin + dyMin;
		final float minZ = zMin + dzMin;
		final float maxX = xMax + dxMax;
		final float maxY = yMax + dyMax;
		final float maxZ = zMax + dzMax;

		final Color4f colorX = this.color1();
		final Color4f colorY = this.color2();
		final Color4f colorZ = this.color3();
		final float lineWidth = this.lineWidth();

		int start, end;

		// Edges along the X-axis
		start = (pos1.getX() == xMin && pos1.getY() == yMin && pos1.getZ() == zMin) || (pos2.getX() == xMin && pos2.getY() == yMin && pos2.getZ() == zMin) ? xMin + 1 : xMin;
		end = (pos1.getX() == xMax && pos1.getY() == yMin && pos1.getZ() == zMin) || (pos2.getX() == xMax && pos2.getY() == yMin && pos2.getZ() == zMin) ? xMax : xMax + 1;

		if (end > start)
		{
			// .setLineWidth(lineWidth)
			buffer.vertex(start + dxMin, minY, minZ).color(colorX.r, colorX.g, colorX.b, colorX.a);
			buffer.vertex(end + dxMax, minY, minZ).color(colorX.r, colorX.g, colorX.b, colorX.a);
		}

		start = (pos1.getX() == xMin && pos1.getY() == yMax && pos1.getZ() == zMin) || (pos2.getX() == xMin && pos2.getY() == yMax && pos2.getZ() == zMin) ? xMin + 1 : xMin;
		end = (pos1.getX() == xMax && pos1.getY() == yMax && pos1.getZ() == zMin) || (pos2.getX() == xMax && pos2.getY() == yMax && pos2.getZ() == zMin) ? xMax : xMax + 1;

		if (end > start)
		{
			buffer.vertex(start + dxMin, maxY + 1, minZ).color(colorX.r, colorX.g, colorX.b, colorX.a);
			buffer.vertex(end + dxMax, maxY + 1, minZ).color(colorX.r, colorX.g, colorX.b, colorX.a);
		}

		start = (pos1.getX() == xMin && pos1.getY() == yMin && pos1.getZ() == zMax) || (pos2.getX() == xMin && pos2.getY() == yMin && pos2.getZ() == zMax) ? xMin + 1 : xMin;
		end = (pos1.getX() == xMax && pos1.getY() == yMin && pos1.getZ() == zMax) || (pos2.getX() == xMax && pos2.getY() == yMin && pos2.getZ() == zMax) ? xMax : xMax + 1;

		if (end > start)
		{
			buffer.vertex(start + dxMin, minY, maxZ + 1).color(colorX.r, colorX.g, colorX.b, colorX.a);
			buffer.vertex(end + dxMax, minY, maxZ + 1).color(colorX.r, colorX.g, colorX.b, colorX.a);
		}

		start = (pos1.getX() == xMin && pos1.getY() == yMax && pos1.getZ() == zMax) || (pos2.getX() == xMin && pos2.getY() == yMax && pos2.getZ() == zMax) ? xMin + 1 : xMin;
		end = (pos1.getX() == xMax && pos1.getY() == yMax && pos1.getZ() == zMax) || (pos2.getX() == xMax && pos2.getY() == yMax && pos2.getZ() == zMax) ? xMax : xMax + 1;

		if (end > start)
		{
			buffer.vertex(start + dxMin, maxY + 1, maxZ + 1).color(colorX.r, colorX.g, colorX.b, colorX.a);
			buffer.vertex(end + dxMax, maxY + 1, maxZ + 1).color(colorX.r, colorX.g, colorX.b, colorX.a);
		}

		// Edges along the Y-axis
		start = (pos1.getX() == xMin && pos1.getY() == yMin && pos1.getZ() == zMin) || (pos2.getX() == xMin && pos2.getY() == yMin && pos2.getZ() == zMin) ? yMin + 1 : yMin;
		end = (pos1.getX() == xMin && pos1.getY() == yMax && pos1.getZ() == zMin) || (pos2.getX() == xMin && pos2.getY() == yMax && pos2.getZ() == zMin) ? yMax : yMax + 1;

		if (end > start)
		{
			buffer.vertex(minX, start + dyMin, minZ).color(colorY.r, colorY.g, colorY.b, colorY.a);
			buffer.vertex(minX, end + dyMax, minZ).color(colorY.r, colorY.g, colorY.b, colorY.a);
		}

		start = (pos1.getX() == xMax && pos1.getY() == yMin && pos1.getZ() == zMin) || (pos2.getX() == xMax && pos2.getY() == yMin && pos2.getZ() == zMin) ? yMin + 1 : yMin;
		end = (pos1.getX() == xMax && pos1.getY() == yMax && pos1.getZ() == zMin) || (pos2.getX() == xMax && pos2.getY() == yMax && pos2.getZ() == zMin) ? yMax : yMax + 1;

		if (end > start)
		{
			buffer.vertex(maxX + 1, start + dyMin, minZ).color(colorY.r, colorY.g, colorY.b, colorY.a);
			buffer.vertex(maxX + 1, end + dyMax, minZ).color(colorY.r, colorY.g, colorY.b, colorY.a);
		}

		start = (pos1.getX() == xMin && pos1.getY() == yMin && pos1.getZ() == zMax) || (pos2.getX() == xMin && pos2.getY() == yMin && pos2.getZ() == zMax) ? yMin + 1 : yMin;
		end = (pos1.getX() == xMin && pos1.getY() == yMax && pos1.getZ() == zMax) || (pos2.getX() == xMin && pos2.getY() == yMax && pos2.getZ() == zMax) ? yMax : yMax + 1;

		if (end > start)
		{
			buffer.vertex(minX, start + dyMin, maxZ + 1).color(colorY.r, colorY.g, colorY.b, colorY.a);
			buffer.vertex(minX, end + dyMax, maxZ + 1).color(colorY.r, colorY.g, colorY.b, colorY.a);
		}

		start = (pos1.getX() == xMax && pos1.getY() == yMin && pos1.getZ() == zMax) || (pos2.getX() == xMax && pos2.getY() == yMin && pos2.getZ() == zMax) ? yMin + 1 : yMin;
		end = (pos1.getX() == xMax && pos1.getY() == yMax && pos1.getZ() == zMax) || (pos2.getX() == xMax && pos2.getY() == yMax && pos2.getZ() == zMax) ? yMax : yMax + 1;

		if (end > start)
		{
			buffer.vertex(maxX + 1, start + dyMin, maxZ + 1).color(colorY.r, colorY.g, colorY.b, colorY.a);
			buffer.vertex(maxX + 1, end + dyMax, maxZ + 1).color(colorY.r, colorY.g, colorY.b, colorY.a);
		}

		// Edges along the Z-axis
		start = (pos1.getX() == xMin && pos1.getY() == yMin && pos1.getZ() == zMin) || (pos2.getX() == xMin && pos2.getY() == yMin && pos2.getZ() == zMin) ? zMin + 1 : zMin;
		end = (pos1.getX() == xMin && pos1.getY() == yMin && pos1.getZ() == zMax) || (pos2.getX() == xMin && pos2.getY() == yMin && pos2.getZ() == zMax) ? zMax : zMax + 1;

		if (end > start)
		{
			buffer.vertex(minX, minY, start + dzMin).color(colorZ.r, colorZ.g, colorZ.b, colorZ.a);
			buffer.vertex(minX, minY, end + dzMax).color(colorZ.r, colorZ.g, colorZ.b, colorZ.a);
		}

		start = (pos1.getX() == xMax && pos1.getY() == yMin && pos1.getZ() == zMin) || (pos2.getX() == xMax && pos2.getY() == yMin && pos2.getZ() == zMin) ? zMin + 1 : zMin;
		end = (pos1.getX() == xMax && pos1.getY() == yMin && pos1.getZ() == zMax) || (pos2.getX() == xMax && pos2.getY() == yMin && pos2.getZ() == zMax) ? zMax : zMax + 1;

		if (end > start)
		{
			buffer.vertex(maxX + 1, minY, start + dzMin).color(colorZ.r, colorZ.g, colorZ.b, colorZ.a);
			buffer.vertex(maxX + 1, minY, end + dzMax).color(colorZ.r, colorZ.g, colorZ.b, colorZ.a);
		}

		start = (pos1.getX() == xMin && pos1.getY() == yMax && pos1.getZ() == zMin) || (pos2.getX() == xMin && pos2.getY() == yMax && pos2.getZ() == zMin) ? zMin + 1 : zMin;
		end = (pos1.getX() == xMin && pos1.getY() == yMax && pos1.getZ() == zMax) || (pos2.getX() == xMin && pos2.getY() == yMax && pos2.getZ() == zMax) ? zMax : zMax + 1;

		if (end > start)
		{
			buffer.vertex(minX, maxY + 1, start + dzMin).color(colorZ.r, colorZ.g, colorZ.b, colorZ.a);
			buffer.vertex(minX, maxY + 1, end + dzMax).color(colorZ.r, colorZ.g, colorZ.b, colorZ.a);
		}

		start = (pos1.getX() == xMax && pos1.getY() == yMax && pos1.getZ() == zMin) || (pos2.getX() == xMax && pos2.getY() == yMax && pos2.getZ() == zMin) ? zMin + 1 : zMin;
		end = (pos1.getX() == xMax && pos1.getY() == yMax && pos1.getZ() == zMax) || (pos2.getX() == xMax && pos2.getY() == yMax && pos2.getZ() == zMax) ? zMax : zMax + 1;

		if (end > start)
		{
			buffer.vertex(maxX + 1, maxY + 1, start + dzMin).color(colorZ.r, colorZ.g, colorZ.b, colorZ.a);
			buffer.vertex(maxX + 1, maxY + 1, end + dzMax).color(colorZ.r, colorZ.g, colorZ.b, colorZ.a);
		}
	}
}
