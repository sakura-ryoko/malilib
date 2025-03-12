package fi.dy.masa.malilib.mixin.render;

import java.util.Map;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gl.ShaderPipelines;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.render.MaLiLibPipelines;

@Mixin(ShaderPipelines.class)
public abstract class MixinShaderPipelines
{
    @Shadow @Final public static Map<Identifier, RenderPipeline> PIPELINES;
    @Shadow @Final public static RenderPipeline.Snippet field_56846;    // MATRICES
    @Shadow @Final public static RenderPipeline.Snippet field_56847;    // FOG_NO_COLOR
    @Shadow @Final public static RenderPipeline.Snippet field_56848;    // FOG
    @Shadow @Final public static RenderPipeline.Snippet field_56849;    // MATRICES_COLOR
    @Shadow @Final public static RenderPipeline.Snippet field_56850;    // MATRICES_COLOR_FOG
    @Shadow @Final public static RenderPipeline.Snippet field_56851;    // MATRICES_COLOR_FOG_OFFSET
    @Shadow @Final public static RenderPipeline.Snippet field_56852;    // MATRICES_COLOR_FOG_LIGHT_DIR
    @Shadow @Final public static RenderPipeline.Snippet field_56853;    // TERRAIN
    @Shadow @Final public static RenderPipeline.Snippet field_56854;    // ENTITY
    @Shadow @Final public static RenderPipeline.Snippet field_56855;    // RENDERTYPE_BEACON_BEAM
    @Shadow @Final public static RenderPipeline.Snippet field_56856;    // TEXT
    @Shadow @Final public static RenderPipeline.Snippet field_56857;    // RENDERTYPE_END_PORTAL
    @Shadow @Final public static RenderPipeline.Snippet field_56858;    // RENDERTYPE_CLOUDS
    @Shadow @Final public static RenderPipeline.Snippet field_56859;    // RENDERTYPE_LINES
    @Shadow @Final public static RenderPipeline.Snippet field_56860;    // DEBUG_FILLED
    @Shadow @Final public static RenderPipeline.Snippet field_56861;    // PARTICLE_TEX
    @Shadow @Final public static RenderPipeline.Snippet field_56862;    // WEATHER
    @Shadow @Final public static RenderPipeline.Snippet field_56863;    // GUI
    @Shadow @Final public static RenderPipeline.Snippet field_56864;    // GUI_TEXTURED
    @Shadow @Final public static RenderPipeline.Snippet field_56892;    // RENDERTYPE_OUTLINE
    @Shadow @Final public static RenderPipeline.Snippet field_56838;    // POST_PROCESSOR

//    @Unique
//    private static final BlendFunction BLENDER = new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);

