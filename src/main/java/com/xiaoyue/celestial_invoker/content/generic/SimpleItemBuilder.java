package com.xiaoyue.celestial_invoker.content.generic;

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

@SuppressWarnings("unchecked")
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

    public B defaultDamage(float defaultDamage) {
        this.defaultDamage = defaultDamage;
        return (B) this;
    }

    public B defaultSpeed(float defaultSpeed) {
        this.defaultSpeed = defaultSpeed;
        return (B) this;
    }

    public B info(BiConsumer<ItemStack, List<Component>> info) {
        this.info = info;
        return (B) this;
    }

    public B attribute(AttributeAdderCallback attribute) {
        this.attribute = attribute;
        return (B) this;
    }

    public B onInventory(InventoryTickCallback callback) {
        this.inventoryTick = callback;
        return (B) this;
    }

    public B setCanHurt(Function<DamageSource, Boolean> canHurt) {
        this.canHurt = canHurt;
        return (B) this;
    }

    public B foil(Function<ItemStack, Boolean> foil) {
        this.foil = foil;
        return (B) this;
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
