package com.xiaoyue.celestial_invoker.content.client.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.xiaoyue.celestial_invoker.content.client.ColorGradient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2f;

import java.util.function.Supplier;

public class BarHudElement extends HudElement {

    private float currentValue = 1.0f;
    private float maxValue = 1.0f;
    private Supplier<Float> valueSupplier;
    private Supplier<Float> maxValueSupplier;
    private ResourceLocation backgroundTexture;
    private ResourceLocation fillTexture;
    private ResourceLocation overlayTexture;
    private Vector2f bgTexPos = new Vector2f(0, 0);
    private Vector2f bgTexSize = new Vector2f(256, 256);
    private Vector2f fillTexPos = new Vector2f(0, 0);
    private Vector2f fillTexSize = new Vector2f(256, 256);
    private Vector2f overlayTexPos = new Vector2f(0, 0);
    private Vector2f overlayTexSize = new Vector2f(256, 256);
    private int backgroundColor = 0x80000000;
    private int fillColor = 0xFF00FF00;
    private int borderColor = 0xFFFFFFFF;
    private boolean vertical = false;
    private boolean reverseFill = false;
    private boolean showText = false;
    private boolean showBorder = true;
    private boolean smoothAnimation = false;
    private float animationSpeed = 0.1f;
    private int borderThickness = 1;
    private int borderRadius = 3;
    private String textFormat = "%.0f/%.0f";
    private int textColor = 0xFFFFFF;
    private boolean textShadow = true;
    private float displayedValue = 1.0f;
    private float targetValue = 1.0f;
    private boolean isAnimating = false;
    private ColorGradient fillGradient;
    private boolean useGradient = false;
    private TextureFillMode fillMode = TextureFillMode.STRETCH;

    public enum TextureFillMode {
        STRETCH,
        REPEAT,
        SCALE_TO_FIT
    }

    public BarHudElement(String id, int x, int y, int width, int height) {
        super(id, x, y);
        this.width = width;
        this.height = height;
    }

    @Override
    public void render(GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        if (!visible) return;
        int actualX = getActualX(screenWidth);
        int actualY = getActualY(screenHeight);
        if (smoothAnimation && isAnimating) {
            updateAnimation();
        }
        float renderValue = smoothAnimation ? displayedValue : currentValue;
        float percent = Math.max(0, Math.min(1, renderValue / maxValue));
        renderBackground(guiGraphics, actualX, actualY);
        renderFill(guiGraphics, actualX, actualY, percent);
        if (showBorder) {
            renderBorder(guiGraphics, actualX, actualY);
        }
        if (overlayTexture != null) {
            renderOverlay(guiGraphics, actualX, actualY);
        }
        if (showText) {
            renderText(guiGraphics, actualX, actualY, percent);
        }
    }

    @Override
    public void tick() {
        if (valueSupplier != null) {
            targetValue = valueSupplier.get();
            if (!smoothAnimation) {
                currentValue = targetValue;
            } else {
                isAnimating = Math.abs(currentValue - targetValue) > 0.01f;
            }
        }
        if (maxValueSupplier != null) {
            maxValue = maxValueSupplier.get();
        }
        if (!smoothAnimation && valueSupplier != null) {
            currentValue = targetValue;
        }
    }

    private void updateAnimation() {
        if (Math.abs(displayedValue - targetValue) < 0.01f) {
            displayedValue = targetValue;
            isAnimating = false;
            return;
        }
        float difference = targetValue - displayedValue;
        displayedValue += difference * animationSpeed;
    }

