package com.xiaoyue.celestial_invoker.content.generic.shared;

import com.xiaoyue.celestial_invoker.CelestialInvoker;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {

    public static final String VERSION = "1";
    public static int ID = 0;

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(CelestialInvoker.loc("main"),
            () -> VERSION, VERSION::equals, VERSION::equals);

    public static void register() {
        INSTANCE.registerMessage(ID++, ClickEmptyPayload.class, ClickEmptyPayload::encode, ClickEmptyPayload::decode, ClickEmptyPayload::handle);
    }
}
