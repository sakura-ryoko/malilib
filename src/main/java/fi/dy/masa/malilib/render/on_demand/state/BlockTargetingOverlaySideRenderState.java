package fi.dy.masa.malilib.render.on_demand.state;

import org.jspecify.annotations.NonNull;

import com.mojang.blaze3d.pipeline.RenderPipeline;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import fi.dy.masa.malilib.render.MaLiLibPipelines;
import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.malilib.util.position.PositionUtils;
import fi.dy.masa.malilib.util.position.Vec3d;

public class BlockTargetingOverlaySideRenderState extends AbstractBlockTargetingOverlayRenderState
{
	public BlockTargetingOverlaySideRenderState(BlockPos pos,
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

		final int quadAlpha = (int) (0.18f * 255f);
		final int hr = (int) (this.color().r * 255f);
		final int hg = (int) (this.color().g * 255f);
		final int hb = (int) (this.color().b * 255f);
		final int ha = (int) (this.color().a * 255f);
		final int c = 255;

		// White full block background
		buffer.vertex((float) (x - 0.5), (float) (y - 0.5), (float) z).color(c, c, c, quadAlpha);
		buffer.vertex((float) (x + 0.5), (float) (y - 0.5), (float) z).color(c, c, c, quadAlpha);
		buffer.vertex((float) (x + 0.5), (float) (y + 0.5), (float) z).color(c, c, c, quadAlpha);
		buffer.vertex((float) (x - 0.5), (float) (y + 0.5), (float) z).color(c, c, c, quadAlpha);

		if (this.isSimple() || this.part() == null) { return; }

		// (Blue?) Hit Part Side
		switch (this.part())
		{
			case CENTER:
				buffer.vertex((float) (x - 0.25), (float) (y - 0.25), (float) z).color(hr, hg, hb, ha);
				buffer.vertex((float) (x + 0.25), (float) (y - 0.25), (float) z).color(hr, hg, hb, ha);
				buffer.vertex((float) (x + 0.25), (float) (y + 0.25), (float) z).color(hr, hg, hb, ha);
				buffer.vertex((float) (x - 0.25), (float) (y + 0.25), (float) z).color(hr, hg, hb, ha);
				break;
			case LEFT:
				buffer.vertex((float) (x - 0.50), (float) (y - 0.50), (float) z).color(hr, hg, hb, ha);
				buffer.vertex((float) (x - 0.25), (float) (y - 0.25), (float) z).color(hr, hg, hb, ha);
				buffer.vertex((float) (x - 0.25), (float) (y + 0.25), (float) z).color(hr, hg, hb, ha);
				buffer.vertex((float) (x - 0.50), (float) (y + 0.50), (float) z).color(hr, hg, hb, ha);
				break;
			case RIGHT:
				buffer.vertex((float) (x + 0.50), (float) (y - 0.50), (float) z).color(hr, hg, hb, ha);
				buffer.vertex((float) (x + 0.25), (float) (y - 0.25), (float) z).color(hr, hg, hb, ha);
				buffer.vertex((float) (x + 0.25), (float) (y + 0.25), (float) z).color(hr, hg, hb, ha);
				buffer.vertex((float) (x + 0.50), (float) (y + 0.50), (float) z).color(hr, hg, hb, ha);
				break;
			case TOP:
				buffer.vertex((float) (x - 0.50), (float) (y + 0.50), (float) z).color(hr, hg, hb, ha);
				buffer.vertex((float) (x - 0.25), (float) (y + 0.25), (float) z).color(hr, hg, hb, ha);
				buffer.vertex((float) (x + 0.25), (float) (y + 0.25), (float) z).color(hr, hg, hb, ha);
				buffer.vertex((float) (x + 0.50), (float) (y + 0.50), (float) z).color(hr, hg, hb, ha);
				break;
			case BOTTOM:
				buffer.vertex((float) (x - 0.50), (float) (y - 0.50), (float) z).color(hr, hg, hb, ha);
				buffer.vertex((float) (x - 0.25), (float) (y - 0.25), (float) z).color(hr, hg, hb, ha);
				buffer.vertex((float) (x + 0.25), (float) (y - 0.25), (float) z).color(hr, hg, hb, ha);
				buffer.vertex((float) (x + 0.50), (float) (y - 0.50), (float) z).color(hr, hg, hb, ha);
				break;
			case null:
				break;
			default:
		}
	}
}
