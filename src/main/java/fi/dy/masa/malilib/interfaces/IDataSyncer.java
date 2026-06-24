package fi.dy.masa.malilib.interfaces;

import javax.annotation.Nullable;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;
import fi.dy.masa.malilib.mixin.entity.IMixinAbstractHorseEntity;
import fi.dy.masa.malilib.mixin.entity.IMixinPiglinEntity;
import fi.dy.masa.malilib.util.InventoryUtils;
import fi.dy.masa.malilib.util.WorldUtils;
import fi.dy.masa.malilib.util.data.DataEntityUtils;
import fi.dy.masa.malilib.util.data.tag.CompoundData;
import fi.dy.masa.malilib.util.data.tag.converter.DataConverterNbt;
import fi.dy.masa.malilib.util.nbt.NbtEntityUtils;

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
    default World getWorld()
    {
        if (MinecraftClient.getInstance() == null)
        {
            return null;
        }

        return WorldUtils.getBestWorld(MinecraftClient.getInstance());
    }

    /**
     * Get the Client World Object
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
    default NbtCompound getFromBlockEntityCacheNbt(BlockPos pos) { return null; }

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
    default NbtCompound getFromEntityCacheNbt(int entityId) { return null; }

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

    /**
     * Request the Block Entity Pair from the server;
     * if the Cache contains the Data, return the data Pair.
     * @param world ()
     * @param pos ()
     * @return (The Data Pair|Null)
     */
    @Nullable
    default Pair<BlockEntity, NbtCompound> requestBlockEntity(World world, BlockPos pos)
    {
        if (world == null)
        {
            world = this.getWorld();
        }

        if (world == null) return null;

        if (world.getBlockState(pos).getBlock() instanceof BlockEntityProvider)
        {
            BlockEntity be = world.getWorldChunk(pos).getBlockEntity(pos);

            if (be != null)
            {
                NbtCompound nbt = be.createNbtWithIdentifyingData(world.getRegistryManager());

                return Pair.of(be, nbt);
            }
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
	default Pair<BlockEntity, CompoundData> requestBlockEntityNew(World world, BlockPos pos)
	{
		if (world == null)
		{
			world = this.getWorld();
		}

		if (world == null) return null;

		if (world.getBlockState(pos).getBlock() instanceof BlockEntityProvider)
		{
			BlockEntity be = world.getWorldChunk(pos).getBlockEntity(pos);

			if (be != null)
			{
				return Pair.of(be, DataConverterNbt.fromVanillaCompound(be.createNbtWithIdentifyingData(world.getRegistryManager())));
			}
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
    default Pair<Entity, NbtCompound> requestEntity(World world, int entityId)
    {
        if (world == null)
        {
            world = this.getWorld();
        }

        if (world == null) return null;

        Entity entity = world.getEntityById(entityId);

        if (entity != null)
        {
            return Pair.of(entity, NbtEntityUtils.invokeEntityNbtDataNoPassengers(entity, entityId));
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
	default Pair<Entity, CompoundData> requestEntityNew(World world, int entityId)
	{
		if (world == null)
		{
			world = this.getWorld();
		}

		if (world == null) return null;

		Entity entity = world.getEntityById(entityId);

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
    default Inventory getBlockInventory(World world, BlockPos pos, boolean useNbt)
    {
        if (world == null)
        {
            world = this.getWorld();
        }

        if (world == null) return null;

        Pair<BlockEntity, NbtCompound> pair = this.requestBlockEntity(world, pos);
        Inventory inv = null;

        if (pair == null) return null;

        if (useNbt)
        {
            inv = InventoryUtils.getNbtInventory(pair.getRight(), -1, world.getRegistryManager());
        }
        else
        {
            BlockEntity be = pair.getLeft();
            BlockState state = world.getBlockState(pos);

            if (state.isIn(BlockTags.AIR) || !state.hasBlockEntity())
            {
                // Don't keep requesting if we're tick warping or something.
                return null;
            }

            if (be instanceof Inventory inv1)
            {
                if (be instanceof ChestBlockEntity && state.contains(ChestBlock.CHEST_TYPE))
                {
                    ChestType type = state.get(ChestBlock.CHEST_TYPE);

                    if (type != ChestType.SINGLE)
                    {
                        BlockPos posAdj = pos.offset(ChestBlock.getFacing(state));

                        if (!world.isChunkLoaded(posAdj)) return null;
                        BlockState stateAdj = world.getBlockState(posAdj);

                        Pair<BlockEntity, NbtCompound> pairAdj = this.requestBlockEntity(world, posAdj);

                        if (pairAdj == null)
                        {
                            return inv1;
                        }

                        if (stateAdj.getBlock() == state.getBlock() &&
                            pairAdj.getLeft() instanceof ChestBlockEntity inv2 &&
                            stateAdj.get(ChestBlock.CHEST_TYPE) != ChestType.SINGLE &&
                            stateAdj.get(ChestBlock.FACING) == state.get(ChestBlock.FACING))
                        {
                            Inventory invRight = type == ChestType.RIGHT ? inv1 : inv2;
                            Inventory invLeft = type == ChestType.RIGHT ? inv2 : inv1;

                            inv = new DoubleInventory(invRight, invLeft);
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
	 * Used to Obtain the Inventory Object from the Specified BlockPos,
	 * and handle if it is a Double Chest.  If the Data doesn't exist in the Cache, request it.
	 * @param world (Provided for compatibility with other worlds)
	 * @param pos ()
	 * @param useNbt ()
	 * @return (Inventory|EmptyInventory|Null)
	 */
	@Nullable
	@SuppressWarnings("deprecation")
	default Inventory getBlockInventoryNew(World world, BlockPos pos, boolean useNbt)
	{
		if (world == null)
		{
			world = this.getWorld();
		}

		if (world == null) return null;

		Pair<BlockEntity, CompoundData> pair = this.requestBlockEntityNew(world, pos);
		Inventory inv = null;

		if (pair == null) return null;

		if (useNbt)
		{
			inv = InventoryUtils.getDataInventory(pair.getRight(), -1, world.getRegistryManager());
		}
		else
		{
			BlockEntity be = pair.getLeft();
			BlockState state = world.getBlockState(pos);

			if (state.isIn(BlockTags.AIR) || !state.hasBlockEntity())
			{
				// Don't keep requesting if we're tick warping or something.
				return null;
			}

			if (be instanceof Inventory inv1)
			{
				if (be instanceof ChestBlockEntity && state.contains(ChestBlock.CHEST_TYPE))
				{
					ChestType type = state.get(ChestBlock.CHEST_TYPE);

					if (type != ChestType.SINGLE)
					{
						BlockPos posAdj = pos.offset(ChestBlock.getFacing(state));

						if (!world.isChunkLoaded(posAdj)) return null;
						BlockState stateAdj = world.getBlockState(posAdj);

						Pair<BlockEntity, CompoundData> pairAdj = this.requestBlockEntityNew(world, posAdj);

						if (pairAdj == null)
						{
							return inv1;
						}

						if (stateAdj.getBlock() == state.getBlock() &&
							pairAdj.getLeft() instanceof ChestBlockEntity inv2 &&
							stateAdj.get(ChestBlock.CHEST_TYPE) != ChestType.SINGLE &&
							stateAdj.get(ChestBlock.FACING) == state.get(ChestBlock.FACING))
						{
							Inventory invRight = type == ChestType.RIGHT ? inv1 : inv2;
							Inventory invLeft = type == ChestType.RIGHT ? inv2 : inv1;

							inv = new DoubleInventory(invRight, invLeft);
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
     * @param useNbt ()
     * @return (Inventory|Null)
     */
    @Nullable
    default Inventory getEntityInventory(World world, int entityId, boolean useNbt)
    {
        if (world == null)
        {
            world = this.getWorld();
        }

        if (world == null) return null;

        Pair<Entity, NbtCompound> pair = this.requestEntity(world, entityId);
        Inventory inv = null;

        if (pair == null) return null;

        if (useNbt)
        {
            inv = InventoryUtils.getNbtInventory(pair.getRight(), -1, world.getRegistryManager());
        }
        else
        {
            Entity entity = pair.getLeft();

            if (entity instanceof Inventory)
            {
                inv = (Inventory) entity;
            }
            else if (entity instanceof PlayerEntity player)
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
            else if (entity instanceof PiglinEntity)
            {
                inv = ((IMixinPiglinEntity) entity).malilib_getInventory();
            }

            return inv;
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
	default Inventory getEntityInventoryNew(World world, int entityId, boolean useData)
	{
		if (world == null)
		{
			world = this.getWorld();
		}

		if (world == null) return null;

		Pair<Entity, CompoundData> pair = this.requestEntityNew(world, entityId);
		Inventory inv = null;

		if (pair == null) return null;

		if (useData)
		{
			inv = InventoryUtils.getDataInventory(pair.getRight(), -1, world.getRegistryManager());
		}
		else
		{
			Entity entity = pair.getLeft();

			if (entity instanceof Inventory)
			{
				inv = (Inventory) entity;
			}
			else if (entity instanceof PlayerEntity player)
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
			else if (entity instanceof PiglinEntity)
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
    default BlockEntity handleBlockEntityData(BlockPos pos, NbtCompound nbt, @Nullable Identifier type) { return null; }

    /**
     * Used by your Packet Receiver to hande incoming data from the entityId and the Server Side NBT tags.
     * @param nbt ()
     * @return (Entity|Null)
     */
    default Entity handleEntityData(int entityId, NbtCompound nbt) { return null; }

    /**
     * Used by your Packet Receiver if any Bulk handling of NBT Tags for multiple Entities is required.
     * This is usually used for something like downloading an entire ChunkPos worth of Entity Data; such as with Litematica.
     * @param transactionId ()
     * @param nbt ()
     */
    default void handleBulkEntityData(int transactionId, NbtCompound nbt) {}

    /**
     * Vanilla QueryNbt Packet Receiver & Handling
     * @param transactionId (QueryNbt Transaction Id)
     * @param nbt (The NBT Data returned by the server)
     */
    default void handleVanillaQueryNbt(int transactionId, NbtCompound nbt) {}

	/**
	 * Used by your Packet Receiver to hande incoming data from BlockPos and the Server Side NBT tags.
	 * @param pos ()
	 * @param data ()
	 * @param type (Optional)
	 * @return (BlockEntity|Null)
	 */
	@ApiStatus.Experimental
	default BlockEntity handleBlockEntityData(BlockPos pos, CompoundData data, @Nullable Identifier type) { return null; }

	/**
	 * Used by your Packet Receiver to hande incoming data from the entityId and the Server Side NBT tags.
	 * @param data ()
	 * @return (Entity|Null)
	 */
	@ApiStatus.Experimental
	default Entity handleEntityData(int entityId, CompoundData data) { return null; }

	/**
	 * Used by your Packet Receiver if any Bulk handling of NBT Tags for multiple Entities is required.
	 * This is usually used for something like downloading an entire ChunkPos worth of Entity Data; such as with Litematica.
	 * @param transactionId ()
	 * @param data ()
	 */
	@ApiStatus.Experimental
	default void handleBulkEntityData(int transactionId, CompoundData data) {}

	/**
	 * Vanilla QueryNbt Packet Receiver & Handling
	 * @param transactionId (QueryNbt Transaction Id)
	 * @param data (The NBT Data returned by the server)
	 */
	@ApiStatus.Experimental
	default void handleVanillaQueryNbt(int transactionId, CompoundData data) {}
}
