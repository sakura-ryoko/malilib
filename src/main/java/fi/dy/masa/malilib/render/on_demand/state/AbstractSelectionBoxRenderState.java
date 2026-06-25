package fi.dy.masa.malilib.render.on_demand.state;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

import fi.dy.masa.malilib.interfaces.IOnDemandRenderState;
import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.malilib.util.position.Vec3d;

public abstract class AbstractSelectionBoxRenderState implements IOnDemandRenderState
{
	protected Vec3d camPos;
	protected BlockPos simplePos;
	protected BlockPos pos1;
	protected BlockPos pos2;
	protected float expand;
	protected float lineWidth;
	protected Color4f color1;
	protected Color4f color2;
	protected Color4f color3;
	protected Color4f color4;
	protected boolean renderThrough;
	protected boolean shouldResort;

	protected AbstractSelectionBoxRenderState(Vec3d camPos,
	                                          BlockPos pos1, BlockPos pos2, @Nullable BlockPos simplePos,
	                                          float expand, float lineWidth,
	                                          Color4f color1, Color4f color2,
	                                          Color4f color3, Color4f color4,
	                                          boolean renderThrough, boolean shouldResort)
	{
		this.camPos = camPos;
		this.pos1 = pos1;
		this.pos2 = pos2;
		this.simplePos = simplePos;
		this.expand = expand;
		this.lineWidth = lineWidth;
		this.color1 = color1;
		this.color2 = color2;
		this.color3 = color3;
		this.color4 = color4;
		this.renderThrough = renderThrough;
		this.shouldResort = shouldResort;
	}

	public boolean isSimple()
	{
		return false;
	}

	public boolean isBlockOutline()
	{
		return false;
	}

	public boolean isAreaOutlineNoCorners()
	{
		return false;
	}

	public boolean isAreaSides()
	{
		return false;
	}

	public boolean isOverlapping()
	{
		return false;
	}

	public abstract boolean isEmpty();

	public Vec3d camPos()
	{
		return this.camPos;
	}

	public BlockPos pos1()
	{
		return this.pos1;
	}

	public BlockPos pos2()
	{
		return this.pos2;
	}

	public @Nullable BlockPos simplePos()
	{
		return this.simplePos;
	}

	protected void ensureSimplePos()
	{
		if (this.isSimple() && this.simplePos == null)
		{
			throw new RuntimeException("AbstractSelectionBoxRenderState: Simple Pos is null");
		}
	}

	public float expand()
	{
		return this.expand;
	}

	public float lineWidth()
	{
		return this.lineWidth;
	}

	public Color4f color1()
	{
		return this.color1;
	}

	public Color4f color2()
	{
		return this.color2;
	}

	public Color4f color3()
	{
		return this.color3;
	}

	public Color4f color4()
	{
		return this.color4;
	}

	public boolean renderThrough()
	{
		return this.renderThrough;
	}

	public boolean shouldResort()
	{
		return this.shouldResort;
	}

	protected boolean checkIfCameraInsideOf()
	{
		BlockPos pos1 = this.pos1();
		BlockPos pos2 = this.pos2();

		// Fix Bottom Y Border offset
		if (pos1.getY() < pos2.getY())
		{
			pos1 = pos1.mutable().setY(pos1.getY() - 1).immutable();
		}
		else if (pos2.getY() < pos1.getY())
		{
			pos2 = pos2.mutable().setY(pos2.getY() - 1).immutable();
		}

		return AABB.encapsulatingFullBlocks(pos1, pos2).contains(this.camPos().toVanilla());
	}

