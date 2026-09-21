package fi.dy.masa.malilib.mixin.render;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.ChunkRenderMatrices;

import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.textures.GpuSampler;
import net.minecraft.client.renderer.chunk.ChunkSectionLayerGroup;
import net.minecraft.client.renderer.oit.OitStage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.compat.ModIds;
import fi.dy.masa.malilib.compat.iris.IrisCompat;
import fi.dy.masa.malilib.event.RenderEventHandler;

@Restriction(require = @Condition(value = ModIds.sodium))
@Mixin(value = SodiumWorldRenderer.class)
public abstract class MixinSodiumWorldRenderer_drawLayerGroup
{
	@Inject(method = "drawChunkLayer", at = @At("TAIL"))
	private void malilib_onSodiumDrawChunkLayer(RenderPass pass, ChunkSectionLayerGroup group, ChunkRenderMatrices matrices, double x, double y, double z, GpuSampler terrainSampler, OitStage stage, CallbackInfo ci)
	{
		if (IrisCompat.isShaderActive())
		{
			((RenderEventHandler) RenderEventHandler.getInstance()).runWorldLayerGroups();
		}
	}
}
