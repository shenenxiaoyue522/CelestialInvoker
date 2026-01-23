package com.xiaoyue.celestial_invoker.content.generic.item.combat;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public interface IKillTargetConfig {

    default boolean test(InteractionHand hand, LivingEntity attacker, DamageSource source, LivingEntity target) {
        return true;
    }

    default void onKillTarget(ItemStack stack, LivingEntity attacker, DamageSource source, LivingEntity target, LivingDeathEvent event) {
    }
}
