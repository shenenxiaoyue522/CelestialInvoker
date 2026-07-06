package com.xiaoyue.celestial_invoker.event;

import com.xiaoyue.celestial_invoker.content.common.entry.ArmorSetEntry;
import com.xiaoyue.celestial_invoker.content.generic.item.api.IClickInteraction;
import com.xiaoyue.celestial_invoker.content.generic.shared.ClickEmptyPayload;
import com.xiaoyue.celestial_invoker.content.generic.shared.IBouncyProjectile;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.xiaoyue.celestial_invoker.CelestialInvoker.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CIGeneralEventHandler {

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        ArmorSetEntry.handlers.forEach((set, handler) -> {
            if (set.isFullSet(entity, handler.requiredCount())) handler.onSetTick(entity);
        });
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onDamage(LivingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        ArmorSetEntry.handlers.forEach((set, handler) -> {
            if (set.isFullSet(entity, handler.requiredCount())) handler.onDamaged(entity, event, event.getSource());
        });
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        ArmorSetEntry.handlers.forEach((set, handler) -> {
            if (set.isFullSet(entity, handler.requiredCount())) handler.onDeath(entity, event, event.getSource());
        });
    }

    @SubscribeEvent
    public static void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        if (event.getEntity().level().isClientSide()) {
            new ClickEmptyPayload(false, event.getHand()).toServer();
        }
    }

    @SubscribeEvent
    public static void onRightClickEmpty(PlayerInteractEvent.RightClickEmpty event) {
        if (event.getEntity().level().isClientSide()) {
            new ClickEmptyPayload(true, event.getHand()).toServer();
        }
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof IClickInteraction item) {
            item.onLeftClickBlock(event.getEntity(), stack, event);
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof IClickInteraction item) {
            item.onRightClickBlock(event.getEntity(), stack, event);
        }
    }

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        Projectile projectile = event.getProjectile();
        HitResult hit = event.getRayTraceResult();
        if (hit == null || hit.getType() != HitResult.Type.BLOCK) return;
        if (!(projectile instanceof IBouncyProjectile bouncy)) return;
        if (bouncy.canBounce(projectile)) {
            int maxBounces = bouncy.getMaxBounces();
            int currentBounces = bouncy.getBounceCount();
            if (maxBounces > 0 && currentBounces >= maxBounces) {
                return;
            }
            event.setImpactResult(ProjectileImpactEvent.ImpactResult.STOP_AT_CURRENT);
            BlockHitResult result = (BlockHitResult) hit;
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
