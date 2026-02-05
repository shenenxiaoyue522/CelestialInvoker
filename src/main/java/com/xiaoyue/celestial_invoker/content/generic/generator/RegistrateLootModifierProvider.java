package com.xiaoyue.celestial_invoker.content.generic.generator;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.RegistrateProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;

import java.util.concurrent.CompletableFuture;

public class RegistrateLootModifierProvider extends GlobalLootModifierProvider implements RegistrateProvider {
    private final AbstractRegistrate<?> owner;

    public RegistrateLootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, AbstractRegistrate<?> owner) {
        super(output, registries, owner.getModid());
        this.owner = owner;
    }


    @Override
    public LogicalSide getSide() {
        return LogicalSide.SERVER;
    }

    @Override
    protected void start() {
        owner.genData(CelestialProviders.LOOT_MODIFIER, this);
    }

    @Override
    public String getName() {
        return "Global Loot Modifier Registrate Provider";
    }
}
