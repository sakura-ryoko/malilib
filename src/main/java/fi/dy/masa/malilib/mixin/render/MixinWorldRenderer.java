package fi.dy.masa.malilib.mixin.render;

import com.llamalad7.mixinextras.sugar.Local;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.event.RenderEventHandler;

@Mixin(value = WorldRenderer.class)
public abstract class MixinWorldRenderer
{
    @Shadow @Final private MinecraftClient client;
    @Shadow @Final private DefaultFramebufferSet framebufferSet;
    @Shadow @Final private BufferBuilderStorage bufferBuilders;

    //@Unique private PostEffectProcessor postEffects = null;
    //@Unique private int width;
    //@Unique private int height;

//    @Inject(method = "render",
//            at = @At(value = "INVOKE",
//                     target = "Lnet/minecraft/client/render/WorldRenderer;renderMain(Lnet/minecraft/client/render/FrameGraphBuilder;Lnet/minecraft/client/render/Frustum;Lnet/minecraft/client/render/Camera;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lnet/minecraft/client/render/Fog;ZZLnet/minecraft/client/render/RenderTickCounter;Lnet/minecraft/util/profiler/Profiler;)V",
//                     shift = At.Shift.BEFORE))
//    private void malilib_onRenderWorldPreMain(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline,
//                                                 Camera camera, Matrix4f positionMatrix, Matrix4f projectionMatrix, GpuBufferSlice fog, Vector4f fogColor, boolean shouldRenderSky, CallbackInfo ci,
//                                                 @Local Profiler profiler,
//                                                 @Local Frustum frustum,
//                                                 @Local FrameGraphBuilder frameGraphBuilder)
//    //@Local(ordinal = 0) int i, @Local(ordinal = 1) int j, @Local PostEffectProcessor postEffectProcessor)
//    {
//        ((RenderEventHandler) RenderEventHandler.getInstance()).runRenderWorldPreMain(positionMatrix, projectionMatrix, this.client, frameGraphBuilder, this.framebufferSet, frustum, camera, this.bufferBuilders, profiler);
//
//        /*
//        if (postEffectProcessor != null)
//        {
//            this.width = i;
//            this.height = j;
//            this.postEffects = postEffectProcessor;
//            this.postEffects.render(frameGraphBuilder, this.width, this.height, this.framebufferSet);
//        }
//         */
//    }
//
//    @Inject(method = "render",
//            at = @At(value = "INVOKE",
//                     target = "Lnet/minecraft/client/render/WorldRenderer;renderParticles(Lnet/minecraft/client/render/FrameGraphBuilder;Lnet/minecraft/client/render/Camera;FLnet/minecraft/client/render/Fog;)V",
//                     shift = At.Shift.BEFORE))
//    private void malilib_onRenderWorldPreParticles(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline,
//                                                 Camera camera, Matrix4f positionMatrix, Matrix4f projectionMatrix, GpuBufferSlice fog, Vector4f fogColor, boolean shouldRenderSky, CallbackInfo ci,
//                                                 @Local Profiler profiler,
//                                                 @Local Frustum frustum,
//                                                 @Local FrameGraphBuilder frameGraphBuilder)
//    //@Local(ordinal = 0) int i, @Local(ordinal = 1) int j, @Local PostEffectProcessor postEffectProcessor)
//    {
//        ((RenderEventHandler) RenderEventHandler.getInstance()).runRenderWorldPreParticles(positionMatrix, projectionMatrix, this.client, frameGraphBuilder, this.framebufferSet, frustum, camera, this.bufferBuilders, profiler);
//
//        /*
//        if (postEffectProcessor != null)
//        {
//            this.width = i;
//            this.height = j;
//            this.postEffects = postEffectProcessor;
//            this.postEffects.render(frameGraphBuilder, this.width, this.height, this.framebufferSet);
//        }
//         */
//    }

    @Inject(method = "render",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/client/render/WorldRenderer;renderWeather(Lnet/minecraft/client/render/FrameGraphBuilder;Lnet/minecraft/util/math/Vec3d;FLcom/mojang/blaze3d/buffers/GpuBufferSlice;)V",
                     shift = At.Shift.BEFORE))
    private void malilib_onRenderWorldPreWeather(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline,
                                                 Camera camera, Matrix4f positionMatrix, Matrix4f projectionMatrix, GpuBufferSlice fog, Vector4f fogColor, boolean shouldRenderSky, CallbackInfo ci,
                                                 @Local Profiler profiler,
                                                 @Local Frustum frustum,
                                                 @Local FrameGraphBuilder frameGraphBuilder)
                                                 //@Local(ordinal = 0) int i, @Local(ordinal = 1) int j, @Local PostEffectProcessor postEffectProcessor)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).runRenderWorldPreWeather(positionMatrix, projectionMatrix, this.client, frameGraphBuilder, this.framebufferSet, frustum, camera, this.bufferBuilders, profiler);

        /*
        if (postEffectProcessor != null)
        {
            this.width = i;
            this.height = j;
            this.postEffects = postEffectProcessor;
            this.postEffects.render(frameGraphBuilder, this.width, this.height, this.framebufferSet);
        }
         */
    }

    @Inject(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/WorldRenderer;renderLateDebug(Lnet/minecraft/client/render/FrameGraphBuilder;Lnet/minecraft/util/math/Vec3d;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V",
                    shift = At.Shift.BEFORE))
    private void malilib_onRenderWorldLast(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline,
                                           Camera camera, Matrix4f positionMatrix, Matrix4f projectionMatrix, GpuBufferSlice fog, Vector4f fogColor, boolean shouldRenderSky, CallbackInfo ci,
                                           @Local Profiler profiler,
                                           @Local Frustum frustum,
                                           @Local FrameGraphBuilder frameGraphBuilder)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).runRenderWorldLast(positionMatrix, projectionMatrix, this.client, frameGraphBuilder, this.framebufferSet, frustum, camera, this.bufferBuilders, profiler);

        /*
        if (this.postEffects != null)
        {
            this.postEffects.render(frameGraphBuilder, this.width, this.height, this.framebufferSet);
        }
         */
    }

    /**
     * This injection should allow you to see the renderObjects from vanilla before drawing the Layer,
     * so you can then inject your own using the same pipeline; or inject new drawing elements using
     * the same BuiltChunk data as vanilla.
     */
//    @Inject(method = "renderLayer",
//            at = @At(value = "INVOKE",
//                     target = "Lcom/mojang/blaze3d/systems/RenderSystem$ShapeIndexBuffer;getIndexBuffer(I)Lcom/mojang/blaze3d/buffers/GpuBuffer;",
//                     shift = At.Shift.BEFORE))
//    private void malilib_onRenderWorldLayer(RenderLayer renderLayer, double x, double y, double z,
//                                            Matrix4f viewMatrix, Matrix4f positionMatrix, CallbackInfo ci,
//                                            @Local ArrayList<RenderPass.RenderObject> arrayList,
//                                            @Local ObjectListIterator<ChunkBuilder.BuiltChunk> objectListIterator)
//    {
//        ((RenderEventHandler) RenderEventHandler.getInstance()).runRenderWorldLayerPass(renderLayer, viewMatrix, positionMatrix, new Vec3d(x, y, z), this.client, objectListIterator, arrayList);
//    }
}
