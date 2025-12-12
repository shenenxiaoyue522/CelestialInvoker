package com.xiaoyue.celestial_invoker.event;

import com.xiaoyue.celestial_invoker.content.ancillary.entry.AttributeAdder;
import com.xiaoyue.celestial_invoker.content.generic.item.CelestialArmorItem;
import com.xiaoyue.celestial_invoker.content.generic.item.IClickInteraction;
import com.xiaoyue.celestial_invoker.content.generic.network.ClickEmptyPacket;
import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.BiConsumer;

import static com.xiaoyue.celestial_invoker.CelestialInvoker.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CIGeneralEventHandler {

    public static void postArmorMethod(LivingEntity entity, BiConsumer<ItemStack, CelestialArmorItem> cons) {
        entity.getArmorSlots().forEach(stack -> {
            if (!stack.isEmpty() && stack.getItem() instanceof CelestialArmorItem armor) {
                cons.accept(stack, armor);
            }
        });
    }

    @SubscribeEvent
    public static void onHurtEvent(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        postArmorMethod(target, (stack, armor) -> {
            EquipmentSlot slot = stack.getEquipmentSlot();
            armor.onHurt(target, stack, event, slot);
        });
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            postArmorMethod(attacker, (stack, armor) -> {
                EquipmentSlot slot = stack.getEquipmentSlot();
                armor.onHurtTarget(attacker, stack, event, slot);
            });
        }
    }

    @SubscribeEvent
    public static void onDamageEvent(LivingDamageEvent event) {
        LivingEntity target = event.getEntity();
        postArmorMethod(target, (stack, armor) -> {
            EquipmentSlot slot = stack.getEquipmentSlot();
            armor.onDamage(target, stack, event, slot);
        });
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

    @SubscribeEvent
    public static void onTest(ItemAttributeModifierEvent event) {
        if (event.getItemStack().is(Items.DIAMOND) && event.getSlotType().equals(EquipmentSlot.MAINHAND)) {
            event.addModifier(L2DamageTracker.CRIT_RATE.get(), AttributeAdder.builder().nameWithUUID("test")
                    .value(1).operation(0).toModifier());
            event.addModifier(L2DamageTracker.REDUCTION.get(), AttributeAdder.builder().nameWithUUID("test")
                    .value(-0.05).operation(1).toModifier());
        }
    }
}
