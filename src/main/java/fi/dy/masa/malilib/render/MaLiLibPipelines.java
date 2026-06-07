package fi.dy.masa.malilib.render;

import com.mojang.blaze3d.pipeline.BindGroupLayout;
import com.mojang.blaze3d.pipeline.RenderPipeline;

/**
 * This is meant as a central place to manage all custom Render Pipelines
 */
public class MaLiLibPipelines
{
	// todo POSITION_COLOR Snippet
	public static RenderPipeline.Snippet POSITION_COLOR_STAGE;
	public static RenderPipeline.Snippet POSITION_COLOR_TRANSLUCENT_STAGE;
	public static RenderPipeline.Snippet POSITION_COLOR_MASA_STAGE;

    // POSITION_COLOR_TRANSLUCENT
    public static RenderPipeline POSITION_COLOR_TRANSLUCENT_NO_DEPTH_NO_CULL;
    public static RenderPipeline POSITION_COLOR_TRANSLUCENT_NO_DEPTH;
    public static RenderPipeline POSITION_COLOR_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_1;
    public static RenderPipeline POSITION_COLOR_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_2;
    public static RenderPipeline POSITION_COLOR_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_3;
	public static RenderPipeline POSITION_COLOR_TRANSLUCENT_LEQUAL_DEPTH_NO_CULL;
    public static RenderPipeline POSITION_COLOR_TRANSLUCENT_LEQUAL_DEPTH;
	public static RenderPipeline POSITION_COLOR_TRANSLUCENT_DEPTH_MASK;
    public static RenderPipeline POSITION_COLOR_TRANSLUCENT;

    // POSITION_COLOR_MASA
    public static RenderPipeline POSITION_COLOR_MASA_NO_DEPTH_NO_CULL;
    public static RenderPipeline POSITION_COLOR_MASA_NO_DEPTH;
    public static RenderPipeline POSITION_COLOR_MASA_LEQUAL_DEPTH_OFFSET_1;
    public static RenderPipeline POSITION_COLOR_MASA_LEQUAL_DEPTH_OFFSET_2;
    public static RenderPipeline POSITION_COLOR_MASA_LEQUAL_DEPTH_OFFSET_3;
	public static RenderPipeline POSITION_COLOR_MASA_LEQUAL_DEPTH_NO_CULL;
    public static RenderPipeline POSITION_COLOR_MASA_LEQUAL_DEPTH;
	public static RenderPipeline POSITION_COLOR_MASA_DEPTH_MASK;
    public static RenderPipeline POSITION_COLOR_MASA;

	// TEXT_PLATE_BG
	public static RenderPipeline TEXT_PLATE_BG_MASA_NO_DEPTH;
	public static RenderPipeline TEXT_PLATE_BG_MASA;

	// todo MINIHUD_SHAPE
	public static RenderPipeline MINIHUD_SHAPE_NO_DEPTH_OFFSET;
	public static RenderPipeline MINIHUD_SHAPE_NO_DEPTH;
	public static RenderPipeline MINIHUD_SHAPE_OFFSET_NO_CULL;
	public static RenderPipeline MINIHUD_SHAPE_OFFSET;
	public static RenderPipeline MINIHUD_SHAPE_NO_CULL;
	public static RenderPipeline MINIHUD_SHAPE;

	// todo POSITION_COLOR_LINES Snippet
	public static RenderPipeline.Snippet POSITION_COLOR_LINES_STAGE;
	public static RenderPipeline.Snippet POSITION_COLOR_LINES_TRANSLUCENT_STAGE;
	public static RenderPipeline.Snippet POSITION_COLOR_LINES_MASA_STAGE;

	// POSITION_COLOR_LINES_TRANSLUCENT
	public static RenderPipeline POSITION_COLOR_LINES_TRANSLUCENT_NO_DEPTH_NO_CULL;
	public static RenderPipeline POSITION_COLOR_LINES_TRANSLUCENT_NO_DEPTH;
	public static RenderPipeline POSITION_COLOR_LINES_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_1;
	public static RenderPipeline POSITION_COLOR_LINES_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_2;
	public static RenderPipeline POSITION_COLOR_LINES_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_3;
	public static RenderPipeline POSITION_COLOR_LINES_TRANSLUCENT_LEQUAL_DEPTH_NO_CULL;
	public static RenderPipeline POSITION_COLOR_LINES_TRANSLUCENT_LEQUAL_DEPTH;
	public static RenderPipeline POSITION_COLOR_LINES_TRANSLUCENT;

