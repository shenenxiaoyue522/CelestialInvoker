package com.xiaoyue.celestial_invoker.event;

import com.xiaoyue.celestial_invoker.content.generic.item.ExtraDataArmor;
import dev.xkmc.l2damagetracker.contents.attack.AttackCache;
import dev.xkmc.l2damagetracker.contents.attack.AttackListener;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.function.BiConsumer;

public class CIAttackListener implements AttackListener {

    public static void postArmor(LivingEntity entity, BiConsumer<ItemStack, ExtraDataArmor> cons) {
        entity.getArmorSlots().forEach(stack -> {
            if (!stack.isEmpty() && stack.getItem() instanceof ExtraDataArmor armor) {
                cons.accept(stack, armor);
            }
        });
    }

    @Override
    public void onHurtMaximized(AttackCache cache, ItemStack weapon) {
        LivingHurtEvent event = cache.getLivingHurtEvent();
        assert event != null;
        LivingEntity target = cache.getAttackTarget();
        LivingEntity attacker = cache.getAttacker();
        postArmor(target, (stack, armor) -> {
            EquipmentSlot slot = stack.getEquipmentSlot();
            armor.config.onHurt(target, stack, event, slot);
        });
        if (attacker != null) {
            postArmor(attacker, (stack, armor) -> {
                EquipmentSlot slot = stack.getEquipmentSlot();
                armor.config.onHurtTarget(attacker, stack, event, slot);
            });
        }
    }

    @Override
    public void onDamageFinalized(AttackCache cache, ItemStack weapon) {
        LivingDamageEvent event = cache.getLivingDamageEvent();
        assert event != null;
        LivingEntity target = cache.getAttackTarget();
        postArmor(target, (stack, armor) -> {
            EquipmentSlot slot = stack.getEquipmentSlot();
            armor.config.onDamage(target, stack, event, slot);
        });
    }
}
