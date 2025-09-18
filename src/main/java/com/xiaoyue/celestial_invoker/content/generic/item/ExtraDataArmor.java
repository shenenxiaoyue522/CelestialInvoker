package com.xiaoyue.celestial_invoker.content.generic.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.xiaoyue.celestial_invoker.content.ancillary.entry.AttrModifierEntry;
import com.xiaoyue.celestial_invoker.content.generic.builder.IBaseArmorConfig;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.UUID;

public class ExtraDataArmor extends ArmorItem {

    public final IBaseArmorConfig config;
    public static final EnumMap<Type, UUID> ARMOR_MODIFIER_UUID_PER_TYPE = Util.make(new EnumMap<>(ArmorItem.Type.class), (map) -> {
        map.put(Type.BOOTS, UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"));
        map.put(Type.LEGGINGS, UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"));
        map.put(Type.CHESTPLATE, UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"));
        map.put(Type.HELMET, UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150"));
    });

    public ExtraDataArmor(Type pType, Properties pProperties, IBaseArmorConfig config) {
        super(config.getMaterial(), pType, pProperties);
        this.config = config;
    }

    @Override
    public final void appendHoverText(ItemStack stack, @Nullable Level pLevel, List<Component> list, TooltipFlag pIsAdvanced) {
        EquipmentSlot slot = stack.getEquipmentSlot();
        config.addTooltips(stack, list, slot);
        this.addTooltips(stack, list, slot);
    }

    public void addTooltips(ItemStack stack, List<Component> list, EquipmentSlot slot) {
    }

    @Override
    public final Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot pEquipmentSlot) {
        return ImmutableMultimap.of();
    }

    @Override
    public final Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> modify = LinkedHashMultimap.create();
        if (slot.equals(this.type.getSlot())) {
            UUID uuid = ARMOR_MODIFIER_UUID_PER_TYPE.get(this.type);
            float defense = this.getDefense() + this.config.getExtraDefense(stack).armor;
            float toughness = this.getToughness() + this.config.getExtraDefense(stack).toughness;
            AttrModifierEntry.builder().uuid(uuid)
                    .attr(Attributes.ARMOR).name("Armor modifier").value(defense).toMap(modify)
                    .attr(Attributes.ARMOR_TOUGHNESS).name("Armor toughness").value(toughness).toMap(modify);
            if (this.knockbackResistance > 0.0F) {
                modify.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(uuid, "Armor knockback resistance", this.knockbackResistance, AttributeModifier.Operation.ADDITION));
            }
            this.config.getAttributes(slot, stack, modify);
            this.getAttributes(slot, stack, modify);
        }
        return modify;
    }

    public void getAttributes(EquipmentSlot slot, ItemStack stack, Multimap<Attribute, AttributeModifier> modify) {
    }

    @Override
    public final void inventoryTick(ItemStack pStack, Level pLevel, Entity entity, int pSlotId, boolean pIsSelected) {
        if (!(entity instanceof LivingEntity self)) return;
        if (pSlotId >= 36) {
            int vanillaIndex = pSlotId - 36;
            if (vanillaIndex < 4) {
                this.config.onArmorTick(pStack, pLevel, self, pStack.getEquipmentSlot());
            }
            this.config.onInventoryTick(pStack, pLevel, self, pStack.getEquipmentSlot());
        }
    }

    @Override
    public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        return config.canElytraFly(stack, entity);
    }

    @Override
    public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
        return config.elytraFlightTick(stack, entity, flightTicks);
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return config.isFoil(pStack);
    }

    public record DefenseData(float armor, float toughness, float knockbackResistance) {
    }
}
