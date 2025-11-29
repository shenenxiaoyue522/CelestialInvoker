package com.xiaoyue.celestial_invoker.content.entities;

import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;

@SerialClass
public abstract class SimpleThrowEntity extends AbstractArrow implements IEntityAdditionalSpawnData {

    @SerialClass.SerialField
    public ItemStack weapon = ItemStack.EMPTY;

    protected SimpleThrowEntity(EntityType<? extends AbstractArrow> pEntityType, LivingEntity pShooter, Level pLevel) {
        super(pEntityType, pShooter, pLevel);
    }

    protected SimpleThrowEntity(EntityType<? extends AbstractArrow> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    protected SimpleThrowEntity(EntityType<? extends AbstractArrow> pEntityType, double pX, double pY, double pZ, Level pLevel) {
        super(pEntityType, pX, pY, pZ, pLevel);
    }

    public void setWeapon(ItemStack weapon) {
        this.weapon = weapon;
    }

    @Override
    public void playerTouch(Player pEntity) {
        if (this.ownedBy(pEntity) || this.getOwner() == null) {
            super.playerTouch(pEntity);
        }
    }

    @Override
    protected ItemStack getPickupItem() {
        return weapon;
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buf) {
        buf.writeItemStack(weapon, true);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf buf) {
        weapon = buf.readItem();
    }
}
