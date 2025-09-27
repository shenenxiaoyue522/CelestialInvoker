package com.xiaoyue.celestial_invoker.content.ancillary;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;

public class ItemModelHelper {

    public static final float[] BOW_PULL_VALS = {0.0F, 0.65f, 0.9f};
    public static final float[] CROSSBOW_PULL_VALS = {0.58f, 0.9f};

    public static void createBowModel(DataGenContext<?, ?> ctx, RegistrateItemModelProvider pvd) {
        ItemModelBuilder builder = pvd.withExistingParent(ctx.getName(), "minecraft:bow");
        builder.texture("layer0", "item/bow/" + ctx.getName() + "/bow");
        for(int i = 0; i < 3; ++i) {
            String name = ctx.getName() + "/bow_pulling_" + i;
            ItemModelBuilder ret = pvd.getBuilder("item/bow/" + name).parent(new ModelFile.UncheckedModelFile("minecraft:item/bow_pulling_" + i));
            ret.texture("layer0", "item/bow/" + name);
            ItemModelBuilder.OverrideBuilder override = builder.override();
            override.predicate(new ResourceLocation("pulling"), 1.0F);
            if (BOW_PULL_VALS[i] > 0.0F) {
                override.predicate(new ResourceLocation("pull"), BOW_PULL_VALS[i]);
            }
            override.model(new ModelFile.UncheckedModelFile(ctx.getId().getNamespace() + ":item/bow/" + name));
        }
    }

    public static void createCrossbowModel(DataGenContext<?, ?> ctx, RegistrateItemModelProvider pvd) {
        ItemModelBuilder builder = pvd.withExistingParent(ctx.getName(), "minecraft:crossbow");
        builder.texture("layer0", "item/crossbow/" + ctx.getName() + "/crossbow");
        ItemModelBuilder.OverrideBuilder override = builder.override();
        for(int i = 0; i < 2; ++i) {
            String name = ctx.getName() + "/crossbow_pulling_" + i;
            ItemModelBuilder ret = pvd.getBuilder("item/crossbow/" + name).parent(new ModelFile.UncheckedModelFile("minecraft:item/crossbow_pulling_" + i));
            ret.texture("layer0", "item/crossbow/" + name);
            override.predicate(new ResourceLocation("pulling"), 1.0F);
            if (CROSSBOW_PULL_VALS[i] > 0.0F) {
                override.predicate(new ResourceLocation("pull"), CROSSBOW_PULL_VALS[i]);
            }
            override.model(new ModelFile.UncheckedModelFile(ctx.getId().getNamespace() + ":item/crossbow/" + name));
        }
        override.predicate(new ResourceLocation("charged"), 1);
        override.predicate(new ResourceLocation("firework"), 1)
                .predicate(new ResourceLocation("charged"), 1);
    }

}
