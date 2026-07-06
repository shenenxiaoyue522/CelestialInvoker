package com.xiaoyue.celestial_invoker.content.generic.item.api;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

public interface ISetHandler {

    default int requiredCount() {
        return 4;
    }

    default void onSetTick(LivingEntity entity) {
    }

    default void onDamaged(LivingEntity entity, LivingDamageEvent.Pre event, DamageSource source) {
    }

    default void onDeath(LivingEntity entity, LivingDeathEvent event, DamageSource source) {
    }
}
