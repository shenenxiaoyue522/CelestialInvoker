package com.xiaoyue.celestial_invoker.content.entities;

import com.xiaoyue.celestial_invoker.content.common.ArrowDataHolder;
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
    public ArrowDataHolder holder = null;

    public GenericArrowEntity(EntityType<? extends AbstractArrow> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public GenericArrowEntity(LivingEntity shooter, Level pLevel, ItemStack arrow, ItemStack bow, ArrowDataHolder holder) {
        super(CIEntities.GENERIC_ARROW.get(), shooter, pLevel, arrow, bow);
        this.arrow = arrow;
        this.bow = bow;
        this.holder = holder;
        setData();
    }

    private void setData() {
        if (holder != null) {
            this.setBaseDamage(holder.damage);
            this.setPierceLevel(holder.pierce);
        }
    }

    public void setHolder(ArrowDataHolder holder) {
        this.holder = holder;
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
        return holder != null && holder.ignoreWater ? 0.99f : 0.6f;
    }

    @Override
    public boolean isNoGravity() {
        return holder != null && holder.ignoreGravity;
    }

    @Override
    public void tick() {
        super.tick();
        if (holder != null && holder.onTick != null) {
            holder.onTick.accept(this);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if (holder != null && holder.hitEntity != null) {
            holder.hitEntity.accept(this, pResult.getEntity());
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
        if (holder != null && holder.hitBlock != null) {
            holder.hitBlock.accept(this, pResult);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("Bow", bow.save(registryAccess(), tag));
        tag.put("Arrow", arrow.save(registryAccess(), tag));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        bow = ItemStack.parse(registryAccess(), tag.getCompound("Bow")).orElse(ItemStack.EMPTY);
        arrow = ItemStack.parse(registryAccess(), tag.getCompound("Arrow")).orElse(ItemStack.EMPTY);
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
