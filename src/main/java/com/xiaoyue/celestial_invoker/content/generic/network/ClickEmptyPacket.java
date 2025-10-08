package com.xiaoyue.celestial_invoker.content.generic.network;

import com.xiaoyue.celestial_invoker.CelestialInvoker;
import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Consumer;

@SerialClass
public class ClickEmptyPacket extends SerialPacketBase {

    @SerialClass.SerialField
    public Consumer<ServerPlayer> runnable;

    public ClickEmptyPacket(Consumer<ServerPlayer> runnable) {
        this.runnable = runnable;
    }

    @Deprecated
    public ClickEmptyPacket() {
        this(p -> {});
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        if (player != null) {
            runnable.accept(player);
        }
    }

    public void toServer() {
        CelestialInvoker.HANDLER.toServer(this);
    }
}
