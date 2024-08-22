package fi.dy.masa.malilib.render.shader;

import net.minecraft.class_10149;
import net.minecraft.class_10156;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public class ShaderPrograms
{
    public static final class_10156 POSITION = new class_10156(Identifier.ofVanilla("core/position"), VertexFormats.POSITION, class_10149.field_53930);
    public static final class_10156 POSITION_COLOR = new class_10156(Identifier.ofVanilla("core/position_color"), VertexFormats.POSITION_COLOR, class_10149.field_53930);
    public static final class_10156 POSITION_COLOR_TEX = new class_10156(Identifier.ofVanilla("core/position_tex"), VertexFormats.POSITION_TEXTURE, class_10149.field_53930);
    public static final class_10156 POSITION_COLOR_LIGHTMAP = new class_10156(Identifier.ofVanilla("core/position_color_lightmap"), VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, class_10149.field_53930);
    public static final class_10156 POSITION_COLOR_TEXTURE_LIGHTMAP = new class_10156(Identifier.ofVanilla("core/position_color_tex_lightmap"), VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, class_10149.field_53930);
    public static final class_10156 RENDERTYPE_SOLID = new class_10156(Identifier.ofVanilla("core/rendertype_solid"), VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, class_10149.field_53930);
    public static final class_10156 RENDERTYPE_CUTOUT_MIPPED = new class_10156(Identifier.ofVanilla("core/rendertype_cutout_mipped"), VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, class_10149.field_53930);
    public static final class_10156 RENDERTYPE_CUTOUT = new class_10156(Identifier.ofVanilla("core/rendertype_cutout"), VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, class_10149.field_53930);
    public static final class_10156 RENDERTYPE_TRANSLUCENT = new class_10156(Identifier.ofVanilla("core/rendertype_translucent"), VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, class_10149.field_53930);
    public static final class_10156 RENDERTYPE_WATER_MASK = new class_10156(Identifier.ofVanilla("core/rendertype_water_mask"), VertexFormats.POSITION, class_10149.field_53930);
    public static final class_10156 RENDERTYPE_OUTLINE = new class_10156(Identifier.ofVanilla("core/rendertype_outline"), VertexFormats.POSITION_TEXTURE_COLOR, class_10149.field_53930);
    public static final class_10156 RENDERTYPE_TEXT = new class_10156(Identifier.ofVanilla("core/rendertype_text"), VertexFormats.POSITION_COLOR_TEXTURE_LIGHT, class_10149.field_53930);
    public static final class_10156 RENDERTYPE_TEXT_BACKGROUND = new class_10156(Identifier.ofVanilla("core/rendertype_text_background"), VertexFormats.POSITION_COLOR_LIGHT, class_10149.field_53930);
    public static final class_10156 RENDERTYPE_TEXT_INTENSITY = new class_10156(Identifier.ofVanilla("core/rendertype_text_intensity"), VertexFormats.POSITION_COLOR_TEXTURE_LIGHT, class_10149.field_53930);
    public static final class_10156 RENDERTYPE_TEXT_SEE_THROUGH = new class_10156(Identifier.ofVanilla("core/rendertype_text_see_through"), VertexFormats.POSITION_COLOR_TEXTURE_LIGHT, class_10149.field_53930);
    public static final class_10156 RENDERTYPE_TEXT_BACKGROUND_SEE_THROUGH = new class_10156(Identifier.ofVanilla("core/rendertype_text_background_see_through"), VertexFormats.POSITION_COLOR_LIGHT, class_10149.field_53930);
    public static final class_10156 RENDERTYPE_TEXT_INTENSITY_SEE_THROUGH = new class_10156(Identifier.ofVanilla("core/rendertype_text_intensity_see_through"), VertexFormats.POSITION_COLOR_TEXTURE_LIGHT, class_10149.field_53930);
    public static final class_10156 RENDERTYPE_LINES = new class_10156(Identifier.ofVanilla("core/rendertype_lines"), VertexFormats.LINES, class_10149.field_53930);
    public static final class_10156 RENDERTYPE_GUI = new class_10156(Identifier.ofVanilla("core/rendertype_gui"), VertexFormats.POSITION_COLOR, class_10149.field_53930);
    public static final class_10156 RENDERTYPE_GUI_OVERLAY = new class_10156(Identifier.ofVanilla("core/rendertype_gui_overlay"), VertexFormats.POSITION_COLOR, class_10149.field_53930);
    public static final class_10156 RENDERTYPE_GUI_TEXT_HIGHLIGHT = new class_10156(Identifier.ofVanilla("core/rendertype_gui_text_highlight"), VertexFormats.POSITION_COLOR, class_10149.field_53930);
    public static final class_10156 RENDERTYPE_GUI_GHOST_RECIPE_OVERLAY = new class_10156(Identifier.ofVanilla("core/rendertype_gui_ghost_recipe_overlay"), VertexFormats.POSITION_COLOR, class_10149.field_53930);
}
