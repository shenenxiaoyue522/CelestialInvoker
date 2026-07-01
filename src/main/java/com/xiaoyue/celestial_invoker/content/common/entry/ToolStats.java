package com.xiaoyue.celestial_invoker.content.common.entry;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public record ToolStats(int dur, float speed, float attack, TagKey<Block> lv, int enchant, Ingredient repair) implements Tier {

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
    public TagKey<Block> getIncorrectBlocksForDrops() {
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

        public int durability, enchant = 0;
        public float speed, attack = 0f;
        public TagKey<Block> level;
        public Supplier<Ingredient> repair = Ingredient::of;

        public Builder durability(int durability) {
            this.durability = durability;
            return this;
        }

        public Builder level(TagKey<Block> level) {
            this.level = level;
            return this;
        }

        public Builder enchant(int enchant) {
            this.enchant = enchant;
            return this;
        }

        public Builder speed(float speed) {
            this.speed = speed;
            return this;
        }

        public Builder attack(float attack) {
            this.attack = attack;
            return this;
        }

        public Builder repair(Supplier<Ingredient> repair) {
            this.repair = repair;
            return this;
        }

        public ToolStats build() {
            return new ToolStats(durability, speed, attack, level, enchant, repair.get());
        }
    }
}

