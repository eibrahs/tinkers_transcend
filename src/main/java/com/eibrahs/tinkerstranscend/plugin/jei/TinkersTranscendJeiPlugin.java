package com.eibrahs.tinkerstranscend.plugin.jei;

import com.eibrahs.tinkerstranscend.TinkersTranscend;
import com.eibrahs.tinkerstranscend.client.screen.RoyalFoundryScreen;
import com.eibrahs.tinkerstranscend.recipe.jei.CarvingRecipeCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.plugin.jei.AlloyRecipeCategory;

import java.util.List;

@JeiPlugin
public class TinkersTranscendJeiPlugin implements IModPlugin {
    private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(TinkersTranscend.MODID,"jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }
    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new CarvingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new AlloyRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        CarvingRecipeCategory.registerRecipes(registration);
    }
    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        CarvingRecipeCategory.registerRecipesCatalysts(registration);
    }
    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(RoyalFoundryScreen.class, new IGuiContainerHandler<RoyalFoundryScreen>() {
            @Override
            public List<Rect2i> getGuiExtraAreas(RoyalFoundryScreen screen) {
                return List.of(new Rect2i(61, 16, 53, 106));
            }
        });
    }
}
