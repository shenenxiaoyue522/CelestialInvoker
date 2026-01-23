package com.xiaoyue.celestial_invoker.content.generic.item.combat;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.function.BiConsumer;

public interface ITakeDamageConfig {

    default boolean test(EquipmentSlot slot, LivingEntity entity, DamageSource source) {
        return true;
    }

    default void onTakeDamagePre(ItemStack stack, LivingEntity entity, DamageSource source, LivingHurtEvent event) {
    }

    default void onTakeDamagePost(ItemStack stack, LivingEntity entity, DamageSource source, LivingDamageEvent event) {
    }

    static void invoke(LivingEntity entity, DamageSource source, BiConsumer<ItemStack, ITakeDamageConfig> cons) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (stack.getItem() instanceof ITakeDamageConfig config && config.test(slot, entity, source)) {
                cons.accept(stack, config);
            }
        }
    }
}
