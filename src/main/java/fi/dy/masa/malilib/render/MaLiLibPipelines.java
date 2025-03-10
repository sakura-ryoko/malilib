package fi.dy.masa.malilib.render;

import net.minecraft.client.gl.ShaderPipeline;

public class MaLiLibPipelines
{
    // STAGES
    public static ShaderPipeline.Stage POSITION_STAGE;
    public static ShaderPipeline.Stage POSITION_TEX_STAGE;
    public static ShaderPipeline.Stage LINES_STAGE;

    // POSITION
    public static ShaderPipeline POSITION_SIMPLE;
    public static ShaderPipeline POSITION_CULLING;
    public static ShaderPipeline POSITION_LESSER_DEPTH;
    public static ShaderPipeline POSITION_GREATER_DEPTH;

    // POSITION_COLOR
    public static ShaderPipeline POSITION_COLOR_SIMPLE;
    public static ShaderPipeline POSITION_COLOR_CULLING;
    public static ShaderPipeline POSITION_COLOR_LESSER_DEPTH;
    public static ShaderPipeline POSITION_COLOR_GREATER_DEPTH;

    // POSITION_TEX
    public static ShaderPipeline POSITION_TEX_SIMPLE;
    public static ShaderPipeline POSITION_TEX_CULLING;
    public static ShaderPipeline POSITION_TEX_LESSER_DEPTH;
    public static ShaderPipeline POSITION_TEX_GREATER_DEPTH;

    // POSITION_TEX_COLOR
    public static ShaderPipeline POSITION_TEX_COLOR_SIMPLE;
    public static ShaderPipeline POSITION_TEX_COLOR_CULLING;
    public static ShaderPipeline POSITION_TEX_COLOR_LESSER_DEPTH;
    public static ShaderPipeline POSITION_TEX_COLOR_GREATER_DEPTH;

    // LINES
    public static ShaderPipeline LINES_SIMPLE;
    public static ShaderPipeline LINES_CULLING;
    public static ShaderPipeline LINES_LESSER_DEPTH;
    public static ShaderPipeline LINES_GREATER_DEPTH;

    // DEBUG_LINES
    public static ShaderPipeline DEBUG_LINES_SIMPLE;
    public static ShaderPipeline DEBUG_LINES_CULLING;
    public static ShaderPipeline DEBUG_LINES_LESSER_DEPTH;
    public static ShaderPipeline DEBUG_LINES_GREATER_DEPTH;
}
