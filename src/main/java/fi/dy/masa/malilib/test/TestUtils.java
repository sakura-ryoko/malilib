package fi.dy.masa.malilib.test;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;
import fi.dy.masa.malilib.util.EntityUtils;
import fi.dy.masa.malilib.util.data.Color4f;

@ApiStatus.Experimental
public class TestUtils
{
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

    public static List<Box> calculateBoxes(
            BlockPos posStart,
            BlockPos posEnd)
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

        List<Box> boxes = new ArrayList<>();

        // The sides of the box along the x-axis can be at least partially inside the range
        if (rangeMinX <= boxMaxX && rangeMaxX >= boxMinX)
        {
            minX = Math.max(boxMinX, rangeMinX);
            maxX = Math.min(boxMaxX, rangeMaxX) + 1;

            if (rangeMinZ <= boxMinZ && rangeMaxZ >= boxMinZ)
            {
                minZ = maxZ = boxMinZ;
//                renderWallOutlines((float) minX, (float) minY, (float) minZ, (float) maxX, (float) maxY, (float) maxZ, lineIntervalH, lineIntervalV, alignLinesToModulo, cameraPos, linesColor, bufferQuads, matrices.peek());
//                renderWallQuads((float) minX, (float) minY, (float) minZ, (float) maxX, (float) maxY, (float) maxZ, cameraPos, quadsColor, bufferQuads);
                boxes.add(new Box(minX, minY, minZ, maxX, maxY, maxZ));
            }

            if (rangeMinZ <= boxMaxZ && rangeMaxZ >= boxMaxZ)
            {
                minZ = maxZ = boxMaxZ + 1;
//                renderWallOutlines((float) minX, (float) minY, (float) minZ, (float) maxX, (float) maxY, (float) maxZ, lineIntervalH, lineIntervalV, alignLinesToModulo, cameraPos, linesColor, bufferLines, matrices.peek());
//                renderWallQuads((float) minX, (float) minY, (float) minZ, (float) maxX, (float) maxY, (float) maxZ, cameraPos, quadsColor, bufferQuads);
                boxes.add(new Box(minX, minY, minZ, maxX, maxY, maxZ));
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
//                renderWallOutlines((float) minX, (float) minY, (float) minZ, (float) maxX, (float) maxY, (float) maxZ, lineIntervalH, lineIntervalV, alignLinesToModulo, cameraPos, linesColor, bufferLines, matrices.peek());
//                renderWallQuads((float) minX, (float) minY, (float) minZ, (float) maxX, (float) maxY, (float) maxZ, cameraPos, quadsColor, bufferQuads);
                boxes.add(new Box(minX, minY, minZ, maxX, maxY, maxZ));
            }

            if (rangeMinX <= boxMaxX && rangeMaxX >= boxMaxX)
            {
                minX = maxX = boxMaxX + 1;
//                renderWallOutlines((float) minX, (float) minY, (float) minZ, (float) maxX, (float) maxY, (float) maxZ, lineIntervalH, lineIntervalV, alignLinesToModulo, cameraPos, linesColor, bufferLines, matrices.peek());
//                renderWallQuads((float) minX, (float) minY, (float) minZ, (float) maxX, (float) maxY, (float) maxZ, cameraPos, quadsColor, bufferQuads);
                boxes.add(new Box(minX, minY, minZ, maxX, maxY, maxZ));
            }
        }

        return boxes;
    }

    public static void renderWallQuads(Box box, Vec3d cameraPos, Color4f color, BufferBuilder bufferQuads)
    {
        double cx = cameraPos.x;
        double cy = cameraPos.y;
        double cz = cameraPos.z;

        bufferQuads.vertex((float) (box.minX - cx), (float) (box.maxY - cy), (float) (box.minZ - cz)).color(color.r, color.g, color.b, color.a);
        bufferQuads.vertex((float) (box.minX - cx), (float) (box.minY - cy), (float) (box.minZ - cz)).color(color.r, color.g, color.b, color.a);
        bufferQuads.vertex((float) (box.maxX - cx), (float) (box.minY - cy), (float) (box.maxZ - cz)).color(color.r, color.g, color.b, color.a);
        bufferQuads.vertex((float) (box.maxX - cx), (float) (box.maxY - cy), (float) (box.maxZ - cz)).color(color.r, color.g, color.b, color.a);
    }

