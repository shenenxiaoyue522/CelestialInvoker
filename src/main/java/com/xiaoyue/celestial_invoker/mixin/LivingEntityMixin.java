package com.xiaoyue.celestial_invoker.mixin;

import com.xiaoyue.celestial_invoker.event.api.LivingHealthChangeEvent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow @Final private static EntityDataAccessor<Float> DATA_HEALTH_ID;

    public LivingEntityMixin(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/network/syncher/SynchedEntityData;set(Lnet/minecraft/network/syncher/EntityDataAccessor;Ljava/lang/Object;)V"), method = "setHealth", cancellable = true)
    public void celestial_invoker$setHealth(float pHealth, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        float newHp = pHealth;
        LivingHealthChangeEvent event = new LivingHealthChangeEvent(self, newHp);
        if (MinecraftForge.EVENT_BUS.post(event)) {
            newHp = 0;
        }
        newHp = event.getNewHealth();
        this.entityData.set(DATA_HEALTH_ID, newHp);
        ci.cancel();
    }
}
