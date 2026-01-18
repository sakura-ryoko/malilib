package fi.dy.masa.malilib.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;

/**
 * This is meant as a central place to manage all custom Render Pipelines
 */
public class MaLiLibPipelines
{
    // todo POSITION Snippet
    public static RenderPipeline.Snippet POSITION_STAGE;
    public static RenderPipeline.Snippet POSITION_TRANSLUCENT_STAGE;
    public static RenderPipeline.Snippet POSITION_MASA_STAGE;

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
//	public static RenderPipeline POSITION_COLOR_TRANSLUCENT_LESS_DEPTH;
    public static RenderPipeline POSITION_COLOR_TRANSLUCENT_GREATER_DEPTH;
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
//	public static RenderPipeline POSITION_COLOR_MASA_LESS_DEPTH;
    public static RenderPipeline POSITION_COLOR_MASA_GREATER_DEPTH;
    public static RenderPipeline POSITION_COLOR_MASA_DEPTH_MASK;
    public static RenderPipeline POSITION_COLOR_MASA;

	// TEXT_PLATE
	public static RenderPipeline TEXT_PLATE_MASA_NO_DEPTH;
	public static RenderPipeline TEXT_PLATE_MASA;

	// todo MINIHUD_SHAPE
	public static RenderPipeline MINIHUD_SHAPE_NO_DEPTH_OFFSET;
	public static RenderPipeline MINIHUD_SHAPE_NO_DEPTH;
	public static RenderPipeline MINIHUD_SHAPE_OFFSET_NO_CULL;
	public static RenderPipeline MINIHUD_SHAPE_OFFSET;
	public static RenderPipeline MINIHUD_SHAPE_DEPTH_MASK;
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
	//	public static RenderPipeline POSITION_COLOR_LINES_TRANSLUCENT_LESS_DEPTH;
	public static RenderPipeline POSITION_COLOR_LINES_TRANSLUCENT_GREATER_DEPTH;
	public static RenderPipeline POSITION_COLOR_LINES_TRANSLUCENT_DEPTH_MASK;
	public static RenderPipeline POSITION_COLOR_LINES_TRANSLUCENT;

	// POSITION_COLOR_LINES_MASA
	public static RenderPipeline POSITION_COLOR_LINES_MASA_NO_DEPTH_NO_CULL;
	public static RenderPipeline POSITION_COLOR_LINES_MASA_NO_DEPTH;
	public static RenderPipeline POSITION_COLOR_LINES_MASA_LEQUAL_DEPTH_OFFSET_1;
	public static RenderPipeline POSITION_COLOR_LINES_MASA_LEQUAL_DEPTH_OFFSET_2;
	public static RenderPipeline POSITION_COLOR_LINES_MASA_LEQUAL_DEPTH_OFFSET_3;
	public static RenderPipeline POSITION_COLOR_LINES_MASA_LEQUAL_DEPTH_NO_CULL;
	public static RenderPipeline POSITION_COLOR_LINES_MASA_LEQUAL_DEPTH;
	//	public static RenderPipeline POSITION_COLOR_LINES_MASA_LESS_DEPTH;
	public static RenderPipeline POSITION_COLOR_LINES_MASA_GREATER_DEPTH;
	public static RenderPipeline POSITION_COLOR_LINES_MASA_DEPTH_MASK;
	public static RenderPipeline POSITION_COLOR_LINES_MASA;

	// todo MINIHUD_SHAPE_LINES
	public static RenderPipeline MINIHUD_SHAPE_LINES_NO_DEPTH_OFFSET;
	public static RenderPipeline MINIHUD_SHAPE_LINES_NO_DEPTH;
	public static RenderPipeline MINIHUD_SHAPE_LINES_OFFSET_NO_CULL;
	public static RenderPipeline MINIHUD_SHAPE_LINES_OFFSET;
	public static RenderPipeline MINIHUD_SHAPE_LINES_DEPTH_MASK;
	public static RenderPipeline MINIHUD_SHAPE_LINES_NO_CULL;
	public static RenderPipeline MINIHUD_SHAPE_LINES;

	// todo POSITION_TEX Snippet
	public static RenderPipeline.Snippet POSITION_TEX_STAGE;
	public static RenderPipeline.Snippet POSITION_TEX_TRANSLUCENT_STAGE;
	public static RenderPipeline.Snippet POSITION_TEX_OVERLAY_STAGE;
	public static RenderPipeline.Snippet POSITION_TEX_MASA_STAGE;

	// todo POSITION_TEX_COLOR Snippet
	public static RenderPipeline.Snippet POSITION_TEX_COLOR_STAGE;
	public static RenderPipeline.Snippet POSITION_TEX_COLOR_TRANSLUCENT_STAGE;
	public static RenderPipeline.Snippet POSITION_TEX_COLOR_OVERLAY_STAGE;
	public static RenderPipeline.Snippet POSITION_TEX_COLOR_MASA_STAGE;

