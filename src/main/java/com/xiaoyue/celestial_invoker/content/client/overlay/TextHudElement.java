package com.xiaoyue.celestial_invoker.content.client.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class TextHudElement extends HudElement {

    private String text;
    private int color = 0xFFFFFF;
    private boolean hasShadow = true;
    private Component component;

    public TextHudElement(String id, int x, int y, String text) {
        super(id, x, y);
        this.text = text;
        updateComponent();
    }

    private void updateComponent() {
        this.component = Component.literal(text);
    }

    @Override
    public void render(GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        if (!visible || component == null) return;
        int actualX = getActualX(screenWidth);
        int actualY = getActualY(screenHeight);
        if (hasShadow) {
            guiGraphics.drawString(Minecraft.getInstance().font, component, actualX, actualY, color);
        } else {
            guiGraphics.drawString(Minecraft.getInstance().font, component, actualX, actualY, color, false);
        }
        this.width = Minecraft.getInstance().font.width(component);
        this.height = Minecraft.getInstance().font.lineHeight;
    }

    public void setText(String text) {
        this.text = text;
        updateComponent();
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setHasShadow(boolean hasShadow) {
        this.hasShadow = hasShadow;
    }
}

