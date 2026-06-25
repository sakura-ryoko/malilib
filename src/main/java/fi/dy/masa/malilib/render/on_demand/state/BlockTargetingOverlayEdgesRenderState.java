package fi.dy.masa.malilib.render.on_demand.state;

import org.jspecify.annotations.NonNull;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import fi.dy.masa.malilib.render.MaLiLibPipelines;
import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.malilib.util.position.PositionUtils;
import fi.dy.masa.malilib.util.position.Vec3d;

public class BlockTargetingOverlayEdgesRenderState extends AbstractBlockTargetingOverlayRenderState
{
	public BlockTargetingOverlayEdgesRenderState(BlockPos pos,
	                                             Vec3d camPos,
	                                             Color4f sideColor,
	                                             Color4f lineColor,
	                                             float lineWidth,
	                                             Direction side,
	                                             Direction facing,
	                                             PositionUtils.HitPart part)
	{
		super(pos, camPos, sideColor, lineColor, lineWidth, side, facing, part);
	}

	@Override
	public @NonNull RenderPipeline pipeline()
	{
		return MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_NO_DEPTH_NO_CULL;
	}

	@Override
	public @NonNull Color4f color()
	{
		return this.lineColor();
	}

	@Override
	public void update(VertexConsumer buffer)
	{
		final double x = this.x();
		final double y = this.y();
		final double z = this.z();

		final int c = this.lineColor().getIntValue();
		final float lineWidth = this.lineWidth();

		// Bottom left
		buffer.addVertex((float) (x - 0.50), (float) (y - 0.50), (float) z).setColor(c, c, c, c).setLineWidth(lineWidth);
		buffer.addVertex((float) (x - 0.25), (float) (y - 0.25), (float) z).setColor(c, c, c, c).setLineWidth(lineWidth);

		// Top left
		buffer.addVertex((float) (x - 0.50), (float) (y + 0.50), (float) z).setColor(c, c, c, c).setLineWidth(lineWidth);
		buffer.addVertex((float) (x - 0.25), (float) (y + 0.25), (float) z).setColor(c, c, c, c).setLineWidth(lineWidth);

		// Bottom right
		buffer.addVertex((float) (x + 0.50), (float) (y - 0.50), (float) z).setColor(c, c, c, c).setLineWidth(lineWidth);
		buffer.addVertex((float) (x + 0.25), (float) (y - 0.25), (float) z).setColor(c, c, c, c).setLineWidth(lineWidth);

		// Top right
		buffer.addVertex((float) (x + 0.50), (float) (y + 0.50), (float) z).setColor(c, c, c, c).setLineWidth(lineWidth);
		buffer.addVertex((float) (x + 0.25), (float) (y + 0.25), (float) z).setColor(c, c, c, c).setLineWidth(lineWidth);
	}
}
