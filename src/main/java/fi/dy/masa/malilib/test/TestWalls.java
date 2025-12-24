package fi.dy.masa.malilib.test;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.Color4f;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.*;
import net.minecraft.client.render.*;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.lwjgl.opengl.GL11C;

@ApiStatus.Experimental
public class TestWalls implements AutoCloseable
{
    public static final TestWalls INSTANCE = new TestWalls();

    protected final Tessellator TESSELLATOR_1 = new Tessellator(2097152);
    protected final Tessellator TESSELLATOR_2 = new Tessellator(2097152);
    protected BufferBuilder BUFFER_1;
    protected BufferBuilder BUFFER_2;
    protected VertexBuffer VERTEX_1;
    protected VertexBuffer VERTEX_2;
    protected final ShaderProgramKey SHADER_1 = ShaderProgramKeys.POSITION_COLOR;
    protected final ShaderProgramKey SHADER_2 = ShaderProgramKeys.POSITION_COLOR;

    protected boolean renderThrough;
    protected boolean useCulling;
    protected float glLineWidth;
    private List<Box> boxes;
    private BlockPos center;
    protected BlockPos lastUpdatePos;
    private Vec3d updateCameraPos;
    private boolean hasData;
//    private final boolean shouldResort;
    private final boolean needsUpdate;
    private final int updateDistance = 48;

    public TestWalls()
    {
        this.renderThrough = false;
        this.useCulling = false;
        this.glLineWidth = 3.0f;
        this.lastUpdatePos = null;
        this.updateCameraPos = Vec3d.ZERO;
        this.hasData = false;
//        this.shouldResort = false;
        this.needsUpdate = true;
        this.boxes = new ArrayList<>();
        this.center = null;
    }

