package fi.dy.masa.malilib.render;

import com.mojang.blaze3d.textures.GpuTexture;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.interfaces.IClientTickHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import javax.annotation.Nonnull;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * This system is meant to be a ticked Cache of NativeImageBackedTexture's
 * for use in POSITION_TEX rendering; and periodically "reloads" the textures.
 */
public class TexturePreloadManager implements IClientTickHandler
{
    public static final TexturePreloadManager INSTANCE = new TexturePreloadManager();

    private final HashMap<Identifier, NativeImageBackedTexture> textures;
    private final HashMap<Identifier, Integer> textureIds;
    private final HashMap<Identifier, Long> textureTick;
    private final HashMap<Identifier, Integer> texturePreloads;
    private final long tickTimer = 4000L;
    private final long tickReloadInterval = 30000L;
    private final long tickPreloadTimer = this.tickTimer;
    private final int tickPreloadCount = 5;
    private long lastTick;

    public TexturePreloadManager()
    {
        this.textures = new HashMap<>();
        this.textureIds = new HashMap<>();
        this.textureTick = new HashMap<>();
        this.texturePreloads = new HashMap<>();
    }

    @Override
    public void onClientTick(MinecraftClient mc)
    {
        long now = System.currentTimeMillis();

        if (now - this.lastTick > this.tickTimer)
        {
            this.onTickInternal(now);
        }

        this.lastTick = now;
    }

    private void onTickInternal(long now)
    {
        List<Identifier> remove = new ArrayList<>();

        this.textureTick.forEach(
                (id, tick) ->
                {
                    if (this.tickTextureInternal(id, now, tick))
                    {
                        remove.add(id);
                    }
                });

        for (Identifier id : remove)
        {
            this.unloadTexture(id);
        }
    }

    private boolean tickTextureInternal(Identifier id, long now, long lastTick)
    {
        if (this.shouldReload(id, now, lastTick))
        {
            MaLiLib.debugLog("Textures: Reloading texture [{}] after {} ms.", id.toString(), now - lastTick);
            this.reloadTexture(id);
        }

        return false;
    }

    private boolean shouldReload(Identifier id, long now, long lastTick)
    {
        int count = this.texturePreloads.getOrDefault(id, -1);

        if (count > this.tickPreloadCount)
        {
            return false;
        }

        if ((now - this.lastTick) > this.tickPreloadTimer ||
            (now - lastTick) > this.tickReloadInterval ||
            this.lastTick > now)
        {
            synchronized (this.texturePreloads)
            {
                if (count < 1) count = 1;

                this.texturePreloads.put(id, count);
            }

            return true;
        }

        return false;
    }

    public void reloadTexture(Identifier id) throws RuntimeException
    {
        if (this.hasTexture(id))
        {
            synchronized (this.textures)
            {
                NativeImageBackedTexture old = this.textures.remove(id);
                old.close();
            }

            try (NativeImageBackedTexture newTex = this.tryLoadTexture(id).orElse(null))
            {
                if (newTex != null)
                {
                    newTex.upload();

                    synchronized (this.textures)
                    {
                        this.textures.put(id, newTex);
                    }

                    MaLiLib.debugLog("Texture: [{}] has been reloaded.", id.toString());
                }
                else
                {
                    throw new RuntimeException("Texture: ["+id.toString()+"] has failed to load.");
                }
            }
        }
    }

    public boolean hasTexture(Identifier id)
    {
        return this.textures.containsKey(id);
    }

    private void putTextureInternal(Identifier id, @Nonnull NativeImageBackedTexture tex, int textureId)
    {
        this.ensureTextureId(textureId);

        synchronized (this.textures)
        {
            this.textures.put(id, tex);
        }

        synchronized (this.textureIds)
        {
            this.textureIds.put(id, textureId);
        }

        synchronized (this.textureTick)
        {
            this.textureTick.put(id, System.currentTimeMillis());
        }
    }

    public boolean registerTexture(Identifier texture, int textureId) throws RuntimeException
    {
        this.ensureTextureId(textureId);

        if (this.hasTexture(texture))
        {
            throw new RuntimeException("Texture "+texture.toString()+" has already been registered");
        }

        try (NativeImageBackedTexture tex = this.tryLoadTexture(texture).orElse(null))
        {
            if (tex != null)
            {
                tex.upload();
                this.putTextureInternal(texture, tex, textureId);
                RenderUtils.tex().registerTexture(texture, tex);
                return true;
            }
            else
            {
                throw new RuntimeException("Failed to load texture ["+texture.toString()+"]");
            }
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.warn("Exception loading texture [{}], Exception: {}", texture.toString(), err.getMessage());
            return false;
        }
    }

    public boolean setTextureId(Identifier texture, int textureId)
    {
        if (this.hasTexture(texture))
        {
            synchronized (this.textureIds)
            {
                this.textureIds.put(texture, textureId);
            }

            return true;
        }

        return false;
    }

    public Optional<GpuTexture> getTexture(Identifier texture) throws RuntimeException
    {
        if (this.hasTexture(texture))
        {
            try
            {
                return Optional.of(this.textures.get(texture).getGlTexture());
            }
            catch (Exception err)
            {
                MaLiLib.LOGGER.error("Texture [{}] failed to upload; exception: {}", texture.toString(), err.getMessage());
            }
        }
        else
        {
            MaLiLib.LOGGER.warn("Texture [{}] was not registered", texture.toString());
        }

        return Optional.empty();
    }

    public Optional<Integer> getTextureId(Identifier texture)
    {
        if (this.hasTexture(texture) && this.textureIds.containsKey(texture))
        {
            int result = this.textureIds.getOrDefault(texture, -1);

            if (result > -1)
            {
                return Optional.of(result);
            }
        }

        return Optional.empty();
    }

    private Optional<NativeImageBackedTexture> tryLoadTexture(Identifier texture)
    {
        try
        {
            InputStream inputStream = RenderUtils.mc().getResourceManager().open(texture);

            try (NativeImage image = NativeImage.read(inputStream))
            {
                return Optional.of(new NativeImageBackedTexture(texture::toString, image.getWidth(), image.getHeight(), false));
            }
            catch (Exception err)
            {
                MaLiLib.LOGGER.error("Failed to read texture: '{}'; Exception: {}", texture.toString(), err.getMessage());
            }
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("Error opening input stream for texture: '{}'; Exception: {}", texture.toString(), err.getMessage());
        }

        return Optional.empty();
    }

    private void unloadTexture(Identifier id)
    {
        synchronized (this.textureTick)
        {
            this.textureTick.remove(id);
        }

        synchronized (this.textureIds)
        {
            this.textureIds.remove(id);
        }

        synchronized (this.texturePreloads)
        {
            this.texturePreloads.remove(id);
        }

        synchronized (this.textures)
        {
            NativeImageBackedTexture remove = this.textures.remove(id);
            remove.close();
        }

        RenderUtils.tex().destroyTexture(id);
    }

    public void reset()
    {
        this.textureIds.forEach((id, texId) -> this.unloadTexture(id));

        // Should already be empty
        this.texturePreloads.clear();
        this.textureTick.clear();
        this.textureIds.clear();
        this.textures.clear();
    }

    private void ensureTextureId(int textureId)
    {
        if (textureId < 0 || textureId > 12)
        {
            throw new RuntimeException("Invalid textureId of: "+textureId);
        }
    }
}
