package fi.dy.masa.malilib.test.render;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.nautilus.AbstractNautilus;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.interfaces.IDataSyncer;
import fi.dy.masa.malilib.interfaces.IInventoryOverlayHandler;
import fi.dy.masa.malilib.mixin.entity.IMixinAbstractHorseEntity;
import fi.dy.masa.malilib.mixin.entity.IMixinAbstractNautilus;
import fi.dy.masa.malilib.render.GuiContext;
import fi.dy.masa.malilib.render.InventoryOverlay;
import fi.dy.masa.malilib.render.InventoryOverlayContext;
import fi.dy.masa.malilib.render.InventoryOverlayRefresher;
import fi.dy.masa.malilib.test.data.TestDataSyncer;
import fi.dy.masa.malilib.util.EntityUtils;
import fi.dy.masa.malilib.util.InventoryUtils;
import fi.dy.masa.malilib.util.WorldUtils;
import fi.dy.masa.malilib.util.data.Constants;
import fi.dy.masa.malilib.util.data.DataBlockUtils;
import fi.dy.masa.malilib.util.data.DataEntityUtils;
import fi.dy.masa.malilib.util.data.tag.CompoundData;
import fi.dy.masa.malilib.util.data.tag.converter.DataConverterNbt;
import fi.dy.masa.malilib.util.game.RayTraceUtils;
import fi.dy.masa.malilib.util.nbt.NbtInventory;
import fi.dy.masa.malilib.util.nbt.NbtKeys;

@ApiStatus.Experimental
public class TestInventoryOverlayHandler implements IInventoryOverlayHandler
{
    private static final TestInventoryOverlayHandler INSTANCE = new TestInventoryOverlayHandler();

    public static TestInventoryOverlayHandler getInstance() { return INSTANCE; }

    IDataSyncer syncer;
	InventoryOverlayContext context;
	InventoryOverlayRefresher refresher;

    //private Pair<BlockPos, InventoryOverlayContext> lastBlockEntityContext;
    //private Pair<Integer,  InventoryOverlayContext> lastEntityContext;

    public TestInventoryOverlayHandler()
    {
        //this.lastBlockEntityContext = null;
        //this.lastEntityContext = null;
        this.context = null;
        this.refresher = null;
        this.syncer = null;
    }

    @Override
    public String getModId()
    {
        return MaLiLibReference.MOD_ID;
    }

    @Override
    public IDataSyncer getDataSyncer()
    {
        if (this.syncer == null)
        {
            this.syncer = TestDataSyncer.getInstance();
        }

        return this.syncer;
    }

    @Override
    public void setDataSyncer(IDataSyncer syncer)
    {
        this.syncer = syncer;
    }

	@Override
	public InventoryOverlayRefresher getRefreshHandler()
	{
		if (this.refresher == null)
		{
			this.refresher = new Refresher();
		}

		return this.refresher;
	}

	@Override
    public boolean isEmpty()
    {
        return this.context == null;
    }

	@Override
	public @Nullable InventoryOverlayContext getRenderContextNullable()
	{
		return this.context;
	}

	@Override
	public @Nullable InventoryOverlayContext getRenderContext(GuiContext ctx, ProfilerFiller profiler)
	{
		profiler.push(this.getClass().getName() + "_inventory_overlay");
		this.getTargetInventory(ctx.mc());

		if (!this.isEmpty())
		{
			this.renderInventoryOverlay(ctx, this.getRenderContextNullable(),
			                            true,
			                            true);
		}

		profiler.pop();
		return this.getRenderContextNullable();
	}

