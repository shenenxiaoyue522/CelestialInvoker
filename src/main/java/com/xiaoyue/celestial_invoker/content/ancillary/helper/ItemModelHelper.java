package com.xiaoyue.celestial_invoker.content.ancillary.helper;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;

public class ItemModelHelper {

    public static final float[] BOW_PULL_VALS = {0.0F, 0.65f, 0.9f};
    public static final float[] CROSSBOW_PULL_VALS = {0.25f, 0.58f, 0.9f};

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
        String namespace = ctx.getId().getNamespace();
        String type = "item/crossbow/";
        ModelFile.UncheckedModelFile crossbowParent = new ModelFile.UncheckedModelFile("minecraft:item/crossbow");
        ItemModelBuilder builder = pvd.withExistingParent(ctx.getName(), "minecraft:crossbow");
        builder.texture("layer0", type + ctx.getName() + "/crossbow");
        for(int i = 0; i < 3; ++i) {
            String name = ctx.getName() + "/crossbow_pulling_" + i;
            ItemModelBuilder ret = pvd.getBuilder(type + name).parent(crossbowParent);
            ret.texture("layer0", type + name);
            ItemModelBuilder.OverrideBuilder override = builder.override();
            override.predicate(new ResourceLocation("pulling"), 1.0F);
            if (CROSSBOW_PULL_VALS[i] > 0.0F) {
                override.predicate(new ResourceLocation("pull"), CROSSBOW_PULL_VALS[i]);
            }
            override.model(new ModelFile.UncheckedModelFile(namespace + ":item/crossbow/" + name));
        }
        String texLoc = type + ctx.getName();
        pvd.getBuilder(texLoc + "/crossbow_charged").parent(crossbowParent)
                .texture("layer0", namespace + ":" + texLoc + "/crossbow_charged");
        builder.override().predicate(new ResourceLocation("charged"), 1.0f)
                .model(new ModelFile.UncheckedModelFile(namespace + ":" + texLoc + "/crossbow_charged"));
        pvd.getBuilder(texLoc + "/crossbow_firework").parent(crossbowParent)
                .texture("layer0", namespace + ":" + texLoc + "/crossbow_firework");
        builder.override().predicate(new ResourceLocation("firework"), 1.0f)
                .predicate(new ResourceLocation("charged"), 1.0f)
                .model(new ModelFile.UncheckedModelFile(namespace + ":" + texLoc + "/crossbow_firework"));
    }

    public static void createTridentModel(DataGenContext<?, ?> ctx, RegistrateItemModelProvider pvd) {
        String namespace = ctx.getId().getNamespace();
        String texture = "item/trident/" + ctx.getName();
        String path = "item/" + ctx.getName();
        pvd.getBuilder(ctx.getName() + "_using")
                .parent(new ModelFile.UncheckedModelFile("celestial_invoker:item/spear_using"))
                .texture("layer0", namespace + ":" + texture);
        pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("celestial_invoker:item/spear"))
                .texture("layer0", namespace + ":" + texture)
                .override()
                .predicate(new ResourceLocation("using"), 1f)
                .model(pvd.getExistingFile(new ResourceLocation(namespace, path + "_using")));
    }

}
