package com.mqsmycmz.forging_and_crafting.world.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mqsmycmz.forging_and_crafting.ForgingAndCrafting;
import com.mqsmycmz.forging_and_crafting.recipe.ShapingTableRecipe;
import com.mqsmycmz.forging_and_crafting.world.menu.ShapingTableMenu;
import com.mqsmycmz.forging_and_crafting.world.screen.packet.ShapingTableSelectRecipePacket;
import com.mqsmycmz.forging_and_crafting.world.screen.packet.ShapingTableStartCraftPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ShapingTableScreen extends AbstractContainerScreen<ShapingTableMenu> {
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(ForgingAndCrafting.MOD_ID, "textures/gui/shaping_table.png");

    private static final int BUTTON_X = 132, BUTTON_Y = 64, BUTTON_WIDTH = 34, BUTTON_HEIGHT = 10;
    private static final int BG_UNSELECTED_X = 196, BG_UNSELECTED_Y = 16;
    private static final int BG_SELECTED_X = 196, BG_SELECTED_Y = 34;
    private static final int BG_HOVER_X = 196, BG_HOVER_Y = 52;
    private static final int BG_WIDTH = 16, BG_HEIGHT = 18;
    private static final int ARROW_STATIC_X = 134, ARROW_STATIC_Y = 44;
    private static final int ARROW_PROGRESS_X = 196, ARROW_PROGRESS_Y = 1;
    private static final int ARROW_WIDTH = 27, ARROW_HEIGHT = 13;

    private static final int LIST_START_X = 23, LIST_START_Y = 14;
    private static final int LIST_SPACING_X = 20;

    private Button startButton;
    private int hoveredRecipeIndex = -1;

    public ShapingTableScreen(ShapingTableMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 194;
        this.imageHeight = 186;
        this.titleLabelX = 8;
        this.titleLabelY = 5;
    }

    @Override
    protected void init() {
        super.init();
        this.startButton = this.addRenderableWidget(Button.builder(
                        Component.translatable("container.shaping_table.start"),
                        btn -> {
                            if (menu.getBlockEntity() != null && !menu.isCrafting()) {
                                ShapingTableStartCraftPacket.sendToServer();
                            }
                        })
                .bounds(this.leftPos + BUTTON_X, this.topPos + BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build());
        startButton.active = false;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        int x = this.leftPos;
        int y = this.topPos;
        graphics.blit(GUI_TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        renderArrow(graphics, x, y);
        renderRecipeList(graphics, mouseX, mouseY);

        // 更新按钮状态：使用菜单槽位判断输入是否为空
        boolean canStart = menu.getBlockEntity() != null &&
                menu.getBlockEntity().getSelectedRecipeId() != null &&
                !menu.isCrafting() &&
                !menu.isCraftFinished() &&
                !menu.getSlot(0).getItem().isEmpty();
        startButton.active = canStart;
    }

    private void renderArrow(GuiGraphics graphics, int x, int y) {
        int progress = menu.getScaledProgress();
        if (progress <= 0) {
            graphics.blit(GUI_TEXTURE, x + 134, y + 44, ARROW_STATIC_X, ARROW_STATIC_Y, ARROW_WIDTH, ARROW_HEIGHT);
        } else {
            int cropWidth = Math.min(progress, ARROW_WIDTH);
            graphics.blit(GUI_TEXTURE, x + 134, y + 44, ARROW_PROGRESS_X, ARROW_PROGRESS_Y, cropWidth, ARROW_HEIGHT);
        }
    }

    private void renderRecipeList(GuiGraphics graphics, int mouseX, int mouseY) {
        // 从菜单的输入槽（索引0）获取输入物品，而非从 blockEntity 获取
        ItemStack input = menu.getSlot(0).getItem();
        if (input.isEmpty()) return;

        List<ShapingTableRecipe> recipes = Minecraft.getInstance().level.getRecipeManager()
                .getAllRecipesFor(ShapingTableRecipe.TYPE)
                .stream()
                .filter(r -> r.matches(new ShapingTableRecipe.InventoryWrapper(input), Minecraft.getInstance().level))
                .toList();

        if (recipes.isEmpty()) return;

        ResourceLocation selected = menu.getBlockEntity().getSelectedRecipeId();
        int index = 0;
        for (ShapingTableRecipe recipe : recipes) {
            int x = this.leftPos + LIST_START_X + index * LIST_SPACING_X;
            int y = this.topPos + LIST_START_Y;
            boolean isSelected = selected != null && selected.equals(recipe.getId());
            boolean isHovered = hoveredRecipeIndex == index;

            int bgU, bgV;
            if (isHovered) {
                bgU = BG_HOVER_X; bgV = BG_HOVER_Y;
            } else if (isSelected) {
                bgU = BG_SELECTED_X; bgV = BG_SELECTED_Y;
            } else {
                bgU = BG_UNSELECTED_X; bgV = BG_UNSELECTED_Y;
            }
            graphics.blit(GUI_TEXTURE, x, y, bgU, bgV, BG_WIDTH, BG_HEIGHT);

            ItemStack result = recipe.getResultItem(Minecraft.getInstance().level.registryAccess());
            graphics.renderItem(result, x + 1, y + 1);

            index++;
            if (index >= 10) break;
        }

        // 更新悬浮索引
        hoveredRecipeIndex = -1;
        int idx = 0;
        for (ShapingTableRecipe recipe : recipes) {
            int x = this.leftPos + LIST_START_X + idx * LIST_SPACING_X;
            int y = this.topPos + LIST_START_Y;
            if (mouseX >= x && mouseX < x + BG_WIDTH && mouseY >= y && mouseY < y + BG_HEIGHT) {
                hoveredRecipeIndex = idx;
                break;
            }
            idx++;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (hoveredRecipeIndex >= 0 && menu.getBlockEntity() != null) {
            ItemStack input = menu.getSlot(0).getItem();
            if (!input.isEmpty()) {
                List<ShapingTableRecipe> recipes = Minecraft.getInstance().level.getRecipeManager()
                        .getAllRecipesFor(ShapingTableRecipe.TYPE)
                        .stream()
                        .filter(r -> r.matches(new ShapingTableRecipe.InventoryWrapper(input),
                                Minecraft.getInstance().level))
                        .toList();
                if (hoveredRecipeIndex < recipes.size()) {
                    ShapingTableRecipe recipe = recipes.get(hoveredRecipeIndex);
                    ShapingTableSelectRecipePacket.sendToServer(recipe.getId());
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
    }

    @Override
    public void containerTick() {
        super.containerTick();
    }
}