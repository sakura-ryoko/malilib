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

import fi.dy.masa.malilib.render.RenderTarget;

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
     * Callback for configuring the RenderPhase
     */
    void setupRenderPhase();

    /**
     * Return the RenderPhase Target
     * @return (The Target)
     */
    RenderTarget getRenderPhase();

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
     * Creation for custom RenderPhase objects
     * @return (The RenderPhase Object alias)
     */
    default RenderTarget createTarget()
    {
        return this.createTargetBasic(null, null);
    }

    /**
     * Creation for custom RenderPhase objects
     * @param startDrawing (Runnable for Drawing phase start)
     * @param endDrawing (Runnable for Drawing phase end)
     * @return (The RenderPhase Object alias)
     */
    default RenderTarget createTargetBasic(@Nullable Runnable startDrawing, @Nullable Runnable endDrawing)
    {
        return new RenderTarget(this.getName(), startDrawing != null ? startDrawing : this.beginDrawingBasic(), endDrawing != null ? endDrawing : this.endDrawingBasic());
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
     * Creation for custom RenderPhase objects
     * @param startDrawing (Runnable for Drawing phase start)
     * @param endDrawing (Runnable for Drawing phase end)
     * @return (The RenderPhase Object alias)
     */
    default RenderTarget createTargetWithFb(@Nullable Runnable startDrawing, @Nullable Runnable endDrawing)
    {
        return new RenderTarget(this.getName(), startDrawing != null ? startDrawing : this.beginDrawingWithFb(), endDrawing != null ? endDrawing : this.endDrawingWithFb());
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
