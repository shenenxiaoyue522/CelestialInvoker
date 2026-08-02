package com.xiaoyue.celestial_invoker.content.generic.item.api;

import com.mojang.blaze3d.vertex.PoseStack;
import com.xiaoyue.celestial_invoker.content.common.Bindings;
import com.xiaoyue.celestial_invoker.content.entities.AirBladeEntity;
import com.xiaoyue.celestial_invoker.content.entities.render.AirBladeEntityRender;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

public interface IAirBladeUser {

    default void tickUpdate(AirBladeEntity blade) {
    }

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

    default void renderExtra(AirBladeEntity blade, PoseStack stack, float partial, MultiBufferSource buffer) {
        stack.scale(0.05625F, 0.05625F, 0.05625F);
    }

    default ResourceLocation getTexture(AirBladeEntity blade) {
        return AirBladeEntityRender.DEFAULT_TEXTURE;
    }
}
