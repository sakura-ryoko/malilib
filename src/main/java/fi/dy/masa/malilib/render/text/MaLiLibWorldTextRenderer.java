package fi.dy.masa.malilib.render.text;

import java.util.List;
import org.joml.Matrix4f;
import org.jspecify.annotations.NonNull;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.world.phys.Vec3;

import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.render.RenderContext;
import fi.dy.masa.malilib.render.texture.MaLiLibComplexBinding;

public class MaLiLibWorldTextRenderer implements Font.GlyphVisitor, AutoCloseable
{
	private final RenderContext context;
	private final Matrix4f pose;
	private int light;
	private Font.DisplayMode mode;
	private boolean shouldResort;
	private boolean grayscale;
	private Vec3 camPos;
	private RenderSetup lastRenderSetup;

	public MaLiLibWorldTextRenderer()
	{
		super();
		this.pose = new Matrix4f();
		this.light = 15728880;
		this.mode = Font.DisplayMode.NORMAL;
		this.shouldResort = false;
		this.grayscale = false;
		this.camPos = Vec3.ZERO;
		this.context = new RenderContext(() -> MaLiLibReference.MOD_ID+"_world_text", RenderPipelines.TEXT, 0);
		this.lastRenderSetup = null;
	}

	public void prepare(Matrix4f pose, Vec3 camPos)
	{
		this.prepare(pose, camPos, 15728880);
	}

	public void prepare(Matrix4f pose, Vec3 camPos, int light)
	{
		this.prepare(pose, camPos, light, Font.DisplayMode.NORMAL);
	}

	public void prepare(Matrix4f pose, Vec3 camPos, int light, Font.DisplayMode displayMode)
	{
		this.prepare(pose, camPos, light, displayMode, false);
	}

	public void prepare(Matrix4f pose, Vec3 camPos, int light, Font.DisplayMode displayMode, boolean grayscale)
	{
		this.pose.set(pose);
		this.light = light;
		this.mode = displayMode;
		this.grayscale = grayscale;
		this.camPos = camPos;
	}

	@Override
	public void acceptRenderable(final @NonNull TextRenderable renderable)
	{
		if (this.mode != Font.DisplayMode.NORMAL)
		{
			this.shouldResort = true;
		}

		RenderSetup current = renderable.renderType(this.mode).state;

		if (this.context.isStarted() && this.lastRenderSetup != current)
		{
			this.draw(this.camPos);
		}

		if (!this.context.isStarted())
		{
			List<MaLiLibComplexBinding> textures = MaLiLibComplexBinding.fromRenderSetup(current);
			RenderPipeline pipeline = this.grayscale ? this.selectGrayscale(this.mode) : this.selectColor(this.mode);

			this.context.start(() -> MaLiLibReference.MOD_ID+"_text_plate", pipeline, 0);
			this.context.withLightmap();
			this.context.prepareComplexTextures(textures);
			this.lastRenderSetup = current;
		}

		renderable.render(this.pose, this.context.getBuilder(), this.light, false);
	}

	public RenderPipeline selectGrayscale(final Font.DisplayMode mode)
	{
		return switch (mode)
		{
			case NORMAL -> RenderPipelines.TEXT_GRAYSCALE;
			case POLYGON_OFFSET -> RenderPipelines.TEXT_GRAYSCALE_POLYGON_OFFSET;
			case SEE_THROUGH -> RenderPipelines.TEXT_GRAYSCALE_SEE_THROUGH;
		};
	}

	public RenderPipeline selectColor(final Font.DisplayMode mode)
	{
		return switch (mode)
		{
			case NORMAL -> RenderPipelines.TEXT;
			case POLYGON_OFFSET -> RenderPipelines.TEXT_POLYGON_OFFSET;
			case SEE_THROUGH -> RenderPipelines.TEXT_SEE_THROUGH;
		};
	}

	public void draw(Vec3 camPos)
	{
		BufferBuilder builder = this.context.getBuilder();

		if (builder != null)
		{
			try (MeshData meshData = builder.build())
			{
				if (meshData != null)
				{
					if (this.shouldResort)
					{
						this.context.upload(meshData, true);
						this.context.startResorting(meshData, this.context.createVertexSorter(camPos));
					}
					else
					{
						this.context.upload(meshData, false);
					}

					this.context.drawPost();
					meshData.close();
				}
			}
			catch (Exception ignored) {}
		}

		this.context.reset();
	}

	public void reset()
	{
		this.context.reset();
		this.light = 15728880;
		this.mode = Font.DisplayMode.NORMAL;
		this.shouldResort = false;
		this.grayscale = false;
		this.lastRenderSetup = null;
		this.camPos = Vec3.ZERO;
	}

	@Override
	public void close() throws Exception
	{
		this.context.close();
	}
}
