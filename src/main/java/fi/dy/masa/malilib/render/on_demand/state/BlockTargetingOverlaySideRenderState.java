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
		buffer.addVertex((float) (x - 0.5), (float) (y - 0.5), (float) z).setColor(c, c, c, quadAlpha);
		buffer.addVertex((float) (x + 0.5), (float) (y - 0.5), (float) z).setColor(c, c, c, quadAlpha);
		buffer.addVertex((float) (x + 0.5), (float) (y + 0.5), (float) z).setColor(c, c, c, quadAlpha);
		buffer.addVertex((float) (x - 0.5), (float) (y + 0.5), (float) z).setColor(c, c, c, quadAlpha);

		if (this.isSimple() || this.part() == null) { return; }

		// (Blue?) Hit Part Side
		switch (this.part())
		{
			case CENTER:
				buffer.addVertex((float) (x - 0.25), (float) (y - 0.25), (float) z).setColor(hr, hg, hb, ha);
				buffer.addVertex((float) (x + 0.25), (float) (y - 0.25), (float) z).setColor(hr, hg, hb, ha);
				buffer.addVertex((float) (x + 0.25), (float) (y + 0.25), (float) z).setColor(hr, hg, hb, ha);
				buffer.addVertex((float) (x - 0.25), (float) (y + 0.25), (float) z).setColor(hr, hg, hb, ha);
				break;
			case LEFT:
				buffer.addVertex((float) (x - 0.50), (float) (y - 0.50), (float) z).setColor(hr, hg, hb, ha);
				buffer.addVertex((float) (x - 0.25), (float) (y - 0.25), (float) z).setColor(hr, hg, hb, ha);
				buffer.addVertex((float) (x - 0.25), (float) (y + 0.25), (float) z).setColor(hr, hg, hb, ha);
				buffer.addVertex((float) (x - 0.50), (float) (y + 0.50), (float) z).setColor(hr, hg, hb, ha);
				break;
			case RIGHT:
				buffer.addVertex((float) (x + 0.50), (float) (y - 0.50), (float) z).setColor(hr, hg, hb, ha);
				buffer.addVertex((float) (x + 0.25), (float) (y - 0.25), (float) z).setColor(hr, hg, hb, ha);
				buffer.addVertex((float) (x + 0.25), (float) (y + 0.25), (float) z).setColor(hr, hg, hb, ha);
				buffer.addVertex((float) (x + 0.50), (float) (y + 0.50), (float) z).setColor(hr, hg, hb, ha);
				break;
			case TOP:
				buffer.addVertex((float) (x - 0.50), (float) (y + 0.50), (float) z).setColor(hr, hg, hb, ha);
				buffer.addVertex((float) (x - 0.25), (float) (y + 0.25), (float) z).setColor(hr, hg, hb, ha);
				buffer.addVertex((float) (x + 0.25), (float) (y + 0.25), (float) z).setColor(hr, hg, hb, ha);
				buffer.addVertex((float) (x + 0.50), (float) (y + 0.50), (float) z).setColor(hr, hg, hb, ha);
				break;
			case BOTTOM:
				buffer.addVertex((float) (x - 0.50), (float) (y - 0.50), (float) z).setColor(hr, hg, hb, ha);
				buffer.addVertex((float) (x - 0.25), (float) (y - 0.25), (float) z).setColor(hr, hg, hb, ha);
				buffer.addVertex((float) (x + 0.25), (float) (y - 0.25), (float) z).setColor(hr, hg, hb, ha);
				buffer.addVertex((float) (x + 0.50), (float) (y - 0.50), (float) z).setColor(hr, hg, hb, ha);
				break;
			case null:
				break;
			default:
		}
	}
}
