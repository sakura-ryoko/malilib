package fi.dy.masa.malilib.render;

import org.joml.Matrix4f;

import net.minecraft.class_9916;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.SimpleFramebufferFactory;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.event.FramebufferHandler;
import fi.dy.masa.malilib.interfaces.IFramebufferFactory;

public class MaLiLibRenderer implements IFramebufferFactory, AutoCloseable
{
    //private final Identifier shaderPostProcessor = Identifier.of(MaLiLibReference.MOD_ID, "shaders/transparency.json");
    private final Identifier shaderPostProcessor = null;
    private PostEffectProcessor transparencyPostProcessor;
    private RenderTarget renderPhase;
    private Framebuffer framebuffer;
    private class_9916 renderStageNode;

    private Matrix4f posMatrix = new Matrix4f();
    private Matrix4f projMatrix = new Matrix4f();
    private boolean hasTransparency;
    private Camera camera;
    private Fog fog = Fog.DUMMY;
    private RenderTickCounter tickCounter;
    private Profiler profiler;

    @Override
    public String getName()
    {
        return "malilib_renderer";
    }

    @Override
    public Identifier getStage()
    {
        return Identifier.of(MaLiLibReference.MOD_ID, "renderer");
    }

    @Override
    public void setupRenderPhase()
    {
        this.renderPhase = this.createTarget();
    }

    @Override
    public RenderTarget getRenderPhase()
    {
        return this.renderPhase;
    }

    @Override
    public Framebuffer getFramebuffer()
    {
        return this.framebuffer;
    }

    @Override
    public void onReload(MinecraftClient mc)
    {
        if (MinecraftClient.isFabulousGraphicsOrBetter())
        {
            this.loadTransparencyPostProcessor(mc);
        }
    }

    private void loadTransparencyPostProcessor(MinecraftClient mc)
    {
        if (this.shaderPostProcessor == null)
        {
            return;
        }

        // For Transparent layer rendering
        try
        {
            this.transparencyPostProcessor = PostEffectProcessor.parseEffect(mc.getResourceManager(), mc.getTextureManager(), this.shaderPostProcessor, FramebufferHandler.getInstance().getStages());
        }
        catch (Exception e)
        {
            MaLiLib.logger.error("MaLiLibRenderer(): transparency post processor failure; Error: [{}]", e.getMessage());
        }
    }

    @Override
    public void onClose()
    {
        this.close();
    }

    @Override
    public void onResized(int w, int h)
    {
        if (this.framebuffer != null)
        {
            this.framebuffer.resize(w, h);
        }
    }

    @Override
    public void onFramebufferBasicSetup(Matrix4f posMatrix, Matrix4f projMatrix, MinecraftClient mc, boolean hasTransparency, DefaultFramebufferSet framebufferSet, FrameGraphBuilder frameGraphBuilder)
    {
        this.framebuffer = this.createSimpleFramebuffer(mc, true);
        this.renderStageNode = this.createStageNode(frameGraphBuilder, this.getName());
        this.posMatrix = posMatrix;
        this.projMatrix = projMatrix;
        this.hasTransparency = hasTransparency;
    }

    @Override
    public void onFramebufferTranslucentFactorySetup(FrameGraphBuilder frameGraphBuilder, SimpleFramebufferFactory fbFactory, MinecraftClient mc)
    {
        FramebufferHandler.getInstance().setFramebufferHandle(this, frameGraphBuilder.method_61912(this.getName(), fbFactory));
    }

    @Override
    public void onRenderMainCaptureLocals(MinecraftClient mc, Camera camera, Fog fog, RenderTickCounter counter, Profiler profiler)
    {
        this.camera = camera;
        this.fog = fog;
        this.tickCounter = counter;
        this.profiler = profiler;
    }

    @Override
    public void onRenderNode(FrameGraphBuilder frameGraphBuilder, Matrix4f posMatrix, Matrix4f projMatrix, MinecraftClient mc, Camera camera, DefaultFramebufferSet framebufferSet)
    {
        this.profiler.push(this::getName);
        this.runStage(this.renderStageNode);

        if (this.transparencyPostProcessor != null)
        {
            this.transparencyPostProcessor.method_62234(frameGraphBuilder, this.tickCounter, mc.getFramebuffer().textureWidth, mc.getFramebuffer().textureHeight, framebufferSet);
        }
    }

    @Override
    public void preDraw()
    {
        // NO-OP
    }

    @Override
    public void draw()
    {
        // Items for drawing go here
    }

    @Override
    public void postDraw()
    {
        // NO-OP
    }

    @Override
    public void onRenderFinished()
    {
        this.profiler.pop();
        this.posMatrix = new Matrix4f();
        this.projMatrix = new Matrix4f();
        this.hasTransparency = false;
        this.camera = new Camera();
        this.fog = Fog.DUMMY;
        this.tickCounter = null;
        this.profiler = null;
        this.renderStageNode = null;
    }

    @Override
    public void close()
    {
        if (this.transparencyPostProcessor != null)
        {
            this.transparencyPostProcessor.close();
        }

        this.onRenderFinished();
    }
}