//    public static void renderWallQuads(Box box, Vec3d cameraPos, Color4f color, BufferBuilder bufferQuads, MatrixStack.Entry e)
//    {
//        double cx = cameraPos.x;
//        double cy = cameraPos.y;
//        double cz = cameraPos.z;
//
//        bufferQuads.vertex(e, (float) (box.minX - cx), (float) (box.maxY - cy), (float) (box.minZ - cz)).color(color.r, color.g, color.b, color.a);
//        bufferQuads.vertex(e, (float) (box.minX - cx), (float) (box.minY - cy), (float) (box.minZ - cz)).color(color.r, color.g, color.b, color.a);
//        bufferQuads.vertex(e, (float) (box.maxX - cx), (float) (box.minY - cy), (float) (box.maxZ - cz)).color(color.r, color.g, color.b, color.a);
//        bufferQuads.vertex(e, (float) (box.maxX - cx), (float) (box.maxY - cy), (float) (box.maxZ - cz)).color(color.r, color.g, color.b, color.a);
//    }

    public static void renderWallOutlines(
            Box box,
            double lineIntervalH, double lineIntervalV,
            boolean alignLinesToModulo,
            Vec3d cameraPos,
            Color4f color,
//            BufferBuilder bufferLines, MatrixStack.Entry e)
            BufferBuilder bufferLines)
    {
        double cx = cameraPos.x;
        double cy = cameraPos.y;
        double cz = cameraPos.z;

        if (lineIntervalV > 0.0)
        {
            double lineY = alignLinesToModulo ? roundUp(box.minY, lineIntervalV) : box.minY;

            while (lineY <= box.maxY)
            {
                bufferLines.vertex((float) (box.minX - cx), (float) (lineY - cy), (float) (box.minZ - cz)).color(color.r, color.g, color.b, 1.0F);
                bufferLines.vertex((float) (box.maxX - cx), (float) (lineY - cy), (float) (box.maxZ - cz)).color(color.r, color.g, color.b, 1.0F);

//                bufferLines.vertex(e, (float) (box.minX - cx), (float) (lineY - cy), (float) (box.minZ - cz)).color(color.r, color.g, color.b, 1.0F).normal(e, 0.0f, 0.0f, 0.0f);
//                bufferLines.vertex(e, (float) (box.maxX - cx), (float) (lineY - cy), (float) (box.maxZ - cz)).color(color.r, color.g, color.b, 1.0F).normal(e, 0.0f, 0.0f, 0.0f);
                lineY += lineIntervalV;
            }
        }

        if (lineIntervalH > 0.0)
        {
            if (box.minX == box.maxX)
            {
                double lineZ = alignLinesToModulo ? roundUp(box.minZ, lineIntervalH) : box.minZ;

                while (lineZ <= box.maxZ)
                {
                    bufferLines.vertex((float) (box.minX - cx), (float) (box.minY - cy), (float) (lineZ - cz)).color(color.r, color.g, color.b, 1.0F);
                    bufferLines.vertex((float) (box.minX - cx), (float) (box.maxY - cy), (float) (lineZ - cz)).color(color.r, color.g, color.b, 1.0F);

//                    bufferLines.vertex(e, (float) (box.minX - cx), (float) (box.minY - cy), (float) (lineZ - cz)).color(color.r, color.g, color.b, 1.0F).normal(e, 0.0f, 0.0f, 0.0f);
//                    bufferLines.vertex(e, (float) (box.minX - cx), (float) (box.maxY - cy), (float) (lineZ - cz)).color(color.r, color.g, color.b, 1.0F).normal(e, 0.0f, 0.0f, 0.0f);
                    lineZ += lineIntervalH;
                }
            }
            else if (box.minZ == box.maxZ)
            {
                double lineX = alignLinesToModulo ? roundUp(box.minX, lineIntervalH) : box.minX;

                while (lineX <= box.maxX)
                {
                    bufferLines.vertex((float) (lineX - cx), (float) (box.minY - cy), (float) (box.minZ - cz)).color(color.r, color.g, color.b, 1.0F);
                    bufferLines.vertex((float) (lineX - cx), (float) (box.maxY - cy), (float) (box.minZ - cz)).color(color.r, color.g, color.b, 1.0F);

//                    bufferLines.vertex(e, (float) (lineX - cx), (float) (box.minY - cy), (float) (box.minZ - cz)).color(color.r, color.g, color.b, 1.0F).normal(e, 0.0f, 0.0f, 0.0f);
//                    bufferLines.vertex(e, (float) (lineX - cx), (float) (box.maxY - cy), (float) (box.minZ - cz)).color(color.r, color.g, color.b, 1.0F).normal(e, 0.0f, 0.0f, 0.0f);
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
