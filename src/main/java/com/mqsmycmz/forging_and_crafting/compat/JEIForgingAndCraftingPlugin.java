// JEIForgingAndCraftingPlugin.java
package com.mqsmycmz.forging_and_crafting.compat;

import com.mqsmycmz.forging_and_crafting.ForgingAndCrafting;
import com.mqsmycmz.forging_and_crafting.data.OreProcessingDataLoader;
import com.mqsmycmz.forging_and_crafting.item.ForgingAndCraftingItems;
import com.mqsmycmz.forging_and_crafting.recipe.RockCrusherRecipe;
import com.mqsmycmz.forging_and_crafting.world.screen.RockCrusherScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
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
        // 注册碎石机分类
        registration.addRecipeCategories(new RockCrusherCategory(registration.getJeiHelpers().getGuiHelper()));

        // 注册带3D预览的矿石处理分类（现在使用GeckoLib动画）
        registration.addRecipeCategories(new OreProcessingCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        // 注册碎石机配方
        List<RockCrusherRecipe> rockCrusherRecipes = recipeManager.getAllRecipesFor(RockCrusherRecipe.Type.INSTANCE);
        registration.addRecipes(RockCrusherCategory.ROCK_CRUSHER_TYPE, rockCrusherRecipes);

        // 从 OreProcessingDataLoader 获取所有矿石处理配方
        List<OreProcessingDataLoader.OreProcessingEntry> oreProcessingRecipes =
                new ArrayList<>(OreProcessingDataLoader.getInstance().getAllEntries());

        registration.addRecipes(OreProcessingCategory.RECIPE_TYPE, oreProcessingRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        // 【修改】使用动画物品作为矿石处理配方的催化剂
        if (ForgingAndCraftingItems.CARRIER_DISH_ANIMATED != null &&
                ForgingAndCraftingItems.CARRIER_DISH_ANIMATED.get() != null) {
            registration.addRecipeCatalyst(
                    new ItemStack(ForgingAndCraftingItems.CARRIER_DISH_ANIMATED.get()),
                    OreProcessingCategory.RECIPE_TYPE
            );
        }

        // 凿子作为催化剂
        if (ForgingAndCraftingItems.CHISEL != null && ForgingAndCraftingItems.CHISEL.get() != null) {
            registration.addRecipeCatalyst(
                    new ItemStack(ForgingAndCraftingItems.CHISEL.get()),
                    OreProcessingCategory.RECIPE_TYPE
            );
        }
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(RockCrusherScreen.class, 77, 6, 44, 12,
                RockCrusherCategory.ROCK_CRUSHER_TYPE);
    }
}