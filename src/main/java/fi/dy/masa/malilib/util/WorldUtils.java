package fi.dy.masa.malilib.util;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;

public class WorldUtils
{
    public static String getDimensionId(Level world)
    {
        Identifier id = world.dimension().identifier();
        return id != null ? id.getNamespace() + "_" + id.getPath() : "__fallback";
    }

    /**
     * Best name. Returns the integrated server world for the current dimension
     * in single player, otherwise just the client world.
     * @param mc ()
     * @return ()
     */
    @Nullable
    public static Level getBestWorld(Minecraft mc)
    {
        IntegratedServer server = mc.getSingleplayerServer();

        if (mc.level != null && server != null)
        {
            return server.getLevel(mc.level.dimension());
        }
        else
        {
            return mc.level;
        }
    }

    /**
     * Returns the requested chunk from the integrated server, if it's available.
     * Otherwise, returns the client world chunk.
     * @param chunkX ()
     * @param chunkZ ()
     * @param mc ()
     * @return ()
     */
    @Nullable
    public static LevelChunk getBestChunk(int chunkX, int chunkZ, Minecraft mc)
    {
        IntegratedServer server = mc.getSingleplayerServer();
        LevelChunk chunk = null;

        if (mc.level != null && server != null)
        {
            ServerLevel world = server.getLevel(mc.level.dimension());

            if (world != null)
            {
                chunk = world.getChunk(chunkX, chunkZ);
            }
        }

        if (chunk != null)
        {
            return chunk;
        }

        return mc.level != null ? mc.level.getChunk(chunkX, chunkZ) : null;
    }

    /**
     * Replaces getHighestNonEmptySectionYOffset() marked for removal from Minecraft and used across downstream mods
     * Returns Maximum Y Offset Value of a Chunk.
     */
    public static int getHighestSectionYOffset(ChunkAccess chunk)
    {
        int yMax = chunk.getHighestFilledSectionIndex();

        yMax = yMax == -1 ? chunk.getMinY() : SectionPos.sectionToBlockCoord(chunk.getSectionYFromSectionIndex(yMax));

        return yMax;
    }

    /**
     * Get the Dimension RegistryEntry based on Dimension Type.
     *
     * @param key ()
     * @param registry ()
     * @return ()
     */
    public static Holder<@NotNull DimensionType> getDimensionTypeEntry(DimensionType key, @Nonnull RegistryAccess registry)
    {
        try
        {
            return registry.lookupOrThrow(Registries.DIMENSION_TYPE).wrapAsHolder(key);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * Get the Dimension RegistryEntry based on Dimension ID.
     *
     * @param id ()
     * @param registry ()
     * @return ()
     */
    public static Holder<@NotNull DimensionType> getDimensionTypeEntry(Identifier id, @Nonnull RegistryAccess registry)
    {
        try
        {
            return registry.lookupOrThrow(Registries.DIMENSION_TYPE).get(id).orElseThrow();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * Get the Dimension RegistryEntry based on Dimension ID String.
     *
     * @param id ()
     * @param registry ()
     * @return ()
     */
    public static Holder<@NotNull DimensionType> getDimensionTypeEntry(String id, @Nonnull RegistryAccess registry)
    {
        try
        {
            return registry.lookupOrThrow(Registries.DIMENSION_TYPE).get(Objects.requireNonNull(Identifier.tryParse(id))).orElseThrow();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * Get the Biome Registry Entry from a Biome Registry Key.
     *
     * @param key ()
     * @param registry ()
     * @return ()
     */
    public static Holder<@NotNull Biome> getBiomeEntry(ResourceKey<@NotNull Biome> key, @Nonnull RegistryAccess registry)
    {
        try
        {
            return registry.lookupOrThrow(Registries.BIOME).getOrThrow(key);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * Get the Biome Registry Entry from a Biome ID.
     *
     * @param id ()
     * @param registry ()
     * @return ()
     */
    public static Holder<@NotNull Biome> getBiomeEntry(Identifier id, @Nonnull RegistryAccess registry)
    {
        try
        {
            return registry.lookupOrThrow(Registries.BIOME).get(id).orElseThrow();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * Get the Biome Registry Entry from a Biome ID String.
     *
     * @param id ()
     * @param registry ()
     * @return ()
     */
    public static Holder<@NotNull Biome> getBiomeEntry(String id, @Nonnull RegistryAccess registry)
    {
        try
        {
            return registry.lookupOrThrow(Registries.BIOME).get(Objects.requireNonNull(Identifier.tryParse(id))).orElseThrow();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * Get the PLAINS Biome Registry Entry.
     *
     * @param registry ()
     * @return ()
     */
    public static Holder<@NotNull Biome> getPlains(@Nonnull RegistryAccess registry)
    {
        return getBiomeEntry(Biomes.PLAINS, registry);
    }

    /**
     * Get the NETHER WASTES Biome Registry Entry.
     *
     * @param registry ()
     * @return ()
     */
    public static Holder<@NotNull Biome> getWastes(@Nonnull RegistryAccess registry)
    {
        return getBiomeEntry(Biomes.NETHER_WASTES, registry);
    }

    /**
     * Get the END Biome Registry Entry.
     *
     * @param registry ()
     * @return ()
     */
    public static Holder<@NotNull Biome> getTheEnd(@Nonnull RegistryAccess registry)
    {
        return getBiomeEntry(Biomes.THE_END, registry);
    }
}
