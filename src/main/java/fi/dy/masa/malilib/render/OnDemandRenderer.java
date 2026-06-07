package fi.dy.masa.malilib.render;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4fc;
import org.joml.Vector4f;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.profiling.ProfilerFiller;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.interfaces.*;

/**
 * This is a new Experimental "update / extract / draw" three-stage callback system with a single
 * Integrated RenderContext; such that a Downstream mod will not need to utilize {@link IRenderer} if this is used; and
 * to be able to properly split a basic "Press a Keybind to simply render something" into this 3-part methodology.
 * Downstream mods will ONLY need to register a new instance of each State Renderer by utilizing a Renderer 'key'
 */
@ApiStatus.Experimental
public class OnDemandRenderer implements IOnDemandRenderManager, IClientTickHandler, IRenderer
{
	private static final OnDemandRenderer INSTANCE = new OnDemandRenderer();
	public static OnDemandRenderer getInstance() { return INSTANCE; }

	private static final int INITIAL_CAPACITY = 16;
	private final ConcurrentHashMap<String, IOnDemandRenderer<?>> rendererMap;
	private final ConcurrentHashMap<String, RenderContext> renderContextMap;
	private final ReentrantLock lock = new ReentrantLock();

	private OnDemandRenderer()
	{
		this.rendererMap = new ConcurrentHashMap<>(INITIAL_CAPACITY, 0.9f, 1);
		this.renderContextMap = new ConcurrentHashMap<>(INITIAL_CAPACITY, 0.9f, 1);
	}

	@Override
	public <T extends IOnDemandRenderState> void registerOnDemandRenderer(String key, IOnDemandRenderer<T> renderer)
			throws RuntimeException
	{
		if (this.rendererMap.containsKey(key))
		{
			throw new RuntimeException("OnDemandRenderer: Duplicate renderer key: '"+key+"' provided");
		}

		synchronized (this.rendererMap)
		{
			this.rendererMap.putIfAbsent(key, renderer);
		}
	}

	@ApiStatus.Internal
	@Override
	public Supplier<String> getProfilerSectionSupplier()
	{
		return () -> "OnDemandRenderer";
	}

	@ApiStatus.Internal
	@Override
	public void onClientTick(Minecraft mc)
	{
		synchronized (this.rendererMap)
		{
			for (IOnDemandRenderer<?> renderer : this.rendererMap.values())
			{
				renderer.tick(mc);
			}
		}
	}

	@ApiStatus.Internal
	@Override
	public void onExtractWorldLast(DeltaTracker deltaTracker, Camera camera, float ticks, ProfilerFiller profiler)
	{
		this.onUpdateStates(camera, deltaTracker, profiler);
	}

	@ApiStatus.Internal
	private void onUpdateStates(Camera camera, DeltaTracker deltaTracker, ProfilerFiller profiler)
	{
		synchronized (this.rendererMap)
		{
			Set<String> keys = this.rendererMap.keySet();

			for (String key : keys)
			{
				IOnDemandRenderer<?> renderer = this.rendererMap.get(key);

				if (renderer != null && renderer.hasData())
				{
					this.onUpdateEachState(key, renderer, camera, deltaTracker, profiler);
				}
			}
		}
	}

