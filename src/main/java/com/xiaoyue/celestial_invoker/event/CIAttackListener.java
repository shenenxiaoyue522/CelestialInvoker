package com.xiaoyue.celestial_invoker.event;

import dev.xkmc.l2damagetracker.contents.attack.AttackCache;
import dev.xkmc.l2damagetracker.contents.attack.AttackListener;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import static com.xiaoyue.celestial_invoker.content.generic.item.ExtraDataArmor.postMethod;

public class CIAttackListener implements AttackListener {

    @Override
    public void onHurtMaximized(AttackCache cache, ItemStack weapon) {
        LivingHurtEvent event = cache.getLivingHurtEvent();
        assert event != null;
        LivingEntity target = cache.getAttackTarget();
        LivingEntity attacker = cache.getAttacker();
        postMethod(target, (stack, armor) -> {
            EquipmentSlot slot = stack.getEquipmentSlot();
            armor.onHurt(target, stack, event, slot);
        });
        if (attacker != null) {
            postMethod(attacker, (stack, armor) -> {
                EquipmentSlot slot = stack.getEquipmentSlot();
                armor.onHurtTarget(attacker, stack, event, slot);
            });
        }
    }

    @Override
    public void onDamageFinalized(AttackCache cache, ItemStack weapon) {
        LivingDamageEvent event = cache.getLivingDamageEvent();
        assert event != null;
        LivingEntity target = cache.getAttackTarget();
        postMethod(target, (stack, armor) -> {
            EquipmentSlot slot = stack.getEquipmentSlot();
            armor.onDamage(target, stack, event, slot);
        });
    }
}
