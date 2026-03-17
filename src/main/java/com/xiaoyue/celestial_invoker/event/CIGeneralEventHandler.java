package com.xiaoyue.celestial_invoker.event;

import com.xiaoyue.celestial_invoker.content.generic.items.api.IClickInteraction;
import com.xiaoyue.celestial_invoker.content.generic.shared.ClickEmptyPayload;
import com.xiaoyue.celestial_invoker.content.generic.shared.IBouncyProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import static com.xiaoyue.celestial_invoker.CelestialInvoker.MODID;

@EventBusSubscriber(modid = MODID)
public class CIGeneralEventHandler {

    @SubscribeEvent
    public static void registerPayload(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(ClickEmptyPayload.ID, ClickEmptyPayload.CODEC, ClickEmptyPayload::handlePacket);
    }

    @SubscribeEvent
    public static void leftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        if (event.getSide().equals(LogicalSide.SERVER)) {
            ClickEmptyPayload.acceptItem(event.getEntity(), event.getHand(), false);
        } else {
            PacketDistributor.sendToServer(new ClickEmptyPayload(false, event.getHand()));
        }
    }

    @SubscribeEvent
    public static void rightClickEmpty(PlayerInteractEvent.RightClickEmpty event) {
        if (event.getSide().equals(LogicalSide.SERVER)) {
            ClickEmptyPayload.acceptItem(event.getEntity(), event.getHand(), true);
        } else {
            PacketDistributor.sendToServer(new ClickEmptyPayload(true, event.getHand()));
        }
    }

    @SubscribeEvent
    public static void leftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof IClickInteraction item) {
            item.onLeftClickBlock(event.getEntity(), stack, event);
        }
    }

    @SubscribeEvent
    public static void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof IClickInteraction item) {
            item.onRightClickBlock(event.getEntity(), stack, event);
        }
    }

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
