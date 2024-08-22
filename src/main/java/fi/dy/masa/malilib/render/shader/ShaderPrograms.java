package fi.dy.masa.malilib.render.shader;

import net.minecraft.class_10149;
import net.minecraft.class_10156;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public class ShaderPrograms
{
    public static final class_10156 POSITION_COLOR = new class_10156(Identifier.ofVanilla("core/position_color"), VertexFormats.POSITION_COLOR, class_10149.field_53930);
    public static final class_10156 POSITION_COLOR_TEX = new class_10156(Identifier.ofVanilla("core/position_tex"), VertexFormats.POSITION_TEXTURE, class_10149.field_53930);
    public static final class_10156 POSITION_COLOR_TEXTURE_LIGHT_NORMAL = new class_10156(Identifier.ofVanilla("core/terrain"), VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, class_10149.field_53930);
}
