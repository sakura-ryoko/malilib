package fi.dy.masa.malilib.mixin.render;

import java.util.Map;

import net.minecraft.client.gl.BlendPrograms;
import net.minecraft.client.gl.ShaderPipeline;
import net.minecraft.client.gl.ShaderPipelines;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.render.DepthTestState;
import net.minecraft.client.render.VertexFormat;
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
    @Shadow @Final public static Map<Identifier, ShaderPipeline> PIPELINES;
    @Shadow @Final public static ShaderPipeline.Stage MATRICES;
    @Shadow @Final public static ShaderPipeline.Stage FOG;
    @Shadow @Final public static ShaderPipeline.Stage MATRICES_COLOR;
    @Shadow @Final public static ShaderPipeline.Stage FOG_COLOR;
    @Shadow @Final public static ShaderPipeline.Stage OFFSET_FOG_COLOR;
    @Shadow @Final public static ShaderPipeline.Stage FOG_COLOR_UV;
    @Shadow @Final public static ShaderPipeline.Stage TERRAIN;
    @Shadow @Final public static ShaderPipeline.Stage ENTITY;
    @Shadow @Final public static ShaderPipeline.Stage RENDERTYPE_BEACON_BEAM;
    @Shadow @Final public static ShaderPipeline.Stage TEXT;
    @Shadow @Final public static ShaderPipeline.Stage RENDERTYPE_END_PORTAL;
    @Shadow @Final public static ShaderPipeline.Stage RENDERTYPE_CLOUDS;
    @Shadow @Final public static ShaderPipeline.Stage RENDERTYPE_LINES;
    @Shadow @Final public static ShaderPipeline.Stage POSITION_COLOR;
    @Shadow @Final public static ShaderPipeline.Stage PARTICLE_TEX;
    @Shadow @Final public static ShaderPipeline.Stage WEATHER;
    @Shadow @Final public static ShaderPipeline.Stage CORE_GUI;
    @Shadow @Final public static ShaderPipeline.Stage POSITION_TEX_COLOR;
    @Shadow @Final public static ShaderPipeline.Stage RENDERTYPE_OUTLINE;
    @Shadow @Final public static ShaderPipeline.Stage POST_PROCESSOR;

