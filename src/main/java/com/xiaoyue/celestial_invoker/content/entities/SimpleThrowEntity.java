package com.xiaoyue.celestial_invoker.content.entities;

import com.google.common.collect.Lists;
import com.xiaoyue.celestial_invoker.content.common.Bindings;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public abstract class SimpleThrowEntity extends AbstractArrow implements IEntityAdditionalSpawnData {

    public ItemStack weapon = ItemStack.EMPTY;
    public int loyalty = 0;
    public boolean foil;
    public int remainingHit = 1;
    public int clientSideReturnTridentTickCount;

    protected SimpleThrowEntity(EntityType<? extends AbstractArrow> pEntityType, LivingEntity pShooter, Level pLevel, ItemStack stack) {
        super(pEntityType, pShooter, pLevel);
        this.weapon = stack;
        this.foil = stack.hasFoil();
        this.loyalty = EnchantmentHelper.getLoyalty(stack);
    }

    protected SimpleThrowEntity(EntityType<? extends AbstractArrow> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public void setWeapon(ItemStack weapon) {
        this.weapon = weapon;
    }

    @Override
    public void setPierceLevel(byte pPierceLevel) {
        super.setPierceLevel(pPierceLevel);
        this.remainingHit = pPierceLevel + 1;
    }

    private void tickEarlyReturn() {
        Vec3 origin = position();
        Entity entity = this.getOwner();
        if (this.isNoGravity() && this.getDeltaMovement().length() < 1e-2) {
            remainingHit = 0;
            this.setNoGravity(false);
        }
        if (entity != null && loyalty > 0 && remainingHit > 0) {
            if (position().y < level().getMinBuildHeight() - 32) {
                remainingHit = 0;
            } else {
                Vec3 diff = position().subtract(origin);
                if (diff.horizontalDistance() > 100 || diff.length() > 400) {
                    remainingHit = 0;
                }
            }
        }
    }

    @Override
    public void tick() {
        if (this.inGroundTime > 4) {
            remainingHit = 0;
        }
        tickEarlyReturn();
        Entity owner = this.getOwner();
        if (loyalty > 0 && (this.remainingHit == 0 || this.isNoPhysics()) && owner != null) {
            if (!this.isAcceptibleReturnOwner()) {
                if (!this.level().isClientSide && canDrop()) {
                    this.spawnAtLocation(this.getPickupItem(), 0.1F);
                }
                this.discard();
            } else {
                this.setNoPhysics(true);
                Vec3 vec3 = owner.getEyePosition().subtract(this.position());
                this.setPosRaw(this.getX(), this.getY() + vec3.y * 0.015 * (double) loyalty, this.getZ());
                if (this.level().isClientSide) {
                    this.yOld = this.getY();
                }
                double power = 0.05 * (double) loyalty;
                this.setDeltaMovement(this.getDeltaMovement().scale(0.95).add(vec3.normalize().scale(power)));
                if (this.clientSideReturnTridentTickCount == 0) {
                    this.playSound(SoundEvents.TRIDENT_RETURN, 10.0F, 1.0F);
                }
                ++this.clientSideReturnTridentTickCount;
            }
        }
        super.tick();
    }

    protected boolean canDrop() {
        return this.pickup == Pickup.ALLOWED;
    }

    public boolean isAcceptibleReturnOwner() {
        Entity owner = this.getOwner();
        if (owner != null && owner.isAlive()) {
            return !(owner instanceof ServerPlayer) || !owner.isSpectator();
        } else {
            return false;
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        Entity entity = pResult.getEntity();
        float damage = (float) getDamage(entity);
        if (entity instanceof LivingEntity livingentity) {
            damage += EnchantmentHelper.getDamageBonus(this.weapon, livingentity.getMobType());
        }
        Entity owner = this.getOwner();
        if (this.remainingHit > 0) {
            this.remainingHit--;
            if (this.getPierceLevel() > 0) {
                if (this.piercingIgnoreEntityIds == null) {
                    this.piercingIgnoreEntityIds = new IntOpenHashSet(getPierceLevel() + 1);
                }
                if (this.piercedAndKilledEntities == null) {
                    this.piercedAndKilledEntities = Lists.newArrayListWithCapacity(5);
                }
                this.piercingIgnoreEntityIds.add(entity.getId());
            }
        }
        SoundEvent soundevent = SoundEvents.TRIDENT_HIT;
        DamageSource source = getDamageSource(owner);
        if (entity.hurt(source, damage)) {
            if (entity.getType() == EntityType.ENDERMAN) {
                return;
            }
            if (entity instanceof LivingEntity le) {
                if (owner instanceof LivingEntity) {
                    EnchantmentHelper.doPostHurtEffects(le, owner);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity) owner, le);
                }
                this.doPostHurtEffects(le);
                if (!entity.isAlive() && this.piercedAndKilledEntities != null) {
                    this.piercedAndKilledEntities.add(entity);
                }
            }
        }
        if (this.remainingHit == 0)
            this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01D, -0.1D, -0.01D));
        float f1 = 1.0F;
        this.playSound(soundevent, f1, 1.0F);
    }

    protected DamageSource getDamageSource(@Nullable Entity owner) {
        return level().damageSources().trident(this, owner == null ? this : owner);
    }

    protected double getDamage(Entity target) {
        return getBaseDamage();
    }

    @Override
    protected @Nullable EntityHitResult findHitEntity(Vec3 pStartVec, Vec3 pEndVec) {
        return remainingHit == 0 || isNoPhysics() ? null : super.findHitEntity(pStartVec, pEndVec);
    }

    @Override
    public void playerTouch(Player pEntity) {
        if (this.ownedBy(pEntity) || this.getOwner() == null) {
            super.playerTouch(pEntity);
        }
    }

    @Override
    protected boolean tryPickup(Player pPlayer) {
        return super.tryPickup(pPlayer) || this.isNoPhysics() && this.ownedBy(pPlayer) && pPlayer.getInventory().add(this.getPickupItem());
    }

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.TRIDENT_HIT_GROUND;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("Weapon", weapon.serializeNBT());
        tag.putBoolean("Foil", foil);
        tag.putInt("Loyalty", loyalty);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        weapon = Bindings.getItemFromTag(tag, "Weapon");
        foil = tag.getBoolean("Foil");
        loyalty = tag.getInt("Loyalty");
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void tickDespawn() {
        if (this.pickup != Pickup.ALLOWED || loyalty <= 0) {
            super.tickDespawn();
        }
    }

    @Override
    protected ItemStack getPickupItem() {
        return weapon.copy();
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
