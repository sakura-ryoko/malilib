package malilib.render.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntUnaryOperator;
import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import malilib.gui.icon.Icon;
import malilib.gui.icon.PositionedIcon;
import malilib.render.RenderContext;
import malilib.render.RenderUtils;
import malilib.util.game.wrap.RenderWrap;
import malilib.util.inventory.InventoryView;
import malilib.util.position.Vec2i;

public class InventoryRenderDefinition
{
    protected final ImmutableList<PositionedIcon> backgroundTextures;
    protected final ImmutableList<InventoryRange> inventoryRanges;
    protected final Int2ObjectOpenHashMap<PositionedIcon> emptySlotTextures;
    protected final Int2ObjectOpenHashMap<Vec2i> customSlotPositions;
    protected final IntUnaryOperator renderWidthFunction;
    protected final IntUnaryOperator renderHeightFunction;
    protected final IntUnaryOperator slotsPerRowFunction;
    protected final Vec2i slotOffset;
    protected final boolean hasCustomSlotPositions;
    protected final boolean hasEmptySlotTextures;
    protected final boolean hasInventoryRanges;

    public InventoryRenderDefinition(IntUnaryOperator slotsPerRowFunction,
                                     IntUnaryOperator renderWidthFunction,
                                     IntUnaryOperator renderHeightFunction,
                                     Vec2i slotOffset,
                                     List<PositionedIcon> backgroundTextures,
                                     Int2ObjectOpenHashMap<Vec2i> customSlotPositions,
                                     List<InventoryRange> inventoryRanges,
                                     Int2ObjectOpenHashMap<PositionedIcon> emptySlotTextures)
    {
        this.slotsPerRowFunction = slotsPerRowFunction;
        this.renderWidthFunction = renderWidthFunction;
        this.renderHeightFunction = renderHeightFunction;
        this.slotOffset = slotOffset;
        this.backgroundTextures = ImmutableList.copyOf(backgroundTextures);
        this.inventoryRanges = ImmutableList.copyOf(inventoryRanges);
        this.customSlotPositions = customSlotPositions;
        this.emptySlotTextures = emptySlotTextures;
        this.hasCustomSlotPositions = customSlotPositions.isEmpty() == false;
        this.hasEmptySlotTextures = emptySlotTextures.isEmpty() == false;
        this.hasInventoryRanges = inventoryRanges.isEmpty() == false;
    }

    public int getRenderWidth(InventoryView inv)
    {
        return this.renderWidthFunction.applyAsInt(inv.getSize());
    }

    public int getRenderHeight(InventoryView inv)
    {
        return this.renderHeightFunction.applyAsInt(inv.getSize());
    }

    public void renderInventory(int x, int y, float z, int backgroundTintColor, InventoryView inv, RenderContext ctx)
    {
        RenderWrap.color(1f, 1f, 1f, 1f);
        RenderWrap.disableItemLighting();

        this.renderInventoryBackground(x, y, z, backgroundTintColor, inv, ctx);

        if (this.hasEmptySlotTextures)
        {
            InventoryRenderUtils.renderEmptySlotBackgrounds(x, y, z, backgroundTintColor, inv, this.emptySlotTextures, ctx);
        }

        RenderWrap.pushMatrix(ctx);
        RenderWrap.translate(0f, 0f, z + 1, ctx);

        if (this.hasInventoryRanges)
        {
            InventoryRenderUtils.renderInventoryRanges(x, y, 0, inv, this.inventoryRanges, backgroundTintColor, ctx);
        }

        if (this.hasCustomSlotPositions)
        {
            InventoryRenderUtils.renderCustomPositionedSlots(x, y, 100f, inv, this.customSlotPositions, ctx);
        }

        if (this.hasCustomSlotPositions == false && this.hasInventoryRanges == false)
        {
            int slotsPerRow = this.slotsPerRowFunction.applyAsInt(inv.getSize());
            InventoryRenderUtils.renderGenericInventoryItems(x, y, 100f, 0, -1, slotsPerRow, this.slotOffset, inv, ctx);
        }

        RenderWrap.popMatrix(ctx);
        RenderWrap.color(1f, 1f, 1f, 1f);
    }

