package fi.dy.masa.malilib.render;

import java.util.ArrayList;
import java.util.List;
import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.util.IEntityOwnedInventory;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import fi.dy.masa.malilib.gui.GuiBase;

public class InventoryOverlay
{
    public static final Identifier TEXTURE_BREWING_STAND    = Identifier.ofVanilla("textures/gui/container/brewing_stand.png");
    public static final Identifier TEXTURE_CRAFTER          = Identifier.ofVanilla("textures/gui/container/crafter.png");
    public static final Identifier TEXTURE_DISPENSER        = Identifier.ofVanilla("textures/gui/container/dispenser.png");
    public static final Identifier TEXTURE_DOUBLE_CHEST     = Identifier.ofVanilla("textures/gui/container/generic_54.png");
    public static final Identifier TEXTURE_FURNACE          = Identifier.ofVanilla("textures/gui/container/furnace.png");
    public static final Identifier TEXTURE_HOPPER           = Identifier.ofVanilla("textures/gui/container/hopper.png");
    public static final Identifier TEXTURE_PLAYER_INV       = Identifier.ofVanilla("textures/gui/container/hopper.png");
    public static final Identifier TEXTURE_SINGLE_CHEST     = Identifier.ofVanilla("textures/gui/container/shulker_box.png");

    private static final EquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EquipmentSlot[] { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET };
    public static final InventoryProperties INV_PROPS_TEMP = new InventoryProperties();

    private static final Identifier[] EMPTY_SLOT_TEXTURES = new Identifier[] {
            Identifier.ofVanilla("item/empty_armor_slot_boots"),
            Identifier.ofVanilla("item/empty_armor_slot_leggings"),
            Identifier.ofVanilla("item/empty_armor_slot_chestplate"),
            Identifier.ofVanilla("item/empty_armor_slot_helmet") };

    private static ItemStack hoveredStack = null;

