package com.xiaoyue.celestial_invoker.content.generator;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.RegistrateProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.fml.LogicalSide;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class RegistrateDataEntriesProvider extends DatapackBuiltinEntriesProvider implements RegistrateProvider {
    public RegistrateDataEntriesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, AbstractRegistrate<?> owner, RegistrySetBuilder builder) {
        super(output, registries, builder, Set.of(owner.getModid()));
    }

    @Override
    public LogicalSide getSide() {
        return LogicalSide.SERVER;
    }
}
