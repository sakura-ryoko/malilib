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
    public static ShaderPipeline POSITION_DEPTH;

    // POSITION_COLOR
    public static ShaderPipeline POSITION_COLOR_SIMPLE;
    public static ShaderPipeline POSITION_COLOR_DEPTH;

    // POSITION_TEX
    public static ShaderPipeline POSITION_TEX_SIMPLE;
    public static ShaderPipeline POSITION_TEX_DEPTH;

    // POSITION_TEX_COLOR
    public static ShaderPipeline POSITION_TEX_COLOR_SIMPLE;
    public static ShaderPipeline POSITION_TEX_COLOR_DEPTH;

    // LINES
    public static ShaderPipeline LINES_SIMPLE;
    public static ShaderPipeline LINES_DEPTH;

    // DEBUG_LINES
    public static ShaderPipeline DEBUG_LINES_SIMPLE;
    public static ShaderPipeline DEBUG_LINES_DEPTH;
}
