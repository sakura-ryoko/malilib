package fi.dy.masa.malilib.render;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import fi.dy.masa.malilib.mixin.item.IMixinContainerComponent;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix4f;

import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Mth;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.coppergolem.CopperGolem;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.vehicle.AbstractChestBoat;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.entity.vehicle.MinecartHopper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BrewingStandBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.CrafterBlock;
import net.minecraft.world.level.block.DecoratedPotBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.ShelfBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.entity.ShelfBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.event.RenderEventHandler;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.util.MathUtils;
import fi.dy.masa.malilib.util.WorldUtils;
import fi.dy.masa.malilib.util.data.Constants;
import fi.dy.masa.malilib.util.data.DataBlockUtils;
import fi.dy.masa.malilib.util.data.DataEntityUtils;
import fi.dy.masa.malilib.util.data.tag.CompoundData;
import fi.dy.masa.malilib.util.data.tag.ListData;
import fi.dy.masa.malilib.util.game.IEntityOwnedInventory;
import fi.dy.masa.malilib.util.log.AnsiLogger;
import fi.dy.masa.malilib.util.nbt.NbtBlockUtils;
import fi.dy.masa.malilib.util.nbt.NbtEntityUtils;
import fi.dy.masa.malilib.util.nbt.NbtKeys;

public class InventoryOverlay
{
    private static final AnsiLogger LOGGER = new AnsiLogger(InventoryOverlay.class);

    public static final ResourceLocation TEXTURE_BREWING_STAND    = ResourceLocation.withDefaultNamespace("textures/gui/container/brewing_stand.png");
    public static final ResourceLocation TEXTURE_CRAFTER          = ResourceLocation.withDefaultNamespace("textures/gui/container/crafter.png");
    public static final ResourceLocation TEXTURE_DISPENSER        = ResourceLocation.withDefaultNamespace("textures/gui/container/dispenser.png");
    public static final ResourceLocation TEXTURE_DOUBLE_CHEST     = ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");
    public static final ResourceLocation TEXTURE_FURNACE          = ResourceLocation.withDefaultNamespace("textures/gui/container/furnace.png");
    public static final ResourceLocation TEXTURE_HOPPER           = ResourceLocation.withDefaultNamespace("textures/gui/container/hopper.png");
    public static final ResourceLocation TEXTURE_PLAYER_INV       = ResourceLocation.withDefaultNamespace("textures/gui/container/inventory.png");
    public static final ResourceLocation TEXTURE_SINGLE_CHEST     = ResourceLocation.withDefaultNamespace("textures/gui/container/shulker_box.png");

    public static final ResourceLocation TEXTURE_EMPTY_SHIELD     = ResourceLocation.withDefaultNamespace("container/slot/shield");
    public static final ResourceLocation TEXTURE_LOCKED_SLOT      = ResourceLocation.withDefaultNamespace("container/crafter/disabled_slot");

    // Additional Empty Slot Textures
    public static final ResourceLocation TEXTURE_EMPTY_HORSE_ARMOR = ResourceLocation.withDefaultNamespace("container/slot/horse_armor");
    public static final ResourceLocation TEXTURE_EMPTY_LLAMA_ARMOR = ResourceLocation.withDefaultNamespace("container/slot/llama_armor");
    public static final ResourceLocation TEXTURE_EMPTY_SADDLE      = ResourceLocation.withDefaultNamespace("container/slot/saddle");

    // Brewer Slots (1.21.4+)
    public static final ResourceLocation TEXTURE_EMPTY_BREWER_FUEL = ResourceLocation.withDefaultNamespace("container/slot/brewing_fuel");
    public static final ResourceLocation TEXTURE_EMPTY_POTION      = ResourceLocation.withDefaultNamespace("container/slot/potion");

    // Other Misc Empty Slots (1.21.4+)
    public static final ResourceLocation TEXTURE_EMPTY_SLOT_AMETHYST   = ResourceLocation.withDefaultNamespace("container/slot/amethyst_shard");
    public static final ResourceLocation TEXTURE_EMPTY_SLOT_AXE        = ResourceLocation.withDefaultNamespace("container/slot/axe");
    public static final ResourceLocation TEXTURE_EMPTY_SLOT_BANNER     = ResourceLocation.withDefaultNamespace("container/slot/banner");
    public static final ResourceLocation TEXTURE_EMPTY_SLOT_PATTERN    = ResourceLocation.withDefaultNamespace("container/slot/banner_pattern");
    public static final ResourceLocation TEXTURE_EMPTY_SLOT_DIAMOND    = ResourceLocation.withDefaultNamespace("container/slot/diamond");
    public static final ResourceLocation TEXTURE_EMPTY_SLOT_DYE        = ResourceLocation.withDefaultNamespace("container/slot/dye");
    public static final ResourceLocation TEXTURE_EMPTY_SLOT_EMERALD    = ResourceLocation.withDefaultNamespace("container/slot/emerald");
    public static final ResourceLocation TEXTURE_EMPTY_SLOT_HOE        = ResourceLocation.withDefaultNamespace("container/slot/hoe");
    public static final ResourceLocation TEXTURE_EMPTY_SLOT_INGOT      = ResourceLocation.withDefaultNamespace("container/slot/ingot");
    public static final ResourceLocation TEXTURE_EMPTY_SLOT_LAPIS      = ResourceLocation.withDefaultNamespace("container/slot/lapis_lazuli");
    public static final ResourceLocation TEXTURE_EMPTY_SLOT_PICKAXE    = ResourceLocation.withDefaultNamespace("container/slot/pickaxe");
    public static final ResourceLocation TEXTURE_EMPTY_SLOT_QUARTZ     = ResourceLocation.withDefaultNamespace("container/slot/quartz");
    public static final ResourceLocation TEXTURE_EMPTY_SLOT_REDSTONE   = ResourceLocation.withDefaultNamespace("container/slot/redstone_dust");
    public static final ResourceLocation TEXTURE_EMPTY_SLOT_SHOVEL     = ResourceLocation.withDefaultNamespace("container/slot/shovel");
    public static final ResourceLocation TEXTURE_EMPTY_SLOT_ARMOR_TRIM = ResourceLocation.withDefaultNamespace("container/slot/smithing_template_armor_trim");
    public static final ResourceLocation TEXTURE_EMPTY_SLOT_UPGRADE    = ResourceLocation.withDefaultNamespace("container/slot/smithing_template_netherite_upgrade");
    public static final ResourceLocation TEXTURE_EMPTY_SLOT_SWORD      = ResourceLocation.withDefaultNamespace("container/slot/sword");

    // Other Slot-Related textures (Nine-Slice Slots w/mcmeta)
    public static final ResourceLocation TEXTURE_EMPTY_SLOT            = ResourceLocation.withDefaultNamespace("container/slot");
    public static final ResourceLocation TEXTURE_HIGHLIGHT_BACK        = ResourceLocation.withDefaultNamespace("container/slot_highlight_back");
    public static final ResourceLocation TEXTURE_HIGHLIGHT_FRONT       = ResourceLocation.withDefaultNamespace("container/slot_highlight_front");

    private static final EquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EquipmentSlot[] { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET };
    public static final InventoryProperties INV_PROPS_TEMP = new InventoryProperties();

    private static final ResourceLocation[] EMPTY_SLOT_TEXTURES = new ResourceLocation[]
    {
        ResourceLocation.withDefaultNamespace("container/slot/boots"),
        ResourceLocation.withDefaultNamespace("container/slot/leggings"),
        ResourceLocation.withDefaultNamespace("container/slot/chestplate"),
        ResourceLocation.withDefaultNamespace("container/slot/helmet")
    };

    private static ItemStack hoveredStack = null;

    public static void renderInventoryBackground(GuiGraphics context, InventoryRenderType type, int x, int y, int slotsPerRow, int totalSlots, Minecraft mc)
    {
        renderInventoryBackground(context, type, x, y, slotsPerRow, totalSlots, -1, mc);
    }

