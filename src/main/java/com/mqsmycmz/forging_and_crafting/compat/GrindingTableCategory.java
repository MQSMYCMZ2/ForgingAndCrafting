package com.mqsmycmz.forging_and_crafting.compat;

import com.mqsmycmz.forging_and_crafting.ForgingAndCrafting;
import com.mqsmycmz.forging_and_crafting.block.ForgingAndCraftingBlocks;
import com.mqsmycmz.forging_and_crafting.data.GrindingTableDataLoader;
import com.mqsmycmz.forging_and_crafting.item.ForgingAndCraftingItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;

public class GrindingTableCategory implements IRecipeCategory<GrindingTableDataLoader.OreProcessingEntry> {
    public static final ResourceLocation UID = new ResourceLocation(ForgingAndCrafting.MOD_ID, "grinding_table");
    public static final ResourceLocation TEXTURE = new ResourceLocation(ForgingAndCrafting.MOD_ID,
            "textures/gui/grinding_table_for_recipes.png");

    public static final RecipeType<GrindingTableDataLoader.OreProcessingEntry> GRINDING_TABLE_TYPE =
            new RecipeType<>(UID, GrindingTableDataLoader.OreProcessingEntry.class);

    private static final int ANIMATION_DURATION_MS = 1500;

    private final IDrawable background;
    private final IDrawable icon;

    private final Map<GrindingTableDataLoader.OreProcessingEntry, Long> recipesStartTimes =
            new IdentityHashMap<>();


    public GrindingTableCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 194, 104);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ForgingAndCraftingBlocks.GRINDING_TABLE.get()));
    }

    @Override
    public RecipeType<GrindingTableDataLoader.OreProcessingEntry> getRecipeType() {
        return GRINDING_TABLE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.forging_and_crafting.grinding_table");
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
    public void draw(GrindingTableDataLoader.OreProcessingEntry recipe,
                     IRecipeSlotsView recipeSlotsView,
                     GuiGraphics guiGraphics,
                     double mouseX, double mouseY) {
        long now = System.currentTimeMillis();

        long startTime = this.recipesStartTimes.computeIfAbsent(recipe, r ->
                now);

        long elapsed = now - startTime;

        float progress = (elapsed % ANIMATION_DURATION_MS) / (float) ANIMATION_DURATION_MS;

        int arrowFullWidth = 38;
        int arrowHeight = 11;
        int drawWidth = (int) (arrowFullWidth * progress);
        if (drawWidth > 0) {
            guiGraphics.blit(TEXTURE, 46, 47, 195, 0, drawWidth, arrowHeight);
        }

    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder,
                          GrindingTableDataLoader.OreProcessingEntry grindingTableDataLoader,
                          IFocusGroup iFocusGroup) {
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 22, 26).addItemStack(grindingTableDataLoader.getInputStack());
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 22, 63).addItemStack(new ItemStack(ForgingAndCraftingBlocks.GRINDING_TABLE.get()));
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 57, 32).addItemStack(new ItemStack(ForgingAndCraftingItems.SIMPLE_STONE_CHISEL.get()));
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 99, 45).addItemStack(grindingTableDataLoader.getGranulesStack(1));
        if (grindingTableDataLoader.hasRawOreOutput()) {
            iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 157, 45)
                    .addItemStack(grindingTableDataLoader.getRawOreStack(1));
        }
    }
}
