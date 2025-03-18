package fi.dy.masa.malilib.render;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Matrix4f;

import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.vehicle.AbstractChestBoatEntity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BundleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.event.RenderEventHandler;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.util.MathUtils;
import fi.dy.masa.malilib.util.WorldUtils;
import fi.dy.masa.malilib.util.game.IEntityOwnedInventory;
import fi.dy.masa.malilib.util.game.wrap.GameWrap;
import fi.dy.masa.malilib.util.nbt.NbtBlockUtils;
import fi.dy.masa.malilib.util.nbt.NbtEntityUtils;
import fi.dy.masa.malilib.util.nbt.NbtKeys;

public class InventoryOverlay
{
    public static final Identifier TEXTURE_BREWING_STAND    = Identifier.ofVanilla("textures/gui/container/brewing_stand.png");
    public static final Identifier TEXTURE_CRAFTER          = Identifier.ofVanilla("textures/gui/container/crafter.png");
    public static final Identifier TEXTURE_DISPENSER        = Identifier.ofVanilla("textures/gui/container/dispenser.png");
    public static final Identifier TEXTURE_DOUBLE_CHEST     = Identifier.ofVanilla("textures/gui/container/generic_54.png");
    public static final Identifier TEXTURE_FURNACE          = Identifier.ofVanilla("textures/gui/container/furnace.png");
    public static final Identifier TEXTURE_HOPPER           = Identifier.ofVanilla("textures/gui/container/hopper.png");
    public static final Identifier TEXTURE_PLAYER_INV       = Identifier.ofVanilla("textures/gui/container/inventory.png");
    public static final Identifier TEXTURE_SINGLE_CHEST     = Identifier.ofVanilla("textures/gui/container/shulker_box.png");

    // 1.21.3-
    //public static final Identifier TEXTURE_EMPTY_SHIELD     = Identifier.ofVanilla("item/empty_armor_slot_shield");
    // 1.21.4+
    public static final Identifier TEXTURE_EMPTY_SHIELD     = Identifier.ofVanilla("container/slot/shield");
    public static final Identifier TEXTURE_LOCKED_SLOT      = Identifier.ofVanilla("container/crafter/disabled_slot");

    // Additional Empty Slot Textures
    public static final Identifier TEXTURE_EMPTY_HORSE_ARMOR = Identifier.ofVanilla("container/slot/horse_armor");
    public static final Identifier TEXTURE_EMPTY_LLAMA_ARMOR = Identifier.ofVanilla("container/slot/llama_armor");
    public static final Identifier TEXTURE_EMPTY_SADDLE      = Identifier.ofVanilla("container/slot/saddle");
    // Brewer Slots (1.21.4+)
    public static final Identifier TEXTURE_EMPTY_BREWER_FUEL = Identifier.ofVanilla("container/slot/brewing_fuel");
    public static final Identifier TEXTURE_EMPTY_POTION      = Identifier.ofVanilla("container/slot/potion");
    // Other Misc Empty Slots (1.21.4+)
    public static final Identifier TEXTURE_EMPTY_SLOT_AMETHYST   = Identifier.ofVanilla("container/slot/amethyst_shard");
    public static final Identifier TEXTURE_EMPTY_SLOT_AXE        = Identifier.ofVanilla("container/slot/axe");
    public static final Identifier TEXTURE_EMPTY_SLOT_BANNER     = Identifier.ofVanilla("container/slot/banner");
    public static final Identifier TEXTURE_EMPTY_SLOT_PATTERN    = Identifier.ofVanilla("container/slot/banner_pattern");
    public static final Identifier TEXTURE_EMPTY_SLOT_DIAMOND    = Identifier.ofVanilla("container/slot/diamond");
    public static final Identifier TEXTURE_EMPTY_SLOT_DYE        = Identifier.ofVanilla("container/slot/dye");
    public static final Identifier TEXTURE_EMPTY_SLOT_EMERALD    = Identifier.ofVanilla("container/slot/emerald");
    public static final Identifier TEXTURE_EMPTY_SLOT_HOE        = Identifier.ofVanilla("container/slot/hoe");
    public static final Identifier TEXTURE_EMPTY_SLOT_INGOT      = Identifier.ofVanilla("container/slot/ingot");
    public static final Identifier TEXTURE_EMPTY_SLOT_LAPIS      = Identifier.ofVanilla("container/slot/lapis_lazuli");
    public static final Identifier TEXTURE_EMPTY_SLOT_PICKAXE    = Identifier.ofVanilla("container/slot/pickaxe");
    public static final Identifier TEXTURE_EMPTY_SLOT_QUARTZ     = Identifier.ofVanilla("container/slot/quartz");
    public static final Identifier TEXTURE_EMPTY_SLOT_REDSTONE   = Identifier.ofVanilla("container/slot/redstone_dust");
    public static final Identifier TEXTURE_EMPTY_SLOT_SHOVEL     = Identifier.ofVanilla("container/slot/shovel");
    public static final Identifier TEXTURE_EMPTY_SLOT_ARMOR_TRIM = Identifier.ofVanilla("container/slot/smithing_template_armor_trim");
    public static final Identifier TEXTURE_EMPTY_SLOT_UPGRADE    = Identifier.ofVanilla("container/slot/smithing_template_netherite_upgrade");
    public static final Identifier TEXTURE_EMPTY_SLOT_SWORD      = Identifier.ofVanilla("container/slot/sword");

