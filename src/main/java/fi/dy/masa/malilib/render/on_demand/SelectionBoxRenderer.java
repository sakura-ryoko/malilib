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

public class SelectionBoxRenderer implements IOnDemandRenderer<AbstractSelectionBoxRenderState>
{
	public static final SelectionBoxRenderer INSTANCE = new SelectionBoxRenderer();
	private final CopyOnWriteArrayList<BoxEntry> pending = new CopyOnWriteArrayList<>();
	private final List<BoxEntry> currentEntries = new ArrayList<>();
	private RenderContext renderAreaSides;
	private RenderContext renderAreaOutlinesNoCorners;
	private RenderContext renderBlockOutlines1;
	private RenderContext renderBlockOutlines2;
	private RenderContext renderBlockOutlinesOverlapping;

	@Override
	public Supplier<String> name()
	{
		return () -> MaLiLibReference.MOD_ID+":selection_box";
	}

	@Override
	public boolean shouldUseRenderContext()
	{
		return false;
	}

	public void scheduleBlockOutline(BlockOutlineRenderState state)
	{
		this.addPendingEntry(new BlockOutlineEntry(state));
	}

	public void scheduleBlockBoxWithOutline(AreaSidesRenderState sides, BlockOutlineRenderState outline)
	{
		this.addPendingEntry(new BlockWithOutlineEntry(sides, outline));
	}

	public void scheduleBlockOutlineOverlapping(BlockOutlineOverlappingRenderState state)
	{
		this.addPendingEntry(new BlockOutlineOverlappingEntry(state));
	}

	public void scheduleSelectionBox(AreaOutlineNoCornersRenderState areaOutlines, AreaSidesRenderState areaSides, BlockOutlineRenderState blockOutlines1, BlockOutlineRenderState blockOutlines2)
	{
		this.addPendingEntry(new SelectionBoxEntry(areaOutlines, areaSides, blockOutlines1, blockOutlines2));
	}

	private void addPendingEntry(BoxEntry entry)
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
			if (this.renderAreaOutlinesNoCorners == null)
			{
				this.renderAreaOutlinesNoCorners = new RenderContext(() -> MaLiLibReference.MOD_ID + ":selection_box/area_outlines", MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_LEQUAL_DEPTH, 0);
			}

			if (this.renderAreaSides == null)
			{
				this.renderAreaSides = new RenderContext(() -> MaLiLibReference.MOD_ID + ":selection_box/area_sides", MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_LEQUAL_DEPTH, 0);
			}

			if (this.renderBlockOutlines1 == null)
			{
				this.renderBlockOutlines1 = new RenderContext(() -> MaLiLibReference.MOD_ID + ":selection_box/block_outlines1", MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_LEQUAL_DEPTH, 0);
			}

			if (this.renderBlockOutlines2 == null)
			{
				this.renderBlockOutlines2 = new RenderContext(() -> MaLiLibReference.MOD_ID + ":selection_box/block_outlines2", MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_LEQUAL_DEPTH, 0);
			}

			if (this.renderBlockOutlinesOverlapping == null)
			{
				this.renderBlockOutlinesOverlapping = new RenderContext(() -> MaLiLibReference.MOD_ID + ":selection_box/block_outlines_overlap", MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_LEQUAL_DEPTH, 0);
			}

