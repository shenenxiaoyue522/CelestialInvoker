package com.xiaoyue.celestial_invoker.content.generic;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class SimpleEffectBuilder<B extends SimpleEffectBuilder<B>> {

    public boolean fixed;
    public BiConsumer<LivingEntity, Integer> effectTick;
    public EffectApplyCallback onEffectApply;
    public BiFunction<Integer, Integer, Boolean> isEffective;

    private final B builder;

    public SimpleEffectBuilder(B builder) {
        this.builder = builder;
    }

    public static Impl builder() {
        return new Impl();
    }

    public B fixed() {
        this.fixed = true;
        return builder;
    }

    public B effectTick(BiConsumer<LivingEntity, Integer> effectTick) {
        this.effectTick = effectTick;
        return builder;
    }

    public B onEffectApply(EffectApplyCallback onEffectApply) {
        this.onEffectApply = onEffectApply;
        return builder;
    }

    public B isEffective(BiFunction<Integer, Integer, Boolean> isEffective) {
        this.isEffective = isEffective;
        return builder;
    }

    public static class Impl extends SimpleEffectBuilder<Impl> {
        public Impl() {
            super(new Impl());
        }
    }

    @FunctionalInterface
    public interface EffectApplyCallback {
        void onCallback(@Nullable Entity source, @Nullable Entity indirect, LivingEntity entity, int lv);
    }
}
