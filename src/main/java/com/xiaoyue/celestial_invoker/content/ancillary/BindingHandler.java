package com.xiaoyue.celestial_invoker.content.ancillary;

import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.DataIngredient;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.CommonHooks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;

public class BindingHandler {

    public static int getEnchantmentLv(ItemStack stack, ResourceKey<Enchantment> enchantment) {
        var resolved = CommonHooks.resolveLookup(Registries.ENCHANTMENT);
        if (resolved == null) {
            return 0;
        }
        return resolved.get(enchantment).map(stack::getEnchantmentLevel).orElse(0);
    }

    public static <C extends Container> boolean checkShapelessInputs(List<Ingredient> materials, C inv) {
        List<ItemStack> inputs = new ArrayList<>();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                inputs.add(stack);
            }
        }
        List<Ingredient> requiredIngredients = new ArrayList<>(materials);
        for (ItemStack input : inputs) {
            boolean matched = false;
            for (Iterator<Ingredient> iterator = requiredIngredients.iterator(); iterator.hasNext();) {
                Ingredient ingredient = iterator.next();
                if (ingredient.test(input)) {
                    iterator.remove();
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                return false;
            }
        }
        return requiredIngredients.isEmpty();
    }

    public static Holder.Reference<DamageType> getDamageSource(Level level, ResourceKey<DamageType> key) {
        return level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(key);
    }

    public static void storageCraft(RegistrateRecipeProvider pvd, ResourceLocation path, Item input, ItemLike output) {
        unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, output)::unlockedBy, input)
                .pattern("XXX").pattern("XXX").pattern("XXX").define('X', input)
                .save(pvd, path);
    }

    public static <T> T unlock(RegistrateRecipeProvider pvd, BiFunction<String, Criterion<?>, T> func, Item item) {
        return func.apply("has_" + pvd.safeName(item), DataIngredient.items(item).getCriterion(pvd));
    }

    public static ResourceLocation prefix(ResourceLocation res, String prefix) {
        return ResourceLocation.fromNamespaceAndPath(res.getNamespace(), prefix + res.getPath());
    }
}
