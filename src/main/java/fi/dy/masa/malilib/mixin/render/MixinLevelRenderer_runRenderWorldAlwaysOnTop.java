package fi.dy.masa.malilib.mixin.render;

/**
 * To other Modders: PLEASE use this SAME Mixin point here to create your own Render Pass; and let MaLiLib fix the "executeAlwaysOnTop" Problem for you.
 */
//@Mixin(value = LevelRenderer.class, priority = 500)
@Deprecated
public abstract class MixinLevelRenderer_runRenderWorldAlwaysOnTop
{
//	@Shadow @Final private LevelTargetBundle targets;
//	@Shadow @Final private RenderBuffers renderBuffers;
//	@Shadow @Final private GameRenderer gameRenderer;
//	@Shadow public abstract boolean frameHasAlwaysOnTopGizmos();
//
//	@Unique private boolean cancelAlwaysOnTop;
//
//	@Inject(method = "render",
//	        at = @At(value = "INVOKE",
//	                 target = "Lnet/minecraft/client/renderer/LevelRenderer;addMainPass(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;Lnet/minecraft/client/renderer/feature/FeatureRenderDispatcher$PreparedFrame;Lcom/mojang/renderpearl/api/buffers/GpuBufferSlice;Lnet/minecraft/client/renderer/chunk/ChunkSectionsToRender;Z)V",
//	                 shift = At.Shift.BEFORE
//	        ))
//	private void malilib_onRenderWorldPreMain(GraphicsResourceAllocator resourceAllocator, boolean renderOutline,
//	                                          CameraRenderState cameraState, GpuBufferSlice terrainFog, Vector4f fogColor,
//	                                          boolean shouldRenderSky, boolean consistentDepthRequired, CallbackInfo ci)
//	{
//		this.cancelAlwaysOnTop = false;
//
////		if (this.frameHasAlwaysOnTopGizmos())
////		{
////			this.cancelAlwaysOnTop = ((RenderEventHandler) RenderEventHandler.getInstance()).shouldCancelAlwaysOnTop();
////		}
//	}
//
//	/**
//	 * @implNote This 'executeAlwaysOnTop' clears the Depth Texture --> Breaks "malilib_onRenderWorldLast()"'s Depth without it<br>
//	 * See {@link MixinLevelRenderer_runRenderWorldAlwaysOnTop}
//	 */
//	@Inject(method = "executeAlwaysOnTop", at = @At("HEAD"), cancellable = true)
//	private void malilib_onExecuteAlwaysOnTop(CallbackInfo ci)
//	{
//		if (this.cancelAlwaysOnTop)
//		{
//			ci.cancel();
//		}
//	}
//
//	@Inject(method = "render",
//	        at = @At(value = "INVOKE",
//	                 target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
//	                 ordinal = 3,
//	                 shift = At.Shift.AFTER
//	        ))
//	private void malilib_onRenderWorldAlwaysOnTop(GraphicsResourceAllocator resourceAllocator, boolean renderOutline,
//	                                              CameraRenderState cameraState, GpuBufferSlice terrainFog, Vector4f fogColor,
//	                                              boolean shouldRenderSky, boolean consistentDepthRequired, CallbackInfo ci,
//	                                              @Local(name = "profiler") ProfilerFiller profiler,
//	                                              @Local(name = "frame") FrameGraphBuilder frame,
//	                                              @Local(name = "featureFrame") FeatureRenderDispatcher.PreparedFrame featureFrame)
//	{
//		((RenderEventHandler) RenderEventHandler.getInstance()).runRenderWorldAlwaysOnTop(Minecraft.getInstance(), (LevelRenderer) (Object) this,
//		                                                                                  frame, featureFrame, this.targets,
//		                                                                                  this.gameRenderer.mainCamera().getCullFrustum(), cameraState,
//		                                                                                  this.renderBuffers, consistentDepthRequired,
//		                                                                                  terrainFog, fogColor, profiler);
//	}
}
