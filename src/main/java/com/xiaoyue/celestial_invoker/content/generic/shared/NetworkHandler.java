package com.xiaoyue.celestial_invoker.content.generic.shared;

import com.xiaoyue.celestial_invoker.CelestialInvoker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class NetworkHandler {

    public static final String VERSION = "1";
    public static int ID = 0;

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(CelestialInvoker.loc("main"),
            () -> VERSION, VERSION::equals, VERSION::equals);

    public static void register() {
        registerMSG(ClickEmptyPayload.class, ClickEmptyPayload::encode, ClickEmptyPayload::decode, ClickEmptyPayload::handle);
    }

    public static <MSG> void registerMSG(Class<MSG> msg, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> ctx) {
        INSTANCE.registerMessage(ID++, msg, encoder, decoder, ctx);
    }
}
