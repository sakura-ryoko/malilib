package fi.dy.masa.malilib.util.data_syncer;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import net.minecraft.core.BlockPos;

public class EntityDataRequestTracker
{
	private final LinkedBlockingQueue<BlockPos> pendingBlockEntityQueue = new LinkedBlockingQueue<>();
	private final LinkedBlockingQueue<Integer> pendingEntityQueue = new LinkedBlockingQueue<>();
	private final Set<BlockPos> pendingLocalBlockEntities = ConcurrentHashMap.newKeySet();
	private final Set<Integer> pendingLocalEntities = ConcurrentHashMap.newKeySet();

	public void schedulePendingBlockEntity(BlockPos pos)
	{
		if (!this.pendingBlockEntityQueue.contains(pos))
		{
			this.pendingBlockEntityQueue.offer(pos);
		}
	}

	public void schedulePendingEntity(int entityId)
	{
		if (!this.pendingEntityQueue.contains(entityId))
		{
			this.pendingEntityQueue.offer(entityId);
		}
	}

	public int getPendingBlockEntityCount()
	{
		return this.pendingBlockEntityQueue.size();
	}

	public int getPendingEntityCount()
	{
		return this.pendingEntityQueue.size();
	}

	public BlockPos pollNextBlockEntity()
	{
		return this.pendingBlockEntityQueue.poll();
	}

	public Integer pollNextEntity()
	{
		return this.pendingEntityQueue.poll();
	}

	public boolean hasScheduledBlockEntity(BlockPos pos)
	{
		return this.pendingBlockEntityQueue.contains(pos);
	}

	public boolean hasScheduledEntity(int entityId)
	{
		return this.pendingEntityQueue.contains(entityId);
	}

	public void removeScheduledBlockEntity(BlockPos pos)
	{
		this.pendingBlockEntityQueue.remove(pos);
	}

	public void removeScheduledEntity(int entityId)
	{
		this.pendingEntityQueue.remove(entityId);
	}

	public boolean hasPendingLocalBlockEntity(BlockPos pos)
	{
		return this.pendingLocalBlockEntities.contains(pos);
	}

	public boolean hasPendingLocalEntity(int entityId)
	{
		return this.pendingLocalEntities.contains(entityId);
	}

	public void setPendingLocalBlockEntityRequest(BlockPos pos, boolean pending)
	{
		if (pending)
		{
			this.pendingLocalBlockEntities.add(pos);
		}
		else
		{
			this.pendingLocalBlockEntities.remove(pos);
		}
	}

	public void setPendingLocalEntityRequest(int entityId, boolean pending)
	{
		if (pending)
		{
			this.pendingLocalEntities.add(entityId);
		}
		else
		{
			this.pendingLocalEntities.remove(entityId);
		}
	}

	public void clearAll()
	{
		this.pendingBlockEntityQueue.clear();
		this.pendingEntityQueue.clear();
		this.pendingLocalBlockEntities.clear();
		this.pendingLocalEntities.clear();
	}
}
