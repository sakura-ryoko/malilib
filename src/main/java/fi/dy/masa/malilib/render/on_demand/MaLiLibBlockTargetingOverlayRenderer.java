package fi.dy.masa.malilib.render.on_demand;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.jetbrains.annotations.ApiStatus;
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
import net.minecraft.world.phys.Vec3;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.interfaces.IOnDemandRenderer;
import fi.dy.masa.malilib.render.RenderContext;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.on_demand.state.AbstractMaLiLibBlockTargetingOverlayRenderState;
import fi.dy.masa.malilib.render.on_demand.state.MaLiLibBlockTargetingOverlayCenterRenderState;
import fi.dy.masa.malilib.render.on_demand.state.MaLiLibBlockTargetingOverlayEdgesRenderState;
import fi.dy.masa.malilib.render.on_demand.state.MaLiLibBlockTargetingOverlaySideRenderState;
import fi.dy.masa.malilib.util.MathUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.malilib.util.position.PositionUtils;

@ApiStatus.Experimental
public class MaLiLibBlockTargetingOverlayRenderer implements IOnDemandRenderer<AbstractMaLiLibBlockTargetingOverlayRenderState>
{
	private final static float TICK_RATE = 0.5F;
	private final IConfigBoolean config;
	private final @Nullable IKeybind keybind;
	private final boolean useCtrl;
	private final boolean useAlt;
	private final Color4f targetColor;
	private Color4f lineColor;
	private float lineWidth;
	private long lastTick;

	private Target currentTarget;
	private Entry currentEntry;
	private RenderContext renderSides;
	private RenderContext renderCenter;
	private RenderContext renderEdges;

	public MaLiLibBlockTargetingOverlayRenderer(IConfigBoolean config, boolean useCtrl, boolean useAlt)
	{
		this(Color4f.fromColor(StringUtils.getColor("#C03030F0", 0)), config, useCtrl, useAlt, null);
	}

	public MaLiLibBlockTargetingOverlayRenderer(IConfigBoolean config, boolean useCtrl, boolean useAlt, @Nullable IKeybind keybind)
	{
		this(Color4f.fromColor(StringUtils.getColor("#C03030F0", 0)), config, useCtrl, useAlt, keybind);
	}

	public MaLiLibBlockTargetingOverlayRenderer(Color4f targetColor, IConfigBoolean config, boolean useCtrl, boolean useAlt, @Nullable IKeybind keybind)
	{
		this.targetColor = targetColor;
		this.config = config;
		this.useCtrl = useCtrl;
		this.useAlt = useAlt;
		this.keybind = keybind;
		this.lastTick = System.currentTimeMillis();
		this.lineColor = Color4f.WHITE;
		this.lineWidth = 1.6F;
	}

	@Override
	public Supplier<String> name()
	{
		return () -> MaLiLibReference.MOD_ID+":block_targeting_overlay";
	}

	@Override
	public boolean shouldDrawColor()
	{
		return true;
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
		final long now = System.currentTimeMillis();

		if ((now - this.lastTick) > this.calcTickRate())
		{
			this.checkConfigAndTarget(mc);
			this.lastTick = now;
		}
	}

