package fi.dy.masa.malilib.render.on_demand;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Matrix4fc;
import org.jspecify.annotations.Nullable;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.util.profiling.ProfilerFiller;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.interfaces.IOnDemandRenderer;
import fi.dy.masa.malilib.render.MaLiLibPipelines;
import fi.dy.masa.malilib.render.RenderContext;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.on_demand.state.AbstractTextPlateRenderState;
import fi.dy.masa.malilib.render.on_demand.state.TextPlateRenderState;
import fi.dy.masa.malilib.render.text.MaLiLibWorldTextRenderer;

public class TextPlateRenderer implements IOnDemandRenderer<AbstractTextPlateRenderState>
{
	public static final TextPlateRenderer INSTANCE = new TextPlateRenderer();
	private final CopyOnWriteArrayList<AbstractTextPlateRenderState> states = new CopyOnWriteArrayList<>();
	private final List<AbstractTextPlateRenderState> currentStates = new ArrayList<>();
	private RenderContext renderBackground;
	private RenderContext renderBackgroundNoDepth;

	@Override
	public Supplier<String> name()
	{
		return () -> MaLiLibReference.MOD_ID+":text_plate";
	}

	@Override
	public boolean shouldUseRenderContext()
	{
		return false;
	}

	@Override
	public void schedule(AbstractTextPlateRenderState state)
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

	private boolean hasCurrentData()
	{
		return !this.currentStates.isEmpty();
	}

	private void setupRenderContext()
	{
		if (this.hasCurrentData())
		{
			if (this.renderBackground == null)
			{
				this.renderBackground = new RenderContext(() -> MaLiLibReference.MOD_ID + ":text_plate_bg", MaLiLibPipelines.TEXT_PLATE_BG_MASA, 0);
			}

			if (this.renderBackgroundNoDepth == null)
			{
				this.renderBackgroundNoDepth = new RenderContext(() -> MaLiLibReference.MOD_ID + ":text_plate_bg/no_depth", MaLiLibPipelines.TEXT_PLATE_BG_MASA_NO_DEPTH, 0);
			}

			this.renderBackground.reset();
			this.renderBackgroundNoDepth.reset();
		}
	}

	@Override
	public @Nullable AbstractTextPlateRenderState updatePre(Camera camera, DeltaTracker tracker, ProfilerFiller profiler)
	{
		if (this.hasData())
		{
			synchronized (this.states)
			{
				if (!this.states.isEmpty())
				{
					this.currentStates.addAll(this.states);
					this.states.clear();
				}
			}
		}

		return null;
	}

	@Override
	public @Nullable AbstractTextPlateRenderState drawPre(Matrix4fc modelViewMatrix, CameraRenderState cameraState, ProfilerFiller profiler)
	{
		if (this.hasCurrentData())
		{
			this.setupRenderContext();

			try (MaLiLibWorldTextRenderer renderer = new MaLiLibWorldTextRenderer())
			{
				this.currentStates.forEach(state -> this.drawEachInternal(state, cameraState, renderer));
				renderer.close();
			}
			catch (Exception ignored) {}
		}

		this.currentStates.clear();
		return null;
	}

	private void drawEachInternal(AbstractTextPlateRenderState state, CameraRenderState cameraState, MaLiLibWorldTextRenderer renderer)
	{
		double cx = cameraState.pos.x();
		double cy = cameraState.pos.y();
		double cz = cameraState.pos.z();
		float fYaw = cameraState.yRot;
		float fPitch = cameraState.xRot;

		Matrix4fStack global4fStack = RenderSystem.getModelViewStack();

		global4fStack.pushMatrix();
		global4fStack.translate((float) (state.x() - cx), (float) (state.y() - cy), (float) (state.z() - cz));
		global4fStack.rotateYXZ((-fYaw) * ((float) (Math.PI / 180.0)), fPitch * ((float) (Math.PI / 180.0)), 0.0F);
		global4fStack.scale((-state.scale()), (-state.scale()), state.scale());

		TextPlateRenderState st = (TextPlateRenderState) state;
		RenderContext ctx = st.seeThrough() ? this.renderBackgroundNoDepth : this.renderBackground;
		BufferBuilder builder = ctx.start(() -> MaLiLibReference.MOD_ID + ":text_plate_bg", st.pipeline(), st.formatIndex());
		state.update(builder);

		try (MeshData meshData = builder.build())
		{
			if (meshData != null)
			{
				ctx.draw(meshData, false);
				meshData.close();
			}

			ctx.reset();
		}
		catch (Exception e)
		{
			MaLiLib.LOGGER.error("TextPlateRenderer: Draw Exception; {}", e.getLocalizedMessage());
		}

		Font font = Minecraft.getInstance().font;
		final int textColor = st.textColor().getIntValue();
		int textY = 0;
		Matrix4f matrix4f = new Matrix4f();

		for (String line : st.text())
		{
			Component comp = Component.literal(line);
			final int lineWidth = font.width(comp);
			float textX = switch (st.alignment())
			{
				case LEFT -> -st.strLenHalf();
				case RIGHT -> st.strLenHalf() - lineWidth;
				case CENTER -> -(lineWidth / 2.0F);
			};

			renderer.prepare(matrix4f, RenderUtils.camPos(), st.light(), st.seeThrough() ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL);

			Font.PreparedText preparedText = font.prepareText(comp.getVisualOrderText(),
			                                                  textX, textY,
			                                                  st.seeThrough() ? (0x20000000 | (textColor & 0xF0FFFFFF)) : textColor,
			                                                  false,
			                                                  false,
			                                                  0);

			preparedText.visit(renderer);
			textY += font.lineHeight;
		}

		renderer.draw(RenderUtils.camPos());
		RenderSystem.getModelViewStack().popMatrix();
	}
}
