package fi.dy.masa.malilib.interfaces;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
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
    /**
     * Return your Mod's ID for the Screen Title
     * @return ()
     */
    String getModId();

    /**
     * Return your ServerDataSyncer Instance.
     * @return ()
     */
    IDataSyncer getDataSyncer();

    /**
     * Manually change a Built-In Data Syncer.
     * @param syncer ()
     */
    void setDataSyncer(IDataSyncer syncer);

    /**
     * Return your Inventory.Overlay Refresh Handler.
     * @return ()
     */
    InventoryOverlay.Refresher getRefreshHandler();

    /**
     * Return if the saved InventoryOverlay.Context is Empty or not.
     * @return ()
     */
    boolean isEmpty();

    /**
     * Get the Existing saved InventoryOverlay.Context, whether it's null or not.
     * @return ()
     */
    @Nullable
    InventoryOverlay.Context getRenderContextNullable();

    /**
     * Start your Rendering Context & Request the Context Data from your Server Data Syncer.
     * It optionally returns the Current Context.
     * @param drawContext ()
     * @param profiler ()
     * @param mc ()
     * @return ()
     */
    @Nullable
    InventoryOverlay.Context getRenderContext(DrawContext drawContext, Profiler profiler, MinecraftClient mc);

    /**
     * Render the InventoryOverlay.Context on Screen for the First time.
     * @param context ()
     * @param drawContext ()
     * @param mc ()
     * @param shulkerBGColors ()
     */
    default void renderInventoryOverlay(InventoryOverlay.Context context, DrawContext drawContext, MinecraftClient mc, boolean shulkerBGColors)
    {
        Screen screen = new InventoryOverlayScreen(this.getModId(), context, shulkerBGColors);
        screen.init(mc, 0, 0);
        screen.render(drawContext, 0, 0, 0);
    }

    /**
     * Refresh your InventoryOverlay.Context and redraw the Screen.
     * Used for using the Assigned Hotkey to "open" the Screen; and keep the data updated.
     * @param mc ()
     * @param shulkerBGColors ()
     */
    default void refreshInventoryOverlay(MinecraftClient mc, boolean shulkerBGColors)
    {
        this.getTargetInventory(mc);

        if (!this.isEmpty())
        {
            mc.setScreen(new InventoryOverlayScreen(this.getModId(), this.getRenderContextNullable(), shulkerBGColors));
        }
    }

    /**
     * This is used to 'pre-Request' your DataSyncer to Sync a Block Entity,
     * particularly for a Double Chest situation.
     * @param world ()
     * @param pos ()
     * @return ()
     */
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

    /**
     * The Main Function used to Build the InventoryOverlay.Context, and Build the Inventory Objects, etc.
     * @param mc ()
     * @return ()
     */
    @Nullable
    InventoryOverlay.Context getTargetInventory(MinecraftClient mc);

    /**
     * The code used to build the Block Entity Context.
     * @param world ()
     * @param pos ()
     * @param be ()
     * @param nbt ()
     * @return ()
     */
    @Nullable
    InventoryOverlay.Context getTargetInventoryFromBlock(World world, BlockPos pos, @Nullable BlockEntity be, NbtCompound nbt);

    /**
     * The code used to build the Entity Context.
     * @param entity ()
     * @param nbt ()
     * @return ()
     */
    @Nullable
    InventoryOverlay.Context getTargetInventoryFromEntity(Entity entity, NbtCompound nbt);
}
