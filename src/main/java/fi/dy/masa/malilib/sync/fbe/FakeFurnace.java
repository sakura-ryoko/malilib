package fi.dy.masa.malilib.sync.fbe;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
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
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;

public class FakeFurnace extends FakeLockableContainer implements SidedInventory
{
    private static final int MAX_SLOTS = 27;
    private final Object2IntOpenHashMap<Identifier> recipesUsed;
    private int burnTime;
    private int fuelTime;
    private int cookTime;
    private int cookTimeTotal;

    public FakeFurnace(BlockEntityType<?> type, BlockPos pos, BlockState state, int maxSlots)
    {
        super(type, pos, state, maxSlots);
        this.recipesUsed = new Object2IntOpenHashMap<>();
    }

    public FakeFurnace(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        this(type, pos, state, MAX_SLOTS);
    }

    public FakeFurnace(BlockEntity be, World world)
    {
        this(be.getType(), be.getPos(), be.getCachedState());
        //this.setWorld(world);
        this.copyFromBlockEntityInternal(be, world.getRegistryManager());
    }

    private boolean isBurning() {
        return this.burnTime > 0;
    }


    public int[] getAvailableSlots(Direction side)
    {
        if (side == Direction.DOWN)
        {
            return new int[]{2,1};
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
        nbt.putShort("BurnTime", (short)this.burnTime);
        nbt.putShort("CookTime", (short)this.cookTime);
        nbt.putShort("CookTimeTotal", (short)this.cookTimeTotal);
        Inventories.writeNbt(nbt, this.inventory, registry);
        NbtCompound nbtCompound = new NbtCompound();
        this.recipesUsed.forEach((identifier, count) -> nbtCompound.putInt(identifier.toString(), count));
        nbt.put("RecipesUsed", nbtCompound);
    }
}
