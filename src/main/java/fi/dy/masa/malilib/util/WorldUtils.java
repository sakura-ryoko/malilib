package fi.dy.masa.malilib.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;

public class WorldUtils
{
    public static String getDimensionId(World world)
    {
        Identifier id = world.getRegistryKey().getValue();
        return id != null ? id.getNamespace() + "_" + id.getPath() : "__fallback";
    }

    /**
     * Best name. Returns the integrated server world for the current dimension
     * in single player, otherwise just the client world.
     * @param mc
     * @return
     */
    @Nullable
    public static World getBestWorld(MinecraftClient mc)
    {
        IntegratedServer server = mc.getServer();

        if (mc.world != null && server != null)
        {
            return server.getWorld(mc.world.getRegistryKey());
        }
        else
        {
            return mc.world;
        }
    }

    /**
     * Returns the requested chunk from the integrated server, if it's available.
     * Otherwise returns the client world chunk.
     * @param chunkX
     * @param chunkZ
     * @param mc
     * @return
     */
    @Nullable
    public static WorldChunk getBestChunk(int chunkX, int chunkZ, MinecraftClient mc)
    {
        IntegratedServer server = mc.getServer();
        WorldChunk chunk = null;

        if (mc.world != null && server != null)
        {
            ServerWorld world = server.getWorld(mc.world.getRegistryKey());

            if (world != null)
            {
                chunk = world.getChunk(chunkX, chunkZ);
            }
        }

        if (chunk != null)
        {
            return chunk;
        }

        return mc.world != null ? mc.world.getChunk(chunkX, chunkZ) : null;
    }

    /**
     * Replaces getHighestNonEmptySectionYOffset() marked for removal from Minecraft and used across downstream mods
     * Returns Maximum Y Offset Value of a Chunk.
     */
    public static int getHighestSectionYOffset(Chunk chunk)
    {
        int yMax = chunk.getHighestNonEmptySection();

        yMax = yMax == -1 ? chunk.getBottomY() : ChunkSectionPos.getBlockCoord(chunk.sectionIndexToCoord(yMax));

        return yMax;
    }

    public static RegistryEntry<DimensionType> getDimensionTypeEntry(DimensionType key, @Nonnull DynamicRegistryManager registry)
    {
        try
        {
            return registry.getOrThrow(RegistryKeys.DIMENSION_TYPE).getEntry(key);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public static RegistryEntry<DimensionType> getDimensionTypeEntry(Identifier id, @Nonnull DynamicRegistryManager registry)
    {
        try
        {
            return registry.getOrThrow(RegistryKeys.DIMENSION_TYPE).getEntry(id).orElseThrow();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public static RegistryEntry<DimensionType> getDimensionTypeEntry(String id, @Nonnull DynamicRegistryManager registry)
    {
        try
        {
            return registry.getOrThrow(RegistryKeys.DIMENSION_TYPE).getEntry(Identifier.tryParse(id)).orElseThrow();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public static RegistryEntry<Biome> getBiomeEntry(RegistryKey<Biome> key, @Nonnull DynamicRegistryManager registry)
    {
        try
        {
            return registry.getOrThrow(RegistryKeys.BIOME).getOrThrow(key);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public static RegistryEntry<Biome> getBiomeEntry(Identifier id, @Nonnull DynamicRegistryManager registry)
    {
        try
        {
            return registry.getOrThrow(RegistryKeys.BIOME).getEntry(id).orElseThrow();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public static RegistryEntry<Biome> getBiomeEntry(String id, @Nonnull DynamicRegistryManager registry)
    {
        try
        {
            return registry.getOrThrow(RegistryKeys.BIOME).getEntry(Identifier.tryParse(id)).orElseThrow();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public static RegistryEntry<Biome> getPlains(@Nonnull DynamicRegistryManager registry)
    {
        return getBiomeEntry(BiomeKeys.PLAINS, registry);
    }

    public static RegistryEntry<Biome> getWastes(@Nonnull DynamicRegistryManager registry)
    {
        return getBiomeEntry(BiomeKeys.NETHER_WASTES, registry);
    }

    public static RegistryEntry<Biome> getTheEnd(@Nonnull DynamicRegistryManager registry)
    {
        return getBiomeEntry(BiomeKeys.THE_END, registry);
    }
}
