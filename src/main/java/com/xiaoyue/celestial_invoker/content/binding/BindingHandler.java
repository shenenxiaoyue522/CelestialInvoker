package com.xiaoyue.celestial_invoker.content.binding;

import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.DataIngredient;
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
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;
import net.minecraftforge.forgespi.language.ModFileScanData;

import java.util.function.BiFunction;

public class BindingHandler {

    public static Holder.Reference<DamageType> getDamageSource(Level level, ResourceKey<DamageType> key) {
        return level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(key);
    }

    public static void runForceLoad(String modid) {
        for (ModFileScanData.AnnotationData data : SimpleInvoker.getModAnno(modid, ForceLoadClass.class)) {
            try {
                Class.forName(data.clazz().getClassName());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
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
