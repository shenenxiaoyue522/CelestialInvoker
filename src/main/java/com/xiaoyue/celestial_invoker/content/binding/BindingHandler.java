package com.xiaoyue.celestial_invoker.content.binding;

import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.DataIngredient;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;

import java.util.function.BiFunction;

public class BindingHandler {

    public static TagKey<Item> getTagFromArmorSlot(ArmorItem.Type type) {
        return switch (type) {
            case HELMET -> Tags.Items.ARMORS_HELMETS;
            case CHESTPLATE -> Tags.Items.ARMORS_CHESTPLATES;
            case LEGGINGS -> Tags.Items.ARMORS_LEGGINGS;
            case BOOTS -> Tags.Items.ARMORS_BOOTS;
        };
    }

    public static void metalCraft(RegistrateRecipeProvider pvd, String path, MetalItemEntry<?,?> entry) {
        unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, entry.block())::unlockedBy, entry.ingot().get())
                .pattern("XXX").pattern("XXX").pattern("XXX")
                .define('X', entry.ingot())
                .save(pvd, prefix(entry.block().getId(), path + "block_from_ingot/"));
        unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, entry.ingot())::unlockedBy, entry.nugget().get())
                .pattern("XXX").pattern("XXX").pattern("XXX").define('X', entry.nugget())
                .save(pvd, prefix(entry.block().getId(), path + "ingot_from_nugget/"));
        unlock(pvd, ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, entry.ingot(), 9)::unlockedBy, entry.block().asItem())
                .requires(entry.block()).save(pvd, prefix(entry.ingot().getId(), path + "ingot_from_block/"));
        unlock(pvd, ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, entry.nugget(), 9)::unlockedBy, entry.ingot().get())
                .requires(entry.ingot()).save(pvd, prefix(entry.ingot().getId(), path + "nugget_from_ingot/"));
    }

    public static void storgeCraft(RegistrateRecipeProvider pvd, ResourceLocation path, Item input, ItemLike output) {
        unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, output)::unlockedBy, input)
                .pattern("XXX").pattern("XXX").pattern("XXX")
                .define('X', input)
                .save(pvd, path);
    }

    public static <T> T unlock(RegistrateRecipeProvider pvd, BiFunction<String, InventoryChangeTrigger.TriggerInstance, T> func, Item item) {
        return func.apply("has_" + pvd.safeName(item), DataIngredient.items(item).getCritereon(pvd));
    }

    public static ResourceLocation prefix(ResourceLocation res, String prefix) {
        return new ResourceLocation(res.getNamespace(), prefix + res.getPath());
    }
}
