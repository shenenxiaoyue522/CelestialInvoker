package com.xiaoyue.celestial_invoker.content.ancillary;

import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ItemCooldownTracker {

    public static final String TAG_TICK = "CelestialSeries:itemCooldownTick";
    public static final String TAG_MAX_TICK = "CelestialSeries:itemCooldownMaxTick";
    public static final String TAG_COLOR = "CelestialSeries:itemCooldownColor";

    public static void onUpdate(ItemStack stack) {
        int cooldownTick = getCooldownTick(stack);
        if (cooldownTick > 0) {
            stack.getTag().putInt(TAG_TICK, cooldownTick - 1);
        }
        if (cooldownTick <= 0) {
            removeCooldown(stack);
        }
    }

    public static void addCooldown(ItemStack stack, Player entity, int tick, int color) {
        if (!stack.isEmpty()) {
            stack = stack.split(1);
            stack.getOrCreateTag().putInt(TAG_MAX_TICK, tick);
            stack.getOrCreateTag().putInt(TAG_TICK, tick);
            stack.getOrCreateTag().putInt(TAG_COLOR, color);
            entity.addItem(stack);
        }
    }

    public static void addCooldown(ItemStack stack, Player entity, int tick) {
        addCooldown(stack, entity, tick, Integer.MAX_VALUE);
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

    public static int getCooldownColor(ItemStack stack) {
        if (!stack.isEmpty() && stack.hasTag()) {
            if (stack.getTag().contains(TAG_COLOR, Tag.TAG_INT)) {
                return stack.getTag().getInt(TAG_COLOR);
            }
        }
        return Integer.MAX_VALUE;
    }

    public static void removeCooldown(ItemStack stack) {
        if (!stack.isEmpty() && stack.hasTag()) {
            stack.getTag().remove(TAG_MAX_TICK);
            stack.getTag().remove(TAG_TICK);
            if (stack.getTag().contains(TAG_COLOR, Tag.TAG_INT)) {
                stack.getTag().remove(TAG_COLOR);
            }
        }
    }
}