    private static final EquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EquipmentSlot[] { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET };
    public static final InventoryProperties INV_PROPS_TEMP = new InventoryProperties();

    private static final Identifier[] EMPTY_SLOT_TEXTURES = new Identifier[]
    {
            /* 1.21.3-
        Identifier.ofVanilla("item/empty_armor_slot_boots"),
        Identifier.ofVanilla("item/empty_armor_slot_leggings"),
        Identifier.ofVanilla("item/empty_armor_slot_chestplate"),
        Identifier.ofVanilla("item/empty_armor_slot_helmet")
             */
        // 1.21.4+
        Identifier.ofVanilla("container/slot/boots"),
        Identifier.ofVanilla("container/slot/leggings"),
        Identifier.ofVanilla("container/slot/chestplate"),
        Identifier.ofVanilla("container/slot/helmet")
    };

    private static ItemStack hoveredStack = null;

    public static void renderInventoryBackground(InventoryRenderType type, int x, int y, int slotsPerRow, int totalSlots, MinecraftClient mc, DrawContext context)
    {
        renderInventoryBackground(type, x, y, slotsPerRow, totalSlots, -1, mc, context);
    }

    public static void renderInventoryBackground(InventoryRenderType type, int x, int y, int slotsPerRow, int totalSlots, int color, MinecraftClient mc, DrawContext context)
    {
        VertexConsumer buffer;
        Matrix4f posMatrix;
        //RenderUtils.blend(true);
        // ShaderPipelines.POSITION_TEX_PANORAMA
        /*
        RenderContext ctx = new RenderContext(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR, ShaderPipelines.GUI_TEXTURED);
        BufferBuilder buffer = ctx.getBuilder();
         */
        
        if (type == InventoryRenderType.FURNACE)
        {
            buffer = RenderUtils.bindGuiTexture(TEXTURE_FURNACE, context);
            posMatrix = context.getMatrices().peek().getPositionMatrix();
            
            RenderUtils.drawTexturedRectBatched(posMatrix, x     , y     ,   0,   0,   4,  64, buffer); // left (top)
            RenderUtils.drawTexturedRectBatched(posMatrix, x +  4, y     ,  84,   0,  92,   4, buffer); // top (right)
            RenderUtils.drawTexturedRectBatched(posMatrix, x     , y + 64,   0, 162,  92,   4, buffer); // bottom (left)
            RenderUtils.drawTexturedRectBatched(posMatrix, x + 92, y +  4, 172, 102,   4,  64, buffer); // right (bottom)
            RenderUtils.drawTexturedRectBatched(posMatrix, x +  4, y +  4,  52,  13,  88,  60, buffer); // middle
        }
        else if (type == InventoryRenderType.BREWING_STAND)
        {
            buffer = RenderUtils.bindGuiTexture(TEXTURE_BREWING_STAND, context);
            posMatrix = context.getMatrices().peek().getPositionMatrix();
            
            RenderUtils.drawTexturedRectBatched(posMatrix, x      , y     ,   0,   0,   4,  68, buffer); // left (top)
            RenderUtils.drawTexturedRectBatched(posMatrix, x +   4, y     ,  63,   0, 113,   4, buffer); // top (right)
            RenderUtils.drawTexturedRectBatched(posMatrix, x      , y + 68,   0, 162, 113,   4, buffer); // bottom (left)
            RenderUtils.drawTexturedRectBatched(posMatrix, x + 113, y +  4, 172,  98,   4,  68, buffer); // right (bottom)
            RenderUtils.drawTexturedRectBatched(posMatrix, x +   4, y +  4,  13,  13, 109,  64, buffer); // middle
        }
        else if (type == InventoryRenderType.CRAFTER)
        {
            // We just hack in the Dispenser Texture, so it displays right.  Easy.
            buffer = RenderUtils.bindGuiTexture(TEXTURE_DISPENSER, context);
            posMatrix = context.getMatrices().peek().getPositionMatrix();

            RenderUtils.drawTexturedRectBatched(posMatrix, x     , y     ,   0,   0,   7,  61, buffer); // left (top)
            RenderUtils.drawTexturedRectBatched(posMatrix, x +  7, y     , 115,   0,  61,   7, buffer); // top (right)
            RenderUtils.drawTexturedRectBatched(posMatrix, x     , y + 61,   0, 159,  61,   7, buffer); // bottom (left)
            RenderUtils.drawTexturedRectBatched(posMatrix, x + 61, y +  7, 169, 105,   7,  61, buffer); // right (bottom)
            RenderUtils.drawTexturedRectBatched(posMatrix, x +  7, y +  7,  61,  16,  54,  54, buffer); // middle
        }
        else if (type == InventoryRenderType.DISPENSER)
        {
            buffer = RenderUtils.bindGuiTexture(TEXTURE_DISPENSER, context);
            posMatrix = context.getMatrices().peek().getPositionMatrix();

            RenderUtils.drawTexturedRectBatched(posMatrix, x     , y     ,   0,   0,   7,  61, buffer); // left (top)
            RenderUtils.drawTexturedRectBatched(posMatrix, x +  7, y     , 115,   0,  61,   7, buffer); // top (right)
            RenderUtils.drawTexturedRectBatched(posMatrix, x     , y + 61,   0, 159,  61,   7, buffer); // bottom (left)
            RenderUtils.drawTexturedRectBatched(posMatrix, x + 61, y +  7, 169, 105,   7,  61, buffer); // right (bottom)
            RenderUtils.drawTexturedRectBatched(posMatrix, x +  7, y +  7,  61,  16,  54,  54, buffer); // middle
        }
        else if (type == InventoryRenderType.HOPPER)
        {
            buffer = RenderUtils.bindGuiTexture(TEXTURE_HOPPER, context);
            posMatrix = context.getMatrices().peek().getPositionMatrix();

            RenderUtils.drawTexturedRectBatched(posMatrix, x      , y     ,   0,   0,   7,  25, buffer); // left (top)
            RenderUtils.drawTexturedRectBatched(posMatrix, x +   7, y     ,  79,   0,  97,   7, buffer); // top (right)
            RenderUtils.drawTexturedRectBatched(posMatrix, x      , y + 25,   0, 126,  97,   7, buffer); // bottom (left)
            RenderUtils.drawTexturedRectBatched(posMatrix, x +  97, y +  7, 169, 108,   7,  25, buffer); // right (bottom)
            RenderUtils.drawTexturedRectBatched(posMatrix, x +   7, y +  7,  43,  19,  90,  18, buffer); // middle
        }
        // Most likely a Villager, or possibly a Llama
        else if (type == InventoryRenderType.VILLAGER)
        {
            buffer = RenderUtils.bindGuiTexture(TEXTURE_DOUBLE_CHEST, context);
            posMatrix = context.getMatrices().peek().getPositionMatrix();

            RenderUtils.drawTexturedRectBatched(posMatrix, x     , y     ,   0,   0,   7,  79, color, buffer); // left (top)
            RenderUtils.drawTexturedRectBatched(posMatrix, x +  7, y     , 133,   0,  43,   7, color, buffer); // top (right)
            RenderUtils.drawTexturedRectBatched(posMatrix, x     , y + 79,   0, 215,  43,   7, color, buffer); // bottom (left)
            RenderUtils.drawTexturedRectBatched(posMatrix, x + 43, y +  7, 169, 143,   7,  79, color, buffer); // right (bottom)
            RenderUtils.drawTexturedRectBatched(posMatrix, x +  7, y +  7,   7,  17,  36,  72, color, buffer); // 2x4 slots
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
            buffer = RenderUtils.bindGuiTexture(TEXTURE_DOUBLE_CHEST, context);
            posMatrix = context.getMatrices().peek().getPositionMatrix();

            // Draw the slot backgrounds according to how many slots there actually are
            int rows = (int) (Math.ceil((double) totalSlots / (double) slotsPerRow));
            int bgw = Math.min(totalSlots, slotsPerRow) * 18 + 7;
            int bgh = rows * 18 + 7;

            RenderUtils.drawTexturedRectBatched(posMatrix, x      , y      ,         0,         0,   7, bgh, color, buffer); // left (top)
            RenderUtils.drawTexturedRectBatched(posMatrix, x +   7, y      , 176 - bgw,         0, bgw,   7, color, buffer); // top (right)
            RenderUtils.drawTexturedRectBatched(posMatrix, x      , y + bgh,         0,       215, bgw,   7, color, buffer); // bottom (left)
            RenderUtils.drawTexturedRectBatched(posMatrix, x + bgw, y +   7,       169, 222 - bgh,   7, bgh, color, buffer); // right (bottom)

            for (int row = 0; row < rows; row++)
            {
                int rowLen = MathHelper.clamp(totalSlots - (row * slotsPerRow), 1, slotsPerRow);
                RenderUtils.drawTexturedRectBatched(posMatrix, x + 7, y + row * 18 + 7, 7, 17, rowLen * 18, 18, color, buffer);

                // Render the background for the last non-existing slots on the last row,
                // in two strips of the background texture from the double chest texture's top part.
                if (rows > 1 && rowLen < slotsPerRow)
                {
                    RenderUtils.drawTexturedRectBatched(posMatrix, x + rowLen * 18 + 7, y + row * 18 +  7, 7, 3, (slotsPerRow - rowLen) * 18, 9, color, buffer);
                    RenderUtils.drawTexturedRectBatched(posMatrix, x + rowLen * 18 + 7, y + row * 18 + 16, 7, 3, (slotsPerRow - rowLen) * 18, 9, color, buffer);
                }
            }
        }

        //RenderUtils.depthTest(true); X
        //RenderUtils.blend(true);

        /*
        try
        {
            ctx.draw(buffer.end());
            ctx.close();
        }
        catch (Exception ignored) { }
         */
    }

