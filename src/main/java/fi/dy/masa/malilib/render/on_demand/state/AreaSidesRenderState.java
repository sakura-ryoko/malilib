package fi.dy.masa.malilib.render.on_demand.state;

import org.jspecify.annotations.NonNull;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.core.BlockPos;

import fi.dy.masa.malilib.render.MaLiLibPipelines;
import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.malilib.util.position.Vec3d;

public class AreaSidesRenderState extends AbstractSelectionBoxRenderState
{
	public static final AreaSidesRenderState EMPTY = new AreaSidesRenderState(Vec3d.ZERO, BlockPos.ZERO, BlockPos.ZERO, Color4f.ZERO, false)
	{
		@Override
		public boolean isEmpty() { return true; }
	};
	private final boolean isCameraInside;

	public AreaSidesRenderState(Vec3d camPos,
	                            BlockPos pos1, BlockPos pos2,
	                            Color4f sidesColor,
	                            boolean shouldResort)
	{
		super(camPos,
		      pos1, pos2, null,
		      0.002f, 3f,
		      sidesColor, Color4f.ZERO,
		      Color4f.ZERO, Color4f.ZERO,
		      false, shouldResort
		);
		this.isCameraInside = this.checkIfCameraInsideOf();
	}

	@Override
	public boolean isAreaSides()
	{
		return true;
	}

	@Override
	public boolean isEmpty()
	{
		return false;
	}

	public boolean isCameraInside()
	{
		return this.isCameraInside;
	}

	@Override
	public @NonNull RenderPipeline pipeline()
	{
		return this.isCameraInside()
		       ? MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_3
		       : MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_LEQUAL_DEPTH;
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
		final double dx = this.camPos().x;
		final double dy = this.camPos().y;
		final double dz = this.camPos().z;

		double minX;
		double minY;
		double minZ;
		double maxX;
		double maxY;
		double maxZ;

		if (this.pos1().equals(this.pos2()))
		{
			minX = (float) (this.pos1().getX() - dx - this.expand());
			minY = (float) (this.pos1().getY() - dy - this.expand());
			minZ = (float) (this.pos1().getZ() - dz - this.expand());
			maxX = (float) (this.pos1().getX() - dx + this.expand() + 1);
			maxY = (float) (this.pos1().getY() - dy + this.expand() + 1);
			maxZ = (float) (this.pos1().getZ() - dz + this.expand() + 1);
		}
		else
		{
			minX = Math.min(this.pos1().getX(), this.pos2().getX()) - dx - this.expand();
			minY = Math.min(this.pos1().getY(), this.pos2().getY()) - dy - this.expand();
			minZ = Math.min(this.pos1().getZ(), this.pos2().getZ()) - dz - this.expand();
			maxX = Math.max(this.pos1().getX(), this.pos2().getX()) + 1 - dx + this.expand();
			maxY = Math.max(this.pos1().getY(), this.pos2().getY()) + 1 - dy + this.expand();
			maxZ = Math.max(this.pos1().getZ(), this.pos2().getZ()) + 1 - dz + this.expand();
		}

		this.renderHorizonalQuads((float) minX, (float) minY, (float) minZ, (float) maxX, (float) maxY, (float) maxZ, this.color1(), buffer);
		this.renderTopQuads((float) minX, (float) minY, (float) minZ, (float) maxX, (float) maxY, (float) maxZ, this.color1(), buffer);
		this.renderBottomQuads((float) minX, (float) minY, (float) minZ, (float) maxX, (float) maxY, (float) maxZ, this.color1(), buffer);
	}
}
