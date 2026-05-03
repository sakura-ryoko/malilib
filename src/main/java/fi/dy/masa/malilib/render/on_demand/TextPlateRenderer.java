package fi.dy.masa.malilib.render.on_demand;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4fStack;
import org.joml.Matrix4fc;
import org.jspecify.annotations.Nullable;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.profiling.ProfilerFiller;

import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.interfaces.IOnDemandRenderState;
import fi.dy.masa.malilib.interfaces.IOnDemandRenderer;
import fi.dy.masa.malilib.render.on_demand.state.TextPlateBackgroundRenderState;

/**
 * WARNING!!! Not tested!
 */
@ApiStatus.Experimental
public class TextPlateRenderer implements IOnDemandRenderer<TextPlateBackgroundRenderState>
{
	private final CopyOnWriteArrayList<TextPlateBackgroundRenderState> states = new CopyOnWriteArrayList<>();
	private TextPlateBackgroundRenderState currentState;

	@Override
	public Supplier<String> name()
	{
		return () -> MaLiLibReference.MOD_ID+":text_plate";
	}

	@Override
	public void schedule(TextPlateBackgroundRenderState state)
	{
		synchronized (this.states)
		{
			this.states.add(state);
		}
	}

	@Override
	public boolean hasData()
	{
		synchronized (this.states)
		{
			return !this.states.isEmpty();
		}
	}

	@Override
	public @Nullable TextPlateBackgroundRenderState updatePre(Camera camera, DeltaTracker tracker, ProfilerFiller profiler)
	{
		if (this.hasData())
		{
			synchronized (this.states)
			{
				return this.states.removeFirst();
			}
		}

		return null;
	}

	@Override
	public void onUpdatePost(IOnDemandRenderState state)
	{
		this.currentState = (TextPlateBackgroundRenderState) state;
	}

	@Override
	public @Nullable TextPlateBackgroundRenderState drawPre(Matrix4fc modelViewMatrix, CameraRenderState cameraState, ProfilerFiller profiler)
	{
		if (this.currentState != null)
		{
			double cx = cameraState.pos.x();
			double cy = cameraState.pos.y();
			double cz = cameraState.pos.z();

			Matrix4fStack global4fStack = RenderSystem.getModelViewStack();
			TextPlateBackgroundRenderState state = this.currentState;

			global4fStack.pushMatrix();
			global4fStack.translate((float) (state.x() - cx), (float) (state.y() - cy), (float) (state.z() - cz));
			global4fStack.rotateYXZ((-state.yaw()) * ((float) (Math.PI / 180.0)), state.pitch() * ((float) (Math.PI / 180.0)), 0.0F);
			global4fStack.scale((-state.scale()), (-state.scale()), state.scale());

			return state;
		}

		return null;
	}

	@Override
	public void onDrawPost(IOnDemandRenderState state)
	{
		// TODO: Port to 26.2 rendering paradigm.
		//  MultiBufferSource.immediate() and Font.drawInBatch() were removed.
		//  Text rendering now uses SubmitNodeCollector.submitText() which requires
		//  a collector context not available here. This needs architectural rework.
		RenderSystem.getModelViewStack().popMatrix();
		this.currentState = null;
	}
}