	@Override
	public @Nullable InventoryOverlayContext getTargetInventory(Minecraft mc)
	{
		Level world = WorldUtils.getBestWorld(mc);
		Entity cameraEntity = EntityUtils.getCameraEntity();
		this.context = null;

		if (mc.player == null || world == null || mc.level == null)
		{
			return null;
		}

		if (cameraEntity == mc.player && world instanceof ServerLevel)
		{
			// We need to get the player from the server world (if available, i.e. in single player),
			// so that the player itself won't be included in the ray trace
			Entity serverPlayer = world.getPlayerByUUID(mc.player.getUUID());

			if (serverPlayer != null)
			{
				cameraEntity = serverPlayer;
			}
		}

		if (cameraEntity == null)
		{
			return null;
		}

		HitResult trace;

		if (cameraEntity != mc.player)
		{
			trace = RayTraceUtils.getRayTraceFromEntity(mc.level, cameraEntity, ClipContext.Fluid.NONE);
		}
		else
		{
			trace = mc.hitResult;
		}

		CompoundData data = new CompoundData();

		if (trace == null || trace.getType() == HitResult.Type.MISS)
		{
			return null;
		}

		if (trace.getType() == HitResult.Type.BLOCK)
		{
			BlockPos pos = ((BlockHitResult) trace).getBlockPos();
			BlockState state = world.getBlockState(pos);
			Block blockTmp = state.getBlock();
			BlockEntity be = null;

			MaLiLib.LOGGER.warn("getTarget():1: pos [{}], state [{}]", pos.toShortString(), state.toString());

			if (blockTmp instanceof EntityBlock)
			{
				if (world instanceof ServerLevel)
				{
					be = world.getChunkAt(pos).getBlockEntity(pos);

					if (be != null)
					{
						data = DataConverterNbt.fromVanillaCompound(be.saveWithFullMetadata(world.registryAccess()));
					}
				}
				else
				{
					Pair<BlockEntity, CompoundData> pair = this.getDataSyncer().requestBlockEntity(world, pos);

					if (pair != null)
					{
						data = pair.getRight();
					}
				}

				MaLiLib.LOGGER.warn("getTarget():2: pos [{}], be [{}], data [{}]", pos.toShortString(), be != null, data != null);
				return this.getTargetInventoryFromBlock(world, pos, be, data);
			}

			return null;
		}
		else if (trace.getType() == HitResult.Type.ENTITY)
		{
			Entity entity = ((EntityHitResult) trace).getEntity();

			if (entity.getUUID().equals(cameraEntity.getUUID()))
			{
				return null;
			}

			if (mc.crosshairPickEntity != null && entity.getId() != mc.crosshairPickEntity.getId())
			{
				MaLiLib.LOGGER.error("getTarget(): entityId Not Equal: [{} != {}]", entity.getId(), mc.crosshairPickEntity.getId());
			}

			MaLiLib.LOGGER.warn("getTarget(): entityUUID [{}] vs targetedUUID [{}]", entity.getStringUUID(), mc.crosshairPickEntity != null ? mc.crosshairPickEntity.getStringUUID() : "<NULL>");

			if (world instanceof ServerLevel)
			{
				entity = world.getEntity(entity.getId());

				if (entity != null)
				{
					return this.getTargetInventoryFromEntity(entity, DataEntityUtils.invokeEntityDataTagNoPassengers(entity, entity.getId()));
				}
			}
			else
			{
				Pair<Entity, CompoundData> pair = this.getDataSyncer().requestEntity(world, entity.getId());

				if (pair != null)
				{
					return this.getTargetInventoryFromEntity(world.getEntity(pair.getLeft().getId()), pair.getRight());
				}
			}
		}

		return null;
	}