			this.renderAreaSides.reset();
			this.renderAreaOutlinesNoCorners.reset();
			this.renderBlockOutlines1.reset();
			this.renderBlockOutlines2.reset();
			this.renderBlockOutlinesOverlapping.reset();
		}
	}

	@Override
	public @Nullable AbstractSelectionBoxRenderState updatePre(Camera camera, DeltaTracker tracker, ProfilerFiller profiler)
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
	public @Nullable AbstractSelectionBoxRenderState drawPre(Matrix4fc modelViewMatrix, CameraRenderState cameraState, ProfilerFiller profiler)
	{
		if (!this.hasCurrentData()) { return null; }
		this.setupRenderContext();

		try
		{
			// Area Outlines (No Corners)
			this.batchAndDraw(this.renderAreaOutlinesNoCorners,
			                  this.currentEntries.stream().map(BoxEntry::areaOutlineNoCorners).filter(s -> !s.isEmpty()).toList());

			// Area Sides (Quads)
			List<AreaSidesRenderState> sides = this.currentEntries.stream().map(BoxEntry::areaSides).filter(s -> !s.isEmpty()).toList();

			List<AreaSidesRenderState> insideSides = sides.stream().filter(AreaSidesRenderState::isCameraInside).toList();
			this.batchAndDraw(this.renderAreaSides, insideSides);

			List<AreaSidesRenderState> outsideSides = sides.stream().filter(s -> !s.isCameraInside()).toList();
			this.batchAndDraw(this.renderAreaSides, outsideSides);

			// Block Outlines 1 & 2
			this.batchAndDraw(this.renderBlockOutlines1,
			                  this.currentEntries.stream().map(BoxEntry::blockOutlines).filter(s -> !s.isEmpty()).toList());

			this.batchAndDraw(this.renderBlockOutlines2,
			                  this.currentEntries.stream().map(BoxEntry::blockOutlines2).filter(s -> !s.isEmpty()).toList());

			// Overlapping Block Outlines
			this.batchAndDraw(this.renderBlockOutlinesOverlapping,
			                  this.currentEntries.stream().map(BoxEntry::blockOutlineOverlapping).filter(s -> !s.isEmpty()).toList());
		}
		catch (Exception ignored) {}

		this.currentEntries.clear();
		return null;
	}

	private void batchAndDraw(RenderContext ctx, List<? extends AbstractSelectionBoxRenderState> states)
	{
		if (states.isEmpty()) { return; }
		AbstractSelectionBoxRenderState firstState = states.getFirst();
		BufferBuilder builder = ctx.start(() -> MaLiLibReference.MOD_ID+":selection_box/batch_draw", firstState.pipeline(), firstState.formatIndex());

		for (AbstractSelectionBoxRenderState state : states)
		{
			state.update(builder);
		}

		try (MeshData meshData = builder.build())
		{
			if (meshData != null)
			{
				ctx.draw(meshData, firstState.shouldResort());
			}
			ctx.reset();
		}
		catch (Exception e)
		{
			MaLiLib.LOGGER.error("SelectionBoxRenderer#batchAndDraw(): Draw Exception; {}", e.getLocalizedMessage());
		}
	}

	public interface BoxEntry
	{
		AreaOutlineNoCornersRenderState areaOutlineNoCorners();
		AreaSidesRenderState areaSides();
		BlockOutlineRenderState blockOutlines();
		BlockOutlineRenderState blockOutlines2();
		BlockOutlineOverlappingRenderState blockOutlineOverlapping();
	}

	public record BlockOutlineEntry(BlockOutlineRenderState blockOutlines) implements BoxEntry
	{
		@Override
		public AreaOutlineNoCornersRenderState areaOutlineNoCorners()
		{
			return AreaOutlineNoCornersRenderState.EMPTY;
		}

		@Override
		public AreaSidesRenderState areaSides()
		{
			return AreaSidesRenderState.EMPTY;
		}

		@Override
		public BlockOutlineRenderState blockOutlines2()
		{
			return BlockOutlineRenderState.EMPTY;
		}

		@Override
		public BlockOutlineOverlappingRenderState blockOutlineOverlapping()
		{
			return BlockOutlineOverlappingRenderState.EMPTY;
		}
	}

	public record BlockWithOutlineEntry(AreaSidesRenderState areaSides, BlockOutlineRenderState blockOutlines) implements BoxEntry
	{
		@Override
		public AreaOutlineNoCornersRenderState areaOutlineNoCorners()
		{
			return AreaOutlineNoCornersRenderState.EMPTY;
		}

		@Override
		public BlockOutlineRenderState blockOutlines2()
		{
			return BlockOutlineRenderState.EMPTY;
		}

		@Override
		public BlockOutlineOverlappingRenderState blockOutlineOverlapping()
		{
			return BlockOutlineOverlappingRenderState.EMPTY;
		}
	}

	public record BlockOutlineOverlappingEntry(BlockOutlineOverlappingRenderState blockOutlineOverlapping) implements BoxEntry
	{
		@Override
		public AreaOutlineNoCornersRenderState areaOutlineNoCorners()
		{
			return AreaOutlineNoCornersRenderState.EMPTY;
		}

		@Override
		public AreaSidesRenderState areaSides()
		{
			return AreaSidesRenderState.EMPTY;
		}

		@Override
		public BlockOutlineRenderState blockOutlines()
		{
			return BlockOutlineRenderState.EMPTY;
		}

		@Override
		public BlockOutlineRenderState blockOutlines2()
		{
			return BlockOutlineRenderState.EMPTY;
		}
	}

	public record SelectionBoxEntry(AreaOutlineNoCornersRenderState areaOutlineNoCorners, AreaSidesRenderState areaSides,
	                                BlockOutlineRenderState blockOutlines,
	                                BlockOutlineRenderState blockOutlines2) implements BoxEntry
	{
		@Override
		public BlockOutlineOverlappingRenderState blockOutlineOverlapping()
		{
			return BlockOutlineOverlappingRenderState.EMPTY;
		}
	}
}
