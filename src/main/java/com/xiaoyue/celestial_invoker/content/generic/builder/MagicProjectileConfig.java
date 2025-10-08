package com.xiaoyue.celestial_invoker.content.generic.builder;

import com.xiaoyue.celestial_invoker.content.entities.MagicProjectile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class MagicProjectileConfig {

    public final List<IEffectFactory> effects = new ArrayList<>();
    public float speed = 1.0f;
    public float gravity = 0.0f;
    public int lifetime = 100;
    public boolean seekTarget = false;
    public float seekRange = 0.0f;
    public int color = 0x00f4ff;
    public ParticleOptions trailParticle = ParticleTypes.END_ROD;
    public SoundEvent launchSound = SoundEvents.ENDER_PEARL_THROW;
    public SoundEvent hitSound = SoundEvents.GLASS_BREAK;

    public MagicProjectileConfig(IEffectFactory... effect) {
        this.effects.addAll(Arrays.stream(effect).toList());
    }

    public void forEffects(Consumer<IEffectFactory> cons) {
        effects.forEach(cons);
    }

    public static Builder builder(IEffectFactory... effect) {
        return new Builder(effect);
    }

    public static class Builder {
        private final MagicProjectileConfig config;

        public Builder(IEffectFactory... effect) {
            this.config = new MagicProjectileConfig(effect);
        }

        public Builder speed(float speed) {
            config.speed = speed;
            return this;
        }

        public Builder gravity(float gravity) {
            config.gravity = gravity;
            return this;
        }

        public Builder lifetime(int ticks) {
            config.lifetime = ticks;
            return this;
        }

        public Builder seekTarget(float range) {
            config.seekTarget = true;
            config.seekRange = range;
            return this;
        }

        public Builder color(int color) {
            config.color = color;
            return this;
        }

        public Builder trailParticle(ParticleOptions particle) {
            config.trailParticle = particle;
            return this;
        }

        public Builder sounds(SoundEvent launch, SoundEvent hit) {
            config.launchSound = launch;
            config.hitSound = hit;
            return this;
        }

        public MagicProjectileConfig build() {
            return config;
        }
    }

    public interface IEffectFactory {

        IEffectFactory defaultEffect = new IEffectFactory() {
        };

        default void onHitEntity(Level level, LivingEntity caster, MagicProjectile projectile, Entity target, Vec3 hitPos, float power) {
            projectile.discard();
        }

        default void onHitBlock(Level level, LivingEntity caster, MagicProjectile projectile, BlockPos pos, Vec3 hitPos, float power) {
            projectile.discard();
        }

        default void onTick(Level level, LivingEntity caster, MagicProjectile projectile, float power) {

        }

        default void beRemove(Level level, LivingEntity caster, MagicProjectile projectile, Vec3 position, float power) {

        }

        default void renderExtra(MagicProjectile entity, Level level, Vec3 position, float age) {

        }
    }
}
