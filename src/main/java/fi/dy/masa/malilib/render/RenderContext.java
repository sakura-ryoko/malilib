package fi.dy.masa.malilib.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.class_10881;
import net.minecraft.class_10883;
import net.minecraft.client.gl.*;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.DrawableTexture;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.util.Identifier;
import net.minecraft.util.TriState;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;

import javax.annotation.Nullable;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.Supplier;
import org.joml.Matrix4f;

import fi.dy.masa.malilib.mixin.render.IMixinBufferBuilder;

public class RenderContext implements AutoCloseable
{
    private Supplier<String> name;
    private GlUsage usage;
    private GpuBuffer buffer;
    private RenderSystem.ShapeIndexBuffer shapeIndex;
    private BufferAllocator alloc;
    private BufferBuilder builder;
    private ShaderPipeline shader;
    private VertexFormat format;
    private VertexFormat.DrawMode drawMode;
    private boolean started;
    private int bufferIndex;

    public RenderContext(ShaderPipeline shader)
    {
        this(shader, GlUsage.DYNAMIC_WRITE);
    }

    public RenderContext(ShaderPipeline shader, GlUsage usage)
    {
        this(() -> "RenderContext", shader, usage);
    }

    public RenderContext(Supplier<String> name, ShaderPipeline shader, GlUsage usage)
    {
        this.name = name;
        this.alloc = new BufferAllocator(shader.getFormat().getVertexSizeByte() * 4);
        this.builder = new BufferBuilder(this.alloc, shader.getDrawMode(), shader.getFormat());
        this.shapeIndex = RenderSystem.getSequentialBuffer(shader.getDrawMode());
        this.format = shader.getFormat();
        this.drawMode = shader.getDrawMode();
        this.shader = shader;
        this.usage = usage;
        this.buffer = null;
        this.bufferIndex = -1;
        this.started = true;
    }

    public BufferBuilder start(ShaderPipeline shader)
    {
        return this.start(() -> "RenderContext", shader, GlUsage.STATIC_WRITE);
    }

    public BufferBuilder start(Supplier<String> name, ShaderPipeline shader, GlUsage usage)
    {
        this.reset();
        this.name = name;
        this.alloc = new BufferAllocator(shader.getFormat().getVertexSizeByte() * 4);
        this.builder = new BufferBuilder(this.alloc, shader.getDrawMode(), shader.getFormat());
        this.shapeIndex = RenderSystem.getSequentialBuffer(shader.getDrawMode());
        this.format = shader.getFormat();
        this.drawMode = shader.getDrawMode();
        this.shader = shader;
        this.usage = usage;
        this.buffer = null;
        this.bufferIndex = -1;

        this.started = true;
        return this.builder;
    }

    // Textured Quads Draw
    public BufferBuilder start(VertexFormat format, VertexFormat.DrawMode drawMode)
    {
        return this.start(() -> "RenderContext", format, drawMode, GlUsage.STATIC_WRITE);
    }

    public BufferBuilder start(Supplier<String> name, VertexFormat format, VertexFormat.DrawMode drawMode, GlUsage usage)
    {
        this.reset();
        this.name = name;
        this.alloc = new BufferAllocator(format.getVertexSizeByte() * 4);
        this.builder = new BufferBuilder(this.alloc, drawMode, format);
        this.shapeIndex = RenderSystem.getSequentialBuffer(drawMode);
        this.format = format;
        this.drawMode = drawMode;
        this.shader = null;     // Add shader for draw
        this.usage = usage;
        this.buffer = null;
        this.bufferIndex = -1;

        this.started = true;
        return this.builder;
    }

    public BufferBuilder getBuilder()
    {
        return this.builder;
    }

    public GlUsage getUsage()
    {
        return this.usage;
    }

    public VertexFormat getFormat()
    {
        return this.format;
    }

    public VertexFormat.DrawMode getDrawMode()
    {
        return this.drawMode;
    }

    public VertexFormat getShaderFormat()
    {
        if (this.shader != null)
        {
            return this.shader.getFormat();
        }

        return this.format;
    }

    public VertexFormat.DrawMode getShaderDrawMode()
    {
        if (this.shader != null)
        {
            return this.shader.getDrawMode();
        }

        return this.drawMode;
    }

    /**
     * BUILDER PHASE --
     * -
     * This is to simply ensure that the builder is stored again
     * @param builder ()
     * @return ()
     */
    public RenderContext setBuilder(BufferBuilder builder)
    {
        this.builder = builder;
        return this;
    }

    public RenderContext setShader(ShaderPipeline shader) throws RuntimeException
    {
        if (this.format != shader.getFormat() || this.drawMode != shader.getDrawMode())
        {
            throw new RuntimeException("Shader does not match Format/Draw mode!");
        }

        this.shader = shader;
        return this;
    }

