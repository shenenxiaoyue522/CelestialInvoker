package com.xiaoyue.celestial_invoker.content.tooltip;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;

public final class TooltipEntry {

    public final String tooltip;
    public final Object[] obj;

    public String key;

    public TooltipEntry(String tooltip, Object... obj) {
        this.tooltip = tooltip;
        this.obj = obj;
    }

    public static TooltipEntry define(String tooltip, Object... obj) {
        return new TooltipEntry(tooltip, obj);
    }

    public void setKey(String key) {
        this.key = key;
    }

    public MutableComponent get() {
        return Component.translatable(key, obj);
    }

    public static MutableComponent chance(double v) {
        return Component.literal(Math.round(v * 100f) + "%").withStyle(ChatFormatting.AQUA);
    }

    public static MutableComponent num(int v) {
        return Component.literal("" + v).withStyle(ChatFormatting.AQUA);
    }

    public static MutableComponent entity(EntityType<?> type) {
        return type.getDescription().copy().withStyle(ChatFormatting.AQUA);
    }

    public static MutableComponent eff(MobEffect eff) {
        return eff.getDisplayName().copy().withStyle(eff.getCategory().getTooltipFormatting());
    }

    public static MutableComponent per(double v) {
        return Component.literal(v * 100f + "%").withStyle(ChatFormatting.AQUA);
    }
}