    public static void renderInventoryBackground(InventoryRenderType type, int x, int y, int slotsPerRow, int totalSlots, MinecraftClient mc)
    {
        RenderUtils.setupBlend();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        BuiltBuffer builtBuffer;

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.applyModelViewMatrix();

        if (type == InventoryRenderType.FURNACE)
        {
            RenderUtils.bindTexture(TEXTURE_FURNACE);
            RenderUtils.drawTexturedRectBatched(x     , y     ,   0,   0,   4,  64, buffer); // left (top)
            RenderUtils.drawTexturedRectBatched(x +  4, y     ,  84,   0,  92,   4, buffer); // top (right)
            RenderUtils.drawTexturedRectBatched(x     , y + 64,   0, 162,  92,   4, buffer); // bottom (left)
            RenderUtils.drawTexturedRectBatched(x + 92, y +  4, 172, 102,   4,  64, buffer); // right (bottom)
            RenderUtils.drawTexturedRectBatched(x +  4, y +  4,  52,  13,  88,  60, buffer); // middle
        }
        else if (type == InventoryRenderType.BREWING_STAND)
        {
            RenderUtils.bindTexture(TEXTURE_BREWING_STAND);
            RenderUtils.drawTexturedRectBatched(x      , y     ,   0,   0,   4,  68, buffer); // left (top)
            RenderUtils.drawTexturedRectBatched(x +   4, y     ,  63,   0, 113,   4, buffer); // top (right)
            RenderUtils.drawTexturedRectBatched(x      , y + 68,   0, 162, 113,   4, buffer); // bottom (left)
            RenderUtils.drawTexturedRectBatched(x + 113, y +  4, 172,  98,   4,  68, buffer); // right (bottom)
            RenderUtils.drawTexturedRectBatched(x +   4, y +  4,  13,  13, 109,  64, buffer); // middle
        }
        else if (type == InventoryRenderType.CRAFTER)
        {
            // We just hack in the Dispenser Texture, so it displays right.  Easy.
            RenderUtils.bindTexture(TEXTURE_DISPENSER);
            RenderUtils.drawTexturedRectBatched(x     , y     ,   0,   0,   7,  61, buffer); // left (top)
            RenderUtils.drawTexturedRectBatched(x +  7, y     , 115,   0,  61,   7, buffer); // top (right)
            RenderUtils.drawTexturedRectBatched(x     , y + 61,   0, 159,  61,   7, buffer); // bottom (left)
            RenderUtils.drawTexturedRectBatched(x + 61, y +  7, 169, 105,   7,  61, buffer); // right (bottom)
            RenderUtils.drawTexturedRectBatched(x +  7, y +  7,  61,  16,  54,  54, buffer); // middle
        }
        else if (type == InventoryRenderType.DISPENSER)
        {
            RenderUtils.bindTexture(TEXTURE_DISPENSER);
            RenderUtils.drawTexturedRectBatched(x     , y     ,   0,   0,   7,  61, buffer); // left (top)
            RenderUtils.drawTexturedRectBatched(x +  7, y     , 115,   0,  61,   7, buffer); // top (right)
            RenderUtils.drawTexturedRectBatched(x     , y + 61,   0, 159,  61,   7, buffer); // bottom (left)
            RenderUtils.drawTexturedRectBatched(x + 61, y +  7, 169, 105,   7,  61, buffer); // right (bottom)
            RenderUtils.drawTexturedRectBatched(x +  7, y +  7,  61,  16,  54,  54, buffer); // middle
        }
        else if (type == InventoryRenderType.HOPPER)
        {
            RenderUtils.bindTexture(TEXTURE_HOPPER);
            RenderUtils.drawTexturedRectBatched(x      , y     ,   0,   0,   7,  25, buffer); // left (top)
            RenderUtils.drawTexturedRectBatched(x +   7, y     ,  79,   0,  97,   7, buffer); // top (right)
            RenderUtils.drawTexturedRectBatched(x      , y + 25,   0, 126,  97,   7, buffer); // bottom (left)
            RenderUtils.drawTexturedRectBatched(x +  97, y +  7, 169, 108,   7,  25, buffer); // right (bottom)
            RenderUtils.drawTexturedRectBatched(x +   7, y +  7,  43,  19,  90,  18, buffer); // middle
        }
        // Most likely a Villager, or possibly a Llama
        else if (type == InventoryRenderType.VILLAGER)
        {
            RenderUtils.bindTexture(TEXTURE_DOUBLE_CHEST);
            RenderUtils.drawTexturedRectBatched(x     , y     ,   0,   0,   7,  79, buffer); // left (top)
            RenderUtils.drawTexturedRectBatched(x +  7, y     , 133,   0,  43,   7, buffer); // top (right)
            RenderUtils.drawTexturedRectBatched(x     , y + 79,   0, 215,  43,   7, buffer); // bottom (left)
            RenderUtils.drawTexturedRectBatched(x + 43, y +  7, 169, 143,   7,  79, buffer); // right (bottom)
            RenderUtils.drawTexturedRectBatched(x +  7, y +  7,   7,  17,  36,  72, buffer); // 2x4 slots
        }
        else if (type == InventoryRenderType.FIXED_27)
        {
            renderInventoryBackground27(x, y, buffer, mc);
        }
        else if (type == InventoryRenderType.FIXED_54)
        {
            renderInventoryBackground54(x, y, buffer, mc);
        }
        else
        {
            RenderUtils.bindTexture(TEXTURE_DOUBLE_CHEST);

            // Draw the slot backgrounds according to how many slots there actually are
            int rows = (int) (Math.ceil((double) totalSlots / (double) slotsPerRow));
            int bgw = Math.min(totalSlots, slotsPerRow) * 18 + 7;
            int bgh = rows * 18 + 7;

            RenderUtils.drawTexturedRectBatched(x      , y      ,         0,         0,   7, bgh, buffer); // left (top)
            RenderUtils.drawTexturedRectBatched(x +   7, y      , 176 - bgw,         0, bgw,   7, buffer); // top (right)
            RenderUtils.drawTexturedRectBatched(x      , y + bgh,         0,       215, bgw,   7, buffer); // bottom (left)
            RenderUtils.drawTexturedRectBatched(x + bgw, y +   7,       169, 222 - bgh,   7, bgh, buffer); // right (bottom)

            for (int row = 0; row < rows; row++)
            {
                int rowLen = MathHelper.clamp(totalSlots - (row * slotsPerRow), 1, slotsPerRow);
                RenderUtils.drawTexturedRectBatched(x + 7, y + row * 18 + 7, 7, 17, rowLen * 18, 18, buffer);

                // Render the background for the last non-existing slots on the last row,
                // in two strips of the background texture from the double chest texture's top part.
                if (rows > 1 && rowLen < slotsPerRow)
                {
                    RenderUtils.drawTexturedRectBatched(x + rowLen * 18 + 7, y + row * 18 +  7, 7, 3, (slotsPerRow - rowLen) * 18, 9, buffer);
                    RenderUtils.drawTexturedRectBatched(x + rowLen * 18 + 7, y + row * 18 + 16, 7, 3, (slotsPerRow - rowLen) * 18, 9, buffer);
                }
            }
        }

        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();

        try
        {
            builtBuffer = buffer.end();
            BufferRenderer.drawWithGlobalProgram(builtBuffer);
            builtBuffer.close();
        }
        catch (Exception ignored) { }
    }