    /**
     * DRAW PHASE --
     * -
     * Performs the Renderer draw to the specified Frame Buffer
     */
    public void draw() throws RuntimeException
    {
        this.ensureSafe();

        if (RenderSystem.isOnRenderThread())
        {
            this.draw(this.builder.endNullable());
        }
    }

    public void draw(BuiltBuffer meshData) throws RuntimeException
    {
        this.ensureSafe();

        if (RenderSystem.isOnRenderThread())
        {
            this.draw(meshData, new float[]{0f, 0f, 0f});
        }
    }

    public void draw(Supplier<String> name, BuiltBuffer meshData) throws RuntimeException
    {
        this.draw(name, null, GlBufferTarget.VERTICES, -1, meshData, new float[]{0f, 0f, 0f});
    }

    public void draw(Supplier<String> name, BuiltBuffer meshData, int color) throws RuntimeException
    {
        this.draw(name, null, GlBufferTarget.VERTICES, color, meshData, new float[]{0f, 0f, 0f});
    }

    public void draw(Supplier<String> name, BuiltBuffer meshData, GlBufferTarget target, int color) throws RuntimeException
    {
        this.draw(name, null, target, color, meshData, new float[]{0f, 0f, 0f});
    }

    public void draw(BuiltBuffer meshData, float[] offset) throws RuntimeException
    {
        this.draw(() -> "Render Context", null, GlBufferTarget.VERTICES, -1, meshData, offset);
    }

    public void draw(BuiltBuffer meshData, int color, float[] offset) throws RuntimeException
    {
        this.draw(() -> "Render Context", null, GlBufferTarget.VERTICES, color, meshData, offset);
    }

    public void draw(BuiltBuffer meshData, GlBufferTarget target, int color, float[] offset) throws RuntimeException
    {
        this.draw(() -> "Render Context", null, target, color, meshData, offset);
    }

    public void draw(Supplier<String> name, BuiltBuffer meshData, GlBufferTarget target, int color, float[] offset) throws RuntimeException
    {
        this.draw(name, null, target, color, meshData, offset);
    }

    public void draw(@Nullable Framebuffer otherFb, BuiltBuffer meshData) throws RuntimeException
    {
        this.draw(() -> "Render Context", otherFb, GlBufferTarget.VERTICES, -1, meshData, new float[]{0f, 0f, 0f});
    }

    public void draw(@Nullable Framebuffer otherFb, BuiltBuffer meshData, float[] offset) throws RuntimeException
    {
        this.draw(() -> "Render Context", otherFb, GlBufferTarget.VERTICES, -1, meshData, offset);
    }

    public void draw(@Nullable Framebuffer otherFb, BuiltBuffer meshData, int color, float[] offset) throws RuntimeException
    {
        this.draw(() -> "Render Context", otherFb, GlBufferTarget.VERTICES, color, meshData, offset);
    }

    public void draw(Supplier<String> name, @Nullable Framebuffer otherFb, GlBufferTarget target, int color, BuiltBuffer meshData, float[] offset) throws RuntimeException
    {
        this.ensureSafe();

        if (RenderSystem.isOnRenderThread())
        {
            if (meshData == null)
            {
                this.bufferIndex = 0;
            }
            else
            {
                if (this.buffer != null && this.buffer.size >= meshData.getBuffer().remaining())
                {
                    class_10881 device = RenderSystem.getDevice().method_68389();
                    device.method_68350(this.buffer, meshData.getBuffer(), 0);
                }
                else
                {
                    if (this.buffer != null)
                    {
                        this.buffer.close();
                    }

                    this.buffer = RenderSystem.getDevice().method_68387(name, target, this.usage, meshData.getBuffer());
                }

                this.bufferIndex = meshData.getDrawParameters().indexCount();
            }

            if (this.bufferIndex > 0)
            {
                float[] rgba = {ColorHelper.getRedFloat(color), ColorHelper.getGreenFloat(color), ColorHelper.getBlueFloat(color), ColorHelper.getAlphaFloat(color)};

                RenderSystem.setShaderColor(rgba[0], rgba[1], rgba[2], rgba[3]);
                this.drawInternal(otherFb, offset);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            }

            this.started = false;
        }
    }

