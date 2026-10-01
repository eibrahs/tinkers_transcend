package com.eibrahs.tinkerstranscend.recipe;

import com.eibrahs.tinkerstranscend.TinkersTranscend;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;
import slimeknights.mantle.recipe.helper.LoadableRecipeSerializer;

import java.util.function.Supplier;

@Mod(TinkersTranscend.MODID)
public class TinkersTranscendRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, TinkersTranscend.MODID);
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, TinkersTranscend.MODID);
    public static final Supplier<RecipeSerializer<CarvingRecipe>> CARVING_SERIALIZER =
            TinkersTranscendRecipes.RECIPE_SERIALIZERS.register("carving", () -> LoadableRecipeSerializer.of(CarvingRecipe.LOADER));
    public static final Supplier<RecipeType<CarvingRecipe>> CARVING_TYPE =
            TinkersTranscendRecipes.TYPES.register("carving", () -> new RecipeType<CarvingRecipe>() {});

}