	private long calcTickRate()
	{
		return (long) (TICK_RATE * 1000L);
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

			if (this.keybind != null && this.keybind.isKeybindHeld())
			{
				this.scheduleTarget(entity.getDirection(), hitResult.getBlockPos(), hitResult.getDirection(), hitResult.getLocation());
			}
			else if (this.useCtrl && GuiBase.isCtrlDown())
			{
				this.scheduleTarget(entity.getDirection(), hitResult.getBlockPos(), hitResult.getDirection(), hitResult.getLocation());
			}
			else if (this.useAlt && GuiBase.isAltDown())
			{
				this.scheduleTarget(entity.getDirection(), hitResult.getBlockPos(), hitResult.getDirection(), hitResult.getLocation());
			}
		}
	}

	private void scheduleTarget(Direction facing, BlockPos pos, Direction side, Vec3 hitVec)
	{
		if (this.currentTarget == null)
		{
			this.currentTarget = new Target(facing, pos, side, hitVec);
		}
	}

	@Override
	public boolean hasData()
	{
		return this.currentTarget != null;
	}

	private Entry buildEntry(Vec3 camPos, Direction facing, BlockPos pos, Direction side, Vec3 hitVec)
	{
		PositionUtils.HitPart part = PositionUtils.getHitPart(side, facing, pos, hitVec);

		MaLiLibBlockTargetingOverlaySideRenderState sideState       = new MaLiLibBlockTargetingOverlaySideRenderState(
				pos, camPos, this.targetColor, this.lineColor, this.lineWidth, side, facing, part
		);
		MaLiLibBlockTargetingOverlayCenterRenderState centerState   = new MaLiLibBlockTargetingOverlayCenterRenderState(
				pos, camPos, this.targetColor, this.lineColor, this.lineWidth, side, facing, part
		);
		MaLiLibBlockTargetingOverlayEdgesRenderState edgesState     = new MaLiLibBlockTargetingOverlayEdgesRenderState(
				pos, camPos, this.targetColor, this.lineColor, this.lineWidth, side, facing, part
		);

		sideState.updateCameraOffset(camPos);
		centerState.updateCameraOffset(camPos);
		edgesState.updateCameraOffset(camPos);

		return new Entry(sideState, centerState, edgesState);
	}

	private void setupRenderContext()
	{
		if (this.currentEntry != null)
		{
			if (this.renderSides == null)
			{
				this.renderSides = new RenderContext(() -> MaLiLibReference.MOD_ID+":block_targeting_overlay/side", this.currentEntry.sideState().pipeline());
			}
			if (this.renderCenter == null)
			{
				this.renderCenter = new RenderContext(() -> MaLiLibReference.MOD_ID+":block_targeting_overlay/center", this.currentEntry.centerState().pipeline());
			}
			if (this.renderEdges == null)
			{
				this.renderEdges = new RenderContext(() -> MaLiLibReference.MOD_ID+":block_targeting_overlay/edges", this.currentEntry.edgesState().pipeline());
			}

			this.renderSides.reset();
			this.renderCenter.reset();
			this.renderEdges.reset();
		}
	}

	private void uploadBuffers()
	{
		if (this.currentEntry != null)
		{
			if (this.renderSides != null && !this.renderSides.isUploaded())
			{
				BufferBuilder buffer = this.renderSides.getBuilder();
				this.currentEntry.sideState().update(buffer);

				try (MeshData meshData = buffer.build())
				{
					if (meshData != null)
					{
						this.renderSides.color(this.targetColor.getIntValue());
						this.renderSides.upload(buffer, false);
					}
				}
				catch (Exception err)
				{
					MaLiLib.LOGGER.error("MaLiLibBlockTargetingOverlayRenderer:SIDES: Upload Exception; {}", err.getLocalizedMessage());
				}
			}
			if (this.renderCenter != null && !this.renderCenter.isUploaded())
			{
				BufferBuilder buffer = this.renderCenter.getBuilder();
				this.currentEntry.centerState().update(buffer);

				try (MeshData meshData = buffer.build())
				{
					if (meshData != null)
					{
						this.renderCenter.color(this.lineColor.getIntValue());
						this.renderCenter.upload(buffer, false);
					}
				}
				catch (Exception err)
				{
					MaLiLib.LOGGER.error("MaLiLibBlockTargetingOverlayRenderer:CENTER: Upload Exception; {}", err.getLocalizedMessage());
				}
			}
			if (this.renderEdges != null && !this.renderEdges.isUploaded())
			{
				BufferBuilder buffer = this.renderEdges.getBuilder();
				this.currentEntry.edgesState().update(buffer);

				try (MeshData meshData = buffer.build())
				{
					if (meshData != null)
					{
						this.renderEdges.color(this.lineColor.getIntValue());
						this.renderEdges.upload(buffer, false);
					}
				}
				catch (Exception err)
				{
					MaLiLib.LOGGER.error("MaLiLibBlockTargetingOverlayRenderer:EDGES: Upload Exception; {}", err.getLocalizedMessage());
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
			if (this.renderSides != null && this.renderSides.isUploaded())
			{
				this.renderSides.drawPost(true);
			}
			if (this.renderCenter != null && this.renderCenter.isUploaded())
			{
				this.renderCenter.drawPost(true);
			}
			if (this.renderEdges != null && this.renderEdges.isUploaded())
			{
				this.renderEdges.drawPost(true);
			}
		}
	}

	private void reset()
	{
		this.currentTarget = null;
		this.currentEntry = null;

		if (this.renderSides != null)
		{
			this.renderSides.reset();
		}
		if (this.renderCenter != null)
		{
			this.renderCenter.reset();
		}
		if (this.renderEdges != null)
		{
			this.renderEdges.reset();
		}
	}

	@Override
	public @Nullable AbstractMaLiLibBlockTargetingOverlayRenderState updatePre(Camera camera, DeltaTracker tracker, ProfilerFiller profiler)
	{
		if (this.hasData())
		{
			Target target = this.currentTarget;

			if (target != null)
			{
				this.currentEntry = this.buildEntry(camera.position(), target.facing(), target.pos(), target.side(), target.hitVec());
				this.setupRenderContext();
				this.uploadBuffers();
				this.currentTarget = null;
			}
		}

		return null;
	}

	@Override
	public @Nullable AbstractMaLiLibBlockTargetingOverlayRenderState drawPre(Matrix4fc modelViewMatrix, CameraRenderState cameraState, ProfilerFiller profiler)
	{
		if (this.hasEntry())
		{
			MaLiLibBlockTargetingOverlaySideRenderState state = this.currentEntry.sideState();
			Matrix4fStack global4fStack = RenderSystem.getModelViewStack();
			global4fStack.pushMatrix();
			RenderUtils.blockTargetingOverlayTranslations(state.x(), state.y(), state.z(), state.side(), state.facing(), global4fStack);

			this.drawBuffers();
			global4fStack.popMatrix();
		}

		this.reset();
		return null;
	}

	public record Target(Direction facing, BlockPos pos, Direction side, Vec3 hitVec) {}

	public record Entry(MaLiLibBlockTargetingOverlaySideRenderState sideState,
	                    MaLiLibBlockTargetingOverlayCenterRenderState centerState,
	                    MaLiLibBlockTargetingOverlayEdgesRenderState edgesState) {}
}
