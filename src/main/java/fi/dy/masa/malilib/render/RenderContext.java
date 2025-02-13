package fi.dy.masa.malilib.render;

import javax.annotation.Nullable;

import net.minecraft.class_10785;
import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.GlUsage;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.util.BufferAllocator;

public class RenderContext implements AutoCloseable
{

    @Nullable
    private VertexBuffer vertex;
    private BufferAllocator alloc;
    private BufferBuilder builder;

    public RenderContext()
    {
        this.vertex = null;
        this.alloc = null;
        this.builder = null;
    }

    public RenderContext(VertexFormat.DrawMode drawMode, VertexFormat format)
    {
        this.alloc = new BufferAllocator(format.getVertexSizeByte());
        this.builder = new BufferBuilder(this.alloc, drawMode, format);
        this.vertex = new VertexBuffer(GlUsage.STATIC_WRITE);
    }

    public RenderContext(VertexFormat.DrawMode drawMode, VertexFormat format, GlUsage usage)
    {
        this.alloc = new BufferAllocator(format.getVertexSizeByte());
        this.builder = new BufferBuilder(this.alloc, drawMode, format);
        this.vertex = new VertexBuffer(usage);
    }

    public BufferBuilder start(VertexFormat.DrawMode drawMode, VertexFormat format)
    {
        return this.start(drawMode, format, GlUsage.STATIC_WRITE);
    }

    public BufferBuilder start(VertexFormat.DrawMode drawMode, VertexFormat format, GlUsage usage)
    {
        this.alloc = new BufferAllocator(format.getVertexSizeByte());
        this.builder = new BufferBuilder(this.alloc, drawMode, format);
        this.vertex = new VertexBuffer(usage);

        return this.builder;
    }

    public @Nullable BufferBuilder getBuilder()
    {
        return this.builder;
    }

    public void draw() throws RuntimeException
    {
        if (this.vertex == null)
        {
            throw new RuntimeException("Vertex Buffer is null!");
        }

        if (RenderSystem.isOnRenderThread())
        {
            this.ensureSafe();
            this.vertex.bind();
            this.vertex.upload(this.builder.end());
            this.vertex.draw();
            VertexBuffer.unbind();
        }
    }

    public void draw(BuiltBuffer meshData) throws RuntimeException
    {
        if (this.vertex == null)
        {
            throw new RuntimeException("Vertex Buffer is null!");
        }

        if (RenderSystem.isOnRenderThread())
        {
            this.ensureSafe();
            this.vertex.bind();
            this.vertex.upload(meshData);
            this.vertex.draw();
            VertexBuffer.unbind();
        }
    }

