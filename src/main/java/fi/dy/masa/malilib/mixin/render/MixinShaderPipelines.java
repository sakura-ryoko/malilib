package fi.dy.masa.malilib.mixin.render;

import java.util.Map;

import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

    @Unique
    private static final BlendPrograms BLENDER = new BlendPrograms(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);

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
                ShaderPipeline.builder(MATRICES)
                              .vertices("core/position")
                              .pass("core/position")
                              .blender(BlendPrograms.TRANSLUCENT)
                              .format(VertexFormats.POSITION, VertexFormat.DrawMode.QUADS)
                              .build();

        MaLiLibPipelines.POSITION_TEX_STAGE =
                ShaderPipeline.builder(MATRICES)
                              .vertices("core/position_tex")
                              .pass("core/position_tex")
                              .blender(BlendPrograms.TRANSLUCENT)
                              .format(VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS)
                              .build();

        MaLiLibPipelines.LINES_STAGE =
        ShaderPipeline.builder(FOG_COLOR)
                      .vertices("core/rendertype_lines")
                      .pass("core/rendertype_lines")
                      .uniform("LineWidth", UniformType.FLOAT)
                      .uniform("ScreenSize", UniformType.VEC2)
                      .blender(BlendPrograms.TRANSLUCENT)
                      .culling(false)
                      .format(VertexFormats.LINES, VertexFormat.DrawMode.LINES)
                      .build();

        // POSITION
        MaLiLibPipelines.POSITION_SIMPLE =
                register(ShaderPipeline.builder(MaLiLibPipelines.POSITION_STAGE)
                                       .id("pipeline/position_simple")
                                       .culling(false)
                                       .depth(false)
                                       .depthTest(DepthTestState.NO_DEPTH_TEST)
                                       .create()
                );

        MaLiLibPipelines.POSITION_DEPTH =
                register(ShaderPipeline.builder(MaLiLibPipelines.POSITION_STAGE)
                                       .id("pipeline/position_depth")
                                       .culling(false)
                                       .depth(true)
                                       .depthTest(DepthTestState.NO_DEPTH_TEST)
                                       .create()
                );

        // POSITION_COLOR
        MaLiLibPipelines.POSITION_COLOR_SIMPLE =
                register(ShaderPipeline.builder(POSITION_COLOR)
                                 .id("pipeline/position_color_simple")
                                 .culling(false)
                                 .depth(false)
                                 .depthTest(DepthTestState.NO_DEPTH_TEST)
                                 .create()
                );

        MaLiLibPipelines.POSITION_COLOR_DEPTH =
                register(ShaderPipeline.builder(POSITION_COLOR)
                                       .id("pipeline/position_color_depth")
                                       .culling(false)
                                       .depth(true)
                                       .depthTest(DepthTestState.NO_DEPTH_TEST)
                                       .create()
                );

        // POSITION_TEX
        MaLiLibPipelines.POSITION_TEX_SIMPLE =
                register(ShaderPipeline.builder(MaLiLibPipelines.POSITION_TEX_STAGE)
                                       .id("pipeline/position_tex_simple")
                                       .culling(false)
                                       .depth(false)
                                       .depthTest(DepthTestState.NO_DEPTH_TEST)
                                       .create()
                );

        MaLiLibPipelines.POSITION_TEX_DEPTH =
                register(ShaderPipeline.builder(MaLiLibPipelines.POSITION_TEX_STAGE)
                                       .id("pipeline/position_tex_depth")
                                       .culling(false)
                                       .depth(true)
                                       .depthTest(DepthTestState.NO_DEPTH_TEST)
                                       .create()
                );

        // POSITION_TEX_COLOR
        MaLiLibPipelines.POSITION_TEX_COLOR_SIMPLE =
                register(ShaderPipeline.builder(POSITION_TEX_COLOR)
                                       .id("pipeline/position_tex_color_simple")
                                       .culling(false)
                                       .depth(false)
                                       .depthTest(DepthTestState.NO_DEPTH_TEST)
                                       .create()
                );

        MaLiLibPipelines.POSITION_TEX_COLOR_DEPTH =
                register(ShaderPipeline.builder(POSITION_TEX_COLOR)
                                       .id("pipeline/position_tex_color_depth")
                                       .culling(false)
                                       .depth(true)
                                       .depthTest(DepthTestState.NO_DEPTH_TEST)
                                       .create()
                );

        // LINES
        MaLiLibPipelines.LINES_SIMPLE =
                register(ShaderPipeline.builder(MaLiLibPipelines.LINES_STAGE)
                                       .id("pipeline/lines_simple")
                                       .culling(false)
                                       .depth(false)
                                       .depthTest(DepthTestState.NO_DEPTH_TEST)
                                       .create()
                );

        MaLiLibPipelines.LINES_DEPTH =
                register(ShaderPipeline.builder(MaLiLibPipelines.LINES_STAGE)
                                       .id("pipeline/lines_depth")
                                       .culling(false)
                                       .depth(true)
                                       .depthTest(DepthTestState.NO_DEPTH_TEST)
                                       .create()
                );

        // DEBUG_LINES
        MaLiLibPipelines.DEBUG_LINES_SIMPLE =
                register(ShaderPipeline.builder(MATRICES_COLOR)
                                       .id("pipeline/debug_lines_simple")
                                       .vertices("core/position_color")
                                       .pass("core/position_color")
                                       .culling(false)
                                       .depth(false)
                                       .depthTest(DepthTestState.NO_DEPTH_TEST)
                                       .format(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.DEBUG_LINE_STRIP)
                                       .blender(BlendPrograms.TRANSLUCENT)
                                       .create()
                );

        MaLiLibPipelines.DEBUG_LINES_DEPTH =
                register(ShaderPipeline.builder(MATRICES_COLOR)
                                       .id("pipeline/debug_lines_depth")
                                       .vertices("core/position_color")
                                       .pass("core/position_color")
                                       .culling(false)
                                       .depth(true)
                                       .depthTest(DepthTestState.NO_DEPTH_TEST)
                                       .format(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.DEBUG_LINE_STRIP)
                                       .blender(BlendPrograms.TRANSLUCENT)
                                       .create()
                );
    }
}
