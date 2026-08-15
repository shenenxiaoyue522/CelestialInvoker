package com.xiaoyue.celestial_invoker.event;

import com.xiaoyue.celestial_invoker.content.common.entry.ArmorSetEntry;
import com.xiaoyue.celestial_invoker.content.generic.item.api.IClickInteraction;
import com.xiaoyue.celestial_invoker.content.generic.item.api.IEquipChanged;
import com.xiaoyue.celestial_invoker.content.generic.item.api.ISetHandler;
import com.xiaoyue.celestial_invoker.content.network.ClickEmptyPayload;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

import static com.xiaoyue.celestial_invoker.CelestialInvoker.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CIGeneralEventHandler {

    private static final Map<Integer, List<ISetHandler>> LAST_SET_MAP = new HashMap<>();

    @SubscribeEvent
    public static void onLivingTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (event.phase.equals(TickEvent.Phase.START)) return;
        if (player.level().isClientSide()) return;
        int entityId = player.getId();
        List<ISetHandler> currentSets = new ArrayList<>();
        for (Map.Entry<ArmorSetEntry<? extends Item>, ISetHandler> entry : ArmorSetEntry.handlers.entrySet()) {
            ISetHandler handler = entry.getValue();
            if (entry.getKey().isFullSet(player, handler.requiredCount())) {
                currentSets.add(handler);
            }
        }
        List<ISetHandler> lastSets = LAST_SET_MAP.getOrDefault(entityId, Collections.emptyList());
        for (ISetHandler handler : lastSets) {
            if (!currentSets.contains(handler)) {
                handler.onSetDeactivate(player);
            }
        }
        for (ISetHandler handler : currentSets) {
            if (!lastSets.contains(handler)) {
                handler.onSetActivate(player);
            }
            handler.onSetTick(player);
        }
        if (currentSets.isEmpty()) {
            LAST_SET_MAP.remove(entityId);
        } else {
            LAST_SET_MAP.put(entityId, currentSets);
        }
    }

    @SubscribeEvent
    public static void onChangeEquip(LivingEquipmentChangeEvent event) {
        LivingEntity entity = event.getEntity();
        if (event.getTo().getItem() instanceof IEquipChanged changed) {
            changed.onEquipItem(entity, event.getTo(), event.getFrom(), event.getSlot());
        }
        if (event.getFrom().getItem() instanceof IEquipChanged changed) {
            changed.onUnequipItem(entity, event.getFrom(), event.getTo(), event.getSlot());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onDamage(LivingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        ArmorSetEntry.handlers.forEach((set, handler) -> {
            if (set.isFullSet(entity, handler.requiredCount())) handler.onPlayerDamaged(player, event, event.getSource());
        });
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        ArmorSetEntry.handlers.forEach((set, handler) -> {
            if (set.isFullSet(entity, handler.requiredCount())) handler.onPlayerDeath(player, event, event.getSource());
        });
    }

    @SubscribeEvent
    public static void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        if (event.getEntity().level().isClientSide()) {
            new ClickEmptyPayload(false, event.getHand()).toServer();
        }
    }

    @SubscribeEvent
    public static void onRightClickEmpty(PlayerInteractEvent.RightClickEmpty event) {
        if (event.getEntity().level().isClientSide()) {
            new ClickEmptyPayload(true, event.getHand()).toServer();
        }
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof IClickInteraction item) {
            item.onLeftClickBlock(event.getEntity(), stack, event);
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof IClickInteraction item) {
            item.onRightClickBlock(event.getEntity(), stack, event);
        }
    }
}
