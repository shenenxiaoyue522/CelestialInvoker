package com.xiaoyue.celestial_invoker.content.common;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.builders.NoConfigBuilder;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.xiaoyue.celestial_invoker.content.common.entry.ArmorSetEntry;
import com.xiaoyue.celestial_invoker.content.common.entry.MetalItemEntry;
import com.xiaoyue.celestial_invoker.invoker.tooltip.TooltipLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
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
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public record IRegistrateExtra<R extends AbstractRegistrate<R>>(R registrate) {

    static TagKey<Item> forgeTag(String id) {
        return ItemTags.create(forgeLoc(id));
    }

    static ResourceLocation forgeLoc(String path) {
        return new ResourceLocation("forge", path);
    }

    public TooltipLoader genSubscribeTooltips() {
        TooltipLoader loader = new TooltipLoader(registrate.getModid());
        loader.generator(registrate);
        return loader;
    }

    public <T> ResourceKey<T> createKey(ResourceKey<? extends Registry<T>> key, String id) {
        return ResourceKey.create(key, new ResourceLocation(registrate.getModid(), id));
    }

    public <T extends Potion> NoConfigBuilder<Potion, T, R> potion(String name, NonNullSupplier<T> sup) {
        return registrate.entry(name, cb -> new NoConfigBuilder<>(registrate, registrate, name, cb, ForgeRegistries.Keys.POTIONS, sup));
    }

    public <T extends MobEffect> NoConfigBuilder<MobEffect, T, R> simpleEffect(String name, NonNullSupplier<T> sup, String desc) {
        registrate.addRawLang("effect." + registrate.getModid() + "." + name + ".description", desc);
        return registrate.entry(name, cb -> new NoConfigBuilder<>(registrate, registrate, name, cb, ForgeRegistries.Keys.MOB_EFFECTS, sup)
                .lang(MobEffect::getDescriptionId));
    }

    public String getTabName(String name) {
        return name.equals("tab") ? RegistrateLangProvider.toEnglishName(registrate.getModid()) :
                RegistrateLangProvider.toEnglishName(registrate.getModid() + "_" + name);
    }

    public RegistryEntry<CreativeModeTab> buildCreativeTab(Consumer<CreativeModeTab.Builder> config) {
        return buildCreativeTab("tab", getTabName("tab"), config);
    }

    public RegistryEntry<CreativeModeTab> buildCreativeTab(String name, Consumer<CreativeModeTab.Builder> config) {
        return buildCreativeTab(name, getTabName(name), config);
    }

    public RegistryEntry<CreativeModeTab> buildCreativeTab(String name, String def, Consumer<CreativeModeTab.Builder> config) {
        ResourceLocation id = new ResourceLocation(registrate.getModid(), name);
        registrate.defaultCreativeTab(ResourceKey.create(Registries.CREATIVE_MODE_TAB, id));
        return this.buildCreativeTabImpl(name, registrate.addLang("itemGroup", id, def), config);
    }

    public RegistryEntry<CreativeModeTab> buildCreativeTabImpl(String name, Component comp, Consumer<CreativeModeTab.Builder> config) {
        return registrate.generic(registrate, name, Registries.CREATIVE_MODE_TAB, () -> {
            CreativeModeTab.Builder builder = CreativeModeTab.builder().title(comp).withTabsBefore(CreativeModeTabs.SPAWN_EGGS);
            config.accept(builder);
            return builder.build();
        }).register();
    }

    public <T extends Item> ItemEntry<T> armor(String name, String path, ArmorItem.Type type, NonNullFunction<Item.Properties, T> item) {
        return registrate.item(name + "_" + type.getName(), item).model((ctx, pvd) ->
                pvd.generated(ctx, pvd.modLoc("item/" + path + ctx.getName()))).tag(Tags.Items.ARMORS, Bindings.getArmorSlotTag(type)).register();
    }

    public <T extends Item> ArmorSetEntry<T> armors(String name, String path, ArmorTypeCallback<T> item) {
        return armors(type -> name + "_" + type.getName(), path, item);
    }

    public <T extends Item> ArmorSetEntry<T> armors(ArmorNameCallback name, String path, ArmorTypeCallback<T> item) {
        var map = Arrays.stream(ArmorItem.Type.values()).collect(Collectors.toMap(type -> type, type -> registrate.item(name.onCallback(type), item.onCallback(type))
                .model((ctx, pvd) -> pvd.generated(ctx, pvd.modLoc("item/" + path + ctx.getName())))
                .tag(Tags.Items.ARMORS, Bindings.getArmorSlotTag(type)).register(), (a, b) -> b, HashMap::new));
        return ArmorSetEntry.handler(registrate, new ArmorSetEntry<>(map));
    }

    public MetalItemEntry<Item, Block> slimeMetal(String id) {
        return this.metal(id, Item::new, p -> new Block(BlockBehaviour.Properties
                .of().sound(SoundType.METAL).requiresCorrectToolForDrops().strength(5f)));
    }

    public <T extends Item, B extends Block> MetalItemEntry<T, B> metal(String id, NonNullFunction<Item.Properties, T> item, NonNullFunction<BlockBehaviour.Properties, B> block) {
        return new MetalItemEntry<>(ingotItem(id, item), nuggetItem(id, item), metalBlock(id, block));
    }

    public <T extends Item> ItemEntry<T> ingotItem(String id, NonNullFunction<Item.Properties, T> item) {
        return metalBuilder(id, "ingot", item).register();
    }

    public <T extends Item> ItemEntry<T> nuggetItem(String id, NonNullFunction<Item.Properties, T> item) {
        return metalBuilder(id, "nugget", item).register();
    }

    public <T extends Item> ItemBuilder<T, R> metalBuilder(String id, String type, NonNullFunction<Item.Properties, T> item) {
        return registrate.item(id + "_" + type, item).model((ctx, pvd) ->
                pvd.generated(ctx, pvd.modLoc("item/metal/" + ctx.getName()))).tag(forgeTag(type + "s/" + id));
    }

    public <B extends Block> BlockEntry<B> metalBlock(String id, NonNullFunction<BlockBehaviour.Properties, B> block) {
        return registrate.block(id + "_block", block).blockstate((ctx, pvd) ->
                        pvd.simpleBlock(ctx.get(), pvd.models().cubeAll(ctx.getName(), pvd.modLoc("block/metal/" + ctx.getName()))))
                .item().tag(forgeTag("storage_blocks/" + id)).build().register();
    }

    @FunctionalInterface
    public interface ArmorTypeCallback<T> {
        NonNullFunction<Item.Properties, T> onCallback(ArmorItem.Type type);
    }

    @FunctionalInterface
    public interface ArmorNameCallback {
        String onCallback(ArmorItem.Type type);
    }
}
