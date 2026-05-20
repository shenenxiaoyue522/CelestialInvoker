package com.xiaoyue.celestial_invoker.content.common.registrar;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public interface NeoForgeRegister<E> {

    <T extends E> Supplier<T> object(String id, Supplier<T> sup);

    <T extends E> Supplier<T> object(String id, Function<ResourceLocation, ? extends T> func);

    <T> Supplier<DataComponentType<T>> component(String id, UnaryOperator<DataComponentType.Builder<T>> builder);

    record Basic<R>(DeferredRegister<R> register) implements NeoForgeRegister<R> {

        @Override
        public <T extends R> Supplier<T> object(String id, Supplier<T> sup) {
            return register.register(id, sup);
        }

        @Override
        public <T extends R> Supplier<T> object(String id, Function<ResourceLocation, ? extends T> func) {
            return register.register(id, func);
        }

        @Override
        public <T> Supplier<DataComponentType<T>> component(String id, UnaryOperator<DataComponentType.Builder<T>> builder) {
            return null;
        }
    }

    record Component(DeferredRegister.DataComponents register) implements NeoForgeRegister<DataComponentType<?>> {

        @Override
        public <T extends DataComponentType<?>> Supplier<T> object(String id, Supplier<T> sup) {
            return null;
        }

        @Override
        public <T extends DataComponentType<?>> Supplier<T> object(String id, Function<ResourceLocation, ? extends T> func) {
            return null;
        }

        @Override
        public <T> Supplier<DataComponentType<T>> component(String id, UnaryOperator<DataComponentType.Builder<T>> builder) {
            return register.registerComponentType(id, builder);
        }
    }
}
