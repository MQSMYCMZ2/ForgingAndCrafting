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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * JEI分类：展示从JSON加载的矿石处理配方
 * 使用 OreProcessingDataLoader 中的数据
 */
public class OreProcessingCategory implements IRecipeCategory<OreProcessingDataLoader.OreProcessingEntry> {

    public static final ResourceLocation UID = new ResourceLocation(ForgingAndCrafting.MOD_ID, "ore_processing");
    public static final RecipeType<OreProcessingDataLoader.OreProcessingEntry> RECIPE_TYPE =
            new RecipeType<>(UID, OreProcessingDataLoader.OreProcessingEntry.class);

    // 背景纹理
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(ForgingAndCrafting.MOD_ID, "textures/gui/ore_processing.png");

    private final IDrawable background;
    private final IDrawable icon;
    private final Component title;
    private final IGuiHelper guiHelper;

    public OreProcessingCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        // 创建背景 (宽118，高54，与标准冶炼配方类似)
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 118, 54);
        // 使用铁碎粒作为图标
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ForgingAndCraftingBlocks.CARRIER_DISH.get()));
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
        // 输入槽：粗矿块 (x=11, y=19)
        builder.addSlot(RecipeIngredientRole.INPUT, 11, 19)
                .addItemStack(new ItemStack(recipe.input()));

        // 输出槽1：碎粒 (x=91, y=9)
        builder.addSlot(RecipeIngredientRole.OUTPUT, 91, 9)
                .addItemStack(new ItemStack(recipe.outputGranules()));

        // 输出槽2：粗矿（如果有） (x=91, y=32)
        if (recipe.outputRawOre() != null) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 91, 32)
                    .addItemStack(new ItemStack(recipe.outputRawOre()));
        }
    }

    @Override
    public void draw(OreProcessingDataLoader.OreProcessingEntry recipe, IRecipeSlotsView recipeSlotsView,
                     GuiGraphics guiGraphics, double mouseX, double mouseY) {
        // 绘制箭头动画或其他装饰
        Minecraft minecraft = Minecraft.getInstance();

        // 绘制"凿刻中..."提示
        Component hint = Component.translatable("jei.forging_and_crafting.ore_processing.hint");
        int hintWidth = minecraft.font.width(hint);
        guiGraphics.drawString(minecraft.font, hint, (118 - hintWidth) / 2, 40, 0x808080, false);
    }
}