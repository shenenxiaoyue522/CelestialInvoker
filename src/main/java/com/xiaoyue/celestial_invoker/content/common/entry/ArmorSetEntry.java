package com.xiaoyue.celestial_invoker.content.common.entry;

import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;

import java.util.Map;

public class ArmorSetEntry<T extends Item> {
    private final Map<ArmorItem.Type, ItemEntry<T>> map;

    public ArmorSetEntry(Map<ArmorItem.Type, ItemEntry<T>> map) {
        this.map = map;
    }

    public static <T extends Item> ArmorSetEntry<T> of(Map<ArmorItem.Type, ItemEntry<T>> map) {
        return new ArmorSetEntry<>(map);
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
