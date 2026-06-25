package fi.dy.masa.malilib.interfaces;

import java.util.List;
import javax.annotation.Nullable;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.nautilus.AbstractNautilus;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;

import fi.dy.masa.malilib.mixin.entity.IMixinAbstractHorseEntity;
import fi.dy.masa.malilib.mixin.entity.IMixinAbstractNautilus;
import fi.dy.masa.malilib.mixin.entity.IMixinPiglinEntity;
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.malilib.util.InventoryUtils;
import fi.dy.masa.malilib.util.WorldUtils;
import fi.dy.masa.malilib.util.data.Constants;
import fi.dy.masa.malilib.util.data.DataEntityUtils;
import fi.dy.masa.malilib.util.data.tag.CompoundData;
import fi.dy.masa.malilib.util.data.tag.converter.DataConverterNbt;
import fi.dy.masa.malilib.util.data_syncer.EntityDataCache;
import fi.dy.masa.malilib.util.data_syncer.EntityDataPairEntry;
import fi.dy.masa.malilib.util.data_syncer.EntityDataRequestTracker;
import fi.dy.masa.malilib.util.nbt.NbtKeys;
import fi.dy.masa.malilib.util.nbt.NbtView;

/**
 * Used as a common Server Data Syncer interface used by the IInventoryOverlayHandler Interface.
 * A lot of this is optional, but the main required items for a Successful Data Syncer are
 * the Requesters, Getters, and the Vanilla Packet Handler; at the Minimum.
 * -
 * The included default code is only enough to get the Data from the ServerWorld in Single Player.
 */
public interface IDataSyncer
{
	/**
	 * Return your Cache instance.
	 *
	 * @return -
	 */
	EntityDataCache getCache();

	/**
	 * Return your request Tracker
	 *
	 * @return -
	 */
	EntityDataRequestTracker getRequestTracker();

	/**
	 * Return a list of ignored Cache Ids
	 */
	default List<String> ignoredIds()
	{
		return List.of(this.getCache().getId());
	}

	/**
	 * Return if this Data Syncer is enabled
	 *
	 * @return -
	 */
	boolean isEnabled();

	/**
	 * Return if this data syncer's "Backup Mode" is enabled
	 *
	 * @return -
	 */
	boolean isBackupEnabled();

	/**
	 * Return the length in time for a configured Cache refresh
	 *
	 * @return -
	 */
	long getRefreshTime();

	/**
	 * Return the Cache's timeout setting
	 *
	 * @return -
	 */
	long getCacheTimeout();

	/**
	 * Return whether to "load" the Nbt into a Block Entity Container
	 *
	 * @return -
	 */
	boolean loadContainerBlockEntities();

	/**
	 * Get the 'Best World' object
	 *
	 * @return ()
	 */
	@Nullable
	default Level getBestWorld()
	{
		if (Minecraft.getInstance() == null)
		{
			return null;
		}

		return WorldUtils.getBestWorld(Minecraft.getInstance());
	}

	/**
	 * Get the Client World Object
	 *
	 * @return ()
	 */
	@Nullable
	default ClientLevel getClientWorld()
	{
		if (Minecraft.getInstance().level == null)
		{
			return null;
		}

		return Minecraft.getInstance().level;
	}

	/**
	 * Return if there is a local single player server
	 *
	 * @return -
	 */
	default boolean hasSingleplayerServer() {return Minecraft.getInstance().hasSingleplayerServer();}

	/**
	 * Return if we are running on the local Server Thread
	 *
	 * @return -
	 */
	default boolean isOnLocalServerThread()
	{
		if (this.hasSingleplayerServer() && Minecraft.getInstance().getSingleplayerServer() != null)
		{
			return Minecraft.getInstance().getSingleplayerServer().isSameThread();
		}

		return false;
	}

	/**
	 * Called when Joining / Leaving worlds; used to "reset" any Data Syncer Cache.
	 *
	 * @param isLogout ()
	 */
	default void reset(boolean isLogout) {}

	/**
	 * If you need to initialize a Packet Handler's Payload Registration.
	 * Needs to be called during your Mod Init Function.
	 */
	default void onGameInit() {}