	// POSITION_TEX_COLOR_TRANSLUCENT
    public static RenderPipeline POSITION_TEX_COLOR_TRANSLUCENT_NO_DEPTH_NO_CULL;
    public static RenderPipeline POSITION_TEX_COLOR_TRANSLUCENT_NO_DEPTH;
    public static RenderPipeline POSITION_TEX_COLOR_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_1;
    public static RenderPipeline POSITION_TEX_COLOR_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_2;
    public static RenderPipeline POSITION_TEX_COLOR_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_3;
	public static RenderPipeline POSITION_TEX_COLOR_TRANSLUCENT_LEQUAL_DEPTH_NO_CULL;
    public static RenderPipeline POSITION_TEX_COLOR_TRANSLUCENT_LEQUAL_DEPTH;
    public static RenderPipeline POSITION_TEX_COLOR_TRANSLUCENT_GREATER_DEPTH;
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
    public static RenderPipeline POSITION_TEX_COLOR_MASA_GREATER_DEPTH;
    public static RenderPipeline POSITION_TEX_COLOR_MASA_DEPTH_MASK;
    public static RenderPipeline POSITION_TEX_COLOR_MASA;

	// todo LINES Snippet
	public static RenderPipeline.Snippet LINES_STAGE;
	public static RenderPipeline.Snippet LINES_TRANSLUCENT_STAGE;
	public static RenderPipeline.Snippet LINES_MASA_SIMPLE_STAGE;

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

	// todo GUI
    public static RenderPipeline GUI_OVERLAY;
    public static RenderPipeline GUI_TEXTURED_OVERLAY;

	// todo TERRAIN Snippet
	public static RenderPipeline.Snippet TERRAIN_STAGE;
	public static RenderPipeline.Snippet TERRAIN_TRANSLUCENT_STAGE;
	public static RenderPipeline.Snippet TERRAIN_MASA_STAGE;

	// TERRAIN_MASA
	public static RenderPipeline SOLID_TERRAIN_MASA;
	public static RenderPipeline WIREFRAME_MASA;
	public static RenderPipeline CUTOUT_TERRAIN_MASA;
	public static RenderPipeline TRANSLUCENT_MASA;
	public static RenderPipeline TRIPWIRE_TERRAIN_MASA;

	// TERRAIN_MASA_OFFSET
	public static RenderPipeline SOLID_TERRAIN_MASA_OFFSET;
	public static RenderPipeline WIREFRAME_MASA_OFFSET;
	public static RenderPipeline CUTOUT_TERRAIN_MASA_OFFSET;
	public static RenderPipeline TRANSLUCENT_MASA_OFFSET;
	public static RenderPipeline TRIPWIRE_TERRAIN_MASA_OFFSET;

	// todo BLOCK Snippet
	public static RenderPipeline.Snippet BLOCK_STAGE;
	public static RenderPipeline.Snippet BLOCK_TRANSLUCENT_STAGE;
	public static RenderPipeline.Snippet BLOCK_MASA_STAGE;

	// BLOCK_MASA
	public static RenderPipeline SOLID_BLOCK_MASA;
	public static RenderPipeline CUTOUT_BLOCK_MASA;
	public static RenderPipeline TRIPWIRE_BLOCK_MASA;

	// BLOCK_MASA_OFFSET
	public static RenderPipeline SOLID_BLOCK_MASA_OFFSET;
	public static RenderPipeline CUTOUT_BLOCK_MASA_OFFSET;
	public static RenderPipeline TRIPWIRE_BLOCK_MASA_OFFSET;

	// todo LEGACY_TERRAIN Snippet
	public static RenderPipeline.Snippet LEGACY_TERRAIN_STAGE;
	public static RenderPipeline.Snippet LEGACY_TERRAIN_TRANSLUCENT_STAGE;
	public static RenderPipeline.Snippet LEGACY_TERRAIN_MASA_STAGE;

	// LEGACY_TERRAIN_MASA
	public static RenderPipeline LEGACY_SOLID_TERRAIN_MASA;
	public static RenderPipeline LEGACY_WIREFRAME_MASA;
	public static RenderPipeline LEGACY_CUTOUT_TERRAIN_MASA;
	public static RenderPipeline LEGACY_TRANSLUCENT_MASA;
	public static RenderPipeline LEGACY_TRIPWIRE_TERRAIN_MASA;

	// LEGACY_TERRAIN_MASA_OFFSET
	public static RenderPipeline LEGACY_SOLID_TERRAIN_MASA_OFFSET;
	public static RenderPipeline LEGACY_WIREFRAME_MASA_OFFSET;
	public static RenderPipeline LEGACY_CUTOUT_TERRAIN_MASA_OFFSET;
	public static RenderPipeline LEGACY_TRANSLUCENT_MASA_OFFSET;
	public static RenderPipeline LEGACY_TRIPWIRE_TERRAIN_MASA_OFFSET;
}
