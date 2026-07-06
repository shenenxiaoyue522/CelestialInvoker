package com.xiaoyue.celestial_invoker.content.common.registrar;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.util.OneTimeEventReceiver;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.xiaoyue.celestial_invoker.content.generic.item.api.ISetHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.HashMap;
import java.util.Map;

public class ArmorSetEntry<T extends Item> {

    public static final HashMap<ArmorSetEntry<? extends Item>, ISetHandler> handlers = new HashMap<>();
    private final Map<ArmorItem.Type, ItemEntry<T>> map;

    public ArmorSetEntry(Map<ArmorItem.Type, ItemEntry<T>> map) {
        this.map = map;
    }

    public static <T extends Item> ArmorSetEntry<T> handler(AbstractRegistrate<?> registrate, ArmorSetEntry<T> entry) {
        OneTimeEventReceiver.addModListener(registrate, FMLCommonSetupEvent.class, event -> entry.getSet().values().forEach(e -> {
            if (e.asItem() instanceof ISetHandler handler) {
                handlers.put(entry, handler);
            }
        }));
        return entry;
    }

    public ItemEntry<T> getHelmet() {
        return map.get(ArmorItem.Type.HELMET);
    }

    public ItemEntry<T> getChestplate() {
        return map.get(ArmorItem.Type.CHESTPLATE);
    }

    public ItemEntry<T> getLeggings() {
        return map.get(ArmorItem.Type.LEGGINGS);
    }

    public ItemEntry<T> getBoots() {
        return map.get(ArmorItem.Type.BOOTS);
    }

    public int getSetArmorCount(LivingEntity entity) {
        int count = 0;
        for (ArmorItem.Type type : map.keySet()) {
            if (entity.getItemBySlot(type.getSlot()).is(map.get(type).get())) {
                count++;
            }
        }
        return count;
    }

    public boolean isFullSet(LivingEntity entity, int condition) {
        return getSetArmorCount(entity) == condition;
    }

    public boolean isFullSet(LivingEntity entity) {
        return isFullSet(entity, 4);
    }

    public Map<ArmorItem.Type, ItemEntry<T>> getSet() {
        return map;
    }
}