	/**
	 * If you need to initialize a Packet Receiver, aka. register your Global Receiver.
	 * Needs to be called during the onWorldJoinPre() phase.
	 */
	default void onWorldPre() {}

	/**
	 * What to do when joining a world?  Such a register your
	 * Data Syncer with any Server Back end; requesting Metadata, etc.
	 * Needs to be called during the onWorldJoinPost() phase.
	 */
	default void onWorldJoin() {}

	/**
	 * Used to return an NBT Object from the Entity Data Syncer Cache at the specific BlockPos.
	 * Note, that these functions are intended to be simple Getters.
	 * For Requesting Server Data, use `requestBlockEntity()`
	 *
	 * @param pos ()
	 * @return ()
	 */
	@Nullable
	default CompoundTag getFromBlockEntityCacheNbt(BlockPos pos)
	{
		CompoundData data = this.getFromBlockEntityCacheData(pos);

		if (data != null)
		{
			return DataConverterNbt.toVanillaCompound(data);
		}

		return new CompoundTag();
	}

	/**
	 * Used to return an NBT Object from the Entity Data Syncer Cache at the specific BlockPos.
	 * Note, that these functions are intended to be simple Getters.
	 * For Requesting Server Data, use `requestBlockEntity()`
	 *
	 * @param pos ()
	 * @return ()
	 */
	@Nullable
	default CompoundData getFromBlockEntityCacheData(BlockPos pos)
	{
		return this.getCache().getBlockEntityDataFromCache(pos);
	}

	/**
	 * Used to return an BlockEntity Object from the Entity Data Syncer Cache at the specific BlockPos.
	 * Note, that these functions are intended to be simple Getters.
	 * For Requesting Server Data, use `requestBlockEntity()`
	 *
	 * @param pos ()
	 * @return ()
	 */
	@Nullable
	default BlockEntity getFromBlockEntityCache(BlockPos pos)
	{
		return this.getCache().getBlockEntityFromCache(pos);
	}

	/**
	 * Used to return an NBT Object from the Entity Data Syncer Cache at the specific BlockPos.
	 * Note, that these functions are intended to be simple Getters.
	 * For Requesting Server Data, use `requestEntity()`
	 *
	 * @param entityId ()
	 * @return ()
	 */
	@Nullable
	default CompoundTag getFromEntityCacheNbt(int entityId)
	{
		CompoundData data = this.getFromEntityCacheData(entityId);

		if (data != null)
		{
			return DataConverterNbt.toVanillaCompound(data);
		}

		return new CompoundTag();
	}

	/**
	 * Used to return an NBT Object from the Entity Data Syncer Cache at the specific BlockPos.
	 * Note, that these functions are intended to be simple Getters.
	 * For Requesting Server Data, use `requestEntity()`
	 *
	 * @param entityId ()
	 * @return ()
	 */
	@Nullable
	default CompoundData getFromEntityCacheData(int entityId)
	{
		return this.getCache().getEntityDataFromCache(entityId);
	}

	/**
	 * Used to return an Entity Object from the Entity Data Syncer Cache at the specific BlockPos.
	 * Note, that these functions are intended to be simple Getters.
	 * For Requesting Server Data, use `requestEntity()`
	 *
	 * @param entityId ()
	 * @return ()
	 */
	@Nullable
	default Entity getFromEntityCache(int entityId)
	{
		return this.getCache().getEntityFromCache(entityId);
	}

	@Nullable
	default Pair<BlockEntity, CompoundTag> requestBlockEntityNbt(Level world, BlockPos pos)
	{
		Pair<BlockEntity, CompoundData> pair = this.requestBlockEntity(world, pos);

		if (pair != null)
		{
			return Pair.of(pair.getLeft(), DataConverterNbt.toVanillaCompound(pair.getRight()));
		}

		return null;
	}

