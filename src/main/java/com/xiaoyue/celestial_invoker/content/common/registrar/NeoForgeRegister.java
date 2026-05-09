package com.xiaoyue.celestial_invoker.content.common.registrar;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public record NeoForgeRegister<E>(DeferredRegister<E> register) {

    public <T extends E> Supplier<T> object(String id, Supplier<T> sup) {
        return register.register(id, sup);
    }

    public <T extends E> Supplier<T> object(String id, Function<ResourceLocation, ? extends T> func) {
        return register.register(id, func);
    }

    public <T> Supplier<DataComponentType<T>> component(String id, UnaryOperator<DataComponentType.Builder<T>> builder) {
        if (register instanceof DeferredRegister.DataComponents components) {
            return components.registerComponentType(id, builder);
        }
        return null;
    }
}
