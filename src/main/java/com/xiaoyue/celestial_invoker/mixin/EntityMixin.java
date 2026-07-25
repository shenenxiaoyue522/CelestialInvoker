package com.xiaoyue.celestial_invoker.mixin;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(at = @At("TAIL"), method = "isCurrentlyGlowing", cancellable = true)
    public void celestial_invoker$setHealth(CallbackInfoReturnable<Boolean> cir) {
        Entity self = (Entity) (Object) this;
        cir.setReturnValue(true);
    }
}
