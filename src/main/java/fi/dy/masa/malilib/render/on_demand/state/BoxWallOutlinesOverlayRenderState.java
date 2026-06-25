package fi.dy.masa.malilib.render.on_demand.state;

import org.jspecify.annotations.NonNull;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

import fi.dy.masa.malilib.render.MaLiLibPipelines;
import fi.dy.masa.malilib.util.MathUtils;
import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.malilib.util.position.Vec2d;
import fi.dy.masa.malilib.util.position.Vec3d;

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
	public @NonNull RenderPipeline pipeline()
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

		for (AABB box : this.boxes())
		{
			if (lineIntervalV > 0.0)
			{
				double lineY = alignLinesToModulo ? MathUtils.roundUp(box.minY, lineIntervalV) : box.minY;

				while (lineY <= box.maxY)
				{
					consumer.addVertex((float) (box.minX - cx), (float) (lineY - cy), (float) (box.minZ - cz)).setColor(color.r, color.g, color.b, 1.0F).setLineWidth(lineWidth);
					consumer.addVertex((float) (box.maxX - cx), (float) (lineY - cy), (float) (box.maxZ - cz)).setColor(color.r, color.g, color.b, 1.0F).setLineWidth(lineWidth);

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
						consumer.addVertex((float) (box.minX - cx), (float) (box.minY - cy), (float) (lineZ - cz)).setColor(color.r, color.g, color.b, 1.0F).setLineWidth(lineWidth);
						consumer.addVertex((float) (box.minX - cx), (float) (box.maxY - cy), (float) (lineZ - cz)).setColor(color.r, color.g, color.b, 1.0F).setLineWidth(lineWidth);

						lineZ += lineIntervalH;
					}
				}
				else if (box.minZ == box.maxZ)
				{
					double lineX = alignLinesToModulo ? MathUtils.roundUp(box.minX, lineIntervalH) : box.minX;

					while (lineX <= box.maxX)
					{
						consumer.addVertex((float) (lineX - cx), (float) (box.minY - cy), (float) (box.minZ - cz)).setColor(color.r, color.g, color.b, 1.0F).setLineWidth(lineWidth);
						consumer.addVertex((float) (lineX - cx), (float) (box.maxY - cy), (float) (box.minZ - cz)).setColor(color.r, color.g, color.b, 1.0F).setLineWidth(lineWidth);

						lineX += lineIntervalH;
					}
				}
			}
		}
	}
}
