package com.xiaoyue.celestial_invoker.content.ancillary.material;

import dev.xkmc.l2damagetracker.contents.materials.api.ArmorMat;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

public class ArmorMate extends ArmorMat {
    public ArmorMate(String name, int durability, int[] defense, int enchant, SoundEvent sound, float tough, float kb, Supplier<Ingredient> repair) {
        super(name, durability, defense, enchant, sound, tough, kb, repair);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        public String name = "";
        public int durability, enchant = 0;
        public int[] defense = new int[]{0, 0, 0, 0};
        public SoundEvent sound = SoundEvents.ARMOR_EQUIP_GENERIC;
        public float toughness, knockResist = 0f;
        public Supplier<Ingredient> repair = Ingredient::of;

        public void setName(String name) {
            this.name = name;
        }

        public void durability(int durability) {
            this.durability = durability;
        }

        public void enchant(int enchant) {
            this.enchant = enchant;
        }

        public void defense(int[] defense) {
            this.defense = defense;
        }

        public void sound(SoundEvent sound) {
            this.sound = sound;
        }

        public void toughness(float toughness) {
            this.toughness = toughness;
        }

        public void knockResist(float knockResist) {
            this.knockResist = knockResist;
        }

        public void repair(Supplier<Ingredient> repair) {
            this.repair = repair;
        }

        public ArmorMate build() {
            return new ArmorMate(name, durability, defense, enchant, sound, toughness, knockResist, repair);
        }
    }
}
