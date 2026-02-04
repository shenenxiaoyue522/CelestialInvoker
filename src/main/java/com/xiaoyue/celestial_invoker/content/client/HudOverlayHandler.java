package com.xiaoyue.celestial_invoker.content.client;

import com.xiaoyue.celestial_invoker.content.client.overlay.HudElement;
import com.xiaoyue.celestial_invoker.content.client.overlay.HudManager;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;

public class HudOverlayHandler implements LayeredDraw.Layer {

    @Override
    public void render(GuiGraphics graphics, DeltaTracker tracker) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui) return;
        HudManager manager = HudManager.INSTANCE;
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        for (HudElement element : manager.getAllElements()) {
            if (element.isVisible()) {
                element.render(graphics, tracker, screenWidth, screenHeight);
            }
        }
    }
}
