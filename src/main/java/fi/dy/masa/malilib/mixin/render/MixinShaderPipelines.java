package fi.dy.masa.malilib.mixin.render;

import java.util.Map;

import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import net.minecraft.client.gl.BlendPrograms;
import net.minecraft.client.gl.ShaderPipeline;
import net.minecraft.client.gl.ShaderPipelines;
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
    @Shadow @Final private static Map<Identifier, ShaderPipeline> PIPELINES;
    @Shadow @Final private static ShaderPipeline.Stage MATRICES;
    @Shadow @Final private static ShaderPipeline.Stage MATRICES_COLOR;
    @Shadow @Final private static ShaderPipeline.Stage POSITION_COLOR;
    @Shadow @Final private static ShaderPipeline.Stage POSITION_TEX_COLOR;
    @Shadow @Final private static ShaderPipeline.Stage CORE_GUI;

    @Unique
    private static final BlendPrograms BLENDER = new BlendPrograms(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);

    @Shadow
    private static ShaderPipeline register(ShaderPipeline pipeline)
    {
        PIPELINES.put(pipeline.getId(), pipeline);
        return pipeline;
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void malilib_onRegisterPipelines(CallbackInfo ci)
    {
        // POSITION_TEX
        MaLiLibPipelines.POSITION_COLOR_DEPTH_TEST_OFF =
                register(ShaderPipeline.builder(POSITION_COLOR)
                                       .id("pipeline/position_color_depth_off")
                                       .culling(false)
                                       .format(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.DEBUG_LINE_STRIP)
                                       .depth(false)
                                       .blender(BLENDER)
                                       .depthTest(DepthTestState.NO_DEPTH_TEST)
                                       .create()
                );

        MaLiLibPipelines.POSITION_COLOR_DEPTH_TEST_LESSER =
                register(ShaderPipeline.builder(POSITION_COLOR)
                                       .id("pipeline/position_color_depth_equal")
                                       .culling(false)
                                       .format(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.DEBUG_LINE_STRIP)
                                       .depth(false)
                                       .blender(BLENDER)
                                       .depthTest(DepthTestState.LESS_DEPTH_TEST)
                                       .create()
                );

        MaLiLibPipelines.POSITION_COLOR_DEPTH_TEST_EQUAL =
                register(ShaderPipeline.builder(POSITION_COLOR)
                                       .id("pipeline/position_color_depth_equal")
                                       .culling(false)
                                       .format(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.DEBUG_LINE_STRIP)
                                       .depth(false)
                                       .blender(BLENDER)
                                       .depthTest(DepthTestState.EQUAL_DEPTH_TEST)
                                       .create()
                );

        MaLiLibPipelines.POSITION_COLOR_DEPTH_TEST_GREATER =
                register(ShaderPipeline.builder(POSITION_COLOR)
                                       .id("pipeline/position_color_depth_greater")
                                       .culling(false)
                                       .format(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.DEBUG_LINE_STRIP)
                                       .depth(false)
                                       .blender(BLENDER)
                                       .depthTest(DepthTestState.GREATER_DEPTH_TEST)
                                       .create()
                );

        // POSITION_TEX_COLOR
        MaLiLibPipelines.POSITION_TEX_COLOR_DEPTH_TEST_OFF =
                register(ShaderPipeline.builder(POSITION_TEX_COLOR)
                                       .id("pipeline/position_tex_depth_off")
                                       .culling(false)
                                       .format(VertexFormats.POSITION_TEXTURE_COLOR, VertexFormat.DrawMode.DEBUG_LINE_STRIP)
                                       .depth(false)
                                       .blender(BLENDER)
                                       .depthTest(DepthTestState.NO_DEPTH_TEST)
                                       .create()
                );

        MaLiLibPipelines.POSITION_TEX_COLOR_DEPTH_TEST_LESSER =
                register(ShaderPipeline.builder(POSITION_TEX_COLOR)
                                       .id("pipeline/position_tex_depth_lesser")
                                       .culling(false)
                                       .format(VertexFormats.POSITION_TEXTURE_COLOR, VertexFormat.DrawMode.DEBUG_LINE_STRIP)
                                       .depth(false)
                                       .blender(BLENDER)
                                       .depthTest(DepthTestState.LESS_DEPTH_TEST)
                                       .create()
                );

        MaLiLibPipelines.POSITION_TEX_COLOR_DEPTH_TEST_EQUAL =
                register(ShaderPipeline.builder(POSITION_TEX_COLOR)
                                       .id("pipeline/position_tex_depth_equal")
                                       .depth(false)
                                       .blender(BLENDER)
                                       .depthTest(DepthTestState.EQUAL_DEPTH_TEST)
                                       .create()
                );

        MaLiLibPipelines.POSITION_TEX_COLOR_DEPTH_TEST_GREATER =
                register(ShaderPipeline.builder(POSITION_TEX_COLOR)
                                       .id("pipeline/position_tex_depth_greater")
                                       .depth(false)
                                       .blender(BLENDER)
                                       .depthTest(DepthTestState.GREATER_DEPTH_TEST)
                                       .create()
                );

        // DEBUG_LINES
        MaLiLibPipelines.DEBUG_LINES_BLEND_FUNC =
                register(ShaderPipeline.builder(MATRICES_COLOR)
                                       .id("pipeline/debug_lines_depth_off")
                                       .vertices("core/position_color")
                                       .pass("core/position_color")
                                       .culling(false)
                                       .format(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.DEBUG_LINE_STRIP)
                                       .depth(false)
                                       .blender(BLENDER)
                                       .create()
                );

        MaLiLibPipelines.DEBUG_LINES_DEPTH_TEST_OFF =
                register(ShaderPipeline.builder(MATRICES_COLOR)
                                       .id("pipeline/debug_lines_depth_off")
                                       .vertices("core/position_color")
                                       .pass("core/position_color")
                                       .culling(false)
                                       .format(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.DEBUG_LINE_STRIP)
                                       .depth(false)
                                       .blender(BLENDER)
                                       .depthTest(DepthTestState.NO_DEPTH_TEST)
                                       .create()
                );

        MaLiLibPipelines.DEBUG_LINES_DEPTH_TEST_LESSER =
                register(ShaderPipeline.builder(MATRICES_COLOR)
                                       .id("pipeline/debug_lines_depth_lesser")
                                       .vertices("core/position_color")
                                       .pass("core/position_color")
                                       .culling(false)
                                       .format(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.DEBUG_LINE_STRIP)
                                       .depth(false)
                                       .blender(BLENDER)
                                       .depthTest(DepthTestState.LESS_DEPTH_TEST)
                                       .create()
                );

        MaLiLibPipelines.DEBUG_LINES_DEPTH_TEST_EQUAL =
                register(ShaderPipeline.builder(MATRICES_COLOR)
                                       .id("pipeline/debug_lines_depth_equal")
                                       .vertices("core/position_color")
                                       .pass("core/position_color")
                                       .culling(false)
                                       .format(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.DEBUG_LINE_STRIP)
                                       .depth(false)
                                       .blender(BLENDER)
                                       .depthTest(DepthTestState.EQUAL_DEPTH_TEST)
                                       .create()
                );

        MaLiLibPipelines.DEBUG_LINES_DEPTH_TEST_GREATER =
                register(ShaderPipeline.builder(MATRICES_COLOR)
                                       .id("pipeline/debug_lines_depth_greater")
                                       .vertices("core/position_color")
                                       .pass("core/position_color")
                                       .culling(false)
                                       .format(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.DEBUG_LINE_STRIP)
                                       .depth(false)
                                       .blender(BLENDER)
                                       .depthTest(DepthTestState.GREATER_DEPTH_TEST)
                                       .create()
                );

        MaLiLibPipelines.DEBUG_LINES_CULLING_DEPTH_TEST_OFF =
                register(ShaderPipeline.builder(MATRICES_COLOR)
                                       .id("pipeline/debug_lines_depth_off")
                                       .vertices("core/position_color")
                                       .pass("core/position_color")
                                       .culling(true)
                                       .format(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.DEBUG_LINE_STRIP)
                                       .depth(false)
                                       .blender(BLENDER)
                                       .depthTest(DepthTestState.NO_DEPTH_TEST)
                                       .create()
                );

        MaLiLibPipelines.DEBUG_LINES_CULLING_DEPTH_TEST_LESSER =
                register(ShaderPipeline.builder(MATRICES_COLOR)
                                       .id("pipeline/debug_lines_depth_lesser")
                                       .vertices("core/position_color")
                                       .pass("core/position_color")
                                       .culling(true)
                                       .format(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.DEBUG_LINE_STRIP)
                                       .depth(false)
                                       .blender(BLENDER)
                                       .depthTest(DepthTestState.LESS_DEPTH_TEST)
                                       .create()
                );

        MaLiLibPipelines.DEBUG_LINES_CULLING_DEPTH_TEST_EQUAL =
                register(ShaderPipeline.builder(MATRICES_COLOR)
                                       .id("pipeline/debug_lines_depth_equal")
                                       .vertices("core/position_color")
                                       .pass("core/position_color")
                                       .culling(true)
                                       .format(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.DEBUG_LINE_STRIP)
                                       .depth(false)
                                       .blender(BLENDER)
                                       .depthTest(DepthTestState.EQUAL_DEPTH_TEST)
                                       .create()
                );

        MaLiLibPipelines.DEBUG_LINES_CULLING_DEPTH_TEST_GREATER =
                register(ShaderPipeline.builder(MATRICES_COLOR)
                                       .id("pipeline/debug_lines_depth_greater")
                                       .vertices("core/position_color")
                                       .pass("core/position_color")
                                       .culling(true)
                                       .format(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.DEBUG_LINE_STRIP)
                                       .depth(false)
                                       .blender(BLENDER)
                                       .depthTest(DepthTestState.GREATER_DEPTH_TEST)
                                       .create()
                );
    }
}
