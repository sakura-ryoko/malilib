package fi.dy.masa.malilib.render;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.event.FramebufferHandler;
import fi.dy.masa.malilib.interfaces.IFramebufferFactory;
import fi.dy.masa.malilib.render.shader.ShaderEntry;
import fi.dy.masa.malilib.render.shader.ShaderPrograms;
import net.minecraft.class_9916;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.SimpleFramebufferFactory;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.joml.Matrix4f;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class MaLiLibRenderer implements IFramebufferFactory, AutoCloseable
{
    private RenderTarget renderPhase;
    private Framebuffer framebuffer;
    private class_9916 renderStageNode;

    private Matrix4f posMatrix = new Matrix4f();
    private Matrix4f projMatrix = new Matrix4f();
    private Camera camera;
    private Fog fog = Fog.DUMMY;
    private RenderTickCounter tickCounter;
    private Profiler profiler;
    private final ShaderEntry vanillaTransparency = new ShaderEntry("vanilla_transparency", null, Identifier.ofVanilla("transparency"));
    private final Map<String, ShaderEntry> shaders = new HashMap<>();

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
        this.clear();
        this.buildShaders();

        if (this.shaders.isEmpty() == false)
        {
            this.loadShaders(mc);
        }
    }

    private void buildShaders()
    {
        this.shaders.computeIfAbsent("position_color", (entry) -> new ShaderEntry(entry, ShaderPrograms.POSITION_COLOR, null));
        this.shaders.computeIfAbsent("position_color_tex", (entry) -> new ShaderEntry(entry, ShaderPrograms.POSITION_COLOR_TEX, null));
        this.shaders.computeIfAbsent("rendertype_solid", (entry) -> new ShaderEntry(entry, ShaderPrograms.RENDERTYPE_SOLID, null));
    }

    private void loadShaders(MinecraftClient mc)
    {
        for (ShaderEntry entry : this.shaders.values())
        {
            try
            {
                entry.load(mc.method_62887());
            }
            catch (Exception e)
            {
                MaLiLib.logger.error("MaLiLibRenderer: Error loading ShaderEntry \"{}\": [{}]", entry.getName(), e.getMessage());
            }
        }
    }

    private boolean startProgram(String name, MinecraftClient mc)
    {
        if (this.shaders.containsKey(name))
        {
            return this.shaders.get(name).startProgram(mc.method_62887());
        }

        return false;
    }

    private boolean runProgram(String name)
    {
        if (this.shaders.containsKey(name))
        {
            return this.shaders.get(name).runProgram();
        }

        return false;
    }

    private void runPostEffects(FrameGraphBuilder frameGraphBuilder, int width, int height, DefaultFramebufferSet framebufferSet)
    {
        for (ShaderEntry entry : this.shaders.values())
        {
            entry.runPostEffects(frameGraphBuilder, width, height, framebufferSet);
        }
    }

    private void endShaders()
    {
        for (ShaderEntry entry : this.shaders.values())
        {
            entry.end();
        }

        this.vanillaTransparency.end();
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
        this.vanillaTransparency.setPostEffects(postEffectProcessor);
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
        this.runPostEffects(frameGraphBuilder, mc.getFramebuffer().textureWidth, mc.getFramebuffer().textureHeight, framebufferSet);
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
        MaLiLib.logger.error("MaLiLibRenderer() --> draw()");
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
        this.endShaders();
    }

    @Override
    public void close()
    {
        this.clear();
    }
}
