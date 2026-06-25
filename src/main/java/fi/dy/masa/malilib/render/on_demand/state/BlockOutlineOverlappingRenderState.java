package fi.dy.masa.malilib.render.on_demand.state;

import javax.annotation.Nonnull;
import org.jspecify.annotations.NonNull;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.core.BlockPos;

import fi.dy.masa.malilib.render.MaLiLibPipelines;
import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.malilib.util.position.Vec3d;

public class BlockOutlineOverlappingRenderState extends AbstractSelectionBoxRenderState
{
	public static final BlockOutlineOverlappingRenderState EMPTY = new BlockOutlineOverlappingRenderState(Vec3d.ZERO, BlockPos.ZERO, 0f, 3f, Color4f.ZERO, Color4f.ZERO, Color4f.ZERO, false)
	{
		@Override
		public boolean isEmpty() {return true;}
	};
	public BlockOutlineOverlappingRenderState(Vec3d camPos,
	                                          @Nonnull BlockPos simplePos,
	                                          float expand, float lineWidth,
	                                          Color4f colorPos1, Color4f colorPos2,
	                                          Color4f colorOverlapping,
	                                          boolean renderThrough)
	{
		super(camPos,
		      BlockPos.ZERO, BlockPos.ZERO, simplePos,
		      expand, lineWidth,
		      colorPos1, colorPos2, colorOverlapping,
		      Color4f.ZERO,
		      renderThrough, false
		);
	}

	@Override
	public boolean isBlockOutline()
	{
		return true;
	}

	@Override
	public boolean isOverlapping()
	{
		return true;
	}

	@Override
	public boolean isEmpty()
	{
		return false;
	}

	@Override
	public @NonNull RenderPipeline pipeline()
	{
		return this.renderThrough
		       ? MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_NO_DEPTH_NO_CULL
		       : MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_LEQUAL_DEPTH;
	}

	@Override
	public double x()
	{
		this.ensureSimplePos();
		return this.simplePos().getX();
	}

	@Override
	public double y()
	{
		this.ensureSimplePos();
		return this.simplePos().getY();
	}

	@Override
	public double z()
	{
		this.ensureSimplePos();
		return this.simplePos().getZ();
	}

	@Override
	public void update(VertexConsumer buffer)
	{
		final double dx = this.camPos().x;
		final double dy = this.camPos().y;
		final double dz = this.camPos().z;

		this.ensureSimplePos();
		final BlockPos pos = this.simplePos();

		final float minX = (float) (pos.getX() - dx - this.expand());
		final float minY = (float) (pos.getY() - dy - this.expand());
		final float minZ = (float) (pos.getZ() - dz - this.expand());
		final float maxX = (float) (pos.getX() - dx + this.expand() + 1);
		final float maxY = (float) (pos.getY() - dy + this.expand() + 1);
		final float maxZ = (float) (pos.getZ() - dz + this.expand() + 1);
		final Color4f color1 = this.color1();
		final Color4f color2 = this.color2();
		final Color4f color3 = this.color3();
		final float lineWidth = this.lineWidth();

		// Min corner
		buffer.addVertex(minX, minY, minZ).setColor(color1.r, color1.g, color1.b, color1.a).setLineWidth(lineWidth);
		buffer.addVertex(maxX, minY, minZ).setColor(color1.r, color1.g, color1.b, color1.a).setLineWidth(lineWidth);

		buffer.addVertex(minX, minY, minZ).setColor(color1.r, color1.g, color1.b, color1.a).setLineWidth(lineWidth);
		buffer.addVertex(minX, maxY, minZ).setColor(color1.r, color1.g, color1.b, color1.a).setLineWidth(lineWidth);

		buffer.addVertex(minX, minY, minZ).setColor(color1.r, color1.g, color1.b, color1.a).setLineWidth(lineWidth);
		buffer.addVertex(minX, minY, maxZ).setColor(color1.r, color1.g, color1.b, color1.a).setLineWidth(lineWidth);

		// Max corner
		buffer.addVertex(minX, maxY, maxZ).setColor(color2.r, color2.g, color2.b, color2.a).setLineWidth(lineWidth);
		buffer.addVertex(maxX, maxY, maxZ).setColor(color2.r, color2.g, color2.b, color2.a).setLineWidth(lineWidth);

		buffer.addVertex(maxX, minY, maxZ).setColor(color2.r, color2.g, color2.b, color2.a).setLineWidth(lineWidth);
		buffer.addVertex(maxX, maxY, maxZ).setColor(color2.r, color2.g, color2.b, color2.a).setLineWidth(lineWidth);

		buffer.addVertex(maxX, maxY, minZ).setColor(color2.r, color2.g, color2.b, color2.a).setLineWidth(lineWidth);
		buffer.addVertex(maxX, maxY, maxZ).setColor(color2.r, color2.g, color2.b, color2.a).setLineWidth(lineWidth);

		// The rest of the edges
		buffer.addVertex(minX, maxY, minZ).setColor(color3.r, color3.g, color3.b, color3.a).setLineWidth(lineWidth);
		buffer.addVertex(maxX, maxY, minZ).setColor(color3.r, color3.g, color3.b, color3.a).setLineWidth(lineWidth);

		buffer.addVertex(minX, minY, maxZ).setColor(color3.r, color3.g, color3.b, color3.a).setLineWidth(lineWidth);
		buffer.addVertex(maxX, minY, maxZ).setColor(color3.r, color3.g, color3.b, color3.a).setLineWidth(lineWidth);

		buffer.addVertex(maxX, minY, minZ).setColor(color3.r, color3.g, color3.b, color3.a).setLineWidth(lineWidth);
		buffer.addVertex(maxX, maxY, minZ).setColor(color3.r, color3.g, color3.b, color3.a).setLineWidth(lineWidth);

		buffer.addVertex(minX, minY, maxZ).setColor(color3.r, color3.g, color3.b, color3.a).setLineWidth(lineWidth);
		buffer.addVertex(minX, maxY, maxZ).setColor(color3.r, color3.g, color3.b, color3.a).setLineWidth(lineWidth);

		buffer.addVertex(maxX, minY, minZ).setColor(color3.r, color3.g, color3.b, color3.a).setLineWidth(lineWidth);
		buffer.addVertex(maxX, minY, maxZ).setColor(color3.r, color3.g, color3.b, color3.a).setLineWidth(lineWidth);

		buffer.addVertex(minX, maxY, minZ).setColor(color3.r, color3.g, color3.b, color3.a).setLineWidth(lineWidth);
		buffer.addVertex(minX, maxY, maxZ).setColor(color3.r, color3.g, color3.b, color3.a).setLineWidth(lineWidth);
	}
}