    private void drawInternal(@Nullable Framebuffer otherFb, float[] offset)
    {
        RenderSystem.setModelOffset(-offset[0], offset[1], -offset[2]);
        Framebuffer mainFb = RenderUtils.fb();
        DrawableTexture texture1;
        DrawableTexture texture2;

        if (otherFb != null)
        {
            texture1 = otherFb.getColorAttachment();
            texture2 = otherFb.getDepthAttachment();
        }
        else
        {
            texture1 = mainFb.getColorAttachment();
            texture2 = mainFb.getDepthAttachment();
        }

        // Attach Frame buffers
        try (class_10883 device = RenderSystem.getDevice()
                                              .method_68389()
                                              .method_68368(texture1, OptionalInt.empty(),
                                                            texture2, OptionalDouble.empty()))
        {
            device.method_68412(this.shader);
            device.method_68411(this.shapeIndex.method_68274(this.bufferIndex), this.shapeIndex.getIndexType());
            device.method_68410(0, this.buffer);
            device.method_68408(0, this.bufferIndex);
        }

        RenderSystem.resetModelOffset();
        this.started = false;
    }

    public void drawLayer() throws RuntimeException
    {
        this.ensureSafe();
        this.drawLayer(RenderUtils.fb(), this.builder.endNullable());
    }

    public void drawLayer(Framebuffer fb) throws RuntimeException
    {
        this.ensureSafe();
        this.drawLayer(fb, this.builder.endNullable());
    }

    public void drawLayer(Framebuffer fb, BuiltBuffer meshData) throws RuntimeException
    {
        this.ensureSafe();

        if (RenderSystem.isOnRenderThread())
        {
            try
            {
                GpuBuffer gpuBuffer = this.shader.getFormat().method_68460(meshData.getBuffer());
                GpuBuffer sortedBuffer = meshData.getSortedBuffer() != null ? this.shader.getFormat().method_68461(meshData.getSortedBuffer()) : null;

                try (class_10883 dev = RenderSystem.getDevice()
                                                   .method_68389()
                                                   .method_68368(fb.getColorAttachment(), OptionalInt.empty(),
                                                                 fb.useDepthAttachment ? fb.getDepthAttachment() : null, OptionalDouble.empty()))
                {
                    // SetShader ?
                    dev.method_68412(this.shader);
                    dev.method_68410(0, gpuBuffer);

                    // Scissor
                    if (RenderSystem.SCISSOR_STATE.method_68455())
                    {
                        dev.method_68413(RenderSystem.SCISSOR_STATE);
                    }

                    // Draw Textures
                    for (int i = 0; i < 12; i++)
                    {
                        DrawableTexture texture = RenderSystem.getShaderTexture(i);

                        if (texture != null)
                        {
                            dev.method_68414("RenderContext"+ i, texture);
                        }
                    }

                    // Sorting
                    if (sortedBuffer != null)
                    {
                        dev.method_68411(sortedBuffer, meshData.getDrawParameters().indexType());
                    }
                    else
                    {
                        RenderSystem.ShapeIndexBuffer shapeIndex = RenderSystem.getSequentialBuffer(meshData.getDrawParameters().mode());
                        dev.method_68411(shapeIndex.method_68274(meshData.getDrawParameters().indexCount()), shapeIndex.getIndexType());
                    }

                    // Unbind ?
                    dev.method_68408(0, meshData.getDrawParameters().indexCount());
                }

            }
            catch (Exception err)
            {
                if (meshData != null)
                {
                    try
                    {
                        meshData.close();
                    }
                    catch (Exception ignored) {}
                }

                this.started = false;
                throw new RuntimeException("Exception drawing; "+ err.getMessage());
            }

            meshData.close();
            this.started = false;
        }
    }

    public void drawTranslucentLayer() throws RuntimeException
    {
        this.ensureSafe();
        this.drawTranslucentLayer(RenderUtils.fb(), this.builder);
    }

    public void drawTranslucentLayer(Framebuffer fb) throws RuntimeException
    {
        this.ensureSafe();
        this.drawTranslucentLayer(fb, this.builder);
    }

    public void drawTranslucentLayer(Framebuffer fb, BufferBuilder builder) throws RuntimeException
    {
        this.ensureSafe();

        if (RenderSystem.isOnRenderThread())
        {
            try
            {
                BuiltBuffer meshData = builder.endNullable();

                if (meshData != null)
                {
                    meshData.sortQuads(this.alloc, RenderSystem.getProjectionType().getVertexSorter());
                }

                this.drawLayer(fb, meshData);
            }
            catch (Exception err)
            {
                throw new RuntimeException("Translucent Draw Failure!");
            }
        }
    }

    public static AbstractTexture bindTexture(Identifier id)
    {
        RenderUtils.mc().getTextureManager().registerTexture(id);
        return RenderUtils.mc().getTextureManager().getTexture(id);
    }

    public static void unbindTexture(Identifier id)
    {
        RenderUtils.mc().getTextureManager().destroyTexture(id);
    }

    public void upload() throws RuntimeException
    {
        this.ensureSafeNoShader();
        this.upload(this.builder.endNullable());
    }

