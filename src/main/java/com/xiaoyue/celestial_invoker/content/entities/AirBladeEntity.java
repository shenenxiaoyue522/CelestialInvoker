package com.xiaoyue.celestial_invoker.content.entities;

import com.xiaoyue.celestial_invoker.content.common.Bindings;
import com.xiaoyue.celestial_invoker.content.generic.item.api.IAirBladeUser;
import com.xiaoyue.celestial_invoker.register.CIEntities;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

public class AirBladeEntity extends ThrowableProjectile implements IEntityAdditionalSpawnData {

    public float damage = 2f;
    public int life = 100;
    public float zRot = 0f;
    public ItemStack stack = ItemStack.EMPTY;

    public AirBladeEntity(EntityType<? extends ThrowableProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public AirBladeEntity(Level pLevel) {
        super(CIEntities.AIR_BLADE.get(), pLevel);
    }

    @Override
    protected void defineSynchedData() {
    }

    public void setData(Entity owner, float damage, int life, float zRot, ItemStack stack) {
        setOwner(owner);
        setPos(owner.getX(), owner.getEyeY() - 0.5f, owner.getZ());
        this.setData(damage, life, zRot, stack);
    }

    public void setData(float damage, int life, float zRot, ItemStack stack) {
        this.damage = damage;
        this.life = life;
        this.zRot = zRot;
        this.stack = stack;
        Vec3 v3 = this.getDeltaMovement();
        float f = Mth.sqrt((float) (v3.x * v3.x + v3.z * v3.z));
        this.setXRot((float) (Mth.atan2(v3.y(), f) * (double) (180F / (float) Math.PI)));
        this.setYRot((float) (Mth.atan2(v3.x(), v3.z()) * (double) (180F / (float) Math.PI)));
        this.xRotO = this.getXRot();
        this.yRotO = this.getYRot();
    }

    @Override
    public void tick() {
        Vec3 speed = getDeltaMovement();
        super.tick();
        setDeltaMovement(speed);
        life--;
        if (life <= 0) {
            discard();
        }
        ParticleOptions particle;
        if (stack.getItem() instanceof IAirBladeUser user) {
            particle = user.getTrajectoryParticles();
            user.tickUpdate(this);
        } else {
            particle = ParticleTypes.CRIT;
        }
        double vx = speed.x;
        double vy = speed.y;
        double vz = speed.z;
        for (int i = 0; i < 4; ++i) {
            level().addParticle(particle, this.getX() + vx * (double) i / 4.0D, this.getY() + vy * (double) i / 4.0D,
                    this.getZ() + vz * (double) i / 4.0D, 0, 0, 0);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!level().isClientSide) {
            Entity entity = result.getEntity();
            Entity owner = this.getOwner();
            DamageSource source;
            if (stack.getItem() instanceof IAirBladeUser user) {
                source = user.getSource(this, owner);
            } else {
                source = new DamageSource(Bindings.getDamageSource(level(), DamageTypes.MOB_PROJECTILE), owner, this);
            }
            float dmg = damage;
            if (stack.getItem() instanceof IAirBladeUser user) {
                if (user.canHurt(this, entity, dmg)) {
                    entity.hurt(source, dmg);
                }
            } else {
                entity.hurt(source, dmg);
            }
            if (owner instanceof LivingEntity) {
                doEnchantDamageEffects((LivingEntity) owner, entity);
            }
            if (stack.getItem() instanceof IAirBladeUser user) {
                user.onHitEntity(this, entity);
            } else {
                discard();
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!level().isClientSide) {
            if (stack.getItem() instanceof IAirBladeUser user) {
                user.onHitBlock(this, result.getBlockPos());
            }
            discard();
        }
    }

    @Override
    protected float getGravity() {
        return 0;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("Item", stack.serializeNBT());
        tag.putFloat("Damage", damage);
        tag.putFloat("ZRot", zRot);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        stack = Bindings.getItemFromTag(tag, "Item");
        damage = tag.getFloat("Damage");
        zRot = tag.getFloat("ZRot");
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buf) {
        buf.writeFloat(zRot);
        buf.writeItemStack(stack, true);
        buf.writeFloat(damage);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf buf) {
        zRot = buf.readFloat();
        stack = buf.readItem();
        damage = buf.readFloat();
    }

    @Override
    public void shootFromRotation(Entity user, float xr, float yr, float angle, float v, float rand) {
        float f = -Mth.sin(yr * 0.017453292F) * Mth.cos(xr * 0.017453292F);
        float f1 = -Mth.sin((xr + angle) * 0.017453292F);
        float f2 = Mth.cos(yr * 0.017453292F) * Mth.cos(xr * 0.017453292F);
        this.shoot(f, f1, f2, v, rand);
        Vec3 vec3 = user.getDeltaMovement();
        if (vec3.length() < v * 0.75) return;
        this.setDeltaMovement(this.getDeltaMovement().add(vec3.x, user.onGround() ? 0.0 : vec3.y, vec3.z));
    }
}
