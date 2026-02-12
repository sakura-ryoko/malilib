package fi.dy.masa.malilib.test.render;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

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

    private List<AABB> boxes;
    private BlockPos center;
    protected BlockPos lastUpdatePos;
    private Vec3 updateCameraPos;
    private boolean hasData;
    private final boolean shouldResort;
    private boolean needsUpdate;
    private final int updateDistance = 48;

    public TestRenderWalls()
    {
        this.renderThrough = false;
        this.useCulling = false;
        this.glLineWidth = 1.6f;
        this.lastUpdatePos = null;
        this.updateCameraPos = Vec3.ZERO;
        this.hasData = false;
        this.shouldResort = false;
        this.needsUpdate = true;
        this.boxes = new ArrayList<>();
        this.center = null;
    }

    public Vec3 getUpdatePosition()
    {
        return updateCameraPos;
    }

    public void setUpdatePosition(Vec3 cameraPosition)
    {
        this.updateCameraPos = cameraPosition;
    }

    public boolean needsUpdate(Entity cameraEntity, Minecraft mc)
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

    public void update(Camera camera, Entity entity, Minecraft mc)
    {
        if (mc.level == null || mc.player == null)
        {
            return;
        }

        int radius = MaLiLibConfigs.Test.TEST_CONFIG_INTEGER.getIntegerValue();
        this.glLineWidth = 1.6f;
        Vec3 vec = camera.position();
        BlockPos pos = entity.blockPosition();
        BlockPos testPos = pos.offset(2, 0, 2);
        Pair<BlockPos, BlockPos> corners = TestRenderUtils.getSpawnChunkCorners(testPos, radius, mc.level);
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
        this.setUpdatePosition(vec);
    }

    public void render(Camera camera, Matrix4f matrix4f, Matrix4f projMatrix, Minecraft mc, ProfilerFiller profiler)
    {
        profiler.push("render_test_walls");

        if (this.hasData && !this.boxes.isEmpty() && this.center != null)
        {
            this.renderQuads(camera, mc, profiler);
            this.renderOutlines(camera, mc, profiler);
            this.boxes.clear();
            this.center = null;
            this.hasData = false;
        }

        profiler.pop();
    }

    private void renderQuads(Camera camera, Minecraft mc, ProfilerFiller profiler)
    {
        if (mc.level == null || mc.player == null ||
            !this.hasData || this.boxes.isEmpty())
        {
            return;
        }

        profiler.push("quads");
        final Color4f quadsColor = MaLiLibConfigs.Test.TEST_CONFIG_COLOR.getColor();
        Vec3 cameraPos = camera.position();

        // MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_NO_DEPTH_NO_CULL
        RenderContext ctx = new RenderContext(() -> "malilib:TestWalls/quads", MaLiLibPipelines.MINIHUD_SHAPE_OFFSET_NO_CULL);
        BufferBuilder builder = ctx.getBuilder();
        Matrix4fStack matrix4fstack = RenderSystem.getModelViewStack();
        Vec3 updatePos = this.getUpdatePosition();

        matrix4fstack.pushMatrix();
        matrix4fstack.translate((float) (updatePos.x - cameraPos.x), (float) (updatePos.y - cameraPos.y), (float) (updatePos.z - cameraPos.z));

        RenderUtils.drawBlockBoundingBoxSidesBatchedQuads(this.center, cameraPos, quadsColor, 0.001, builder);

        for (AABB entry : this.boxes)
        {
            TestRenderUtils.renderWallQuads(entry, cameraPos, quadsColor, builder);
        }

        try
        {
            MeshData meshData = builder.build();

            if (meshData != null)
            {
                if (this.shouldResort)
                {
                    ctx.upload(meshData, true);
                    ctx.startResorting(meshData, ctx.createVertexSorter(camera));
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

    private void renderOutlines(Camera camera, Minecraft mc, ProfilerFiller profiler)
    {
        if (mc.level == null || mc.player == null)
        {
            return;
        }

        profiler.push("outlines");
        boolean useColor = ConfigTestEnum.TEST_WALLS_USE_COLOR.getBooleanValue();
        final Color4f linesColor = useColor
                                   ? Color4f.fromColor(MaLiLibConfigs.Test.TEST_CONFIG_COLOR.getColor(), 0xFF)
                                   : Color4f.WHITE;
        Vec3 cameraPos = camera.position();

        // RenderPipelines.LINES
        RenderContext ctx = new RenderContext(() -> "malilib:TestWalls/lines", MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_LEQUAL_DEPTH);
        BufferBuilder builder = ctx.getBuilder();
        Matrix4fStack matrix4fstack = RenderSystem.getModelViewStack();
        Vec3 updatePos = this.getUpdatePosition();

        matrix4fstack.pushMatrix();
        matrix4fstack.translate((float) (updatePos.x - cameraPos.x), (float) (updatePos.y - cameraPos.y), (float) (updatePos.z - cameraPos.z));

        RenderUtils.drawBlockBoundingBoxOutlinesBatchedLines(this.center, cameraPos, linesColor, 0.001, this.glLineWidth, builder);

        for (AABB entry : this.boxes)
        {
            TestRenderUtils.renderWallOutlines(entry, 16, 16, true, cameraPos, linesColor, this.glLineWidth, builder);
        }

        matrix4fstack.popMatrix();

        try
        {
            MeshData meshData = builder.build();

            if (meshData != null)
            {
//                ctx.lineWidth(this.glLineWidth);
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
        this.lastUpdatePos = BlockPos.ZERO;
        this.hasData = false;
        this.boxes.clear();
    }

    @Override
    public void close()
    {
        clear();
    }
}
