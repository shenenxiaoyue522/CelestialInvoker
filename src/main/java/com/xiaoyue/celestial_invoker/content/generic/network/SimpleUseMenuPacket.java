package com.xiaoyue.celestial_invoker.content.generic.network;

import com.xiaoyue.celestial_invoker.CelestialInvoker;
import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraftforge.network.NetworkEvent;

@SerialClass
public class SimpleUseMenuPacket extends SerialPacketBase {

    @SerialClass.SerialField
    public Component title;
    @SerialClass.SerialField
    public MenuConstructor sup;

    public SimpleUseMenuPacket(Component title, MenuConstructor sup) {
        this.title = title;
        this.sup = sup;
    }

    @Deprecated
    public SimpleUseMenuPacket() {
        this(Component.empty(), (id, inv, p) -> null);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        if (player != null) {
            player.openMenu(new SimpleMenuProvider(sup, title));
        }
    }

    public void toServer() {
        CelestialInvoker.HANDLER.toServer(this);
    }
}
