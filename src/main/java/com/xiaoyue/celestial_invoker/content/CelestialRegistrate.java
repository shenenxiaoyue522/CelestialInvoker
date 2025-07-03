package com.xiaoyue.celestial_invoker.content;

import com.tterrag.registrate.builders.NoConfigBuilder;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.xiaoyue.celestial_invoker.content.binding.MetalItemEntry;
import com.xiaoyue.celestial_invoker.content.tooltip.TooltipLoader;
import dev.xkmc.l2library.base.L2Registrate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class CelestialRegistrate extends L2Registrate {
    public CelestialRegistrate(String modid) {
        super(modid);
    }

    public void generatorModTooltip() {
        this.addDataGenerator(ProviderType.LANG, new TooltipLoader(getModid())::generator);
    }

    public <T extends MobEffect> NoConfigBuilder<MobEffect, T, CelestialRegistrate> simpleEffect(String name, NonNullSupplier<T> sup, String desc) {
        this.addRawLang("effect." + this.getModid() + "." + name + ".description", desc);
        return this.entry(name, (cb) -> new NoConfigBuilder<>(this, this, name, cb, ForgeRegistries.Keys.MOB_EFFECTS, sup));
    }

    public RegistryEntry<CreativeModeTab> buildCreativeTab(String name, Consumer<CreativeModeTab.Builder> config) {
        String def = name.equals("tab") ? RegistrateLangProvider.toEnglishName(getModid()) :
                RegistrateLangProvider.toEnglishName(this.getModid() + "_" + name);
        return this.buildModCreativeTab(name, def, config);
    }

    public RegistryEntry<CreativeModeTab> buildTabWithItems(String name, String def, List<ItemStack> items) {
        return this.buildModCreativeTab(name, def,b -> b.displayItems(
                (p, o) -> o.acceptAll(items)));
    }

    public MetalItemEntry<Item, Block> slimeMetal(String id) {
        return this.metal(id, Item::new, p -> new Block(BlockBehaviour.Properties
                .of().sound(SoundType.METAL).requiresCorrectToolForDrops().strength(5.0F)));
    }

    public <T extends Item, B extends Block> MetalItemEntry<T, B> metal(String id, NonNullFunction<Item.Properties, T> item, NonNullFunction<BlockBehaviour.Properties, B> block) {
        ItemEntry<T> ingot = this.item(id + "_ingot", item)
                .model((ctx, pvd) ->
                        pvd.generated(ctx, pvd.modLoc("item/metal/" + ctx.getName())))
                .tag(ItemTags.create(new ResourceLocation("forge:ingots/" + id)))
                .register();
        ItemEntry<T> nugget = this.item(id + "_nugget", item)
                .model((ctx, pvd) ->
                        pvd.generated(ctx, pvd.modLoc("item/metal/" + ctx.getName())))
                .tag(ItemTags.create(new ResourceLocation("forge:nuggets/" + id)))
                .register();
        BlockEntry<B> storge = this.block(id + "_block", block)
                .blockstate((ctx, pvd) ->
                        pvd.simpleBlock(ctx.get(), pvd.models().cubeAll(ctx.getName(),
                                new ResourceLocation(getModid(), "block/metal/" + ctx.getName()))))
                .item().tag(ItemTags.create(new ResourceLocation("forge:storage_blocks/" + id)))
                .build().register();
        return new MetalItemEntry<>(ingot, nugget, storge);
    }
}
