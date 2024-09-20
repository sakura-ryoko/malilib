package fi.dy.masa.malilib.sync.fbe;

import java.util.List;
import javax.annotation.Nonnull;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class FakeFurnace extends FakeLockableContainer implements SidedInventory
{
    private static final int MAX_SLOTS = 3;
    private final Object2IntOpenHashMap<Identifier> recipesUsed;
    private int burnTime;
    private int fuelTime;
    private int cookTime;
    private int cookTimeTotal;

    public FakeFurnace(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state, MAX_SLOTS);
        this.inventory = DefaultedList.ofSize(MAX_SLOTS, ItemStack.EMPTY);
        this.fuelTime = 0;
        this.recipesUsed = new Object2IntOpenHashMap<>();
    }

    public FakeFurnace(BlockEntity be, World world)
    {
        this(be.getType(), be.getPos(), be.getCachedState());
        System.out.print("be -> FakeFurnace\n");
        this.copyFromBlockEntityInternal(be, world.getRegistryManager());
    }

    public FakeFurnace createBlockEntity(BlockPos pos, BlockState state)
    {
        return new FakeFurnace(BlockEntityType.FURNACE, pos, state);
    }

    public boolean isBurning()
    {
        return this.burnTime > 0;
    }

    public int getFuelTime()
    {
        return this.fuelTime;
    }

    public int getCookTime()
    {
        return this.cookTime;
    }

    public int getCookTimeTotal()
    {
        return this.cookTimeTotal;
    }

    public int[] getAvailableSlots(Direction side)
    {
        if (side == Direction.DOWN)
        {
            return new int[]{2, 1};
        }
        else
        {
            return side == Direction.UP ? new int[]{0} : new int[]{1};
        }
    }

    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir)
    {
        return true;
    }

    public boolean canExtract(int slot, ItemStack stack, Direction dir)
    {
        return true;
    }

    public void setLastRecipe(@Nullable RecipeEntry<?> recipe)
    {
        if (recipe != null)
        {
            Identifier identifier = recipe.id();
            this.recipesUsed.addTo(identifier, 1);
        }
    }

    public Object2IntOpenHashMap<Identifier> getRecipesUsed()
    {
        return this.recipesUsed;
    }

    @Nullable
    public RecipeEntry<?> getLastRecipe()
    {
        return null;
    }

    public void unlockLastRecipe(PlayerEntity player, List<ItemStack> ingredients) {}

    public void readNbt(@Nonnull NbtCompound nbt, RegistryWrapper.WrapperLookup registry)
    {
        super.readNbt(nbt, registry);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.readNbt(nbt, this.inventory, registry);
        this.burnTime = nbt.getShort("BurnTime");
        this.cookTime = nbt.getShort("CookTime");
        this.cookTimeTotal = nbt.getShort("CookTimeTotal");
        this.fuelTime = 0;
        NbtCompound nbtCompound = nbt.getCompound("RecipesUsed");

        for (String string : nbtCompound.getKeys())
        {
            this.recipesUsed.put(Identifier.of(string), nbtCompound.getInt(string));
        }
    }

    public void writeNbt(@Nonnull NbtCompound nbt, RegistryWrapper.WrapperLookup registry)
    {
        super.writeNbt(nbt, registry);
        nbt.putShort("BurnTime", (short) this.burnTime);
        nbt.putShort("CookTime", (short) this.cookTime);
        nbt.putShort("CookTimeTotal", (short) this.cookTimeTotal);
        Inventories.writeNbt(nbt, this.inventory, registry);
        NbtCompound nbtCompound = new NbtCompound();
        this.recipesUsed.forEach((identifier, count) -> nbtCompound.putInt(identifier.toString(), count));
        nbt.put("RecipesUsed", nbtCompound);
    }
}