    public static void renderInventoryBackground27(int x, int y, int color, MinecraftClient mc, DrawContext context)
    {
        VertexConsumer buffer = RenderUtils.bindGuiTexture(TEXTURE_SINGLE_CHEST, context);
        Matrix4f posMatrix = context.getMatrices().peek().getPositionMatrix();

        RenderUtils.drawTexturedRectBatched(posMatrix, x      , y     ,   0,   0,   7,  61, color, buffer); // left (top)
        RenderUtils.drawTexturedRectBatched(posMatrix, x +   7, y     ,   7,   0, 169,   7, color, buffer); // top (right)
        RenderUtils.drawTexturedRectBatched(posMatrix, x      , y + 61,   0, 159, 169,   7, color, buffer); // bottom (left)
        RenderUtils.drawTexturedRectBatched(posMatrix, x + 169, y +  7, 169, 105,   7,  61, color, buffer); // right (bottom)
        RenderUtils.drawTexturedRectBatched(posMatrix, x +   7, y +  7,   7,  17, 162,  54, color, buffer); // middle
    }

    public static void renderInventoryBackground54(int x, int y, int color, MinecraftClient mc, DrawContext context)
    {
        VertexConsumer buffer = RenderUtils.bindGuiTexture(TEXTURE_DOUBLE_CHEST, context);
        Matrix4f posMatrix = context.getMatrices().peek().getPositionMatrix();

        RenderUtils.drawTexturedRectBatched(posMatrix, x      , y      ,   0,   0,   7, 115, color, buffer); // left (top)
        RenderUtils.drawTexturedRectBatched(posMatrix, x +   7, y      ,   7,   0, 169,   7, color, buffer); // top (right)
        RenderUtils.drawTexturedRectBatched(posMatrix, x      , y + 115,   0, 215, 169,   7, color, buffer); // bottom (left)
        RenderUtils.drawTexturedRectBatched(posMatrix, x + 169, y +   7, 169, 107,   7, 115, color, buffer); // right (bottom)
        RenderUtils.drawTexturedRectBatched(posMatrix, x +   7, y +   7,   7,  17, 162, 108, color, buffer); // middle
    }

