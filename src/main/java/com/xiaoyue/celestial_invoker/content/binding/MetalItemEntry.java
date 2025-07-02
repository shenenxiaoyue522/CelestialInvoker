package com.xiaoyue.celestial_invoker.content.binding;

import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public record MetalItemEntry<T extends Item, B extends Block>(ItemEntry<T> ingot, ItemEntry<T> nugget, BlockEntry<B> block) {

    public Item blockItem() {
        return block.asItem();
    }
}
