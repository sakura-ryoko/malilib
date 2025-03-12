package fi.dy.masa.malilib.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;

public class MaLiLibPipelines
{
    // STAGES
    public static RenderPipeline.Snippet POSITION_STAGE;
    public static RenderPipeline.Snippet POSITION_COLOR_STAGE;
    public static RenderPipeline.Snippet POSITION_TEX_STAGE;
    public static RenderPipeline.Snippet POSITION_TEX_COLOR_STAGE;
    public static RenderPipeline.Snippet LINES_STAGE;

    // POSITION
    public static RenderPipeline POSITION_SIMPLE;
    public static RenderPipeline POSITION_CULLING;
    public static RenderPipeline POSITION_LESSER_DEPTH;
    public static RenderPipeline POSITION_LEQUAL_DEPTH;
    public static RenderPipeline POSITION_EQUAL_DEPTH;
    public static RenderPipeline POSITION_GREATER_DEPTH;

    // POSITION_COLOR
    public static RenderPipeline POSITION_COLOR_SIMPLE;
    public static RenderPipeline POSITION_COLOR_CULLING;
    public static RenderPipeline POSITION_COLOR_LESSER_DEPTH;
    public static RenderPipeline POSITION_COLOR_LEQUAL_DEPTH;
    public static RenderPipeline POSITION_COLOR_EQUAL_DEPTH;
    public static RenderPipeline POSITION_COLOR_GREATER_DEPTH;

    // POSITION_TEX
    public static RenderPipeline POSITION_TEX_SIMPLE;
    public static RenderPipeline POSITION_TEX_CULLING;
    public static RenderPipeline POSITION_TEX_LESSER_DEPTH;
    public static RenderPipeline POSITION_TEX_LEQUAL_DEPTH;
    public static RenderPipeline POSITION_TEX_EQUAL_DEPTH;
    public static RenderPipeline POSITION_TEX_GREATER_DEPTH;

    // POSITION_TEX_COLOR
    public static RenderPipeline POSITION_TEX_COLOR_SIMPLE;
    public static RenderPipeline POSITION_TEX_COLOR_CULLING;
    public static RenderPipeline POSITION_TEX_COLOR_LESSER_DEPTH;
    public static RenderPipeline POSITION_TEX_COLOR_LEQUAL_DEPTH;
    public static RenderPipeline POSITION_TEX_COLOR_EQUAL_DEPTH;
    public static RenderPipeline POSITION_TEX_COLOR_GREATER_DEPTH;

    // LINES
    public static RenderPipeline LINES_SIMPLE;
    public static RenderPipeline LINES_CULLING;
    public static RenderPipeline LINES_NO_DEPTH;
//    public static RenderPipeline LINES_LESSER_DEPTH;
//    public static RenderPipeline LINES_LEQUAL_DEPTH;
//    public static RenderPipeline LINES_EQUAL_DEPTH;
//    public static RenderPipeline LINES_GREATER_DEPTH;

    // DEBUG_LINES
    public static RenderPipeline DEBUG_LINES_SIMPLE;
//    public static RenderPipeline DEBUG_LINES_CULLING;
//    public static RenderPipeline DEBUG_LINES_LESSER_DEPTH;
//    public static RenderPipeline DEBUG_LINES_LEQUAL_DEPTH;
//    public static RenderPipeline DEBUG_LINES_EQUAL_DEPTH;
//    public static RenderPipeline DEBUG_LINES_GREATER_DEPTH;
}