    public static void renderInventoryBackground(GuiGraphics context, InventoryRenderType type, int x, int y, int slotsPerRow, int totalSlots, int color, Minecraft mc)
    {
        if (type == InventoryRenderType.FURNACE)
        {
            GpuTextureView gpuTextureView = RenderUtils.bindGpuTextureView(TEXTURE_FURNACE);
            if (gpuTextureView == null) return;
            
            RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x     , y     ,   0,   0,   4,  64, color); // left (top)
            RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x +  4, y     ,  84,   0,  92,   4, color); // top (right)
            RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x     , y + 64,   0, 162,  92,   4, color); // bottom (left)
            RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x + 92, y +  4, 172, 102,   4,  64, color); // right (bottom)
            RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x +  4, y +  4,  52,  13,  88,  60, color); // middle
        }
        else if (type == InventoryRenderType.BREWING_STAND)
        {
            GpuTextureView gpuTextureView = RenderUtils.bindGpuTextureView(TEXTURE_BREWING_STAND);
            if (gpuTextureView == null) return;

            RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x      , y     ,   0,   0,   4,  68, color); // left (top)
            RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x +   4, y     ,  63,   0, 113,   4, color); // top (right)
            RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x      , y + 68,   0, 162, 113,   4, color); // bottom (left)
            RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x + 113, y +  4, 172,  98,   4,  68, color); // right (bottom)
            RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x +   4, y +  4,  13,  13, 109,  64, color); // middle
        }
        else if (type == InventoryRenderType.CRAFTER)
        {
            // We just hack in the Dispenser Texture, so it displays right.  Easy.
            GpuTextureView gpuTextureView = RenderUtils.bindGpuTextureView(TEXTURE_DISPENSER);
            if (gpuTextureView == null) return;

            RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x     , y     ,   0,   0,   7,  61, color); // left (top)
            RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x +  7, y     , 115,   0,  61,   7, color); // top (right)
            RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x     , y + 61,   0, 159,  61,   7, color); // bottom (left)
            RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x + 61, y +  7, 169, 105,   7,  61, color); // right (bottom)
            RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x +  7, y +  7,  61,  16,  54,  54, color); // middle
        }
        else if (type == InventoryRenderType.DISPENSER)
        {
            GpuTextureView gpuTextureView = RenderUtils.bindGpuTextureView(TEXTURE_DISPENSER);
            if (gpuTextureView == null) return;

            RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x     , y     ,   0,   0,   7,  61, color); // left (top)
            RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x +  7, y     , 115,   0,  61,   7, color); // top (right)
            RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x     , y + 61,   0, 159,  61,   7, color); // bottom (left)
            RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x + 61, y +  7, 169, 105,   7,  61, color); // right (bottom)
            RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x +  7, y +  7,  61,  16,  54,  54, color); // middle
        }
        else if (type == InventoryRenderType.HOPPER)
        {
            GpuTextureView gpuTextureView = RenderUtils.bindGpuTextureView(TEXTURE_HOPPER);
            if (gpuTextureView == null) return;

            RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x      , y     ,   0,   0,   7,  25, color); // left (top)
            RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x +   7, y     ,  79,   0,  97,   7, color); // top (right)
            RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x      , y + 25,   0, 126,  97,   7, color); // bottom (left)
            RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x +  97, y +  7, 169, 108,   7,  25, color); // right (bottom)
            RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x +   7, y +  7,  43,  19,  90,  18, color); // middle
        }
        // Most likely a Villager, or possibly a Llama
        else if (type == InventoryRenderType.VILLAGER)
        {
            GpuTextureView gpuTextureView = RenderUtils.bindGpuTextureView(TEXTURE_DOUBLE_CHEST);
            if (gpuTextureView == null) return;

            RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x     , y     ,   0,   0,   7,  79, color); // left (top)
            RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x +  7, y     , 133,   0,  43,   7, color); // top (right)
            RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x     , y + 79,   0, 215,  43,   7, color); // bottom (left)
            RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x + 43, y +  7, 169, 143,   7,  79, color); // right (bottom)
            RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x +  7, y +  7,   7,  17,  36,  72, color); // 2x4 slots
        }
        else if (type == InventoryRenderType.FIXED_27)
        {
            renderInventoryBackground27(x, y, color, mc, context);
        }
        else if (type == InventoryRenderType.FIXED_54)
        {
            renderInventoryBackground54(x, y, color, mc, context);
        }
        else
        {
            GpuTextureView gpuTextureView = RenderUtils.bindGpuTextureView(TEXTURE_DOUBLE_CHEST);
            if (gpuTextureView == null) return;

            // Draw the slot backgrounds according to how many slots there actually are
            int rows = (int) (Math.ceil((double) totalSlots / (double) slotsPerRow));
            int bgw = Math.min(totalSlots, slotsPerRow) * 18 + 7;
            int bgh = rows * 18 + 7;

            RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x      , y      ,         0,         0,   7, bgh, color); // left (top)
            RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x +   7, y      , 176 - bgw,         0, bgw,   7, color); // top (right)
            RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x      , y + bgh,         0,       215, bgw,   7, color); // bottom (left)
            RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x + bgw, y +   7,       169, 222 - bgh,   7, bgh, color); // right (bottom)

            for (int row = 0; row < rows; row++)
            {
                int rowLen = Mth.clamp(totalSlots - (row * slotsPerRow), 1, slotsPerRow);
                RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x + 7, y + row * 18 + 7, 7, 17, rowLen * 18, 18, color);

                // Render the background for the last non-existing slots on the last row,
                // in two strips of the background texture from the double chest texture's top part.
                if (rows > 1 && rowLen < slotsPerRow)
                {
                    RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x + rowLen * 18 + 7, y + row * 18 +  7, 7, 3, (slotsPerRow - rowLen) * 18, 9, color);
                    RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x + rowLen * 18 + 7, y + row * 18 + 16, 7, 3, (slotsPerRow - rowLen) * 18, 9, color);
                }
            }
        }
    }

	@ApiStatus.Experimental
	public static void renderInventoryBackgroundNew(GuiGraphics context, InventoryOverlayType type, int x, int y, int slotsPerRow, int totalSlots, Minecraft mc)
	{
		renderInventoryBackgroundNew(context, type, x, y, slotsPerRow, totalSlots, -1, mc);
	}

	@ApiStatus.Experimental
	public static void renderInventoryBackgroundNew(GuiGraphics context, InventoryOverlayType type, int x, int y, int slotsPerRow, int totalSlots, int color, Minecraft mc)
	{
		if (type == InventoryOverlayType.FURNACE)
		{
			GpuTextureView gpuTextureView = RenderUtils.bindGpuTextureView(TEXTURE_FURNACE);
			if (gpuTextureView == null) return;

			RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x     , y     ,   0,   0,   4,  64, color); // left (top)
			RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x +  4, y     ,  84,   0,  92,   4, color); // top (right)
			RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x     , y + 64,   0, 162,  92,   4, color); // bottom (left)
			RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x + 92, y +  4, 172, 102,   4,  64, color); // right (bottom)
			RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x +  4, y +  4,  52,  13,  88,  60, color); // middle
		}
		else if (type == InventoryOverlayType.BREWING_STAND)
		{
			GpuTextureView gpuTextureView = RenderUtils.bindGpuTextureView(TEXTURE_BREWING_STAND);
			if (gpuTextureView == null) return;

			RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x      , y     ,   0,   0,   4,  68, color); // left (top)
			RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x +   4, y     ,  63,   0, 113,   4, color); // top (right)
			RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x      , y + 68,   0, 162, 113,   4, color); // bottom (left)
			RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x + 113, y +  4, 172,  98,   4,  68, color); // right (bottom)
			RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x +   4, y +  4,  13,  13, 109,  64, color); // middle
		}
		else if (type == InventoryOverlayType.CRAFTER)
		{
			// We just hack in the Dispenser Texture, so it displays right.  Easy.
			GpuTextureView gpuTextureView = RenderUtils.bindGpuTextureView(TEXTURE_DISPENSER);
			if (gpuTextureView == null) return;

			RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x     , y     ,   0,   0,   7,  61, color); // left (top)
			RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x +  7, y     , 115,   0,  61,   7, color); // top (right)
			RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x     , y + 61,   0, 159,  61,   7, color); // bottom (left)
			RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x + 61, y +  7, 169, 105,   7,  61, color); // right (bottom)
			RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x +  7, y +  7,  61,  16,  54,  54, color); // middle
		}
		else if (type == InventoryOverlayType.DISPENSER)
		{
			GpuTextureView gpuTextureView = RenderUtils.bindGpuTextureView(TEXTURE_DISPENSER);
			if (gpuTextureView == null) return;

			RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x     , y     ,   0,   0,   7,  61, color); // left (top)
			RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x +  7, y     , 115,   0,  61,   7, color); // top (right)
			RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x     , y + 61,   0, 159,  61,   7, color); // bottom (left)
			RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x + 61, y +  7, 169, 105,   7,  61, color); // right (bottom)
			RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x +  7, y +  7,  61,  16,  54,  54, color); // middle
		}
		else if (type == InventoryOverlayType.HOPPER)
		{
			GpuTextureView gpuTextureView = RenderUtils.bindGpuTextureView(TEXTURE_HOPPER);
			if (gpuTextureView == null) return;

			RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x      , y     ,   0,   0,   7,  25, color); // left (top)
			RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x +   7, y     ,  79,   0,  97,   7, color); // top (right)
			RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x      , y + 25,   0, 126,  97,   7, color); // bottom (left)
			RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x +  97, y +  7, 169, 108,   7,  25, color); // right (bottom)
			RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x +   7, y +  7,  43,  19,  90,  18, color); // middle
		}
		// Most likely a Villager, or possibly a Llama
		else if (type == InventoryOverlayType.VILLAGER)
		{
			GpuTextureView gpuTextureView = RenderUtils.bindGpuTextureView(TEXTURE_DOUBLE_CHEST);
			if (gpuTextureView == null) return;

			RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x     , y     ,   0,   0,   7,  79, color); // left (top)
			RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x +  7, y     , 133,   0,  43,   7, color); // top (right)
			RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x     , y + 79,   0, 215,  43,   7, color); // bottom (left)
			RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x + 43, y +  7, 169, 143,   7,  79, color); // right (bottom)
			RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x +  7, y +  7,   7,  17,  36,  72, color); // 2x4 slots
		}
		else if (type == InventoryOverlayType.FIXED_27)
		{
			renderInventoryBackground27(x, y, color, mc, context);
		}
		else if (type == InventoryOverlayType.FIXED_54)
		{
			renderInventoryBackground54(x, y, color, mc, context);
		}
		else
		{
			GpuTextureView gpuTextureView = RenderUtils.bindGpuTextureView(TEXTURE_DOUBLE_CHEST);
			if (gpuTextureView == null) return;

			// Draw the slot backgrounds according to how many slots there actually are
			int rows = (int) (Math.ceil((double) totalSlots / (double) slotsPerRow));
			int bgw = Math.min(totalSlots, slotsPerRow) * 18 + 7;
			int bgh = rows * 18 + 7;

			RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x      , y      ,         0,         0,   7, bgh, color); // left (top)
			RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x +   7, y      , 176 - bgw,         0, bgw,   7, color); // top (right)
			RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x      , y + bgh,         0,       215, bgw,   7, color); // bottom (left)
			RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x + bgw, y +   7,       169, 222 - bgh,   7, bgh, color); // right (bottom)

			for (int row = 0; row < rows; row++)
			{
				int rowLen = Mth.clamp(totalSlots - (row * slotsPerRow), 1, slotsPerRow);
				RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x + 7, y + row * 18 + 7, 7, 17, rowLen * 18, 18, color);

				// Render the background for the last non-existing slots on the last row,
				// in two strips of the background texture from the double chest texture's top part.
				if (rows > 1 && rowLen < slotsPerRow)
				{
					RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x + rowLen * 18 + 7, y + row * 18 +  7, 7, 3, (slotsPerRow - rowLen) * 18, 9, color);
					RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x + rowLen * 18 + 7, y + row * 18 + 16, 7, 3, (slotsPerRow - rowLen) * 18, 9, color);
				}
			}
		}
	}

    public static void renderInventoryBackground27(int x, int y, int color, Minecraft mc, GuiGraphics context)
    {
        GpuTextureView gpuTextureView = RenderUtils.bindGpuTextureView(TEXTURE_SINGLE_CHEST);
        if (gpuTextureView == null) return;

        RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x      , y     ,   0,   0,   7,  61, color); // left (top)
        RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x +   7, y     ,   7,   0, 169,   7, color); // top (right)
        RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x      , y + 61,   0, 159, 169,   7, color); // bottom (left)
        RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x + 169, y +  7, 169, 105,   7,  61, color); // right (bottom)
        RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x +   7, y +  7,   7,  17, 162,  54, color); // middle
    }

    public static void renderInventoryBackground54(int x, int y, int color, Minecraft mc, GuiGraphics context)
    {
        GpuTextureView gpuTextureView = RenderUtils.bindGpuTextureView(TEXTURE_DOUBLE_CHEST);
        if (gpuTextureView == null) return;

        RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x      , y      ,   0,   0,   7, 115, color); // left (top)
        RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x +   7, y      ,   7,   0, 169,   7, color); // top (right)
        RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x      , y + 115,   0, 215, 169,   7, color); // bottom (left)
        RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x + 169, y +   7, 169, 107,   7, 115, color); // right (bottom)
        RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x +   7, y +   7,   7,  17, 162, 108, color); // middle
    }

    public static void renderInventoryBackgroundSlots(GuiGraphics drawContext, InventoryRenderType type, Container inv, int x, int y)
    {
        if (type == InventoryRenderType.BREWING_STAND)
        {
            renderBrewerBackgroundSlots(drawContext, inv, x, y);
        }
        else if (type == InventoryRenderType.HORSE)
        {
            renderHorseArmorBackgroundSlots(drawContext, inv, x, y);
        }
        else if (type == InventoryRenderType.LLAMA)
        {
            renderLlamaArmorBackgroundSlots(drawContext, inv, x, y);
        }
        else if (type == InventoryRenderType.WOLF || type == InventoryRenderType.HAPPY_GHAST || type == InventoryRenderType.COPPER_GOLEM)
        {
            renderWolfArmorBackgroundSlots(drawContext, inv, x, y);
        }
    }

	@ApiStatus.Experimental
	public static void renderInventoryBackgroundSlotsNew(GuiGraphics drawContext, InventoryOverlayType type, Container inv, int x, int y)
	{
		if (type == InventoryOverlayType.BREWING_STAND)
		{
			renderBrewerBackgroundSlots(drawContext, inv, x, y);
		}
		else if (type == InventoryOverlayType.HORSE)
		{
			renderHorseArmorBackgroundSlots(drawContext, inv, x, y);
		}
		else if (type == InventoryOverlayType.LLAMA)
		{
			renderLlamaArmorBackgroundSlots(drawContext, inv, x, y);
		}
		else if (type == InventoryOverlayType.WOLF || type == InventoryOverlayType.HAPPY_GHAST || type == InventoryOverlayType.COPPER_GOLEM)
		{
			renderWolfArmorBackgroundSlots(drawContext, inv, x, y);
		}
	}

	public static void renderBrewerBackgroundSlots(GuiGraphics drawContext, Container inv, int x, int y)
    {
        renderBrewerBackgroundSlots(drawContext, inv, x, y, 0.9f, 0, 0);
    }

    public static void renderBrewerBackgroundSlots(GuiGraphics drawContext, Container inv, int x, int y, float scale, double mouseX, double mouseY)
    {
        if (inv.getItem(0).isEmpty())
        {
            renderBackgroundSlotAt(drawContext, TEXTURE_EMPTY_POTION, x + 47, y + 42, scale, mouseX, mouseY);
        }
        if (inv.getItem(1).isEmpty())
        {
            renderBackgroundSlotAt(drawContext, TEXTURE_EMPTY_POTION, x + 70, y + 49, scale, mouseX, mouseY);
        }
        if (inv.getItem(2).isEmpty())
        {
            renderBackgroundSlotAt(drawContext, TEXTURE_EMPTY_POTION, x + 93, y + 42, scale, mouseX, mouseY);
        }
        if (inv.getItem(4).isEmpty())
        {
            renderBackgroundSlotAt(drawContext, TEXTURE_EMPTY_BREWER_FUEL, x + 8, y + 8, scale, mouseX, mouseY);
        }
    }

    public static void renderHorseArmorBackgroundSlots(GuiGraphics drawContext, Container inv, int x, int y)
    {
        renderHorseArmorBackgroundSlots(drawContext, inv, x, y, 0.9f, 0, 0);
    }

    public static void renderHorseArmorBackgroundSlots(GuiGraphics drawContext, Container inv, int x, int y, float scale, double mouseX, double mouseY)
    {
        if (inv.getItem(0).isEmpty())
        {
            renderBackgroundSlotAt(drawContext, TEXTURE_EMPTY_HORSE_ARMOR, x, y, scale, mouseX, mouseY);
        }

        if (inv.getItem(1).isEmpty())
        {
            renderBackgroundSlotAt(drawContext, TEXTURE_EMPTY_SADDLE, x, y + 18, scale, mouseX, mouseY);
        }
    }

    public static void renderLlamaArmorBackgroundSlots(GuiGraphics drawContext, Container inv, int x, int y)
    {
        renderLlamaArmorBackgroundSlots(drawContext, inv, x, y, 0.9f, 0, 0);
    }

    public static void renderLlamaArmorBackgroundSlots(GuiGraphics drawContext, Container inv, int x, int y, float scale, double mouseX, double mouseY)
    {
        if (inv.getItem(0).isEmpty())
        {
            renderBackgroundSlotAt(drawContext, TEXTURE_EMPTY_LLAMA_ARMOR, x, y, scale, mouseX, mouseY);
        }
    }

    public static void renderWolfArmorBackgroundSlots(GuiGraphics drawContext, Container inv, int x, int y)
    {
        renderWolfArmorBackgroundSlots(drawContext, inv, x, y, 0.9f, 0, 0);
    }

    public static void renderWolfArmorBackgroundSlots(GuiGraphics drawContext, Container inv, int x, int y, float scale, double mouseX, double mouseY)
    {
        if (inv.getItem(0).isEmpty())
        {
            renderBackgroundSlotAt(drawContext, TEXTURE_EMPTY_HORSE_ARMOR, x, y, scale, mouseX, mouseY);
        }
    }

    public static void renderEquipmentOverlayBackground(GuiGraphics context, int x, int y, LivingEntity entity)
    {
        GpuTextureView gpuTextureView = RenderUtils.bindGpuTextureView(TEXTURE_DISPENSER);
        if (gpuTextureView == null) return;

        RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x     , y     ,   0,   0, 50, 83); // top-left (main part)
        RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x + 50, y     , 173,   0,  3, 83); // right edge top
        RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x     , y + 83,   0, 163, 50,  3); // bottom edge left
        RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x + 50, y + 83, 173, 163,  3,  3); // bottom right corner

        for (int i = 0, xOff = 7, yOff = 7; i < 4; ++i, yOff += 18)
        {
            RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x + xOff, y + yOff, 61, 16, 18, 18);
        }

        // Main hand and offhand
        RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x + 28, y + 2 * 18 + 7, 61, 16, 18, 18);
        RenderUtils.drawTexturedRectBatched(context, gpuTextureView, x + 28, y + 3 * 18 + 7, 61, 16, 18, 18);

        if (entity.getItemBySlot(EquipmentSlot.OFFHAND).isEmpty())
        {
            renderBackgroundSlotAt(context, TEXTURE_EMPTY_SHIELD, x + 28 + 1, y + 3 * 18 + 7 + 1);
        }

        for (int i = 0, xOff = 7, yOff = 7; i < 4; ++i, yOff += 18)
        {
            final EquipmentSlot eqSlot = VALID_EQUIPMENT_SLOTS[i];

            if (entity.getItemBySlot(eqSlot).isEmpty())
            {
                ResourceLocation texture = EMPTY_SLOT_TEXTURES[eqSlot.getIndex()];
                renderBackgroundSlotAt(context, texture, x + xOff + 1, y + yOff + 1);
            }
        }
    }

    public static InventoryRenderType getInventoryType(@Nullable Container inv)
    {
        if (inv == null)
        {
            return InventoryRenderType.GENERIC;
        }

        if (inv instanceof ShulkerBoxBlockEntity)
        {
            return InventoryRenderType.FIXED_27;
        }
        else if (inv instanceof CompoundContainer)
        {
            return InventoryRenderType.FIXED_54;
        }
        else if (inv instanceof AbstractChestBoat)
        {
            return InventoryRenderType.FIXED_27;
        }
        else if (inv instanceof MinecartChest)
        {
            return InventoryRenderType.FIXED_27;
        }
        else if (inv instanceof AbstractFurnaceBlockEntity)
        {
            return InventoryRenderType.FURNACE;
        }
        else if (inv instanceof BrewingStandBlockEntity)
        {
            return InventoryRenderType.BREWING_STAND;
        }
        else if (inv instanceof CrafterBlockEntity)
        {
            return InventoryRenderType.CRAFTER;
        }
        else if (inv instanceof DispenserBlockEntity)
        {
            // this includes the Dropper as a subclass
            return InventoryRenderType.DISPENSER;
        }
        else if (inv instanceof HopperBlockEntity)
        {
            return InventoryRenderType.HOPPER;
        }
        else if (inv instanceof MinecartHopper)
        {
            return InventoryRenderType.HOPPER;
        }
        else if (inv instanceof ChiseledBookShelfBlockEntity)
        {
            return InventoryRenderType.BOOKSHELF;
        }
        else if (inv instanceof ShelfBlockEntity)
        {
            return InventoryRenderType.WALL_SHELF;
        }
        else if (inv instanceof Inventory)
        {
            return InventoryRenderType.PLAYER;
        }
        else if (inv instanceof IEntityOwnedInventory inventory)
        {
            if (inventory.malilib$getEntityOwner() instanceof Llama)
            {
                return InventoryRenderType.LLAMA;
            }
            else if (inventory.malilib$getEntityOwner() instanceof Wolf)
            {
                return InventoryRenderType.WOLF;
            }
			else if (inventory.malilib$getEntityOwner() instanceof CopperGolem)
			{
				return InventoryRenderType.COPPER_GOLEM;
			}
            else if (inventory.malilib$getEntityOwner() instanceof AbstractHorse)
            {
                return InventoryRenderType.HORSE;
            }
            else if (inventory.malilib$getEntityOwner() instanceof Piglin)
            {
                return InventoryRenderType.VILLAGER;
            }
        }

        return InventoryRenderType.GENERIC;
    }

	@ApiStatus.Experimental
	public static InventoryOverlayType getInventoryTypeNew(@Nullable Container inv)
	{
		if (inv == null)
		{
			return InventoryOverlayType.GENERIC;
		}

		if (inv instanceof ShulkerBoxBlockEntity)
		{
			return InventoryOverlayType.FIXED_27;
		}
		else if (inv instanceof CompoundContainer)
		{
			return InventoryOverlayType.FIXED_54;
		}
		else if (inv instanceof AbstractChestBoat)
		{
			return InventoryOverlayType.FIXED_27;
		}
		else if (inv instanceof MinecartChest)
		{
			return InventoryOverlayType.FIXED_27;
		}
		else if (inv instanceof AbstractFurnaceBlockEntity)
		{
			return InventoryOverlayType.FURNACE;
		}
		else if (inv instanceof BrewingStandBlockEntity)
		{
			return InventoryOverlayType.BREWING_STAND;
		}
		else if (inv instanceof CrafterBlockEntity)
		{
			return InventoryOverlayType.CRAFTER;
		}
		else if (inv instanceof DispenserBlockEntity)
		{
			// this includes the Dropper as a subclass
			return InventoryOverlayType.DISPENSER;
		}
		else if (inv instanceof HopperBlockEntity)
		{
			return InventoryOverlayType.HOPPER;
		}
		else if (inv instanceof MinecartHopper)
		{
			return InventoryOverlayType.HOPPER;
		}
		else if (inv instanceof ChiseledBookShelfBlockEntity)
		{
			return InventoryOverlayType.BOOKSHELF;
		}
		else if (inv instanceof ShelfBlockEntity)
		{
			return InventoryOverlayType.WALL_SHELF;
		}
		else if (inv instanceof Inventory)
		{
			return InventoryOverlayType.PLAYER;
		}
		else if (inv instanceof IEntityOwnedInventory inventory)
		{
			if (inventory.malilib$getEntityOwner() instanceof Llama)
			{
				return InventoryOverlayType.LLAMA;
			}
			else if (inventory.malilib$getEntityOwner() instanceof Wolf)
			{
				return InventoryOverlayType.WOLF;
			}
			else if (inventory.malilib$getEntityOwner() instanceof CopperGolem)
			{
				return InventoryOverlayType.COPPER_GOLEM;
			}
			else if (inventory.malilib$getEntityOwner() instanceof AbstractHorse)
			{
				return InventoryOverlayType.HORSE;
			}
			else if (inventory.malilib$getEntityOwner() instanceof Piglin)
			{
				return InventoryOverlayType.VILLAGER;
			}
		}

		return InventoryOverlayType.GENERIC;
	}

	public static InventoryRenderType getInventoryType(ItemStack stack)
    {
        Item item = stack.getItem();
        ItemContainerContents container = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);

        if (item instanceof BlockItem)
        {
            Block block = ((BlockItem) item).getBlock();

            if (block instanceof ShulkerBoxBlock || block instanceof ChestBlock || block instanceof BarrelBlock)
            {
                final int size = ((IMixinContainerComponent) (Object) container).malilib_getStacks().size();

                // For "Double Inventory" Barrels, etc.
                if (size >= 0 && size <= 27)
                {
                    return InventoryRenderType.FIXED_27;
                }
                else if (size > 27 && size <= 54)
                {
                    return InventoryRenderType.FIXED_54;
                }
                else if (size > 54 && size < 256)
                {
                    return InventoryRenderType.GENERIC;
                }
            }
            else if (block instanceof AbstractFurnaceBlock)
            {
                return InventoryRenderType.FURNACE;
            }
            else if (block instanceof DispenserBlock) // this includes the Dropper as a sub class
            {
                return InventoryRenderType.DISPENSER;
            }
            else if (block instanceof HopperBlock)
            {
                return InventoryRenderType.HOPPER;
            }
            else if (block instanceof BrewingStandBlock)
            {
                return InventoryRenderType.BREWING_STAND;
            }
            else if (block instanceof CrafterBlock)
            {
                return InventoryRenderType.CRAFTER;
            }
            else if (block instanceof DecoratedPotBlock || block instanceof JukeboxBlock || block instanceof LecternBlock)
            {
                return InventoryRenderType.SINGLE_ITEM;
            }
            else if (block instanceof ChiseledBookShelfBlock)
            {
                return InventoryRenderType.BOOKSHELF;
            }
            else if (block instanceof ShelfBlock)
            {
                return InventoryRenderType.WALL_SHELF;
            }
            else if (block instanceof EnderChestBlock)
            {
                return InventoryRenderType.ENDER_CHEST;
            }
        }
        else if (item instanceof BundleItem)
        {
            return InventoryRenderType.BUNDLE;
        }

        return InventoryRenderType.GENERIC;
    }

	@ApiStatus.Experimental
	public static InventoryOverlayType getInventoryTypeNew(ItemStack stack)
	{
		Item item = stack.getItem();
		ItemContainerContents container = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);

		if (item instanceof BlockItem)
		{
			Block block = ((BlockItem) item).getBlock();

			if (block instanceof ShulkerBoxBlock || block instanceof ChestBlock || block instanceof BarrelBlock)
			{
				final int size = ((IMixinContainerComponent) (Object) container).malilib_getStacks().size();

				// For "Double Inventory" Barrels, etc.
				if (size >= 0 && size <= 27)
				{
					return InventoryOverlayType.FIXED_27;
				}
				else if (size > 27 && size <= 54)
				{
					return InventoryOverlayType.FIXED_54;
				}
				else if (size > 54 && size < 256)
				{
					return InventoryOverlayType.GENERIC;
				}
			}
			else if (block instanceof AbstractFurnaceBlock)
			{
				return InventoryOverlayType.FURNACE;
			}
			else if (block instanceof DispenserBlock) // this includes the Dropper as a sub class
			{
				return InventoryOverlayType.DISPENSER;
			}
			else if (block instanceof HopperBlock)
			{
				return InventoryOverlayType.HOPPER;
			}
			else if (block instanceof BrewingStandBlock)
			{
				return InventoryOverlayType.BREWING_STAND;
			}
			else if (block instanceof CrafterBlock)
			{
				return InventoryOverlayType.CRAFTER;
			}
			else if (block instanceof DecoratedPotBlock || block instanceof JukeboxBlock || block instanceof LecternBlock)
			{
				return InventoryOverlayType.SINGLE_ITEM;
			}
			else if (block instanceof ChiseledBookShelfBlock)
			{
				return InventoryOverlayType.BOOKSHELF;
			}
			else if (block instanceof ShelfBlock)
			{
				return InventoryOverlayType.WALL_SHELF;
			}
			else if (block instanceof EnderChestBlock)
			{
				return InventoryOverlayType.ENDER_CHEST;
			}
		}
		else if (item instanceof BundleItem)
		{
			return InventoryOverlayType.BUNDLE;
		}

		return InventoryOverlayType.GENERIC;
	}

	/**
     * Attempts to get the Inventory Type based on raw NBT tags.
     * @param nbt
     * @return
     */
    public static InventoryRenderType getInventoryType(@Nonnull CompoundTag nbt)
    {
        BlockEntityType<?> blockType = NbtBlockUtils.getBlockEntityTypeFromNbt(nbt);

        if (blockType != null)
        {
            if (blockType.equals(BlockEntityType.SHULKER_BOX) ||
                blockType.equals(BlockEntityType.BARREL) ||
                blockType.equals(BlockEntityType.CHEST) ||
                blockType.equals(BlockEntityType.TRAPPED_CHEST))
            {
                if (nbt.contains(NbtKeys.ITEMS))
                {
                    ListTag list = nbt.getList(NbtKeys.ITEMS).orElse(new ListTag());

                    if (list.size() > 27)
                    {
                        return InventoryRenderType.FIXED_54;
                    }
                }

                return InventoryRenderType.FIXED_27;
            }
            else if (blockType.equals(BlockEntityType.FURNACE) ||
                blockType.equals(BlockEntityType.BLAST_FURNACE) ||
                blockType.equals(BlockEntityType.SMOKER))
            {
                return InventoryRenderType.FURNACE;
            }
            else if (blockType.equals(BlockEntityType.DISPENSER) ||
                    blockType.equals(BlockEntityType.DROPPER))
            {
                return InventoryRenderType.DISPENSER;
            }
            else if (blockType.equals(BlockEntityType.HOPPER))
            {
                return InventoryRenderType.HOPPER;
            }
            else if (blockType.equals(BlockEntityType.BREWING_STAND))
            {
                return InventoryRenderType.BREWING_STAND;
            }
            else if (blockType.equals(BlockEntityType.CRAFTER))
            {
                return InventoryRenderType.CRAFTER;
            }
            else if (blockType.equals(BlockEntityType.DECORATED_POT) ||
                    blockType.equals(BlockEntityType.JUKEBOX) ||
                    blockType.equals(BlockEntityType.LECTERN))
            {
                return InventoryRenderType.SINGLE_ITEM;
            }
            else if (blockType.equals(BlockEntityType.CHISELED_BOOKSHELF))
            {
                return InventoryRenderType.BOOKSHELF;
            }
            else if (blockType.equals(BlockEntityType.SHELF))
            {
                return InventoryRenderType.WALL_SHELF;
            }
            else if (blockType.equals(BlockEntityType.ENDER_CHEST))
            {
                return InventoryRenderType.ENDER_CHEST;
            }
        }

        EntityType<?> entityType = NbtEntityUtils.getEntityTypeFromNbt(nbt);

        if (entityType != null)
        {
            if (entityType.equals(EntityType.CHEST_MINECART) ||
                entityType.equals(EntityType.ACACIA_CHEST_BOAT) ||
                entityType.equals(EntityType.BAMBOO_CHEST_RAFT) ||
                entityType.equals(EntityType.BIRCH_CHEST_BOAT) ||
                entityType.equals(EntityType.CHERRY_CHEST_BOAT) ||
                entityType.equals(EntityType.DARK_OAK_CHEST_BOAT) ||
                entityType.equals(EntityType.JUNGLE_CHEST_BOAT) ||
                entityType.equals(EntityType.MANGROVE_CHEST_BOAT) ||
                entityType.equals(EntityType.OAK_CHEST_BOAT) ||
                entityType.equals(EntityType.PALE_OAK_CHEST_BOAT) ||
                entityType.equals(EntityType.SPRUCE_CHEST_BOAT))
            {
                return InventoryRenderType.FIXED_27;
            }
            else if (entityType.equals(EntityType.HOPPER_MINECART))
            {
                return InventoryRenderType.HOPPER;
            }
            else if (entityType.equals(EntityType.HORSE) ||
                     entityType.equals(EntityType.DONKEY) ||
                     entityType.equals(EntityType.MULE) ||
                     entityType.equals(EntityType.CAMEL) ||
                     entityType.equals(EntityType.SKELETON_HORSE) ||
                     entityType.equals(EntityType.ZOMBIE_HORSE))
            {
                return InventoryRenderType.HORSE;
            }
            else if (entityType.equals(EntityType.LLAMA) ||
                entityType.equals(EntityType.TRADER_LLAMA))
            {
                return InventoryRenderType.LLAMA;
            }
            else if (entityType.equals(EntityType.WOLF))
            {
                return InventoryRenderType.WOLF;
            }
            else if (entityType.equals(EntityType.HAPPY_GHAST))
            {
                return InventoryRenderType.HAPPY_GHAST;
            }
			else if (entityType.equals(EntityType.COPPER_GOLEM))
			{
				return InventoryRenderType.COPPER_GOLEM;
			}
            else if (entityType.equals(EntityType.VILLAGER) ||
                     entityType.equals(EntityType.ALLAY) ||
                     entityType.equals(EntityType.PILLAGER) ||
                     entityType.equals(EntityType.PIGLIN) ||
                     entityType.equals(EntityType.WANDERING_TRADER) ||
                     entityType.equals(EntityType.ZOMBIE_VILLAGER))
            {
                return InventoryRenderType.VILLAGER;
            }
            else if (entityType.equals(EntityType.PLAYER))
            {
                return InventoryRenderType.PLAYER;
            }
            else if (entityType.equals(EntityType.ARMOR_STAND))
            {
                return InventoryRenderType.ARMOR_STAND;
            }
            else if (nbt.contains(NbtKeys.ATTRIB) || nbt.contains(NbtKeys.EFFECTS) || nbt.contains(NbtKeys.FALL_FLYING))
            {
                return InventoryRenderType.LIVING_ENTITY;
            }
        }

        return InventoryRenderType.GENERIC;
    }

	/**
	 * Attempts to get the Inventory Type based on raw NBT tags.
	 * @param data
	 * @return
	 */
	@ApiStatus.Experimental
	public static InventoryOverlayType getInventoryTypeNew(@Nonnull CompoundData data)
	{
		BlockEntityType<?> blockType = DataBlockUtils.getBlockEntityType(data);

		if (blockType != null)
		{
			if (blockType.equals(BlockEntityType.SHULKER_BOX) ||
				blockType.equals(BlockEntityType.BARREL) ||
				blockType.equals(BlockEntityType.CHEST) ||
				blockType.equals(BlockEntityType.TRAPPED_CHEST))
			{
				if (data.contains(NbtKeys.ITEMS, Constants.NBT.TAG_LIST))
				{
					ListData list = data.getList(NbtKeys.ITEMS);

					if (list.size() > 27)
					{
						return InventoryOverlayType.FIXED_54;
					}
				}

				return InventoryOverlayType.FIXED_27;
			}
			else if (blockType.equals(BlockEntityType.FURNACE) ||
					blockType.equals(BlockEntityType.BLAST_FURNACE) ||
					blockType.equals(BlockEntityType.SMOKER))
			{
				return InventoryOverlayType.FURNACE;
			}
			else if (blockType.equals(BlockEntityType.DISPENSER) ||
					blockType.equals(BlockEntityType.DROPPER))
			{
				return InventoryOverlayType.DISPENSER;
			}
			else if (blockType.equals(BlockEntityType.HOPPER))
			{
				return InventoryOverlayType.HOPPER;
			}
			else if (blockType.equals(BlockEntityType.BREWING_STAND))
			{
				return InventoryOverlayType.BREWING_STAND;
			}
			else if (blockType.equals(BlockEntityType.CRAFTER))
			{
				return InventoryOverlayType.CRAFTER;
			}
			else if (blockType.equals(BlockEntityType.DECORATED_POT) ||
					blockType.equals(BlockEntityType.JUKEBOX) ||
					blockType.equals(BlockEntityType.LECTERN))
			{
				return InventoryOverlayType.SINGLE_ITEM;
			}
			else if (blockType.equals(BlockEntityType.CHISELED_BOOKSHELF))
			{
				return InventoryOverlayType.BOOKSHELF;
			}
			else if (blockType.equals(BlockEntityType.SHELF))
			{
				return InventoryOverlayType.WALL_SHELF;
			}
			else if (blockType.equals(BlockEntityType.ENDER_CHEST))
			{
				return InventoryOverlayType.ENDER_CHEST;
			}
		}

		EntityType<?> entityType = DataEntityUtils.getEntityType(data);

		if (entityType != null)
		{
			if (entityType.equals(EntityType.CHEST_MINECART) ||
				entityType.equals(EntityType.ACACIA_CHEST_BOAT) ||
				entityType.equals(EntityType.BAMBOO_CHEST_RAFT) ||
				entityType.equals(EntityType.BIRCH_CHEST_BOAT) ||
				entityType.equals(EntityType.CHERRY_CHEST_BOAT) ||
				entityType.equals(EntityType.DARK_OAK_CHEST_BOAT) ||
				entityType.equals(EntityType.JUNGLE_CHEST_BOAT) ||
				entityType.equals(EntityType.MANGROVE_CHEST_BOAT) ||
				entityType.equals(EntityType.OAK_CHEST_BOAT) ||
				entityType.equals(EntityType.PALE_OAK_CHEST_BOAT) ||
				entityType.equals(EntityType.SPRUCE_CHEST_BOAT))
			{
				return InventoryOverlayType.FIXED_27;
			}
			else if (entityType.equals(EntityType.HOPPER_MINECART))
			{
				return InventoryOverlayType.HOPPER;
			}
			else if (entityType.equals(EntityType.HORSE) ||
					entityType.equals(EntityType.DONKEY) ||
					entityType.equals(EntityType.MULE) ||
					entityType.equals(EntityType.CAMEL) ||
					entityType.equals(EntityType.SKELETON_HORSE) ||
					entityType.equals(EntityType.ZOMBIE_HORSE))
			{
				return InventoryOverlayType.HORSE;
			}
			else if (entityType.equals(EntityType.LLAMA) ||
					entityType.equals(EntityType.TRADER_LLAMA))
			{
				return InventoryOverlayType.LLAMA;
			}
			else if (entityType.equals(EntityType.WOLF))
			{
				return InventoryOverlayType.WOLF;
			}
			else if (entityType.equals(EntityType.HAPPY_GHAST))
			{
				return InventoryOverlayType.HAPPY_GHAST;
			}
			else if (entityType.equals(EntityType.COPPER_GOLEM))
			{
				return InventoryOverlayType.COPPER_GOLEM;
			}
			else if (entityType.equals(EntityType.VILLAGER) ||
					entityType.equals(EntityType.ALLAY) ||
					entityType.equals(EntityType.PILLAGER) ||
					entityType.equals(EntityType.PIGLIN) ||
					entityType.equals(EntityType.WANDERING_TRADER) ||
					entityType.equals(EntityType.ZOMBIE_VILLAGER))
			{
				return InventoryOverlayType.VILLAGER;
			}
			else if (entityType.equals(EntityType.PLAYER))
			{
				return InventoryOverlayType.PLAYER;
			}
			else if (entityType.equals(EntityType.ARMOR_STAND))
			{
				return InventoryOverlayType.ARMOR_STAND;
			}
			else if (data.containsLenient(NbtKeys.ATTRIB) || data.containsLenient(NbtKeys.EFFECTS) || data.containsLenient(NbtKeys.FALL_FLYING))
			{
				return InventoryOverlayType.LIVING_ENTITY;
			}
		}

		return InventoryOverlayType.GENERIC;
	}

    /**
     * Two-Way match to try to get the Best Inventory Type based on the INV Object, or NBT Tags.
     * @param inv
     * @param nbt
     * @return
     */
    public static InventoryRenderType getBestInventoryType(@Nonnull Container inv, @Nonnull CompoundTag nbt)
    {
        InventoryRenderType i = getInventoryType(inv);
        InventoryRenderType n = getInventoryType(nbt);

        // Don't use the NBT value if the INV result is FIXED_54.
        if (i != n && i == InventoryRenderType.GENERIC)
        {
            return n;
        }

        return i;
    }

    /**
     * Three-Way match to try to get the Best Inventory Type based on the INV Object, NBT tags, or an Overlay Context.
     * @param inv
     * @param nbt
     * @param ctx
     * @return
     */
    public static InventoryRenderType getBestInventoryType(@Nullable Container inv, @Nonnull CompoundTag nbt, Context ctx)
    {
        InventoryRenderType i = getInventoryType(inv);
        InventoryRenderType n = getInventoryType(nbt);

        // Don't use the NBT value if the INV result is FIXED_54.
        if (i != n && i == InventoryRenderType.GENERIC)
        {
            if (n != ctx.type() && ctx.type() != InventoryRenderType.GENERIC)
            {
                return ctx.type();
            }

            return n;
        }

        return i;
    }

	/**
	 * Two-Way match to try to get the Best Inventory Type based on the INV Object, or NBT Tags.
	 * @param inv
	 * @param data
	 * @return
	 */
	@ApiStatus.Experimental
	public static InventoryOverlayType getBestInventoryTypeNew(@Nonnull Container inv, @Nonnull CompoundData data)
	{
		InventoryOverlayType i = getInventoryTypeNew(inv);
		InventoryOverlayType n = getInventoryTypeNew(data);

		// Don't use the NBT value if the INV result is FIXED_54.
		if (i != n && i == InventoryOverlayType.GENERIC)
		{
			return n;
		}

		return i;
	}

	/**
	 * Three-Way match to try to get the Best Inventory Type based on the INV Object, NBT tags, or an Overlay Context.
	 * @param inv
	 * @param data
	 * @param ctx
	 * @return
	 */
	@ApiStatus.Experimental
	public static InventoryOverlayType getBestInventoryTypeNew(@Nullable Container inv, @Nonnull CompoundData data, InventoryOverlayContext ctx)
	{
		InventoryOverlayType i = getInventoryTypeNew(inv);
		InventoryOverlayType n = getInventoryTypeNew(data);

		// Don't use the NBT value if the INV result is FIXED_54.
		if (i != n && i == InventoryOverlayType.GENERIC)
		{
			if (n != ctx.type() && ctx.type() != InventoryOverlayType.GENERIC)
			{
				return ctx.type();
			}

			return n;
		}

		return i;
	}

	/**
     * Returns the instance of the shared/temporary properties instance,
     * with the values set for the type of inventory provided.
     * Don't hold on to the instance, as the values will mutate when this
     * method is called again!
     * @param type ()
     * @param totalSlots ()
     * @return ()
     */
    public static InventoryProperties getInventoryPropsTemp(InventoryRenderType type, int totalSlots)
    {
        // Default slotsPerARow is only used for Bundles
        return getInventoryPropsTemp(type, totalSlots, 9);
    }

    /**
     * Returns the instance of the shared/temporary properties instance,
     * with the values set for the type of inventory provided.
     * Don't hold on to the instance, as the values will mutate when this
     * method is called again!
     * @param type ()
     * @param totalSlots ()
     * @param slotsPerARow ()
     * @return
     */
    public static InventoryProperties getInventoryPropsTemp(InventoryRenderType type, int totalSlots, int slotsPerARow)
    {
        INV_PROPS_TEMP.totalSlots = totalSlots;

        if (type == InventoryRenderType.FURNACE)
        {
            INV_PROPS_TEMP.slotsPerRow = 1;
            INV_PROPS_TEMP.slotOffsetX = 0;
            INV_PROPS_TEMP.slotOffsetY = 0;
            INV_PROPS_TEMP.width = 96;
            INV_PROPS_TEMP.height = 68;
        }
        else if (type == InventoryRenderType.BREWING_STAND)
        {
            INV_PROPS_TEMP.slotsPerRow = 9;
            INV_PROPS_TEMP.slotOffsetX = 0;
            INV_PROPS_TEMP.slotOffsetY = 0;
            //INV_PROPS_TEMP.width = 127;
            INV_PROPS_TEMP.width = 109;
            INV_PROPS_TEMP.height = 72;
        }
        else if (type == InventoryRenderType.CRAFTER || type == InventoryRenderType.DISPENSER)
        {
            INV_PROPS_TEMP.slotsPerRow = 3;
            INV_PROPS_TEMP.slotOffsetX = 8;
            INV_PROPS_TEMP.slotOffsetY = 8;
            INV_PROPS_TEMP.width = 68;
            INV_PROPS_TEMP.height = 68;
        }
        else if (type == InventoryRenderType.HORSE || type == InventoryRenderType.LLAMA || type == InventoryRenderType.WOLF || type == InventoryRenderType.COPPER_GOLEM)
        {
            INV_PROPS_TEMP.slotsPerRow = Math.max(1, totalSlots / 3);
            INV_PROPS_TEMP.slotOffsetX = 8;
            INV_PROPS_TEMP.slotOffsetY = 8;
            INV_PROPS_TEMP.width = totalSlots * 18 / 3 + 14;
            INV_PROPS_TEMP.height = 68;
        }
        else if (type == InventoryRenderType.HOPPER)
        {
            INV_PROPS_TEMP.slotsPerRow = 5;
            INV_PROPS_TEMP.slotOffsetX = 8;
            INV_PROPS_TEMP.slotOffsetY = 8;
            INV_PROPS_TEMP.width = 105;
            INV_PROPS_TEMP.height = 32;
        }
        else if (type == InventoryRenderType.VILLAGER)
        {
            INV_PROPS_TEMP.slotsPerRow = 2;
            INV_PROPS_TEMP.slotOffsetX = 8;
            INV_PROPS_TEMP.slotOffsetY = 8;
            INV_PROPS_TEMP.width = 50;
            INV_PROPS_TEMP.height = 86;
        }
        else if (type == InventoryRenderType.SINGLE_ITEM)
        {
            INV_PROPS_TEMP.slotsPerRow = 1;
            INV_PROPS_TEMP.slotOffsetX = 8;
            INV_PROPS_TEMP.slotOffsetY = 8;
            INV_PROPS_TEMP.width = 32;
            INV_PROPS_TEMP.height = 32;
        }
        else if (type == InventoryRenderType.BOOKSHELF)
        {
            INV_PROPS_TEMP.slotsPerRow = 3;
            INV_PROPS_TEMP.slotOffsetX = 8;
            INV_PROPS_TEMP.slotOffsetY = 8;
            INV_PROPS_TEMP.width = 68;
            INV_PROPS_TEMP.height = 50;
            INV_PROPS_TEMP.totalSlots = 6;
        }
        else if (type == InventoryRenderType.WALL_SHELF)
        {
            INV_PROPS_TEMP.slotsPerRow = 3;
            INV_PROPS_TEMP.slotOffsetX = 8;
            INV_PROPS_TEMP.slotOffsetY = 8;
            INV_PROPS_TEMP.width = 68;
            INV_PROPS_TEMP.height = 50;
            INV_PROPS_TEMP.totalSlots = 3;
        }
        else if (type == InventoryRenderType.BUNDLE)
        {
            INV_PROPS_TEMP.slotsPerRow = slotsPerARow != 9 ? MathUtils.clamp(slotsPerARow, 6, 9) : 9;
            INV_PROPS_TEMP.slotOffsetX = 8;
            INV_PROPS_TEMP.slotOffsetY = 8;
            int rows = (int) (Math.ceil((double) totalSlots / (double) INV_PROPS_TEMP.slotsPerRow));
            INV_PROPS_TEMP.width = Math.min(INV_PROPS_TEMP.slotsPerRow, totalSlots) * 18 + 14;
            INV_PROPS_TEMP.height = rows * 18 + 14;
            INV_PROPS_TEMP.totalSlots = rows * INV_PROPS_TEMP.slotsPerRow;
        }
        else
        {
            if (type == InventoryRenderType.FIXED_27 || type == InventoryRenderType.PLAYER || type == InventoryRenderType.ENDER_CHEST)
            {
                totalSlots = 27;
            }
            else if (type == InventoryRenderType.FIXED_54)
            {
                totalSlots = 54;
            }

            INV_PROPS_TEMP.slotsPerRow = 9;
            INV_PROPS_TEMP.slotOffsetX = 8;
            INV_PROPS_TEMP.slotOffsetY = 8;
            int rows = (int) (Math.ceil((double) totalSlots / (double) INV_PROPS_TEMP.slotsPerRow));
            INV_PROPS_TEMP.width = Math.min(INV_PROPS_TEMP.slotsPerRow, totalSlots) * 18 + 14;
            INV_PROPS_TEMP.height = rows * 18 + 14;
        }

        return INV_PROPS_TEMP;
    }

	/**
	 * Returns the instance of the shared/temporary properties instance,
	 * with the values set for the type of inventory provided.
	 * Don't hold on to the instance, as the values will mutate when this
	 * method is called again!
	 * @param type ()
	 * @param totalSlots ()
	 * @return ()
	 */
	@ApiStatus.Experimental
	public static InventoryProperties getInventoryPropsTempNew(InventoryOverlayType type, int totalSlots)
	{
		// Default slotsPerARow is only used for Bundles
		return getInventoryPropsTempNew(type, totalSlots, 9);
	}

	/**
	 * Returns the instance of the shared/temporary properties instance,
	 * with the values set for the type of inventory provided.
	 * Don't hold on to the instance, as the values will mutate when this
	 * method is called again!
	 * @param type ()
	 * @param totalSlots ()
	 * @param slotsPerARow ()
	 * @return
	 */
	@ApiStatus.Experimental
	public static InventoryProperties getInventoryPropsTempNew(InventoryOverlayType type, int totalSlots, int slotsPerARow)
	{
		INV_PROPS_TEMP.totalSlots = totalSlots;

		if (type == InventoryOverlayType.FURNACE)
		{
			INV_PROPS_TEMP.slotsPerRow = 1;
			INV_PROPS_TEMP.slotOffsetX = 0;
			INV_PROPS_TEMP.slotOffsetY = 0;
			INV_PROPS_TEMP.width = 96;
			INV_PROPS_TEMP.height = 68;
		}
		else if (type == InventoryOverlayType.BREWING_STAND)
		{
			INV_PROPS_TEMP.slotsPerRow = 9;
			INV_PROPS_TEMP.slotOffsetX = 0;
			INV_PROPS_TEMP.slotOffsetY = 0;
			//INV_PROPS_TEMP.width = 127;
			INV_PROPS_TEMP.width = 109;
			INV_PROPS_TEMP.height = 72;
		}
		else if (type == InventoryOverlayType.CRAFTER || type == InventoryOverlayType.DISPENSER)
		{
			INV_PROPS_TEMP.slotsPerRow = 3;
			INV_PROPS_TEMP.slotOffsetX = 8;
			INV_PROPS_TEMP.slotOffsetY = 8;
			INV_PROPS_TEMP.width = 68;
			INV_PROPS_TEMP.height = 68;
		}
		else if (type == InventoryOverlayType.HORSE || type == InventoryOverlayType.LLAMA || type == InventoryOverlayType.WOLF || type == InventoryOverlayType.COPPER_GOLEM)
		{
			INV_PROPS_TEMP.slotsPerRow = Math.max(1, totalSlots / 3);
			INV_PROPS_TEMP.slotOffsetX = 8;
			INV_PROPS_TEMP.slotOffsetY = 8;
			INV_PROPS_TEMP.width = totalSlots * 18 / 3 + 14;
			INV_PROPS_TEMP.height = 68;
		}
		else if (type == InventoryOverlayType.HOPPER)
		{
			INV_PROPS_TEMP.slotsPerRow = 5;
			INV_PROPS_TEMP.slotOffsetX = 8;
			INV_PROPS_TEMP.slotOffsetY = 8;
			INV_PROPS_TEMP.width = 105;
			INV_PROPS_TEMP.height = 32;
		}
		else if (type == InventoryOverlayType.VILLAGER)
		{
			INV_PROPS_TEMP.slotsPerRow = 2;
			INV_PROPS_TEMP.slotOffsetX = 8;
			INV_PROPS_TEMP.slotOffsetY = 8;
			INV_PROPS_TEMP.width = 50;
			INV_PROPS_TEMP.height = 86;
		}
		else if (type == InventoryOverlayType.SINGLE_ITEM)
		{
			INV_PROPS_TEMP.slotsPerRow = 1;
			INV_PROPS_TEMP.slotOffsetX = 8;
			INV_PROPS_TEMP.slotOffsetY = 8;
			INV_PROPS_TEMP.width = 32;
			INV_PROPS_TEMP.height = 32;
		}
		else if (type == InventoryOverlayType.BOOKSHELF)
		{
			INV_PROPS_TEMP.slotsPerRow = 3;
			INV_PROPS_TEMP.slotOffsetX = 8;
			INV_PROPS_TEMP.slotOffsetY = 8;
			INV_PROPS_TEMP.width = 68;
			INV_PROPS_TEMP.height = 50;
			INV_PROPS_TEMP.totalSlots = 6;
		}
		else if (type == InventoryOverlayType.WALL_SHELF)
		{
			INV_PROPS_TEMP.slotsPerRow = 3;
			INV_PROPS_TEMP.slotOffsetX = 8;
			INV_PROPS_TEMP.slotOffsetY = 8;
			INV_PROPS_TEMP.width = 68;
			INV_PROPS_TEMP.height = 50;
			INV_PROPS_TEMP.totalSlots = 3;
		}
		else if (type == InventoryOverlayType.BUNDLE)
		{
			INV_PROPS_TEMP.slotsPerRow = slotsPerARow != 9 ? MathUtils.clamp(slotsPerARow, 6, 9) : 9;
			INV_PROPS_TEMP.slotOffsetX = 8;
			INV_PROPS_TEMP.slotOffsetY = 8;
			int rows = (int) (Math.ceil((double) totalSlots / (double) INV_PROPS_TEMP.slotsPerRow));
			INV_PROPS_TEMP.width = Math.min(INV_PROPS_TEMP.slotsPerRow, totalSlots) * 18 + 14;
			INV_PROPS_TEMP.height = rows * 18 + 14;
			INV_PROPS_TEMP.totalSlots = rows * INV_PROPS_TEMP.slotsPerRow;
		}
		else
		{
			if (type == InventoryOverlayType.FIXED_27 || type == InventoryOverlayType.PLAYER || type == InventoryOverlayType.ENDER_CHEST)
			{
				totalSlots = 27;
			}
			else if (type == InventoryOverlayType.FIXED_54)
			{
				totalSlots = 54;
			}

			INV_PROPS_TEMP.slotsPerRow = 9;
			INV_PROPS_TEMP.slotOffsetX = 8;
			INV_PROPS_TEMP.slotOffsetY = 8;
			int rows = (int) (Math.ceil((double) totalSlots / (double) INV_PROPS_TEMP.slotsPerRow));
			INV_PROPS_TEMP.width = Math.min(INV_PROPS_TEMP.slotsPerRow, totalSlots) * 18 + 14;
			INV_PROPS_TEMP.height = rows * 18 + 14;
		}

		return INV_PROPS_TEMP;
	}

    public static void renderInventoryStacks(GuiGraphics drawContext, InventoryRenderType type, Container inv, int startX, int startY, int slotsPerRow, int startSlot, int maxSlots, Minecraft mc)
    {
        renderInventoryStacks(drawContext, type, inv, startX, startY, slotsPerRow, startSlot, maxSlots, Set.of(), mc, 0, 0);
    }

    /**
     * Supports lockable Crafter Slots
     * @param type
     * @param inv
     * @param startX
     * @param startY
     * @param slotsPerRow
     * @param startSlot
     * @param maxSlots
     * @param disabledSlots (Locked Crafter Slots as a numbered Set)
     * @param mc
     * @param drawContext
     */
    public static void renderInventoryStacks(GuiGraphics drawContext, InventoryRenderType type, Container inv, int startX, int startY, int slotsPerRow, int startSlot, int maxSlots, Set<Integer> disabledSlots, Minecraft mc)
    {
        renderInventoryStacks(drawContext, type, inv, startX, startY, slotsPerRow, startSlot, maxSlots, disabledSlots, mc, 0, 0);
    }

    public static void renderInventoryStacks(GuiGraphics drawContext, InventoryRenderType type, Container inv, int startX, int startY, int slotsPerRow, int startSlot, int maxSlots, Minecraft mc, double mouseX, double mouseY)
    {
        renderInventoryStacks(drawContext, type, inv, startX, startY, slotsPerRow, startSlot, maxSlots, Set.of(), mc, mouseX, mouseY);
    }

	@ApiStatus.Experimental
	public static void renderInventoryStacksNew(GuiGraphics drawContext, InventoryOverlayType type, Container inv, int startX, int startY, int slotsPerRow, int startSlot, int maxSlots, Minecraft mc)
	{
		renderInventoryStacksNew(drawContext, type, inv, startX, startY, slotsPerRow, startSlot, maxSlots, Set.of(), mc, 0, 0);
	}

	/**
	 * Supports lockable Crafter Slots
	 * @param type
	 * @param inv
	 * @param startX
	 * @param startY
	 * @param slotsPerRow
	 * @param startSlot
	 * @param maxSlots
	 * @param disabledSlots (Locked Crafter Slots as a numbered Set)
	 * @param mc
	 * @param drawContext
	 */
	@ApiStatus.Experimental
	public static void renderInventoryStacksNew(GuiGraphics drawContext, InventoryOverlayType type, Container inv, int startX, int startY, int slotsPerRow, int startSlot, int maxSlots, Set<Integer> disabledSlots, Minecraft mc)
	{
		renderInventoryStacksNew(drawContext, type, inv, startX, startY, slotsPerRow, startSlot, maxSlots, disabledSlots, mc, 0, 0);
	}

	@ApiStatus.Experimental
	public static void renderInventoryStacksNew(GuiGraphics drawContext, InventoryOverlayType type, Container inv, int startX, int startY, int slotsPerRow, int startSlot, int maxSlots, Minecraft mc, double mouseX, double mouseY)
	{
		renderInventoryStacksNew(drawContext, type, inv, startX, startY, slotsPerRow, startSlot, maxSlots, Set.of(), mc, mouseX, mouseY);
	}

	/**
     * Render the Inventory Stacks.  Now Supports Lockable Crafter Slots.
     *
     * @param type
     * @param inv
     * @param startX
     * @param startY
     * @param slotsPerRow
     * @param startSlot
     * @param maxSlots
     * @param disabledSlots  (Locked Crafter Slots as a numbered Set)
     * @param mc
     * @param drawContext
     * @param mouseX
     * @param mouseY
     */
    public static void renderInventoryStacks(GuiGraphics drawContext, InventoryRenderType type, Container inv, int startX, int startY, int slotsPerRow, int startSlot, int maxSlots, Set<Integer> disabledSlots, Minecraft mc, double mouseX, double mouseY)
    {
        if (inv == null)
        {
            // Only so this doesn't crash if inv was set to null
            inv = new SimpleContainer(maxSlots > 0 ? maxSlots : INV_PROPS_TEMP.totalSlots);
        }

        if (type == InventoryRenderType.FURNACE)
        {
            renderStackAt(drawContext, inv.getItem(0), startX + 8, startY + 8, 1, mc, mouseX, mouseY);
            renderStackAt(drawContext, inv.getItem(1), startX + 8, startY + 44, 1, mc, mouseX, mouseY);
            renderStackAt(drawContext, inv.getItem(2), startX + 68, startY + 26, 1, mc, mouseX, mouseY);
        }
        else if (type == InventoryRenderType.BREWING_STAND)
        {
            renderStackAt(drawContext, inv.getItem(0), startX + 47, startY + 42, 1, mc, mouseX, mouseY);
            renderStackAt(drawContext, inv.getItem(1), startX + 70, startY + 49, 1, mc, mouseX, mouseY);
            renderStackAt(drawContext, inv.getItem(2), startX + 93, startY + 42, 1, mc, mouseX, mouseY);
            renderStackAt(drawContext, inv.getItem(3), startX + 70, startY + 8, 1, mc, mouseX, mouseY);
            renderStackAt(drawContext, inv.getItem(4), startX + 8, startY + 8, 1, mc, mouseX, mouseY);
        }
        else
        {
            final int slots = inv.getContainerSize();
            int x = startX;
            int y = startY;

            if (maxSlots < 0)
            {
                maxSlots = slots;
            }

//            LOGGER.debug("renderInventoryStacks: slotsPerRow [{}], startSlot [{}], maxSlots [{}]", slotsPerRow, startSlot, maxSlots);

            for (int slot = startSlot, i = 0; slot < slots && i < maxSlots; )
            {
                for (int column = 0; column < slotsPerRow && slot < slots && i < maxSlots; ++column, ++slot, ++i)
                {
                    ItemStack stack = inv.getItem(slot).copy();

                    if (disabledSlots.contains(slot))
                    {
                        // Requires -1 offset, because locked texture is 18 x 18.
                        renderLockedSlotAt(drawContext, x - 1, y - 1, 1, mouseX, mouseY);
                    }
                    else if (!stack.isEmpty())
                    {
//                        LOGGER.debug("renderInventoryStacks: slot[{}/{}]: [{}]", slot, slots, stack.toString());
                        renderStackAt(drawContext, stack, x, y, 1, mc, mouseX, mouseY);
                    }

                    x += 18;
                }

                x = startX;
                y += 18;
            }
        }

        if (hoveredStack != null)
        {
            var stack = hoveredStack.copy();
            hoveredStack = null;
            // Some mixin / side effects can happen here
            //drawContext.drawItemTooltip(mc.textRenderer, stack, (int) mouseX, (int) mouseY);
            renderStackToolTipStyled(drawContext, (int) mouseX, (int) mouseY, stack, mc);
        }
    }

	/**
	 * Render the Inventory Stacks.  Now Supports Lockable Crafter Slots.
	 *
	 * @param type
	 * @param inv
	 * @param startX
	 * @param startY
	 * @param slotsPerRow
	 * @param startSlot
	 * @param maxSlots
	 * @param disabledSlots  (Locked Crafter Slots as a numbered Set)
	 * @param mc
	 * @param drawContext
	 * @param mouseX
	 * @param mouseY
	 */
	@ApiStatus.Experimental
	public static void renderInventoryStacksNew(GuiGraphics drawContext, InventoryOverlayType type, Container inv, int startX, int startY, int slotsPerRow, int startSlot, int maxSlots, Set<Integer> disabledSlots, Minecraft mc, double mouseX, double mouseY)
	{
		if (inv == null)
		{
			// Only so this doesn't crash if inv was set to null
			inv = new SimpleContainer(maxSlots > 0 ? maxSlots : INV_PROPS_TEMP.totalSlots);
		}

		if (type == InventoryOverlayType.FURNACE)
		{
			renderStackAt(drawContext, inv.getItem(0), startX + 8, startY + 8, 1, mc, mouseX, mouseY);
			renderStackAt(drawContext, inv.getItem(1), startX + 8, startY + 44, 1, mc, mouseX, mouseY);
			renderStackAt(drawContext, inv.getItem(2), startX + 68, startY + 26, 1, mc, mouseX, mouseY);
		}
		else if (type == InventoryOverlayType.BREWING_STAND)
		{
			renderStackAt(drawContext, inv.getItem(0), startX + 47, startY + 42, 1, mc, mouseX, mouseY);
			renderStackAt(drawContext, inv.getItem(1), startX + 70, startY + 49, 1, mc, mouseX, mouseY);
			renderStackAt(drawContext, inv.getItem(2), startX + 93, startY + 42, 1, mc, mouseX, mouseY);
			renderStackAt(drawContext, inv.getItem(3), startX + 70, startY + 8, 1, mc, mouseX, mouseY);
			renderStackAt(drawContext, inv.getItem(4), startX + 8, startY + 8, 1, mc, mouseX, mouseY);
		}
		else
		{
			final int slots = inv.getContainerSize();
			int x = startX;
			int y = startY;

			if (maxSlots < 0)
			{
				maxSlots = slots;
			}

//            LOGGER.debug("renderInventoryStacks: slotsPerRow [{}], startSlot [{}], maxSlots [{}]", slotsPerRow, startSlot, maxSlots);

			for (int slot = startSlot, i = 0; slot < slots && i < maxSlots; )
			{
				for (int column = 0; column < slotsPerRow && slot < slots && i < maxSlots; ++column, ++slot, ++i)
				{
					ItemStack stack = inv.getItem(slot).copy();

					if (disabledSlots.contains(slot))
					{
						// Requires -1 offset, because locked texture is 18 x 18.
						renderLockedSlotAt(drawContext, x - 1, y - 1, 1, mouseX, mouseY);
					}
					else if (!stack.isEmpty())
					{
//                        LOGGER.debug("renderInventoryStacks: slot[{}/{}]: [{}]", slot, slots, stack.toString());
						renderStackAt(drawContext, stack, x, y, 1, mc, mouseX, mouseY);
					}

					x += 18;
				}

				x = startX;
				y += 18;
			}
		}

		if (hoveredStack != null)
		{
			var stack = hoveredStack.copy();
			hoveredStack = null;
			// Some mixin / side effects can happen here
			//drawContext.drawItemTooltip(mc.textRenderer, stack, (int) mouseX, (int) mouseY);
			renderStackToolTipStyled(drawContext, (int) mouseX, (int) mouseY, stack, mc);
		}
	}

	public static void renderEquipmentStacks(GuiGraphics drawContext, LivingEntity entity, int x, int y, Minecraft mc)
    {
        renderEquipmentStacks(drawContext, entity, x, y, mc, 0, 0);
    }

    public static void renderEquipmentStacks(GuiGraphics drawContext, LivingEntity entity, int x, int y, Minecraft mc, double mouseX, double mouseY)
    {
        for (int i = 0, xOff = 7, yOff = 7; i < 4; ++i, yOff += 18)
        {
            final EquipmentSlot eqSlot = VALID_EQUIPMENT_SLOTS[i];
            ItemStack stack = entity.getItemBySlot(eqSlot);

            if (stack.isEmpty() == false)
            {
                renderStackAt(drawContext, stack.copy(), x + xOff + 1, y + yOff + 1, 1, mc, mouseX, mouseY);
            }
        }

        ItemStack stack = entity.getItemBySlot(EquipmentSlot.MAINHAND);

        if (stack.isEmpty() == false)
        {
            renderStackAt(drawContext, stack.copy(), x + 28, y + 2 * 18 + 7 + 1, 1, mc, mouseX, mouseY);
        }

        stack = entity.getItemBySlot(EquipmentSlot.OFFHAND);

        if (stack.isEmpty() == false)
        {
            renderStackAt(drawContext, stack.copy(), x + 28, y + 3 * 18 + 7 + 1, 1, mc, mouseX, mouseY);
        }

        if (hoveredStack != null)
        {
            stack = hoveredStack.copy();
            hoveredStack = null;
            // Some mixin / side effects can happen here, so reset hoveredStack
            //drawContext.drawItemTooltip(mc.textRenderer, stack, (int) mouseX, (int) mouseY);
            renderStackToolTipStyled(drawContext, (int) mouseX, (int) mouseY, stack, mc);
        }
    }

    public static void renderItemStacks(GuiGraphics drawContext, NonNullList<ItemStack> items, int startX, int startY, int slotsPerRow, int startSlot, int maxSlots, Minecraft mc)
    {
        renderItemStacks(drawContext, items, startX, startY, slotsPerRow, startSlot, maxSlots, Set.of(), mc);
    }

    /**
     * Renders an ItemList.  Now supports Lockable Crafter Slots.
     *
     * @param items
     * @param startX
     * @param startY
     * @param slotsPerRow
     * @param startSlot
     * @param maxSlots
     * @param disabledSlots  (Locked Crafter Slots as a numbered Set)
     * @param mc
     * @param drawContext
     */
    public static void renderItemStacks(GuiGraphics drawContext, NonNullList<ItemStack> items, int startX, int startY, int slotsPerRow, int startSlot, int maxSlots, Set<Integer> disabledSlots, Minecraft mc)
    {
        final int slots = items.size();
        int x = startX;
        int y = startY;

        if (maxSlots < 0)
        {
            maxSlots = slots;
        }

        for (int slot = startSlot, i = 0; slot < slots && i < maxSlots;)
        {
            for (int column = 0; column < slotsPerRow && slot < slots && i < maxSlots; ++column, ++slot, ++i)
            {
                ItemStack stack = items.get(slot).copy();

                if (disabledSlots.contains(slot))
                {
                    // Requires -1 offset, because locked texture is 18 x 18.
                    renderLockedSlotAt(drawContext, x - 1, y - 1, 1, 0, 0);
                }
                else if (!stack.isEmpty())
                {
                    renderStackAt(drawContext, stack, x, y, 1, mc);
                }

                x += 18;
            }

            x = startX;
            y += 18;
        }
    }

    public static void renderStackAt(GuiGraphics drawContext, ItemStack stack, float x, float y, float scale, Minecraft mc)
    {
        renderStackAt(drawContext, stack, x, y, scale, mc, 0, 0);
    }

    public static void renderStackAt(GuiGraphics drawContext, ItemStack stack, float x, float y, float scale, Minecraft mc, double mouseX, double mouseY)
    {
        Matrix3x2fStack matrixStack = drawContext.pose();

        matrixStack.pushMatrix();
        matrixStack.translate(x, y);
        matrixStack.scale(scale, scale);
        drawContext.renderItem(stack.copy(), 0, 0);
        drawContext.renderItemDecorations(mc.font, stack.copy(), 0, 0);
        matrixStack.popMatrix();

        if (mouseX >= x && mouseX < x + 16 * scale && mouseY >= y && mouseY < y + 16 * scale)
        {
            hoveredStack = stack.copy();
        }
    }

    /**
     * Render's a locked Crafter Slot at the specified location.
     *
     * @param x
     * @param y
     * @param scale
     * @param drawContext
     * @param mouseX
     * @param mouseY
     */
    public static void renderLockedSlotAt(GuiGraphics drawContext, float x, float y, float scale, double mouseX, double mouseY)
    {
        Matrix3x2fStack matrixStack = drawContext.pose();
        int color = -1;

        matrixStack.pushMatrix();
        matrixStack.translate(x, y);
        matrixStack.scale(scale, scale);
        color = CommonColors.WHITE;
        drawContext.blitSprite(RenderPipelines.GUI_TEXTURED, TEXTURE_LOCKED_SLOT, 0, 0, 18, 18, color);
        matrixStack.popMatrix();

        if (mouseX >= x && mouseX < x + 16 * scale && mouseY >= y && mouseY < y + 16 * scale)
        {
            hoveredStack = null;
        }
    }

    public static void renderBackgroundSlotAt(GuiGraphics drawContext, ResourceLocation texture, float x, float y)
    {
        renderBackgroundSlotAt(drawContext, texture, x, y, 0.9f, 0, 0);
    }

    public static void renderBackgroundSlotAt(GuiGraphics drawContext, ResourceLocation texture, float x, float y, float scale, double mouseX, double mouseY)
    {
        Matrix3x2fStack matrixStack = drawContext.pose();
        int color = -1;

        matrixStack.pushMatrix();
        matrixStack.translate(x, y);
        matrixStack.scale(scale, scale);
        color = CommonColors.WHITE;
        drawContext.blitSprite(RenderPipelines.GUI_TEXTURED, texture, 0, 0, 18, 18, color);
        matrixStack.popMatrix();

        if (mouseX >= x && mouseX < x + 16 * scale && mouseY >= y && mouseY < y + 16 * scale)
        {
            hoveredStack = null;
        }
    }

    /**
     * This is a more "basic" hover tooltip
     * @param x
     * @param y
     * @param stack
     * @param mc
     * @param drawContext
     */
    public static void renderStackToolTip(GuiGraphics drawContext, int x, int y, ItemStack stack, Minecraft mc)
    {
        List<Component> list = stack.getTooltipLines(Item.TooltipContext.of(mc.level), mc.player, mc.options.advancedItemTooltips ? TooltipFlag.ADVANCED : TooltipFlag.NORMAL);
        List<String> lines = new ArrayList<>();

//        if (MaLiLibReference.DEBUG_MODE)
//        {
//            dumpStack(stack, list);
//        }
        for (int i = 0; i < list.size(); ++i)
        {
            if (i == 0)
            {
                lines.add(stack.getRarity().color() + list.get(i).getString());
            }
            else
            {
                lines.add(GuiBase.TXT_DARK_GRAY + list.get(i).getString());
            }
        }

        RenderUtils.drawHoverText(drawContext, x, y, lines);
    }

    /**
     * This is a more Advanced version, with full Color Style, etc; just like Vanilla's display.
     * This should even be able to display the Bundle pop up interface.
     * @param x
     * @param y
     * @param stack
     * @param mc
     * @param drawContext
     */
    public static void renderStackToolTipStyled(GuiGraphics drawContext, int x, int y, ItemStack stack, Minecraft mc)
    {
        if (stack.isEmpty() == false && mc.level != null && mc.player != null)
        {
            // Not sure why getBestWorld() is required here,
            // it's also required when connected to a server;
            // or else not be able to see Enchantment tooltips. (>.>)
            List<Component> toolTips = stack.getTooltipLines(Item.TooltipContext.of(WorldUtils.getBestWorld(mc)), mc.player, mc.options.advancedItemTooltips ? TooltipFlag.ADVANCED : TooltipFlag.NORMAL);
//            if (MaLiLibReference.DEBUG_MODE)
//            {
//                dumpStack(stack, toolTips);
//            }
            drawContext.setTooltipForNextFrame(mc.font,
                                    toolTips,
                                    stack.getTooltipImage(), // Bundle/Optional Data
                                    x, y,
                                    stack.get(DataComponents.TOOLTIP_STYLE));

            // Extra Hook for this tooltip style
            ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderTooltipLast(drawContext, stack, x, y);
        }
    }

    private static void dumpStack(ItemStack stack, @Nullable List<Component> list)
    {
        if (stack.isEmpty())
        {
            LOGGER.info("dumpStack(): [{}]", ItemStack.EMPTY.toString());
            return;
        }

        LOGGER.info("dumpStack(): [{}}]", ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, stack).getPartialOrThrow());

        if (list != null && !list.isEmpty())
        {
            int i = 0;

            for (Component entry : list)
            {
                LOGGER.info("ToolTip[{}]: {}", i, entry.getString());
                i++;
            }
        }
    }

    public static class InventoryProperties
    {
        public int totalSlots = 1;
        public int width = 176;
        public int height = 83;
        public int slotsPerRow = 9;
        public int slotOffsetX = 8;
        public int slotOffsetY = 8;
    }

	@Deprecated
    public enum InventoryRenderType
    {
        BREWING_STAND,
        CRAFTER,
        DISPENSER,
        FURNACE,
        HOPPER,
        HORSE,
        LLAMA,
        WOLF,
        HAPPY_GHAST,
		COPPER_GOLEM,
        FIXED_27,
        FIXED_54,
        VILLAGER,
        PLAYER,
        ENDER_CHEST,
        BOOKSHELF,
        WALL_SHELF,
        SINGLE_ITEM,
        BUNDLE,
        ARMOR_STAND,
        LIVING_ENTITY,
        GENERIC;
    }

   /**
     * New InventoryOverlay Context interface.
     *
     * @param type
     * @param inv
     * @param be
     * @param entity
     * @param nbt
     * @param handler
     */
    @Deprecated
    public record Context(InventoryRenderType type, @Nullable Container inv, @Nullable BlockEntity be, @Nullable LivingEntity entity, @Nullable CompoundTag nbt, Refresher handler) {}

	@Deprecated
    public interface Refresher
    {
        Context onContextRefresh(Context data, Level world);
    }
}
