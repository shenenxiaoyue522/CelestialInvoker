package com.xiaoyue.celestial_invoker.content.common.registrar;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.xiaoyue.celestial_invoker.content.common.Bindings;
import com.xiaoyue.celestial_invoker.invoker.config.wrapper.ConfigWrapper;
import com.xiaoyue.celestial_invoker.invoker.tooltip.TooltipLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public record RegistrateExtra<R extends AbstractRegistrate<R>>(R owner) {
    public static boolean addConfigTitle = true;

    public static <R, T extends R> IEntryWrapper<T> entry(RegistryEntry<R, T> entry) {
        return new EntryWrapper<>(entry);
    }

    public  <T> NeoForgeRegister<T> neoforgeRegister(Registry<T> registry) {
        DeferredRegister<T> register = DeferredRegister.create(registry, owner().getModid());
        register.register(Objects.requireNonNull(owner().getModEventBus()));
        return new NeoForgeRegister.Basic<>(register);
    }

    public NeoForgeRegister<DataComponentType<?>> neoforgeRegister(ResourceKey<Registry<DataComponentType<?>>> registry) {
        DeferredRegister.DataComponents register = DeferredRegister.createDataComponents(registry, owner().getModid());
        register.register(Objects.requireNonNull(owner().getModEventBus()));
        return new NeoForgeRegister.Component(register);
    }

    public <T extends ConfigWrapper> T initConfig(ModConfig.Type type, Function<ConfigWrapper.Builder, T> factory) {
        if (addConfigTitle) {
            ConfigWrapper.addTitleTooltip(owner);
            addConfigTitle = false;
        }
        return ConfigWrapper.init(owner, type, factory);
    }

    public TooltipLoader genSubscribeTooltips() {
        TooltipLoader loader = new TooltipLoader(owner().getModid());
        loader.generator(owner());
        return loader;
    }

    public String getTabName(String name) {
        return name.equals("tab") ? RegistrateLangProvider.toEnglishName(owner().getModid()) :
                RegistrateLangProvider.toEnglishName(owner().getModid() + "_" + name);
    }

    public RegistryEntry<CreativeModeTab, CreativeModeTab> buildCreativeTab(Consumer<CreativeModeTab.Builder> config) {
        return buildCreativeTab("tab", getTabName("tab"), config);
    }

    public RegistryEntry<CreativeModeTab, CreativeModeTab> buildCreativeTab(String name, Consumer<CreativeModeTab.Builder> config) {
        return buildCreativeTab(name, getTabName(name), config);
    }

    public  <T extends Item> ItemEntry<T> armor(String name, String path, ArmorItem.Type type, NonNullFunction<Item.Properties, T> item) {
        return owner().item(name + "_" + type.getName(), item).model((ctx, pvd) ->
                pvd.generated(ctx, pvd.modLoc("item/" + path + ctx.getName()))).tag(Tags.Items.ARMORS, Bindings.getArmorSlotTag(type)).register();
    }

    public  <T extends Item> Map<ArmorItem.Type, ItemEntry<T>> armors(String name, String path, ArmorTypeCallback<T> item) {
        return armors(type -> name + "_" + type.getName(), path, item);
    }

    public  <T extends Item> Map<ArmorItem.Type, ItemEntry<T>> armors(ArmorNameCallback name, String path, ArmorTypeCallback<T> item) {
        return Arrays.stream(ArmorItem.Type.values()).collect(Collectors.toMap(type -> type, type -> owner().item(name.onCallback(type), item.onCallback(type))
                .model((ctx, pvd) -> pvd.generated(ctx, pvd.modLoc("item/" + path + ctx.getName())))
                .tag(Tags.Items.ARMORS, Bindings.getArmorSlotTag(type)).register(), (a, b) -> b, TreeMap::new));
    }

    public MetalItemEntry<Item, Block> slimeMetal(String id) {
        return this.metal(id, Item::new, p -> new Block(BlockBehaviour.Properties
                .of().sound(SoundType.METAL).requiresCorrectToolForDrops().strength(5f)));
    }

    public  <T extends Item, B extends Block> MetalItemEntry<T, B> metal(String id, NonNullFunction<Item.Properties, T> item, NonNullFunction<BlockBehaviour.Properties, B> block) {
        return new MetalItemEntry<>(ingotItem(id, item), nuggetItem(id, item), metalBlock(id, block));
    }

    public  <T extends Item> ItemEntry<T> ingotItem(String id, NonNullFunction<Item.Properties, T> item) {
        return metalBuilder(id, "ingot", item, tagC("ingots/" + id)).register();
    }

    public  <T extends Item> ItemEntry<T> nuggetItem(String id, NonNullFunction<Item.Properties, T> item) {
        return metalBuilder(id, "nugget", item, tagC("nuggets/" + id)).register();
    }

    @SafeVarargs
    public final <T extends Item> ItemBuilder<T, R> metalBuilder(String id, String type, NonNullFunction<Item.Properties, T> item, TagKey<Item>... tag) {
        return owner().item(id + "_" + type, item).tag(tag).model((ctx, pvd) ->
                pvd.generated(ctx, pvd.modLoc("item/metal/" + ctx.getName())));
    }

    public  <B extends Block> BlockEntry<B> metalBlock(String id, NonNullFunction<BlockBehaviour.Properties, B> block) {
        return owner().block(id + "_block", block).blockstate((ctx, pvd) ->
                        pvd.simpleBlock(ctx.get(), pvd.models().cubeAll(ctx.getName(), pvd.modLoc("block/metal/" + ctx.getName()))))
                .item().tag(tagC("storage_blocks/" + id)).build().register();
    }

    public static TagKey<Item> tagC(String name) {
        return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", name));
    }

    public RegistryEntry<CreativeModeTab, CreativeModeTab> buildCreativeTab(String name, String text, Consumer<CreativeModeTab.Builder> config) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(owner().getModid(), name);
        owner().defaultCreativeTab(ResourceKey.create(Registries.CREATIVE_MODE_TAB, id));
        return this.buildCreativeTabImpl(name, owner().addLang("itemGroup", id, text), config);
    }

    public RegistryEntry<CreativeModeTab, CreativeModeTab> buildCreativeTabImpl(String name, Component comp, Consumer<CreativeModeTab.Builder> config) {
        return owner().generic(owner(), name, Registries.CREATIVE_MODE_TAB, () -> {
            CreativeModeTab.Builder builder = CreativeModeTab.builder().title(comp).withTabsBefore(CreativeModeTabs.SPAWN_EGGS);
            config.accept(builder);
            return builder.build();
        }).register();
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