    public Vec3d getUpdatePosition()
    {
        return this.updateCameraPos;
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

    public void update(Camera camera, Entity entity, MinecraftClient mc)
    {
        Color4f color = MaLiLibConfigs.Test.TEST_CONFIG_COLOR.getColor();

        if (mc.world == null || mc.player == null)
        {
            return;
        }

        int radius = MaLiLibConfigs.Test.TEST_CONFIG_INTEGER.getIntegerValue();
        Vec3d vec = camera.getPos();
        BlockPos pos = camera.getBlockPos();
        BlockPos testPos = pos.add(2, 0, 2);
        Pair<BlockPos, BlockPos> corners = TestUtils.getSpawnChunkCorners(testPos, radius, mc.world);
        this.boxes = TestUtils.calculateBoxes(corners.getLeft(), corners.getRight());

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

        setUpdatePosition(vec);
    }

    public void render(Camera camera, Matrix4f matrix4f, Matrix4f projMatrix, MinecraftClient mc, Profiler profiler)
    {
        profiler.push("render_test_walls");

        if (this.hasData && !this.boxes.isEmpty() && this.center != null)
        {
            this.renderQuads(camera, mc, profiler);
            this.renderOutlines(camera, mc, profiler);
            this.draw(camera.getPos(), matrix4f, projMatrix, mc, profiler);
            this.boxes.clear();
            this.center = null;
            this.hasData = false;
        }

        profiler.pop();
    }

    private void renderQuads(Camera camera, MinecraftClient mc, Profiler profiler)
    {
        if (mc.world == null || mc.player == null ||
            !this.hasData || this.boxes.isEmpty())
        {
            return;
        }
        if (this.VERTEX_1 == null || this.VERTEX_1.isClosed())
        {
            this.VERTEX_1 = new VertexBuffer(GlUsage.STATIC_WRITE);
        }

        profiler.push("quads");
        Color4f quadsColor = MaLiLibConfigs.Test.TEST_CONFIG_COLOR.getColor();
        Vec3d cameraPos = camera.getPos();

        this.BUFFER_1 = this.TESSELLATOR_1.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
//        Matrix4fStack matrix4fstack = RenderSystem.getModelViewStack();
//        Vec3d updatePos = this.getUpdatePosition();

//        this.preRender();
//        matrix4fstack.pushMatrix();
//        matrix4fstack.translate((float) (updatePos.x - cameraPos.x), (float) (updatePos.y - cameraPos.y), (float) (updatePos.z - cameraPos.z));

        RenderUtils.drawBlockBoundingBoxSidesBatchedQuads(this.center, cameraPos, quadsColor, 0.001, this.BUFFER_1);

//        TestUtils.renderWallsWithLines(corners.getLeft(), corners.getRight(), vec, 16, 16, true, color, BUFFER_1, BUFFER_2);

        for (Box entry : this.boxes)
        {
            TestUtils.renderWallQuads(entry, cameraPos, quadsColor, this.BUFFER_1);
        }

        this.uploadData(this.BUFFER_1, this.VERTEX_1);
//        this.postRender();
        profiler.pop();
    }

    private void renderOutlines(Camera camera, MinecraftClient mc, Profiler profiler)
    {
        if (mc.world == null || mc.player == null ||
            !this.hasData || this.boxes.isEmpty())
        {
            return;
        }
        if (this.VERTEX_2 == null || this.VERTEX_2.isClosed())
        {
            this.VERTEX_2 = new VertexBuffer(GlUsage.STATIC_WRITE);
        }

        profiler.push("outlines");
        Color4f linesColor = Color4f.WHITE;
        Vec3d cameraPos = camera.getPos();

        this.BUFFER_2 = this.TESSELLATOR_2.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
//        Matrix4fStack matrix4fstack = RenderSystem.getModelViewStack();
//        Vec3d updatePos = this.getUpdatePosition();

//        this.preRender();
//        matrix4fstack.pushMatrix();
//        matrix4fstack.translate((float) (updatePos.x - cameraPos.x), (float) (updatePos.y - cameraPos.y), (float) (updatePos.z - cameraPos.z));

        RenderUtils.drawBlockBoundingBoxOutlinesBatchedLines(this.center, cameraPos, linesColor, 0.001, this.BUFFER_2);

//        TestUtils.renderWallsWithLines(corners.getLeft(), corners.getRight(), vec, 16, 16, true, color, BUFFER_1, BUFFER_2);

        for (Box entry : this.boxes)
        {
            TestUtils.renderWallOutlines(entry, 16, 16, true, cameraPos, linesColor, this.BUFFER_2);
        }

        this.uploadData(this.BUFFER_2, this.VERTEX_2);
//        this.postRender();
        profiler.pop();
    }

    private void uploadData(BufferBuilder bufferBuilder, VertexBuffer vertexBuffer)
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
                this.hasData = true;
                vertexBuffer.bind();
                vertexBuffer.upload(builtBuffer);
                VertexBuffer.unbind();
                builtBuffer.close();
            }
        }
        catch (Exception ignored) { }
    }

    protected void preRender()
    {
        if (this.renderThrough)
        {
            RenderSystem.disableDepthTest();
            //RenderSystem.depthMask(false);
        }
        else
        {
            RenderSystem.enableDepthTest();
            RenderSystem.depthFunc(GL11C.GL_LEQUAL);
        }

        if (this.useCulling)
        {
            RenderSystem.enableCull();
        }
        else
        {
            RenderSystem.disableCull();
        }

        RenderSystem.depthMask(false);
        RenderSystem.lineWidth(this.glLineWidth);
        RenderSystem.polygonOffset(-3f, -3f);
        RenderSystem.enablePolygonOffset();
        RenderUtils.setupBlend();
        RenderUtils.color(1f, 1f, 1f, 1f);
    }

    protected void postRender()
    {
        if (this.renderThrough)
        {
            RenderSystem.enableDepthTest();
            //RenderSystem.depthMask(true);
        }

        if (!this.useCulling)
        {
            RenderSystem.enableCull();
        }

        RenderSystem.polygonOffset(0f, 0f);
        RenderSystem.disablePolygonOffset();
        RenderUtils.color(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
    }

    public void draw(Vec3d cameraPos, Matrix4f matrix4f, Matrix4f projMatrix, MinecraftClient mc, Profiler profiler)
    {
        profiler.push(() -> "TestWalls#draw()");

//        RenderSystem.disableCull();
//        RenderSystem.enableDepthTest();
        this.preRender();

        Matrix4fStack matrix4fstack = RenderSystem.getModelViewStack();

        Vec3d updatePos = this.getUpdatePosition();

        matrix4fstack.pushMatrix();
        matrix4fstack.translate((float) (updatePos.x - cameraPos.x), (float) (updatePos.y - cameraPos.y), (float) (updatePos.z - cameraPos.z));
        drawData(matrix4f, projMatrix);
        matrix4fstack.popMatrix();

//        RenderSystem.enableDepthTest();
//        RenderSystem.enableCull();
        this.postRender();

        profiler.pop();
    }

    private void drawData(Matrix4f matrix4f, Matrix4f projMatrix)
    {
        if (this.hasData)
        {
            this.preRender();
            drawInternal(matrix4f, projMatrix, this.VERTEX_1, this.SHADER_1);
            drawInternal(matrix4f, projMatrix, this.VERTEX_2, this.SHADER_2);
            this.postRender();
        }
    }

    private void drawInternal(Matrix4f matrix4f, Matrix4f projMatrix, VertexBuffer vertexBuffer, ShaderProgramKey shaderKey)
    {
        if (this.hasData)
        {
            ShaderProgram shader = RenderSystem.setShader(shaderKey);
            vertexBuffer.bind();
            vertexBuffer.draw(matrix4f, projMatrix, shader);
            VertexBuffer.unbind();
        }
    }

    public void clear()
    {
        this.lastUpdatePos = BlockPos.ORIGIN;
        this.hasData = false;
        this.boxes.clear();
        this.VERTEX_1.close();
        this.VERTEX_2.close();
        this.TESSELLATOR_1.clear();
        this.TESSELLATOR_2.clear();
    }

    @Override
    public void close()
    {
        clear();
    }
}
