package com.xiaoyue.celestial_invoker.content.ancillary.helper.breaker;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class MinerConfig {

    private final int maxBlocks;
    private final int maxDistance;
    private final Predicate<BlockState> blockFilter;
    private final Predicate<Player> playerFilter;
    private final Consumer<BlockPos> onBlockBreak;
    private final Set<Direction> allowedDirections;
    private final boolean includeVertical;
    private final boolean includeHorizontal;

    public MinerConfig(int maxBlocks, int maxDistance, Predicate<BlockState> blockFilter, Predicate<Player> playerFilter, Consumer<BlockPos> onBlockBreak, Set<Direction> allowedDirections) {
        this.maxBlocks = maxBlocks;
        this.maxDistance = maxDistance;
        this.blockFilter = blockFilter;
        this.playerFilter = playerFilter;
        this.onBlockBreak = onBlockBreak;
        this.allowedDirections = EnumSet.copyOf(allowedDirections);
        this.includeVertical = allowedDirections.contains(Direction.UP) || allowedDirections.contains(Direction.DOWN);
        this.includeHorizontal = allowedDirections.stream().anyMatch(dir -> dir == Direction.NORTH || dir == Direction.SOUTH || dir == Direction.EAST || dir == Direction.WEST);
    }

    public MinerConfig(int maxBlocks, int maxDistance, Predicate<BlockState> blockFilter, Predicate<Player> playerFilter, Consumer<BlockPos> onBlockBreak) {
        this(maxBlocks, maxDistance, blockFilter, playerFilter, onBlockBreak, EnumSet.allOf(Direction.class));
    }

    public int getMaxBlocks() {
        return maxBlocks;
    }

    public int getMaxDistance() {
        return maxDistance;
    }

    public Predicate<BlockState> getBlockFilter() {
        return blockFilter;
    }

    public Predicate<Player> getPlayerFilter() {
        return playerFilter;
    }

    public Consumer<BlockPos> getOnBlockBreak() {
        return onBlockBreak;
    }

    public Set<Direction> getAllowedDirections() {
        return allowedDirections;
    }

    public boolean includeVertical() {
        return includeVertical;
    }

    public boolean includeHorizontal() {
        return includeHorizontal;
    }
}
