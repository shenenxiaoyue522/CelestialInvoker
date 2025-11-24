package com.xiaoyue.celestial_invoker.content.entities;

import com.xiaoyue.celestial_invoker.content.generic.builder.ArrowDataBuilder;
import com.xiaoyue.celestial_invoker.register.CIEntities;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;

@SerialClass
public class GenericArrowEntity extends AbstractArrow implements IEntityAdditionalSpawnData {

    @SerialClass.SerialField
    public ItemStack bow, arrow = ItemStack.EMPTY;
    @SerialClass.SerialField
    public ArrowDataBuilder builder = null;

    public GenericArrowEntity(EntityType<? extends AbstractArrow> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public GenericArrowEntity(Level pLevel, LivingEntity shooter) {
        super(CIEntities.GENERIC_ARROW.get(), shooter, pLevel);
    }

    public GenericArrowEntity(Level pLevel, LivingEntity shooter, ArrowDataBuilder builder) {
        super(CIEntities.GENERIC_ARROW.get(), shooter, pLevel);
        this.builder = builder;
        setData();
    }

    private void setData() {
        if (builder != null) {
            this.setBaseDamage(builder.damage);
            this.setKnockback(builder.knock);
            this.setPierceLevel(builder.pierce);
        }
    }

    public void setBuilder(ArrowDataBuilder builder) {
        this.builder = builder;
    }

    public void setBow(ItemStack bow) {
        this.bow = bow;
    }

    public void setArrow(ItemStack arrow) {
        this.arrow = arrow;
    }

    @Override
    protected ItemStack getPickupItem() {
        return arrow;
    }

    @Override
    protected float getWaterInertia() {
        return builder != null && builder.ignoreWater ? 0.99f : 0.6f;
    }

    @Override
    public boolean isNoGravity() {
        return builder != null && builder.ignoreGravity;
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if (builder != null && builder.hitEntity != null) {
            builder.hitEntity.accept(this, pResult.getEntity());
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
        if (builder != null && builder.hitBlock != null) {
            builder.hitBlock.accept(this, pResult);
        }
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buf) {
        buf.writeItemStack(bow, true);
        buf.writeItemStack(arrow, true);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf buf) {
        bow = buf.readItem();
        arrow = buf.readItem();
    }
}
