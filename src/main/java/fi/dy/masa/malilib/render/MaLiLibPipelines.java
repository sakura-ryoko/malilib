package fi.dy.masa.malilib.render;

import net.minecraft.client.gl.ShaderPipeline;

public class MaLiLibPipelines
{
    // POSITION_COLOR
    public static ShaderPipeline POSITION_COLOR_DEPTH_TEST_OFF;
    public static ShaderPipeline POSITION_COLOR_DEPTH_TEST_LESSER;
    public static ShaderPipeline POSITION_COLOR_DEPTH_TEST_EQUAL;
    public static ShaderPipeline POSITION_COLOR_DEPTH_TEST_GREATER;

    // POSITION_TEX_COLOR
    public static ShaderPipeline POSITION_TEX_COLOR_DEPTH_TEST_OFF;
    public static ShaderPipeline POSITION_TEX_COLOR_DEPTH_TEST_LESSER;
    public static ShaderPipeline POSITION_TEX_COLOR_DEPTH_TEST_EQUAL;
    public static ShaderPipeline POSITION_TEX_COLOR_DEPTH_TEST_GREATER;

    // DEBUG_LINES
    public static ShaderPipeline DEBUG_LINES_BLEND_FUNC;
    public static ShaderPipeline DEBUG_LINES_DEPTH_TEST_OFF;
    public static ShaderPipeline DEBUG_LINES_DEPTH_TEST_LESSER;
    public static ShaderPipeline DEBUG_LINES_DEPTH_TEST_EQUAL;
    public static ShaderPipeline DEBUG_LINES_DEPTH_TEST_GREATER;
    public static ShaderPipeline DEBUG_LINES_CULLING_DEPTH_TEST_OFF;
    public static ShaderPipeline DEBUG_LINES_CULLING_DEPTH_TEST_LESSER;
    public static ShaderPipeline DEBUG_LINES_CULLING_DEPTH_TEST_EQUAL;
    public static ShaderPipeline DEBUG_LINES_CULLING_DEPTH_TEST_GREATER;
}
