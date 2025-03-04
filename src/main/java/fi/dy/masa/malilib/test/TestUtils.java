package fi.dy.masa.malilib.test;

import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.EntityUtils;
import fi.dy.masa.malilib.util.data.Color4f;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public class TestUtils
{
    public static void drawBlockBoundingBoxSidesBatchedQuads(BlockPos pos, Vec3d cameraPos, Color4f color, double expand, BufferBuilder buffer)
    {
        float minX = (float) (pos.getX() - cameraPos.x - expand);
        float minY = (float) (pos.getY() - cameraPos.y - expand);
        float minZ = (float) (pos.getZ() - cameraPos.z - expand);
        float maxX = (float) (pos.getX() - cameraPos.x + expand + 1);
        float maxY = (float) (pos.getY() - cameraPos.y + expand + 1);
        float maxZ = (float) (pos.getZ() - cameraPos.z + expand + 1);

        RenderUtils.drawBoxAllSidesBatchedQuads(minX, minY, minZ, maxX, maxY, maxZ, color, buffer);
    }

    protected static Pair<BlockPos, BlockPos> getSpawnChunkCorners(BlockPos worldSpawn, int chunkRange, World world)
    {
        int cx = (worldSpawn.getX() >> 4);
        int cz = (worldSpawn.getZ() >> 4);
        int minY = getMinY(world);
        int maxY = world != null ? world.getTopYInclusive() + 1 : 320;
        BlockPos pos1 = new BlockPos( (cx - chunkRange) << 4      , minY,  (cz - chunkRange) << 4);
        BlockPos pos2 = new BlockPos(((cx + chunkRange) << 4) + 15, maxY, ((cz + chunkRange) << 4) + 15);

        return Pair.of(pos1, pos2);
    }

    private static int getMinY(World world)
    {
        MinecraftClient mc = MinecraftClient.getInstance();
        int minY;

        // For whatever reason, in Fabulous! Graphics, the Y level gets rendered through to -64,
        //  so let's make use of the player's current Y position, and seaLevel.
        if (MinecraftClient.isFabulousGraphicsOrBetter() && world != null && mc.player != null)
        {
            if (mc.player.getBlockPos().getY() >= world.getSeaLevel())
            {
                minY = world.getSeaLevel() - 2;
            }
            else
            {
                minY = world.getBottomY();
            }
        }
        else
        {
            minY = world != null ? world.getBottomY() : -64;
        }

        return minY;
    }

    public static void renderWallsWithLines(
            BlockPos posStart,
            BlockPos posEnd,
            Vec3d cameraPos,
            double lineIntervalH,
            double lineIntervalV,
            boolean alignLinesToModulo,
            Color4f quadsColor,
            BufferBuilder bufferQuads,
            Color4f linesColor,
            BufferBuilder bufferLines)
    {
        Entity entity = EntityUtils.getCameraEntity();
        final int boxMinX = Math.min(posStart.getX(), posEnd.getX());
        final int boxMinZ = Math.min(posStart.getZ(), posEnd.getZ());
        final int boxMaxX = Math.max(posStart.getX(), posEnd.getX());
        final int boxMaxZ = Math.max(posStart.getZ(), posEnd.getZ());

        final int centerX = (int) Math.floor(entity.getX());
        final int centerZ = (int) Math.floor(entity.getZ());
        final int maxDist = MinecraftClient.getInstance().options.getViewDistance().getValue() * 32; // double the view distance in blocks
        final int rangeMinX = centerX - maxDist;
        final int rangeMinZ = centerZ - maxDist;
        final int rangeMaxX = centerX + maxDist;
        final int rangeMaxZ = centerZ + maxDist;
        final double minY = Math.min(posStart.getY(), posEnd.getY());
        final double maxY = Math.max(posStart.getY(), posEnd.getY()) + 1;
        double minX, minZ, maxX, maxZ;

        // The sides of the box along the x-axis can be at least partially inside the range
        if (rangeMinX <= boxMaxX && rangeMaxX >= boxMinX)
        {
            minX = Math.max(boxMinX, rangeMinX);
            maxX = Math.min(boxMaxX, rangeMaxX) + 1;

            if (rangeMinZ <= boxMinZ && rangeMaxZ >= boxMinZ)
            {
                minZ = maxZ = boxMinZ;
                renderWallWithLines((float) minX, (float) minY, (float) minZ, (float) maxX, (float) maxY, (float) maxZ, lineIntervalH, lineIntervalV, alignLinesToModulo, cameraPos, quadsColor, bufferQuads, linesColor, bufferLines);
            }

            if (rangeMinZ <= boxMaxZ && rangeMaxZ >= boxMaxZ)
            {
                minZ = maxZ = boxMaxZ + 1;
                renderWallWithLines((float) minX, (float) minY, (float) minZ, (float) maxX, (float) maxY, (float) maxZ, lineIntervalH, lineIntervalV, alignLinesToModulo, cameraPos, quadsColor, bufferQuads, linesColor, bufferLines);
            }
        }

        // The sides of the box along the z-axis can be at least partially inside the range
        if (rangeMinZ <= boxMaxZ && rangeMaxZ >= boxMinZ)
        {
            minZ = Math.max(boxMinZ, rangeMinZ);
            maxZ = Math.min(boxMaxZ, rangeMaxZ) + 1;

            if (rangeMinX <= boxMinX && rangeMaxX >= boxMinX)
            {
                minX = maxX = boxMinX;
                renderWallWithLines((float) minX, (float) minY, (float) minZ, (float) maxX, (float) maxY, (float) maxZ, lineIntervalH, lineIntervalV, alignLinesToModulo, cameraPos, quadsColor, bufferQuads, linesColor, bufferLines);
            }

            if (rangeMinX <= boxMaxX && rangeMaxX >= boxMaxX)
            {
                minX = maxX = boxMaxX + 1;
                renderWallWithLines((float) minX, (float) minY, (float) minZ, (float) maxX, (float) maxY, (float) maxZ, lineIntervalH, lineIntervalV, alignLinesToModulo, cameraPos, quadsColor, bufferQuads, linesColor, bufferLines);
            }
        }
    }

    public static void renderWallWithLines(
            double minX, double minY, double minZ,
            double maxX, double maxY, double maxZ,
            double lineIntervalH, double lineIntervalV,
            boolean alignLinesToModulo,
            Vec3d cameraPos,
            Color4f qColor,
            BufferBuilder bufferQuads,
            Color4f lColor,
            BufferBuilder bufferLines)
    {
        double cx = cameraPos.x;
        double cy = cameraPos.y;
        double cz = cameraPos.z;

        bufferQuads.vertex((float) (minX - cx), (float) (maxY - cy), (float) (minZ - cz)).color(qColor.r, qColor.g, qColor.b, qColor.a);
        bufferQuads.vertex((float) (minX - cx), (float) (minY - cy), (float) (minZ - cz)).color(qColor.r, qColor.g, qColor.b, qColor.a);
        bufferQuads.vertex((float) (maxX - cx), (float) (minY - cy), (float) (maxZ - cz)).color(qColor.r, qColor.g, qColor.b, qColor.a);
        bufferQuads.vertex((float) (maxX - cx), (float) (maxY - cy), (float) (maxZ - cz)).color(qColor.r, qColor.g, qColor.b, qColor.a);

        if (lineIntervalV > 0.0)
        {
            double lineY = alignLinesToModulo ? roundUp(minY, lineIntervalV) : minY;

            while (lineY <= maxY)
            {
                bufferLines.vertex((float) (minX - cx), (float) (lineY - cy), (float) (minZ - cz)).color(lColor.r, lColor.g, lColor.b, 1.0F);
                bufferLines.vertex((float) (maxX - cx), (float) (lineY - cy), (float) (maxZ - cz)).color(lColor.r, lColor.g, lColor.b, 1.0F);
                lineY += lineIntervalV;
            }
        }

        if (lineIntervalH > 0.0)
        {
            if (minX == maxX)
            {
                double lineZ = alignLinesToModulo ? roundUp(minZ, lineIntervalH) : minZ;

                while (lineZ <= maxZ)
                {
                    bufferLines.vertex((float) (minX - cx), (float) (minY - cy), (float) (lineZ - cz)).color(lColor.r, lColor.g, lColor.b, 1.0F);
                    bufferLines.vertex((float) (minX - cx), (float) (maxY - cy), (float) (lineZ - cz)).color(lColor.r, lColor.g, lColor.b, 1.0F);
                    lineZ += lineIntervalH;
                }
            }
            else if (minZ == maxZ)
            {
                double lineX = alignLinesToModulo ? roundUp(minX, lineIntervalH) : minX;

                while (lineX <= maxX)
                {
                    bufferLines.vertex((float) (lineX - cx), (float) (minY - cy), (float) (minZ - cz)).color(lColor.r, lColor.g, lColor.b, 1.0F);
                    bufferLines.vertex((float) (lineX - cx), (float) (maxY - cy), (float) (minZ - cz)).color(lColor.r, lColor.g, lColor.b, 1.0F);
                    lineX += lineIntervalH;
                }
            }
        }
    }

    public static double roundUp(double value, double interval)
    {
        if (interval == 0.0)
        {
            return 0.0;
        }
        else if (value == 0.0)
        {
            return interval;
        }
        else
        {
            if (value < 0.0)
            {
                interval *= -1.0;
            }

            double remainder = value % interval;

            return remainder == 0.0 ? value : value + interval - remainder;
        }
    }
}
