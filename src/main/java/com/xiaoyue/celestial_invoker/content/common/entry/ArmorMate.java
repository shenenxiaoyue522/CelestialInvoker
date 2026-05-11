package com.xiaoyue.celestial_invoker.content.common.entry;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

public record ArmorMate(String name, int durability, int[] defense, int enchant, SoundEvent sound, float tough, float kb, Supplier<Ingredient> repair) implements ArmorMaterial {

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        int[] mul = {13, 15, 16, 11};
        return mul[type.getSlot().getIndex()] * this.durability;
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return defense[type.getSlot().getIndex()];
    }

    @Override
    public int getEnchantmentValue() {
        return enchant;
    }

    @Override
    public SoundEvent getEquipSound() {
        return sound;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repair.get();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public float getToughness() {
        return tough;
    }

    @Override
    public float getKnockbackResistance() {
        return kb;
    }

    public static class Builder {

        public String name = "";
        public int durability, enchant = 0;
        public int[] defense = new int[]{0, 0, 0, 0};
        public SoundEvent sound = SoundEvents.ARMOR_EQUIP_GENERIC;
        public float toughness, knockResist = 0f;
        public Supplier<Ingredient> repair = Ingredient::of;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder durability(int durability) {
            this.durability = durability;
            return this;
        }

        public Builder enchant(int enchant) {
            this.enchant = enchant;
            return this;
        }

        public Builder defense(int[] defense) {
            this.defense = defense;
            return this;
        }

        public Builder sound(SoundEvent sound) {
            this.sound = sound;
            return this;
        }

        public Builder toughness(float toughness) {
            this.toughness = toughness;
            return this;
        }

        public Builder knockResist(float knockResist) {
            this.knockResist = knockResist;
            return this;
        }

        public Builder repair(Supplier<Ingredient> repair) {
            this.repair = repair;
            return this;
        }

        public ArmorMate build() {
            return new ArmorMate(name, durability, defense, enchant, sound, toughness, knockResist, repair);
        }
    }
}
