package fi.dy.masa.malilib.test;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
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
import fi.dy.masa.malilib.render.MaLiLibPipelines;
import fi.dy.masa.malilib.render.RenderContext;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.data.Color4f;

@ApiStatus.Experimental
public class TestWalls implements AutoCloseable
{
    protected static final RenderContext CONTEXT_1 = new RenderContext(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
    protected static final RenderContext CONTEXT_2 = new RenderContext(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
    /*
    protected static final Tessellator TESSELLATOR_1 = new Tessellator(2097152);
    protected static final Tessellator TESSELLATOR_2 = new Tessellator(2097152);
    protected static BufferBuilder BUFFER_1;
    protected static BufferBuilder BUFFER_2;
    protected static VertexBuffer VERTEX_1;
    protected static VertexBuffer VERTEX_2;
     */
    protected static boolean renderThrough = false;
    protected static boolean useCulling = false;
    protected static float glLineWidth = 1f;

    protected static BlockPos lastPos = BlockPos.ORIGIN;
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
        if (lastPos.equals(BlockPos.ORIGIN))
        {
            lastPos = pos;
            return true;
        } else if (!pos.equals(BlockPos.ORIGIN) &&
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

        /*
        if (VERTEX_1 == null || VERTEX_1.isClosed())
        {
            VERTEX_1 = new VertexBuffer(GlUsage.STATIC_WRITE);
        }
        if (VERTEX_2 == null || VERTEX_2.isClosed())
        {
            VERTEX_2 = new VertexBuffer(GlUsage.STATIC_WRITE);
        }

        BUFFER_1 = TESSELLATOR_1.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        BUFFER_2 = TESSELLATOR_2.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
         */

        CONTEXT_1.start(VertexFormat.DrawMode.QUADS,       VertexFormats.POSITION_COLOR, ShaderPipelines.DEBUG_QUADS);
        CONTEXT_2.start(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR, ShaderPipelines.DEBUG_LINE_STRIP);

        BufferBuilder builder1 = CONTEXT_1.getBuilder();
        BufferBuilder builder2 = CONTEXT_2.getBuilder();

        //RenderUtils.drawBlockBoundingBoxOutlinesBatchedLines(pos, vec, color, 0.001, BUFFER_2);
        //TestUtils.drawBlockBoundingBoxSidesBatchedQuads(pos, vec, color, 0.001, BUFFER_1);

        Pair<BlockPos, BlockPos> corners;
        corners = TestUtils.getSpawnChunkCorners(pos, radius, mc.world);
        TestUtils.renderWallsWithLines(corners.getLeft(), corners.getRight(), vec, 16, 16, true, color, builder1, builder2);

        //uploadData(CONTEXT_1, builder1);
        //uploadData(CONTEXT_2, builder2);

        CONTEXT_1.setBuilder(builder1);
        CONTEXT_2.setBuilder(builder2);

        setUpdatePosition(vec);
    }

    /*
    private static void uploadData(RenderContext ctx, BufferBuilder buffer)
    {
        try
        {
            hasData = true;
            *
            vertexBuffer.bind();
            vertexBuffer.upload(builtBuffer);
            VertexBuffer.unbind();
            builtBuffer.close();
             *

            ctx.draw(RenderUtils.fb(), buffer.endNullable());
            ctx.reset();
        }
        catch (Exception ignored) {}
    }
    */

    protected static void preRender()
    {
        RenderSystem.lineWidth(glLineWidth);

        if (renderThrough & useCulling)
        {
            //RenderSystem.disableDepthTest();
            //RenderSystem.depthMask(false);

            CONTEXT_1.setShader(MaLiLibPipelines.DEBUG_LINES_CULLING_DEPTH_TEST_OFF);
            CONTEXT_2.setShader(MaLiLibPipelines.DEBUG_LINES_CULLING_DEPTH_TEST_OFF);
        }
        else if (renderThrough)
        {
            CONTEXT_1.setShader(MaLiLibPipelines.DEBUG_LINES_DEPTH_TEST_OFF);
            CONTEXT_2.setShader(MaLiLibPipelines.DEBUG_LINES_DEPTH_TEST_OFF);
        }
        /*
        if (useCulling)
        {
            RenderSystem.enableCull();
        }
        else
        {
            RenderSystem.disableCull();
        }
         */
        else
        {
            CONTEXT_1.setShader(ShaderPipelines.DEBUG_QUADS);
            CONTEXT_2.setShader(ShaderPipelines.DEBUG_LINE_STRIP);
        }
    }

    protected static void postRender()
    {
        if (renderThrough || useCulling)
        {
            //RenderSystem.enableDepthTest();
            //RenderSystem.depthMask(true);
            CONTEXT_1.setShader(ShaderPipelines.DEBUG_QUADS);
            CONTEXT_2.setShader(ShaderPipelines.DEBUG_LINE_STRIP);
        }

        //RenderSystem.enableCull();
    }

    public static void draw(Vec3d cameraPos, Matrix4f matrix4f, Matrix4f projMatrix, MinecraftClient mc, Profiler profiler)
    {
        profiler.push(() -> "TestWalls#draw()");

        /*
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.polygonOffset(-3f, -3f);
        RenderSystem.enablePolygonOffset();

        RenderUtils.setupBlend();
         */
        RenderUtils.color(1f, 1f, 1f, 1f);

        Matrix4fStack matrix4fstack = RenderSystem.getModelViewStack();

        Vec3d updatePos = getUpdatePosition();

        matrix4fstack.pushMatrix();
        matrix4fstack.translate((float) (updatePos.x - cameraPos.x), (float) (updatePos.y - cameraPos.y), (float) (updatePos.z - cameraPos.z));
        drawData(matrix4f, projMatrix);
        matrix4fstack.popMatrix();

        /*
        RenderSystem.polygonOffset(0f, 0f);
        RenderSystem.disablePolygonOffset();
         */
        RenderUtils.color(1f, 1f, 1f, 1f);
        /*
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
         */

        profiler.pop();
    }

    private static void drawData(Matrix4f matrix4f, Matrix4f projMatrix)
    {
        if (hasData)
        {
            preRender();
            drawInternal(matrix4f, projMatrix, CONTEXT_1);
            drawInternal(matrix4f, projMatrix, CONTEXT_2);
            postRender();
        }
    }

    private static void drawInternal(Matrix4f matrix4f, Matrix4f projMatrix, RenderContext ctx)
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
                ctx.draw(RenderUtils.fb());
                ctx.reset();
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
        /*
        VERTEX_1.close();
        VERTEX_2.close();
        TESSELLATOR_1.clear();
        TESSELLATOR_2.clear();
         */
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
