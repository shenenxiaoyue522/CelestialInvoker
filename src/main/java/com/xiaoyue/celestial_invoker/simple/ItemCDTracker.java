package com.xiaoyue.celestial_invoker.simple;

import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ItemCDTracker {

    public static final String TAG_TICK = "CelestialSeries:itemCooldownTick";
    public static final String TAG_MAX_TICK = "CelestialSeries:itemCooldownMaxTick";

    public static void onUpdate(ItemStack stack) {
        int cooldownTick = getCooldownTick(stack);
        if (cooldownTick > 0) {
            stack.getTag().putInt(TAG_TICK, cooldownTick - 1);
        }
        if (cooldownTick <= 0) {
            removeCooldown(stack);
        }
    }

    public static void addCooldown(ItemStack stack, Player entity, int tick) {
        if (!stack.isEmpty()) {
            stack = stack.split(1);
            stack.getOrCreateTag().putInt(TAG_MAX_TICK, tick);
            stack.getOrCreateTag().putInt(TAG_TICK, tick);
            entity.addItem(stack);
        }
    }

    public static int getCooldownTick(ItemStack stack) {
        if (!stack.isEmpty() && stack.hasTag()) {
            if (stack.getTag().contains(TAG_TICK, Tag.TAG_INT)) {
                return stack.getTag().getInt(TAG_TICK);
            }
        }
        return 0;
    }

    public static int getCooldownMaxTick(ItemStack stack) {
        if (onCooldown(stack)) {
            return stack.getTag().getInt(TAG_MAX_TICK);
        }
        return 0;
    }

    public static boolean onCooldown(ItemStack stack) {
        return getCooldownTick(stack) > 0;
    }

    public static float getCooldownPercent(ItemStack stack) {
        float per = (float) getCooldownTick(stack) / getCooldownMaxTick(stack);
        return Mth.clamp(per, 0, 1);
    }

    public static void removeCooldown(ItemStack stack) {
        if (!stack.isEmpty() && stack.hasTag()) {
            stack.getTag().remove(TAG_MAX_TICK);
            stack.getTag().remove(TAG_TICK);
        }
    }
}
