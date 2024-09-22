package fi.dy.masa.malilib.sync;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import it.unimi.dsi.fastutil.Pair;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.sync.data.SyncData;
import fi.dy.masa.malilib.sync.data.SyncGeneric;

public class SyncDataCache<E extends SyncData, B extends SyncData> implements AutoCloseable
{
    private final ConcurrentHashMap<BlockPos, Pair<Long, B>> blockEntities = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer,  Pair<Long, E>> entities      = new ConcurrentHashMap<>();
    private World world;

    public SyncDataCache() {}

    public void setWorld(World world)
    {
        this.world = world;
    }

    public World getWorld()
    {
        if (this.world == null)
        {
            if (MinecraftClient.getInstance().world != null)
            {
                this.world = MinecraftClient.getInstance().world;
            }
            else
            {
                return null;
            }
        }

        return this.world;
    }

    @SuppressWarnings("unchecked")
    public B createBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        if (this.blockEntities.containsKey(pos))
        {
            if (this.blockEntities.get(pos).right().isLoaded())
            {
                return this.blockEntities.get(pos).right();
            }

            this.blockEntities.get(pos).right().clear();
            this.blockEntities.remove(pos);
        }

        this.blockEntities.put(pos, Pair.of(Util.getMeasuringTimeMs(), (B) new SyncGeneric(type, pos, state, this.getWorld())));
        return this.blockEntities.get(pos).right();
    }

    @SuppressWarnings("unchecked")
    public B createBlockEntity(BlockEntity be)
    {
        BlockPos pos = be.getPos();

        if (this.blockEntities.containsKey(pos))
        {
            if (this.blockEntities.get(pos).right().isLoaded())
            {
                return this.blockEntities.get(pos).right();
            }

            this.blockEntities.get(pos).right().clear();
            this.blockEntities.remove(pos);
        }

        this.blockEntities.put(pos, Pair.of(Util.getMeasuringTimeMs(), (B) new SyncGeneric(be)));
        return this.blockEntities.get(pos).right();
    }

    public boolean hasBlockEntity(BlockPos pos)
    {
        return this.blockEntities.containsKey(pos);
    }

    @Nullable
    public B getBlockEntity(BlockPos pos)
    {
        if (this.blockEntities.containsKey(pos))
        {
            return this.blockEntities.get(pos).right();
        }

        return null;
    }

    public void storeBlockEntity(BlockPos pos, B be)
    {
        if (this.hasBlockEntity(pos))
        {
            this.blockEntities.get(pos).right().clear();
            this.blockEntities.replace(pos, Pair.of(Util.getMeasuringTimeMs(), be));
        }
        else
        {
            this.blockEntities.put(pos, Pair.of(Util.getMeasuringTimeMs(), be));
        }
    }

    @SuppressWarnings("unchecked")
    public E createEntity(EntityType<?> type, int entityId)
    {
        if (this.entities.containsKey(entityId))
        {
            if (this.entities.get(entityId).right().isLoaded())
            {
                return this.entities.get(entityId).right();
            }

            this.entities.get(entityId).right().clear();
            this.entities.remove(entityId);
        }

        this.entities.put(entityId, Pair.of(Util.getMeasuringTimeMs(), (E) new SyncGeneric(type, this.getWorld(), entityId)));
        return this.entities.get(entityId).right();
    }

    @SuppressWarnings("unchecked")
    public E createEntity(Entity entity)
    {
        int entityId = entity.getId();

        if (this.entities.containsKey(entityId))
        {
            if (this.entities.get(entityId).right().isLoaded())
            {
                return this.entities.get(entityId).right();
            }

            this.entities.get(entityId).right().clear();
            this.entities.remove(entityId);
        }

        this.entities.put(entityId, Pair.of(Util.getMeasuringTimeMs(), (E) new SyncGeneric(entity)));
        return this.entities.get(entityId).right();
    }

    public boolean hasEntity(int entityId)
    {
        return this.entities.containsKey(entityId);
    }

    @Nullable
    public E getEntity(int entityId)
    {
        if (this.entities.containsKey(entityId))
        {
            return this.entities.get(entityId).right();
        }

        return null;
    }

    public void storeEntity(int entityId, E ent)
    {
        if (this.hasEntity(entityId))
        {
            this.entities.get(entityId).right().clear();
            this.entities.replace(entityId, Pair.of(Util.getMeasuringTimeMs(), ent));
        }
        else
        {
            this.entities.put(entityId, Pair.of(Util.getMeasuringTimeMs(), ent));
        }
    }

    public void tickEntities(long now, long timeout)
    {
        List<BlockPos> expiredBlocks = new ArrayList<>();
        List<Integer> expiredEntities = new ArrayList<>();
        int oldBlockSize = this.blockEntities.size();
        int oldEntitySize = this.entities.size();

        this.blockEntities.forEach((pos, val) ->
        {
            if (isExpired(now, val.right().getEntryTime(), timeout))
            {
                expiredBlocks.add(pos);
            }
        });
        this.entities.forEach((id, val) ->
        {
            if (isExpired(now, val.right().getEntryTime(), timeout))
            {
                expiredEntities.add(id);
            }
        });
        expiredBlocks.forEach(this.blockEntities::remove);
        expiredEntities.forEach(this.entities::remove);
        if (!expiredBlocks.isEmpty())
        {
            MaLiLib.printDebug("SyncDataCache#tickEntities(): expired [{}] Block Entities, [{} -> {}]", expiredBlocks.size(), oldBlockSize, this.blockEntities.size());
        }
        if (!expiredEntities.isEmpty())
        {
            MaLiLib.printDebug("SyncDataCache#tickEntities(): expired [{}] Entities, [{} -> {}]", expiredEntities.size(), oldEntitySize, this.entities.size());
        }
        expiredBlocks.clear();
        expiredEntities.clear();
    }

    private boolean isExpired(long time, long then, long timeout)
    {
        // Accommodate for Negative Time Adjustments
        if ((time - then) < 0)
        {
            return false;
        }
        else return (time - then) > timeout;
    }

    public void clear()
    {
        this.blockEntities.forEach((l, v) -> v.right().clear());
        this.entities.forEach((l, v) -> v.right().clear());
        this.blockEntities.clear();
        this.entities.clear();
        this.world = null;
    }

    @Override
    public void close() throws Exception
    {
        this.clear();
    }
}
