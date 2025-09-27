package com.xiaoyue.celestial_invoker.content.ancillary.handlers.breaker;

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
    private Set<Direction> allowedDirections = EnumSet.allOf(Direction.class);

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

    public MinerConfigBuilder setAllowedDirections(Set<Direction> directions) {
        this.allowedDirections = EnumSet.copyOf(directions);
        return this;
    }

    public MinerConfigBuilder upwardOnly() {
        this.allowedDirections = EnumSet.of(Direction.UP);
        return this;
    }

    public MinerConfigBuilder downwardOnly() {
        this.allowedDirections = EnumSet.of(Direction.DOWN);
        return this;
    }

    public MinerConfigBuilder horizontalOnly() {
        this.allowedDirections = EnumSet.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);
        return this;
    }

    public MinerConfigBuilder allDirections() {
        this.allowedDirections = EnumSet.allOf(Direction.class);
        return this;
    }

    public MinerConfigBuilder verticalOnly() {
        this.allowedDirections = EnumSet.of(Direction.UP, Direction.DOWN);
        return this;
    }

    public MinerConfigBuilder directions(Direction... directions) {
        this.allowedDirections = EnumSet.of(directions[0], directions);
        return this;
    }

    public MinerConfig build() {
        return new MinerConfig(maxBlocks, maxDistance, blockFilter, playerFilter, onBlockBreak, allowedDirections);
    }
}

