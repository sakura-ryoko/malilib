package fi.dy.masa.malilib.render.on_demand.state;

import org.jspecify.annotations.NonNull;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import fi.dy.masa.malilib.render.MaLiLibPipelines;
import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.malilib.util.position.Vec3d;

public class SimpleBlockTargetingOverlayQuadsRenderState extends AbstractBlockTargetingOverlayRenderState
{
	public SimpleBlockTargetingOverlayQuadsRenderState(BlockPos pos,
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
		return MaLiLibPipelines.POSITION_COLOR_MASA_NO_DEPTH_NO_CULL;
	}

	@Override
	public @NonNull Color4f color()
	{
		return this.sideColor();
	}

	@Override
	public void update(VertexConsumer buffer)
	{
		final double x = this.x();
		final double y = this.y();
		final double z = this.z();

		int a = (int) (this.color().a * 255f);
		int r = (int) (this.color().r * 255f);
		int g = (int) (this.color().g * 255f);
		int b = (int) (this.color().b * 255f);

		// Simple colored quad
		buffer.addVertex((float) (x - 0.5), (float) (y - 0.5), (float) z).setColor(r, g, b, a);
		buffer.addVertex((float) (x + 0.5), (float) (y - 0.5), (float) z).setColor(r, g, b, a);
		buffer.addVertex((float) (x + 0.5), (float) (y + 0.5), (float) z).setColor(r, g, b, a);
		buffer.addVertex((float) (x - 0.5), (float) (y + 0.5), (float) z).setColor(r, g, b, a);
	}
}
