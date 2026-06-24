package fi.dy.masa.malilib.interfaces;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4fc;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.util.profiler.Profiler;

@ApiStatus.Experimental
public interface IOnDemandRenderer<T extends IOnDemandRenderState>
{
	Supplier<String> name();

	default boolean shouldResort() { return false; }

	default boolean shouldBindTexture() { return false; }

	default boolean shouldDrawColor() { return false; }

	default boolean shouldUseOffset() { return false; }

	default boolean shouldUseLineWidth() { return false; }

	default boolean shouldUseLightmap() { return false; }

	default boolean shouldUseRenderContext() { return true; }

	default void tick(MinecraftClient mc) {}

	default void schedule(T state) {}

	boolean hasData();

	@Nullable
	T updatePre(Camera camera, RenderTickCounter tracker, Profiler profiler);

	default void onUpdatePost(IOnDemandRenderState state) {}

	@Nullable
	T drawPre(Matrix4fc modelViewMatrix, CameraRenderState cameraState, Profiler profiler);

	default void onDrawPost(IOnDemandRenderState state) {}
}
