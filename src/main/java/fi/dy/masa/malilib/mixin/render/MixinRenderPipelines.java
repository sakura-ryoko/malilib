package fi.dy.masa.malilib.mixin.render;

import java.util.Map;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.BlendFactor;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.platform.PolygonMode;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
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

    @Shadow @Final private static RenderPipeline.Snippet GLOBALS_SNIPPET;                      // GLOBALS_SNIPPET
    @Shadow @Final private static RenderPipeline.Snippet GUI_SNIPPET;                          // GUI
    @Shadow @Final private static RenderPipeline.Snippet GUI_TEXTURED_SNIPPET;                 // GUI_TEXTURED

	// AKA, the legacy Vanilla "default blend mode".
    @Unique private static final BlendFunction MASA_BLEND = new BlendFunction(BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_SRC_ALPHA, BlendFactor.ONE, BlendFactor.ZERO);
    @Unique private static final BlendFunction MASA_BLEND_SIMPLE = new BlendFunction(BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_SRC_ALPHA);

    @Shadow
    private static RenderPipeline register(RenderPipeline pipeline)
    {
        PIPELINES_BY_LOCATION.put(pipeline.getLocation(), pipeline);
        return pipeline;
    }

	@Unique
	private static Identifier getId(String id)
	{
		return Identifier.fromNamespaceAndPath(MaLiLibReference.MOD_ID, id);
	}

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void malilib_onRegisterPipelines(CallbackInfo ci)
    {
		// todo POSITION_COLOR Snippet
	    MaLiLibPipelines.POSITION_COLOR_STAGE =
			    RenderPipeline.builder()
			                  .withVertexShader(getId("int_position_color"))
			                  .withFragmentShader(getId("int_position_color"))
			                  .withBindGroupLayout(BindGroupLayouts.MATRICES_PROJECTION)
//			                  .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
			                  .withPrimitiveTopology(PrimitiveTopology.QUADS)
			                  .withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR)
			                  .buildSnippet();

	    MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_STAGE =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_STAGE)
			                  .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
			                  .buildSnippet();

	    MaLiLibPipelines.POSITION_COLOR_MASA_STAGE =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_STAGE)
			                  .withColorTargetState(new ColorTargetState(MASA_BLEND))
			                  .buildSnippet();

	    // todo POSITION_COLOR_TRANSLUCENT
	    MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_NO_DEPTH_NO_CULL =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_STAGE)
			                  .withLocation(getId("pipeline/position_color/translucent/no_depth/no_cull"))
			                  .withCull(false)
			                  .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_NO_DEPTH =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_STAGE)
			                  .withLocation(getId("pipeline/position_color/translucent/no_depth"))
			                  .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_1 =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_STAGE)
			                  .withLocation(getId("pipeline/position_color/translucent/lequal_depth/offset_1"))
			                  .withCull(false)
			                  .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false, 0.3f, 0.6f))
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_2 =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_STAGE)
			                  .withLocation(getId("pipeline/position_color/translucent/lequal_depth/offset_2"))
			                  .withCull(false)
			                  .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false, 0.4f, 0.8f))
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_3 =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_STAGE)
			                  .withLocation(getId("pipeline/position_color/translucent/lequal_depth/offset_3"))
			                  .withCull(false)
			                  .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false, 3f, 3f))
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_LEQUAL_DEPTH_NO_CULL =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_STAGE)
			                  .withLocation(getId("pipeline/position_color/translucent/lequal_depth/no_cull"))
			                  .withCull(false)
			                  .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_LEQUAL_DEPTH =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_STAGE)
			                  .withLocation(getId("pipeline/position_color/translucent/lequal_depth"))
			                  .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_DEPTH_MASK =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_STAGE)
			                  .withLocation(getId("pipeline/position_color/translucent/depth_mask"))
			                  .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, true))
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
			                  .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_MASA_NO_DEPTH =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_MASA_STAGE)
			                  .withLocation(getId("pipeline/position_color/masa/no_depth"))
			                  .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_MASA_LEQUAL_DEPTH_OFFSET_1 =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_MASA_STAGE)
			                  .withLocation(getId("pipeline/position_color/masa/lequal_depth/offset_1"))
			                  .withCull(false)
			                  .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false, 0.3f, 0.6f))
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_MASA_LEQUAL_DEPTH_OFFSET_2 =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_MASA_STAGE)
			                  .withLocation(getId("pipeline/position_color/masa/lequal_depth/offset_2"))
			                  .withCull(false)
			                  .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false, 0.4f, 0.8f))
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_MASA_LEQUAL_DEPTH_OFFSET_3 =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_MASA_STAGE)
			                  .withLocation(getId("pipeline/position_color/masa/lequal_depth/offset_3"))
			                  .withCull(false)
			                  .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false, 3f, 3f))
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_MASA_LEQUAL_DEPTH_NO_CULL =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_MASA_STAGE)
			                  .withLocation(getId("pipeline/position_color/masa/lequal_depth/no_cull"))
			                  .withCull(false)
			                  .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_MASA_LEQUAL_DEPTH =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_MASA_STAGE)
			                  .withLocation(getId("pipeline/position_color/masa/lequal_depth"))
			                  .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_MASA_DEPTH_MASK =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_MASA_STAGE)
			                  .withLocation(getId("pipeline/position_color/masa/depth_mask"))
			                  .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, true))
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_MASA =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_MASA_STAGE)
			                  .withLocation(getId("pipeline/position_color/masa"))
			                  .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
			                  .build();

		// todo TEXT_PLATE_BG
	    MaLiLibPipelines.TEXT_PLATE_BG_MASA_NO_DEPTH =
			    register(RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_MASA_STAGE)
			                  .withLocation(getId("pipeline/text_plate_bg/no_depth"))
			                  .withCull(false)
			                  .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
			                  .build());

	    MaLiLibPipelines.TEXT_PLATE_BG_MASA =
			    register(RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_MASA_STAGE)
			                  .withLocation(getId("pipeline/text_plate_bg"))
					          .withCull(false)
			                  .build());

	    // todo MINIHUD_SHAPE
	    MaLiLibPipelines.MINIHUD_SHAPE_NO_DEPTH_OFFSET =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_MASA_STAGE)
			                  .withLocation(getId("pipeline/minihud/shape/no_depth/offset"))
			                  .withCull(false)
			                  .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false, 3.0f, 3.0f))
			                  .build();

	    MaLiLibPipelines.MINIHUD_SHAPE_NO_DEPTH =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_MASA_STAGE)
			                  .withLocation(getId("pipeline/minihud/shape/no_depth"))
			                  .withCull(false)
			                  .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
			                  .build();

	    MaLiLibPipelines.MINIHUD_SHAPE_OFFSET_NO_CULL =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_MASA_STAGE)
			                  .withLocation(getId("pipeline/minihud/shape/offset/no_cull"))
			                  .withCull(false)
			                  .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false, 3.0f, 3.0f))
			                  .build();

	    MaLiLibPipelines.MINIHUD_SHAPE_OFFSET =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_MASA_STAGE)
			                  .withLocation(getId("pipeline/minihud/shape/offset"))
			                  .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false, 3.0f, 3.0f))
			                  .build();

	    MaLiLibPipelines.MINIHUD_SHAPE_NO_CULL =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_MASA_STAGE)
			                  .withLocation(getId("pipeline/minihud/shape/no_cull"))
			                  .withCull(false)
			                  .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
			                  .build();

	    MaLiLibPipelines.MINIHUD_SHAPE =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_MASA_STAGE)
			                  .withLocation(getId("pipeline/minihud/shape"))
			                  .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
			                  .build();

	    // todo POSITION_COLOR_LINES Snippet
	    MaLiLibPipelines.POSITION_COLOR_LINES_STAGE =
			    RenderPipeline.builder()
                              .withVertexShader(getId("position_color_lines"))
                              .withFragmentShader(getId("position_color_lines"))