    @Shadow
    public static RenderPipeline method_67887(RenderPipeline renderPipeline)
    {
        PIPELINES.put(renderPipeline.getLocation(), renderPipeline);
        return renderPipeline;
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void  malilib_onRegisterPipelines(CallbackInfo ci)
    {
        // STAGES
        MaLiLibPipelines.POSITION_STAGE =
                RenderPipeline.builder(field_56850)         // MATRICES_COLOR_FOG
                              .withVertexShader("core/position")
                              .withFragmentShader("core/position")
                              .withBlend(BlendFunction.TRANSLUCENT)
                              .withVertexFormat(VertexFormats.field_29336, VertexFormat.class_5596.QUADS)
                              .buildSnippet();              // POSITION

        MaLiLibPipelines.POSITION_COLOR_STAGE =
                RenderPipeline.builder(field_56849)         // MATRICES_COLOR
                              .withVertexShader("core/position_color")
                              .withFragmentShader("core/position_color")
                              .withBlend(BlendFunction.TRANSLUCENT)
                              .withVertexFormat(VertexFormats.field_1576, VertexFormat.class_5596.QUADS)
                              .buildSnippet();              // POSITION_COLOR

        MaLiLibPipelines.POSITION_TEX_STAGE =
                RenderPipeline.builder(field_56849)         // MATRICES_COLOR
                              .withVertexShader("core/position_tex")
                              .withFragmentShader("core/position_tex")
                              .withSampler("Sampler0")
                              .withBlend(BlendFunction.TRANSLUCENT)
                              .withVertexFormat(VertexFormats.field_1585, VertexFormat.class_5596.QUADS)
                              .buildSnippet();              // POSITION_TEX

        MaLiLibPipelines.POSITION_TEX_COLOR_STAGE =
                RenderPipeline.builder(field_56849)         // MATRICES_COLOR
                              .withVertexShader("core/position_tex_color")
                              .withFragmentShader("core/position_tex_color")
                              .withSampler("Sampler0")
                              .withBlend(BlendFunction.TRANSLUCENT)
                              .withVertexFormat(VertexFormats.field_1575, VertexFormat.class_5596.QUADS)
                              .buildSnippet();              // POSITION_TEX_COLOR

        MaLiLibPipelines.LINES_STAGE =
                RenderPipeline.builder(field_56850)         // MATRICES_COLOR_FOG
                              .withVertexShader("core/rendertype_lines")
                              .withFragmentShader("core/rendertype_lines")
                              .withUniform("LineWidth", UniformType.FLOAT)
                              .withUniform("ScreenSize", UniformType.VEC2)
//                              .withBlend(BlendFunction.TRANSLUCENT)
                              .withCull(false)
                              .withVertexFormat(VertexFormats.field_29337, VertexFormat.class_5596.LINES)
                              .buildSnippet();              // LINES

        // POSITION
        MaLiLibPipelines.POSITION_SIMPLE =
                method_67887(RenderPipeline.builder(MaLiLibPipelines.POSITION_STAGE)
                                       .withLocation(MaLiLibReference.MOD_ID+"_pipeline/position_simple")
                                       .withCull(false)
                                       .withDepthWrite(false)
                                       .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                                       .build()
                );

        MaLiLibPipelines.POSITION_CULLING =
                method_67887(RenderPipeline.builder(MaLiLibPipelines.POSITION_STAGE)
                                       .withLocation(MaLiLibReference.MOD_ID+"_pipeline/position_culling")
                                       .withCull(true)
                                       .withDepthWrite(false)
                                       .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                                       .build()
                );

        MaLiLibPipelines.POSITION_LESSER_DEPTH =
                method_67887(RenderPipeline.builder(MaLiLibPipelines.POSITION_STAGE)
                                       .withLocation(MaLiLibReference.MOD_ID+"_pipeline/position_lesser_depth")
                                       .withCull(false)
                                       .withDepthWrite(true)
                                       .withDepthTestFunction(DepthTestFunction.LESS_DEPTH_TEST)
                                       .build()
                );

        MaLiLibPipelines.POSITION_EQUAL_DEPTH =
                method_67887(RenderPipeline.builder(MaLiLibPipelines.POSITION_STAGE)
                                       .withLocation(MaLiLibReference.MOD_ID+"_pipeline/position_equal_depth")
                                       .withCull(false)
                                       .withDepthWrite(true)
                                       .withDepthTestFunction(DepthTestFunction.EQUAL_DEPTH_TEST)
                                       .build()
                );

        MaLiLibPipelines.POSITION_LEQUAL_DEPTH =
                method_67887(RenderPipeline.builder(MaLiLibPipelines.POSITION_STAGE)
                                       .withLocation(MaLiLibReference.MOD_ID+"_pipeline/position_lequal_depth")
                                       .withCull(false)
                                       .withDepthWrite(true)
                                       .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
                                       .build()
                );

        MaLiLibPipelines.POSITION_GREATER_DEPTH =
                method_67887(RenderPipeline.builder(MaLiLibPipelines.POSITION_STAGE)
                                       .withLocation(MaLiLibReference.MOD_ID+"_pipeline/position_greater_depth")
                                       .withCull(false)
                                       .withDepthWrite(true)
                                       .withDepthTestFunction(DepthTestFunction.GREATER_DEPTH_TEST)
                                       .build()
                );

        // POSITION_COLOR
        MaLiLibPipelines.POSITION_COLOR_SIMPLE =
                method_67887(RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_STAGE)    // POSITION_COLOR
                                       .withLocation(MaLiLibReference.MOD_ID+"_pipeline/position_color_simple")
                                       .withCull(false)
                                       .withDepthWrite(false)
                                       .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                                       .build()
                );

        MaLiLibPipelines.POSITION_COLOR_CULLING =
                method_67887(RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_STAGE)    // POSITION_COLOR
                                       .withLocation(MaLiLibReference.MOD_ID+"_pipeline/position_color_culling")
                                       .withCull(true)
                                       .withDepthWrite(false)
                                       .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                                       .build()
                );

