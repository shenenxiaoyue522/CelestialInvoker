package com.xiaoyue.celestial_invoker.content.generic;

import com.xiaoyue.celestial_invoker.content.generic.builder.SimpleEffectBuilder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class SimpleEffect extends MobEffect {
    public final SimpleEffectBuilder<?> builder;

    public SimpleEffect(MobEffectCategory pCategory, int pColor, SimpleEffectBuilder<?> builder) {
        super(pCategory, pColor);
        this.builder = builder;
        if (builder.attrs != null) {
            this.builder.attrs.forEach(entry ->
                    this.addAttributeModifier(entry.attr, entry.uuid.toString(), entry.value, entry.operation));
        }
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
        if (builder.effectTick != null) {
            builder.effectTick.accept(pLivingEntity, pAmplifier);
        }
    }

    @Override
    public void applyInstantenousEffect(@Nullable Entity pSource, @Nullable Entity pIndirectSource, LivingEntity pLivingEntity, int pAmplifier, double pHealth) {
        if (builder.onEffectApply != null) {
            builder.onEffectApply.onCallback(pSource, pIndirectSource, pLivingEntity, pAmplifier);
        }
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        if (builder.isEffective != null) {
            return builder.isEffective.apply(pDuration, pAmplifier);
        }
        return true;
    }

    @Override
    public void initializeClient(Consumer<IClientMobEffectExtensions> consumer) {
        consumer.accept(new IClientMobEffectExtensions() {
            @Override
            public boolean isVisibleInInventory(MobEffectInstance instance) {
                return !builder.hidden;
            }
            @Override
            public boolean isVisibleInGui(MobEffectInstance instance) {
                return !builder.hidden;
            }
        });
    }
}
