package fi.dy.masa.malilib.interfaces;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.gl.SimpleFramebufferFactory;
import net.minecraft.client.render.*;
import net.minecraft.client.util.Handle;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@ApiStatus.Experimental
public interface IFramebufferFactory
{
    /**
     * Renderer name
     * @return (Name)
     */
    String getName();

    /**
     * Framebuffer Stage ID
     * @return (Stage ID)
     */
    Identifier getStage();

    /**
     * Framebuffer Translucent Stage ID
     * @return (Stage ID)
     */
    Identifier getPostProcessorStage();

    /**
     * Callback for configuring the RenderPhase
     */
    void setupRenderPhase();

    /**
     * Return the RenderPhase Target
     * @return (The Target)
     */
    RenderPhase getRenderPhase();

    /**
     * The Framebuffer Object
     * @return (Framebuffer)
     */
    Framebuffer getFramebuffer();

    /**
     * Render Setup Events
     */
    void onReload(MinecraftClient mc);
    void onClose();
    void onResized(int w, int h);
    void onFramebufferBasicSetup(Matrix4f posMatrix, Matrix4f projMatrix, MinecraftClient mc, @Nullable PostEffectProcessor postEffectProcessor, DefaultFramebufferSet framebufferSet, FrameGraphBuilder frameGraphBuilder);
    void onFramebufferTranslucentFactorySetup(FrameGraphBuilder frameGraphBuilder, SimpleFramebufferFactory fbFactory, MinecraftClient mc);
    void onRenderMainCaptureLocals(MinecraftClient mc, Camera camera, Fog fog, RenderTickCounter counter, Profiler profiler);
    void onRenderNode(FrameGraphBuilder frameGraphBuilder, Matrix4f posMatrix, Matrix4f projMatrix, MinecraftClient mc, Camera camera, DefaultFramebufferSet framebufferSet);
    void onRenderFinished();

    /**
     * Returns a supplier for the profiler section name that should be used for this renderer
     */
    default Supplier<String> getProfilerSectionSupplier()
    {
        return () -> this.getClass().getName();
    }

    /**
     * Default RenderPhase beginDrawingBasic()
     * @return (Runnable)
     */
    default Runnable beginDrawingBasic()
    {
        return () ->
        {
            MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
        };
    }

    /**
     * Default RenderPhase endDrawingBasic()
     * @return (Runnable)
     */
    default Runnable endDrawingBasic()
    {
        return () -> { };
    }

    /**
     * Default RenderPhase beginDrawingWithFb()
     * @return (Runnable)
     */
    default Runnable beginDrawingWithFb()
    {
        return () ->
        {
            if (MinecraftClient.isFabulousGraphicsOrBetter())
            {
                this.getFramebuffer().beginWrite(false);
            }
            else
            {
                MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
            }
        };
    }

    /**
     * Default RenderPhase endDrawingWithFb()
     * @return (Runnable)
     */
    default Runnable endDrawingWithFb()
    {
        return () ->
        {
            if (MinecraftClient.isFabulousGraphicsOrBetter())
            {
                MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
            }
        };
    }

    /**
     * Create a Simple Framebuffer Object based on the Window's size
     * @param mc (Client Object)
     * @param useDepth (Depth Enabled)
     * @return (Framebuffer Object)
     */
    default Framebuffer createSimpleFramebuffer(MinecraftClient mc, boolean useDepth)
    {
        return new SimpleFramebuffer(mc.getWindow().getFramebufferWidth(), mc.getWindow().getFramebufferHeight(), useDepth);
    }

    /**
     * Create a Translucent Framebuffer Object
     * @param frameGraphBuilder (Required frameGraphBuilder Object from WorldRenderer)
     * @param fbFactory (The Translucent Factory object from WorldRenderer.render())
     * @return (The Handle Object built)
     */
    default Handle<Framebuffer> createTranslucentFactory(FrameGraphBuilder frameGraphBuilder, SimpleFramebufferFactory fbFactory)
    {
        return frameGraphBuilder.method_61912(this.getName(), fbFactory);
    }

    /**
     * Default functionality (Similar to the ChunkBuilder, or Entity Outlines)
     * @param frameGraphBuilder (Required frameGraphBuilder Object from WorldRenderer)
     * @param name (Name of the Frame Graph Object)
     * @param framebuffer (The returned Framebuffer Object)
     * @return (The Handle Object built)
     */
    default Handle<Framebuffer> createObjectNode(FrameGraphBuilder frameGraphBuilder, String name, Framebuffer framebuffer)
    {
        return frameGraphBuilder.createObjectNode(name, framebuffer);
    }
}
