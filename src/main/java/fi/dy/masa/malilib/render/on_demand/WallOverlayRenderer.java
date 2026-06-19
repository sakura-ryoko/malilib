package fi.dy.masa.malilib.render.on_demand;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import org.joml.Matrix4fc;
import org.jspecify.annotations.Nullable;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.profiling.ProfilerFiller;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.interfaces.IOnDemandRenderer;
import fi.dy.masa.malilib.render.MaLiLibPipelines;
import fi.dy.masa.malilib.render.RenderContext;
import fi.dy.masa.malilib.render.on_demand.state.*;

public class WallOverlayRenderer implements IOnDemandRenderer<AbstractWallOverlayRenderState>
{
	public static final WallOverlayRenderer INSTANCE = new WallOverlayRenderer();
	private final CopyOnWriteArrayList<WallEntry> pending = new CopyOnWriteArrayList<>();
	private final List<WallEntry> currentEntries = new ArrayList<>();
	private RenderContext renderQuads;
	private RenderContext renderOutlines;

	@Override
	public Supplier<String> name()
	{
		return () -> MaLiLibReference.MOD_ID+":walls";
	}

	@Override
	public boolean shouldUseRenderContext()
	{
		return false;
	}

	public void scheduleWalls(BoxWallQuadsOverlayRenderState quads, BoxWallOutlinesOverlayRenderState outlines)
	{
		this.addPendingEntry(new WallEntry(quads, outlines));
	}

	private void addPendingEntry(WallEntry entry)
	{
		synchronized (this.pending)
		{
			this.pending.add(entry);
		}
	}

	@Override
	public boolean hasData()
	{
		synchronized (this.pending)
		{
			return !this.pending.isEmpty();
		}
	}

	private boolean hasCurrentData()
	{
		return !this.currentEntries.isEmpty();
	}

	private void setupRenderContext()
	{
		if (this.hasCurrentData())
		{
			if (this.renderQuads == null)
			{
				this.renderQuads = new RenderContext(() -> MaLiLibReference.MOD_ID + ":walls/quads", MaLiLibPipelines.MINIHUD_SHAPE_OFFSET_NO_CULL, 0);
			}

			if (this.renderOutlines == null)
			{
				this.renderOutlines = new RenderContext(() -> MaLiLibReference.MOD_ID + ":walls/outlines", MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_LEQUAL_DEPTH, 0);
			}

			this.renderQuads.reset();
			this.renderOutlines.reset();
		}
	}

	@Override
	public @Nullable AbstractWallOverlayRenderState updatePre(Camera camera, DeltaTracker tracker, ProfilerFiller profiler)
	{
		if (this.hasData())
		{
			synchronized (this.pending)
			{
				if (!this.pending.isEmpty())
				{
					this.currentEntries.addAll(this.pending);
					this.pending.clear();
				}
			}
		}

		return null;
	}

	@Override
	public @Nullable AbstractWallOverlayRenderState drawPre(Matrix4fc modelViewMatrix, CameraRenderState cameraState, ProfilerFiller profiler)
	{
		if (!this.hasCurrentData()) { return null; }
		this.setupRenderContext();

		try
		{
			// Wall Quads
			this.batchAndDraw(this.renderQuads, this.currentEntries.stream().map(WallEntry::quads).toList());

			// Wall Outlines
			this.batchAndDraw(this.renderOutlines, this.currentEntries.stream().map(WallEntry::outlines).toList());
		}
		catch (Exception ignored) {}

		this.currentEntries.clear();
		return null;
	}

	private void batchAndDraw(RenderContext ctx, List<? extends AbstractWallOverlayRenderState> states)
	{
		if (states.isEmpty()) { return; }
		AbstractWallOverlayRenderState firstState = states.getFirst();
		BufferBuilder builder = ctx.start(() -> MaLiLibReference.MOD_ID+":walls/batch_draw", firstState.pipeline(), firstState.formatIndex());

		for (AbstractWallOverlayRenderState state : states)
		{
			state.update(builder);
		}

		try (MeshData meshData = builder.build())
		{
			if (meshData != null)
			{
				ctx.draw(meshData, false);
			}
			ctx.reset();
		}
		catch (Exception e)
		{
			MaLiLib.LOGGER.error("WallOverlayRenderer#batchAndDraw(): Draw Exception; {}", e.getLocalizedMessage());
		}
	}

	public record WallEntry(BoxWallQuadsOverlayRenderState quads, BoxWallOutlinesOverlayRenderState outlines)
	{
	}
}