	/**
	 * Request the Block Entity Pair from the server;
	 * if the Cache contains the Data, return the data Pair.
	 *
	 * @param world ()
	 * @param pos   ()
	 * @return (The Data Pair|Null)
	 */
	@Nullable
	default Pair<BlockEntity, CompoundData> requestBlockEntity(Level world, BlockPos pos)
	{
		if (world == null)
		{
			world = this.getBestWorld();
		}

		if (world == null)
		{
			return null;
		}

		EntityDataPairEntry pair = this.getCache().getBlockEntityPairFromCache(pos);
		final long now = System.currentTimeMillis();

		if (pair != null)
		{
			if (!this.hasSingleplayerServer() && (this.isEnabled() || this.isBackupEnabled()))
			{
				if ((now - pair.time()) > this.getRefreshTime())
				{
					this.getRequestTracker().schedulePendingBlockEntity(pos);
				}
			}

			if (world instanceof ServerLevel sl)
			{
				if (this.isOnLocalServerThread())
				{
					return this.refreshBlockEntityFromWorld(sl, pos);
				}
				else if ((now - pair.time()) > this.getRefreshTime() && !this.getRequestTracker().hasPendingLocalBlockEntity(pos))
				{
					this.getRequestTracker().setPendingLocalBlockEntityRequest(pos, true);
					this.requestBlockEntityFromLocalServer(Minecraft.getInstance(), world, pos);
				}
			}

			CompoundData globalData = Registry.ENTITY_DATA_REGISTRY.scanForBlockEntityData(pos, this.ignoredIds());

			if (!globalData.isEmpty())
			{
				return Pair.of(pair.be(), globalData);
			}

			return Pair.of(pair.be(), pair.data());
		}
		else if (world.getBlockState(pos).getBlock() instanceof EntityBlock)
		{
			CompoundData globalData = Registry.ENTITY_DATA_REGISTRY.scanForBlockEntityData(pos, this.ignoredIds());
			BlockEntity be = this.getClientWorld() != null ? this.getClientWorld().getBlockEntity(pos) : null;

			if (be != null && !globalData.isEmpty())
			{
				this.getCache().removeFromCache(pos);
				this.getCache().addToCache(pos, be, globalData);
				return Pair.of(be, globalData);
			}

			if (!this.hasSingleplayerServer() && (this.isEnabled() || this.isBackupEnabled()))
			{
				this.getRequestTracker().schedulePendingBlockEntity(pos);
			}

			if (world instanceof ServerLevel sl && this.isOnLocalServerThread())
			{
				return this.refreshBlockEntityFromWorld(sl, pos);
			}
			else if (this.hasSingleplayerServer() && !this.getRequestTracker().hasPendingLocalBlockEntity(pos))
			{
				this.getRequestTracker().setPendingLocalBlockEntityRequest(pos, true);
				this.requestBlockEntityFromLocalServer(Minecraft.getInstance(), world, pos);
			}

			return this.refreshBlockEntityFromWorld(this.getClientWorld(), pos);
		}

		return null;
	}

	/**
	 * Refresh the Block Entity from the World
	 *
	 * @param world -
	 * @param pos   -
	 * @return -
	 */
	default @Nullable Pair<BlockEntity, CompoundData> refreshBlockEntityFromWorld(Level world, BlockPos pos)
	{
		if (world != null && world.getBlockState(pos).hasBlockEntity())
		{
			BlockEntity be = world.getChunkAt(pos).getBlockEntity(pos);

			if (be != null)
			{
				CompoundData data = DataConverterNbt.fromVanillaCompound(be.saveWithFullMetadata(world.registryAccess()));
				Pair<BlockEntity, CompoundData> pair = Pair.of(be, data);

				this.getCache().removeFromCache(pos);
				this.getCache().addToCache(pos, be, data);

				return pair;
			}
		}

		return null;
	}

	/**
	 * Request the Block Entity NBT data from a local server; via it's Thread Executor, and then have it call `handleBlockEntityData()`
	 *
	 * @param mc    -
	 * @param world -
	 * @param pos   -
	 * @return Return if the Request should proceed.
	 */
	default boolean requestBlockEntityFromLocalServer(Minecraft mc, Level world, BlockPos pos)
	{
		if (mc.hasSingleplayerServer() && mc.getSingleplayerServer() != null &&
				!mc.getSingleplayerServer().isSameThread())
		{
			mc.getSingleplayerServer().execute(() ->
			                                   {
				                                   Pair<BlockEntity, CompoundData> pair = this.refreshBlockEntityFromWorld(world, pos);

				                                   if (pair != null && !pair.getRight().isEmpty())
				                                   {
					                                   CompoundData data = pair.getRight();
					                                   mc.execute(() -> this.handleBlockEntityData(pos, data));
				                                   }
			                                   });
			return false;
		}

		return true;
	}