//			                  .withVertexFormat(DefaultVertexFormat.POSITION_COLOR_LINE_WIDTH, VertexFormat.Mode.LINES)
                              .withBindGroupLayout(BindGroupLayouts.MATRICES_PROJECTION)
			                  .withPrimitiveTopology(PrimitiveTopology.LINES)
			                  .withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR_LINE_WIDTH)
			                  .buildSnippet();

	    MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_STAGE =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_STAGE)
			                  .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
			                  .buildSnippet();

	    MaLiLibPipelines.POSITION_COLOR_LINES_MASA_STAGE =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_STAGE)
			                  .withColorTargetState(new ColorTargetState(MASA_BLEND))
			                  .buildSnippet();

	    // todo POSITION_COLOR_LINES_TRANSLUCENT
	    MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_NO_DEPTH_NO_CULL =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_STAGE)
			                  .withLocation(getId("pipeline/position_color_lines/translucent/no_depth/no_cull"))
			                  .withCull(false)
			                  .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_NO_DEPTH =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_STAGE)
			                  .withLocation(getId("pipeline/position_color_lines/translucent/no_depth"))
			                  .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_1 =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_STAGE)
			                  .withLocation(getId("pipeline/position_color_lines/translucent/lequal_depth/offset_1"))
			                  .withCull(false)
			                  .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false, 0.3f, 0.6f))
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_2 =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_STAGE)
			                  .withLocation(getId("pipeline/position_color_lines/translucent/lequal_depth/offset_2"))
			                  .withCull(false)
			                  .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false, 0.4f, 0.8f))
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_3 =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_STAGE)
			                  .withLocation(getId("pipeline/position_color_lines/translucent/lequal_depth/offset_3"))
			                  .withCull(false)
			                  .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false, 3f, 3f))
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_LEQUAL_DEPTH_NO_CULL =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_STAGE)
			                  .withLocation(getId("pipeline/position_color_lines/translucent/lequal_depth/no_cull"))
			                  .withCull(false)
			                  .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_LEQUAL_DEPTH =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_STAGE)
			                  .withLocation(getId("pipeline/position_color_lines/translucent/lequal_depth"))
			                  .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
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
			                  .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_LINES_MASA_NO_DEPTH =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_MASA_STAGE)
			                  .withLocation(getId("pipeline/position_color_lines/masa/no_depth"))
			                  .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_LINES_MASA_LEQUAL_DEPTH_OFFSET_1 =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_MASA_STAGE)
			                  .withLocation(getId("pipeline/position_color_lines/masa/lequal_depth/offset_1"))
			                  .withCull(false)
			                  .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false, 0.3f, 0.6f))
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_LINES_MASA_LEQUAL_DEPTH_OFFSET_2 =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_MASA_STAGE)
			                  .withLocation(getId("pipeline/position_color_lines/masa/lequal_depth/offset_2"))
			                  .withCull(false)
			                  .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false, 0.4f, 0.8f))
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_LINES_MASA_LEQUAL_DEPTH_OFFSET_3 =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_MASA_STAGE)
			                  .withLocation(getId("pipeline/position_color_lines/masa/lequal_depth/offset_3"))
			                  .withCull(false)
			                  .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false, 3f, 3f))
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_LINES_MASA_LEQUAL_DEPTH_NO_CULL =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_MASA_STAGE)
			                  .withLocation(getId("pipeline/position_color_lines/masa/lequal_depth/no_cull"))
			                  .withCull(false)
			                  .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_LINES_MASA_LEQUAL_DEPTH =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_MASA_STAGE)
			                  .withLocation(getId("pipeline/position_color_lines/masa/lequal_depth"))
			                  .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
			                  .build();

	    MaLiLibPipelines.POSITION_COLOR_LINES_MASA =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_MASA_STAGE)
			                  .withLocation(getId("pipeline/position_color_lines/masa"))
			                  .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
			                  .build();

	    // todo MINIHUD_SHAPE_LINES
	    MaLiLibPipelines.MINIHUD_SHAPE_LINES_NO_DEPTH_OFFSET =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_MASA_STAGE)
			                  .withLocation(getId("pipeline/minihud/shape_lines/no_depth/offset"))
			                  .withCull(false)
			                  .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false, 3.0f, 3.0f))
			                  .build();

	    MaLiLibPipelines.MINIHUD_SHAPE_LINES_NO_DEPTH =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_MASA_STAGE)
			                  .withLocation(getId("pipeline/minihud/shape_lines/no_depth"))
			                  .withCull(false)
			                  .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
			                  .build();

	    MaLiLibPipelines.MINIHUD_SHAPE_LINES_OFFSET_NO_CULL =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_MASA_STAGE)
			                  .withLocation(getId("pipeline/minihud/shape_lines/offset/no_cull"))
			                  .withCull(false)
			                  .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false, 3.0f, 3.0f))
			                  .build();

	    MaLiLibPipelines.MINIHUD_SHAPE_LINES_OFFSET =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_MASA_STAGE)
			                  .withLocation(getId("pipeline/minihud/shape_lines/offset"))
			                  .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false, 3.0f, 3.0f))
			                  .build();

	    MaLiLibPipelines.MINIHUD_SHAPE_LINES_NO_CULL =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_MASA_STAGE)
			                  .withLocation(getId("pipeline/minihud/shape_lines/no_cull"))
			                  .withCull(false)
			                  .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
			                  .build();

	    MaLiLibPipelines.MINIHUD_SHAPE_LINES =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_COLOR_LINES_MASA_STAGE)
			                  .withLocation(getId("pipeline/minihud/shape_lines"))
			                  .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
			                  .build();

	    // todo POSITION_TEX_COLOR
	    MaLiLibPipelines.POSITION_TEX_COLOR_STAGE =
			    RenderPipeline.builder()
			                  .withVertexShader(getId("int_position_tex_color"))
			                  .withFragmentShader(getId("int_position_tex_color"))
			                  .withBindGroupLayout(BindGroupLayouts.MATRICES_PROJECTION)
