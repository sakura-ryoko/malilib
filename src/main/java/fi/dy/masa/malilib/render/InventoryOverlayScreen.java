package fi.dy.masa.malilib.render;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.HappyGhast;
import net.minecraft.world.entity.animal.coppergolem.CopperGolem;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.mixin.entity.IMixinMerchantEntity;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.InventoryUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.WorldUtils;
import fi.dy.masa.malilib.util.data.Constants;
import fi.dy.masa.malilib.util.data.DataBlockUtils;
import fi.dy.masa.malilib.util.data.DataEntityUtils;
import fi.dy.masa.malilib.util.data.tag.CompoundData;
import fi.dy.masa.malilib.util.game.BlockUtils;
import fi.dy.masa.malilib.util.nbt.NbtBlockUtils;
import fi.dy.masa.malilib.util.nbt.NbtEntityUtils;
import fi.dy.masa.malilib.util.nbt.NbtKeys;

public class InventoryOverlayScreen extends Screen implements Renderable
{
    String modId;
    private InventoryOverlay.Context previewData;
	private InventoryOverlayContext previewDataNew;
    private final boolean shulkerBGColors;
    private final boolean villagerBGColors;
    private int ticks;

	@Deprecated
    public InventoryOverlayScreen(String modId, @Nullable InventoryOverlay.Context previewData)
    {
        this(modId, previewData, true, false);
    }

	@Deprecated
	public InventoryOverlayScreen(String modId, @Nullable InventoryOverlay.Context previewData, boolean shulkerBGColors)
    {
        this(modId, previewData, shulkerBGColors, false);
    }

	@Deprecated
	public InventoryOverlayScreen(String modId, @Nullable InventoryOverlay.Context previewData, boolean shulkerBGColors, boolean villagerBGColors)
    {
        super(StringUtils.translateAsText(MaLiLibReference.MOD_ID + ".gui.title.inventory_overlay", modId));
        this.modId = modId;
        this.previewData = previewData;
		this.previewDataNew = null;
        this.shulkerBGColors = shulkerBGColors;
        this.villagerBGColors = villagerBGColors;
    }

	public InventoryOverlayScreen(String modId, @Nullable InventoryOverlayContext previewData)
	{
		this(modId, previewData, true, false);
	}

	public InventoryOverlayScreen(String modId, @Nullable InventoryOverlayContext previewData, boolean shulkerBGColors)
	{
		this(modId, previewData, shulkerBGColors, false);
	}

	public InventoryOverlayScreen(String modId, @Nullable InventoryOverlayContext previewData, boolean shulkerBGColors, boolean villagerBGColors)
	{
		super(StringUtils.translateAsText(MaLiLibReference.MOD_ID + ".gui.title.inventory_overlay", modId));
		this.modId = modId;
		this.previewData = null;
		this.previewDataNew = previewData;
		this.shulkerBGColors = shulkerBGColors;
		this.villagerBGColors = villagerBGColors;
	}