	@Nullable
	default Pair<Entity, CompoundTag> requestEntityNbt(Level world, int entityId)
	{
		Pair<Entity, CompoundData> pair = this.requestEntity(world, entityId);

		if (pair != null)
		{
			return Pair.of(pair.getLeft(), DataConverterNbt.toVanillaCompound(pair.getRight()));
		}

		return null;
	}

	/**
	 * Request the Entity Pair from the server;
	 * if the Cache contains the Data, return the data Pair.
	 *
	 * @param entityId ()
	 * @return (The Data Pair|Null)
	 */
	@Nullable
	default Pair<Entity, CompoundData> requestEntity(Level world, int entityId)
	{
		if (world == null)
		{
			world = this.getBestWorld();
		}

		if (world == null)
		{
			return null;
		}

		EntityDataPairEntry pair = this.getCache().getEntityPairFromCache(entityId);
		final long now = System.currentTimeMillis();

		if (pair != null)
		{
			if (!this.hasSingleplayerServer() && (this.isEnabled() || this.isBackupEnabled()))
			{
				if ((now - pair.time()) > this.getRefreshTime())
				{
					this.getRequestTracker().schedulePendingEntity(entityId);
				}
			}

			if (world instanceof ServerLevel sl)
			{
				if (this.isOnLocalServerThread())
				{
					return this.refreshEntityFromWorld(sl, entityId);
				}
				else if ((now - pair.time()) > this.getRefreshTime() && !this.getRequestTracker().hasPendingLocalEntity(entityId))
				{
					this.getRequestTracker().setPendingLocalEntityRequest(entityId, true);
					this.requestEntityFromLocalServer(Minecraft.getInstance(), world, entityId);
				}
			}

			CompoundData globalData = Registry.ENTITY_DATA_REGISTRY.scanForEntityData(entityId, this.ignoredIds());

			if (!globalData.isEmpty())
			{
				return Pair.of(pair.ent(), globalData);
			}

			return Pair.of(pair.ent(), pair.data());
		}

		CompoundData globalData = Registry.ENTITY_DATA_REGISTRY.scanForEntityData(entityId, this.ignoredIds());
		Entity entity = this.getClientWorld() != null ? this.getClientWorld().getEntity(entityId) : null;

		if (entity != null && !globalData.isEmpty())
		{
			this.getCache().removeFromCache(entityId);
			this.getCache().addToCache(entityId, entity, globalData);
			return Pair.of(entity, globalData);
		}

		if (!this.hasSingleplayerServer() && (this.isEnabled() || this.isBackupEnabled()))
		{
			this.getRequestTracker().schedulePendingEntity(entityId);
		}

		if (world instanceof ServerLevel sl && this.isOnLocalServerThread())
		{
			return this.refreshEntityFromWorld(sl, entityId);
		}
		else if (this.hasSingleplayerServer() && !this.getRequestTracker().hasPendingLocalEntity(entityId))
		{
			this.getRequestTracker().setPendingLocalEntityRequest(entityId, true);
			this.requestEntityFromLocalServer(Minecraft.getInstance(), world, entityId);
		}

		return this.refreshEntityFromWorld(this.getClientWorld(), entityId);
	}

	/**
	 * Refresh an entity from the World
	 *
	 * @param world    -
	 * @param entityId -
	 * @return -
	 */
	default @Nullable Pair<Entity, CompoundData> refreshEntityFromWorld(Level world, int entityId)
	{
		if (world != null)
		{
			Entity entity = world.getEntity(entityId);

			if (entity != null)
			{
				CompoundData data = DataEntityUtils.invokeEntityDataTagNoPassengers(entity, entityId);

				if (!data.isEmpty())
				{
					Pair<Entity, CompoundData> pair = Pair.of(entity, data);

					this.getCache().removeFromCache(entityId);
					this.getCache().addToCache(entityId, entity, data);

					return pair;
				}
			}
		}

		return null;
	}

