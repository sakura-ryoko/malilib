package fi.dy.masa.malilib.interfaces;

import javax.annotation.Nullable;
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
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;

import fi.dy.masa.malilib.render.*;
import fi.dy.masa.malilib.util.data.tag.CompoundData;

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
	 * Return your InventoryOverlay Refresh Handler.
	 * @return ()
	 */
	default InventoryOverlayRefresher getRefreshHandler() { return null; }

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
	default InventoryOverlayContext getRenderContextNullable() { return null; }

	/**
	 * Start your Rendering Context & Request the Context Data from your Server Data Syncer.
	 * It optionally returns the Current Context.
	 * @param profiler ()
	 * @return ()
	 */
	@Nullable
	default InventoryOverlayContext getRenderContext(GuiContext ctx, Profiler profiler) { return null; }

	/**
	 * Render the InventoryOverlayContext on Screen for the First time.
	 * @param ctx ()
	 * @param context ()
	 * @param shulkerBGColors (Display the Shulker Box Background Colors)
	 * @param villagerBGColors (Display the Villager Profession Background Colors)
	 */
	default void renderInventoryOverlay(GuiContext ctx, InventoryOverlayContext context, boolean shulkerBGColors, boolean villagerBGColors)
	{
		Screen screen = new InventoryOverlayScreen(this.getModId(), context, shulkerBGColors, villagerBGColors);
		screen.init(0, 0);
		screen.render(ctx, 0, 0, 0);
	}

	default void renderInventoryOverlay(GuiContext ctx, InventoryOverlayContext context, boolean shulkerBGColors)
	{
		this.renderInventoryOverlay(ctx, context, shulkerBGColors, false);
	}

	default void renderInventoryOverlay(GuiContext ctx, InventoryOverlayContext context)
	{
		this.renderInventoryOverlay(ctx, context, false, false);
	}

	/**
     * Refresh your InventoryOverlay.Context and redraw the Screen.
     * Used for using the Assigned Hotkey to "open" the Screen; and keep the data updated.
     * @param mc ()
     * @param shulkerBGColors (Display the Shulker Box Background Colors)
     * @param villagerBGColors (Display the Villager Profession Background Colors)
     */
    default void refreshInventoryOverlay(MinecraftClient mc, boolean shulkerBGColors, boolean villagerBGColors)
    {
	    this.getTargetInventory(mc);

        if (!this.isEmpty())
        {
			mc.setScreen(new InventoryOverlayScreen(this.getModId(), this.getRenderContextNullable(), shulkerBGColors, villagerBGColors));
        }
    }

    default void refreshInventoryOverlay(MinecraftClient mc, boolean shulkerBGColors)
    {
        this.refreshInventoryOverlay(mc, shulkerBGColors, false);
    }

    default void refreshInventoryOverlay(MinecraftClient mc)
    {
        this.refreshInventoryOverlay(mc, false, false);
    }

	/**
	 * This is used to 'pre-Request' your DataSyncer to Sync a Block Entity,
	 * particularly for a Double Chest situation.
	 * @param world ()
	 * @param pos ()
	 * @return ()
	 */
	@Nullable
	default Pair<BlockEntity, CompoundData> requestBlockEntityAt(World world, BlockPos pos)
	{
		if (!(world instanceof ServerWorld))
		{
			Pair<BlockEntity, CompoundData> pair = this.getDataSyncer().requestBlockEntity(world, pos);

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
	 * The Main Function used to Build the InventoryOverlayContext, and Build the Inventory Objects, etc.
	 * @param mc ()
	 * @return ()
	 */
	@Nullable
	default InventoryOverlayContext getTargetInventory(MinecraftClient mc) { return null; }

	/**
	 * The code used to build the Block Entity Context.
	 * @param world ()
	 * @param pos ()
	 * @param be ()
	 * @param data ()
	 * @return ()
	 */
	@Nullable
	default InventoryOverlayContext getTargetInventoryFromBlock(World world, BlockPos pos, @Nullable BlockEntity be, CompoundData data) { return null; }

	/**
	 * The code used to build the Entity Context.
	 * @param entity ()
	 * @param data ()
	 * @return ()
	 */
	@Nullable
	default InventoryOverlayContext getTargetInventoryFromEntity(Entity entity, CompoundData data) { return null; }
}
