package com.xiaoyue.celestial_invoker.content.ancillary;

import com.xiaoyue.celestial_invoker.content.ancillary.helper.IRegistrateHelper;
import dev.xkmc.l2library.base.L2Registrate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

@SuppressWarnings("unused")
public class CelestialRegistrate extends L2Registrate implements IRegistrateHelper<CelestialRegistrate> {
    public CelestialRegistrate(String modid) {
        super(modid);
    }

    @Override
    public CelestialRegistrate owner() {
        return this;
    }

    public static TagKey<Item> forgeTag(String id) {
        return ItemTags.create(forgeLoc(id));
    }

    public static ResourceLocation forgeLoc(String path) {
        return new ResourceLocation("forge", path);
    }
}
