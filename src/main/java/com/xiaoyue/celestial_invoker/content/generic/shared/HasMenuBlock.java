package com.xiaoyue.celestial_invoker.content.generic.shared;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class HasMenuBlock extends Block {
    private final MenuConstructor constructor;

    public HasMenuBlock(Properties pProperties, MenuConstructor constructor) {
        super(pProperties);
        this.constructor = constructor;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        pPlayer.openMenu(pState.getMenuProvider(pLevel, pPos));
        return InteractionResult.SUCCESS;
    }

    @Override
    public @Nullable MenuProvider getMenuProvider(BlockState pState, Level pLevel, BlockPos pPos) {
        return new SimpleMenuProvider(constructor, this.getName());
    }
}
