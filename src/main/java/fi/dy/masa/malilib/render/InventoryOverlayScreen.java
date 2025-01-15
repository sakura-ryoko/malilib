package fi.dy.masa.malilib.render;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.CrafterBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.World;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.mixin.IMixinMerchantEntity;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.InventoryUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.WorldUtils;
import fi.dy.masa.malilib.util.game.BlockUtils;
import fi.dy.masa.malilib.util.nbt.NbtBlockUtils;
import fi.dy.masa.malilib.util.nbt.NbtEntityUtils;
import fi.dy.masa.malilib.util.nbt.NbtKeys;

// todo - using GuiBase works; but it adds some lag / delay to the screen opening.
public class InventoryOverlayScreen extends Screen implements Drawable
//public class InventoryOverlayScreen extends GuiBase implements Drawable
{
    String modId;
    private InventoryOverlay.Context previewData;
    private final boolean shulkerBGColors;
    private final boolean villagerBGColors;
    private int ticks;


    public InventoryOverlayScreen(String modId, @Nullable InventoryOverlay.Context previewData)
    {
        this(modId, previewData, true, false);
    }

    public InventoryOverlayScreen(String modId, @Nullable InventoryOverlay.Context previewData, boolean shulkerBGColors)
    {
        this(modId, previewData, shulkerBGColors, false);
    }

    public InventoryOverlayScreen(String modId, @Nullable InventoryOverlay.Context previewData, boolean shulkerBGColors, boolean villagerBGColors)
    {
        super(StringUtils.translateAsText(MaLiLibReference.MOD_ID + ".gui.title.inventory_overlay", modId));
        //this.setTitle(StringUtils.translate(MaLiLibReference.MOD_ID + ".gui.title.inventory_overlay", modId));
        //this.setParent(mc.currentScreen);
        this.modId = modId;
        this.previewData = previewData;
        this.shulkerBGColors = shulkerBGColors;
        this.villagerBGColors = villagerBGColors;
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta)
    {
        ticks++;
        MinecraftClient mc = MinecraftClient.getInstance();
        World world = WorldUtils.getBestWorld(mc);

        if (previewData != null && world != null)
        {
            final int xCenter = GuiUtils.getScaledWindowWidth() / 2;
            final int yCenter = GuiUtils.getScaledWindowHeight() / 2;
            int x = xCenter - 52 / 2;
            int y = yCenter - 92;

            int startSlot = 0;
            int totalSlots = previewData.inv() == null ? 0 : previewData.inv().size();
            List<ItemStack> armourItems = new ArrayList<>();
            if (previewData.entity() instanceof AbstractHorseEntity)
            {
                if (previewData.inv() == null)
                {
                    MaLiLib.LOGGER.warn("InventoryOverlayScreen(): Horse inv() = null");
                    return;
                }
                armourItems.add(previewData.entity().getEquippedStack(EquipmentSlot.BODY));
                armourItems.add(previewData.inv().getStack(0));
                startSlot = 1;
                totalSlots = previewData.inv().size() - 1;
            }
            else if (previewData.entity() instanceof WolfEntity)
            {
                armourItems.add(previewData.entity().getEquippedStack(EquipmentSlot.BODY));
                //armourItems.add(ItemStack.EMPTY);
            }

            final InventoryOverlay.InventoryRenderType type = (previewData.entity() instanceof VillagerEntity) ? InventoryOverlay.InventoryRenderType.VILLAGER : InventoryOverlay.getBestInventoryType(previewData.inv(), previewData.nbt() != null ? previewData.nbt() : new NbtCompound(), previewData);
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
                MaLiLib.LOGGER.warn("render():0: type [{}], previewData.type [{}], previewData.inv [{}], previewData.be [{}], previewData.ent [{}], previewData.nbt [{}]", type.toString(), previewData.type().toString(),
                                    previewData.inv() != null, previewData.be() != null, previewData.entity() != null, previewData.nbt() != null ? previewData.nbt().getString("id") : null);
                MaLiLib.LOGGER.error("0: -> inv.type [{}] // nbt.type [{}]", previewData.inv() != null ? InventoryOverlay.getInventoryType(previewData.inv()) : null, previewData.nbt() != null ? InventoryOverlay.getInventoryType(previewData.nbt()) : null);
                MaLiLib.LOGGER.error("1: -> inv.size [{}] // inv.isEmpty [{}]", previewData.inv() != null ? previewData.inv().size() : -1, previewData.inv() != null ? previewData.inv().isEmpty() : -1);
            }

            if (previewData.entity() != null)
            {
                x = xCenter - 55;
                xInv = xCenter + 2;
                yInv = Math.min(yInv, yCenter - 92);
            }
            if (previewData.be() instanceof CrafterBlockEntity cbe)
            {
                lockedSlots = BlockUtils.getDisabledSlots(cbe);
            }
            else if (previewData.nbt() != null && previewData.nbt().contains(NbtKeys.DISABLED_SLOTS))
            {
                lockedSlots = NbtBlockUtils.getDisabledSlotsFromNbt(previewData.nbt());
            }

            if (!armourItems.isEmpty())
            {
                Inventory horseInv = new SimpleInventory(armourItems.toArray(new ItemStack[0]));
                InventoryOverlay.renderInventoryBackground(type, xInv, yInv, 1, horseInv.size(), mc);
                InventoryOverlay.renderInventoryBackgroundSlots(type, horseInv, xInv + props.slotOffsetX, yInv + props.slotOffsetY, drawContext);
                InventoryOverlay.renderInventoryStacks(type, horseInv, xInv + props.slotOffsetX, yInv + props.slotOffsetY, 1, 0, horseInv.size(), mc, drawContext, mouseX, mouseY);
                xInv += 32 + 4;
            }

            if (previewData.be() != null && previewData.be().getCachedState().getBlock() instanceof ShulkerBoxBlock sbb)
            {
                RenderUtils.setShulkerboxBackgroundTintColor(sbb, this.shulkerBGColors);
            }

            // Inv Display
            if (totalSlots > 0 && previewData.inv() != null)
            {
                InventoryOverlay.renderInventoryBackground(type, xInv, yInv, props.slotsPerRow, totalSlots, mc);
                // TODO 1.21.4+
                if (type == InventoryOverlay.InventoryRenderType.BREWING_STAND)
                {
                    InventoryOverlay.renderBrewerBackgroundSlots(previewData.inv(), xInv, yInv, drawContext);
                }
                InventoryOverlay.renderInventoryStacks(type, previewData.inv(), xInv + props.slotOffsetX, yInv + props.slotOffsetY, props.slotsPerRow, startSlot, totalSlots, lockedSlots, mc, drawContext, mouseX, mouseY);
            }

            // EnderItems Display
            if (previewData.type() == InventoryOverlay.InventoryRenderType.PLAYER &&
                previewData.nbt() != null && previewData.nbt().contains(NbtKeys.ENDER_ITEMS))
            {
                EnderChestInventory enderItems = InventoryUtils.getPlayerEnderItemsFromNbt(previewData.nbt(), world.getRegistryManager());

                if (enderItems == null)
                {
                    enderItems = new EnderChestInventory();
                }

                yInv = yCenter + 6;
                InventoryOverlay.renderInventoryBackground(InventoryOverlay.InventoryRenderType.GENERIC, xInv, yInv, 9, 27, mc);
                InventoryOverlay.renderInventoryStacks(InventoryOverlay.InventoryRenderType.GENERIC, enderItems, xInv + props.slotOffsetX, yInv + props.slotOffsetY, 9, 0, 27, mc, drawContext, mouseX, mouseY);
            }
            else if (previewData.entity() instanceof PlayerEntity player)
            {
                yInv = yCenter + 6;
                InventoryOverlay.renderInventoryBackground(InventoryOverlay.InventoryRenderType.GENERIC, xInv, yInv, 9, 27, mc);
                InventoryOverlay.renderInventoryStacks(InventoryOverlay.InventoryRenderType.GENERIC, player.getEnderChestInventory(), xInv + props.slotOffsetX, yInv + props.slotOffsetY, 9, 0, 27, mc, drawContext, mouseX, mouseY);
            }

            // Villager Trades Display
            if (type == InventoryOverlay.InventoryRenderType.VILLAGER &&
                previewData.nbt() != null && previewData.nbt().contains(NbtKeys.OFFERS))
            {
                DefaultedList<ItemStack> offers = InventoryUtils.getSellingItemsFromNbt(previewData.nbt(), world.getRegistryManager());
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

                    RenderUtils.setVillagerBackgroundTintColor(NbtEntityUtils.getVillagerDataFromNbt(previewData.nbt()), this.villagerBGColors);
                    InventoryOverlay.renderInventoryBackground(InventoryOverlay.InventoryRenderType.GENERIC, xInvOffset - props.slotOffsetX, yInv, 9, offerSlotCount, mc);
                    InventoryOverlay.renderInventoryStacks(InventoryOverlay.InventoryRenderType.GENERIC, tradeOffers, xInvOffset, yInv + props.slotOffsetY, 9, 0, offerSlotCount, mc, drawContext, mouseX, mouseY);
                }
            }
            else if (previewData.entity() instanceof MerchantEntity merchant)
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
                        RenderUtils.setVillagerBackgroundTintColor(villager.getVillagerData(), this.villagerBGColors);
                    }
                    InventoryOverlay.renderInventoryBackground(InventoryOverlay.InventoryRenderType.GENERIC, xInvOffset - props.slotOffsetX, yInv, 9, offerSlotCount, mc);
                    InventoryOverlay.renderInventoryStacks(InventoryOverlay.InventoryRenderType.GENERIC, tradeOffers, xInvOffset, yInv + props.slotOffsetY, 9, 0, offerSlotCount, mc, drawContext, mouseX, mouseY);
                }
            }

            // Entity Display
            if (previewData.entity() != null)
            {
                InventoryOverlay.renderEquipmentOverlayBackground(x, y, previewData.entity(), drawContext);
                InventoryOverlay.renderEquipmentStacks(previewData.entity(), x, y, mc, drawContext, mouseX, mouseY);
            }

            // Refresh
            if (ticks % 4 == 0)
            {
                previewData = previewData.handler().onContextRefresh(previewData, world);
            }
        }
    }

    @Override
    public boolean shouldPause()
    {
        return false;
    }

    private void dumpOffers(DefaultedList<ItemStack> offers, @Nonnull DynamicRegistryManager registry)
    {
        System.out.print("dumpOffers(): DUMP OFFERS -->\n");

        if (offers == null || offers.isEmpty())
        {
            System.out.print("EMPTY!\n");
            return;
        }

        int i = 0;

        for (ItemStack entry : offers)
        {
            if (entry.isEmpty())
            {
                System.out.printf("[%d]: EMPTY!\n", i);
            }
            else
            {
                System.out.printf("[%d] Item Nbt: [%s]\n", i, entry.toNbt(registry));
            }

            i++;
        }

        System.out.print("END!\n");
    }
}
