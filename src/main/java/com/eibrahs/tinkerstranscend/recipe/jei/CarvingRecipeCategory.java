package com.eibrahs.tinkerstranscend.recipe.jei;

import com.eibrahs.tinkerstranscend.recipe.CarvingRecipe;
import com.eibrahs.tinkerstranscend.recipe.TinkersTranscendRecipes;
import com.eibrahs.tinkerstranscend.tinker.TinkersTranscendTools;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;
import slimeknights.mantle.plugin.jei.MantleJEIConstants;
import slimeknights.mantle.plugin.jei.entity.EntityIngredientRenderer;
import slimeknights.mantle.recipe.ingredient.EntityIngredient;

import java.util.List;

public class CarvingRecipeCategory extends AbstractRecipeCategory<CarvingRecipe> {
    private final EntityIngredientRenderer entityRenderer = new EntityIngredientRenderer(32);
    public static final RecipeType<CarvingRecipe> RECIPE_TYPE =
            RecipeType.create("tinkers_transcend", "carving", CarvingRecipe.class);

    public CarvingRecipeCategory(IGuiHelper guiHelper) {
        super(
                RECIPE_TYPE,
                Component.translatable("gui.tinkers_transcend.carving"),
                guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, (TinkersTranscendTools.HUNTING_KNIFE.get()).getRenderTool()),
                150, // 宽度
                75   // 高度
        );
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CarvingRecipe recipe, IFocusGroup focuses) {
        EntityIngredient input = recipe.getIngredient();
        builder.addSlot(RecipeIngredientRole.INPUT, 3, 3)
                .setCustomRenderer(MantleJEIConstants.ENTITY_TYPE, this.entityRenderer)
                .addIngredients(MantleJEIConstants.ENTITY_TYPE, input.getDisplay());


        for (int i = 0; i < recipe.getOutputs().size(); i++) {
            var entry = recipe.getOutputs().get(i);
            String chanceText = I18n.get("gui.tinkers_transcend.carving.chance")+String.format("%.1f%%", entry.chance() * 100);
            String damageText = entry.damage()+I18n.get("gui.tinkers_transcend.carving.damage");
            IRecipeSlotBuilder slot = builder.addOutputSlot(90+i%3*18, 6 + i/3 * 18)
                    .addItemStack(entry.output().get())
                    .setStandardSlotBackground()
                    .addRichTooltipCallback((recipeSlotView, tooltip) ->tooltip.add(Component.literal(chanceText)));
            if (entry.damage()>0){
                slot.addRichTooltipCallback((recipeSlotView, tooltip) ->tooltip.add(Component.literal(damageText)));
            }
        }
    }
    @Override
    public void draw(CarvingRecipe recipe, IRecipeSlotsView recipeSlotsView,
                     GuiGraphics guiGraphics, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);

        var font = Minecraft.getInstance().font;
        String countText = I18n.get("gui.tinkers_transcend.carving.count")+recipe.getCount();
        guiGraphics.drawString(
                font, countText, 6, 60,
                0xFFFFFF, true
        );
        if (recipe.isRequiresWeakness()) {
            String weaknessText =I18n.get("gui.tinkers_transcend.carving.weakness");
            guiGraphics.drawString(
                    font, weaknessText, 6, 50,
                    0xde4949, true
            );
        }

    }
    public static void registerRecipes(IRecipeRegistration registration) {
        var level = Minecraft.getInstance().level;
        if (level == null) return;

        List<RecipeHolder<CarvingRecipe>> recipes = level.getRecipeManager()
                .getAllRecipesFor(TinkersTranscendRecipes.CARVING_TYPE.get());

        List<CarvingRecipe> recipeList = recipes.stream()
                .map(RecipeHolder::value)
                .toList();

        registration.addRecipes(RECIPE_TYPE, recipeList);
    }

    public static void registerRecipesCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(
                (TinkersTranscendTools.HUNTING_KNIFE.get()).getRenderTool(),
                RECIPE_TYPE
        );
    }
}
