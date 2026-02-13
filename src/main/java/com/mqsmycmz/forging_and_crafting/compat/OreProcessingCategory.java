// OreProcessingCategory.java
package com.mqsmycmz.forging_and_crafting.compat;

import com.mqsmycmz.forging_and_crafting.ForgingAndCrafting;
import com.mqsmycmz.forging_and_crafting.data.OreProcessingDataLoader;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
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

public class OreProcessingCategory implements IRecipeCategory<OreProcessingDataLoader.OreProcessingEntry> {

    public static final ResourceLocation UID = new ResourceLocation(ForgingAndCrafting.MOD_ID, "ore_processing");
    public static final RecipeType<OreProcessingDataLoader.OreProcessingEntry> RECIPE_TYPE =
            new RecipeType<>(UID, OreProcessingDataLoader.OreProcessingEntry.class);

    private final IDrawable background;
    private final IDrawable icon;  // JEI标签图标
    private final Component title;

    private static final int WIDTH = 140;
    private static final int HEIGHT = 90;
    private static final int PREVIEW_X = 25;
    private static final int PREVIEW_Y = 10;
    private static final float PREVIEW_SCALE = 25.0f;
    private static final int RECIPE_START_X = 75;
    private static final int INPUT_Y = 20;
    private static final int OUTPUT_Y = 50;

    public OreProcessingCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);

        // 【自定义标签图标】16x16区域，scale=12，旋转角度可调
        // 示例：稍微俯视角度，像工作台图标那样
        this.icon = new AnimatedCarrierDishIconDrawable.Builder()
                .size(20, 20)
                .scale(15.0f)
                .rotation(32f, 23f, 0f)  // 自定义角度
                .offset(-10f, 7f, 0f)       // 微调位置
                .build();

        this.title = Component.translatable("jei.forging_and_crafting.category.ore_processing");
    }

    @Override
    public RecipeType<OreProcessingDataLoader.OreProcessingEntry> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return title;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, OreProcessingDataLoader.OreProcessingEntry recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, RECIPE_START_X, INPUT_Y)
                .addItemStack(new ItemStack(recipe.input()));
        builder.addSlot(RecipeIngredientRole.OUTPUT, RECIPE_START_X, OUTPUT_Y)
                .addItemStack(new ItemStack(recipe.outputGranules()));
        if (recipe.outputRawOre() != null) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, RECIPE_START_X + 25, OUTPUT_Y)
                    .addItemStack(new ItemStack(recipe.outputRawOre()));
        }
    }

    @Override
    public void draw(OreProcessingDataLoader.OreProcessingEntry recipe, IRecipeSlotsView recipeSlotsView,
                     GuiGraphics guiGraphics, double mouseX, double mouseY) {
        // 配方界面中的大预览（使用原来的渲染方法）
        JEIBlockRenderer.renderAnimatedCarrierDish(guiGraphics, PREVIEW_X, PREVIEW_Y, PREVIEW_SCALE);
    }

    @Override
    public int getWidth() { return WIDTH; }

    @Override
    public int getHeight() { return HEIGHT; }
}