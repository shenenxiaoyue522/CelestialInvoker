package com.xiaoyue.celestial_invoker.event;

import com.xiaoyue.celestial_invoker.content.generic.item.IClickInteraction;
import com.xiaoyue.celestial_invoker.content.generic.item.combat.IAttackTargetConfig;
import com.xiaoyue.celestial_invoker.content.generic.item.combat.IKillTargetConfig;
import com.xiaoyue.celestial_invoker.content.generic.item.combat.ITakeDamageConfig;
import com.xiaoyue.celestial_invoker.content.generic.network.ClickEmptyPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.xiaoyue.celestial_invoker.CelestialInvoker.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CIGeneralEventHandler {

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();
        if (entity.level().isClientSide()) return;
        if (source.getEntity() instanceof LivingEntity attacker) {
            IAttackTargetConfig.invoke(attacker, entity, source, (i, c) ->
                    c.onHurtTarget(i, attacker, source, entity, event));
        }
        ITakeDamageConfig.invoke(entity, source, (i, c) -> c.onTakeDamagePre(i, entity, source, event));
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onDamage(LivingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();
        if (entity.level().isClientSide()) return;
        if (source.getEntity() instanceof LivingEntity attacker) {
            IAttackTargetConfig.invoke(attacker, entity, source, (i, c) ->
                    c.onDamageTarget(i, attacker, source, entity, event));
        }
        ITakeDamageConfig.invoke(entity, source, (i, c) -> c.onTakeDamagePost(i, entity, source, event));
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onCriticalHit(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();
        if (entity.level().isClientSide()) return;
        if (source.getEntity() instanceof LivingEntity attacker) {
            for (InteractionHand hand : InteractionHand.values()) {
                ItemStack stack = attacker.getItemInHand(hand);
                if (stack.getItem() instanceof IKillTargetConfig config && config.test(hand, attacker, source, entity)) {
                    config.onKillTarget(stack, attacker, source, entity, event);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        if (event.getEntity().level().isClientSide()) {
            new ClickEmptyPacket(false, event.getHand()).toServer();
        }
    }

    @SubscribeEvent
    public static void onRightClickEmpty(PlayerInteractEvent.RightClickEmpty event) {
        if (event.getEntity().level().isClientSide()) {
            new ClickEmptyPacket(true, event.getHand()).toServer();
        }
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getItemStack().getItem() instanceof IClickInteraction item) {
            item.onLeftClickBlock(event.getItemStack(), event, event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getItemStack().getItem() instanceof IClickInteraction item) {
            item.onRightClickBlock(event.getItemStack(), event, event.getEntity());
        }
    }
}