	// POSITION_COLOR_LINES_MASA
	public static RenderPipeline POSITION_COLOR_LINES_MASA_NO_DEPTH_NO_CULL;
	public static RenderPipeline POSITION_COLOR_LINES_MASA_NO_DEPTH;
	public static RenderPipeline POSITION_COLOR_LINES_MASA_LEQUAL_DEPTH_OFFSET_1;
	public static RenderPipeline POSITION_COLOR_LINES_MASA_LEQUAL_DEPTH_OFFSET_2;
	public static RenderPipeline POSITION_COLOR_LINES_MASA_LEQUAL_DEPTH_OFFSET_3;
	public static RenderPipeline POSITION_COLOR_LINES_MASA_LEQUAL_DEPTH_NO_CULL;
	public static RenderPipeline POSITION_COLOR_LINES_MASA_LEQUAL_DEPTH;
	public static RenderPipeline POSITION_COLOR_LINES_MASA;

	// todo MINIHUD_SHAPE_LINES
	public static RenderPipeline MINIHUD_SHAPE_LINES_NO_DEPTH_OFFSET;
	public static RenderPipeline MINIHUD_SHAPE_LINES_NO_DEPTH;
	public static RenderPipeline MINIHUD_SHAPE_LINES_OFFSET_NO_CULL;
	public static RenderPipeline MINIHUD_SHAPE_LINES_OFFSET;
	public static RenderPipeline MINIHUD_SHAPE_LINES_NO_CULL;
	public static RenderPipeline MINIHUD_SHAPE_LINES;

	// todo POSITION_TEX_COLOR Snippet
	public static RenderPipeline.Snippet POSITION_TEX_COLOR_STAGE;
	public static RenderPipeline.Snippet POSITION_TEX_COLOR_TRANSLUCENT_STAGE;
	public static RenderPipeline.Snippet POSITION_TEX_COLOR_MASA_STAGE;

	// POSITION_TEX_COLOR_TRANSLUCENT
    public static RenderPipeline POSITION_TEX_COLOR_TRANSLUCENT_NO_DEPTH_NO_CULL;
    public static RenderPipeline POSITION_TEX_COLOR_TRANSLUCENT_NO_DEPTH;
    public static RenderPipeline POSITION_TEX_COLOR_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_1;
    public static RenderPipeline POSITION_TEX_COLOR_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_2;
    public static RenderPipeline POSITION_TEX_COLOR_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_3;
	public static RenderPipeline POSITION_TEX_COLOR_TRANSLUCENT_LEQUAL_DEPTH_NO_CULL;
    public static RenderPipeline POSITION_TEX_COLOR_TRANSLUCENT_LEQUAL_DEPTH;
	public static RenderPipeline POSITION_TEX_COLOR_TRANSLUCENT_DEPTH_MASK;
    public static RenderPipeline POSITION_TEX_COLOR_TRANSLUCENT;

    // POSITION_TEX_COLOR_MASA
    public static RenderPipeline POSITION_TEX_COLOR_MASA_NO_DEPTH_NO_CULL;
    public static RenderPipeline POSITION_TEX_COLOR_MASA_NO_DEPTH;
    public static RenderPipeline POSITION_TEX_COLOR_MASA_LEQUAL_DEPTH_OFFSET_1;
    public static RenderPipeline POSITION_TEX_COLOR_MASA_LEQUAL_DEPTH_OFFSET_2;
    public static RenderPipeline POSITION_TEX_COLOR_MASA_LEQUAL_DEPTH_OFFSET_3;
	public static RenderPipeline POSITION_TEX_COLOR_MASA_LEQUAL_DEPTH_NO_CULL;
    public static RenderPipeline POSITION_TEX_COLOR_MASA_LEQUAL_DEPTH;
	public static RenderPipeline POSITION_TEX_COLOR_MASA_DEPTH_MASK;
    public static RenderPipeline POSITION_TEX_COLOR_MASA;

	// todo DEBUG_LINES Snippet
	public static RenderPipeline.Snippet DEBUG_LINES_STAGE;
	public static RenderPipeline.Snippet DEBUG_LINES_TRANSLUCENT_STAGE;
	public static RenderPipeline.Snippet DEBUG_LINES_MASA_SIMPLE_STAGE;

