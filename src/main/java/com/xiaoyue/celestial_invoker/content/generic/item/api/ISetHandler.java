package com.xiaoyue.celestial_invoker.content.generic.item.api;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public interface ISetHandler {

    default int requiredCount() {
        return 4;
    }

    default void onSetActivate(Player player) {
    }

    default void onSetDeactivate(Player player) {
    }

    default void onSetTick(Player player) {
    }

    default void onPlayerDamaged(Player player, LivingDamageEvent event, DamageSource source) {
    }

    default void onPlayerDeath(Player player, LivingDeathEvent event, DamageSource source) {
    }
}
