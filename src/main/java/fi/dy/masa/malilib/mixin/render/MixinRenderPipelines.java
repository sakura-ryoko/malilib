package fi.dy.masa.malilib.mixin.render;

import java.util.Map;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.PolygonMode;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.compat.iris.IrisCompat;
import fi.dy.masa.malilib.render.MaLiLibPipelines;

@Mixin(value = RenderPipelines.class, priority = 990)
public abstract class MixinRenderPipelines
{
    @Shadow @Final private static Map<Identifier, RenderPipeline> PIPELINES_BY_LOCATION;

    @Shadow @Final private static RenderPipeline.Snippet MATRICES_PROJECTION_SNIPPET;          // TRANSFORMS_AND_PROJECTION_SNIPPET
	@Shadow @Final private static RenderPipeline.Snippet FOG_SNIPPET;                          // FOG
    @Shadow @Final private static RenderPipeline.Snippet GLOBALS_SNIPPET;                      // GLOBALS_SNIPPET
    @Shadow @Final private static RenderPipeline.Snippet MATRICES_FOG_SNIPPET;                 // TRANSFORMS_PROJECTION_FOG_SNIPPET
    @Shadow @Final private static RenderPipeline.Snippet MATRICES_FOG_LIGHT_DIR_SNIPPET;       // TRANSFORMS_PROJECTION_FOG_LIGHTING_SNIPPET
	@Shadow @Final private static RenderPipeline.Snippet GENERIC_BLOCKS_SNIPPET;               // FOG_AND_SAMPLERS_SNIPPET
    @Shadow @Final private static RenderPipeline.Snippet TERRAIN_SNIPPET;                      // TERRAIN
	@Shadow @Final private static RenderPipeline.Snippet BLOCK_SNIPPET;                        // BLOCK
    @Shadow @Final private static RenderPipeline.Snippet ENTITY_SNIPPET;                       // ENTITY
    @Shadow @Final private static RenderPipeline.Snippet ENTITY_EMISSIVE_SNIPPET;              // ENTITY_EMISSIVE_SNIPPET
    @Shadow @Final private static RenderPipeline.Snippet BEACON_BEAM_SNIPPET;                  // RENDERTYPE_BEACON_BEAM
    @Shadow @Final private static RenderPipeline.Snippet TEXT_SNIPPET;                         // TEXT
    @Shadow @Final private static RenderPipeline.Snippet END_PORTAL_SNIPPET;                   // RENDERTYPE_END_PORTAL
    @Shadow @Final private static RenderPipeline.Snippet CLOUDS_SNIPPET;                       // RENDERTYPE_CLOUDS
    @Shadow @Final private static RenderPipeline.Snippet LINES_SNIPPET;                        // RENDERTYPE_LINES
    @Shadow @Final private static RenderPipeline.Snippet DEBUG_FILLED_SNIPPET;                 // DEBUG_FILLED
    @Shadow @Final private static RenderPipeline.Snippet PARTICLE_SNIPPET;                     // PARTICLE_TEX
    @Shadow @Final private static RenderPipeline.Snippet WEATHER_SNIPPET;                      // WEATHER
    @Shadow @Final private static RenderPipeline.Snippet GUI_SNIPPET;                          // GUI
    @Shadow @Final private static RenderPipeline.Snippet GUI_TEXTURED_SNIPPET;                 // GUI_TEXTURED
	@Shadow @Final private static RenderPipeline.Snippet GUI_TEXT_SNIPPET;            		   // GUI_TEXT
    @Shadow @Final private static RenderPipeline.Snippet OUTLINE_SNIPPET;                      // RENDERTYPE_OUTLINE
    @Shadow @Final public  static RenderPipeline.Snippet POST_PROCESSING_SNIPPET;              // POST_PROCESSOR

    @Unique private static final BlendFunction MASA_BLEND = new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
    @Unique private static final BlendFunction MASA_BLEND_SIMPLE = new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

    @Shadow
    private static RenderPipeline register(RenderPipeline renderPipeline)
    {
        PIPELINES_BY_LOCATION.put(renderPipeline.getLocation(), renderPipeline);
        return renderPipeline;
    }

	@Unique
	private static Identifier getId(String id)
	{
		return Identifier.fromNamespaceAndPath(MaLiLibReference.MOD_ID, id);
	}

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void malilib_onRegisterPipelines(CallbackInfo ci)
    {
        // todo POSITION
	    MaLiLibPipelines.POSITION_STAGE =
			    RenderPipeline.builder(MATRICES_FOG_SNIPPET)
			                  .withVertexShader(getId("int_position"))
			                  .withFragmentShader(getId("int_position"))
			                  .withVertexFormat(DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS)
			                  .buildSnippet();

	    MaLiLibPipelines.POSITION_TRANSLUCENT_STAGE =
                RenderPipeline.builder(MaLiLibPipelines.POSITION_STAGE)
                              .withBlend(BlendFunction.TRANSLUCENT)
                              .buildSnippet();

        MaLiLibPipelines.POSITION_MASA_STAGE =
		        RenderPipeline.builder(MaLiLibPipelines.POSITION_STAGE)
                              .withBlend(MASA_BLEND)
                              .buildSnippet();

		// todo POSITION_COLOR Snippet
	    MaLiLibPipelines.POSITION_COLOR_STAGE =
			    RenderPipeline.builder(MATRICES_PROJECTION_SNIPPET)
			                  .withVertexShader(getId("int_position_color"))
			                  .withFragmentShader(getId("int_position_color"))
			                  .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
			                  .buildSnippet();

	    MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_STAGE =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_STAGE)
			                  .withBlend(BlendFunction.TRANSLUCENT)
			                  .buildSnippet();

	    MaLiLibPipelines.POSITION_COLOR_MASA_STAGE =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_STAGE)
			                  .withBlend(MASA_BLEND)
			                  .buildSnippet();

	    // todo POSITION_COLOR_TRANSLUCENT
	    MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_NO_DEPTH_NO_CULL =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_STAGE)
			                  .withLocation(getId("pipeline/position_color/translucent/no_depth/no_cull"))
			                  .withCull(false)
			                  .withDepthWrite(false)
			                  .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_NO_DEPTH =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_STAGE)
			                  .withLocation(getId("pipeline/position_color/translucent/no_depth"))
			                  .withDepthWrite(false)
			                  .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_1 =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_STAGE)
			                  .withLocation(getId("pipeline/position_color/translucent/lequal_depth/offset_1"))
			                  .withCull(false)
			                  .withDepthWrite(false)
			                  .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
			                  .withDepthBias(-0.3f, -0.6f)
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_2 =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_STAGE)
			                  .withLocation(getId("pipeline/position_color/translucent/lequal_depth/offset_2"))
			                  .withCull(false)
			                  .withDepthWrite(false)
			                  .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
			                  .withDepthBias(-0.4f, -0.8f)
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_3 =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_STAGE)
			                  .withLocation(getId("pipeline/position_color/translucent/lequal_depth/offset_3"))
			                  .withCull(false)
			                  .withDepthWrite(false)
			                  .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
			                  .withDepthBias(-3f, -3f)
			                  .build();

