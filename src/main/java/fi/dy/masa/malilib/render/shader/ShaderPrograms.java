package fi.dy.masa.malilib.render.shader;

import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public class ShaderPrograms
{
    public static final ShaderProgramKey POSITION = new ShaderProgramKey(Identifier.ofVanilla("core/position"), VertexFormats.POSITION, Defines.EMPTY);
    public static final ShaderProgramKey POSITION_COLOR = new ShaderProgramKey(Identifier.ofVanilla("core/position_color"), VertexFormats.POSITION_COLOR, Defines.EMPTY);
    public static final ShaderProgramKey POSITION_COLOR_TEX = new ShaderProgramKey(Identifier.ofVanilla("core/position_tex"), VertexFormats.POSITION_TEXTURE, Defines.EMPTY);
    public static final ShaderProgramKey POSITION_COLOR_LIGHTMAP = new ShaderProgramKey(Identifier.ofVanilla("core/position_color_lightmap"), VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, Defines.EMPTY);
    public static final ShaderProgramKey POSITION_COLOR_TEXTURE_LIGHTMAP = new ShaderProgramKey(Identifier.ofVanilla("core/position_color_tex_lightmap"), VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, Defines.EMPTY);
    public static final ShaderProgramKey RENDERTYPE_SOLID = new ShaderProgramKey(Identifier.ofVanilla("core/rendertype_solid"), VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, Defines.EMPTY);
    public static final ShaderProgramKey RENDERTYPE_CUTOUT_MIPPED = new ShaderProgramKey(Identifier.ofVanilla("core/rendertype_cutout_mipped"), VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, Defines.EMPTY);
    public static final ShaderProgramKey RENDERTYPE_CUTOUT = new ShaderProgramKey(Identifier.ofVanilla("core/rendertype_cutout"), VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, Defines.EMPTY);
    public static final ShaderProgramKey RENDERTYPE_TRANSLUCENT = new ShaderProgramKey(Identifier.ofVanilla("core/rendertype_translucent"), VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, Defines.EMPTY);
    public static final ShaderProgramKey RENDERTYPE_WATER_MASK = new ShaderProgramKey(Identifier.ofVanilla("core/rendertype_water_mask"), VertexFormats.POSITION, Defines.EMPTY);
    public static final ShaderProgramKey RENDERTYPE_OUTLINE = new ShaderProgramKey(Identifier.ofVanilla("core/rendertype_outline"), VertexFormats.POSITION_TEXTURE_COLOR, Defines.EMPTY);
    public static final ShaderProgramKey RENDERTYPE_TEXT = new ShaderProgramKey(Identifier.ofVanilla("core/rendertype_text"), VertexFormats.POSITION_COLOR_TEXTURE_LIGHT, Defines.EMPTY);
    public static final ShaderProgramKey RENDERTYPE_TEXT_BACKGROUND = new ShaderProgramKey(Identifier.ofVanilla("core/rendertype_text_background"), VertexFormats.POSITION_COLOR_LIGHT, Defines.EMPTY);
    public static final ShaderProgramKey RENDERTYPE_TEXT_INTENSITY = new ShaderProgramKey(Identifier.ofVanilla("core/rendertype_text_intensity"), VertexFormats.POSITION_COLOR_TEXTURE_LIGHT, Defines.EMPTY);
    public static final ShaderProgramKey RENDERTYPE_TEXT_SEE_THROUGH = new ShaderProgramKey(Identifier.ofVanilla("core/rendertype_text_see_through"), VertexFormats.POSITION_COLOR_TEXTURE_LIGHT, Defines.EMPTY);
    public static final ShaderProgramKey RENDERTYPE_TEXT_BACKGROUND_SEE_THROUGH = new ShaderProgramKey(Identifier.ofVanilla("core/rendertype_text_background_see_through"), VertexFormats.POSITION_COLOR_LIGHT, Defines.EMPTY);
    public static final ShaderProgramKey RENDERTYPE_TEXT_INTENSITY_SEE_THROUGH = new ShaderProgramKey(Identifier.ofVanilla("core/rendertype_text_intensity_see_through"), VertexFormats.POSITION_COLOR_TEXTURE_LIGHT, Defines.EMPTY);
    public static final ShaderProgramKey RENDERTYPE_LINES = new ShaderProgramKey(Identifier.ofVanilla("core/rendertype_lines"), VertexFormats.LINES, Defines.EMPTY);
    public static final ShaderProgramKey RENDERTYPE_GUI = new ShaderProgramKey(Identifier.ofVanilla("core/rendertype_gui"), VertexFormats.POSITION_COLOR, Defines.EMPTY);
    public static final ShaderProgramKey RENDERTYPE_GUI_OVERLAY = new ShaderProgramKey(Identifier.ofVanilla("core/rendertype_gui_overlay"), VertexFormats.POSITION_COLOR, Defines.EMPTY);
    public static final ShaderProgramKey RENDERTYPE_GUI_TEXT_HIGHLIGHT = new ShaderProgramKey(Identifier.ofVanilla("core/rendertype_gui_text_highlight"), VertexFormats.POSITION_COLOR, Defines.EMPTY);
    public static final ShaderProgramKey RENDERTYPE_GUI_GHOST_RECIPE_OVERLAY = new ShaderProgramKey(Identifier.ofVanilla("core/rendertype_gui_ghost_recipe_overlay"), VertexFormats.POSITION_COLOR, Defines.EMPTY);
}
