package fi.dy.masa.malilib.render.uniform;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.TextureFilteringMethod;
import net.minecraft.client.renderer.MappableRingBuffer;

import fi.dy.masa.malilib.compat.iris.IrisCompat;

public class ChunkFixUniform implements AutoCloseable
{
	private static final int UBO_SIZE = new Std140SizeCalculator().putIVec2().putFloat().putInt().putInt().get();
	private final MappableRingBuffer ubo = new MappableRingBuffer(() -> "ChunkFix UBO", 130, UBO_SIZE);

	/**
	 * Fill the UBO Buffer
	 *
	 * @param atlasWidth  ()
	 * @param atlasHeight ()
	 * @param chunkVisibility ()
	 */
	public void fillBuffer(int atlasWidth, int atlasHeight, float chunkVisibility)
			throws IllegalArgumentException
	{
		if (atlasWidth <= 0 || atlasHeight <= 0)
		{
			throw new IllegalArgumentException("atlasWidth and atlasHeight must be positive");
		}

		final int useRGSS = Minecraft.getInstance().options.textureFiltering().get() == TextureFilteringMethod.RGSS ? 1 : 0;
		final int hasShadersOn = IrisCompat.isShaderActive() ? 1 : 0;

		try (GpuBuffer.MappedView mappedView = RenderSystem.getDevice().createCommandEncoder()
		                                                   .mapBuffer(this.ubo.currentBuffer(), false, true))
		{
			Std140Builder.intoBuffer(mappedView.data()).putIVec2(atlasWidth, atlasHeight).putFloat(chunkVisibility).putInt(useRGSS).putInt(hasShadersOn);
		}
	}

	/**
	 * Draw the UBO buffer to a render pass
	 * @param pass ()
	 */
	public void drawPass(@Nonnull RenderPass pass)
	{
		pass.setUniform("ChunkFix", this.getCurrentBuffer());
	}

	/**
	 * Get the 'currentBuffer' from the Ring Buffer.
	 * @return ()
	 */
	public GpuBuffer getCurrentBuffer()
	{
		return this.ubo.currentBuffer();
	}

	/**
	 * Call at the end if the Frame Pass {@link RenderSystem} flipFrame()
	 */
	public void endFrame()
	{
		this.ubo.rotate();
	}

	@Override
	public void close() throws Exception
	{
		this.ubo.close();
	}
}
