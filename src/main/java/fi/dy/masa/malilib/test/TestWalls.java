package fi.dy.masa.malilib.test;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlUsage;
import net.minecraft.client.gl.ShaderPipeline;
import net.minecraft.client.gl.ShaderPipelines;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.render.RenderContext;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.data.Color4f;

@ApiStatus.Experimental
public class TestWalls implements AutoCloseable
{
    protected static RenderContext CONTEXT_1 = new RenderContext(ShaderPipelines.DEBUG_QUADS, GlUsage.STATIC_WRITE);
    protected static RenderContext CONTEXT_2 = new RenderContext(ShaderPipelines.DEBUG_LINE_STRIP, GlUsage.STATIC_WRITE);
    protected static boolean renderThrough = false;
    protected static boolean useCulling = false;
    protected static float glLineWidth = 1f;

    protected static BlockPos lastPos = null;
    private static Vec3d updateCameraPos = Vec3d.ZERO;
    private static boolean hasData = false;

    public static Vec3d getUpdatePosition()
    {
        return updateCameraPos;
    }

    public static void setUpdatePosition(Vec3d cameraPosition)
    {
        updateCameraPos = cameraPosition;
    }

    public static boolean needsUpdate(BlockPos pos)
    {
        if (lastPos == null || lastPos.equals(BlockPos.ORIGIN))
        {
            lastPos = pos;
            return true;
        }
        else if (!pos.equals(BlockPos.ORIGIN) &&
                !pos.equals(lastPos))
        {
            lastPos = pos;
            return true;
        }

        return false;
    }

    public static void update(Camera camera, MinecraftClient mc)
    {
        Color4f color = MaLiLibConfigs.Test.TEST_CONFIG_COLOR.getColor();

        if (mc.world == null || mc.player == null)
        {
            return;
        }

        BlockPos pos = camera.getBlockPos();
        Vec3d vec = camera.getPos();
        int radius = 5;

        CONTEXT_1.start(() -> "TestWalls Quads", VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.QUADS, GlUsage.STATIC_WRITE);
        CONTEXT_2.start(() -> "TestWalls Lines", VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.DEBUG_LINE_STRIP, GlUsage.STATIC_WRITE);

        BufferBuilder builder1 = CONTEXT_1.getBuilder();
        BufferBuilder builder2 = CONTEXT_2.getBuilder();

        RenderUtils.drawBlockBoundingBoxOutlinesBatchedLines(pos, vec, color, 0.001, builder2);
        TestUtils.drawBlockBoundingBoxSidesBatchedQuads(pos, vec, color, 0.001, builder1);

        Pair<BlockPos, BlockPos> corners = TestUtils.getSpawnChunkCorners(pos, radius, mc.world);
        TestUtils.renderWallsWithLines(corners.getLeft(), corners.getRight(), vec, 16, 16, true, color, builder1, builder2);

        hasData = true;

        uploadData(CONTEXT_1, builder1);
        uploadData(CONTEXT_2, builder2);

        setUpdatePosition(vec);
    }

    protected static void uploadData(RenderContext ctx, BufferBuilder builder)
    {
        if (hasData)
        {
            try
            {
                ctx = ctx.setBuilder(builder);
                ctx.upload(builder.endNullable());
            }
            catch (Exception err)
            {
                MaLiLib.LOGGER.error("TestWalls#uploadData() - Render Context exception; {}", err.getMessage());
            }
        }
    }

    protected static void preRender()
    {
        RenderSystem.lineWidth(glLineWidth);

        if (renderThrough)
        {
            RenderUtils.depthTest(false);
        }

        if (useCulling)
        {
            RenderUtils.culling(true);
        }
        else
        {
            RenderUtils.culling(false);
        }
    }

    protected static void postRender()
    {
        if (renderThrough)
        {
            RenderUtils.depthTest(true);
        }

        RenderUtils.culling(true);
    }

    public static void draw(Vec3d cameraPos, Matrix4f matrix4f, Matrix4f projMatrix, MinecraftClient mc, Profiler profiler)
    {
        if (!hasData) return;

        profiler.push(() -> "TestWalls#draw()");

        /*
        RenderUtils.culling(false);
        RenderUtils.depthTest(true);
        RenderUtils.depthMask(false);
        RenderUtils.polygonOffset(-3f, -3f);
        RenderUtils.polygonOffset(true);
         */
        RenderUtils.blend(true);
        RenderUtils.color(1f, 1f, 1f, 1f);

        Matrix4fStack matrix4fstack = RenderSystem.getModelViewStack();
        Vec3d updatePos = getUpdatePosition();

        matrix4fstack.pushMatrix();
        matrix4fstack.translate((float) (updatePos.x - cameraPos.x), (float) (updatePos.y - cameraPos.y), (float) (updatePos.z - cameraPos.z));
        drawData(matrix4f, projMatrix);
        matrix4fstack.popMatrix();

        /*
        RenderUtils.polygonOffset(0f, 0f);
        RenderUtils.polygonOffset(false);
        RenderUtils.color(1f, 1f, 1f, 1f);
        RenderUtils.depthTest(true);
        RenderUtils.culling(true);
        RenderUtils.depthMask(true);
         */
        RenderUtils.blend(false);
        profiler.pop();
    }

    private static void drawData(Matrix4f matrix4f, Matrix4f projMatrix)
    {
        if (hasData)
        {
            preRender();
            drawInternal(matrix4f, projMatrix, ShaderPipelines.DEBUG_QUADS, CONTEXT_1);
            drawInternal(matrix4f, projMatrix, ShaderPipelines.DEBUG_LINE_STRIP, CONTEXT_2);
            postRender();
        }
    }

    private static void drawInternal(Matrix4f matrix4f, Matrix4f projMatrix, ShaderPipeline shaderKey, RenderContext ctx)
    {
        if (hasData)
        {
            //ShaderProgram shader = RenderSystem.setShader(shaderKey);
            /*
            vertexBuffer.bind();
            vertexBuffer.draw(matrix4f, projMatrix, shaderKey.getProgram());
            VertexBuffer.unbind();
             */

            try
            {
                ctx.setShader(shaderKey);
                ctx.draw();
                ctx.reset();
                hasData = false;
            }
            catch (Exception err)
            {
                MaLiLib.LOGGER.error("TestWalls#drawInternal() - Render Context exception; {}", err.getMessage());
            }
        }
    }

    public static void clear()
    {
        lastPos = BlockPos.ORIGIN;
        CONTEXT_1.reset();
        CONTEXT_2.reset();
        hasData = false;
    }

    @Override
    public void close()
    {
        clear();
    }
}
