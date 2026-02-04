package com.xiaoyue.celestial_invoker.content.entities;

import com.xiaoyue.celestial_invoker.content.common.ArrowContext;
import com.xiaoyue.celestial_invoker.content.common.helper.NBTSerialHelper;
import com.xiaoyue.celestial_invoker.register.CIEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

public class GenericArrowEntity extends AbstractArrow implements IEntityWithComplexSpawn {

    public ItemStack bow, arrow = ItemStack.EMPTY;
    public ArrowContext context = null;

    public GenericArrowEntity(EntityType<? extends AbstractArrow> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public GenericArrowEntity(LivingEntity shooter, Level pLevel, ItemStack arrow, ItemStack bow, ArrowContext context) {
        super(CIEntities.GENERIC_ARROW.get(), shooter, pLevel, arrow, bow);
        this.arrow = arrow;
        this.bow = bow;
        this.context = context;
        setData();
    }

    private void setData() {
        if (context != null) {
            this.setBaseDamage(context.damage);
            this.setPierceLevel(context.pierce);
        }
    }

    public void setContext(ArrowContext context) {
        this.context = context;
    }

    @Override
    protected ItemStack getPickupItem() {
        return arrow;
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return arrow;
    }

    @Override
    protected float getWaterInertia() {
        return context != null && context.ignoreWater ? 0.99f : 0.6f;
    }

    @Override
    public boolean isNoGravity() {
        return context != null && context.ignoreGravity;
    }

    @Override
    public void tick() {
        super.tick();
        if (context != null && context.onTick != null) {
            context.onTick.accept(this);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if (context != null && context.hitEntity != null) {
            context.hitEntity.accept(this, pResult.getEntity());
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
        if (context != null && context.hitBlock != null) {
            context.hitBlock.accept(this, pResult);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        NBTSerialHelper.save(tag, "Bow", bow);
        NBTSerialHelper.save(tag, "Arrow", arrow);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        bow = NBTSerialHelper.loadOrDefault(tag, "Bow", ItemStack.class, ItemStack.EMPTY);
        arrow = NBTSerialHelper.loadOrDefault(tag, "Arrow", ItemStack.class, ItemStack.EMPTY);
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, bow);
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, arrow);
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf additionalData) {
        bow = ItemStack.OPTIONAL_STREAM_CODEC.decode(additionalData);
        arrow = ItemStack.OPTIONAL_STREAM_CODEC.decode(additionalData);
    }
}
