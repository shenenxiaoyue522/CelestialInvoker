package com.xiaoyue.celestial_invoker.content.client;

import com.xiaoyue.celestial_invoker.content.client.hud.HudElement;
import com.xiaoyue.celestial_invoker.content.client.hud.HudManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class HudOverlayHandler implements IGuiOverlay {

    @Override
    public void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int x, int y) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui) return;
        HudManager manager = HudManager.INSTANCE;
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        for (HudElement element : manager.getAllElements()) {
            if (element.isVisible()) {
                element.render(graphics, partialTick, screenWidth, screenHeight);
            }
        }
    }
}