    public static void renderInventoryBackgroundSlots(InventoryRenderType type, Inventory inv, int x, int y, DrawContext drawContext)
    {
        if (type == InventoryRenderType.BREWING_STAND)
        {
            renderBrewerBackgroundSlots(inv, x, y, drawContext);
        }
        else if (type == InventoryRenderType.HORSE)
        {
            renderHorseArmorBackgroundSlots(inv, x, y, drawContext);
        }
        else if (type == InventoryRenderType.LLAMA)
        {
            renderLlamaArmorBackgroundSlots(inv, x, y, drawContext);
        }
        else if (type == InventoryRenderType.WOLF)
        {
            renderWolfArmorBackgroundSlots(inv, x, y, drawContext);
        }
    }

    public static void renderBrewerBackgroundSlots(Inventory inv, int x, int y, DrawContext drawContext)
    {
        renderBrewerBackgroundSlots(inv, x, y, 0.9f, drawContext, 0, 0);
    }

    public static void renderBrewerBackgroundSlots(Inventory inv, int x, int y, float scale, DrawContext drawContext, double mouseX, double mouseY)
    {
        if (inv.getStack(0).isEmpty())
        {
            renderBackgroundSlotAt(x + 47, y + 42, scale, TEXTURE_EMPTY_POTION, drawContext, mouseX, mouseY);
        }
        if (inv.getStack(1).isEmpty())
        {
            renderBackgroundSlotAt(x + 70, y + 49, scale, TEXTURE_EMPTY_POTION, drawContext, mouseX, mouseY);
        }
        if (inv.getStack(2).isEmpty())
        {
            renderBackgroundSlotAt(x + 93, y + 42, scale, TEXTURE_EMPTY_POTION, drawContext, mouseX, mouseY);
        }
        if (inv.getStack(4).isEmpty())
        {
            renderBackgroundSlotAt(x + 8, y + 8, scale, TEXTURE_EMPTY_BREWER_FUEL, drawContext, mouseX, mouseY);
        }
    }

    public static void renderHorseArmorBackgroundSlots(Inventory inv, int x, int y, DrawContext drawContext)
    {
        renderHorseArmorBackgroundSlots(inv, x, y, 0.9f, drawContext, 0, 0);
    }

    public static void renderHorseArmorBackgroundSlots(Inventory inv, int x, int y, float scale, DrawContext drawContext, double mouseX, double mouseY)
    {
        if (inv.getStack(0).isEmpty())
        {
            renderBackgroundSlotAt(x, y, scale, TEXTURE_EMPTY_HORSE_ARMOR, drawContext, mouseX, mouseY);
        }

        if (inv.getStack(1).isEmpty())
        {
            renderBackgroundSlotAt(x, y + 18, scale, TEXTURE_EMPTY_SADDLE, drawContext, mouseX, mouseY);
        }
    }

