package com.xiaoyue.celestial_invoker.content.generic.builder;

import com.xiaoyue.celestial_invoker.content.binding.AttrModifierEntry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public abstract class SimpleEffectBuilder<B extends SimpleEffectBuilder<B>> {

    public boolean fixed, hidden = false;
    public List<AttrModifierEntry> attrs;
    public BiConsumer<LivingEntity, Integer> effectTick;
    public EffectApplyCallback onEffectApply;
    public BiFunction<Integer, Integer, Boolean> isEffective;

    public static Impl impl() {
        return new Impl();
    }

    @SuppressWarnings("unchecked")
    public B self() {
        return (B) this;
    }

    public B fixed() {
        this.fixed = true;
        return self();
    }

    public B hidden() {
        this.hidden = true;
        return self();
    }

    public B attr(AttrModifierEntry... attrs) {
        this.attrs = Arrays.stream(attrs).toList();
        return self();
    }

    public B effectTick(BiConsumer<LivingEntity, Integer> effectTick) {
        this.effectTick = effectTick;
        return self();
    }

    public B onEffectApply(EffectApplyCallback onEffectApply) {
        this.onEffectApply = onEffectApply;
        return self();
    }

    public B isEffective(BiFunction<Integer, Integer, Boolean> isEffective) {
        this.isEffective = isEffective;
        return self();
    }

    public static class Impl extends SimpleEffectBuilder<Impl> {
    }

    @FunctionalInterface
    public interface EffectApplyCallback {
        void onCallback(@Nullable Entity source, @Nullable Entity indirect, LivingEntity entity, int lv);
    }
}
