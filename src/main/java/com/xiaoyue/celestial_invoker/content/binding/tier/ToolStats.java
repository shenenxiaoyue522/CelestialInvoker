package com.xiaoyue.celestial_invoker.content.binding.tier;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

public record ToolStats(int dur, float speed, float attack, int lv, int enchant, Ingredient repair) implements Tier {

    public static ToolStats create(int dur, float speed, float attack, int lv, int enchant, Ingredient repair) {
        return new ToolStats(dur, speed, attack, lv, enchant, repair);
    }

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
}
