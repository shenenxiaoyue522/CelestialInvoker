package com.xiaoyue.celestial_invoker.content.binding.world;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.Set;

public class StructureFactory {

    private final RegistrySetBuilder builder;
    public ResourceKey<Structure> structureKey;
    public ResourceKey<StructureSet> structureSetKey;

    public StructureFactory(ResourceLocation location) {
        this.builder = new RegistrySetBuilder();
    }

    public StructureFactory(RegistrySetBuilder builder, ResourceLocation location) {
        this.builder = builder;
    }

    public StructureFactory key(ResourceLocation location) {
        this.structureKey = ResourceKey.create(Registries.STRUCTURE, location);
        this.structureSetKey = ResourceKey.create(Registries.STRUCTURE_SET, location);
        return this;
    }

    public <T extends Structure> StructureFactory structure(T structure) {
        builder.add(Registries.STRUCTURE, c -> {
            HolderGetter<Biome> holderBio = c.lookup(Registries.BIOME);
            c.register(structureKey, structure);
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
