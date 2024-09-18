package fi.dy.masa.malilib.sync.fe;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class FakeHostile extends FakeMob implements Monster
{
    public FakeHostile(EntityType<?> type, World world, int entityId)
    {
        super(type, world, entityId);
        //this.experiencePoints = 5;
    }

    public FakeHostile(Entity input)
    {
        super(input);

        if (input instanceof HostileEntity)
        {
            this.buildAttributes(createHostileAttributes());
            this.readCustomDataFromNbt(this.getNbt());
        }
    }

    public static DefaultAttributeContainer.Builder createHostileAttributes()
    {
        return MobEntity.createMobAttributes().add(EntityAttributes.ATTACK_DAMAGE);
    }

    public boolean shouldDropXp()
    {
        return false;
    }

    protected boolean shouldDropLoot()
    {
        return false;
    }

    public boolean isAngryAt(PlayerEntity player)
    {
        return false;
    }

    public ItemStack getProjectileType(ItemStack stack)
    {
        return ItemStack.EMPTY;
    }
}
