package com.xiaoyue.celestial_invoker.content.generic.builder;

import com.google.common.collect.Multimap;
import com.xiaoyue.celestial_invoker.content.generic.item.ExtraDataArmor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public interface IBaseArmorConfig {

    default int getSeriesArmors(LivingEntity entity) {
        AtomicInteger i = new AtomicInteger();
        entity.getArmorSlots().forEach(stack -> {
            if (!stack.isEmpty() && stack.getItem() instanceof ExtraDataArmor armor) {
                if (armor.config.equals(this)) i.getAndIncrement();
            }
        });
        return i.get();
    }

    default ArmorMaterial getMaterial() {
        return ArmorMaterials.IRON;
    }

    default ExtraDataArmor.DefenseData getExtraDefense(ItemStack stack) {
        return new ExtraDataArmor.DefenseData(0f, 0f, 0f);
    }

    default boolean isFoil(ItemStack stack) {
        return false;
    }

    default boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        return false;
    }

    default boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
        return false;
    }

    default void addTooltips(ItemStack stack, List<Component> list, EquipmentSlot slot) {

    }

    default void onArmorTick(ItemStack stack, Level level, LivingEntity entity, EquipmentSlot slot) {

    }

    default void onInventoryTick(ItemStack stack, Level level, LivingEntity entity, EquipmentSlot slot) {

    }

    default void getAttributes(EquipmentSlot slot, ItemStack stack, Multimap<Attribute, AttributeModifier> modify) {

    }

    default void onHurt(LivingEntity entity, ItemStack stack, LivingHurtEvent event, EquipmentSlot slot) {

    }

    default void onDamage(LivingEntity entity, ItemStack stack, LivingDamageEvent event, EquipmentSlot slot) {

    }

    default void onHurtTarget(LivingEntity attacker, ItemStack stack, LivingHurtEvent event, EquipmentSlot slot) {

    }
}