//        MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_4 =
//                RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_STAGE)
//                              .withLocation(getId("pipeline/position_color/translucent/lequal_depth/offset_4"))
//                              .withCull(false)
//                              .withDepthWrite(false)
//                              .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
//                              .withDepthBias(-0.6f, -1.2f)
//                              .build();

	    MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_LEQUAL_DEPTH_NO_CULL =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_STAGE)
			                  .withLocation(getId("pipeline/position_color/translucent/lequal_depth/no_cull"))
			                  .withCull(false)
			                  .withDepthWrite(false)
			                  .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_LEQUAL_DEPTH =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_STAGE)
			                  .withLocation(getId("pipeline/position_color/translucent/lequal_depth"))
			                  .withDepthWrite(false)
			                  .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
			                  .build();

//	    MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_LESS_DEPTH =
//			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_STAGE)
//			                  .withLocation(getId("pipeline/position_color/translucent/less_depth"))
//			                  .withDepthWrite(false)
//			                  .withDepthTestFunction(DepthTestFunction.LESS_DEPTH_TEST)
//			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_GREATER_DEPTH =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_STAGE)
			                  .withLocation(getId("pipeline/position_color/translucent/greater_depth"))
			                  .withDepthWrite(false)
			                  .withDepthTestFunction(DepthTestFunction.GREATER_DEPTH_TEST)
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_DEPTH_MASK =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_STAGE)
			                  .withLocation(getId("pipeline/position_color/translucent/depth_mask"))
			                  .withDepthWrite(true)
			                  .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_STAGE)
			                  .withLocation(getId("pipeline/position_color/translucent"))
			                  .build();

	    // todo POSITION_COLOR_MASA
	    MaLiLibPipelines.POSITION_COLOR_MASA_NO_DEPTH_NO_CULL =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_MASA_STAGE)
			                  .withLocation(getId("pipeline/position_color/masa/no_depth/no_cull"))
			                  .withCull(false)
			                  .withDepthWrite(false)
			                  .withColorWrite(true)
			                  .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_MASA_NO_DEPTH =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_MASA_STAGE)
			                  .withLocation(getId("pipeline/position_color/masa/no_depth"))
			                  .withDepthWrite(false)
			                  .withColorWrite(true)
			                  .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_MASA_LEQUAL_DEPTH_OFFSET_1 =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_MASA_STAGE)
			                  .withLocation(getId("pipeline/position_color/masa/lequal_depth/offset_1"))
			                  .withCull(false)
			                  .withDepthWrite(false)
			                  .withColorWrite(true)
			                  .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
			                  .withDepthBias(-0.3f, -0.6f)
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_MASA_LEQUAL_DEPTH_OFFSET_2 =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_MASA_STAGE)
			                  .withLocation(getId("pipeline/position_color/masa/lequal_depth/offset_2"))
			                  .withCull(false)
			                  .withDepthWrite(false)
			                  .withColorWrite(true)
			                  .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
			                  .withDepthBias(-0.4f, -0.8f)
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_MASA_LEQUAL_DEPTH_OFFSET_3 =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_MASA_STAGE)
			                  .withLocation(getId("pipeline/position_color/masa/lequal_depth/offset_3"))
			                  .withCull(false)
			                  .withDepthWrite(false)
			                  .withColorWrite(true)
			                  .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
			                  .withDepthBias(-3f, -3f)
			                  .build();

//        MaLiLibPipelines.POSITION_COLOR_MASA_LEQUAL_DEPTH_OFFSET_4 =
//                RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_MASA_STAGE)
//                              .withLocation(getId("pipeline/position_color/masa/lequal_depth/offset_4"))
//                              .withCull(false)
//                              .withDepthWrite(false)
//                              .withColorWrite(true)
//                              .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
//                              .withDepthBias(-0.6f, -1.2f)
//                              .build();

	    MaLiLibPipelines.POSITION_COLOR_MASA_LEQUAL_DEPTH_NO_CULL =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_MASA_STAGE)
			                  .withLocation(getId("pipeline/position_color/masa/lequal_depth/no_cull"))
			                  .withCull(false)
			                  .withDepthWrite(false)
			                  .withColorWrite(true)
			                  .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_MASA_LEQUAL_DEPTH =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_MASA_STAGE)
			                  .withLocation(getId("pipeline/position_color/masa/lequal_depth"))
			                  .withDepthWrite(false)
			                  .withColorWrite(true)
			                  .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
			                  .build();

