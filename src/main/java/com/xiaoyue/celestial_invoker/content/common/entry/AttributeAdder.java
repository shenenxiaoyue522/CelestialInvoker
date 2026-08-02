package com.xiaoyue.celestial_invoker.content.common.entry;

import com.google.common.collect.Multimap;
import com.xiaoyue.celestial_invoker.CelestialInvoker;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.function.BiConsumer;

public class AttributeAdder {

    public Holder<Attribute> attr = Attributes.ATTACK_DAMAGE;
    public ResourceLocation name = CelestialInvoker.loc("default_modifier_name");
    public double value = 0;
    public AttributeModifier.Operation operation = AttributeModifier.Operation.ADD_VALUE;
    public EquipmentSlotGroup slot = EquipmentSlotGroup.MAINHAND;

    public static AttributeAdder builder() {
        return new AttributeAdder();
    }

    public AttributeAdder attr(Holder<Attribute> attr) {
        this.attr = attr;
        return this;
    }

    public AttributeAdder name(String name) {
        this.name = ResourceLocation.parse(name);
        return this;
    }

    public AttributeAdder name(ResourceLocation name) {
        this.name = name;
        return this;
    }

    public AttributeAdder value(double value) {
        this.value = value;
        return this;
    }

    public AttributeAdder operation(AttributeModifier.Operation operation) {
        this.operation = operation;
        return this;
    }

    public AttributeAdder operation(int operation) {
        this.operation = AttributeModifier.Operation.BY_ID.apply(operation);
        return this;
    }

    public void slot(EquipmentSlotGroup slot) {
        this.slot = slot;
    }

    public void slot(EquipmentSlot slot) {
        this.slot = EquipmentSlotGroup.bySlot(slot);
    }

    public AttributeModifier modifier() {
        return new AttributeModifier(this.name, this.value, this.operation);
    }

    public AttributeAdder toCons(BiConsumer<Holder<Attribute>, AttributeModifier> user) {
        user.accept(attr, this.modifier());
        return this;
    }

    public AttributeAdder toMap(Multimap<Holder<Attribute>, AttributeModifier> user) {
        user.put(attr, this.modifier());
        return this;
    }

    public AttributeAdder toItem(ItemAttributeModifiers.Builder builder) {
        builder.add(attr, modifier(), slot);
        return this;
    }

    public ItemAttributeModifiers.Builder BuilderItem() {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        builder.add(attr, modifier(), slot);
        return builder;
    }
}
