package fi.dy.masa.malilib.render;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.class_9916;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gl.SimpleFramebufferFactory;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.Handle;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.event.FramebufferHandler;
import fi.dy.masa.malilib.interfaces.IFramebufferFactory;

/**
 * Experimental Class Only!!!
 */
@ApiStatus.Experimental
public class MaLiLibRenderer implements IFramebufferFactory, AutoCloseable
{
    private final Identifier renderStage = Identifier.of(MaLiLibReference.MOD_ID, "renderer");
    private final Identifier postProcessStage = Identifier.of(MaLiLibReference.MOD_ID, "renderer_post");
    private RenderTarget renderPhase;
    private Framebuffer framebuffer;

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
        return this.renderStage;
    }

    @Override
    public Identifier getPostProcessorStage()
    {
        return this.postProcessStage;
    }

    @Override
    public void setupRenderPhase()
    {
        this.renderPhase = new RenderTarget(this.getName(), this.beginDrawingBasic(), this.endDrawingBasic());
    }

    @Override
    public RenderPhase getRenderPhase()
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
        this.shaders.computeIfAbsent("position_color", (entry) -> new ShaderEntry(entry, ShaderProgramKeys.POSITION_COLOR, null));
        this.shaders.computeIfAbsent("position_color_tex", (entry) -> new ShaderEntry(entry, ShaderProgramKeys.POSITION_TEX_COLOR, null));
        this.shaders.computeIfAbsent("rendertype_solid", (entry) -> new ShaderEntry(entry, ShaderProgramKeys.RENDERTYPE_SOLID, null));
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
        this.posMatrix = posMatrix;
        this.projMatrix = projMatrix;
        this.vanillaTransparency.setPostEffects(postEffectProcessor);
    }

    @Override
    public void onFramebufferTranslucentFactorySetup(FrameGraphBuilder frameGraphBuilder, SimpleFramebufferFactory fbFactory,
                                                     MinecraftClient mc)
    {
        //FramebufferHandler.getInstance().setFramebufferHandle(this, frameGraphBuilder.createStageNode(this.getName()), this.getStage(), false);

        if (this.vanillaTransparency.getPostEffects() != null)
        {
            FramebufferHandler.getInstance().setFramebufferHandle(this, frameGraphBuilder.method_61912(this.getPostProcessorStage().toString(), fbFactory), this.getPostProcessorStage(), true);
        }
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
        this.render(framebufferSet, frameGraphBuilder, camera);
        //this.runStage(this.renderStageNode);
        //this.runPostEffects(frameGraphBuilder, mc.getFramebuffer().textureWidth, mc.getFramebuffer().textureHeight, framebufferSet);
    }

    public void render(DefaultFramebufferSet framebufferSet, FrameGraphBuilder frameGraphBuilder, Camera camera)
    {
        this.profiler.push(this::getName);
        BlockState blockState = Blocks.COMMAND_BLOCK.getDefaultState();
        BakedModel bakedModel = MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(blockState);
        //boolean hasPostEffects = this.vanillaTransparency.getPostEffects() != null;
        class_9916 stage = frameGraphBuilder.createStageNode(this.getName());

        Handle<Framebuffer> handleMain = FramebufferHandler.getInstance().getFramebufferHandle(this, this.getStage(), false);

        /*
        Handle<Framebuffer> handlePost;
        if (hasPostEffects)
        {
            FramebufferHandler.getInstance().setFramebufferHandle(this,
                    stage.method_61933(FramebufferHandler.getInstance().getFramebufferHandle(this,
                            this.getPostProcessorStage(), true)),
                    this.getPostProcessorStage(), true);
            handlePost = FramebufferHandler.getInstance().getFramebufferHandle(this, this.getPostProcessorStage(), true);
        }
         */

        stage.method_61929(() ->
        {
            this.getRenderPhase().startDrawing();
            RenderUtils.renderModel(bakedModel, blockState);
            this.getRenderPhase().endDrawing();
        });
        this.profiler.pop();
    }

    @Override
    public void onRenderFinished()
    {
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
        this.endShaders();
    }

    @Override
    public void close()
    {
        this.clear();
    }
}
