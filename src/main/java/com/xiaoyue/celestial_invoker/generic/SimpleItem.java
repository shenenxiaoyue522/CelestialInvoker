package com.xiaoyue.celestial_invoker.generic;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.xiaoyue.celestial_invoker.CelestialInvoker;
import com.xiaoyue.celestial_invoker.content.generic.SimpleItemBuilder;
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

public class SimpleItem extends Item {
    public final SimpleItemBuilder<?> builder;

    public SimpleItem(Properties pProperties, SimpleItemBuilder<?> builder) {
        super(pProperties);
        this.builder = builder;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> list, TooltipFlag pIsAdvanced) {
        builder.info.accept(pStack, list);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> map = LinkedHashMultimap.create();
        String name = CelestialInvoker.loc("default_melee_attribute").toString();
        if (builder.defaultDamage != 0) {
            map.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, name, builder.defaultDamage,
                    AttributeModifier.Operation.ADDITION));
        }
        if (builder.defaultSpeed != 0) {
            map.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, name, 4 - (4 - builder.defaultSpeed),
                    AttributeModifier.Operation.ADDITION));
        }
        builder.attribute.onCallback(stack, slot, map);
        return map;
    }

    @Override
    public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
        builder.inventoryTick.onCallback(stack, level, player);
    }

    @Override
    public boolean canBeHurtBy(DamageSource pDamageSource) {
        return builder.canHurt.apply(pDamageSource);
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return builder.foil.apply(pStack);
    }
}