//			                  .withSampler("Sampler0")
//			                  .withVertexFormat(DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS)
                              .withBindGroupLayout(BindGroupLayouts.SAMPLER0)
                              .withVertexBinding(0, DefaultVertexFormat.POSITION_TEX_COLOR)
                              .withPrimitiveTopology(PrimitiveTopology.QUADS)
			                  .buildSnippet();

	    MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_STAGE =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_STAGE)
			                  .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
			                  .buildSnippet();

	    MaLiLibPipelines.POSITION_TEX_COLOR_MASA_STAGE =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_STAGE)
			                  .withColorTargetState(new ColorTargetState(MASA_BLEND))
			                  .buildSnippet();

	    // todo POSITION_TEX_COLOR_TRANSLUCENT
        MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_NO_DEPTH_NO_CULL =
                RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/position_tex_color/translucent/no_depth/no_cull"))
                              .withCull(false)
                              .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
                              .build();

        MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_NO_DEPTH =
                RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/position_tex_color/translucent/no_depth"))
                              .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
                              .build();

        MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_1 =
                RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/position_tex_color/translucent/lequal_depth/offset_1"))
                              .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false, 0.3f, 0.6f))
                              .build();

        MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_2 =
                RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/position_tex_color/translucent/lequal_depth/offset_2"))
                              .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false, 0.4f, 0.8f))
                              .build();

        MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_3 =
                RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/position_tex_color/translucent/lequal_depth/offset_3"))
                              .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false, 3f, 3f))
                              .build();

	    MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_LEQUAL_DEPTH_NO_CULL =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_STAGE)
			                  .withLocation(getId("pipeline/position_tex_color/translucent/lequal_depth/no_cull"))
					          .withCull(false)
			                  .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
			                  .build();

	    MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_LEQUAL_DEPTH =
                RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/position_tex_color/translucent/lequal_depth"))
                              .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
                              .build();

        MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_DEPTH_MASK =
                RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/position_tex_color/translucent/depth_mask"))
                              .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, true))
                              .build();

        MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT =
                RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/position_tex_color/translucent"))
                              .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
                              .build();

        // todo POSITION_TEX_COLOR_MASA
        MaLiLibPipelines.POSITION_TEX_COLOR_MASA_NO_DEPTH_NO_CULL =
                RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_MASA_STAGE)
                              .withLocation(getId("pipeline/position_tex_color/masa/no_depth/no_cull"))
                              .withCull(false)
                              .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
                              .build();

        MaLiLibPipelines.POSITION_TEX_COLOR_MASA_NO_DEPTH =
                RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_MASA_STAGE)
                              .withLocation(getId("pipeline/position_tex_color/masa/no_depth"))
                              .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
                              .build();

        MaLiLibPipelines.POSITION_TEX_COLOR_MASA_LEQUAL_DEPTH_OFFSET_1 =
                RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_MASA_STAGE)
                              .withLocation(getId("pipeline/position_tex_color/masa/lequal_depth/offset_1"))
                              .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false, 0.3f, 0.6f))
                              .build();

        MaLiLibPipelines.POSITION_TEX_COLOR_MASA_LEQUAL_DEPTH_OFFSET_2 =
                RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_MASA_STAGE)
                              .withLocation(getId("pipeline/position_tex_color/masa/lequal_depth/offset_2"))
                              .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false, 0.4f, 0.8f))
                              .build();

        MaLiLibPipelines.POSITION_TEX_COLOR_MASA_LEQUAL_DEPTH_OFFSET_3 =
                RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_MASA_STAGE)
                              .withLocation(getId("pipeline/position_tex_color/masa/lequal_depth/offset_3"))
                              .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false, 3f, 3f))
                              .build();

	    MaLiLibPipelines.POSITION_TEX_COLOR_MASA_LEQUAL_DEPTH_NO_CULL =
			    RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_MASA_STAGE)
			                  .withLocation(getId("pipeline/position_tex_color/masa/lequal_depth/no_cull"))
					          .withCull(false)
			                  .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
			                  .build();

	    MaLiLibPipelines.POSITION_TEX_COLOR_MASA_LEQUAL_DEPTH =
                RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_MASA_STAGE)
                              .withLocation(getId("pipeline/position_tex_color/masa/lequal_depth"))
                              .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
                              .build();

        MaLiLibPipelines.POSITION_TEX_COLOR_MASA_DEPTH_MASK =
                RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_MASA_STAGE)
                              .withLocation(getId("pipeline/position_tex_color/masa/depth_mask"))
                              .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, true))
                              .build();

        MaLiLibPipelines.POSITION_TEX_COLOR_MASA =
                RenderPipeline.builder(MaLiLibPipelines.POSITION_TEX_COLOR_MASA_STAGE)
                              .withLocation(getId("pipeline/position_tex_color/masa"))
                              .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
                              .build();

	    // todo DEBUG_LINES Snippet
	    MaLiLibPipelines.DEBUG_LINES_STAGE =
			    RenderPipeline.builder()
                              .withVertexShader(getId("position_color_lines"))
                              .withFragmentShader(getId("position_color_lines"))
			                  .withBindGroupLayout(BindGroupLayouts.MATRICES_PROJECTION)
