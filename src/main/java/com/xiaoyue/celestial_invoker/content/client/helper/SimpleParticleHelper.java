package com.xiaoyue.celestial_invoker.content.client.helper;

import com.xiaoyue.celestial_invoker.content.network.SpawnParticlePayload;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class SimpleParticleHelper {

    private final Level level;
    private final Random random = new Random();
    private ParticleOptions particle;
    private int count = 10;
    private Vec3 position;
    private Vec3 targetPosition;
    private double speed = 0.3;
    private double spreadMin = 0.0;
    private double spreadMax = 3.0;
    private boolean gravity = false;
    private double offsetX = 0, offsetY = 0, offsetZ = 0;
    private final List<Function<Vec3, Vec3>> modifiers = new ArrayList<>();
    private boolean useCustomVelocity = false;
    private Vec3 customVelocity;
    private boolean useBatchMode = false;

    public SimpleParticleHelper(Level level) {
        this.level = level;
        this.position = Vec3.ZERO;
    }

    public SimpleParticleHelper particle(ParticleOptions particle) {
        this.particle = particle;
        return this;
    }

    public SimpleParticleHelper particle(SimpleParticleType type) {
        this.particle = type;
        return this;
    }

    public SimpleParticleHelper count(int count) {
        this.count = Math.max(1, count);
        return this;
    }

    public SimpleParticleHelper position(Vec3 pos) {
        this.position = pos;
        return this;
    }

    public SimpleParticleHelper position(double x, double y, double z) {
        this.position = new Vec3(x, y, z);
        return this;
    }

    public SimpleParticleHelper flyTo(Vec3 target) {
        this.targetPosition = target;
        return this;
    }

    public SimpleParticleHelper speed(double speed) {
        this.speed = Math.max(0.01, speed);
        return this;
    }

    public SimpleParticleHelper velocity(Vec3 velocity) {
        this.customVelocity = velocity;
        this.useCustomVelocity = true;
        return this;
    }

    public SimpleParticleHelper spread(double minRadius, double maxRadius) {
        this.spreadMin = minRadius;
        this.spreadMax = maxRadius;
        return this;
    }

    public SimpleParticleHelper offset(double x, double y, double z) {
        this.offsetX = x;
        this.offsetY = y;
        this.offsetZ = z;
        return this;
    }

    public SimpleParticleHelper gravity(boolean gravity) {
        this.gravity = gravity;
        return this;
    }

    public SimpleParticleHelper modify(Function<Vec3, Vec3> modifier) {
        this.modifiers.add(modifier);
        return this;
    }

    public SimpleParticleHelper batchMode() {
        this.useBatchMode = true;
        return this;
    }

    public SimpleParticleHelper ring(double radius, double height) {
        this.spreadMin = radius;
        this.spreadMax = radius;
        this.offsetY = height;
        return this;
    }

    public SimpleParticleHelper drawPoints(List<Vec3> points) {
        for (Vec3 point : points) {
            this.position(point).count(1).speed(0);
            spawn();
        }
        return this;
    }

    public SimpleParticleHelper drawLine(List<Vec3> points, int density) {
        if (points.size() < 2) return this;
        for (int i = 0; i < points.size() - 1; i++) {
            Vec3 start = points.get(i);
            Vec3 end = points.get(i + 1);
            for (int j = 0; j <= density; j++) {
                double t = (double) j / density;
                Vec3 pos = start.lerp(end, t);
                this.position(pos).count(1).speed(0);
                spawn();
            }
        }
        return this;
    }

    public SimpleParticleHelper drawClosedLine(List<Vec3> points, int density) {
        if (points.size() < 2) return this;
        List<Vec3> closedPoints = new ArrayList<>(points);
        closedPoints.add(points.get(0));
        return drawLine(closedPoints, density);
    }

    public void spawn() {
        if (particle == null) {
            throw new IllegalStateException("Particle type not set! Please call .particle()");
        }
        if (level.isClientSide()) {
            spawnClient();
        } else if (level instanceof ServerLevel serverLevel) {
            spawnServer(serverLevel);
        } else {
            throw new IllegalStateException("Unrecognized Level type");
        }
    }

    private void spawnClient() {
        for (int i = 0; i < count; i++) {
            Vec3 spawnPos = calculateSpawnPosition();
            Vec3 velocity = calculateVelocity(spawnPos);
            for (Function<Vec3, Vec3> modifier : modifiers) {
                velocity = modifier.apply(velocity);
            }
            level.addParticle(particle, spawnPos.x, spawnPos.y, spawnPos.z, velocity.x, velocity.y, velocity.z);
        }
    }

    private void spawnServer(ServerLevel serverLevel) {
        boolean shouldBatch = useBatchMode || ((targetPosition != null || useCustomVelocity) && count > 8);
        if (shouldBatch) {
            List<Vec3> positions = new ArrayList<>();
            List<Vec3> velocities = new ArrayList<>();
            int actualCount = Math.min(count, 256);
            for (int i = 0; i < actualCount; i++) {
                positions.add(calculateSpawnPosition());
                velocities.add(calculateVelocity(positions.get(i)));
            }
            SpawnParticlePayload payload = new SpawnParticlePayload(particle.getType(), positions, velocities);
            PacketDistributor.sendToPlayersNear(serverLevel, null, position.x, position.y, position.z, 32, payload);
            return;
        }
        if (targetPosition == null && !useCustomVelocity) {
            double range = spreadMax > 0 ? spreadMax : 1.0;
            serverLevel.sendParticles(particle, position.x, position.y, position.z, Math.min(count, 50), range, range, range, speed);
            return;
        }
        int sendCount = count;
        for (int i = 0; i < sendCount; i++) {
            Vec3 spawnPos = calculateSpawnPosition();
            Vec3 velocity = calculateVelocity(spawnPos);
            serverLevel.sendParticles(particle, spawnPos.x, spawnPos.y, spawnPos.z, 0, velocity.x, velocity.y, velocity.z, 1.0);
        }
    }

    private Vec3 calculateSpawnPosition() {
        Vec3 basePos = position.add(offsetX, offsetY, offsetZ);
        if (targetPosition != null && spreadMax > 0) {
            double radius = spreadMin + random.nextDouble() * (spreadMax - spreadMin);
            double theta = random.nextDouble() * 2 * Math.PI;
            double phi = random.nextDouble() * Math.PI;
            double x = radius * Math.sin(phi) * Math.cos(theta);
            double y = radius * Math.cos(phi) + 0.5; // 稍微抬高
            double z = radius * Math.sin(phi) * Math.sin(theta);
            return targetPosition.add(x, y, z);
        }
        if (spreadMax > 0) {
            double radius = spreadMin + random.nextDouble() * (spreadMax - spreadMin);
            double theta = random.nextDouble() * 2 * Math.PI;
            double phi = random.nextDouble() * Math.PI;
            return basePos.add(radius * Math.sin(phi) * Math.cos(theta), radius * Math.cos(phi), radius * Math.sin(phi) * Math.sin(theta));
        }
        return basePos;
    }

    private Vec3 calculateVelocity(Vec3 spawnPos) {
        if (useCustomVelocity) {
            return customVelocity;
        }
        if (targetPosition != null) {
            Vec3 toTarget = targetPosition.subtract(spawnPos);
            if (toTarget.lengthSqr() < 0.0001) {
                return Vec3.ZERO;
            }
            return toTarget.normalize().scale(speed);
        }
        Vec3 randomDir = new Vec3((random.nextDouble() - 0.5) * 2, (random.nextDouble() - 0.5) * 2, (random.nextDouble() - 0.5) * 2);
        if (randomDir.lengthSqr() < 0.0001) {
            randomDir = new Vec3(0, 1, 0);
        }
        Vec3 result = randomDir.normalize().scale(speed);
        if (gravity) {
            result = result.add(0, -0.02, 0);
        }
        return result;
    }
}
