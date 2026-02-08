package fi.dy.masa.malilib.mixin.render;

import com.mojang.blaze3d.platform.cursor.CursorType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.MaterialSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = GuiGraphics.class)
public interface IMixinGuiGraphics
{
//    @Accessor("vertexConsumers")
//    VertexConsumerProvider.Immediate malilib_getVertexConsumers();

	@Accessor("minecraft")
	Minecraft malilib_getClient();

    @Accessor("guiRenderState")
    GuiRenderState malilib_getRenderState();

    @Accessor("scissorStack")
    GuiGraphics.ScissorStack malilib_getScissorStack();

	@Accessor("mouseX")
	int malilib_getMouseX();

	@Accessor("mouseY")
	int malilib_getMouseY();

	@Accessor("pendingCursor")
	CursorType malilib_getCursor();

	@Accessor("materials")
	MaterialSet malilib_getSpriteHolder();

	@Accessor("guiSprites")
	TextureAtlas malilib_getSpriteAtlas();
}
