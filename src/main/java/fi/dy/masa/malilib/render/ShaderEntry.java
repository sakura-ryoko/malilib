package fi.dy.masa.malilib.render;

import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.event.FramebufferHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.ShaderLoader;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public class ShaderEntry
{
    private final String name;
    private final ShaderProgramKey key;
    private ShaderProgram program;
    private final Identifier postEffectId;
    private PostEffectProcessor postEffects;

    public ShaderEntry(String name, @Nullable ShaderProgramKey key, @Nullable Identifier postEffectId)
    {
        this.name = name;
        this.key = key;
        this.program = null;
        this.postEffectId = postEffectId;
        this.postEffects = null;
    }

    public String getName()
    {
        return this.name;
    }

    public ShaderProgramKey getKey()
    {
        return this.key;
    }

    private void setProgram(ShaderProgram program)
    {
        this.program = program;
    }

    @Nullable
    public ShaderProgram getProgram()
    {
        return this.program;
    }

    public boolean startProgram(ShaderLoader loader)
    {
        this.setProgram(loader.getOrCreateProgram(this.getKey()));
        return this.getProgram() != null;
    }

    public boolean runProgram()
    {
        if (this.getProgram() != null)
        {
            RenderSystem.setShader(this.getProgram());
            return true;
        }

        return false;
    }

    private void endProgram()
    {
        if (this.program != null)
        {
            this.program.close();
            this.program = null;
        }
    }

    @Nullable
    public Identifier getPostEffectId()
    {
        return this.postEffectId;
    }

    public void setPostEffects(PostEffectProcessor postEffects)
    {
        this.postEffects = postEffects;
    }

    @Nullable
    public PostEffectProcessor getPostEffects()
    {
        return this.postEffects;
    }

    public boolean loadPostEffects(ShaderLoader loader)
    {
        if (MinecraftClient.isFabulousGraphicsOrBetter() == false || this.getPostEffectId() == null)
        {
            return false;
        }
        else
        {
            this.setPostEffects(loader.loadPostEffect(this.getPostEffectId(), FramebufferHandler.getInstance().getStages()));
            return this.getPostEffects() != null;
        }
    }

    public boolean runPostEffects(FrameGraphBuilder frameGraphBuilder, int width, int height, DefaultFramebufferSet framebufferSet)
    {
        if (this.getPostEffects() != null)
        {
            this.getPostEffects().method_62234(frameGraphBuilder, width, height, framebufferSet);
            return true;
        }

        return false;
    }

    private void endPostEffects()
    {
        this.postEffects = null;
    }

    public boolean load(ShaderLoader loader)
    {
        boolean hasProgram;
        boolean hasPostEffects;

        hasProgram = this.startProgram(loader);
        hasPostEffects = this.loadPostEffects(loader);

        return hasProgram || hasPostEffects;
    }

    public void end()
    {
        this.endProgram();
        this.endPostEffects();
    }
}