    public static void renderInventoryBackground27(int x, int y, BufferBuilder buffer, MinecraftClient mc)
    {
        RenderUtils.bindTexture(TEXTURE_SINGLE_CHEST);
        RenderUtils.drawTexturedRectBatched(x      , y     ,   0,   0,   7,  61, buffer); // left (top)
        RenderUtils.drawTexturedRectBatched(x +   7, y     ,   7,   0, 169,   7, buffer); // top (right)
        RenderUtils.drawTexturedRectBatched(x      , y + 61,   0, 159, 169,   7, buffer); // bottom (left)
        RenderUtils.drawTexturedRectBatched(x + 169, y +  7, 169, 105,   7,  61, buffer); // right (bottom)
        RenderUtils.drawTexturedRectBatched(x +   7, y +  7,   7,  17, 162,  54, buffer); // middle
    }

    public static void renderInventoryBackground54(int x, int y, BufferBuilder buffer, MinecraftClient mc)
    {
        RenderUtils.bindTexture(TEXTURE_DOUBLE_CHEST);
        RenderUtils.drawTexturedRectBatched(x      , y      ,   0,   0,   7, 115, buffer); // left (top)
        RenderUtils.drawTexturedRectBatched(x +   7, y      ,   7,   0, 169,   7, buffer); // top (right)
        RenderUtils.drawTexturedRectBatched(x      , y + 115,   0, 215, 169,   7, buffer); // bottom (left)
        RenderUtils.drawTexturedRectBatched(x + 169, y +   7, 169, 107,   7, 115, buffer); // right (bottom)
        RenderUtils.drawTexturedRectBatched(x +   7, y +   7,   7,  17, 162, 108, buffer); // middle
    }

