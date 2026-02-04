package com.xiaoyue.celestial_invoker.content.entities;

import com.xiaoyue.celestial_invoker.content.ancillary.helper.NBTSerialHelper;
import com.xiaoyue.celestial_invoker.content.generic.builder.ArrowDataBuilder;
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
    public ArrowDataBuilder builder = null;

    public GenericArrowEntity(EntityType<? extends AbstractArrow> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public GenericArrowEntity(LivingEntity shooter, Level pLevel, ItemStack arrow, ItemStack bow, ArrowDataBuilder builder) {
        super(CIEntities.GENERIC_ARROW.get(), shooter, pLevel, arrow, bow);
        this.arrow = arrow;
        this.bow = bow;
        this.builder = builder;
        setData();
    }

    private void setData() {
        if (builder != null) {
            this.setBaseDamage(builder.damage);
            this.setPierceLevel(builder.pierce);
        }
    }

    public void setBuilder(ArrowDataBuilder builder) {
        this.builder = builder;
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
        return builder != null && builder.ignoreWater ? 0.99f : 0.6f;
    }

    @Override
    public boolean isNoGravity() {
        return builder != null && builder.ignoreGravity;
    }

    @Override
    public void tick() {
        super.tick();
        if (builder != null && builder.onTick != null) {
            builder.onTick.accept(this);
        }
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
