package malilib.util.inventory;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;
import malilib.util.MathUtils;
import malilib.util.data.Constants;
import malilib.util.data.tag.CompoundData;
import malilib.util.data.tag.ListData;
import malilib.util.data.tag.converter.DataConverterNbt;
import malilib.util.game.wrap.DefaultedList;
import malilib.util.nbt.NbtKeys;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;

/**
 * This is useful as an Abstract Inventory Holder for all Inventory types.
 * This is primarily for use with {@link malilib.util.data.tag.DataView} in a predictable manner.
 * In the future, it can then add in the future <b>NbtReaderView</b> support as well from 1.21.6+
 */
public class DataInventoryView implements InventoryView
// implements AutoCloseable
{
	public static final Comparator<EntrySlot> COMPARATOR = new EntrySlotComparator();
	public static final int VILLAGER_SIZE = 8;
	public static final int DEFAULT_SIZE = 27;
	public static final int PLAYER_SIZE = 36;
	public static final int DOUBLE_SIZE = 54;
	public static final int MAX_SIZE = 256;
	private HashSet<EntrySlot> items;

	private DataInventoryView() {}

	/**
	 * Create a new blank {@link DataInventoryView} of the size specified.
	 *
	 * @param size -
	 * @return -
	 */
	public static DataInventoryView create(int size)
	{
		DataInventoryView newInv = new DataInventoryView();
		size = getAdjustedSize(MathUtils.clamp(size, 1, MAX_SIZE));
		newInv.buildEmptyList(size);
		return newInv;
	}

	/**
	 * Common Function to try to get the "corrected" Inventory size based on
	 * an existing `list.size()` for example.
	 * <br>
	 *
	 * @param size The Size to adjust.
	 * @return The Adjusted Size.
	 */
	public static int getAdjustedSize(int size)
	{
		if (size <= VILLAGER_SIZE)
		{
			return size;
		}
		else if (size <= DEFAULT_SIZE)
		{
			return DEFAULT_SIZE;
		}
		else if (size <= PLAYER_SIZE)
		{
			return PLAYER_SIZE;
		}
		else if (size <= DOUBLE_SIZE)
		{
			return DOUBLE_SIZE;
		}
		else
		{
			return Math.min(size, MAX_SIZE);
		}
	}

	private void buildEmptyList(int size) throws RuntimeException
	{
		if (this.items != null)
		{
			throw new RuntimeException("List not empty!");
		}

		this.items = new HashSet<EntrySlot>();

		for (int i = 0; i < size; i++)
		{
			this.items.add(new EntrySlot(i, ItemStack.EMPTY));
		}
	}

	/**
	 * This exists because an NBT List can have empty slots not accounted for in the middle of its current size;
	 * Such as an empty slot in the middle of a Hopper Minecart.  This code fixes the problem.
	 *
	 * @param slotsUsed -
	 */
	private void verifySize(List<Integer> slotsUsed, int maxSlot)
	{
		int size = Math.max(this.size(), maxSlot);

		size = getAdjustedSize(size);

		for (int i = 0; i < size; i++)
		{
			if (!slotsUsed.contains(i))
			{
				this.items.add(new EntrySlot(i, ItemStack.EMPTY));
			}
		}
	}

	/**
	 * Resort this {@link DataInventoryView} by Slot ID.
	 */
	public DataInventoryView sorted()
	{
		if (this.size() > 0)
		{
			List<EntrySlot> sorted = new ArrayList<>(this.items);
			sorted.sort(COMPARATOR);
			this.items.clear();
			this.items.addAll(sorted);
		}

		return this;
	}

	public boolean isEmpty()
	{
		if (this.items == null || this.items.isEmpty())
		{
			return true;
		}
		AtomicBoolean bool = new AtomicBoolean(true);

		this.items.forEach(
				(slot) ->
				{
					if (!slot.stack().isEmpty())
					{
						bool.set(false);
					}
				});

		return bool.get();
	}

	public int size()
	{
		if (this.items == null)
		{
			return -1;
		}
		return this.items.size();
	}

	//	@Override
	public void close() throws Exception
	{
		this.items.clear();
	}

	/**
	 * Return this Inventory as a {@link DefaultedList}
	 *
	 * @return -
	 */
	public DefaultedList<ItemStack> toVanillaList(int size)
	{
		if (this.isEmpty())
		{
			return DefaultedList.empty();
		}

		size = getAdjustedSize(MathUtils.clamp(size, this.size(), MAX_SIZE));

		DefaultedList<ItemStack> list = DefaultedList.ofSize(size, ItemStack.EMPTY);
		AtomicInteger i = new AtomicInteger(0);

		this.items.forEach(
				(slot) ->
				{
					list.set(slot.slot(), slot.stack());
					i.getAndIncrement();
				});

		return list;
	}

	/**
	 * Create a new {@link DataInventoryView} from a {@link DefaultedList}; making all the slot numbers the stack index.
	 *
	 * @param list -
	 * @return -
	 */
	public static Optional<DataInventoryView> fromVanillaList(@Nonnull DefaultedList<ItemStack> list)
	{
		int size = list.size();
		if (size < 1)
		{
			return Optional.empty();
		}

		size = getAdjustedSize(MathUtils.clamp(size, 1, MAX_SIZE));
		DataInventoryView newInv = new DataInventoryView();
		newInv.items = new HashSet<>();

		for (int i = 0; i < size; i++)
		{
			newInv.items.add(new EntrySlot(i, list.get(i)));
		}

		return Optional.of(newInv);
	}

	/**
	 * Convert this Inventory to a Vanilla Inventory object.
	 * Supports oversized Inventories (MAX_SIZE) and DoubleInventory (DOUBLE_SIZE); or defaults to (DEFAULT_SIZE)
	 *
	 * @return -
	 */
	// todo when Vanilla allows for a 'SimpleInventory' Object ...
	public Optional<IInventory> toInventory(final int size)
	{
		if (this.isEmpty())
		{
			return Optional.empty();
		}

		int sizeAdj = getAdjustedSize(MathUtils.clamp(size, this.size(), MAX_SIZE));
		InventoryBasic inv = new InventoryBasic(new TextComponentString("dataInventory"), sizeAdj);
		AtomicInteger i = new AtomicInteger(0);

		this.items.forEach(
				(slot) ->
				{
					inv.setInventorySlotContents(slot.slot(), slot.stack());
					i.getAndIncrement();
				});

		return Optional.of(inv);
	}

	/**
	 * Creates a new {@link DataInventoryView} from a vanilla Inventory object; making all the slot numbers the stack index.
	 *
	 * @param inv -
	 * @return -
	 */
	// todo when Vanilla allows for a 'SimpleInventory' Object ...
	public static DataInventoryView fromInventory(@Nonnull IInventory inv)
	{
		DataInventoryView newInv = new DataInventoryView();
		List<Integer> slotsUsed = new ArrayList<>();
		int size = inv.getSizeInventory();
		int maxSlot = 0;

		size = getAdjustedSize(MathUtils.clamp(size, 1, MAX_SIZE));
		newInv.items = new HashSet<>();

		for (int i = 0; i < size; i++)
		{
			EntrySlot slot = new EntrySlot(i, inv.getStackInSlot(i));

			newInv.items.add(slot);
			slotsUsed.add(slot.slot());

			if (slot.slot() > maxSlot)
			{
				maxSlot = slot.slot();
			}
		}

		newInv.verifySize(slotsUsed, maxSlot);

		return newInv;
	}

	/**
	 * Creates a new {@link DataInventoryView} from a vanilla Inventory object; making all the slot numbers the stack index.
	 *
	 * @param inv -
	 * @return -
	 */
	public static DataInventoryView fromInventory(@Nonnull Container inv)
	{
		DataInventoryView newInv = new DataInventoryView();
		List<Integer> slotsUsed = new ArrayList<>();
		int size = inv.getInventory().size();
		int maxSlot = 0;

		size = getAdjustedSize(MathUtils.clamp(size, 1, MAX_SIZE));
		newInv.items = new HashSet<>();

		for (int i = 0; i < size; i++)
		{
			EntrySlot slot = new EntrySlot(i, inv.getInventory().get(i));

			newInv.items.add(slot);
			slotsUsed.add(slot.slot());

			if (slot.slot() > maxSlot)
			{
				maxSlot = slot.slot();
			}
		}

		newInv.verifySize(slotsUsed, maxSlot);

		return newInv;
	}

	/**
	 * Uses the newer Vanilla 'WriterView' interface to write this Inventory to it; using our 'NbtView' wrapper.
	 * @param registry RegistryAccess object
	 * @return -
	 * @implNote This is used after 1.21.6
	 */
//	public @Nullable NbtView toNbtWriterView(@Nonnull RegistryAccess registry)
//	{
//		if (this.isEmpty())
//		{
//			return null;
//		}
//
//		final int size = getAdjustedSize(this.size());
//
//		NbtView view = NbtView.getWriter(registry);
//		NonNullList<ItemStack> list = this.toVanillaList(size);
//
//		ContainerHelper.saveAllItems(Objects.requireNonNull(view.getWriter()), list);
//
//		return view;
//	}

	/**
	 * Uses the newer Vanilla 'ReaderView' interface to create a new NbtInventory; using our 'NbtView' wrapper.
	 * @param view -
	 * @param size -
	 * @return -
	 * @implNote This is used after 1.21.6
	 */
//	public static @Nullable DataInventory fromNbtReaderView(@Nonnull NbtView view, int size)
//	{
//		if (size < 1)
//		{
//			return null;
//		}
//
//		size = getAdjustedSize(MathUtils.clamp(size, 1, MAX_SIZE));
//		NonNullList<ItemStack> list = NonNullList.withSize(size, ItemStack.EMPTY);
//
//		ContainerHelper.saveAllItems(Objects.requireNonNull(view.getReader()), list);
//		return fromVanillaList(list);
//	}

	/**
	 * Converts the first {@link DataInventoryView} element to a single {@link CompoundData}.
	 *
	 * @return -
	 * @throws RuntimeException -
	 */
	public CompoundData toDataSingle() throws RuntimeException // (@Nonnull RegistryAccess registry)
	{
		if (this.size() > 1)
		{
			throw new RuntimeException("Inventory is too large for a single entry!");
		}

		EntrySlot slot = this.items.stream().findFirst().orElse(new EntrySlot(0, ItemStack.EMPTY));

		if (!slot.stack().isEmpty())
		{
			return slot.toData();
		}

		return new CompoundData();
	}

	/**
	 * Converts this {@link DataInventoryView} to a basic {@link ListData} with Slot information.
	 *
	 * @return -
	 * @throws RuntimeException -
	 */
	public ListData toDataList() // (@Nonnull RegistryAccess registry)
	{
		ListData list = new ListData(Constants.NBT.TAG_COMPOUND);
		if (this.isEmpty())
		{
			return list;
		}

		this.items.forEach(
				(slot) ->
				{
					if (!slot.stack().isEmpty())
					{
						list.add(slot.toData());
					}
				});

		return list;
	}

	/**
	 * Writes this {@link DataInventoryView} to a Data Type (List or Compound) using a key; with slot information.
	 *
	 * @param type -
	 * @param key  -
	 * @return -
	 * @throws RuntimeException -
	 */
	public CompoundData toData(int type, String key) throws RuntimeException
	// (@Nonnull RegistryAccess registry)
	{
		CompoundData data = new CompoundData();

		if (type == Constants.NBT.TAG_LIST)
		{
			ListData list = this.toDataList();

			if (list.isEmpty())
			{
				return data;
			}

			return data.put(key, list);
		}
		else if (type == Constants.NBT.TAG_COMPOUND)
		{
			return data.put(key, this.toDataSingle());
		}

		throw new RuntimeException("Unsupported Data Type!");
	}

	/**
	 * Creates a new {@link DataInventoryView} from a Data Type (List or Compound) using a key; retains slot information.
	 *
	 * @param data     -
	 * @param key      The Key of the Data to read
	 * @param noSlotId If the List doesn't include Slots, generate them using inventory index
	 * @return -
	 * @throws RuntimeException -
	 */
	public static Optional<DataInventoryView> fromData(@Nonnull CompoundData data, String key, boolean noSlotId)
			throws RuntimeException
	// (@Nonnull RegistryAccess registry)
	{
		if (data.isEmpty())
		{
			return Optional.empty();
		}

		if (data.containsList(key, Constants.NBT.TAG_COMPOUND))
		{
			return fromDataList(data.getList(key, Constants.NBT.TAG_COMPOUND), noSlotId);
		}
		else if (data.contains(key, Constants.NBT.TAG_COMPOUND))
		{
			return fromDataSingle(data.getCompound(key));
		}
		else
		{
			throw new RuntimeException("Invalid Data Type!");
		}
	}

	/**
	 * Creates a new {@link DataInventoryView} from a single-member {@link CompoundData} containing a single item with a slot number.
	 *
	 * @param data -
	 * @return -
	 * @throws RuntimeException -
	 */
	public static Optional<DataInventoryView> fromDataSingle(@Nonnull CompoundData data) throws RuntimeException
	// (@Nonnull RegistryAccess registry)
	{
		if (data.isEmpty())
		{
			return Optional.empty();
		}
		DataInventoryView newInv = new DataInventoryView();
		CompoundData tag = checkDataForIDOverrides(data);

		newInv.items = new HashSet<>();
		newInv.items.add(EntrySlot.fromData(tag));

		return Optional.of(newInv);
	}

	/**
	 * Creates a new {@link DataInventoryView} from an {@link ListData}; utilizing Slot information.
	 *
	 * @param list     -
	 * @param noSlotId If the List doesn't include Slots, generate them using inventory index
	 * @return -
	 * @throws RuntimeException -
	 */
	public static Optional<DataInventoryView> fromDataList(@Nonnull ListData list, boolean noSlotId)
			throws RuntimeException
	// (@Nonnull RegistryAccess registry)
	{
		if (list.isEmpty())
		{
			return Optional.empty();
		}
		else if (list.size() > MAX_SIZE)
		{
			throw new RuntimeException("Data List is too large!");
		}

		int size = list.size();
		size = getAdjustedSize(MathUtils.clamp(size, 1, MAX_SIZE));
		DataInventoryView newInv = new DataInventoryView();
		List<Integer> slotsUsed = new ArrayList<>();
		int maxSlot = 0;

		newInv.items = new HashSet<>();

		for (int i = 0; i < list.size(); i++)
		{
			CompoundData tag = checkDataForIDOverrides(list.getCompoundAt(i));
			EntrySlot slot;

			// Some lists, such as the "Inventory" tag does not include slot ID's
			if (noSlotId)
			{
				slot = EntrySlot.fromData(tag);
				slot.setSlot(i);
			}
			else
			{
				slot = EntrySlot.fromData(tag);
			}

			newInv.items.add(slot);
			slotsUsed.add(slot.slot());

			if (slot.slot() > maxSlot)
			{
				maxSlot = slot.slot();
			}
		}

		newInv.verifySize(slotsUsed, maxSlot);

		return Optional.of(newInv);
	}

	/**
	 * Primarily for Broken NBT (Item ID) situations where the Server
	 * might not be equal in version over ViaVersion, and the like.
	 * Such problems arise under the DataInventory.
	 *
	 * @param in -
	 * @return -
	 */
	private static CompoundData checkDataForIDOverrides(CompoundData in)
	{
//		String id = in.getStringOrDefault(NbtKeys.ID, "");
//
//		if (NbtOverrides.ID_OVERRIDES.containsKey(id))
//		{
//			id = NbtOverrides.ID_OVERRIDES.get(id);
//			in.putString(NbtKeys.ID, id);
//		}

		return in;
	}

	@Override
	public int getSize()
	{
		return this.size();
	}

	@Override
	public ItemStack getStack(int slot)
	{
		AtomicReference<ItemStack> result = new AtomicReference<>(ItemStack.EMPTY);

		this.items.forEach(
				entry ->
				{
					if (entry.slot() == slot)
					{
						result.set(entry.stack().copy());
					}
				});

		return result.get();
	}

	/**
	 * Equivalence with <b>ItemStackWithSlot</b> from ~1.21.8+
	 */
	public static class EntrySlot
	{
		private final ItemStack stack;
		private int slot;

		public EntrySlot(int slot, ItemStack stack)
		{
			this.slot = slot;
			this.stack = stack.copy();
		}

		public void setSlot(int slot)
		{
			this.slot = slot;
		}

		public int slot() {return this.slot;}

		public ItemStack stack() {return this.stack;}

		// todo Use DataOps to Serialize ItemStack in the future
		public CompoundData toData() // (@Nonnull RegistryAccess registry)
		{
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setByte(NbtKeys.SLOT, (byte) this.slot);
			this.stack.writeToNBT(nbt);
			return DataConverterNbt.fromVanillaCompound(nbt);
		}

		// todo Use DataOps to Deserialize ItemStack in the future
		public static EntrySlot fromData(CompoundData data) // (@Nonnull RegistryAccess registry)
		{
			final int slot = data.getByteOrDefault(NbtKeys.SLOT, (byte) 0) & 0xFF;
			final ItemStack stack = new ItemStack(DataConverterNbt.toVanillaCompound(data));
			return new EntrySlot(slot, stack.copy());
		}
	}

	public static class EntrySlotComparator implements Comparator<EntrySlot>
	{
		@Override
		public int compare(EntrySlot o1, EntrySlot o2)
		{
			return Integer.compare(o1.slot(), o2.slot());
		}
	}
}