//			                  .withVertexFormat(DefaultVertexFormat.POSITION_COLOR_LINE_WIDTH, VertexFormat.Mode.DEBUG_LINES)
			                  .withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR_LINE_WIDTH)
			                  .withPrimitiveTopology(PrimitiveTopology.DEBUG_LINES)
			                  .buildSnippet();

	    MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_STAGE =
			    RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINES_STAGE)
			                  .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
			                  .buildSnippet();

	    MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_STAGE =
			    RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINES_STAGE)
			                  .withColorTargetState(new ColorTargetState(MASA_BLEND_SIMPLE))
			                  .buildSnippet();

	    // todo DEBUG_LINES_TRANSLUCENT
        MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_NO_DEPTH_NO_CULL =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/debug_lines/translucent/no_depth/no_cull"))
                              .withCull(false)
                              .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
                              .build();

        MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_NO_DEPTH =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/debug_lines/translucent/no_depth"))
                              .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
                              .build();

        MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_NO_CULL =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/debug_lines/translucent/no_cull"))
                              .withCull(false)
                              .build();

        MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_LEQUAL_DEPTH =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/debug_lines/translucent/lequal_depth"))
                              .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
                              .build();

        MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_OFFSET_1 =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/debug_lines/translucent/offset_1"))
                              .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false, 0.8f, 1.8f))
                              .build();

        MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_OFFSET_2 =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/debug_lines/translucent/offset_2"))
                              .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false, 1.2f, 0.2f))
                              .build();

        MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_OFFSET_3 =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/debug_lines/translucent/offset_3"))
                              .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false, 3.0f, 3.0f))
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
                              .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
                              .build();

        MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_NO_DEPTH =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_STAGE)
                              .withLocation(getId("pipeline/debug_lines/masa_simple/no_depth"))
                              .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
                              .build();

        MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_NO_CULL =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_STAGE)
                              .withLocation(getId("pipeline/debug_lines/masa_simple/no_cull"))
                              .withCull(false)
                              .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
                              .build();

        MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_LEQUAL_DEPTH =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_STAGE)
                              .withLocation(getId("pipeline/debug_lines/masa_simple/lequal_depth"))
                              .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
                              .build();

        MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_OFFSET_1 =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_STAGE)
                              .withLocation(getId("pipeline/debug_lines/masa_simple/offset_1"))
                              .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false, 0.8f, 1.8f))
                              .build();

        MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_OFFSET_2 =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_STAGE)
                              .withLocation(getId("pipeline/debug_lines/masa_simple/offset_2"))
                              .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false, 1.2f, 0.2f))
                              .build();

        MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_OFFSET_3 =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_STAGE)
                              .withLocation(getId("pipeline/debug_lines/masa_simple/offset_3"))
                              .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false, 3.0f, 3.0f))
                              .build();

        MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_STAGE)
                              .withLocation(getId("pipeline/debug_lines/masa_simple"))
                              .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
                              .build();

	    // todo DEBUG_LINE_STRIP
	    MaLiLibPipelines.DEBUG_LINE_STRIP_STAGE =
			    RenderPipeline.builder()
                              .withVertexShader(getId("position_color_lines"))
                              .withFragmentShader(getId("position_color_lines"))
                              .withBindGroupLayout(BindGroupLayouts.MATRICES_PROJECTION)