    public static void renderLlamaArmorBackgroundSlots(Inventory inv, int x, int y, DrawContext drawContext)
    {
        renderLlamaArmorBackgroundSlots(inv, x, y, 0.9f, drawContext, 0, 0);
    }

    public static void renderLlamaArmorBackgroundSlots(Inventory inv, int x, int y, float scale, DrawContext drawContext, double mouseX, double mouseY)
    {
        if (inv.getStack(0).isEmpty())
        {
            renderBackgroundSlotAt(x, y, scale, TEXTURE_EMPTY_LLAMA_ARMOR, drawContext, mouseX, mouseY);
        }
    }

    public static void renderWolfArmorBackgroundSlots(Inventory inv, int x, int y, DrawContext drawContext)
    {
        renderWolfArmorBackgroundSlots(inv, x, y, 0.9f, drawContext, 0, 0);
    }

    public static void renderWolfArmorBackgroundSlots(Inventory inv, int x, int y, float scale, DrawContext drawContext, double mouseX, double mouseY)
    {
        if (inv.getStack(0).isEmpty())
        {
            renderBackgroundSlotAt(x, y, scale, TEXTURE_EMPTY_HORSE_ARMOR, drawContext, mouseX, mouseY);
        }
    }

    public static void renderEquipmentOverlayBackground(int x, int y, LivingEntity entity, DrawContext drawContext)
    {
        RenderUtils.color(1f, 1f, 1f, 1f);
        /*
        RenderContext ctx = new RenderContext(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE, ShaderPipelines.POSITION_TEX_PANORAMA);
        BufferBuilder buffer = ctx.getBuilder();
         */

        VertexConsumer buffer = RenderUtils.bindGuiTexture(TEXTURE_DISPENSER, drawContext);
        Matrix4f posMatrix = drawContext.getMatrices().peek().getPositionMatrix();

        RenderUtils.drawTexturedRectBatched(posMatrix, x     , y     ,   0,   0, 50, 83, buffer); // top-left (main part)
        RenderUtils.drawTexturedRectBatched(posMatrix, x + 50, y     , 173,   0,  3, 83, buffer); // right edge top
        RenderUtils.drawTexturedRectBatched(posMatrix, x     , y + 83,   0, 163, 50,  3, buffer); // bottom edge left
        RenderUtils.drawTexturedRectBatched(posMatrix, x + 50, y + 83, 173, 163,  3,  3, buffer); // bottom right corner

        for (int i = 0, xOff = 7, yOff = 7; i < 4; ++i, yOff += 18)
        {
            RenderUtils.drawTexturedRectBatched(posMatrix, x + xOff, y + yOff, 61, 16, 18, 18, buffer);
        }

        // Main hand and offhand
        RenderUtils.drawTexturedRectBatched(posMatrix, x + 28, y + 2 * 18 + 7, 61, 16, 18, 18, buffer);
        RenderUtils.drawTexturedRectBatched(posMatrix, x + 28, y + 3 * 18 + 7, 61, 16, 18, 18, buffer);

        /*
        try
        {
            ctx.draw(RenderUtils.fb(), buffer.end());
            ctx.close();
        }
        catch (Exception ignored) { }
         */

        if (entity.getEquippedStack(EquipmentSlot.OFFHAND).isEmpty())
        {
            //RenderUtils.renderSprite(x + 28 + 1, y + 3 * 18 + 7 + 1, 16, 16, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, TEXTURE_EMPTY_SHIELD, drawContext);
            renderBackgroundSlotAt(x + 28 + 1, y + 3 * 18 + 7 + 1, TEXTURE_EMPTY_SHIELD, drawContext);
        }

        for (int i = 0, xOff = 7, yOff = 7; i < 4; ++i, yOff += 18)
        {
            final EquipmentSlot eqSlot = VALID_EQUIPMENT_SLOTS[i];

            if (entity.getEquippedStack(eqSlot).isEmpty())
            {
                Identifier texture = EMPTY_SLOT_TEXTURES[eqSlot.getEntitySlotId()];
                //RenderUtils.renderSprite(x + xOff + 1, y + yOff + 1, 16, 16, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, texture, drawContext);
                renderBackgroundSlotAt(x + xOff + 1, y + yOff + 1, texture, drawContext);
            }
        }
    }

