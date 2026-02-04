package com.xiaoyue.celestial_invoker.content.client;

public class ColorGradient {

    private final int[] colors;
    private final float[] positions;

    public ColorGradient(int[] colors, float[] positions) {
        this.colors = colors;
        this.positions = positions;
    }

    public int getColorAt(float position) {
        if (colors.length == 1) return colors[0];
        for (int i = 0; i < positions.length - 1; i++) {
            if (position >= positions[i] && position <= positions[i + 1]) {
                float t = (position - positions[i]) / (positions[i + 1] - positions[i]);
                return interpolateColor(colors[i], colors[i + 1], t);
            }
        }
        return colors[colors.length - 1];
    }

    private int interpolateColor(int color1, int color2, float t) {
        int a1 = (color1 >> 24) & 0xFF;
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;
        int a2 = (color2 >> 24) & 0xFF;
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;
        int a = (int)(a1 + (a2 - a1) * t);
        int r = (int)(r1 + (r2 - r1) * t);
        int g = (int)(g1 + (g2 - g1) * t);
        int b = (int)(b1 + (b2 - b1) * t);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}