package fi.dy.masa.malilib.test;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlUsage;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.render.MaLiLibPipelines;
import fi.dy.masa.malilib.render.RenderContext;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.data.Color4f;

@ApiStatus.Experimental
public class TestWalls implements AutoCloseable
{
    public static final TestWalls INSTANCE = new TestWalls();

    protected RenderContext CONTEXT_1;
    protected RenderContext CONTEXT_2;
    protected boolean renderThrough;
    protected boolean useCulling;
    protected float glLineWidth;

    protected BlockPos lastUpdatePos;
    private Vec3d updateCameraPos;
    private boolean hasData;
    private final boolean needsUpdate;
    private final int updateDistance = 48;

    public TestWalls()
    {
        this.CONTEXT_1 = new RenderContext(MaLiLibPipelines.POSITION_COLOR_SIMPLE, GlUsage.STATIC_WRITE);
        this.CONTEXT_2 = new RenderContext(MaLiLibPipelines.DEBUG_LINES_SIMPLE, GlUsage.STATIC_WRITE);
        this.renderThrough = false;
        this.useCulling = false;
        this.glLineWidth = 3.0f;
        this.lastUpdatePos = null;
        this.updateCameraPos = Vec3d.ZERO;
        this.hasData = false;
        this.needsUpdate = true;
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
        /*
        if (lastPos == null || lastPos.equals(BlockPos.ORIGIN))
        {
            MaLiLib.LOGGER.error("TestWalls#needsUpdate(): now [{}] // last [{}] --> UPDATE 1", pos.toShortString(), lastPos != null ? lastPos.toShortString() : "<NULL>");
            lastPos = pos;
            return true;
        }
        else if (pos.compareTo(lastPos) != 0)
        {
            MaLiLib.LOGGER.error("TestWalls#needsUpdate(): now [{}] // last [{}] --> UPDATE 2", pos.toShortString(), lastPos.toShortString());
            lastPos = pos;
            return true;
        }

        MaLiLib.LOGGER.error("TestWalls#needsUpdate(): now [{}] // last [{}] --> FAIL", pos.toShortString(), lastPos.toShortString());
        return false;
         */

        return this.needsUpdate || this.lastUpdatePos == null ||
                Math.abs(cameraEntity.getX() - this.lastUpdatePos.getX()) > this.updateDistance ||
                Math.abs(cameraEntity.getZ() - this.lastUpdatePos.getZ()) > this.updateDistance ||
                Math.abs(cameraEntity.getY() - this.lastUpdatePos.getY()) > this.updateDistance;
    }

    public void update(Camera camera, Entity entity, MinecraftClient mc)
    {
        Color4f color = MaLiLibConfigs.Test.TEST_CONFIG_COLOR.getColor();

        if (mc.world == null || mc.player == null)
        {
            return;
        }

        BlockPos pos = entity.getBlockPos();
        Vec3d vec = camera.getPos();
        int radius = MaLiLibConfigs.Test.TEST_CONFIG_INTEGER.getIntegerValue();

        BufferBuilder builder1 = CONTEXT_1.startShader(() -> "TestWalls Quads", MaLiLibPipelines.POSITION_COLOR_SIMPLE, GlUsage.STATIC_WRITE);
        BufferBuilder builder2 = CONTEXT_2.startShader(() -> "TestWalls Lines", MaLiLibPipelines.DEBUG_LINES_SIMPLE, GlUsage.STATIC_WRITE);

        BlockPos testPos = pos.add(2, 0, 2);

        RenderUtils.drawBlockBoundingBoxOutlinesBatchedLines(testPos, vec, color, 0.001, builder2);
        TestUtils.drawBlockBoundingBoxSidesBatchedQuads(testPos, vec, color, 0.001, builder1);

        Pair<BlockPos, BlockPos> corners = TestUtils.getSpawnChunkCorners(testPos, radius, mc.world);
        TestUtils.renderWallsWithLines(corners.getLeft(), corners.getRight(), vec, 16, 16, true, color, builder1, builder2);

        this.hasData = true;

        uploadData(this.CONTEXT_1, builder1);
        uploadData(this.CONTEXT_2, builder2);

        setUpdatePosition(vec);
    }

    protected void uploadData(RenderContext ctx, BufferBuilder builder)
    {
        if (this.hasData)
        {
            try
            {
                ctx.upload(builder);
            }
            catch (Exception err)
            {
                MaLiLib.LOGGER.error("TestWalls#uploadData() - Render Context exception; {}", err.getMessage());
            }
        }
    }

    protected void preRender()
    {
        RenderSystem.lineWidth(this.glLineWidth);

        if (this.renderThrough)
        {
            RenderUtils.depthTest(false);
        }

        RenderUtils.culling(this.useCulling);
    }

    protected void postRender()
    {
        if (this.renderThrough)
        {
            RenderUtils.depthTest(true);
        }

        RenderUtils.culling(true);
    }

    public void draw(Vec3d cameraPos, Matrix4f matrix4f, Matrix4f projMatrix, MinecraftClient mc, Profiler profiler)
    {
        if (!this.hasData) return;

        profiler.push(() -> "TestWalls#draw()");

//        RenderUtils.culling(false);
//        RenderUtils.depthTest(true);
//        RenderUtils.depthMask(false);
        RenderUtils.polygonOffset(-3f, -3f);
        RenderUtils.polygonOffset(true);
        RenderUtils.blend(true);
        int color = RenderUtils.color(1f, 1f, 1f, 1f);

        //RenderSystem.backupProjectionMatrix();
        //RenderSystem.setProjectionMatrix(projMatrix, ProjectionType.PERSPECTIVE);
        Matrix4fStack matrix4fstack = RenderSystem.getModelViewStack();
        Vec3d updatePos = this.getUpdatePosition();

        matrix4fstack.pushMatrix();
        matrix4fstack.translate((float) (updatePos.x - cameraPos.x), (float) (updatePos.y - cameraPos.y), (float) (updatePos.z - cameraPos.z));
        drawData(color);
        //RenderSystem.restoreProjectionMatrix();
        matrix4fstack.popMatrix();

        RenderUtils.polygonOffset(0f, 0f);
        RenderUtils.polygonOffset(false);
        RenderUtils.color(1f, 1f, 1f, 1f);
//        RenderUtils.depthTest(true);
//        RenderUtils.culling(true);
//        RenderUtils.depthMask(true);
        RenderUtils.blend(false);

        profiler.pop();
    }

    private void drawData(int color)
    {
        if (this.hasData)
        {
            preRender();
            drawInternal(CONTEXT_1, color);
            drawInternal(CONTEXT_2, color);
            postRender();
        }
    }

    private void drawInternal(RenderContext ctx, int color)
    {
        if (this.hasData)
        {
            try
            {
                ctx.draw(color);
                ctx.reset();
                this.hasData = false;
            }
            catch (Exception err)
            {
                MaLiLib.LOGGER.error("TestWalls#drawInternal() - Render Context exception; {}", err.getMessage());
            }
        }
    }

    public void clear()
    {
        this.lastUpdatePos = BlockPos.ORIGIN;
        this.CONTEXT_1.reset();
        this.CONTEXT_2.reset();
        this.hasData = false;
    }

    @Override
    public void close()
    {
        clear();
    }
}
