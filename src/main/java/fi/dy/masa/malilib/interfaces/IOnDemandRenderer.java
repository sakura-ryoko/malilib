package fi.dy.masa.malilib.interfaces;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4fc;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.profiling.ProfilerFiller;

@ApiStatus.Experimental
public interface IOnDemandRenderer<T extends IOnDemandRenderState>
{
	Supplier<String> name();

	default boolean shouldResort() { return false; }

	default boolean shouldBindTexture() { return false; }

	default boolean shouldDrawColor() { return false; }

	default boolean shouldUseOffset() { return false; }

	default boolean shouldUseRenderContext() { return true; }

	default void tick(Minecraft mc) {}

	default void schedule(T state) {}

	boolean hasData();

	@Nullable
	T updatePre(Camera camera, DeltaTracker tracker, ProfilerFiller profiler);

	default void onUpdatePost(IOnDemandRenderState state) {}

	@Nullable
	T drawPre(Matrix4fc modelViewMatrix, CameraRenderState cameraState, ProfilerFiller profiler);

	default void onDrawPost(IOnDemandRenderState state) {}
}
