package com.mqsmycmz.forging_and_crafting.world.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import com.mqsmycmz.forging_and_crafting.ForgingAndCrafting;
import com.mqsmycmz.forging_and_crafting.world.menu.RockCrusherMenu;

public class RockCrusherScreen extends AbstractContainerScreen<RockCrusherMenu> {

    //GUI材质的位置
    @SuppressWarnings("removal")
    private static final ResourceLocation TEXTURE = new ResourceLocation(ForgingAndCrafting.MOD_ID, "textures/gui/rock_crusher.png");

    //构造方法
    public RockCrusherScreen(RockCrusherMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);

        //自定义了希望渲染的材质的区域/大小
        this.imageWidth = 195;//宽度(195)
        this.imageHeight = 187;//高度(187)
    }

    //初始化回调
    // (其余可以添加的功能：
    // 计算并保存 GUI 左上角的起始坐标（leftPos / topPos 已自动算好）。
    //添加自定义按钮、标签、小部件。
    //注册拖拽区域、提示框等交互元素)
    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        //指定用“位置+纹理”着色器（默认 GUI 着色器）
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        //颜色乘 1→不变色、不闪烁
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        //绑定纹理（256×256 png）
        RenderSystem.setShaderTexture(0, TEXTURE);

        //计算左上角坐标，让窗口居中
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageWidth) / 2;

        //先画整个 GUI 背景（从纹理 0,0 区域裁出 imageWidth×imageHeight）
        pGuiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        //再画动态进度条
        renderProgressGear(pGuiGraphics, x, y);
    }

    //渲染可变的进度齿轮
    private void renderProgressGear(GuiGraphics guiGraphics, int x, int y) {
        if (menu.isCrafting()) { //正在加工才渲染
            // 从纹理 (0,188) 开始裁一条宽度=当前进度像素、高度 67 的区域
            // 实际绘制位置：背景左上角再右 38、下 19 像素处
            guiGraphics.blit(TEXTURE, x + 38, y + 19, 0, 188, menu.getScaledProgress(), 67);
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }
}
