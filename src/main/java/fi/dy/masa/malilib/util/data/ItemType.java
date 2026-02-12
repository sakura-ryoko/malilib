package fi.dy.masa.malilib.util.data;

import java.util.Objects;
import javax.annotation.Nonnull;
import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

/**
 * A wrapper around ItemStack, that implements hashCode() and equals().
 * Whether or not the NBT data is considered by those methods,
 * depends on the checkNBT argument to the constructor.
 */
public class ItemType
{
    public static final Codec<ItemType> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    ItemStack.CODEC.fieldOf("stack").forGetter(get -> get.stack),
                    PrimitiveCodec.BOOL.fieldOf("checkNBT").forGetter(get -> get.checkNBT)
            ).apply(inst, ItemType::new)
    );
    public static final Codec<ItemType> SIMPLE_CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    ItemStack.CODEC.fieldOf("stack").forGetter(get -> get.stack)
            ).apply(inst, ItemType::new)
    );
    public static final StreamCodec<@NotNull RegistryFriendlyByteBuf, @NotNull ItemType> PACKET_CODEC = new StreamCodec<>()
    {
        @Override
        public void encode(@Nonnull RegistryFriendlyByteBuf buf, ItemType value)
        {
            ItemStack.STREAM_CODEC.encode(buf, value.stack);
            ByteBufCodecs.BOOL.encode(buf, value.checkNBT);
        }

        @Override
        public @Nonnull ItemType decode(@Nonnull RegistryFriendlyByteBuf buf)
        {
            return new ItemType(
                    ItemStack.STREAM_CODEC.decode(buf),
                    ByteBufCodecs.BOOL.decode(buf)
            );
        }
    };

    private ItemStack stack;
    private final boolean checkNBT;

    public ItemType(ItemStack stack)
    {
        this(stack, true, true);
    }

    public ItemType(ItemStack stack, boolean checkNBT)
    {
        this(stack, true, checkNBT);
    }

    public ItemType(ItemStack stack, boolean copy, boolean checkNBT)
    {
        this.stack = stack.isEmpty() ? ItemStack.EMPTY : (copy ? stack.copy() : stack);
        this.checkNBT = checkNBT;
    }

    public ItemStack getStack()
    {
        return this.stack;
    }

    public boolean checkNBT()
    {
        return this.checkNBT;
    }

    public void setStack(ItemStack stack)
    {
        this.stack = stack;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.stack.getItem().hashCode();

        if (this.checkNBT())
        {
            result = prime * result + (this.stack.getComponents() != null ? this.stack.getComponents().hashCode() : 0);
        }

        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;

        ItemType other = (ItemType) obj;

        if (this.stack.isEmpty() || other.stack.isEmpty())
        {
            return this.stack.isEmpty() == other.stack.isEmpty();
        }
        else
        {
            if (this.stack.getItem() != other.stack.getItem())
            {
                return false;
            }

            return this.checkNBT() == false || Objects.equals(this.stack.getComponents(), other.stack.getComponents());
        }
    }

    @Override
    public String toString()
    {
        if (this.checkNBT())
        {
            Identifier rl = BuiltInRegistries.ITEM.getKey(this.stack.getItem());
            return rl + " " + this.stack.getComponents();
        }
        else
        {
            return BuiltInRegistries.ITEM.getKey(this.stack.getItem()).toString();
        }
    }
}
