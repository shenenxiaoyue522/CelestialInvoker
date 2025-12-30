package com.xiaoyue.celestial_invoker.content.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.xiaoyue.celestial_invoker.content.ancillary.ItemCooldownTracker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IItemDecorator;

@OnlyIn(value = Dist.CLIENT)
public class ItemDecorationHandler implements IItemDecorator {

    @Override
    public boolean render(GuiGraphics graphics, Font font, ItemStack stack, int x, int y) {
        if (!stack.isEmpty()) {
            float f = ItemCooldownTracker.getCooldownPercent(stack);
            if (f > 0.0f) {
                int i1 = y + Mth.floor(16.0F * (1.0F - f));
                int j1 = i1 + Mth.ceil(16.0F * f);
                graphics.fill(RenderType.guiOverlay(), x, i1, x + 16, j1, ItemCooldownTracker.getCooldownColor(stack));
            }
        }
        if (stack.getItem() instanceof Factory factory) {
            factory.addCustomDecoration(graphics, font, stack, x, y);
        }
        return false;
    }

    public static float getPercent(float current, float max) {
        return current / max;
    }

    public static void drawProgressBar(GuiGraphics guiGraphics, int x, int y, float progress, int barColor, int backgroundColor, BarDirection direction) {
        progress = Mth.clamp(progress, 0f, 1f);
        int BAR_WIDTH;
        int BAR_HEIGHT;
        if (direction == BarDirection.LEFT_TO_RIGHT) {
            BAR_WIDTH = 13;
            BAR_HEIGHT = 1;
        } else {
            BAR_WIDTH = 1;
            BAR_HEIGHT = 13;
        }
        int progressWidth;
        int progressHeight;
        if (direction == BarDirection.LEFT_TO_RIGHT) {
            progressWidth = (int) (BAR_WIDTH * progress);
            drawBarSegment(guiGraphics, x, y, progressWidth, BAR_HEIGHT, barColor, backgroundColor);
        } else if (direction == BarDirection.BOTTOM_TO_TOP) {
            progressHeight = (int) (BAR_HEIGHT * progress);
            int startY = y + BAR_HEIGHT - progressHeight;
            drawBarSegment(guiGraphics, x, startY, BAR_WIDTH, progressHeight, barColor, backgroundColor);
        }
    }

    private static void drawBarSegment(GuiGraphics guiGraphics, int x, int y, int width, int height, int color, int backgroundColor) {
        if (width <= 0 || height <= 0) return;
        float a = (float)(color >> 24 & 255) / 255.0F;
        float r = (float)(color >> 16 & 255) / 255.0F;
        float g = (float)(color >> 8 & 255) / 255.0F;
        float b = (float)(color & 255) / 255.0F;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(r, g, b, a);
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(0, 0, 200);
        guiGraphics.fill(x, y, x + width, y + height, backgroundColor);
        guiGraphics.fill(x, y, x + width, y + height, color);
        pose.popPose();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }

    public static void drawDurabilityLikeBar(GuiGraphics guiGraphics, int x, int y, float progress) {
        int color = calculateDurabilityColor(progress);
        drawProgressBar(guiGraphics, x, y, progress, color, 0xff000000, BarDirection.LEFT_TO_RIGHT);
    }

    public static int calculateDurabilityColor(float progress) {
        if (progress > 0.5f) {
            float ratio = (progress - 0.5f) * 2.0f;
            int r = (int)(255 * (1 - ratio));
            int g = 255;
            return 0xFF000000 | (r << 16) | (g << 8);
        } else {
            float ratio = progress * 2.0f;
            int r = 255;
            int g = (int)(255 * ratio);
            return 0xFF000000 | (r << 16) | (g << 8);
        }
    }

    public enum BarDirection {
        LEFT_TO_RIGHT,
        BOTTOM_TO_TOP
    }

    public interface Factory {
        void addCustomDecoration(GuiGraphics graphics, Font font, ItemStack stack, int x, int y);
    }
}