    public static InventoryRenderType getInventoryType(Inventory inv)
    {
        if (inv instanceof ShulkerBoxBlockEntity)
        {
            return InventoryRenderType.FIXED_27;
        }
        else if (inv instanceof DoubleInventory)
        {
            return InventoryRenderType.FIXED_54;
        }
        else if (inv instanceof AbstractChestBoatEntity)
        {
            return InventoryRenderType.FIXED_27;
        }
        else if (inv instanceof ChestMinecartEntity)
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
        else if (inv instanceof HopperMinecartEntity)
        {
            return InventoryRenderType.HOPPER;
        }
        else if (inv instanceof PlayerInventory)
        {
            return InventoryRenderType.PLAYER;
        }
        else if (inv instanceof IEntityOwnedInventory inventory)
        {
            if (inventory.malilib$getEntityOwner() instanceof LlamaEntity)
            {
                return InventoryRenderType.LLAMA;
            }
            else if (inventory.malilib$getEntityOwner() instanceof WolfEntity)
            {
                return InventoryRenderType.WOLF;
            }
            else if (inventory.malilib$getEntityOwner() instanceof AbstractHorseEntity)
            {
                return InventoryRenderType.HORSE;
            }
            else if (inventory.malilib$getEntityOwner() instanceof PiglinEntity)
            {
                return InventoryRenderType.VILLAGER;
            }
        }
        return InventoryRenderType.GENERIC;
    }

