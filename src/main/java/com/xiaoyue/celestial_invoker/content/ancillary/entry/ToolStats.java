package com.xiaoyue.celestial_invoker.content.ancillary.entry;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

public record ToolStats(int dur, float speed, float attack, int lv, int enchant, Ingredient repair) implements Tier {

    @Override
    public int getUses() {
        return dur;
    }

    @Override
    public float getSpeed() {
        return speed;
    }

    @Override
    public float getAttackDamageBonus() {
        return attack;
    }

    @Override
    public int getLevel() {
        return lv;
    }

    @Override
    public int getEnchantmentValue() {
        return enchant;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repair;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        public int durability, level, enchant = 0;
        public float speed, attack = 0f;
        public Supplier<Ingredient> repair = Ingredient::of;

        public void durability(int durability) {
            this.durability = durability;
        }

        public void level(int level) {
            this.level = level;
        }

        public void enchant(int enchant) {
            this.enchant = enchant;
        }

        public void speed(float speed) {
            this.speed = speed;
        }

        public void attack(float attack) {
            this.attack = attack;
        }

        public void setRepair(Supplier<Ingredient> repair) {
            this.repair = repair;
        }

        public ToolStats build() {
            return new ToolStats(durability, speed, attack, level, enchant, repair.get());
        }
    }
}