	@Override
    public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float deltaTicks)
    {
        // NO BLUR / MASKING
    }

    @Override
    public void render(GuiGraphics drawContext, int mouseX, int mouseY, float delta)
    {
		if (this.previewData != null)
		{
			this.renderNbt(drawContext, mouseX, mouseY, delta);
		}
		else if (this.previewDataNew != null)
	    {
		    this.renderData(drawContext, mouseX, mouseY, delta);
	    }
    }

	@Deprecated
	private void renderNbt(GuiGraphics drawContext, int mouseX, int mouseY, float delta)
	{
		this.ticks++;
		Minecraft mc = Minecraft.getInstance();
		Level world = WorldUtils.getBestWorld(mc);

		if (this.previewData != null && world != null)
		{
			final int xCenter = GuiUtils.getScaledWindowWidth() / 2;
			final int yCenter = GuiUtils.getScaledWindowHeight() / 2;
			int x = xCenter - 52 / 2;
			int y = yCenter - 92;

			int startSlot = 0;
			int totalSlots = this.previewData.inv() == null ? 0 : this.previewData.inv().getContainerSize();
			List<ItemStack> armourItems = new ArrayList<>();

			if (this.previewData.entity() instanceof AbstractHorse)
			{
				if (this.previewData.inv() == null)
				{
					MaLiLib.LOGGER.warn("renderNbt(): Horse inv() = null");
					return;
				}
				armourItems.add(this.previewData.entity().getItemBySlot(EquipmentSlot.BODY));
				armourItems.add(this.previewData.inv().getItem(0));
				startSlot = 1;
				totalSlots = this.previewData.inv().getContainerSize() - 1;
			}
			else if (this.previewData.entity() instanceof Wolf || this.previewData.entity() instanceof HappyGhast)
			{
				armourItems.add(this.previewData.entity().getItemBySlot(EquipmentSlot.BODY));
				//armourItems.add(ItemStack.EMPTY);
			}
			else if (this.previewData.entity() instanceof CopperGolem)
			{
				armourItems.add(this.previewData.entity().getItemBySlot(EquipmentSlot.SADDLE));
			}

			final InventoryOverlay.InventoryRenderType type = (this.previewData.entity() instanceof Villager) ? InventoryOverlay.InventoryRenderType.VILLAGER : InventoryOverlay.getBestInventoryType(this.previewData.inv(), this.previewData.nbt() != null ? this.previewData.nbt() : new CompoundTag(), this.previewData);
			final InventoryOverlay.InventoryProperties props = InventoryOverlay.getInventoryPropsTemp(type, totalSlots);
			final int rows = (int) Math.ceil((double) totalSlots / props.slotsPerRow);
			Set<Integer> lockedSlots = new HashSet<>();
			int xInv = xCenter - (props.width / 2);
			int yInv = yCenter - props.height - 6;

			if (rows > 6)
			{
				yInv -= (rows - 6) * 18;
				y -= (rows - 6) * 18;
			}

			if (MaLiLibReference.DEBUG_MODE)
			{
				MaLiLib.LOGGER.warn("renderNbt():0: type [{}], previewData.type [{}], previewData.inv [{}], previewData.be [{}], previewData.ent [{}], previewData.nbt [{}]", type.toString(), this.previewData.type().toString(),
				                    this.previewData.inv() != null, this.previewData.be() != null, this.previewData.entity() != null, this.previewData.nbt() != null ? this.previewData.nbt().getString("id").orElse("[invalid]") : null);
				MaLiLib.LOGGER.error("0: -> inv.type [{}] // nbt.type [{}]", this.previewData.inv() != null ? InventoryOverlay.getInventoryType(this.previewData.inv()) : null, this.previewData.nbt() != null ? InventoryOverlay.getInventoryType(this.previewData.nbt()) : null);
				MaLiLib.LOGGER.error("1: -> inv.size [{}] // inv.isEmpty [{}]", this.previewData.inv() != null ? this.previewData.inv().getContainerSize() : -1, this.previewData.inv() != null ? this.previewData.inv().isEmpty() : -1);
				MaLiLib.LOGGER.error("2: -> total slots [{}] // rows [{}] // startSlot [{}]", totalSlots, rows, startSlot);
			}

			if (this.previewData.entity() != null)
			{
				x = xCenter - 55;
				xInv = xCenter + 2;
				yInv = Math.min(yInv, yCenter - 92);
			}
			if (this.previewData.be() instanceof CrafterBlockEntity cbe)
			{
				lockedSlots = BlockUtils.getDisabledSlots(cbe);
			}
			else if (this.previewData.nbt() != null && this.previewData.nbt().contains(NbtKeys.DISABLED_SLOTS))
			{
				lockedSlots = NbtBlockUtils.getDisabledSlotsFromNbt(this.previewData.nbt());
			}

			if (!armourItems.isEmpty())
			{
				Container horseInv = new SimpleContainer(armourItems.toArray(new ItemStack[0]));
				InventoryOverlay.renderInventoryBackground(drawContext, type, xInv, yInv, 1, horseInv.getContainerSize(), mc);
				InventoryOverlay.renderInventoryBackgroundSlots(drawContext, type, horseInv, xInv + props.slotOffsetX, yInv + props.slotOffsetY);
				InventoryOverlay.renderInventoryStacks(drawContext, type, horseInv, xInv + props.slotOffsetX, yInv + props.slotOffsetY, 1, 0, horseInv.getContainerSize(), mc, mouseX, mouseY);
				xInv += 32 + 4;
			}

			int color = -1;

			if (this.previewData.be() != null && this.previewData.be().getBlockState().getBlock() instanceof ShulkerBoxBlock sbb)
			{
				color = RenderUtils.setShulkerboxBackgroundTintColor(sbb, this.shulkerBGColors);
			}

			// Inv Display
			if (totalSlots > 0 && this.previewData.inv() != null)
			{
				InventoryOverlay.renderInventoryBackground(drawContext, type, xInv, yInv, props.slotsPerRow, totalSlots, color, mc);

				if (type == InventoryOverlay.InventoryRenderType.BREWING_STAND)
				{
					InventoryOverlay.renderBrewerBackgroundSlots(drawContext, this.previewData.inv(), xInv, yInv);
				}

				InventoryOverlay.renderInventoryStacks(drawContext, type, this.previewData.inv(), xInv + props.slotOffsetX, yInv + props.slotOffsetY, props.slotsPerRow, startSlot, totalSlots, lockedSlots, mc, mouseX, mouseY);
			}

			// EnderItems Display
			if ((this.previewData.type() == InventoryOverlay.InventoryRenderType.PLAYER || type == InventoryOverlay.InventoryRenderType.ENDER_CHEST) &&
				this.previewData.nbt() != null && this.previewData.nbt().contains(NbtKeys.ENDER_ITEMS))
			{
				PlayerEnderChestContainer enderItems = InventoryUtils.getPlayerEnderItemsFromNbt(this.previewData.nbt(), world.registryAccess());

				if (enderItems == null)
				{
					enderItems = new PlayerEnderChestContainer();
				}

				yInv = yCenter + 6;
				InventoryOverlay.renderInventoryBackground(drawContext, InventoryOverlay.InventoryRenderType.GENERIC, xInv, yInv, 9, 27, color, mc);
				InventoryOverlay.renderInventoryStacks(drawContext, InventoryOverlay.InventoryRenderType.GENERIC, enderItems, xInv + props.slotOffsetX, yInv + props.slotOffsetY, 9, 0, 27, mc, mouseX, mouseY);
			}
			// Player Inventory Display
			else if (this.previewData.entity() instanceof Player player)
			{
				yInv = yCenter + 6;
				InventoryOverlay.renderInventoryBackground(drawContext, InventoryOverlay.InventoryRenderType.GENERIC, xInv, yInv, 9, 27, color, mc);
				InventoryOverlay.renderInventoryStacks(drawContext, InventoryOverlay.InventoryRenderType.GENERIC, player.getEnderChestInventory(), xInv + props.slotOffsetX, yInv + props.slotOffsetY, 9, 0, 27, mc, mouseX, mouseY);
			}

			// Villager Trades Display
			if (type == InventoryOverlay.InventoryRenderType.VILLAGER &&
				this.previewData.nbt() != null && this.previewData.nbt().contains(NbtKeys.OFFERS))
			{
				NonNullList<ItemStack> offers = InventoryUtils.getSellingItemsFromNbt(this.previewData.nbt(), world.registryAccess());
				Container tradeOffers = InventoryUtils.getAsInventory(offers);

				if (tradeOffers != null && !tradeOffers.isEmpty())
				{
					int xInvOffset = (xCenter - 55) - (props.width / 2);
					int offerSlotCount = 9;

					yInv = yCenter + 6;

					// Realistically, this should never go above 9; but because Minecraft doesn't have these guard rails, be prepared for it.
					if (offers.size() > 9)
					{
						offerSlotCount = 18;
					}

					color = RenderUtils.setVillagerBackgroundTintColor(NbtEntityUtils.getVillagerDataFromNbt(this.previewData.nbt()), this.villagerBGColors);
					InventoryOverlay.renderInventoryBackground(drawContext, InventoryOverlay.InventoryRenderType.GENERIC, xInvOffset - props.slotOffsetX, yInv, 9, offerSlotCount, color, mc);
					InventoryOverlay.renderInventoryStacks(drawContext, InventoryOverlay.InventoryRenderType.GENERIC, tradeOffers, xInvOffset, yInv + props.slotOffsetY, 9, 0, offerSlotCount, mc, mouseX, mouseY);
				}
			}
			// Villager Trades Display
			else if (this.previewData.entity() instanceof AbstractVillager merchant)
			{
				MerchantOffers trades = ((IMixinMerchantEntity) merchant).malilib_offers();
				NonNullList<ItemStack> offers = trades != null ? InventoryUtils.getSellingItems(trades) : NonNullList.create();
				Container tradeOffers = InventoryUtils.getAsInventory(offers);

				if (tradeOffers != null && !tradeOffers.isEmpty())
				{
					int xInvOffset = (xCenter - 55) - (props.width / 2);
					int offerSlotCount = 9;

					yInv = yCenter + 6;

					// Realistically, this should never go above 9; but because Minecraft doesn't have these guard rails, be prepared for it.
					if (offers.size() > 9)
					{
						offerSlotCount = 18;
					}

					if (merchant instanceof Villager villager)
					{
						color = RenderUtils.setVillagerBackgroundTintColor(villager.getVillagerData(), this.villagerBGColors);
					}

					InventoryOverlay.renderInventoryBackground(drawContext, InventoryOverlay.InventoryRenderType.GENERIC, xInvOffset - props.slotOffsetX, yInv, 9, offerSlotCount, color, mc);
					InventoryOverlay.renderInventoryStacks(drawContext, InventoryOverlay.InventoryRenderType.GENERIC, tradeOffers, xInvOffset, yInv + props.slotOffsetY, 9, 0, offerSlotCount, mc, mouseX, mouseY);
				}
			}

			// Entity Display
			if (this.previewData.entity() != null)
			{
				InventoryOverlay.renderEquipmentOverlayBackground(drawContext, x, y, this.previewData.entity());
				InventoryOverlay.renderEquipmentStacks(drawContext, this.previewData.entity(), x, y, mc, mouseX, mouseY);
			}

			// Refresh
			if (this.ticks % 4 == 0)
			{
				this.previewData = this.previewData.handler().onContextRefresh(this.previewData, world);
			}
		}
	}

	private void renderData(GuiGraphics drawContext, int mouseX, int mouseY, float delta)
	{
		this.ticks++;
		Minecraft mc = Minecraft.getInstance();
		Level world = WorldUtils.getBestWorld(mc);

		if (this.previewDataNew != null && world != null)
		{
			final int xCenter = GuiUtils.getScaledWindowWidth() / 2;
			final int yCenter = GuiUtils.getScaledWindowHeight() / 2;
			int x = xCenter - 52 / 2;
			int y = yCenter - 92;

			int startSlot = 0;
			int totalSlots = this.previewDataNew.inv() == null ? 0 : this.previewDataNew.inv().getContainerSize();
			List<ItemStack> armourItems = new ArrayList<>();

			if (this.previewDataNew.entity() instanceof AbstractHorse)
			{
				if (this.previewDataNew.inv() == null)
				{
					MaLiLib.LOGGER.warn("renderData(): Horse inv() = null");
					return;
				}
				armourItems.add(this.previewDataNew.entity().getItemBySlot(EquipmentSlot.BODY));
				armourItems.add(this.previewDataNew.inv().getItem(0));
				startSlot = 1;
				totalSlots = this.previewDataNew.inv().getContainerSize() - 1;
			}
			else if (this.previewDataNew.entity() instanceof Wolf || this.previewDataNew.entity() instanceof HappyGhast)
			{
				armourItems.add(this.previewDataNew.entity().getItemBySlot(EquipmentSlot.BODY));
				//armourItems.add(ItemStack.EMPTY);
			}
			else if (this.previewDataNew.entity() instanceof CopperGolem)
			{
				armourItems.add(this.previewDataNew.entity().getItemBySlot(EquipmentSlot.SADDLE));
			}

			final InventoryOverlayType type = (this.previewDataNew.entity() instanceof Villager) ? InventoryOverlayType.VILLAGER : InventoryOverlay.getBestInventoryTypeNew(this.previewDataNew.inv(), this.previewDataNew.data() != null ? this.previewDataNew.data() : new CompoundData(), this.previewDataNew);
			final InventoryOverlay.InventoryProperties props = InventoryOverlay.getInventoryPropsTempNew(type, totalSlots);
			final int rows = (int) Math.ceil((double) totalSlots / props.slotsPerRow);
			Set<Integer> lockedSlots = new HashSet<>();
			int xInv = xCenter - (props.width / 2);
			int yInv = yCenter - props.height - 6;

			if (rows > 6)
			{
				yInv -= (rows - 6) * 18;
				y -= (rows - 6) * 18;
			}

			if (MaLiLibReference.DEBUG_MODE)
			{
				MaLiLib.LOGGER.warn("renderData():0: type [{}], previewData.type [{}], previewData.inv [{}], previewData.be [{}], previewData.ent [{}], previewData.data [{}]", type.toString(), this.previewDataNew.type().toString(),
				                    this.previewDataNew.inv() != null, this.previewDataNew.be() != null, this.previewDataNew.entity() != null, this.previewDataNew.data() != null ? this.previewDataNew.data().getString("id") : null);
				MaLiLib.LOGGER.error("0: -> inv.type [{}] // data.type [{}]", this.previewDataNew.inv() != null ? InventoryOverlay.getInventoryTypeNew(this.previewDataNew.inv()) : null, this.previewDataNew.data() != null ? InventoryOverlay.getInventoryTypeNew(this.previewDataNew.data()) : null);
				MaLiLib.LOGGER.error("1: -> inv.size [{}] // inv.isEmpty [{}]", this.previewDataNew.inv() != null ? this.previewDataNew.inv().getContainerSize() : -1, this.previewDataNew.inv() != null ? this.previewDataNew.inv().isEmpty() : -1);
				MaLiLib.LOGGER.error("2: -> total slots [{}] // rows [{}] // startSlot [{}]", totalSlots, rows, startSlot);
			}

			if (this.previewDataNew.entity() != null)
			{
				x = xCenter - 55;
				xInv = xCenter + 2;
				yInv = Math.min(yInv, yCenter - 92);
			}
			if (this.previewDataNew.be() instanceof CrafterBlockEntity cbe)
			{
				lockedSlots = BlockUtils.getDisabledSlots(cbe);
			}
			else if (this.previewDataNew.data() != null && this.previewDataNew.data().contains(NbtKeys.DISABLED_SLOTS, Constants.NBT.TAG_INT_ARRAY))
			{
				lockedSlots = DataBlockUtils.getDisabledSlots(this.previewDataNew.data());
			}

			if (!armourItems.isEmpty())
			{
				Container horseInv = new SimpleContainer(armourItems.toArray(new ItemStack[0]));
				InventoryOverlay.renderInventoryBackgroundNew(drawContext, type, xInv, yInv, 1, horseInv.getContainerSize(), mc);
				InventoryOverlay.renderInventoryBackgroundSlotsNew(drawContext, type, horseInv, xInv + props.slotOffsetX, yInv + props.slotOffsetY);
				InventoryOverlay.renderInventoryStacksNew(drawContext, type, horseInv, xInv + props.slotOffsetX, yInv + props.slotOffsetY, 1, 0, horseInv.getContainerSize(), mc, mouseX, mouseY);
				xInv += 32 + 4;
			}

			int color = -1;

			if (this.previewDataNew.be() != null && this.previewDataNew.be().getBlockState().getBlock() instanceof ShulkerBoxBlock sbb)
			{
				color = RenderUtils.setShulkerboxBackgroundTintColor(sbb, this.shulkerBGColors);
			}

			// Inv Display
			if (totalSlots > 0 && this.previewDataNew.inv() != null)
			{
				InventoryOverlay.renderInventoryBackgroundNew(drawContext, type, xInv, yInv, props.slotsPerRow, totalSlots, color, mc);

				if (type == InventoryOverlayType.BREWING_STAND)
				{
					InventoryOverlay.renderBrewerBackgroundSlots(drawContext, this.previewDataNew.inv(), xInv, yInv);
				}

				InventoryOverlay.renderInventoryStacksNew(drawContext, type, this.previewDataNew.inv(), xInv + props.slotOffsetX, yInv + props.slotOffsetY, props.slotsPerRow, startSlot, totalSlots, lockedSlots, mc, mouseX, mouseY);
			}

			// EnderItems Display
			if ((this.previewDataNew.type() == InventoryOverlayType.PLAYER || type == InventoryOverlayType.ENDER_CHEST) &&
				this.previewDataNew.data() != null && this.previewDataNew.data().contains(NbtKeys.ENDER_ITEMS, Constants.NBT.TAG_LIST))
			{
				PlayerEnderChestContainer enderItems = InventoryUtils.getPlayerEnderItemsFromData(this.previewDataNew.data(), world.registryAccess());

				if (enderItems == null)
				{
					enderItems = new PlayerEnderChestContainer();
				}

				MaLiLib.LOGGER.error("renderData(): enderItems [{}]", enderItems.getContainerSize());

				yInv = yCenter + 6;
				InventoryOverlay.renderInventoryBackgroundNew(drawContext, InventoryOverlayType.GENERIC, xInv, yInv, 9, 27, color, mc);
				InventoryOverlay.renderInventoryStacksNew(drawContext, InventoryOverlayType.GENERIC, enderItems, xInv + props.slotOffsetX, yInv + props.slotOffsetY, 9, 0, 27, mc, mouseX, mouseY);
			}
			// Player Inventory Display
			else if (this.previewDataNew.entity() instanceof Player player)
			{
				yInv = yCenter + 6;
				InventoryOverlay.renderInventoryBackgroundNew(drawContext, InventoryOverlayType.GENERIC, xInv, yInv, 9, 27, color, mc);
				InventoryOverlay.renderInventoryStacksNew(drawContext, InventoryOverlayType.GENERIC, player.getEnderChestInventory(), xInv + props.slotOffsetX, yInv + props.slotOffsetY, 9, 0, 27, mc, mouseX, mouseY);
			}

			// Villager Trades Display
			if (type == InventoryOverlayType.VILLAGER &&
				this.previewDataNew.data() != null && this.previewDataNew.data().contains(NbtKeys.OFFERS, Constants.NBT.TAG_LIST))
			{
				NonNullList<ItemStack> offers = InventoryUtils.getSellingItemsFromData(this.previewDataNew.data(), world.registryAccess());
				Container tradeOffers = InventoryUtils.getAsInventory(offers);

				if (tradeOffers != null && !tradeOffers.isEmpty())
				{
					int xInvOffset = (xCenter - 55) - (props.width / 2);
					int offerSlotCount = 9;

					yInv = yCenter + 6;

					// Realistically, this should never go above 9; but because Minecraft doesn't have these guard rails, be prepared for it.
					if (offers.size() > 9)
					{
						offerSlotCount = 18;
					}

					color = RenderUtils.setVillagerBackgroundTintColor(DataEntityUtils.getVillagerData(this.previewDataNew.data()), this.villagerBGColors);
					InventoryOverlay.renderInventoryBackgroundNew(drawContext, InventoryOverlayType.GENERIC, xInvOffset - props.slotOffsetX, yInv, 9, offerSlotCount, color, mc);
					InventoryOverlay.renderInventoryStacksNew(drawContext, InventoryOverlayType.GENERIC, tradeOffers, xInvOffset, yInv + props.slotOffsetY, 9, 0, offerSlotCount, mc, mouseX, mouseY);
				}
			}
			// Villager Trades Display
			else if (this.previewDataNew.entity() instanceof AbstractVillager merchant)
			{
				MerchantOffers trades = ((IMixinMerchantEntity) merchant).malilib_offers();
				NonNullList<ItemStack> offers = trades != null ? InventoryUtils.getSellingItems(trades) : NonNullList.create();
				Container tradeOffers = InventoryUtils.getAsInventory(offers);

				if (tradeOffers != null && !tradeOffers.isEmpty())
				{
					int xInvOffset = (xCenter - 55) - (props.width / 2);
					int offerSlotCount = 9;

					yInv = yCenter + 6;

					// Realistically, this should never go above 9; but because Minecraft doesn't have these guard rails, be prepared for it.
					if (offers.size() > 9)
					{
						offerSlotCount = 18;
					}

					if (merchant instanceof Villager villager)
					{
						color = RenderUtils.setVillagerBackgroundTintColor(villager.getVillagerData(), this.villagerBGColors);
					}

					InventoryOverlay.renderInventoryBackgroundNew(drawContext, InventoryOverlayType.GENERIC, xInvOffset - props.slotOffsetX, yInv, 9, offerSlotCount, color, mc);
					InventoryOverlay.renderInventoryStacksNew(drawContext, InventoryOverlayType.GENERIC, tradeOffers, xInvOffset, yInv + props.slotOffsetY, 9, 0, offerSlotCount, mc, mouseX, mouseY);
				}
			}

			// Entity Display
			if (this.previewDataNew.entity() != null)
			{
				InventoryOverlay.renderEquipmentOverlayBackground(drawContext, x, y, this.previewDataNew.entity());
				InventoryOverlay.renderEquipmentStacks(drawContext, this.previewDataNew.entity(), x, y, mc, mouseX, mouseY);
			}

			// Refresh
			if (this.ticks % 4 == 0)
			{
				this.previewDataNew = this.previewDataNew.refresher().onContextRefresh(this.previewDataNew, world);
			}
		}
	}

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }
}
