package fi.dy.masa.malilib.mixin.render;

import java.util.Map;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.PolygonMode;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gl.ShaderPipelines;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.render.MaLiLibPipelines;

@Mixin(ShaderPipelines.class)
public abstract class MixinShaderPipelines
{
    @Shadow @Final public static Map<Identifier, RenderPipeline> PIPELINES;
    @Shadow @Final public static RenderPipeline.Snippet MATRICES_SNIPPET;                       // MATRICES
    @Shadow @Final public static RenderPipeline.Snippet FOG_NO_COLOR_SNIPPET;                   // FOG_NO_COLOR
    @Shadow @Final public static RenderPipeline.Snippet FOG_SNIPPET;                            // FOG
    @Shadow @Final public static RenderPipeline.Snippet MATRICES_COLOR_SNIPPET;                 // MATRICES_COLOR
    @Shadow @Final public static RenderPipeline.Snippet MATRICES_COLOR_FOG_SNIPPET;             // MATRICES_COLOR_FOG
    @Shadow @Final public static RenderPipeline.Snippet MATRICES_COLOR_FOG_OFFSET_SNIPPET;      // MATRICES_COLOR_FOG_OFFSET
    @Shadow @Final public static RenderPipeline.Snippet MATRICES_COLOR_FOG_LIGHT_DIR_SNIPPET;   // MATRICES_COLOR_FOG_LIGHT_DIR
    @Shadow @Final public static RenderPipeline.Snippet TERRAIN_SNIPPET;                        // TERRAIN
    @Shadow @Final public static RenderPipeline.Snippet ENTITY_SNIPPET;                         // ENTITY
    @Shadow @Final public static RenderPipeline.Snippet RENDERTYPE_BEACON_BEAM_SNIPPET;         // RENDERTYPE_BEACON_BEAM
    @Shadow @Final public static RenderPipeline.Snippet TEXT_SNIPPET;                           // TEXT
    @Shadow @Final public static RenderPipeline.Snippet RENDERTYPE_END_PORTAL_SNIPPET;          // RENDERTYPE_END_PORTAL
    @Shadow @Final public static RenderPipeline.Snippet RENDERTYPE_CLOUDS_SNIPPET;              // RENDERTYPE_CLOUDS
    @Shadow @Final public static RenderPipeline.Snippet RENDERTYPE_LINES_SNIPPET;               // RENDERTYPE_LINES
    @Shadow @Final public static RenderPipeline.Snippet POSITION_COLOR_SNIPPET;                 // DEBUG_FILLED
    @Shadow @Final public static RenderPipeline.Snippet PARTICLE_SNIPPET;                       // PARTICLE_TEX
    @Shadow @Final public static RenderPipeline.Snippet WEATHER_SNIPPET;                        // WEATHER
    @Shadow @Final public static RenderPipeline.Snippet GUI_SNIPPET;                            // GUI
    @Shadow @Final public static RenderPipeline.Snippet POSITION_TEX_COLOR_SNIPPET;             // GUI_TEXTURED
    @Shadow @Final public static RenderPipeline.Snippet RENDERTYPE_OUTLINE_SNIPPET;             // RENDERTYPE_OUTLINE
    @Shadow @Final public static RenderPipeline.Snippet POST_EFFECT_PROCESSOR_SNIPPET;          // POST_PROCESSOR

    @Unique
    private static final BlendFunction MASA_BLEND = new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
    // PANORAMA

