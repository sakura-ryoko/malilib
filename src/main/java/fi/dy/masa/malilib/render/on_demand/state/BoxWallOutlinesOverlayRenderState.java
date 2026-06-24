package fi.dy.masa.malilib.render.on_demand.state;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.pipeline.RenderPipeline;

import net.minecraft.client.render.VertexConsumer;

import fi.dy.masa.malilib.render.MaLiLibPipelines;
import fi.dy.masa.malilib.util.MathUtils;
import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.malilib.util.position.BlockPos;
import fi.dy.masa.malilib.util.position.Vec2d;
import fi.dy.masa.malilib.util.position.Vec3d;
import fi.dy.masa.malilib.util.position.VecBox;

public class BoxWallOutlinesOverlayRenderState extends AbstractWallOverlayRenderState
{
	public BoxWallOutlinesOverlayRenderState(BlockPos posStart, BlockPos posEnd,
	                                         Vec3d camPos)
	{
		this(posStart, posEnd, camPos, Color4f.WHITE);
	}

	public BoxWallOutlinesOverlayRenderState(BlockPos posStart, BlockPos posEnd,
	                                         Vec3d camPos,
	                                         Color4f linesColor)
	{
		this(posStart, posEnd, camPos,
		     linesColor, 1.6f);
	}

	public BoxWallOutlinesOverlayRenderState(BlockPos posStart, BlockPos posEnd,
	                                         Vec3d camPos,
	                                         Color4f linesColor,
	                                         float linesWidth)
	{
		this(posStart, posEnd, camPos,
		      new Vec2d(16, 16), true,
		      linesColor, linesWidth);
	}

	public BoxWallOutlinesOverlayRenderState(BlockPos posStart, BlockPos posEnd,
	                                         Vec3d camPos,
	                                         double lineIntervalH, double lineIntervalV,
	                                         boolean alignLinesToModulo,
	                                         Color4f linesColor,
	                                         float linesWidth)
	{
		this(posStart, posEnd, camPos, new Vec2d(lineIntervalH, lineIntervalV), alignLinesToModulo, linesColor, linesWidth);
	}

	public BoxWallOutlinesOverlayRenderState(BlockPos posStart, BlockPos posEnd,
	                                         Vec3d camPos,
	                                         Vec2d lineIntervals, boolean alignLinesToModulo,
	                                         Color4f linesColor,
	                                         float linesWidth)
	{
		super(posStart, posEnd, camPos,
		      lineIntervals, alignLinesToModulo,
		      Color4f.ZERO, linesColor, linesWidth);
	}

	@Override
	public @Nonnull RenderPipeline pipeline()
	{
		return MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_LEQUAL_DEPTH;
	}

	@Override
	public void update(VertexConsumer consumer)
	{
		final double cx = this.camPos().x;
		final double cy = this.camPos().y;
		final double cz = this.camPos().z;
		final double lineIntervalH = this.lineIntervalH();
		final double lineIntervalV = this.lineIntervalV();
		final boolean alignLinesToModulo = this.alignLinesToModulo();
		final Color4f color = this.linesColor();
		final float lineWidth = this.linesWidth();

		// .setLineWidth(lineWidth)
		for (VecBox box : this.boxes())
		{
			if (lineIntervalV > 0.0)
			{
				double lineY = alignLinesToModulo ? MathUtils.roundUp(box.minY, lineIntervalV) : box.minY;

				while (lineY <= box.maxY)
				{
					consumer.vertex((float) (box.minX - cx), (float) (lineY - cy), (float) (box.minZ - cz)).color(color.r, color.g, color.b, 1.0F);
					consumer.vertex((float) (box.maxX - cx), (float) (lineY - cy), (float) (box.maxZ - cz)).color(color.r, color.g, color.b, 1.0F);

					lineY += lineIntervalV;
				}
			}

			if (lineIntervalH > 0.0)
			{
				if (box.minX == box.maxX)
				{
					double lineZ = alignLinesToModulo ? MathUtils.roundUp(box.minZ, lineIntervalH) : box.minZ;

					while (lineZ <= box.maxZ)
					{
						consumer.vertex((float) (box.minX - cx), (float) (box.minY - cy), (float) (lineZ - cz)).color(color.r, color.g, color.b, 1.0F);
						consumer.vertex((float) (box.minX - cx), (float) (box.maxY - cy), (float) (lineZ - cz)).color(color.r, color.g, color.b, 1.0F);

						lineZ += lineIntervalH;
					}
				}
				else if (box.minZ == box.maxZ)
				{
					double lineX = alignLinesToModulo ? MathUtils.roundUp(box.minX, lineIntervalH) : box.minX;

					while (lineX <= box.maxX)
					{
						consumer.vertex((float) (lineX - cx), (float) (box.minY - cy), (float) (box.minZ - cz)).color(color.r, color.g, color.b, 1.0F);
						consumer.vertex((float) (lineX - cx), (float) (box.maxY - cy), (float) (box.minZ - cz)).color(color.r, color.g, color.b, 1.0F);

						lineX += lineIntervalH;
					}
				}
			}
		}
	}
}