	// Library of common buffer filling methods
	protected void renderOutlinesAllSides(float minX, float minY, float minZ, float maxX, float maxY, float maxZ,
	                                      Color4f color, float lineWidth,
	                                      VertexConsumer buffer)
	{
		// West side
		buffer.addVertex(minX, minY, minZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);
		buffer.addVertex(minX, minY, maxZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);

		buffer.addVertex(minX, minY, maxZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);
		buffer.addVertex(minX, maxY, maxZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);

		buffer.addVertex(minX, maxY, maxZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);
		buffer.addVertex(minX, maxY, minZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);

		buffer.addVertex(minX, maxY, minZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);
		buffer.addVertex(minX, minY, minZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);

		// East side
		buffer.addVertex(maxX, minY, maxZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);
		buffer.addVertex(maxX, minY, minZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);

		buffer.addVertex(maxX, minY, minZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);
		buffer.addVertex(maxX, maxY, minZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);

		buffer.addVertex(maxX, maxY, minZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);
		buffer.addVertex(maxX, maxY, maxZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);

		buffer.addVertex(maxX, maxY, maxZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);
		buffer.addVertex(maxX, minY, maxZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);

		// North side (don't repeat the vertical lines that are done by the east/west sides)
		buffer.addVertex(maxX, minY, minZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);
		buffer.addVertex(minX, minY, minZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);

		buffer.addVertex(minX, maxY, minZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);
		buffer.addVertex(maxX, maxY, minZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);

		// South side (don't repeat the vertical lines that are done by the east/west sides)
		buffer.addVertex(minX, minY, maxZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);
		buffer.addVertex(maxX, minY, maxZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);

		buffer.addVertex(maxX, maxY, maxZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);
		buffer.addVertex(minX, maxY, maxZ).setColor(color.r, color.g, color.b, color.a).setLineWidth(lineWidth);
	}

	protected void renderHorizonalQuads(float minX, float minY, float minZ, float maxX, float maxY, float maxZ,
	                                    Color4f color, VertexConsumer buffer)
	{
		// West side
		buffer.addVertex(minX, minY, minZ).setColor(color.r, color.g, color.b, color.a);
		buffer.addVertex(minX, minY, maxZ).setColor(color.r, color.g, color.b, color.a);
		buffer.addVertex(minX, maxY, maxZ).setColor(color.r, color.g, color.b, color.a);
		buffer.addVertex(minX, maxY, minZ).setColor(color.r, color.g, color.b, color.a);

		// East side
		buffer.addVertex(maxX, minY, maxZ).setColor(color.r, color.g, color.b, color.a);
		buffer.addVertex(maxX, minY, minZ).setColor(color.r, color.g, color.b, color.a);
		buffer.addVertex(maxX, maxY, minZ).setColor(color.r, color.g, color.b, color.a);
		buffer.addVertex(maxX, maxY, maxZ).setColor(color.r, color.g, color.b, color.a);

		// North side
		buffer.addVertex(maxX, minY, minZ).setColor(color.r, color.g, color.b, color.a);
		buffer.addVertex(minX, minY, minZ).setColor(color.r, color.g, color.b, color.a);
		buffer.addVertex(minX, maxY, minZ).setColor(color.r, color.g, color.b, color.a);
		buffer.addVertex(maxX, maxY, minZ).setColor(color.r, color.g, color.b, color.a);

		// South side
		buffer.addVertex(minX, minY, maxZ).setColor(color.r, color.g, color.b, color.a);
		buffer.addVertex(maxX, minY, maxZ).setColor(color.r, color.g, color.b, color.a);
		buffer.addVertex(maxX, maxY, maxZ).setColor(color.r, color.g, color.b, color.a);
		buffer.addVertex(minX, maxY, maxZ).setColor(color.r, color.g, color.b, color.a);
	}

	protected void renderTopQuads(float minX, float minY, float minZ, float maxX, float maxY, float maxZ,
	                              Color4f color, VertexConsumer buffer)
	{
		// Top side
		buffer.addVertex(minX, maxY, maxZ).setColor(color.r, color.g, color.b, color.a);
		buffer.addVertex(maxX, maxY, maxZ).setColor(color.r, color.g, color.b, color.a);
		buffer.addVertex(maxX, maxY, minZ).setColor(color.r, color.g, color.b, color.a);
		buffer.addVertex(minX, maxY, minZ).setColor(color.r, color.g, color.b, color.a);
	}

	protected void renderBottomQuads(float minX, float minY, float minZ, float maxX, float maxY, float maxZ,
	                                 Color4f color, VertexConsumer buffer)
	{
		// Bottom side
		buffer.addVertex(maxX, minY, maxZ).setColor(color.r, color.g, color.b, color.a);
		buffer.addVertex(minX, minY, maxZ).setColor(color.r, color.g, color.b, color.a);
		buffer.addVertex(minX, minY, minZ).setColor(color.r, color.g, color.b, color.a);
		buffer.addVertex(maxX, minY, minZ).setColor(color.r, color.g, color.b, color.a);
	}
}
