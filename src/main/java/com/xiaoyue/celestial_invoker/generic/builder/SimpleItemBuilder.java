package com.xiaoyue.celestial_invoker.generic.builder;

import com.google.common.collect.Multimap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class SimpleItemBuilder<B extends SimpleItemBuilder<B>> {

    public float defaultDamage, defaultSpeed = 0;
    public BiConsumer<ItemStack, List<Component>> info;
    public AttributeAdderCallback attribute;
    public InventoryTickCallback inventoryTick;
    public Function<DamageSource, Boolean> canHurt;
    public Function<ItemStack, Boolean> foil;

    public static Impl impl() {
        return new Impl();
    }

    @SuppressWarnings("unchecked")
    public B self() {
        return (B) this;
    }

    public B defaultDamage(float defaultDamage) {
        this.defaultDamage = defaultDamage;
        return self();
    }

    public B defaultSpeed(float defaultSpeed) {
        this.defaultSpeed = defaultSpeed;
        return self();
    }

    public B info(BiConsumer<ItemStack, List<Component>> info) {
        this.info = info;
        return self();
    }

    public B attribute(AttributeAdderCallback attribute) {
        this.attribute = attribute;
        return self();
    }

    public B onInventory(InventoryTickCallback callback) {
        this.inventoryTick = callback;
        return self();
    }

    public B setCanHurt(Function<DamageSource, Boolean> canHurt) {
        this.canHurt = canHurt;
        return self();
    }

    public B foil(Function<ItemStack, Boolean> foil) {
        this.foil = foil;
        return self();
    }

    public static class Impl extends SimpleItemBuilder<Impl> {
    }

    @FunctionalInterface
    public interface AttributeAdderCallback {
        void onCallback(ItemStack stack, EquipmentSlot slot, Multimap<Attribute, AttributeModifier> map);
    }

    @FunctionalInterface
    public interface InventoryTickCallback {
        void onCallback(ItemStack stack, Level level, Player player);
    }
}
