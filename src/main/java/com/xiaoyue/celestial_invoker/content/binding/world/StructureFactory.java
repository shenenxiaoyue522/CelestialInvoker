package com.xiaoyue.celestial_invoker.content.binding.world;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class StructureFactory {

    private final RegistrySetBuilder builder;
    public ResourceKey<Structure> structureKey;
    public ResourceKey<StructureSet> structureSetKey;

    public StructureFactory() {
        this.builder = new RegistrySetBuilder();
    }

    public StructureFactory(RegistrySetBuilder builder) {
        this.builder = builder;
    }

    public StructureFactory key(ResourceLocation location) {
        this.structureKey = ResourceKey.create(Registries.STRUCTURE, location);
        this.structureSetKey = ResourceKey.create(Registries.STRUCTURE_SET, location);
        return this;
    }

    public StructureFactory pool(String key, Function<BootstapContext<StructureTemplatePool>, StructureTemplatePool> func) {
        builder.add(Registries.TEMPLATE_POOL, c -> {
            Pools.register(c, key, func.apply(c));
        });
        return this;
    }

    public <T extends Structure> StructureFactory structure(BiFunction<HolderGetter<Biome>, HolderGetter<StructureTemplatePool>, T> func) {
        builder.add(Registries.STRUCTURE, c -> {
            HolderGetter<Biome> holderBiome = c.lookup(Registries.BIOME);
            HolderGetter<StructureTemplatePool> holderPool = c.lookup(Registries.TEMPLATE_POOL);
            c.register(structureKey, func.apply(holderBiome, holderPool));
        });
        return this;
    }

    public StructureFactory structureSet(StructurePlacement placement) {
        builder.add(Registries.STRUCTURE_SET, c -> {
            HolderGetter<Structure> holderStr = c.lookup(Registries.STRUCTURE);
            c.register(structureSetKey, new StructureSet(holderStr.getOrThrow(structureKey), placement));
        });
        return this;
    }

    public RegistrySetBuilder build() {
        return builder;
    }

    public void generator(GatherDataEvent event, String modid) {
        DataGenerator gen = event.getGenerator();
        var provider = new DatapackBuiltinEntriesProvider(gen.getPackOutput(), event.getLookupProvider(), builder, Set.of(modid));
        gen.addProvider(event.includeServer(), provider);
    }
}
