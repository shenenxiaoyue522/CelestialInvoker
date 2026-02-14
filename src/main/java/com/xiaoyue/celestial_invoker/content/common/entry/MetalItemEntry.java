package com.xiaoyue.celestial_invoker.content.common.entry;

import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public record MetalItemEntry<T extends Item, B extends Block>(ItemEntry<T> ingot, ItemEntry<T> nugget, BlockEntry<B> block) {

    public B blockItem() {
        return block.get();
    }
}
