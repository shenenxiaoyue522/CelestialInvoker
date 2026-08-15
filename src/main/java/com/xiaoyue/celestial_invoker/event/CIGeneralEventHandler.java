package com.xiaoyue.celestial_invoker.event;

import com.xiaoyue.celestial_invoker.content.common.helper.DelayHelper;
import com.xiaoyue.celestial_invoker.content.common.registrar.ArmorSetEntry;
import com.xiaoyue.celestial_invoker.content.generic.item.api.IClickInteraction;
import com.xiaoyue.celestial_invoker.content.generic.item.api.IEquipChanged;
import com.xiaoyue.celestial_invoker.content.generic.item.api.ISetHandler;
import com.xiaoyue.celestial_invoker.content.network.ClickEmptyPayload;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;

import static com.xiaoyue.celestial_invoker.CelestialInvoker.MODID;

@EventBusSubscriber(modid = MODID)
public class CIGeneralEventHandler {

    private static final Map<Integer, List<ISetHandler>> LAST_SET_MAP = new HashMap<>();

    @SubscribeEvent
    public static void onLivingTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
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
    public static void onDamage(LivingDamageEvent.Pre event) {
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
    public static void leftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        if (event.getSide().equals(LogicalSide.SERVER)) {
            ClickEmptyPayload.acceptItem(event.getEntity(), event.getHand(), false);
        } else {
            PacketDistributor.sendToServer(new ClickEmptyPayload(false, event.getHand()));
        }
    }

    @SubscribeEvent
    public static void rightClickEmpty(PlayerInteractEvent.RightClickEmpty event) {
        if (event.getSide().equals(LogicalSide.SERVER)) {
            ClickEmptyPayload.acceptItem(event.getEntity(), event.getHand(), true);
        } else {
            PacketDistributor.sendToServer(new ClickEmptyPayload(true, event.getHand()));
        }
    }

    @SubscribeEvent
    public static void leftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof IClickInteraction item) {
            item.onLeftClickBlock(event.getEntity(), stack, event);
        }
    }

    @SubscribeEvent
    public static void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof IClickInteraction item) {
            item.onRightClickBlock(event.getEntity(), stack, event);
        }
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        DelayHelper.serverTick();
    }
}
