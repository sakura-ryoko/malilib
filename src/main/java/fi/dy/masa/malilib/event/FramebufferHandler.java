package fi.dy.masa.malilib.event;

import java.util.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.gl.PostEffectProcessor;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebufferFactory;
import net.minecraft.client.render.*;
import net.minecraft.client.util.Handle;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.interfaces.IFramebufferFactory;
import fi.dy.masa.malilib.interfaces.IFramebufferManager;

/**
 * A New Frame buffer Compliant Rendering System.
 * This is made as an effort for a replacement for the RenderHandler process;
 * and is designed to be able to be run at a lower level of the WorldRenderer.
 */
public class FramebufferHandler implements IFramebufferManager
{
    private static final FramebufferHandler INSTANCE = new FramebufferHandler();
    public static IFramebufferManager getInstance() { return INSTANCE; }

    private final List<IFramebufferFactory> handlers = new ArrayList<>();
    private final Set<Identifier> STAGES = new HashSet<>();
    private final Map<Identifier, Handle<Framebuffer>> HANDLES = new HashMap<>();

    @Override
    public void registerFramebufferHandler(IFramebufferFactory handler)
    {
        if (this.handlers.contains(handler) == false)
        {
            this.handlers.add(handler);

            if (this.STAGES.contains(handler.getStage()) == false)
            {
                this.STAGES.add(handler.getStage());
                handler.setupRenderPhase();
            }

            this.HANDLES.putIfAbsent(handler.getStage(), null);
        }
    }

    @Override
    public void setFramebufferHandle(IFramebufferFactory handler, @Nonnull Handle<Framebuffer> framebufferHandle)
    {
        if (this.handlers.contains(handler))
        {
            this.onFramebufferSet(handler.getStage(), framebufferHandle);
        }
    }

    @Override
    public @Nullable Handle<Framebuffer> getFramebufferHandle(IFramebufferFactory handler)
    {
        if (this.handlers.contains(handler))
        {
            return this.HANDLES.get(handler.getStage());
        }

        return null;
    }

    @Override
    public Set<Identifier> getStages()
    {
        return this.STAGES;
    }

    @ApiStatus.Internal
    public void onFramebufferSet(Identifier stage, @Nullable Handle<Framebuffer> frameBufferHandle)
    {
        if (this.STAGES.contains(stage) == false)
        {
            this.STAGES.add(stage);
        }
        if (this.HANDLES.containsKey(stage) == false)
        {
            this.HANDLES.put(stage, frameBufferHandle);
        }
        for (IFramebufferFactory handler : this.handlers)
        {
            if (handler.getStage().equals(stage))
            {
                this.HANDLES.putIfAbsent(stage, frameBufferHandle);
            }
        }
    }

    @Nullable
    @ApiStatus.Internal
    public Handle<Framebuffer> onFramebufferGet(Identifier stage)
    {
        if (this.STAGES.contains(stage) && this.HANDLES.containsKey(stage))
        {
            return this.HANDLES.get(stage);
        }

        return null;
    }

    @ApiStatus.Internal
    public void onFramebufferClear()
    {
        this.HANDLES.replaceAll((identifier, framebufferHandle) -> Handle.empty());
    }

    @ApiStatus.Internal
    public Set<Identifier> onStagesRemap(Set<Identifier> stages)
    {
        Set<Identifier> newStages = new HashSet<>(stages);

        try
        {
            newStages.addAll(this.STAGES);
        }
        catch (Exception e)
        {
            MaLiLib.logger.error("FramebufferHandler(): Exception remapping Framebuffer Stages.  Reason: [{}]", e.getMessage());
        }

        return newStages;
    }

    @ApiStatus.Internal
    public void onReload(MinecraftClient mc)
    {
        for (IFramebufferFactory handler : this.handlers)
        {
            handler.onReload(mc);
        }
    }

    @ApiStatus.Internal
    public void onClose()
    {
        for (IFramebufferFactory handler : this.handlers)
        {
            handler.onClose();
        }
    }

    @ApiStatus.Internal
    public void onResized(int w, int h)
    {
        for (IFramebufferFactory handler : this.handlers)
        {
            handler.onResized(w, h);
        }
    }

    @ApiStatus.Internal
    public void onFramebufferSetup(Matrix4f posMatrix, Matrix4f projMatrix, MinecraftClient mc, @Nullable PostEffectProcessor postEffectProcessor, DefaultFramebufferSet framebufferSet, FrameGraphBuilder frameGraphBuilder)
    {
        for (IFramebufferFactory handler : this.handlers)
        {
            handler.onFramebufferBasicSetup(posMatrix, projMatrix, mc, postEffectProcessor, framebufferSet, frameGraphBuilder);
        }
    }

    @ApiStatus.Internal
    public void onFramebufferTranslucentFactorySetup(FrameGraphBuilder frameGraphBuilder, SimpleFramebufferFactory fbFactory, MinecraftClient mc)
    {
        for (IFramebufferFactory handler : this.handlers)
        {
            handler.onFramebufferTranslucentFactorySetup(frameGraphBuilder, fbFactory, mc);
        }
    }

    @ApiStatus.Internal
    public void onRenderMainCaptureLocals(MinecraftClient mc, Camera camera, Fog fog, RenderTickCounter counter, Profiler profiler)
    {
        for (IFramebufferFactory handler : this.handlers)
        {
            handler.onRenderMainCaptureLocals(mc, camera, fog, counter, profiler);
        }
    }

    @ApiStatus.Internal
    public void onRenderNode(FrameGraphBuilder frameGraphBuilder, Matrix4f posMatrix, Matrix4f projMatrix, MinecraftClient mc, Camera camera, DefaultFramebufferSet framebufferSet)
    {
        for (IFramebufferFactory handler : this.handlers)
        {
            handler.onRenderNode(frameGraphBuilder, posMatrix, projMatrix, mc, camera, framebufferSet);
        }
    }

    @ApiStatus.Internal
    public void onRenderFinished()
    {
        for (IFramebufferFactory handler : this.handlers)
        {
            handler.onRenderFinished();
        }
    }
}
