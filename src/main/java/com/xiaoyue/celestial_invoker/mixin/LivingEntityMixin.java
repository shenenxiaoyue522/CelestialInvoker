package com.xiaoyue.celestial_invoker.mixin;

import com.xiaoyue.celestial_invoker.event.api.LivingHealthChangeEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/network/syncher/SynchedEntityData;set(Lnet/minecraft/network/syncher/EntityDataAccessor;Ljava/lang/Object;)V"), index = 2, method = "setHealth")
    public float celestial_invoker$setHealth(float value) {
        LivingEntity self = (LivingEntity) (Object) this;
        LivingHealthChangeEvent event = new LivingHealthChangeEvent(self, value);
        if (MinecraftForge.EVENT_BUS.post(event)) {
            return 0f;
        }
        return event.getNewHealth();
    }
}
