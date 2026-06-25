package fi.dy.masa.malilib.render.on_demand;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.joml.Matrix4fStack;
import org.joml.Matrix4fc;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.interfaces.IOnDemandRenderer;
import fi.dy.masa.malilib.render.RenderContext;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.on_demand.state.AbstractBlockTargetingOverlayRenderState;
import fi.dy.masa.malilib.render.on_demand.state.SimpleBlockTargetingOverlayLinesRenderState;
import fi.dy.masa.malilib.render.on_demand.state.SimpleBlockTargetingOverlayQuadsRenderState;
import fi.dy.masa.malilib.util.MathUtils;
import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.malilib.util.position.Vec3d;

public class SimpleBlockTargetingOverlayRenderer implements IOnDemandRenderer<AbstractBlockTargetingOverlayRenderState>
{
	private final IConfigBoolean config;
	private final @Nullable IKeybind keybind;
	private final boolean useCtrl;
	private final boolean useAlt;
	private Color4f targetColor;
	private Color4f lineColor;
	private float lineWidth;
	private Target currentTarget;
	private Target lastTarget;
	private Entry currentEntry;
	private RenderContext renderQuads;
	private RenderContext renderLines;
	private boolean dirty;

	public SimpleBlockTargetingOverlayRenderer(IConfigBoolean config, boolean useCtrl, boolean useAlt)
	{
		this(Color4f.fromColor(0xC03030F0), config, useCtrl, useAlt, null);
	}

	public SimpleBlockTargetingOverlayRenderer(IConfigBoolean config, boolean useCtrl, boolean useAlt, @Nullable IKeybind keybind)
	{
		this(Color4f.fromColor(0xC03030F0), config, useCtrl, useAlt, keybind);
	}

	public SimpleBlockTargetingOverlayRenderer(Color4f targetColor, IConfigBoolean config, boolean useCtrl, boolean useAlt, @Nullable IKeybind keybind)
	{
		this.targetColor = targetColor;
		this.config = config;
		this.useCtrl = useCtrl;
		this.useAlt = useAlt;
		this.keybind = keybind;
		this.lineColor = Color4f.WHITE;
		this.lineWidth = 1.6F;
	}

	@Override
	public Supplier<String> name()
	{
		return () -> MaLiLibReference.MOD_ID+":simple_block_targeting_overlay";
	}

	@Override
	public boolean shouldDrawColor()
	{
		return true;
	}

	@Override
	public boolean shouldUseRenderContext()
	{
		return false;
	}

	public void setTargetColor(final Color4f targetColor)
	{
		this.targetColor = targetColor;
	}

	public void setLineColor(final Color4f lineColor)
	{
		this.lineColor = lineColor;
	}

	public void setLineWidth(final float lineWidth)
	{
		this.lineWidth = MathUtils.clamp(lineWidth, 0.1F, 8.0F);
	}

	@Override
	public void tick(Minecraft mc)
	{
		this.checkConfigAndTarget(mc);
	}

	private void checkConfigAndTarget(Minecraft mc)
	{
		Entity entity = mc.getCameraEntity();

		if (entity != null &&
			mc.hitResult != null &&
			mc.hitResult.getType() == HitResult.Type.BLOCK &&
			this.config.getBooleanValue())
		{
			BlockHitResult hitResult = (BlockHitResult) mc.hitResult;

			if (this.keybind != null)
			{
				if (this.keybind.isKeybindHeld())
				{
					this.scheduleTarget(entity.getDirection(), hitResult.getBlockPos(), hitResult.getDirection());
				}
				else { this.reset(); }
			}
			else if (this.useCtrl)
			{
				if (GuiBase.isCtrlDown())
				{
					this.scheduleTarget(entity.getDirection(), hitResult.getBlockPos(), hitResult.getDirection());
				}
				else { this.reset(); }
			}
			else if (this.useAlt)
			{
				if (GuiBase.isAltDown())
				{
					this.scheduleTarget(entity.getDirection(), hitResult.getBlockPos(), hitResult.getDirection());
				}
				else { this.reset(); }
			}
		}
		else { this.reset(); }
	}

	private void scheduleTarget(Direction facing, BlockPos pos, Direction side)
	{
		Target newTarget = new Target(facing, pos, side);

		if (this.currentTarget == null || !this.currentTarget.equals(newTarget))
		{
			this.reset();
			this.lastTarget = this.currentTarget;
			this.currentTarget = newTarget;
			this.dirty = true;
		}
	}

	@Override
	public boolean hasData()
	{
		return this.currentTarget != null;
	}

	private Entry buildEntry(Vec3d camPos, Direction facing, BlockPos pos, Direction side)
	{
		SimpleBlockTargetingOverlayQuadsRenderState quadsState  = new SimpleBlockTargetingOverlayQuadsRenderState(
				pos, camPos, this.targetColor, this.lineColor, this.lineWidth, side, facing
		);
		SimpleBlockTargetingOverlayLinesRenderState linesState = new SimpleBlockTargetingOverlayLinesRenderState(
				pos, camPos, this.targetColor, this.lineColor, this.lineWidth, side, facing
		);

		quadsState.updateCameraOffset(camPos);
		linesState.updateCameraOffset(camPos);

		return new Entry(quadsState, linesState);
	}