	@ApiStatus.Internal
	private void onUpdateEachState(String key, IOnDemandRenderer<?> renderer, Camera camera, DeltaTracker deltaTracker, ProfilerFiller profiler)
	{
		this.lock.lock();
		try
		{
			IOnDemandRenderState state = renderer.updatePre(camera, deltaTracker, profiler);

			if (state != null && renderer.shouldUseRenderContext())
			{
				RenderContext ctx = this.getRenderContext(key, renderer.name(), state.pipeline(), state.formatIndex());

				if (ctx.isStarted())
				{
					ctx.reset();
				}

				if (state.bindOverlay())
				{
					ctx = ctx.withOverlay();
				}

				if (state.bindLightmap())
				{
					ctx = ctx.withLightmap();
				}

				BufferBuilder builder = ctx.start(renderer.name(), state.pipeline(), state.formatIndex());

				if (!state.complexTextures().isEmpty() || state.bindLightmap() || state.bindOverlay())
				{
					ctx.prepareComplexTextures(state.complexTextures());
				}
				else if (renderer.shouldBindTexture() && state.texture() != null)
				{
					try
					{
						ctx.bindTexture(state.texture(), state.textureId(), state.textureWidth(), state.textureHeight());
					}
					catch (RuntimeException e)
					{
						MaLiLib.LOGGER.warn("OnDemandRenderer: Exception binding texture for [{}] id: [{}]; {}", renderer.name().get(), state.texture(), e.getLocalizedMessage());
					}
				}

				state.update(builder);
				renderer.onUpdatePost(state);

				try (MeshData meshData = builder.build())
				{
					if (meshData != null)
					{
						if (renderer.shouldResort())
						{
							ctx.upload(meshData, true);
							ctx.startResorting(meshData, ctx.createVertexSorter(camera));
						}
						else
						{
							ctx.upload(meshData, false);
						}

						meshData.close();
					}
				}
				catch (Throwable ignored) {}
			}
			else if (!renderer.shouldUseRenderContext())
			{
				renderer.onUpdatePost(state);
			}
		}
		finally
		{
			this.lock.unlock();
		}
	}

	@ApiStatus.Internal
	private RenderContext getRenderContext(String key, Supplier<String> name, RenderPipeline pipeline, int formatIndex)
	{
		synchronized (this.renderContextMap)
		{
			if (!this.renderContextMap.containsKey(key))
			{
				this.renderContextMap.put(key, new RenderContext(name, pipeline, formatIndex));
			}

			return this.renderContextMap.get(key);
		}
	}

	@ApiStatus.Internal
	@Override
	public void onRenderWorldLast(RenderTarget fb, Matrix4fc modelViewMatrix, CameraRenderState cameraState, Frustum culling, RenderBuffers buffers, GpuBufferSlice terrainFog, Vector4f fogColor, ProfilerFiller profiler)
	{
		this.onDrawStates(modelViewMatrix, cameraState, profiler);
	}

	@ApiStatus.Internal
	private void onDrawStates(Matrix4fc modelViewMatrix, CameraRenderState cameraState, ProfilerFiller profiler)
	{
		synchronized (this.rendererMap)
		{
			Set<String> keys = this.rendererMap.keySet();

			for (String key : keys)
			{
				IOnDemandRenderer<?> renderer = this.rendererMap.get(key);

				if (renderer != null)
				{
					this.onDrawEachState(key, renderer, modelViewMatrix, cameraState, profiler);
				}
			}
		}
	}

	@ApiStatus.Internal
	private void onDrawEachState(String key, IOnDemandRenderer<?> renderer, Matrix4fc modelViewMatrix, CameraRenderState cameraState, ProfilerFiller profiler)
	{
		this.lock.lock();
		try
		{
			IOnDemandRenderState state = renderer.drawPre(modelViewMatrix, cameraState, profiler);

			if (state != null && renderer.shouldUseRenderContext())
			{
				RenderContext ctx = this.getRenderContext(key, renderer.name(), state.pipeline(), state.formatIndex());

				if (!ctx.isUploaded())
				{
					ctx.reset();
					MaLiLib.LOGGER.error("OnDemandRenderer: Error, Render Context for [{}] has not been uploaded!", renderer.name().get());
				}
				else
				{
					boolean useColor = false;
					boolean useOffset = false;

					if (renderer.shouldDrawColor())
					{
						ctx.color(state.color().getIntValue());
						useColor = true;
					}
					if (renderer.shouldUseOffset())
					{
						if (state.offset().length == 3)
						{
							ctx.offset(state.offset());
							useOffset = true;
						}
						else
						{
							MaLiLib.LOGGER.warn("OnDemandRenderer: Error, Model Offset for [{}] has is the wrong size!", renderer.name().get());
						}
					}

					try
					{
						ctx.drawPost(null, useColor, useOffset);
					}
					catch (RuntimeException ignored) {}

					ctx.reset();
				}

				renderer.onDrawPost(state);
			}
			else if (!renderer.shouldUseRenderContext())
			{
				renderer.onDrawPost(state);
			}
		}
		finally
		{
			this.lock.unlock();
		}
	}
}
