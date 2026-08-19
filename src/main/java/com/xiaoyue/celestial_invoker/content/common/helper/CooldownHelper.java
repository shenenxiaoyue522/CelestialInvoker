package com.xiaoyue.celestial_invoker.content.common.helper;

import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownHelper {
    private static final Map<UUID, Map<String, CooldownEntry>> COOLDOWNS = new ConcurrentHashMap<>();

    public static void setCooldown(Player player, String key, int durationTicks) {
        COOLDOWNS.computeIfAbsent(player.getUUID(), u -> new ConcurrentHashMap<>())
                .put(key, new CooldownEntry(durationTicks, player.tickCount));
    }

    public static int getRemainingCooldown(Player player, String key) {
        Map<String, CooldownEntry> map = COOLDOWNS.get(player.getUUID());
        if (map == null) return 0;
        CooldownEntry entry = map.get(key);
        if (entry == null) return 0;
        int elapsed = player.tickCount - entry.startTick;
        int remaining = entry.duration - elapsed;
        if (remaining <= 0) {
            map.remove(key);
            if (map.isEmpty()) {
                COOLDOWNS.remove(player.getUUID());
            }
            return 0;
        }
        return remaining;
    }

    public static boolean isCooldownReady(Player player, String key) {
        return getRemainingCooldown(player, key) == 0;
    }

    public static float getCooldownProgress(Player player, String key) {
        int remaining = getRemainingCooldown(player, key);
        if (remaining == 0) return 1.0f;
        Map<String, CooldownEntry> map = COOLDOWNS.get(player.getUUID());
        if (map == null) return 1.0f;
        CooldownEntry entry = map.get(key);
        if (entry == null) return 1.0f;
        return 1.0f - ((float) remaining / entry.duration);
    }

    public static void clearCooldown(Player player, String key) {
        Map<String, CooldownEntry> map = COOLDOWNS.get(player.getUUID());
        if (map != null) {
            map.remove(key);
            if (map.isEmpty()) {
                COOLDOWNS.remove(player.getUUID());
            }
        }
    }

    public static void clearAllCooldowns(Player player) {
        COOLDOWNS.remove(player.getUUID());
    }

    public static Map<String, Integer> getAllRemaining(Player player) {
        Map<String, Integer> result = new java.util.HashMap<>();
        Map<String, CooldownEntry> map = COOLDOWNS.get(player.getUUID());
        if (map == null) return result;
        for (var entry : map.entrySet()) {
            int remaining = entry.getValue().duration - (player.tickCount - entry.getValue().startTick);
            if (remaining > 0) {
                result.put(entry.getKey(), remaining);
            }
        }
        return result;
    }

    private record CooldownEntry(int duration, int startTick) {
    }
}