	/**
	 * Request the Entity NBT data from a local server; via it's Thread Executor, and then have it call `handleEntityData()`
	 *
	 * @param mc       -
	 * @param world    -
	 * @param entityId -
	 * @return Return if the Request should proceed.
	 */
	default boolean requestEntityFromLocalServer(Minecraft mc, Level world, int entityId)
	{
		if (mc.hasSingleplayerServer() && mc.getSingleplayerServer() != null &&
				!this.isOnLocalServerThread())
		{
			mc.getSingleplayerServer().execute(() ->
			                                   {
				                                   Pair<Entity, CompoundData> pair = this.refreshEntityFromWorld(world, entityId);

				                                   if (pair != null && !pair.getRight().isEmpty())
				                                   {
					                                   CompoundData data = pair.getRight();
					                                   mc.execute(() -> this.handleEntityData(entityId, data));
				                                   }
			                                   });

			return false;
		}

		return true;
	}

	/**
	 * Used to Obtain the Inventory Object from the Specified BlockPos,
	 * and handle if it is a Double Chest.  If the Data doesn't exist in the Cache, request it.
	 *
	 * @param world  (Provided for compatibility with other worlds)
	 * @param pos    ()
	 * @param useNbt ()
	 * @return (Inventory|EmptyInventory|Null)
	 */
	@Nullable
	@SuppressWarnings("deprecation")
	default Container getBlockInventory(Level world, BlockPos pos, boolean useNbt)
	{
		if (world == null)
		{
			world = this.getBestWorld();
		}

		if (world == null)
		{
			return null;
		}

		EntityDataPairEntry pair = this.getCache().getBlockEntityPairFromCache(pos);

		if (pair != null)
		{
			Container inv = null;
			BlockState state = world.getBlockState(pos);

			if (!useNbt && (state.is(BlockTags.AIR) || !state.hasBlockEntity()))
			{
				this.getCache().removeFromCache(pos);
				return null;
			}

			Pair<BlockPos, BlockState> barrelAdj = InventoryUtils.getCarpetTISLargeBarrel(world, pos, state);

			if (barrelAdj != null)
			{
				if (!world.hasChunkAt(barrelAdj.getLeft()))
				{
					return null;
				}

				BlockPos posAdj = barrelAdj.getLeft();
				BlockState stateAdj = barrelAdj.getRight();
				EntityDataPairEntry pairAdj = this.getCache().getBlockEntityPairFromCache(posAdj);

				if (pairAdj == null)
				{
					this.requestBlockEntity(world, posAdj);
				}
				else
				{
					Container inv1 = null;
					Container inv2 = null;

					if (useNbt)
					{
						inv1 = InventoryUtils.getDataInventory(pair.data(), -1, world.registryAccess());
						inv2 = InventoryUtils.getDataInventory(pairAdj.data(), -1, world.registryAccess());
					}
					else if (pair.be() instanceof Container c1 && pairAdj.be() instanceof Container c2)
					{
						inv1 = c1;
						inv2 = c2;
					}

					// Just recycling "ChestType" here.  Negative Axis Direction == First Side.
					ChestType type = state.getValue(BarrelBlock.FACING).getAxisDirection() == net.minecraft.core.Direction.AxisDirection.NEGATIVE ? ChestType.RIGHT : ChestType.LEFT;

					if (inv1 != null && inv2 != null)
					{
						Container invRight = type == ChestType.RIGHT ? inv1 : inv2;
						Container invLeft = type == ChestType.RIGHT ? inv2 : inv1;

						inv = new CompoundContainer(invRight, invLeft);
					}
				}
			}
			else if (state.hasProperty(BlockStateProperties.CHEST_TYPE) && state.hasProperty(BlockStateProperties.HORIZONTAL_FACING))
			{
				ChestType type = state.getValue(BlockStateProperties.CHEST_TYPE);

				if (type != ChestType.SINGLE)
				{
					Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
					Direction offsetDir = type == ChestType.LEFT ? facing.getClockWise() : facing.getCounterClockWise();
					BlockPos posAdj = pos.relative(offsetDir);

					if (!world.hasChunkAt(posAdj))
					{
						return null;
					}

					BlockState stateAdj = world.getBlockState(posAdj);
					EntityDataPairEntry pairAdj = this.getCache().getBlockEntityPairFromCache(posAdj);

					if (pairAdj == null)
					{
						this.requestBlockEntity(world, posAdj);
					}
					else if (stateAdj.getBlock() == state.getBlock() &&
							 stateAdj.hasProperty(BlockStateProperties.CHEST_TYPE) &&
							 stateAdj.hasProperty(BlockStateProperties.HORIZONTAL_FACING) &&
							 stateAdj.getValue(BlockStateProperties.CHEST_TYPE) != ChestType.SINGLE &&
							 stateAdj.getValue(BlockStateProperties.HORIZONTAL_FACING) == facing)
					{
						Container inv1 = null;
						Container inv2 = null;

						if (useNbt)
						{
							inv1 = InventoryUtils.getDataInventory(pair.data(), -1, world.registryAccess());
							inv2 = InventoryUtils.getDataInventory(pairAdj.data(), -1, world.registryAccess());
						}
						else if (pair.be() instanceof Container c1 && pairAdj.be() instanceof Container c2)
						{
							inv1 = c1;
							inv2 = c2;
						}

						if (inv1 != null && inv2 != null)
						{
							Container invRight = type == ChestType.RIGHT ? inv1 : inv2;
							Container invLeft = type == ChestType.RIGHT ? inv2 : inv1;

							inv = new CompoundContainer(invRight, invLeft);
						}
					}
				}
			}

			if (inv == null)
			{
				if (useNbt)
				{
					inv = InventoryUtils.getDataInventory(pair.data(), -1, world.registryAccess());
				}
				else if (pair.be() instanceof Container inv2)
				{
					inv = inv2;
				}
			}

			if (inv != null)
			{
				return inv;
			}
		}

		if (this.isEnabled() || this.isBackupEnabled())
		{
			this.requestBlockEntity(this.getBestWorld(), pos);
		}

		return null;
	}