        MaLiLibPipelines.POSITION_COLOR_LESSER_DEPTH =
                method_67887(RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_STAGE)    // POSITION_COLOR
                                       .withLocation(MaLiLibReference.MOD_ID+"_pipeline/position_color_lesser_depth")
                                       .withCull(false)
                                       .withDepthWrite(true)
                                       .withDepthTestFunction(DepthTestFunction.LESS_DEPTH_TEST)
                                       .build()
                );

        MaLiLibPipelines.POSITION_COLOR_EQUAL_DEPTH =
                method_67887(RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_STAGE)    // POSITION_COLOR
                                       .withLocation(MaLiLibReference.MOD_ID+"_pipeline/position_color_equal_depth")
                                       .withCull(false)
                                       .withDepthWrite(true)
                                       .withDepthTestFunction(DepthTestFunction.EQUAL_DEPTH_TEST)
                                       .build()
                );

        MaLiLibPipelines.POSITION_COLOR_LEQUAL_DEPTH =
                method_67887(RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_STAGE)    // POSITION_COLOR
                                       .withLocation(MaLiLibReference.MOD_ID+"_pipeline/position_color_lequal_depth")
                                       .withCull(false)
                                       .withDepthWrite(true)
                                       .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
                                       .build()
                );

        MaLiLibPipelines.POSITION_COLOR_GREATER_DEPTH =
                method_67887(RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_STAGE)    // POSITION_COLOR
                                       .withLocation(MaLiLibReference.MOD_ID+"_pipeline/position_color_greater_depth")
                                       .withCull(false)
                                       .withDepthWrite(true)
                                       .withDepthTestFunction(DepthTestFunction.GREATER_DEPTH_TEST)
                                       .build()
                );

        // POSITION_TEX
        MaLiLibPipelines.POSITION_TEX_SIMPLE =
                method_67887(RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_STAGE)
                                       .withLocation(MaLiLibReference.MOD_ID+"_pipeline/position_tex_simple")
                                       .withCull(false)
                                       .withDepthWrite(false)
                                       .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                                       .build()
                );

        MaLiLibPipelines.POSITION_TEX_CULLING =
                method_67887(RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_STAGE)
                                       .withLocation(MaLiLibReference.MOD_ID+"_pipeline/position_tex_culling")
                                       .withCull(true)
                                       .withDepthWrite(false)
                                       .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                                       .build()
                );

        MaLiLibPipelines.POSITION_TEX_LESSER_DEPTH =
                method_67887(RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_STAGE)
                                       .withLocation(MaLiLibReference.MOD_ID+"_pipeline/position_tex_lesser_depth")
                                       .withCull(false)
                                       .withDepthWrite(true)
                                       .withDepthTestFunction(DepthTestFunction.LESS_DEPTH_TEST)
                                       .build()
                );

        MaLiLibPipelines.POSITION_TEX_EQUAL_DEPTH =
                method_67887(RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_STAGE)
                                       .withLocation(MaLiLibReference.MOD_ID+"_pipeline/position_tex_equal_depth")
                                       .withCull(false)
                                       .withDepthWrite(true)
                                       .withDepthTestFunction(DepthTestFunction.EQUAL_DEPTH_TEST)
                                       .build()
                );

        MaLiLibPipelines.POSITION_TEX_LEQUAL_DEPTH =
                method_67887(RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_STAGE)
                                       .withLocation(MaLiLibReference.MOD_ID+"_pipeline/position_tex_lequal_depth")
                                       .withCull(false)
                                       .withDepthWrite(true)
                                       .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
                                       .build()
                );

        MaLiLibPipelines.POSITION_TEX_GREATER_DEPTH =
                method_67887(RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_STAGE)
                                       .withLocation(MaLiLibReference.MOD_ID+"_pipeline/position_tex_greater_depth")
                                       .withCull(false)
                                       .withDepthWrite(true)
                                       .withDepthTestFunction(DepthTestFunction.GREATER_DEPTH_TEST)
                                       .build()
                );

        // POSITION_TEX_COLOR
        MaLiLibPipelines.POSITION_TEX_COLOR_SIMPLE =
                method_67887(RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_STAGE)
                                       .withLocation(MaLiLibReference.MOD_ID+"_pipeline/position_tex_color_simple")
                                       .withCull(false)
                                       .withDepthWrite(false)
                                       .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                                       .build()
                );

        MaLiLibPipelines.POSITION_TEX_COLOR_CULLING =
                method_67887(RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_STAGE)
                                       .withLocation(MaLiLibReference.MOD_ID+"_pipeline/position_tex_color_culling")
                                       .withCull(true)
                                       .withDepthWrite(false)
                                       .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                                       .build()
                );

        MaLiLibPipelines.POSITION_TEX_COLOR_LESSER_DEPTH =
                method_67887(RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_STAGE)
                                       .withLocation(MaLiLibReference.MOD_ID+"_pipeline/position_tex_color_lesser_depth")
                                       .withCull(false)
                                       .withDepthWrite(true)
                                       .withDepthTestFunction(DepthTestFunction.LESS_DEPTH_TEST)
                                       .build()
                );

        MaLiLibPipelines.POSITION_TEX_COLOR_LEQUAL_DEPTH =
                method_67887(RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_STAGE)
                                       .withLocation(MaLiLibReference.MOD_ID+"_pipeline/position_tex_color_lequal_depth")
                                       .withCull(false)
                                       .withDepthWrite(true)
                                       .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
                                       .build()
                );

        MaLiLibPipelines.POSITION_TEX_COLOR_EQUAL_DEPTH =
                method_67887(RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_STAGE)
                                       .withLocation(MaLiLibReference.MOD_ID+"_pipeline/position_tex_color_equal_depth")
                                       .withCull(false)
                                       .withDepthWrite(true)
                                       .withDepthTestFunction(DepthTestFunction.EQUAL_DEPTH_TEST)
                                       .build()
                );

        MaLiLibPipelines.POSITION_TEX_COLOR_GREATER_DEPTH =
                method_67887(RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_STAGE)
                                       .withLocation(MaLiLibReference.MOD_ID+"_pipeline/position_tex_color_greater_depth")
                                       .withCull(false)
                                       .withDepthWrite(true)
                                       .withDepthTestFunction(DepthTestFunction.GREATER_DEPTH_TEST)
                                       .build()                 // POSITION_TEX_COLOR
                );

        // LINES
        MaLiLibPipelines.LINES_SIMPLE =
                method_67887(RenderPipeline.builder(MaLiLibPipelines.LINES_STAGE)
                                       .withLocation(MaLiLibReference.MOD_ID+"_pipeline/lines_simple")
                                       .withCull(false)
                                       .withDepthWrite(false)
                                       .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                                       .build()
                );

        MaLiLibPipelines.LINES_CULLING =
                method_67887(RenderPipeline.builder(MaLiLibPipelines.LINES_STAGE)
                                       .withLocation(MaLiLibReference.MOD_ID+"_pipeline/lines_culling")
                                       .withCull(true)
                                       .withDepthWrite(false)
                                       .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                                       .build()
                );

        MaLiLibPipelines.LINES_NO_DEPTH =
                method_67887(RenderPipeline.builder(MaLiLibPipelines.LINES_STAGE)
                                           .withLocation(MaLiLibReference.MOD_ID+"_pipeline/lines_no_depth")
                                           .withCull(false)
                                           .build()
                );

