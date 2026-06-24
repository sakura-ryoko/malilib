package fi.dy.masa.malilib.interfaces;

import java.util.List;
import javax.annotation.Nullable;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import fi.dy.masa.malilib.mixin.entity.IMixinAbstractHorseEntity;
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
import fi.dy.masa.malilib.util.position.BlockPos;
import fi.dy.masa.malilib.util.position.Direction;

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
	default World getBestWorld()
	{
		if (MinecraftClient.getInstance() == null)
		{
			return null;
		}

		return WorldUtils.getBestWorld(MinecraftClient.getInstance());
	}

	/**
	 * Get the Client World Object
	 *
	 * @return ()
	 */
	@Nullable
	default ClientWorld getClientWorld()
	{
		if (MinecraftClient.getInstance().world == null)
		{
			return null;
		}

		return MinecraftClient.getInstance().world;
	}

	/**
	 * Return if there is a local single player server
	 *
	 * @return -
	 */
	default boolean hasSingleplayerServer() {return MinecraftClient.getInstance().isIntegratedServerRunning();}

	/**
	 * Return if we are running on the local Server Thread
	 *
	 * @return -
	 */
	default boolean isOnLocalServerThread()
	{
		if (this.hasSingleplayerServer() && MinecraftClient.getInstance().getServer() != null)
		{
			return MinecraftClient.getInstance().getServer().isOnThread();
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
	default NbtCompound getFromBlockEntityCacheNbt(BlockPos pos)
	{
		CompoundData data = this.getFromBlockEntityCacheData(pos);

		if (data != null)
		{
			return DataConverterNbt.toVanillaCompound(data);
		}

		return new NbtCompound();
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
	default NbtCompound getFromEntityCacheNbt(int entityId)
	{
		CompoundData data = this.getFromEntityCacheData(entityId);

		if (data != null)
		{
			return DataConverterNbt.toVanillaCompound(data);
		}

		return new NbtCompound();
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
	default Pair<BlockEntity, NbtCompound> requestBlockEntityNbt(World world, BlockPos pos)
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
	default Pair<BlockEntity, CompoundData> requestBlockEntity(World world, BlockPos pos)
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

			if (world instanceof ServerWorld sl)
			{
				if (this.isOnLocalServerThread())
				{
					return this.refreshBlockEntityFromWorld(sl, pos);
				}
				else if ((now - pair.time()) > this.getRefreshTime() && !this.getRequestTracker().hasPendingLocalBlockEntity(pos))
				{
					this.getRequestTracker().setPendingLocalBlockEntityRequest(pos, true);
					this.requestBlockEntityFromLocalServer(MinecraftClient.getInstance(), world, pos);
				}
			}

			CompoundData globalData = Registry.ENTITY_DATA_REGISTRY.scanForBlockEntityData(pos, this.ignoredIds());

			if (!globalData.isEmpty())
			{
				return Pair.of(pair.be(), globalData);
			}

			return Pair.of(pair.be(), pair.data());
		}
		else if (world.getBlockState(pos).getBlock() instanceof BlockWithEntity)
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

			if (world instanceof ServerWorld sl && this.isOnLocalServerThread())
			{
				return this.refreshBlockEntityFromWorld(sl, pos);
			}
			else if (this.hasSingleplayerServer() && !this.getRequestTracker().hasPendingLocalBlockEntity(pos))
			{
				this.getRequestTracker().setPendingLocalBlockEntityRequest(pos, true);
				this.requestBlockEntityFromLocalServer(MinecraftClient.getInstance(), world, pos);
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
	default @Nullable Pair<BlockEntity, CompoundData> refreshBlockEntityFromWorld(World world, BlockPos pos)
	{
		if (world != null && world.getBlockState(pos).hasBlockEntity())
		{
			BlockEntity be = world.getChunk(pos).getBlockEntity(pos);

			if (be != null)
			{
				CompoundData data = DataConverterNbt.fromVanillaCompound(be.createNbtWithIdentifyingData(world.getRegistryManager()));
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
	default boolean requestBlockEntityFromLocalServer(MinecraftClient mc, World world, BlockPos pos)
	{
		if (mc.isIntegratedServerRunning() && mc.getServer() != null &&
			!mc.getServer().isOnThread())
		{
			mc.getServer().execute(() ->
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
	default Pair<Entity, NbtCompound> requestEntityNbt(World world, int entityId)
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
	default Pair<Entity, CompoundData> requestEntity(World world, int entityId)
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

			if (world instanceof ServerWorld sl)
			{
				if (this.isOnLocalServerThread())
				{
					return this.refreshEntityFromWorld(sl, entityId);
				}
				else if ((now - pair.time()) > this.getRefreshTime() && !this.getRequestTracker().hasPendingLocalEntity(entityId))
				{
					this.getRequestTracker().setPendingLocalEntityRequest(entityId, true);
					this.requestEntityFromLocalServer(MinecraftClient.getInstance(), world, entityId);
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
		Entity entity = this.getClientWorld() != null ? this.getClientWorld().getEntityById(entityId) : null;

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

		if (world instanceof ServerWorld sl && this.isOnLocalServerThread())
		{
			return this.refreshEntityFromWorld(sl, entityId);
		}
		else if (this.hasSingleplayerServer() && !this.getRequestTracker().hasPendingLocalEntity(entityId))
		{
			this.getRequestTracker().setPendingLocalEntityRequest(entityId, true);
			this.requestEntityFromLocalServer(MinecraftClient.getInstance(), world, entityId);
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
	default @Nullable Pair<Entity, CompoundData> refreshEntityFromWorld(World world, int entityId)
	{
		if (world != null)
		{
			Entity entity = world.getEntityById(entityId);

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
	default boolean requestEntityFromLocalServer(MinecraftClient mc, World world, int entityId)
	{
		if (mc.isIntegratedServerRunning() && mc.getServer() != null &&
				!this.isOnLocalServerThread())
		{
			mc.getServer().execute(() ->
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
	default Inventory getBlockInventory(World world, BlockPos pos, boolean useNbt)
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
			Inventory inv = null;
			BlockState state = world.getBlockState(pos.toVanillaPos());

			if (!useNbt && (state.isIn(BlockTags.AIR) || !state.hasBlockEntity()))
			{
				this.getCache().removeFromCache(pos);
				return null;
			}

			if (state.contains(Properties.CHEST_TYPE) && state.contains(Properties.HORIZONTAL_FACING))
			{
				ChestType type = state.get(Properties.CHEST_TYPE);

				if (type != ChestType.SINGLE)
				{
					Direction facing = Direction.of(state.get(Properties.HORIZONTAL_FACING));
					Direction offsetDir = type == ChestType.LEFT ? facing.rotateY() : facing.rotateYCCW();
					BlockPos posAdj = pos.offset(offsetDir);

					if (!world.isChunkLoaded(posAdj))
					{
						return null;
					}

					BlockState stateAdj = world.getBlockState(posAdj.toVanillaPos());
					EntityDataPairEntry pairAdj = this.getCache().getBlockEntityPairFromCache(posAdj);

					if (pairAdj == null)
					{
						// Issue a network request for the missing half
						this.requestBlockEntity(world, posAdj);
					}
					else if (stateAdj.getBlock() == state.getBlock() &&
							 stateAdj.contains(Properties.CHEST_TYPE) &&
							 stateAdj.contains(Properties.HORIZONTAL_FACING) &&
							 stateAdj.get(Properties.CHEST_TYPE) != ChestType.SINGLE &&
							 stateAdj.get(Properties.HORIZONTAL_FACING) == facing.getVanillaDirection())
					{
						Inventory inv1 = null;
						Inventory inv2 = null;

						if (useNbt)
						{
							inv1 = InventoryUtils.getDataInventory(pair.data(), -1, world.getRegistryManager());
							inv2 = InventoryUtils.getDataInventory(pairAdj.data(), -1, world.getRegistryManager());
						}
						else if (pair.be() instanceof Inventory c1 && pairAdj.be() instanceof Inventory c2)
						{
							inv1 = c1;
							inv2 = c2;
						}

						if (inv1 != null && inv2 != null)
						{
							Inventory invRight = type == ChestType.RIGHT ? inv1 : inv2;
							Inventory invLeft = type == ChestType.RIGHT ? inv2 : inv1;

							inv = new DoubleInventory(invRight, invLeft);
						}
					}
				}
			}

			if (inv == null)
			{
				if (useNbt)
				{
					inv = InventoryUtils.getDataInventory(pair.data(), -1, world.getRegistryManager());
				}
				else if (pair.be() instanceof Inventory inv2)
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
	default Inventory getEntityInventory(World world, int entityId, boolean useData)
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
			Inventory inv = null;

			if (useData)
			{
				inv = InventoryUtils.getDataInventory(pair.data(), -1, world.getRegistryManager());
			}
			else
			{
				Entity entity = pair.ent();

				if (entity instanceof Inventory)
				{
					inv = (Inventory) entity;
				}
				else if (entity instanceof PlayerEntity player && player != null)
				{
					inv = new SimpleInventory(player.getInventory().getMainStacks().toArray(new ItemStack[36]));
				}
				else if (entity instanceof VillagerEntity)
				{
					inv = ((VillagerEntity) entity).getInventory();
				}
				else if (entity instanceof AbstractHorseEntity)
				{
					inv = ((IMixinAbstractHorseEntity) entity).malilib_getHorseInventory();
				}
//				else if (entity instanceof AbstractNautilusEntity)
//				{
//					inv = ((IMixinAbstractNautilus) entity).malilib_getNautilusInventory();
//				}
				else if (entity instanceof PiglinEntity)
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
	default BlockEntity handleBlockEntityData(BlockPos pos, NbtCompound nbt)
	{
		return this.handleBlockEntityData(pos, DataConverterNbt.fromVanillaCompound(nbt));
	}

	/**
	 * Used by your Packet Receiver to hande incoming data from the entityId and the Server Side NBT tags.
	 *
	 * @param nbt ()
	 * @return (Entity|Null)
	 */
	default Entity handleEntityData(int entityId, NbtCompound nbt)
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
	default void handleBulkEntityData(int transactionId, NbtCompound nbt)
	{
		this.handleBulkEntityData(transactionId, DataConverterNbt.fromVanillaCompound(nbt));
	}

	/**
	 * Vanilla QueryNbt Packet Receiver & Handling
	 *
	 * @param transactionId (QueryNbt Transaction Id)
	 * @param nbt           (The NBT Data returned by the server)
	 */
	default void handleVanillaQueryNbt(int transactionId, NbtCompound nbt)
	{
		this.handleVanillaQueryNbt(transactionId, DataConverterNbt.fromVanillaCompound(nbt));
	}

	/**
	 * Specific format for receiving Packets
	 * @param pos -
	 * @param nbt -
	 * @return -
	 */
	default @Nullable BlockEntity handleBlockEntityData(net.minecraft.util.math.BlockPos pos, NbtCompound nbt)
	{
		return handleBlockEntityData(BlockPos.of(pos), DataConverterNbt.fromVanillaCompound(nbt));
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
				Identifier id = Registries.BLOCK_ENTITY_TYPE.getId(be.getType());

				if (id != null)
				{
					data.putString(NbtKeys.ID, id.toString());
				}
			}

			this.getCache().removeFromCache(pos);
			this.getCache().addToCache(pos, be, data);

			if (this.loadContainerBlockEntities() && be instanceof Inventory)
			{
				NbtView view = NbtView.getReader(data, this.getClientWorld().getRegistryManager());
				be.read(view.getReader());
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
		Entity entity = this.getClientWorld().getEntityById(entityId);

		if (entity != null)
		{
			if (!data.contains(NbtKeys.ID, Constants.NBT.TAG_STRING))
			{
				Identifier id = EntityType.getId(entity.getType());

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
