package com.xiaoyue.celestial_invoker.generic;

import com.xiaoyue.celestial_invoker.content.generic.SimpleEffectBuilder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SimpleEffect extends MobEffect {
    public SimpleEffectBuilder<?> builder;

    protected SimpleEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        if (!builder.fixed) {
            return super.getCurativeItems();
        }
        return List.of();
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        builder.effectTick.accept(pLivingEntity, pAmplifier);
    }

    @Override
    public void applyInstantenousEffect(@Nullable Entity pSource, @Nullable Entity pIndirectSource, LivingEntity pLivingEntity, int pAmplifier, double pHealth) {
        builder.onEffectApply.onCallback(pSource, pIndirectSource, pLivingEntity, pAmplifier);
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return builder.isEffective.apply(pDuration, pAmplifier);
    }
}