    public static InventoryRenderType getInventoryType(ItemStack stack)
    {
        Item item = stack.getItem();

        if (item instanceof BlockItem)
        {
            Block block = ((BlockItem) item).getBlock();

            if (block instanceof ShulkerBoxBlock || block instanceof ChestBlock || block instanceof BarrelBlock)
            {
                return InventoryRenderType.FIXED_27;
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
            else if (block instanceof ChiseledBookshelfBlock)
            {
                return InventoryRenderType.BOOKSHELF;
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

    /**
     * Attempts to get the Inventory Type based on raw NBT tags.
     * @param nbt
     * @return
     */
    public static InventoryRenderType getInventoryType(@Nonnull NbtCompound nbt)
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
                    NbtList list = nbt.getList(NbtKeys.ITEMS).orElse(new NbtList());

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
     * Two-Way match to try to get the Best Inventory Type based on the INV Object, or NBT Tags.
     * @param inv
     * @param nbt
     * @return
     */
    public static InventoryRenderType getBestInventoryType(@Nonnull Inventory inv, @Nonnull NbtCompound nbt)
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
    public static InventoryRenderType getBestInventoryType(@Nonnull Inventory inv, @Nonnull NbtCompound nbt, Context ctx)
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
        else if (type == InventoryRenderType.HORSE || type == InventoryRenderType.LLAMA || type == InventoryRenderType.WOLF)
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

    public static void renderInventoryStacks(InventoryRenderType type, Inventory inv, int startX, int startY, int slotsPerRow, int startSlot, int maxSlots, MinecraftClient mc, DrawContext drawContext)
    {
        renderInventoryStacks(type, inv, startX, startY, slotsPerRow, startSlot, maxSlots, Set.of(), mc, drawContext, 0, 0);
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
    public static void renderInventoryStacks(InventoryRenderType type, Inventory inv, int startX, int startY, int slotsPerRow, int startSlot, int maxSlots, Set<Integer> disabledSlots, MinecraftClient mc, DrawContext drawContext)
    {
        renderInventoryStacks(type, inv, startX, startY, slotsPerRow, startSlot, maxSlots, disabledSlots, mc, drawContext, 0, 0);
    }

    public static void renderInventoryStacks(InventoryRenderType type, Inventory inv, int startX, int startY, int slotsPerRow, int startSlot, int maxSlots, MinecraftClient mc, DrawContext drawContext, double mouseX, double mouseY)
    {
        renderInventoryStacks(type, inv, startX, startY, slotsPerRow, startSlot, maxSlots, Set.of(), mc, drawContext, mouseX, mouseY);
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
    public static void renderInventoryStacks(InventoryRenderType type, Inventory inv, int startX, int startY, int slotsPerRow, int startSlot, int maxSlots, Set<Integer> disabledSlots, MinecraftClient mc, DrawContext drawContext, double mouseX, double mouseY)
    {
        if (inv == null)
        {
            // Only so this doesn't crash if inv was set to null
            inv = new SimpleInventory(maxSlots > 0 ? maxSlots : INV_PROPS_TEMP.totalSlots);
        }

        if (type == InventoryRenderType.FURNACE)
        {
            renderStackAt(inv.getStack(0), startX + 8, startY + 8, 1, mc, drawContext, mouseX, mouseY);
            renderStackAt(inv.getStack(1), startX + 8, startY + 44, 1, mc, drawContext, mouseX, mouseY);
            renderStackAt(inv.getStack(2), startX + 68, startY + 26, 1, mc, drawContext, mouseX, mouseY);
        }
        else if (type == InventoryRenderType.BREWING_STAND)
        {
            renderStackAt(inv.getStack(0), startX + 47, startY + 42, 1, mc, drawContext, mouseX, mouseY);
            renderStackAt(inv.getStack(1), startX + 70, startY + 49, 1, mc, drawContext, mouseX, mouseY);
            renderStackAt(inv.getStack(2), startX + 93, startY + 42, 1, mc, drawContext, mouseX, mouseY);
            renderStackAt(inv.getStack(3), startX + 70, startY + 8, 1, mc, drawContext, mouseX, mouseY);
            renderStackAt(inv.getStack(4), startX + 8, startY + 8, 1, mc, drawContext, mouseX, mouseY);
        }
        else
        {
            final int slots = inv.size();
            int x = startX;
            int y = startY;

            if (maxSlots < 0)
            {
                maxSlots = slots;
            }

            for (int slot = startSlot, i = 0; slot < slots && i < maxSlots; )
            {
                for (int column = 0; column < slotsPerRow && slot < slots && i < maxSlots; ++column, ++slot, ++i)
                {
                    ItemStack stack = inv.getStack(slot).copy();

                    if (disabledSlots.contains(slot))
                    {
                        // Requires -1 offset, because locked texture is 18 x 18.
                        renderLockedSlotAt(x - 1, y - 1, 1, drawContext, mouseX, mouseY);
                    }
                    else if (!stack.isEmpty())
                    {
                        //System.out.printf("renderInventoryStacks: slot[%d/%d]: [%s]\n", slot, slots, stack);
                        renderStackAt(stack, x, y, 1, mc, drawContext, mouseX, mouseY);
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
            renderStackToolTipStyled((int) mouseX, (int) mouseY, stack, mc, drawContext);
        }
    }

    public static void renderEquipmentStacks(LivingEntity entity, int x, int y, MinecraftClient mc, DrawContext drawContext)
    {
        renderEquipmentStacks(entity, x, y, mc, drawContext, 0, 0);
    }

    public static void renderEquipmentStacks(LivingEntity entity, int x, int y, MinecraftClient mc, DrawContext drawContext, double mouseX, double mouseY)
    {
        for (int i = 0, xOff = 7, yOff = 7; i < 4; ++i, yOff += 18)
        {
            final EquipmentSlot eqSlot = VALID_EQUIPMENT_SLOTS[i];
            ItemStack stack = entity.getEquippedStack(eqSlot);

            if (stack.isEmpty() == false)
            {
                renderStackAt(stack.copy(), x + xOff + 1, y + yOff + 1, 1, mc, drawContext, mouseX, mouseY);
            }
        }

        ItemStack stack = entity.getEquippedStack(EquipmentSlot.MAINHAND);

        if (stack.isEmpty() == false)
        {
            renderStackAt(stack.copy(), x + 28, y + 2 * 18 + 7 + 1, 1, mc, drawContext, mouseX, mouseY);
        }

        stack = entity.getEquippedStack(EquipmentSlot.OFFHAND);

        if (stack.isEmpty() == false)
        {
            renderStackAt(stack.copy(), x + 28, y + 3 * 18 + 7 + 1, 1, mc, drawContext, mouseX, mouseY);
        }

        if (hoveredStack != null)
        {
            stack = hoveredStack.copy();
            hoveredStack = null;
            // Some mixin / side effects can happen here, so reset hoveredStack
            //drawContext.drawItemTooltip(mc.textRenderer, stack, (int) mouseX, (int) mouseY);
            renderStackToolTipStyled((int) mouseX, (int) mouseY, stack, mc, drawContext);
        }
    }

    public static void renderItemStacks(DefaultedList<ItemStack> items, int startX, int startY, int slotsPerRow, int startSlot, int maxSlots, MinecraftClient mc, DrawContext drawContext)
    {
        renderItemStacks(items, startX, startY, slotsPerRow, startSlot, maxSlots, Set.of(), mc, drawContext);
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
    public static void renderItemStacks(DefaultedList<ItemStack> items, int startX, int startY, int slotsPerRow, int startSlot, int maxSlots, Set<Integer> disabledSlots, MinecraftClient mc, DrawContext drawContext)
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
                    renderLockedSlotAt(x - 1, y - 1, 1, drawContext, 0, 0);
                }
                else if (!stack.isEmpty())
                {
                    renderStackAt(stack, x, y, 1, mc, drawContext);
                }

                x += 18;
            }

            x = startX;
            y += 18;
        }
    }

    public static void renderStackAt(ItemStack stack, float x, float y, float scale, MinecraftClient mc, DrawContext drawContext)
    {
        renderStackAt(stack, x, y, scale, mc, drawContext, 0, 0);
    }

    public static void renderStackAt(ItemStack stack, float x, float y, float scale, MinecraftClient mc, DrawContext drawContext, double mouseX, double mouseY)
    {
        MatrixStack matrixStack = drawContext.getMatrices();
        matrixStack.push();
        matrixStack.translate(x, y, 0.f);
        matrixStack.scale(scale, scale, 1);

        RenderUtils.enableDiffuseLightingGui3D();
        RenderUtils.color(1f, 1f, 1f, 1f);

        drawContext.drawItem(stack.copy(), 0, 0);

        RenderUtils.color(1f, 1f, 1f, 1f);
        drawContext.drawStackOverlay(mc.textRenderer, stack.copyWithCount(stack.getCount()), 0, 0);
        //RenderUtils.forceDraw(drawContext);

        RenderUtils.color(1f, 1f, 1f, 1f);
        matrixStack.pop();

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
    public static void renderLockedSlotAt(float x, float y, float scale, DrawContext drawContext, double mouseX, double mouseY)
    {
        MatrixStack matrixStack = drawContext.getMatrices();
        int color = -1;

        matrixStack.push();
        matrixStack.translate(x, y, 0.f);
        matrixStack.scale(scale, scale, 1);

        RenderUtils.enableDiffuseLightingGui3D();
        RenderUtils.color(1f, 1f, 1f, 1f);

        drawContext.drawGuiTexture(RenderLayer::getGuiTextured, TEXTURE_LOCKED_SLOT, 0, 0, 18, 18, color);
        //RenderUtils.forceDraw(drawContext);

        RenderUtils.color(1f, 1f, 1f, 1f);
        matrixStack.pop();

        if (mouseX >= x && mouseX < x + 16 * scale && mouseY >= y && mouseY < y + 16 * scale)
        {
            hoveredStack = null;
        }
    }

    public static void renderBackgroundSlotAt(float x, float y, Identifier texture, DrawContext drawContext)
    {
        renderBackgroundSlotAt(x, y, 0.9f, texture, drawContext, 0, 0);
    }

    public static void renderBackgroundSlotAt(float x, float y, float scale, Identifier texture, DrawContext drawContext, double mouseX, double mouseY)
    {
        MatrixStack matrixStack = drawContext.getMatrices();
        int color = -1;

        matrixStack.push();
        matrixStack.translate(x, y, 0.f);
        matrixStack.scale(scale, scale, 1);

        RenderUtils.enableDiffuseLightingGui3D();
        color = RenderUtils.color(1f, 1f, 1f, 1f);

        drawContext.drawGuiTexture(RenderLayer::getGuiTextured, texture, 0, 0, 18, 18, color);
        //RenderUtils.forceDraw(drawContext);

        color = RenderUtils.color(1f, 1f, 1f, 1f);
        matrixStack.pop();

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
    public static void renderStackToolTip(int x, int y, ItemStack stack, MinecraftClient mc, DrawContext drawContext)
    {
        List<Text> list = stack.getTooltip(Item.TooltipContext.create(mc.world), mc.player, mc.options.advancedItemTooltips ? TooltipType.ADVANCED : TooltipType.BASIC);
        List<String> lines = new ArrayList<>();

        if (MaLiLibReference.DEBUG_MODE)
        {
            dumpStack(stack, list);
        }
        for (int i = 0; i < list.size(); ++i)
        {
            if (i == 0)
            {
                lines.add(stack.getRarity().getFormatting() + list.get(i).getString());
            }
            else
            {
                lines.add(GuiBase.TXT_DARK_GRAY + list.get(i).getString());
            }
        }

        RenderUtils.drawHoverText(x, y, lines, drawContext);
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
    public static void renderStackToolTipStyled(int x, int y, ItemStack stack, MinecraftClient mc, DrawContext drawContext)
    {
        if (stack.isEmpty() == false && mc.world != null && mc.player != null)
        {
            // Not sure why getBestWorld() is required here,
            // it's also required when connected to a server;
            // or else not be able to see Enchantment tooltips. (>.>)
            List<Text> toolTips = stack.getTooltip(Item.TooltipContext.create(WorldUtils.getBestWorld(mc)), mc.player, mc.options.advancedItemTooltips ? TooltipType.ADVANCED : TooltipType.BASIC);
            if (MaLiLibReference.DEBUG_MODE)
            {
                dumpStack(stack, toolTips);
            }
            drawContext.drawTooltip(mc.textRenderer,
                                    toolTips,
                                    stack.getTooltipData(), // Bundle/Optional Data
                                    x, y,
                                    stack.get(DataComponentTypes.TOOLTIP_STYLE));

            // Extra Hook for this tooltip style
            ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderTooltipLast(drawContext, stack, x, y);
        }
    }

    private static void dumpStack(ItemStack stack, @Nullable List<Text> list)
    {
        if (stack.isEmpty())
        {
            System.out.printf("dumpStack(): [%s]\n", ItemStack.EMPTY.toString());
            return;
        }

        System.out.printf("dumpStack(): [%s]\n", stack.toNbt(WorldUtils.getBestWorld(GameWrap.getClient()).getRegistryManager()).toString());

        if (list != null && !list.isEmpty())
        {
            int i = 0;

            for (Text entry : list)
            {
                System.out.printf("ToolTip[%d]: %s\n", i, entry.getString());
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
        FIXED_27,
        FIXED_54,
        VILLAGER,
        PLAYER,
        ENDER_CHEST,
        BOOKSHELF,
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
    public record Context(InventoryRenderType type, @Nullable Inventory inv, @Nullable BlockEntity be, @Nullable LivingEntity entity, @Nullable NbtCompound nbt, Refresher handler) {}

    public interface Refresher
    {
        Context onContextRefresh(Context data, World world);
    }
}