	@Override
	public @Nullable InventoryOverlayContext getTargetInventoryFromBlock(Level world, BlockPos pos, @Nullable BlockEntity be, CompoundData data)
	{
		Container inv;

		if (be != null)
		{
			if (data.isEmpty())
			{
				data = DataConverterNbt.fromVanillaCompound(be.saveWithFullMetadata(world.registryAccess()));
			}

			inv = InventoryUtils.getInventory(world, pos);
		}
		else
		{
			if (data.isEmpty())
			{
				Pair<BlockEntity, CompoundData> pair = this.getDataSyncer().requestBlockEntity(world, pos);

				if (pair != null)
				{
					data = pair.getRight();
				}
			}

			inv = this.getDataSyncer().getBlockInventory(world, pos, false);
		}

		MaLiLib.LOGGER.error("getTargetFromBlock: inv [{}], data [{}]", inv != null ? inv.getContainerSize() : "<NULL>", data != null ? data.toString() : "<NULL>");
		BlockEntityType<?> beType = data != null ? DataBlockUtils.getBlockEntityType(data) : null;

		if ((beType != null && beType.equals(BlockEntityType.ENDER_CHEST)) ||
			be instanceof EnderChestBlockEntity)
		{
			if (Minecraft.getInstance().player != null)
			{
				Player player = world.getPlayerByUUID(Minecraft.getInstance().player.getUUID());

				if (player != null)
				{
					// Fetch your own EnderItems from Server ...
					Pair<Entity, CompoundData> enderPair = this.getDataSyncer().requestEntity(world, player.getId());
					PlayerEnderChestContainer enderItems = null;

					if (enderPair != null && enderPair.getRight() != null && enderPair.getRight().contains(NbtKeys.ENDER_ITEMS, Constants.NBT.TAG_LIST))
					{
						enderItems = InventoryUtils.getPlayerEnderItemsFromData(enderPair.getRight(), world.registryAccess());
					}
					else if (world instanceof ServerLevel)
					{
						enderItems = player.getEnderChestInventory();
					}

					if (enderItems != null)
					{
						inv = enderItems;
					}

					MaLiLib.LOGGER.error("getTargetFromBlock: EnderItems [{}]", enderItems != null ? enderItems.getContainerSize() : "<NULL>");
				}
			}
		}

		if (data != null && !data.isEmpty())
		{
			MaLiLib.LOGGER.warn("getTargetFromBlock(): rawData: [{}]", data.toString());
			Container inv2 = InventoryUtils.getDataInventory(data, inv != null ? inv.getContainerSize() : -1, world.registryAccess());

			if (inv == null)
			{
				inv = inv2;
			}
		}

		MaLiLib.LOGGER.warn("getTargetFromBlock():3: pos [{}], inv [{}], be [{}], data [{}]", pos.toShortString(), inv != null, be != null, data != null ? data.getString("id") : new CompoundData());

		if (inv == null || data == null)
		{
			return null;
		}

		this.context = new InventoryOverlayContext(InventoryOverlay.getBestInventoryType(inv, data), inv,
		                                              be != null ? be : world.getBlockEntity(pos), null,
		                                              data, this.getRefreshHandler());

		return this.context;
	}