    // DEBUG_LINES_TRANSLUCENT
    public static RenderPipeline DEBUG_LINES_TRANSLUCENT_NO_DEPTH_NO_CULL;
    public static RenderPipeline DEBUG_LINES_TRANSLUCENT_NO_DEPTH;
    public static RenderPipeline DEBUG_LINES_TRANSLUCENT_NO_CULL;
    public static RenderPipeline DEBUG_LINES_TRANSLUCENT_LEQUAL_DEPTH;
    public static RenderPipeline DEBUG_LINES_TRANSLUCENT_OFFSET_1;
    public static RenderPipeline DEBUG_LINES_TRANSLUCENT_OFFSET_2;
    public static RenderPipeline DEBUG_LINES_TRANSLUCENT_OFFSET_3;
    public static RenderPipeline DEBUG_LINES_TRANSLUCENT;

    // DEBUG_LINES_MASA_SIMPLE
    public static RenderPipeline DEBUG_LINES_MASA_SIMPLE_NO_DEPTH_NO_CULL;
    public static RenderPipeline DEBUG_LINES_MASA_SIMPLE_NO_DEPTH;
    public static RenderPipeline DEBUG_LINES_MASA_SIMPLE_NO_CULL;
    public static RenderPipeline DEBUG_LINES_MASA_SIMPLE_LEQUAL_DEPTH;
    public static RenderPipeline DEBUG_LINES_MASA_SIMPLE_OFFSET_1;
    public static RenderPipeline DEBUG_LINES_MASA_SIMPLE_OFFSET_2;
    public static RenderPipeline DEBUG_LINES_MASA_SIMPLE_OFFSET_3;
    public static RenderPipeline DEBUG_LINES_MASA_SIMPLE;

	// todo DEBUG_LINE_STRIP Snippet
	public static RenderPipeline.Snippet DEBUG_LINE_STRIP_STAGE;
	public static RenderPipeline.Snippet DEBUG_LINE_STRIP_TRANSLUCENT_STAGE;
	public static RenderPipeline.Snippet DEBUG_LINE_STRIP_MASA_SIMPLE_STAGE;

    // DEBUG_LINE_STRIP_TRANSLUCENT
    public static RenderPipeline DEBUG_LINE_STRIP_TRANSLUCENT_NO_DEPTH_NO_CULL;
    public static RenderPipeline DEBUG_LINE_STRIP_TRANSLUCENT_NO_DEPTH;
    public static RenderPipeline DEBUG_LINE_STRIP_TRANSLUCENT_NO_CULL;
    public static RenderPipeline DEBUG_LINE_STRIP_TRANSLUCENT_OFFSET_1;
    public static RenderPipeline DEBUG_LINE_STRIP_TRANSLUCENT_OFFSET_2;
    public static RenderPipeline DEBUG_LINE_STRIP_TRANSLUCENT_OFFSET_3;
    public static RenderPipeline DEBUG_LINE_STRIP_TRANSLUCENT;

    // DEBUG_LINE_STRIP_MASA_SIMPLE
    public static RenderPipeline DEBUG_LINE_STRIP_MASA_SIMPLE_NO_DEPTH_NO_CULL;
    public static RenderPipeline DEBUG_LINE_STRIP_MASA_SIMPLE_NO_DEPTH;
    public static RenderPipeline DEBUG_LINE_STRIP_MASA_SIMPLE_NO_CULL;
    public static RenderPipeline DEBUG_LINE_STRIP_MASA_SIMPLE_OFFSET_1;
    public static RenderPipeline DEBUG_LINE_STRIP_MASA_SIMPLE_OFFSET_2;
    public static RenderPipeline DEBUG_LINE_STRIP_MASA_SIMPLE_OFFSET_3;
    public static RenderPipeline DEBUG_LINE_STRIP_MASA_SIMPLE;

	// todo LEGACY_TERRAIN Snippet
	public static BindGroupLayout LEGACY_TERRAIN_GROUP;
	public static RenderPipeline.Snippet LEGACY_TERRAIN_STAGE;
	public static RenderPipeline.Snippet LEGACY_TERRAIN_TRANSLUCENT_STAGE;

	// LEGACY_TERRAIN
	public static RenderPipeline LEGACY_SOLID_TERRAIN;
	public static RenderPipeline LEGACY_WIREFRAME;
	public static RenderPipeline LEGACY_CUTOUT_TERRAIN;

	// LEGACY_TERRAIN_OFFSET
	public static RenderPipeline LEGACY_SOLID_TERRAIN_OFFSET;
	public static RenderPipeline LEGACY_WIREFRAME_OFFSET;
	public static RenderPipeline LEGACY_CUTOUT_TERRAIN_OFFSET;

	// LEGACY_TERRAIN_TRANSLUCENT
	public static RenderPipeline LEGACY_TRANSLUCENT;
	public static RenderPipeline LEGACY_TRANSLUCENT_OFFSET;
}
