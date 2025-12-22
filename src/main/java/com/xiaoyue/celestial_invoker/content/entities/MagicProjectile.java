package com.xiaoyue.celestial_invoker.content.entities;

import com.xiaoyue.celestial_invoker.content.generic.builder.MagicProjectileConfig;
import com.xiaoyue.celestial_invoker.content.generic.builder.MagicProjectileConfig.IFactory;
import com.xiaoyue.celestial_invoker.register.CIEntities;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;

import java.util.List;

public class MagicProjectile extends AbstractHurtingProjectile implements IEntityAdditionalSpawnData {

    public MagicProjectileConfig config = MagicProjectileConfig.builder(IFactory.defaultEffect).build();
    public LivingEntity caster;
    public float power = 2f;
    public int age = 0;

    public MagicProjectile(EntityType<? extends AbstractHurtingProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public MagicProjectile(Level level, LivingEntity caster, MagicProjectileConfig config, float power) {
        super(CIEntities.MAGIC_PROJECTILE.get(), caster, 0, 0, 0, level);
        this.caster = caster;
        this.config = config;
        this.power = power;
    }

    public MagicProjectile(Level level, LivingEntity caster, float power) {
        super(CIEntities.MAGIC_PROJECTILE.get(), caster, 0, 0, 0, level);
        this.caster = caster;
        this.power = power;
    }

    private void setupProjectile() {
        this.setNoGravity(config.gravity == 0f);
        if (!level().isClientSide) {
            level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    config.launchSound, SoundSource.PLAYERS, 1.0f, 1.0f);
        }
    }

    public void setupShoot(float inaccuracy) {
        setupProjectile();
        setOwner(caster);
        this.setPos(caster.getX(), caster.getY() + caster.getEyeHeight(), caster.getZ());
        Vec3 look = caster.getLookAngle();
        this.shoot(look.x, look.y, look.z, config.speed, inaccuracy);
    }

    @Override
    public void tick() {
        Vec3 originMovement = this.getDeltaMovement();
        super.tick();
        this.setDeltaMovement(originMovement);
        age++;
        if (age > config.lifetime && !level().isClientSide) {
            beRemove();
            return;
        }
        if (level().isClientSide) {
            spawnTrailParticles();
        }
        if (config.seekTarget && !level().isClientSide) {
            seekTarget();
        }
        config.forEffects(e -> e.onTick(level(), caster, this, power));
        if (!this.isNoGravity()) {
            this.setDeltaMovement(originMovement.x, originMovement.y - this.config.gravity, originMovement.z);
        }
    }

    @Override
    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        level().playSound(null, this.getX(), this.getY(), this.getZ(), config.hitSound, SoundSource.PLAYERS, 1.0f, 1.0f);
        handleHit(pResult);
    }

    protected void handleHit(HitResult result) {
        if (result.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityResult = (EntityHitResult) result;
            config.forEffects(e -> e.onHitEntity(level(), caster, this, entityResult.getEntity(), result.getLocation(), power));
        } else if (result.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockResult = (BlockHitResult) result;
            config.forEffects(e -> e.onHitBlock(level(), caster, this, blockResult.getBlockPos(), result.getLocation(), power));
        }
    }

    protected void spawnTrailParticles() {
        for (int i = 0; i < 2; i++) {
            level().addParticle(config.trailParticle, this.getX() + (random.nextDouble() - 0.5) * 0.2,
                this.getY() + (random.nextDouble() - 0.5) * 0.2, this.getZ() + (random.nextDouble() - 0.5) * 0.2,
                0, 0, 0);
        }
    }

    protected void seekTarget() {
        List<LivingEntity> targets = level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(config.seekRange),
                entity -> entity != caster && entity.isAlive());
        if (!targets.isEmpty()) {
            LivingEntity target = targets.get(0);
            Vec3 direction = target.position().subtract(this.position()).normalize();
            this.setDeltaMovement(this.getDeltaMovement().add(direction.scale(0.1)));
        }
    }

    protected void beRemove() {
        config.forEffects(e -> e.beRemove(level(), caster, this, this.position(), power));
        this.discard();
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buf) {
        buf.writeInt(age);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf buf) {
        age = buf.readInt();
    }
}