//        MaLiLibPipelines.LINES_LESSER_DEPTH =
//                method_67887(RenderPipeline.builder(MaLiLibPipelines.LINES_STAGE)
//                                       .withLocation(MaLiLibReference.MOD_ID+"_pipeline/lines_lesser_withDepthWrite")
//                                       .withCull(false)
//                                       .withDepthWrite(true)
//                                       .withDepthTestFunction(DepthTestFunction.LESS_DEPTH_TEST)
//                                       .build()
//                );
//
//        MaLiLibPipelines.LINES_EQUAL_DEPTH =
//                method_67887(RenderPipeline.builder(MaLiLibPipelines.LINES_STAGE)
//                                       .withLocation(MaLiLibReference.MOD_ID+"_pipeline/lines_equal_withDepthWrite")
//                                       .withCull(false)
//                                       .withDepthWrite(true)
//                                       .withDepthTestFunction(DepthTestFunction.EQUAL_DEPTH_TEST)
//                                       .build()
//                );
//
//        MaLiLibPipelines.LINES_LEQUAL_DEPTH =
//                method_67887(RenderPipeline.builder(MaLiLibPipelines.LINES_STAGE)
//                                       .withLocation(MaLiLibReference.MOD_ID+"_pipeline/lines_lequal_withDepthWrite")
//                                       .withCull(false)
//                                       .withDepthWrite(true)
//                                       .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
//                                       .build()
//                );
//
//        MaLiLibPipelines.LINES_GREATER_DEPTH =
//                method_67887(RenderPipeline.builder(MaLiLibPipelines.LINES_STAGE)
//                                       .withLocation(MaLiLibReference.MOD_ID+"_pipeline/lines_greater_withDepthWrite")
//                                       .withCull(false)
//                                       .withDepthWrite(true)
//                                       .withDepthTestFunction(DepthTestFunction.GREATER_DEPTH_TEST)
//                                       .build()
//                );
//
//        // DEBUG_LINES
        MaLiLibPipelines.DEBUG_LINES_SIMPLE =
                method_67887(RenderPipeline.builder(field_56849)                // MATRIX_COLOR
                                       .withLocation(MaLiLibReference.MOD_ID+"_pipeline/debug_lines_simple")
                                       .withVertexShader("core/position_color")
                                       .withFragmentShader("core/position_color")
                                       .withCull(false)
                                       .withDepthWrite(false)
                                       .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                                       .withVertexFormat(VertexFormats.field_1576, VertexFormat.class_5596.DEBUG_LINE_STRIP)
                                       .withBlend(BlendFunction.TRANSLUCENT)
                                       .build()
                );

