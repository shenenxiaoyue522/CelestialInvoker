package com.xiaoyue.celestial_invoker.mixin;

import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.xiaoyue.celestial_invoker.content.common.Bindings;
import com.xiaoyue.celestial_invoker.simple.ItemAccessor;
import dev.xkmc.l2damagetracker.contents.curios.AttrTooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

import static net.minecraft.world.item.ItemStack.ATTRIBUTE_MODIFIER_FORMAT;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shouldShowInTooltip(ILnet/minecraft/world/item/ItemStack$TooltipPart;)Z", ordinal = 4), method = "getTooltipLines")
    public void celestial_invoker$setModifiers(Player pPlayer, TooltipFlag pIsAdvanced, CallbackInfoReturnable<List<Component>> cir, @Local List<Component> list) {
        ItemStack stack = (ItemStack) (Object) this;
        if (!stack.is(Bindings.CUSTOM_ATTRIBUTE_TOOLTIP)) {
            return;
        }
        for(EquipmentSlot slot : EquipmentSlot.values()) {
            Multimap<Attribute, AttributeModifier> modifiers = stack.getAttributeModifiers(slot);
            if (modifiers.isEmpty()) {
                continue;
            }
            list.add(CommonComponents.EMPTY);
            list.add(Component.translatable("item.modifiers." + slot.getName()).withStyle(ChatFormatting.GRAY));
            for(Map.Entry<Attribute, AttributeModifier> entry : modifiers.entries()) {
                AttributeModifier modifier = entry.getValue();
                double amount = modifier.getAmount();
                boolean flag = false;
                if (pPlayer != null) {
                    if (modifier.getId() == ItemAccessor.getBaseDamageUUID()) {
                        amount += pPlayer.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
                        if (!ModList.get().isLoaded("celestial_overhaul")) {
                            amount += EnchantmentHelper.getDamageBonus(stack, MobType.UNDEFINED);
                        }
                        flag = true;
                    } else if (modifier.getId() == ItemAccessor.getBaseSpeedUUID()) {
                        amount += pPlayer.getAttributeBaseValue(Attributes.ATTACK_SPEED);
                        flag = true;
                    }
                }
                double newAmount;
                if (modifier.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && modifier.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
                    if (entry.getKey().equals(Attributes.KNOCKBACK_RESISTANCE)) {
                        newAmount = amount * (double)10.0F;
                    } else {
                        newAmount = amount;
                    }
                } else {
                    newAmount = amount * (double)100.0F;
                }
                int intOp = modifier.getOperation().toValue();
                String id = entry.getKey().getDescriptionId();
                if (flag) {
                    list.add(CommonComponents.space().append(Component.translatable("attribute.modifier.equals." + intOp,
                            ATTRIBUTE_MODIFIER_FORMAT.format(newAmount), Component.translatable(id))).withStyle(ChatFormatting.DARK_GREEN));
                } else if (AttrTooltip.isMult(entry.getKey()) || AttrTooltip.isNegative(entry.getKey())) {
                    list.add(AttrTooltip.getDesc(entry.getKey(), entry.getValue().getAmount(), entry.getValue().getOperation()));
                } else if (amount > (double)0.0F) {
                    list.add(Component.translatable("attribute.modifier.plus." + intOp,
                            ATTRIBUTE_MODIFIER_FORMAT.format(newAmount), Component.translatable(id)).withStyle(ChatFormatting.BLUE));
                } else if (amount < (double)0.0F) {
                    newAmount *= -1.0F;
                    list.add(Component.translatable("attribute.modifier.take." + intOp,
                            ATTRIBUTE_MODIFIER_FORMAT.format(newAmount), Component.translatable(id)).withStyle(ChatFormatting.RED));
                }
            }
        }
    }

    @WrapOperation(at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Multimap;isEmpty()Z"), method = "getTooltipLines")
    public boolean celestial_invoker$canModifyTooltips(Multimap<Attribute, AttributeModifier> instance, Operation<Boolean> original) {
        ItemStack stack = (ItemStack) (Object) this;
        if (stack.is(Bindings.CUSTOM_ATTRIBUTE_TOOLTIP)) {
            return true;
        }
        return original.call(instance);
    }
}
