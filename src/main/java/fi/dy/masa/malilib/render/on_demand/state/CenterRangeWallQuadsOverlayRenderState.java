package fi.dy.masa.malilib.render.on_demand.state;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.malilib.util.position.Vec3d;

public class CenterRangeWallQuadsOverlayRenderState extends BoxWallQuadsOverlayRenderState
{
	private CenterRangeWallQuadsOverlayRenderState(BlockPos posStart, BlockPos posEnd, Vec3d camPos, Color4f quadsColor)
	{
		super(posStart, posEnd, camPos, quadsColor);
	}

	public static CenterRangeWallQuadsOverlayRenderState create(BlockPos posCenter, int chunkRange, Level level,
	                                                            Vec3d camPos, Color4f quadsColor)
	{
		final int cx = (posCenter.getX() >> 4);
		final int cz = (posCenter.getZ() >> 4);
		final int minY = level != null ? level.getMinY() : -64;
		final int maxY = level != null ? level.getMaxY() + 1 : 320;
		BlockPos pos1 = new BlockPos((cx - chunkRange) << 4       , minY, (cz - chunkRange) << 4);
		BlockPos pos2 = new BlockPos(((cx + chunkRange) << 4) + 15, maxY, ((cz + chunkRange) << 4) + 15);

		return new CenterRangeWallQuadsOverlayRenderState(pos1, pos2, camPos, quadsColor);
	}
}
