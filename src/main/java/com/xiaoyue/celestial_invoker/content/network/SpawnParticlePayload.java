package com.xiaoyue.celestial_invoker.content.network;

import com.xiaoyue.celestial_invoker.content.client.helper.SimpleParticleHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SpawnParticlePayload {

    private final ResourceLocation particleId;
    private final List<Vec3> positions;
    private final List<Vec3> velocities;

    public SpawnParticlePayload(ParticleOptions particle, List<Vec3> positions, List<Vec3> velocities) {
        this.particleId = ForgeRegistries.PARTICLE_TYPES.getKey(particle.getType());
        this.positions = positions;
        this.velocities = velocities;
    }

    private SpawnParticlePayload(ResourceLocation particleId, List<Vec3> positions, List<Vec3> velocities) {
        this.particleId = particleId;
        this.positions = positions;
        this.velocities = velocities;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(particleId);
        buf.writeVarInt(positions.size());
        for (Vec3 pos : positions) {
            buf.writeDouble(pos.x);
            buf.writeDouble(pos.y);
            buf.writeDouble(pos.z);
        }
        for (Vec3 vel : velocities) {
            buf.writeDouble(vel.x);
            buf.writeDouble(vel.y);
            buf.writeDouble(vel.z);
        }
    }

    public static SpawnParticlePayload decode(FriendlyByteBuf buf) {
        ResourceLocation id = buf.readResourceLocation();
        int size = buf.readVarInt();
        List<Vec3> positions = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            positions.add(new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()));
        }
        List<Vec3> velocities = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            velocities.add(new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()));
        }
        return new SpawnParticlePayload(id, positions, velocities);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> SimpleParticleHelper.spawn(particleId, positions, velocities));
        ctx.get().setPacketHandled(true);
    }

    public void toNear(ServerLevel level, Vec3 pos) {
        NetworkHandler.INSTANCE.send(PacketDistributor.NEAR.with(() ->
                new PacketDistributor.TargetPoint(pos.x, pos.y, pos.z, 32, level.dimension())), this);
    }
}
