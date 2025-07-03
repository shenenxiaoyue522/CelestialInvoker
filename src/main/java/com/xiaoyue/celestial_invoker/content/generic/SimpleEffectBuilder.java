package com.xiaoyue.celestial_invoker.content.generic;

import com.xiaoyue.celestial_invoker.content.binding.AttrModifierEntry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

@SuppressWarnings("unchecked")
public abstract class SimpleEffectBuilder<B extends SimpleEffectBuilder<B>> {

    public boolean fixed, hidden = false;
    public List<AttrModifierEntry> attrs;
    public BiConsumer<LivingEntity, Integer> effectTick;
    public EffectApplyCallback onEffectApply;
    public BiFunction<Integer, Integer, Boolean> isEffective;

    public static Impl impl() {
        return new Impl();
    }

    public B fixed() {
        this.fixed = true;
        return (B) this;
    }

    public B hidden() {
        this.hidden = true;
        return (B) this;
    }

    public B attr(AttrModifierEntry... attrs) {
        this.attrs = Arrays.stream(attrs).toList();
        return (B) this;
    }

    public B effectTick(BiConsumer<LivingEntity, Integer> effectTick) {
        this.effectTick = effectTick;
        return (B) this;
    }

    public B onEffectApply(EffectApplyCallback onEffectApply) {
        this.onEffectApply = onEffectApply;
        return (B) this;
    }

    public B isEffective(BiFunction<Integer, Integer, Boolean> isEffective) {
        this.isEffective = isEffective;
        return (B) this;
    }

    public static class Impl extends SimpleEffectBuilder<Impl> {
    }

    @FunctionalInterface
    public interface EffectApplyCallback {
        void onCallback(@Nullable Entity source, @Nullable Entity indirect, LivingEntity entity, int lv);
    }
}
