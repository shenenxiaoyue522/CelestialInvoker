package com.xiaoyue.celestial_invoker.content.ancillary;

import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.DataIngredient;
import com.xiaoyue.celestial_invoker.content.ancillary.entry.MetalItemEntry;
import com.xiaoyue.celestial_invoker.invoker.provider.ForceLoadClass;
import com.xiaoyue.celestial_invoker.simple.SimpleInvoker;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;
import net.minecraftforge.forgespi.language.ModFileScanData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;

public class BindingHandler {

    public static void setUnbreakable(ItemStack stack) {
        stack.getOrCreateTag().putBoolean("Unbreakable", true);
    }

    public static <C extends Container> boolean checkInputs(List<Ingredient> materials, C inv) {
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

    public static void postForceLoader(String modid, String type) {
        for (ModFileScanData.AnnotationData data : SimpleInvoker.getModAnno(modid, ForceLoadClass.class)) {
            String dataType = (String) data.annotationData().getOrDefault("type", "all");
            if (dataType.equals(type)) {
                try {
                    Class.forName(data.clazz().getClassName());
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static TagKey<Item> getArmorSlotTag(ArmorItem.Type type) {
        return switch (type) {
            case HELMET -> Tags.Items.ARMORS_HELMETS;
            case CHESTPLATE -> Tags.Items.ARMORS_CHESTPLATES;
            case LEGGINGS -> Tags.Items.ARMORS_LEGGINGS;
            case BOOTS -> Tags.Items.ARMORS_BOOTS;
        };
    }

    public static void metalCraft(RegistrateRecipeProvider pvd, String path, MetalItemEntry<?,?> entry) {
        storgeCraft(pvd, prefix(entry.block().getId(), path + "block_from_ingot/"), entry.ingot().get(), entry.block());
        storgeCraft(pvd, prefix(entry.ingot().getId(), path + "ingot_from_nugget/"), entry.nugget().get(), entry.ingot());
        unlock(pvd, ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, entry.ingot(), 9)::unlockedBy, entry.block().asItem())
                .requires(entry.block()).save(pvd, prefix(entry.ingot().getId(), path + "ingot_from_block/"));
        unlock(pvd, ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, entry.nugget(), 9)::unlockedBy, entry.ingot().get())
                .requires(entry.ingot()).save(pvd, prefix(entry.ingot().getId(), path + "nugget_from_ingot/"));
    }

    public static void storgeCraft(RegistrateRecipeProvider pvd, ResourceLocation path, Item input, ItemLike output) {
        unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, output)::unlockedBy, input)
                .pattern("XXX").pattern("XXX").pattern("XXX").define('X', input)
                .save(pvd, path);
    }

    public static <T> T unlock(RegistrateRecipeProvider pvd, BiFunction<String, InventoryChangeTrigger.TriggerInstance, T> func, Item item) {
        return func.apply("has_" + pvd.safeName(item), DataIngredient.items(item).getCritereon(pvd));
    }

    public static ResourceLocation prefix(ResourceLocation res, String prefix) {
        return new ResourceLocation(res.getNamespace(), prefix + res.getPath());
    }
}
