package fi.dy.masa.malilib.interfaces;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;

import fi.dy.masa.malilib.render.InventoryOverlay;
import fi.dy.masa.malilib.render.InventoryOverlayScreen;

public interface IInventoryOverlayHandler
{
    String getModId();

    IDataSyncer getDataSyncer();

    InventoryOverlay.Refresher getRefreshHandler();

    boolean isEmpty();

    @Nullable
    InventoryOverlay.Context getRenderContextNullable();

    @Nullable
    InventoryOverlay.Context getRenderContext(DrawContext drawContext, Profiler profiler, MinecraftClient mc);

    default void renderInventoryOverlay(InventoryOverlay.Context context, DrawContext drawContext, MinecraftClient mc, boolean shulkerBGColors)
    {
        var screen = new InventoryOverlayScreen(this.getModId(), context, shulkerBGColors);
        screen.init(mc, 0, 0);
        screen.render(drawContext, 0, 0, 0);
    }

    default void refreshInventoryOverlay(MinecraftClient mc, boolean shulkerBGColors)
    {
        //this.getTargetInventory(mc, newScreen);
        this.getTargetInventory(mc);

        if (!this.isEmpty())
        {
            mc.setScreen(new InventoryOverlayScreen(this.getModId(), this.getRenderContextNullable(), shulkerBGColors));
        }
    }

    @Nullable
    default Pair<BlockEntity, NbtCompound> requestBlockEntityAt(World world, BlockPos pos)
    {
        if (!(world instanceof ServerWorld))
        {
            Pair<BlockEntity, NbtCompound> pair = this.getDataSyncer().requestBlockEntity(world, pos);

            BlockState state = world.getBlockState(pos);

            if (state.getBlock() instanceof ChestBlock)
            {
                ChestType type = state.get(ChestBlock.CHEST_TYPE);

                if (type != ChestType.SINGLE)
                {
                    return this.getDataSyncer().requestBlockEntity(world, pos.offset(ChestBlock.getFacing(state)));
                }
            }

            return pair;
        }

        return null;
    }

    @Nullable
    InventoryOverlay.Context getTargetInventory(MinecraftClient mc);

    @Nullable
    InventoryOverlay.Context getTargetInventoryFromBlock(World world, BlockPos pos, @Nullable BlockEntity be, NbtCompound nbt);

    @Nullable
    InventoryOverlay.Context getTargetInventoryFromEntity(Entity entity, NbtCompound nbt);
}