    @Shadow
    public static RenderPipeline register(RenderPipeline renderPipeline)
    {
        PIPELINES.put(renderPipeline.getLocation(), renderPipeline);
        return renderPipeline;
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void  malilib_onRegisterPipelines(CallbackInfo ci)
    {
        // STAGES
        MaLiLibPipelines.POSITION_TRANSLUCENT_STAGE =
                RenderPipeline.builder(MATRICES_COLOR_FOG_SNIPPET)
                        .withVertexShader("core/position")
                        .withFragmentShader("core/position")
                        .withBlend(BlendFunction.TRANSLUCENT)
                        .withVertexFormat(VertexFormats.POSITION, VertexFormat.DrawMode.QUADS)
                        .buildSnippet();

        MaLiLibPipelines.POSITION_MASA_STAGE =
                RenderPipeline.builder(MATRICES_COLOR_FOG_SNIPPET)
                        .withVertexShader("core/position")
                        .withFragmentShader("core/position")
                        .withBlend(MASA_BLEND)
                        .withVertexFormat(VertexFormats.POSITION, VertexFormat.DrawMode.QUADS)
                        .buildSnippet();

        MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_STAGE =
                RenderPipeline.builder(MATRICES_COLOR_SNIPPET)
                        .withVertexShader("core/position_color")
                        .withFragmentShader("core/position_color")
                        .withBlend(BlendFunction.TRANSLUCENT)
                        .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.QUADS)
                        .buildSnippet();

        MaLiLibPipelines.POSITION_COLOR_MASA_STAGE =
                RenderPipeline.builder(MATRICES_COLOR_SNIPPET)
                        .withVertexShader("core/position_color")
                        .withFragmentShader("core/position_color")
                        .withBlend(MASA_BLEND)
                        .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.QUADS)
                        .buildSnippet();

        MaLiLibPipelines.POSITION_TEX_TRANSLUCENT_STAGE =
                RenderPipeline.builder(MATRICES_COLOR_SNIPPET)
                        .withVertexShader("core/position_tex")
                        .withFragmentShader("core/position_tex")
                        .withSampler("Sampler0")
                        .withSampler("Sampler10")
                        .withBlend(BlendFunction.TRANSLUCENT)
                        .withVertexFormat(VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS)
                        .buildSnippet();

        MaLiLibPipelines.POSITION_TEX_MASA_STAGE =
                RenderPipeline.builder(MATRICES_COLOR_SNIPPET)
                        .withVertexShader("core/position_tex")
                        .withFragmentShader("core/position_tex")
                        .withSampler("Sampler0")
                        .withSampler("Sampler10")
                        .withBlend(MASA_BLEND)
                        .withVertexFormat(VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS)
                        .buildSnippet();

        MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_STAGE =
                RenderPipeline.builder(MATRICES_COLOR_SNIPPET)
                        .withVertexShader("core/position_tex_color")
                        .withFragmentShader("core/position_tex_color")
                        .withSampler("Sampler0")
                        .withSampler("Sampler10")
                        .withBlend(BlendFunction.TRANSLUCENT)
                        .withVertexFormat(VertexFormats.POSITION_TEXTURE_COLOR, VertexFormat.DrawMode.QUADS)
                        .buildSnippet();

        MaLiLibPipelines.POSITION_TEX_COLOR_MASA_STAGE =
                RenderPipeline.builder(MATRICES_COLOR_SNIPPET)
                        .withVertexShader("core/position_tex_color")
                        .withFragmentShader("core/position_tex_color")
                        .withSampler("Sampler0")
                        .withSampler("Sampler10")
                        .withBlend(MASA_BLEND)
                        .withVertexFormat(VertexFormats.POSITION_TEXTURE_COLOR, VertexFormat.DrawMode.QUADS)
                        .buildSnippet();

        MaLiLibPipelines.LINES_STAGE =
                RenderPipeline.builder(MATRICES_COLOR_FOG_SNIPPET)
                        .withVertexShader("core/rendertype_lines")
                        .withFragmentShader("core/rendertype_lines")
                        .withUniform("LineWidth", UniformType.FLOAT)
                        .withUniform("ScreenSize", UniformType.VEC2)
//                        .withBlend(BlendFunction.TRANSLUCENT)
                        .withVertexFormat(VertexFormats.POSITION_COLOR_NORMAL, VertexFormat.DrawMode.LINES)
                        .buildSnippet();

