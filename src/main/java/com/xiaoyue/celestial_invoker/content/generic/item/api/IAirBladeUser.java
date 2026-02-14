package com.xiaoyue.celestial_invoker.content.generic.item.api;

import com.xiaoyue.celestial_invoker.content.common.Bindings;
import com.xiaoyue.celestial_invoker.content.entities.AirBladeEntity;
import com.xiaoyue.celestial_invoker.content.entities.render.AirBladeEntityRender;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

public interface IAirBladeUser {

    default ParticleOptions getTrajectoryParticles() {
        return ParticleTypes.CRIT;
    }

    default DamageSource getSource(AirBladeEntity blade, @Nullable Entity shooter) {
        return new DamageSource(Bindings.getDamageSource(blade.level(), DamageTypes.MOB_PROJECTILE), shooter, blade);
    }

    default boolean canHurt(AirBladeEntity blade, Entity target, float dmg) {
        return true;
    }

    default void onHitEntity(AirBladeEntity blade, Entity target) {
        blade.discard();
    }

    default void onHitBlock(AirBladeEntity blade, BlockPos pos) {
    }

    default boolean isGlow() {
        return true;
    }

    default ResourceLocation getTexture(AirBladeEntity blade) {
        return AirBladeEntityRender.DEF_TEXTURE;
    }
}