	/**
	 * Used to Obtain the Inventory Object from the Specified Entity, if available;
	 * and handle if it needs special handling.  If the Data doesn't exist in the Cache, request it.
	 *
	 * @param entityId ()
	 * @param useData  ()
	 * @return (Inventory|Null)
	 */
	@Nullable
	default Container getEntityInventory(Level world, int entityId, boolean useData)
	{
		if (world == null)
		{
			world = this.getBestWorld();
		}

		if (world == null)
		{
			return null;
		}

		EntityDataPairEntry pair = this.getCache().getEntityPairFromCache(entityId);

		if (pair != null && this.getBestWorld() != null)
		{
			Container inv = null;

			if (useData)
			{
				inv = InventoryUtils.getDataInventory(pair.data(), -1, world.registryAccess());
			}
			else
			{
				Entity entity = pair.ent();

				if (entity instanceof Container)
				{
					inv = (Container) entity;
				}
				else if (entity instanceof Player player && player != null)
				{
					inv = new SimpleContainer(player.getInventory().getNonEquipmentItems().toArray(new ItemStack[36]));
				}
				else if (entity instanceof Villager)
				{
					inv = ((Villager) entity).getInventory();
				}
				else if (entity instanceof AbstractHorse)
				{
					inv = ((IMixinAbstractHorseEntity) entity).malilib_getHorseInventory();
				}
				else if (entity instanceof AbstractNautilus)
				{
					inv = ((IMixinAbstractNautilus) entity).malilib_getNautilusInventory();
				}
				else if (entity instanceof Piglin)
				{
					inv = ((IMixinPiglinEntity) entity).malilib_getInventory();
				}

				return inv;
			}

			if (inv != null)
			{
				return inv;
			}
		}

		if (this.isEnabled() || this.isBackupEnabled())
		{
			this.requestEntity(this.getBestWorld(), entityId);
		}

		return null;
	}

	/**
	 * Used by your Packet Receiver to hande incoming data from BlockPos and the Server Side NBT tags.
	 *
	 * @param pos  ()
	 * @param nbt  ()
	 * @return (BlockEntity|Null)
	 */
	default BlockEntity handleBlockEntityData(BlockPos pos, CompoundTag nbt)
	{
		return this.handleBlockEntityData(pos, DataConverterNbt.fromVanillaCompound(nbt));
	}

