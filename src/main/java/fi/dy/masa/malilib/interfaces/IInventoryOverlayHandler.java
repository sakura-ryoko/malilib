package fi.dy.masa.malilib.interfaces;

import javax.annotation.Nullable;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

import fi.dy.masa.malilib.render.GuiContext;
import fi.dy.masa.malilib.render.InventoryOverlayContext;
import fi.dy.masa.malilib.render.InventoryOverlayRefresher;
import fi.dy.masa.malilib.render.InventoryOverlayScreen;
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
	default InventoryOverlayContext getRenderContext(GuiContext ctx, ProfilerFiller profiler) { return null; }

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
    default void refreshInventoryOverlay(Minecraft mc, boolean shulkerBGColors, boolean villagerBGColors)
    {
	    this.getTargetInventory(mc);

        if (!this.isEmpty())
        {
			mc.setScreen(new InventoryOverlayScreen(this.getModId(), this.getRenderContextNullable(), shulkerBGColors, villagerBGColors));
        }
    }

    default void refreshInventoryOverlay(Minecraft mc, boolean shulkerBGColors)
    {
        this.refreshInventoryOverlay(mc, shulkerBGColors, false);
    }

    default void refreshInventoryOverlay(Minecraft mc)
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
	default Pair<BlockEntity, CompoundData> requestBlockEntityAt(Level world, BlockPos pos)
	{
		if (!(world instanceof ServerLevel))
		{
			Pair<BlockEntity, CompoundData> pair = this.getDataSyncer().requestBlockEntity(world, pos);

			BlockState state = world.getBlockState(pos);

			if (state.getBlock() instanceof ChestBlock)
			{
				ChestType type = state.getValue(ChestBlock.TYPE);

				if (type != ChestType.SINGLE)
				{
					return this.getDataSyncer().requestBlockEntity(world, pos.relative(ChestBlock.getConnectedDirection(state)));
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
	default InventoryOverlayContext getTargetInventory(Minecraft mc) { return null; }

	/**
	 * The code used to build the Block Entity Context.
	 * @param world ()
	 * @param pos ()
	 * @param be ()
	 * @param data ()
	 * @return ()
	 */
	@Nullable
	default InventoryOverlayContext getTargetInventoryFromBlock(Level world, BlockPos pos, @Nullable BlockEntity be, CompoundData data) { return null; }

	/**
	 * The code used to build the Entity Context.
	 * @param entity ()
	 * @param data ()
	 * @return ()
	 */
	@Nullable
	default InventoryOverlayContext getTargetInventoryFromEntity(Entity entity, CompoundData data) { return null; }
}
