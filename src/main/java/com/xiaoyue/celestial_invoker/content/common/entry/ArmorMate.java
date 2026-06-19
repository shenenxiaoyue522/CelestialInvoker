package com.xiaoyue.celestial_invoker.content.common.entry;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.*;
import java.util.function.Supplier;

public final class ArmorMate {

    public List<ArmorMaterial.Layer> layers = ArmorMaterials.DIAMOND.value().layers();
    public int durability, enchant = 0;
    public Map<ArmorItem.Type, Integer> defense = new HashMap<>();
    public Holder<SoundEvent> sound = SoundEvents.ARMOR_EQUIP_GENERIC;
    public float toughness, knockResist = 0f;
    public Supplier<Ingredient> repair = Ingredient::of;

    public static ArmorMate builder() {
        return new ArmorMate();
    }

    public ArmorMate name(ResourceLocation name) {
        this.layers = Collections.singletonList(new ArmorMaterial.Layer(name));
        return this;
    }

    public ArmorMate layer(ArmorMaterial.Layer layer) {
        this.layers = Collections.singletonList(layer);
        return this;
    }

    public ArmorMate layer(ArmorMaterial.Layer... layers) {
        this.layers = Arrays.stream(layers).toList();
        return this;
    }

    public ArmorMate durability(int durability) {
        this.durability = durability;
        return this;
    }

    public ArmorMate enchant(int enchant) {
        this.enchant = enchant;
        return this;
    }

    public ArmorMate defense(int[] defense) {
        for (ArmorItem.Type value : ArmorItem.Type.values()) {
            this.defense.put(value, defense[value.getSlot().getIndex()]);
        }
        return this;
    }

    public ArmorMate sound(Holder<SoundEvent> sound) {
        this.sound = sound;
        return this;
    }

    public ArmorMate toughness(float toughness) {
        this.toughness = toughness;
        return this;
    }

    public ArmorMate knockResist(float knockResist) {
        this.knockResist = knockResist;
        return this;
    }

    public ArmorMate repair(Supplier<Ingredient> repair) {
        this.repair = repair;
        return this;
    }

    public ArmorMaterial build() {
        return new ArmorMaterial(defense, enchant, sound, repair, layers, toughness, knockResist);
    }
}

