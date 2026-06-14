package com.xiaoyue.celestial_invoker.invoker.tooltip;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.enchantment.Enchantment;

import java.text.DecimalFormat;

public final class TooltipEntry {

    public final String tooltip;
    public final Object[] obj;

    public String key = "";

    public TooltipEntry(String tooltip, Object... obj) {
        this.tooltip = tooltip;
        this.obj = obj;
    }

    public TooltipEntry(String key, String tooltip, Object... obj) {
        this.key = key;
        this.tooltip = tooltip;
        this.obj = obj;
    }

    public static TooltipEntry define(String tooltip, Object... obj) {
        return new TooltipEntry(tooltip, obj);
    }

    public static TooltipEntry define(String key, String tooltip, Object... obj) {
        return new TooltipEntry(key, tooltip, obj);
    }

    public void setKey(String key) {
        this.key = key;
    }

    public MutableComponent get() {
        return Component.translatable(key, obj);
    }

    public MutableComponent get(Object... obj) {
        return Component.translatable(key, obj);
    }

    public MutableComponent withGray() {
        return Component.translatable(key, obj).withStyle(ChatFormatting.GRAY);
    }

    public MutableComponent withGray(Object... obj) {
        return Component.translatable(key, obj).withStyle(ChatFormatting.GRAY);
    }

    public MutableComponent withColor(ChatFormatting color) {
        return Component.translatable(key, obj).withStyle(color);
    }

    public MutableComponent withColor(ChatFormatting color, Object... obj) {
        return Component.translatable(key, obj).withStyle(color);
    }

    public MutableComponent withColor(int color) {
        return Component.translatable(key, obj).withStyle(style -> style.withColor(color));
    }

    public MutableComponent withColor(int color, Object... obj) {
        return Component.translatable(key, obj).withStyle(style -> style.withColor(color));
    }

    public static final DecimalFormat intFormat = new DecimalFormat("#");
    public static final DecimalFormat floatFormat = new DecimalFormat("#.#");
    public static final DecimalFormat doubleFormat = new DecimalFormat("#.##");

    public static MutableComponent per(double v) {
        return Component.literal(Float.parseFloat(floatFormat.format(v * 100f)) + "%").withStyle(ChatFormatting.AQUA);
    }

    public static MutableComponent round(double v) {
        return Component.literal(Math.round(v * 100f) + "%").withStyle(ChatFormatting.AQUA);
    }

    public static MutableComponent num(int v) {
        return Component.literal(intFormat.format(v)).withStyle(ChatFormatting.AQUA);
    }

    public static MutableComponent num(float v) {
        return Component.literal(floatFormat.format(v)).withStyle(ChatFormatting.AQUA);
    }

    public static MutableComponent num(double v) {
        return Component.literal(doubleFormat.format(v)).withStyle(ChatFormatting.AQUA);
    }

    public static MutableComponent entity(EntityType<?> type) {
        return type.getDescription().copy().withStyle(ChatFormatting.AQUA);
    }

    public static MutableComponent eff(MobEffect eff) {
        return eff.getDisplayName().copy().withStyle(eff.getCategory().getTooltipFormatting());
    }

    public static MutableComponent enchantment(Enchantment enchantment) {
        return Component.translatable(enchantment.getDescriptionId());
    }

    public static MutableComponent attr(Attribute attr) {
        return Component.translatable(attr.getDescriptionId());
    }
}
