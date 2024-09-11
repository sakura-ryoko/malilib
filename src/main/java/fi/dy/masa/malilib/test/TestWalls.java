package fi.dy.masa.malilib.test;

import net.minecraft.class_10209;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.Color4f;

public class TestWalls implements AutoCloseable
{
    protected static final Tessellator TESSELLATOR_1 = new Tessellator(2097152);
    protected static final Tessellator TESSELLATOR_2 = new Tessellator(2097152);
    protected static BufferBuilder BUFFER_1;
    protected static BufferBuilder BUFFER_2;
    protected static VertexBuffer VERTEX_1;
    protected static VertexBuffer VERTEX_2;
    protected static ShaderProgramKey SHADER_1 = ShaderProgramKeys.POSITION_COLOR;
    protected static ShaderProgramKey SHADER_2 = ShaderProgramKeys.POSITION_COLOR;
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

        if (VERTEX_1 == null || VERTEX_1.isClosed())
        {
            VERTEX_1 = new VertexBuffer(VertexBuffer.Usage.STATIC);
        }
        if (VERTEX_2 == null || VERTEX_2.isClosed())
        {
            VERTEX_2 = new VertexBuffer(VertexBuffer.Usage.STATIC);
        }

        BUFFER_1 = TESSELLATOR_1.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        BUFFER_2 = TESSELLATOR_2.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        //RenderUtils.drawBlockBoundingBoxOutlinesBatchedLines(pos, vec, color, 0.001, BUFFER_2);
        //TestUtils.drawBlockBoundingBoxSidesBatchedQuads(pos, vec, color, 0.001, BUFFER_1);

        Pair<BlockPos, BlockPos> corners;
        corners = TestUtils.getSpawnChunkCorners(pos, radius, mc.world);
        TestUtils.renderWallsWithLines(corners.getLeft(), corners.getRight(), vec, 16, 16, true, color, BUFFER_1, BUFFER_2);

        uploadData(BUFFER_1, VERTEX_1);
        uploadData(BUFFER_2, VERTEX_2);

        setUpdatePosition(vec);
    }

    private static void uploadData(BufferBuilder bufferBuilder, VertexBuffer vertexBuffer)
    {
        BuiltBuffer builtBuffer;

        if (vertexBuffer.isClosed())
        {
            return;
        }
        try
        {
            builtBuffer = bufferBuilder.endNullable();

            if (builtBuffer != null)
            {
                hasData = true;
                vertexBuffer.bind();
                vertexBuffer.upload(builtBuffer);
                VertexBuffer.unbind();
                builtBuffer.close();
            }
        }
        catch (Exception ignored) { }
    }

    protected static void preRender()
    {
        RenderSystem.lineWidth(glLineWidth);

        if (renderThrough)
        {
            RenderSystem.disableDepthTest();
            //RenderSystem.depthMask(false);
        }

        if (useCulling)
        {
            RenderSystem.enableCull();
        }
        else
        {
            RenderSystem.disableCull();
        }
    }

    protected static void postRender()
    {
        if (renderThrough)
        {
            RenderSystem.enableDepthTest();
            //RenderSystem.depthMask(true);
        }

        RenderSystem.enableCull();
    }

    public static void draw(Vec3d cameraPos, Matrix4f matrix4f, Matrix4f projMatrix, MinecraftClient mc)
    {
        class_10209.method_64146().push(() -> "TestWalls#draw()");

        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.polygonOffset(-3f, -3f);
        RenderSystem.enablePolygonOffset();

        RenderUtils.setupBlend();
        RenderUtils.color(1f, 1f, 1f, 1f);

        Matrix4fStack matrix4fstack = RenderSystem.getModelViewStack();

        Vec3d updatePos = getUpdatePosition();

        matrix4fstack.pushMatrix();
        matrix4fstack.translate((float) (updatePos.x - cameraPos.x), (float) (updatePos.y - cameraPos.y), (float) (updatePos.z - cameraPos.z));
        drawData(matrix4f, projMatrix);
        matrix4fstack.popMatrix();

        RenderSystem.polygonOffset(0f, 0f);
        RenderSystem.disablePolygonOffset();
        RenderUtils.color(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);

        class_10209.method_64146().pop();
    }

    private static void drawData(Matrix4f matrix4f, Matrix4f projMatrix)
    {
        if (hasData)
        {
            preRender();
            drawInternal(matrix4f, projMatrix, VERTEX_1, SHADER_1);
            drawInternal(matrix4f, projMatrix, VERTEX_2, SHADER_2);
            postRender();
        }
    }

    private static void drawInternal(Matrix4f matrix4f, Matrix4f projMatrix, VertexBuffer vertexBuffer, ShaderProgramKey shaderKey)
    {
        if (hasData)
        {
            ShaderProgram shader = RenderSystem.setShader(shaderKey);
            vertexBuffer.bind();
            vertexBuffer.draw(matrix4f, projMatrix, shader);
            VertexBuffer.unbind();
        }
    }

    public static void clear()
    {
        lastPos = BlockPos.ORIGIN;
        VERTEX_1.close();
        VERTEX_2.close();
        TESSELLATOR_1.clear();
        TESSELLATOR_2.clear();
        hasData = false;
    }

    @Override
    public void close()
    {
        clear();
    }
}
