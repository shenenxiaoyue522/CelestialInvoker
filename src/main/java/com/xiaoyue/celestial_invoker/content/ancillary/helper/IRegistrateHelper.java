package com.xiaoyue.celestial_invoker.content.ancillary.helper;

import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.builders.NoConfigBuilder;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.xiaoyue.celestial_invoker.content.ancillary.BindingHandler;
import com.xiaoyue.celestial_invoker.content.ancillary.CelestialRegistrate;
import com.xiaoyue.celestial_invoker.content.ancillary.entry.MetalItemEntry;
import dev.xkmc.l2library.base.L2Registrate;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public interface IRegistrateHelper<R extends L2Registrate> {

    static <R extends L2Registrate> IRegistrateHelper<L2Registrate> simpleHelper(R registrate) {
        return () -> registrate;
    }

    static TagKey<Item> forgeTag(String id) {
        return ItemTags.create(forgeLoc(id));
    }

    static ResourceLocation forgeLoc(String path) {
        return new ResourceLocation("forge", path);
    }

    R owner();

    default <T> ResourceKey<T> createKey(ResourceKey<? extends Registry<T>> key, String id) {
        return ResourceKey.create(key, new ResourceLocation(owner().getModid(), id));
    }

    default <T extends Recipe<?>> RegistryEntry<RecipeSerializer<T>> recipeSerial(String name, NonNullSupplier<RecipeSerializer<T>> sup) {
        return owner().simple(this, name, ForgeRegistries.Keys.RECIPE_SERIALIZERS, sup);
    }

    default <T extends Potion> NoConfigBuilder<Potion, T, R> potion(String name, NonNullSupplier<T> sup) {
        return owner().entry(name, cb -> new NoConfigBuilder<>(owner(), owner(), name, cb, ForgeRegistries.Keys.POTIONS, sup));
    }

    default <T extends MobEffect> NoConfigBuilder<MobEffect, T, R> simpleEffect(String name, NonNullSupplier<T> sup, String desc) {
        owner().addRawLang("effect." + owner().getModid() + "." + name + ".description", desc);
        return owner().entry(name, cb -> new NoConfigBuilder<>(owner(), owner(), name, cb, ForgeRegistries.Keys.MOB_EFFECTS, sup)
                .lang(MobEffect::getDescriptionId));
    }

    default String getTabName(String name) {
        return name.equals("tab") ? RegistrateLangProvider.toEnglishName(owner().getModid()) :
                RegistrateLangProvider.toEnglishName(owner().getModid() + "_" + name);
    }

    default RegistryEntry<CreativeModeTab> buildModNameCreativeTab(Consumer<CreativeModeTab.Builder> config) {
        return owner().buildModCreativeTab("tab", getTabName("tab"), config);
    }

    default RegistryEntry<CreativeModeTab> buildCreativeTab(String name, Consumer<CreativeModeTab.Builder> config) {
        return owner().buildModCreativeTab(name, getTabName(name), config);
    }

    default <T extends Item> ItemEntry<T> armor(String name, String path, ArmorItem.Type type, NonNullFunction<Item.Properties, T> item) {
        return owner().item(name + "_" + type.getName(), item).model((ctx, pvd) ->
                pvd.generated(ctx, pvd.modLoc("item/" + path + ctx.getName()))).tag(Tags.Items.ARMORS, BindingHandler.getArmorSlotTag(type)).register();
    }

    default <T extends Item> Map<ArmorItem.Type, ItemEntry<T>> armors(String name, String path, CelestialRegistrate.ArmorTypeCallback<T> item) {
        return armors(type -> name + "_" + type.getName(), path, item);
    }

    default <T extends Item> Map<ArmorItem.Type, ItemEntry<T>> armors(CelestialRegistrate.ArmorNameCallback name, String path, CelestialRegistrate.ArmorTypeCallback<T> item) {
        return Arrays.stream(ArmorItem.Type.values()).collect(Collectors.toMap(type -> type, type -> owner().item(name.onCallback(type), item.onCallback(type))
                .model((ctx, pvd) -> pvd.generated(ctx, pvd.modLoc("item/" + path + ctx.getName())))
                .tag(Tags.Items.ARMORS, BindingHandler.getArmorSlotTag(type)).register(), (a, b) -> b, TreeMap::new));
    }

    default MetalItemEntry<Item, Block> slimeMetal(String id) {
        return this.metal(id, Item::new, p -> new Block(BlockBehaviour.Properties
                .of().sound(SoundType.METAL).requiresCorrectToolForDrops().strength(5f)));
    }

    default <T extends Item, B extends Block> MetalItemEntry<T, B> metal(String id, NonNullFunction<Item.Properties, T> item, NonNullFunction<BlockBehaviour.Properties, B> block) {
        return new MetalItemEntry<>(ingotItem(id, item), nuggetItem(id, item), metalBlock(id, block));
    }

    default <T extends Item> ItemEntry<T> ingotItem(String id, NonNullFunction<Item.Properties, T> item) {
        return metalBuilder(id, "ingot", item).register();
    }

    default <T extends Item> ItemEntry<T> nuggetItem(String id, NonNullFunction<Item.Properties, T> item) {
        return metalBuilder(id, "nugget", item).register();
    }

    default <T extends Item> ItemBuilder<T, L2Registrate> metalBuilder(String id, String type, NonNullFunction<Item.Properties, T> item) {
        return owner().item(id + "_" + type, item).model((ctx, pvd) ->
                pvd.generated(ctx, pvd.modLoc("item/metal/" + ctx.getName()))).tag(forgeTag(type + "s/" + id));
    }

    default <B extends Block> BlockEntry<B> metalBlock(String id, NonNullFunction<BlockBehaviour.Properties, B> block) {
        return owner().block(id + "_block", block).blockstate((ctx, pvd) ->
                        pvd.simpleBlock(ctx.get(), pvd.models().cubeAll(ctx.getName(), pvd.modLoc("block/metal/" + ctx.getName()))))
                .item().tag(forgeTag("storage_blocks/" + id)).build().register();
    }

    @FunctionalInterface
    interface ArmorTypeCallback<T> {
        NonNullFunction<Item.Properties, T> onCallback(ArmorItem.Type type);
    }

    @FunctionalInterface
    interface ArmorNameCallback {
        String onCallback(ArmorItem.Type type);
    }
}
