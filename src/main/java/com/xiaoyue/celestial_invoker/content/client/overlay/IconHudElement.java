package com.xiaoyue.celestial_invoker.content.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class IconHudElement extends HudElement {

    private final ResourceLocation texture;
    private final int texX;
    private final int texY;
    private final int texWidth;
    private final int texHeight;
    private int tint = 0xFFFFFFFF;

    public IconHudElement(String id, int x, int y, ResourceLocation texture, int texX, int texY, int texWidth, int texHeight) {
        super(id, x, y);
        this.texture = texture;
        this.texX = texX;
        this.texY = texY;
        this.texWidth = texWidth;
        this.texHeight = texHeight;
        this.width = texWidth;
        this.height = texHeight;
    }

    @Override
    public void render(GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        if (!visible || texture == null) return;
        int actualX = getActualX(screenWidth);
        int actualY = getActualY(screenHeight);
        RenderSystem.setShaderColor(((tint >> 16) & 0xFF) / 255f, ((tint >> 8) & 0xFF) / 255f,
                (tint & 0xFF) / 255f, ((tint >> 24) & 0xFF) / 255f);
        guiGraphics.blit(texture, actualX, actualY, texX, texY, texWidth, texHeight);
    }

    public void setTint(int tint) {
        this.tint = tint;
    }
}

