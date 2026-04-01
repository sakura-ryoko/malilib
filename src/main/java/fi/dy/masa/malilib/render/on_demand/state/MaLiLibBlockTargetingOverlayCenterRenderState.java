package fi.dy.masa.malilib.render.on_demand.state;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

import fi.dy.masa.malilib.render.MaLiLibPipelines;
import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.malilib.util.position.PositionUtils;

@ApiStatus.Experimental
public class MaLiLibBlockTargetingOverlayCenterRenderState extends AbstractMaLiLibBlockTargetingOverlayRenderState
{
	public MaLiLibBlockTargetingOverlayCenterRenderState(BlockPos pos,
	                                                     Vec3 camPos,
	                                                     Color4f sideColor, Color4f lineColor,
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
		return MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_NO_DEPTH_NO_CULL;
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

		// Middle small rectangle
		buffer.addVertex((float) (x - 0.25), (float) (y - 0.25), (float) z).setColor(c, c, c, c).setLineWidth(lineWidth);
		buffer.addVertex((float) (x + 0.25), (float) (y - 0.25), (float) z).setColor(c, c, c, c).setLineWidth(lineWidth);
		buffer.addVertex((float) (x + 0.25), (float) (y + 0.25), (float) z).setColor(c, c, c, c).setLineWidth(lineWidth);
		buffer.addVertex((float) (x - 0.25), (float) (y + 0.25), (float) z).setColor(c, c, c, c).setLineWidth(lineWidth);
		buffer.addVertex((float) (x - 0.25), (float) (y - 0.25), (float) z).setColor(c, c, c, c).setLineWidth(lineWidth);
	}
}
