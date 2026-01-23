package com.xiaoyue.celestial_invoker.content.generic.item.combat;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.function.BiConsumer;

public interface IAttackTargetConfig {

    default boolean test(EquipmentSlot slot, LivingEntity attacker, DamageSource source, LivingEntity target) {
        return true;
    }

    default void onHurtTarget(ItemStack stack, LivingEntity attacker, DamageSource source, LivingEntity target, LivingHurtEvent event) {
    }

    default void onDamageTarget(ItemStack stack, LivingEntity attacker, DamageSource source, LivingEntity target, LivingDamageEvent event) {
    }

    static void invoke(LivingEntity attacker, LivingEntity entity, DamageSource source, BiConsumer<ItemStack, IAttackTargetConfig> cons) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (stack.getItem() instanceof IAttackTargetConfig config && config.test(slot, attacker, source, entity)) {
                cons.accept(stack, config);
            }
        }
    }
}
