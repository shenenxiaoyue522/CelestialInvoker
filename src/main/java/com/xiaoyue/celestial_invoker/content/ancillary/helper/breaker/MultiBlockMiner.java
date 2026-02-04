package com.xiaoyue.celestial_invoker.content.ancillary.helper.breaker;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class MultiBlockMiner {

    public static int executeChainMining(Level level, Player player, BlockPos startPos, MinerConfig config) {
        if (!config.getPlayerFilter().test(player)) {
            return 0;
        }
        BlockState startState = level.getBlockState(startPos);
        if (!config.getBlockFilter().test(startState)) {
            return 0;
        }
        Set<BlockPos> minedPositions = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();
        queue.add(startPos);
        minedPositions.add(startPos);
        int blocksMined = 0;
        while (!queue.isEmpty() && blocksMined < config.getMaxBlocks()) {
            BlockPos current = queue.poll();
            if (current != null && !current.equals(startPos)) {
                if (breakBlockSafe(level, player, current, startState)) {
                    blocksMined++;
                    config.getOnBlockBreak().accept(current);
                }
            }
            if (blocksMined < config.getMaxBlocks()) {
                findAdjacentBlocks(level, current, startState, config, minedPositions, queue);
            }
        }
        return blocksMined;
    }

    private static boolean breakBlockSafe(Level level, Player player, BlockPos pos, BlockState targetState) {
        BlockState state = level.getBlockState(pos);
        if (state.isAir() || !level.isInWorldBounds(pos)) {
            return false;
        }
        return level.destroyBlock(pos, true, player);
    }

    private static void findAdjacentBlocks(Level level, BlockPos center, BlockState targetState, MinerConfig config, Set<BlockPos> mined, Queue<BlockPos> queue) {
        for (Direction dir : config.getAllowedDirections()) {
            BlockPos neighbor = center.relative(dir);
            if (isBeyondMaxDistance(center, neighbor, config.getMaxDistance(), dir)) {
                continue;
            }
            if (!mined.contains(neighbor)) {
                BlockState neighborState = level.getBlockState(neighbor);
                if (config.getBlockFilter().test(neighborState)) {
                    mined.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
    }

    private static boolean isBeyondMaxDistance(BlockPos center, BlockPos neighbor, int maxDistance, Direction dir) {
        int dx = Math.abs(neighbor.getX() - center.getX());
        int dy = Math.abs(neighbor.getY() - center.getY());
        int dz = Math.abs(neighbor.getZ() - center.getZ());
        return switch (dir) {
            case UP, DOWN -> dy > maxDistance;
            case NORTH, SOUTH -> dz > maxDistance;
            case EAST, WEST -> dx > maxDistance;
        };
    }
}
