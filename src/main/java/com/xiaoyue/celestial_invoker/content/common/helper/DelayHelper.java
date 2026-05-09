package com.xiaoyue.celestial_invoker.content.common.helper;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DelayHelper {

    private static final Map<ResourceLocation, DelayHelper> MAP = new HashMap<>();

    public int runTick;
    public int runCount;
    public int needRunTick;
    public int needRunCount;
    public final Runnable action;
    public boolean isEnd;

    public DelayHelper(int singleRunTick, Runnable action) {
        this.runTick = 0;
        this.runCount = 0;
        this.needRunTick = singleRunTick;
        this.needRunCount = 1;
        this.action = action;
    }

    public DelayHelper(int needRunTick, int needRunCount, Runnable action) {
        this.runTick = 0;
        this.runCount = 0;
        this.needRunTick = needRunTick;
        this.needRunCount = needRunCount;
        this.action = action;
    }

    public static Optional<DelayHelper> getUtils(ResourceLocation id) {
        return Optional.ofNullable(MAP.get(id));
    }

    public static void serverTick() {
        if (MAP.isEmpty()) return;
        MAP.forEach((key, utils) -> {
            if (utils.runTick >= utils.needRunTick) {
                utils.action.run();
                utils.runCount++;
                if (utils.runCount >= utils.needRunCount) {
                    utils.isEnd = true;
                } else {
                    utils.runTick = 0;
                    utils.isEnd = false;
                }
            } else {
                utils.runTick++;
                utils.isEnd = false;
            }
        });
        MAP.entrySet().removeIf(entry -> entry.getValue().isEnd);
    }

    public static void schedule(ResourceLocation id, int needRunTick, Runnable action) {
        DelayHelper utils = new DelayHelper(needRunTick, action);
        MAP.put(id, utils);
    }

    public static void schedule(ResourceLocation id, int needRunTick, int needRunCount, Runnable action) {
        DelayHelper utils = new DelayHelper(needRunTick, needRunCount, action);
        MAP.put(id, utils);
    }
}
