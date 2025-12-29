package com.xiaoyue.celestial_invoker.content.client.overlay;

import java.util.*;

public class HudManager {

    public static final HudManager INSTANCE = new HudManager();
    private final Map<String, HudElement> elements = new LinkedHashMap<>();
    private final List<HudElement> renderOrder = new ArrayList<>();
    private boolean enabled = true;

    public void registerElement(HudElement element) {
        elements.put(element.getId(), element);
        updateRenderOrder();
    }

    public void unregisterElement(String id) {
        elements.remove(id);
        updateRenderOrder();
    }

    public HudElement getElement(String id) {
        return elements.get(id);
    }

    public Collection<HudElement> getAllElements() {
        return elements.values();
    }

    private void updateRenderOrder() {
        renderOrder.clear();
        renderOrder.addAll(elements.values());
        renderOrder.sort(Comparator.comparingInt(HudElement::getZIndex));
    }

    public void tick() {
        if (!enabled) return;
        for (HudElement element : elements.values()) {
            element.tick();
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