	/**
	 * Used by your Packet Receiver to hande incoming data from the entityId and the Server Side NBT tags.
	 *
	 * @param nbt ()
	 * @return (Entity|Null)
	 */
	default Entity handleEntityData(int entityId, CompoundTag nbt)
	{
		return handleEntityData(entityId, DataConverterNbt.fromVanillaCompound(nbt));
	}

	/**
	 * Used by your Packet Receiver if any Bulk handling of NBT Tags for multiple Entities is required.
	 * This is usually used for something like downloading an entire ChunkPos worth of Entity Data; such as with Litematica.
	 *
	 * @param transactionId ()
	 * @param nbt           ()
	 */
	default void handleBulkEntityData(int transactionId, CompoundTag nbt)
	{
		this.handleBulkEntityData(transactionId, DataConverterNbt.fromVanillaCompound(nbt));
	}

	/**
	 * Vanilla QueryNbt Packet Receiver & Handling
	 *
	 * @param transactionId (QueryNbt Transaction Id)
	 * @param nbt           (The NBT Data returned by the server)
	 */
	default void handleVanillaQueryNbt(int transactionId, CompoundTag nbt)
	{
		this.handleVanillaQueryNbt(transactionId, DataConverterNbt.fromVanillaCompound(nbt));
	}

	/**
	 * Used by your Packet Receiver to handle incoming data from BlockPos and the Server Side NBT tags.
	 *
	 * @param pos  ()
	 * @param data ()
	 * @return (BlockEntity|Null)
	 */
	default @Nullable BlockEntity handleBlockEntityData(BlockPos pos, CompoundData data)
	{
		this.getRequestTracker().removeScheduledBlockEntity(pos);
		this.getRequestTracker().setPendingLocalBlockEntityRequest(pos, false);
		if (data == null || this.getClientWorld() == null)
		{
			return null;
		}

		BlockEntity be = this.getClientWorld().getBlockEntity(pos);

		if (be != null)
		{
			if (!data.contains(NbtKeys.ID, Constants.NBT.TAG_STRING))
			{
				Identifier id = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(be.getType());

				if (id != null)
				{
					data.putString(NbtKeys.ID, id.toString());
				}
			}

			this.getCache().removeFromCache(pos);
			this.getCache().addToCache(pos, be, data);

			if (this.loadContainerBlockEntities() && be instanceof Container)
			{
				NbtView view = NbtView.getReader(data, this.getClientWorld().registryAccess());
				be.loadWithComponents(view.getReader());
			}

			return be;
		}

		return null;
	}

	/**
	 * Used by your Packet Receiver to handle incoming data from the entityId and the Server Side NBT tags.
	 *
	 * @param data ()
	 * @return (Entity|Null)
	 */
	default @Nullable Entity handleEntityData(int entityId, CompoundData data)
	{
		this.getRequestTracker().removeScheduledEntity(entityId);
		this.getRequestTracker().setPendingLocalEntityRequest(entityId, false);
		if (data == null || this.getClientWorld() == null)
		{
			return null;
		}
		Entity entity = this.getClientWorld().getEntity(entityId);

		if (entity != null)
		{
			if (!data.contains(NbtKeys.ID, Constants.NBT.TAG_STRING))
			{
				Identifier id = EntityType.getKey(entity.getType());

				if (id != null)
				{
					data.putString(NbtKeys.ID, id.toString());
				}
			}

			this.getCache().removeFromCache(entityId);
			this.getCache().addToCache(entityId, entity, data);
			// Load Nbt into an entity? (How about NO!)
		}

		return entity;
	}

	/**
	 * Used by your Packet Receiver if any Bulk handling of NBT Tags for multiple Entities is required.
	 * This is usually used for something like downloading an entire ChunkPos worth of Entity Data; such as with Litematica.
	 *
	 * @param transactionId ()
	 * @param data          ()
	 */
	default void handleBulkEntityData(int transactionId, CompoundData data) {}

	/**
	 * Vanilla QueryNbt Packet Receiver & Handling
	 *
	 * @param transactionId (QueryNbt Transaction Id)
	 * @param data          (The NBT Data returned by the server)
	 */
	default void handleVanillaQueryNbt(int transactionId, CompoundData data) {}

	/**
	 * Clear All pending Quests and Cache.
	 */
	default void clearAll()
	{
		this.getRequestTracker().clearAll();
		this.getCache().clearAll();
	}
}
