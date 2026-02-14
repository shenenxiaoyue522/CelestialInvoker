package com.xiaoyue.celestial_invoker.content.common.entry;

import com.google.common.collect.Multimap;
import dev.xkmc.l2library.util.math.MathHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

public class AttributeAdder {

    public Attribute attr = Attributes.ATTACK_DAMAGE;
    public String name = "celestial_series:default_modifier_name";
    public UUID uuid = UUID.randomUUID();
    public double value = 0;
    public AttributeModifier.Operation operation = AttributeModifier.Operation.ADDITION;

    public static AttributeAdder builder() {
        return new AttributeAdder();
    }

    public AttributeAdder attr(Attribute attr) {
        this.attr = attr;
        return this;
    }

    public AttributeAdder name(String name) {
        this.name = name;
        return this;
    }

    public AttributeAdder uuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public AttributeAdder nameWithUUID(ResourceLocation name) {
        String res = name.toString();
        this.name = res;
        this.uuid = MathHelper.getUUIDFromString(res);
        return this;
    }

    public AttributeAdder nameWithUUID(String name) {
        this.name = name;
        this.uuid = MathHelper.getUUIDFromString(name);
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
        this.operation = AttributeModifier.Operation.fromValue(operation);
        return this;
    }

    public AttributeModifier toModifier() {
        return new AttributeModifier(this.uuid, this.name, this.value, this.operation);
    }

    public AttributeAdder toMap(Map<Attribute, AttributeModifier> user) {
        user.put(attr, this.toModifier());
        return this;
    }

    public AttributeAdder toMap(Multimap<Attribute, AttributeModifier> user) {
        user.put(attr, this.toModifier());
        return this;
    }

    public AttributeAdder toCons(BiConsumer<Attribute, AttributeModifier> user) {
        user.accept(attr, this.toModifier());
        return this;
    }
}
