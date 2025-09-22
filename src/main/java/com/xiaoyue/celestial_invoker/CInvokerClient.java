package com.xiaoyue.celestial_invoker;

import com.xiaoyue.celestial_invoker.content.client.ItemDecorationHandler;
import com.xiaoyue.celestial_invoker.invoker.provider.EventPoster.ArmorModelSet;
import com.xiaoyue.celestial_invoker.simple.SimpleInvoker;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterItemDecorationsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static com.xiaoyue.celestial_invoker.CelestialInvoker.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CInvokerClient {

    @SubscribeEvent
    public static void initGuiLayer(RegisterGuiOverlaysEvent event) {

    }

    @SubscribeEvent
    public static void initItemDecor(RegisterItemDecorationsEvent event) {
        ForgeRegistries.ITEMS.getValues().forEach(item -> event.register(item, new ItemDecorationHandler()));
    }

    @SubscribeEvent
    public static void initArmorModel(EntityRenderersEvent.RegisterLayerDefinitions event) {
        BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> cons = event::registerLayerDefinition;
        SimpleInvoker.invokeAllMethod(ArmorModelSet.class, cons);
    }
}