//			                  .withVertexFormat(DefaultVertexFormat.POSITION_COLOR_LINE_WIDTH, VertexFormat.Mode.DEBUG_LINE_STRIP)
			                  .withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR_LINE_WIDTH)
			                  .withPrimitiveTopology(PrimitiveTopology.DEBUG_LINE_STRIP)
			                  .buildSnippet();

	    MaLiLibPipelines.DEBUG_LINE_STRIP_TRANSLUCENT_STAGE =
			    RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINE_STRIP_STAGE)
			                  .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
			                  .buildSnippet();

	    MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_STAGE =
			    RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINE_STRIP_STAGE)
			                  .withColorTargetState(new ColorTargetState(MASA_BLEND_SIMPLE))
			                  .buildSnippet();

	    // todo DEBUG_LINE_STRIP_TRANSLUCENT
        MaLiLibPipelines.DEBUG_LINE_STRIP_TRANSLUCENT_NO_DEPTH_NO_CULL =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINE_STRIP_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/debug_line_strip/translucent/no_depth/no_cull"))
                              .withCull(false)
                              .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
                              .build();

        MaLiLibPipelines.DEBUG_LINE_STRIP_TRANSLUCENT_NO_DEPTH =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINE_STRIP_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/debug_line_strip/translucent/no_depth"))
                              .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
                              .build();

        MaLiLibPipelines.DEBUG_LINE_STRIP_TRANSLUCENT_NO_CULL =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINE_STRIP_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/debug_line_strip/translucent/no_cull"))
                              .withCull(false)
                              .build();

        MaLiLibPipelines.DEBUG_LINE_STRIP_TRANSLUCENT_OFFSET_1 =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINE_STRIP_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/debug_line_strip/translucent/offset_1"))
                              .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false, 0.8f, 1.8f))
                              .build();

        MaLiLibPipelines.DEBUG_LINE_STRIP_TRANSLUCENT_OFFSET_2 =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINE_STRIP_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/debug_line_strip/translucent/offset_2"))
                              .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false, 1.2f, 0.2f))
                              .build();

        MaLiLibPipelines.DEBUG_LINE_STRIP_TRANSLUCENT_OFFSET_3 =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINE_STRIP_TRANSLUCENT_STAGE)
                              .withLocation(getId("pipeline/debug_line_strip/translucent/offset_3"))
                              .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false, 3.0f, 3.0f))
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
                              .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
                              .build();

        MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_NO_DEPTH =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_STAGE)
                              .withLocation(getId("pipeline/debug_line_strip/masa_simple/no_depth"))
                              .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
                              .build();

        MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_NO_CULL =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_STAGE)
                              .withLocation(getId("pipeline/debug_line_strip/masa_simple/no_cull"))
                              .withCull(false)
                              .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
                              .build();

        MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_OFFSET_1 =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_STAGE)
                              .withLocation(getId("pipeline/debug_line_strip/masa_simple/offset_1"))
                              .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false, 0.8f, 1.8f))
                              .build();

        MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_OFFSET_2 =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_STAGE)
                              .withLocation(getId("pipeline/debug_line_strip/masa_simple/offset_2"))
                              .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false, 1.2f, 0.2f))
                              .build();

        MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_OFFSET_3 =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_STAGE)
                              .withLocation(getId("pipeline/debug_line_strip/masa_simple/offset_3"))
                              .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false, 3.0f, 3.0f))
                              .build();

        MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE =
                RenderPipeline.builder(MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_STAGE)
                              .withLocation(getId("pipeline/debug_line_strip/masa_simple"))
                              .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
                              .build();

	    // todo GUI