//        MaLiLibPipelines.DEBUG_LINES_CULLING =
//                method_67887(RenderPipeline.builder(MATRICES_COLOR)
//                                       .withLocation(MaLiLibReference.MOD_ID+"_pipeline/debug_lines_withCull")
//                                       .withVertexShader("core/position_color")
//                                       .withFragmentShader("core/position_color")
//                                       .withCull(true)
//                                       .withDepthWrite(false)
//                                       .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
//                                       .withVertexFormat(VertexFormats.POSITION_COLOR, class_5596.DEBUG_LINE_STRIP)
//                                       .withBlend(BlendFunction.TRANSLUCENT)
//                                       .build()
//                );
//
//        MaLiLibPipelines.DEBUG_LINES_LESSER_DEPTH =
//                method_67887(RenderPipeline.builder(MATRICES_COLOR)
//                                       .withLocation(MaLiLibReference.MOD_ID+"_pipeline/debug_lines_lesser_withDepthWrite")
//                                       .withVertexShader("core/position_color")
//                                       .withFragmentShader("core/position_color")
//                                       .withCull(false)
//                                       .withDepthWrite(true)
//                                       .withDepthTestFunction(DepthTestFunction.LESS_DEPTH_TEST)
//                                       .withVertexFormat(VertexFormats.POSITION_COLOR, class_5596.DEBUG_LINE_STRIP)
//                                       .withBlend(BlendFunction.TRANSLUCENT)
//                                       .build()
//                );
//
//        MaLiLibPipelines.DEBUG_LINES_LEQUAL_DEPTH =
//                method_67887(RenderPipeline.builder(MATRICES_COLOR)
//                                       .withLocation(MaLiLibReference.MOD_ID+"_pipeline/debug_lines_lequal_withDepthWrite")
//                                       .withVertexShader("core/position_color")
//                                       .withFragmentShader("core/position_color")
//                                       .withCull(false)
//                                       .withDepthWrite(true)
//                                       .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
//                                       .withVertexFormat(VertexFormats.POSITION_COLOR, class_5596.DEBUG_LINE_STRIP)
//                                       .withBlend(BlendFunction.TRANSLUCENT)
//                                       .build()
//                );
//
//        MaLiLibPipelines.DEBUG_LINES_EQUAL_DEPTH =
//                method_67887(RenderPipeline.builder(MATRICES_COLOR)
//                                       .withLocation(MaLiLibReference.MOD_ID+"_pipeline/debug_lines_equal_withDepthWrite")
//                                       .withVertexShader("core/position_color")
//                                       .withFragmentShader("core/position_color")
//                                       .withCull(false)
//                                       .withDepthWrite(true)
//                                       .withDepthTestFunction(DepthTestFunction.EQUAL_DEPTH_TEST)
//                                       .withVertexFormat(VertexFormats.POSITION_COLOR, class_5596.DEBUG_LINE_STRIP)
//                                       .withBlend(BlendFunction.TRANSLUCENT)
//                                       .build()
//                );
//
//        MaLiLibPipelines.DEBUG_LINES_GREATER_DEPTH =
//                method_67887(RenderPipeline.builder(MATRICES_COLOR)
//                                       .withLocation(MaLiLibReference.MOD_ID+"_pipeline/debug_lines_greater_withDepthWrite")
//                                       .withVertexShader("core/position_color")
//                                       .withFragmentShader("core/position_color")
//                                       .withCull(false)
//                                       .withDepthWrite(true)
//                                       .withDepthTestFunction(DepthTestFunction.GREATER_DEPTH_TEST)
//                                       .withVertexFormat(VertexFormats.POSITION_COLOR, class_5596.DEBUG_LINE_STRIP)
//                                       .withBlend(BlendFunction.TRANSLUCENT)
//                                       .build()
//                );
    }
}
