package com.xiaoyue.celestial_invoker.content.generic.shared;

import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.BlockHitResult;

public interface IBouncyProjectile {

    default boolean canBounce(Projectile proj) {
        return true;
    }

    int getMaxBounces();

    int getBounceCount();

    void setBounceCount(int count);

    float getBounceEnergy();

    default void onBounce(Projectile proj, BlockHitResult result) {
    }
}

