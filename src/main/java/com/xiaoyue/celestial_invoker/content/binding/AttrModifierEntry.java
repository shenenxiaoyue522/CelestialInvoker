package com.xiaoyue.celestial_invoker.content.binding;

import com.google.common.collect.Multimap;
import dev.xkmc.l2library.util.math.MathHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

public class AttrModifierEntry {

    public Attribute attr = Attributes.ATTACK_DAMAGE;
    public String name;
    public UUID uuid;
    public double value;
    public AttributeModifier.Operation operation;

    public static AttrModifierEntry builder() {
        return new AttrModifierEntry();
    }

    public AttrModifierEntry attr(Attribute attr) {
        this.attr = attr;
        return this;
    }

    public AttrModifierEntry name(String name) {
        this.name = name;
        return this;
    }

    public AttrModifierEntry uuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public AttrModifierEntry randomUUID() {
        this.uuid = UUID.randomUUID();
        return this;
    }

    public AttrModifierEntry nameWithUUID(ResourceLocation name) {
        String res = name.toString();
        this.name = res;
        this.uuid = MathHelper.getUUIDFromString(res);
        return this;
    }

    public AttrModifierEntry value(double value) {
        this.value = value;
        return this;
    }

    public AttrModifierEntry operation(AttributeModifier.Operation operation) {
        this.operation = operation;
        return this;
    }

    public AttrModifierEntry operation(int operation) {
        this.operation = AttributeModifier.Operation.fromValue(operation);
        return this;
    }

    public AttributeModifier toModifier() {
        return new AttributeModifier(this.uuid, this.name, this.value, this.operation);
    }

    public AttrModifierEntry toMap(Map<Attribute, AttributeModifier> user) {
        user.put(attr, this.toModifier());
        return this;
    }

    public AttrModifierEntry toMap(Multimap<Attribute, AttributeModifier> user) {
        user.put(attr, this.toModifier());
        return this;
    }

    public AttrModifierEntry toCons(BiConsumer<Attribute, AttributeModifier> user) {
        user.accept(attr, this.toModifier());
        return this;
    }
}
