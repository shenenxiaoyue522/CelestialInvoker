package com.xiaoyue.celestial_invoker.content.binding.world;

import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class FeatureHelper {

    private final RegistrySetBuilder builder;
    public final ResourceKey<ConfiguredFeature<?, ?>> configuredKey;
    public final ResourceKey<PlacedFeature> placedKey;
    public final ResourceKey<BiomeModifier> biomeKey;

    public FeatureHelper(ResourceLocation location) {
        this.configuredKey = ResourceKey.create(Registries.CONFIGURED_FEATURE, location);
        this.placedKey = ResourceKey.create(Registries.PLACED_FEATURE, location);
        this.biomeKey = ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, location);
        this.builder = new RegistrySetBuilder();
    }

    public static FeatureHelper builder(ResourceLocation location) {
        return new FeatureHelper(location);
    }

    public <FC extends FeatureConfiguration, F extends Feature<FC>> FeatureHelper configured(
            Function<BootstapContext<ConfiguredFeature<?,?>>, Pair<F, FC>> function) {
        builder.add(Registries.CONFIGURED_FEATURE, c -> {
            Pair<F, FC> obj = function.apply(c);
            FeatureUtils.register(c, configuredKey, obj.left(), obj.right());
        });
        return this;
    }

    public FeatureHelper placed(Function<BootstapContext<PlacedFeature>, List<PlacementModifier>> function) {
        builder.add(Registries.PLACED_FEATURE, c -> {
            var holder = c.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(configuredKey);
            PlacementUtils.register(c, placedKey, holder, function.apply(c));
        });
        return this;
    }

    public FeatureHelper biome(BiFunction<BootstapContext<BiomeModifier>, Holder.Reference<PlacedFeature>, BiomeModifier> function) {
        builder.add(ForgeRegistries.Keys.BIOME_MODIFIERS, c -> {
            var placed = c.lookup(Registries.PLACED_FEATURE).getOrThrow(placedKey);
            c.register(biomeKey, function.apply(c, placed));
        });
        return this;
    }

    public RegistrySetBuilder build() {
        return builder;
    }
}