	private void setupRenderContext()
	{
		if (this.currentEntry != null)
		{
			if (this.renderQuads == null)
			{
				this.renderQuads = new RenderContext(() -> MaLiLibReference.MOD_ID+":simple_block_targeting_overlay/quads", this.currentEntry.quadsState().pipeline(), this.currentEntry.quadsState().formatIndex());
			}
			if (this.renderLines == null)
			{
				this.renderLines = new RenderContext(() -> MaLiLibReference.MOD_ID+":simple_block_targeting_overlay/lines", this.currentEntry.linesState().pipeline(), this.currentEntry.linesState().formatIndex());
			}

			this.renderQuads.reset();
			this.renderLines.reset();
		}
	}

	private void uploadBuffers()
	{
		if (this.currentEntry != null)
		{
			if (this.renderQuads != null && !this.renderQuads.isUploaded())
			{
				BufferBuilder buffer = this.renderQuads.start(() -> MaLiLibReference.MOD_ID+":block_targeting_overlay/quads", this.currentEntry.quadsState().pipeline(), this.currentEntry.quadsState().formatIndex());
				this.currentEntry.quadsState().update(buffer);

				try (MeshData meshData = buffer.build())
				{
					if (meshData != null)
					{
						this.renderQuads.upload(meshData, false);
						meshData.close();
					}
				}
				catch (Exception err)
				{
					MaLiLib.LOGGER.error("SimpleBlockTargetingOverlayRenderer:QUADS: Upload Exception; {}", err.getLocalizedMessage());
				}
			}
			if (this.renderLines != null && !this.renderLines.isUploaded())
			{
				BufferBuilder buffer = this.renderLines.start(() -> MaLiLibReference.MOD_ID+":block_targeting_overlay/lines", this.currentEntry.linesState().pipeline(), this.currentEntry.linesState().formatIndex());
				this.currentEntry.linesState().update(buffer);

				try (MeshData meshData = buffer.build())
				{
					if (meshData != null)
					{
						this.renderLines.color(this.lineColor.getIntValue());
						this.renderLines.upload(meshData, false);
						meshData.close();
					}
				}
				catch (Exception err)
				{
					MaLiLib.LOGGER.error("SimpleBlockTargetingOverlayRenderer:LINES: Upload Exception; {}", err.getLocalizedMessage());
				}
			}
		}
	}

	private boolean hasEntry()
	{
		return this.currentEntry != null;
	}

	private void drawBuffers()
	{
		if (this.currentEntry != null)
		{
			if (this.renderQuads != null && this.renderQuads.isUploaded())
			{
				this.renderQuads.drawPost();
			}
			if (this.renderLines != null && this.renderLines.isUploaded())
			{
				this.renderLines.drawPost(true);
			}
		}
	}

	private void reset()
	{
		this.currentTarget = null;
		this.lastTarget = null;
		this.currentEntry = null;
		this.dirty = true;

		if (this.renderQuads != null)
		{
			this.renderQuads.reset();
		}
		if (this.renderLines != null)
		{
			this.renderLines.reset();
		}
	}

	@Override
	public @Nullable AbstractBlockTargetingOverlayRenderState updatePre(Camera camera, DeltaTracker tracker, ProfilerFiller profiler)
	{
		if (this.hasData() && this.dirty)
		{
			if (this.currentEntry == null ||
				this.lastTarget == null ||
				!this.lastTarget.equals(this.currentTarget))
			{
				this.lastTarget = this.currentTarget;
				this.currentEntry = this.buildEntry(Vec3d.of(camera.position()), this.currentTarget.facing(), this.currentTarget.pos(), this.currentTarget.side());
				this.setupRenderContext();
				this.uploadBuffers();
			}

			this.dirty = false;
		}

		return null;
	}

	@Override
	public @Nullable AbstractBlockTargetingOverlayRenderState drawPre(Matrix4fc modelViewMatrix, CameraRenderState cameraState, ProfilerFiller profiler)
	{
		if (this.hasEntry())
		{
			SimpleBlockTargetingOverlayQuadsRenderState state = this.currentEntry.quadsState();
			Matrix4fStack global4fStack = RenderSystem.getModelViewStack();

			global4fStack.pushMatrix();
			RenderUtils.blockTargetingOverlayTranslations(state.x(), state.y(), state.z(), state.side(), state.facing(), global4fStack);
			this.drawBuffers();
			global4fStack.popMatrix();
		}

		return null;
	}

	public record Target(Direction facing, BlockPos pos, Direction side)
	{
		@Override
		public boolean equals(Object o)
		{
			if (this == o) { return true; }
			if (o == null || this.getClass() != o.getClass()) { return false; }

			Target other = (Target) o;

			return  this.facing().equals(other.facing()) &&
					this.pos().equals(other.pos()) &&
					this.side().equals(other.side());
		}
	}

	public record Entry(SimpleBlockTargetingOverlayQuadsRenderState quadsState,
	                    SimpleBlockTargetingOverlayLinesRenderState linesState) {}
}
