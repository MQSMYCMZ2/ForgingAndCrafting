package com.mqsmycmz.forging_and_crafting.datagen;

import com.mqsmycmz.forging_and_crafting.ForgingAndCrafting;
import com.mqsmycmz.forging_and_crafting.block.ForgingAndCraftingBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.List;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    //熔炼用：
    //public static final List<ItemLike> CLAY_BRICK = List.of(Items.BRICKS, ForgingAndCraftingBlocks.CLAY_BRICK.get());

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ForgingAndCraftingBlocks.CLAY_BRICK.get())
                .pattern("XZX")
                .pattern("YAY")
                .pattern("XZX")
                .define('X', Items.CLAY_BALL).define('Y', Items.SAND).define('Z', Items.CHARCOAL).define('A', Items.BRICKS).unlockedBy(getHasName(Items.CLAY_BALL), has(Items.CLAY_BALL))
                .save(pWriter);

        //熔炉：
        //oreSmelting(pWriter, CLAY_BRICK, RecipeCategory.MISC, ForgingAndCraftingBlocks.CLAY_BRICK.get(), 0.25F, 200, "clay_brick");
        //高炉：
        //oreBlasting(pWriter, CLAY_BRICK, RecipeCategory.MISC, ForgingAndCraftingBlocks.CLAY_BRICK.get(), 0.25F, 100, "clay_brick");

        //无序合成：
//        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.BRICKS, 9)
//                .requires(ForgingAndCraftingBlocks.CLAY_BRICK.get())
//                .unlockedBy(getHasName(Items.CLAY_BALL), has(Items.CLAY_BALL))
//                .save(pWriter);
    }

//    protected static void oreSmelting(Consumer<FinishedRecipe> pFinishedRecipeConsumer, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTIme, String pGroup) {
//        oreCooking(pFinishedRecipeConsumer, RecipeSerializer.SMELTING_RECIPE, pIngredients, pCategory, pResult, pExperience, pCookingTIme, pGroup, "_from_smelting");
//    }
//
//    protected static void oreBlasting(Consumer<FinishedRecipe> pFinishedRecipeConsumer, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup) {
//        oreCooking(pFinishedRecipeConsumer, RecipeSerializer.BLASTING_RECIPE, pIngredients, pCategory, pResult, pExperience, pCookingTime, pGroup, "_from_blasting");
//    }
//
//    protected static void oreCooking(Consumer<FinishedRecipe> pFinishedRecipeConsumer, RecipeSerializer<? extends AbstractCookingRecipe> pCookingSerializer, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup, String pRecipeName) {
//        for(ItemLike itemlike : pIngredients) {
//            SimpleCookingRecipeBuilder.generic(Ingredient.of(new ItemLike[]{itemlike}), pCategory, pResult, pExperience, pCookingTime, pCookingSerializer).group(pGroup)
//                    .unlockedBy(getHasName(itemlike), has(itemlike))
//                    .save(pFinishedRecipeConsumer, ForgingAndCrafting.MOD_ID + ":" +  getItemName(pResult) + pRecipeName + "_" + getItemName(itemlike));
//        }
//    }
}