    protected void renderInventoryBackground(int x, int y, float z, int backgroundTintColor,
                                             InventoryView inv, RenderContext ctx)
    {
        if (this.backgroundTextures.isEmpty() == false)
        {
            RenderUtils.renderPositionedIcons(x, y, z, backgroundTintColor, this.backgroundTextures, ctx);
        }
        else
        {
            int slotsPerRow = this.slotsPerRowFunction.applyAsInt(inv.getSize());
            int slotCount = inv.getSize();
            InventoryRenderUtils.renderDynamicInventoryBackground(x, y, z, backgroundTintColor, slotsPerRow, slotCount, ctx);
        }
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
    {
        protected final Int2ObjectOpenHashMap<Vec2i> customSlotPositions = new Int2ObjectOpenHashMap<>();
        protected final Int2ObjectOpenHashMap<PositionedIcon> emptySlotTextures = new Int2ObjectOpenHashMap<>();
        protected final List<PositionedIcon> backgroundTextures = new ArrayList<>();
        protected final List<InventoryRange> inventoryRanges = new ArrayList<>();
        protected Vec2i slotOffset = new Vec2i(8, 8);
        protected IntUnaryOperator renderWidthFunction = (slots) -> 176;
        protected IntUnaryOperator renderHeightFunction = (slots) -> 68;
        protected IntUnaryOperator slotsPerRowFunction = (slots) -> 9;

        public Builder slotsPerRow(int slotsPerRow)
        {
            this.slotsPerRowFunction = (slots) -> slotsPerRow;
            return this;
        }

        public Builder slotsPerRowFunction(IntUnaryOperator slotsPerRowFunction)
        {
            this.slotsPerRowFunction = slotsPerRowFunction;
            return this;
        }

        public Builder renderSize(int renderWidth, int renderHeight)
        {
            this.renderWidthFunction = (slots) -> renderWidth;
            this.renderHeightFunction = (slots) -> renderHeight;
            return this;
        }

        public Builder renderSizeFunctions(IntUnaryOperator renderWidthFunction,
                                           IntUnaryOperator renderHeightFunction)
        {
            this.renderWidthFunction = renderWidthFunction;
            this.renderHeightFunction = renderHeightFunction;
            return this;
        }

        public Builder slotOffset(int slotOffsetX, int slotOffsetY)
        {
            this.slotOffset = new Vec2i(slotOffsetX, slotOffsetY);
            return this;
        }

        public Builder slotPosition(int slotNumber, int slotX, int slotY)
        {
            this.customSlotPositions.put(slotNumber, new Vec2i(slotX, slotY));
            return this;
        }

        public Builder backgroundTexturePiece(int x, int y, Icon icon)
        {
            this.backgroundTextures.add(PositionedIcon.of(new Vec2i(x, y), icon));
            return this;
        }

        public Builder inventoryRange(int startSlot, int slotCount,
                                      int slotsPerRow, int offsetX, int offsetY)
        {
            return this.inventoryRange(startSlot, slotCount, slotsPerRow, offsetX, offsetY, false);
        }

        public Builder inventoryRange(int startSlot, int slotCount,
                                      int slotsPerRow, int offsetX, int offsetY, boolean renderSlots)
        {
            this.inventoryRanges.add(InventoryRange.of(startSlot, slotCount, renderSlots,
                                                       new Vec2i(offsetX, offsetY), (slots) -> slotsPerRow));
            return this;
        }

        public Builder inventoryRange(int startSlot, int slotCount,
                                      IntUnaryOperator slotsPerRowFunction,
                                      int offsetX, int offsetY)
        {
            return this.inventoryRange(startSlot, slotCount, slotsPerRowFunction, offsetX, offsetY, false);
        }

        public Builder inventoryRange(int startSlot, int slotCount,
                                      IntUnaryOperator slotsPerRowFunction,
                                      int offsetX, int offsetY, boolean renderSlots)
        {
            this.inventoryRanges.add(InventoryRange.of(startSlot, slotCount, renderSlots,
                                                       new Vec2i(offsetX, offsetY), slotsPerRowFunction));
            return this;
        }

        public Builder emptySlotBackgroundTexture(int slotNum, int x, int y, Icon icon)
        {
            this.emptySlotTextures.put(slotNum, PositionedIcon.of(new Vec2i(x, y), icon));
            return this;
        }

        public InventoryRenderDefinition build()
        {
            return new InventoryRenderDefinition(this.slotsPerRowFunction,
                                                 this.renderWidthFunction,
                                                 this.renderHeightFunction,
                                                 this.slotOffset,
                                                 this.backgroundTextures,
                                                 this.customSlotPositions,
                                                 this.inventoryRanges,
                                                 this.emptySlotTextures);
        }
    }
}
