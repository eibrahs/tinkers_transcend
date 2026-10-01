package com.eibrahs.tinkerstranscend.recipe;

import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.ingredient.EntityIngredient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CarvingRecipeProvider extends RecipeProvider {
    public CarvingRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        EntityIngredient ingredient = EntityIngredient.of(EntityType.ZOMBIE);

        List<CarvingRecipe.OutputEntry> outputs = List.of(
                new CarvingRecipe.OutputEntry(
                        ItemOutput.fromStack(new ItemStack(Items.ROTTEN_FLESH, 2)),
                        0.6f,
                        0.5f
                ),
                new CarvingRecipe.OutputEntry(
                        ItemOutput.fromStack(new ItemStack(Items.IRON_INGOT, 1)),
                        0.3f,
                        0.5f
                ),
                new CarvingRecipe.OutputEntry(
                        ItemOutput.fromStack(new ItemStack(Items.DIAMOND, 1)),
                        0.1f,
                        0.5f
                )
        );
        CarvingRecipe recipe = new CarvingRecipe(
                null, // 或者一个占位符 ResourceLocation
                ingredient,
                outputs,
                3,
                true
        );
        recipeOutput.accept(
                ResourceLocation.fromNamespaceAndPath("tinkers_transcend", "carving/test"),
                recipe,
                null // 或者一个 AdvancementHolder，用于解锁配方
        );
    }
}