//    @Unique
//    private static final BlendPrograms BLENDER = new BlendPrograms(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);

    @Shadow
    public static ShaderPipeline register(ShaderPipeline pipeline)
    {
        PIPELINES.put(pipeline.getId(), pipeline);
        return pipeline;
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void malilib_onRegisterPipelines(CallbackInfo ci)
    {
        // STAGES
        MaLiLibPipelines.POSITION_STAGE =
                ShaderPipeline.builder(FOG_COLOR)
                              .vertexShader("core/position")
                              .fragmentShader("core/position")
                              .blender(BlendPrograms.TRANSLUCENT)
                              .format(VertexFormats.POSITION, VertexFormat.DrawMode.QUADS)
                              .buildStage();

        MaLiLibPipelines.POSITION_TEX_STAGE =
                ShaderPipeline.builder(MATRICES_COLOR)
                              .vertexShader("core/position_tex")
                              .fragmentShader("core/position_tex")
                              .samples("Sampler0")
                              .blender(BlendPrograms.TRANSLUCENT)
                              .format(VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS)
                              .buildStage();

        MaLiLibPipelines.LINES_STAGE =
                ShaderPipeline.builder(FOG_COLOR)
                              .vertexShader("core/rendertype_lines")
                              .fragmentShader("core/rendertype_lines")
                              .uniform("LineWidth", UniformType.FLOAT)
                              .uniform("ScreenSize", UniformType.VEC2)
                              .blender(BlendPrograms.TRANSLUCENT)
                              .culling(false)
                              .format(VertexFormats.LINES, VertexFormat.DrawMode.LINES)
                              .buildStage();

        // POSITION
        MaLiLibPipelines.POSITION_SIMPLE =
                register(ShaderPipeline.builder(MaLiLibPipelines.POSITION_STAGE)
                                       .id(MaLiLibReference.MOD_ID+"_pipeline/position_simple")
                                       .culling(false)
                                       .depth(false)
                                       .depthTestState(DepthTestState.NO_DEPTH_TEST)
                                       .buildPipeline()
                );

        MaLiLibPipelines.POSITION_CULLING =
                register(ShaderPipeline.builder(MaLiLibPipelines.POSITION_STAGE)
                                       .id(MaLiLibReference.MOD_ID+"_pipeline/position_culling")
                                       .culling(true)
                                       .depth(false)
                                       .depthTestState(DepthTestState.NO_DEPTH_TEST)
                                       .buildPipeline()
                );

        MaLiLibPipelines.POSITION_LESSER_DEPTH =
                register(ShaderPipeline.builder(MaLiLibPipelines.POSITION_STAGE)
                                       .id(MaLiLibReference.MOD_ID+"_pipeline/position_lesser_depth")
                                       .culling(false)
                                       .depth(true)
                                       .depthTestState(DepthTestState.LESS_DEPTH_TEST)
                                       .buildPipeline()
                );

        MaLiLibPipelines.POSITION_EQUAL_DEPTH =
                register(ShaderPipeline.builder(MaLiLibPipelines.POSITION_STAGE)
                                       .id(MaLiLibReference.MOD_ID+"_pipeline/position_equal_depth")
                                       .culling(false)
                                       .depth(true)
                                       .depthTestState(DepthTestState.EQUAL_DEPTH_TEST)
                                       .buildPipeline()
                );

        MaLiLibPipelines.POSITION_LEQUAL_DEPTH =
                register(ShaderPipeline.builder(MaLiLibPipelines.POSITION_STAGE)
                                       .id(MaLiLibReference.MOD_ID+"_pipeline/position_lequal_depth")
                                       .culling(false)
                                       .depth(true)
                                       .depthTestState(DepthTestState.LEQUAL_DEPTH_TEST)
                                       .buildPipeline()
                );

        MaLiLibPipelines.POSITION_GREATER_DEPTH =
                register(ShaderPipeline.builder(MaLiLibPipelines.POSITION_STAGE)
                                       .id(MaLiLibReference.MOD_ID+"_pipeline/position_greater_depth")
                                       .culling(false)
                                       .depth(true)
                                       .depthTestState(DepthTestState.GREATER_DEPTH_TEST)
                                       .buildPipeline()
                );

        // POSITION_COLOR
        MaLiLibPipelines.POSITION_COLOR_SIMPLE =
                register(ShaderPipeline.builder(POSITION_COLOR)
                                       .id(MaLiLibReference.MOD_ID+"_pipeline/position_color_simple")
                                       .culling(false)
                                       .depth(false)
                                       .depthTestState(DepthTestState.NO_DEPTH_TEST)
                                       .buildPipeline()
                );

        MaLiLibPipelines.POSITION_COLOR_CULLING =
                register(ShaderPipeline.builder(POSITION_COLOR)
                                       .id(MaLiLibReference.MOD_ID+"_pipeline/position_color_culling")
                                       .culling(true)
                                       .depth(false)
                                       .depthTestState(DepthTestState.NO_DEPTH_TEST)
                                       .buildPipeline()
                );

        MaLiLibPipelines.POSITION_COLOR_LESSER_DEPTH =
                register(ShaderPipeline.builder(POSITION_COLOR)
                                       .id(MaLiLibReference.MOD_ID+"_pipeline/position_color_lesser_depth")
                                       .culling(false)
                                       .depth(true)
                                       .depthTestState(DepthTestState.LESS_DEPTH_TEST)
                                       .buildPipeline()
                );

        MaLiLibPipelines.POSITION_COLOR_EQUAL_DEPTH =
                register(ShaderPipeline.builder(POSITION_COLOR)
                                       .id(MaLiLibReference.MOD_ID+"_pipeline/position_color_equal_depth")
                                       .culling(false)
                                       .depth(true)
                                       .depthTestState(DepthTestState.EQUAL_DEPTH_TEST)
                                       .buildPipeline()
                );

        MaLiLibPipelines.POSITION_COLOR_LEQUAL_DEPTH =
                register(ShaderPipeline.builder(POSITION_COLOR)
                                       .id(MaLiLibReference.MOD_ID+"_pipeline/position_color_lequal_depth")
                                       .culling(false)
                                       .depth(true)
                                       .depthTestState(DepthTestState.LEQUAL_DEPTH_TEST)
                                       .buildPipeline()
                );

        MaLiLibPipelines.POSITION_COLOR_GREATER_DEPTH =
                register(ShaderPipeline.builder(POSITION_COLOR)
                                       .id(MaLiLibReference.MOD_ID+"_pipeline/position_color_greater_depth")
                                       .culling(false)
                                       .depth(true)
                                       .depthTestState(DepthTestState.GREATER_DEPTH_TEST)
                                       .buildPipeline()
                );

        // POSITION_TEX
        MaLiLibPipelines.POSITION_TEX_SIMPLE =
                register(ShaderPipeline.builder(MaLiLibPipelines.POSITION_TEX_STAGE)
                                       .id(MaLiLibReference.MOD_ID+"_pipeline/position_tex_simple")
                                       .culling(false)
                                       .depth(false)
                                       .depthTestState(DepthTestState.NO_DEPTH_TEST)
                                       .format(VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS)
                                       .buildPipeline()
                );

        MaLiLibPipelines.POSITION_TEX_CULLING =
                register(ShaderPipeline.builder(MaLiLibPipelines.POSITION_TEX_STAGE)
                                       .id(MaLiLibReference.MOD_ID+"_pipeline/position_tex_culling")
                                       .culling(true)
                                       .depth(false)
                                       .depthTestState(DepthTestState.NO_DEPTH_TEST)
                                       .format(VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS)
                                       .buildPipeline()
                );

        MaLiLibPipelines.POSITION_TEX_LESSER_DEPTH =
                register(ShaderPipeline.builder(MaLiLibPipelines.POSITION_TEX_STAGE)
                                       .id(MaLiLibReference.MOD_ID+"_pipeline/position_tex_lesser_depth")
                                       .culling(false)
                                       .depth(true)
                                       .depthTestState(DepthTestState.LESS_DEPTH_TEST)
                                       .format(VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS)
                                       .buildPipeline()
                );

        MaLiLibPipelines.POSITION_TEX_EQUAL_DEPTH =
                register(ShaderPipeline.builder(MaLiLibPipelines.POSITION_TEX_STAGE)
                                       .id(MaLiLibReference.MOD_ID+"_pipeline/position_tex_equal_depth")
                                       .culling(false)
                                       .depth(true)
                                       .depthTestState(DepthTestState.EQUAL_DEPTH_TEST)
                                       .format(VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS)
                                       .buildPipeline()
                );

        MaLiLibPipelines.POSITION_TEX_LEQUAL_DEPTH =
                register(ShaderPipeline.builder(MaLiLibPipelines.POSITION_TEX_STAGE)
                                       .id(MaLiLibReference.MOD_ID+"_pipeline/position_tex_lequal_depth")
                                       .culling(false)
                                       .depth(true)
                                       .depthTestState(DepthTestState.LEQUAL_DEPTH_TEST)
                                       .format(VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS)
                                       .buildPipeline()
                );

        MaLiLibPipelines.POSITION_TEX_GREATER_DEPTH =
                register(ShaderPipeline.builder(MaLiLibPipelines.POSITION_TEX_STAGE)
                                       .id(MaLiLibReference.MOD_ID+"_pipeline/position_tex_greater_depth")
                                       .culling(false)
                                       .depth(true)
                                       .depthTestState(DepthTestState.GREATER_DEPTH_TEST)
                                       .format(VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS)
                                       .buildPipeline()
                );

        // POSITION_TEX_COLOR
        MaLiLibPipelines.POSITION_TEX_COLOR_SIMPLE =
                register(ShaderPipeline.builder(POSITION_TEX_COLOR)
                                       .id(MaLiLibReference.MOD_ID+"_pipeline/position_tex_color_simple")
                                       .culling(false)
                                       .depth(false)
                                       .depthTestState(DepthTestState.NO_DEPTH_TEST)
                                       .format(VertexFormats.POSITION_TEXTURE_COLOR, VertexFormat.DrawMode.QUADS)
                                       .buildPipeline()
                );

        MaLiLibPipelines.POSITION_TEX_COLOR_CULLING =
                register(ShaderPipeline.builder(POSITION_TEX_COLOR)
                                       .id(MaLiLibReference.MOD_ID+"_pipeline/position_tex_color_culling")
                                       .culling(true)
                                       .depth(false)
                                       .depthTestState(DepthTestState.NO_DEPTH_TEST)
                                       .format(VertexFormats.POSITION_TEXTURE_COLOR, VertexFormat.DrawMode.QUADS)
                                       .buildPipeline()
                );

        MaLiLibPipelines.POSITION_TEX_COLOR_LESSER_DEPTH =
                register(ShaderPipeline.builder(POSITION_TEX_COLOR)
                                       .id(MaLiLibReference.MOD_ID+"_pipeline/position_tex_color_lesser_depth")
                                       .culling(false)
                                       .depth(true)
                                       .depthTestState(DepthTestState.LESS_DEPTH_TEST)
                                       .format(VertexFormats.POSITION_TEXTURE_COLOR, VertexFormat.DrawMode.QUADS)
                                       .buildPipeline()
                );

        MaLiLibPipelines.POSITION_TEX_COLOR_LEQUAL_DEPTH =
                register(ShaderPipeline.builder(POSITION_TEX_COLOR)
                                       .id(MaLiLibReference.MOD_ID+"_pipeline/position_tex_color_lequal_depth")
                                       .culling(false)
                                       .depth(true)
                                       .depthTestState(DepthTestState.LEQUAL_DEPTH_TEST)
                                       .format(VertexFormats.POSITION_TEXTURE_COLOR, VertexFormat.DrawMode.QUADS)
                                       .buildPipeline()
                );

        MaLiLibPipelines.POSITION_TEX_COLOR_EQUAL_DEPTH =
                register(ShaderPipeline.builder(POSITION_TEX_COLOR)
                                       .id(MaLiLibReference.MOD_ID+"_pipeline/position_tex_color_equal_depth")
                                       .culling(false)
                                       .depth(true)
                                       .depthTestState(DepthTestState.EQUAL_DEPTH_TEST)
                                       .format(VertexFormats.POSITION_TEXTURE_COLOR, VertexFormat.DrawMode.QUADS)
                                       .buildPipeline()
                );

        MaLiLibPipelines.POSITION_TEX_COLOR_GREATER_DEPTH =
                register(ShaderPipeline.builder(POSITION_TEX_COLOR)
                                       .id(MaLiLibReference.MOD_ID+"_pipeline/position_tex_color_greater_depth")
                                       .culling(false)
                                       .depth(true)
                                       .depthTestState(DepthTestState.GREATER_DEPTH_TEST)
                                       .format(VertexFormats.POSITION_TEXTURE_COLOR, VertexFormat.DrawMode.QUADS)
                                       .buildPipeline()
                );

        // LINES
        MaLiLibPipelines.LINES_SIMPLE =
                register(ShaderPipeline.builder(MaLiLibPipelines.LINES_STAGE)
                                       .id(MaLiLibReference.MOD_ID+"_pipeline/lines_simple")
                                       .culling(false)
                                       .depth(false)
                                       .depthTestState(DepthTestState.NO_DEPTH_TEST)
                                       .buildPipeline()
                );

//        MaLiLibPipelines.LINES_CULLING =
//                register(ShaderPipeline.builder(MaLiLibPipelines.LINES_STAGE)
//                                       .id(MaLiLibReference.MOD_ID+"_pipeline/lines_culling")
//                                       .culling(true)
//                                       .depth(false)
//                                       .depthTestState(DepthTestState.NO_DEPTH_TEST)
//                                       .buildPipeline()
//                );
//
//        MaLiLibPipelines.LINES_LESSER_DEPTH =
//                register(ShaderPipeline.builder(MaLiLibPipelines.LINES_STAGE)
//                                       .id(MaLiLibReference.MOD_ID+"_pipeline/lines_lesser_depth")
//                                       .culling(false)
//                                       .depth(true)
//                                       .depthTestState(DepthTestState.LESS_DEPTH_TEST)
//                                       .buildPipeline()
//                );
//
//        MaLiLibPipelines.LINES_EQUAL_DEPTH =
//                register(ShaderPipeline.builder(MaLiLibPipelines.LINES_STAGE)
//                                       .id(MaLiLibReference.MOD_ID+"_pipeline/lines_equal_depth")
//                                       .culling(false)
//                                       .depth(true)
//                                       .depthTestState(DepthTestState.EQUAL_DEPTH_TEST)
//                                       .buildPipeline()
//                );
//
//        MaLiLibPipelines.LINES_LEQUAL_DEPTH =
//                register(ShaderPipeline.builder(MaLiLibPipelines.LINES_STAGE)
//                                       .id(MaLiLibReference.MOD_ID+"_pipeline/lines_lequal_depth")
//                                       .culling(false)
//                                       .depth(true)
//                                       .depthTestState(DepthTestState.LEQUAL_DEPTH_TEST)
//                                       .buildPipeline()
//                );
//
//        MaLiLibPipelines.LINES_GREATER_DEPTH =
//                register(ShaderPipeline.builder(MaLiLibPipelines.LINES_STAGE)
//                                       .id(MaLiLibReference.MOD_ID+"_pipeline/lines_greater_depth")
//                                       .culling(false)
//                                       .depth(true)
//                                       .depthTestState(DepthTestState.GREATER_DEPTH_TEST)
//                                       .buildPipeline()
//                );
//
//        // DEBUG_LINES
        MaLiLibPipelines.DEBUG_LINES_SIMPLE =
                register(ShaderPipeline.builder(MATRICES_COLOR)
                                       .id(MaLiLibReference.MOD_ID+"_pipeline/debug_lines_simple")
                                       .vertexShader("core/position_color")
                                       .fragmentShader("core/position_color")
                                       .culling(false)
                                       .depth(false)
                                       .depthTestState(DepthTestState.NO_DEPTH_TEST)
                                       .format(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.DEBUG_LINE_STRIP)
                                       .blender(BlendPrograms.TRANSLUCENT)
                                       .buildPipeline()
                );

//        MaLiLibPipelines.DEBUG_LINES_CULLING =
//                register(ShaderPipeline.builder(MATRICES_COLOR)
//                                       .id(MaLiLibReference.MOD_ID+"_pipeline/debug_lines_culling")
//                                       .vertexShader("core/position_color")
//                                       .fragmentShader("core/position_color")
//                                       .culling(true)
//                                       .depth(false)
//                                       .depthTestState(DepthTestState.NO_DEPTH_TEST)
//                                       .format(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.DEBUG_LINE_STRIP)
//                                       .blender(BlendPrograms.TRANSLUCENT)
//                                       .buildPipeline()
//                );
//
//        MaLiLibPipelines.DEBUG_LINES_LESSER_DEPTH =
//                register(ShaderPipeline.builder(MATRICES_COLOR)
//                                       .id(MaLiLibReference.MOD_ID+"_pipeline/debug_lines_lesser_depth")
//                                       .vertexShader("core/position_color")
//                                       .fragmentShader("core/position_color")
//                                       .culling(false)
//                                       .depth(true)
//                                       .depthTestState(DepthTestState.LESS_DEPTH_TEST)
//                                       .format(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.DEBUG_LINE_STRIP)
//                                       .blender(BlendPrograms.TRANSLUCENT)
//                                       .buildPipeline()
//                );
//
//        MaLiLibPipelines.DEBUG_LINES_LEQUAL_DEPTH =
//                register(ShaderPipeline.builder(MATRICES_COLOR)
//                                       .id(MaLiLibReference.MOD_ID+"_pipeline/debug_lines_lequal_depth")
//                                       .vertexShader("core/position_color")
//                                       .fragmentShader("core/position_color")
//                                       .culling(false)
//                                       .depth(true)
//                                       .depthTestState(DepthTestState.LEQUAL_DEPTH_TEST)
//                                       .format(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.DEBUG_LINE_STRIP)
//                                       .blender(BlendPrograms.TRANSLUCENT)
//                                       .buildPipeline()
//                );
//
//        MaLiLibPipelines.DEBUG_LINES_EQUAL_DEPTH =
//                register(ShaderPipeline.builder(MATRICES_COLOR)
//                                       .id(MaLiLibReference.MOD_ID+"_pipeline/debug_lines_equal_depth")
//                                       .vertexShader("core/position_color")
//                                       .fragmentShader("core/position_color")
//                                       .culling(false)
//                                       .depth(true)
//                                       .depthTestState(DepthTestState.EQUAL_DEPTH_TEST)
//                                       .format(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.DEBUG_LINE_STRIP)
//                                       .blender(BlendPrograms.TRANSLUCENT)
//                                       .buildPipeline()
//                );
//
//        MaLiLibPipelines.DEBUG_LINES_GREATER_DEPTH =
//                register(ShaderPipeline.builder(MATRICES_COLOR)
//                                       .id(MaLiLibReference.MOD_ID+"_pipeline/debug_lines_greater_depth")
//                                       .vertexShader("core/position_color")
//                                       .fragmentShader("core/position_color")
//                                       .culling(false)
//                                       .depth(true)
//                                       .depthTestState(DepthTestState.GREATER_DEPTH_TEST)
//                                       .format(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.DEBUG_LINE_STRIP)
//                                       .blender(BlendPrograms.TRANSLUCENT)
//                                       .buildPipeline()
//                );
    }
}
