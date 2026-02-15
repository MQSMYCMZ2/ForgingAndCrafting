package com.mqsmycmz.forging_and_crafting.datagen;

import com.mqsmycmz.forging_and_crafting.block.ForgingAndCraftingBlocks;
import com.mqsmycmz.forging_and_crafting.item.ForgingAndCraftingItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

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
                .define('X', Items.CLAY_BALL).define('Y', Items.SAND).define('Z', Items.CHARCOAL).define('A', Items.BRICKS)
                .unlockedBy(getHasName(Items.CLAY_BALL), has(Items.CLAY_BALL))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ForgingAndCraftingBlocks.ROCK_CRUSHER.get())
                .pattern("XYZ")
                .pattern("ABA")
                .pattern("ZYX")
                .define('X', Items.POLISHED_DEEPSLATE).define('Y', Items.COAL).define('Z', Items.POLISHED_BLACKSTONE).define('A', Items.REDSTONE_BLOCK).define('B', ForgingAndCraftingItems.GEAR.get())
                .unlockedBy(getHasName(ForgingAndCraftingItems.GEAR.get()), has(ForgingAndCraftingItems.GEAR.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ForgingAndCraftingItems.GEAR.get())
                .pattern("XXX")
                .pattern("XYX")
                .pattern("XXX")
                .define('X', Items.REDSTONE).define('Y', Items.IRON_INGOT)
                .unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ForgingAndCraftingItems.CHISEL.get())
                .pattern(" X ")
                .pattern(" Y ")
                .pattern(" Z ")
                .define('X', Items.IRON_INGOT).define('Y', Items.IRON_BLOCK).define('Z', Items.STICK)
                .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ForgingAndCraftingBlocks.CARRIER_DISH.get())
                .pattern("   ")
                .pattern("X X")
                .pattern("XXX")
                .define('X', Items.IRON_INGOT)
                .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ForgingAndCraftingBlocks.MELTING_POT.get())
                .pattern("XXX")
                .pattern("XYX")
                .pattern("XXX")
                .define('X', Items.IRON_INGOT).define('Y', Items.CAULDRON)
                .unlockedBy(getHasName(Items.CAULDRON), has(Items.CAULDRON))
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