        MaLiLibPipelines.LINES_MASA_STAGE =
                RenderPipeline.builder(MATRICES_COLOR_FOG_SNIPPET)
                        .withVertexShader("core/rendertype_lines")
                        .withFragmentShader("core/rendertype_lines")
                        .withUniform("LineWidth", UniformType.FLOAT)
                        .withUniform("ScreenSize", UniformType.VEC2)
                        .withBlend(MASA_BLEND)
                        .withVertexFormat(VertexFormats.POSITION_COLOR_NORMAL, VertexFormat.DrawMode.LINES)
                        .buildSnippet();

        MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_STAGE =
                RenderPipeline.builder(MATRICES_COLOR_SNIPPET)
                        .withVertexShader("core/position_color")
                        .withFragmentShader("core/position_color")
                        .withUniform("LineWidth", UniformType.FLOAT)
                        .withUniform("ScreenSize", UniformType.VEC2)
                        .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.DEBUG_LINE_STRIP)
                        .withBlend(BlendFunction.TRANSLUCENT)
                        .buildSnippet();

        MaLiLibPipelines.DEBUG_LINES_MASA_STAGE =
                RenderPipeline.builder(MATRICES_COLOR_SNIPPET)
                        .withVertexShader("core/position_color")
                        .withFragmentShader("core/position_color")
                        .withUniform("LineWidth", UniformType.FLOAT)
                        .withUniform("ScreenSize", UniformType.VEC2)
                        .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.DEBUG_LINE_STRIP)
                        .withBlend(MASA_BLEND)
                        .buildSnippet();

        MaLiLibPipelines.TERRAIN_TRANSLUCENT_STAGE =
                RenderPipeline.builder(MATRICES_COLOR_FOG_OFFSET_SNIPPET)
                        .withVertexShader("core/terrain")
                        .withFragmentShader("core/terrain")
                        .withSampler("Sampler0")
                        .withSampler("Sampler2")
                        .withVertexFormat(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS)
                        .withBlend(BlendFunction.TRANSLUCENT)
                        .buildSnippet();

        MaLiLibPipelines.TERRAIN_MASA_STAGE =
                RenderPipeline.builder(MATRICES_COLOR_FOG_OFFSET_SNIPPET)
                        .withVertexShader("core/terrain")
                        .withFragmentShader("core/terrain")
                        .withSampler("Sampler0")
                        .withSampler("Sampler2")
                        .withVertexFormat(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS)
                        .withBlend(MASA_BLEND)
                        .buildSnippet();

        MaLiLibPipelines.ENTITY_TRANSLUCENT_STAGE =
                RenderPipeline.builder(MATRICES_COLOR_FOG_LIGHT_DIR_SNIPPET)
                        .withVertexShader("core/entity")
                        .withFragmentShader("core/entity")
                        .withSampler("Sampler0")
                        .withSampler("Sampler2")
                        .withVertexFormat(VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS)
                        .withBlend(BlendFunction.TRANSLUCENT)
                        .buildSnippet();

        MaLiLibPipelines.ENTITY_MASA_STAGE =
                RenderPipeline.builder(MATRICES_COLOR_FOG_LIGHT_DIR_SNIPPET)
                        .withVertexShader("core/entity")
                        .withFragmentShader("core/entity")
                        .withSampler("Sampler0")
                        .withSampler("Sampler2")
                        .withVertexFormat(VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS)
                        .withBlend(MASA_BLEND)
                        .buildSnippet();

        // POSITION_TRANSLUCENT
        MaLiLibPipelines.POSITION_TRANSLUCENT_NO_DEPTH_NO_CULL =
                register(RenderPipeline.builder(MaLiLibPipelines.POSITION_TRANSLUCENT_STAGE)
                                       .withLocation(MaLiLibReference.MOD_ID+"/pipeline/position/translucent/no_depth/no_cull")
                                       .withCull(false)
                                       .withDepthWrite(false)
                                       .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                                       .build()
                );

        MaLiLibPipelines.POSITION_TRANSLUCENT_NO_DEPTH =
                register(RenderPipeline.builder(MaLiLibPipelines.POSITION_TRANSLUCENT_STAGE)
                                       .withLocation(MaLiLibReference.MOD_ID+"/pipeline/position/translucent/no_depth")
                                       .withDepthWrite(false)
                                       .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                                       .build()
                );

        MaLiLibPipelines.POSITION_TRANSLUCENT_LESSER_DEPTH =
                register(RenderPipeline.builder(MaLiLibPipelines.POSITION_TRANSLUCENT_STAGE)
                                       .withLocation(MaLiLibReference.MOD_ID+"/pipeline/position/translucent/lesser_depth/no_cull")
                                       .withCull(false)
                                       .withDepthWrite(true)
                                       .withDepthTestFunction(DepthTestFunction.LESS_DEPTH_TEST)
                                       .build()
                );

        MaLiLibPipelines.POSITION_TRANSLUCENT_GREATER_DEPTH =
                register(RenderPipeline.builder(MaLiLibPipelines.POSITION_TRANSLUCENT_STAGE)
                                       .withLocation(MaLiLibReference.MOD_ID+"/pipeline/position/translucent/greater_depth/no_cull")
                                       .withCull(false)
                                       .withDepthWrite(true)
                                       .withDepthTestFunction(DepthTestFunction.GREATER_DEPTH_TEST)
                                       .build()
                );

        // POSITION_MASA
        MaLiLibPipelines.POSITION_MASA_NO_DEPTH_NO_CULL =
                register(RenderPipeline.builder(MaLiLibPipelines.POSITION_MASA_STAGE)
                        .withLocation(MaLiLibReference.MOD_ID+"/pipeline/position/masa/no_depth/no_cull")
                        .withCull(false)
                        .withDepthWrite(false)
                        .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                        .build()
                );

        MaLiLibPipelines.POSITION_MASA_NO_DEPTH =
                register(RenderPipeline.builder(MaLiLibPipelines.POSITION_MASA_STAGE)
                        .withLocation(MaLiLibReference.MOD_ID+"/pipeline/position/masa/no_depth")
                        .withDepthWrite(false)
                        .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                        .build()
                );

        MaLiLibPipelines.POSITION_MASA_LESSER_DEPTH =
                register(RenderPipeline.builder(MaLiLibPipelines.POSITION_MASA_STAGE)
                        .withLocation(MaLiLibReference.MOD_ID+"/pipeline/position/masa/lesser_depth")
                        .withCull(false)
                        .withDepthWrite(true)
                        .withDepthTestFunction(DepthTestFunction.LESS_DEPTH_TEST)
                        .build()
                );

        MaLiLibPipelines.POSITION_MASA_GREATER_DEPTH =
                register(RenderPipeline.builder(MaLiLibPipelines.POSITION_MASA_STAGE)
                        .withLocation(MaLiLibReference.MOD_ID+"/pipeline/position/masa/greater_depth")
                        .withCull(false)
                        .withDepthWrite(true)
                        .withDepthTestFunction(DepthTestFunction.GREATER_DEPTH_TEST)
                        .build()
                );

        // POSITION_COLOR_TRANSLUCENT
        MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_NO_DEPTH_NO_CULL =
                register(RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_STAGE)
                                       .withLocation(MaLiLibReference.MOD_ID+"/pipeline/position_color/translucent/no_depth/no_cull")
                                       .withCull(false)
                                       .withDepthWrite(false)
                                       .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                                       .build()
                );

        MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_NO_DEPTH =
                register(RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_STAGE)
                                       .withLocation(MaLiLibReference.MOD_ID+"/pipeline/position_color/translucent/no_depth")
                                       .withDepthWrite(false)
                                       .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                                       .build()
                );

        MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_LESSER_DEPTH =
                register(RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_STAGE)
                                       .withLocation(MaLiLibReference.MOD_ID+"/pipeline/position_color/translucent/lesser_depth")
                                       .withCull(false)
                                       .withDepthWrite(true)
                                       .withDepthTestFunction(DepthTestFunction.LESS_DEPTH_TEST)
                                       .build()
                );

        MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_GREATER_DEPTH =
                register(RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_STAGE)
                                       .withLocation(MaLiLibReference.MOD_ID+"/pipeline/position_color/translucent/greater_depth")
                                       .withCull(false)
                                       .withDepthWrite(true)
                                       .withDepthTestFunction(DepthTestFunction.GREATER_DEPTH_TEST)
                                       .build()
                );

        // POSITION_COLOR_MASA
        MaLiLibPipelines.POSITION_COLOR_MASA_NO_DEPTH_NO_CULL =
                register(RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_MASA_STAGE)
                        .withLocation(MaLiLibReference.MOD_ID+"/pipeline/position_color/masa/no_depth/no_cull")
                        .withCull(false)
                        .withDepthWrite(false)
                        .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                        .build()
                );

        MaLiLibPipelines.POSITION_COLOR_MASA_NO_DEPTH =
                register(RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_MASA_STAGE)
                        .withLocation(MaLiLibReference.MOD_ID+"/pipeline/position_color/masa/no_depth")
                        .withDepthWrite(false)
                        .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                        .build()
                );

        MaLiLibPipelines.POSITION_COLOR_MASA_LESSER_DEPTH =
                register(RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_MASA_STAGE)
                        .withLocation(MaLiLibReference.MOD_ID+"/pipeline/position_color/masa/lesser_depth")
                        .withCull(false)
                        .withDepthWrite(true)
                        .withDepthTestFunction(DepthTestFunction.LESS_DEPTH_TEST)
                        .build()
                );

        MaLiLibPipelines.POSITION_COLOR_MASA_GREATER_DEPTH =
                register(RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_MASA_STAGE)
                        .withLocation(MaLiLibReference.MOD_ID+"/pipeline/position_color/masa/greater_depth")
                        .withCull(false)
                        .withDepthWrite(true)
                        .withDepthTestFunction(DepthTestFunction.GREATER_DEPTH_TEST)
                        .build()
                );

        // POSITION_TEX_TRANSLUCENT
        MaLiLibPipelines.POSITION_TEX_TRANSLUCENT_NO_DEPTH_NO_CULL =
                register(RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_TRANSLUCENT_STAGE)
                                       .withLocation(MaLiLibReference.MOD_ID+"/pipeline/position_tex/translucent/no_depth/no_cull")
                                       .withCull(false)
                                       .withDepthWrite(false)
                                       .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                                       .build()
                );

        MaLiLibPipelines.POSITION_TEX_TRANSLUCENT_NO_DEPTH =
                register(RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_TRANSLUCENT_STAGE)
                                       .withLocation(MaLiLibReference.MOD_ID+"/pipeline/position_tex/translucent/no_depth")
                                       .withDepthWrite(false)
                                       .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                                       .build()
                );

        MaLiLibPipelines.POSITION_TEX_TRANSLUCENT_LESSER_DEPTH =
                register(RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_TRANSLUCENT_STAGE)
                                       .withLocation(MaLiLibReference.MOD_ID+"/pipeline/position_tex/translucent/lesser_depth")
                                       .withCull(false)
                                       .withDepthWrite(true)
                                       .withDepthTestFunction(DepthTestFunction.LESS_DEPTH_TEST)
                                       .build()
                );

        MaLiLibPipelines.POSITION_TEX_TRANSLUCENT_GREATER_DEPTH =
                register(RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_TRANSLUCENT_STAGE)
                                       .withLocation(MaLiLibReference.MOD_ID+"/pipeline/position_tex/translucent/greater_depth")
                                       .withCull(false)
                                       .withDepthWrite(true)
                                       .withDepthTestFunction(DepthTestFunction.GREATER_DEPTH_TEST)
                                       .build()
                );

        // POSITION_TEX_MASA
        MaLiLibPipelines.POSITION_TEX_MASA_NO_DEPTH_NO_CULL =
                register(RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_MASA_STAGE)
                        .withLocation(MaLiLibReference.MOD_ID+"/pipeline/position_tex/masa/no_depth/no_cull")
                        .withCull(false)
                        .withDepthWrite(false)
                        .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                        .build()
                );

        MaLiLibPipelines.POSITION_TEX_MASA_NO_DEPTH =
                register(RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_MASA_STAGE)
                        .withLocation(MaLiLibReference.MOD_ID+"/pipeline/position_tex/masa/no_depth")
                        .withDepthWrite(false)
                        .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                        .build()
                );

        MaLiLibPipelines.POSITION_TEX_MASA_LESSER_DEPTH =
                register(RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_MASA_STAGE)
                        .withLocation(MaLiLibReference.MOD_ID+"/pipeline/position_tex/masa/lesser_depth")
                        .withCull(false)
                        .withDepthWrite(true)
                        .withDepthTestFunction(DepthTestFunction.LESS_DEPTH_TEST)
                        .build()
                );

        MaLiLibPipelines.POSITION_TEX_MASA_GREATER_DEPTH =
                register(RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_MASA_STAGE)
                        .withLocation(MaLiLibReference.MOD_ID+"/pipeline/position_tex/masa/greater_depth")
                        .withCull(false)
                        .withDepthWrite(true)
                        .withDepthTestFunction(DepthTestFunction.GREATER_DEPTH_TEST)
                        .build()
                );

        // POSITION_TEX_COLOR_TRANSLUCENT
        MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_NO_DEPTH_NO_CULL =
                register(RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_STAGE)
                                       .withLocation(MaLiLibReference.MOD_ID+"/pipeline/position_tex_color/translucent/no_depth/no_cull")
                                       .withCull(false)
                                       .withDepthWrite(false)
                                       .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                                       .build()
                );

        MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_NO_DEPTH =
                register(RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_STAGE)
                                       .withLocation(MaLiLibReference.MOD_ID+"/pipeline/position_tex_color/translucent/no_depth")
                                       .withDepthWrite(false)
                                       .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                                       .build()
                );

        MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_LESSER_DEPTH =
                register(RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_STAGE)
                                       .withLocation(MaLiLibReference.MOD_ID+"/pipeline/position_tex_color/translucent/lesser_depth")
                                       .withCull(false)
                                       .withDepthWrite(true)
                                       .withDepthTestFunction(DepthTestFunction.LESS_DEPTH_TEST)
                                       .build()
                );

        MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_GREATER_DEPTH =
                register(RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_STAGE)
                                       .withLocation(MaLiLibReference.MOD_ID+"/pipeline/position_tex_color/translucent/greater_depth")
                                       .withCull(false)
                                       .withDepthWrite(true)
                                       .withDepthTestFunction(DepthTestFunction.GREATER_DEPTH_TEST)
                                       .build()
                );

        // POSITION_TEX_COLOR_MASA
        MaLiLibPipelines.POSITION_TEX_COLOR_MASA_NO_DEPTH_NO_CULL =
                register(RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_MASA_STAGE)
                        .withLocation(MaLiLibReference.MOD_ID+"/pipeline/position_tex_color/masa/no_depth/no_cull")
                        .withCull(false)
                        .withDepthWrite(false)
                        .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                        .build()
                );

        MaLiLibPipelines.POSITION_TEX_COLOR_MASA_NO_DEPTH =
                register(RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_MASA_STAGE)
                        .withLocation(MaLiLibReference.MOD_ID+"/pipeline/position_tex_color/masa/no_depth")
                        .withDepthWrite(false)
                        .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                        .build()
                );

        MaLiLibPipelines.POSITION_TEX_COLOR_MASA_LESSER_DEPTH =
                register(RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_MASA_STAGE)
                        .withLocation(MaLiLibReference.MOD_ID+"/pipeline/position_tex_color/masa/lesser_depth")
                        .withCull(false)
                        .withDepthWrite(true)
                        .withDepthTestFunction(DepthTestFunction.LESS_DEPTH_TEST)
                        .build()
                );

        MaLiLibPipelines.POSITION_TEX_COLOR_MASA_GREATER_DEPTH =
                register(RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_MASA_STAGE)
                        .withLocation(MaLiLibReference.MOD_ID+"/pipeline/position_tex_color/masa/greater_depth")
                        .withCull(false)
                        .withDepthWrite(true)
                        .withDepthTestFunction(DepthTestFunction.GREATER_DEPTH_TEST)
                        .build()
                );

        // LINES
        MaLiLibPipelines.LINES_NO_DEPTH_NO_CULL =
                register(RenderPipeline.builder(MaLiLibPipelines.LINES_STAGE)
                                       .withLocation(MaLiLibReference.MOD_ID+"/pipeline/lines/no_depth/no_cull")
                                       .withCull(false)
                                       .withDepthWrite(false)
                                       .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                                       .build()
                );

        MaLiLibPipelines.LINES_NO_DEPTH =
                register(RenderPipeline.builder(MaLiLibPipelines.LINES_STAGE)
                                       .withLocation(MaLiLibReference.MOD_ID+"/pipeline/lines/no_depth")
                                       .withDepthWrite(false)
                                       .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                                       .build()
                );

        MaLiLibPipelines.LINES_NO_CULL =
                register(RenderPipeline.builder(MaLiLibPipelines.LINES_STAGE)
                                           .withLocation(MaLiLibReference.MOD_ID+"/pipeline/lines/no_cull")
                                           .withCull(false)
                                           .build()
                );

        // LINES_MASA
        MaLiLibPipelines.LINES_MASA_NO_DEPTH_NO_CULL =
                register(RenderPipeline.builder(MaLiLibPipelines.LINES_MASA_STAGE)
                        .withLocation(MaLiLibReference.MOD_ID+"/pipeline/lines/masa/no_depth/no_cull")
                        .withCull(false)
                        .withDepthWrite(false)
                        .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                        .build()
                );

        MaLiLibPipelines.LINES_MASA_NO_DEPTH =
                register(RenderPipeline.builder(MaLiLibPipelines.LINES_MASA_STAGE)
                        .withLocation(MaLiLibReference.MOD_ID+"/pipeline/lines/masa/no_depth")
                        .withDepthWrite(false)
                        .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                        .build()
                );

        MaLiLibPipelines.LINES_MASA_NO_CULL =
                register(RenderPipeline.builder(MaLiLibPipelines.LINES_MASA_STAGE)
                        .withLocation(MaLiLibReference.MOD_ID+"/pipeline/lines/masa/no_cull")
                        .withCull(false)
                        .build()
                );

        // DEBUG_LINES_TRANSLUCENT
        MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_NO_DEPTH_NO_CULL =
                register(RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_STAGE)
                                       .withLocation(MaLiLibReference.MOD_ID+"/pipeline/debug_lines/translucent/no_depth/no_cull")
                                       .withCull(false)
                                       .withDepthWrite(false)
                                       .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                                       .build()
                );

        MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_NO_DEPTH =
                register(RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_STAGE)
                        .withLocation(MaLiLibReference.MOD_ID+"/pipeline/debug_lines/translucent/no_depth")
                        .withDepthWrite(false)
                        .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                        .build()
                );

        MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_NO_CULL =
                register(RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_STAGE)
                        .withLocation(MaLiLibReference.MOD_ID+"/pipeline/debug_lines/translucent/no_cull")
                        .withDepthWrite(false)
                        .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                        .build()
                );

        // DEBUG_LINES_MASA
        MaLiLibPipelines.DEBUG_LINES_MASA_NO_DEPTH_NO_CULL =
                register(RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINES_MASA_STAGE)
                        .withLocation(MaLiLibReference.MOD_ID+"/pipeline/debug_lines/masa/no_depth/no_cull")
                        .withCull(false)
                        .withDepthWrite(false)
                        .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                        .build()
                );

        MaLiLibPipelines.DEBUG_LINES_MASA_NO_DEPTH =
                register(RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINES_MASA_STAGE)
                        .withLocation(MaLiLibReference.MOD_ID+"/pipeline/debug_lines/masa/no_depth")
                        .withDepthWrite(false)
                        .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                        .build()
                );

        MaLiLibPipelines.DEBUG_LINES_MASA_NO_CULL =
                register(RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINES_MASA_STAGE)
                        .withLocation(MaLiLibReference.MOD_ID+"/pipeline/debug_lines/masa/no_cull")
                        .withDepthWrite(false)
                        .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                        .build()
                );

        // TERRAIN_TRANSLUCENT
        MaLiLibPipelines.SOLID_TRANSLUCENT =
                register(RenderPipeline.builder(MaLiLibPipelines.TERRAIN_TRANSLUCENT_STAGE)
                        .withLocation(MaLiLibReference.MOD_ID+"/pipeline/solid/translucent")
                        .build()
                );

        MaLiLibPipelines.WIREFRAME_TRANSLUCENT =
                register(RenderPipeline.builder(MaLiLibPipelines.TERRAIN_TRANSLUCENT_STAGE)
                        .withLocation(MaLiLibReference.MOD_ID+"/pipeline/wireframe/translucent")
                        .withPolygonMode(PolygonMode.WIREFRAME)
                        .build()
                );

        MaLiLibPipelines.CUTOUT_MIPPED_TRANSLUCENT =
                register(RenderPipeline.builder(MaLiLibPipelines.TERRAIN_TRANSLUCENT_STAGE)
                        .withLocation(MaLiLibReference.MOD_ID+"/pipeline/cutout_mipped/translucent")
                        .withShaderDefine("ALPHA_CUTOUT", 0.5F)
                        .build()
                );

        MaLiLibPipelines.CUTOUT_TRANSLUCENT =
                register(RenderPipeline.builder(MaLiLibPipelines.TERRAIN_TRANSLUCENT_STAGE)
                        .withLocation(MaLiLibReference.MOD_ID+"/pipeline/cutout/translucent")
                        .withShaderDefine("ALPHA_CUTOUT", 0.1F)
                        .build()
                );

        // TERRAIN_MASA
        MaLiLibPipelines.SOLID_MASA =
                register(RenderPipeline.builder(MaLiLibPipelines.TERRAIN_MASA_STAGE)
                        .withLocation(MaLiLibReference.MOD_ID+"/pipeline/solid/masa")
                        .build()
                );

        MaLiLibPipelines.WIREFRAME_MASA =
                register(RenderPipeline.builder(MaLiLibPipelines.TERRAIN_MASA_STAGE)
                        .withLocation(MaLiLibReference.MOD_ID+"/pipeline/wireframe/masa")
                        .withPolygonMode(PolygonMode.WIREFRAME)
                        .build()
                );

        MaLiLibPipelines.CUTOUT_MIPPED_MASA =
                register(RenderPipeline.builder(MaLiLibPipelines.TERRAIN_MASA_STAGE)
                        .withLocation(MaLiLibReference.MOD_ID+"/pipeline/cutout_mipped/masa")
                        .withShaderDefine("ALPHA_CUTOUT", 0.5F)
                        .build()
                );

        MaLiLibPipelines.CUTOUT_MASA =
                register(RenderPipeline.builder(MaLiLibPipelines.TERRAIN_MASA_STAGE)
                        .withLocation(MaLiLibReference.MOD_ID+"/pipeline/cutout/masa")
                        .withShaderDefine("ALPHA_CUTOUT", 0.1F)
                        .build()
                );

    }
}
