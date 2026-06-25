package fi.dy.masa.malilib.render.on_demand.state;

import org.jspecify.annotations.NonNull;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import fi.dy.masa.malilib.render.MaLiLibPipelines;
import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.malilib.util.position.Vec3d;

public class SimpleBlockTargetingOverlayLinesRenderState extends AbstractBlockTargetingOverlayRenderState
{
	public SimpleBlockTargetingOverlayLinesRenderState(BlockPos pos,
	                                                   Vec3d camPos,
	                                                   Color4f sideColor,
	                                                   Color4f lineColor,
	                                                   float lineWidth,
	                                                   Direction side,
	                                                   Direction facing)
	{
		super(pos, camPos, sideColor, lineColor, lineWidth, side, facing, null);
	}

	@Override
	public boolean isSimple()
	{
		return true;
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

		final int c = this.color().getIntValue();
		final float lineWidth = this.lineWidth();

		// Middle rectangle
		buffer.addVertex((float) (x - 0.375), (float) (y - 0.375), (float) z).setColor(c, c, c, c).setLineWidth(lineWidth);
		buffer.addVertex((float) (x + 0.375), (float) (y - 0.375), (float) z).setColor(c, c, c, c).setLineWidth(lineWidth);
		buffer.addVertex((float) (x + 0.375), (float) (y + 0.375), (float) z).setColor(c, c, c, c).setLineWidth(lineWidth);
		buffer.addVertex((float) (x - 0.375), (float) (y + 0.375), (float) z).setColor(c, c, c, c).setLineWidth(lineWidth);
	}
}
