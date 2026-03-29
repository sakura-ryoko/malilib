package fi.dy.masa.malilib.interfaces;

import java.util.function.Supplier;
import javax.annotation.Nullable;

import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4fc;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.profiling.ProfilerFiller;

@ApiStatus.Experimental
public interface IOnDemandRenderer<T extends IOnDemandRenderState>
{
	Supplier<String> name();

	boolean shouldResort();

	boolean shouldBindTexture();

	boolean shouldDrawColor();

	boolean shouldUseOffset();

	void tick();

	boolean hasData();

	@Nullable
	T updatePre(Camera camera, DeltaTracker tracker, ProfilerFiller profiler);

	void onUpdatePost(IOnDemandRenderState state);

	@Nullable
	T drawPre(Matrix4fc modelViewMatrix, CameraRenderState cameraState, ProfilerFiller profiler);

	void onDrawPost(IOnDemandRenderState state);
}
