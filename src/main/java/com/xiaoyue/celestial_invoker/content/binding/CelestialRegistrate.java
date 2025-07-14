package com.xiaoyue.celestial_invoker.content.binding;

import com.tterrag.registrate.builders.NoConfigBuilder;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.xiaoyue.celestial_invoker.content.generator.GeneratorTypes;
import com.xiaoyue.celestial_invoker.content.tooltip.TooltipLoader;
import dev.xkmc.l2library.base.L2Registrate;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.common.data.SoundDefinition;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;

@SuppressWarnings("unused")
public class CelestialRegistrate extends L2Registrate {
    public CelestialRegistrate(String modid) {
        super(modid);
    }

    public void addModTooltipGen() {
        this.addDataGenerator(ProviderType.LANG, new TooltipLoader(getModid())::generator);
    }

    public void addDatapackEntriesGen(RegistrySetBuilder builder) {
        this.addDataGenerator(GeneratorTypes.DATA_ENTRIES(builder), p -> {});
    }

    public RegistryEntry<SoundEvent> sound(String name, float range, SoundDefinition def) {
        SoundEvent sound = SoundEvent.createFixedRangeEvent(new ResourceLocation(getModid(), name), range);
        this.addDataGenerator(GeneratorTypes.SOUND_EVENT, e -> e.add(sound, def));
        return this.generic(this, name, Registries.SOUND_EVENT, () -> sound).register();
    }

    public RegistryEntry<SoundEvent> sound(String name, SoundDefinition def) {
        SoundEvent sound = SoundEvent.createVariableRangeEvent(new ResourceLocation(getModid(), name));
        this.addDataGenerator(GeneratorTypes.SOUND_EVENT, e -> e.add(sound, def));
        return this.generic(this, name, Registries.SOUND_EVENT, () -> sound).register();
    }

    public <T extends Potion> NoConfigBuilder<Potion, T, CelestialRegistrate> potion(String name, NonNullSupplier<T> sup) {
        return this.entry(name, cb -> new NoConfigBuilder<>(this, this, name, cb, ForgeRegistries.Keys.POTIONS, sup));
    }

    public <T extends MobEffect> NoConfigBuilder<MobEffect, T, CelestialRegistrate> simpleEffect(String name, NonNullSupplier<T> sup, String desc) {
        this.addRawLang("effect." + this.getModid() + "." + name + ".description", desc);
        return this.entry(name, cb -> new NoConfigBuilder<>(this, this, name, cb, ForgeRegistries.Keys.MOB_EFFECTS, sup));
    }

    public String getTabName(String name) {
        return name.equals("tab") ? RegistrateLangProvider.toEnglishName(getModid()) :
                RegistrateLangProvider.toEnglishName(this.getModid() + "_" + name);
    }

    public RegistryEntry<CreativeModeTab> buildCreativeTab(String name, Consumer<CreativeModeTab.Builder> config) {
        return this.buildModCreativeTab(name, getTabName(name), config);
    }

    public MetalItemEntry<Item, Block> slimeMetal(String id) {
        return this.metal(id, Item::new, p -> new Block(BlockBehaviour.Properties
                .of().sound(SoundType.METAL).requiresCorrectToolForDrops().strength(5.0F)));
    }

    public <T extends Item, B extends Block> MetalItemEntry<T, B> metal(String id, NonNullFunction<Item.Properties, T> item, NonNullFunction<BlockBehaviour.Properties, B> block) {
        return new MetalItemEntry<>(ingotItem(id, item), nuggetItem(id, item), metalBlock(id, block));
    }

    public <T extends Item> ItemEntry<T> ingotItem(String id, NonNullFunction<Item.Properties, T> item) {
        return this.item(id + "_ingot", item).model((ctx, pvd) ->
                        pvd.generated(ctx, pvd.modLoc("item/metal/" + ctx.getName())))
                .tag(ItemTags.create(new ResourceLocation("forge:ingots/" + id))).register();
    }

    public <T extends Item> ItemEntry<T> nuggetItem(String id, NonNullFunction<Item.Properties, T> item) {
        return this.item(id + "_nugget", item).model((ctx, pvd) ->
                        pvd.generated(ctx, pvd.modLoc("item/metal/" + ctx.getName())))
                .tag(ItemTags.create(new ResourceLocation("forge:nuggets/" + id))).register();
    }

    public <B extends Block> BlockEntry<B> metalBlock(String id, NonNullFunction<BlockBehaviour.Properties, B> block) {
        return this.block(id + "_block", block).blockstate((ctx, pvd) ->
                        pvd.simpleBlock(ctx.get(), pvd.models().cubeAll(ctx.getName(), pvd.modLoc("block/metal/" + ctx.getName()))))
                .item().tag(ItemTags.create(new ResourceLocation("forge:storage_blocks/" + id)))
                .build().register();
    }
}
