package com.xiaoyue.celestial_invoker.content.network;

import com.xiaoyue.celestial_invoker.CelestialInvoker;
import com.xiaoyue.celestial_invoker.content.client.helper.SimpleParticleHelper;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

public record SpawnParticlePayload(ParticleType<?> particle, List<Vec3> positions, List<Vec3> velocities) implements CustomPacketPayload {
    public static final Type<SpawnParticlePayload> ID = new Type<>(CelestialInvoker.loc("spawn_particle"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static StreamCodec<FriendlyByteBuf, SpawnParticlePayload> CODEC = CustomPacketPayload.codec(SpawnParticlePayload::write, SpawnParticlePayload::read);

    public void write(FriendlyByteBuf buf) {
        ResourceLocation key = BuiltInRegistries.PARTICLE_TYPE.getKey(particle);
        buf.writeResourceLocation(key);
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

    public static SpawnParticlePayload read(FriendlyByteBuf buf) {
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
        return new SpawnParticlePayload(BuiltInRegistries.PARTICLE_TYPE.get(id), positions, velocities);
    }

    public static void handlePacket(SpawnParticlePayload packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> SimpleParticleHelper.spawn(packet));
    }
}
