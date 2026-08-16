package com.xiaoyue.celestial_invoker.content.common.helper.breaker;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class MinerConfigBuilder {

    private int maxBlocks = 32;
    private int maxDistance = 10;
    private Predicate<BlockState> blockFilter = state -> true;
    private Predicate<Player> playerFilter = player -> true;
    private Consumer<BlockPos> onBlockBreak = pos -> {};

    public MinerConfigBuilder setMaxBlocks(int maxBlocks) {
        this.maxBlocks = maxBlocks;
        return this;
    }

    public MinerConfigBuilder setMaxDistance(int maxDistance) {
        this.maxDistance = maxDistance;
        return this;
    }

    public MinerConfigBuilder setBlockFilter(Predicate<BlockState> blockFilter) {
        this.blockFilter = blockFilter;
        return this;
    }

    public MinerConfigBuilder onlyBlocks(Block... blocks) {
        Set<Block> blockSet = Set.of(blocks);
        this.blockFilter = state -> blockSet.contains(state.getBlock());
        return this;
    }

    public MinerConfigBuilder onlyTag(TagKey<Block> tag) {
        this.blockFilter = state -> state.is(tag);
        return this;
    }

    public MinerConfigBuilder sameBlock(BlockState original) {
        Block originalBlock = original.getBlock();
        this.blockFilter = state -> state.getBlock() == originalBlock;
        return this;
    }

    public MinerConfigBuilder setPlayerFilter(Predicate<Player> playerFilter) {
        this.playerFilter = playerFilter;
        return this;
    }

    public MinerConfigBuilder checkDurability(ItemStack tool, int costPerBlock) {
        this.playerFilter = player -> {
            if (tool.isDamageableItem()) {
                return tool.getDamageValue() + costPerBlock < tool.getMaxDamage();
            }
            return true;
        };
        return this;
    }

    public MinerConfigBuilder setOnBlockBreak(Consumer<BlockPos> onBlockBreak) {
        this.onBlockBreak = onBlockBreak;
        return this;
    }

    public MinerConfig build() {
        return new MinerConfig(maxBlocks, maxDistance, blockFilter, playerFilter, onBlockBreak);
    }
}

