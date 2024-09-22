package fi.dy.masa.malilib.sync.data;

import java.util.List;
import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.Sherds;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import fi.dy.masa.malilib.util.Constants;

public class SyncSingleStack extends SyncData implements SingleStackInventory.SingleStackBlockEntityInventory
{
    private static final String SHERDS_KEY = "sherds";
    private static final String POT_ITEM_KEY = "item";
    private static final String RECORD_KEY = "RecordItem";
    private static final String ITEM_ENTITY_KEY = "Item";
    private Sherds sherds;
    private ItemStack stack;
    private boolean recordItem;
    private boolean itemEntity;
    private boolean hasSherds;

    public SyncSingleStack(BlockEntityType<?> type, BlockPos pos, BlockState state, World world)
    {
        super(type, pos, state, world);
        this.initData();
    }

    public SyncSingleStack(EntityType<?> type, World world, int entityId)
    {
        super(type, world, entityId);
        this.initData();
    }

    public SyncSingleStack(BlockEntity be)
    {
        this(be.getType(), be.getPos(), be.getCachedState(), be.getWorld());
        this.copyNbtFromBlockEntity(be);
        //this.readCustomDataFromNbt(this.getNbt());
    }

    public SyncSingleStack(Entity entity)
    {
        this(entity.getType(), entity.getWorld(), entity.getId());
        this.copyNbtFromEntity(entity);
        //this.readCustomDataFromNbt(this.getNbt());
    }

    protected void initInventory()
    {
        this.initData();
    }

    protected void initAttributes(Entity entity)
    {
        // NO-OP
    }

    private void initData()
    {
        this.sherds = Sherds.DEFAULT;
        this.stack = ItemStack.EMPTY;
        this.recordItem = false;
        this.hasSherds = false;
    }

    public BlockEntity asBlockEntity()
    {
        return null;
    }

    public ItemStack getStack()
    {
        return this.stack;
    }

    public void setStack(ItemStack stack)
    {
        this.stack = stack;
    }

    public void markDirty()
    {
        // NO-OP
    }

    public Sherds getSherds() {
        return this.sherds;
    }

    public void readFrom(ItemStack stack)
    {
        this.readComponents(stack);
    }

    public ItemStack asStack()
    {
        ItemStack itemStack = Items.DECORATED_POT.getDefaultStack();
        itemStack.applyComponentsFrom(this.createComponentMap());
        return itemStack;
    }

    public static ItemStack getStackWith(Sherds sherds)
    {
        ItemStack itemStack = Items.DECORATED_POT.getDefaultStack();
        itemStack.set(DataComponentTypes.POT_DECORATIONS, sherds);
        return itemStack;
    }

    public void readNbt(@Nonnull NbtCompound nbt)
    {
        this.readCustomDataFromNbt(nbt);
        super.readNbt(nbt);
    }

    public void writeNbt(@Nonnull NbtCompound nbt)
    {
        this.writeCustomDataToNbt(nbt);
        super.writeNbt(nbt);
    }

    protected void readCustomDataFromNbt(NbtCompound nbt)
    {
        if (nbt.contains(SHERDS_KEY))
        {
            this.sherds = Sherds.fromNbt(nbt);
            this.hasSherds = true;
        }
        if (nbt.contains(RECORD_KEY, Constants.NBT.TAG_COMPOUND))
        {
            this.stack = ItemStack.fromNbt(this.getRegistryManager(), nbt.getCompound(RECORD_KEY)).orElse(ItemStack.EMPTY);
            this.recordItem = true;
        }
        else if (nbt.contains(ITEM_ENTITY_KEY, Constants.NBT.TAG_COMPOUND))
        {
            this.stack = ItemStack.fromNbt(this.getRegistryManager(), nbt.getCompound(ITEM_ENTITY_KEY)).orElse(ItemStack.EMPTY);
            this.itemEntity = true;
        }
        else if (nbt.contains(POT_ITEM_KEY, Constants.NBT.TAG_COMPOUND))
        {
            this.stack = ItemStack.fromNbt(this.getRegistryManager(), nbt.getCompound(POT_ITEM_KEY)).orElse(ItemStack.EMPTY);
        }
        else
        {
            this.stack = ItemStack.EMPTY;
        }
    }

    protected void writeCustomDataToNbt(NbtCompound nbt)
    {
        if (this.hasSherds)
        {
            this.sherds.toNbt(nbt);
        }
        if (!this.stack.isEmpty())
        {
            if (this.recordItem)
            {
                nbt.put(RECORD_KEY, this.stack.toNbt(this.getRegistryManager()));
            }
            else if (this.itemEntity)
            {
                nbt.put(ITEM_ENTITY_KEY, this.stack.toNbt(this.getRegistryManager()));
            }
            else
            {
                nbt.put(POT_ITEM_KEY, this.stack.toNbt(this.getRegistryManager()));
            }
        }
    }

    protected void readComponents(SyncData.ComponentsAccess components)
    {
        super.readComponents(components);
        this.sherds = components.getOrDefault(DataComponentTypes.POT_DECORATIONS, Sherds.DEFAULT);
        this.stack = components.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT).copyFirstStack();
    }

    protected void addComponents(ComponentMap.Builder builder)
    {
        super.addComponents(builder);
        if (this.hasSherds)
        {
            builder.add(DataComponentTypes.POT_DECORATIONS, this.sherds);
        }
        if (!this.recordItem)
        {
            builder.add(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(List.of(this.stack)));
        }
    }
}
