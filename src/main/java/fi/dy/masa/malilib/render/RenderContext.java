package fi.dy.masa.malilib.render;

import java.util.*;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.ScissorState;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.texture.TextureContents;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import com.mojang.blaze3d.systems.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.mixin.render.IMixinAbstractTexture;
import fi.dy.masa.malilib.mixin.render.IMixinBufferBuilder;

/**
 * MaLiLib 1.21.5+ RenderContext for World Rendering
 * -
 * NOTE:  lineWidth is now part of the Vertex Buffer Builder params.
 */
public class RenderContext implements AutoCloseable
{
    private Supplier<String> name;
    private RenderPipeline shader;
    private GpuBuffer vertexBuffer;
    @Nullable private GpuBuffer indexBuffer;
    private RenderSystem.ShapeIndexBuffer shapeIndex;
    private VertexFormat.IndexType indexType;
    private BufferAllocator alloc;
    private BufferBuilder builder;
    private VertexFormat format;
    private VertexFormat.DrawMode drawMode;
    private final HashMap<Integer, ResourceTexture> textures;
    @Nullable private BuiltBuffer.SortState sortState;
    private float[] offset;
//    private float lineWidth;
    private int color;
    private boolean started;
    private boolean uploaded;
    private int indexCount;

    public RenderContext(Supplier<String> name, RenderPipeline shader)
    {
        this.name = name;
        this.alloc = new BufferAllocator(shader.getVertexFormat().getVertexSize() * 4);
        this.builder = new BufferBuilder(this.alloc, shader.getVertexFormatMode(), shader.getVertexFormat());
        this.shapeIndex = RenderSystem.getSequentialBuffer(shader.getVertexFormatMode());
        this.indexType = this.shapeIndex.getIndexType();
        this.format = shader.getVertexFormat();
        this.drawMode = shader.getVertexFormatMode();
        this.shader = shader;
        this.vertexBuffer = null;
        this.indexBuffer = null;
        this.sortState = null;
        this.indexCount = -1;
		this.textures = new HashMap<>();
        this.offset = new float[]{0f, 0f, 0f};
        this.color = -1;
//        this.lineWidth = 1.0f;
        this.started = true;
        this.uploaded = false;
    }

    public BufferBuilder start(Supplier<String> name, RenderPipeline shader)
    {
        this.reset();
        this.name = name;
        this.alloc = new BufferAllocator(shader.getVertexFormat().getVertexSize() * 4);
        this.builder = new BufferBuilder(this.alloc, shader.getVertexFormatMode(), shader.getVertexFormat());
        this.shapeIndex = RenderSystem.getSequentialBuffer(shader.getVertexFormatMode());
        this.indexType = this.shapeIndex.getIndexType();
        this.format = shader.getVertexFormat();
        this.drawMode = shader.getVertexFormatMode();
        this.shader = shader;
        this.vertexBuffer = null;
        this.indexBuffer = null;
        this.sortState = null;
        this.indexCount = -1;
        this.offset = new float[]{0f, 0f, 0f};
        this.color = -1;
//        this.lineWidth = 1.0f;
        this.started = true;
        this.uploaded = false;
        return this.builder;
    }

    public boolean isStarted() { return this.started; }

    public boolean isUploaded() { return this.uploaded; }

    public String getName()
    {
        return this.name.get();
    }

    public BufferBuilder getBuilder()
    {
        return this.builder;
    }

