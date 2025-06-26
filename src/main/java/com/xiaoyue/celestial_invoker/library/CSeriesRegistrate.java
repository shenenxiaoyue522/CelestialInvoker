package com.xiaoyue.celestial_invoker.library;

import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.xiaoyue.celestial_invoker.library.binding.MetalItemEntry;
import dev.xkmc.l2library.base.L2Registrate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class CSeriesRegistrate extends L2Registrate {
    public CSeriesRegistrate(String modid) {
        super(modid);
    }

    public MetalItemEntry<Item> metal(String id) {
        return this.metal(id, Item::new);
    }

    public <T extends Item> MetalItemEntry<T> metal(String id, NonNullFunction<Item.Properties, T> factory) {
        ItemEntry<T> ingot = this.item(id + "_ingot", factory)
                .tag(ItemTags.create(new ResourceLocation("forge:ingots/" + id)))
                .register();
        ItemEntry<T> nugget = this.item(id + "_nugget", factory)
                .tag(ItemTags.create(new ResourceLocation("forge:nuggets/" + id)))
                .register();
        BlockEntry<Block> block = this.block(id + "_block", p -> new Block(BlockBehaviour.Properties
                        .of().sound(SoundType.METAL).requiresCorrectToolForDrops().strength(5.0F)))
                .item().tag(ItemTags.create(new ResourceLocation("forge:storage_blocks/" + id)))
                .build().register();
        return new MetalItemEntry<>(ingot, nugget, block);
    }
}
