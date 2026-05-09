package com.xiaoyue.celestial_invoker;

import com.mojang.logging.LogUtils;
import com.tterrag.registrate.Registrate;
import com.xiaoyue.celestial_invoker.content.common.registrar.RegistrateExtra;
import com.xiaoyue.celestial_invoker.content.generic.shared.ClickEmptyPayload;
import com.xiaoyue.celestial_invoker.invoker.tooltip.TooltipLoader;
import com.xiaoyue.celestial_invoker.register.CIEntities;
import com.xiaoyue.celestial_invoker.register.CIObjects;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

import static com.xiaoyue.celestial_invoker.CelestialInvoker.MODID;

@Mod(MODID)
@EventBusSubscriber(modid = MODID)
public class CelestialInvoker {

    public static final String MODID = "celestial_invoker";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Registrate REGISTRATE = Registrate.create(MODID);
    public static final RegistrateExtra<Registrate> EXTRA = new RegistrateExtra<>(REGISTRATE);

    public CelestialInvoker() {
        CIObjects.register();
        CIEntities.register();
        TooltipLoader.generator(MODID, REGISTRATE);
    }

    @SubscribeEvent
    public static void registerPayload(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(ClickEmptyPayload.ID, ClickEmptyPayload.CODEC, ClickEmptyPayload::handlePacket);
    }

    public static ResourceLocation loc(String s) {
        return ResourceLocation.fromNamespaceAndPath(MODID, s);
    }
}
