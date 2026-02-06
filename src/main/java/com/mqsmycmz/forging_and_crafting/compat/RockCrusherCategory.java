package com.mqsmycmz.forging_and_crafting.compat;

import com.mqsmycmz.forging_and_crafting.ForgingAndCrafting;
import com.mqsmycmz.forging_and_crafting.block.ForgingAndCraftingBlocks;
import com.mqsmycmz.forging_and_crafting.recipe.RockCrusherRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class RockCrusherCategory implements IRecipeCategory<RockCrusherRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(ForgingAndCrafting.MOD_ID, "rock_crusher");
    public static final ResourceLocation TEXTURE = new ResourceLocation(ForgingAndCrafting.MOD_ID,
            "textures/gui/rock_crusher_for_jei.png");

    public static final RecipeType<RockCrusherRecipe> ROCK_CRUSHER_TYPE =
            new RecipeType<>(UID, RockCrusherRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public RockCrusherCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 194, 104);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ForgingAndCraftingBlocks.ROCK_CRUSHER.get()));
    }

    @Override
    public RecipeType<RockCrusherRecipe> getRecipeType() {
        return ROCK_CRUSHER_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.forging_and_crafting.rock_crusher");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, RockCrusherRecipe rockCrusherRecipe, IFocusGroup iFocusGroup) {
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 10, 42).addIngredients(rockCrusherRecipe.getIngredients().get(0));

        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 171, 42).addItemStack(rockCrusherRecipe.getResultItem(null));
    }
}
