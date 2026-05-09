package com.xiaoyue.celestial_invoker.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.xiaoyue.celestial_invoker.content.generic.items.api.ICustomName;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @ModifyReturnValue(at = @At("TAIL"), method = "getDisplayName")
    public Component celestial_invoker$modifyItemName(Component original) {
        ItemStack stack = (ItemStack) (Object) this;
        if (stack.getItem() instanceof ICustomName item) {
            return item.getCustomName(stack, original);
        }
        return original;
    }
}
