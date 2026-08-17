package com.mqsmycmz.forging_and_crafting.compat;

import com.mqsmycmz.forging_and_crafting.ForgingAndCrafting;
import com.mqsmycmz.forging_and_crafting.data.GrindingTableDataLoader;
import com.mqsmycmz.forging_and_crafting.recipe.RockCrusherRecipe;
import com.mqsmycmz.forging_and_crafting.world.screen.RockCrusherScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class JEIForgingAndCraftingPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(ForgingAndCrafting.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new GrindingTableCategory(guiHelper));
        registration.addRecipeCategories(new RockCrusherCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        List<RockCrusherRecipe> rockCrusherRecipes = recipeManager.getAllRecipesFor(RockCrusherRecipe.Type.INSTANCE);
        registration.addRecipes(RockCrusherCategory.ROCK_CRUSHER_TYPE, rockCrusherRecipes);

        List<GrindingTableDataLoader.OreProcessingEntry> grindingTableRecipes = new ArrayList<>(
                GrindingTableDataLoader.getInstance().getAllEntries());
        registration.addRecipes(GrindingTableCategory.GRINDING_TABLE_TYPE, grindingTableRecipes);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(RockCrusherScreen.class, 77, 6, 44, 12,
                RockCrusherCategory.ROCK_CRUSHER_TYPE);
    }
}