	@Override
	public @Nullable InventoryOverlayContext getTargetInventoryFromEntity(Entity entity, CompoundData data)
	{
		Container inv = null;
		LivingEntity entityLivingBase = null;

		if (entity instanceof LivingEntity)
		{
			entityLivingBase = (LivingEntity) entity;
		}

		if (entity instanceof Container)
		{
			inv = (Container) entity;
		}
		else if (entity instanceof Player player)
		{
			inv = new SimpleContainer(player.getInventory().getNonEquipmentItems().toArray(new ItemStack[36]));
		}
		else if (entity instanceof AbstractHorse)
		{
			inv = ((IMixinAbstractHorseEntity) entity).malilib_getHorseInventory();
		}
		else if (entity instanceof AbstractNautilus)
		{
			inv = ((IMixinAbstractNautilus) entity).malilib_getNautilusInventory();
		}
		else if (entity instanceof InventoryCarrier)
		{
			inv = ((InventoryCarrier) entity).getInventory();
		}
		if (!data.isEmpty())
		{
			Container inv2;
			MaLiLib.LOGGER.warn("getTargetInventoryFromEntity(): rawData: [{}]", data.toString());

			// Fix for empty horse inv
			if (inv != null &&
				data.contains(NbtKeys.ITEMS, Constants.NBT.TAG_LIST) &&
				data.getList(NbtKeys.ITEMS).size() > 1)
			{
				MaLiLib.LOGGER.warn("getTargetInventoryFromEntity(): [Fix for horse inv] inv.size: [{}]", inv.getContainerSize());

				if (entity instanceof AbstractHorse || entity instanceof AbstractNautilus)
				{
					inv2 = InventoryUtils.getDataInventoryHorseFix(data, inv.getContainerSize(), entity.registryAccess());
				}
				else
				{
					inv2 = InventoryUtils.getDataInventory(data, inv.getContainerSize(), entity.registryAccess());
				}
				inv = null;
			}
			// Fix for saddled horse, no inv
			else if (inv != null &&
					data.containsLenient(NbtKeys.EQUIPMENT) && data.containsLenient(NbtKeys.EATING_HAY))
			{
				MaLiLib.LOGGER.warn("getTargetInventoryFromEntity(): [Fix for saddled horse inv] inv.size: [{}]", inv.getContainerSize());

				inv2 = InventoryUtils.getDataInventoryHorseFix(data, inv.getContainerSize(), entity.registryAccess());
				inv = null;
			}
			// Fix for empty Villager/Piglin inv
			else if (inv != null && inv.getContainerSize() == NbtInventory.VILLAGER_SIZE &&
					data.contains(NbtKeys.INVENTORY, Constants.NBT.TAG_LIST) &&
					!data.getList(NbtKeys.INVENTORY).isEmpty())
			{
				MaLiLib.LOGGER.warn("getTargetInventoryFromEntity(): [Fix for empty villager/piglin inv] inv.size: [{}]", inv.getContainerSize());
				inv2 = InventoryUtils.getDataInventory(data, NbtInventory.VILLAGER_SIZE, entity.registryAccess());
				inv = null;
			}
			else
			{
				MaLiLib.LOGGER.warn("getTargetInventoryFromEntity(): [Default] inv.size: [{}]", inv != null ? inv.getContainerSize() : -1);
				inv2 = InventoryUtils.getDataInventory(data, inv != null ? inv.getContainerSize() : -1, entity.registryAccess());

				if (inv2 != null)
				{
					inv = null;
				}
			}

			MaLiLib.LOGGER.error("getTargetInventoryFromEntity(): inv.size [{}], inv2.size [{}]", inv != null ? inv.getContainerSize() : "null", inv2 != null ? inv2.getContainerSize() : "null");

			if (inv2 != null)
			{
				inv = inv2;
			}
		}

		if (inv == null && entityLivingBase == null)
		{
			return null;
		}

		this.context = new InventoryOverlayContext(inv != null
		                                              ? InventoryOverlay.getBestInventoryType(inv, data)
		                                              : InventoryOverlay.getInventoryType(data),
		                                              inv, null, entityLivingBase, data, this.getRefreshHandler());

		return this.context;
	}

	public static class Refresher implements InventoryOverlayRefresher
	{
		public Refresher() {}

		@Override
		public InventoryOverlayContext onContextRefresh(InventoryOverlayContext data, Level world)
		{
			// Refresh data
			if (data.be() != null)
			{
				TestInventoryOverlayHandler.getInstance().requestBlockEntityAt(world, data.be().getBlockPos());
				data = TestInventoryOverlayHandler.getInstance().getTargetInventoryFromBlock(data.be().getLevel(), data.be().getBlockPos(), data.be(), data.data());
			}
			else if (data.entity() != null)
			{
				TestInventoryOverlayHandler.getInstance().getDataSyncer().requestEntity(world, data.entity().getId());
				data = TestInventoryOverlayHandler.getInstance().getTargetInventoryFromEntity(data.entity(), data.data());
			}

			return data;
		}
	}
}