    public VertexFormat getVertexFormat()
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
            return this.shader.getVertexFormat();
        }

        return this.format;
    }

    public VertexFormat.DrawMode getShaderDrawMode()
    {
        if (this.shader != null)
        {
            return this.shader.getVertexFormatMode();
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
    public RenderContext setBuilder(BufferBuilder builder) throws RuntimeException
    {
        this.ensureBuilding(builder);
        this.builder = builder;
        return this;
    }

//    public RenderContext lineWidth(float width)
//    {
//        this.lineWidth = Math.clamp(width, 0.0f, 25.0f);
//        return this;
//    }

    public RenderContext offset(float[] value)
    {
        if (value.length != 3)
        {
            value = new float[]{0f, 0f, 0f};
        }

        this.offset[0] = value[0];
        this.offset[1] = value[1];
        this.offset[2] = value[2];

        return this;
    }

    public RenderContext color(int color)
    {
        this.color = color;
        return this;
    }

    /**
     * UPLOAD PHASE --
     * -
     * This uploads the BufferBuilder to the GpuBuffer for Drawing
     */
    public void upload() throws RuntimeException
    {
        this.upload(false);
    }

    public void upload(boolean shouldResort) throws RuntimeException
    {
        this.ensureSafeNoShader();
        this.ensureBuilding(this.builder);

        try (BuiltBuffer meshData = this.builder.endNullable())
        {
            if (meshData != null)
            {
                this.upload(meshData, shouldResort);
            }
            else
            {
                throw new RuntimeException("Empty Mesh Data!");
            }
        }
    }

    public void upload(BufferBuilder builder) throws RuntimeException
    {
        this.upload(builder, false);
    }

    public void upload(BufferBuilder builder, boolean shouldResort) throws RuntimeException
    {
        this.ensureSafeNoShader();
        this.ensureBuilding(builder);
        this.builder = builder;

        try (BuiltBuffer meshData = this.builder.endNullable())
        {
            if (meshData != null)
            {
                this.upload(meshData, shouldResort);
            }
            else
            {
                throw new RuntimeException("Empty Mesh Data!");
            }
        }
    }

    public void upload(BuiltBuffer meshData, boolean shouldResort) throws RuntimeException
    {
        this.ensureSafeNoShader();

        if (RenderSystem.isOnRenderThread() && meshData != null)
        {
            int expectedSize = meshData.getBuffer().remaining();

            if (this.vertexBuffer != null)
            {
                this.vertexBuffer.close();
            }

            if (this.indexBuffer != null)
            {
                this.indexBuffer.close();
                this.indexBuffer = null;
            }

            GpuDevice device = RenderSystem.tryGetDevice();

            if (device == null)
            {
                MaLiLib.LOGGER.warn("RenderContext#upload: GpuDevice is null for renderer '{}'", this.name.get());
                return;
            }

            if (this.vertexBuffer == null)
            {
                // BufferType.VERTICES - 40
                this.vertexBuffer = device.createBuffer(() -> this.name.get()+" VertexBuffer", 40, expectedSize);
            }
            else if (this.vertexBuffer.size() < expectedSize)
            {
                this.vertexBuffer.close();
                this.vertexBuffer = device.createBuffer(() -> this.name.get()+" VertexBuffer", 40, expectedSize);
            }

            CommandEncoder encoder = device.createCommandEncoder();

            if (!this.vertexBuffer.isClosed())
            {
                encoder.writeToBuffer(this.vertexBuffer.slice(), meshData.getBuffer());
            }
            else
            {
                throw new RuntimeException("Vertex Buffer is closed!");
            }

            // Resorting
            if (shouldResort && meshData.getSortedBuffer() != null)
            {
                if (this.indexBuffer != null && this.indexBuffer.size() >= meshData.getSortedBuffer().remaining())
                {
                    if (!this.indexBuffer.isClosed())
                    {
                        encoder.writeToBuffer(this.indexBuffer.slice(), meshData.getSortedBuffer());
                    }
                }
                else
                {
                    if (this.indexBuffer != null)
                    {
                        this.indexBuffer.close();
                    }

                    // BufferType.INDICES --> 72
                    this.indexBuffer = device.createBuffer(() -> this.name.get()+" IndexBuffer", 72, meshData.getSortedBuffer());
                }
            }
            else if (this.indexBuffer != null)
            {
                this.indexBuffer.close();
                this.indexBuffer = null;
            }

            this.indexCount = meshData.getDrawParameters().indexCount();
            this.indexType = meshData.getDrawParameters().indexType();
            this.uploaded = true;
//            meshData.close();
        }
    }

    /**
     * INDEX RESORTING PHASE --
     * -
     * Performs the Index Buffer Resorting
     */
    protected VertexSorter createVertexSorter(float x, float y, float z)
    {
        return VertexSorter.byDistance(x, y, z);
    }

    public VertexSorter createVertexSorter(Vec3d pos)
    {
        return this.createVertexSorter(pos, BlockPos.ORIGIN);
    }

    public VertexSorter createVertexSorter(Camera camera)
    {
        return this.createVertexSorter(camera.getCameraPos(), BlockPos.ORIGIN);
    }

    public VertexSorter createVertexSorter(Camera camera, BlockPos origin)
    {
        return this.createVertexSorter(camera.getCameraPos(), origin);
    }

    public VertexSorter createVertexSorter(Vec3d pos, BlockPos origin)
    {
        return VertexSorter.byDistance((float)(pos.x - (double)origin.getX()), (float)(pos.y - (double) origin.getY()), (float)(pos.z - (double) origin.getZ()));
    }

    public void startResorting(@Nonnull BuiltBuffer meshData, @Nonnull VertexSorter sorter) throws RuntimeException
    {
        this.ensureSafeNoBuffer();

        if (RenderSystem.isOnRenderThread())
        {
            this.sortState = meshData.sortQuads(this.alloc, sorter);
            this.resortTranslucent(sorter);
        }
    }

    public boolean shouldResort()
    {
        return this.sortState != null;
    }

    public void resortTranslucent(@Nonnull VertexSorter sorter) throws RuntimeException
    {
        this.ensureSafeNoBuffer();

        if (RenderSystem.isOnRenderThread())
        {
            if (this.sortState == null)
            {
                throw new RuntimeException("Sort State is empty!");
            }

            BufferAllocator.CloseableBuffer result = this.sortState.sortAndStore(this.alloc, sorter);

            if (result != null)
            {
                this.uploadIndex(result);
                result.close();
            }
            else
            {
                throw new RuntimeException("Unable to Store Sorting Data in Result Buffer!");
            }
        }
    }

    public void uploadIndex(@Nonnull BufferAllocator.CloseableBuffer buffer) throws RuntimeException
    {
        this.ensureSafeNoBuffer();

        if (RenderSystem.isOnRenderThread())
        {
            GpuDevice device = RenderSystem.tryGetDevice();

            if (device == null)
            {
                MaLiLib.LOGGER.warn("RenderContext#uploadIndex: GpuDevice is null for renderer '{}'", this.name.get());
                return;
            }

            if (this.indexBuffer == null)
            {
                this.indexBuffer = device.createBuffer(() -> this.name.get()+" IndexBuffer", 72, buffer.getBuffer());
            }
            else
            {
                if (!this.indexBuffer.isClosed())
                {
                    device.createCommandEncoder().writeToBuffer(this.indexBuffer.slice(), buffer.getBuffer());
                }
                else
                {
                    throw new RuntimeException("Index Buffer is closed!");
                }
            }
        }
    }

    /**
     * BIND TEXTURE PHASE --
     * -
     * Performs the Texture Binding/Unbind for the "Shader Texture" layer
     * -------------------------------------------------------------------
     * [Sampler0] - Main or Atlas Texture
     * [Sampler1] - Overlay Texture
     * [Sampler2] - Lightmap Texture
     */
    public void bindTexture(Identifier id, int textureId, int width, int height) throws RuntimeException
    {
        this.ensureSafeNoBuffer();

        if (textureId < 0 || textureId > 12)
        {
            throw new RuntimeException("Invalid textureId of: "+textureId+" for texture: "+id.toString());
        }

        try
        {
            // Verify that we potentially have the correct texture by checking various values
            while (!this.isTextureValid(textureId, width, height))
            {
                this.textures.put(textureId, (ResourceTexture) RenderUtils.tex().getTexture(id));

                if (this.isTextureValid(textureId, width, height))
                {
                    break;
                }
            }
        }
        catch (Exception err)
        {
            throw new RuntimeException("Exception reading Texture ["+id.toString()+"]: "+err.getMessage());
        }

        // General failure & cleanup
        if (this.textures.containsKey(textureId))
        {
            // Simple texture rebind since we already have a valid texture
            return;
        }

        MaLiLib.LOGGER.error("bindTexture: Error uploading texture [{}]", id.toString());

        if (this.textures.containsKey(textureId))
        {
	        try (ResourceTexture tex = this.textures.remove(textureId))
	        {
				tex.close();
	        }
			catch (Exception ignored) {}
        }
    }

    private boolean isTextureValid(int textureId, int width, int height)
    {
        if (this.textures.isEmpty() || !this.textures.containsKey(textureId))
        {
            return false;
        }

		ResourceTexture tex = this.textures.get(textureId);

		try (TextureContents content = tex.loadContents(RenderUtils.mc().getResourceManager()))
		{
			NativeImage image = content.image();

			if (image == null || image.getWidth() != width || image.getHeight() != height)
			{
				return false;
			}
		}
		catch (Exception e)
		{
			this.textures.remove(textureId).close();
			return false;
		}

        if (((IMixinAbstractTexture) tex).malilib_getGlTextureView() == null ||
            tex.getGlTextureView().isClosed())
        {
	        this.textures.remove(textureId).close();
            return false;
        }

        return true;
    }

    public void unbindTexture(@Nullable Identifier id)
    {
        if (id != null)
        {
            RenderUtils.tex().destroyTexture(id);
        }

		List<Integer> list = new ArrayList<>();

		for (Integer key : this.textures.keySet())
        {
			if (this.textures.get(key).getId().equals(id))
			{
				list.add(key);
			}
        }

		for (Integer key : list)
		{
			try (ResourceTexture tex = this.textures.remove(key))
			{
				RenderUtils.tex().destroyTexture(tex.getId());
				tex.close();
			}
			catch (Exception ignored) {}
		}
    }

    /**
     * DRAW PHASE --
     * -
     * Performs the Renderer draw to the specified Frame Buffer
     */
    public void draw() throws RuntimeException
    {
        this.draw(false);
    }

    public void draw(boolean shouldResort) throws RuntimeException
    {
        this.ensureSafeNoBuffer();
        this.ensureBuilding(this.builder);
        BuiltBuffer meshData = this.builder.endNullable();

        if (meshData != null)
        {
            this.draw(meshData, shouldResort);
            meshData.close();
        }
    }

    public void draw(BuiltBuffer meshData) throws RuntimeException
    {
        this.ensureSafeNoBuffer();
        this.draw(null, meshData, false, false, false);
    }

    public void draw(BuiltBuffer meshData, boolean shouldResort) throws RuntimeException
    {
        this.ensureSafeNoBuffer();
        this.draw(null, meshData, shouldResort, false, false);
    }

    public void draw(BuiltBuffer meshData, boolean shouldResort, boolean setColor) throws RuntimeException
    {
        this.ensureSafeNoBuffer();
        this.draw(null, meshData, shouldResort, setColor, false);
    }

    public void draw(BuiltBuffer meshData, boolean shouldResort, boolean setColor, boolean useOffset) throws RuntimeException
    {
        this.ensureSafeNoBuffer();
        this.draw(null, meshData, shouldResort, setColor, useOffset);
    }

    public void draw(@Nullable Framebuffer otherFb, BuiltBuffer meshData, boolean shouldResort) throws RuntimeException
    {
        this.ensureSafeNoBuffer();
        this.draw(otherFb, meshData, shouldResort, false, false);
    }

    public void draw(@Nullable Framebuffer otherFb, BuiltBuffer meshData, boolean shouldResort,
                     boolean setColor, boolean useOffset) throws RuntimeException
    {
        this.ensureSafeNoBuffer();

        if (RenderSystem.isOnRenderThread())
        {
            if (meshData == null)
            {
                this.indexCount = 0;
            }
            else
            {
                // Create & upload buffer
                if (this.indexCount < 1)
                {
                    //MaLiLib.LOGGER.warn("RenderContext#draw() [{}] --> upload()", this.name.get());
                    this.upload(meshData, shouldResort);
                }
            }

            // Draw
            if (this.indexCount > 0)
            {
                float[] rgba = {ColorHelper.getRedFloat(this.color), ColorHelper.getGreenFloat(this.color), ColorHelper.getBlueFloat(this.color), ColorHelper.getAlphaFloat(this.color)};

                //MaLiLib.LOGGER.warn("RenderContext#drawPost() [{}] --> drawInternal()", this.name.get());
                this.drawInternal(otherFb, rgba, setColor, useOffset);
            }
        }
    }

    public void drawPost() throws RuntimeException
    {
        this.ensureSafeNoTexture();
        this.drawPost(null, false, false);
    }

    public void drawPost(boolean setColor) throws RuntimeException
    {
        this.ensureSafeNoTexture();
        this.drawPost(null, setColor, false);
    }

    public void drawPost(@Nullable Framebuffer otherFb) throws RuntimeException
    {
        this.ensureSafeNoTexture();
        this.drawPost(otherFb, false, false);
    }

    public void drawPost(@Nullable Framebuffer otherFb, boolean setColor) throws RuntimeException
    {
        this.ensureSafeNoTexture();
        this.drawPost(otherFb, setColor, false);
    }

    public void drawPost(@Nullable Framebuffer otherFb, boolean setColor, boolean useOffset) throws RuntimeException
    {
        this.ensureSafeNoTexture();

        if (this.indexCount > 0)
        {
            float[] rgba = new float[]{ColorHelper.getRedFloat(this.color), ColorHelper.getGreenFloat(this.color), ColorHelper.getBlueFloat(this.color), ColorHelper.getAlphaFloat(this.color)};

            this.drawInternal(otherFb, rgba, setColor, useOffset);
        }
    }

    private void drawInternal(@Nullable Framebuffer otherFb, float[] rgba, boolean setColor, boolean useOffset) throws RuntimeException
    {
        this.ensureSafeNoTexture();

        if (RenderSystem.isOnRenderThread())
        {
            Vector4f colorMod = new Vector4f(1f, 1f, 1f, 1f);
            Vector3f modelOffset = new Vector3f(0f, 0f, 0f);
            Matrix4f texMatrix = new Matrix4f();
            float line = 0.0f;

            if (setColor)
            {
                colorMod.set(rgba);
            }

//            if (setLineWidth)
//            {
//                line = this.lineWidth > 0.0f ? this.lineWidth : RenderSystem.getShaderLineWidth();
//            }

            if (useOffset)
            {
                modelOffset.set(this.offset);
            }

            GpuDevice device = RenderSystem.getDevice();

            if (device == null)
            {
                MaLiLib.LOGGER.warn("RenderContext#drawInternal: GpuDevice is null for renderer '{}'", this.name.get());
                return;
            }

            Framebuffer mainFb = RenderUtils.fb();
            GpuTextureView texture1;
            GpuTextureView texture2;

            if (otherFb != null)
            {
                texture1 = otherFb.getColorAttachmentView();
                texture2 = otherFb.useDepthAttachment ? otherFb.getDepthAttachmentView() : null;
            }
            else
            {
                texture1 = mainFb.getColorAttachmentView();
                texture2 = mainFb.useDepthAttachment ? mainFb.getDepthAttachmentView() : null;
            }

            //MaLiLib.LOGGER.warn("RenderContext#drawInternal() [{}] --> new renderPass", this.name.get());
            GpuBuffer indexBuffer = this.shapeIndex.getIndexBuffer(this.indexCount);

            //MaLiLib.LOGGER.warn("RenderContext#drawInternal() [{}] renderPass --> setUniform() // lineWidth [{}]", this.name.get(), width);

            GpuBufferSlice gpuSlice = RenderSystem.getDynamicUniforms()
                                                        .write(
                                                                RenderSystem.getModelViewMatrix(),
                                                                colorMod,
                                                                modelOffset,
                                                                texMatrix);

            // Attach Frame buffers
            try (RenderPass pass = device.createCommandEncoder()
                    .createRenderPass(this.name,
                                      texture1, OptionalInt.empty(),
                                      texture2, OptionalDouble.empty())
            )
            {
//                MaLiLib.LOGGER.warn("RenderContext#drawInternal() [{}] renderPass --> setPipeline() [{}] // isDevelopment [{}]", this.name.get(), this.shader.getLocation().toString(), RenderPassImpl.IS_DEVELOPMENT);
                pass.setPipeline(this.shader);

                ScissorState scissorState = RenderSystem.getScissorStateForRenderTypeDraws();

                if (scissorState.isEnabled())
                {
                    pass.enableScissor(scissorState.getX(), scissorState.getY(), scissorState.getWidth(), scissorState.getHeight());
                }

                RenderSystem.bindDefaultUniforms(pass);
                pass.setUniform("DynamicTransforms", gpuSlice);

                //MaLiLib.LOGGER.warn("RenderContext#drawInternal() [{}] renderPass --> setIndexBuffer() [{}]", this.name.get(), this.bufferIndex);
                if (this.indexBuffer == null)
                {
                    pass.setIndexBuffer(indexBuffer, this.shapeIndex.getIndexType());
                }
                else
                {
                    pass.setIndexBuffer(this.indexBuffer, this.indexType);
                }

                //MaLiLib.LOGGER.warn("RenderContext#drawInternal() [{}] renderPass --> setVertexBuffer() [0]", this.name.get());
                pass.setVertexBuffer(0, this.vertexBuffer);

                if (!this.textures.isEmpty())
                {
	                for (int i = 0; i < this.textures.size(); i++)
	                {
						if (this.textures.containsKey(i))
						{
							ResourceTexture tex = this.textures.get(i);

							if (tex != null)
							{
								pass.bindTexture("Sampler"+i, tex.getGlTextureView(), tex.getSampler());
							}
						}
	                }
                }

                //MaLiLib.LOGGER.warn("RenderContext#drawInternal() [{}] renderPass --> drawIndexed() [0, {}]", this.name.get(), this.bufferIndex);
                pass.drawIndexed(0, 0, this.indexCount, 1);
            }

            //MaLiLib.LOGGER.warn("RenderContext#drawInternal() [{}] --> END", this.name.get());
        }
    }

    private void ensureBuilding(BufferBuilder builder) throws RuntimeException
    {
        // Check BufferBuilder status
        if (!((IMixinBufferBuilder) builder).malilib_isBuilding())
        {
            throw new RuntimeException("Buffer Builder is not building!");
        }

        if (((IMixinBufferBuilder) builder).malilib_getVertexCount() == 0)
        {
            throw new RuntimeException("Buffer Builder vertices are zero!");
        }

        if (((IMixinBufferBuilder) builder).malilib_getVertexPointer() == -1L)
        {
            throw new RuntimeException("Buffer Builder has no vertices!");
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

        if (this.name.get().isEmpty())
        {
            this.name = () -> "RenderContext";
        }
    }

    private void ensureSafeNoBuffer() throws RuntimeException
    {
        this.ensureSafeNoShader();

        if (this.shader == null)
        {
            throw new RuntimeException("Shader Pipeline not valid!");
        }
    }

    private void ensureSafeNoTexture() throws RuntimeException
    {
        this.ensureSafeNoBuffer();

        if (this.vertexBuffer == null)
        {
            throw new RuntimeException("GpuBuffer not uploaded!");
        }
    }

    private void ensureSafe()
    {
        this.ensureSafeNoTexture();

        if (this.textures.isEmpty())
        {
            throw new RuntimeException("A Texture Object is expected to be bound");
        }
    }

    public void reset()
    {
        if (this.vertexBuffer != null)
        {
            this.vertexBuffer.close();
            this.vertexBuffer = null;
        }

        if (this.indexBuffer != null)
        {
            this.indexBuffer.close();
            this.indexBuffer = null;
        }

        if (this.sortState != null)
        {
            this.sortState = null;
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

        this.indexCount = -1;
        this.indexType = null;
        this.offset = new float[]{0f, 0f, 0f};
        this.color = -1;
//        this.lineWidth = 1.0f;
        this.started = false;
        this.uploaded = false;
    }

    @Override
    public void close() throws Exception
    {
        if (!this.textures.isEmpty())
        {
			for (ResourceTexture tex : this.textures.values())
			{
				RenderUtils.tex().destroyTexture(tex.getId());
				tex.close();
			}

            this.textures.clear();
        }

        this.reset();
    }
}
