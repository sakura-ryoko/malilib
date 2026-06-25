package fi.dy.masa.malilib.render.on_demand.state;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.malilib.util.position.Vec2d;
import fi.dy.masa.malilib.util.position.Vec3d;

public class CenterRangeWallOutlinesOverlayRenderState extends BoxWallOutlinesOverlayRenderState
{
	private CenterRangeWallOutlinesOverlayRenderState(BlockPos posStart, BlockPos posEnd, Vec3d camPos)
	{
		super(posStart, posEnd, camPos);
	}

	private CenterRangeWallOutlinesOverlayRenderState(BlockPos posStart, BlockPos posEnd, Vec3d camPos, Color4f linesColor)
	{
		super(posStart, posEnd, camPos, linesColor);
	}

	private CenterRangeWallOutlinesOverlayRenderState(BlockPos posStart, BlockPos posEnd, Vec3d camPos, Color4f linesColor, float linesWidth)
	{
		super(posStart, posEnd, camPos, linesColor, linesWidth);
	}

	private CenterRangeWallOutlinesOverlayRenderState(BlockPos posStart, BlockPos posEnd, Vec3d camPos, double lineIntervalH, double lineIntervalV, boolean alignLinesToModulo, Color4f linesColor, float linesWidth)
	{
		super(posStart, posEnd, camPos, lineIntervalH, lineIntervalV, alignLinesToModulo, linesColor, linesWidth);
	}

	private CenterRangeWallOutlinesOverlayRenderState(BlockPos posStart, BlockPos posEnd, Vec3d camPos, Vec2d lineIntervals, boolean alignLinesToModulo, Color4f linesColor, float linesWidth)
	{
		super(posStart, posEnd, camPos, lineIntervals, alignLinesToModulo, linesColor, linesWidth);
	}

	private static Pair<BlockPos, BlockPos> calculatePositions(BlockPos posCenter, int chunkRange, Level level)
	{
		final int cx = (posCenter.getX() >> 4);
		final int cz = (posCenter.getZ() >> 4);
		final int minY = level != null ? level.getMinY() : -64;
		final int maxY = level != null ? level.getMaxY() + 1 : 320;
		BlockPos pos1 = new BlockPos((cx - chunkRange) << 4       , minY, (cz - chunkRange) << 4);
		BlockPos pos2 = new BlockPos(((cx + chunkRange) << 4) + 15, maxY, ((cz + chunkRange) << 4) + 15);

		return Pair.of(pos1, pos2);
	}

	public static CenterRangeWallOutlinesOverlayRenderState create(BlockPos posCenter, int chunkRange, Level level, Vec3d camPos)
	{
		Pair<BlockPos, BlockPos> pair = calculatePositions(posCenter, chunkRange, level);
		return new CenterRangeWallOutlinesOverlayRenderState(pair.getLeft(), pair.getRight(), camPos);
	}

	public static CenterRangeWallOutlinesOverlayRenderState create(BlockPos posCenter, int chunkRange, Level level, Vec3d camPos,
	                                                               Color4f linesColor)
	{
		Pair<BlockPos, BlockPos> pair = calculatePositions(posCenter, chunkRange, level);
		return new CenterRangeWallOutlinesOverlayRenderState(pair.getLeft(), pair.getRight(), camPos, linesColor);
	}

	public static CenterRangeWallOutlinesOverlayRenderState create(BlockPos posCenter, int chunkRange, Level level, Vec3d camPos,
	                                                               Color4f linesColor, float linesWidth)
	{
		Pair<BlockPos, BlockPos> pair = calculatePositions(posCenter, chunkRange, level);
		return new CenterRangeWallOutlinesOverlayRenderState(pair.getLeft(), pair.getRight(), camPos, linesColor, linesWidth);
	}

	public static CenterRangeWallOutlinesOverlayRenderState create(BlockPos posCenter, int chunkRange, Level level, Vec3d camPos,
	                                                               double lineIntervalH, double lineIntervalV, boolean alignLinesToModulo,
	                                                               Color4f linesColor, float linesWidth)
	{
		Pair<BlockPos, BlockPos> pair = calculatePositions(posCenter, chunkRange, level);
		return new CenterRangeWallOutlinesOverlayRenderState(pair.getLeft(), pair.getRight(), camPos,
		                                                     lineIntervalH,  lineIntervalV, alignLinesToModulo,
		                                                     linesColor, linesWidth);
	}

	public static CenterRangeWallOutlinesOverlayRenderState create(BlockPos posCenter, int chunkRange, Level level, Vec3d camPos,
	                                                               Vec2d lineIntervals, boolean alignLinesToModulo,
	                                                               Color4f linesColor, float linesWidth)
	{
		Pair<BlockPos, BlockPos> pair = calculatePositions(posCenter, chunkRange, level);
		return new CenterRangeWallOutlinesOverlayRenderState(pair.getLeft(), pair.getRight(), camPos,
		                                                     lineIntervals, alignLinesToModulo,
		                                                     linesColor, linesWidth);
	}
}
