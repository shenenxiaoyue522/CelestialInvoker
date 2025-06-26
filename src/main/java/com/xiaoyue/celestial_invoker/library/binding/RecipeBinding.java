package com.xiaoyue.celestial_invoker.library.binding;

import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.DataIngredient;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.function.BiFunction;

public class RecipeBinding {

    public static void metalCraft(RegistrateRecipeProvider pvd, MetalItemEntry<?> entry) {
        unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, entry.block())::unlockedBy, entry.ingot().get())
                .pattern("XXX").pattern("XXX").pattern("XXX")
                .define('X', entry.ingot())
                .save(pvd, prefix(entry.block().getId(), "block_from_ingot/"));
        unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, entry.ingot())::unlockedBy, entry.nugget().get())
                .pattern("XXX").pattern("XXX").pattern("XXX").define('X', entry.nugget())
                .save(pvd, prefix(entry.block().getId(), "ingot_from_nugget/"));
        unlock(pvd, ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, entry.ingot(), 9)::unlockedBy, entry.block().asItem())
                .requires(entry.block()).save(pvd, prefix(entry.ingot().getId(), "ingot_from_block/"));
        unlock(pvd, ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, entry.nugget(), 9)::unlockedBy, entry.ingot().get())
                .requires(entry.ingot()).save(pvd, prefix(entry.ingot().getId(), "nugget_from_ingot/"));
    }

    public static <T> T unlock(RegistrateRecipeProvider pvd, BiFunction<String, InventoryChangeTrigger.TriggerInstance, T> func, Item item) {
        return func.apply("has_" + pvd.safeName(item), DataIngredient.items(item).getCritereon(pvd));
    }

    public static ResourceLocation prefix(ResourceLocation res, String prefix) {
        return new ResourceLocation(res.getNamespace(), prefix + res.getPath());
    }
}
