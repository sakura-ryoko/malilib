package fi.dy.masa.malilib.mixin.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.cursor.Cursor;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.SpriteHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = DrawContext.class)
public interface IMixinDrawContext
{
//    @Accessor("vertexConsumers")
//    VertexConsumerProvider.Immediate malilib_getVertexConsumers();

	@Accessor("client")
	MinecraftClient malilib_getClient();

    @Accessor("state")
    GuiRenderState malilib_getRenderState();

    @Accessor("scissorStack")
    DrawContext.ScissorStack malilib_getScissorStack();

	@Accessor("mouseX")
	int malilib_getMouseX();

	@Accessor("mouseY")
	int malilib_getMouseY();

	@Accessor("cursor")
	Cursor malilib_getCursor();

	@Accessor("spriteHolder")
	SpriteHolder malilib_getSpriteHolder();

	@Accessor("spriteAtlasTexture")
	SpriteAtlasTexture malilib_getSpriteAtlas();
}
