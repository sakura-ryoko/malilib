package fi.dy.masa.malilib.render.on_demand.state;

import org.jspecify.annotations.NonNull;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

import fi.dy.masa.malilib.render.MaLiLibPipelines;
import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.malilib.util.position.Vec2d;
import fi.dy.masa.malilib.util.position.Vec3d;

public class BoxWallQuadsOverlayRenderState extends AbstractWallOverlayRenderState
{
	public BoxWallQuadsOverlayRenderState(BlockPos posStart, BlockPos posEnd,
	                                      Vec3d camPos, Color4f quadsColor)
	{
		super(posStart, posEnd, camPos,
		      new Vec2d(16, 16), true,
		      quadsColor, Color4f.ZERO, 3f);
	}

	@Override
	public @NonNull RenderPipeline pipeline()
	{
		return MaLiLibPipelines.MINIHUD_SHAPE_OFFSET_NO_CULL;
	}

	@Override
	public void update(VertexConsumer consumer)
	{
		final double cx = this.camPos().x;
		final double cy = this.camPos().y;
		final double cz = this.camPos().z;
		final Color4f color = this.quadsColor();

		for (AABB box : this.boxes())
		{
			consumer.addVertex((float) (box.minX - cx), (float) (box.maxY - cy), (float) (box.minZ - cz)).setColor(color.r, color.g, color.b, color.a);
			consumer.addVertex((float) (box.minX - cx), (float) (box.minY - cy), (float) (box.minZ - cz)).setColor(color.r, color.g, color.b, color.a);
			consumer.addVertex((float) (box.maxX - cx), (float) (box.minY - cy), (float) (box.maxZ - cz)).setColor(color.r, color.g, color.b, color.a);
			consumer.addVertex((float) (box.maxX - cx), (float) (box.maxY - cy), (float) (box.maxZ - cz)).setColor(color.r, color.g, color.b, color.a);
		}
	}
}
