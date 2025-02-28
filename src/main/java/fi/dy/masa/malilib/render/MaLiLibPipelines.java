package fi.dy.masa.malilib.render;

import net.minecraft.client.gl.ShaderPipeline;

public class MaLiLibPipelines
{
    // STAGES
    public static ShaderPipeline.Stage POSITION_STAGE;
    public static ShaderPipeline.Stage POSITION_TEX_STAGE;

    // POSITION
    public static ShaderPipeline POSITION_SIMPLE;

    // POSITION_COLOR
    public static ShaderPipeline POSITION_COLOR_SIMPLE;

    // POSITION_TEX
    public static ShaderPipeline POSITION_TEX_SIMPLE;

    // POSITION_TEX_COLOR
    public static ShaderPipeline POSITION_TEX_COLOR_SIMPLE;

    // DEBUG_LINES
    public static ShaderPipeline DEBUG_LINES_SIMPLE;
}
