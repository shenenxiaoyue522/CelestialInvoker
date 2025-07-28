package com.xiaoyue.celestial_invoker.content.binding;

import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.builders.NoConfigBuilder;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.xiaoyue.celestial_invoker.content.generator.CelestialProviders;
import com.xiaoyue.celestial_invoker.invoker.config.ConfigHolderMap;
import com.xiaoyue.celestial_invoker.invoker.config.ConfigLoader;
import com.xiaoyue.celestial_invoker.invoker.tooltip.TooltipLoader;
import dev.xkmc.l2library.base.L2Registrate;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.SoundDefinition;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class CelestialRegistrate extends L2Registrate {

    public ResourceKey<CreativeModeTab> defaultCreativeTab;

    public CelestialRegistrate(String modid) {
        super(modid);
        this.defaultCreativeTab = CreativeModeTabs.SEARCH;
    }

    public ConfigHolderMap mapConfig() {
        return ConfigLoader.mapConfig(getModid());
    }

    public void addModTooltipGen() {
        this.addDataGenerator(ProviderType.LANG, new TooltipLoader(getModid())::generator);
    }

    public RegistryEntry<SoundEvent> sound(String name, float range, SoundDefinition def) {
        SoundEvent sound = SoundEvent.createFixedRangeEvent(new ResourceLocation(getModid(), name), range);
        this.addDataGenerator(CelestialProviders.SOUND_EVENT, e -> e.add(sound, def));
        return this.generic(this, name, Registries.SOUND_EVENT, () -> sound).register();
    }

    public RegistryEntry<SoundEvent> sound(String name, SoundDefinition def) {
        SoundEvent sound = SoundEvent.createVariableRangeEvent(new ResourceLocation(getModid(), name));
        this.addDataGenerator(CelestialProviders.SOUND_EVENT, e -> e.add(sound, def));
        return this.generic(this, name, Registries.SOUND_EVENT, () -> sound).register();
    }

    public <T extends Potion> NoConfigBuilder<Potion, T, CelestialRegistrate> potion(String name, NonNullSupplier<T> sup) {
        return this.entry(name, cb -> new NoConfigBuilder<>(this, this, name, cb, ForgeRegistries.Keys.POTIONS, sup));
    }

    public <T extends MobEffect> NoConfigBuilder<MobEffect, T, CelestialRegistrate> simpleEffect(String name, NonNullSupplier<T> sup, String desc) {
        this.addRawLang("effect." + this.getModid() + "." + name + ".description", desc);
        return this.entry(name, cb -> new NoConfigBuilder<>(this, this, name, cb, ForgeRegistries.Keys.MOB_EFFECTS, sup)
                .lang(MobEffect::getDescriptionId));
    }

    public String getTabName(String name) {
        return name.equals("tab") ? RegistrateLangProvider.toEnglishName(getModid()) :
                RegistrateLangProvider.toEnglishName(this.getModid() + "_" + name);
    }

    public RegistryEntry<CreativeModeTab> buildModNameCreativeTab(Consumer<CreativeModeTab.Builder> config) {
        return this.buildModCreativeTab("tab", getTabName("tab"), config);
    }

    public RegistryEntry<CreativeModeTab> buildCreativeTab(String name, Consumer<CreativeModeTab.Builder> config) {
        return this.buildModCreativeTab(name, getTabName(name), config);
    }

    @Override
    public L2Registrate defaultCreativeTab(ResourceKey<CreativeModeTab> creativeModeTab) {
        this.defaultCreativeTab = creativeModeTab;
        return super.defaultCreativeTab(creativeModeTab);
    }

    public <T extends Item> ItemBuilder<T, L2Registrate> nullTabItem(String name, NonNullFunction<Item.Properties, T> func) {
        return this.item(name, func).removeTab(this.defaultCreativeTab);
    }

    public <T extends Item> Map<ArmorItem.Type, RegistryEntry<T>> armors(String name, String path, NonNullFunction<Item.Properties, T> item) {
        return Arrays.stream(ArmorItem.Type.values()).collect(Collectors.toMap(type -> type, type -> this.item(name + "_" + type.getName(), item)
                .model((ctx, pvd) -> pvd.generated(ctx, pvd.modLoc("item/" + path + ctx.getName())))
                .tag(Tags.Items.ARMORS, BindingHandler.getArmorSlotTag(type)).register(), (a, b) -> b, TreeMap::new));
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
                .tag(ItemTags.create(forgeLoc("ingots/" + id))).register();
    }

    public <T extends Item> ItemEntry<T> nuggetItem(String id, NonNullFunction<Item.Properties, T> item) {
        return this.item(id + "_nugget", item).model((ctx, pvd) ->
                        pvd.generated(ctx, pvd.modLoc("item/metal/" + ctx.getName())))
                .tag(ItemTags.create(forgeLoc("nuggets/" + id))).register();
    }

    public <B extends Block> BlockEntry<B> metalBlock(String id, NonNullFunction<BlockBehaviour.Properties, B> block) {
        return this.block(id + "_block", block).blockstate((ctx, pvd) ->
                        pvd.simpleBlock(ctx.get(), pvd.models().cubeAll(ctx.getName(), pvd.modLoc("block/metal/" + ctx.getName()))))
                .item().tag(ItemTags.create(forgeLoc("storage_blocks/" + id)))
                .build().register();
    }

    public static ResourceLocation forgeLoc(String path) {
        return new ResourceLocation("forge", path);
    }
}