//	    MaLiLibPipelines.POSITION_COLOR_MASA_LESS_DEPTH =
//			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_MASA_STAGE)
//			                  .withLocation(getId("pipeline/position_color/masa/less_depth"))
//			                  .withDepthWrite(false)
//			                  .withColorWrite(true)
//			                  .withDepthTestFunction(DepthTestFunction.LESS_DEPTH_TEST)
//			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_MASA_GREATER_DEPTH =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_MASA_STAGE)
			                  .withLocation(getId("pipeline/position_color/masa/greater_depth"))
			                  .withDepthWrite(false)
			                  .withColorWrite(true)
			                  .withDepthTestFunction(DepthTestFunction.GREATER_DEPTH_TEST)
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_MASA_DEPTH_MASK =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_MASA_STAGE)
			                  .withLocation(getId("pipeline/position_color/masa/depth_mask"))
			                  .withDepthWrite(true)
			                  .withColorWrite(true)
			                  .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_MASA =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_MASA_STAGE)
			                  .withLocation(getId("pipeline/position_color/masa"))
			                  .withDepthWrite(false)
			                  .withColorWrite(true)
			                  .build();

	    MaLiLibPipelines.TEXT_PLATE_MASA_NO_DEPTH =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_MASA_STAGE)
			                  .withLocation(getId("pipeline/text_plate/no_depth"))
			                  .withCull(false)
			                  .withColorWrite(true)
			                  .withDepthWrite(false)
			                  .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			                  .build();

	    MaLiLibPipelines.TEXT_PLATE_MASA =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_MASA_STAGE)
			                  .withLocation(getId("pipeline/text_plate"))
					          .withCull(false)
			                  .withColorWrite(true)
			                  .build();

	    // todo MINIHUD_SHAPE
	    MaLiLibPipelines.MINIHUD_SHAPE_NO_DEPTH_OFFSET =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_MASA_STAGE)
			                  .withLocation(getId("pipeline/minihud/shape/no_depth/offset"))
			                  .withDepthBias(-3.0f, -3.0f)
			                  .withCull(false)
			                  .withDepthWrite(false)
			                  .withColorWrite(true)
			                  .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			                  .build();

	    MaLiLibPipelines.MINIHUD_SHAPE_NO_DEPTH =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_MASA_STAGE)
			                  .withLocation(getId("pipeline/minihud/shape/no_depth"))
			                  .withCull(false)
			                  .withDepthWrite(false)
			                  .withColorWrite(true)
			                  .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			                  .build();

	    MaLiLibPipelines.MINIHUD_SHAPE_OFFSET_NO_CULL =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_MASA_STAGE)
			                  .withLocation(getId("pipeline/minihud/shape/offset/no_cull"))
			                  .withDepthBias(-3.0f, -3.0f)
			                  .withCull(false)
			                  .withDepthWrite(false)
			                  .withColorWrite(true)
			                  .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
			                  .build();

	    MaLiLibPipelines.MINIHUD_SHAPE_OFFSET =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_MASA_STAGE)
			                  .withLocation(getId("pipeline/minihud/shape/offset"))
			                  .withDepthBias(-3.0f, -3.0f)
			                  .withDepthWrite(false)
			                  .withColorWrite(true)
			                  .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
			                  .build();

	    MaLiLibPipelines.MINIHUD_SHAPE_DEPTH_MASK =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_MASA_STAGE)
			                  .withLocation(getId("pipeline/minihud/shape/depth_mask"))
			                  .withDepthWrite(true)
			                  .withColorWrite(true)
			                  .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			                  .build();

	    MaLiLibPipelines.MINIHUD_SHAPE_NO_CULL =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_MASA_STAGE)
			                  .withLocation(getId("pipeline/minihud/shape/no_cull"))
			                  .withCull(false)
			                  .withDepthWrite(false)
			                  .withColorWrite(true)
			                  .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
			                  .build();

	    MaLiLibPipelines.MINIHUD_SHAPE =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_MASA_STAGE)
			                  .withLocation(getId("pipeline/minihud/shape"))
			                  .withDepthWrite(false)
			                  .withColorWrite(true)
			                  .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
			                  .build();

	    // todo POSITION_COLOR_LINES Snippet
	    MaLiLibPipelines.POSITION_COLOR_LINES_STAGE =
			    RenderPipeline.builder(MATRICES_PROJECTION_SNIPPET)
                              .withVertexShader(getId("position_color_lines"))
                              .withFragmentShader(getId("position_color_lines"))
			                  .withVertexFormat(DefaultVertexFormat.POSITION_COLOR_LINE_WIDTH, VertexFormat.Mode.LINES)
			                  .buildSnippet();

	    MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_STAGE =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_STAGE)
			                  .withBlend(BlendFunction.TRANSLUCENT)
			                  .buildSnippet();

	    MaLiLibPipelines.POSITION_COLOR_LINES_MASA_STAGE =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_STAGE)
			                  .withBlend(MASA_BLEND)
			                  .buildSnippet();

	    // todo POSITION_COLOR_LINES_TRANSLUCENT
	    MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_NO_DEPTH_NO_CULL =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_STAGE)
			                  .withLocation(getId("pipeline/position_color_lines/translucent/no_depth/no_cull"))
			                  .withCull(false)
			                  .withDepthWrite(false)
			                  .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_NO_DEPTH =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_STAGE)
			                  .withLocation(getId("pipeline/position_color_lines/translucent/no_depth"))
			                  .withDepthWrite(false)
			                  .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_1 =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_STAGE)
			                  .withLocation(getId("pipeline/position_color_lines/translucent/lequal_depth/offset_1"))
			                  .withCull(false)
			                  .withDepthWrite(false)
			                  .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
			                  .withDepthBias(-0.3f, -0.6f)
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_2 =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_STAGE)
			                  .withLocation(getId("pipeline/position_color_lines/translucent/lequal_depth/offset_2"))
			                  .withCull(false)
			                  .withDepthWrite(false)
			                  .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
			                  .withDepthBias(-0.4f, -0.8f)
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_3 =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_STAGE)
			                  .withLocation(getId("pipeline/position_color_lines/translucent/lequal_depth/offset_3"))
			                  .withCull(false)
			                  .withDepthWrite(false)
			                  .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
			                  .withDepthBias(-3f, -3f)
			                  .build();

//        MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_4 =
//                RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_STAGE)
//                              .withLocation(getId("pipeline/position_color_lines/translucent/lequal_depth/offset_4"))
//                              .withCull(false)
//                              .withDepthWrite(false)
//                              .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
//                              .withDepthBias(-0.6f, -1.2f)
//                              .build();

	    MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_LEQUAL_DEPTH_NO_CULL =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_STAGE)
			                  .withLocation(getId("pipeline/position_color_lines/translucent/lequal_depth/no_cull"))
			                  .withCull(false)
			                  .withDepthWrite(false)
			                  .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_LEQUAL_DEPTH =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_STAGE)
			                  .withLocation(getId("pipeline/position_color_lines/translucent/lequal_depth"))
			                  .withDepthWrite(false)
			                  .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
			                  .build();

//	    MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_LESS_DEPTH =
//			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_STAGE)
//			                  .withLocation(getId("pipeline/position_color_lines/translucent/less_depth"))
//			                  .withDepthWrite(false)
//			                  .withDepthTestFunction(DepthTestFunction.LESS_DEPTH_TEST)
//			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_GREATER_DEPTH =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_STAGE)
			                  .withLocation(getId("pipeline/position_color_lines/translucent/greater_depth"))
			                  .withDepthWrite(false)
			                  .withDepthTestFunction(DepthTestFunction.GREATER_DEPTH_TEST)
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_DEPTH_MASK =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_STAGE)
			                  .withLocation(getId("pipeline/position_color_lines/translucent/depth_mask"))
			                  .withDepthWrite(true)
			                  .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_STAGE)
			                  .withLocation(getId("pipeline/position_color_lines/translucent"))
			                  .build();

	    // todo POSITION_COLOR_LINES_MASA
	    MaLiLibPipelines.POSITION_COLOR_LINES_MASA_NO_DEPTH_NO_CULL =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_MASA_STAGE)
			                  .withLocation(getId("pipeline/position_color_lines/masa/no_depth/no_cull"))
			                  .withCull(false)
			                  .withDepthWrite(false)
			                  .withColorWrite(true)
			                  .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_LINES_MASA_NO_DEPTH =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_MASA_STAGE)
			                  .withLocation(getId("pipeline/position_color_lines/masa/no_depth"))
			                  .withDepthWrite(false)
			                  .withColorWrite(true)
			                  .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_LINES_MASA_LEQUAL_DEPTH_OFFSET_1 =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_MASA_STAGE)
			                  .withLocation(getId("pipeline/position_color_lines/masa/lequal_depth/offset_1"))
			                  .withCull(false)
			                  .withDepthWrite(false)
			                  .withColorWrite(true)
			                  .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
			                  .withDepthBias(-0.3f, -0.6f)
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_LINES_MASA_LEQUAL_DEPTH_OFFSET_2 =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_MASA_STAGE)
			                  .withLocation(getId("pipeline/position_color_lines/masa/lequal_depth/offset_2"))
			                  .withCull(false)
			                  .withDepthWrite(false)
			                  .withColorWrite(true)
			                  .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
			                  .withDepthBias(-0.4f, -0.8f)
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_LINES_MASA_LEQUAL_DEPTH_OFFSET_3 =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_MASA_STAGE)
			                  .withLocation(getId("pipeline/position_color_lines/masa/lequal_depth/offset_3"))
			                  .withCull(false)
			                  .withDepthWrite(false)
			                  .withColorWrite(true)
			                  .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
			                  .withDepthBias(-3f, -3f)
			                  .build();