    // fixme RenderLayer.method_67902()
    /*
    @Nullable
		@Override
		public ShaderProgram method_67902() {
			return this.field_56922.method_67730();
		}

		@Override
		public VertexFormat getVertexFormat() {
			return this.field_56922.method_67734();
		}

		@Override
		public VertexFormat.DrawMode getDrawMode() {
			return this.field_56922.method_67735();
		}
     */
    /*
    private static final Function<Double, RenderLayer.MultiPhase> DEBUG_LINE_STRIP = Util.memoize(
		(Function<Double, RenderLayer.MultiPhase>)(lineWidth -> of(
				"debug_line_strip",
				1536,
				class_10799.field_56836,
				RenderLayer.MultiPhaseParameters.builder().lineWidth(new RenderPhase.LineWidth(OptionalDouble.of(lineWidth))).build(false)
			))
	);

	private static final Function<Double, RenderLayer.MultiPhase> field_56918 = Util.memoize(
		(Function<Double, RenderLayer.MultiPhase>)(double_ -> of(
				"debug_line",
				1536,
				class_10799.field_56833,
				RenderLayer.MultiPhaseParameters.builder().lineWidth(new RenderPhase.LineWidth(OptionalDouble.of(double_))).build(false)
			))
	);
	private static final RenderLayer.MultiPhase DEBUG_FILLED_BOX = of(
		"debug_filled_box", 1536, false, true, class_10799.field_56837, RenderLayer.MultiPhaseParameters.builder().layering(VIEW_OFFSET_Z_LAYERING).build(false)
	);
	private static final RenderLayer.MultiPhase DEBUG_QUADS = of(
		"debug_quads", 1536, false, true, class_10799.field_56865, RenderLayer.MultiPhaseParameters.builder().build(false)
	);
	private static final RenderLayer.MultiPhase DEBUG_TRIANGLE_FAN = of(
		"debug_triangle_fan", 1536, false, true, class_10799.field_56866, RenderLayer.MultiPhaseParameters.builder().build(false)
	);
	private static final RenderLayer.MultiPhase DEBUG_STRUCTURE_QUADS = of(
		"debug_structure_quads", 1536, false, true, class_10799.field_56867, RenderLayer.MultiPhaseParameters.builder().build(false)
	);
	private static final RenderLayer.MultiPhase DEBUG_SECTION_QUADS = of(
		"debug_section_quads", 1536, false, true, class_10799.field_56868, RenderLayer.MultiPhaseParameters.builder().layering(VIEW_OFFSET_Z_LAYERING).build(false)
	);

		public static final class_10785 field_56836 = method_67887(
		class_10785.method_67729(field_56849)
			.method_67748("pipeline/debug_line_strip")
			.method_67762("core/position_color")
			.method_67757("core/position_color")
			.method_67753(false)
			.method_67746(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.DEBUG_LINE_STRIP)
			.method_67760()
	);
	public static final class_10785 field_56837 = method_67887(
		class_10785.method_67729(field_56860)
			.method_67748("pipeline/debug_filled_box")
			.method_67746(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLE_STRIP)
			.method_67760()
	);
	public static final class_10785 field_56865 = method_67887(
		class_10785.method_67729(field_56860).method_67748("pipeline/debug_quads").method_67753(false).method_67760()
	);
	public static final class_10785 field_56866 = method_67887(
		class_10785.method_67729(field_56860)
			.method_67748("pipeline/debug_triangle_fan")
			.method_67753(false)
			.method_67746(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLE_FAN)
			.method_67760()
	);
	public static final class_10785 field_56867 = method_67887(
		class_10785.method_67729(field_56860).method_67748("pipeline/debug_structure_quads").method_67753(false).method_67763(false).method_67760()
	);
	public static final class_10785 field_56868 = method_67887(class_10785.method_67729(field_56860).method_67748("pipeline/debug_section_quads").method_67760());
     */


    public void drawWithShaders(BuiltBuffer meshData, class_10785 shaderKey) throws RuntimeException
    {
        if (RenderSystem.isOnRenderThread())
        {
            this.ensureSafe();
            this.drawWithShaders(meshData, RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), shaderKey);
        }
    }

    public void drawWithShaders(BuiltBuffer meshData, Matrix4f modelView, Matrix4f posMatrix, class_10785 shaderKey) throws RuntimeException
    {
        if (this.vertex == null)
        {
            throw new RuntimeException("Vertex Buffer is null!");
        }

        if (RenderSystem.isOnRenderThread())
        {
            this.ensureSafe();
            this.vertex.bind();
            this.vertex.upload(meshData);
            // fixme draw()
            this.vertex.method_67804(modelView, posMatrix, shaderKey.method_67730());
            VertexBuffer.unbind();
            meshData.close();
        }
    }

    private void ensureSafe() throws RuntimeException
    {
        if (this.vertex == null)
        {
            throw new RuntimeException("Vertex Buffer is null!");
        }

        if (this.vertex.isClosed())
        {
            throw new RuntimeException("Vertex Buffer is closed!");
        }

        if (this.alloc == null)
        {
            throw new RuntimeException("Allocator not valid!");
        }

        if (this.builder == null)
        {
            throw new RuntimeException("Buffer Builder not valid!");
        }

        RenderSystem.assertOnRenderThread();
    }

    public void reset()
    {
        if (this.vertex != null && !this.vertex.isClosed())
        {
            this.vertex.close();
            this.vertex = null;
            VertexBuffer.unbind();
        }

        if (this.alloc != null)
        {
            this.alloc.close();
            this.alloc = null;
        }

        if (this.builder != null)
        {
            this.builder = null;
        }
    }

    @Override
    public void close() throws Exception
    {
        this.reset();
    }
}
