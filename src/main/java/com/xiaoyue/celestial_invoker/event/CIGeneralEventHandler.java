package com.xiaoyue.celestial_invoker.event;

import com.xiaoyue.celestial_invoker.content.generic.shared.ClickEmptyPacket;
import com.xiaoyue.celestial_invoker.content.generic.shared.IBouncyProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.xiaoyue.celestial_invoker.CelestialInvoker.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CIGeneralEventHandler {

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        Projectile projectile = event.getProjectile();
        if (event.getRayTraceResult().getType() != HitResult.Type.BLOCK) return;
        if (!(projectile instanceof IBouncyProjectile bouncy)) return;
        if (bouncy.canBounce(projectile)) {
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

    @SubscribeEvent
    public static void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        if (event.getEntity().level().isClientSide()) {
            new ClickEmptyPacket(false, event.getHand()).toServer();
        }
    }

    @SubscribeEvent
    public static void onRightClickEmpty(PlayerInteractEvent.RightClickEmpty event) {
        if (event.getEntity().level().isClientSide()) {
            new ClickEmptyPacket(true, event.getHand()).toServer();
        }
    }
}
