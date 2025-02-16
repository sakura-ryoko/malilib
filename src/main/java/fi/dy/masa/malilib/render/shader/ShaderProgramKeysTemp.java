package fi.dy.masa.malilib.render.shader;

import net.minecraft.client.gl.ShaderProgramLayer;
import net.minecraft.client.gl.ShaderProgramLayers;

@Deprecated(forRemoval = true)
public class ShaderProgramKeysTemp
{
    // DEBUG_LINES
    public static final ShaderProgramLayer POSITION_COLOR_LEGACY            = ShaderProgramLayers.DEBUG_LINE_STRIP;
    public static final ShaderProgramLayer POSITION_TEX_LEGACY              = ShaderProgramLayers.POSITION_TEX_PANORAMA;
    // GUI_TEXTURED_OVERLAY
    public static final ShaderProgramLayer POSITION_TEX_COLOR_LEGACY        = ShaderProgramLayers.GUI_TEXTURED_OVERLAY;
    // RENDER_TYPES
    public static final ShaderProgramLayer RENDER_TYPE_SOLID_LEGACY         = ShaderProgramLayers.SOLID;
    public static final ShaderProgramLayer RENDER_TYPE_CUTOUT_LEGACY        = ShaderProgramLayers.CUTOUT;
    public static final ShaderProgramLayer RENDER_TYPE_CUTOUT_MIPPED_LEGACY = ShaderProgramLayers.CUTOUT_MIPPED;
    public static final ShaderProgramLayer RENDER_TYPE_TRANSLUCENT_LEGACY   = ShaderProgramLayers.TRANSLUCENT;
}
