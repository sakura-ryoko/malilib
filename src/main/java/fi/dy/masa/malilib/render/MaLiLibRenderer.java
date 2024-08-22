package fi.dy.masa.malilib.render;

import javax.annotation.Nullable;
import org.joml.Matrix4f;

import net.minecraft.class_10149;
import net.minecraft.class_10156;
import net.minecraft.class_9916;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.SimpleFramebufferFactory;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.event.FramebufferHandler;
import fi.dy.masa.malilib.interfaces.IFramebufferFactory;

public class MaLiLibRenderer implements IFramebufferFactory, AutoCloseable
{
    //private final Identifier shaderPostProcessor = Identifier.of(MaLiLibReference.MOD_ID, "shaders/transparency.json");
    private final Identifier shaderPostProcessor = null;
    private RenderTarget renderPhase;
    private Framebuffer framebuffer;
    private class_9916 renderStageNode;

    private Matrix4f posMatrix = new Matrix4f();
    private Matrix4f projMatrix = new Matrix4f();
    private Camera camera;
    private Fog fog = Fog.DUMMY;
    private RenderTickCounter tickCounter;
    private Profiler profiler;
    @Nullable
    private PostEffectProcessor transparencyPostProcessor;
    @Nullable
    private class_10156 shader = new class_10156(Identifier.ofVanilla("core/terrain"), VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, class_10149.field_53930);
    @Nullable
    private ShaderProgram shaderProgram;
    @Nullable
    private PostEffectProcessor shaderPostEffects;

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
        /*
        if (MinecraftClient.isFabulousGraphicsOrBetter())
        {
            this.loadTransparencyPostProcessor(mc);
        }
         */

        this.clear();

        if (this.shader != null)
        {
            this.loadShader();
        }
    }

    private void loadShader()
    {
        // Load shader
        /*
        try
        {
            ShaderProgram shaderProgram = RenderSystem.setShader(this.shader);
            if (shaderProgram != null)
            {
                this.shaderProgram = shaderProgram;
            }
            //this.shaderPostEffects;
        }
        catch (Exception e)
        {
            // Ignored
        }
         */
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
    public void onFramebufferBasicSetup(Matrix4f posMatrix, Matrix4f projMatrix, MinecraftClient mc,
                                        @Nullable PostEffectProcessor postEffectProcessor,
                                        DefaultFramebufferSet framebufferSet,
                                        FrameGraphBuilder frameGraphBuilder)
    {
        this.framebuffer = this.createSimpleFramebuffer(mc, true);
        this.renderStageNode = this.createStageNode(frameGraphBuilder, this.getName());
        this.posMatrix = posMatrix;
        this.projMatrix = projMatrix;
        this.transparencyPostProcessor = postEffectProcessor;
    }

    @Override
    public void onFramebufferTranslucentFactorySetup(FrameGraphBuilder frameGraphBuilder, SimpleFramebufferFactory fbFactory,
                                                     MinecraftClient mc)
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
    public void onRenderNode(FrameGraphBuilder frameGraphBuilder, Matrix4f posMatrix, Matrix4f projMatrix, MinecraftClient mc,
                             Camera camera, DefaultFramebufferSet framebufferSet)
    {
        this.profiler.push(this::getName);
        this.runStage(this.renderStageNode);

        if (this.transparencyPostProcessor != null)
        {
            this.transparencyPostProcessor.method_62234(frameGraphBuilder, mc.getFramebuffer().textureWidth, mc.getFramebuffer().textureHeight, framebufferSet);
        }
        if (this.shaderPostEffects != null)
        {
            this.shaderPostEffects.method_62234(frameGraphBuilder, mc.getFramebuffer().textureWidth, mc.getFramebuffer().textureHeight, framebufferSet);
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
        this.clear();
    }

    public void clear()
    {
        this.posMatrix = new Matrix4f();
        this.projMatrix = new Matrix4f();
        this.camera = new Camera();
        this.fog = Fog.DUMMY;
        this.tickCounter = null;
        this.profiler = null;
        this.renderStageNode = null;
        this.transparencyPostProcessor = null;
        this.shaderPostEffects = null;
        if (this.shaderProgram != null)
        {
            this.shaderProgram.close();
            this.shaderProgram = null;
        }
    }

    @Override
    public void close()
    {
        this.clear();
    }
}
