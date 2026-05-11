package com.xiaoyue.celestial_invoker.content.common.registrar;

import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.resources.ResourceLocation;

public record EntryWrapper<T, S extends T>(RegistryEntry<T, S> entry) implements IEntryWrapper<S> {

    @Override
    public S get() {
        return entry.get();
    }

    @Override
    public ResourceLocation getId() {
        return entry.getId();
    }

    @Override
    public String getRegisteredName() {
        return entry.getRegisteredName();
    }
}
