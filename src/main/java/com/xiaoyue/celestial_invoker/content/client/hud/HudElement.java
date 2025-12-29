package com.xiaoyue.celestial_invoker.content.client.hud;

import net.minecraft.client.gui.GuiGraphics;

public abstract class HudElement {

    protected final String id;
    protected int x, y;
    protected int width, height;
    protected Alignment horizontalAlign = Alignment.LEFT;
    protected Alignment verticalAlign = Alignment.TOP;
    protected boolean visible = true;
    protected int zIndex = 0;

    public HudElement(String id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public abstract void render(GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight);

    public void tick() {
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setAlignment(Alignment horizontal, Alignment vertical) {
        this.horizontalAlign = horizontal;
        this.verticalAlign = vertical;
    }

    public int getActualX(int screenWidth) {
        return switch (horizontalAlign) {
            case CENTER -> (screenWidth - width) / 2 + x;
            case RIGHT -> screenWidth - width - x;
            default -> x;
        };
    }

    public int getActualY(int screenHeight) {
        return switch (verticalAlign) {
            case CENTER -> (screenHeight - height) / 2 + y;
            case BOTTOM -> screenHeight - height - y;
            default -> y;
        };
    }

    public String getId() {
        return id;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getZIndex() {
        return zIndex;
    }

    public void setZIndex(int zIndex) {
        this.zIndex = zIndex;
    }

    public enum Alignment {
        LEFT, CENTER, RIGHT, TOP, BOTTOM
    }
}