    public static void renderEquipmentOverlayBackground(int x, int y, LivingEntity entity, DrawContext drawContext)
    {
        RenderUtils.color(1f, 1f, 1f, 1f);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        BuiltBuffer builtBuffer;

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.applyModelViewMatrix();

        RenderUtils.bindTexture(TEXTURE_DISPENSER);

        RenderUtils.drawTexturedRectBatched(x     , y     ,   0,   0, 50, 83, buffer); // top-left (main part)
        RenderUtils.drawTexturedRectBatched(x + 50, y     , 173,   0,  3, 83, buffer); // right edge top
        RenderUtils.drawTexturedRectBatched(x     , y + 83,   0, 163, 50,  3, buffer); // bottom edge left
        RenderUtils.drawTexturedRectBatched(x + 50, y + 83, 173, 163,  3,  3, buffer); // bottom right corner

        for (int i = 0, xOff = 7, yOff = 7; i < 4; ++i, yOff += 18)
        {
            RenderUtils.drawTexturedRectBatched(x + xOff, y + yOff, 61, 16, 18, 18, buffer);
        }

        // Main hand and offhand
        RenderUtils.drawTexturedRectBatched(x + 28, y + 2 * 18 + 7, 61, 16, 18, 18, buffer);
        RenderUtils.drawTexturedRectBatched(x + 28, y + 3 * 18 + 7, 61, 16, 18, 18, buffer);

        try
        {
            builtBuffer = buffer.end();
            BufferRenderer.drawWithGlobalProgram(builtBuffer);
            builtBuffer.close();
        }
        catch (Exception ignored) { }

        RenderUtils.bindTexture(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);

        if (entity.getEquippedStack(EquipmentSlot.OFFHAND).isEmpty())
        {
            Identifier texture = Identifier.ofVanilla("item/empty_armor_slot_shield");
            RenderUtils.renderSprite(x + 28 + 1, y + 3 * 18 + 7 + 1, 16, 16, PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, texture, drawContext);
        }

        for (int i = 0, xOff = 7, yOff = 7; i < 4; ++i, yOff += 18)
        {
            final EquipmentSlot eqSlot = VALID_EQUIPMENT_SLOTS[i];

            if (entity.getEquippedStack(eqSlot).isEmpty())
            {
                Identifier texture = EMPTY_SLOT_TEXTURES[eqSlot.getEntitySlotId()];
                RenderUtils.renderSprite(x + xOff + 1, y + yOff + 1, 16, 16, PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, texture, drawContext);
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
        else if (inv instanceof DispenserBlockEntity) // this includes the Dropper as a sub class
        {
            return InventoryRenderType.DISPENSER;
        }
        else if (inv instanceof HopperBlockEntity)
        {
            return InventoryRenderType.HOPPER;
        }
        else if (inv instanceof IEntityOwnedInventory inventory)
        {
            if (inventory.malilib$getEntityOwner() instanceof AbstractHorseEntity)
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

            if (block instanceof ShulkerBoxBlock || block instanceof ChestBlock)
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
        }

        return InventoryRenderType.GENERIC;
    }

    /**
     * Returns the instance of the shared/temporary properties instance,
     * with the values set for the type of inventory provided.
     * Don't hold on to the instance, as the values will mutate when this
     * method is called again!
     * @param type
     * @param totalSlots
     * @return
     */
    public static InventoryProperties getInventoryPropsTemp(InventoryRenderType type, int totalSlots)
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
            INV_PROPS_TEMP.width = 127;
            INV_PROPS_TEMP.height = 72;
        }
        else if (type == InventoryRenderType.CRAFTER)
        {
            INV_PROPS_TEMP.slotsPerRow = 3;
            INV_PROPS_TEMP.slotOffsetX = 8;
            INV_PROPS_TEMP.slotOffsetY = 8;
            INV_PROPS_TEMP.width = 68;
            INV_PROPS_TEMP.height = 68;
        }
        else if (type == InventoryRenderType.DISPENSER)
        {
            INV_PROPS_TEMP.slotsPerRow = 3;
            INV_PROPS_TEMP.slotOffsetX = 8;
            INV_PROPS_TEMP.slotOffsetY = 8;
            INV_PROPS_TEMP.width = 68;
            INV_PROPS_TEMP.height = 68;
        }
        else if (type == InventoryRenderType.HORSE)
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
        else
        {
            if (type == InventoryRenderType.FIXED_27)
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
        renderInventoryStacks(type, inv, startX, startY, slotsPerRow, startSlot, maxSlots, mc, drawContext, 0, 0);
    }

    public static void renderInventoryStacks(InventoryRenderType type, Inventory inv, int startX, int startY, int slotsPerRow, int startSlot, int maxSlots, MinecraftClient mc, DrawContext drawContext, double mouseX, double mouseY)
    {
        if (type == InventoryRenderType.FURNACE)
        {
            renderStackAt(inv.getStack(0), startX +   8, startY +  8, 1, mc, drawContext, mouseX, mouseY);
            renderStackAt(inv.getStack(1), startX +   8, startY + 44, 1, mc, drawContext, mouseX, mouseY);
            renderStackAt(inv.getStack(2), startX +  68, startY + 26, 1, mc, drawContext, mouseX, mouseY);
        }
        else if (type == InventoryRenderType.BREWING_STAND)
        {
            renderStackAt(inv.getStack(0), startX +  47, startY + 42, 1, mc, drawContext, mouseX, mouseY);
            renderStackAt(inv.getStack(1), startX +  70, startY + 49, 1, mc, drawContext, mouseX, mouseY);
            renderStackAt(inv.getStack(2), startX +  93, startY + 42, 1, mc, drawContext, mouseX, mouseY);
            renderStackAt(inv.getStack(3), startX +  70, startY +  8, 1, mc, drawContext, mouseX, mouseY);
            renderStackAt(inv.getStack(4), startX +   8, startY +  8, 1, mc, drawContext, mouseX, mouseY);
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

            for (int slot = startSlot, i = 0; slot < slots && i < maxSlots;)
            {
                for (int column = 0; column < slotsPerRow && slot < slots && i < maxSlots; ++column, ++slot, ++i)
                {
                    ItemStack stack = inv.getStack(slot);

                    if (stack.isEmpty() == false)
                    {
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
            var stack = hoveredStack;
            hoveredStack = null;
            // Some mixin / side effects can happen here
            drawContext.drawItemTooltip(mc.textRenderer, stack, (int) mouseX, (int) mouseY);
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
                renderStackAt(stack, x + xOff + 1, y + yOff + 1, 1, mc, drawContext, mouseX, mouseY);
            }
        }

        ItemStack stack = entity.getEquippedStack(EquipmentSlot.MAINHAND);

        if (stack.isEmpty() == false)
        {
            renderStackAt(stack, x + 28, y + 2 * 18 + 7 + 1, 1, mc, drawContext, mouseX, mouseY);
        }

        stack = entity.getEquippedStack(EquipmentSlot.OFFHAND);

        if (stack.isEmpty() == false)
        {
            renderStackAt(stack, x + 28, y + 3 * 18 + 7 + 1, 1, mc, drawContext, mouseX, mouseY);
        }

        if (hoveredStack != null)
        {
            stack = hoveredStack;
            hoveredStack = null;
            // Some mixin / side effects can happen here, so reset hoveredStack
            drawContext.drawItemTooltip(mc.textRenderer, stack, (int) mouseX, (int) mouseY);
        }
    }

    public static void renderItemStacks(DefaultedList<ItemStack> items, int startX, int startY, int slotsPerRow, int startSlot, int maxSlots, MinecraftClient mc, DrawContext drawContext)
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
                ItemStack stack = items.get(slot);

                if (stack.isEmpty() == false)
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

        drawContext.drawItem(stack, 0, 0);

        RenderUtils.color(1f, 1f, 1f, 1f);
        drawContext.drawItemInSlot( mc.textRenderer, stack, 0, 0);

        RenderUtils.color(1f, 1f, 1f, 1f);
        matrixStack.pop();
        if (mouseX >= x && mouseX < x + 16 * scale && mouseY >= y && mouseY < y + 16 * scale)
        {
            hoveredStack = stack;
        }
    }

    public static void renderStackToolTip(int x, int y, ItemStack stack, MinecraftClient mc, DrawContext drawContext)
    {
        List<Text> list = stack.getTooltip(Item.TooltipContext.DEFAULT, mc.player, mc.options.advancedItemTooltips ? TooltipType.ADVANCED : TooltipType.BASIC);
        List<String> lines = new ArrayList<>();

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
        FIXED_27,
        FIXED_54,
        VILLAGER,
        GENERIC;
    }
}