    private void renderBackground(GuiGraphics guiGraphics, int x, int y) {
        if (backgroundTexture != null) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            switch (fillMode) {
                case STRETCH:
                    guiGraphics.blit(backgroundTexture, x, y,
                            bgTexPos.x(), bgTexPos.y(), width, height,
                            (int)bgTexSize.x(), (int)bgTexSize.y());
                    break;
                case REPEAT:
                    renderRepeatingTexture(guiGraphics, backgroundTexture,
                            x, y, width, height, bgTexPos, bgTexSize);
                    break;
                case SCALE_TO_FIT:
                    renderScaledTexture(guiGraphics, backgroundTexture,
                            x, y, width, height, bgTexPos, bgTexSize);
                    break;
            }
        } else {
            if (borderRadius > 0) {
                drawRoundedRect(guiGraphics, x, y, width, height, borderRadius, backgroundColor);
            } else {
                guiGraphics.fill(x, y, x + width, y + height, backgroundColor);
            }
        }
    }

    private void renderFill(GuiGraphics guiGraphics, int x, int y, float percent) {
        if (percent <= 0) return;
        int fillWidth, fillHeight;
        int fillX = x, fillY = y;
        if (vertical) {
            fillHeight = (int)(height * percent);
            if (reverseFill) {
                fillY = y + height - fillHeight;
            }
            fillWidth = width;
        } else {
            fillWidth = (int)(width * percent);
            if (reverseFill) {
                fillX = x + width - fillWidth;
            }
            fillHeight = height;
        }
        int borderPadding = showBorder ? borderThickness : 0;
        fillX += borderPadding;
        fillY += borderPadding;
        fillWidth -= borderPadding * 2;
        fillHeight -= borderPadding * 2;
        if (fillWidth <= 0 || fillHeight <= 0) return;
        if (fillTexture != null) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            switch (fillMode) {
                case STRETCH:
                    guiGraphics.blit(fillTexture, fillX, fillY,
                            fillTexPos.x(), fillTexPos.y(), fillWidth, fillHeight,
                            (int)fillTexSize.x(), (int)fillTexSize.y());
                    break;
                case REPEAT:
                    renderRepeatingTexture(guiGraphics, fillTexture,
                            fillX, fillY, fillWidth, fillHeight, fillTexPos, fillTexSize);
                    break;
                case SCALE_TO_FIT:
                    renderScaledTexture(guiGraphics, fillTexture,
                            fillX, fillY, fillWidth, fillHeight, fillTexPos, fillTexSize);
                    break;
            }
        } else {
            int color = fillColor;
            if (useGradient && fillGradient != null) {
                color = fillGradient.getColorAt(percent);
            }
            if (borderRadius > 0) {
                drawRoundedRect(guiGraphics, fillX, fillY, fillWidth, fillHeight,
                        Math.max(0, borderRadius - borderPadding), color);
            } else {
                guiGraphics.fill(fillX, fillY, fillX + fillWidth, fillY + fillHeight, color);
            }
        }
    }

    private void renderBorder(GuiGraphics guiGraphics, int x, int y) {
        if (borderThickness <= 0) return;
        guiGraphics.fill(x, y, x + width, y + borderThickness, borderColor);
        guiGraphics.fill(x, y + height - borderThickness, x + width, y + height, borderColor);
        guiGraphics.fill(x, y, x + borderThickness, y + height, borderColor);
        guiGraphics.fill(x + width - borderThickness, y, x + width, y + height, borderColor);
        if (borderRadius > 0) {
            int cornerSize = Math.min(borderRadius, borderThickness * 2);
            guiGraphics.fill(x, y, x + cornerSize, y + borderThickness, borderColor);
            guiGraphics.fill(x, y, x + borderThickness, y + cornerSize, borderColor);
            guiGraphics.fill(x + width - cornerSize, y, x + width, y + borderThickness, borderColor);
            guiGraphics.fill(x + width - borderThickness, y, x + width, y + cornerSize, borderColor);
            guiGraphics.fill(x, y + height - borderThickness, x + cornerSize, y + height, borderColor);
            guiGraphics.fill(x, y + height - cornerSize, x + borderThickness, y + height, borderColor);
            guiGraphics.fill(x + width - cornerSize, y + height - borderThickness, x + width, y + height, borderColor);
            guiGraphics.fill(x + width - borderThickness, y + height - cornerSize, x + width, y + height, borderColor);
        }
    }

    private void renderOverlay(GuiGraphics guiGraphics, int x, int y) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.blit(overlayTexture, x, y,
                overlayTexPos.x(), overlayTexPos.y(), width, height,
                (int)overlayTexSize.x(), (int)overlayTexSize.y());
    }

    private void renderText(GuiGraphics guiGraphics, int x, int y, float percent) {
        String text = String.format(textFormat, currentValue, maxValue);
        int textWidth = Minecraft.getInstance().font.width(text);
        int textHeight = Minecraft.getInstance().font.lineHeight;
        int textX = x + (width - textWidth) / 2;
        int textY = y + (height - textHeight) / 2;
        if (textShadow) {
            guiGraphics.drawString(Minecraft.getInstance().font, text, textX, textY, textColor);
        } else {
            guiGraphics.drawString(Minecraft.getInstance().font, text, textX, textY, textColor, false);
        }
    }

    private void renderRepeatingTexture(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, int width, int height, Vector2f texPos, Vector2f texSize) {
        int texWidth = (int)texSize.x();
        int texHeight = (int)texSize.y();
        for (int i = 0; i < width; i += texWidth) {
            for (int j = 0; j < height; j += texHeight) {
                int drawWidth = Math.min(texWidth, width - i);
                int drawHeight = Math.min(texHeight, height - j);
                guiGraphics.blit(texture, x + i, y + j,
                        texPos.x(), texPos.y(), drawWidth, drawHeight,
                        texWidth, texHeight);
            }
        }
    }

    private void renderScaledTexture(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, int width, int height, Vector2f texPos, Vector2f texSize) {
        float scaleX = width / texSize.x();
        float scaleY = height / texSize.y();
        float scale = Math.min(scaleX, scaleY);
        int scaledWidth = (int)(texSize.x() * scale);
        int scaledHeight = (int)(texSize.y() * scale);
        int offsetX = (width - scaledWidth) / 2;
        int offsetY = (height - scaledHeight) / 2;
        guiGraphics.blit(texture, x + offsetX, y + offsetY,
                texPos.x(), texPos.y(), scaledWidth, scaledHeight,
                (int)texSize.x(), (int)texSize.y());
    }

    private void drawRoundedRect(GuiGraphics guiGraphics, int x, int y, int width, int height, int radius, int color) {
        guiGraphics.fill(x + radius, y, x + width - radius, y + radius, color);
        guiGraphics.fill(x, y + radius, x + width, y + height - radius, color);
        guiGraphics.fill(x + radius, y + height - radius, x + width - radius, y + height, color);
        guiGraphics.fill(x, y + radius, x + radius, y, color);
        guiGraphics.fill(x + width - radius, y, x + width, y + radius, color);
        guiGraphics.fill(x, y + height - radius, x + radius, y + height, color);
        guiGraphics.fill(x + width - radius, y + height, x + width, y + height - radius, color);
    }

    public void setValue(float current, float max) {
        this.currentValue = Math.max(0, current);
        this.maxValue = Math.max(1, max);
        this.targetValue = currentValue;
        this.displayedValue = currentValue;
    }

    public void setValueSupplier(Supplier<Float> valueSupplier, Supplier<Float> maxValueSupplier) {
        this.valueSupplier = valueSupplier;
        this.maxValueSupplier = maxValueSupplier;
    }

    public void setBackgroundTexture(ResourceLocation texture) {
        this.backgroundTexture = texture;
    }

    public void setBackgroundTexture(ResourceLocation texture, int texX, int texY, int texWidth, int texHeight) {
        this.backgroundTexture = texture;
        this.bgTexPos = new Vector2f(texX, texY);
        this.bgTexSize = new Vector2f(texWidth, texHeight);
    }

    public void setFillTexture(ResourceLocation texture) {
        this.fillTexture = texture;
    }

    public void setFillTexture(ResourceLocation texture, int texX, int texY, int texWidth, int texHeight) {
        this.fillTexture = texture;
        this.fillTexPos = new Vector2f(texX, texY);
        this.fillTexSize = new Vector2f(texWidth, texHeight);
    }

    public void setOverlayTexture(ResourceLocation texture) {
        this.overlayTexture = texture;
    }

    public void setOverlayTexture(ResourceLocation texture, int texX, int texY, int texWidth, int texHeight) {
        this.overlayTexture = texture;
        this.overlayTexPos = new Vector2f(texX, texY);
        this.overlayTexSize = new Vector2f(texWidth, texHeight);
    }

    public void setFillMode(TextureFillMode fillMode) {
        this.fillMode = fillMode;
    }

    public void setColors(int backgroundColor, int fillColor, int borderColor) {
        this.backgroundColor = backgroundColor;
        this.fillColor = fillColor;
        this.borderColor = borderColor;
    }

    public void setFillGradient(ColorGradient gradient) {
        this.fillGradient = gradient;
        this.useGradient = true;
    }

    public void setVertical(boolean vertical) {
        this.vertical = vertical;
    }

    public void setReverseFill(boolean reverseFill) {
        this.reverseFill = reverseFill;
    }

    public void setShowText(boolean showText) {
        this.showText = showText;
    }

    public void setTextFormat(String format) {
        this.textFormat = format;
    }

    public void setTextStyle(int color, boolean shadow) {
        this.textColor = color;
        this.textShadow = shadow;
    }

    public void setBorder(int thickness, int radius, int color) {
        this.borderThickness = thickness;
        this.borderRadius = radius;
        this.borderColor = color;
        this.showBorder = thickness > 0;
    }

    public void setSmoothAnimation(boolean enabled, float speed) {
        this.smoothAnimation = enabled;
        this.animationSpeed = Math.max(0.01f, Math.min(1.0f, speed));
        if (!enabled) {
            this.displayedValue = this.currentValue;
        }
    }

    public float getCurrentValue() {
        return currentValue;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public float getPercentage() {
        return currentValue / maxValue;
    }
}

