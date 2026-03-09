package com.xiaoyue.celestial_invoker.content.common.helper;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FindTargetHelper {

    public static BlockHitResult getBlockInCrosshair(Player player, double distance, boolean ignoreFluid) {
        Level level = player.level();
        Vec3 eyePos = player.getEyePosition(1f);
        Vec3 lookVec = player.getLookAngle();
        Vec3 endPos = eyePos.add(lookVec.scale(distance));
        ClipContext.Fluid fluidMode = ignoreFluid ? ClipContext.Fluid.NONE : ClipContext.Fluid.ANY;
        ClipContext context = new ClipContext(eyePos, endPos, ClipContext.Block.OUTLINE, fluidMode, player);
        return level.clip(context);
    }

    @Nullable
    public static Entity getEntityInCrosshair(Player player, double range, Predicate<Entity> filter) {
        Level level = player.level();
        Vec3 eyePosition = player.getEyePosition(1f);
        Vec3 lookVector = player.getLookAngle();
        Vec3 targetPosition = eyePosition.add(lookVector.x * range, lookVector.y * range, lookVector.z * range);
        AABB searchBox = player.getBoundingBox()
                .expandTowards(lookVector.x * range, lookVector.y * range, lookVector.z * range).inflate(1.0D);
        EntityHitResult hitResult = ProjectileUtil.getEntityHitResult(level, player, eyePosition, targetPosition, searchBox,
                (target) -> !target.isSpectator() && target.isAlive() && target.isPickable() && filter.test(target), 0.3f);
        return hitResult != null ? hitResult.getEntity() : null;
    }

    public static List<Entity> getEntitiesInFov(Player player, double distance, double fov, @Nullable Predicate<Entity> filter) {
        Vec3 eyePos = player.getEyePosition(1f);
        Vec3 lookVec = player.getLookAngle().normalize();
        double cosThreshold = Math.cos(Math.toRadians(fov / 2.0));
        AABB searchArea = player.getBoundingBox().inflate(distance);
        List<Entity> entities = player.level().getEntities(player, searchArea,
                e -> e.isPickable() && !e.isSpectator() && (filter == null || filter.test(e)));
        return entities.stream().filter(e -> {
                    Vec3 toEntity = e.getBoundingBox().getCenter().subtract(eyePos);
                    double distSq = toEntity.lengthSqr();
                    if (distSq > distance * distance) return false;
                    Vec3 dirToEntity = toEntity.normalize();
                    return lookVec.dot(dirToEntity) >= cosThreshold;
                })
                .sorted((e1, e2) -> {
                    double d1 = e1.distanceToSqr(player);
                    double d2 = e2.distanceToSqr(player);
                    return Double.compare(d1, d2);
                }).collect(Collectors.toList());
    }

    public static List<Entity> getEntitiesInFov(Player player, double distance, @Nullable Predicate<Entity> filter) {
        return getEntitiesInFov(player, distance, 70, filter);
    }

    @Nullable
    public static Entity getNearestEntity(Player player, double radius, @Nullable Predicate<Entity> filter) {
        Level level = player.level();
        AABB searchArea = player.getBoundingBox().inflate(radius);
        List<Entity> entities = level.getEntities(player, searchArea,
                e -> e.isPickable() && !e.isSpectator() && (filter == null || filter.test(e)));
        Entity nearest = null;
        double nearestDistSq = Double.MAX_VALUE;
        for (Entity entity : entities) {
            double distSq = entity.distanceToSqr(player);
            if (distSq < nearestDistSq) {
                nearestDistSq = distSq;
                nearest = entity;
            }
        }
        return nearest;
    }
}
