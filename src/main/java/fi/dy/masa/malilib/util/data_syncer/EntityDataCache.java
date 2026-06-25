package fi.dy.masa.malilib.util.data_syncer;

import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;

import fi.dy.masa.malilib.util.data.tag.CompoundData;
import fi.dy.masa.malilib.util.data.tag.converter.DataConverterNbt;

public class EntityDataCache
{
	private final ConcurrentHashMap<BlockPos, Pair<Long, Pair<BlockEntity, CompoundData>>> blockEntityCache;
	private final ConcurrentHashMap<Integer, Pair<Long, Pair<Entity, CompoundData>>> entityCache;
	private final String id;
	private long timeout;

	public EntityDataCache(String modId, final long timeout)
	{
		this(modId, timeout, 16);
	}

	public EntityDataCache(String modId, final long timeout, final int initialCapacity)
	{
		this.blockEntityCache = new ConcurrentHashMap<>(initialCapacity, 0.9f, 1);
		this.entityCache = new ConcurrentHashMap<>(initialCapacity, 0.9f, 1);
		this.id = modId;
		this.timeout = timeout;
	}

	public String getId()
	{
		return this.id;
	}

	public long getTimeout()
	{
		return this.timeout;
	}

	public void setTimeout(final long timeout)
	{
		this.timeout = timeout;
	}

	public void tickCache(final long nowTime)
	{
		this.blockEntityCache.forEach((pos, pair) -> {
			if (pair != null)
			{
				if ((nowTime - pair.getLeft()) > this.getTimeout() || pair.getLeft() > nowTime)
				{
					this.blockEntityCache.remove(pos, pair);
				}
			}
		});

		this.entityCache.forEach((entityId, pair) -> {
			if (pair != null)
			{
				if ((nowTime - pair.getLeft()) > this.getTimeout() || pair.getLeft() > nowTime)
				{
					this.entityCache.remove(entityId, pair);
				}
			}
		});
	}

	public boolean isEmpty()
	{
		return this.blockEntityCache.isEmpty() && this.entityCache.isEmpty();
	}

	public int blockEntityCount()
	{
		return this.blockEntityCache.size();
	}

	public int entityCount()
	{
		return this.entityCache.size();
	}

	public void clearAll()
	{
		this.blockEntityCache.clear();
		this.entityCache.clear();
	}

	public CompoundData getBlockEntityDataFromCache(BlockPos pos)
	{
		Pair<Long, Pair<BlockEntity, CompoundData>> pair = this.blockEntityCache.get(pos);
		return pair != null ? pair.getRight().getRight() : new CompoundData();
	}

	public CompoundTag getBlockEntityNbtFromCache(BlockPos pos)
	{
		return DataConverterNbt.toVanillaCompound(this.getBlockEntityDataFromCache(pos));
	}

	public @Nullable BlockEntity getBlockEntityFromCache(BlockPos pos)
	{
		Pair<Long, Pair<BlockEntity, CompoundData>> pair = this.blockEntityCache.get(pos);
		return pair != null ? pair.getRight().getLeft() : null;
	}

	public @Nullable EntityDataPairEntry getBlockEntityPairFromCache(BlockPos pos)
	{
		Pair<Long, Pair<BlockEntity, CompoundData>> pair = this.blockEntityCache.get(pos);

		if (pair != null)
		{
			Pair<BlockEntity, CompoundData> pair2 = pair.getRight();
			return new EntityDataPairEntry(pair.getLeft(), pair2.getRight(), pair2.getLeft(), null);
		}

		return null;
	}

	public @Nullable EntityDataEntry getBlockEntityDataEntryFromCache(BlockPos pos)
	{
		Pair<Long, Pair<BlockEntity, CompoundData>> pair = this.blockEntityCache.get(pos);

		if (pair != null)
		{
			return new EntityDataEntry(pair.getLeft(), pair.getRight().getRight());
		}

		return null;
	}

	public CompoundData getEntityDataFromCache(int entityId)
	{
		Pair<Long, Pair<Entity, CompoundData>> pair = this.entityCache.get(entityId);
		return pair != null ? pair.getRight().getRight() : new CompoundData();
	}

	public CompoundTag getEntityNbtFromCache(int entityId)
	{
		return DataConverterNbt.toVanillaCompound(this.getEntityDataFromCache(entityId));
	}

	public @Nullable Entity getEntityFromCache(int entityId)
	{
		Pair<Long, Pair<Entity, CompoundData>> pair = this.entityCache.get(entityId);
		return pair != null ? pair.getRight().getLeft() : null;
	}

	public @Nullable EntityDataPairEntry getEntityPairFromCache(int entityId)
	{
		Pair<Long, Pair<Entity, CompoundData>> pair = this.entityCache.get(entityId);

		if (pair != null)
		{
			Pair<Entity, CompoundData> pair2 = pair.getRight();
			return new EntityDataPairEntry(pair.getLeft(), pair2.getRight(), null, pair2.getLeft());
		}

		return null;
	}

	public @Nullable EntityDataEntry getEntityDataEntryFromCache(int entityId)
	{
		Pair<Long, Pair<Entity, CompoundData>> pair = this.entityCache.get(entityId);

		if (pair != null)
		{
			return new EntityDataEntry(pair.getLeft(), pair.getRight().getRight());
		}

		return null;
	}

	public void addToCache(final BlockPos pos, final BlockEntity blockEntity, final CompoundData data)
	{
		this.blockEntityCache.put(pos, Pair.of(System.currentTimeMillis(), Pair.of(blockEntity, data)));
	}

	public void addToCache(final int entityId, final Entity entity, final CompoundData data)
	{
		this.entityCache.put(entityId, Pair.of(System.currentTimeMillis(), Pair.of(entity, data)));
	}

	public void removeFromCache(final BlockPos pos)
	{
		this.blockEntityCache.remove(pos);
	}

	public void removeFromCache(final int entityId)
	{
		this.entityCache.remove(entityId);
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) { return true; }
		if (o == null || getClass() != o.getClass()) { return false; }

		EntityDataCache other = (EntityDataCache) o;
		return this.id.equals(other.id) && this.timeout == other.timeout;
	}

	@Override
	public int hashCode()
	{
		int result = this.id.hashCode();
		result = 31 * result + (int) (this.timeout ^ (this.timeout >>> 32));
		return result;
	}
}
