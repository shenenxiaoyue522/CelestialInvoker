package com.xiaoyue.celestial_invoker.event;

import com.xiaoyue.celestial_invoker.content.generic.shared.IBouncyProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;

import static com.xiaoyue.celestial_invoker.CelestialInvoker.MODID;

@EventBusSubscriber(modid = MODID)
public class CIGeneralEventHandler {

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        Projectile projectile = event.getProjectile();
        if (event.getRayTraceResult().getType() != HitResult.Type.BLOCK) return;
        if (projectile instanceof IBouncyProjectile bouncy) {
            if (!bouncy.canBounce(projectile)) return;
            int maxBounces = bouncy.getMaxBounces();
            int currentBounces = bouncy.getBounceCount();
            if (maxBounces > 0 && currentBounces >= maxBounces) {
                return;
            }
            event.setCanceled(true);
            BlockHitResult result = (BlockHitResult) event.getRayTraceResult();
            Vec3 incoming = projectile.getDeltaMovement();
            Vec3 normal = Vec3.atLowerCornerOf(result.getDirection().getNormal());
            double dot = incoming.dot(normal);
            Vec3 reflected = incoming.subtract(normal.scale(2.0 * dot));
            reflected = reflected.scale(bouncy.getBounceEnergy());
            projectile.setDeltaMovement(reflected);
            bouncy.setBounceCount(currentBounces + 1);
            projectile.setPos(result.getLocation().add(reflected.normalize().scale(0.1)));
            bouncy.onBounce(projectile, result);
            projectile.hasImpulse = true;
        }
    }
}
