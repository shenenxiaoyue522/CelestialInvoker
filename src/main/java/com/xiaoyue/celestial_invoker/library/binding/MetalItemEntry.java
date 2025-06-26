package com.xiaoyue.celestial_invoker.library.binding;

import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public record MetalItemEntry<T extends Item>(ItemEntry<T> ingot, ItemEntry<T> nugget, BlockEntry<Block> block) {
}
