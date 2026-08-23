package fi.dy.masa.malilib.test.render;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.render.MaLiLibPipelines;
import fi.dy.masa.malilib.render.RenderContext;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.test.config.ConfigTestEnum;
import fi.dy.masa.malilib.util.data.Color4f;

@ApiStatus.Experimental
public class TestRenderWalls implements AutoCloseable
{
    public static final TestRenderWalls INSTANCE = new TestRenderWalls();

    protected boolean renderThrough;
    protected boolean useCulling;
    protected float glLineWidth;

    private List<Box> boxes;
    private BlockPos center;
    protected BlockPos lastUpdatePos;
    private Vec3d updateCameraPos;
    private boolean hasData;
    private final boolean shouldResort;
    private boolean needsUpdate;
    private final int updateDistance = 48;

    public TestRenderWalls()
    {
        this.renderThrough = false;
        this.useCulling = false;
        this.glLineWidth = 3.0f;
        this.lastUpdatePos = null;
        this.updateCameraPos = Vec3d.ZERO;
        this.hasData = false;
        this.shouldResort = false;
        this.needsUpdate = true;
        this.boxes = new ArrayList<>();
        this.center = null;
    }

    public Vec3d getUpdatePosition()
    {
        return updateCameraPos;
    }

    public void setUpdatePosition(Vec3d cameraPosition)
    {
        this.updateCameraPos = cameraPosition;
    }

    public boolean needsUpdate(Entity cameraEntity, MinecraftClient mc)
    {
        return this.needsUpdate || this.lastUpdatePos == null ||
                Math.abs(cameraEntity.getX() - this.lastUpdatePos.getX()) > this.updateDistance ||
                Math.abs(cameraEntity.getZ() - this.lastUpdatePos.getZ()) > this.updateDistance ||
                Math.abs(cameraEntity.getY() - this.lastUpdatePos.getY()) > this.updateDistance;
    }

    public void setNeedsUpdate()
    {
        this.needsUpdate = true;
    }

    public boolean hasData()
    {
        return this.hasData;
    }

    public void update(Vec3d camPos, Entity entity, MinecraftClient mc)
    {
        if (mc.world == null || mc.player == null)
        {
            return;
        }

        int radius = MaLiLibConfigs.Test.TEST_CONFIG_INTEGER.getIntegerValue();
        BlockPos pos = entity.getBlockPos();
        BlockPos testPos = pos.add(2, 0, 2);
        Pair<BlockPos, BlockPos> corners = TestRenderUtils.getSpawnChunkCorners(testPos, radius, mc.world);
        this.boxes = TestRenderUtils.calculateBoxes(corners.getLeft(), corners.getRight());

        if (!this.boxes.isEmpty())
        {
            this.center = testPos;
            this.hasData = true;
        }
        else
        {
            this.center = null;
            this.hasData = false;
        }

        this.needsUpdate = false;
        this.setUpdatePosition(camPos);
    }

    public void render(Vec3d camPos, Matrix4f matrix4f, Matrix4f projMatrix, MinecraftClient mc, Profiler profiler)
    {
        profiler.push("render_test_walls");

        if (this.hasData && !this.boxes.isEmpty() && this.center != null)
        {
            this.renderQuads(camPos, mc, profiler);
            this.renderOutlines(camPos, mc, profiler);
            this.boxes.clear();
            this.center = null;
            this.hasData = false;
        }

        profiler.pop();
    }

    private void renderQuads(Vec3d camPos, MinecraftClient mc, Profiler profiler)
    {
        if (mc.world == null || mc.player == null ||
            !this.hasData || this.boxes.isEmpty())
        {
            return;
        }

        profiler.push("quads");
        final Color4f quadsColor = MaLiLibConfigs.Test.TEST_CONFIG_COLOR.getColor();

        // MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_NO_DEPTH_NO_CULL
        RenderContext ctx = new RenderContext(() -> "malilib:TestWalls/quads", MaLiLibPipelines.MINIHUD_SHAPE_OFFSET_NO_CULL);
        BufferBuilder builder = ctx.getBuilder();
        Matrix4fStack matrix4fstack = RenderSystem.getModelViewStack();
        Vec3d updatePos = this.getUpdatePosition();

        matrix4fstack.pushMatrix();
        matrix4fstack.translate((float) (updatePos.x - camPos.x), (float) (updatePos.y - camPos.y), (float) (updatePos.z - camPos.z));

        RenderUtils.drawBlockBoundingBoxSidesBatchedQuads(this.center, camPos, quadsColor, 0.001, builder);

        for (Box entry : this.boxes)
        {
            TestRenderUtils.renderWallQuads(entry, camPos, quadsColor, builder);
        }

        try
        {
            BuiltBuffer meshData = builder.endNullable();

            if (meshData != null)
            {
                if (this.shouldResort)
                {
                    ctx.upload(meshData, true);
                    ctx.startResorting(meshData, ctx.createVertexSorter(camPos));
                }
                else
                {
                    ctx.upload(meshData, false);
                }

                ctx.drawPost();
                meshData.close();
            }

            ctx.close();
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("TestWalls#renderQuads(): Exception; {}", err.getMessage());
        }

        matrix4fstack.popMatrix();
        profiler.pop();
    }

    private void renderOutlines(Vec3d camPos, MinecraftClient mc, Profiler profiler)
    {
        if (mc.world == null || mc.player == null)
        {
            return;
        }

        profiler.push("outlines");
        boolean useColor = ConfigTestEnum.TEST_WALLS_USE_COLOR.getBooleanValue();
        final Color4f linesColor = useColor
                                   ? Color4f.fromColor(MaLiLibConfigs.Test.TEST_CONFIG_COLOR.getColor(), 0xFF)
                                   : Color4f.WHITE;

        // RenderPipelines.LINES
        RenderContext ctx = new RenderContext(() -> "malilib:TestWalls/lines", MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_LEQUAL_DEPTH);
        BufferBuilder builder = ctx.getBuilder();
        Matrix4fStack matrix4fstack = RenderSystem.getModelViewStack();
        Vec3d updatePos = this.getUpdatePosition();

        matrix4fstack.pushMatrix();
        matrix4fstack.translate((float) (updatePos.x - camPos.x), (float) (updatePos.y - camPos.y), (float) (updatePos.z - camPos.z));

        RenderUtils.drawBlockBoundingBoxOutlinesBatchedLines(this.center, camPos, linesColor, 0.001, builder);

        for (Box entry : this.boxes)
        {
            TestRenderUtils.renderWallOutlines(entry, 16, 16, true, camPos, linesColor, builder);
        }

        matrix4fstack.popMatrix();

        try
        {
            BuiltBuffer meshData = builder.endNullable();

            if (meshData != null)
            {
                ctx.lineWidth(this.glLineWidth);
                ctx.draw(meshData, false, true);
                meshData.close();
            }

            ctx.close();
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("TestWalls#renderOutlines(): Exception; {}", err.getMessage());
        }

        profiler.pop();
    }

    public void clear()
    {
        this.lastUpdatePos = BlockPos.ORIGIN;
        this.hasData = false;
        this.boxes.clear();
    }

    @Override
    public void close()
    {
        clear();
    }
}
