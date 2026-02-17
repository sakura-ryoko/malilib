package malilib.util.inventory;

import java.util.Optional;
import javax.annotation.Nonnull;
import malilib.util.data.Constants;
import malilib.util.data.Identifier;
import malilib.util.data.tag.CompoundData;
import malilib.util.data.tag.ListData;
import malilib.util.game.DataEntityUtils;
import malilib.util.nbt.NbtKeys;

public class DataInventoryUtils
{
	/**
	 * Read any Items tag from a Tile Entity into a {@link DataInventoryView} utilizing the Data Tag system.
	 * @implNote 1.21+ requires the use of 'DynamicRegistry' aka 'RegistryAccess'
	 * @param data -
	 * @return -
	 */
	public static Optional<DataInventoryView> getItemsAsDataInventory(@Nonnull CompoundData data)
	// @Nonnull RegistryAccess registry
	{
		if (data.containsList(NbtKeys.ITEMS, Constants.NBT.TAG_COMPOUND))
		{
			DataInventoryView inv = DataInventoryView.fromDataList(data.getList(NbtKeys.ITEMS, Constants.NBT.TAG_COMPOUND), false).orElse(null);
			if (inv == null || inv.isEmpty()) { return Optional.empty(); }

			return Optional.of(inv.sorted());
		}

		return Optional.empty();
	}

	/**
	 * Read any Inventory tag from an Entity into a {@link DataInventoryView} utilizing the Data Tag system.
	 * @implNote 1.21+ requires the use of 'DynamicRegistry' aka 'RegistryAccess'
	 * @param data -
	 * @return -
	 */
	public static Optional<DataInventoryView> getInventoryAsDataInventory(@Nonnull CompoundData data)
	// @Nonnull RegistryAccess registry
	{
		Identifier id = DataEntityUtils.getEntityType(data).orElse(null);
		Identifier player = Identifier.of("player");
		boolean isPlayer = false;

		if (id != null && player != null)
		{
			isPlayer = id.equals(player);
		}

		if (data.containsList(NbtKeys.INVENTORY, Constants.NBT.TAG_COMPOUND))
		{
			ListData list = data.getList(NbtKeys.INVENTORY, Constants.NBT.TAG_COMPOUND);
			DataInventoryView inv = DataInventoryView.fromDataList(list, !isPlayer).orElse(null);
			if (inv == null || inv.isEmpty()) { return Optional.empty(); }

			return Optional.of(inv.sorted());
		}

		return Optional.empty();
	}

	/**
	 * Read any EnderItems tag from a Player into a {@link DataInventoryView} utilizing the Data Tag system.
	 * @implNote 1.21+ requires the use of 'DynamicRegistry' aka 'RegistryAccess'
	 * @param data -
	 * @return -
	 */
	public static Optional<DataInventoryView> getEnderItemsAsDataInventory(@Nonnull CompoundData data)
	// @Nonnull RegistryAccess registry
	{
		if (data.containsList(NbtKeys.ENDER_ITEMS, Constants.NBT.TAG_COMPOUND))
		{
			ListData list = data.getList(NbtKeys.ENDER_ITEMS, Constants.NBT.TAG_COMPOUND);
			DataInventoryView inv = DataInventoryView.fromDataList(list, false).orElse(null);
			if (inv == null || inv.isEmpty()) { return Optional.empty(); }

			return Optional.of(inv.sorted());
		}

		return Optional.empty();
	}
}
