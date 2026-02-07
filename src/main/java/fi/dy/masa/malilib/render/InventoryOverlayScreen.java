package fi.dy.masa.malilib.render;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.golem.CopperGolem;
import net.minecraft.world.entity.animal.happyghast.HappyGhast;
import net.minecraft.world.entity.animal.nautilus.AbstractNautilus;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.Villager;
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
import fi.dy.masa.malilib.util.nbt.NbtKeys;

public class InventoryOverlayScreen extends Screen implements Renderable
{
    String modId;
	private InventoryOverlayContext previewData;
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
		this.previewData = previewData;
		this.shulkerBGColors = shulkerBGColors;
		this.villagerBGColors = villagerBGColors;
	}

	@Override
    public void renderBackground(@NotNull GuiGraphics context, int mouseX, int mouseY, float deltaTicks)
    {
        // NO BLUR / MASKING
    }

    @Override
    public void render(@NotNull GuiGraphics drawContext, int mouseX, int mouseY, float delta)
    {
		if (this.previewData != null)
	    {
		    this.renderData(GuiContext.fromGuiGraphics(drawContext), mouseX, mouseY, delta);
	    }
    }

	private void renderData(GuiContext ctx, int mouseX, int mouseY, float delta)
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
					MaLiLib.LOGGER.warn("renderData(): Horse inv() = null");
					return;
				}
				armourItems.add(this.previewData.entity().getItemBySlot(EquipmentSlot.BODY));
				armourItems.add(this.previewData.inv().getItem(0));
				startSlot = 1;
				totalSlots = this.previewData.inv().getContainerSize() - 1;
			}
			else if (this.previewData.entity() instanceof AbstractNautilus)
			{
				armourItems.add(this.previewData.entity().getItemBySlot(EquipmentSlot.BODY));
				armourItems.add(this.previewData.entity().getItemBySlot(EquipmentSlot.SADDLE));
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

			final InventoryOverlayType type = (this.previewData.entity() instanceof Villager)
			                                  ? InventoryOverlayType.VILLAGER
			                                  : InventoryOverlay.getBestInventoryType(this.previewData, this.previewData.inv(), this.previewData.data() != null ? this.previewData.data() : new CompoundData());
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
				MaLiLib.LOGGER.warn("renderData():0: type [{}], previewData.type [{}], previewData.inv [{}], previewData.be [{}], previewData.ent [{}], previewData.data [{}]", type.toString(), this.previewData.type().toString(),
				                    this.previewData.inv() != null, this.previewData.be() != null, this.previewData.entity() != null, this.previewData.data() != null ? this.previewData.data().getString("id") : null);
				MaLiLib.LOGGER.error("0: -> inv.type [{}] // data.type [{}]", this.previewData.inv() != null ? InventoryOverlay.getInventoryType(this.previewData.inv()) : null, this.previewData.data() != null ? InventoryOverlay.getInventoryType(this.previewData.data()) : null);
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
			else if (this.previewData.data() != null && this.previewData.data().contains(NbtKeys.DISABLED_SLOTS, Constants.NBT.TAG_INT_ARRAY))
			{
				lockedSlots = DataBlockUtils.getDisabledSlots(this.previewData.data());
			}

			if (!armourItems.isEmpty())
			{
				Container horseInv = new SimpleContainer(armourItems.toArray(new ItemStack[0]));
				InventoryOverlay.renderInventoryBackground(ctx, type, xInv, yInv, 1, horseInv.getContainerSize());
				InventoryOverlay.renderInventoryBackgroundSlots(ctx, type, horseInv, xInv + props.slotOffsetX, yInv + props.slotOffsetY);
				InventoryOverlay.renderInventoryStacks(ctx, type, horseInv, xInv + props.slotOffsetX, yInv + props.slotOffsetY, 1, 0, horseInv.getContainerSize(), mouseX, mouseY);
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
				InventoryOverlay.renderInventoryBackground(ctx, type, xInv, yInv, props.slotsPerRow, totalSlots, color);

				if (type == InventoryOverlayType.BREWING_STAND)
				{
					InventoryOverlay.renderBrewerBackgroundSlots(ctx, this.previewData.inv(), xInv, yInv);
				}

				InventoryOverlay.renderInventoryStacks(ctx, type, this.previewData.inv(), xInv + props.slotOffsetX, yInv + props.slotOffsetY, props.slotsPerRow, startSlot, totalSlots, lockedSlots, mouseX, mouseY);
			}

			// EnderItems Display
			if ((this.previewData.type() == InventoryOverlayType.PLAYER || type == InventoryOverlayType.ENDER_CHEST) &&
				this.previewData.data() != null && this.previewData.data().contains(NbtKeys.ENDER_ITEMS, Constants.NBT.TAG_LIST))
			{
				PlayerEnderChestContainer enderItems = InventoryUtils.getPlayerEnderItemsFromData(this.previewData.data(), world.registryAccess());

				if (enderItems == null)
				{
					enderItems = new PlayerEnderChestContainer();
				}

				if (MaLiLibReference.DEBUG_MODE)
				{
					MaLiLib.LOGGER.error("renderData(): enderItems [{}]", enderItems.getContainerSize());
				}

				yInv = yCenter + 6;
				InventoryOverlay.renderInventoryBackground(ctx, InventoryOverlayType.GENERIC, xInv, yInv, 9, 27, color);
				InventoryOverlay.renderInventoryStacks(ctx, InventoryOverlayType.GENERIC, enderItems, xInv + props.slotOffsetX, yInv + props.slotOffsetY, 9, 0, 27, mouseX, mouseY);
			}
			// Player Inventory Display
			else if (this.previewData.entity() instanceof Player player)
			{
				yInv = yCenter + 6;
				InventoryOverlay.renderInventoryBackground(ctx, InventoryOverlayType.GENERIC, xInv, yInv, 9, 27, color);
				InventoryOverlay.renderInventoryStacks(ctx, InventoryOverlayType.GENERIC, player.getEnderChestInventory(), xInv + props.slotOffsetX, yInv + props.slotOffsetY, 9, 0, 27, mouseX, mouseY);
			}

			// Villager Trades Display
			if (type == InventoryOverlayType.VILLAGER &&
				this.previewData.data() != null && this.previewData.data().contains(NbtKeys.OFFERS, Constants.NBT.TAG_COMPOUND))
			{
				NonNullList<@NotNull ItemStack> offers = InventoryUtils.getSellingItemsFromData(this.previewData.data(), world.registryAccess());
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

					color = RenderUtils.setVillagerBackgroundTintColor(DataEntityUtils.getVillagerData(this.previewData.data()), this.villagerBGColors);
					InventoryOverlay.renderInventoryBackground(ctx, InventoryOverlayType.GENERIC, xInvOffset - props.slotOffsetX, yInv, 9, offerSlotCount, color);
					InventoryOverlay.renderInventoryStacks(ctx, InventoryOverlayType.GENERIC, tradeOffers, xInvOffset, yInv + props.slotOffsetY, 9, 0, offerSlotCount, mouseX, mouseY);
				}
			}
			// Villager Trades Display
			else if (this.previewData.entity() instanceof AbstractVillager merchant)
			{
				MerchantOffers trades = ((IMixinMerchantEntity) merchant).malilib_offers();
				NonNullList<@NotNull ItemStack> offers = trades != null ? InventoryUtils.getSellingItems(trades) : NonNullList.create();
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

					InventoryOverlay.renderInventoryBackground(ctx, InventoryOverlayType.GENERIC, xInvOffset - props.slotOffsetX, yInv, 9, offerSlotCount, color);
					InventoryOverlay.renderInventoryStacks(ctx, InventoryOverlayType.GENERIC, tradeOffers, xInvOffset, yInv + props.slotOffsetY, 9, 0, offerSlotCount, mouseX, mouseY);
				}
			}

			// Entity Display
			if (this.previewData.entity() != null)
			{
				InventoryOverlay.renderEquipmentOverlayBackground(ctx, x, y, this.previewData.entity());
				InventoryOverlay.renderEquipmentStacks(ctx, this.previewData.entity(), x, y, mouseX, mouseY);
			}

			// Refresh
			if (this.ticks % 4 == 0)
			{
				this.previewData = this.previewData.refresher().onContextRefresh(this.previewData, world);
			}
		}
	}

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }
}