//	    MaLiLibPipelines.GUI_OVERLAY =
//			    RenderPipeline.builder(GUI_SNIPPET)
//			                  .withLocation(getId("pipeline/gui_overlay"))
//			                  .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
//			                  .withColorTargetState(new ColorTargetState(BlendFunction.OVERLAY))
//			                  .build();
//
//	    MaLiLibPipelines.GUI_TEXTURED_OVERLAY =
//			    RenderPipeline.builder(GUI_TEXTURED_SNIPPET)
//			                  .withLocation(getId("pipeline/gui_textured_overlay"))
//			                  .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
//			                  .withColorTargetState(new ColorTargetState(BlendFunction.OVERLAY))
//			                  .build();

	    // todo LEGACY_TERRAIN Snippet
	    MaLiLibPipelines.LEGACY_TERRAIN_STAGE =
			    RenderPipeline.builder()
			                  .withVertexShader(getId("legacy_terrain"))
			                  .withFragmentShader(getId("legacy_terrain"))
			                  .withBindGroupLayout(BindGroupLayouts.MATRICES_PROJECTION)
			                  .withBindGroupLayout(BindGroupLayouts.FOG)
			                  .withBindGroupLayout(BindGroupLayouts.SAMPLER0_SAMPLER2)
			                  .withBindGroupLayout(MaLiLibPipelines.LEGACY_TERRAIN_GROUP)
