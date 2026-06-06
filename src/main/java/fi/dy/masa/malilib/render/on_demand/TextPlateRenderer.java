package fi.dy.masa.malilib.render.on_demand;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4fStack;
import org.joml.Matrix4fc;
import org.jspecify.annotations.Nullable;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.util.profiling.ProfilerFiller;

import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.interfaces.IOnDemandRenderState;
import fi.dy.masa.malilib.interfaces.IOnDemandRenderer;
import fi.dy.masa.malilib.render.RenderUtils;
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
		TextPlateBackgroundRenderState st = (TextPlateBackgroundRenderState) state;
		Font font = Minecraft.getInstance().font;
		final int textColor = st.textColor().getIntValue();
		int textY = 0;
//		Matrix4f modelMatrix = new Matrix4f();
//		modelMatrix.identity();
//		ByteBufferBuilder allocator = new ByteBufferBuilder(RenderType.TRANSIENT_BUFFER_SIZE);

		PoseStack pose = new PoseStack();
		SubmitNodeStorage nodes = RenderUtils.nodes();

		for (String line : st.text())
		{
//			MultiBufferSource.BufferSource immediate = MultiBufferSource.immediate(allocator);
//
//			font.drawInBatch(line, -st.strLenHalf(), textY,
//			                 st.disableDepth() ? (0x20000000 | (textColor & 0xFFFFFFFF)) : textColor,
//			                 false, modelMatrix, immediate,
//			                 st.disableDepth() ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.POLYGON_OFFSET,
//			                 0, 15728880
//			);
//
//			immediate.endBatch();

			Component comp = Component.literal(line);

			nodes.submitText(
					pose,
					-st.strLenHalf(), textY,
					comp.getVisualOrderText(), false,
					st.disableDepth() ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.POLYGON_OFFSET,
					0,
					st.disableDepth() ? (0x20000000 | (textColor & 0xFFFFFFFF)) : textColor,
					15728880,
					0
			);

			textY += font.lineHeight;
		}

//		allocator.close();
		RenderSystem.getModelViewStack().popMatrix();
		this.currentState = null;
	}
}