    public void upload(BuiltBuffer meshData) throws RuntimeException
    {
        this.ensureSafeNoShader();

        if (RenderSystem.isOnRenderThread())
        {
            if (meshData != null)
            {
                int expectedSize = meshData.getBuffer().remaining();

                if (this.buffer != null)
                {
                    this.buffer.close();
                }

                this.buffer = RenderSystem.getDevice().method_68386(name, GlBufferTarget.VERTICES, this.usage, expectedSize);

                RenderSystem.getDevice()
                            .method_68389()
                            .method_68350(this.buffer, meshData.getBuffer(), 0);
            }
        }
    }

    public void drawTextured(@Nullable Framebuffer otherFb, Identifier texture, int color, float[] offset, ShaderPipeline shader)
    {
        this.shader = shader;
        this.ensureSafe();

        if (offset.length != 3)
        {
            throw new RuntimeException("Offset needs to be a size of 3.");
        }

        if (RenderSystem.isOnRenderThread())
        {
            float a = ColorHelper.getAlphaFloat(color);
            float r = ColorHelper.getRedFloat(color);
            float g = ColorHelper.getGreenFloat(color);
            float b = ColorHelper.getBlueFloat(color);
            float time = (float) (Util.getMeasuringTimeMs() % 3000L) / 3000.0F;

            RenderUtils.color(r, g, b, a);
            RenderSystem.setTextureMatrix(new Matrix4f().translation(time, time, 0.0f));
            RenderSystem.setModelOffset(offset[0], offset[1], offset[2]);
            AbstractTexture tex = RenderUtils.mc().getTextureManager().getTexture(texture);
            tex.setFilter(TriState.FALSE, false);

            Framebuffer mainFb = RenderUtils.fb();
            DrawableTexture texture1;
            DrawableTexture texture2;

            if (otherFb != null)
            {
                texture1 = otherFb.getColorAttachment();
                texture2 = otherFb.getDepthAttachment();
            }
            else
            {
                texture1 = mainFb.getColorAttachment();
                texture2 = mainFb.getDepthAttachment();
            }

            try (class_10883 dev = RenderSystem.getDevice()
                                               .method_68389()
                                               .method_68368(texture1, OptionalInt.empty(),
                                                             texture2, OptionalDouble.empty()))
            {
                dev.method_68412(this.shader);
                dev.method_68411(this.shapeIndex.method_68274(4), this.shapeIndex.getIndexType());
                dev.method_68414("Sampler0", tex.getGlTexture());
                dev.method_68410(0, this.buffer);
            }

            RenderUtils.color(1f, 1f, 1f, 1f);
            RenderSystem.resetTextureMatrix();
            RenderSystem.resetModelOffset();

            this.started = false;
        }
    }

    private void ensureSafeNoShader() throws RuntimeException
    {
        if (!this.started)
        {
            throw new RuntimeException("Context not started!");
        }

        if (this.alloc == null)
        {
            throw new RuntimeException("Allocator not valid!");
        }

        if (this.builder == null)
        {
            throw new RuntimeException("Buffer Builder not valid!");
        }
    }

    private void ensureSafe() throws RuntimeException
    {
        if (!this.started)
        {
            throw new RuntimeException("Context not started!");
        }

        if (this.alloc == null)
        {
            throw new RuntimeException("Allocator not valid!");
        }

        if (this.builder == null)
        {
            throw new RuntimeException("Buffer Builder not valid!");
        }

        if (this.shader == null)
        {
            throw new RuntimeException("Shader Pipeline not valid!");
        }

        /*
        if (!((IMixinBufferBuilder) this.builder).malilib_isBuilding())
        {
            throw new RuntimeException("Builder not building!");
        }

        if (((IMixinBufferBuilder) this.builder).malilib_getVertexCount() == 0)
        {
            throw new RuntimeException("Buffer Builder vertices are zero!");
        }

        if (((IMixinBufferBuilder) this.builder).malilib_getVertexPointer() == -1L)
        {
            throw new RuntimeException("Buffer Builder has no vertices!");
        }
         */
    }

    public void reset()
    {
        if (this.buffer != null)
        {
            this.buffer.close();
            this.buffer = null;
        }

        if (this.builder != null)
        {
            if (((IMixinBufferBuilder) this.builder).malilib_isBuilding() &&
                ((IMixinBufferBuilder) this.builder).malilib_getVertexCount() != 0)
            {
                try
                {
                    BuiltBuffer meshData = this.builder.endNullable();

                    if (meshData != null)
                    {
                        meshData.close();
                    }
                }
                catch (Exception ignored) { }
            }

            this.builder = null;
        }

        if (this.alloc != null)
        {
            this.alloc.close();
            this.alloc = null;
        }

        this.bufferIndex = -1;
        this.started = false;
    }

    @Override
    public void close() throws Exception
    {
        this.reset();
    }
}