//			                  .withSampler("Sampler0")
//			                  .withSampler("Sampler2")
//			                  .withUniform("ChunkFix", UniformType.UNIFORM_BUFFER)
//			                  .withVertexFormat(DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS)
			                  .withVertexBinding(0, DefaultVertexFormat.BLOCK)
			                  .withPrimitiveTopology(PrimitiveTopology.QUADS)
			                  .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, true))
			                  .buildSnippet();

	    // todo LEGACY_TERRAIN
	    MaLiLibPipelines.LEGACY_SOLID_TERRAIN =
			    register(RenderPipeline.builder(MaLiLibPipelines.LEGACY_TERRAIN_STAGE)
			                           .withLocation(getId("pipeline/legacy/solid"))
			                           .build());

	    MaLiLibPipelines.LEGACY_WIREFRAME =
			    register(RenderPipeline.builder(MaLiLibPipelines.LEGACY_TERRAIN_STAGE)
			                           .withLocation(getId("pipeline/legacy/wireframe"))
			                           .withPolygonMode(PolygonMode.WIREFRAME)
			                           .build());

	    MaLiLibPipelines.LEGACY_CUTOUT_TERRAIN =
			    register(RenderPipeline.builder(MaLiLibPipelines.LEGACY_TERRAIN_STAGE)
			                           .withLocation(getId("pipeline/legacy/cutout"))
			                           .withShaderDefine("ALPHA_CUTOUT", 0.5F)
			                           .build());

	    // todo LEGACY_TERRAIN_OFFSET --> PRE-REGISTER
	    MaLiLibPipelines.LEGACY_SOLID_TERRAIN_OFFSET =
			    register(RenderPipeline.builder(MaLiLibPipelines.LEGACY_TERRAIN_STAGE)
			                           .withLocation(getId("pipeline/legacy/solid/masa/offset"))
			                           .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, true, 0.3f, 0.6f))
			                           .build());

	    MaLiLibPipelines.LEGACY_WIREFRAME_OFFSET =
			    register(RenderPipeline.builder(MaLiLibPipelines.LEGACY_TERRAIN_STAGE)
			                           .withLocation(getId("pipeline/legacy/wireframe/offset"))
			                           .withPolygonMode(PolygonMode.WIREFRAME)
			                           .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, true, 0.3f, 0.6f))
			                           .build());

	    MaLiLibPipelines.LEGACY_CUTOUT_TERRAIN_OFFSET =
			    register(RenderPipeline.builder(MaLiLibPipelines.LEGACY_TERRAIN_STAGE)
			                           .withLocation(getId("pipeline/legacy/cutout/offset"))
			                           .withShaderDefine("ALPHA_CUTOUT", 0.5F)
			                           .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, true, 0.3f, 0.6f))
			                           .build());

	    // todo LEGACY_TERRAIN_TRANSLUCENT Snippet
	    MaLiLibPipelines.LEGACY_TERRAIN_TRANSLUCENT_STAGE =
			    RenderPipeline.builder(MaLiLibPipelines.LEGACY_TERRAIN_STAGE)
			                  .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
			                  .buildSnippet();

	    // todo LEGACY_TERRAIN_TRANSLUCENT
	    MaLiLibPipelines.LEGACY_TRANSLUCENT =
			    register(RenderPipeline.builder(MaLiLibPipelines.LEGACY_TERRAIN_TRANSLUCENT_STAGE)
			                           .withLocation(getId("pipeline/legacy/translucent"))
			                           .withShaderDefine("ALPHA_CUTOUT", 0.1F)
			                           .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, true))
			                           .build());

	    MaLiLibPipelines.LEGACY_TRANSLUCENT_OFFSET =
			    register(RenderPipeline.builder(MaLiLibPipelines.LEGACY_TERRAIN_TRANSLUCENT_STAGE)
			                           .withLocation(getId("pipeline/legacy/translucent/offset"))
			                           .withShaderDefine("ALPHA_CUTOUT", 0.1F)
			                           .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, true, 0.3f, 0.6f))
			                           .build());

	    // Try registering with Iris.
        IrisCompat.registerPipelines();
    }
}
