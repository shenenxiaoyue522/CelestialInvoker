package com.xiaoyue.celestial_invoker.content.client.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;

@OnlyIn(Dist.CLIENT)
public class ClientHelper {

    public static boolean hasAltDown() {
        return DistExecutor.unsafeRunForDist(() -> Screen::hasAltDown, () -> () -> false);
    }

    public static boolean hasShiftDown() {
        return DistExecutor.unsafeRunForDist(() -> Screen::hasShiftDown, () -> () -> false);
    }

    public static boolean hasControlDown() {
        return DistExecutor.unsafeRunForDist(() -> Screen::hasControlDown, () -> () -> false);
    }

    public static Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    public static Player getPlayer() {
        return DistExecutor.unsafeRunForDist(() -> ClientHelper::getClientPlayer, () -> () -> null);
    }
}
