package fi.dy.masa.malilib.render;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.CrafterBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.CopperGolemEntity;
import net.minecraft.entity.passive.HappyGhastEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.World;
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

public class InventoryOverlayScreen extends Screen implements Drawable
{
    String modId;
	private InventoryOverlayContext previewDataNew;
    private final boolean shulkerBGColors;
    private final boolean villagerBGColors;
    private int ticks;

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
		this.previewDataNew = previewData;
		this.shulkerBGColors = shulkerBGColors;
		this.villagerBGColors = villagerBGColors;
	}

	@Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks)
    {
        // NO BLUR / MASKING
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta)
    {
		if (this.previewDataNew != null)
	    {
		    this.renderData(GuiContext.fromGuiGraphics(drawContext), mouseX, mouseY, delta);
	    }
    }

	private void renderData(GuiContext ctx, int mouseX, int mouseY, float delta)
	{
		this.ticks++;
		MinecraftClient mc = MinecraftClient.getInstance();
		World world = WorldUtils.getBestWorld(mc);

		if (this.previewDataNew != null && world != null)
		{
			final int xCenter = GuiUtils.getScaledWindowWidth() / 2;
			final int yCenter = GuiUtils.getScaledWindowHeight() / 2;
			int x = xCenter - 52 / 2;
			int y = yCenter - 92;

			int startSlot = 0;
			int totalSlots = this.previewDataNew.inv() == null ? 0 : this.previewDataNew.inv().size();
			List<ItemStack> armourItems = new ArrayList<>();

			if (this.previewDataNew.entity() instanceof AbstractHorseEntity)
			{
				if (this.previewDataNew.inv() == null)
				{
					MaLiLib.LOGGER.warn("renderData(): Horse inv() = null");
					return;
				}
				armourItems.add(this.previewDataNew.entity().getEquippedStack(EquipmentSlot.BODY));
				armourItems.add(this.previewDataNew.inv().getStack(0));
				startSlot = 1;
				totalSlots = this.previewDataNew.inv().size() - 1;
			}
			else if (this.previewDataNew.entity() instanceof WolfEntity || this.previewDataNew.entity() instanceof HappyGhastEntity)
			{
				armourItems.add(this.previewDataNew.entity().getEquippedStack(EquipmentSlot.BODY));
				//armourItems.add(ItemStack.EMPTY);
			}
			else if (this.previewDataNew.entity() instanceof CopperGolemEntity)
			{
				armourItems.add(this.previewDataNew.entity().getEquippedStack(EquipmentSlot.SADDLE));
			}

			final InventoryOverlayType type = (this.previewDataNew.entity() instanceof VillagerEntity)
			                                  ? InventoryOverlayType.VILLAGER
			                                  : InventoryOverlay.getBestInventoryType(this.previewDataNew, this.previewDataNew.inv(), this.previewDataNew.data() != null ? this.previewDataNew.data() : new CompoundData());
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
				MaLiLib.LOGGER.warn("renderData():0: type [{}], previewData.type [{}], previewData.inv [{}], previewData.be [{}], previewData.ent [{}], previewData.data [{}]", type.toString(), this.previewDataNew.type().toString(),
				                    this.previewDataNew.inv() != null, this.previewDataNew.be() != null, this.previewDataNew.entity() != null, this.previewDataNew.data() != null ? this.previewDataNew.data().getString("id") : null);
				MaLiLib.LOGGER.error("0: -> inv.type [{}] // data.type [{}]", this.previewDataNew.inv() != null ? InventoryOverlay.getInventoryType(this.previewDataNew.inv()) : null, this.previewDataNew.data() != null ? InventoryOverlay.getInventoryType(this.previewDataNew.data()) : null);
				MaLiLib.LOGGER.error("1: -> inv.size [{}] // inv.isEmpty [{}]", this.previewDataNew.inv() != null ? this.previewDataNew.inv().size() : -1, this.previewDataNew.inv() != null ? this.previewDataNew.inv().isEmpty() : -1);
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
				Inventory horseInv = new SimpleInventory(armourItems.toArray(new ItemStack[0]));
				InventoryOverlay.renderInventoryBackground(ctx, type, xInv, yInv, 1, horseInv.size());
				InventoryOverlay.renderInventoryBackgroundSlots(ctx, type, horseInv, xInv + props.slotOffsetX, yInv + props.slotOffsetY);
				InventoryOverlay.renderInventoryStacks(ctx, type, horseInv, xInv + props.slotOffsetX, yInv + props.slotOffsetY, 1, 0, horseInv.size(), mouseX, mouseY);
				xInv += 32 + 4;
			}

			int color = -1;

			if (this.previewDataNew.be() != null && this.previewDataNew.be().getCachedState().getBlock() instanceof ShulkerBoxBlock sbb)
			{
				color = RenderUtils.setShulkerboxBackgroundTintColor(sbb, this.shulkerBGColors);
			}

			// Inv Display
			if (totalSlots > 0 && this.previewDataNew.inv() != null)
			{
				InventoryOverlay.renderInventoryBackground(ctx, type, xInv, yInv, props.slotsPerRow, totalSlots, color);

				if (type == InventoryOverlayType.BREWING_STAND)
				{
					InventoryOverlay.renderBrewerBackgroundSlots(ctx, this.previewDataNew.inv(), xInv, yInv);
				}

				InventoryOverlay.renderInventoryStacks(ctx, type, this.previewDataNew.inv(), xInv + props.slotOffsetX, yInv + props.slotOffsetY, props.slotsPerRow, startSlot, totalSlots, lockedSlots, mouseX, mouseY);
			}

			// EnderItems Display
			if ((this.previewDataNew.type() == InventoryOverlayType.PLAYER || type == InventoryOverlayType.ENDER_CHEST) &&
				this.previewDataNew.data() != null && this.previewDataNew.data().contains(NbtKeys.ENDER_ITEMS, Constants.NBT.TAG_LIST))
			{
				EnderChestInventory enderItems = InventoryUtils.getPlayerEnderItemsFromData(this.previewDataNew.data(), world.getRegistryManager());

				if (enderItems == null)
				{
					enderItems = new EnderChestInventory();
				}

				if (MaLiLibReference.DEBUG_MODE)
				{
					MaLiLib.LOGGER.error("renderData(): enderItems [{}]", enderItems.size());
				}

				yInv = yCenter + 6;
				InventoryOverlay.renderInventoryBackground(ctx, InventoryOverlayType.GENERIC, xInv, yInv, 9, 27, color);
				InventoryOverlay.renderInventoryStacks(ctx, InventoryOverlayType.GENERIC, enderItems, xInv + props.slotOffsetX, yInv + props.slotOffsetY, 9, 0, 27, mouseX, mouseY);
			}
			// Player Inventory Display
			else if (this.previewDataNew.entity() instanceof PlayerEntity player)
			{
				yInv = yCenter + 6;
				InventoryOverlay.renderInventoryBackground(ctx, InventoryOverlayType.GENERIC, xInv, yInv, 9, 27, color);
				InventoryOverlay.renderInventoryStacks(ctx, InventoryOverlayType.GENERIC, player.getEnderChestInventory(), xInv + props.slotOffsetX, yInv + props.slotOffsetY, 9, 0, 27, mouseX, mouseY);
			}

			// Villager Trades Display
			if (type == InventoryOverlayType.VILLAGER &&
				this.previewDataNew.data() != null && this.previewDataNew.data().contains(NbtKeys.OFFERS, Constants.NBT.TAG_LIST))
			{
				DefaultedList<ItemStack> offers = InventoryUtils.getSellingItemsFromData(this.previewDataNew.data(), world.getRegistryManager());
				Inventory tradeOffers = InventoryUtils.getAsInventory(offers);

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
					InventoryOverlay.renderInventoryBackground(ctx, InventoryOverlayType.GENERIC, xInvOffset - props.slotOffsetX, yInv, 9, offerSlotCount, color);
					InventoryOverlay.renderInventoryStacks(ctx, InventoryOverlayType.GENERIC, tradeOffers, xInvOffset, yInv + props.slotOffsetY, 9, 0, offerSlotCount, mouseX, mouseY);
				}
			}
			// Villager Trades Display
			else if (this.previewDataNew.entity() instanceof MerchantEntity merchant)
			{
				TradeOfferList trades = ((IMixinMerchantEntity) merchant).malilib_offers();
				DefaultedList<ItemStack> offers = trades != null ? InventoryUtils.getSellingItems(trades) : DefaultedList.of();
				Inventory tradeOffers = InventoryUtils.getAsInventory(offers);

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

					if (merchant instanceof VillagerEntity villager)
					{
						color = RenderUtils.setVillagerBackgroundTintColor(villager.getVillagerData(), this.villagerBGColors);
					}

					InventoryOverlay.renderInventoryBackground(ctx, InventoryOverlayType.GENERIC, xInvOffset - props.slotOffsetX, yInv, 9, offerSlotCount, color);
					InventoryOverlay.renderInventoryStacks(ctx, InventoryOverlayType.GENERIC, tradeOffers, xInvOffset, yInv + props.slotOffsetY, 9, 0, offerSlotCount, mouseX, mouseY);
				}
			}

			// Entity Display
			if (this.previewDataNew.entity() != null)
			{
				InventoryOverlay.renderEquipmentOverlayBackground(ctx, x, y, this.previewDataNew.entity());
				InventoryOverlay.renderEquipmentStacks(ctx, this.previewDataNew.entity(), x, y, mouseX, mouseY);
			}

			// Refresh
			if (this.ticks % 4 == 0)
			{
				this.previewDataNew = this.previewDataNew.refresher().onContextRefresh(this.previewDataNew, world);
			}
		}
	}

    @Override
    public boolean shouldPause()
    {
        return false;
    }
}
