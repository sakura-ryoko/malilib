package fi.dy.masa.malilib.render.on_demand.state;

import javax.annotation.Nonnull;
import org.jspecify.annotations.NonNull;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.core.BlockPos;

import fi.dy.masa.malilib.render.MaLiLibPipelines;
import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.malilib.util.position.Vec3d;

public class BlockOutlineRenderState extends AbstractSelectionBoxRenderState
{
	public static final BlockOutlineRenderState EMPTY = new BlockOutlineRenderState(Vec3d.ZERO, BlockPos.ZERO, 1f, 3f, Color4f.ZERO, Color4f.ZERO, false)
	{
		@Override
		public boolean isEmpty() { return true; }
	};
	public BlockOutlineRenderState(Vec3d camPos,
	                               @Nonnull BlockPos simplePos,
	                               float expand, float lineWidth,
	                               Color4f sidesColor, Color4f linesColor,
	                               boolean renderThrough)
	{
		super(camPos,
		      BlockPos.ZERO, BlockPos.ZERO, simplePos,
		      expand, lineWidth,
		      sidesColor, linesColor,
		      Color4f.ZERO, Color4f.ZERO,
		      renderThrough, false
		);
	}

	@Override
	public boolean isSimple()
	{
		return true;
	}

	@Override
	public boolean isBlockOutline()
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

		float minX = (float) (pos.getX() - dx - this.expand());
		float minY = (float) (pos.getY() - dy - this.expand());
		float minZ = (float) (pos.getZ() - dz - this.expand());
		float maxX = (float) (pos.getX() - dx + this.expand() + 1);
		float maxY = (float) (pos.getY() - dy + this.expand() + 1);
		float maxZ = (float) (pos.getZ() - dz + this.expand() + 1);
		final Color4f color = this.color2();
		final float lineWidth = this.lineWidth();

		this.renderOutlinesAllSides(minX, minY, minZ, maxX, maxY, maxZ, color, lineWidth, buffer);
	}
}
