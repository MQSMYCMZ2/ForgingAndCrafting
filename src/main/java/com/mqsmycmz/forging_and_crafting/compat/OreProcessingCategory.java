package com.mqsmycmz.forging_and_crafting.compat;

import com.mqsmycmz.forging_and_crafting.ForgingAndCrafting;
import com.mqsmycmz.forging_and_crafting.block.ForgingAndCraftingBlocks;
import com.mqsmycmz.forging_and_crafting.data.OreProcessingDataLoader;
import mezz.jei.api.constants.VanillaTypes;
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

/**
 * JEI分类：展示矿石处理配方，顶部带3D承物盘预览
 */
public class OreProcessingCategory implements IRecipeCategory<OreProcessingDataLoader.OreProcessingEntry> {

    public static final ResourceLocation UID = new ResourceLocation(ForgingAndCrafting.MOD_ID, "ore_processing");
    public static final RecipeType<OreProcessingDataLoader.OreProcessingEntry> RECIPE_TYPE =
            new RecipeType<>(UID, OreProcessingDataLoader.OreProcessingEntry.class);

    // 背景纹理（可选，可以用空白背景）
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(ForgingAndCrafting.MOD_ID, "textures/gui/jei/ore_processing.png");

    private final IDrawable background;
    private final IDrawable icon;
    private final Component title;

    // 界面尺寸
    private static final int WIDTH = 140;
    private static final int HEIGHT = 90;

    // 3D预览区域位置
    private static final int PREVIEW_X = 0;  // 左侧预览
    private static final int PREVIEW_Y = 25;
    private static final float PREVIEW_SCALE = 25.0f;

    // 配方区域位置（右侧）
    private static final int RECIPE_START_X = 75;
    private static final int INPUT_Y = 20;
    private static final int OUTPUT_Y = 50;

    public OreProcessingCategory(IGuiHelper guiHelper) {
        // 创建空白背景或自定义纹理
        this.background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
        // 使用承物盘作为图标
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ForgingAndCraftingBlocks.CARRIER_DISH.get()));
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
        // 输入槽：粗矿块 (右侧上方)
        builder.addSlot(RecipeIngredientRole.INPUT, RECIPE_START_X, INPUT_Y)
                .addItemStack(new ItemStack(recipe.input()));

        // 输出槽1：碎粒 (右侧下方左侧)
        builder.addSlot(RecipeIngredientRole.OUTPUT, RECIPE_START_X, OUTPUT_Y)
                .addItemStack(new ItemStack(recipe.outputGranules()));

        // 输出槽2：粗矿（如果有）(右侧下方右侧)
        if (recipe.outputRawOre() != null) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, RECIPE_START_X + 25, OUTPUT_Y)
                    .addItemStack(new ItemStack(recipe.outputRawOre()));
        }
    }

    @Override
    public void draw(OreProcessingDataLoader.OreProcessingEntry recipe, IRecipeSlotsView recipeSlotsView,
                     GuiGraphics guiGraphics, double mouseX, double mouseY) {

        // 渲染3D承物盘模型（左侧，无旋转）
        JEIBlockRenderer.renderCarrierDish(guiGraphics, PREVIEW_X, PREVIEW_Y, PREVIEW_SCALE);

        // 箭头渲染已移除
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }
}