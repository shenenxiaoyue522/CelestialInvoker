package com.xiaoyue.celestial_invoker;

import com.mojang.logging.LogUtils;
import com.xiaoyue.celestial_invoker.content.ancillary.CelestialRegistrate;
import com.xiaoyue.celestial_invoker.content.network.ClickEmptyPacket;
import com.xiaoyue.celestial_invoker.content.network.OpenMenuPacket;
import com.xiaoyue.celestial_invoker.register.CIEntities;
import dev.xkmc.l2library.serial.config.PacketHandlerWithConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;
import org.slf4j.Logger;

@Mod(CelestialInvoker.MODID)
@Mod.EventBusSubscriber(modid = CelestialInvoker.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CelestialInvoker
{
    public static final String MODID = "celestial_invoker";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final CelestialRegistrate REGISTRATE = new CelestialRegistrate(MODID);
    public static final PacketHandlerWithConfig HANDLER = new PacketHandlerWithConfig(loc("main"), 2,
            e -> e.create(ClickEmptyPacket.class, NetworkDirection.PLAY_TO_SERVER),
            e -> e.create(OpenMenuPacket.class, NetworkDirection.PLAY_TO_SERVER));
    
    public CelestialInvoker() {
        CIEntities.register();
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {

    }

    public static ResourceLocation loc(String s) {
        return new ResourceLocation(MODID, s);
    }
}
