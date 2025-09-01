package com.xiaoyue.celestial_invoker.content.generic.item;

import com.xiaoyue.celestial_invoker.content.generic.entity.AirBladeEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

public interface IAirBladeUser {

    ParticleOptions getParticle();

    DamageSource getSource(AirBladeEntity blade, @Nullable Entity shooter);

    default boolean canHurt(AirBladeEntity blade, Entity target, float dmg) {
        return true;
    }

    default void onHitEntity(AirBladeEntity blade, Entity target) {
        blade.discard();
    }

    default void onHitBlock(AirBladeEntity blade, BlockPos pos) {
    }

    ResourceLocation getTexture(AirBladeEntity blade);

    default boolean isGlow() {
        return true;
    }

}