//        MaLiLibPipelines.POSITION_COLOR_LINES_MASA_LEQUAL_DEPTH_OFFSET_4 =
//                RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_MASA_STAGE)
//                              .withLocation(getId("pipeline/position_color_lines/masa/lequal_depth/offset_4"))
//                              .withCull(false)
//                              .withDepthWrite(false)
//                              .withColorWrite(true)
//                              .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
//                              .withDepthBias(-0.6f, -1.2f)
//                              .build();

	    MaLiLibPipelines.POSITION_COLOR_LINES_MASA_LEQUAL_DEPTH_NO_CULL =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_MASA_STAGE)
			                  .withLocation(getId("pipeline/position_color_lines/masa/lequal_depth/no_cull"))
			                  .withCull(false)
			                  .withDepthWrite(false)
			                  .withColorWrite(true)
			                  .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_LINES_MASA_LEQUAL_DEPTH =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_MASA_STAGE)
			                  .withLocation(getId("pipeline/position_color_lines/masa/lequal_depth"))
			                  .withDepthWrite(false)
			                  .withColorWrite(true)
			                  .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
			                  .build();

//	    MaLiLibPipelines.POSITION_COLOR_LINES_MASA_LESS_DEPTH =
//			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_MASA_STAGE)
//			                  .withLocation(getId("pipeline/position_color_lines/masa/less_depth"))
//			                  .withDepthWrite(false)
//			                  .withColorWrite(true)
//			                  .withDepthTestFunction(DepthTestFunction.LESS_DEPTH_TEST)
//			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_LINES_MASA_GREATER_DEPTH =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_MASA_STAGE)
			                  .withLocation(getId("pipeline/position_color_lines/masa/greater_depth"))
			                  .withDepthWrite(false)
			                  .withColorWrite(true)
			                  .withDepthTestFunction(DepthTestFunction.GREATER_DEPTH_TEST)
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_LINES_MASA_DEPTH_MASK =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_MASA_STAGE)
			                  .withLocation(getId("pipeline/position_color_lines/masa/depth_mask"))
			                  .withDepthWrite(true)
			                  .withColorWrite(true)
			                  .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_LINES_MASA =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_MASA_STAGE)
			                  .withLocation(getId("pipeline/position_color_lines/masa"))
			                  .withDepthWrite(false)
			                  .withColorWrite(true)
			                  .build();

	    // todo MINIHUD_SHAPE_LINES
	    MaLiLibPipelines.MINIHUD_SHAPE_LINES_NO_DEPTH_OFFSET =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_MASA_STAGE)
			                  .withLocation(getId("pipeline/minihud/shape_lines/no_depth/offset"))
			                  .withDepthBias(-3.0f, -3.0f)
			                  .withCull(false)
			                  .withDepthWrite(false)
			                  .withColorWrite(true)
			                  .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			                  .build();

	    MaLiLibPipelines.MINIHUD_SHAPE_LINES_NO_DEPTH =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_MASA_STAGE)
			                  .withLocation(getId("pipeline/minihud/shape_lines/no_depth"))
			                  .withCull(false)
			                  .withDepthWrite(false)
			                  .withColorWrite(true)
			                  .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			                  .build();

	    MaLiLibPipelines.MINIHUD_SHAPE_LINES_OFFSET_NO_CULL =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_MASA_STAGE)
			                  .withLocation(getId("pipeline/minihud/shape_lines/offset/no_cull"))
			                  .withDepthBias(-3.0f, -3.0f)
			                  .withCull(false)
			                  .withDepthWrite(false)
			                  .withColorWrite(true)
			                  .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
			                  .build();

	    MaLiLibPipelines.MINIHUD_SHAPE_LINES_OFFSET =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_MASA_STAGE)
			                  .withLocation(getId("pipeline/minihud/shape_lines/offset"))
			                  .withDepthBias(-3.0f, -3.0f)
			                  .withDepthWrite(false)
			                  .withColorWrite(true)
			                  .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
			                  .build();

	    MaLiLibPipelines.MINIHUD_SHAPE_LINES_DEPTH_MASK =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_MASA_STAGE)
			                  .withLocation(getId("pipeline/minihud/shape_lines/depth_mask"))
			                  .withDepthWrite(true)
			                  .withColorWrite(true)
			                  .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			                  .build();

	    MaLiLibPipelines.MINIHUD_SHAPE_LINES_NO_CULL =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_MASA_STAGE)
			                  .withLocation(getId("pipeline/minihud/shape_lines/no_cull"))
			                  .withCull(false)
			                  .withDepthWrite(false)
			                  .withColorWrite(true)
			                  .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
			                  .build();

	    MaLiLibPipelines.MINIHUD_SHAPE_LINES =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_MASA_STAGE)
			                  .withLocation(getId("pipeline/minihud/shape_lines"))
			                  .withDepthWrite(false)
			                  .withColorWrite(true)
			                  .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
			                  .build();

	    // todo POSITION_TEX Snippet
	    MaLiLibPipelines.POSITION_TEX_STAGE =
			    RenderPipeline.builder(MATRICES_PROJECTION_SNIPPET)
			                  .withVertexShader(getId("int_position_tex"))
			                  .withFragmentShader(getId("int_position_tex"))
			                  .withSampler("Sampler0")
			                  .withVertexFormat(DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS)
			                  .buildSnippet();

	    MaLiLibPipelines.POSITION_TEX_TRANSLUCENT_STAGE =
                RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_STAGE)
                              .withBlend(BlendFunction.TRANSLUCENT)
                              .buildSnippet();

        MaLiLibPipelines.POSITION_TEX_OVERLAY_STAGE =
                RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_STAGE)
                              .withBlend(BlendFunction.OVERLAY)
                              .buildSnippet();

        MaLiLibPipelines.POSITION_TEX_MASA_STAGE =
                RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_STAGE)
                              .withBlend(MASA_BLEND)
                              .buildSnippet();

	    // todo POSITION_TEX_COLOR
	    MaLiLibPipelines.POSITION_TEX_COLOR_STAGE =
			    RenderPipeline.builder(MATRICES_PROJECTION_SNIPPET)
			                  .withVertexShader(getId("int_position_tex_color"))
			                  .withFragmentShader(getId("int_position_tex_color"))
			                  .withSampler("Sampler0")
			                  .withVertexFormat(DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS)
			                  .buildSnippet();

	    MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_STAGE =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_STAGE)
			                  .withBlend(BlendFunction.TRANSLUCENT)
			                  .buildSnippet();

        MaLiLibPipelines.POSITION_TEX_COLOR_OVERLAY_STAGE =
                RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_STAGE)
                              .withBlend(BlendFunction.OVERLAY)
                              .buildSnippet();

	    MaLiLibPipelines.POSITION_TEX_COLOR_MASA_STAGE =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_STAGE)
			                  .withBlend(MASA_BLEND)
			                  .buildSnippet();

	    // todo POSITION_TEX_COLOR_TRANSLUCENT
        MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_NO_DEPTH_NO_CULL =
                RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/position_tex_color/translucent/no_depth/no_cull"))
                              .withCull(false)
                              .withDepthWrite(false)
                              .withColorWrite(true)
                              .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                              .build();

        MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_NO_DEPTH =
                RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/position_tex_color/translucent/no_depth"))
                              .withDepthWrite(false)
                              .withColorWrite(true)
                              .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                              .build();

        MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_1 =
                RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/position_tex_color/translucent/lequal_depth/offset_1"))
                              .withDepthWrite(false)
                              .withColorWrite(true)
                              .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
                              .withDepthBias(-0.3f, -0.6f)
                              .build();

        MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_2 =
                RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/position_tex_color/translucent/lequal_depth/offset_2"))
                              .withDepthWrite(false)
                              .withColorWrite(true)
                              .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
                              .withDepthBias(-0.4f, -0.8f)
                              .build();

        MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_3 =
                RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/position_tex_color/translucent/lequal_depth/offset_3"))
                              .withDepthWrite(false)
                              .withColorWrite(true)
                              .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
                              .withDepthBias(-3f, -3f)
                              .build();

	    MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_LEQUAL_DEPTH_NO_CULL =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_STAGE)
			                  .withLocation(getId("pipeline/position_tex_color/translucent/lequal_depth/no_cull"))
					          .withCull(false)
			                  .withDepthWrite(false)
			                  .withColorWrite(true)
			                  .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
			                  .build();

	    MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_LEQUAL_DEPTH =
                RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/position_tex_color/translucent/lequal_depth"))
                              .withDepthWrite(false)
                              .withColorWrite(true)
                              .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
                              .build();

        MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_GREATER_DEPTH =
                RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/position_tex_color/translucent/greater_depth"))
                              .withDepthWrite(false)
                              .withColorWrite(true)
                              .withDepthTestFunction(DepthTestFunction.GREATER_DEPTH_TEST)
                              .build();

        MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_DEPTH_MASK =
                RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/position_tex_color/translucent/depth_mask"))
                              .withDepthWrite(true)
                              .withColorWrite(true)
                              .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                              .build();

        MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT =
                RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/position_tex_color/translucent"))
                              .withDepthWrite(false)
                              .withColorWrite(true)
                              .build();

        // todo POSITION_TEX_COLOR_MASA
        MaLiLibPipelines.POSITION_TEX_COLOR_MASA_NO_DEPTH_NO_CULL =
                RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_MASA_STAGE)
                              .withLocation(getId("pipeline/position_tex_color/masa/no_depth/no_cull"))
                              .withCull(false)
                              .withDepthWrite(false)
                              .withColorWrite(true)
                              .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                              .build();

        MaLiLibPipelines.POSITION_TEX_COLOR_MASA_NO_DEPTH =
                RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_MASA_STAGE)
                              .withLocation(getId("pipeline/position_tex_color/masa/no_depth"))
                              .withDepthWrite(false)
                              .withColorWrite(true)
                              .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                              .build();

        MaLiLibPipelines.POSITION_TEX_COLOR_MASA_LEQUAL_DEPTH_OFFSET_1 =
                RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_MASA_STAGE)
                              .withLocation(getId("pipeline/position_tex_color/masa/lequal_depth/offset_1"))
                              .withDepthWrite(false)
                              .withColorWrite(true)
                              .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
                              .withDepthBias(-0.3f, -0.6f)
                              .build();

        MaLiLibPipelines.POSITION_TEX_COLOR_MASA_LEQUAL_DEPTH_OFFSET_2 =
                RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_MASA_STAGE)
                              .withLocation(getId("pipeline/position_tex_color/masa/lequal_depth/offset_2"))
                              .withDepthWrite(false)
                              .withColorWrite(true)
                              .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
                              .withDepthBias(-0.4f, -0.8f)
                              .build();

        MaLiLibPipelines.POSITION_TEX_COLOR_MASA_LEQUAL_DEPTH_OFFSET_3 =
                RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_MASA_STAGE)
                              .withLocation(getId("pipeline/position_tex_color/masa/lequal_depth/offset_3"))
                              .withDepthWrite(false)
                              .withColorWrite(true)
                              .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
                              .withDepthBias(-3f, -3f)
                              .build();

	    MaLiLibPipelines.POSITION_TEX_COLOR_MASA_LEQUAL_DEPTH_NO_CULL =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_MASA_STAGE)
			                  .withLocation(getId("pipeline/position_tex_color/masa/lequal_depth/no_cull"))
					          .withCull(false)
			                  .withDepthWrite(false)
			                  .withColorWrite(true)
			                  .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
			                  .build();

	    MaLiLibPipelines.POSITION_TEX_COLOR_MASA_LEQUAL_DEPTH =
                RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_MASA_STAGE)
                              .withLocation(getId("pipeline/position_tex_color/masa/lequal_depth"))
                              .withDepthWrite(false)
                              .withColorWrite(true)
                              .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
                              .build();

        MaLiLibPipelines.POSITION_TEX_COLOR_MASA_GREATER_DEPTH =
                RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_MASA_STAGE)
                              .withLocation(getId("pipeline/position_tex_color/masa/greater_depth"))
                              .withDepthWrite(false)
                              .withColorWrite(true)
                              .withDepthTestFunction(DepthTestFunction.GREATER_DEPTH_TEST)
                              .build();

        MaLiLibPipelines.POSITION_TEX_COLOR_MASA_DEPTH_MASK =
                RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_MASA_STAGE)
                              .withLocation(getId("pipeline/position_tex_color/masa/depth_mask"))
                              .withDepthWrite(true)
                              .withColorWrite(true)
                              .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                              .build();

        MaLiLibPipelines.POSITION_TEX_COLOR_MASA =
                RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_MASA_STAGE)
                              .withLocation(getId("pipeline/position_tex_color/masa"))
                              .withDepthWrite(false)
                              .withColorWrite(true)
                              .build();

	    // todo LINES
	    MaLiLibPipelines.LINES_STAGE =
			    RenderPipeline.builder(MATRICES_PROJECTION_SNIPPET, GLOBALS_SNIPPET)
			                  .withVertexShader("core/rendertype_lines")
			                  .withFragmentShader("core/rendertype_lines")
			                  .withVertexFormat(DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH, VertexFormat.Mode.LINES)
			                  .buildSnippet();

	    MaLiLibPipelines.LINES_TRANSLUCENT_STAGE =
			    RenderPipeline.builder(MaLiLibPipelines.LINES_STAGE)
			                  .withBlend(BlendFunction.TRANSLUCENT)
			                  .buildSnippet();

	    MaLiLibPipelines.LINES_MASA_SIMPLE_STAGE =
			    RenderPipeline.builder(MaLiLibPipelines.LINES_STAGE)
			                  .withBlend(MASA_BLEND_SIMPLE)
			                  .buildSnippet();

	    // todo DEBUG_LINES Snippet
	    MaLiLibPipelines.DEBUG_LINES_STAGE =
			    RenderPipeline.builder(MATRICES_PROJECTION_SNIPPET)
                              .withVertexShader(getId("position_color_lines"))
                              .withFragmentShader(getId("position_color_lines"))
			                  .withVertexFormat(DefaultVertexFormat.POSITION_COLOR_LINE_WIDTH, VertexFormat.Mode.DEBUG_LINES)
			                  .buildSnippet();

	    MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_STAGE =
			    RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINES_STAGE)
			                  .withBlend(BlendFunction.TRANSLUCENT)
			                  .buildSnippet();

	    MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_STAGE =
			    RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINES_STAGE)
			                  .withBlend(MASA_BLEND_SIMPLE)
			                  .buildSnippet();

	    // todo DEBUG_LINES_TRANSLUCENT
        MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_NO_DEPTH_NO_CULL =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/debug_lines/translucent/no_depth/no_cull"))
                              .withCull(false)
                              .withDepthWrite(false)
                              .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                              .build();

        MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_NO_DEPTH =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/debug_lines/translucent/no_depth"))
                              .withDepthWrite(false)
                              .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                              .build();

        MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_NO_CULL =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/debug_lines/translucent/no_cull"))
                              .withCull(false)
                              .build();

        MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_LEQUAL_DEPTH =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/debug_lines/translucent/lequal_depth"))
                              .withDepthWrite(false)
                              .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
                              .build();

        MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_OFFSET_1 =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/debug_lines/translucent/offset_1"))
                              .withDepthBias(-0.8f, -1.8f)
                              .build();

        MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_OFFSET_2 =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/debug_lines/translucent/offset_2"))
                              .withDepthBias(-1.2f, -0.2f)
                              .build();

        MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_OFFSET_3 =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/debug_lines/translucent/offset_3"))
                              .withDepthBias(-3.0f, -3.0f)
                              .build();

        MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/debug_lines/translucent"))
                              .build();

        // todo DEBUG_LINES_MASA_SIMPLE
        MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_NO_DEPTH_NO_CULL =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_STAGE)
                              .withLocation(getId("pipeline/debug_lines/masa_simple/no_depth/no_cull"))
                              .withCull(false)
                              .withDepthWrite(false)
                              .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                              .build();

        MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_NO_DEPTH =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_STAGE)
                              .withLocation(getId("pipeline/debug_lines/masa_simple/no_depth"))
                              .withDepthWrite(false)
                              .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                              .build();

        MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_NO_CULL =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_STAGE)
                              .withLocation(getId("pipeline/debug_lines/masa_simple/no_cull"))
                              .withCull(false)
                              .withDepthWrite(false)
                              .build();

        MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_LEQUAL_DEPTH =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_STAGE)
                              .withLocation(getId("pipeline/debug_lines/masa_simple/lequal_depth"))
                              .withDepthWrite(false)
                              .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
                              .build();

        MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_OFFSET_1 =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_STAGE)
                              .withLocation(getId("pipeline/debug_lines/masa_simple/offset_1"))
                              .withDepthBias(-0.8f, -1.8f)
                              .withDepthWrite(false)
                              .build();

        MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_OFFSET_2 =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_STAGE)
                              .withLocation(getId("pipeline/debug_lines/masa_simple/offset_2"))
                              .withDepthBias(-1.2f, -0.2f)
                              .withDepthWrite(false)
                              .build();

        MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_OFFSET_3 =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_STAGE)
                              .withLocation(getId("pipeline/debug_lines/masa_simple/offset_3"))
                              .withDepthBias(-3.0f, -3.0f)
                              .withDepthWrite(false)
                              .build();

        MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_STAGE)
                              .withLocation(getId("pipeline/debug_lines/masa_simple"))
                              .withDepthWrite(false)
                              .build();

	    // todo DEBUG_LINE_STRIP
	    MaLiLibPipelines.DEBUG_LINE_STRIP_STAGE =
			    RenderPipeline.builder(MATRICES_PROJECTION_SNIPPET)
                              .withVertexShader(getId("position_color_lines"))
                              .withFragmentShader(getId("position_color_lines"))
			                  .withVertexFormat(DefaultVertexFormat.POSITION_COLOR_LINE_WIDTH, VertexFormat.Mode.DEBUG_LINE_STRIP)
			                  .buildSnippet();

	    MaLiLibPipelines.DEBUG_LINE_STRIP_TRANSLUCENT_STAGE =
			    RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINE_STRIP_STAGE)
			                  .withBlend(BlendFunction.TRANSLUCENT)
			                  .buildSnippet();

	    MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_STAGE =
			    RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINE_STRIP_STAGE)
			                  .withBlend(MASA_BLEND_SIMPLE)
			                  .buildSnippet();

	    // todo DEBUG_LINE_STRIP_TRANSLUCENT
        MaLiLibPipelines.DEBUG_LINE_STRIP_TRANSLUCENT_NO_DEPTH_NO_CULL =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINE_STRIP_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/debug_line_strip/translucent/no_depth/no_cull"))
                              .withCull(false)
                              .withDepthWrite(false)
                              .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                              .build();

        MaLiLibPipelines.DEBUG_LINE_STRIP_TRANSLUCENT_NO_DEPTH =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINE_STRIP_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/debug_line_strip/translucent/no_depth"))
                              .withDepthWrite(false)
                              .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                              .build();

        MaLiLibPipelines.DEBUG_LINE_STRIP_TRANSLUCENT_NO_CULL =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINE_STRIP_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/debug_line_strip/translucent/no_cull"))
                              .withCull(false)
                              .build();

        MaLiLibPipelines.DEBUG_LINE_STRIP_TRANSLUCENT_OFFSET_1 =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINE_STRIP_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/debug_line_strip/translucent/offset_1"))
                              .withDepthBias(-0.8f, -1.8f)
                              .build();

        MaLiLibPipelines.DEBUG_LINE_STRIP_TRANSLUCENT_OFFSET_2 =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINE_STRIP_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/debug_line_strip/translucent/offset_2"))
                              .withDepthBias(-1.2f, -0.2f)
                              .build();

        MaLiLibPipelines.DEBUG_LINE_STRIP_TRANSLUCENT_OFFSET_3 =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINE_STRIP_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/debug_line_strip/translucent/offset_3"))
                              .withDepthBias(-3.0f, -3.0f)
                              .build();

        MaLiLibPipelines.DEBUG_LINE_STRIP_TRANSLUCENT =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINE_STRIP_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/debug_line_strip/translucent"))
                              .build();

        // todo DEBUG_LINE_STRIP_MASA_SIMPLE
        MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_NO_DEPTH_NO_CULL =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_STAGE)
                              .withLocation(getId("pipeline/debug_line_strip/masa_simple/no_depth/no_cull"))
                              .withCull(false)
                              .withDepthWrite(false)
                              .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                              .build();

        MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_NO_DEPTH =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_STAGE)
                              .withLocation(getId("pipeline/debug_line_strip/masa_simple/no_depth"))
                              .withDepthWrite(false)
                              .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                              .build();

        MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_NO_CULL =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_STAGE)
                              .withLocation(getId("pipeline/debug_line_strip/masa_simple/no_cull"))
                              .withCull(false)
                              .withDepthWrite(false)
                              .build();

        MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_OFFSET_1 =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_STAGE)
                              .withLocation(getId("pipeline/debug_line_strip/masa_simple/offset_1"))
                              .withDepthBias(-0.8f, -1.8f)
                              .withDepthWrite(false)
                              .build();

        MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_OFFSET_2 =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_STAGE)
                              .withLocation(getId("pipeline/debug_line_strip/masa_simple/offset_2"))
                              .withDepthBias(-1.2f, -0.2f)
                              .withDepthWrite(false)
                              .build();

        MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_OFFSET_3 =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_STAGE)
                              .withLocation(getId("pipeline/debug_line_strip/masa_simple/offset_3"))
                              .withDepthBias(-3.0f, -3.0f)
                              .withDepthWrite(false)
                              .build();

        MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_STAGE)
                              .withLocation(getId("pipeline/debug_line_strip/masa_simple"))
                              .withDepthWrite(false)
                              .build();
	    // todo GUI
	    MaLiLibPipelines.GUI_OVERLAY =
			    RenderPipeline.builder(GUI_SNIPPET)
			                  .withLocation(getId("pipeline/gui_overlay"))
			                  .withDepthWrite(false)
			                  .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			                  .withBlend(BlendFunction.OVERLAY)
			                  .build();

	    MaLiLibPipelines.GUI_TEXTURED_OVERLAY =
			    RenderPipeline.builder(GUI_TEXTURED_SNIPPET)
			                  .withLocation(getId("pipeline/gui_textured_overlay"))
			                  .withDepthWrite(false)
			                  .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			                  .withBlend(BlendFunction.OVERLAY)
			                  .build();

	    // todo TERRAIN Snippet
	    MaLiLibPipelines.TERRAIN_STAGE =
			    RenderPipeline.builder(GENERIC_BLOCKS_SNIPPET)
			                  .withVertexShader("core/terrain")
			                  .withFragmentShader("core/terrain")
			                  .withUniform("Projection", UniformType.UNIFORM_BUFFER)
			                  .withUniform("ChunkSection", UniformType.UNIFORM_BUFFER)
			                  .withVertexFormat(DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS)
			                  .buildSnippet();

	    MaLiLibPipelines.TERRAIN_TRANSLUCENT_STAGE =
			    RenderPipeline.builder(MaLiLibPipelines.TERRAIN_STAGE)
			                  .withBlend(BlendFunction.TRANSLUCENT)
			                  .buildSnippet();

	    MaLiLibPipelines.TERRAIN_MASA_STAGE =
			    RenderPipeline.builder(MaLiLibPipelines.TERRAIN_STAGE)
			                  .withBlend(MASA_BLEND)
			                  .buildSnippet();

	    // todo TERRAIN_MASA --> PRE-REGISTER
        MaLiLibPipelines.SOLID_TERRAIN_MASA =
                register(RenderPipeline.builder(MaLiLibPipelines.TERRAIN_MASA_STAGE)
                              .withLocation(getId("pipeline/solid_terrain/masa"))
                              .build());

	    MaLiLibPipelines.WIREFRAME_MASA =
                register(RenderPipeline.builder(MaLiLibPipelines.TERRAIN_MASA_STAGE)
                              .withLocation(getId("pipeline/wireframe/masa"))
                              .withPolygonMode(PolygonMode.WIREFRAME)
                              .build());

        MaLiLibPipelines.CUTOUT_TERRAIN_MASA =
                register(RenderPipeline.builder(MaLiLibPipelines.TERRAIN_MASA_STAGE)
                              .withLocation(getId("pipeline/cutout_terrain/masa"))
                              .withShaderDefine("ALPHA_CUTOUT", 0.5F)
                              .build());

        MaLiLibPipelines.TRANSLUCENT_MASA =
                register(RenderPipeline.builder(MaLiLibPipelines.TERRAIN_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/translucent/masa"))
                               .withShaderDefine("ALPHA_CUTOUT", 0.01F)
                              .build());

        MaLiLibPipelines.TRIPWIRE_TERRAIN_MASA =
                register(RenderPipeline.builder(MaLiLibPipelines.TERRAIN_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/tripwire_terran/masa"))
                              .withShaderDefine("ALPHA_CUTOUT", 0.1F)
                              .build());

	    // todo TERRAIN_MASA_OFFSET --> PRE-REGISTER
	    MaLiLibPipelines.SOLID_TERRAIN_MASA_OFFSET =
			    register(RenderPipeline.builder(MaLiLibPipelines.TERRAIN_MASA_STAGE)
			                           .withLocation(getId("pipeline/solid_terrain/masa/offset"))
			                           .withDepthBias(-0.3f, -0.6f)
			                           .build());

	    MaLiLibPipelines.WIREFRAME_MASA_OFFSET =
			    register(RenderPipeline.builder(MaLiLibPipelines.TERRAIN_MASA_STAGE)
			                           .withLocation(getId("pipeline/wireframe/masa/offset"))
			                           .withPolygonMode(PolygonMode.WIREFRAME)
			                           .withDepthBias(-0.3f, -0.6f)
			                           .build());

	    MaLiLibPipelines.CUTOUT_TERRAIN_MASA_OFFSET =
			    register(RenderPipeline.builder(MaLiLibPipelines.TERRAIN_MASA_STAGE)
			                           .withLocation(getId("pipeline/cutout_terrain/masa/offset"))
			                           .withShaderDefine("ALPHA_CUTOUT", 0.5F)
			                           .withDepthBias(-0.3f, -0.6f)
			                           .build());

	    MaLiLibPipelines.TRANSLUCENT_MASA_OFFSET =
			    register(RenderPipeline.builder(MaLiLibPipelines.TERRAIN_TRANSLUCENT_STAGE)
			                           .withLocation(getId("pipeline/translucent/masa/offset"))
			                           .withShaderDefine("ALPHA_CUTOUT", 0.01F)
			                           .withDepthBias(-0.3f, -0.6f)
			                           .build());

	    MaLiLibPipelines.TRIPWIRE_TERRAIN_MASA_OFFSET =
			    register(RenderPipeline.builder(MaLiLibPipelines.TERRAIN_TRANSLUCENT_STAGE)
			                           .withLocation(getId("pipeline/tripwire_terrain/masa/offset"))
			                           .withShaderDefine("ALPHA_CUTOUT", 0.1F)
			                           .withDepthBias(-0.3f, -0.6f)
			                           .build());

		// todo BLOCK Snippet
	    MaLiLibPipelines.BLOCK_STAGE =
			    RenderPipeline.builder(GENERIC_BLOCKS_SNIPPET, MATRICES_PROJECTION_SNIPPET)
			                  .withVertexShader("core/block")
			                  .withFragmentShader("core/block")
			                  .withVertexFormat(DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS)
			                  .buildSnippet();

	    MaLiLibPipelines.BLOCK_TRANSLUCENT_STAGE =
			    RenderPipeline.builder(MaLiLibPipelines.BLOCK_STAGE)
			                  .withBlend(BlendFunction.TRANSLUCENT)
			                  .buildSnippet();

	    MaLiLibPipelines.BLOCK_MASA_STAGE =
			    RenderPipeline.builder(MaLiLibPipelines.BLOCK_STAGE)
//			                  .withBlend(MASA_BLEND)
			                  .buildSnippet();

		// todo BLOCK
	    MaLiLibPipelines.SOLID_BLOCK_MASA =
			    register(RenderPipeline.builder(MaLiLibPipelines.BLOCK_MASA_STAGE)
			                           .withLocation(getId("pipeline/solid_block/masa"))
			                           .build());

	    MaLiLibPipelines.CUTOUT_BLOCK_MASA =
			    register(RenderPipeline.builder(MaLiLibPipelines.BLOCK_MASA_STAGE)
			                           .withLocation(getId("pipeline/cutout_block/masa"))
			                           .withShaderDefine("ALPHA_CUTOUT", 0.5F)
			                           .build());

	    MaLiLibPipelines.TRIPWIRE_BLOCK_MASA =
			    register(RenderPipeline.builder(MaLiLibPipelines.BLOCK_TRANSLUCENT_STAGE)
			                           .withLocation(getId("pipeline/tripwire_block/masa"))
			                           .withShaderDefine("ALPHA_CUTOUT", 0.1F)
			                           .build());

	    // todo BLOCK_OFFSET
	    MaLiLibPipelines.SOLID_BLOCK_MASA_OFFSET =
			    register(RenderPipeline.builder(MaLiLibPipelines.BLOCK_MASA_STAGE)
			                           .withLocation(getId("pipeline/solid_block/masa/offset"))
			                           .withDepthBias(-0.3f, -0.6f)
			                           .build());

	    MaLiLibPipelines.CUTOUT_BLOCK_MASA_OFFSET =
			    register(RenderPipeline.builder(MaLiLibPipelines.BLOCK_MASA_STAGE)
			                           .withLocation(getId("pipeline/cutout_block/masa/offset"))
			                           .withShaderDefine("ALPHA_CUTOUT", 0.5F)
			                           .withDepthBias(-0.3f, -0.6f)
			                           .build());

	    MaLiLibPipelines.TRIPWIRE_BLOCK_MASA_OFFSET =
			    register(RenderPipeline.builder(MaLiLibPipelines.BLOCK_TRANSLUCENT_STAGE)
			                           .withLocation(getId("pipeline/tripwire_block/masa/offset"))
			                           .withShaderDefine("ALPHA_CUTOUT", 0.1F)
			                           .withDepthBias(-0.3f, -0.6f)
			                           .build());

		// todo LEGACY_TERRAIN Snippet
		MaLiLibPipelines.LEGACY_TERRAIN_STAGE =
				RenderPipeline.builder(MATRICES_FOG_SNIPPET)
							  .withVertexShader(getId("legacy_terrain"))
							  .withFragmentShader(getId("legacy_terrain"))
							  .withSampler("Sampler0")
							  .withSampler("Sampler2")
							  .withUniform("ChunkFix", UniformType.UNIFORM_BUFFER)
							  .withVertexFormat(DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS)
							  .buildSnippet();

		MaLiLibPipelines.LEGACY_TERRAIN_MASA_STAGE =
				RenderPipeline.builder(MaLiLibPipelines.LEGACY_TERRAIN_STAGE)
//							  .withBlend(MASA_BLEND)
							  .buildSnippet();

		MaLiLibPipelines.LEGACY_TERRAIN_TRANSLUCENT_STAGE =
				RenderPipeline.builder(MaLiLibPipelines.LEGACY_TERRAIN_STAGE)
							  .withBlend(BlendFunction.TRANSLUCENT)
							  .buildSnippet();

		// todo LEGACY_TERRAIN_MASA --> PRE-REGISTER
		MaLiLibPipelines.LEGACY_SOLID_TERRAIN_MASA =
				register(RenderPipeline.builder(MaLiLibPipelines.LEGACY_TERRAIN_MASA_STAGE)
									   .withLocation(getId("pipeline/legacy/solid/masa"))
									   .build());

		MaLiLibPipelines.LEGACY_WIREFRAME_MASA =
				register(RenderPipeline.builder(MaLiLibPipelines.LEGACY_TERRAIN_MASA_STAGE)
									   .withLocation(getId("pipeline/legacy/wireframe/masa"))
									   .withPolygonMode(PolygonMode.WIREFRAME)
									   .build());

		MaLiLibPipelines.LEGACY_CUTOUT_TERRAIN_MASA =
				register(RenderPipeline.builder(MaLiLibPipelines.LEGACY_TERRAIN_MASA_STAGE)
									   .withLocation(getId("pipeline/legacy/cutout/masa"))
									   .withShaderDefine("ALPHA_CUTOUT", 0.5F)
									   .build());

		MaLiLibPipelines.LEGACY_TRANSLUCENT_MASA =
				register(RenderPipeline.builder(MaLiLibPipelines.LEGACY_TERRAIN_TRANSLUCENT_STAGE)
									   .withLocation(getId("pipeline/legacy/translucent/masa"))
									   .withShaderDefine("ALPHA_CUTOUT", 0.01F)
				                       .build());

		MaLiLibPipelines.LEGACY_TRIPWIRE_TERRAIN_MASA =
				register(RenderPipeline.builder(MaLiLibPipelines.LEGACY_TERRAIN_TRANSLUCENT_STAGE)
									   .withLocation(getId("pipeline/legacy/tripwire/masa"))
									   .withShaderDefine("ALPHA_CUTOUT", 0.1F)
									   .build());

		// todo LEGACY_TERRAIN_MASA_OFFSET --> PRE-REGISTER
		MaLiLibPipelines.LEGACY_SOLID_TERRAIN_MASA_OFFSET =
				register(RenderPipeline.builder(MaLiLibPipelines.LEGACY_TERRAIN_MASA_STAGE)
									   .withLocation(getId("pipeline/legacy/solid/masa/offset"))
									   .withDepthBias(-0.3f, -0.6f)
									   .build());

		MaLiLibPipelines.LEGACY_WIREFRAME_MASA_OFFSET =
				register(RenderPipeline.builder(MaLiLibPipelines.LEGACY_TERRAIN_MASA_STAGE)
									   .withLocation(getId("pipeline/legacy/wireframe/masa/offset"))
									   .withPolygonMode(PolygonMode.WIREFRAME)
									   .withDepthBias(-0.3f, -0.6f)
									   .build());

		MaLiLibPipelines.LEGACY_CUTOUT_TERRAIN_MASA_OFFSET =
				register(RenderPipeline.builder(MaLiLibPipelines.LEGACY_TERRAIN_MASA_STAGE)
									   .withLocation(getId("pipeline/legacy/cutout/masa/offset"))
									   .withShaderDefine("ALPHA_CUTOUT", 0.5F)
									   .withDepthBias(-0.3f, -0.6f)
									   .build());

		MaLiLibPipelines.LEGACY_TRANSLUCENT_MASA_OFFSET =
				register(RenderPipeline.builder(MaLiLibPipelines.LEGACY_TERRAIN_TRANSLUCENT_STAGE)
									   .withLocation(getId("pipeline/legacy/translucent/masa/offset"))
									   .withShaderDefine("ALPHA_CUTOUT", 0.01F)
									   .withDepthBias(-0.3f, -0.6f)
									   .build());

		MaLiLibPipelines.LEGACY_TRIPWIRE_TERRAIN_MASA_OFFSET =
				register(RenderPipeline.builder(MaLiLibPipelines.LEGACY_TERRAIN_TRANSLUCENT_STAGE)
									   .withLocation(getId("pipeline/legacy/tripwire/masa/offset"))
									   .withShaderDefine("ALPHA_CUTOUT", 0.1F)
									   .withDepthBias(-0.3f, -0.6f)
									   .build());

		// Try registering with Iris.
        IrisCompat.registerPipelines();
    }
}
