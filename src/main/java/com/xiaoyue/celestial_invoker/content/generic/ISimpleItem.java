package com.xiaoyue.celestial_invoker.content.generic;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.xiaoyue.celestial_invoker.CelestialInvoker;
import com.xiaoyue.celestial_invoker.content.generic.builder.SimpleItemBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public interface ISimpleItem {

    UUID BASE_ATTACK_DAMAGE_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    UUID BASE_ATTACK_SPEED_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");

    SimpleItemBuilder<?> getBuilder();

    default void addTooltips(ItemStack pStack, List<Component> list) {
        if (getBuilder().info != null) {
            getBuilder().info.accept(pStack, list);
        }
    }

    default Multimap<Attribute, AttributeModifier> addAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> map = LinkedHashMultimap.create();
        String name = CelestialInvoker.loc("default_melee_attribute").toString();
        if (getBuilder().defaultDamage != 0 && slot.equals(EquipmentSlot.MAINHAND)) {
            map.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, name, getBuilder().defaultDamage,
                    AttributeModifier.Operation.ADDITION));
        }
        if (getBuilder().defaultSpeed != 0 && slot.equals(EquipmentSlot.MAINHAND)) {
            map.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, name, 4 - (4 - getBuilder().defaultSpeed),
                    AttributeModifier.Operation.ADDITION));
        }
        if (getBuilder().attribute != null) {
            getBuilder().attribute.onCallback(stack, slot, map);
        }
        return map;
    }

    default void onInvTick(ItemStack stack, Level level, Player player) {
        if (getBuilder().inventoryTick != null) {
            getBuilder().inventoryTick.onCallback(stack, level, player);
        }
    }

    default boolean canHurtBy(DamageSource pDamageSource) {
        if (getBuilder().canHurt != null) {
            return getBuilder().canHurt.apply(pDamageSource);
        }
        return true;
    }

    default boolean isRenderFoil(ItemStack pStack) {
        if (getBuilder().foil != null) {
            return getBuilder().foil.apply(pStack);
        }
        return false;
    }

    abstract class Factory extends Item implements ISimpleItem {
        public Factory(Properties pProperties) {
            super(pProperties);
        }

        @Override
        public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
            addTooltips(pStack, pTooltipComponents);
        }

        @Override
        public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
            return addAttributeModifiers(slot, stack);
        }

        @Override
        public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
            onInvTick(stack, level, player);
        }

        @Override
        public boolean canBeHurtBy(DamageSource pDamageSource) {
            return canHurtBy(pDamageSource);
        }

        @Override
        public boolean isFoil(ItemStack pStack) {
            return isRenderFoil(pStack);
        }
    }
}
