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
import fi.dy.masa.malilib.render.InventoryOverlay;
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
	 * Return if this handler uses the new code
	 * @return (True|False)
	 */
	@ApiStatus.Experimental
	default boolean isNewCode() { return false; }

    /**
     * Return your Inventory.Overlay Refresh Handler.
     * @return ()
     */
    InventoryOverlay.Refresher getRefreshHandler();

	/**
	 * Return your Inventory.Overlay Refresh Handler.
	 * @return ()
	 */
	@ApiStatus.Experimental
	default InventoryOverlayRefresher getRefreshHandlerNew() { return null; }

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
	 * Get the Existing saved InventoryOverlay.Context, whether it's null or not.
	 * @return ()
	 */
	@Nullable
	@ApiStatus.Experimental
	default InventoryOverlayContext getRenderContextNullableNew() { return null; }

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
	 * Start your Rendering Context & Request the Context Data from your Server Data Syncer.
	 * It optionally returns the Current Context.
	 * @param drawContext ()
	 * @param profiler ()
	 * @param mc ()
	 * @return ()
	 */
	@Nullable
	@ApiStatus.Experimental
	default InventoryOverlayContext getRenderContextNew(DrawContext drawContext, Profiler profiler, MinecraftClient mc) { return null; }

	/**
     * Render the InventoryOverlay.Context on Screen for the First time.
     * @param context ()
     * @param drawContext ()
     * @param mc ()
     * @param shulkerBGColors (Display the Shulker Box Background Colors)
     * @param villagerBGColors (Display the Villager Profession Background Colors)
     */
    default void renderInventoryOverlay(DrawContext drawContext, InventoryOverlay.Context context, MinecraftClient mc, boolean shulkerBGColors, boolean villagerBGColors)
    {
        Screen screen = new InventoryOverlayScreen(this.getModId(), context, shulkerBGColors, villagerBGColors);
        screen.init(mc, 0, 0);
        screen.render(drawContext, 0, 0, 0);
    }

    default void renderInventoryOverlay(DrawContext drawContext, InventoryOverlay.Context context, MinecraftClient mc, boolean shulkerBGColors)
    {
        this.renderInventoryOverlay(drawContext, context, mc, shulkerBGColors, false);
    }

    default void renderInventoryOverlay(DrawContext drawContext, InventoryOverlay.Context context, MinecraftClient mc)
    {
        this.renderInventoryOverlay(drawContext, context, mc, false, false);
    }

	/**
	 * Render the InventoryOverlayContext on Screen for the First time.
	 * @param context ()
	 * @param drawContext ()
	 * @param mc ()
	 * @param shulkerBGColors (Display the Shulker Box Background Colors)
	 * @param villagerBGColors (Display the Villager Profession Background Colors)
	 */
	@ApiStatus.Experimental
	default void renderInventoryOverlayNew(DrawContext drawContext, InventoryOverlayContext context, MinecraftClient mc, boolean shulkerBGColors, boolean villagerBGColors)
	{
		Screen screen = new InventoryOverlayScreen(this.getModId(), context, shulkerBGColors, villagerBGColors);
		screen.init(mc, 0, 0);
		screen.render(drawContext, 0, 0, 0);
	}

	@ApiStatus.Experimental
	default void renderInventoryOverlayNew(DrawContext drawContext, InventoryOverlayContext context, MinecraftClient mc, boolean shulkerBGColors)
	{
		this.renderInventoryOverlayNew(drawContext, context, mc, shulkerBGColors, false);
	}

	@ApiStatus.Experimental
	default void renderInventoryOverlayNew(DrawContext drawContext, InventoryOverlayContext context, MinecraftClient mc)
	{
		this.renderInventoryOverlayNew(drawContext, context, mc, false, false);
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
	    if (this.isNewCode())
	    {
		    this.getTargetInventoryNew(mc);
	    }
		else
	    {
		    this.getTargetInventory(mc);
	    }

        if (!this.isEmpty())
        {
			if (this.isNewCode())
			{
				mc.setScreen(new InventoryOverlayScreen(this.getModId(), this.getRenderContextNullableNew(), shulkerBGColors, villagerBGColors));
			}
			else
			{
				mc.setScreen(new InventoryOverlayScreen(this.getModId(), this.getRenderContextNullable(), shulkerBGColors, villagerBGColors));
			}
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
	 * This is used to 'pre-Request' your DataSyncer to Sync a Block Entity,
	 * particularly for a Double Chest situation.
	 * @param world ()
	 * @param pos ()
	 * @return ()
	 */
	@Nullable
	@ApiStatus.Experimental
	default Pair<BlockEntity, CompoundData> requestBlockEntityAtNew(World world, BlockPos pos)
	{
		if (!(world instanceof ServerWorld))
		{
			Pair<BlockEntity, CompoundData> pair = this.getDataSyncer().requestBlockEntityNew(world, pos);

			BlockState state = world.getBlockState(pos);

			if (state.getBlock() instanceof ChestBlock)
			{
				ChestType type = state.get(ChestBlock.CHEST_TYPE);

				if (type != ChestType.SINGLE)
				{
					return this.getDataSyncer().requestBlockEntityNew(world, pos.offset(ChestBlock.getFacing(state)));
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

	/**
	 * The Main Function used to Build the InventoryOverlayContext, and Build the Inventory Objects, etc.
	 * @param mc ()
	 * @return ()
	 */
	@Nullable
	@ApiStatus.Experimental
	default InventoryOverlayContext getTargetInventoryNew(MinecraftClient mc) { return null; }

	/**
	 * The code used to build the Block Entity Context.
	 * @param world ()
	 * @param pos ()
	 * @param be ()
	 * @param data ()
	 * @return ()
	 */
	@Nullable
	@ApiStatus.Experimental
	default InventoryOverlayContext getTargetInventoryFromBlockNew(World world, BlockPos pos, @Nullable BlockEntity be, CompoundData data) { return null; }

	/**
	 * The code used to build the Entity Context.
	 * @param entity ()
	 * @param data ()
	 * @return ()
	 */
	@Nullable
	@ApiStatus.Experimental
	default InventoryOverlayContext getTargetInventoryFromEntityNew(Entity entity, CompoundData data) { return null; }
}
