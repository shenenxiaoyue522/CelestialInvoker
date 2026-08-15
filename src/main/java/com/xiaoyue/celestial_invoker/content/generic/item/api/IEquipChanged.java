package com.xiaoyue.celestial_invoker.content.generic.item.api;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface IEquipChanged {

    default void onEquipItem(LivingEntity entity, ItemStack stack, ItemStack from, EquipmentSlot slot) {
    }

    default void onUnequipItem(LivingEntity entity, ItemStack stack, ItemStack to, EquipmentSlot slot) {
    }
}
