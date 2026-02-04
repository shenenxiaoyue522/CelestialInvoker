package com.xiaoyue.celestial_invoker.mixin;

import com.xiaoyue.celestial_invoker.event.api.LivingHealthChangeEvent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow @Final private static EntityDataAccessor<Float> DATA_HEALTH_ID;

    @Shadow public abstract float getMaxHealth();

    public LivingEntityMixin(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/network/syncher/SynchedEntityData;set(Lnet/minecraft/network/syncher/EntityDataAccessor;Ljava/lang/Object;)V"), method = "setHealth", cancellable = true)
    public void celestial_invoker$setHealth(float pHealth, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        LivingHealthChangeEvent event = new LivingHealthChangeEvent(self, pHealth);
        float newHp = NeoForge.EVENT_BUS.post(event).isCanceled() ? 0f : event.getNewHealth();
        this.entityData.set(DATA_HEALTH_ID, Mth.clamp(newHp, 0.0f, getMaxHealth()));
        ci.cancel();
    }
}
