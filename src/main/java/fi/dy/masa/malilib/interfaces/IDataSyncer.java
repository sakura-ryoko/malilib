package fi.dy.masa.malilib.interfaces;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.nautilus.AbstractNautilus;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.apache.commons.lang3.tuple.Pair;

import fi.dy.masa.malilib.mixin.entity.IMixinAbstractHorseEntity;
import fi.dy.masa.malilib.mixin.entity.IMixinAbstractNautilus;
import fi.dy.masa.malilib.mixin.entity.IMixinPiglinEntity;
import fi.dy.masa.malilib.util.InventoryUtils;
import fi.dy.masa.malilib.util.WorldUtils;
import fi.dy.masa.malilib.util.data.DataEntityUtils;
import fi.dy.masa.malilib.util.data.tag.CompoundData;
import fi.dy.masa.malilib.util.data.tag.converter.DataConverterNbt;

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
     * Get the 'Best World' object
     * @return ()
     */
    @Nullable
    default Level getWorld()
    {
        if (Minecraft.getInstance() == null)
        {
            return null;
        }

        return WorldUtils.getBestWorld(Minecraft.getInstance());
    }

    /**
     * Get the Client World Object
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
     * Called when Joining / Leaving worlds; used to "reset" any Data Syncer Cache.
     * @param isLogout ()
     */
    default void reset(boolean isLogout) { }

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
     * @param pos ()
     * @return ()
     */
    @Nullable
    default CompoundTag getFromBlockEntityCacheNbt(BlockPos pos)
    {
		CompoundData data =this.getFromBlockEntityCacheData(pos);

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
	 * @param pos ()
	 * @return ()
	 */
	@Nullable
	default CompoundData getFromBlockEntityCacheData(BlockPos pos) { return null; }

    /**
     * Used to return an BlockEntity Object from the Entity Data Syncer Cache at the specific BlockPos.
     * Note, that these functions are intended to be simple Getters.
     * For Requesting Server Data, use `requestBlockEntity()`
     * @param pos ()
     * @return ()
     */
    @Nullable
    default BlockEntity getFromBlockEntityCache(BlockPos pos) { return null; }

    /**
     * Used to return an NBT Object from the Entity Data Syncer Cache at the specific BlockPos.
     * Note, that these functions are intended to be simple Getters.
     * For Requesting Server Data, use `requestEntity()`
     * @param entityId ()
     * @return ()
     */
    @Nullable
    default CompoundTag getFromEntityCacheNbt(int entityId)
    {
	    CompoundData data =this.getFromEntityCacheData(entityId);

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
	 * @param entityId ()
	 * @return ()
	 */
	@Nullable
	default CompoundData getFromEntityCacheData(int entityId) { return null; }

    /**
     * Used to return an Entity Object from the Entity Data Syncer Cache at the specific BlockPos.
     * Note, that these functions are intended to be simple Getters.
     * For Requesting Server Data, use `requestEntity()`
     * @param entityId ()
     * @return ()
     */
    @Nullable
    default Entity getFromEntityCache(int entityId) { return null; }

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
	 * @param world ()
	 * @param pos ()
	 * @return (The Data Pair|Null)
	 */
	@Nullable
	default Pair<BlockEntity, CompoundData> requestBlockEntity(Level world, BlockPos pos)
	{
		if (world == null)
		{
			world = this.getWorld();
		}

		if (world == null) return null;

		if (world.getBlockState(pos).getBlock() instanceof EntityBlock)
		{
			BlockEntity be = world.getChunkAt(pos).getBlockEntity(pos);

			if (be != null)
			{
				return Pair.of(be, DataConverterNbt.fromVanillaCompound(be.saveWithFullMetadata(world.registryAccess())));
			}
		}

		return null;
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
	 * @param entityId ()
	 * @return (The Data Pair|Null)
	 */
	@Nullable
	default Pair<Entity, CompoundData> requestEntity(Level world, int entityId)
	{
		if (world == null)
		{
			world = this.getWorld();
		}

		if (world == null) return null;

		Entity entity = world.getEntity(entityId);

		if (entity != null)
		{
			return Pair.of(entity, DataEntityUtils.invokeEntityDataTagNoPassengers(entity, entityId));
		}

		return null;
	}

	/**
	 * Used to Obtain the Inventory Object from the Specified BlockPos,
	 * and handle if it is a Double Chest.  If the Data doesn't exist in the Cache, request it.
	 * @param world (Provided for compatibility with other worlds)
	 * @param pos ()
	 * @param useNbt ()
	 * @return (Inventory|EmptyInventory|Null)
	 */
	@Nullable
	@SuppressWarnings("deprecation")
	default Container getBlockInventory(Level world, BlockPos pos, boolean useNbt)
	{
		if (world == null)
		{
			world = this.getWorld();
		}

		if (world == null) return null;

		Pair<BlockEntity, CompoundData> pair = this.requestBlockEntity(world, pos);
		Container inv = null;

		if (pair == null) return null;

		if (useNbt)
		{
			inv = InventoryUtils.getDataInventory(pair.getRight(), -1, world.registryAccess());
		}
		else
		{
			BlockEntity be = pair.getLeft();
			BlockState state = world.getBlockState(pos);

			if (state.is(BlockTags.AIR) || !state.hasBlockEntity())
			{
				// Don't keep requesting if we're tick warping or something.
				return null;
			}

			if (be instanceof Container inv1)
			{
				if (be instanceof ChestBlockEntity && state.hasProperty(ChestBlock.TYPE))
				{
					ChestType type = state.getValue(ChestBlock.TYPE);

					if (type != ChestType.SINGLE)
					{
						BlockPos posAdj = pos.relative(ChestBlock.getConnectedDirection(state));

						if (!world.hasChunkAt(posAdj)) return null;
						BlockState stateAdj = world.getBlockState(posAdj);

						Pair<BlockEntity, CompoundData> pairAdj = this.requestBlockEntity(world, posAdj);

						if (pairAdj == null)
						{
							return inv1;
						}

						if (stateAdj.getBlock() == state.getBlock() &&
							pairAdj.getLeft() instanceof ChestBlockEntity inv2 &&
							stateAdj.getValue(ChestBlock.TYPE) != ChestType.SINGLE &&
							stateAdj.getValue(ChestBlock.FACING) == state.getValue(ChestBlock.FACING))
						{
							Container invRight = type == ChestType.RIGHT ? inv1 : inv2;
							Container invLeft = type == ChestType.RIGHT ? inv2 : inv1;

							inv = new CompoundContainer(invRight, invLeft);
						}
					}
					else
					{
						inv = inv1;
					}
				}
				else
				{
					inv = inv1;
				}
			}
		}

		return inv;
	}

	/**
	 * Used to Obtain the Inventory Object from the Specified Entity, if available;
	 * and handle if it needs special handling.  If the Data doesn't exist in the Cache, request it.
	 * @param entityId ()
	 * @param useData ()
	 * @return (Inventory|Null)
	 */
	@Nullable
	default Container getEntityInventory(Level world, int entityId, boolean useData)
	{
		if (world == null)
		{
			world = this.getWorld();
		}

		if (world == null) return null;

		Pair<Entity, CompoundData> pair = this.requestEntity(world, entityId);
		Container inv = null;

		if (pair == null) return null;

		if (useData)
		{
			inv = InventoryUtils.getDataInventory(pair.getRight(), -1, world.registryAccess());
		}
		else
		{
			Entity entity = pair.getLeft();

			if (entity instanceof Container)
			{
				inv = (Container) entity;
			}
			else if (entity instanceof Player player)
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

		return inv;
	}

    /**
     * Used by your Packet Receiver to hande incoming data from BlockPos and the Server Side NBT tags.
     * @param pos ()
     * @param nbt ()
     * @param type (Optional)
     * @return (BlockEntity|Null)
     */
    default BlockEntity handleBlockEntityData(BlockPos pos, CompoundTag nbt, @Nullable Identifier type)
    {
		return this.handleBlockEntityData(pos, DataConverterNbt.fromVanillaCompound(nbt), type);
	}

    /**
     * Used by your Packet Receiver to hande incoming data from the entityId and the Server Side NBT tags.
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
     * @param transactionId ()
     * @param nbt ()
     */
    default void handleBulkEntityData(int transactionId, CompoundTag nbt)
    {
		this.handleBulkEntityData(transactionId, DataConverterNbt.fromVanillaCompound(nbt));
    }

    /**
     * Vanilla QueryNbt Packet Receiver & Handling
     * @param transactionId (QueryNbt Transaction Id)
     * @param nbt (The NBT Data returned by the server)
     */
    default void handleVanillaQueryNbt(int transactionId, CompoundTag nbt)
    {
		this.handleVanillaQueryNbt(transactionId, DataConverterNbt.fromVanillaCompound(nbt));
    }

	/**
	 * Used by your Packet Receiver to hande incoming data from BlockPos and the Server Side NBT tags.
	 * @param pos ()
	 * @param data ()
	 * @param type (Optional)
	 * @return (BlockEntity|Null)
	 */
	default BlockEntity handleBlockEntityData(BlockPos pos, CompoundData data, @Nullable Identifier type) { return null; }

	/**
	 * Used by your Packet Receiver to hande incoming data from the entityId and the Server Side NBT tags.
	 * @param data ()
	 * @return (Entity|Null)
	 */
	default Entity handleEntityData(int entityId, CompoundData data) { return null; }

	/**
	 * Used by your Packet Receiver if any Bulk handling of NBT Tags for multiple Entities is required.
	 * This is usually used for something like downloading an entire ChunkPos worth of Entity Data; such as with Litematica.
	 * @param transactionId ()
	 * @param data ()
	 */
	default void handleBulkEntityData(int transactionId, CompoundData data) {}

	/**
	 * Vanilla QueryNbt Packet Receiver & Handling
	 * @param transactionId (QueryNbt Transaction Id)
	 * @param data (The NBT Data returned by the server)
	 */
	default void handleVanillaQueryNbt(int transactionId, CompoundData data) {}
}
