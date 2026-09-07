package com.xiaoyue.celestial_invoker.content.client.helper;

import com.xiaoyue.celestial_invoker.content.common.Bindings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientHelper {

    public static boolean hasAltDown() {
        return Bindings.unsafeRunForDist(() -> Screen::hasAltDown, () -> () -> false);
    }

    public static boolean hasShiftDown() {
        return Bindings.unsafeRunForDist(() -> Screen::hasShiftDown, () -> () -> false);
    }

    public static boolean hasControlDown() {
        return Bindings.unsafeRunForDist(() -> Screen::hasControlDown, () -> () -> false);
    }

    public static Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    public static Player getPlayer() {
        return Bindings.unsafeRunForDist(() -> ClientHelper::getClientPlayer, () -> () -> null);
    }
}
